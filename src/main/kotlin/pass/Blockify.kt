package pass

import main.Texp

fun (Texp).blockify(): Texp =
  map {
    when (it.value) {
      "def" -> container(it, 3).also { it[3].`do`() }
      else  -> it
    }
  }

private fun container(s: Texp, i: Int) = with(s) {
  subTexp(value, 0, i).push(subTexp("do", i, size()))
}

private fun (Texp).`do`(): Texp =
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

----------------------------------

(@Def this:(def _ _ _ *stmts)
  (gen (def _ _ _ (do *stmts))))

 */