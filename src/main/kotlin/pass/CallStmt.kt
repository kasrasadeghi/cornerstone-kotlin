package pass

import main.Sexp

fun (Sexp).callStmt(): Sexp =
  map {
    when (it.value) {
      "def" -> it.also { initLocals(it) }.doAt(3) { it.`do`() }
      else -> it
    }
  }


private fun (Sexp).`do`(): Sexp =
  map {
    when (it.value) {
      "call", "call-tail", "call-vargs" -> it.call()
      "do" -> it.`do`()
      "if" -> it.doAt(1) { it.`do`() }
      else -> it
    }
  }

private fun (Sexp).call(): Sexp {
  if ("void" == this[2].value) return this
  return Sexp("let", Sexp("$" + newLocal().toString()), this)
}

/*
(@Stmt s
  (CallLike (do
    (let local (call newlocal (types) (args)))
    (gen (let local s))))
*/