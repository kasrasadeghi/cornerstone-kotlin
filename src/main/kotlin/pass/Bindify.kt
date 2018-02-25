package pass

import main.Sexp

fun (Sexp).bindify(): Sexp =
    map {
      when (it.value) {
        "def" -> it.also { initLocals(it) }.doAt(3) { it.`do`() }
        else -> it
      }
    }

private fun (Sexp).`do`(): Sexp =
    map {
      when (it.value) {
        "let" -> it.let()
        "return" -> if (it.list.size == 2) it else it.doAt(1) { it.bind() }
        "if" -> it.doAt(0) { it.bind() }.doAt(1) { it.`do`() }
        "store" -> it.doAt(0, 2) { it.bind() }
        "do" -> it.`do`()
        "auto" -> it
        else -> throw IllegalStateException()
      }
    }

private fun (Sexp).let(): Sexp = this.doAt(1) { it.expr() }

private fun (Sexp).expr(): Sexp {
  return when (value) {
    "call", "call-tail", "call-vargs"     -> this.doAt(3) { it.map { it.bind()} }
    "+", "<", ">", "<=", ">=", "!=", "==" -> this.doAt(1, 2) { it.bind() }
    "load" -> this.doAt(1) { it.bind() }
    "index" -> this.doAt(0, 2) { it.bind() }
    "cast" -> this.doAt(2) { it.bind() }
    else -> throw IllegalStateException()
  }
}

private fun (Sexp).bind(): Sexp {
  return if (this.isTall()) {
    val local = "$" + newLocal().toString()
    Sexp("bind", Sexp(local), Sexp("let", Sexp(local), this).let())
  } else this
}

private fun (Sexp).isTall(): Boolean = value in setOf(
    "call", "call-tail", "call-vargs",
    "+", "<", ">", "<=", ">=", "!=", "==",
    "load", "index", "cast")


