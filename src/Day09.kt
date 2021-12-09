import kotlin.math.abs
import kotlin.system.measureTimeMillis

data class P(val x: Int, val y: Int, val v: Int)

data class Cave(val width: Int, val height: Int, val points: Set<P>) {
    fun neighbours(p: P) = points.filter {
        abs(it.x - p.x) + abs(it.y - p.y) == 1
    }.toSet()

    fun lowPoints(): Set<P> {
        return points.filter { p ->
            neighbours(p).let { n ->
                n.isNotEmpty() && n.all { p.v < it.v }
            }
        }.toSet()
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val m = input.mapIndexed { y, p ->
            p.indices.map { x ->
                P(x, y, input[y][x].toString().toInt())
            }
        }.flatten().toSet()

        val cave = Cave(input.maxOf { it.length }, input.size, m)

        return cave.lowPoints().sumOf {
            1 + it.v
        }
    }

    class Basin(lowPoint: P) {
        val expansion: MutableList<Set<P>> = mutableListOf(setOf(lowPoint))

        fun contains(p: P) = expansion.any { it.contains(p) }

        fun expand(cave: Cave) {
            expansion.last().flatMap { p ->
                cave.neighbours(p).filter { it.v < 9 && it.v > p.v && !contains(it) }.toSet()
            }.toSet().let { expanded ->
                if (expanded.isNotEmpty()) {
                    expansion.add(expanded)
                }
            }
        }

        val size: Int get() = expansion.sumOf { it.size }
    }

    fun part2(input: List<String>): Int {
        val m = input.mapIndexed { y, p ->
            p.indices.map { x ->
                P(x, y, input[y][x].toString().toInt())
            }
        }.flatten().toSet()

        val cave = Cave(input.maxOf { it.length }, input.size, m)

        val lowPoints = cave.lowPoints()
        val basins = lowPoints.map {
            Basin(it)
        }

        repeat(cave.height) {
            basins.map {
                it.expand(cave)
            }
        }

        return basins.map {
            it.size
        }.sortedDescending().take(3).fold(1) { acc, v -> acc * v }
    }

    val testInput = readInput("Day09_test")
    val input = readInput("Day09")

    println(part1(testInput))
    check(part1(testInput) == 15)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 478)

    println(part2(testInput))
    check(part2(testInput) == 1134)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 1327014)
}
