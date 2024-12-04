package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2024.day4

import pt.davidafsilva.aoc2022.loadInput

private const val XMAS = "XMAS"
private val EMPTY = CharArray(0)

fun main() {
    val matrix = loadInput(4)
        .map { it.toCharArray() }
        .toList()

    // 1st part
    var xmas = 0
    for (y in matrix.indices) {
        for (x in matrix[y].indices) {
            // down
            xmas += matrix.countXmas(x, y) { cx, cy -> cx + 1 to cy }
            xmas += matrix.countXmas(x, y) { cx, cy -> cx + 1 to cy + 1 }
            xmas += matrix.countXmas(x, y) { cx, cy -> cx + 1 to cy - 1 }
            // up
            xmas += matrix.countXmas(x, y) { cx, cy -> cx - 1 to cy }
            xmas += matrix.countXmas(x, y) { cx, cy -> cx - 1 to cy + 1 }
            xmas += matrix.countXmas(x, y) { cx, cy -> cx - 1 to cy - 1 }
            // right
            xmas += matrix.countXmas(x, y) { cx, cy -> cx to cy + 1 }
            // left
            xmas += matrix.countXmas(x, y) { cx, cy -> cx to cy - 1 }
        }
    }
    println("Total XMAS: $xmas")

    var mas = 0
    for (y in matrix.indices) {
        for (x in matrix[y].indices) {
            if (matrix.hasXMas(x, y)) mas += 1
        }
    }
    println("Total X-MAS: $mas")
}

private fun List<CharArray>.countXmas(
    x: Int,
    y: Int,
    xmasIdx: Int = 0,
    next: (Int, Int) -> Pair<Int, Int>,
): Int {
    // check out-of-bounds
    if (y < 0 || y >= size || x < 0 || x >= get(y).size) return 0

    // check matching Xmas
    if (get(y)[x] != XMAS[xmasIdx]) return 0

    // have we reached the end of the word?
    if (xmasIdx == XMAS.length - 1) return 1

    // move on
    val (nextX, nextY) = next(x, y)
    return countXmas(nextX, nextY, xmasIdx + 1, next)
}

private fun List<CharArray>.hasXMas(x: Int, y: Int): Boolean {
    // check matching A, which is the center
    if (get(y)[x] != 'A') return false

    // check surroundings for M and S
    val topLeft = getOrElse(y - 1) { EMPTY }.getOrElse(x - 1) { '?' }
    val topRight = getOrElse(y - 1) { EMPTY }.getOrElse(x + 1) { '?' }
    val bottomLeft = getOrElse(y + 1) { EMPTY }.getOrElse(x - 1) { '?' }
    val bottomRight = getOrElse(y + 1) { EMPTY }.getOrElse(x + 1) { '?' }
    return (topLeft == 'M' && bottomRight == 'S' || topLeft == 'S' && bottomRight == 'M') &&
        (bottomLeft == 'M' && topRight == 'S' || bottomLeft == 'S' && topRight == 'M')
}
