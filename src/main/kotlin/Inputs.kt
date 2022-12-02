package pt.davidafsilva.aoc2022

import java.lang.ClassLoader.getSystemResourceAsStream
import kotlin.streams.asSequence

internal fun loadInput(
    day: Int,
): Sequence<String> = getSystemResourceAsStream("day$day/input.txt")!!
    .bufferedReader()
    .lines().asSequence()
