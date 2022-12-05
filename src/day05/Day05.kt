package day05

import readInput

typealias Crate = Char
typealias CrateStack = MutableList<Crate>
typealias CrateStacks = List<CrateStack>
typealias NumberOfCrates = Int
typealias StackNumber = Int
typealias MoveOperation = Pair<StackNumber, StackNumber>
typealias Instruction = Pair<NumberOfCrates, MoveOperation>
typealias RearrangementProcedure = List<Instruction>
typealias Crane = Pair<CrateStacks, RearrangementProcedure>

@JvmInline
value class CrateMover9000(val crane: Crane) {
    fun rearrange(): String {
        crane.let { (crates, instructions) ->
            instructions.forEach { (numCrates, operation) ->
                operation.let { (fromStack, toStack) ->
                    repeat(numCrates) {
                        crates[toStack].add(crates[fromStack].removeLast())
                    }
                }
            }
            return crates.joinToString("") { it.last().toString() }
        }
    }
}

@JvmInline
value class CrateMover9001(val crane: Crane) {
    fun rearrange(): String {
        crane.let { (crates, instructions) ->
            instructions.forEach { (numCrates, operation) ->
                operation.let { (fromStack, toStack) ->
                    val cratesOnCrane = crates[fromStack].takeLast(numCrates)
                    crates[toStack].addAll(cratesOnCrane)
                    repeat(numCrates) { crates[fromStack].removeLast() }
                }
            }
            return crates.joinToString("") { it.last().toString() }
        }
    }
}

fun List<String>.crane(): Crane {
    partition { it.contains("move") }.let { (instructions, stacks) ->
        stacks.filter { it.isNotEmpty() }.let {
            val crates = mutableListOf<MutableList<Char>>()

            it.take(it.size - 1).forEach {
                it.chunked(4).forEachIndexed { index, crate ->
                    if (crates.size <= index) {
                        crates.add(mutableListOf<Char>())
                    }
                    if (crate.isNotBlank()) {
                        crates[index].add(0, crate[1])
                    }
                }
            }
            val ops = instructions.map { instruction ->
                instruction.split(" from ").partition { it.contains("move") }.let { (numCratesStr, operationStr) ->

                    val numCrates = numCratesStr.first().substring(5).toInt()
                    val operation = operationStr.first().split(" to ").map(String::toInt)
                        .let { (fromStack, toStack) -> fromStack-1 to toStack-1 }
                    numCrates to operation
                }
            }
            return crates.toList() to ops
        }
    }
}

fun main() {
    fun part1(input: List<String>): String = input.crane().let { crane ->
        val crateMover = CrateMover9000(crane)
        crateMover.rearrange()
    }

    fun part2(input: List<String>): String = input.crane().let { crane ->
        val crateMover = CrateMover9001(crane)
        crateMover.rearrange()
    }

    val testInput = readInput("day05/Day05_test")
    val input = readInput("day05/Day05")

    check(part1(testInput).also { println(it) } == "CMZ")
    check(part1(input).also { println(it) } == "JCMHLVGMG")

    check(part2(testInput).also { println(it) } == "MCD")
    check(part2(input).also { println(it) } == "LVMRWSSPZ")
}