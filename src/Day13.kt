import kotlin.math.abs
import kotlin.system.measureTimeMillis

data class Dot(val x: Int, val y: Int)
data class FoldInstruction(val orientation: Char, val position: Int)

fun foldNext(foldInstruction: FoldInstruction, dots: Set<Dot>): Set<Dot> {
    return if (foldInstruction.orientation == 'y') {
        val newSet = dots.map { (x, y) ->
            Dot(x, foldInstruction.position - abs(y - foldInstruction.position))
        }.toSet()
        newSet
    } else {
        val newSet = dots.map { (x, y) ->
            Dot(foldInstruction.position - abs(x - foldInstruction.position), y)
        }.toSet()
        newSet
    }
}

fun foldManual(foldInstructions: List<FoldInstruction>, dots: Set<Dot>): Set<Dot> {
    return foldInstructions.fold(dots) { acc, instruction -> foldNext(instruction, acc) }
}

fun main() {
    fun parse(input: List<String>): Pair<List<FoldInstruction>, Set<Dot>> {
        return input.filter { it.isNotBlank() }.partition { row -> row.startsWith("fold") }
            .let { (foldInstructions, dots) ->
                foldInstructions.map {
                    it.replace("fold along ", "").split("=").let { (orientation, position) ->
                        FoldInstruction(orientation.first(), position.toInt())
                    }
                } to dots.map {
                    it.split(",").let { (x, y) ->
                        Dot(x.toInt(), y.toInt())
                    }
                }.toSet()
            }
    }

    fun part1(input: List<String>): Int {
        return parse(input).let { (foldInstructions, dots) ->
            foldNext(foldInstructions.first(), dots).size
        }
    }

    fun part2(input: List<String>): String {
        return parse(input).let { (foldInstructions, dots) ->
            foldManual(foldInstructions, dots)
                .groupBy { it.y }
                .toSortedMap()
                .map { (_, dotsInLine) ->
                    (0..dotsInLine.maxOf { it.x }).map { x ->
                        if (dotsInLine.any { it.x == x }) '#' else ' '
                    }.joinToString("").let {
                        println(it)
                    }
                }
            ""
        }
    }

    val testInput = readInput("Day13_test")
    val input = readInput("Day13")

    println(part1(testInput))
    check(part1(testInput) == 17)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 602)

    println(part2(testInput))
    check(part2(testInput) == "")

    val solutionPart2: String
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == "")
}
