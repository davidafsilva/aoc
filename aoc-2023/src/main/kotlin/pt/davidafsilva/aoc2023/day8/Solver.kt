package pt.davidafsilva.aoc2023.day8

import pt.davidafsilva.aoc2022.scanInput

fun main() {
    val map = loadNavigationMap()
    println("1st part: ${map.steps(map.nodes { it == "AAA" }, map.nodes { it == "ZZZ" })}")
    println("2nd part: ${map.steps(map.nodes { it.endsWith("A") }, map.nodes { it.endsWith("Z") })}")
}

private fun NavigationMap.nodes(predicate: (String) -> Boolean): List<String> =
    destinations.keys.filter(predicate)

private fun NavigationMap.steps(from: List<String>, target: List<String>): Long {
    var lrIdx = 0

    val targetSet = target.toSet()
    val stepsToATarget = from.map { start ->
        var steps = 0
        var current = start
        while (current !in targetSet) {
            val dir = lr[lrIdx]
            val jump = when (dir) {
                'L' -> Pair<String, String>::first
                'R' -> Pair<String, String>::second
                else -> error("invalid direction: $dir")
            }
            current = jump(destinations[current]!!)

            steps++
            if (++lrIdx >= lr.length) lrIdx = 0
        }
        steps.toLong()
    }

    return stepsToATarget.reduce { acc, s -> lcm(acc, s) }
}

private fun lcm(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
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
