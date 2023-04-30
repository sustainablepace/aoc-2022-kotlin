package day17

import readInput
import kotlin.system.measureTimeMillis

fun main() {
    fun part1(input: List<String>): Int = TODO()

    fun part2(input: List<String>): Int = TODO()

    val testInput = readInput("day17/Day17_test")
    val input = readInput("day17/Day17")

    println(part1(testInput))
    check(part1(testInput) == TODO())

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == TODO())

    println(part2(testInput))
    check(part2(testInput) == TODO())

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == TODO())

}
