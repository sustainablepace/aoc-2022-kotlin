package day13

import readInput
import kotlin.system.measureTimeMillis
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlin.math.max

val mapper = jacksonObjectMapper()


fun List<String>.parse() = mapNotNull {
    if (it.isNotBlank()) {
        mapper.readValue<List<*>>(it)
    } else null
}

fun List<*>.inRightOrder(other: List<*>): Boolean? {
    val numLeft = this.size
    val numRight = other.size
    val i = max(numLeft, numRight)

    repeat(i) { num: Int ->
        if (num >= numLeft) {
            return true
        }
        if (num >= numRight) {
            return false
        }

        val left = this[num]
        val right = other[num]
        if (left is Int && right is Int) {
            if (left < right) {
                return true
            }
            if (left > right) {
                return false
            }
        } else {
            val p = if (left is Int && right is List<*>) {
                listOf(left).inRightOrder(right)
            } else if (left is List<*> && right is Int) {
                left.inRightOrder(listOf(right))
            } else if (left is List<*> && right is List<*>) {
                left.inRightOrder(right)
            } else null
            if (p is Boolean) {
                return p
            }
        }
    }
    return null

}

fun main() {
    fun part1(input: List<String>): Int = input.parse().chunked(2).map {
        it[0] to it[1]
    }.map {
        it.first as List<*> to it.second as List<*>
    }.mapIndexed { index, (left, right) ->
        if (left.inRightOrder(right) == true) index + 1 else 0
    }.sum()

    val cx = Comparator<List<*>> { left, right ->
        when(left.inRightOrder(right)) {
            true -> 1
            false -> -1
            else -> 0
        }
    }

    fun part2(input: List<String>): Int = input.parse().let {
        val dividerPackets = listOf(
            listOf(listOf(2)),
            listOf(listOf(6))
        )
        val p = it.toMutableList()
        p.addAll(dividerPackets)
        val q = p.sortedWith(cx).toList().reversed()
        q.mapIndexed { index, packet ->
            if(packet in dividerPackets) {
                index+1
            } else null
        }.filterNotNull()
    }.let {
        it.fold(1) { acc, item -> acc * item }
    }

    val testInput = readInput("day13/Day13_test")
    val input = readInput("day13/Day13")

    println(part1(testInput))
    check(part1(testInput) == 13)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 5506)

    println(part2(testInput))
    check(part2(testInput) == 140)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 21756)
}
