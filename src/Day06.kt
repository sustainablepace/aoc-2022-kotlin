fun main() {
    fun part1(input: List<String>): Int {
        var fish = input.first().split(",").map { it.toInt() }
        repeat(80) { day ->
            fish = fish.flatMap { fish ->
                when {
                    fish == 0 -> listOf(6, 8)
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
    println(part1(input))
    check(part1(input) == 346063)

    println(part2(testInput))
    check(part2(testInput) == 26984457539)
    println(part2(input))
    check(part2(input) == 1572358335990)
}
