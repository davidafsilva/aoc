package pt.davidafsilva.aoc2025.day1

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    data class State(
        private val pos: Int = 50,
        val crossedZero: Int = 0,
    ) {
        fun rotate(dir: Char, amount: Int): State {
            val move = when (dir) {
                'L' -> -amount
                'R' -> amount
                else -> error("invalid direction: $dir")
            }

            val range = when {
                move > 0 -> IntRange(pos + 1, pos + move)
                else -> IntRange(pos + move, pos - 1).reversed()
            }
            val newPosCrossedZero = range.count { it % 100 == 0 }
            return copy(
                pos = ((pos + move) % 100 + 100) % 100,
                crossedZero = crossedZero + newPosCrossedZero,
            ).also { println("$pos + $dir$amount = ${it.pos} (${it.crossedZero})") }
        }
    }

    val state = loadInput(1).fold(State()) { s, line ->
        val d = line[0]
        val r = line.substring(1).toInt()
        s.rotate(d, r)
    }
    println("Total zero points: ${state.crossedZero}")
}
