package pass

import main.Texp

//region gen-local

var stackCounter = 0
var foundStack = false

fun initLocals(s: Texp) {
  foundStack = false
  s.`do`()

  stackCounter = if (!foundStack) 0 else stackCounter + 1
}

fun newLocal(): Int {
  return stackCounter++
}

private fun (Texp).`do`() {
  list.forEach {
    when (it.value) {
      "let" -> it.let()
      "do" -> it.`do`()
      "if" -> it[1].`do`()
    }
  }
}

/**
 * During computation, stackCounter represents the top of the current
 * stack. Upon completion of traversal, stackCounter is incremented, thus
 * representing the next available gen-local.
 */
private fun (Texp).let() {
  assert(value.isNotEmpty())

  if (value[0] != '$') return // not a gen-local

  val size = value.substring(1).toInt()

  if (!foundStack) {
    foundStack = true
    stackCounter = 0
  } else if (size > stackCounter) {
    stackCounter = size
  }
}

//endregion gen-local