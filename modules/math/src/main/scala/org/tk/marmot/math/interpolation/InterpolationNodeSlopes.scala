package org.tk.marmot
package math.interpolation


/**
 * Node slopes for interpolation.
 * @tparam T Type of the interpolation nodes (e.g., Double, Float).
 */
trait InterpolationNodeSlopes[T] extends Function2[Array[T], Array[T], Array[T]] {
  def cacheSlopes(x: Array[T], y: Array[T]): CachedSlopes[T] = {
    val computedSlopes = apply(x, y)
    CachedSlopes(computedSlopes)
  }
}

/**
 * Special form of InterpolationNodeSlopes that caches the slopes for reuse.
 * @param slopes Cached slopes for interpolation.
 * @tparam T Type of the interpolation nodes (e.g., Double, Float).
 */
final case class CachedSlopes[T](cached: Array[T]) extends InterpolationNodeSlopes[T] {
  inline override def apply(x: Array[T], y: Array[T]): Array[T] = cached
}

