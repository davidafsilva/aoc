package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2024.day6

import pt.davidafsilva.aoc2022.loadInput

private data class Guard(val dir: Char, val pos: Position)
private data class Position(val row: Int, val col: Int)
private sealed class MoveResult {
    data class Moved(val guard: Guard) : MoveResult()
    data object Loop : MoveResult()
    data object OutOfBounds : MoveResult()
}

private class Simulation(
    private val map: List<List<Char>>,
    private val startingGuard: Guard,
    private val tryObstructions: Boolean = true,
) {
    companion object {
        val GUARD_MARKERS = listOf('^', '>', '<', 'v')
        private val move = mapOf(
            '^' to Position(-1, 0),
            'v' to Position(1, 0),
            '<' to Position(0, -1),
            '>' to Position(0, 1),
        )
        private val rotate = mapOf(
            '^' to '>',
            'v' to '<',
            '<' to '^',
            '>' to 'v',
        )
    }

    val possibleObstructions = mutableSetOf<Position>()
    val path: MutableSet<Guard> = mutableSetOf()

    fun run(): MoveResult {
        path.add(startingGuard)

        var res = move(startingGuard)
        while (res is MoveResult.Moved) {
            res = move(res.guard)
        }

        return res
    }

    private fun move(guard: Guard): MoveResult {
        val res = computeMove(guard)
        if (res !is MoveResult.Moved) return res
        path.add(res.guard)

        // check possible obstruction inclusion to create a loop
        if (tryObstructions) {
            val obstacle = res.guard.pos
            if (!obstacle.isOutOfBounds() && obstacle != startingGuard.pos && obstacle !in possibleObstructions) {
                val newSim = Simulation(
                    map = map.map { it.toMutableList() }
                        .apply { get(obstacle.row)[obstacle.col] = '#' },
                    startingGuard = startingGuard,
                    tryObstructions = false,
                )
                if (newSim.run() is MoveResult.Loop) possibleObstructions.add(obstacle)
            }
        }

        return res
    }

    private fun computeMove(guard: Guard): MoveResult {
        val next = guard.step()
        return when {
            next.pos.isOutOfBounds() -> MoveResult.OutOfBounds
            next in path -> MoveResult.Loop
            next.pos.isObstacle() -> computeMove(guard.copy(dir = rotate[guard.dir]!!))
            else -> MoveResult.Moved(next)
        }
    }

    private fun Guard.step(): Guard {
        val p = move[dir]!!
        return copy(pos = pos.copy(row = pos.row + p.row, col = pos.col + p.col))
    }

    private fun Position.isOutOfBounds(): Boolean = row < 0 || row >= map.size ||
        col < 0 || col >= map.first().size

    private fun Position.isObstacle(): Boolean = map[row][col] == '#'
}

fun main() {
    val map = loadInput(6)
        .map { row -> row.toMutableList() }
        .toList()
    val guard = map.flatten()
        .withIndex()
        .find { it.value in Simulation.GUARD_MARKERS }!!
        .let { (idx, p) ->
            val row = idx / map.first().size
            val col = idx % map.first().size
            val pos = Position(row, col)
            Guard(p, pos)
        }
    val simulation = Simulation(map, guard).apply { run() }

    // 1st part
    println("unique visited positions: ${simulation.path.toSet().size}")

    // 2nd part - 1516
    println("unique obstructions possible: ${simulation.possibleObstructions.size}")
}
