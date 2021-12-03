typealias BinaryNumber = String
typealias DiagnosticReport = List<BinaryNumber>

fun DiagnosticReport.countOnesAtPosition(position: Int) = count { binaryNumber -> binaryNumber[position] == '1' }
fun BinaryNumber.toDecimal() = toInt(2)

fun DiagnosticReport.mostCommonBitAt(position: Int): Char =
    countOnesAtPosition(position).let { numberOfOnes ->
        when {
            numberOfOnes >= size - numberOfOnes -> '1'
            else -> '0'
        }
    }

fun DiagnosticReport.leastCommonBitAt(position: Int): Char =
    countOnesAtPosition(position).let { numberOfOnes ->
        when {
            numberOfOnes < size - numberOfOnes -> '1'
            else -> '0'
        }
    }

fun DiagnosticReport.numberOfDigits() = maxOf { it.length }

fun DiagnosticReport.gammaRate(): Int =
    (0 until numberOfDigits()).map { position ->
        mostCommonBitAt(position)
    }.joinToString("").toDecimal()

fun DiagnosticReport.epsilonRate(): Int =
    (0 until numberOfDigits()).map { position ->
        leastCommonBitAt(position)
    }.joinToString("").toDecimal()

fun DiagnosticReport.powerConsumption(): Int = gammaRate() * epsilonRate()

fun DiagnosticReport.oxygenGeneratorRatingFilter(position: Int = 0): DiagnosticReport =
    filter { binaryNumber ->
        binaryNumber[position] == mostCommonBitAt(position)
    }.let { filteredReport ->
        if (filteredReport.size == 1) {
            filteredReport
        } else {
            filteredReport.oxygenGeneratorRatingFilter(position + 1)
        }
    }

fun DiagnosticReport.co2ScrubberRatingFilter(position: Int = 0): DiagnosticReport =
    filter { binaryNumber ->
        binaryNumber[position] == leastCommonBitAt(position)
    }.let { filteredReport ->
        if (filteredReport.size == 1) {
            filteredReport
        } else {
            filteredReport.co2ScrubberRatingFilter(position + 1)
        }
    }

fun DiagnosticReport.oxygenGeneratorRating() = oxygenGeneratorRatingFilter().first().toDecimal()
fun DiagnosticReport.co2ScrubberRating() = co2ScrubberRatingFilter().first().toDecimal()
fun DiagnosticReport.lifeSupportRating(): Int = oxygenGeneratorRating() * co2ScrubberRating()

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(testInput.powerConsumption() == 198)

    val input = readInput("Day03")
    println(input.powerConsumption())

    check(testInput.lifeSupportRating() == 230)
    println(input.lifeSupportRating())
}
