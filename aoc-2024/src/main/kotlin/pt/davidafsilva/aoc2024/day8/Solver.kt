package pt.davidafsilva.aoc2024.day8

import pt.davidafsilva.aoc2022.loadInput

private data class Position(val row: Int, val col: Int)

fun main() {
    val map = loadInput(8)
        .map { line -> line.toList() }
        .toList()

    // compute the antennas locations
    val antennas = mutableMapOf<Char, MutableList<Position>>()
    for ((rowIdx, row) in map.withIndex()) {
        for ((colIdx, col) in row.withIndex()) {
            if (col != '.') {
                antennas.computeIfAbsent(col) { mutableListOf() }
                    .add(Position(rowIdx, colIdx))
            }
        }
    }

    // compute the anti-nodes
    val antiNodes = mutableSetOf<Position>()
    val antiNodesWithResonantHarmonics = mutableSetOf<Position>()
    for ((_, positions) in antennas) {
        for (a1 in positions) {
            for (a2 in positions) {
                if (a1 != a2) {
                    var m = 1
                    var pos = antiNodePos(a1, a2, m)
                    antiNodesWithResonantHarmonics.add(a2)

                    while (!map.isOutOfBounds(pos)) {
                        if (m == 1) antiNodes.add(pos)
                        antiNodesWithResonantHarmonics.add(pos)
                        pos = antiNodePos(a1, a2, ++m)
                    }
                }
            }
        }
    }

    // 1st part
    println("Total anti-nodes: ${antiNodes.size}")

    // 2nd part
    println("Total anti-nodes with resonant harmonics: ${antiNodesWithResonantHarmonics.size}")
}

private fun antiNodePos(a: Position, b: Position, m: Int) = Position(
    row = a.row + ((a.row - b.row) * m),
    col = a.col + ((a.col - b.col) * m),
)

private fun List<List<Char>>.isOutOfBounds(p: Position): Boolean =
    p.row < 0 || p.row >= size || p.col < 0 || p.col >= first().size
