package org.tk.marmot

package core.time

import org.tk.marmot.core.{RefDataId, Util}

import java.time.LocalDate

final class Calendar(val id: String, val holidays: Set[LocalDate]) extends Serializable {
  def isHoliday(date: LocalDate): Boolean = holidays.contains(date)
  def isBusinessDay(date: LocalDate): Boolean = !isHoliday(date) && !isWeekend(date)
  def isWeekend(date: LocalDate): Boolean = date.getDayOfWeek.getValue >= 6
  def adjustDate(date: LocalDate, adjustment: BusinessDateAdjustmentConvention): LocalDate = adjustment.adjust(date, this)
  def advance(date: LocalDate, tenor: Tenor, adjustment: BusinessDateAdjustmentConvention): LocalDate = adjustDate(date + tenor, adjustment)

  override def toString: String = s"Calendar('$id')"
}

/**
 * Represents an identifier for a calendar.
 * @param name the name of the calendar
 */
final case class CalendarId(name: String) extends Serializable, RefDataId[Calendar] {
  override val hashCode: Int = Util.fastHash(this)

  override def equals(obj: Any): Boolean = obj match {
    case that: CalendarId => this.hashCode == that.hashCode
    case _ => false
  }

  // override def toString: String = s"$name"

  override def klass: Class[Calendar] = classOf[Calendar]

  override def simpleId: String = name
}
