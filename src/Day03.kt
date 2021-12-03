typealias BinaryNumber = String
fun BinaryNumber.toDecimal() = toInt(2)

typealias Pos = Int

@JvmInline
value class DiagnosticReport(private val binaryNumbers: List<BinaryNumber>) {

    fun powerConsumption(): Int = gammaRate() * epsilonRate()
    fun lifeSupportRating(): Int = oxygenGeneratorRating() * co2ScrubberRating()

    private fun countOnesAt(i: Pos) = binaryNumbers.count { binaryNumber -> binaryNumber[i] == '1' }

    private fun mostCommonBitAt(i: Pos): Char =
        countOnesAt(i).let { numberOfOnes -> if(numberOfOnes >= binaryNumbers.size - numberOfOnes) '1' else '0' }

    private fun leastCommonBitAt(i: Pos): Char = if(mostCommonBitAt(i) == '1') '0' else '1'

    private fun binaryNumberLength() = binaryNumbers.maxOf { it.length }

    private fun gammaRate(): Int =
        (0 until binaryNumberLength()).map { mostCommonBitAt(it) }.joinToString("").toDecimal()

    private fun epsilonRate(): Int =
        (0 until binaryNumberLength()).map { leastCommonBitAt(it) }.joinToString("").toDecimal()

    private fun toDecimal(): Int = binaryNumbers.first().toDecimal()

    private fun oxygenGeneratorRatingFilter(i: Pos = 0): DiagnosticReport =
        binaryNumbers.filter { binaryNumber ->
            binaryNumber[i] == mostCommonBitAt(i)
        }.let { filteredReport ->
            if (filteredReport.size == 1) {
                DiagnosticReport(filteredReport)
            } else {
                DiagnosticReport(filteredReport).oxygenGeneratorRatingFilter(i + 1)
            }
        }

    private fun co2ScrubberRatingFilter(i: Pos = 0): DiagnosticReport =
        binaryNumbers.filter { binaryNumber ->
            binaryNumber[i] == leastCommonBitAt(i)
        }.let { filteredReport ->
            if (filteredReport.size == 1) {
                DiagnosticReport(filteredReport)
            } else {
                DiagnosticReport(filteredReport).co2ScrubberRatingFilter(i + 1)
            }
        }

    private fun oxygenGeneratorRating() = oxygenGeneratorRatingFilter().toDecimal()
    private fun co2ScrubberRating() = co2ScrubberRatingFilter().toDecimal()
}


fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(DiagnosticReport(testInput).powerConsumption() == 198)

    val input = readInput("Day03")
    println(DiagnosticReport(input).powerConsumption())

    check(DiagnosticReport(testInput).lifeSupportRating() == 230)
    println(DiagnosticReport(input).lifeSupportRating())
}
