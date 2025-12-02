package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2025.day2

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    val ranges = loadInput(day = 2)
        .flatMap { s -> s.splitToSequence(",") }
        .map { s ->
            val (start, end) = s.split('-')
            LongRange(start.toLong(), end.toLong())
        }
        .toList()
    println("Part 1 solution: ${ranges.flatMap { r -> r.filter(::isIdInvalidSimple) }.sum()}")
    println("Part 2 solution: ${ranges.flatMap { r -> r.filter(::isIdInvalidComplex) }.sum()}")
}

private fun isIdInvalidSimple(id: Long): Boolean {
    val str = id.toString()
    if (str.length % 2 == 1) return false
    val half = str.take(str.length / 2)
    return str == "$half$half"
}

private fun isIdInvalidComplex(id: Long): Boolean {
    val str = id.toString()
    for (i in 1..str.length / 2) {
        val left = str.take(i)
        val repeated = left.repeat((str.length - i) / i + 1)
        if (repeated == str) return true
    }
    return false
}
