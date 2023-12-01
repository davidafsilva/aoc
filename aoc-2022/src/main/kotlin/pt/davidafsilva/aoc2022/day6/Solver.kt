package pt.davidafsilva.aoc2022.day6

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    val signal = loadInput(day = 6).first()

    println("1st part: ${findMarker(signal, length = 4)}")
    println("1st part: ${findMarker(signal, length = 14)}")
}

private fun findMarker(signal: String, length: Int): Int {
    for (idx in signal.indices) {
        if (idx + length - 1 >= signal.length) break

        val isValidMarker = signal.subSequence(idx, idx + length)
            .toSet().size == length
        if (isValidMarker) {
            return idx + length
        }
    }

    return -1
}
