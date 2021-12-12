package past

import readInput
import kotlin.math.abs
import kotlin.math.max
import kotlin.system.measureTimeMillis

typealias Point = Vector

data class Vector(val x: Int, val y: Int) {

    val length = max(abs(x), abs(y))
    val normalized: Vector
        get() = if (length == 0) Vector(0, 0) else Vector(x / length, y / length)

    operator fun minus(v: Vector) = Vector(x - v.x, y - v.y)
    operator fun plus(v: Vector) = Vector(x + v.x, y + v.y)
    operator fun times(factor: Int) = Vector(factor * x, factor * y)
}

class LineSegment(private val p1: Point, p2: Point) {

    private val vector = p2 - p1

    val isDiagonal = vector.normalized.let { v -> v.x != 0 && v.y != 0 }

    fun pointsInLineSegment() = (0..vector.length).map { length -> p1 + vector.normalized * length }

    companion object {
        fun parse(input: List<String>): List<LineSegment> =
            input.map { line ->
                line.split(" -> ")
                    .map { points ->
                        points.split(",").let { (x, y) ->
                            Point(x.toInt(), y.toInt())
                        }
                    }.let { (p1, p2) ->
                        LineSegment(p1, p2)
                    }
            }
    }
}

fun main() {
    fun part1(input: List<String>): Int =
        LineSegment.parse(input).filter {
            !it.isDiagonal
        }.flatMap { line ->
            line.pointsInLineSegment()
        }.groupBy { it }.count { it.value.size > 1 }

    fun part2(input: List<String>): Int =
        LineSegment.parse(input).flatMap { line ->
            line.pointsInLineSegment()
        }.groupBy { it }.count { it.value.size > 1 }

    val testInput = readInput("Day05_test")
    val input = readInput("Day05")

    println(part1(testInput))
    check(part1(testInput) == 5)
    val msPart1 = measureTimeMillis {
        val solutionPart1 = part1(input)
        println(solutionPart1)
        check(solutionPart1 == 4728)
    }
    println("$msPart1 ms")

    println(part2(testInput))
    check(part2(testInput) == 12)
    val msPart2 = measureTimeMillis {
        val solutionPart2 = part2(input)
        println(solutionPart2)
        check(solutionPart2 == 17717)
    }
    println("$msPart2 ms")
}
