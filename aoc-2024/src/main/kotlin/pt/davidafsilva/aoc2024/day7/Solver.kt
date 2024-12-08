package pt.davidafsilva.aoc2024.day7

import pt.davidafsilva.aoc2022.loadInput
import pt.davidafsilva.aoc2024.day7.Operation.Concat
import pt.davidafsilva.aoc2024.day7.Operation.Multiply
import pt.davidafsilva.aoc2024.day7.Operation.Sum

private data class Equation(val value: Long, val numbers: List<Long>)

private sealed class Operation(private val f: (Long, Long) -> Long) {
    operator fun invoke(a: Long, b: Long): Long = f(a, b)

    data object Multiply : Operation(Long::times)
    data object Sum : Operation(Long::plus)
    data object Concat : Operation({ a, b -> "$a$b".toLong() })
}

fun main() {
    val equations = loadInput(7).map { line ->
        val (value, numbers) = line.split(": ", limit = 2)
        Equation(value.toLong(), numbers.split(" ").map { it.toLong() })
    }.toList()

    // 1st part
    var totalCalibrationResult = equations.asSequence()
        .filter { eq -> eq.isSolvable(listOf(Multiply, Sum)) }
        .sumOf(Equation::value)
    println("Total calibration result: $totalCalibrationResult")

    // 2nd part
    totalCalibrationResult = equations.asSequence()
        .filter { eq -> eq.isSolvable(listOf(Multiply, Sum, Concat)) }
        .sumOf(Equation::value)
    println("Total calibration result: $totalCalibrationResult")
}

private fun Equation.isSolvable(operations: List<Operation>): Boolean = when (numbers.size) {
    1 -> numbers.first() == value
    else -> operations.any { op ->
        val v = op(numbers[0], numbers[1])
        v <= value && copy(
            numbers = listOf(v) + numbers.subList(2, numbers.size),
        ).isSolvable(operations)
    }
}
