
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.File
import java.io.IOException


class TestSuite {
  @TestFactory
  fun createParserTests(): Collection<DynamicTest> {

    val root = "tests/parser/"
    val files = File(root).listFiles().toList().sorted()

    val validTests = files
        .filter { it.name.endsWith(".bb") }
        .map { it.name.substringBeforeLast(".") }
        .filter { files.map { it.name }.contains(it + ".ok") }

    return validTests.map {
      DynamicTest.dynamicTest(it, {
        println(File(root + "$it.bb").readText())

        val reference = File(root + "$it.ok").readText()

        try {
          val input = parse(root + "$it.bb").toString()
          assertEquals(input.trim(), reference.trim())
        } catch (e: IOException) {
          assertTrue(reference.contains(e.message!!), "\"$reference\" does not contains \"${e.message!!}\" ")
        }
      })
    }
  }
}