
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.File
import java.io.IOException


class TestSuite {
  private fun validTests(root: String): List<String> =
      File(root).listFiles().toList().sorted()
      .filter { it.name.endsWith(".bb") }
      .map { it.name.substringBeforeLast(".") }
      .filter { File(root).listFiles().toList().map { it.name }.contains(it + ".ok") }

  private fun (List<String>).makeTests(root: String, function: (String) -> Sexp)
      = this.map { DynamicTest.dynamicTest(it, { test(root, it, function) }) }

  private fun test(root: String, testName: String, function: (String) -> Sexp) {
    val src = File(root + "$testName.bb").readText()
    val ref = File(root + "$testName.ok").readText()

    try {
      val input = function(testName).toString()
      if (input.trim() != ref.trim()) {
        println(src)
      }
      assertEquals(ref.trim(), input.trim())
    } catch (e: IOException) {
      if (!ref.contains(e.message!!)) {
        println(src)
        assertTrue(false, "\"$ref\" does not contains \"${e.message!!}\" ")
      }
    }
  }

  @TestFactory
  fun `Parser tests`(): Collection<DynamicTest> {
    val root = "tests/parser/"
    return validTests(root).makeTests(root) { parse(root + "$it.bb") }
  }

  @TestFactory
  fun `Blockify tests`(): Collection<DynamicTest> {
    val root = "tests/blockify/"
    return validTests(root).makeTests(root) { parse(root + "$it.bb").Blockify() }
  }

  @TestFactory
  fun `Become tests`(): Collection<DynamicTest> {
    val root = "tests/become/"
    return listOf("become").makeTests(root) { parse(root + "$it.bb").Blockify().Become() }
  }
}