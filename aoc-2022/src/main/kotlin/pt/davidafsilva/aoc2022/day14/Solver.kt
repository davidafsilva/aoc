package pt.davidafsilva.aoc2022.day14

import pt.davidafsilva.aoc2022.loadInput
import kotlin.math.max
import kotlin.math.min

fun main() {
    val pouringPoint = Point(500, 0)

    // load the simulation
    val simulation = loadCaveSimulation(pouringPoint)
    simulation.print()
    println()

    // 1st part: pour sand (ignoring floor existence)
    do {
        val r = simulation.simulatePour(floorIsVoid = true)
    } while (r == PourResult.REST)
    simulation.print()
    println("1st part: ${simulation.restCount}")
    println()

    // 2nd part: pour sand (with floor existence)
    do {
        val r = simulation.simulatePour(floorIsVoid = false)
    } while (r == PourResult.REST)
    simulation.print()
    println("2nd part: ${simulation.restCount}")
}

private fun loadCaveSimulation(pouringPoint: Point): CaveSimulation {
    var minX = pouringPoint.x
    var maxX = pouringPoint.x
    var maxY = pouringPoint.y
    val points = loadInput(day = 14).flatMap { lines ->
        lines.splitToSequence(" -> ").map { point ->
            val (x, y) = point.split(",")
            val p = Point(x.toInt(), y.toInt())
            minX = min(p.x, minX)
            maxX = max(p.x, maxX)
            maxY = max(p.y, maxY)
            p
        }.fold(mutableListOf<Point>()) { acc, p ->
            if (acc.isEmpty()) acc.add(p)
            else {
                val prev = acc.last()
                val xDirection = if (p.x == prev.x) 0 else if (p.x > prev.x) 1 else -1
                val yDirection = if (p.y == prev.y) 0 else if (p.y > prev.y) 1 else -1
                var curr = prev
                while (curr != p) {
                    val step = Point(curr.x + xDirection, curr.y + yDirection)
                    acc.add(step)
                    curr = step
                }
            }
            acc
        }.asSequence()
    }.toSet()

    // due to the floor mapping (2nd part)
    // we are going to adjust:
    // y = highestY + 2 (where the floor is located)
    // x +/- highestY to cope with potential sand blocks being stored within
    //   that space
    maxY += 2
    minX -= maxY // no support for negative values which are not needed for the problem input
    maxX += maxY

    return CaveSimulation.create(minX, maxX, maxY, points, pouringPoint)
}

private class CaveSimulation(
    private val minX: Int,
    private val pouringPoint: Point,
    private val points: Array<Array<Type>>,
) {
    var restCount: Int = 0
        private set

    companion object {
        fun create(
            minX: Int,
            maxX: Int,
            maxY: Int,
            linePoints: Set<Point>,
            pouringPoint: Point,
        ): CaveSimulation = CaveSimulation(
            minX,
            pouringPoint,
            Array(maxY + 1) { y ->
                Array(maxX - minX + 1) { x ->
                    val p = Point(x + minX, y)
                    when {
                        p == pouringPoint -> Type.POURING
                        p in linePoints -> Type.ROCK
                        p.y == maxY -> Type.FLOOR
                        else -> Type.AIR
                    }
                }
            }
        )
    }

    fun simulatePour(
        floorIsVoid: Boolean,
    ): PourResult {
        var currX = pouringPoint.x
        var currY = pouringPoint.y

        do {
            var hasMoved = false

            // down
            while (true) {
                val t = typeAt(currX, currY + 1, floorIsVoid)
                if (t == Type.VOID) return PourResult.VOID
                if (t == Type.AIR) {
                    currY++
                    hasMoved = true
                    continue
                }
                break
            }

            // down-left
            var t = typeAt(currX - 1, currY + 1, floorIsVoid)
            if (t == Type.VOID) return PourResult.VOID
            if (t == Type.AIR) {
                currX--
                currY++
                hasMoved = true
                continue
            }

            // down-right
            t = typeAt(currX + 1, currY + 1, floorIsVoid)
            if (t == Type.VOID) return PourResult.VOID
            if (t == Type.AIR) {
                currX++
                currY++
                hasMoved = true
            }
        } while (hasMoved)

        // mark sand at rest
        markSandAtRest(currX, currY)
        restCount++

        return when {
            currX == pouringPoint.x && currY == pouringPoint.y -> PourResult.BLOCKED
            else -> PourResult.REST
        }
    }

    fun print() {
        minX.toString().forEach { println("    $it") }
        points.forEachIndexed { idx, ps ->
            print(idx.toString().padEnd(4))
            ps.forEach { p -> print(p.c) }
            println()
        }
    }

    private fun typeAt(x: Int, y: Int, floorIsVoid: Boolean): Type {
        if (y == points.size - 1) {
            return if (floorIsVoid) Type.VOID else Type.FLOOR
        }

        return points.getOrNull(y)
            ?.getOrNull(x - minX)
            ?: Type.VOID
    }

    private fun markSandAtRest(x: Int, y: Int) {
        points[y][x - minX] = Type.SAND
    }
}

private enum class Type(val c: Char) {
    POURING('+'), SAND('o'), ROCK('#'), AIR('.'), VOID('V'), FLOOR('#')
}

private enum class PourResult { REST, VOID, BLOCKED }

private data class Point(val x: Int, val y: Int)
