import kotlin.system.measureTimeMillis

data class P(val x: Int, val y: Int, val v: Int)

class Cave(val heightMap: Array<Array<Int>>) {
    private val width = heightMap.maxOf { it.size }
    private val height = heightMap.size

    fun neighbours(x: Int, y: Int): Set<P> =
        mutableSetOf<P>().let { neighbours ->
            if (x > 0) neighbours.add(P(x - 1, y, heightMap[y][x - 1]))
            if (y > 0) neighbours.add(P(x, y - 1, heightMap[y - 1][x]))
            if (x < width - 1) neighbours.add(P(x + 1, y, heightMap[y][x + 1]))
            if (y < height - 1) neighbours.add(P(x, y + 1, heightMap[y + 1][x]))
            neighbours
        }

    private operator fun P.compareTo(neighbours: Set<P>): Int {
        return if (neighbours.isNotEmpty() && neighbours.all { v < it.v }) -1 else 1
    }

    fun lowPoints(): Set<P> =
        mutableSetOf<P>().let { lowPoints ->
            heightMap.forEachIndexed { y, row ->
                row.forEachIndexed { x, value ->
                    val p = P(x, y, value)
                    if (p < neighbours(x, y)) {
                        lowPoints.add(p)
                    }
                }
            }
            lowPoints
        }

    fun basins(): Set<Basin> =
        lowPoints().map {
            Basin(this, it)
        }.let { basins ->
            var size: Int
            var newSize = 0
            do {
                size = newSize
                newSize = basins.sumOf { basin ->
                    basin.expand()
                    basin.size
                }
            } while (newSize > size)
            basins.toSet()
        }

    class Basin(private val cave: Cave, lowPoint: P) {
        val size: Int get() = expansion.sumOf { it.size }

        private val expansion: MutableList<Set<P>> = mutableListOf(setOf(lowPoint))

        fun expand() {
            expansion.last().flatMap { p ->
                cave.neighbours(p.x, p.y)
                    .filter {
                        it.v < 9 && it.v > p.v && !contains(it)
                    }
                    .toSet()
            }.toSet().let { expanded ->
                if (expanded.isNotEmpty()) {
                    expansion.add(expanded)
                }
            }
        }

        private fun contains(p: P) = expansion.any { it.contains(p) }
    }
}

fun parse(input: List<String>): Array<Array<Int>> {
    val map: Array<Array<Int>> = (input.indices).toList().map<Int, Array<Int>> { arrayOf() }.toTypedArray()
    input.forEachIndexed { y, p ->
        p.indices.map { x ->
            input[y][x].toString().toInt()
        }.let {
            map[y] = arrayOf(*it.toTypedArray())
        }
    }
    return map
}

fun main() {
    fun part1(input: List<String>): Int =
        Cave(parse(input)).lowPoints().sumOf { 1 + it.v }

    fun part2(input: List<String>): Int =
        Cave(parse(input)).basins().map { it.size }
            .sortedDescending()
            .take(3)
            .fold(1) { acc, v -> acc * v }

    val testInput = readInput("Day09_test")
    val input = readInput("Day09")

    println(part1(testInput))
    check(part1(testInput) == 15)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 478)

    println(part2(testInput))
    check(part2(testInput) == 1134)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 1327014)
}
