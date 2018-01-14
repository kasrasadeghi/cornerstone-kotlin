import java.lang.System.exit
import java.nio.file.Paths

fun main(args: Array<String>) {
  if (args.size != 1) {
    println("Usage: cornerstone.")
    exit(1)
  }
  val content = Paths.get("input/" + args[0]).toFile().readLines()
  content.forEachIndexed { i, line ->
    println("${i + 1} | $line")
  }
}