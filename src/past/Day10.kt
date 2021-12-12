package past

import past.Syntax.brackets
import past.Syntax.completeChunks
import past.Syntax.completionScore
import past.Syntax.openingBrackets
import past.Syntax.syntaxErrorScore
import readInput
import kotlin.system.measureTimeMillis

typealias Bracket = Char

object Syntax {
    val brackets = setOf(
        '(' to ')',
        '[' to ']',
        '{' to '}',
        '<' to '>'
    )
    val completeChunks: Set<String> = brackets.map { it.first.toString() + it.second.toString() }.toSet()
    val openingBrackets: Set<String> = brackets.map { it.first.toString() }.toSet()

    fun syntaxErrorScore(closingBracket: Char) =
        when (closingBracket) {
            ')' -> 3
            ']' -> 57
            '}' -> 1197
            '>' -> 25137
            else -> 0
        }

    fun completionScore(closingBracket: Char) =
        when (closingBracket) {
            ')' -> 1L
            ']' -> 2L
            '}' -> 3L
            '>' -> 4L
            else -> 0L
        }
}

typealias Line = String

fun Line.withoutOpeningBrackets(): Line =
    openingBrackets.fold(this) { acc, b -> acc.replace(b, "") }

fun Line.withoutCompleteChunks(): Line {
    var line = this
    while (completeChunks.any { line.contains(it) }) {
        line = completeChunks.fold(line) { acc, chunk -> acc.replace(chunk, "") }
    }
    return line
}

fun Line.syntaxErrorScore() =
    withoutCompleteChunks()
        .withoutOpeningBrackets()
        .firstOrNull()
        ?.let { syntaxErrorScore(it) } ?: 0

fun Line.completionScore() =
    withoutCompleteChunks()
        .reversed()
        .let {
            brackets.fold(it) { acc, bracket -> acc.replace(bracket.first, bracket.second) }
        }
        .toList()
        .fold(0L) { acc, ch -> 5L * acc + completionScore(ch) }

class NavigationSubsystem(private val input: List<String>) {

    fun totalSyntaxErrorScore() = input.sumOf { it.syntaxErrorScore() }

    fun middleCompletionScore(): Long =
        input
            .filter { it.syntaxErrorScore() == 0 }
            .map { it.completionScore() }
            .sortedDescending()
            .let {
                it[it.size - it.size / 2 - 1]
            }
}

fun main() {
    fun part1(input: List<String>): Int =
        NavigationSubsystem(input).totalSyntaxErrorScore()

    fun part2(input: List<String>): Long =
        NavigationSubsystem(input).middleCompletionScore()

    val testInput = readInput("Day10_test")
    val input = readInput("Day10")

    println(part1(testInput))
    check(part1(testInput) == 26397)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 315693)

    println(part2(testInput))
    check(part2(testInput) == 288957L)

    val solutionPart2: Long
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 1870887234L)
}
