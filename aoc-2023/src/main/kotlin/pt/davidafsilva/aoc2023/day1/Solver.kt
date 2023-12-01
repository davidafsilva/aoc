package pt.davidafsilva.aoc2023.day1

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    val calibrationValues = loadInput(1)
        .map { line -> line.filter(Char::isDigit).map(Char::digitToInt) }
        .map { numbers -> numbers.first() * 10 + numbers.last() }
    println("1st part: ${calibrationValues.sum()}")
}
