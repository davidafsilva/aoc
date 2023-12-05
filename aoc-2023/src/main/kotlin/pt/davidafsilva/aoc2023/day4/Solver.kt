package pt.davidafsilva.aoc2023.day4

import pt.davidafsilva.aoc2022.loadInput
import kotlin.math.pow

fun main() {
    println("1st part: ${loadCards().map(Card::totalPoints).sum()}")
    println("1st part: ${loadCards().totalScratchcards()}")
}

private fun Sequence<Card>.totalScratchcards(): Int {
    val original = toList()
    val cardCopies = mutableMapOf<Int, Int>()
    var total = 0

    for ((nIdx, card) in original.withIndex()) {
        val cardNumber = nIdx + 1
        val copies = cardCopies.remove(cardNumber) ?: 0
        total += 1 + copies

        val matches = card.totalMatchingNumbers()
        for (cn in (cardNumber + 1)..(cardNumber + matches)) {
            cardCopies.compute(cn) { _, curr -> (curr ?: 0) + 1 + copies }
        }
    }

    return total
}

private fun Card.totalPoints(): Int {
    val matching = totalMatchingNumbers()
    return when {
        matching <= 1 -> matching
        else -> 2.0.pow(matching - 1.0).toInt()
    }
}

private fun Card.totalMatchingNumbers() = numbers.count { n -> n in winning }

private fun loadCards() = loadInput(4).map(String::parseCard)

private fun String.parseCard(): Card {
    val (_, allNumbers) = split(":")
    val (winning, numbers) = allNumbers.trim().split("|")
    return Card(
        winning = winning.parseNumbers(),
        numbers = numbers.parseNumbers(),
    )
}

private fun String.parseNumbers(): List<Int> = splitToSequence(" ")
    .mapNotNull { n -> n.trim().toIntOrNull() }
    .toList()

private data class Card(val winning: List<Int>, val numbers: List<Int>)
