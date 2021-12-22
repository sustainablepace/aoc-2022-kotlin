package day22

import readInput
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

fun IntRange.hasIntersection(r: IntRange) = first in r || last in r || r.first in this || r.last in this
fun IntRange.intersect(r: IntRange): IntRange = max(first, r.first)..min(last, r.last)
fun IntRange.contains(r: IntRange) = first <= r.first && last >= r.last
fun IntRange.minus(r: IntRange) =
    (
            if (first < r.first) {
                setOf(first..r.first - 1)
            } else emptySet()
            ).union(
            if (last > r.last) {
                setOf(r.last + 1..last)
            } else emptySet()
        )

data class Cuboid(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    val size: Long get() = (xRange.last.toLong() - xRange.first + 1) * (yRange.last - yRange.first + 1) * (zRange.last - zRange.first + 1)

    fun contains(r: Cuboid) = xRange.contains(r.xRange) &&
            yRange.contains(r.yRange) &&
            zRange.contains(r.zRange)

    private fun hasIntersection(c: Cuboid) =
        xRange.hasIntersection(c.xRange) &&
                yRange.hasIntersection(c.yRange) &&
                zRange.hasIntersection(c.zRange)

    fun intersect(c: Cuboid) = if (hasIntersection(c)) {
        Cuboid(
            xRange.intersect(c.xRange),
            yRange.intersect(c.yRange),
            zRange.intersect(c.zRange),
        )
    } else null

    operator fun plus(c: Set<Cuboid>): Set<Cuboid> {
        return c.flatMap { it.minus(this) }.union(setOf(this))
    }
    operator fun minus(c: Cuboid): Set<Cuboid> {
        return if (!hasIntersection(c)) {
            setOf(this)
        } else {
            val intersection = intersect(c)!!
            val xRanges: Set<IntRange> = xRange.minus(intersection.xRange)
            val yRanges: Set<IntRange> = yRange.minus(intersection.yRange)
            val zRanges: Set<IntRange> = zRange.minus(intersection.zRange)

            return if(xRanges.size == 0 && yRanges.size == 0 && zRanges.size == 0) { // #01
                emptySet()
            } else if(xRanges.size == 1 && yRanges.size == 0 && zRanges.size == 0) { // #02
                setOf(Cuboid(xRanges.first(), yRange, zRange))
            } else if(xRanges.size == 0 && yRanges.size == 1 && zRanges.size == 0) { // #03
                setOf(Cuboid(xRange, yRanges.first(), zRange))
            } else if(xRanges.size == 0 && yRanges.size == 0 && zRanges.size == 1) { // #04
                setOf(Cuboid(xRange, yRange, zRanges.first()))
            } else if(xRanges.size == 2 && yRanges.size == 0 && zRanges.size == 0) { // #05
                xRanges.map {
                    Cuboid(it, yRange, zRange)
                }.toSet()
            } else if(xRanges.size == 0 && yRanges.size == 2 && zRanges.size == 0) { // #06
                yRanges.map {
                    Cuboid(xRange, it, zRange)
                }.toSet()
            } else if(xRanges.size == 0 && yRanges.size == 0 && zRanges.size == 2) { // #07
                zRanges.map {
                    Cuboid(xRange, yRange, it)
                }.toSet()
            } else if(xRanges.size == 1 && yRanges.size == 1 && zRanges.size == 0) { // #08
                setOf(
                    Cuboid(xRanges.first(), yRange, zRange),
                    Cuboid(intersection.xRange, yRanges.first(), zRange)
                )
            } else if(xRanges.size == 1 && yRanges.size == 0 && zRanges.size == 1) { // #09
                setOf(
                    Cuboid(xRanges.first(), yRange, zRange),
                    Cuboid(intersection.xRange, yRange, zRanges.first())
                )
            } else if(xRanges.size == 0 && yRanges.size == 1 && zRanges.size == 1) { // #10
                setOf(
                    Cuboid(xRange, yRanges.first(), zRange),
                    Cuboid(xRange, intersection.yRange, zRanges.first())
                )
            } else if(xRanges.size == 1 && yRanges.size == 1 && zRanges.size == 1) { // #11
                setOf(
                    Cuboid(xRanges.first(), yRange, zRange),
                    Cuboid(intersection.xRange, yRanges.first(), zRange),
                    Cuboid(intersection.xRange, intersection.yRange, zRanges.first())
                )
            } else if(xRanges.size == 2 && yRanges.size == 1 && zRanges.size == 1) { // #12
                xRanges.map {
                    Cuboid(it, yRange, zRange)
                }.toSet().union(
                setOf(
                    Cuboid(intersection.xRange, yRanges.first(), zRange),
                    Cuboid(intersection.xRange, intersection.yRange, zRanges.first())
                ) )
            } else if(xRanges.size == 1 && yRanges.size == 2 && zRanges.size == 1) { // #13
                yRanges.map {
                    Cuboid(xRange, it, zRange)
                }.toSet().union(
                setOf(
                    Cuboid(xRanges.first(), intersection.yRange, zRange),
                    Cuboid(intersection.xRange, intersection.yRange, zRanges.first())
                ) )
            } else if(xRanges.size == 1 && yRanges.size == 1 && zRanges.size == 2) {  // #14
                zRanges.map {
                    Cuboid(xRange, yRange, it)
                }.toSet().union(
                setOf(
                    Cuboid(xRanges.first(), yRange, intersection.zRange),
                    Cuboid(intersection.xRange, yRanges.first(), intersection.zRange)
                ) )
            } else if(xRanges.size == 2 && yRanges.size == 2 && zRanges.size == 1) { // #15
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
            } else if(xRanges.size == 2 && yRanges.size == 1 && zRanges.size == 2) { // #16
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
            } else if(xRanges.size == 1 && yRanges.size == 2 && zRanges.size == 2) {  // #17
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
            } else if(xRanges.size == 2 && yRanges.size == 2 && zRanges.size == 2) {  // #18
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
            } else if(xRanges.size == 0 && yRanges.size == 2 && zRanges.size == 2) {  // #19
                yRanges.map {
                    Cuboid(xRange, it, zRange)
                }.toSet().union(
                    zRanges.map {
                        Cuboid(xRange, intersection.yRange, it)
                    }
                )
            } else if(xRanges.size == 2 && yRanges.size == 0 && zRanges.size == 2) {  // #20
                xRanges.map {
                    Cuboid(it, yRange, zRange)
                }.toSet().union(
                    zRanges.map {
                        Cuboid(intersection.xRange, yRange, it)
                    }
                )
            } else if(xRanges.size == 2 && yRanges.size == 2 && zRanges.size == 0) {  // #21
                yRanges.map {
                    Cuboid(xRange, it, zRange)
                }.toSet().union(
                    xRanges.map {
                        Cuboid(it, intersection.yRange, zRange)
                    }
                )
            } else if(xRanges.size == 0 && yRanges.size == 1 && zRanges.size == 2) {  // #22
                zRanges.map {
                    Cuboid(xRange, yRange, it)
                }.toSet().union(
                    setOf(Cuboid(xRange, yRanges.first(), intersection.zRange))
                )
            } else if(xRanges.size == 0 && yRanges.size == 2 && zRanges.size == 1) {  // #23
                yRanges.map {
                    Cuboid(xRange, it, zRange)
                }.toSet().union(
                    setOf(Cuboid(xRange, intersection.yRange, zRanges.first()))
                )
            } else if(xRanges.size == 1 && yRanges.size == 0 && zRanges.size == 2) {  // #24
                zRanges.map {
                    Cuboid(xRange, yRange, it)
                }.toSet().union(
                    setOf(Cuboid(xRanges.first(), yRange, intersection.zRange))
                )
            } else if(xRanges.size == 1 && yRanges.size == 2 && zRanges.size == 0) {  // #25
                yRanges.map {
                    Cuboid(xRange, it, zRange)
                }.toSet().union(
                    setOf(Cuboid(xRanges.first(), intersection.yRange, zRange))
                )
            } else if(xRanges.size == 2 && yRanges.size == 0 && zRanges.size == 1) {  // #26
                xRanges.map {
                    Cuboid(it, yRange, zRange)
                }.toSet().union(
                    setOf(Cuboid(intersection.xRange, yRange, zRanges.first()))
                )
            } else if(xRanges.size == 2 && yRanges.size == 1 && zRanges.size == 0) {  // #27
                xRanges.map {
                    Cuboid(it, yRange, zRange)
                }.toSet().union(
                    setOf(Cuboid(intersection.xRange, yRanges.first(), zRange))
                )
            }  else emptySet()
        }
    }
}

data class Cube(val x: Int, val y: Int, val z: Int)

fun cuboid(xRange: IntRange, yRange: IntRange, zRange: IntRange): Set<Cube> {
    return xRange.flatMap { x ->
        yRange.flatMap { y ->
            zRange.map { z ->
                Cube(x, y, z)
            }.filter { (x, y, z) ->
                x in -50..50 && y in -50..50 && z in -50..50
            }
        }
    }.toSet()
}

fun main() {

    fun part1(input: List<String>): Int {
        val cuboids = input.map { row ->
            row.split(" ").let { (command, cubes) ->
                command to cubes.split(",").let { (xRange, yRange, zRange) ->
                    cuboid(
                        xRange.replace("x=", "").split("..").let { (start, end) ->
                            max(-50, min(start.toInt(), end.toInt()))..min(50, max(start.toInt(), end.toInt()))
                        },
                        yRange.replace("y=", "").split("..").let { (start, end) ->
                            max(-50, min(start.toInt(), end.toInt()))..min(50, max(start.toInt(), end.toInt()))
                        },
                        zRange.replace("z=", "").split("..").let { (start, end) ->
                            max(-50, min(start.toInt(), end.toInt()))..min(50, max(start.toInt(), end.toInt()))
                        }
                    )
                }
            }
        }
        var c = 0
        val rebootSteps = cuboids.reduce { cubes, cube ->
            if (cube.first == "on") {
                "" to cubes.second.union(cube.second)
            } else {
                "" to cubes.second.minus(cube.second)
            }
        }.second
        return rebootSteps.size
    }

    fun part2(input: List<String>): Long {

        val cuboids = input.map { row ->
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
/*
        cuboids = listOf(
            "on" to Cuboid(0..3, 0..3, 0..3),
            "on" to Cuboid(1..4, 1..4, 1..4),
            "off" to Cuboid(0..9, 0..10, 0..10),
            "on" to Cuboid(11..100, 0..10, 0..10),
            "off" to Cuboid(-10..0, 0..10, 0..10)
        )*/
        var on = emptySet<Cuboid>()
        cuboids.forEach { (cmd, cube) ->
            if (cmd == "on") {
                on = cube.plus(on)
            } else {
                on = on.flatMap {
                    it.minus(cube)
                }.toSet()
            }
        }
        return on.sumOf { it.size }
    }

    val testInput = readInput("day22/Day22_test")
    val testInput2 = readInput("day22/Day22_test_2")
    val testInput3 = readInput("day22/Day22_test_3")
    val input = readInput("day22/Day22")

    println(part1(testInput))
    check(part1(testInput) == 39)

    println(part1(testInput2))
    check(part1(testInput2) == 590784)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 591365)


    println(part2(testInput3))
    check(part2(testInput3) == 2758514936282235L)

    val solutionPart2: Long
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 1211172281877240)
}
