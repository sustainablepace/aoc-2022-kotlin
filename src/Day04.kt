typealias DrawnNumbers = List<Int>

@JvmInline
value class Bingo(val score: Int)

@JvmInline
value class Board(val rows: List<List<Int>>) {
    init {
        assert(rows.size == 5)
        assert(rows.all { it.size == 5 })
    }

    private val allNumbers: Set<Int>
        get() = rows.flatMap { it }.toSet()

    private val columns: List<List<Int>>
        get() = (0 until 5).map { column ->
            rows.map { row -> row[column] }
        }

    private fun score(drawnNumbers: DrawnNumbers): Int =
        allNumbers.minus(drawnNumbers).sum() * drawnNumbers.last()

    private fun checkRows(drawnNumbers: DrawnNumbers): Bingo? =
        rows.find { row -> drawnNumbers.containsAll(row) }?.let {
            Bingo(score(drawnNumbers))
        }

    private fun checkColumns(drawnNumbers: DrawnNumbers): Bingo? =
        columns.find { column -> drawnNumbers.containsAll(column) }?.let {
            Bingo(score(drawnNumbers))
        }

    fun check(drawnNumbers: DrawnNumbers): Bingo? =
        checkRows(drawnNumbers) ?: checkColumns(drawnNumbers)
}


class BingoSubsystem(input: List<String>) {
    val numbers: DrawnNumbers
    private val boards: List<Board>

    init {
        input.filter {
            it.trim() != ""
        }.let { filteredInput ->
            numbers = filteredInput.first().split(",").map { it.toInt() }
            boards = filteredInput.subList(1, filteredInput.size).chunked(5).map { rows ->
                rows.map { row ->
                    row.trim().replace("\\s+".toRegex(), ",").split(",").map { it.toInt() }
                }.let {
                    Board(it)
                }
            }
        }
    }

    fun check(numNumbersDrawn: Int): Bingo? {
        val drawnNumbers = numbers.subList(0, numNumbersDrawn)
        boards.forEach { board ->
            board.check(drawnNumbers)?.let {
                return it
            }
        }
        return null
    }

    fun checkLast(): Bingo? {
        val unfinishedBoards = boards.indices.toMutableSet()
        (5 until numbers.size).forEach { numNumbersDrawn ->
            val drawnNumbers = numbers.subList(0, numNumbersDrawn)

            boards.forEachIndexed { index, board ->
                if (index in unfinishedBoards) {
                    board.check(drawnNumbers)?.let { result ->
                        unfinishedBoards.remove(index)
                        if (unfinishedBoards.isEmpty()) {
                            return result
                        }
                    }
                }
            }
        }

        return null
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val bingo = BingoSubsystem(input)
        (5 until bingo.numbers.size).forEach { index ->
            bingo.check(index)?.let {
                return it.score
            }
        }
        throw IllegalArgumentException("No bingo")
    }

    fun part2(input: List<String>): Int {
        val bingo = BingoSubsystem(input)
        bingo.checkLast()?.let {
            return it.score
        }
        throw IllegalArgumentException("No bingo")

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    println(part1(testInput))
    check(part1(testInput) == 4512)

    val input = readInput("Day04")
    println(part1(input))

    println(part2(testInput))
    check(part2(testInput) == 1924)
    println(part2(input))
}
