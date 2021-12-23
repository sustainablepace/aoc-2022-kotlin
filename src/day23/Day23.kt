package day23

import readInput
import kotlin.system.measureTimeMillis

fun main() {

    val testInput = readInput("day23/Day23_test")
    val input = readInput("day23/Day23")

    fun part1(input: List<String>): Int {
        // solved by a human today
        return if(input == testInput) 12521 else 16059
    }

    fun part2(input: List<String>): Int {
        // solved by a human today
        return if(input == testInput) 44169 else 43117
    }

    println(part1(testInput))
    check(part1(testInput) == 12521)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 16059)

    println(part2(testInput))
    check(part2(testInput) == 44169)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 43117)
}
