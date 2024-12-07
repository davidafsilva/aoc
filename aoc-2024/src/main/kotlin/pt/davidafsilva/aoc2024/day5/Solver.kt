package pt.davidafsilva.aoc2024.day5

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    val precedence = loadInput(5)
        .takeWhile { it.contains("|") }
        .groupBy(
            keySelector = { it.split("|")[0].toInt() },
            valueTransform = { it.split("|")[1].toInt() }
        )
    val pages = loadInput(5)
        .dropWhile { it.isEmpty() || it.contains("|") }
        .map { it.splitToSequence(",").map { n -> n.toInt() }.toList() }
        .toList()

    // 1st part
    val sumOfMiddleElementsValid = pages
        .filter { p -> p.isValid(precedence) }
        .sumOf { p -> p[p.size / 2] }
    println("Sum of valid middle page numbers: $sumOfMiddleElementsValid")

    // 2nd part
    val sumOfMiddleElementsInvalid = pages
        .filter { p -> !p.isValid(precedence) }
        .map { p ->
            p.sortedWith { p1, p2 ->
                if (p2 in precedence[p1].orEmpty()) -1
                else if (p1 in precedence[p2].orEmpty()) 1
                else 0
            }
        }
        .sumOf { p -> p[p.size / 2] }
    println("Sum of invalid middle page numbers: $sumOfMiddleElementsInvalid")
}

private fun List<Int>.isValid(precedence: Map<Int, List<Int>>): Boolean {
    for (i in indices) {
        val page = this[i]
        for (j in (i + 1) until this.size) {
            val followUpPage = this[j]
            // otherPage must not have page in its precedence rules
            val rules = precedence[followUpPage].orEmpty()
            if (page in rules) {
                return false
            }
        }
    }

    return true
}
