package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2025.day5

import pt.davidafsilva.aoc2022.scanInput
import kotlin.math.max
import kotlin.math.min

fun main() {
    val inventory = loadInventory()
    println("1st solution: ${inventory.availableFreshIngredients().size}")
    println("2nd solution: ${inventory.freshIngredients().sumOf { it.last - it.first + 1 }}")
}

private fun loadInventory() = Inventory().apply {
    val sc = scanInput(day = 5)
    // fresh ingredients
    while (sc.hasNext()) {
        val next = sc.nextLine()
        if (next.trim().isEmpty()) break
        val (l, r) = next.split("-")
        addFreshIngredients(LongRange(l.toLong(), r.toLong()))
    }
    // available ingredients
    while (sc.hasNext()) {
        addAvailableIngredient(sc.nextLong())
    }
}

private data class Inventory(
    private val freshIngredients: MutableList<LongRange> = mutableListOf(),
    private val availableIngredients: MutableList<Long> = mutableListOf(),
) {
    fun freshIngredients(): List<LongRange> = freshIngredients

    fun addFreshIngredients(list: LongRange) {
        var curr = list

        val iter = freshIngredients.listIterator()
        while (iter.hasNext()) {
            val r = iter.next()
            if (curr.first <= r.last && curr.last >= r.first) {
                curr = LongRange(
                    start = min(r.first, curr.first),
                    endInclusive = max(r.last, curr.last),
                )
                iter.remove()
            }
        }

        freshIngredients.add(curr)
    }

    fun addAvailableIngredient(ingredient: Long) {
        availableIngredients.add(ingredient)
    }

    fun availableFreshIngredients(): List<Long> =
        availableIngredients.filter { i -> freshIngredients.any { r -> i in r } }
}
