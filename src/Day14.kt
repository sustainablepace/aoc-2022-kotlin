import kotlin.system.measureTimeMillis

typealias Element = Char

data class ElementPair(val elementPair: Pair<Element, Element>, val increment: Long = 1L) {
    fun insert(element: Element): List<ElementPair> = listOf(
        ElementPair(elementPair.first to element, increment),
        ElementPair(element to elementPair.second, increment)
    )
}

fun reinforceHull(input: List<String>, repetitions: Int): Long {
    val pairInsertionRules = input.filter { it.contains('>') }.map {
        it.split(" -> ").let { (from, to) ->
            (from[0] to from[1]) to to.first()
        }
    }.toMap()
    val originalPolymerTemplate = input.filter { it.isNotBlank() && !it.contains('>') }.first()
    val counter = pairInsertionRules.keys.flatMap { listOf(it.first, it.second) }.toSet().map { element ->
        element to originalPolymerTemplate.count { it == element }.toLong()
    }.toMap().toMutableMap()

    fun insert(elementPair: ElementPair): List<ElementPair> =
        pairInsertionRules[elementPair.elementPair]?.let { n ->
            counter[n] = counter[n]!! + elementPair.increment
            elementPair.insert(n)
        } ?: emptyList()

    fun List<ElementPair>.aggregate(): List<ElementPair> =
        groupBy { it.elementPair }.map {
            ElementPair(it.value.first().elementPair, it.value.sumOf { it.increment })
        }

    var polymerTemplate = originalPolymerTemplate.windowed(2).map { ElementPair(it[0] to it[1]) }.toMutableList()

    repeat(repetitions) {
        polymerTemplate = polymerTemplate.flatMap {
            insert(it)
        }.aggregate().toMutableList()
    }
    return counter.values.sorted().let {
        it.last() - it.first()
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        return reinforceHull(input, 10)
    }

    fun part2(input: List<String>): Long {
        return reinforceHull(input, 40)
    }

    val testInput = readInput("Day14_test")
    val input = readInput("Day14")

    println(part1(testInput))
    check(part1(testInput) == 1588L)

    val solutionPart1: Long
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 3906L)

    val testSolutionPart2: Long
    val msTestPart2 = measureTimeMillis {
        testSolutionPart2 = part2(testInput)
    }
    println("$testSolutionPart2 ($msTestPart2 ms)")
    check(part2(testInput) == 2188189693529L)

    val solutionPart2: Long
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 4441317262452L)
}
