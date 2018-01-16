import java.lang.System.exit

fun main(args: Array<String>) {
  if (args.size != 1) {
    println("Usage: cornerstone <path>")
    exit(1)
  }
  val program = parse("input/" + args[0])
  print(program)
}