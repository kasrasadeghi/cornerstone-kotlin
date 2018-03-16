package main

import parse.parse

fun parseGrammar(filePath: String): Map<String, Sexp> {
  val grammarSexp = parse(filePath)[0]
  return grammarSexp.list.map { it.value to it[0] }.toMap()
}

/**
 * @throws IllegalStateException with a message about the check that failed.
 * Otherwise returns true.
 *
 * Vocab:
 * A primitive rule is a rule that does not have children and thus is only matched by value.
 *  - The two primitive rules are strings and value classes.
 *  - String are lower-case and are matched lexicographically
 *  - Value classes start with a hash ('#') and correlate with a set/regular expression.
 */
fun validate(grammar: Map<String, Sexp>, program: Sexp, type: String): Boolean {
  // match primitive rule
  fun matchValue(rule: String): Boolean {
    println("match ${program.value} with $rule")
    return if (rule[0] == '#') {
      when (rule) {
        "#name" -> true
        "#nat" -> program.value[0] != '-' && program.value.toIntOrNull() != null
        "#string" -> program.value[0] == '"' && program.value.last() == '"'
        else -> throw GrammarError("unmatched value class: $rule")
      }
    } else rule == program.value
  }

  fun matchSequence(program: Sexp, types: List<String>) {

  }

  // match child sequences using naive regular expressions
  fun matchChildren(rule: Sexp): Boolean {
    println("matching ${program.list} with ${rule.list}")

    //TODO grammar validation tests
    //TODO move the checks for kleene restrictions to a separate validation of only the grammar
    //region check(current kleene restrictions for rule)

    // only use *

    // only one kleene-* per seq
    check(rule.list.filter { it.value == "*"}.count() in setOf(0, 1))

    // if contains kleene star, must be last one
    check(rule.list.indexOfFirst { it.value == "*" } in setOf(-1, rule.list.size - 1))

    // check that kleene star only has one child
    check(rule.list.filter { it.value == "*"}.map { it.size() }.all { it == 1 })

    //endregion

    // convert to strings where *'s get appended to front
    val types = rule.list.map { if (it.size() == 0) it.value else it.value + it[0].value}

    if (types.last() == "*") {
      check(types.size - 1 <= program.list.size) { "length of $program is not at least ${types.size - 1}" }
      // check things before star
      for (match in program.list.zip(types).take(types.size - 1)) {
        val (child, childType) = match
        validate(grammar, child, childType)
      }
      for (child in program.list.drop(types.size - 1)) {
        println("hello world ${types.last()[0]}")
        validate(grammar, child, types.last())
      }
    } else {
      check(types.size == program.list.size)
      { "when lacking a kleene-*, the Texp must have the same length as the rule" }
      for (match in program.list.zip(types)) {
        val (child, childType) = match
        validate(grammar, child, childType)
      }
    }
    return true
  }

  println(type)

  if (type[0].isLowerCase() || type[0] == '#') {
    println("primitive matching")
    return matchValue(type)
  }

  val rule = grammar[type]!!

  if (rule.value == "|") {
    val choices = rule.list.map { it.value }
    println("matching \n$program with choice of $choices")

    for (choice in choices) {
      try {
        if (validate(grammar, program, choice)) return true
      } catch (e: IllegalStateException) {
        error(e.message ?: e)
      }
    }

    throw GrammarError("\n$program did not match any choice of \n$choices")
  }
  // @below, not a choice operator

  //TODO @below, which is better?
  /* return */ matchValue(rule.value) && matchChildren(rule)
  return true
}

class GrammarError(message: String) : IllegalStateException(message)

fun Iterable<String>.join(sep: String = ""): String = joinToString(separator = sep) { it }