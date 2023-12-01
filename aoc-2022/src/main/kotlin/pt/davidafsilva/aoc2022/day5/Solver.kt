package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2022.day5

import pt.davidafsilva.aoc2022.loadInput
import java.util.Stack

fun main() {
    val crateStacks = mutableListOf<Stack<Char>>()

    var processingCrateStacks = true
    for (line in loadInput(day = 5)) {
        if (line.startsWith(" 1") || line.isBlank()) {
            // reached the end of the stack definition
            processingCrateStacks = false
            continue
        }

        // process the crates
        if (processingCrateStacks) createStackAndAddCrate(crateStacks, line)
        // move crates
        else moveCrate(crateStacks, line)
    }

    println("1st/2nd part: ${crateStacks.joinToString(separator = "") { "${it.peek()}" }}")
}

private fun createStackAndAddCrate(
    crateStacks: MutableList<Stack<Char>>,
    line: String,
) {
    var crateCodeIdx = 1
    while (crateCodeIdx <= line.length - 2) {
        val code = line[crateCodeIdx]
        val crateIdx = when (crateCodeIdx) {
            1 -> 0
            else -> (crateCodeIdx - 1) / 4
        }

        // create stack if we have not yet created it
        if (crateIdx >= crateStacks.size) crateStacks.add(Stack())

        // add the crate code
        if (code != ' ') {
            crateStacks[crateIdx].add(0, code)
        }

        crateCodeIdx += 4
    }
}

private fun moveCrate(
    crateStacks: MutableList<Stack<Char>>,
    line: String,
) {
    val parts = line.split(" ")
    var count = parts[1].toInt()
    val fromIdx = parts[3].toInt() - 1
    val toIdx = parts[5].toInt() - 1

    // 1st part
//    while (count-- > 0) {
//        val code = crateStacks[fromIdx].pop()
//        crateStacks[toIdx].push(code)
//    }

    // 2nd part
    val codes = CharArray(count) { crateStacks[fromIdx].pop() }
    for (idx in codes.indices) {
        val code = codes[codes.size - 1 - idx]
        crateStacks[toIdx].push(code)
    }
}
