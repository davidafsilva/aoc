package pt.davidafsilva.aoc2022.day2

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    println("1st part: ${totalScore()}")
}

private enum class Shape(
    val value: Int,
    private val keys: Set<String>,
) {
    ROCK(value = 1, keys = setOf("A"/*, "X"*/)),
    PAPER(value = 2, keys = setOf("B"/*, "Y"*/)),
    SCISSORS(value = 3, keys = setOf("C"/*, "Z"*/));

    companion object {
        private val keysMap: Map<String, Shape>

        init {
            keysMap = mutableMapOf()
            for (shape in values()) {
                shape.keys.forEach { k -> keysMap[k] = shape }
            }
        }

        fun fromKey(key: String): Shape = requireNotNull(keysMap[key]) {
            "invalid shape for key: $key"
        }
    }
}

private enum class RoundResult(val key: String) {
    LOSE(key = "X"),
    DRAW(key = "Y"),
    WIN(key = "Z");

    companion object {
        private val keyMapping = values().associateBy(RoundResult::key)

        fun fromKey(key: String): RoundResult = requireNotNull(keyMapping[key]) {
            "invalid result for key: $key"
        }
    }
}

private fun totalScore(): Int = loadInput(day = 2)
    .sumOf { roundShapes ->
        val (otherKey, resultKey) = roundShapes.split(" ", limit = 2)
        val otherShape = Shape.fromKey(otherKey)
        //val yourShape = Shape.fromKey(yourKey)
        val result = RoundResult.fromKey(resultKey)
        val yourShape = computeShapeForResult(result, otherShape)
        roundScore(otherShape, yourShape)
    }

private fun computeShapeForResult(
    result: RoundResult,
    otherShape: Shape,
): Shape = when (result) {
    RoundResult.WIN -> when (otherShape) {
        Shape.ROCK -> Shape.PAPER
        Shape.PAPER -> Shape.SCISSORS
        Shape.SCISSORS -> Shape.ROCK
    }
    RoundResult.DRAW -> otherShape
    RoundResult.LOSE -> when (otherShape) {
        Shape.ROCK -> Shape.SCISSORS
        Shape.PAPER -> Shape.ROCK
        Shape.SCISSORS -> Shape.PAPER
    }
}

private fun roundScore(
    other: Shape,
    your: Shape,
): Int = your.value + when {
    // tie
    your == other -> 3
    // win
    your == Shape.ROCK && other == Shape.SCISSORS ||
        your == Shape.PAPER && other == Shape.ROCK ||
        your == Shape.SCISSORS && other == Shape.PAPER -> 6
    // loss
    else -> 0
}
