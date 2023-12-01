package pt.davidafsilva.aoc2022.day3

import pt.davidafsilva.aoc2022.loadInput

private data class Rucksack(
    val c1: Compartment,
    val c2: Compartment,
)

private data class Compartment(val items: Set<Char>)

fun main() {
    println("1st part: ${totalDuplicatedItemsPriority()}")
    println("2nd part: ${totalGroupPriorities()}")
}

private fun totalGroupPriorities() = loadInput(day = 3)
    .chunked(3)
    .sumOf { group ->
        val duplicates = group.fold(null as Set<Char>?) { acc, sack ->
            val items = sack.toSet()
            acc?.let { it intersect items } ?: items
        }.orEmpty()
        duplicates.sumOf(::itemPriority)
    }

private fun totalDuplicatedItemsPriority() = loadInput(day = 3)
    .map { compartmentItems ->
        val c1 = compartmentItems.subSequence(0, compartmentItems.length / 2)
        val c2 = compartmentItems.subSequence(compartmentItems.length / 2, compartmentItems.length)
        c1.toSet() to c2.toSet()
    }.sumOf { (c1, c2) ->
        val duplicates = c1 intersect c2
        duplicates.sumOf(::itemPriority)
    }

private fun itemPriority(c: Char): Int {
    val offset = if (c.isUpperCase()) 'A' - 27 else 'a' - 1
    return c - offset
}
