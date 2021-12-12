package past

import past.Connection.Companion.connections
import readInput
import kotlin.system.measureTimeMillis

interface Cave {
    val name: String

    fun isStart() = name == "start"
    fun isEnd() = name == "end"
}

@JvmInline
value class SmallCave(override val name: String) : Cave {
    init {
        assert(name.lowercase() == name)
    }
}

@JvmInline
value class BigCave(override val name: String) : Cave {
    init {
        assert(name.uppercase() == name)
    }
}

data class Connection(val from: Cave, val to: Cave) {
    companion object {
        fun connections(input: List<String>) = input.flatMap { connection ->
            connection.split("-").map {
                if (it.lowercase() == it) SmallCave(it) else BigCave(it)
            }.let { (from, to) ->
                listOf(
                    Connection(from, to),
                    Connection(to, from)
                )
            }
        }
    }
}

class Path(val caves: List<Cave>) {

    private var smallCaveHasBeenVisitedTwice: Boolean = false

    fun aSmallCaveHasBeenVisitedTwice(): Boolean {
        if(!smallCaveHasBeenVisitedTwice) {
            smallCaveHasBeenVisitedTwice = caves.filterIsInstance<SmallCave>().any { smallCave -> caves.count { it == smallCave } == 2}
        }
        return smallCaveHasBeenVisitedTwice
    }


    override fun toString() = caves.joinToString(",") { it.name }
}

fun main() {
    fun part1(input: List<String>): Int {
        val network = connections(input)
        val startConnection = network.filter { it.from.isStart() }
        var paths = startConnection.map { Path(listOf(it.from, it.to)) }

        while (paths.any { !it.caves.last().isEnd() }) {
            paths = paths.flatMap { path ->
                val lastCave = path.caves.last()
                if (lastCave.isEnd()) {
                    listOf(path)
                } else {
                    network.filter { it.from == lastCave && (it.to !is SmallCave || !path.caves.contains(it.to)) }.map {
                        Path(path.caves.toMutableList() + it.to)
                    }
                }
            }
        }
        return paths.size
    }

    fun part2(input: List<String>): Int {
        val network = connections(input)
        val startConnection = network.filter { it.from.isStart() }
        var paths = startConnection.map { Path(listOf(it.from, it.to)) }

        while (paths.any { !it.caves.last().isEnd() }) {
            paths = paths.flatMap { path ->
                val lastCave = path.caves.last()
                if (lastCave.isEnd()) {
                    listOf(path)
                } else {
                    network.filter { it.from == lastCave }.mapNotNull {
                        if (it.to is SmallCave && path.caves.contains(it.to) && path.aSmallCaveHasBeenVisitedTwice() || it.to.isStart()) {
                            null
                        } else {
                            Path(path.caves.toMutableList() + it.to)
                        }
                    }
                }
            }
        }
        return paths.size
    }

    val testInput = readInput("Day12_test")
    val testInput2 = readInput("Day12_test_2")
    val testInput3 = readInput("Day12_test_3")
    val input = readInput("Day12")

    println(part1(testInput))
    check(part1(testInput) == 10)

    println(part1(testInput2))
    check(part1(testInput2) == 19)

    println(part1(testInput3))
    check(part1(testInput3) == 226)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 4749)

    println(part2(testInput))
    check(part2(testInput) == 36)

    println(part2(testInput2))
    check(part2(testInput2) == 103)

    println(part2(testInput3))
    check(part2(testInput3) == 3509)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 123054)
}
