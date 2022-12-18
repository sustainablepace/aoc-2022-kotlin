package day10

import readInput

sealed class Operation
object Noop : Operation()
data class Addx(val x: Int) : Operation()

fun String.operation(): Operation = when {
    this == "noop" -> Noop
    else -> Addx(this.split(" ").get(1).toInt())
}

fun List<String>.operations() = map { it.operation() }

fun main() {
    fun part1(input: List<String>): Int {
        val x = mutableListOf<Int>(1)
        input.operations().forEach {
            when (it) {
                is Addx -> {
                    x.add(x.last())
                    x.add(x.last() + it.x)
                }
                Noop -> {
                    x.add(x.last())
                }
            }
        }

        return x.mapIndexedNotNull { index, i ->
            if(index.mod(40)==19) {
                (index+1)*i
            } else null
        }.sum()
    }

    data class Display(val str: MutableList<Char> = mutableListOf<Char>()) {
        init {
            repeat(240) {
                str.add('.')
            }
        }

        fun setPixel(x: Int, index: Int) {
            if (((x - 1)..(x + 1)).contains(index % 40)) {
                str[index] = '#'
            }
        }

        override fun toString() = str.joinToString("").chunked(40).joinToString("\n")
    }
    fun part2(input: List<String>): String {
        val display = Display()

        val x = mutableListOf<Int>(1)
        input.operations().forEach {
            when (it) {
                is Addx -> {
                    display.setPixel(x.last(), x.size-1)
                    x.add(x.last())
                    display.setPixel(x.last(), x.size-1)
                    x.add(x.last() + it.x)
                }
                Noop -> {
                    display.setPixel(x.last(), x.size-1)
                    x.add(x.last())
                }
            }
        }
        return display.toString()
    }


    val testInput = readInput("day10/Day10_test")
    val input = readInput("day10/Day10")

    check(part1(testInput).also { println(it) } == 13140)
    check(part1(input).also { println(it) } == 15880)

    check(part2(testInput).also { println(it + "\n") } == """
        ##..##..##..##..##..##..##..##..##..##..
        ###...###...###...###...###...###...###.
        ####....####....####....####....####....
        #####.....#####.....#####.....#####.....
        ######......######......######......####
        #######.......#######.......#######.....
    """.trimIndent())

    check(part2(input).also { println(it + "\n") } == """
        ###..#.....##..####.#..#..##..####..##..
        #..#.#....#..#.#....#.#..#..#....#.#..#.
        #..#.#....#....###..##...#..#...#..#....
        ###..#....#.##.#....#.#..####..#...#.##.
        #....#....#..#.#....#.#..#..#.#....#..#.
        #....####..###.#....#..#.#..#.####..###.
    """.trimIndent())
}
