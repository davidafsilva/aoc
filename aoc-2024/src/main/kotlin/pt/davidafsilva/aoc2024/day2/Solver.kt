package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2024.day2

import pt.davidafsilva.aoc2022.loadInput
import kotlin.math.abs

fun main() {
    // 1st part
    var safeReports = countSafeReports(allowRemovingSingleLevel = false)
    println("Safe reports: $safeReports")

    // 2nd part
    safeReports = countSafeReports(allowRemovingSingleLevel = true)
    println("Safe reports: $safeReports")
}

private fun countSafeReports(allowRemovingSingleLevel: Boolean) = loadInput(2).fold(0) { acc, report ->
    val levels = report.split(" ").map { it.toInt() }

    var isSafe = isReportSafe(levels)
    if (!isSafe && allowRemovingSingleLevel) {
        for (i in levels.indices) {
            isSafe = isReportSafe(levels.copyWithout(i))
            if (isSafe) break
        }
    }

    acc + if (isSafe) 1 else 0
}

private fun isReportSafe(levels: List<Int>): Boolean {
    val incr: (Int, Int) -> Boolean = { a, b -> a > b }
    val decr: (Int, Int) -> Boolean = { a, b -> a < b }
    val cmp = if (levels[0] > levels[1]) incr else decr

    for (i in 0 until levels.size - 1) {
        val a = levels[i]
        val b = levels[i + 1]
        if (!cmp(a, b) || abs(a - b) > 3) return false
    }

    return true
}

private fun List<Int>.copyWithout(idx: Int) = toMutableList().apply { removeAt(idx) }
