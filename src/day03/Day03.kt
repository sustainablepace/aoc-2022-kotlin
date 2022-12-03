package day03

import readInput

typealias Rucksack = Pair<Compartment, Compartment>
typealias Compartment = String
typealias ItemType = Char

fun ItemType.priority() = if (isUpperCase()) {
    code - 65 + 27
} else {
    code - 97 + 1
}

fun Rucksack.findDuplicate() = first.toSet().intersect(second.toSet()).first()

fun main() {
    fun part1(input: List<String>): Int = input.map { line ->
        val half = line.length / 2
        line.slice(0 until half) to
                line.slice(half until line.length)
    }.map { rucksack: Rucksack ->
        rucksack.findDuplicate()
    }.sumOf {
        it.priority()
    }

    fun part2(input: List<String>): Int = input.chunked(3).flatMap { group ->
        group.map {
            it.toSet()
        }.reduce { acc, elfInGroup ->
            acc.intersect(elfInGroup)
        }
    }.sumOf {
        it.priority()
    }

    val testInput = readInput("day03/Day03_test")
    val input = readInput("day03/Day03")

    check(part1(testInput).also { println(it) } == 157)
    check(part1(input).also { println(it) } == 7553)

    check(part2(testInput).also { println(it) } == 70)
    check(part2(input).also { println(it) } == 2758)
}
