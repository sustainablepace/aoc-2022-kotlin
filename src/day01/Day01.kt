package day01

import readInput

typealias Snacks = List<Int>

fun snacks(input: List<String>): List<Snacks> {
    return input.joinToString(",")
        .split(",,")
        .map { elf ->
            elf.split(",").map { it.toInt() }
        }
}
fun Snacks.calories(): Int = sum()
fun List<Snacks>.sortedByCalories() = map { it.calories() }.sortedDescending()

fun main() {
    fun part1(input: List<String>): Int =
        snacks(input).sortedByCalories().first()

    fun part2(input: List<String>): Int =
        snacks(input).sortedByCalories().take(3).sum()

    val testInput = readInput("day01/Day01_test")
    println(part1(testInput))
    check(part1(testInput) == 24000)
    println(part2(testInput))
    check(part2(testInput) == 45000)

    val input = readInput("day01/Day01")
    println(part1(input))
    println(part2(input))
}
