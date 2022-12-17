package day08

import readInput

typealias Tree = Int
typealias TreeLocation = Pair<Int, Int>
typealias Forest = List<List<Tree>>

fun Forest.rotate() = List(first().size) { index -> map { it[index] } }
fun Forest.countVisibleTrees() = visibleTreesInRows().union(visibleTreesInColumns()).size
fun Forest.visibleTreesInColumns() = rotate().visibleTreesInRows().map { (row, col) -> col to row }.toSet()

fun List<Tree>.visibleTrees(): Set<Tree> = (1 until size).mapNotNull { index ->
    index.takeIf { index == take(index + 1).let { treesSoFar -> treesSoFar.indexOf(treesSoFar.max()) } }
}.toSet()

fun Forest.visibleTreesInRows(): Set<TreeLocation> = flatMapIndexed { row, line ->
    when (row) {
        0, size - 1 -> line.indices.map { it to row }.toSet()
        else -> line.visibleTrees().map { it to row }.union(
            line.reversed().visibleTrees().map { line.size - 1 - it to row }
        )
    }
}.toSet()

fun List<String>.forest(): Forest = map { it.map { it.toString().toInt() } }

fun List<Tree>.scenicScore() = when {
    size > 1 -> takeLast(size - 1).indexOfFirst { first() <= it }.takeIf { it != -1 }?.plus(1) ?: (size - 1)
    else -> 0
}


fun main() {
    fun part1(input: List<String>): Int = input.forest().countVisibleTrees()

    fun part2(input: List<String>): Int {
        val forest = input.forest()
        val rotatedForest = forest.rotate()

        return forest.flatMapIndexed { rIndex, row ->
            List(row.size) { cIndex ->
                val left = row.take(cIndex + 1).reversed()
                val right = row.takeLast(row.size - cIndex)

                val column = rotatedForest[cIndex]
                val up = column.take(rIndex + 1).reversed()
                val down = column.takeLast(column.size - rIndex)

                left.scenicScore() * right.scenicScore() * up.scenicScore() * down.scenicScore()
            }
        }.max()
    }

    val testInput = readInput("day08/Day08_test")
    val input = readInput("day08/Day08")

    check(part1(testInput).also { println(it) } == 21)
    check(part1(input).also { println(it) } == 1796)

    check(part2(testInput).also { println(it) } == 8)
    check(part2(input).also { println(it) } == 288120)
}
