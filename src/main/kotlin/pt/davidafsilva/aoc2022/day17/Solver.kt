package pt.davidafsilva.aoc2022.day17

import pt.davidafsilva.aoc2022.loadInput
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.time.toKotlinDuration

fun main() {
    val shifts = loadInput(day = 17).first()
        .map(Direction::fromCode)
    val rocks = listOf(Rock.Minus, Rock.Plus, Rock.L, Rock.I, Rock.Square)

    println("1st part: ${runFallingRocksSimulation(rocks, shifts, numberOfStoppedRocks = 2022)}")
    //println("2nd part: ${runFallingRocksSimulation(rocks, shifts, numberOfStoppedRocks = 1_000_000_000_000L)}")

    // Determined by analyzing the data when everything is leveled out (line 171)
    //
    // At height 5686 with 3671 rocks at rest (shift 1127, 5th rock), everything is
    // leveled out.
    // Every 2695 height it'll level out again after 1735 rocks are stopped,
    // starting from shift at index 1128
    val deltaHeight = 2695
    val deltaRocks = 1735

    val targetStoppedRocks = 1_000_000_000_000L
    var currentHeight = 5686L
    var currentStoppedRocks = 3671L
    while (currentStoppedRocks != targetStoppedRocks) {
        if (currentStoppedRocks + deltaRocks > targetStoppedRocks) {
            currentHeight += runFallingRocksSimulation(
                rocks = rocks,
                shifts = shifts.subList(1128, shifts.size) + shifts.subList(0, 1128),
                numberOfStoppedRocks = targetStoppedRocks - currentStoppedRocks + 1
            )
            break
        }

        currentHeight += deltaHeight
        currentStoppedRocks += deltaRocks
    }
    println("2nd part: $currentHeight")
}

private fun runFallingRocksSimulation(
    rocks: List<Rock>,
    shifts: List<Direction>,
    numberOfStoppedRocks: Long,
): Long {
    val chamber = Chamber()

    var stoppedRocks = 0L
    var rockIdx = 0
    var jetShiftIdx = 0
    val start = LocalDateTime.now()
    val move = { direction: Direction ->
        var stopped = false
        if (chamber.shiftLastDropped(direction)) {
            // at rest
            stoppedRocks++
            stopped = true

            if (numberOfStoppedRocks > 1000000 &&
                stoppedRocks % (numberOfStoppedRocks / 1000000) == 0L
            ) {
                val progress = String.format("%.4f", (stoppedRocks * 100.0 / numberOfStoppedRocks))
                val duration = Duration.between(start, LocalDateTime.now()).toKotlinDuration()
                println("$stoppedRocks stopped rocks at ${chamber.height} height ($progress%) [$duration]")
            }
        }
        stopped
    }

    // drop rocks while we can
    while (stoppedRocks < numberOfStoppedRocks) {
        val rock = rocks[rockIdx++ % rocks.size]

        // drop rock
        chamber.drop(rock)

        // keep moving until it has stopped
        while (true) {
            // push one unit by the current jet
            // and then fall down one unit
            move(shifts[jetShiftIdx++ % shifts.size])

            if (move(Direction.Down)) break
        }
    }

    return chamber.height
}

private class Chamber {
    companion object {
        private const val emptyLocation = '.'
        private const val emptyRowSize = 7
        private val emptyRowTemplate = { CharArray(emptyRowSize) { emptyLocation } }
    }

    private val grid = mutableListOf<CharArray>()
    private var cutHeight = 0L
    private val maxY = IntArray(emptyRowSize) { 0 }
    private lateinit var lastDroppedRock: Set<Point>

    val height: Long
        get() = cutHeight + grid.size

    fun drop(rock: Rock) {
        //println("DROP ${rock.javaClass.simpleName}")

        // add spacing
        repeat(3) { grid.add(emptyRowTemplate()) }

        // add rock
        for (idx in rock.template.indices.reversed()) {
            val row = rock.template[idx]
            grid.add(row.toCharArray())
        }

        // update last dropped
        lastDroppedRock = rock.template.flatMapIndexedTo(mutableSetOf()) { yOffset, row ->
            row.mapIndexedNotNull { x, c ->
                if (c == Rock.MARKER) Point(x, grid.size - 1 - yOffset)
                else null
            }
        }

        //print()
    }

    fun shiftLastDropped(direction: Direction): Boolean {
        //println("MOVE ${direction.javaClass.simpleName}")

        // move all points
        val newRockLocation = lastDroppedRock
            .mapTo(mutableSetOf()) { p -> p.move(direction) }

        val hitsAnotherRock = { p: Point ->
            p.x in 0 until emptyRowSize &&
                p.y in 0 until grid.size &&
                grid[p.y][p.x] == Rock.MARKER &&
                p !in lastDroppedRock
        }

        // check if we're out of bounds on the X axis,
        // or we hit another rock while moving sideways
        if (newRockLocation.any { p ->
                p.x < 0 || p.x >= emptyRowSize ||
                    (hitsAnotherRock(p) && direction !is Direction.Down)
            }) {
            return false
        }

        // check for collisions
        // hit the floor or another existent rock while moving down
        if (newRockLocation.any { p -> p.y < 0 || hitsAnotherRock(p) && direction is Direction.Down }) {
            // update maxY indices
            for (x in maxY.indices) {
                lastDroppedRock.filter { p -> p.x == x }
                    .maxByOrNull(Point::y)
                    ?.let { p ->
                        maxY[x] = max(maxY[x], p.y)
                    }
            }

            // get the lowest value, which will be our borderline
            var minY = maxY.min()
            if (minY > 0) {
                // if they're all equal, we can actually remove that line as well
                if (maxY.all { y -> y == minY }) minY += 1

                // remove the rows below
                repeat(minY) { grid.removeAt(0) }

                // adjust the lastDroppedRock and maxY coordinates
                lastDroppedRock = lastDroppedRock
                    .mapTo(mutableSetOf()) { p -> p.copy(y = p.y - minY) }
                maxY.forEachIndexed { idx, v -> maxY[idx] = v - minY }

                // update the cutHeight
                cutHeight += minY
            }

            return true
        }

        // update grid
        lastDroppedRock.forEach { p -> grid[p.y][p.x] = emptyLocation }
        newRockLocation.forEach { p -> grid[p.y][p.x] = Rock.MARKER }
        lastDroppedRock = newRockLocation

        // trim grid
        if (direction == Direction.Down) {
            val prevY = newRockLocation.maxBy(Point::y).y + 1
            if (grid[prevY].all { c -> c == emptyLocation }) {
                grid.removeAt(prevY)
            }
        }

        //print()

        return false
    }

    fun print() {
        for (row in grid.asReversed()) {
            print("|")
            row.forEach(::print)
            println("|")
        }
        println("+${"-".repeat(emptyRowSize)}+")
        println()
    }
}

private data class Point(val x: Int, val y: Int) {
    fun move(direction: Direction): Point = when (direction) {
        Direction.Down -> copy(y = y - 1)
        Direction.Left -> copy(x = x - 1)
        Direction.Right -> copy(x = x + 1)
    }
}

private sealed class Direction {
    object Left : Direction()
    object Right : Direction()
    object Down : Direction()

    companion object {
        fun fromCode(code: Char): Direction = when (code) {
            '>' -> Right
            '<' -> Left
            'v' -> Down
            else -> error("unsupported shift code: $code")
        }
    }
}

private sealed class Rock {
    companion object {
        const val MARKER = '#'
    }

    abstract val template: Array<String>

    object Minus : Rock() {
        override val template = arrayOf(
            "..####."
        )
    }

    object Plus : Rock() {
        override val template = arrayOf(
            "...#...",
            "..###..",
            "...#...",
        )
    }

    object L : Rock() {
        override val template = arrayOf(
            "....#..",
            "....#..",
            "..###..",
        )
    }

    object I : Rock() {
        override val template = arrayOf(
            "..#....",
            "..#....",
            "..#....",
            "..#....",
        )
    }

    object Square : Rock() {
        override val template = arrayOf(
            "..##...",
            "..##...",
        )
    }
}
