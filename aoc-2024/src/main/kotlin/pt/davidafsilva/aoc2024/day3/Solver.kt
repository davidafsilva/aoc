package pt.davidafsilva.aoc2024.day3

import pt.davidafsilva.aoc2022.loadInput
import java.util.regex.Pattern
import kotlin.streams.asSequence

fun main() {
    // 1st part
    var res = loadInput(3).mul()
    println("Multiplications: $res")

    // 2nd part
    res = loadInput(3).removeDisabled().mul()
    println("Enabled Multiplications: $res")
}

private fun Sequence<String>.removeDisabled(): Sequence<String> = sequence {
    val doo = "do()"
    val dont = "don't()"

    val line = this@removeDisabled.joinToString()
    var startIdx = 0
    while (startIdx < line.length) {
        // deal with the enabled portion
        val disableIdx = line.indexOf(dont, startIndex = startIdx)
        if (disableIdx < 0) {
            // report the remaining string
            yield(line.substring(startIdx))
            break
        }

        // report the enabled portion
        yield(line.substring(startIdx, disableIdx))
        startIdx = disableIdx + dont.length

        // "remove" the disabled portion up to the do()
        val enabledIdx = line.indexOf(doo, startIndex = startIdx)
        if (enabledIdx < 0) break
        startIdx = enabledIdx + doo.length
    }
}

private fun Sequence<String>.mul(): Long = fold(0L) { acc, line ->
    val m = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)").matcher(line)
    acc + m.results().asSequence().sumOf { r -> r.group(1).toLong() * r.group(2).toLong() }
}
