package day09

import day09.Direction.*
import readInput
import kotlin.math.abs

sealed class RopeSegment
data class Knot(val x: Int = 0, val y: Int = 0): RopeSegment()
data class Rope(val head: Knot = Knot(), val tail: RopeSegment = Knot()) : RopeSegment() {
    fun moveHead(direction: Direction): Rope = (head move direction).let {
        Rope(
            head = it,
            tail = tail follow it
        )
    }

    fun tail(): Knot = when (tail) {
        is Rope -> tail.tail()
        is Knot -> tail
    }

    companion object {
        private infix fun Int.follow(head: Int) = when {
            head > this -> this + 1
            head < this -> this - 1
            else -> this
        }

        private infix fun RopeSegment.follow(head: Knot): RopeSegment = when (this) {
            is Knot -> when {
                this isTouching head -> this
                this isAlignedWith head -> Knot(
                        x = x follow head.x,
                        y = y follow head.y
                )
                else -> Knot(
                        x = when {
                            abs(head.x - x) == 2 -> x follow head.x
                            else -> head.x
                        },
                        y = when {
                            abs(head.y - y) == 2 -> y follow head.y
                            else -> head.y
                        }
                )
            }
            is Rope -> ((this.head follow head) as Knot).let { movedHead ->
                Rope(
                    head = movedHead,
                    tail = this.tail follow movedHead
                )
            }
        }
    }
}

fun rope(knots: Int): Rope = when (knots) {
    2 -> Rope()
    else -> Rope(tail = rope(knots - 1))
}

infix fun Knot.move(direction: Direction): Knot = when (direction) {
    U -> Knot(x, y - 1)
    D -> Knot(x, y + 1)
    L -> Knot(x - 1, y)
    R -> Knot(x + 1, y)
}

infix fun RopeSegment.isTouching(head: Knot): Boolean = when (this) {
    is Knot -> abs(x - head.x) <= 1 && abs(y - head.y) <= 1
    is Rope -> this.head isTouching head
}
infix fun RopeSegment.isAlignedWith(knot: Knot): Boolean = when (this) {
    is Knot -> setOf(abs(x - knot.x), abs(y - knot.y)).containsAll(setOf(0, 2))
    is Rope -> this.head isAlignedWith head
}

data class Instruction(val direction: Direction, val steps: Int)
enum class Direction { U, D, L, R }

fun List<Instruction>.followTailOf(r: Rope): Set<Knot> {
    var rope = r
    val tailLocations = mutableSetOf(rope.tail())
    forEach { instruction: Instruction ->
        repeat(instruction.steps) {
            rope = rope.moveHead(instruction.direction)
            tailLocations.add(rope.tail())
        }
    }
    return tailLocations.toSet()
}

fun String.instruction(): Instruction = split(" ").let { (direction, steps) ->
    Instruction(direction = enumValueOf(direction), steps = steps.toInt())
}

fun List<String>.instructions() = map { it.instruction() }

fun main() {
    fun part1(input: List<String>): Int = input.instructions().followTailOf(rope(2)).size
    fun part2(input: List<String>): Int = input.instructions().followTailOf(rope(10)).size

    val testInput = readInput("day09/Day09_test")
    val largerTestInput = readInput("day09/Day09_test_larger")
    val input = readInput("day09/Day09")

    check(part1(testInput).also { println(it) } == 13)
    check(part1(input).also { println(it) } == 5619)

    check(part2(testInput).also { println(it) } == 1)
    check(part2(largerTestInput).also { println(it) } == 36)
    check(part2(input).also { println(it) } == 2376)
}
