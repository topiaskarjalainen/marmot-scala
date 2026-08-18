package org.tk.marmot
package core

object Util {
  inline def fastHash(p: Product): Int = scala.util.hashing.MurmurHash3.productHash(p, scala.util.hashing.MurmurHash3.productSeed, true)
}
