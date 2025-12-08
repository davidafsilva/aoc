package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2025.day8

import pt.davidafsilva.aoc2022.loadInput
import kotlin.math.pow

fun main() {
    val target = 1000

    // load the junction boxes
    val boxes = loadInput(day = 8).map { line ->
        val (x, y, z) = line.split(",")
        JunctionBox(x.toInt(), y.toInt(), z.toInt())
    }.toList()

    // sort the pairs by distance
    val sortedPairs = boxes.flatMapIndexed { idx1, b1 ->
        boxes.filterIndexed { idx2, _ -> idx2 > idx1 }.map { b2 -> b1 to b2 }
    }.sortedBy { (b1, b2) -> b1.distance(b2) }

    println("1st solution: ${formCircuits(sortedPairs.take(target)).take(3).fold(1) { acc, c -> acc * c.boxes.size }}")
    println("2nd solution: ${formCircuits(sortedPairs).first().lastPair!!.let { (b1, b2) -> b1.x.toLong() * b2.x }}")
}

private fun formCircuits(pairs: List<Pair<JunctionBox, JunctionBox>>): List<Circuit> {
    val circuits = pairs.flatMapTo(mutableSetOf()) { (b1, b2) ->
        listOf(Circuit(setOf(b1)), Circuit(setOf(b2)))
    }

    for ((b1, b2) in pairs) {
        val c1 = circuits.first { c -> b1 in c.boxes }
        val c2 = circuits.first { c -> b2 in c.boxes }
        if (c1 === c2) continue

        circuits.remove(c1)
        circuits.remove(c2)
        circuits.add(Circuit(c1.boxes + c2.boxes, b1 to b2))
    }

    return circuits.sortedByDescending { c -> c.boxes.size }
}

private data class Circuit(val boxes: Set<JunctionBox>, val lastPair: Pair<JunctionBox, JunctionBox>? = null)

private data class JunctionBox(val x: Int, val y: Int, val z: Int) {
    fun distance(other: JunctionBox): Double = 0 +
        (other.x - x * 1.0).pow(2) +
        (other.y - y * 1.0).pow(2) +
        (other.z - z * 1.0).pow(2)
}
