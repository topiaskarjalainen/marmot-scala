package org.tk.marmot
package core.time

enum TenorUnit {
  case Days, Weeks, Months, Years
}

case class Tenor(value: Int, unit: TenorUnit) {
  override def toString: String = s"$value ${unit.toString.toLowerCase}"

  def +(other: Tenor): Tenor = {
    if (this.unit == other.unit) {
      Tenor(this.value + other.value, this.unit)
    } else {
      throw new IllegalArgumentException("Cannot add tenors with different units")
    }
  }

  def -(other: Tenor): Tenor = {
    if (this.unit == other.unit) {
      Tenor(this.value - other.value, this.unit)
    } else {
      throw new IllegalArgumentException("Cannot subtract tenors with different units")
    }
  }

  def +(date: java.time.LocalDate): java.time.LocalDate = {
    unit match {
      case TenorUnit.Days => date.plusDays(value)
      case TenorUnit.Weeks => date.plusWeeks(value)
      case TenorUnit.Months => date.plusMonths(value)
      case TenorUnit.Years => date.plusYears(value)
    }
  }

  def -(date: java.time.LocalDate): java.time.LocalDate = {
    unit match {
      case TenorUnit.Days => date.minusDays(value)
      case TenorUnit.Weeks => date.minusWeeks(value)
      case TenorUnit.Months => date.minusMonths(value)
      case TenorUnit.Years => date.minusYears(value)
    }
  }
}

object Tenor {
  final val ONE_DAY = Tenor(1, TenorUnit.Days)
  final val ONE_WEEK = Tenor(1, TenorUnit.Weeks)
  final val ONE_MONTH = Tenor(1, TenorUnit.Months)
  final val ONE_YEAR = Tenor(1, TenorUnit.Years)
  final val THREE_MONTHS = Tenor(3, TenorUnit.Months)
  final val SIX_MONTHS = Tenor(6, TenorUnit.Months)

  def apply(value: Int, unit: TenorUnit): Tenor = new Tenor(value, unit)

  def fromString(tenorStr: String): Tenor = {
    val pattern = """(\d+)([DWMY])""".r
    tenorStr match {
      case pattern(value, unit) =>
        val tenorUnit = unit match {
          case "D" => TenorUnit.Days
          case "W" => TenorUnit.Weeks
          case "M" => TenorUnit.Months
          case "Y" => TenorUnit.Years
        }
        Tenor(value.toInt, tenorUnit)
      case _ => throw new IllegalArgumentException(s"Invalid tenor string: $tenorStr")
    }
  }
}

extension (date: java.time.LocalDate) {
  def +(tenor: Tenor): java.time.LocalDate = tenor + date
  def -(tenor: Tenor): java.time.LocalDate = tenor - date
}
