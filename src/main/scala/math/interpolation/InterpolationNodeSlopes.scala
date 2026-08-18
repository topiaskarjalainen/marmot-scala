package org.tk.marmot
package math.interpolation


/**
 * Node slopes for interpolation.
 * @tparam T Type of the interpolation nodes (e.g., Double, Float).
 */
trait InterpolationNodeSlopes[T] {
  def slopes(x: Array[T], y: Array[T]): Array[T]
  def cacheSlopes(x: Array[T], y: Array[T]): CachedSlopes[T] = {
    val computedSlopes = slopes(x, y)
    CachedSlopes(computedSlopes)
  }
}

/**
 * Special form of InterpolationNodeSlopes that caches the slopes for reuse.
 * @param slopes Cached slopes for interpolation.
 * @tparam T Type of the interpolation nodes (e.g., Double, Float).
 */
final case class CachedSlopes[T](cached: Array[T]) extends InterpolationNodeSlopes[T] {
  inline override def slopes(x: Array[T], y: Array[T]): Array[T] = cached
}

