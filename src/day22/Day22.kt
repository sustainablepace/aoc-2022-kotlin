package day22

import readInput
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

fun IntRange.hasIntersection(r: IntRange) = first in r || last in r || r.first in this || r.last in this
fun IntRange.intersect(r: IntRange): IntRange = max(first, r.first)..min(last, r.last)
fun IntRange.contains(r: IntRange) = first <= r.first && last >= r.last
operator fun IntRange.minus(r: IntRange) =
    if (first < r.first) {
        setOf(first..r.first - 1)
    } else {
        emptySet()
    } + if (last > r.last) {
        setOf(r.last + 1..last)
    } else {
        emptySet()
    }

data class Cuboid(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {

    val size: Long
        get() = (xRange.last.toLong() - xRange.first + 1) *
                (yRange.last - yRange.first + 1) *
                (zRange.last - zRange.first + 1)

    fun intersect(c: Cuboid) =
        if (xRange.hasIntersection(c.xRange) &&
            yRange.hasIntersection(c.yRange) &&
            zRange.hasIntersection(c.zRange)
        ) {
            Cuboid(
                xRange.intersect(c.xRange),
                yRange.intersect(c.yRange),
                zRange.intersect(c.zRange),
            )
        } else null

    operator fun plus(c: Set<Cuboid>): Set<Cuboid> {
        return c.flatMap { it - this }.union(setOf(this))
    }

    operator fun minus(c: Cuboid): Set<Cuboid> =
        intersect(c)?.let { intersection ->
            val xRanges: Set<IntRange> = xRange - intersection.xRange
            val yRanges: Set<IntRange> = yRange - intersection.yRange
            val zRanges: Set<IntRange> = zRange - intersection.zRange
            val l = listOf(xRanges, yRanges, zRanges)

            return if (l.sumOf { it.size } == 1 || l.sumOf { it.size } == 2 && l.any { it.size == 2 }) {
                xRanges.map { Cuboid(it, yRange, zRange) }.toSet() +
                        yRanges.map { Cuboid(xRange, it, zRange) } +
                        zRanges.map { Cuboid(xRange, yRange, it) }
            } else if (xRanges.size == 1 && yRanges.size == 1 && zRanges.size == 0) { // #08
                xRanges.map { Cuboid(it, yRange, zRange) }.toSet() +
                        yRanges.map { Cuboid(intersection.xRange, it, zRange) }
            } else if (xRanges.size == 1 && yRanges.size == 0 && zRanges.size == 1) { // #09
                zRanges.map { Cuboid(xRange, yRange, it) }.toSet() +
                        xRanges.map { Cuboid(it, yRange, intersection.zRange) }
            } else if (xRanges.size == 0 && yRanges.size == 1 && zRanges.size == 1) { // #10
                yRanges.map {Cuboid(xRange, it, zRange) }.toSet() +
                        zRanges.map { Cuboid(xRange, intersection.yRange, it) }
            } else if (xRanges.size == 1 && yRanges.size == 1 && zRanges.size == 1) { // #11
                setOf(
                    Cuboid(xRanges.first(), yRange, zRange),
                    Cuboid(intersection.xRange, yRanges.first(), zRange),
                    Cuboid(intersection.xRange, intersection.yRange, zRanges.first())
                )
            } else if (xRanges.size == 2 && yRanges.size == 1 && zRanges.size == 1) { // #12
                xRanges.map {
                    Cuboid(it, yRange, zRange)
                }.toSet().union(
                    setOf(
                        Cuboid(intersection.xRange, yRanges.first(), zRange),
                        Cuboid(intersection.xRange, intersection.yRange, zRanges.first())
                    )
                )
            } else if (xRanges.size == 1 && yRanges.size == 2 && zRanges.size == 1) { // #13
                yRanges.map {
                    Cuboid(xRange, it, zRange)
                }.toSet().union(
                    setOf(
                        Cuboid(xRanges.first(), intersection.yRange, zRange),
                        Cuboid(intersection.xRange, intersection.yRange, zRanges.first())
                    )
                )
            } else if (xRanges.size == 1 && yRanges.size == 1 && zRanges.size == 2) {  // #14
                zRanges.map {
                    Cuboid(xRange, yRange, it)
                }.toSet().union(
                    setOf(
                        Cuboid(xRanges.first(), yRange, intersection.zRange),
                        Cuboid(intersection.xRange, yRanges.first(), intersection.zRange)
                    )
                )
            } else if (xRanges.size == 2 && yRanges.size == 2 && zRanges.size == 1) { // #15
                xRanges.map {
                    Cuboid(it, yRange, zRange)
                }.toSet().union(
                    yRanges.map {
                        Cuboid(intersection.xRange, it, zRange)
                    }.toSet()
                ).union(
                    setOf(
                        Cuboid(intersection.xRange, intersection.yRange, zRanges.first()),
                    )
                )
            } else if (xRanges.size == 2 && yRanges.size == 1 && zRanges.size == 2) { // #16
                xRanges.map {
                    Cuboid(it, yRange, zRange)
                }.toSet().union(
                    zRanges.map {
                        Cuboid(intersection.xRange, yRange, it)
                    }.toSet()
                ).union(
                    setOf(
                        Cuboid(intersection.xRange, yRanges.first(), intersection.zRange),
                    )
                )
            } else if (xRanges.size == 1 && yRanges.size == 2 && zRanges.size == 2) {  // #17
                yRanges.map {
                    Cuboid(xRange, it, zRange)
                }.toSet().union(
                    zRanges.map {
                        Cuboid(xRange, intersection.yRange, it)
                    }.toSet()
                ).union(
                    setOf(
                        Cuboid(xRanges.first(), intersection.yRange, intersection.zRange),
                    )
                )
            } else if (xRanges.size == 2 && yRanges.size == 2 && zRanges.size == 2) {  // #18
                xRanges.map {
                    Cuboid(it, yRange, zRange)
                }.toSet().union(
                    yRanges.map {
                        Cuboid(intersection.xRange, it, zRange)
                    }.toSet()
                ).union(
                    zRanges.map {
                        Cuboid(intersection.xRange, intersection.yRange, it)
                    }.toSet()
                )
            } else if (xRanges.size == 0 && yRanges.size == 2 && zRanges.size == 2) {  // #19
                yRanges.map {
                    Cuboid(xRange, it, zRange)
                }.toSet().union(
                    zRanges.map {
                        Cuboid(xRange, intersection.yRange, it)
                    }
                )
            } else if (xRanges.size == 2 && yRanges.size == 0 && zRanges.size == 2) {  // #20
                xRanges.map {
                    Cuboid(it, yRange, zRange)
                }.toSet().union(
                    zRanges.map {
                        Cuboid(intersection.xRange, yRange, it)
                    }
                )
            } else if (xRanges.size == 2 && yRanges.size == 2 && zRanges.size == 0) {  // #21
                yRanges.map {
                    Cuboid(xRange, it, zRange)
                }.toSet().union(
                    xRanges.map {
                        Cuboid(it, intersection.yRange, zRange)
                    }
                )
            } else if (xRanges.size == 0 && yRanges.size == 1 && zRanges.size == 2) {  // #22
                zRanges.map {
                    Cuboid(xRange, yRange, it)
                }.toSet().union(
                    setOf(Cuboid(xRange, yRanges.first(), intersection.zRange))
                )
            } else if (xRanges.size == 0 && yRanges.size == 2 && zRanges.size == 1) {  // #23
                yRanges.map {
                    Cuboid(xRange, it, zRange)
                }.toSet().union(
                    setOf(Cuboid(xRange, intersection.yRange, zRanges.first()))
                )
            } else if (xRanges.size == 1 && yRanges.size == 0 && zRanges.size == 2) {  // #24
                zRanges.map {
                    Cuboid(xRange, yRange, it)
                }.toSet().union(
                    setOf(Cuboid(xRanges.first(), yRange, intersection.zRange))
                )
            } else if (xRanges.size == 1 && yRanges.size == 2 && zRanges.size == 0) {  // #25
                yRanges.map {
                    Cuboid(xRange, it, zRange)
                }.toSet().union(
                    setOf(Cuboid(xRanges.first(), intersection.yRange, zRange))
                )
            } else if (xRanges.size == 2 && yRanges.size == 0 && zRanges.size == 1) {  // #26
                xRanges.map {
                    Cuboid(it, yRange, zRange)
                }.toSet() +
                        setOf(Cuboid(intersection.xRange, yRange, zRanges.first()))

            } else if (xRanges.size == 2 && yRanges.size == 1 && zRanges.size == 0) {  // #27
                xRanges.map {
                    Cuboid(it, yRange, zRange)
                }.toSet().union(
                    setOf(Cuboid(intersection.xRange, yRanges.first(), zRange))
                )
            } else emptySet()

        } ?: setOf(this)
}

data class Cube(val x: Int, val y: Int, val z: Int)

fun cuboids(input: List<String>) = input.map { row ->
    row.split(" ").let { (command, cubes) ->
        command to cubes.split(",").let { (xRange, yRange, zRange) ->
            Cuboid(
                xRange.replace("x=", "").split("..").let { (start, end) ->
                    min(start.toInt(), end.toInt())..max(start.toInt(), end.toInt())
                },
                yRange.replace("y=", "").split("..").let { (start, end) ->
                    min(start.toInt(), end.toInt())..max(start.toInt(), end.toInt())
                },
                zRange.replace("z=", "").split("..").let { (start, end) ->
                    min(start.toInt(), end.toInt())..max(start.toInt(), end.toInt())
                }
            )
        }
    }
}

fun main() {

    fun part1(input: List<String>): Long {

        var on = emptySet<Cuboid>()
        cuboids(input).mapNotNull { (cmd, cuboid) ->
            cuboid.intersect(Cuboid(-50..50, -50..50, -50..50))?.let {
                cmd to it
            }
        }.forEach { (cmd, cube) ->
            if (cmd == "on") {
                on = cube + on
            } else {
                on = on.flatMap { it - cube }.toSet()
            }
        }
        return on.sumOf { it.size }
    }


    fun part2(input: List<String>): Long {

        var on = emptySet<Cuboid>()
        cuboids(input).forEach { (cmd, cube) ->
            if (cmd == "on") {
                on = cube + on
            } else {
                on = on.flatMap { it - cube }.toSet()
            }
        }
        return on.sumOf { it.size }
    }

    val testInput = readInput("day22/Day22_test")
    val testInput2 = readInput("day22/Day22_test_2")
    val testInput3 = readInput("day22/Day22_test_3")
    val input = readInput("day22/Day22")

    println(part1(testInput))
    check(part1(testInput) == 39L)

    println(part1(testInput2))
    check(part1(testInput2) == 590784L)

    val solutionPart1: Long
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 591365L)


    println(part2(testInput3))
    check(part2(testInput3) == 2758514936282235L)

    val solutionPart2: Long
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 1211172281877240)
}
