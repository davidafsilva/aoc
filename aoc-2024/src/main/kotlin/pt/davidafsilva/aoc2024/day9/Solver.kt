package pt.davidafsilva.aoc2024.day9

import pt.davidafsilva.aoc2022.loadInput

fun main() {
    val diskMap = loadInput(9).first()
    val fileBlocks = mutableListOf<Int>()
    val freeBlocks = mutableListOf<Int>()
    for ((idx, block) in diskMap.withIndex()) {
        if (idx % 2 == 0) {
            fileBlocks.add(block.digitToInt())
        } else {
            freeBlocks.add(block.digitToInt())
        }
    }

    // 1st part
    println("checksum: ${compactChecksum(fileBlocks, freeBlocks)}")
}

fun compactChecksum(fileBlocks: MutableList<Int>, freeBlocks: MutableList<Int>): Long {
    var checksum = 0L
    var id = 0
    var idx = 0
    var reverseIdx = fileBlocks.size - 1
    var reverseBlocksLeft = fileBlocks.last()

    checksum@ while (idx < fileBlocks.size) {
        // file block
        if (idx < reverseIdx) {
            repeat(fileBlocks[idx]) {
                checksum += id++ * idx
            }
            idx++
        }

        // check free space
        if (reverseIdx >= idx) {
            var capacity = freeBlocks[idx - 1]
            while (capacity > 0) {
                if (reverseBlocksLeft == 0) {
                    if (reverseIdx == idx) break@checksum
                    reverseIdx--
                    reverseBlocksLeft = fileBlocks[reverseIdx]
                }
                checksum += (id++ * reverseIdx)
                reverseBlocksLeft--
                capacity--
            }
        }
    }

    return checksum
}
