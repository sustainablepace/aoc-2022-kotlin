package day02

import day02.Shape.*
import day02.Result.*
import readInput

enum class Shape(val score: Int) {
    Rock(1), Paper(2), Scissors(3)
}

enum class Result(val score:Int) {
    Win(6), Draw(3), Loss(0)
}

fun Shape.beats() = when(this) {
    Rock -> Scissors
    Paper -> Rock
    Scissors -> Paper
}

fun Shape.losesTo() = when(this) {
    Rock -> Paper
    Paper -> Scissors
    Scissors -> Rock
}

fun Shape.reactionFor(result: Result) = when(result) {
    Win -> losesTo()
    Draw -> this
    Loss -> beats()
}

infix fun Shape.vs(other: Shape) = when {
    this == other -> Draw
    other == this.beats() -> Win
    else -> Loss
}

fun rockPaperScissorsPart1(input: String) = when (input) {
    "A", "X" -> Rock
    "B", "Y" -> Paper
    else -> Scissors
}

fun rockPaperScissorsPart2(input: String) = when (input) {
    "A" -> Rock
    "B" -> Paper
    else -> Scissors
}

fun rockPaperScissorsResult(input: String) = when(input) {
    "X" -> Loss
    "Y" -> Draw
    else -> Win
}

fun main() {
    fun part1(input: List<String>): Int = input.map {
        it.split(" ")
            .map { rockPaperScissorsPart1(it) }
            .let { list -> list[0] to list[1] }
    }.sumOf { (elf, me) ->
        me.score + (me vs elf).score
    }

    fun part2(input: List<String>): Int = input.map {
        it.split(" ")
            .let { list -> rockPaperScissorsPart2(list[0]) to rockPaperScissorsResult(list[1]) }
    }.sumOf { (elf, result) ->
        result.score + elf.reactionFor(result).score
    }

    val testInput = readInput("day02/Day02_test")
    val input = readInput("day02/Day02")

    check(part1(testInput) == 15)
    println(part1(input))

    check(part2(testInput) == 12)
    println(part2(input))
}