import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

fun main() {
    fun part1(input: List<String>): Int {
        val (trenchXRange, trenchYRange) = input.first().replace("target area: ", "").split(", ").map {
            it.substring(2).split("..").map {
                it.toInt()
            }.let { (x, y) -> min(x, y)..max(x, y) }
        }
        val xRange = 0..100
        val yRange = -100..100
        return xRange.maxOf { x ->
            yRange.maxOf { y ->
                var currentX = 0
                var currentY = 0
                var xVelocity = x
                var yVelocity = y
                var step = 0
                var maxY = Int.MIN_VALUE
                var inTrench = false
                while (currentY > trenchYRange.first() && !inTrench) {
                    currentX += xVelocity
                    currentY += yVelocity
                    step++
                    if (currentY > maxY) {
                        maxY = currentY
                    }
                    if (currentX in trenchXRange && currentY in trenchYRange) {
                        inTrench = true
                    }
                    if (xVelocity > 0) {
                        xVelocity--
                    } else if (xVelocity < 0) {
                        xVelocity++
                    }
                    yVelocity--
                }
                if (!inTrench) {
                    maxY = Int.MIN_VALUE
                }
                maxY
            }
        }
    }

    fun part2(input: List<String>): Int {
        val (trenchXRange, trenchYRange) = input.first().replace("target area: ", "").split(", ").map {
            it.substring(2).split("..").map {
                it.toInt()
            }.let { (x, y) -> min(x, y)..max(x, y) }
        }
        val xRange = -250..250
        val yRange = -100..100
        val pairs = xRange.flatMap { x ->
            yRange.mapNotNull { y ->
                var currentX = 0
                var currentY = 0
                var xVelocity = x
                var yVelocity = y
                var step = 0
                var inTrench = false
                while (currentY > trenchYRange.first() && !inTrench) {
                    currentX += xVelocity
                    currentY += yVelocity
                    step++

                    if (currentX in trenchXRange && currentY in trenchYRange) {
                        inTrench = true
                    }
                    if (xVelocity > 0) {
                        xVelocity--
                    } else if (xVelocity < 0) {
                        xVelocity++
                    }
                    yVelocity--
                }
                if (inTrench) {

                    x to y
                } else null
            }
        }.filterNotNull()
        return pairs.size
    }

    val testInput = readInput("day17/Day17_test")
    val input = readInput("day17/Day17")

    println(part1(testInput))
    check(part1(testInput) == 45)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 4278)

    println(part2(testInput))
    check(part2(testInput) == 112)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 1994)
}
