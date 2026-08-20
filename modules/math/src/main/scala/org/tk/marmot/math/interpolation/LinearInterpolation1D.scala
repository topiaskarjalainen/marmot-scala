package org.tk.marmot
package math.interpolation

class LinearInterpolation1D extends Interpolation1D[Double] {
  override def interpolate(xq: Double, x: Array[Double], y: Array[Double], slopes: InterpolationNodeSlopes[Double]): Double = ???

  override def firstDerivative(xq: Double, x: Array[Double], y: Array[Double], slopes: InterpolationNodeSlopes[Double]): Double = ???
}

object LinearInterpolation1D {
  inline def lerp(x0: Double, x1: Double, y0: Double, y1: Double, xq: Double): Double = {
    val t = (xq - x0) / (x1 - x0)
    y0 + t * (y1 - y0)
  }
}
