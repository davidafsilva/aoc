package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2022.day4

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    println("1st part: ${countDuplicates(Boolean::and)}")
    println("2nd part: ${countDuplicates(Boolean::or)}")
}

private fun countDuplicates(
    pairOverlapExpressionCombiner: (Boolean, Boolean) -> Boolean,
): Int = sectionPairs().fold(0) { acc, (a, b) ->
    val contained = pairOverlapExpressionCombiner(a.first in b, a.last in b) ||
        pairOverlapExpressionCombiner(b.first in a, b.last in a)
    if (contained) acc + 1 else acc
}

private fun sectionPairs(): Sequence<List<IntRange>> = loadInput(day = 4)
    .flatMap { line -> line.split(",", limit = 2) }
    .map { range ->
        val (start, end) = range.split("-", limit = 2)
        IntRange(start.toInt(), end.toInt())
    }
    .chunked(2)
