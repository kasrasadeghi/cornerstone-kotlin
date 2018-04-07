package main

import parse.parse

fun parseGrammar(filePath: String): Map<String, Texp> {
  val grammarTexp = parse(filePath)[0]
  return grammarTexp.list.map { it.value to it[0] }.toMap()
}

fun validateGrammar(grammar: Map<String, Texp>) {
  for ((key, rule) in grammar) {
    // only use *
    //TODO

    // only one kleene-* per seq
    check(rule.list.filter { it.value == "*"}.count() in setOf(0, 1))

    // if contains kleene star, must be last one
    check(rule.list.indexOfFirst { it.value == "*" } in setOf(-1, rule.list.size - 1))

    // check that kleene star only has one child
    check(rule.list.filter { it.value == "*"}.map { it.size() }.all { it == 1 })
  }
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
fun validate(grammar: Map<String, Texp>, program: Texp, type: String): Result {
  // match primitive rule
  fun matchValue(type: String): Result {
    val result = Result("  match value:${program.value} with $type")
    val match = when (type) {
      "#name"       -> true
      "#nat"        -> program.value[0] != '-' && program.value.toIntOrNull() != null
      "#int"        -> program.value.toIntOrNull() != null
      "#string"     -> program.value[0] == '"' && program.value.last() == '"'
      "#type"       -> true
      program.value -> true
      else          -> false
    }

    return result * Result(when {
      match          -> Pair("$program matches $type", true)
      type[0] == '#' -> Pair("unmatched value class: $type", false)
      else           -> Pair("$type does not match ${program.value}", false)
    })
  }

  // match child sequences using naive regular expressions
  fun matchChildren(rule: Texp): Result {

    //TODO consider: just having *Type
    // convert to strings where *'s get appended to front
    val types = rule.list.map { if (it.size() == 0) it.value else it.value + it[0].value }
    val result = Result("  matching ${program.list} with $types")

    if (types.last()[0] == '*') {
      result.check(types.size - 1 <= program.list.size) { "length of $program is not at least ${types.size - 1}" }
      if (result.err())

      // check things before star
      program.list.zip(types).take(types.size - 1).forEach { (child, childType) ->
        result * validate(grammar, child, childType)
      }

      // check star
      program.list.drop(types.size - 1).forEach { child ->
        result * validate(grammar, child, types.last().substring(1))
      }
    } else {
      result.check(types.size == program.list.size)
          { "when lacking a kleene-*, the Texp must have the same length as the rule" }
      program.list.zip(types).forEach { (child, childType) ->
        result * validate(grammar, child, childType)
      }
    }
    return result
  }

  val result = Result(type)

  // primitive type
  if (type[0].isLowerCase() || type[0] == '#') {
    result + "  primitive matching"
    return result * matchValue(type)
  }

  // non primitive types need to be looked up
  val rule = grammar[type]!!

  // choice operator
  if (rule.value == "|") {
    val choices = rule.list.map { it.value }
    result + "  matching $program with choice of $choices"

    choices.forEach { choice ->
      result * validate(grammar, program, choice)
      result + "\n"
      result + "  $program matches choice of $choice"
    }

    result + "\n$program did not match any choice of \n$choices"
  }

  // non primitive production
  result * matchValue(rule.value)
  result * matchChildren(rule)
  return result
}

class GrammarError(message: String) : IllegalStateException(message)

class Result(msg: String, var check: Boolean = true) {
  constructor(p: Pair<String, Boolean>) : this(p.first, p.second)

  var acc: StringBuilder = StringBuilder(msg).append('\n')

  operator fun plus(msg: String): Result {
    acc.append(msg).append('\n')
    return this
  }

  operator fun times(other: Result): Result {
    return if (check) {
      plus(other.acc.toString())
    } else {
      this
    }
  }

  override fun toString(): String {
    return acc.toString()
  }

  fun fails(): Result {
    check = false
    return this
  }

  fun err(): Boolean {
    return !check
  }

  fun ok(): Boolean {
    return !check
  }

  /**
   * if this result is true and if the predicate passes
   * if this result is true but the predicate fails, append the error message for the predicate and set the result to false
   * if this result is false, maintains the previous error
   */
  fun check(pred: Boolean, errFunc: () -> String): Result {
    return when {
      check && pred -> this
      check && !pred -> this.fails() + errFunc()
      else  -> this
    }
  }
}