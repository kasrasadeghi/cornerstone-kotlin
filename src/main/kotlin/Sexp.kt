data class Sexp(var value: String, var list: ArrayList<Sexp> = ArrayList()) {
  fun push(el: Sexp) {
    list.add(el)
  }

  override fun toString(): String {
    return toString(0)
  }

  private fun toString(level: Int): String {
    return (0 until level).joinToString("", "", "") {"  "} +
        "$value\n" +
        list.joinToString("", "", "") { it.toString(level + 1)}
  }
}