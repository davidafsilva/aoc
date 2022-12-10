package pt.davidafsilva.aoc2022.day10

import pt.davidafsilva.aoc2022.loadInput
import kotlin.math.floor

fun main() {
    var cycle = 0
    var x = 1
    var accSignalStrength = 0
    val crt = Array(6) { CharArray(40) { '.' } }

    for (instruction in loadInput(day = 10)) {
        val parts = instruction.split(" ")
        val (cycles, op) = when (parts[0]) {
            "noop" -> 1 to {}
            "addx" -> 2 to { x += parts[1].toInt() }
            else -> error("unsupported instruction: ${parts[0]}")
        }

        for (delay in cycles downTo 1) {
            cycle++

            // acc the signal strength (1st part)
            if (cycle == 20 || (cycle - 20) % 40.0 == 0.0) {
                accSignalStrength += cycle * x
            }

            // print the pixel to the CRT (2nd part)
            val crtX = (cycle - 1) % crt[0].size
            val crtY = if (cycle == 1) 0 else floor((cycle - 1) / 40.0).toInt()
            println("[$crtX,$crtY] ($cycle)")
            val draw = if (crtX in IntRange(x - 1, x + 1)) '#' else '.'
            crt[crtY][crtX] = draw

            if (delay == 1) op()
        }
    }

    println("1st part: $accSignalStrength")

    println("2nd part")
    crt.forEach(::println)
}
