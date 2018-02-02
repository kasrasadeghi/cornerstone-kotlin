package pass

import main.Sexp

fun (Sexp).Blockify(): Sexp {
  list = list.map {
    when (it.value) {
      "def" -> container(it, 3).also { it[3].`do`() }
      else  -> it
    }
  }
  return this
}

private fun container(s: Sexp, i: Int) = with(s) {
  subSexp(value, 0, i).push(subSexp("do", i, size()))
}

private fun (Sexp).`do`(): Sexp {
  list = list.map {
    when (it.value) {
      "do" -> it.`do`()
      "if" -> container(it, 1)
      else -> it
    }
  }
  return this
}