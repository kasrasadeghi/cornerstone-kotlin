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
        else -> throw GrammarError("unmatched value class: $rule")
      }
    } else rule == program.value
  }

  // match child sequences using naive regular expressions
  fun matchChildren(rule: Sexp): Boolean {
    println("matching ${program.list} with ${rule.list}")
    val types = rule.list

    //TODO grammar validation tests
    //TODO move the checks for kleene restrictions to a separate validation of only the grammar
    //region check(current kleene restrictions)

    // only use *

    // only one kleene-* per seq
    check(types.filter { it.value == "*"}.count() in setOf(0, 1))

    // if contains kleene star, must be last one
    check(types.indexOfFirst { it.value == "*" } in setOf(-1, types.size - 1))

    // check that kleene star only has one child
    check(types.filter { it.value == "*"}.map { it.size() }.all { it == 1 })

    //endregion

    if (types.last().value == "*") {
      check(types.size - 1 <= program.list.size) { "length of $program is not at least ${types.size - 1}" }
      // check things before star
      for (match in program.list.zip(types).take(types.size - 1)) {
        //TODO check match with recursion
        print(match)
      }
      for (child in program.list.drop(types.size - 1)) {
        print(types.last()[0])
        validate(grammar, child, types.last()[0].value)
      }
    } else {
      check(types.size == program.list.size)
      { "when lacking a kleene-*, the Texp must have the same length as the rule" }
      for (match in program.list.zip(types)) {
        //TODO check match with recursion
        print(match)
      }
    }
    return true
  }

  if (type[0].isLowerCase()) {
    println("primitive matching")
    return matchValue(type)
  }

  val rule = grammar[type]!!
  println(rule.list)

  if (rule.value == "|") {
    println("matching choice")
    println(rule)
    //TODO finish matching choice
    return true
  }

  // @below, not a choice operator

  return matchValue(rule.value) && matchChildren(rule)
}

class GrammarError(message: String) : IllegalStateException(message)