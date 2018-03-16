package pass

import main.Texp

fun (Texp).bindify(): Texp =
    map {
      when (it.value) {
        "def" -> it.also { initLocals(it) }.doAt(3) { it.`do`() }
        else  -> it
      }
    }

private fun (Texp).`do`(): Texp =
    map {
      when (it.value) {
        "let"        -> it.let()
        "return"     -> if (it.list.size == 1) it else
                        it.doAt(0)    { it.bind() }
        "if"         -> it.doAt(0)    { it.bind() }.doAt(1) { it.`do`() }
        "store"      -> it.doAt(0, 2) { it.bind() }
        "do"         -> it.`do`()
        "auto"       -> it
        "call",
        "call-tail",
        "call-vargs" -> it.doAt(3) { it.map { it.bind() } }
        else         -> throw IllegalStateException("illegal statement found \n$it")
      }
    }

private fun (Texp).let(): Texp = doAt(1) { it.expr() }

private fun (Texp).expr(): Texp {
  return when (value) {
    "call", "call-tail", "call-vargs"     -> doAt(3)    { it.map { it.bind() } }
    "+", "<", ">", "<=", ">=", "!=", "==" -> doAt(1, 2) { it.bind() }
    "load"                                -> doAt(1)    { it.bind() }
    "index"                               -> doAt(0, 2) { it.bind() }
    "cast"                                -> doAt(2)    { it.bind() }
    else                                  -> throw IllegalStateException("expecting expression at \n${this}")
  }
}

private fun (Texp).bind(): Texp {
  return if (this.isTall()) {
    val local = "$" + newLocal().toString()
    Texp("bind", Texp(local), Texp("let", Texp(local), this).let())
  } else this
}

private fun (Texp).isTall(): Boolean = value in setOf(
    "call", "call-tail", "call-vargs",
    "+", "<", ">", "<=", ">=", "!=", "==",
    "load", "index", "cast")

/*
(-> bind e:Expr Value
  (when e
    (Value e)
    (else  (do
      (let local (call local.new (types) (args)))
      (gen (bind local (let local e)))))))

(@Expr e
  (context e
    ((let #name e) (let #name e))
    (else (bind e))
*/

