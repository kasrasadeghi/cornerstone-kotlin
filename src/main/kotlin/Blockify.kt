fun (Sexp).blockify(): Sexp {
  list = list.map {
    when (it.value) {
      "def" -> container(it, 3).also { `do`(it[3]) }
      else  -> it
    }
  }
  return this
}

fun container(s: Sexp, i: Int) = with(s) {
  subSexp(value, 0, i).push(subSexp("do", i, size()))
}

fun `do`(s: Sexp): Sexp {
  s.list = s.list.map {
    when (it.value) {
      "do" -> `do`(it)
      "if" -> container(it, 1)
      else -> it
    }
  }
  return s
}