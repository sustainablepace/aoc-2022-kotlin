package day25

import readInput
import kotlin.system.measureTimeMillis

typealias Pos = Int

interface Turtle {
    val pos: Pos
}

@JvmInline
value class EastFacingTurtle(override val pos: Pos) : Turtle

@JvmInline
value class SouthFacingTurtle(override val pos: Pos) : Turtle

interface SeaFloor {

    val width: Int
    val height: Int
    val eastFacingTurtles: Set<EastFacingTurtle>
    val southFacingTurtles: Set<SouthFacingTurtle>

    fun print() {
        (0 until width * height).map { index ->
            if (eastFacingTurtles.find { it.pos == index } != null) {
                '>'
            } else if (southFacingTurtles.find { it.pos == index } != null) {
                'v'
            } else '.'
        }.chunked(width).forEach {
            println(it.joinToString(""))
        }
        println()
    }

    fun Pos.north() = (this - width).let {
        if (it < 0) {
            it + width * height
        } else it
    }

    fun Pos.east() = (this + 1).let {
        if (it % width == 0) {
            it - width
        } else it
    }

    fun Pos.south() = (this + width).let {
        if (it > width * height - 1) {
            it % width
        } else it
    }

    fun Pos.west() = (this - 1).let {
        if (it < 0) {
            it + width
        } else if (it % width == width - 1) {
            it + width
        } else it
    }

    fun EastFacingTurtle.move() = EastFacingTurtle(this.pos.east())
    fun SouthFacingTurtle.move() = SouthFacingTurtle(this.pos.south())
}

data class SeaFloorEast(
    override val width: Int,
    override val height: Int,
    override val eastFacingTurtles: Set<EastFacingTurtle> = emptySet(),
    override val southFacingTurtles: Set<SouthFacingTurtle> = emptySet()
) : SeaFloor {
    fun movementIsPossible(): Boolean {
        return this != moveEast().moveSouth()
    }

    fun blockedTurtles(): Set<EastFacingTurtle> {
        return southFacingTurtles.union(eastFacingTurtles).map { it.pos }.let { occupied ->
            eastFacingTurtles.filter { it.pos.east() in occupied }
        }.toSet()
    }

    fun moveEast(): SeaFloorSouth {
        val blockedTurtles = blockedTurtles()
        return SeaFloorSouth(
            width,
            height,
            blockedTurtles.union((eastFacingTurtles - blockedTurtles).map {
                it.move()
            }),
            southFacingTurtles
        )
    }
}

data class SeaFloorSouth(
    override val width: Int,
    override val height: Int,
    override val eastFacingTurtles: Set<EastFacingTurtle>,
    override val southFacingTurtles: Set<SouthFacingTurtle>
) : SeaFloor {

    fun movementIsPossible(): Boolean {
        print()
        eastFacingTurtles.forEach { eastFacingTurtles ->
            if (southFacingTurtles.find { it.pos == eastFacingTurtles.pos.north() } != null) {
                return true
            }
        }
        return false
    }

    fun blockedTurtles(): Set<SouthFacingTurtle> {
        return southFacingTurtles.union(eastFacingTurtles).map { it.pos }.let { occupied ->
            southFacingTurtles.filter { it.pos.south() in occupied }
        }.toSet()
    }

    fun moveSouth(): SeaFloorEast {
        val blockedTurtles = blockedTurtles()
        return SeaFloorEast(
            width,
            height,
            eastFacingTurtles,
            blockedTurtles.union((southFacingTurtles - blockedTurtles).map {
                it.move()
            }),
        )
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val turtles = input.joinToString("").mapIndexedNotNull { i, c ->
            when (c) {
                '>' -> EastFacingTurtle(i)
                'v' -> SouthFacingTurtle(i)
                else -> null
            }
        }

        val seaFloor = SeaFloorEast(
            input.first().length,
            input.size,
            turtles.filterIsInstance(EastFacingTurtle::class.java).toSet(),
            turtles.filterIsInstance(SouthFacingTurtle::class.java).toSet()
        )

        val s = generateSequence(seaFloor) {
            it.moveEast().moveSouth()
        }.takeWhile { it.movementIsPossible() }.toList()

        return s.size + 1
    }

    fun part2(input: List<String>): Int {
        return 1
    }

    SeaFloorEast(2, 2, setOf(EastFacingTurtle(0)), setOf(SouthFacingTurtle(1))).let {
        assert(it.blockedTurtles() == setOf(EastFacingTurtle(0)))
    }

    SeaFloorEast(2, 2, setOf(EastFacingTurtle(1)), setOf(SouthFacingTurtle(0))).let {
        assert(it.blockedTurtles() == setOf(EastFacingTurtle(1)))
    }

    SeaFloorEast(3, 3, setOf(EastFacingTurtle(8), EastFacingTurtle(7)), setOf(SouthFacingTurtle(6))).let {
        assert(it.blockedTurtles() == setOf(EastFacingTurtle(8), EastFacingTurtle(7)))
    }

    SeaFloorEast(3, 3, setOf(EastFacingTurtle(8), EastFacingTurtle(7)), setOf(SouthFacingTurtle(6))).let {
        assert(it.moveEast().eastFacingTurtles == setOf(EastFacingTurtle(8), EastFacingTurtle(7)))
    }

    SeaFloorEast(3, 3, setOf(EastFacingTurtle(8), EastFacingTurtle(7)), emptySet()).let {
        assert(it.moveEast().eastFacingTurtles == setOf(EastFacingTurtle(6), EastFacingTurtle(7)))
    }

    val testInput = readInput("day25/Day25_test")
    val input = readInput("day25/Day25")

    println(part1(testInput))
    check(part1(testInput) == 58)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 424)

    println(part2(testInput))
    check(part2(testInput) == 168)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 99053143)
}
