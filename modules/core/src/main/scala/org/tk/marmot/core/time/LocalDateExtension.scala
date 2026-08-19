package org.tk.marmot
package core.time

extension (date: java.time.LocalDate) {
  inline infix def <(other: java.time.LocalDate): Boolean = date.isBefore(other)
  inline infix def >(other: java.time.LocalDate): Boolean = date.isAfter(other)
  inline infix def <=(other: java.time.LocalDate): Boolean = date.isBefore(other) || date.isEqual(other)
  inline infix def >=(other: java.time.LocalDate): Boolean = date.isAfter(other) || date.isEqual(other)
}
