package pt.davidafsilva.aoc2024.day9

import pt.davidafsilva.aoc2022.loadInput

private sealed class Block {
    data class File(val id: Int, val size: Int) : Block()
    data class Free(val size: Int) : Block()
}

fun main() {
    val map = loadInput(9).joinToString("")
    val disk = mutableListOf<Block>()
    var id = 0
    for ((idx, block) in map.withIndex()) {
        if (idx % 2 == 0) {
            val size = block.digitToInt()
            disk.add(Block.File(id, size))
            id++
        } else {
            disk.add(Block.Free(size = block.digitToInt()))
        }
    }

    // hack to easily - aka no changes - share the 2nd solution with the 1st one
    val expandedDisk = disk.flatMapTo(mutableListOf()) { b ->
        if (b is Block.File) {
            List(b.size) { Block.File(b.id, size = 1) }
        } else {
            listOf(b)
        }
    }

    // 1st part
    println("Checksum: ${checksum(expandedDisk)}")

    // 2nd part
    println("Checksum: ${checksum(disk.toMutableList())}")
}

private fun checksum(disk: MutableList<Block>): Long {
    // compact
    var rIdx = disk.size - 1
    while (rIdx >= 0) {
        val block = disk[rIdx]
        if (block is Block.File) {
            val freeBlockIdxValue = disk.withIndex().asSequence()
                .take(rIdx)
                .firstOrNull { (_, b) -> b is Block.Free && b.size >= block.size }
            if (freeBlockIdxValue != null) {
                val freeBlock = freeBlockIdxValue.value as Block.Free
                val capacityLeft = freeBlock.size - block.size
                disk[freeBlockIdxValue.index] = block
                disk[rIdx] = Block.Free(block.size)
                if (capacityLeft > 0) {
                    disk.add(freeBlockIdxValue.index + 1, Block.Free(capacityLeft))
                    rIdx++
                }
            }
        }
        rIdx--
    }

    // checksum
    var checksum = 0L
    var idx = 0
    disk.forEach { b ->
        when (b) {
            is Block.File -> repeat(b.size) {
                checksum += (idx++ * b.id)
            }
            is Block.Free -> idx += b.size
        }
    }

    return checksum
}
