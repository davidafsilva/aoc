package pt.davidafsilva.aoc2024.day10

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    val map = loadInput(10)
        .map { r -> r.map { c -> c.digitToInt() } }
        .toList()

    // 1st part
    println("Total trailhead score: ${map.trailheadScore { it.distinct().size }}")

    // 2nd part
    println("Total trailhead score: ${map.trailheadScore { it.size }}")
}

private fun List<List<Int>>.trailheadScore(
    countFn: (List<Location>) -> Int,
): Int = withIndex().sumOf { (rIdx, r) ->
    r.withIndex().sumOf { (cIdx, c) ->
        if (c == 0) countFn(reachableNines(rIdx, cIdx))
        else 0
    }
}

private fun List<List<Int>>.reachableNines(
    row: Int,
    col: Int,
    targetHeight: Int = 0,
): List<Location> {
    // check out of bounds
    if (row < 0 || row >= size || col < 0 || col >= this[row].size) return emptyList()

    // check if the current position has the target height
    if (this[row][col] != targetHeight) return emptyList()

    // check if we reached the end
    if (this[row][col] == 9) return listOf(Location(row, col))

    // expand the trail
    return reachableNines(row = row - 1, col = col, targetHeight = targetHeight + 1) +
        reachableNines(row = row + 1, col = col, targetHeight = targetHeight + 1) +
        reachableNines(row = row, col = col - 1, targetHeight = targetHeight + 1) +
        reachableNines(row = row, col = col + 1, targetHeight = targetHeight + 1)
}

private data class Location(val row: Int, val col: Int)
