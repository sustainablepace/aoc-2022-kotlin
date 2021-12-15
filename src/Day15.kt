import kotlin.math.abs
import kotlin.system.measureTimeMillis

data class P(val x: Int, val y: Int) {
    fun dist(risk: Risk) = abs(risk.x - x) + abs(risk.y - y)
    fun toRisk(cave: TheCave) = cave.get(x, y)
}

data class Risk(val x: Int, val y: Int, val v: Int = 0, var shortestRoute: Int = Int.MAX_VALUE) {
    fun dist(risk: Risk) = abs(risk.x - x) + abs(risk.y - y)
    fun toPoint(): P = P(x, y)
}

class TheCave(val heightMap: Array<Array<Risk>>) {
    val width = heightMap.maxOf { it.size }
    val height = heightMap.size
    val start = heightMap[0][0]
    val exit = heightMap[width - 1][height - 1]

    fun get(x: Int, y: Int) = heightMap[y][x]
    fun neighbours(p: P) = neighbours(Risk(p.x, p.y))
    fun neighbours(risk: Risk): Set<Risk> =
        mutableSetOf<Risk>().let { neighbours ->
            if (risk.x > 0) neighbours.add(heightMap[risk.y][risk.x - 1])
            if (risk.y > 0) neighbours.add(heightMap[risk.y - 1][risk.x])
            if (risk.x < width - 1) neighbours.add(heightMap[risk.y][risk.x + 1])
            if (risk.y < height - 1) neighbours.add(heightMap[risk.y + 1][risk.x])
            neighbours
        }

    private operator fun Risk.compareTo(neighbours: Set<Risk>): Int {
        return if (neighbours.isNotEmpty() && neighbours.all { v < it.v }) -1 else 1
    }
}

fun parse(input: List<String>): Array<Array<Risk>> {
    val map: Array<Array<Risk>> = (input.indices).toList().map<Int, Array<Risk>> { arrayOf() }.toTypedArray()
    input.forEachIndexed { y, p ->
        p.indices.map { x ->
            Risk(x, y, input[y][x].toString().toInt())
        }.let {
            map[y] = arrayOf(*it.toTypedArray())
        }
    }
    return map
}

fun parse2(input: List<String>): Array<Array<Risk>> {
    var map: Array<Array<Risk>> = (input.indices).toList().map<Int, Array<Risk>> { arrayOf() }.toTypedArray()
    input.forEachIndexed { y, p ->
        p.indices.map { x ->
            Risk(x, y, input[y][x].toString().toInt())
        }.let {
            map[y] = arrayOf(*it.toTypedArray())
        }
    }
    val dim = map.size
    (1..4).forEach { c ->
        map.forEachIndexed { y, row ->
            val list = row.toMutableList() + row.take(dim).map {
                it.copy(
                    x = it.x + dim * c,
                    v = if (it.v + c > 9) (it.v + c) % 9 else it.v + c
                )
            }
            map[y] = arrayOf(*list.toTypedArray())
        }
    }
    (1..4).forEach { c ->
        map.take(dim).forEachIndexed { y, row ->
            val list = row.map {
                it.copy(
                    y = it.y + dim * c,
                    v = if (it.v + c > 9) (it.v + c) % 9 else it.v + c
                )
            }
            map += arrayOf(*list.toTypedArray())
        }
    }
    return map
}

fun <T> append(arr: Array<T>, element: T): Array<T?> {
    val array = arr.copyOf(arr.size + 1)
    array[arr.size] = element
    return array
}

fun explore(
    cave: TheCave,
    path: List<P>,
    toBeat: Int,
    points: List<Risk>,
    distanceToExit: Int = 0,
    distSoFar: Int = 0,
    depth: Int = 0
): Int =
    if (depth > 2) Int.MAX_VALUE else // found this depth threshold through experimentation
        cave.neighbours(path.last())
            .filterNot { path.contains(it.toPoint()) }
            .mapNotNull { neighbour ->
                if (neighbour.shortestRoute < Int.MAX_VALUE && neighbour.shortestRoute + distSoFar <= toBeat) {
                    neighbour.shortestRoute + distSoFar
                } else {
                    val minDist = points.minOf { it.shortestRoute + it.dist(neighbour) }
                    if (distSoFar + (neighbour.dist(cave.exit) - distanceToExit) + minDist > toBeat) {
                        null
                    } else {
                        explore(
                            cave,
                            path.toMutableList() + neighbour.toPoint(),
                            toBeat,
                            points,
                            distanceToExit,
                            distSoFar + neighbour.v,
                            depth + 1
                        )
                    }
                }
            }.minOrNull() ?: Int.MAX_VALUE


fun findShortestRouteFor(currentPoint: Risk, points: List<Risk>, cave: TheCave): List<P> =
    if (currentPoint.shortestRoute < Int.MAX_VALUE) {
        cave.neighbours(currentPoint).map { it.toPoint() }
    } else {
        val toBeat = cave.neighbours(currentPoint).minOf { it.shortestRoute }
        currentPoint.shortestRoute =
            currentPoint.v + cave.neighbours(currentPoint).filter { it.shortestRoute == Int.MAX_VALUE }.let {
                if (it.isEmpty()) {
                    toBeat
                } else {
                    it.minOf {
                        // check if an alternate route beats it
                        val alternative = explore(
                            cave,
                            mutableListOf(currentPoint.toPoint()),
                            toBeat,
                            points,
                            currentPoint.dist(cave.exit)
                        )
                        if (alternative < toBeat) {
                            alternative
                        } else {
                            toBeat
                        }
                    }
                }
            }
        cave.neighbours(currentPoint).filter { it.shortestRoute == Int.MAX_VALUE }.map { it.toPoint() }
    }

fun main() {
    fun part1(input: List<String>): Int {
        val cave = TheCave(parse(input))
        cave.exit.shortestRoute = cave.exit.v

        var points = setOf(cave.exit.toPoint())
        var oldPoints: Set<P> = emptySet()

        (2 * (cave.height - 1) downTo 0).forEach {
            val newPoints = points.flatMap {
                findShortestRouteFor(
                    it.toRisk(cave),
                    oldPoints.map { it.toRisk(cave) },
                    cave
                )
            }.toSet()
            oldPoints = points
            points = newPoints
        }
        return cave.start.shortestRoute - cave.start.v
    }

    fun part2(input: List<String>): Int {
        val cave = TheCave(parse2(input))
        cave.exit.shortestRoute = cave.exit.v

        var points = setOf(cave.exit.toPoint())
        var oldPoints: Set<P> = emptySet()

        (2 * (cave.height - 1) downTo 0).forEach {
            val newPoints = points.flatMap {
                findShortestRouteFor(
                    it.toRisk(cave),
                    oldPoints.map { it.toRisk(cave) },
                    cave
                )
            }.toSet()
            oldPoints = points
            points = newPoints
        }
        return cave.start.shortestRoute - cave.start.v
    }

    val testInput = readInput("Day15_test")
    val input = readInput("Day15")

    println(part1(testInput))
    check(part1(testInput) == 40)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 540)

    println(part2(testInput))
    check(part2(testInput) == 315)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 2879)

}
