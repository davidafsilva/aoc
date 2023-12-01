package pt.davidafsilva.aoc2023.day1

import pt.davidafsilva.aoc2022.loadInput

private val orderedDigitsStr = listOf(
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
)

fun main() {
    println("1st part: ${loadInput(1).calibrationValues().sum()}")
    println("2nd part: ${loadInput(1).map(String::normaliseDigits).calibrationValues().sum()}")
}

private fun Sequence<String>.calibrationValues() =
    map { line -> line.mapNotNull(Char::digitToIntOrNull) }
        .map { numbers -> numbers.first() * 10 + numbers.last() }

private fun String.normaliseDigits(): String {
    var firstDigitIdx = Int.MAX_VALUE
    var firstDigit = -1
    for ((v, d) in orderedDigitsStr.withIndex()) {
        val idx = indexOf(d)
        if (idx < 0) continue

        if (idx < firstDigitIdx) {
            firstDigitIdx = idx
            firstDigit = v + 1
        }

        if (idx <= 2) break
    }

    if (firstDigit == -1) return this
    return replaceRange(
        startIndex = firstDigitIdx,
        endIndex = firstDigitIdx + orderedDigitsStr[firstDigit - 1].length - 1,
        replacement = firstDigit.toString(),
    ).normaliseDigits()
}
