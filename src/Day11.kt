import kotlin.system.measureTimeMillis

typealias Grid = Array<Array<Int>>

data class Flash(val x: Int, val y: Int)

fun gridOf(input: List<String>): Grid =
    (input.indices).toList().map<Int, Array<Int>> { arrayOf() }.toTypedArray().let { grid ->
        input.forEachIndexed { y, p ->
            p.indices.map { x -> input[y][x].toString().toInt() }.let {
                grid[y] = arrayOf(*it.toTypedArray())
            }
        }
        grid
    }

fun neighbours(x: Int, y: Int): Set<Pair<Int, Int>> =
    (-1..1).flatMap { dx ->
        (-1..1).mapNotNull { dy ->
            if(dx != 0 || dy != 0) x + dx to y + dy else null
        }
    }.filter { (x, y) -> x in 0..9 && y in 0..9 }.toSet()

fun Grid.increaseAndRecordFlashes(): Set<Flash> =
    mutableSetOf<Flash>().let { flashes ->
        forEachIndexed { y, row ->
            row.forEachIndexed { x, value ->
                if (value == 9) flashes.add(Flash(x, y))
                this[y][x] = value + 1
            }
        }
        flashes.toSet()
    }

fun Grid.processFlashes(recordedFlashes: Set<Flash>): Int {
    var numFlashesInStep = 0
    var flashes = recordedFlashes
    while (flashes.isNotEmpty()) {
        numFlashesInStep += flashes.size
        val newFlashes = mutableSetOf<Flash>()
        flashes.forEach { (x, y) ->
            neighbours(x, y).forEach { (_x, _y) ->
                if (this[_y][_x] == 9) newFlashes.add(Flash(_x, _y))
                this[_y][_x] = (this[_y][_x] + 1)
            }
        }
        flashes = newFlashes
    }

    // cap values at 9, reset flashes to 0
    this.forEachIndexed { y, row ->
        row.forEachIndexed { x, value ->
            this[y][x] = if (value <= 9) value else 0
        }
    }
    return numFlashesInStep
}

fun main() {
    fun part1(input: List<String>): Int {
        val grid: Grid = gridOf(input)
        return (0..99).sumOf {
            grid.increaseAndRecordFlashes().let {
                grid.processFlashes(it)
            }
        }
    }

    fun part2(input: List<String>): Int {
        val grid: Grid = gridOf(input)
        var flashesInStep: Int
        var steps = 0
        do {
            steps++
            grid.increaseAndRecordFlashes().also {
                flashesInStep = grid.processFlashes(it)
            }
        } while (flashesInStep != 100)
        return steps
    }

    val testInput = readInput("Day11_test")
    val input = readInput("Day11")

    val testSolutionPart1 = part1(testInput)
    println(testSolutionPart1)
    check(testSolutionPart1 == 1656)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 1725)

    println(part2(testInput))
    check(part2(testInput) == 195)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 308)
}
