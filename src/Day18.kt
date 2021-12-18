import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.IntNode
import java.lang.IllegalArgumentException
import java.lang.Math.ceil
import java.lang.Math.floor
import java.util.*
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

}

fun List<SnailfishNumber>.toSnailfishNumber() = SnailfishNumberPair(this.toMutableList(), this.first().depth)

fun List<SnailfishNumber>.sum() = reduce { a1, a2 ->
    (a1 + a2).reduce()
}

data class SnailfishNumberPair(
    var pair: MutableList<SnailfishNumber>,
    override val depth: Int,
    override val id: UUID = UUID.randomUUID()
) : SnailfishNumber() {
    init {
        assert(pair.size == 2)
    }

    override fun toList(): List<SnailfishNumber> {
        return mutableListOf<SnailfishNumberPair>().let { list ->
            when {
                pair[0] is SnailfishNumberAtomic && pair[1] is SnailfishNumberAtomic -> listOf(this)
                pair[0] is SnailfishNumberAtomic -> listOf(this) + pair[1].toList()
                pair[1] is SnailfishNumberAtomic -> pair[0].toList() + listOf(this)
                else -> pair[0].toList() + pair[1].toList()
            }
        }
    }

    override fun contains(explosion: SnailfishNumberPair): Boolean {
        return this == explosion || pair.any { it.contains(explosion) }
    }

    private fun replaceExplosion(explosion: SnailfishNumberPair): SnailfishNumberPair {
        return copy(
            pair = pair.map {
                when (it) {
                    is SnailfishNumberAtomic -> it
                    is SnailfishNumberPair -> {
                        if (it.pair[0] == explosion) {
                            it.copy(
                                pair = listOf(SnailfishNumberAtomic(0, it.depth + 1), it.pair[1]).toMutableList()
                            )
                        } else if (it.pair[1] == explosion) {
                            it.copy(
                                pair = listOf(it.pair[0], SnailfishNumberAtomic(0, it.depth + 1)).toMutableList()
                            )
                        } else it.replaceExplosion(explosion)
                    }
                }
            }.toMutableList()
        )
    }

    override fun split(): Boolean {
        val index = pair.indexOfFirst { it.containsNumberGreater9() }
        return if (index == -1
            || index == 0 && pair.get(0) is SnailfishNumberPair
            || index == 1 && pair.get(1) is SnailfishNumberPair
        ) {
            if (pair[0].split()) {
                true
            } else {
                pair[1].split()
            }
        } else {
            val a = floor((pair[index] as SnailfishNumberAtomic).value.toDouble() / 2).toInt()
            val b = ceil((pair[index] as SnailfishNumberAtomic).value.toDouble() / 2).toInt()
            assert(a + b == (pair[index] as SnailfishNumberAtomic).value)
            pair[index] = SnailfishNumberPair(
                pair = mutableListOf(
                    SnailfishNumberAtomic(
                        floor((pair[index] as SnailfishNumberAtomic).value.toDouble() / 2).toInt(),
                        pair[index].depth + 1
                    ),
                    SnailfishNumberAtomic(
                        ceil((pair[index] as SnailfishNumberAtomic).value.toDouble() / 2).toInt(),
                        pair[index].depth + 1
                    )
                ),
                depth = pair[index].depth
            )
            true
        }
    }

    override fun reduce(): SnailfishNumber {
        var start = this.toString()
        var old = this.toString()
        var number = this
        do {
            old = number.toString()
            number = number.explode() as SnailfishNumberPair
        } while (old != number.toString())
        //println("after explode: $number")

        number.split()
        //println("after split  : $number")


        if (number.toString() == start) {
            return number
        } else {
            return number.reduce()
        }

    }

    override fun containsNumberGreater9(): Boolean {
        return pair.any { it.containsNumberGreater9() }
    }

    override fun magnitude(): Int {
        return 3*pair[0].magnitude() + 2*pair[1].magnitude()
    }

    fun explode(): SnailfishNumber {
        val list = this.toList()
        list.firstOrNull { it.depth >= 4 && it is SnailfishNumberPair && it.pair.all { it is SnailfishNumberAtomic } }
            ?.let { explosion ->
                val explosionIndex = list.indexOf(explosion as SnailfishNumberPair)
                if (explosionIndex != -1) {
                    if (explosionIndex > 0) {
                        val left = list[explosionIndex - 1]
                        when (left) {
                            is SnailfishNumberAtomic -> left.value =
                                left.value + (explosion.pair[0] as SnailfishNumberAtomic).value
                            is SnailfishNumberPair -> if (left.pair[1].contains(explosion) && left.pair[0] is SnailfishNumberAtomic) {
                                (left.pair[0] as SnailfishNumberAtomic).value =
                                    (left.pair[0] as SnailfishNumberAtomic).value + (explosion.pair[0] as SnailfishNumberAtomic).value
                            } else {
                                (left.pair[1] as SnailfishNumberAtomic).value =
                                    (left.pair[1] as SnailfishNumberAtomic).value + (explosion.pair[0] as SnailfishNumberAtomic).value
                            }
                        }
                    }

                    if (explosionIndex + 1 < list.size) {
                        val right = list[explosionIndex + 1] as SnailfishNumberPair
                        if (right.pair[0].contains(explosion)) {
                            if (right.pair[1] is SnailfishNumberAtomic) {
                                (right.pair[1] as SnailfishNumberAtomic).value =
                                    (right.pair[1] as SnailfishNumberAtomic).value + (explosion.pair[1] as SnailfishNumberAtomic).value
                            } else {
                                (right.pair[0] as SnailfishNumberAtomic).value =
                                    (right.pair[0] as SnailfishNumberAtomic).value + (explosion.pair[1] as SnailfishNumberAtomic).value
                            }
                        } else {
                            if (right.pair[0] is SnailfishNumberAtomic) {
                                (right.pair[0] as SnailfishNumberAtomic).value =
                                    (right.pair[0] as SnailfishNumberAtomic).value + (explosion.pair[1] as SnailfishNumberAtomic).value
                            } else {
                                (right.pair[1] as SnailfishNumberAtomic).value =
                                    (right.pair[1] as SnailfishNumberAtomic).value + (explosion.pair[1] as SnailfishNumberAtomic).value
                            }

                        }
                    }
                    return replaceExplosion(explosion)
                }
            }
        return this
    }

    override fun increaseDepth(): SnailfishNumber {
        return copy(
            pair = pair.map {
                it.increaseDepth()
            }.toMutableList(),
            depth = depth + 1
        )
    }

    override fun toString() = "[${pair[0]},${pair[1]}]"
}


data class SnailfishNumberAtomic(
    var value: Int,
    override val depth: Int,
    override val id: UUID = UUID.randomUUID()
) :
    SnailfishNumber() {
    override fun increaseDepth(): SnailfishNumber {
        return copy(
            depth = depth + 1
        )
    }

    override fun toList(): List<SnailfishNumber> {
        return emptyList()
    }

    override fun contains(explosion: SnailfishNumberPair): Boolean {
        return false
    }

    override fun split(): Boolean {
        return false
    }

    override fun reduce(): SnailfishNumber {
        return this
    }

    override fun containsNumberGreater9(): Boolean {
        return value > 9
    }

    override fun magnitude(): Int {
        return value
    }


    override fun toString() = "$value"
}

val objectMapper = ObjectMapper()

fun IntNode.parse(depth: Int): SnailfishNumberAtomic {
    return SnailfishNumberAtomic(intValue(), depth)
}

fun ArrayNode.parse(depth: Int = 0): SnailfishNumberPair {
    return map {
        when (it) {
            is ArrayNode -> it.parse(depth + 1)
            is IntNode -> it.parse(depth + 1)
            else -> throw IllegalArgumentException("Not a valid Snailfish number")
        }
    }.let {
        SnailfishNumberPair(mutableListOf(it.get(0), it.get(1)), depth)
    }
}

fun List<ArrayNode>.parse(): SnailfishNumber {
    return map { it.parse() }.sum()
}

fun main() {
    val objectMapper: ObjectMapper = ObjectMapper()

    fun part1(number: SnailfishNumber): Int {
        return number.magnitude()
    }

    fun part2(numbers: List<ArrayNode>): Int {
        val parsedNumbers = numbers.map { it.parse() }
        return parsedNumbers.flatMap { number1 ->
            parsedNumbers.map { number2 ->
                number1 to number2
        } }.maxOf { (number1, number2) ->
            (number1 + number2).reduce().magnitude()
        }
    }

    val sumExample0 = (objectMapper.readTree("[[[[4,3],4],4],[7,[[8,4],9]]]") as ArrayNode).parse() +
            (objectMapper.readTree("[1,1]") as ArrayNode).parse()
    assert(sumExample0.reduce().toString() == "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")

    val sumExample1 = (objectMapper.readTree("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]") as ArrayNode).parse() +
            (objectMapper.readTree("[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]") as ArrayNode).parse()
    assert(sumExample1.reduce().toString() == "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]")

    val sumExample2 =
        (objectMapper.readTree("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]") as ArrayNode).parse() +
                (objectMapper.readTree("[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]") as ArrayNode).parse()
    assert(sumExample2.reduce().toString() == "[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]")

    val sumExample3 =
        (objectMapper.readTree("[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]") as ArrayNode).parse() +
                (objectMapper.readTree("[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]") as ArrayNode).parse()
    assert(sumExample3.reduce().toString() == "[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]")

    val sumExample4 =
        (objectMapper.readTree("[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]") as ArrayNode).parse() +
                (objectMapper.readTree("[7,[5,[[3,8],[1,4]]]]") as ArrayNode).parse()
    assert(sumExample4.reduce().toString() == "[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]")

    val sumExample5 =
        (objectMapper.readTree("[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]") as ArrayNode).parse() +
                (objectMapper.readTree("[[2,[2,2]],[8,[8,1]]]") as ArrayNode).parse()
    assert(sumExample5.reduce().toString() == "[[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]")

    val sumExample6 =
        (objectMapper.readTree("[[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]") as ArrayNode).parse() +
                (objectMapper.readTree("[2,9]") as ArrayNode).parse()
    assert(sumExample6.reduce().toString() == "[[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]")

    val sumExample7 =
        (objectMapper.readTree("[[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]") as ArrayNode).parse() +
                (objectMapper.readTree("[1,[[[9,3],9],[[9,0],[0,7]]]]") as ArrayNode).parse()
    assert(sumExample7.reduce().toString() == "[[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]")

    val sumExample8 =
        (objectMapper.readTree("[[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]") as ArrayNode).parse() +
                (objectMapper.readTree("[[[5,[7,4]],7],1]") as ArrayNode).parse()
    assert(sumExample8.reduce().toString() == "[[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]")

    val sumExample9 =
        (objectMapper.readTree("[[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]") as ArrayNode).parse() +
                (objectMapper.readTree("[[[[4,2],2],6],[8,7]]") as ArrayNode).parse()
    assert(sumExample9.reduce().toString() == "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")


    val magnitudeExample = (objectMapper.readTree("[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]") as ArrayNode).parse()
    assert(magnitudeExample.magnitude()==4140)

    val testInput1 = readInput("Day18_test_1").map {
        objectMapper.readValue(it, JsonNode::class.java) as ArrayNode
    }.parse()

    val reduceExample1 =
        (objectMapper.readTree("[[[[4,0],[5,4]],[[7,0],[[7,8],5]]],[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]]") as ArrayNode).parse()
    assert(reduceExample1.reduce().toString() == "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]")

    val explodeExample4 = (objectMapper.readTree("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]") as ArrayNode).parse()
    assert(explodeExample4.explode().toString() == "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")

    val explodeExample1 = (objectMapper.readTree("[[[[[9,8],1],2],3],4]") as ArrayNode).parse()
    assert(explodeExample1.explode().toString() == "[[[[0,9],2],3],4]")

    val explodeExample2 = (objectMapper.readTree("[7,[6,[5,[4,[3,2]]]]]") as ArrayNode).parse()
    assert(explodeExample2.explode().toString() == "[7,[6,[5,[7,0]]]]")

    val explodeExample3 = (objectMapper.readTree("[[6,[5,[4,[3,2]]]],1]") as ArrayNode).parse()
    assert(explodeExample3.explode().toString() == "[[6,[5,[7,0]]],3]")

    val explodeExample5 = (objectMapper.readTree("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]") as ArrayNode).parse()
    assert(explodeExample5.explode().toString() == "[[3,[2,[8,0]]],[9,[5,[7,0]]]]")

    val splitExample1 = (objectMapper.readTree("[[[[0,7],4],[15,[0,13]]],[1,1]]") as ArrayNode).parse()
    splitExample1.split()
    assert(splitExample1.toString() == "[[[[0,7],4],[[7,8],[0,13]]],[1,1]]")

    val splitExample2 = (objectMapper.readTree("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]") as ArrayNode).parse()
    splitExample2.split()
    assert(splitExample2.toString() == "[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]")


    val testInput2 = readInput("Day18_test_2").map {
        objectMapper.readValue(it, JsonNode::class.java) as ArrayNode
    }.parse()
    val testInput3 = readInput("Day18_test_3").map {
        objectMapper.readValue(it, JsonNode::class.java) as ArrayNode
    }.parse()
    val testInput4 = readInput("Day18_test_4").map {
        objectMapper.readValue(it, JsonNode::class.java) as ArrayNode
    }.parse()
    val testInput5 = readInput("Day18_test_5").map {
        objectMapper.readValue(it, JsonNode::class.java) as ArrayNode
    }.parse()

    assert(testInput1.toString() == "[[[[1,1],[2,2]],[3,3]],[4,4]]")
    assert(testInput2.toString() == "[[[[3,0],[5,3]],[4,4]],[5,5]]")
    assert(testInput3.toString() == "[[[[5,0],[7,4]],[5,5]],[6,6]]")
    assert(testInput4.toString() == "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")
    assert(testInput5.toString() == "[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]")

    val testInput = readInput("Day18_test").map {
        objectMapper.readValue(it, JsonNode::class.java) as ArrayNode
    }
    val input = readInput("Day18").map {
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
