package org.tk

import org.tk.marmot.core.FileReferenceDataLoader
import org.tk.marmot.core.time.*

import java.time.LocalDate

@main
def main(): Unit = {
  val fileRefDataProvider = new org.tk.marmot.core.FileReferenceDataProvider

  var scheduleConfig = ScheduleConfig(
    startDate = LocalDate.of(2026, 8, 1),
    endDate = LocalDate.of(2026, 8, 31),
    tenor = Tenor(1, TenorUnit.Days),
    calendar = CalendarId("Tgt"),
    dateAdjustment = BusinessDateAdjustmentConvention.FOLLOWING,
    stubType = StubType.LONG_START
  )

  scheduleConfig = ScheduleConfig(
    startDate = LocalDate.of(2026, 8, 1),
    endDate = LocalDate.of(2026, 8, 1) + Tenor(1, TenorUnit.Years) + Tenor(3, TenorUnit.Weeks),
    tenor = Tenor(3, TenorUnit.Months),
    calendar = CalendarId("Tgt"),
    dateAdjustment = BusinessDateAdjustmentConvention.FOLLOWING,
    stubType = StubType.SHORT_END
  )
  println(scheduleConfig)
  println()


  scheduleConfig.resolve(fileRefDataProvider).periods.foreach { period =>
      println(period)
  }
  println("----")
  scheduleConfig.copy(stubType = StubType.LONG_END).resolve(fileRefDataProvider).periods.foreach { period =>
    println(period)
  }
}

