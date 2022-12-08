package pt.davidafsilva.aoc2022.day8

import pt.davidafsilva.aoc2022.loadInput
import kotlin.math.max

fun main() {
    val grid = loadInput(day = 8)
        .map { line -> line.chars().toArray() }
        .toList()
    println("1st part: ${visibleTreesOn(grid)}")
    println("2nd part: ${highestScenicScore(grid)}")
}

private fun visibleTreesOn(grid: List<IntArray>): Int {
    var visible = 0
    for (y in grid.indices) {
        for (x in grid[y].indices) {
            if (isTreeVisibleFromOutside(grid, x, y)) visible++
        }
    }
    return visible
}

fun isTreeVisibleFromOutside(grid: List<IntArray>, x: Int, y: Int): Boolean {
    /* check left */
    if ((x - 1 downTo 0).none { grid[y][it] >= grid[y][x] }) return true

    // check right
    if ((x + 1 until grid[y].size).none { grid[y][it] >= grid[y][x] }) return true

    // check top
    if ((y - 1 downTo 0).none { grid[it][x] >= grid[y][x] }) return true

    // check bottom
    if ((y + 1 until grid.size).none { grid[it][x] >= grid[y][x] }) return true

    return false
}

private fun highestScenicScore(grid: List<IntArray>): Int {
    var highest = 0
    for (y in grid.indices) {
        for (x in grid[y].indices) {
            highest = max(highest, scenicScore(grid, x, y))
        }
    }
    return highest
}

private fun scenicScore(grid: List<IntArray>, x: Int, y: Int): Int {
    if (x == 0 || y == 0 || x == grid[0].size - 1 || y == grid.size - 1) return 0

    /* check left */
    val l = (x - 1 downTo 0)
        .takeWhile { grid[y][it] < grid[y][x] }
        .let { trees -> trees.size + if (trees.size == x) 0 else 1 }

    // check right
    val r = (x + 1 until grid[y].size)
        .takeWhile { grid[y][it] < grid[y][x] }
        .let { trees -> trees.size + if (trees.size == grid[y].size - x - 1) 0 else 1 }

    // check top
    val t = (y - 1 downTo 0)
        .takeWhile { grid[it][x] < grid[y][x] }
        .let { trees -> trees.size + if (trees.size == y) 0 else 1 }

    // check bottom
    val b = (y + 1 until grid.size)
        .takeWhile { grid[it][x] < grid[y][x] }
        .let { trees -> trees.size + if (trees.size == grid.size - y - 1) 0 else 1 }

    return l * r * t * b
}
