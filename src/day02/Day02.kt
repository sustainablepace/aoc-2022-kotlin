package day02

import day02.Shape.*
import day02.Outcome.*
import readInput

enum class Shape(val score: Int) {
    Rock(1), Paper(2), Scissors(3)
}

infix fun Shape.vs(other: Shape) = RockPaperScissors(this, other)

data class RockPaperScissors(val mine: Shape, val elf: Shape) {
    fun play() = when {
        mine == elf -> Draw
        wins.any { it == this } -> Win
        else -> Loss
    }

    override fun toString() = "$mine vs $elf => ${play()}"

    companion object {
        val wins = setOf<RockPaperScissors>(
            Rock vs Scissors,
            Scissors vs Paper,
            Paper vs Rock
        )
    }
}


enum class Outcome(val score:Int) {
    Win(6), Draw(3), Loss(0)
}

fun Shape.reactionForOutcome(outcome: Outcome) = when(outcome) {
    Win -> RockPaperScissors.wins.first { it.elf == this }.mine
    Loss -> RockPaperScissors.wins.first { it.mine == this }.elf
    Draw -> this
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
        (me vs elf).run {
            mine.score + play().score
        }
    }

    fun part2(input: List<String>): Int = input.map {
        it.split(" ")
            .let { list -> rockPaperScissorsPart2(list[0]) to rockPaperScissorsResult(list[1]) }
    }.sumOf { (elf, result) ->
        result.score + elf.reactionForOutcome(result).score
    }

    val testInput = readInput("day02/Day02_test")
    val input = readInput("day02/Day02")

    check(part1(testInput).also { println(it) } == 15)
    check(part1(input).also { println(it) } == 15422)

    check(part2(testInput).also { println(it) } == 12)
    check(part2(input).also { println(it) } == 15442)
}