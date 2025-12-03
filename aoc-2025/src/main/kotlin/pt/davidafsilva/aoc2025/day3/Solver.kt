package pt.davidafsilva.aoc2022.pt.davidafsilva.aoc2025.day3

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    val banks = loadInput(day = 3).toList()
    println("1st solution: ${banks.sumOf { bank -> pickBatteries(bank, amount = 2) }}")
    println("2nd solution: ${banks.sumOf { bank -> pickBatteries(bank, amount = 12) }}")
}

private fun pickBatteries(bank: String, amount: Int): Long {
    var value = ""

    var left = amount
    var startIdx = 0
    while (left > 0) {
        var highest = bank[startIdx].digitToInt()
        var highestIdx = startIdx
        val endIdx = bank.length - left
        for (i in startIdx + 1..endIdx) {
            val curr = bank[i].digitToInt()
            if (curr > highest) {
                highest = curr
                highestIdx = i
            }
        }

        value = "$value$highest"
        startIdx = highestIdx + 1
        left--
    }

    return value.toLong()
}
