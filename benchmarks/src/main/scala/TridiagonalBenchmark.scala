package org.tk.marmot.benchmarks

import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import org.tk.marmot.math.linalg.TridiagonalOperator

import java.util.concurrent.TimeUnit
import scala.compiletime.uninitialized

@State(Scope.Benchmark)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(0)
class TridiagonalBenchmark {

  @Param(Array("10", "100"))
  var n: Int = uninitialized

  private var lower: Array[Double] = uninitialized
  private var diag: Array[Double] = uninitialized
  private var upper: Array[Double] = uninitialized
  private var rhs: Array[Double] = uninitialized

  private var tridiagonal: TridiagonalOperator = uninitialized

  // Dense matrix constructed once during setup
  private var denseMatrix: DMatrixRMaj = uninitialized

  // Reusable EJML workspaces
  private var ejmlB: DMatrixRMaj = uninitialized
  private var ejmlX: DMatrixRMaj = uninitialized

  @Setup(Level.Trial)
  def setup(): Unit = {
    lower = Array.fill(n - 1)(-1.0)
    diag  = Array.fill(n)(4.0)
    upper = Array.fill(n - 1)(-1.0)

    rhs = Array.tabulate(n)(i => i.toDouble)

    tridiagonal = new TridiagonalOperator(
      lower,
      diag,
      upper
    )

    // ------------------------------------------------------------
    // Construct dense matrix ONCE
    // ------------------------------------------------------------

    denseMatrix = new DMatrixRMaj(n, n)

    var i = 0
    while (i < n) {
      denseMatrix.set(i, i, diag(i))

      if (i > 0)
        denseMatrix.set(i, i - 1, lower(i - 1))

      if (i < n - 1)
        denseMatrix.set(i, i + 1, upper(i))

      i += 1
    }

    // ------------------------------------------------------------
    // Preallocated EJML RHS and solution
    // ------------------------------------------------------------

    ejmlB = new DMatrixRMaj(n, 1)
    ejmlX = new DMatrixRMaj(n, 1)

    i = 0
    while (i < n) {
      ejmlB.set(i, 0, rhs(i))
      i += 1
    }
  }

  // ============================================================
  // Custom tridiagonal solver
  // ============================================================

  @Benchmark
  def thomas(blackhole: Blackhole): Unit = {
    val result = tridiagonal.solve(rhs)
    blackhole.consume(result)
  }

  // ============================================================
  // EJML dense solve
  //
  // Matrix is NOT constructed here.
  // RHS and result are allocated here.
  // ============================================================

  // @Benchmark
  def ejmlDense(blackhole: Blackhole): Unit = {
    val b = new DMatrixRMaj(n, 1)

    var i = 0
    while (i < n) {
      b.set(i, 0, rhs(i))
      i += 1
    }

    val x = new DMatrixRMaj(n, 1)

    CommonOps_DDRM.solve(denseMatrix, b, x)

    blackhole.consume(x)
  }

  // ============================================================
  // EJML dense solve with everything preallocated
  // ============================================================

  @Benchmark
  def ejmlDensePreallocated(blackhole: Blackhole): Unit = {
    CommonOps_DDRM.solve(
      denseMatrix,
      ejmlB,
      ejmlX
    )

    blackhole.consume(ejmlX)
  }
}
