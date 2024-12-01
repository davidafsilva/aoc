package pt.davidafsilva.aoc2024.day1

import pt.davidafsilva.aoc2022.loadInput
import java.util.TreeMap
import kotlin.math.max
import kotlin.math.min

fun main() {
    // part 1
    var locations = readLocations()
    var totalDistance = 0L
    while (!locations.isEmpty()) {
        val (l, r) = locations.pick()
        totalDistance += (max(l, r) - min(l, r))
    }
    println("Total distance: $totalDistance")

    // part 2
    locations = readLocations()
    var similarityScore = 0L
    for (l in locations.left) {
        similarityScore += (l.key * (locations.right[l.key] ?: 0)) * l.value
    }
    println("Similarity score: $similarityScore")
}

private fun readLocations() = loadInput(1).fold(Locations()) { acc, str ->
    val pairs = str.split(" ")
    acc.left.compute(pairs.first().toLong()) { _, count -> (count ?: 0) + 1 }
    acc.right.compute(pairs.last().toLong()) { _, count -> (count ?: 0) + 1 }
    acc
}

private class Locations(
    val left: TreeMap<Long, Int> = TreeMap<Long, Int>(),
    val right: TreeMap<Long, Int> = TreeMap<Long, Int>(),
) {
    fun pick(): Pair<Long, Long> {
        val l = left.firstKey()
        if (left[l]!! == 1) left.remove(l)
        else left[l] = left[l]!! - 1

        val r = right.firstKey()
        if (right[r]!! == 1) right.remove(r)
        else right[r] = right[r]!! - 1

        return l to r
    }

    fun isEmpty() = left.isEmpty()
}
