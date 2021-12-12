package past

import readInput
import kotlin.system.measureTimeMillis

fun main() {
    fun part1(input: List<String>): Int {
        var fish = input.first().split(",").map { it.toInt() }

        repeat(80) {
            fish = fish.flatMap { fish ->
                when (fish) {
                    0 -> listOf(6, 8)
                    else -> listOf(fish - 1)
                }
            }
        }

        return fish.size
    }

    fun part2(input: List<String>): Long  {
        val swarm = arrayOf<Long>(0, 0, 0, 0, 0, 0, 0, 0, 0)

        input.first().split(",").map { it.toInt() }.forEach { daysUntilRespawn ->
           swarm[daysUntilRespawn]++
        }

        repeat(256) {
            val respawned = swarm[0]
            (0..8).forEach { daysUntilRespawn ->
                swarm[daysUntilRespawn] = when (daysUntilRespawn) {
                    8 -> respawned
                    6 -> swarm[7] + respawned
                    else -> swarm[daysUntilRespawn+1]
                }
            }
        }

        return swarm.sum()
    }

    val testInput = readInput("Day06_test")
    val input = readInput("Day06")

    println(part1(testInput))
    check(part1(testInput) == 5934)
    val msPart1 = measureTimeMillis {
        val solutionPart1 = part1(input)
        println(solutionPart1)
        check(solutionPart1 == 346063)
    }
    println("$msPart1 ms")

    println(part2(testInput))
    check(part2(testInput) == 26984457539)
    val msPart2 = measureTimeMillis {
        val solutionPart2 = part2(input)
        println(solutionPart2)
        check(solutionPart2 == 1572358335990)
    }
    println("$msPart2 ms")
}
