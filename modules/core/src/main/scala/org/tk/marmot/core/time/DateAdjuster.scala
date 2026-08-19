package org.tk.marmot
package core.time

import java.time.LocalDate

/** Adjust dates according to conventions
 */
trait DateAdjuster {
  /**
   * Adjust date
   * @param date inputt
   * @return new adjusted date
   */
  def adjust(date: LocalDate): LocalDate
}
