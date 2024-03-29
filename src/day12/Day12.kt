package day12

import readInput
import kotlin.system.measureTimeMillis

typealias Coordinates = Pair<Int, Int>
typealias Terrain = Array<CharArray>

operator fun Coordinates.plus(other: Coordinates) = first + other.first to second + other.second

class Map(val start: Coordinates, val destination: Coordinates, val terrain: Terrain) {
    fun contains(coordinates: Coordinates) =
        coordinates.first >= 0 &&
                coordinates.second >= 0 &&
                coordinates.first < terrain.size &&
                coordinates.second < terrain.first().size
}

abstract class Route(val map: Map) {
    private val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

    protected fun Char.elevation() = when (this) {
        'S' -> 'a'
        'E' -> 'z'
        else -> this
    }.code

    private fun Coordinates.isAccessibleFrom(other: Coordinates): Boolean {
        val origin = map.terrain[other.first][other.second].elevation()
        val target = map.terrain[first][second].elevation()
        return target <= 1 + origin
    }

    abstract fun calculateRoute(): Int

    fun calculateRoute(routes: List<List<Coordinates>>): Int {
        var currentRoutes = routes
        while (currentRoutes.all { it.last() != map.destination }) {
            currentRoutes = currentRoutes.flatMap { route ->
                directions.map {
                    it + route.last()
                }.filter { next ->
                    map.contains(next) && next.isAccessibleFrom(route.last())
                }.map {
                    val l = route.toMutableList()
                    l.add(it)
                    l
                }
            }.groupBy {
                it.last()
            }.map {
                it.value.first()
            }
        }
        return currentRoutes.first {
            it.last() == map.destination
        }.size - 1
    }
}

class ScenicRoute(map: Map) : Route(map) {
    override fun calculateRoute(): Int {
        val valleys = map.terrain.flatMapIndexed { row: Int, line: CharArray ->
            line.mapIndexed { column, char ->
                if (char.elevation() == 'a'.code) {
                    row to column
                } else null
            }
        }.filterNotNull().map { listOf(it) }
        return calculateRoute(valleys)
    }
}

class StandardRoute(map: Map) : Route(map) {
    override fun calculateRoute(): Int {
        return calculateRoute(listOf(listOf(map.start)))
    }
}

fun parse(input: List<String>): Map {
    val rows = input.size
    val columns = input[0].length
    val landscape = Array(rows) { CharArray(columns) }
    lateinit var start: Coordinates
    lateinit var destination: Coordinates
    input.forEachIndexed { row, line ->
        line.forEachIndexed { column, char ->
            landscape[row][column] = char
            if (char == 'S') start = row to column
            if (char == 'E') destination = row to column
        }
    }
    return Map(start, destination, landscape)
}

fun main() {
    fun part1(input: List<String>): Int {
        val map = parse(input)
        val route = StandardRoute(map)
        return route.calculateRoute()
    }

    fun part2(input: List<String>): Int {
        val map = parse(input)
        val route = ScenicRoute(map)
        return route.calculateRoute()
    }

    val testInput = readInput("day12/Day12_test")
    val input = readInput("day12/Day12")

    println(part1(testInput))
    check(part1(testInput) == 31)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 330)

    println(part2(testInput))
    check(part2(testInput) == 29)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 321)
}
