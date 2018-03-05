package pass

import main.Sexp

fun (Sexp).becomeify(): Sexp =
  map {
    when (it.value) {
      "def" -> it.doAt(3) { it.`do`() }
      else  -> it
    }
  }

private fun (Sexp).`do`(): Sexp =
  map {
    when (it.value) {
      "become" -> it.become()
      "do" -> it.`do`()
      "if" -> it.doAt(1) { it.`do`() }
      else -> it
    }
  }.flatMap {
    when (it.value) {
      "block" -> {it.list}
      else -> listOf(it)
    }
  }

private fun (Sexp).become(): Sexp {
  val returnType = this[2]
  value = "call-tail"
  return if (returnType.value == "void") {
    //TODO consider making this a "do"
    Sexp("block", this, Sexp("return", Sexp("void")))
  } else {
    Sexp("return", this, returnType)
  }
}

/*

(Stmt become
  (if-else (== [2] "void")
    (gen (block $this (return void))
    (gen (return $this [2]))))

----------------------------------

(@Stmt this:(become function-name arg-types return-type args)
  (if-else (== return-type "void")
    (gen (block $this (return void))
    (gen (return $this $return-type))))

----------------------------------

(@Stmt this:(become _ _ return-type _)
  (if-else (== return-type "void")
    (gen (block this (return `void`))
    (gen (return this return-type))))

----------------------------------

(@Stmt this:(become _ _ return-type _)
  (if-else (== return-type "void")
    (gen (block this (return return-type))
    (gen (return this return-type))))

 */