data class Sexp(var value: String, var list: List<Sexp> = ArrayList()) {
  fun push(el: Sexp): Sexp {
    list += el
    return this
  }

  override fun toString(): String {
    return toString(0)
  }

  private fun toString(level: Int): String {
    return (0 until level).joinToString("", "", "") {"  "} +
        "$value\n" +
        list.joinToString("", "", "") { it.toString(level + 1)}
  }

  operator fun get(i: Int) = list[i]

  fun size() = list.size

  /**
   * Make a subSexp on [lower, upper)
   */
  fun subSexp(value: String, lower: Int, upper: Int) =
      Sexp(value, list.subList(lower, upper))
}