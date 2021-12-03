fun List<String>.mostCommonBitAt(index: Int, tie: Char? = null): Char {
    val count1 = count { binaryCode -> binaryCode[index] == '1' }
    val count0 = size - count1
    return if (count1 > count0) '1'
    else if (count0 > count1) '0'
    else tie ?: '1'
}

fun List<String>.leastCommonBitAt(index: Int, tie: Char? = null): Char {
    val count1 = count { binaryCode -> binaryCode[index] == '1' }
    val count0 = size - count1
    return if (count1 > count0) '0'
    else if (count0 > count1) '1'
    else tie ?: '1'
}

fun main() {
    fun part1(input: List<String>): Int {
        val length = input.maxOf {
            it.length
        }
        val gammaRate = (0 until length).map { index ->
            input.mostCommonBitAt(index)
        }.joinToString("").toInt(2)

        val epsilonRate = (0 until length).map { index ->
            input.leastCommonBitAt(index)
        }.joinToString("").toInt(2)
        return gammaRate * epsilonRate
    }

    fun List<String>.oxy(index: Int = 0): List<String> {
        val length = maxOf {
            it.length
        }
        if (index >= length) {
            return emptyList()
        }
        val bitChar = mostCommonBitAt(index, '1')
        val filteredList = filter { it[index] == bitChar }
        if (filteredList.size == 1) {
            return filteredList
        }
        return filteredList.oxy(index + 1)
    }

    fun List<String>.co2(index: Int = 0): List<String> {
        val length = maxOf {
            it.length
        }
        if (index >= length) {
            return emptyList()
        }
        val bitChar = leastCommonBitAt(index, '0')
        val filteredList = filter { it[index] == bitChar }
        if (filteredList.size == 1) {
            return filteredList
        }
        return filteredList.co2(index + 1)
    }


    fun part2(input: List<String>): Int {
        val oxy = input.oxy().first().toInt(2)
        println(oxy)
        val co2 = input.co2().first().toInt(2)
        println(co2)
        return oxy * co2
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    println(part1(testInput))
    check(part1(testInput) == 198)

    val input = readInput("Day03")
    println(part1(input))

    println(part2(testInput))
    check(part2(testInput) == 230)
    println(part2(input))
}
