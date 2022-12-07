package day07

import readInput

class File(val name: String, val size: Long)

class Directory(
    val parent: Directory? = null,
    val name: String,
    val directories: MutableList<Directory> = mutableListOf(),
    val files: MutableList<File> = mutableListOf()
) {
    fun fileSize(): Long = directories.sumOf { it.fileSize() } + files.sumOf { it.size }

    fun allSubDirectories(): List<Directory> {
        val l = mutableListOf<Directory>()
        l.add(this)
        l.addAll(directories.flatMap { it.allSubDirectories() })
        return l.toList()
    }

    fun rootDirectory(): Directory = if (parent !== null) {
        parent.rootDirectory()
    } else this
}

fun List<String>.filesystem(): Directory {
    var currentDirectory = Directory(null, "/")
    forEach { line ->
        if (line.startsWith("$ ls")) {
            // ignore
        } else if (line.startsWith("$ cd")) {
            line.substring(5).let { newDir ->
                currentDirectory = when (newDir) {
                    "/" -> currentDirectory.rootDirectory()
                    ".." -> currentDirectory.parent!!
                    else -> currentDirectory.directories.first { it.name.endsWith("$newDir/") }
                }
            }
        } else {
            line.split(" ").let { (left, right) ->
                if (left == "dir") {
                    currentDirectory.directories.add(
                        Directory(
                            parent = currentDirectory,
                            name = currentDirectory.name + right + "/"
                        )
                    )
                } else {
                    currentDirectory.files.add(
                        File(
                            name = right,
                            size = left.toLong()
                        )
                    )
                }
            }
        }
    }
    return currentDirectory.rootDirectory()
}

fun main() {
    fun part1(input: List<String>): Long = input.filesystem().let { root ->
        root.allSubDirectories().map { it.fileSize() }.filter { it <= 100000 }.sumOf { it }
    }

    fun part2(input: List<String>): Long = input.filesystem().let { root ->
        val totalSize = 70000000L
        val requiredSizeForUpdate = 30000000L
        val freeSpace = totalSize - root.fileSize()
        val needed = requiredSizeForUpdate - freeSpace

        root.allSubDirectories().map { it.fileSize() }.sortedByDescending { it }.last { it >= needed }
    }

    val testInput = readInput("day07/Day07_test")
    val input = readInput("day07/Day07")

    check(part1(testInput).also { println(it) } == 95437L)
    check(part1(input).also { println(it) } == 1367870L)

    check(part2(testInput).also { println(it) } == 24933642L)
    check(part2(input).also { println(it) } == 549173L)
}
