package main

data class Texp(var value: String, var list: ArrayList<Texp> = ArrayList()) {
  constructor(value: String, vararg list: Texp) : this(value, ArrayList(listOf(*list)))
  constructor(value: String, list: List<Texp>) : this(value, ArrayList(list))

  companion object {
    var PREF_LISP = true
  }

  fun push(el: Texp): Texp {
    list.add(el)
    return this
  }

  override fun toString(): String {
    return if (PREF_LISP) toLisp() else toString(0)
  }

  private fun toString(level: Int): String {
    fun <T> (Iterable<T>).simpleJoin(f: (T) -> CharSequence) = joinToString("", "", "", transform = f)
    return (0 until level).simpleJoin {"  "} +
        "$value\n" +
        list.simpleJoin { it.toString(level + 1)}
  }

  private fun toLisp(): String {
    return if (list.isNotEmpty()) "($value ${list.joinToString(separator = " ")})" else value
  }

  operator fun get(i: Int) = list[i]

  fun size() = list.size

  /**
   * Make a subTexp on [lower, upper)
   */
  fun subTexp(value: String, lower: Int, upper: Int) =
      Texp(value, list.subList(lower, upper))

  fun map(f: (Texp) -> Texp): Texp {
    list = ArrayList(list.map(f))
    return this
  }

  fun flatMap(f: (Texp) -> List<Texp>): Texp {
    list = ArrayList(list.flatMap(f))
    return this
  }

  fun doAt(vararg `i's`: Int, f: (Texp) -> Texp): Texp {
    for (i in `i's`) {
      list[i] = f(list[i])
    }
    return this
  }
}