package day18

import readInput
import kotlin.math.abs

typealias Droplet = Set<Cube>
typealias Cube = Triple<Int, Int, Int>

fun Cube.neighbours(): List<Cube> = toList().flatMapIndexed { index: Int, i: Int ->
    listOf(
        toList().toMutableList().also { it[index] = i - 1 }.toList(),
        toList().toMutableList().also { it[index] = i + 1 }.toList()
    )
}.map { it.toCube() }

fun List<Int>.toCube() = take(3).run {
    Triple(get(0), get(1), get(2))
}

fun List<String>.droplet(): Droplet = map { line ->
    line.split(",").map { it.toInt() }.toCube()
}.toSet()

fun Droplet.openSides(): Int = sumOf { cube ->
    cube.neighbours().count { neighbour: Cube -> this.none { it == neighbour } }
}

fun Cube.isNeighbourOf(cube: Cube): Boolean =
    abs(cube.first - first) == 1 && cube.second == second && cube.third == third ||
            cube.first == first && abs(cube.second - second) == 1 && cube.third == third ||
            cube.first == first && cube.second == second && abs(cube.third - third) == 1

fun Droplet.hull() = box().let { box ->
    val maxX = box.maxBy { it.first }.first
    val minX = box.minBy { it.first }.first
    val maxY = box.maxBy { it.second }.second
    val minY = box.minBy { it.second }.second
    val maxZ = box.maxBy { it.third }.third
    val minZ = box.minBy { it.third }.third
    box.mapNotNull {
        it.takeIf { cube ->
            cube.first == maxX || cube.first == minX || cube.second == maxY || cube.second == minY || cube.third == maxZ || cube.third == minZ
        }
    }.toSet()
}

fun Droplet.openSidesWithoutPockets(): Int {
    val potentialAirPockets = box().minus(this).toMutableList()
    val surroundingAirPockets = hull().toMutableSet()
    var newSurroundingAirCubes: Set<Cube>

    do {
        newSurroundingAirCubes = potentialAirPockets.flatMap { cube ->
            if (surroundingAirPockets.any { it.isNeighbourOf(cube) }) {
                listOf(cube)
            } else emptyList()
        }.toSet()
        surroundingAirPockets.addAll(newSurroundingAirCubes)
        potentialAirPockets.removeAll(newSurroundingAirCubes)

    } while (newSurroundingAirCubes.isNotEmpty())

    return openSides() - potentialAirPockets.toSet().openSides()
}

fun Droplet.box(): Droplet = (minBy { it.first }.first - 1 until maxBy { it.first }.first + 2).flatMap { x ->
    (minBy { it.second }.second - 1 until maxBy { it.second }.second + 2).flatMap { y ->
        (minBy { it.third }.third - 1 until maxBy { it.third }.third + 2).map { z ->
            Triple(x, y, z)
        }
    }
}.toSet()

fun main() {
    fun part1(input: List<String>): Int = input.droplet().openSides()
    fun part2(input: List<String>): Int = input.droplet().openSidesWithoutPockets()

    val testInput = readInput("day18/Day18_test")
    val input = readInput("day18/Day18")

    check(part1(testInput).also { println(it) } == 64)
    check(part1(input).also { println(it) } == 3496)

    check(part2(testInput).also { println(it) } == 58)
    check(part2(input).also { println(it) } == 2064)
}
