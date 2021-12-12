package past

import readInput
import java.util.Collections.max
import java.util.Collections.min
import kotlin.math.abs
import kotlin.system.measureTimeMillis

fun main() {
    fun part1(input: List<String>): Int {
        val crabs = input.first().split(",").map { it.toInt() }
        return min((min(crabs)..max(crabs)).map { pos ->
            crabs.sumOf { abs(it - pos) }
        })
    }

    fun part2(input: List<String>): Int {
        val crabs = input.first().split(",").map { it.toInt() }
        return min((min(crabs)..max(crabs)).map { pos ->
            crabs.sumOf { abs(it - pos).let { n -> n * (n + 1) / 2 } }
        })
    }

    val testInput = readInput("Day07_test")
    val input = readInput("Day07")

    println(part1(testInput))
    check(part1(testInput) == 37)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 352254)

    println(part2(testInput))
    check(part2(testInput) == 168)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 99053143)

}
