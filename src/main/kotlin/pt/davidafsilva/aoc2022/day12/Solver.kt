package pt.davidafsilva.aoc2022.day12

import pt.davidafsilva.aoc2022.loadInput
import java.util.LinkedList
import kotlin.math.min

fun main() {
    lateinit var start: Node
    lateinit var end: Node

    // compute nodes
    val nodes = mutableMapOf<String, Node>()
    loadInput(day = 12).forEachIndexed { rIdx, r ->
        r.toCharArray().forEachIndexed { cIdx, c ->
            val node = nodes.computeIfAbsent("$rIdx,$cIdx") { Node(rIdx, cIdx, c) }
            if (c == 'S') start = node
            else if (c == 'E') end = node
        }
    }

    // compute adjacent nodes (actual graph)
    nodes.forEach { (_, node) ->
        evaluateAdjacentNode(start, end, node, nodes["${node.row + 1},${node.column}"])
            ?.let(node.adjacentNodes::add)
        evaluateAdjacentNode(start, end, node, nodes["${node.row - 1},${node.column}"])
            ?.let(node.adjacentNodes::add)
        evaluateAdjacentNode(start, end, node, nodes["${node.row},${node.column + 1}"])
            ?.let(node.adjacentNodes::add)
        evaluateAdjacentNode(start, end, node, nodes["${node.row},${node.column - 1}"])
            ?.let(node.adjacentNodes::add)
    }

    println("1st part: ${findShortestPathDistance(start, end)}")

    val startNodes = nodes.values.filter { n -> n.code == 'a' }
    val fewestSteps = startNodes.fold(findShortestPathDistance(start, end)) { acc, s ->
        val nMin = findShortestPathDistance(s, end, maxHops = acc)
        if (nMin == 0) acc else min(nMin, acc)
    }
    println("2nd part: $fewestSteps")
}

private fun evaluateAdjacentNode(
    start: Node,
    end: Node,
    current: Node,
    candidate: Node?,
): Node? = when {
    // starting point
    candidate != null && current == start && candidate.code == 'a' -> candidate
    // end hop
    current.code == 'z' && candidate == end -> candidate
    // same level, 1 up
    candidate != null && candidate != end && current.code - candidate.code >= -1 -> candidate
    else -> null
}

private fun findShortestPathDistance(start: Node, end: Node, maxHops: Int = Int.MAX_VALUE): Int {
    data class Hop(val to: Node, val distance: Int)

    val hops = LinkedList<Hop>().apply { add(Hop(start, 0)) }
    val visited = mutableSetOf<Node>()
    while (hops.isNotEmpty()) {
        val hop = hops.poll()

        // are we there yet?
        if (hop.to === end) return hop.distance

        // stop if we reach our limit
        if (hop.distance + 1 > maxHops) break

        // keep walking
        hop.to.adjacentNodes.asSequence()
            .filter(visited::add) // avoid loops
            .map { n -> Hop(n, hop.distance + 1) }
            .forEach(hops::offer)
    }

    return 0
}

private data class Node(val row: Int, val column: Int, val code: Char) {
    val adjacentNodes: MutableList<Node> = mutableListOf()
    override fun toString(): String = "$row,$column ($code)"
}
