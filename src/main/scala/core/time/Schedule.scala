package org.tk.marmot
package core.time

import core.{ReferenceDataProvider, Resolvable}

import java.time.LocalDate
import scala.annotation.tailrec
import scala.math.Ordering.Implicits.infixOrderingOps


/** Generic case class that holds start and end dates for a period */
case class SchedulePeriod(start: LocalDate, end: LocalDate, unadjustedStart: LocalDate, unadjustedEnd: LocalDate) {
  override def toString: String = s"SchedulePeriod($start, $end, $unadjustedStart, $unadjustedEnd, len=${java.time.temporal.ChronoUnit.DAYS.between(start, end)}))"

  def this(start: LocalDate, end: LocalDate, adjuster: DateAdjuster) = {
    this(adjuster.adjust(start), adjuster.adjust(end), start, end)
  }

  /**
   * Merge two periods. Left side is assumed to be before the right side, and the two periods are assumed to be overlapping or adjacent.
   * @param other To merge with this period.
   * @return A new SchedulePeriod that covers the range of both periods
   */
  infix def ++(other: SchedulePeriod): SchedulePeriod = {
    require(this.end >= other.start, s"Cannot merge periods that are not overlapping or adjacent: $this and $other")
    SchedulePeriod(this.start, other.end, this.unadjustedStart, other.unadjustedEnd)
  }
}


/** Stub rule, so do we want the possible irregular period at the start or end of the schedule, and if so, is it a short or long stub.
 */
enum StubType {
  case
    NONE,
    SHORT_START,
    LONG_START,
    SHORT_END,
    LONG_END

  def isForwardsGenerated: Boolean = this match {
    case StubType.SHORT_START | StubType.LONG_START => false
    case StubType.SHORT_END | StubType.LONG_END | StubType.NONE => true
  }
}

/**
 * Configuration for schedule that can be resolved as a periodic schedule.
 * @param startDate
 * @param endDate
 * @param tenor
 * @param calendar
 * @param dateAdjustment
 * @param stubType
 */
case class ScheduleConfig(
                    startDate: LocalDate
                    , endDate: LocalDate
                    , tenor: Tenor
                    , calendar: CalendarId
                    , dateAdjustment: BusinessDateAdjustmentConvention
                    , stubType: StubType
                    ) extends Resolvable[PeriodicSchedule] {
  override def resolve(ref: ReferenceDataProvider): PeriodicSchedule = {
    val cal = ref.get(calendar)
    val adjuster: DateAdjuster = (date: LocalDate) => dateAdjustment.adjust(date, cal)
    val dates = if stubType.isForwardsGenerated
      then generateForwards(startDate, endDate, tenor, adjuster, stubType)
      else generateBackwards(endDate, startDate, tenor, adjuster, stubType).reverse
    PeriodicSchedule(handleStubPeriod(startDate, endDate, stubType, dates, adjuster))
  }
}

def generateForwards(startDate: LocalDate, endDate: LocalDate, tenor: Tenor, adjuster: DateAdjuster, stubType: StubType): List[SchedulePeriod] = {
  val adjustedStart = adjuster.adjust(startDate)
  @tailrec
  def inner(lastPeriod: SchedulePeriod, lastAccepted: SchedulePeriod, accumulatedPeriods: List[SchedulePeriod]): List[SchedulePeriod] = {
    val nextPeriod = new SchedulePeriod(lastPeriod.unadjustedEnd, lastPeriod.unadjustedEnd + tenor, adjuster)
    if lastAccepted.end >= endDate
      then accumulatedPeriods
    else if nextPeriod.start.equals(nextPeriod.end)
      then inner(nextPeriod, lastAccepted, accumulatedPeriods)
    else
      inner(nextPeriod, nextPeriod, accumulatedPeriods ::: List(nextPeriod))
  }
  val start = SchedulePeriod(adjustedStart, adjustedStart, adjustedStart, adjustedStart)
  inner(start, start, List())
}


def generateBackwards(startDate: LocalDate, endDate: LocalDate, tenor: Tenor, adjuster: DateAdjuster, stubType: StubType): List[SchedulePeriod] = {
  val adjustedStart = adjuster.adjust(startDate)
  @tailrec
  def inner(lastPeriod: SchedulePeriod, lastAccepted: SchedulePeriod, accumulatedPeriods: List[SchedulePeriod]): List[SchedulePeriod] = {
    val nextPeriod = new SchedulePeriod(lastPeriod.unadjustedStart - tenor, lastPeriod.unadjustedStart, adjuster)
    if lastAccepted.start <= endDate
      then accumulatedPeriods
    else if nextPeriod.start.equals(nextPeriod.end)
      then inner(nextPeriod, lastAccepted, accumulatedPeriods)
    else
      inner(nextPeriod, nextPeriod, accumulatedPeriods ::: List(nextPeriod))
  }
  val start = SchedulePeriod(adjustedStart, adjustedStart, adjustedStart, adjustedStart)
  inner(start, start, List())
}

def handleStubPeriod(confStart: LocalDate, confEnd: LocalDate, stubType: StubType, dates: List[SchedulePeriod], adjuster: DateAdjuster): List[SchedulePeriod] = {
  lazy val (adjustedConfStart, adjustedConfEnd) = (adjuster.adjust(confStart), adjuster.adjust(confEnd))
  stubType match {
    case StubType.NONE =>
      require(dates.head.start == adjustedConfStart, s"First period start ${dates.head.start} does not match adjusted start $adjustedConfStart")
      require(dates.last.end == adjustedConfEnd, s"Last period end ${dates.last.end} does not match adjusted end $adjustedConfEnd")
      dates
    case StubType.SHORT_START | StubType.LONG_START =>
      // Check if start is aligned with adjustedConfStart, and if not, create a new period from adjustedConfStart to the start of the first period
      val firstPeriod = dates.head
      if firstPeriod.start != adjustedConfStart then
        val stubPeriod = new SchedulePeriod(confStart, firstPeriod.unadjustedEnd, adjuster)
        if stubType == StubType.SHORT_START then
          stubPeriod :: dates.tail
        else {
          // For long start, we need to merge the stub period with the first period
          dates.take(2) match {
            case first :: second :: Nil =>
              val mergedPeriod = stubPeriod ++ second
              mergedPeriod :: dates.drop(2)
            case _ =>
              throw new IllegalStateException("Expected at least two periods for long start stub")
          }
        } else
        dates
    case StubType.SHORT_END | StubType.LONG_END =>
      // Check if end is aligned with adjustedConfEnd, and if not, create a new period from the end of the last period to adjustedConfEnd
      val lastPeriod = dates.last
      if lastPeriod.end != adjustedConfEnd then
        val stubPeriod = new SchedulePeriod(lastPeriod.unadjustedStart, confEnd, adjuster)
        if stubType == StubType.SHORT_END then
          dates.init ::: List(stubPeriod)
        else {
          // For long end, we need to merge the stub period with the last period
          dates.takeRight(2) match {
            case secondLast :: last :: Nil =>
              val mergedPeriod = secondLast ++ stubPeriod
              dates.dropRight(2) ::: List(mergedPeriod)
            case _ =>
              throw new IllegalStateException("Expected at least two periods for long end stub")
          }
        } else
        dates
  }
}

class PeriodicSchedule(val periods: List[SchedulePeriod]) {}
