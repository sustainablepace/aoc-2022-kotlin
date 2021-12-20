package day13

import readInput
import kotlin.math.abs
import kotlin.system.measureTimeMillis

data class Dot(val x: Int, val y: Int)
class FoldInstruction private constructor(val isHorizontalFold: Boolean, val foldLine: Int) {
    companion object {
        fun create(input: String): FoldInstruction =
            input
                .replace("fold along ", "")
                .split("=")
                .let { (orientation, position) ->
                    FoldInstruction(
                        isHorizontalFold = orientation.first() == 'y',
                        foldLine = position.toInt()
                    )
                }
    }
}

typealias Paper = Set<Dot>

fun Paper.fold(vararg foldInstructions: FoldInstruction): Paper =
    foldInstructions.fold(this) { foldedPaper, instruction ->
        if (instruction.isHorizontalFold) {
            foldedPaper.map { (x, y) ->
                Dot(
                    x = x,
                    y = instruction.foldLine - abs(y - instruction.foldLine)
                )
            }.toSet()
        } else {
            foldedPaper.map { (x, y) ->
                Dot(
                    x = instruction.foldLine - abs(x - instruction.foldLine),
                    y = y
                )
            }.toSet()
        }
    }

fun Paper.print() = groupBy { it.y }
    .toSortedMap()
    .map { (_, dotsInLine) ->
        (0..dotsInLine.maxOf { it.x }).map { x ->
            if (dotsInLine.any { it.x == x }) '#' else ' '
        }.joinToString("").let {
            println(it)
        }
    }

fun main() {
    fun parse(input: List<String>): Pair<List<FoldInstruction>, Paper> =
        input
            .filter { it.isNotBlank() }
            .partition { it.startsWith("fold") }
            .let { (foldInstructions, dots) ->
                foldInstructions.map {
                    FoldInstruction.create(it)
                } to dots.map { dot ->
                    dot.split(",")
                        .map { it.toInt() }
                        .let { (x, y) -> Dot(x, y) }
                }.toSet()
            }

    fun part1(input: List<String>): Int =
        parse(input).let { (foldInstructions, paper) ->
            paper.fold(foldInstructions.first()).size
        }

    fun part2(input: List<String>): Unit =
        parse(input).let { (foldInstructions, paper) ->
            paper.fold(*foldInstructions.toTypedArray()).print()
        }

    val testInput = readInput("day13/Day13_test")
    val input = readInput("day13/Day13")

    println(part1(testInput))
    check(part1(testInput) == 17)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 602)

    part2(testInput)

    val msPart2 = measureTimeMillis {
        part2(input)
    }
    println("($msPart2 ms)")
}
