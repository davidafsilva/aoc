package pt.davidafsilva.aoc2023.day6

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    println("1st part: ${loadRaces().map(Race::waysToWin).reduce { acc, v -> acc * v }}")
    println("2nd part: ${loadRace().waysToWin()}")
}

private fun Race.waysToWin(): Long {
    val middle = duration / 2
    var total = 0L
    for (p in middle downTo 0) {
        val distance = distance(p, duration)
        if (distance <= record) break
        total++
    }
    total *= 2
    if (duration % 2.0 == 0.0) total--
    return total
}

private fun distance(pressedFor: Long, duration: Long): Long =
    pressedFor * (duration - pressedFor)

private fun loadRace(): Race {
    val (durationsLine, recordsLine) = loadInput(6).toList()
    val duration = durationsLine.substringAfter(":").replace(" ", "").toLong()
    val record = recordsLine.substringAfter(":").replace(" ", "").toLong()
    return Race(duration, record)
}

private fun loadRaces(): List<Race> {
    val (durationsLine, recordsLine) = loadInput(6).toList()
    val durations = durationsLine.readIntValues()
    val records = recordsLine.readIntValues()
    if (durations.size != records.size) error("invalid races")
    return durations.zip(records).map { (d, r) -> Race(d, r) }
}

private fun String.readIntValues() = substringAfter(":")
    .trim()
    .splitToSequence(" ")
    .filter(String::isNotBlank)
    .map(String::toLong)
    .toList()

private data class Race(
    val duration: Long,
    val record: Long,
)
