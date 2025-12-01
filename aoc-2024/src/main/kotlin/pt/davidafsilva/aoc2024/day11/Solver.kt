package pt.davidafsilva.aoc2024.day11

import pt.davidafsilva.aoc2022.loadInput

fun main() {

    val stones = loadInput(11).first().split(" ")
        .map { it.toLong() }

    // 1st part
    var simulation = Simulation(stones)
    simulation.blink(25)
    println("Total stones: ${simulation.stones.size}")

    // 2nd part
    simulation = Simulation(stones)
    simulation.blink(75)
    println("Total stones: ${simulation.stones.size}")
}

private class Simulation(stones: List<Long>) {
    private data class Blink(val left: Long, val right: Long? = null)

    private val cache = mutableMapOf<Long, MutableList<Blink>>()
    val stones = stones.toMutableList()

    fun blink(times: Int) {
        var idx = 0
        while (idx < stones.size) {
            var stone = stones[idx]
            val blinks = cache.computeIfAbsent(stone) { mutableListOf() }
            val left = times - blinks.size
            repeat(left) { b ->
                val blinkN = left + b + 1
                blinks.add(stone.blink())
            }
            idx++
        }

//        var idx = 0
//        while (idx < stones.size) {
//            val stone = stones[idx]
//            val (left, right) = cache.computeIfAbsent(stone) {
//                when {
//                    stone == 0L -> Val(1)
//                    "$stone".length % 2 == 0 -> {
//                        val div = ("1" + "0".repeat("$stone".length / 2)).toLong()
//                        val left = stone / div
//                        val right = stone % div
//                        Val(left, right)
//                    }
//                    else -> Val(stone * 2024)
//                }
//            }
//            stones[idx] = left
//            idx++
//            if (right != null) {
//                stones.add(idx, right)
//                idx++
//            }
//        }
    }

    private fun Long.blink() = when {
        this == 0L -> Blink(1)
        "$this".length % 2 == 0 -> {
            val div = ("1" + "0".repeat("$this".length / 2)).toLong()
            val left = this / div
            val right = this % div
            Blink(left, right)
        }
        else -> Blink(this * 2024)
    }
}
