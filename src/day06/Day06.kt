package day06

import readInput

typealias Message = String

fun Message.firstStartOfPacketMarker() = windowed(4, 1).indexOfFirst { it.toSet().size == 4 } + 4
fun Message.firstStartOfMessageMarker() = windowed(14, 1).indexOfFirst { it.toSet().size == 14 } + 14

fun main() {
    fun part1(input: List<String>): Int = input.first().firstStartOfPacketMarker()
    fun part2(input: List<String>): Int = input.first().firstStartOfMessageMarker()

    val testInput = readInput("day06/Day06_test")
    val input = readInput("day06/Day06")

    check(part1(testInput).also { println(it) } == 7)
    check(part1(input).also { println(it) } == 1080)

    check(part2(testInput).also { println(it) } == 19)
    check(part2(input).also { println(it) } == 3645)
}
