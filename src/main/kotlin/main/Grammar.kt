package main

import parse.parse

fun parseGrammar(filePath: String): Map<String, Texp> {
  val grammarTexp = parse(filePath)[0]
  return grammarTexp.list.map { it.value to it[0] }.toMap()
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
fun validate(grammar: Map<String, Texp>, program: Texp, type: String): Boolean {
  // match primitive rule
  fun matchValue(type: String): Boolean {
    println("  match ${program.value} with $type")
    return when (type) {
      "#name" -> true
      "#nat" -> program.value[0] != '-' && program.value.toIntOrNull() != null
      "#int" -> program.value.toIntOrNull() != null
      "#string" -> program.value[0] == '"' && program.value.last() == '"'
      "#type" -> true
      program.value -> true
      else -> if (type[0] == '#') throw GrammarError("unmatched value class: $type")
              else                throw GrammarError("$type does not match ${program.value}")
    }
  }

  // match child sequences using naive regular expressions
  fun matchChildren(rule: Texp): Boolean {
    //region check(current kleene restrictions for rule) //TODO remove
    //TODO grammar validation tests
    //TODO move the checks for kleene restrictions to a separate validation of only the grammar

    // only use *

    // only one kleene-* per seq
    check(rule.list.filter { it.value == "*"}.count() in setOf(0, 1))

    // if contains kleene star, must be last one
    check(rule.list.indexOfFirst { it.value == "*" } in setOf(-1, rule.list.size - 1))

    // check that kleene star only has one child
    check(rule.list.filter { it.value == "*"}.map { it.size() }.all { it == 1 })

    //endregion

    //TODO consider: just having *Type
    // convert to strings where *'s get appended to front
    val types = rule.list.map { if (it.size() == 0) it.value else it.value + it[0].value }
    println("  matching ${program.list} with $types")

    if (types.last()[0] == '*') {
      check(types.size - 1 <= program.list.size) { "length of $program is not at least ${types.size - 1}" }

      // check things before star
      program.list.zip(types).take(types.size - 1).forEach { (child, childType) ->
        validate(grammar, child, childType)
      }

      // check star
      program.list.drop(types.size - 1).forEach { child ->
        validate(grammar, child, types.last().substring(1))
      }
    } else {
      check(types.size == program.list.size)
          { "when lacking a kleene-*, the Texp must have the same length as the rule" }
      program.list.zip(types).forEach { (child, childType) ->
        validate(grammar, child, childType)
      }
    }
    return true
  }

  println(type)

  // primitive type
  if (type[0].isLowerCase() || type[0] == '#') {
    println("  primitive matching")
    return matchValue(type)
  }

  // non primitive types need to be looked up
  val rule = grammar[type]!!

  // choice operator
  if (rule.value == "|") {
    val choices = rule.list.map { it.value }
    println("  matching $program with choice of $choices")

    choices.forEach { choice ->
      try {
        if (validate(grammar, program, choice)) {
          println("  $program matches choice of $choice")
          return true
        }
      } catch (e: IllegalStateException) {}
    }

    throw GrammarError("\n$program did not match any choice of \n$choices")
  }

  // non primitive production
  if (!matchValue(rule.value)) throw GrammarError("")
  matchChildren(rule)
  return true
}

class GrammarError(message: String) : IllegalStateException(message)