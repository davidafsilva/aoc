package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2025.day7

import pt.davidafsilva.aoc2022.loadInput
import java.util.LinkedList

fun main() {
    val simulation = loadSimulation()
    simulation.run()
    println("1st part: ${simulation.splits}")
    println("2nd part: ${simulation.timelines}")
}

private fun loadSimulation(): Simulation {
    val grid = loadInput(day = 7).map { l ->
        l.mapTo(mutableListOf()) { it }
    }.toList()
    lateinit var start: Simulation.Beam
    for ((y, row) in grid.withIndex()) {
        for ((x, c) in row.withIndex()) {
            if (c == 'S') {
                start = Simulation.Beam(x, y)
                grid[y][x] = '.'
                break
            }
        }
    }
    return Simulation(grid, start)
}

private data class Simulation(
    private val grid: List<MutableList<Char>>,
    val start: Beam,
) {
    data class Beam(val x: Int, val y: Int)

    private val beams = LinkedList<Beam>(listOf(start))
    private val visited = mutableSetOf<Beam>()

    var splits = 0
        private set
    var timelines: Int = 1
        private set

    fun run() {
        while (beams.isNotEmpty()) {
            val newBeams = move(beams.pop())
            beams.addAll(newBeams)
        }
    }

    private fun move(b: Beam): List<Beam> {
        if (b.isOutOfBounds() || b in visited) return emptyList()
        visited.add(b)

        return when (grid[b.y][b.x]) {
            '.' -> listOf(b.copy(y = b.y + 1))
            '^' -> {
                splits++
                move(b.copy(x = b.x - 1)) + move(b.copy(x = b.x + 1))
            }
            else -> error("unsupported char")
        }
    }

    private fun Beam.isOutOfBounds(): Boolean = y < 0 || y >= grid.size
}
