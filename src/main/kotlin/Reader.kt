import java.io.File

class Reader(filename: String) {
  var array     = File(filename).readBytes()
  var offset    = 0

  fun hasNext() = array.size > offset

  fun get()     = array[offset++].toChar()

  fun peek()    = array[offset].toChar()

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