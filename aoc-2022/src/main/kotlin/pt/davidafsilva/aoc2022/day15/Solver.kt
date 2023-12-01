package pt.davidafsilva.aoc2022.day15

import pt.davidafsilva.aoc2022.loadInput
import kotlin.math.abs

fun main() {
    val sensors = loadSensors()
    println("1st part: ${sensors.row(2000000).count { it == Type.IN_RANGE }}")
    println("2nd part: ${sensors.findDistressBeacon()?.let { it.x * 4_000_000L + it.y }}")
}

private fun List<Sensor>.findDistressBeacon(): Point? {
    val min = 0
    val max = 4_000_000

    val candidates = flatMapTo(mutableSetOf()) { s ->
        val edge = s.distance + 1
        (0..edge).flatMapTo(mutableListOf()) { depth ->
            // compute the points on the edge of each sensor range
            sequenceOf(
                // top
                Point(s.location.x - depth, s.location.y + edge - depth),
                Point(s.location.x + depth, s.location.y + edge - depth),
                // bottom
                Point(s.location.x - depth, s.location.y - edge + depth),
                Point(s.location.x + depth, s.location.y - edge + depth),
            ).filter { pt ->
                // within bounds
                pt.x >= min && pt.y >= min && pt.x <= max && pt.y <= max
            }.filter { center ->
                // which is not in range,but it is between
                // two points that are
                val left = Point(center.x - 1, center.y)
                val right = Point(center.x + 1, center.y)
                type(left) == Type.IN_RANGE &&
                    type(center) == Type.VOID &&
                    type(right) == Type.IN_RANGE
            }
        }
    }

    // should be only one
    return candidates.firstOrNull()
}

private fun List<Sensor>.row(y: Int): Sequence<Type> {
    val minX = minOf { s -> s.min.x }
    val maxX = maxOf { s -> s.max.x }
    return (minX..maxX).asSequence()
        .map { x -> type(Point(x, y)) }
}

private fun List<Sensor>.type(pt: Point): Type {
    var type = Type.VOID
    for (s in this) {
        if (s.location == pt) return Type.SENSOR
        if (s.closestBeaconLocation == pt) return Type.BEACON
        if (s.isInRange(pt)) type = Type.IN_RANGE
    }
    return type
}

private fun loadSensors() = loadInput(day = 15).map { line ->
    val parts = line.split(" ")
    fun extractAxis(at: Int) = parts[at].substring(2).removeSuffix(",").removeSuffix(":").toInt()
    val sensorX = extractAxis(2)
    val sensorY = extractAxis(3)
    val beaconX = extractAxis(8)
    val beaconY = extractAxis(9)
    Sensor(
        location = Point(sensorX, sensorY),
        closestBeaconLocation = Point(beaconX, beaconY),
    )
}.toList()

private data class Point(val x: Int, val y: Int)
private data class Sensor(
    val location: Point,
    val closestBeaconLocation: Point,
) {
    val distance = distance(location, closestBeaconLocation)
    val min = Point(location.x - distance, location.y - distance)
    val max = Point(location.x + distance, location.y + distance)

    fun isInRange(pt: Point): Boolean = distance(location, pt) <= distance

    private fun distance(p1: Point, p2: Point) = abs(p1.x - p2.x) + abs(p1.y - p2.y)
}

private enum class Type(val code: Char) {
    SENSOR('S'),
    BEACON('B'),
    IN_RANGE('#'),
    VOID('.'),
}
