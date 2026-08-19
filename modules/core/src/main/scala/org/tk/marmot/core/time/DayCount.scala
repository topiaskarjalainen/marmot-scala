package org.tk.marmot
package core.time

import java.time.LocalDate

sealed trait DayCount {
  def yearFraction(startDate: java.time.LocalDate, endDate: java.time.LocalDate): Double
  def dayCount(startDate: java.time.LocalDate, endDate: java.time.LocalDate): Int
}

case object Actual360 extends DayCount {
  override def yearFraction(startDate: LocalDate, endDate: LocalDate): Double = dayCount(startDate, endDate) / 360.0
  override def dayCount(startDate: LocalDate, endDate: LocalDate): Int = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt
}

case object Actual365 extends DayCount {
  override def yearFraction(startDate: LocalDate, endDate: LocalDate): Double = dayCount(startDate, endDate) / 365.0
  override def dayCount(startDate: LocalDate, endDate: LocalDate): Int = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt
}
