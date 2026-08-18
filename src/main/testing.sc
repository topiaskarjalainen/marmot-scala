import java.time.LocalDate
import org.tk.marmot.core.time.{Calendar, CalendarId, BusinessDateAdjustmentConvention, ScheduleConfig, SchedulePeriod, Tenor, TenorUnit}


val fileRefDataProvider = new org.tk.marmot.core.FileReferenceDataProvider

val cal = fileRefDataProvider.get(CalendarId("Tgt"))



val scheduleConfig = ScheduleConfig(
  startDate = LocalDate.of(2024, 1, 1),
  endDate = LocalDate.of(2024, 1, 31),
  tenor = Tenor(1, TenorUnit.Days),
  calendar = CalendarId("Tgt"),
  dateAdjustment = BusinessDateAdjustmentConvention.MODIFIED_FOLLOWING,
  stubType = org.tk.marmot.core.time.StubType.LONG_START
)


scheduleConfig.resolve(fileRefDataProvider).periods.foreach { period =>
  println(period)
}
