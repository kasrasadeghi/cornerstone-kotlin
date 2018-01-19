fun (Sexp).Become(): Sexp {
  list = list.map {
    when (it.value) {
      "def" -> it
          .subSexp(it.value, 0, 3)
          .push(it[3].`do`())
      else  -> it
    }
  }
  return this
}

private fun (Sexp).`do`(): Sexp {
  list = list.map {
    when (it.value) {
      "become" -> it.become()
      "do" -> it.`do`()
      "if" -> it
          .subSexp(it.value, 0, 1)
          .push(it[1].`do`())
      else -> it
    }
  }

  //TODO put inserts into the surrounding list
  return this
}

private fun (Sexp).become(): Sexp {
  val returnType = this[2]
  value = "call-tail"
  return if (returnType.value == "void") {
    //TODO consider making this a "do"
    Sexp("insert", listOf(this, Sexp("return", listOf(Sexp("void")))))
  } else {
    Sexp("return", listOf(this, returnType))
  }
}