package parse

import java.io.File

class Reader(filename: String?, array: String = "") {
  var array     = if (filename != null) File(filename).readBytes() else array.toByteArray()
  var offset    = 0

  fun hasNext() = array.size > offset

  fun get()     = array[offset++].toChar()

  fun peek()    = if (offset == array.size) { 0.toChar() } else array[offset].toChar()

  fun prev()    = array[offset - 1].toChar()

  fun reset()   { offset = 0 }

  operator fun get(i: Int) = array[i].toChar()

  operator fun set(i: Int, c: Char) {
    array[i] = c.toByte()
  }

  override fun toString(): String {
    return String(array)
  }
}