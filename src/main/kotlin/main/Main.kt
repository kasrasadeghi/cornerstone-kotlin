package main

import parse.parse
import java.lang.System.exit

fun main(args: Array<String>) {
  if (args.size != 1) {
    println("Usage: cornerstone <path>")
    exit(0)
  }
  val program = parse(args[0])/*.blockify().becomeify().callStmt().bindify().normalize().qualify()*/
  val grammarSexp = parse("src/main/lib/cornerstone.grammar")[0]
  val grammar = grammarSexp.list.map { it.value to it[0] }.toMap()
  validate(grammar, program, "Program")
}

fun validate(grammar: Map<String, Sexp>, program: Sexp, type: String): Boolean {
  val rule = grammar[type]!!
  print(rule)
  return program.value match rule.value /*&& program.list.*/


  /*
  if (type[0].isUpperCase()) {
//     production
    val rule = grammar[type]!!
    if (!(program.value match rule.value)) return false

    when (rule[0].value) {
      "*" -> {
        print(rule[0][0])
        if (!program.list.all { validate(grammar, it, rule[0][0].value) }) return false
      }
      "|" -> {
        rule[0].list.any()
      }
    }
  }
  return true
*/
}

infix fun String.match(other: String): Boolean =
  when (other) {
    "#name" -> true
    else    -> this == other
  }
