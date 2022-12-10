package pt.davidafsilva.aoc2022.day9

import pt.davidafsilva.aoc2022.loadInput
import java.awt.Point
import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val head = Point(0, 0)
    // 1st part = single tail
    // val tails = Array(1) { Point(0, 0) }
    val tails = Array(9) { Point(0, 0) }
    val pointsVisitedByLastTail = mutableSetOf(head.str())

    for (move in loadInput(day = 9)) {
        val (direction, count) = move.split(" ")

        // move the head and its tail(s)
        repeat(count.toInt()) {
            when (direction) {
                "R" -> {
                    head.translate(1, 0)
                    adjustTails(head, tails)
                }
                "L" -> {
                    head.translate(-1, 0)
                    adjustTails(head, tails)
                }
                "U" -> {
                    head.translate(0, 1)
                    adjustTails(head, tails)
                }
                "D" -> {
                    head.translate(0, -1)
                    adjustTails(head, tails)
                }
            }

            // update visited locations
            pointsVisitedByLastTail.add(tails.last().str())
        }
    }
    println("1st/2nd part: ${pointsVisitedByLastTail.size}")
}

private fun adjustTails(
    head: Point,
    tails: Array<Point>,
) {
    var prev = head
    var currentIdx = 0
    while (currentIdx < tails.size && prev.distanceSq(tails[currentIdx]) > 2.0) {
        val tail = tails[currentIdx++]

        val deltaX = abs(prev.x - tail.x)
        val deltaY = abs(prev.y - tail.y)

        // horizontal
        if (deltaX > 1 && deltaY == 0) tail.translate(
            sign(0.0 + prev.x - tail.x).toInt(),
            0
        )
        // verticle
        else if (deltaY > 1 && deltaX == 0) tail.translate(
            0,
            sign(0.0 + prev.y - tail.y).toInt(),
        )
        // diagonal
        else tail.translate(
            sign(0.0 + prev.x - tail.x).toInt(),
            sign(0.0 + prev.y - tail.y).toInt(),
        )

        prev = tail
    }
}

private fun Point.str(): String = "[$x,$y]"
