package pt.davidafsilva.aoc2022

import java.lang.ClassLoader.getSystemResourceAsStream
import java.util.Scanner
import kotlin.streams.asSequence

internal fun loadInput(day: Int): Sequence<String> =
    getSystemResourceAsStream("day$day/input.txt")!!
        .bufferedReader()
        .lines().asSequence()

internal fun scanInput(day: Int): Scanner = Scanner(
    getSystemResourceAsStream("day$day/input.txt")!!
        .bufferedReader()
)
