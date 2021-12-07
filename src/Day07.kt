import java.util.Collections.max
import java.util.Collections.min
import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val crabs = input.first().split(",").map { it.toInt() }
        return min((min(crabs)..max(crabs)).map { pos ->
            crabs.sumOf { abs(it - pos) }
        })
    }

    fun part2(input: List<String>): Int {
        val crabs = input.first().split(",").map { it.toInt() }
        return min((min(crabs)..max(crabs)).map { pos ->
            crabs.sumOf { abs(it - pos).let { n -> n * (n + 1) / 2 } }
        })
    }

    val testInput = readInput("Day07_test")
    val input = readInput("Day07")

    println(part1(testInput))
    check(part1(testInput) == 37)
    println(part1(input))
    check(part1(input) == 352254)

    println(part2(testInput))
    check(part2(testInput) == 168)
    println(part2(input))
    check(part2(input) == 99053143)
}
