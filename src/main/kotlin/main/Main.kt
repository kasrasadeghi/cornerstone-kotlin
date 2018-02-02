package main

import java.lang.System.exit

fun main(args: Array<String>) {
  if (args.size != 1) {
    println("Usage: cornerstone <path>")
    exit(0)
  }
  val program = parse(args[0]).Blockify().Become()
  print(program)
}