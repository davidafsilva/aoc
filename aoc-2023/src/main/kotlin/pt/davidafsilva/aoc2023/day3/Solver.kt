package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2023.day3

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    val gs = loadGameSchematics()
    println("1st part: ${gs.validPartNumbers().sum()}")
    println("2nd part: ${gs.gearRatios().sum()}")
}

private fun EngineSchematics.gearRatios() = symbols.asSequence()
    .filter { (_, s) -> s == '*' }
    .map { (pos, _) -> pos }
    .mapNotNull { asteriskLocation ->
        val gears = parts.asSequence()
            .filter(asteriskLocation::hasAdjacentPart)
            .map { p -> p.number }
            .toList()
        if (gears.size == 2) gears.first() * gears.last()
        else null
    }

private fun EngineSchematics.validPartNumbers() = parts.asSequence()
    .filter { p -> p.hasAdjacentSymbol(symbols.keys) }
    .map(Part::number)

private fun loadGameSchematics(): EngineSchematics {
    val parts = mutableListOf<Part>()
    val symbols = mutableMapOf<Point, Char>()
    var currentDigit = 0L
    var currentDigitStartX = -1

    val addPart = fun(endX: Int, y: Int) {
        if (currentDigitStartX >= 0) {
            val p = Part(currentDigit, Point(currentDigitStartX, y), Point(endX, y))
            parts.add(p)
        }
        currentDigit = 0
        currentDigitStartX = -1
    }

    loadInput(3).withIndex().forEach { (y, line) ->
        line.withIndex().forEach { (x, c) ->
            when {
                c.isDigit() -> {
                    if (currentDigitStartX == -1) currentDigitStartX = x
                    currentDigit = currentDigit * 10 + c.digitToInt()
                }
                c.isPoint() -> addPart(x - 1, y)
                c.isSymbol() -> {
                    addPart(x - 1, y)
                    symbols[Point(x, y)] = c
                }
            }
        }
        addPart(line.length - 1, y)
    }

    return EngineSchematics(parts, symbols)
}

private fun Part.hasAdjacentSymbol(symbols: Set<Point>) =
    (start.x..end.x).any { x -> Point(x, start.y).hasAdjacentSymbol(symbols) }

private fun Point.hasAdjacentSymbol(symbols: Set<Point>) =
    hasAdjacent { p -> p in symbols }

private fun Point.hasAdjacentPart(part: Part) =
    hasAdjacent { p ->
        p.x >= part.start.x && p.x <= part.end.x &&
        p.y >= part.start.y && p.y <= part.end.y
    }

private fun Point.hasAdjacent(fn: (Point) -> Boolean) =
    fn(Point(x - 1, y - 1)) ||
        fn(Point(x - 1, y)) ||
        fn(Point(x - 1, y + 1)) ||
        fn(Point(x, y - 1)) ||
        fn(Point(x, y + 1)) ||
        fn(Point(x + 1, y - 1)) ||
        fn(Point(x + 1, y)) ||
        fn(Point(x + 1, y + 1))

private fun Char.isPoint() = this == '.'
private fun Char.isSymbol() = !isLetterOrDigit()

private data class EngineSchematics(val parts: List<Part>, val symbols: Map<Point, Char>)
private data class Part(val number: Long, val start: Point, val end: Point)
private data class Point(val x: Int, val y: Int)
