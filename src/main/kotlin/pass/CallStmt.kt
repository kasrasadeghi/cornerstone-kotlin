package pass

import main.Texp

fun (Texp).callStmt(): Texp =
  map {
    when (it.value) {
      "def" -> it.also { initLocals(it) }.doAt(3) { it.`do`() }
      else -> it
    }
  }


private fun (Texp).`do`(): Texp =
  map {
    when (it.value) {
      "call", "call-tail", "call-vargs" -> it.call()
      "do" -> it.`do`()
      "if" -> it.doAt(1) { it.`do`() }
      else -> it
    }
  }

private fun (Texp).call(): Texp {
  if ("void" == this[2].value) return this
  return Texp("let", Texp("$" + newLocal().toString()), this)
}

/*
(@Stmt s
  (CallLike (do
    (let local (call newlocal (types) (args)))
    (gen (let local s))))
*/