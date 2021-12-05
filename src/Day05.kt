import kotlin.math.abs
import kotlin.math.max

data class LineSegment(val p1: Vector, val p2: Vector) {
    private val vector: Vector = p2 - p1
    val isDiagonal: Boolean = vector.normalized.let { it.x != 0 && it.y != 0 }

    fun pointsInLineSegment(): List<Vector> =
        (0..vector.length).map { factor ->
            p1 + vector.normalized * factor
        }

    companion object {
        fun parse(input: List<String>): List<LineSegment> =
            input.map { line ->
                line.split(" -> ")
                    .map { points ->
                        points.split(",").let { (x, y) ->
                            Vector(x.toInt(), y.toInt())
                        }
                    }.let { (p1, p2) ->
                        LineSegment(p1, p2)
                    }
            }
    }
}

data class Vector(val x: Int, val y: Int) {
    val length: Int = max(abs(x), abs(y))
    val normalized: Vector
        get() = if (length == 0) Vector(0, 0) else Vector(x / length, y / length)

    operator fun minus(p: Vector): Vector = Vector(x - p.x, y - p.y)
    operator fun plus(p: Vector): Vector = Vector(x + p.x, y + p.y)
    operator fun times(factor: Int) = Vector(factor * x, factor * y)
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
    println(part1(testInput))
    check(part1(testInput) == 5)

    val input = readInput("Day05")
    println(part1(input))

    println(part2(testInput))
    check(part2(testInput) == 12)
    println(part2(input))
}
