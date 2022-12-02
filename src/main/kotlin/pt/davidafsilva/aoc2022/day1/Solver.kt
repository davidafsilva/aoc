package pt.davidafsilva.aoc2022.day1

import pt.davidafsilva.aoc2022.loadInput

private data class Elf(
    val foodItems: MutableList<FoodItem> = mutableListOf(),
) {
    val totalCalories: Int
        get() = foodItems.sumOf(FoodItem::calories)
}

private data class FoodItem(val calories: Int)

private fun loadElves(): List<Elf> {
    data class State(
        val elves: MutableList<Elf> = mutableListOf(),
        val currentElf: Elf = Elf(),
    )

    return loadInput(day = 1).fold(State()) { state, calories ->
        if (calories.isBlank()) {
            state.elves.add(state.currentElf)
            state.copy(currentElf = Elf())
        } else {
            state.currentElf.foodItems.add(FoodItem(calories.toInt()))
            state
        }
    }.elves
}

fun main() {
    val elves = loadElves().sortedByDescending(Elf::totalCalories)
    println("1st part: ${elves.first().totalCalories}")
    println("2nd part: ${elves.take(3).sumOf(Elf::totalCalories)}")
}
