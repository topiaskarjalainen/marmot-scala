package org.tk.marmot
package core

/**
 * Type that can be turned to other type with reference data
 * @tparam T Type of the resolved into object
 */
trait Resolvable[T] {
  /**
   * Resolve the object
   * @param ref Reference data instance
   * @return Instance of the resolved object. Not necessarily new 
   */
  def resolve(ref: ReferenceDataProvider): T
}
