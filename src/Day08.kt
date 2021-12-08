import NoteEntry.Companion.entries
import kotlin.system.measureTimeMillis

typealias Segment = Char
typealias SignalPattern = Set<Segment>
typealias EncodedDigit = Set<Segment>
typealias SevenSegmentDisplay = Map<Int, Set<Segment>>

val sevenSegmentDisplay: SevenSegmentDisplay = mapOf(
    0 to setOf('a', 'b', 'c', 'e', 'f', 'g'),
    1 to setOf('c', 'f'),
    2 to setOf('a', 'c', 'd', 'e', 'g'),
    3 to setOf('a', 'c', 'd', 'f', 'g'),
    4 to setOf('b', 'c', 'd', 'f'),
    5 to setOf('a', 'b', 'd', 'f', 'g'),
    6 to setOf('a', 'b', 'd', 'e', 'f', 'g'),
    7 to setOf('a', 'c', 'f'),
    8 to setOf('a', 'b', 'c', 'd', 'e', 'f', 'g'),
    9 to setOf('a', 'b', 'c', 'd', 'f', 'g')
)

fun SevenSegmentDisplay.findDigit(segments: Set<Segment>) = entries.first { it.value == segments }.key


class Solution(private val signalPatterns: List<SignalPattern>) {

    private val segmentsForOne = signalPatterns.first { it.size == sevenSegmentDisplay[1]!!.size }
    private val segmentsForFour = signalPatterns.first { it.size == sevenSegmentDisplay[4]!!.size }
    private val segmentsForSeven = signalPatterns.first { it.size == sevenSegmentDisplay[7]!!.size }

    private val allSegments = ('a'..'g').toSet()
    private val segmentB = setOf(allSegments.first { s -> signalPatterns.count { it.contains(s) } == 6 })
    private val segmentE = setOf(allSegments.first { s -> signalPatterns.count { it.contains(s) } == 4 })
    private val segmentF = setOf(allSegments.first { s -> signalPatterns.count { it.contains(s) } == 9 })

    private val segmentA = segmentsForSeven - segmentsForOne
    private val segmentC = segmentsForOne - segmentF
    private val segmentD = segmentsForFour - segmentC - segmentB - segmentF

    fun decode(encodedDigit: EncodedDigit) =
        encodedDigit.map { segment ->
            when (setOf(segment)) {
                segmentA -> 'a'
                segmentB -> 'b'
                segmentC -> 'c'
                segmentD -> 'd'
                segmentE -> 'e'
                segmentF -> 'f'
                else -> 'g'
            }
        }.toSet().let { segments ->
            sevenSegmentDisplay.findDigit(segments)
        }
}

data class NoteEntry(
    val signalPatterns: List<SignalPattern>,
    val outputValues: List<EncodedDigit>
) {
    companion object {
        fun entries(input: List<String>): List<NoteEntry> = input.map {
            it.split(" | ").let { (signals, output) ->
                NoteEntry(
                    signalPatterns = signals.split(" ").map(String::toSet),
                    outputValues = output.split(" ").map(String::toSet)
                )
            }
        }
    }
}

fun main() {
    fun part1(input: List<String>) =
        entries(input).sumOf { (_, outputValues) ->
            outputValues.count { it.size in setOf(2, 3, 4, 7) }
        }

    fun part2(input: List<String>) =
        entries(input).sumOf { (signalPatterns, outputValues) ->
            with(Solution(signalPatterns)) {
                outputValues.map {
                    decode(it)
                }.joinToString("").toInt()
            }
        }

    val testInput = readInput("Day08_test")
    val input = readInput("Day08")

    println(part1(testInput))
    check(part1(testInput) == 26)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 554)

    println(part2(testInput))
    check(part2(testInput) == 61229)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 990964)
}