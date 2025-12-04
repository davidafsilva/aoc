package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2025.day4

import pt.davidafsilva.aoc2022.loadInput

private const val ROLL = '@'
private const val FREE = '.'

fun main() {
    val grid = loadInput(day = 4).map { it.toCharArray() }.toList()

    println("1st part: ${grid.accessibleRolls()}")

    val accessibleRolls = generateSequence {
        grid.accessibleRolls { x, y -> grid[y][x] = FREE }
    }.takeWhile { it > 0 }.sum()
    println("2nd part: $accessibleRolls")
}

private fun List<CharArray>.accessibleRolls(fn: (Int, Int) -> Unit = { _, _ -> }): Int {
    var accessible = 0
    for (y in indices) {
        for (x in get(y).indices) {
            if (get(y)[x] == ROLL && isRollAccessible(rollX = x, rollY = y)) {
                fn(x, y)
                accessible++
            }
        }
    }

    return accessible
}

private fun List<CharArray>.isRollAccessible(rollX: Int, rollY: Int): Boolean {
    fun Char?.signal() = if (this == ROLL) 1 else 0
    val topLeft = getOrNull(rollY - 1)?.getOrNull(rollX - 1).signal()
    val left = getOrNull(rollY)?.getOrNull(rollX - 1).signal()
    val bottomLeft = getOrNull(rollY + 1)?.getOrNull(rollX - 1).signal()
    val topRight = getOrNull(rollY - 1)?.getOrNull(rollX + 1).signal()
    val right = getOrNull(rollY)?.getOrNull(rollX + 1).signal()
    val bottomRight = getOrNull(rollY + 1)?.getOrNull(rollX + 1).signal()
    val top = getOrNull(rollY - 1)?.getOrNull(rollX).signal()
    val bottom = getOrNull(rollY + 1)?.getOrNull(rollX).signal()
    return left + right + top + bottom + topLeft + topRight + bottomLeft + bottomRight < 4
}
