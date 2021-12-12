import kotlin.system.measureTimeMillis

interface Cave {
    val name: String
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

data class Connection(val from: Cave, val to: Cave)

@JvmInline
value class Path(val caves: List<Cave>) {
    fun aSmallCaveHasBeenVisitedTwice(): Boolean {
        return caves.filterIsInstance<SmallCave>().any { smallCave -> caves.count { it == smallCave } == 2 }
    }

    override fun toString() = caves.map { it.name }.joinToString(",")
}

fun main() {
    fun part1(input: List<String>): Int {
        val network = input.map { connection ->
            connection.split("-").map {
                if (it.lowercase() == it) {
                    SmallCave(it)
                } else {
                    BigCave(it)
                }
            }.let { (from, to) ->
                Connection(from, to)
            }
        }
        val startConnection = network.filter { it.from.name == "start" || it.to.name == "start" }
        var paths = startConnection.map {
            if (it.from.name == "start") {
                Path(listOf(it.from, it.to))
            } else {
                Path(listOf(it.to, it.from))
            }
        }
        while (paths.any { it.caves.last().name != "end" }) {
            paths = paths.flatMap { path ->
                val lastCave = path.caves.last().name
                if (lastCave == "end") {
                    listOf(path)
                } else {
                    network.filter { it.from.name == lastCave || it.to.name == lastCave }.mapNotNull {
                        if (it.from.name == lastCave) {
                            if (it.to is SmallCave && path.caves.contains(it.to)) {
                                null // visit small caves only once
                            } else {
                                Path(path.caves.toMutableList() + it.to)
                            }
                        } else {
                            if (it.from is SmallCave && path.caves.contains(it.from)) {
                                null // visit small caves only once
                            } else {
                                Path(path.caves.toMutableList() + it.from)
                            }
                        }
                    }
                }
            }
        }
        return paths.size
    }

    fun part2(input: List<String>): Int {
        val network = input.map { connection ->
            connection.split("-").map {
                if (it.lowercase() == it) {
                    SmallCave(it)
                } else {
                    BigCave(it)
                }
            }.let { (from, to) ->
                Connection(from, to)
            }
        }
        val startConnection = network.filter { it.from.name == "start" || it.to.name == "start" }
        var paths = startConnection.map {
            if (it.from.name == "start") {
                Path(listOf(it.from, it.to))
            } else {
                Path(listOf(it.to, it.from))
            }
        }
        while (paths.any { it.caves.last().name != "end" }) {
            paths = paths.flatMap { path ->
                val lastCave = path.caves.last().name
                if (lastCave == "end") {
                    listOf(path)
                } else {
                    network.filter { it.from.name == lastCave || it.to.name == lastCave }.mapNotNull {
                        if (it.from.name == lastCave) {
                            if (it.to is SmallCave && path.caves.contains(it.to) && path.aSmallCaveHasBeenVisitedTwice() || it.to.name == "start" || it.from.name == "start") {
                                null // visit small caves only once
                            } else {
                                Path(path.caves.toMutableList() + it.to)
                            }
                        } else {
                            if (it.from is SmallCave && path.caves.contains(it.from) && path.aSmallCaveHasBeenVisitedTwice()|| it.from.name == "start"|| it.to.name == "start") {
                                null // visit small caves only once
                            } else {
                                Path(path.caves.toMutableList() + it.from)
                            }
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
