package org.tk.marmot.math.linalg

final class TridiagonalOperator(
                                 val lower: Array[Double], // lower(i) = A(i+1, i), length n-1
                                 val diag: Array[Double],  // diag(i)  = A(i, i),   length n
                                 val upper: Array[Double]  // upper(i) = A(i, i+1), length n-1
                               ) {

  private val n = diag.length

  require(n > 0, "Matrix must not be empty")
  require(lower.length == n - 1, "Invalid lower diagonal length")
  require(upper.length == n - 1, "Invalid upper diagonal length")

  /** Solve A*x = rhs using the Thomas algorithm. */
  def solve(rhs: Array[Double]): Array[Double] = {
    require(rhs.length == n, "RHS vector has incorrect length")

    // Copies because the algorithm modifies the coefficients.
    val c = upper.clone()
    val d = rhs.clone()
    val b = diag.clone()

    // Forward elimination
    var i = 1
    while (i < n) {
      if (b(i - 1) == 0.0)
        throw new ArithmeticException("Zero pivot in tridiagonal system")

      val factor = lower(i - 1) / b(i - 1)

      b(i) -= factor * c(i - 1)
      d(i) -= factor * d(i - 1)

      i += 1
    }

    if (b(n - 1) == 0.0)
      throw new ArithmeticException("Zero pivot in tridiagonal system")

    // Back substitution
    val x = new Array[Double](n)

    x(n - 1) = d(n - 1) / b(n - 1)

    i = n - 2
    while (i >= 0) {
      x(i) = (d(i) - c(i) * x(i + 1)) / b(i)
      i -= 1
    }

    x
  }
}
