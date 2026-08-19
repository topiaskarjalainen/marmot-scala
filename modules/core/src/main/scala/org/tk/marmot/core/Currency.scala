package org.tk.marmot.core

final case class Currency(code: String, name: String, symbol: String) {
}

object Currency {
  val USD: Currency = Currency("USD", "United States Dollar", "$")
  val EUR: Currency = Currency("EUR", "Euro", "€")
  val GBP: Currency = Currency("GBP", "British Pound Sterling", "£")
  val JPY: Currency = Currency("JPY", "Japanese Yen", "¥")
  val CHF: Currency = Currency("CHF", "Swiss Franc", "CHF")
  val CAD: Currency = Currency("CAD", "Canadian Dollar", "$")
  val AUD: Currency = Currency("AUD", "Australian Dollar", "$")
  val NZD: Currency = Currency("NZD", "New Zealand Dollar", "$")
  val CNY: Currency = Currency("CNY", "Chinese Yuan Renminbi", "¥")
  val SEK: Currency = Currency("SEK", "Swedish Krona", "kr")
  val NOK: Currency = Currency("NOK", "Norwegian Krone", "kr")
  val DKK: Currency = Currency("DKK", "Danish Krone", "kr")
}
