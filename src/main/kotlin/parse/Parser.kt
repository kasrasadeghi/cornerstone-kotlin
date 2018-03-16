package parse

import main.Texp
import java.io.File
import java.io.IOException

fun parse(filename: String): Texp {
  val r = Reader(filename).removeComments()
  return pProgram(filename, r)
}

fun (Reader).removeComments(): Reader {
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
  return this
}

fun pProgram(filename: String, r: Reader): Texp {
  val program = Texp(File(filename).name)
  while (r.hasNext()) {
    program.push(pTexp(r))
    pWhitespace(r)
  }
  pWhitespace(r)
  return program
}

fun pTexp(r: Reader): Texp {
  pWhitespace(r)
  if (r.peek() == '(') return pList(r)
  return pAtom(r)
}

fun pList(r: Reader): Texp {
  assert(r.get() == '(')
  val curr = Texp(pWord(r))
  while (r.peek() != ')') {
    if (!r.hasNext()) {
      throw IOException("unmatched paren for list")
    }
    curr.push(pTexp(r))
    pWhitespace(r)
  }

  assert(r.get() == ')')
  return curr
}

fun pAtom(r: Reader): Texp {
  fun pBoundedAtom(delim: Char, errorMessage: String): (Reader) -> Texp {
    return fun (r: Reader): Texp {
      var string = ""
      assert(r.peek() == delim)
      string += r.get()

      while (!(r.peek() == delim && r.prev() != '\\')) {
        if (!r.hasNext()) {
          throw IOException(errorMessage)
        }
        string += r.get()
      }

      assert(r.peek() == delim && r.prev() != '\\')
      string += r.get()
      return Texp(string)
    }
  }

  return when (r.peek()) {
    '\"' -> pBoundedAtom('\"', "unmatched quote for string")(r)
    '\'' -> pBoundedAtom('\'', "unmatched apostrophe for char")(r)
    else -> Texp(pWord(r))
  }
}

operator fun <T> (StringBuilder).plusAssign(a: T) {
  this.append(a)
}

fun pWord(r: Reader): String {
  val string = StringBuilder()
  pWhitespace(r)
  if (!r.hasNext()) {
    throw IOException("expecting a word at the end of the file")
  }
  while (r.hasNext() && r.peek() != '(' && r.peek() != ')' && !r.peek().isWhitespace()) {
    string += r.get()
  }
  return string.toString()
}

fun pWhitespace(r: Reader) {
  while(r.hasNext() && r.peek().isWhitespace()) {
    r.get()
  }
}
