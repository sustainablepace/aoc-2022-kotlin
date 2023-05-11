package day14

import readInput
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

typealias Coordinate = Pair<Int, Int>
typealias Cave = List<Coordinate>
typealias Sand = MutableList<Coordinate>

fun Coordinate.isFloor(cave:Cave) = second == 2 + cave.maxOf { it.second }
fun List<String>.parse(): Cave = flatMap { structure ->
    structure
        .split(" -> ")
        .map {
            it
                .split(",")
                .let { (from, to) ->
                    from.toInt() to to.toInt()
                }
        }
        .zipWithNext()
        .flatMap { line: Pair<Coordinate, Coordinate> ->
            (if (line.first.first > line.second.first) {
                (line.second.first..line.first.first)
            } else {
                (line.first.first..line.second.first)
            }).flatMap { x ->
                (if (line.first.second > line.second.second) {
                    (line.second.second..line.first.second)
                } else {
                    (line.first.second..line.second.second)
                }).map { y ->
                    x to y
                }
            }
        }.distinct()
}

fun printMap(sand: Sand, cave: Cave) {
    val minX = min(sand.minOfOrNull { it.first } ?: 0, cave.minOfOrNull { it.first } ?: 0)
    val maxX = max(sand.maxOfOrNull { it.first } ?: 0, cave.maxOfOrNull { it.first } ?: 0)
    val minY = min(sand.minOfOrNull { it.second } ?: 0, cave.minOfOrNull { it.second } ?: 0)
    val maxY = max(sand.maxOfOrNull { it.second } ?: 0, cave.maxOfOrNull { it.second } ?: 0)

    if(minX == maxX || minY == maxY) {
        return
    }
    (minY..maxY).forEach {  y ->
        var row = ""
        (minX..maxX).forEach { x ->
            when {
                sand.contains(x to y) -> row += "o"
                cave.contains(x to y) -> row += "#"
                else -> row += "."
            }
        }
        println(row)
    }
    println("----------------------------------------------")

}
fun Coordinate.drop() = first to second + 1
fun Coordinate.dropLeft() = first - 1 to second + 1
fun Coordinate.dropRight() = first + 1 to second + 1

fun main() {
    fun part1(input: List<String>): Int {
        val cave: Cave = input.parse()
        val entrance = 500 to 0
        val sand = mutableListOf<Coordinate>()
        while (true) {
            var nextSand = entrance
            while (true) {
                if (nextSand.drop() in cave || nextSand.drop() in sand) {
                    if (nextSand.dropLeft() !in cave && nextSand.dropLeft() !in sand) {
                        nextSand = nextSand.dropLeft()
                    } else if (nextSand.dropRight() !in cave && nextSand.dropRight() !in sand) {
                        nextSand = nextSand.dropRight()
                    } else {
                        sand.add(nextSand)
                        break;
                    }
                } else {
                    nextSand = nextSand.drop()
                }
                if (nextSand.second > cave.maxOf { it.second }) {
                    break;
                }
            }
            if (nextSand.second > cave.maxOf { it.second }) {
                break;
            }
        }
        printMap(sand, cave)
        return sand.size
    }

    fun part2(input: List<String>): Int {
        val cave: Cave = input.parse()
        val entrance = 500 to 0
        val sand = mutableListOf<Coordinate>()
        while (true) {
            var nextSand = entrance
            while (true) {
                val nextSandDrop = nextSand.drop()
                if (nextSandDrop in cave || nextSandDrop in sand || nextSandDrop.isFloor(cave)) {
                    if (nextSand.dropLeft() !in cave && nextSand.dropLeft() !in sand && !nextSand.dropLeft().isFloor(cave)) {
                        nextSand = nextSand.dropLeft()
                    } else if (nextSand.dropRight() !in cave && nextSand.dropRight() !in sand && !nextSand.dropRight().isFloor(cave)) {
                        nextSand = nextSand.dropRight()
                    } else {
                        sand.add(nextSand)
                        break;
                    }
                } else {
                    nextSand = nextSandDrop
                }
            }
            if (nextSand == entrance) {

                break;
            }
        }
        printMap(sand, cave)

        return sand.size
    }

    val testInput = readInput("day14/Day14_test")
    val input = readInput("day14/Day14")

    println(part1(testInput))
    check(part1(testInput) == 24)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 655)

    println(part2(testInput))
    check(part2(testInput) == 93)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 26484)
}