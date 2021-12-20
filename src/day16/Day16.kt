package day16

import readInput
import kotlin.system.measureTimeMillis

typealias BinaryNumber = String

fun BinaryNumber.toDecimal() = toLong(2)

val m = mapOf(
    '0' to "0000",
    '1' to "0001",
    '2' to "0010",
    '3' to "0011",
    '4' to "0100",
    '5' to "0101",
    '6' to "0110",
    '7' to "0111",
    '8' to "1000",
    '9' to "1001",
    'A' to "1010",
    'B' to "1011",
    'C' to "1100",
    'D' to "1101",
    'E' to "1110",
    'F' to "1111"
)
data class DecodedMessage(var binaryNumber: String) {
    fun calc(): Long {
        return when (typeId) {
            4L -> literal!!
            0L -> subpackets.sumOf { it.calc() }
            1L -> subpackets.fold(1L) { acc, message -> acc * message.calc() }
            2L -> subpackets.minOf { it.calc() }
            3L -> subpackets.maxOf { it.calc() }
            5L -> if (subpackets[0].calc() > subpackets[1].calc()) {
                1L
            } else 0L
            6L -> if (subpackets[0].calc() < subpackets[1].calc()) {
                1L
            } else 0L
            7L -> if (subpackets[0].calc() == subpackets[1].calc()) {
                1L
            } else 0L
            else -> throw IllegalArgumentException("Invalid type id $typeId")
        }
    }

    fun sumVersions(): Long = version + subpackets.sumOf { it.sumVersions() }
    val version = binaryNumber.substring(0..2).toDecimal()
    val typeId = binaryNumber.substring(3..5).toDecimal()
    val literal: Long? = if (typeId == 4L) {
        var notLastGroup = true
        binaryNumber.substring(6).chunked(4).map { s ->
            if (s.length < 4) {
                s.padEnd(4, '0')
            } else s
        }.joinToString("").chunked(5).takeWhile {
            if (notLastGroup && it[0] == '0') {
                notLastGroup = false
                true
            } else notLastGroup
        }.also {
            binaryNumber = binaryNumber.take(3 + 3 + it.sumOf { it.length })
        }.map {
            it.takeLast(4)
        }.joinToString("").toDecimal()
    } else null

    val lengthTypeId = if (typeId != 4L) binaryNumber[6].toString().toDecimal() else null
    val totalLengthInBits: Long? = if (lengthTypeId == 0L) {
        binaryNumber.substring(7, 7 + 15).toDecimal()
    } else {
        null
    }
    val numberOfSubPackets: Long? = if (lengthTypeId == 1L) {
        binaryNumber.substring(7, 7 + 11).toDecimal()
    } else {
        null
    }

    val subpackets = if (lengthTypeId == 0L) {
        binaryNumber.substring(7 + 15, 7 + 15 + totalLengthInBits!!.toInt()).let {
            var code = it
            var map = mutableListOf<DecodedMessage>()
            while (code.length > 0) {
                val m = DecodedMessage(code)
                map.add(m)
                code = code.substring(m.binaryNumber.length)
            }
            binaryNumber = binaryNumber.take(3 + 3 + 1 + 15 + map.sumOf { it.binaryNumber.length })

            map
        }
    } else if (lengthTypeId == 1L) {
        var i = 0
        var code = binaryNumber.substring(7 + 11)
        var map = mutableListOf<DecodedMessage>()
        while (i < numberOfSubPackets!!) {
            val m = DecodedMessage(code)
            map.add(m)
            val oldCode = code
            code = code.substring(m.binaryNumber.length)
            i++
        }
        binaryNumber = binaryNumber.take(3 + 3 + 1 + 11 + map.sumOf { it.binaryNumber.length })


        map

    } else emptyList()

    companion object {
        fun fromHex(hex: String): DecodedMessage {
            val binaryNumber = hex.map { m[it] }.joinToString("")
            return DecodedMessage(binaryNumber)
        }
    }
}

fun main() {
    fun part1(input: List<String>): Long {

        val message = DecodedMessage.fromHex(input.first())


        return message.sumVersions()
    }

    fun part2(input: List<String>): Long {

        val message = DecodedMessage.fromHex(input.first())


        return message.calc()
    }

    val msgBin = DecodedMessage.fromHex("D2FE28")

    assert(msgBin.binaryNumber == "110100101111111000101")
    assert(msgBin.version == 6L)
    assert(msgBin.typeId == 4L)
    assert(msgBin.literal == 2021L)

    val msg2 = DecodedMessage.fromHex("38006F45291200")
    assert(msg2.binaryNumber == "0011100000000000011011110100010100101001000100100")
    assert(msg2.typeId != 4L)
    assert(msg2.lengthTypeId == 0L)
    assert(msg2.totalLengthInBits == 27L)
    assert(msg2.subpackets.size == 2)

    val msg3 = DecodedMessage.fromHex("EE00D40C823060")
    assert(msg3.binaryNumber == "111011100000000011010100000011001000001000110000011")
    assert(msg3.version == 7L)
    assert(msg3.typeId == 3L)
    assert(msg3.lengthTypeId == 1L)
    assert(msg3.numberOfSubPackets == 3L)
    assert(msg3.subpackets.size == 3)

    val msgBinary = DecodedMessage("11010001010")
    assert(msgBinary.typeId == 4L)
    assert(msgBinary.literal == 10L)
    val msgA = DecodedMessage.fromHex("8A004A801A8002F478")
    assert(msgA.version == 4L)


    val testInput1 = readInput("day16/Day16_test")
    val testInput2 = readInput("day16/Day16_test_2")
    val testInput3 = readInput("day16/Day16_test_3")
    val testInput4 = readInput("day16/Day16_test_4")
    val input = readInput("day16/Day16")



    println(part1(testInput1))
    check(part1(testInput1) == 16L)

    println(part1(testInput2))
    check(part1(testInput2) == 12L)

    println(part1(testInput3))
    check(part1(testInput3) == 23L)

    println(part1(testInput4))
    check(part1(testInput4) == 31L)

    val solutionPart1: Long
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 971L)

    //println(part2(testInput1))
   // check(part2(testInput1) == 168L)

    val solutionPart2: Long
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 831996589851L)
}
