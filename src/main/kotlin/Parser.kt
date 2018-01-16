import java.io.IOException

fun pProgram(filename: String, r: Reader): Sexp {
  //TODO
  return Sexp("", listOf())
}

fun (Reader).removeComments() {
  var mode = 0
  while (hasNext()) {
    val c = peek()

    when (mode) {
      -1 -> when (c) {
        '\n' -> mode = 0
        else -> this[offset] = ' '
      }
      0 -> when (c) {
        '\"' -> mode = 1
        '\'' -> mode = 2
        ';'  -> { mode = -1; this[offset] = ' ' }
      }
      1 -> if ('\"' == c) mode = 0
      2 -> if ('\'' == c) mode = 0
    }
    get()
  }
  reset()
}

fun parse(filename: String): Sexp {
  val r: Reader = try {
    Reader(filename)
  } catch (e: IOException) {
    throw IllegalArgumentException("backbone: error reading file \"$filename\"")
  }
  r.removeComments()
  print(r)
  return pProgram(filename, r)
}