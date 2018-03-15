package main

import parse.parse
import java.lang.System.exit

fun main(args: Array<String>) {
  if (args.size != 1) {
    println("Usage: cornerstone <path>")
    exit(0)
  }
  val program = parse(args[0])/*.blockify().becomeify().callStmt().bindify().normalize().qualify()*/
  val grammar = parseGrammar("src/main/grammar/cornerstone.grammar")
  println(program)
  try {
    validate(grammar, program, "Program")
  } catch (e: IllegalStateException) {
    error(e.message ?: e)
  }
}
