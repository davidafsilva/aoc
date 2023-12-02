package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2023.day2

import pt.davidafsilva.aoc2022.loadInput
import kotlin.math.max

fun main() {
    println("1st part: ${loadGames().possibleGames(12, 13, 14).sumOf(Game::id)}")
    println("2nd part: ${loadGames().minCubes().sumOf { c -> c.red * c.green * c.blue }}")
}

private fun Sequence<Game>.minCubes(): Sequence<Cubes> = map { g ->
    g.reveals.fold(Cubes(0, 0, 0)) { acc, r ->
        Cubes(
            red = max(acc.red, r.red),
            green = max(acc.green, r.green),
            blue = max(acc.blue, r.blue),
        )
    }
}

private fun Sequence<Game>.possibleGames(
    maxRed: Int, maxGreen: Int, maxBlue: Int,
): Sequence<Game> = filter { g ->
    g.reveals.all { r -> r.red <= maxRed && r.green <= maxGreen && r.blue <= maxBlue }
}

private fun loadGames(): Sequence<Game> = loadInput(2)
    .map(::parseGame)

private fun parseGame(data: String): Game {
    val (gameIdStr, cubesData) = data.split(":")
    val id = gameIdStr.removePrefix("Game ").toInt()
    val reveals = cubesData.parseReveals()
    return Game(id, reveals)
}

private fun String.parseReveals(): List<Cubes> = splitToSequence(";")
    .map(String::trim)
    .map(String::parseCubeCounts)
    .toList()

private fun String.parseCubeCounts(): Cubes = splitToSequence(",")
    .map(String::trim)
    .fold(Cubes(0, 0, 0)) { acc, cubeData ->
        val (cube, count) = cubeData.parseCubeCount()
        acc.copy(
            red = acc.red + if (cube == "red") count else 0,
            green = acc.green + if (cube == "green") count else 0,
            blue = acc.blue + if (cube == "blue") count else 0,
        )
    }

private fun String.parseCubeCount(): Pair<String, Int> {
    val (count, cube) = split(" ")
    return cube.trim() to count.toInt()
}

private data class Game(val id: Int, val reveals: List<Cubes>)
private data class Cubes(val red: Int, val green: Int, val blue: Int)
