package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2025.day7

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    val simulation = loadSimulation()
    simulation.run()
    println("1st part: ${simulation.splits}")
    println("2nd part: ${simulation.totalPaths}")
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
    data class Beam(val x: Int, val y: Int) {
        fun move(x: Int = 0) = copy(x = this.x + x, y = this.y + 1)
    }

    private val visited = mutableMapOf<Beam, Long>()

    var splits = 0
        private set
    var totalPaths: Long = 0
        private set

    fun run() {
        totalPaths = move(start)
    }

    private fun move(b: Beam): Long = when {
        b.isOutOfBounds() -> 1
        b in visited -> visited[b]!!
        grid[b.y][b.x] == '.' -> move(b.move())
        grid[b.y][b.x] == '^' -> {
            splits++
            move(b.move(x = -1)) + move(b.move(x = 1))
        }
        else -> 0
    }.also { visited[b] = it }

    private fun Beam.isOutOfBounds(): Boolean = y < 0 || y >= grid.size
}
