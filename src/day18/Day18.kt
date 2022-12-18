package day18

import readInput
import kotlin.math.abs

typealias Droplet = Set<Cube>
typealias Cube = Triple<Int, Int, Int>

fun List<Int>.cube(): Cube = take(3).run { Triple(get(0), get(1), get(2)) }
fun List<String>.droplet(): Droplet = map { line -> line.split(",").map { it.toInt() }.cube() }.toSet()

fun Cube.neighbours(): Set<Cube> = toList().flatMapIndexed { index: Int, i: Int ->
    listOf(
        toList().toMutableList().also { it[index] = i - 1 }.toList(),
        toList().toMutableList().also { it[index] = i + 1 }.toList()
    )
}.map { it.cube() }.toSet()

fun Cube.isNeighbourOf(cube: Cube): Boolean =
    abs(cube.first - first) + abs(cube.second - second) + abs(cube.third - third) == 1

fun Droplet.max() = Triple(maxBy { it.first }.first, maxBy { it.second }.second, maxBy { it.third }.third)
fun Droplet.min() = Triple(minBy { it.first }.first, minBy { it.second }.second, minBy { it.third }.third)

fun Droplet.hull(): Set<Cube> = box().let { box ->
    val max = box.max()
    val min = box.min()
    box.mapNotNull {
        it.takeIf { cube ->
            cube.first == max.first || cube.first == min.first ||
                    cube.second == max.second || cube.second == min.second ||
                    cube.third == max.third || cube.third == min.third
        }
    }.toSet()
}

fun Droplet.box(): Set<Cube> = (minBy { it.first }.first - 1 until maxBy { it.first }.first + 2).flatMap { x ->
    (minBy { it.second }.second - 1 until maxBy { it.second }.second + 2).flatMap { y ->
        (minBy { it.third }.third - 1 until maxBy { it.third }.third + 2).map { z ->
            Triple(x, y, z)
        }
    }
}.toSet()

fun Droplet.openSides(): Int = sumOf { cube -> cube.neighbours().count { neighbour -> this.none { it == neighbour } } }

fun Droplet.openSidesWithoutPockets(): Int {
    val potentialAirPockets = box().minus(this).toMutableSet()
    val surroundingAirPockets = hull().toMutableSet()
    var newSurroundingAirCubes: Set<Cube>

    do {
        newSurroundingAirCubes = potentialAirPockets.flatMap { cube ->
            if (surroundingAirPockets.any { it.isNeighbourOf(cube) }) {
                setOf(cube)
            } else emptySet()
        }.toSet()
        surroundingAirPockets.addAll(newSurroundingAirCubes)
        potentialAirPockets.removeAll(newSurroundingAirCubes)

    } while (newSurroundingAirCubes.isNotEmpty())

    return openSides() - potentialAirPockets.openSides()
}

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
