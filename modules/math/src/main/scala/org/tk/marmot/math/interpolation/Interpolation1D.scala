package org.tk.marmot
package math.interpolation


trait Interpolation1D[@specialized(Double, Float) T] {
  val slopeFn: InterpolationNodeSlopes[T]
  def interpolate(xq: T, x: Array[T], y: Array[T], slopes: Array[T]): T
  def firstDerivative(xq: T, x: Array[T], y: Array[T], slopes: Array[T]): T
}

object Interpolation1D {
  def binarySearch(x: Array[Float], xq: Float): Int = {
    var low = 0
    var high = x.length - 1
    while (low <= high) {
      val mid = (low + high) >>> 1
      if (x(mid) < xq) {
        low = mid + 1
      } else if (x(mid) > xq) {
        high = mid - 1
      } else {
        return mid
      }
    }
    low
  }

  def binarySearch(x: Array[Double], xq: Double): Int = {
    var low = 0
    var high = x.length - 1
    while (low <= high) {
      val mid = (low + high) >>> 1
      if (x(mid) < xq) {
        low = mid + 1
      } else if (x(mid) > xq) {
        high = mid - 1
      } else {
        return mid
      }
    }
    low
  }

  def linearSearch(x: Array[Float], xq: Float): Int = {
    var i = 0
    while (i < x.length && x(i) < xq) {
      i += 1
    }
    i
  }

  def linearSearch(x: Array[Double], xq: Double): Int = {
    var i = 0
    while (i < x.length && x(i) < xq) {
      i += 1
    }
    i
  }
}