package pt.davidafsilva.aoc2022.day13

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import pt.davidafsilva.aoc2022.day13.InOrderComparisonResult.IN_ORDER
import pt.davidafsilva.aoc2022.day13.InOrderComparisonResult.OUT_OF_ORDER
import pt.davidafsilva.aoc2022.day13.InOrderComparisonResult.UNDEFINED
import pt.davidafsilva.aoc2022.loadInput
import kotlin.math.max

fun main() {
    val packets = loadInput(day = 13)
        .filter { it.isNotBlank() }
        .map(PacketValue::parse)
        .toList()
    println("1st part: ${packets.chunked(2).map { (l, r) -> l to r }.countInOrder()}")
    println("2nd part: ${packets.computeDecoderKey()}")
}

private fun List<PacketValue>.computeDecoderKey(): Int {
    val dividerPacket1 = PacketValue.Lst(listOf(PacketValue.Lst(listOf(PacketValue.Numeric(2)))))
    val dividerPacket2 = PacketValue.Lst(listOf(PacketValue.Lst(listOf(PacketValue.Numeric(6)))))
    val organizedPackets = (this + listOf(dividerPacket1, dividerPacket2)).sortedWith { p1, p2 ->
        when (computeInOrder(p1, p2)) {
            IN_ORDER -> -1
            UNDEFINED -> 0
            OUT_OF_ORDER -> 1
        }
    }
    return organizedPackets.foldIndexed(1) { idx, acc, p ->
        if (p === dividerPacket1 || p === dividerPacket2) acc * (idx + 1)
        else acc
    }
}

private fun List<Pair<PacketValue, PacketValue>>.countInOrder(): Int = mapIndexedNotNull { idx, (l, r) ->
    if (computeInOrder(l, r) == IN_ORDER) idx + 1 else null
}.sum()

private fun computeInOrder(left: PacketValue, right: PacketValue): InOrderComparisonResult {
    // both numerics
    if (left is PacketValue.Numeric && right is PacketValue.Numeric) return computeInOrder(left, right)

    // promote left/right members to list if they're numeric
    val l = when (left) {
        is PacketValue.Numeric -> PacketValue.Lst(listOf(left))
        is PacketValue.Lst -> left
    }
    val r = when (right) {
        is PacketValue.Numeric -> PacketValue.Lst(listOf(right))
        is PacketValue.Lst -> right
    }

    return computeInOrder(l, r)

}

private fun computeInOrder(left: PacketValue.Lst, right: PacketValue.Lst): InOrderComparisonResult {
    for (idx in 0 until max(left.values.size, right.values.size)) {
        val leftValue = left.values.getOrNull(idx) ?: return IN_ORDER
        val rightValue = right.values.getOrNull(idx) ?: return OUT_OF_ORDER

        val status = computeInOrder(leftValue, rightValue)
        if (status != UNDEFINED) return status
    }

    return UNDEFINED
}

private fun computeInOrder(left: PacketValue.Numeric, right: PacketValue.Numeric): InOrderComparisonResult = when {
    left.value < right.value -> IN_ORDER
    left.value == right.value -> UNDEFINED
    else -> OUT_OF_ORDER
}

private enum class InOrderComparisonResult { IN_ORDER, OUT_OF_ORDER, UNDEFINED }

private sealed class PacketValue {
    data class Lst(val values: List<PacketValue>) : PacketValue() {
        override fun toString(): String = values.toString()
    }

    data class Numeric(val value: Int) : PacketValue() {
        override fun toString(): String = value.toString()
    }

    companion object {
        fun parse(input: String): PacketValue = unpack(Json.parseToJsonElement(input))

        private fun unpack(e: JsonElement): PacketValue = when (e) {
            is JsonArray -> Lst(e.map(::unpack))
            is JsonPrimitive -> Numeric(e.content.toInt())
            else -> error("unsupported type: ${e.javaClass}")
        }
    }
}
