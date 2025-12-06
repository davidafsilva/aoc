package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2025.day6

import pt.davidafsilva.aoc2022.scanInput

fun main() {
    val problem = parseMathProblem()
    println("1st solution: ${problem.solve(::problemValuesToLong)}")
    println("2nd solution: ${problem.solve(::problemValuesFromValuesColumn)}")
}

private fun problemValuesToLong(problem: Problem): List<Long> {
    return problem.values.map { it.trim().toLong() }
}

private fun problemValuesFromValuesColumn(problem: Problem): List<Long> {
    val width = problem.values.first().length

    val values = mutableListOf<Long>()
    for (idx in 0 until width) {
        val value = StringBuilder()
        for (v in problem.values) {
            if (v[idx] != ' ') value.append(v[idx])
        }

        values.add(value.toString().toLong())
    }

    return values
}

private fun parseMathProblem(): MathProblem {
    val columnWidths = mutableMapOf<Int, Int>()
    val columnValues: MutableMap<Int, MutableList<String>> = mutableMapOf()
    val columnOperands: MutableMap<Int, String> = mutableMapOf()

    // operands + column width
    var sc = scanInput(day = 6)
    while (sc.hasNextLine()) {
        val line = sc.nextLine()
        if (line.startsWith("*") || line.startsWith("+")) {
            var idx = 0
            var column = 0
            while (idx < line.length) {
                val operand = line[idx++]
                var width = 0
                while (idx < line.length && line[idx] == ' ') {
                    width++; idx++
                }
                columnOperands[column] = operand.toString()
                columnWidths[column++] = width
            }
        }
    }

    // values
    sc = scanInput(day = 6)
    while (sc.hasNextLine()) {
        val line = sc.nextLine()
        if (line.startsWith("*") || line.startsWith("+")) break

        var column = 0
        var idx = 0
        while (column < columnWidths.size) {
            var width = columnWidths[column]!!
            if (width == 0) {
                // last column does not have width properly set
                width = line.length - idx
            }
            val value = line.substring(idx, idx + width)
            idx += width
            idx++ // separator
            columnValues.computeIfAbsent(column++) { mutableListOf() }.add(value)
        }
    }

    // pad last column values
    val lastColumnValues = columnValues[columnValues.size - 1]!!
    val maxWidth = lastColumnValues.maxOf { it.length }
    lastColumnValues.replaceAll { it.padEnd(maxWidth, ' ') }

    return MathProblem(columnValues.map { (column, values) ->
        Problem(values, columnOperands[column]!!)
    })
}

private data class Problem(val values: List<String>, val operand: String)

private class MathProblem(val problems: List<Problem>) {

    fun solve(valuesFn: (Problem) -> List<Long>): Long {
        var res = 0L
        for (problem in problems) {
            res += when (problem.operand) {
                "+" -> valuesFn(problem).fold(0L) { acc, i -> acc + i }
                "*" -> valuesFn(problem).fold(1L) { acc, v -> acc * v }
                else -> error("invalid operand")
            }
        }
        return res
    }
}
