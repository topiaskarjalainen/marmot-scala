package org.tk.marmot.math.linalg

import org.scalatest.funsuite.AnyFunSuiteLike

class TridiagonalOperatorTest extends AnyFunSuiteLike {

  private def assertArrayClose(
                                actual: Array[Double],
                                expected: Array[Double],
                                tolerance: Double = 1e-10
                              ): Unit = {
    assert(actual.length == expected.length)

    actual.zip(expected).zipWithIndex.foreach {
      case ((a, e), i) =>
        assert(
          math.abs(a - e) <= tolerance,
          s"Index $i: expected $e but got $a"
        )
    }
  }

  private def multiply(
                        lower: Array[Double],
                        diag: Array[Double],
                        upper: Array[Double],
                        x: Array[Double]
                      ): Array[Double] = {
    val n = diag.length
    val result = new Array[Double](n)

    var i = 0
    while (i < n) {
      result(i) = diag(i) * x(i)

      if (i > 0)
        result(i) += lower(i - 1) * x(i - 1)

      if (i < n - 1)
        result(i) += upper(i) * x(i + 1)

      i += 1
    }

    result
  }

  test("solve a simple 3x3 tridiagonal system") {
    val operator = new TridiagonalOperator(
      lower = Array(1.0, 1.0),
      diag = Array(4.0, 4.0, 4.0),
      upper = Array(1.0, 1.0)
    )

    val rhs = Array(6.0, 7.0, 6.0)

    val result = operator.solve(rhs)

    assertArrayClose(
      result,
      Array(
        1.2142857142857144, 1.1428571428571428, 1.2142857142857142
      )
    )
  }

  test("solve a 4x4 tridiagonal system") {
    val operator = new TridiagonalOperator(
      lower = Array(1.0, 2.0, 1.0),
      diag = Array(4.0, 5.0, 6.0, 4.0),
      upper = Array(2.0, 1.0, 3.0)
    )

    val expected = Array(1.0, 2.0, 3.0, 4.0)

    val rhs = multiply(
      operator.lower,
      operator.diag,
      operator.upper,
      expected
    )

    val result = operator.solve(rhs)

    assertArrayClose(result, expected)
  }

  test("solve a diagonal system") {
    val operator = new TridiagonalOperator(
      lower = Array(0.0, 0.0),
      diag = Array(2.0, 4.0, 5.0),
      upper = Array(0.0, 0.0)
    )

    val rhs = Array(4.0, 8.0, 15.0)

    val result = operator.solve(rhs)

    assertArrayClose(
      result,
      Array(2.0, 2.0, 3.0)
    )
  }

  test("solve identity matrix") {
    val operator = new TridiagonalOperator(
      lower = Array(0.0, 0.0),
      diag = Array(1.0, 1.0, 1.0),
      upper = Array(0.0, 0.0)
    )

    val rhs = Array(3.0, -2.0, 7.5)

    val result = operator.solve(rhs)

    assertArrayClose(result, rhs)
  }

  test("solve a larger system and reproduce the right hand side") {
    val n = 100

    val lower = Array.fill(n - 1)(-1.0)
    val diag = Array.fill(n)(4.0)
    val upper = Array.fill(n - 1)(-1.0)

    val operator = new TridiagonalOperator(
      lower = lower,
      diag = diag,
      upper = upper
    )

    val expected = Array.tabulate(n)(i => math.sin(i * 0.1))

    val rhs = multiply(
      lower,
      diag,
      upper,
      expected
    )

    val result = operator.solve(rhs)

    assertArrayClose(
      result,
      expected,
      tolerance = 1e-9
    )
  }

  test("does not modify the right hand side") {
    val operator = new TridiagonalOperator(
      lower = Array(1.0, 1.0),
      diag = Array(4.0, 4.0, 4.0),
      upper = Array(1.0, 1.0)
    )

    val rhs = Array(6.0, 7.0, 6.0)
    val originalRhs = rhs.clone()

    operator.solve(rhs)

    assert(rhs.sameElements(originalRhs))
  }

  test("rejects an incorrectly sized right hand side") {
    val operator = new TridiagonalOperator(
      lower = Array(1.0, 1.0),
      diag = Array(4.0, 4.0, 4.0),
      upper = Array(1.0, 1.0)
    )

    assertThrows[IllegalArgumentException] {
      operator.solve(Array(1.0, 2.0))
    }
  }

  test("rejects invalid diagonal sizes") {
    assertThrows[IllegalArgumentException] {
      new TridiagonalOperator(
        lower = Array(1.0),
        diag = Array(4.0, 4.0, 4.0),
        upper = Array(1.0, 1.0)
      )
    }
  }
}
