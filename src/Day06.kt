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
        var fishes = arrayOf<Long>(0, 0, 0, 0, 0, 0, 0, 0, 0)

        val fish = input.first().split(",").map { it.toInt() }
        fish.forEach {
           fishes[it]++
        }
        repeat(256) { day ->
            val new8 = fishes[0]
            val new6 = fishes[0]
            fishes[0] = fishes[1]
            fishes[1] = fishes[2]
            fishes[2] = fishes[3]
            fishes[3] = fishes[4]
            fishes[4] = fishes[5]
            fishes[5] = fishes[6]
            fishes[6] = fishes[7] + new6
            fishes[7] = fishes[8]
            fishes[8] = new8
        }
        return fishes.sum()
    }

    val testInput = readInput("Day06_test")
    val input = readInput("Day06")

    println(part1(testInput))
    check(part1(testInput) == 5934)
    println(part1(input))

    println(part2(testInput))
    check(part2(testInput) == 26984457539)
    println(part2(input))
}
