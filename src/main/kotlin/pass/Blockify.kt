package pass

import main.Sexp

fun (Sexp).blockify(): Sexp =
  map {
    when (it.value) {
      "def" -> container(it, 3).also { it[3].`do`() }
      else  -> it
    }
  }

private fun container(s: Sexp, i: Int) = with(s) {
  subSexp(value, 0, i).push(subSexp("do", i, size()))
}

private fun (Sexp).`do`(): Sexp =
  map {
    when (it.value) {
      "do" -> it.`do`()
      "if" -> container(it, 1)
      else -> it
    }
  }

/*

(@(* Stmt) => (do (* this)))
(@(* Stmt) => (do this))

(@Def this:(def _ _ _ *stmts)
  (gen (def _ _ _ (do *stmts))))

 */