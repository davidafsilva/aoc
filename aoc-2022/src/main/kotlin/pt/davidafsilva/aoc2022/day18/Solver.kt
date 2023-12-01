package pt.davidafsilva.aoc2022.day18

import pt.davidafsilva.aoc2022.loadInput
import kotlin.LazyThreadSafetyMode.NONE

fun main() {
    val cubes = loadInput(day = 18).map { coordinates ->
        val (x, y, z) = coordinates.split(",")
        Point(x.toInt(), y.toInt(), z.toInt())
    }.toList()
    println("1st part: ${surfaceArea(cubes)}")
    // 4184 - too high
    println("2nd part: ${surfaceArea(cubes, subtracting = Grid::airPocketsSurfaceArea)}")
}

private fun surfaceArea(
    cubes: List<Point>,
    subtracting: (Grid) -> Int = { 0 },
): Long {
    val grid = Grid()
    var surfaceArea = 0L
    for (cube in cubes) {
        // add the point
        val yIndex = grid.matrix.computeIfAbsent(cube.z) { sortedMapOf() }
        val xIndex = yIndex.computeIfAbsent(cube.y) { sortedMapOf() }
        xIndex[cube.x] = Unit

        // compute the surface area of the cube
        val cubeSurfaceArea = cube.adjacent.count { p ->
            grid.matrix[p.z]?.get(p.y)?.get(p.x) == null
        }
        // update total surface area
        surfaceArea += cubeSurfaceArea
        surfaceArea -= (6 - cubeSurfaceArea)
    }

    grid.print()

    return surfaceArea - subtracting(grid)
}

private fun Grid.airPocketsSurfaceArea(): Int {
    // get the candidates (bottom -> top order)
    val candidates = (minZ..maxZ).flatMap { z ->
        (minY..maxY).flatMap { y ->
            (minX..maxX).mapNotNull { x ->
                if (matrix[z]?.get(y)?.get(x) == null) Point(x, y, z)
                else null
            }
        }
    }

    val airPockets = mutableSetOf<Set<Point>>()
    for (c in candidates) {
        val airPocket = computeAirPocket(this, c)
        if (airPocket.isNotEmpty()) {
            airPockets.add(airPocket)
        }
    }

    TODO("compute the total area")
}

private fun computeAirPocket(
    grid: Grid,
    point: Point,
    visited: MutableSet<Point> = mutableSetOf(),
): Set<Point> = with(grid) {
    if (!visited.add(point)) return@with emptySet()

    val hasCube = { p: Point -> matrix[p.z]?.get(p.y)?.get(p.x) != null }

    // if it's a cube, it's not an air pocket
    if (hasCube(point)) return@with emptySet()

    val isOutOfBounds = { p: Point ->
        p.z < minZ || p.z > maxZ ||
            p.y < minY || p.y > maxY ||
            p.x < minX || p.x > maxX
    }
    val isValid = { p: Point ->
        isOutOfBounds(p) || hasCube(p)
    }

    // extract and evaluate each adjacent points
    val leftValid = isValid(point.copy(x = point.x - 1))
    val rightValid = isValid(point.copy(x = point.x + 1))
    val backValid = isValid(point.copy(x = point.y - 1))
    val frontValid = isValid(point.copy(x = point.y + 1))

    TODO()
}

private class Grid {
    // z -> y -> x
    val matrix: MutableMap<Int, MutableMap<Int, MutableMap<Int, Unit>>> = sortedMapOf()

    val minZ by lazy(NONE) { matrix.keys.min() }
    val maxZ by lazy(NONE) { matrix.keys.max() }
    val minY by lazy(NONE) { matrix.keys.minOf { z -> matrix[z]!!.keys.min() } }
    val maxY by lazy(NONE) { matrix.keys.maxOf { z -> matrix[z]!!.keys.max() } }
    val minX by lazy(NONE) { matrix.keys.minOf { z -> matrix[z]!!.keys.minOf { y -> matrix[z]!![y]!!.keys.min() } } }
    val maxX by lazy(NONE) { matrix.keys.maxOf { z -> matrix[z]!!.keys.maxOf { y -> matrix[z]!![y]!!.keys.max() } } }

    fun print() {
        val minZ = matrix.keys.min()
        val maxZ = matrix.keys.max()
        val minY = matrix.keys.minOf { z -> matrix[z]!!.keys.min() }
        val maxY = matrix.keys.maxOf { z -> matrix[z]!!.keys.max() }
        val minX = matrix.keys.minOf { z -> matrix[z]!!.keys.minOf { y -> matrix[z]!![y]!!.keys.min() } }
        val maxX = matrix.keys.maxOf { z -> matrix[z]!!.keys.maxOf { y -> matrix[z]!![y]!!.keys.max() } }
        for (z in minZ..maxZ) {
            println("Z: $z")
            print("".padEnd(6))
            for (x in minX..maxX) print(x.toString().padEnd(3))
            println()
            for (y in minY..maxY) {
                print(y.toString().padEnd(5))
                for (x in minX..maxX) {
                    val code = when (matrix[z]?.get(y)?.get(x)) {
                        null -> " . "
                        else -> " # "
                    }
                    print(code)
                }
                println()
            }
            println()
        }
    }
}

private data class Point(val x: Int, val y: Int, val z: Int) {
    val adjacent: List<Point>
        get() = listOf(
            Point(x + 1, y, z),
            Point(x - 1, y, z),
            Point(x, y + 1, z),
            Point(x, y - 1, z),
            Point(x, y, z + 1),
            Point(x, y, z - 1),
        )
}
