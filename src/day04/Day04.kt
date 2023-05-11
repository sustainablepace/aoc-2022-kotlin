package day04

import readInput

import kotlin.math.max
import kotlin.math.min
fun IntRange.intersect(r: IntRange): IntRange = max(first, r.first)..min(last, r.last)

typealias CleanupAssignmentPair = Pair<IntRange, IntRange>

fun CleanupAssignmentPair.oneFullyContainsTheOther(): Boolean = first.intersect(second).let { it == second || it == first }
fun CleanupAssignmentPair.overlap(): Boolean = first.intersect(second).count() != 0

fun List<String>.cleanupAssignments(): List<CleanupAssignmentPair> = map { line: String ->
    line.split(",").map { pair: String ->
        pair.split("-").let { (elf1, elf2) ->
            elf1.toInt()..elf2.toInt()
        }
    }.let { (range1, range2) ->
        range1 to range2
    }
}

fun main() {
    fun part1(input: List<String>): Int = input.cleanupAssignments().count(CleanupAssignmentPair::oneFullyContainsTheOther)
    fun part2(input: List<String>): Int = input.cleanupAssignments().count(CleanupAssignmentPair::overlap)

    val testInput = readInput("day04/Day04_test")
    val input = readInput("day04/Day04")

    check(part1(testInput).also { println(it) } == 2)
    check(part1(input).also { println(it) } == 556)

    check(part2(testInput).also { println(it) } == 4)
    check(part2(input).also { println(it) } == 876)
}
