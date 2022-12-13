package pt.davidafsilva.aoc2022.day7

import pt.davidafsilva.aoc2022.loadInput
import java.util.LinkedList
import kotlin.LazyThreadSafetyMode.NONE

private data class File(val name: String, val size: Int)
private data class Directory(
    val name: String,
    val directories: MutableList<Directory> = mutableListOf(),
    val files: MutableList<File> = mutableListOf(),
    val parent: Directory? = null,
) {
    val size: Int by lazy(NONE) {
        files.sumOf(File::size) + directories.sumOf(Directory::size)
    }
}

fun main() {
    val root = loadFileSystem()

    println("1st part: ${root.allDirectoriesWithSizeBelowOrEqualTo(100000).sumOf(Directory::size)}")
    println("2nd part: ${chooseDirectoryForDeletion(root, targetFreeSpace = 30_000_000)?.size}")
}

private fun chooseDirectoryForDeletion(root: Directory, targetFreeSpace: Int): Directory? {
    val totalFsSize = 70_000_000
    val additionalFreeSpaceRequired = targetFreeSpace - (totalFsSize - root.size)
    if (additionalFreeSpaceRequired <= 0) return null

    val directories = mutableListOf<Directory>()
    val candidates = LinkedList(listOf(root))
    while (candidates.isNotEmpty()) {
        val dir = candidates.pop()
        if (dir.size == additionalFreeSpaceRequired) return dir
        if (dir.size > additionalFreeSpaceRequired) directories.add(dir)
        candidates.addAll(dir.directories)
    }

    directories.sortBy(Directory::size)
    return directories.firstOrNull()
}

private fun Directory.allDirectoriesWithSizeBelowOrEqualTo(size: Int): List<Directory> {
    val directories = mutableListOf<Directory>()

    val candidates = LinkedList(listOf(this))
    while (candidates.isNotEmpty()) {
        val dir = candidates.pop()
        if (dir.size <= size) directories.add(dir)
        candidates.addAll(dir.directories)
    }

    return directories
}

private fun loadFileSystem(): Directory {
    val root = Directory("/")

    var currentFolder = root
    for (line in loadInput(day = 7)) {
        val parts = line.split(" ")
        when {
            // command: cd
            parts[0] == "$" && parts[1] == "cd" -> {
                currentFolder = when {
                    parts[2] == "/" -> root
                    parts[2] == ".." -> currentFolder.parent
                        ?: error("${currentFolder.name} has no parent directory")
                    else -> currentFolder.directories
                        .firstOrNull { d -> d.name == parts[2] }
                        ?: error("${currentFolder.name} directory has no dir named ${parts[2]}")
                }
            }
            // command: ls
            parts[0] == "$" && parts[1] == "ls" -> continue
            // output: directory
            parts[0] == "dir" -> currentFolder.directories.add(Directory(parts[1], parent = currentFolder))
            // output: file
            else -> currentFolder.files.add(File(name = parts[1], size = parts[0].toInt()))
        }
    }

    return root
}
