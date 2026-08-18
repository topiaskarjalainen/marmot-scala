package org.tk.marmot.benchmarks

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import org.tk.marmot.math.interpolation.Interpolation1D

import java.util.concurrent.TimeUnit

import scala.collection.Searching.*


@State(Scope.Thread)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 2)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(3)
class SearchingBenchmark {
  private val testArraySmallFloat: Array[Float] = (0 until 30).map(_.toFloat).toArray
  private val testArrayLargeFloat: Array[Float] = (0 until 100000).map(_.toFloat).toArray
  private val testArraySmallDouble: Array[Double] = (0 until 30).map(_.toDouble).toArray
  private val testArrayLargeDouble: Array[Double] = (0 until 100000).map(_.toDouble).toArray

  @Benchmark
  def linearSearchSmallDouble(bh: Blackhole): Unit = {
    val xq = 500.0
    val index = Interpolation1D.linearSearch(testArraySmallDouble, xq)
    bh.consume(index)
  }

  @Benchmark
  def binarySearchSmallDouble(bh: Blackhole): Unit = {
    val xq = 500.0
    val index = Interpolation1D.binarySearch(testArraySmallDouble, xq)
    bh.consume(index)
  }

  @Benchmark
  def buildInSearchSmallDouble(bh: Blackhole): Unit = {
    val xq = 500.0
    val index = java.util.Arrays.binarySearch(testArraySmallDouble, xq)
    bh.consume(index)
  }

  @Benchmark
  def linearSearchLargeDouble(bh: Blackhole): Unit = {
    val xq = 500000.0
    val index = Interpolation1D.linearSearch(testArrayLargeDouble, xq)
    bh.consume(index)
  }

  @Benchmark
  def binarySearchLargeDouble(bh: Blackhole): Unit = {
    val xq = 500000.0
    val index = Interpolation1D.binarySearch(testArrayLargeDouble, xq)
    bh.consume(index)
  }

  @Benchmark
  def buildInSearchLargeDouble(bh: Blackhole): Unit = {
    val xq = 500000.0
    val index = java.util.Arrays.binarySearch(testArrayLargeDouble, xq)
    bh.consume(index)
  }

  @Benchmark
  def scalaDefinedLargeDouble(bh: Blackhole): Unit = {
    val xq = 500000.0
    val index = testArrayLargeDouble.search(xq)
    bh.consume(index)
  }


  @Benchmark
  def scalaDefinedSmallDouble(bh: Blackhole): Unit = {
    val xq = 500.0
    val index = testArraySmallDouble.search(xq)
    bh.consume(index)
  }
}
