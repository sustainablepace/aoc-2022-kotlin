typealias RandomNumbers = List<Int>
typealias Boards = List<Board>

@JvmInline
value class Board(val rows: List<List<Int>>) {
    init {
        assert(rows.size == 5)
        assert(rows.all { it.size == 5})
    }

    private fun unmarkedNumbers(drawnNumbers: RandomNumbers): Set<Int> {
        return rows.flatMap { it }.toSet().minus(drawnNumbers)
    }

    private fun checkRows(drawnNumbers: RandomNumbers): Bingo? {
        rows.forEach { row ->
            if (drawnNumbers.containsAll(row)) {
                return Bingo(unmarkedNumbers(drawnNumbers).sum() * drawnNumbers.last())
            }
        }
        return null
    }

    private fun checkColumns(drawnNumbers: RandomNumbers): Bingo? {
        (0 until 5).forEach { column ->
            if (drawnNumbers.containsAll(rows.map { it[column] })) {
                return Bingo(unmarkedNumbers(drawnNumbers).sum() * drawnNumbers.last())
            }
        }
        return null
    }

    fun check(drawnNumbers: RandomNumbers): Bingo? {
        checkRows(drawnNumbers)?.let {
            return it
        }
        checkColumns(drawnNumbers)?.let {
            return it
        }
        return null
    }
}


@JvmInline
value class Bingo(val score: Int)

class BingoSubsystem(input: List<String>) {
    val numbers: RandomNumbers
    val boards: Boards

    init {
        input.filter {
            it.trim() != ""
        }.let {
            numbers = it.first().split(",").map { it.toInt() }
            boards = it.subList(1, it.size).chunked(5).map { fiveByFive ->
                fiveByFive.map { row ->
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
