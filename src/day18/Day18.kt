package day18

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.IntNode
import readInput
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.system.measureTimeMillis

sealed class SnailfishNumber {
    abstract val id: UUID
    abstract val depth: Int
    operator fun plus(other: SnailfishNumber): SnailfishNumber =
        SnailfishNumberPair(mutableListOf(this.increaseDepth(), other.increaseDepth()), 0)

    abstract fun increaseDepth(): SnailfishNumber
    abstract fun toList(): List<SnailfishNumber>
    abstract fun contains(explosion: SnailfishNumberPair): Boolean
    abstract fun split(): Boolean
    abstract fun reduce(): SnailfishNumber
    abstract fun containsNumberGreater9(): Boolean
    abstract fun magnitude(): Int
    abstract fun replaceLeft(explosion: SnailfishNumberPair)
}

data class SnailfishNumberAtomic(
    var value: Int,
    override val depth: Int,
    override val id: UUID = UUID.randomUUID()
) : SnailfishNumber() {
    override fun increaseDepth(): SnailfishNumber = copy(depth = depth + 1)
    override fun toList(): List<SnailfishNumber> = emptyList()
    override fun contains(explosion: SnailfishNumberPair): Boolean = false
    override fun split(): Boolean = false
    override fun reduce(): SnailfishNumber = this
    override fun containsNumberGreater9(): Boolean = value > 9
    override fun magnitude(): Int = value
    override fun replaceLeft(explosion: SnailfishNumberPair) {
        val pair0Value = (explosion.pair[0] as SnailfishNumberAtomic).value
        value += pair0Value
    }

    override fun toString() = "$value"

}

data class SnailfishNumberPair(
    var pair: MutableList<SnailfishNumber>,
    override val depth: Int,
    override val id: UUID = UUID.randomUUID()
) : SnailfishNumber() {

    override fun split() =
        pair.indexOfFirst {
            it.containsNumberGreater9()
        }.let { index ->
            if (index == -1) {
                false
            } else if (index >= 0 && pair[index] is SnailfishNumberPair) {
                pair[0].split() || pair[1].split()
            } else {
                pair[index] = SnailfishNumberPair(
                    pair = mutableListOf(
                        SnailfishNumberAtomic(
                            value = floor((pair[index] as SnailfishNumberAtomic).value.toDouble() / 2).toInt(),
                            depth = pair[index].depth + 1
                        ),
                        SnailfishNumberAtomic(
                            value = ceil((pair[index] as SnailfishNumberAtomic).value.toDouble() / 2).toInt(),
                            depth = pair[index].depth + 1
                        )
                    ),
                    depth = pair[index].depth
                )
                true
            }
        }

    override fun contains(explosion: SnailfishNumberPair) = this == explosion || pair.any { it.contains(explosion) }

    override fun containsNumberGreater9() = pair.any { it.containsNumberGreater9() }

    override fun toString() = "[${pair[0]},${pair[1]}]"

    override fun magnitude() = 3 * pair[0].magnitude() + 2 * pair[1].magnitude()

    override fun increaseDepth() = copy(
        pair = pair.map { it.increaseDepth() }.toMutableList(),
        depth = depth + 1
    )

    override fun toList(): List<SnailfishNumber> =
        when {
            pair[0] is SnailfishNumberAtomic && pair[1] is SnailfishNumberAtomic -> listOf(this)
            pair[0] is SnailfishNumberAtomic -> listOf(this) + pair[1].toList()
            pair[1] is SnailfishNumberAtomic -> pair[0].toList() + listOf(this)
            else -> pair[0].toList() + pair[1].toList()
        }

    override fun reduce(): SnailfishNumber {
        val start = this.toString()
        var old: String
        var number = this
        do {
            old = number.toString()
            number = number.explode()
        } while (old != number.toString())

        number.split()

        return if (number.toString() == start) {
            number
        } else {
            number.reduce()
        }
    }

    override fun replaceLeft(explosion: SnailfishNumberPair) {
        val pair0Value = (explosion.pair[0] as SnailfishNumberAtomic).value
        if (pair[1].contains(explosion) && pair[0] is SnailfishNumberAtomic) {
            (pair[0] as SnailfishNumberAtomic).value += pair0Value
        } else {
            (pair[1] as SnailfishNumberAtomic).value += pair0Value
        }
    }

    private fun replaceRight(explosion: SnailfishNumberPair) {
        val pair1value = (explosion.pair[1] as SnailfishNumberAtomic).value
        if (pair[0].contains(explosion)) {
            if (pair[1] is SnailfishNumberAtomic) {
                (pair[1] as SnailfishNumberAtomic).value += pair1value
            } else {
                (pair[0] as SnailfishNumberAtomic).value += pair1value
            }
        } else {
            if (pair[0] is SnailfishNumberAtomic) {
                (pair[0] as SnailfishNumberAtomic).value += pair1value
            } else {
                (pair[1] as SnailfishNumberAtomic).value += pair1value
            }
        }
    }

    private fun explode() =
        toList().let { list ->
            list.firstOrNull {
                it.depth >= 4 && it is SnailfishNumberPair && it.pair.all { number -> number is SnailfishNumberAtomic }
            }?.let { explosion ->
                explosion as SnailfishNumberPair
            }?.let { explosion ->
                list.indexOf(explosion).let { explosionIndex ->
                    list.getOrNull(explosionIndex - 1)?.replaceLeft(explosion)
                    list.getOrNull(explosionIndex + 1)?.let { it as SnailfishNumberPair }?.replaceRight(explosion)
                }
                replaceExplosion(explosion)
            } ?: this
        }

    private fun replaceExplosion(explosion: SnailfishNumberPair): SnailfishNumberPair =
        copy(
            pair = pair.map {
                when (it) {
                    is SnailfishNumberAtomic -> it
                    is SnailfishNumberPair -> when (explosion) {
                        it.pair[0] -> it.copy(
                            pair = mutableListOf(SnailfishNumberAtomic(0, it.depth + 1), it.pair[1])
                        )
                        it.pair[1] -> it.copy(
                            pair = mutableListOf(it.pair[0], SnailfishNumberAtomic(0, it.depth + 1))
                        )
                        else -> it.replaceExplosion(explosion)
                    }
                }
            }.toMutableList()
        )
}

val objectMapper = ObjectMapper()

fun ArrayNode.parse(depth: Int = 0): SnailfishNumberPair =
    map {
        when (it) {
            is ArrayNode -> it.parse(depth + 1)
            is IntNode -> SnailfishNumberAtomic(it.intValue(), depth + 1)
            else -> throw IllegalArgumentException("Not a valid Snailfish number")
        }
    }.let {
        SnailfishNumberPair(mutableListOf(it[0], it[1]), depth)
    }

fun List<ArrayNode>.parse(): SnailfishNumber =
    map { it.parse() }.reduce { a1, a2 -> (a1 + a2).reduce() as SnailfishNumberPair }

fun main() {
    fun part1(number: SnailfishNumber): Int = number.magnitude()

    fun part2(numbers: List<ArrayNode>): Int =
        numbers.map { it.parse() }.let { parsedNumbers ->
            parsedNumbers.flatMap { number1 ->
                parsedNumbers.map { number2 ->
                    number1 to number2
                }
            }.maxOf { (number1, number2) ->
                (number1 + number2).reduce().magnitude()
            }
        }

    val testInput1 = readInput("day18/Day18_test_1").map {
        objectMapper.readValue(it, JsonNode::class.java) as ArrayNode
    }.parse()
    val testInput2 = readInput("day18/Day18_test_2").map {
        objectMapper.readValue(it, JsonNode::class.java) as ArrayNode
    }.parse()
    val testInput3 = readInput("day18/Day18_test_3").map {
        objectMapper.readValue(it, JsonNode::class.java) as ArrayNode
    }.parse()
    val testInput4 = readInput("day18/Day18_test_4").map {
        objectMapper.readValue(it, JsonNode::class.java) as ArrayNode
    }.parse()
    val testInput5 = readInput("day18/Day18_test_5").map {
        objectMapper.readValue(it, JsonNode::class.java) as ArrayNode
    }.parse()

    assert(testInput1.toString() == "[[[[1,1],[2,2]],[3,3]],[4,4]]")
    assert(testInput2.toString() == "[[[[3,0],[5,3]],[4,4]],[5,5]]")
    assert(testInput3.toString() == "[[[[5,0],[7,4]],[5,5]],[6,6]]")
    assert(testInput4.toString() == "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")
    assert(testInput5.toString() == "[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]")

    val testInput = readInput("day18/Day18_test").map {
        objectMapper.readValue(it, JsonNode::class.java) as ArrayNode
    }
    val input = readInput("day18/Day18").map {
        objectMapper.readValue(it, JsonNode::class.java) as ArrayNode
    }

    println(part1(testInput.parse()))
    check(part1(testInput.parse()) == 4140)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input.parse())
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 3574)

    println(part2(testInput))
    check(part2(testInput) == 3993)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 4763)
}