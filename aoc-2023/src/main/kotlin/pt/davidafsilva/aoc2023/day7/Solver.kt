package pt.davidafsilva.aoc2023.day7

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    println("1st part: ${loadGame(jackAsJoker = false).playsRank().sumOf { (p, r) -> p.bid * r }}")
    // 249240696 low (keep J)
    // 249255067 low
    println("2nd part: ${loadGame(jackAsJoker = true).playsRank().sumOf { (p, r) -> p.bid * r }}")
}

private fun Game.playsRank(): List<Pair<Play, Int>> {
    val rank = plays.sortedBy(Play::hand)
    return plays.map { p -> p to rank.indexOf(p) + 1 }
}

private fun loadGame(jackAsJoker: Boolean): Game {
    var plays = loadInput(7).map { line ->
        val (cards, bid) = line.split(" ")
        val hand = Hand(toCards(cards), handType(cards))
        Play(hand, bid.toLong())
    }
    if (jackAsJoker) plays = plays.replaceJokers()
    return Game(plays.toList())
}

private fun Sequence<Play>.replaceJokers() = map { p ->
    val jokersCount = p.hand.cards.count { c -> c == Card.JACK }
    val newHandType = when (jokersCount) {
        0 -> p.hand.type
        5 -> Type.FIVE_OF_A_KIND
        else -> {
            val originalHand = p.hand.cards.joinToString(separator = "") { c -> c.label.toString() }
            p.hand.cards.asSequence()
                .filter { c -> c != Card.JACK }
                .distinct()
                .map { c -> originalHand.replace(Card.JACK.label, c.label) }
                .map { h -> Hand(p.hand.cards, handType(h)) }
                .sorted()
                .last()
                .type
        }
    }
    val newCards = p.hand.cards.map { c ->
        if (c == Card.JACK) Card.JOKER else c
    }
    p.copy(hand = Hand(newCards, newHandType))
}

private fun toCards(cards: String): List<Card> = cards.map(Card.Companion::from)

private fun handType(cards: String): Type {
    val cardsCount = cards.groupBy { it }
    return when (cardsCount.size) {
        1 -> Type.FIVE_OF_A_KIND
        2 -> {
            val firstCount = cardsCount.entries.first().value.size
            if (firstCount == 4 || firstCount == 1) Type.FOUR_OF_A_KIND
            else Type.FULL_HOUSE
        }
        3 -> {
            if (cardsCount.any { (_, c) -> c.size == 3 }) Type.THREE_OF_A_KIND
            else Type.TWO_PAIRS
        }
        4 -> Type.PAIR
        5 -> Type.HIGH_CARD
        else -> error("expected 5 cards")
    }

}

private data class Game(val plays: List<Play>)
private data class Hand(val cards: List<Card>, val type: Type) : Comparable<Hand> {
    override fun compareTo(other: Hand): Int {
        // by type
        val typeCmp = type.ordinal - other.type.ordinal
        if (typeCmp != 0) return typeCmp

        // by first diff card
        for ((idx, c1) in cards.withIndex()) {
            val c2 = other.cards[idx]
            val r = c1.ordinal - c2.ordinal
            if (r != 0) return r
        }

        return 0
    }
}

private data class Play(
    val hand: Hand,
    val bid: Long,
)

private enum class Card(val label: Char) {
    JOKER('Z'),
    TWO('2'), THREE('3'), FOUR('4'), FIVE('5'),
    SIX('6'), SEVEN('7'), EIGHT('8'), NINE('9'),
    TEN('T'), JACK('J'), QUEEN('Q'), KING('K'),
    ACE('A');

    companion object {
        fun from(label: Char) = entries.first { c -> c.label == label }
    }
}

private enum class Type {
    HIGH_CARD, PAIR,
    TWO_PAIRS,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND,
}
