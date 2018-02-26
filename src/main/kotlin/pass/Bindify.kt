package pass

import main.Sexp

fun (Sexp).bindify(): Sexp =
    map {
      when (it.value) {
        "def" -> it.also { initLocals(it) }.doAt(3) { it.`do`() }
        else  -> it
      }
    }

private fun (Sexp).`do`(): Sexp =
    map {
      when (it.value) {
        "let"        -> it.let()
        "return"     -> if (it.list.size == 1) it else it.doAt(0) { it.bind() }
        "if"         -> it.doAt(0) { it.bind() }.doAt(1) { it.`do`() }
        "store"      -> it.doAt(0, 2) { it.bind() }
        "do"         -> it.`do`()
        "auto"       -> it
        "call",
        "call-tail",
        "call-vargs" -> it.doAt(3) { it.map { it.bind() } }
        else         -> throw IllegalStateException("illegal statement found \n${this}")
      }
    }

private fun (Sexp).let(): Sexp = doAt(1) { it.expr() }

private fun (Sexp).expr(): Sexp {
  return when (value) {
    "call", "call-tail", "call-vargs"     -> doAt(3) { it.map { it.bind() } }
    "+", "<", ">", "<=", ">=", "!=", "==" -> doAt(1, 2) { it.bind() }
    "load"                                -> doAt(1) { it.bind() }
    "index"                               -> doAt(0, 2) { it.bind() }
    "cast"                                -> doAt(2) { it.bind() }
    else                                  -> throw IllegalStateException("non expression found in expression")
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


