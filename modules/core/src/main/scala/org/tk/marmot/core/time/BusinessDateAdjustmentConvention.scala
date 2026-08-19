package org.tk.marmot

package core.time

import java.time.LocalDate


enum BusinessDateAdjustmentConvention {
  case NO_ADJUSTMENT, FOLLOWING, PRECEDING, MODIFIED_FOLLOWING, MODIFIED_PRECEDING;

  def adjust(date: LocalDate, calendar: Calendar): LocalDate = this match {
    case NO_ADJUSTMENT => date
    case FOLLOWING => adjustFollowing(date, calendar)
    case PRECEDING => adjustPreceding(date, calendar)
    case MODIFIED_FOLLOWING => adjustModifiedFollowing(date, calendar)
    case MODIFIED_PRECEDING => adjustModifiedPreceding(date, calendar)
  }
}

inline def adjustFollowing(date: LocalDate, calendar: Calendar): LocalDate = {
  var adjustedDate = date
  while (!calendar.isBusinessDay(adjustedDate)) {
    adjustedDate = adjustedDate.plusDays(1)
  }
  adjustedDate
}

inline def adjustPreceding(date: LocalDate, calendar: Calendar): LocalDate = {
  var adjustedDate = date
  while (!calendar.isBusinessDay(adjustedDate)) {
    adjustedDate = adjustedDate.minusDays(1)
  }
  adjustedDate
}

inline def adjustModifiedFollowing(date: LocalDate, calendar: Calendar): LocalDate = {
  val adjustedDate = adjustFollowing(date, calendar)
  if (adjustedDate.getMonth != date.getMonth) {
    adjustPreceding(date, calendar)
  } else {
    adjustedDate
  }
}

inline def adjustModifiedPreceding(date: LocalDate, calendar: Calendar): LocalDate = {
  val adjustedDate = adjustPreceding(date, calendar)
  if (adjustedDate.getMonth != date.getMonth) {
    adjustFollowing(date, calendar)
  } else {
    adjustedDate
  }
}
