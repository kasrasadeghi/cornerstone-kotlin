package pass

import main.Sexp

fun (Sexp).callStmt(): Sexp {
  list = list.map {
    when (it.value) {
      "def" -> it.also {initLocals(it)}.subSexp(it.value, 0, 3).push(it[3].`do`())
      else -> it
    }
  }
  return this
}

private fun (Sexp).`do`(): Sexp {
  list = list.map {
    when (it.value) {
      "call" -> it.call()
      "do" -> it.`do`()
      "if" -> it.subSexp(it.value, 0, 1).push(it[1].`do`())
      else -> it
    }
  }
  return this
}

private fun (Sexp).call(): Sexp {
  if ("void" == this[2].value) return this
  return Sexp("let", listOf(Sexp("$" + newLocal().toString()), this))
}