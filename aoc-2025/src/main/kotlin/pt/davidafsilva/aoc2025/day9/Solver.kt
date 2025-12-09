package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2025.day9

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    val grid = loadGrid()
    println("1st part: ${findLargestRectangle(grid).area}")
}

private fun loadGrid(): Grid {
    val redTiles = loadInput(day = 9).map { line ->
        val (x, y) = line.split(",")
        Point(x.toInt(), y.toInt())
    }.toSet()
    val rows = redTiles.maxOf { it.y }
    val columns = redTiles.maxOf { it.x }
    return Grid(rows, columns, redTiles)
}

private fun findLargestRectangle(grid: Grid): Rectangle {
    return grid.redTiles.flatMap { p1 ->
        grid.redTiles
            .filter { p2 -> p2.y > p1.y && p2.x != p1.x }
            .map { p2 -> Rectangle(p1, p2) }
    }.maxBy(Rectangle::area)
}

private data class Rectangle(val p1: Point, val p2: Point) {
    val area: Long = (p2.x - p1.x + 1).toLong() * (p2.y - p1.y + 1)
}

private data class Point(val x: Int, val y: Int)
private data class Grid(
    val rows: Int,
    val columns: Int,
    val redTiles: Set<Point>,
) {
    fun print() {
        for (y in 0..rows) {
            for (x in 0..columns) {
                print(if (Point(x, y) in redTiles) "#" else ".")
            }
            println()
        }
    }
}
