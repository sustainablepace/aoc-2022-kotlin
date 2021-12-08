import kotlin.system.measureTimeMillis

val map = mapOf(
    "abcefg" to 0, // (6)
    "cf" to 1, // 2
    "acdeg" to 2, // (5)
    "acdfg" to 3, // (5)
    "bcdf" to 4, // 4
    "abdfg" to 5, // (5)
    "abdefg" to 6, // (6)
    "acf" to 7, // 3
    "abcdefg" to 8, //7
    "abcdfg" to 9 // (6)
)

fun main() {
    fun part1(input: List<String>): Int {
        val signalsAndOutputs = input.map {
            it.split(" | ").let { (signals, output) ->
                signals.split(" ") to output.split(" ")
            }
        }
        return signalsAndOutputs.sumOf { it.second.count { it.length == 2 || it.length == 3 || it.length == 4 || it.length == 7 } }

    }

    fun String.removeWire(wire: String?): String {
        return toSet().minus(wire!!.toSet()).joinToString("")
    }

    fun part2(input: List<String>): Int {
        val signalsAndOutputs = input.map {
            it.split(" | ").let { (signals, output) ->
                signals.split(" ") to output.split(" ")
            }
        }
        return signalsAndOutputs.map { (signals, outputs) ->
            val possibilities = mutableMapOf<Char, String>(
                'a' to "abcdefg",
                'b' to "abcdefg",
                'c' to "abcdefg",
                'd' to "abcdefg",
                'e' to "abcdefg",
                'f' to "abcdefg",
                'g' to "abcdefg"
            )

            // options for c / f
            val one = signals.find { it.length == 2 }!!
            possibilities['c'] = one.toList().joinToString("")
            possibilities['f'] = one.toList().joinToString("")

            // options for a
            val seven = signals.find { it.length == 3 }
            val aWire = seven!!.toSet() - one.toSet()
            possibilities['a'] = aWire.toList().joinToString("")

            // options for b / d
            val four = signals.find { it.length == 4 }
            val bdWires = four!!.toSet() - one.toSet()
            possibilities['b'] = bdWires.toList().joinToString("")
            possibilities['d'] = bdWires.toList().joinToString("")

            // options for e / g
            possibilities['e'] = possibilities['e']!!.removeWire(possibilities['a']).removeWire(possibilities['c']).removeWire(possibilities['b'])
            possibilities['g'] = possibilities['g']!!.removeWire(possibilities['a']).removeWire(possibilities['c']).removeWire(possibilities['b'])

            // identify e / g
            val zeroSixNine = signals.filter { it.length == 6 }
            assert(zeroSixNine.size == 3)
            val intersection069 = zeroSixNine[0].toSet().intersect(zeroSixNine[1].toSet()).intersect(zeroSixNine[2].toSet()).joinToString("")
            possibilities['g'] = intersection069.removeWire(possibilities['c']).removeWire(possibilities['b']).removeWire(possibilities['a'])
            possibilities['e'] = possibilities['e']!!.removeWire(possibilities['g'])

            // identify b / d
            val twoThreeFive = signals.filter { it.length == 5 }
            assert(twoThreeFive.size == 3)
            val intersection235 = twoThreeFive[0].toSet().intersect(twoThreeFive[1].toSet()).intersect(twoThreeFive[2].toSet()).joinToString("")
            possibilities['b'] = possibilities['b']!!.removeWire(intersection235)
            possibilities['d'] = possibilities['d']!!.removeWire(possibilities['b'])

            // identify c / f
            val f = possibilities.keys.filter { ch -> signals.count { it.contains(ch) } == 9 }.first()
            possibilities['f'] = f.toString()
            possibilities['c'] = possibilities['c']!!.removeWire(possibilities['f'])

            val reversedPossibilities = possibilities.entries.associateBy({ it.value.first() }) { it.key }

            val decoded = outputs.map { digit -> digit.map { reversedPossibilities[it] }.sortedBy { it }.joinToString("") }
            decoded.map { map[it] }.joinToString("").toInt()
        }.sum()

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
    check(solutionPart2 == 99053143)
}
