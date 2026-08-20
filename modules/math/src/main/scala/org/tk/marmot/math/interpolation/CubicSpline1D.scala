package org.tk.marmot.math.interpolation

import org.tk.marmot.math.linalg.TridiagonalOperator

/**
 * Boundary conditions for cubic spline interpolation.
 */
enum CubicSplineBoundary:
  /**
   * Natural boundary conditions: the second derivative at the endpoints is zero.
   */
  case Natural
  /**
   * Clamped boundary conditions: the first derivative at the endpoints is specified.
   */
  case Clamped(leftSlope: Double, rightSlope: Double)


final class CubicSplineSlopes(
                               boundary: CubicSplineBoundary
                             ) extends InterpolationNodeSlopes[Double]:

  override def apply(
                      x: Array[Double],
                      y: Array[Double]
                    ): Array[Double] =
    require(x.length == y.length, "x and y must have the same length")
    require(x.length >= 2, "At least two points are required")

    val n = x.length

    val h = new Array[Double](n - 1)

    var i = 0
    while i < n - 1 do
      h(i) = x(i + 1) - x(i)
      require(h(i) > 0.0, "x must be strictly increasing")
      i += 1

    val lower = new Array[Double](n - 1)
    val diag = new Array[Double](n)
    val upper = new Array[Double](n - 1)
    val rhs = new Array[Double](n)

    boundary match

      // ----------------------------------------------------------
      // Natural spline
      //
      // S''(x0) = 0
      // S''(xn) = 0
      //
      // 2*m0 + m1 = 3*d0
      //
      // m(n-2) + 2*m(n-1) = 3*d(n-2)
      // ----------------------------------------------------------

      case CubicSplineBoundary.Natural =>
        diag(0) = 2.0
        upper(0) = 1.0
        rhs(0) =
          3.0 * (y(1) - y(0)) / h(0)

        i = 1
        while i < n - 1 do
          lower(i - 1) = h(i)
          diag(i) = 2.0 * (h(i - 1) + h(i))
          upper(i) = h(i - 1)

          rhs(i) =
            3.0 * (
              h(i) * (y(i) - y(i - 1)) / h(i - 1) +
                h(i - 1) * (y(i + 1) - y(i)) / h(i)
              )

          i += 1

        lower(n - 2) = 1.0
        diag(n - 1) = 2.0
        rhs(n - 1) =
          3.0 * (y(n - 1) - y(n - 2)) / h(n - 2)

      // ----------------------------------------------------------
      // Clamped spline
      //
      // S'(x0) = leftSlope
      // S'(xn) = rightSlope
      // ----------------------------------------------------------

      case CubicSplineBoundary.Clamped(leftSlope, rightSlope) =>
        diag(0) = 1.0
        rhs(0) = leftSlope

        i = 1
        while i < n - 1 do
          lower(i - 1) = h(i)
          diag(i) = 2.0 * (h(i - 1) + h(i))
          upper(i) = h(i - 1)

          rhs(i) =
            3.0 * (
              h(i) * (y(i) - y(i - 1)) / h(i - 1) +
                h(i - 1) * (y(i + 1) - y(i)) / h(i)
              )

          i += 1

        lower(n - 2) = 0.0
        diag(n - 1) = 1.0
        rhs(n - 1) = rightSlope

    new TridiagonalOperator(
      lower,
      diag,
      upper
    ).solve(rhs)

case class CubicSpline1D() extends Interpolation1D[Double] {
  override def interpolate(xq: Double, x: Array[Double], y: Array[Double], slopes: InterpolationNodeSlopes[Double]): Double = {
    val n = x.length
    require(n >= 2, "At least two points are required")
    require(xq >= x(0) && xq <= x(n - 1), "xq is out of bounds")

    val m = slopes(x, y)

    val i = Interpolation1D.binarySearch(x, xq)
    if (i == 0) {
      return y(0)
    } else if (i == n) {
      return y(n - 1)
    }

    val h = x(i) - x(i - 1)
    val t = (xq - x(i - 1)) / h

    val a = m(i - 1) * h - (y(i) - y(i - 1))
    val b = -m(i) * h + (y(i) - y(i - 1))

    (1.0 - t) * y(i - 1) + t * y(i) + t * (1.0 - t) * (a * (1.0 - t) + b * t)
  }

  override def firstDerivative(xq: Double, x: Array[Double], y: Array[Double], slopes: InterpolationNodeSlopes[Double]): Double = ???
}