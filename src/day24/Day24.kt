package day24

import readInput
import kotlin.system.measureTimeMillis

class Alu(instructions: List<String>) {
    private val steps: List<Pair<String, (Int) -> Int>>

    var w = 0
    var x = 0
    var y = 0
    var z = 0

    val wEqual = { instr2: Int -> { _: Int -> w = if (w == instr2) 1 else 0; w } }
    val xEqual = { instr2: Int -> { _: Int -> x = if (x == instr2) 1 else 0; x } }
    val yEqual = { instr2: Int -> { _: Int -> y = if (y == instr2) 1 else 0; y } }
    val zEqual = { instr2: Int -> { _: Int -> z = if (z == instr2) 1 else 0; z } }

    val wEqualX = { _: Int -> w = if (w == x) 1 else 0; w }
    val wEqualY = { _: Int -> w = if (w == y) 1 else 0; w }
    val wEqualZ = { _: Int -> w = if (w == z) 1 else 0; w }
    val xEqualW = { _: Int -> x = if (x == w) 1 else 0; x }
    val xEqualY = { _: Int -> x = if (x == y) 1 else 0; x }
    val xEqualZ = { _: Int -> x = if (x == z) 1 else 0; x }
    val yEqualX = { _: Int -> y = if (y == x) 1 else 0; y }
    val yEqualW = { _: Int -> y = if (y == w) 1 else 0; y }
    val yEqualZ = { _: Int -> y = if (y == z) 1 else 0; y }
    val zEqualX = { _: Int -> z = if (z == x) 1 else 0; z }
    val zEqualY = { _: Int -> z = if (z == y) 1 else 0; z }
    val zEqualW = { _: Int -> z = if (z == w) 1 else 0; z }

    val inputW = { input: Int -> w = input; w }
    val inputX = { input: Int -> x = input; x }
    val inputY = { input: Int -> y = input; y }
    val inputZ = { input: Int -> z = input; z }

    val wAdd = { instr2: Int -> { _: Int -> w += instr2; w } }
    val xAdd = { instr2: Int -> { _: Int -> x += instr2; x } }
    val yAdd = { instr2: Int -> { _: Int -> y += instr2; y } }
    val zAdd = { instr2: Int -> { _: Int -> z += instr2; z } }

    val wAddX = { _: Int -> w += x; w }
    val wAddY = { _: Int -> w += y; w }
    val wAddZ = { _: Int -> w += z; w }
    val xAddW = { _: Int -> x += w; x }
    val xAddY = { _: Int -> x += y; x }
    val xAddZ = { _: Int -> x += z; x }
    val yAddW = { _: Int -> y += w; y }
    val yAddX = { _: Int -> y += x; y }
    val yAddZ = { _: Int -> y += z; y }
    val zAddW = { _: Int -> z += w; z }
    val zAddX = { _: Int -> z += x; z }
    val zAddY = { _: Int -> z += y; z }

    val wMod = { instr2: Int -> { _: Int -> w %= instr2; w } }
    val xMod = { instr2: Int -> { _: Int -> x %= instr2; x } }
    val yMod = { instr2: Int -> { _: Int -> y %= instr2; y } }
    val zMod = { instr2: Int -> { _: Int -> z %= instr2; z } }

    val wDiv = { instr2: Int -> { _: Int -> w /= instr2; w } }
    val xDiv = { instr2: Int -> { _: Int -> x /= instr2; x } }
    val yDiv = { instr2: Int -> { _: Int -> y /= instr2; y } }
    val zDiv = { instr2: Int -> { _: Int -> z /= instr2; z } }

    val wMul = { instr2: Int -> { _: Int -> w *= instr2; w } }
    val xMul = { instr2: Int -> { _: Int -> x *= instr2; x } }
    val yMul = { instr2: Int -> { _: Int -> y *= instr2; y } }
    val zMul = { instr2: Int -> { _: Int -> z *= instr2; z } }

    val wMulX = { _: Int -> w *= x; w }
    val wMulY = { _: Int -> w *= y; w }
    val wMulZ = { _: Int -> w *= z; w }
    val xMulW = { _: Int -> x *= w; x }
    val xMulY = { _: Int -> x *= y; x }
    val xMulZ = { _: Int -> x *= z; x }
    val yMulW = { _: Int -> y *= w; y }
    val yMulX = { _: Int -> y *= x; y }
    val yMulZ = { _: Int -> y *= z; y }
    val zMulW = { _: Int -> z *= w; z }
    val zMulX = { _: Int -> z *= x; z }
    val zMulY = { _: Int -> z *= y; z }


    init {
        steps = instructions.map {
            it.split(" ").let { instructionParts ->
                val param = if (instructionParts.size > 2) {
                    if (instructionParts[2].length == 1 && !instructionParts[2].first().isDigit()) {
                        instructionParts[2].first()
                    } else {
                        instructionParts[2].toInt()
                    }
                } else null
                val cmd = instructionParts.first()
                cmd to when {
                    cmd == "inp" -> when (instructionParts[1].first()) {
                        'w' -> inputW
                        'x' -> inputX
                        'y' -> inputY
                        'z' -> inputZ
                        else -> throw IllegalArgumentException()
                    }
                    cmd == "add" && param is Int -> when (instructionParts[1].first()) {
                        'w' -> wAdd(param)
                        'x' -> xAdd(param)
                        'y' -> yAdd(param)
                        'z' -> zAdd(param)
                        else -> throw IllegalArgumentException()
                    }
                    cmd == "add" && param is Char -> when (instructionParts[1].first()) {
                        'w' -> when (param) {
                            'x' -> wAddX
                            'y' -> wAddY
                            'z' -> wAddZ
                            else -> throw IllegalArgumentException()
                        }
                        'x' -> when (param) {
                            'w' -> xAddW
                            'y' -> xAddY
                            'z' -> xAddZ
                            else -> throw IllegalArgumentException()
                        }
                        'y' -> when (param) {
                            'x' -> yAddX
                            'w' -> yAddW
                            'z' -> yAddZ
                            else -> throw IllegalArgumentException()
                        }
                        'z' -> when (param) {
                            'x' -> zAddX
                            'y' -> zAddY
                            'w' -> zAddW
                            else -> throw IllegalArgumentException()
                        }
                        else -> throw IllegalArgumentException()
                    }
                    cmd == "eql" && param is Int -> when (instructionParts[1].first()) {
                        'w' -> wEqual(param)
                        'x' -> xEqual(param)
                        'y' -> yEqual(param)
                        'z' -> zEqual(param)
                        else -> throw IllegalArgumentException()
                    }
                    cmd == "mod" && param is Int -> when (instructionParts[1].first()) {
                        'w' -> wMod(param)
                        'x' -> xMod(param)
                        'y' -> yMod(param)
                        'z' -> zMod(param)
                        else -> throw IllegalArgumentException()
                    }
                    cmd == "div" && param is Int -> when (instructionParts[1].first()) {
                        'w' -> wDiv(param)
                        'x' -> xDiv(param)
                        'y' -> yDiv(param)
                        'z' -> zDiv(param)
                        else -> throw IllegalArgumentException()
                    }
                    cmd == "eql" && param is Char -> when (instructionParts[1].first()) {
                        'w' -> when (param) {
                            'x' -> wEqualX
                            'y' -> wEqualY
                            'z' -> wEqualZ
                            else -> throw IllegalArgumentException()
                        }
                        'x' -> when (param) {
                            'w' -> xEqualW
                            'y' -> xEqualY
                            'z' -> xEqualZ
                            else -> throw IllegalArgumentException()
                        }
                        'y' -> when (param) {
                            'x' -> yEqualX
                            'w' -> yEqualW
                            'z' -> yEqualZ
                            else -> throw IllegalArgumentException()
                        }
                        'z' -> when (param) {
                            'x' -> zEqualX
                            'y' -> zEqualY
                            'w' -> zEqualW
                            else -> throw IllegalArgumentException()
                        }
                        else -> throw IllegalArgumentException()
                    }
                    cmd == "mul" && param is Int -> when (instructionParts[1].first()) {
                        'w' -> wMul(param)
                        'x' -> xMul(param)
                        'y' -> yMul(param)
                        'z' -> zMul(param)
                        else -> throw IllegalArgumentException()
                    }
                    cmd == "mul" && param is Char -> when (instructionParts[1].first()) {
                        'w' -> when (param) {
                            'x' -> wMulX
                            'y' -> wMulY
                            'z' -> wMulZ
                            else -> throw IllegalArgumentException()
                        }
                        'x' -> when (param) {
                            'w' -> xMulW
                            'y' -> xMulY
                            'z' -> xMulZ
                            else -> throw IllegalArgumentException()
                        }
                        'y' -> when (param) {
                            'x' -> yMulX
                            'w' -> yMulW
                            'z' -> yMulZ
                            else -> throw IllegalArgumentException()
                        }
                        'z' -> when (param) {
                            'x' -> zMulX
                            'y' -> zMulY
                            'w' -> zMulW
                            else -> throw IllegalArgumentException()
                        }
                        else -> throw IllegalArgumentException()
                    }
                    else -> throw IllegalArgumentException("unknown input $cmd")
                }
            }
        }
    }

    fun execute(vararg input: Int): Int {
        w = 0
        x = 0
        y = 0
        z = 0
        var inputCounter = 0

        val result = steps.fold(0) { acc, instr ->
            if (instr.first == "inp") {
                instr.second(input[inputCounter]).also {
                    inputCounter++
                }
            } else {
                instr.second(acc)
            }
        }
        return result
    }
}

fun main() {

    fun part1(input: List<String>): Long {
        val s = generateSequence(99995969919326L) { l ->
            var next = l
            do {
                next--
            } while (next.toString().contains('0'))
            next
            //}.takeWhile { it < 11_111_121_111_111 }
        }.takeWhile { it > 11_111_111_111_111 }

        val alu = Alu(input)
        s.forEach { num ->
            if (num % 11_111_111 == 1L) println(num)
            val currentNumber = num.toString().map { it.toString().toInt() }
            alu.execute(*currentNumber.toIntArray())
            if (alu.z == 0) {
                return num
            }
        }
        throw IllegalStateException("Not found...")
    }

    fun isValidNumber(
        a: Int,
        b: Int,
        c: Int,
        d: Int,
        e: Int,
        f: Int,
        g: Int,
        h: Int,
        i: Int,
        j: Int,
        k: Int,
        l: Int,
        m: Int,
        n: Int
    ): Boolean {

        val q = ((a + 10) * 26 + b + 5)
        val r = if (c == d) {
            q
        } else {
            q * 26 + d + 12
        }
        val s = if (e + 4 == f) {
            r
        } else {
            r * 26 + f + 4
        }
        val t = (if (g + 3 == h) {
            s * 26 + i + 7
        } else {
            (s * 26 + h + 3) * 26 + i + 7
        })
        val u = if (g + 3 == h) {
            s
        } else {
            s * 26 + h + 3
        }


        var z = (if (j + 8 == k) {
            if (i - 6 == l) {
                if (g + 3 == h) {
                    if (e + 4 == f) {
                        if (c == d) {
                            if (b - 7 == m) 0 else a + 10
                        } else {
                            if (d == m) q / 26 else q
                        }
                    } else {
                        if (f - 8 == m) r / 26 else r
                    }
                } else {
                    if (h - 9 == m) s / 26 else s
                }
            } else {
                if (l == m) u / 26 else u
            }
        } else {
            if (l == m) t / 26 else t
        })

        var x = (if (j + 8 == k) {
            if (i - 6 == l) {
                if (g + 3 == h) {
                    if (e + 4 == f) {
                        if (c == d) {
                            if (b - 7 == m) a + 10 else m + 4
                        } else {
                            if (d == m) q % 26 else m + 4
                        }
                    } else {
                        if (f - 8 == m) r % 26 else m + 4
                    }
                } else {
                    if (h - 9 == m) s % 26 else m + 4
                }
            } else {
                if (l == m) u % 26 else m + 4
            }
        } else {
            if (l == m) t % 26 else m + 4
        }) - 13

        x = if (x != n) {
            1
        } else {
            0
        }
        var y = 25 * x + 1
        z = z * y + x

        y = (n + 11) * x
        z += y

        return z == 0
    }


    fun part2(input: List<String>): Long {

        val numbers = (1..9).flatMap { a ->
            (8..9).flatMap { b ->
                (1..9).flatMap { c ->
                    val d = c
                    (1..5).flatMap { e ->
                        val f = e + 4
                        (1..6).flatMap { g ->
                            val h = g + 3
                            (7..9).flatMap { i ->
                                val j = 1
                                val k = 9
                                val l = i - 6
                                val m = b - 7
                                (1..9).map { n ->
                                    listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
                                }
                            }
                        }
                    }
                }
            }
        }.let {
            it.filter {
                isValidNumber(
                    it[0],
                    it[1],
                    it[2],
                    it[3],
                    it[4],
                    it[5],
                    it[6],
                    it[7],
                    it[8],
                    it[9],
                    it[10],
                    it[11],
                    it[12],
                    it[13]
                )
            }.map {
                it[0] * 10_000_000_000_000 +
                        it[1] * 1_000_000_000_000 +
                        it[2] * 100_000_000_000 +
                        it[3] * 10_000_000_000 +
                        it[4] * 1_000_000_000 +
                        it[5] * 100_000_000 +
                        it[6] * 10_000_000 +
                        it[7] * 1_000_000 +
                        it[8] * 100_000 +
                        it[9] * 10_000 +
                        it[10] * 1_000 +
                        it[11] * 100 +
                        it[12] * 10 +
                        it[13] * 1
            }.sortedBy { it }
        }

        numbers.forEach {
            val n = it.toString().map { it.toString().toInt() }
            if (isValidNumber(
                    n[0],
                    n[1],
                    n[2],
                    n[3],
                    n[4],
                    n[5],
                    n[6],
                    n[7],
                    n[8],
                    n[9],
                    n[10],
                    n[11],
                    n[12],
                    n[13]
                )
            ) {
                return it
            }
        }
        throw IllegalStateException("Not found...")
    }

    val testInput = readInput("day24/Day24_test")
    val testInput2 = readInput("day24/Day24_test_2")
    val testInput3 = readInput("day24/Day24_test_3")
    val input = readInput("day24/Day24")

    assert(Alu(testInput).execute(10) == -10)
    assert(Alu(testInput2).execute(3, 9) == 1)
    val alu3 = Alu(testInput3)
    alu3.execute(7)
    assert(alu3.w == 0)
    assert(alu3.x == 1)
    assert(alu3.y == 1)
    assert(alu3.z == 1)

    val solutionPart1: Long
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 99995969919326L)

    val solutionPart2: Long
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 48111514719111L)
}
