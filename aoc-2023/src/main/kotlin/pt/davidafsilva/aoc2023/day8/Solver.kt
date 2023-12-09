package pt.davidafsilva.aoc2023.day8

import pt.davidafsilva.aoc2022.scanInput

fun main() {
    val map = loadNavigationMap()
    println("1st part: ${map.steps(map.nodes { it == "AAA" }, map.nodes { it == "ZZZ" })}")
    println("2nd part: ${map.steps(map.nodes { it.endsWith("A") }, map.nodes { it.endsWith("Z") })}")
}

private fun NavigationMap.nodes(predicate: (String) -> Boolean): List<String> =
    destinations.keys.filter(predicate)

private fun NavigationMap.steps(from: List<String>, target: List<String>): Int {
    var lrIdx = 0
    var steps = 0

    var current = from
    val targetSet = target.toSet()
    while (!current.all { it in targetSet }) {
        val dir = lr[lrIdx++ % lr.length]
        val jump = when (dir) {
            'L' -> Pair<String, String>::first
            'R' -> Pair<String, String>::second
            else -> error("invalid direction: $dir")
        }
        current = current.map { c -> jump(destinations[c]!!) }
        steps++
    }

    return steps
}

private fun loadNavigationMap(): NavigationMap = scanInput(8).use { sc ->
    val lr = sc.nextLine()
    sc.nextLine()

    val destinations = mutableMapOf<String, Pair<String, String>>()
    while (sc.hasNext()) {
        val src = sc.next()
        sc.next() // =
        val leftDst = sc.next().removePrefix("(")
            .removeSuffix(",")
        val rightDst = sc.next().removeSuffix(")")
        destinations[src] = leftDst to rightDst
    }

    NavigationMap(lr, destinations)
}

private data class NavigationMap(
    val lr: String,
    val destinations: Map<String, Pair<String, String>>,
)
