import java.util.Collections.max
import java.util.Collections.min
import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val horizontalPositions = input.first().split(",").map { it.toInt() }
        val min = min(horizontalPositions)
        val max = max(horizontalPositions)
        return (min..max).map { pos ->
            pos to horizontalPositions.sumOf { abs(it - pos) }
        }.minByOrNull { it.second }?.second!!
    }

    fun part2(input: List<String>): Int {
        val horizontalPositions = input.first().split(",").map { it.toInt() }
        val min = min(horizontalPositions)
        val max = max(horizontalPositions)
        return min((min..max).map { currentPosition ->
            horizontalPositions.sumOf { abs(it - currentPosition).let { n -> n * (n + 1) / 2 } }
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
