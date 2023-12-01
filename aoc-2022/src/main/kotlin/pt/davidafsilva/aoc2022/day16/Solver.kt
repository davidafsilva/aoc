package pt.davidafsilva.aoc2022.day16

import pt.davidafsilva.aoc2022.loadInput
import java.util.LinkedList
import kotlin.math.max

fun main() {
    val valves = loadValves()
    val valvesDistanceMap = valves.mapValues { (_, v) -> valves.distanceMap(from = v) }

    var pressure = valves.totalEventualPressure(
        distanceMap = valvesDistanceMap,
        activePlayer = Player("AA", 30),
    )
    println("1st part: $pressure")

    pressure = valves.totalEventualPressure(
        distanceMap = valvesDistanceMap,
        activePlayer = Player("AA", 26),
        standingByPlayer = Player("AA", 26),
    )
    println("2nd part: $pressure")
}

private fun Map<String, Valve>.distanceMap(from: Valve): Map<String, Int> {
    val distances = mapValuesTo(mutableMapOf()) { 0 }
    distances.remove(from.code)

    data class Candidate(val hops: Int, val code: String)

    val visited = mutableSetOf(from.code)
    val candidates = from.next.mapTo(LinkedList()) {
        Candidate(hops = 1, code = it)
    }
    while (candidates.isNotEmpty()) {
        val candidate = candidates.remove()
        if (!visited.add(candidate.code)) continue

        distances[candidate.code] = candidate.hops

        get(candidate.code)!!.next.mapTo(candidates) {
            Candidate(hops = candidate.hops + 1, code = it)
        }
    }

    return distances
}

private fun Map<String, Valve>.totalEventualPressure(
    distanceMap: Map<String, Map<String, Int>>,
    activePlayer: Player,
    standingByPlayer: Player? = null,
    valvesToOpen: Set<String> = filterValues { v -> v.flowRate > 0 }.keys,
): Long {
    val pressures = distanceMap[activePlayer.currentValveCode]!!
        .mapValues { (code, distance) ->
            val flowRate = get(code)!!.flowRate
            val cost = distance + 1
            cost to (activePlayer.ticksLeft - cost) * flowRate
        }
        .filter { (c, cr) -> cr.second > 0 && c in valvesToOpen }
        .toMutableMap()

    var mostPressure = activePlayer.accumulatedPressure +
        (standingByPlayer?.accumulatedPressure ?: 0)
    while (pressures.isNotEmpty()) {
        val (targetValveCode, costRatePair) = pressures.maxBy { (_, p) -> p.second }
        pressures.remove(targetValveCode)

        val updatedPlayer = activePlayer.copy(
            currentValveCode = targetValveCode,
            ticksLeft = activePlayer.ticksLeft - costRatePair.first,
            accumulatedPressure = activePlayer.accumulatedPressure + costRatePair.second,
        )
        mostPressure = max(
            mostPressure,
            totalEventualPressure(
                distanceMap,
                activePlayer = standingByPlayer ?: updatedPlayer,
                standingByPlayer = standingByPlayer?.let { updatedPlayer },
                valvesToOpen = valvesToOpen - targetValveCode,
            )
        )
    }

    return mostPressure
}

private fun loadValves() = loadInput(day = 16).associate { line ->
    val parts = line.split(" ")
    val code = parts[1]
    val flowRate = parts[4]
        .removePrefix("rate=")
        .removeSuffix(";")
        .toLong()
    val next = parts.subList(9, parts.size)
        .map { it.removeSuffix(",") }
    code to Valve(code, flowRate, next)
}

private data class Player(
    val currentValveCode: String,
    val ticksLeft: Int,
    val accumulatedPressure: Long = 0,
)

private data class Valve(
    val code: String,
    val flowRate: Long,
    val next: List<String>,
)
