package pt.davidafsilva.aoc2023.day5

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import pt.davidafsilva.aoc2022.scanInput
import java.util.Scanner
import java.util.concurrent.Executors

fun main() {
    val almanac = loadAlmanac()
    val dispatcher = Executors.newVirtualThreadPerTaskExecutor()
        .asCoroutineDispatcher()
    runBlocking(dispatcher) {
        println("1st part: ${closestSeedLocation(almanac, almanac.seeds.map { LongRange(it, it) })}")
        println("2nd part: ${closestSeedLocation(almanac, almanac.seeds.toRange())}")
    }
}

private fun List<Long>.toRange(): List<LongRange> = windowed(size = 2, step = 2)
    .map { (start, length) -> LongRange(start, start + length - 1) }

private suspend fun closestSeedLocation(
    almanac: Almanac,
    seedRanges: List<LongRange>,
) = coroutineScope {
    seedRanges.map { sr ->
        async { sr.minOf(almanac::seedLocation) }
    }.awaitAll().min()
}

private fun Almanac.seedLocation(seed: Long) = seed.mapWith(seedToSoil)
    .mapWith(soilToFertilizer)
    .mapWith(fertilizerToWater)
    .mapWith(waterToLight)
    .mapWith(lightToTemperature)
    .mapWith(temperatureToHumidity)
    .mapWith(humidityToLocation)

private fun Long.mapWith(mappings: List<Mapping>) = mappings
    .firstNotNullOfOrNull { m -> m(this) } ?: this

private fun loadAlmanac(): Almanac = scanInput(5).use { sc ->
    val parseMappings = fun() = run { sc.nextLine(); sc.nextMappings() }
    Almanac(
        seeds = run { loadSeeds(sc.nextLine()).also { sc.nextLine() } },
        seedToSoil = parseMappings(),
        soilToFertilizer = parseMappings(),
        fertilizerToWater = parseMappings(),
        waterToLight = parseMappings(),
        lightToTemperature = parseMappings(),
        temperatureToHumidity = parseMappings(),
        humidityToLocation = parseMappings(),
    )
}

private fun Scanner.nextMappings(): List<Mapping> {
    val mappings = mutableListOf<Mapping>()

    while (hasNextLong()) {
        val destinationStart = next().toLong()
        val sourceStart = next().toLong()
        val length = next().toLong()
        mappings.add(
            Mapping(
                sourceRange = LongRange(sourceStart, sourceStart + length - 1),
                destinationStart = destinationStart,
            )
        )
    }

    nextLine() // line break
    if (hasNext()) nextLine() // mappings separator

    return mappings
}

private fun loadSeeds(seedsLine: String) = seedsLine.removePrefix("seeds: ")
    .splitToSequence(" ")
    .map(String::toLong)
    .toList()

private data class Almanac(
    val seeds: List<Long>,
    val seedToSoil: List<Mapping>,
    val soilToFertilizer: List<Mapping>,
    val fertilizerToWater: List<Mapping>,
    val waterToLight: List<Mapping>,
    val lightToTemperature: List<Mapping>,
    val temperatureToHumidity: List<Mapping>,
    val humidityToLocation: List<Mapping>,
)

private data class Mapping(
    private val sourceRange: LongRange,
    private val destinationStart: Long,
) {
    operator fun invoke(n: Long): Long? = when (n) {
        in sourceRange -> destinationStart + (n - sourceRange.first)
        else -> null
    }
}
