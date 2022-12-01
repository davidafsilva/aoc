package pt.davidafsilva.aoc2022.day1

import kotlin.streams.asSequence
import java.lang.ClassLoader.getSystemResourceAsStream as loadResource

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

    return calories().fold(State()) { state, calories ->
        if (calories.isBlank()) {
            state.elves.add(state.currentElf)
            state.copy(currentElf = Elf())
        } else {
            state.currentElf.foodItems.add(FoodItem(calories.toInt()))
            state
        }
    }.elves
}

private fun calories(): Sequence<String> = loadResource("day1/input.txt")!!
    .bufferedReader()
    .lines().asSequence()

fun main() {
    val elves = loadElves().sortedByDescending(Elf::totalCalories)
    println("1st part: ${elves.first().totalCalories}")
    println("2nd part: ${elves.take(3).sumOf(Elf::totalCalories)}")
}
