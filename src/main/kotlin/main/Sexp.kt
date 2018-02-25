package main

data class Sexp(var value: String, var list: ArrayList<Sexp> = ArrayList()) {
  constructor(value: String, vararg list: Sexp) : this(value, ArrayList(listOf(*list)))
  constructor(value: String, list: List<Sexp>) : this(value, ArrayList(list))

  fun push(el: Sexp): Sexp {
    list.add(el)
    return this
  }

  override fun toString(): String {
    return toString(0)
  }

  private fun toString(level: Int): String {
    fun <T> (Iterable<T>).simpleJoin(f: (T) -> CharSequence) = joinToString("", "", "", transform = f)
    return (0 until level).simpleJoin {"  "} +
        "$value\n" +
        list.simpleJoin { it.toString(level + 1)}
  }

  operator fun get(i: Int) = list[i]

  fun size() = list.size

  /**
   * Make a subSexp on [lower, upper)
   */
  fun subSexp(value: String, lower: Int, upper: Int) =
      Sexp(value, list.subList(lower, upper))

  fun map(f: (Sexp) -> Sexp): Sexp {
    list = ArrayList(list.map(f))
    return this
  }

  fun flatMap(f: (Sexp) -> List<Sexp>): Sexp {
    list = ArrayList(list.flatMap(f))
    return this
  }

  fun doAt(vararg `i's`: Int, f: (Sexp) -> Sexp): Sexp {
    for (i in `i's`) {
      list[i] = f(list[i])
    }
    return this
  }
}