package pass

import main.Texp

fun (Texp).becomeify(): Texp =
  map {
    when (it.value) {
      "def" -> it.doAt(3) { it.`do`() }
      else  -> it
    }
  }

private fun (Texp).`do`(): Texp =
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

private fun (Texp).become(): Texp {
  val returnType = this[2]
  value = "call-tail"
  return if (returnType.value == "void") {
    //TODO consider making this a "do"
    Texp("block", this, Texp("return", Texp("void")))
  } else {
    Texp("return", this, returnType)
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