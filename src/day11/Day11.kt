package day11

import readInput

interface I {
    fun worryLevelDivisibleBy(divisor: Int): Boolean
    fun worryLevel(): Long
    fun add(num: Long): I
    fun mul(num: Long): I
}

@JvmInline
value class Item(private val worryLevel: Long) : I {
    override fun worryLevelDivisibleBy(divisor: Int) = worryLevel % divisor == 0L
    override fun worryLevel(): Long = worryLevel
    override fun add(num: Long): Item = Item(worryLevel + num)
    override fun mul(num: Long) = Item(worryLevel * num)
}

@JvmInline
value class ItemWithPrimes(private val worryLevelPrimes: List<Long>) : I {
    override fun worryLevelDivisibleBy(divisor: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun worryLevel(): Long {
        TODO("Not yet implemented")
    }

    override fun add(num: Long): I {
        TODO("Not yet implemented")
    }

    override fun mul(num: Long): I {
        TODO("Not yet implemented")
    }

}
typealias MonkeyNumber = Int
typealias Operation = String

interface Me {
    fun adjustWorryLevel(item: Item): Item
}

class RelievedMe : Me {
    override fun adjustWorryLevel(item: Item): Item = Item(item.worryLevel() / 3)
}

class NervousMe : Me {
    override fun adjustWorryLevel(item: Item): Item = item
}

data class Monkey(
    val number: MonkeyNumber,
    private val operation: Operation,
    private val divisor: Int,
    private val monkeyIfTrue: MonkeyNumber,
    private val monkeyIfFalse: MonkeyNumber,
    var items: MutableList<Item>,
    var inspections: Long = 0
) {
    fun decideWhereToThrow(item: Item): MonkeyNumber =
        if (item.worryLevelDivisibleBy(divisor)) monkeyIfTrue else monkeyIfFalse

    fun inspectItemsAndWatch(me: Me): Monkey {
        inspections += items.size
        items = items.map {
            it.inspectAndWatch(me)
        }.toMutableList()
        return this
    }

    private fun Item.inspectAndWatch(me: Me): Item =
        operation.split(" ").let { (left, operator, right) ->
            Triple(
                if (left == "old") worryLevel() else left.toLong(),
                operator,
                if (right == "old") worryLevel() else right.toLong()
            ).let { (left, operator, right) ->
                Item(
                    when (operator) {
                        "+" -> left + right
                        else -> left * right
                    }
                )
            }
        }.let {
            me.adjustWorryLevel(it)
        }
}

@JvmInline
value class Monkeys(private val monkeys: MutableList<Monkey>) {
    fun levelOfMonkeyBusiness() = monkeys.also {
        monkeys.sortByDescending { it.inspections }
    }.let {
        monkeys.take(2).let { (m1, m2) ->
            m1.inspections * m2.inspections
        }
    }

    fun display() = monkeys.forEach { println(it) }
    fun doNextRoundOfTaunting(me: Me) {
        monkeys.forEach { it.inspectItemsAndWatch(me).throwItems() }
    }

    private fun Monkey.throwItems() {
        items.forEach {
            monkeys[decideWhereToThrow(it)].items.add(it)
            monkeys[number].items = emptyList<Item>().toMutableList()
        }
    }

    companion object {
        fun parse(input: List<String>): Monkeys {
            val monkeys = input.chunked(7).map { monkey: List<String> ->
                Monkey(
                    number = monkey[0].takeLast(2).first().toString().toInt(),
                    divisor = monkey[3].substringAfter("divisible by ").toInt(),
                    items = monkey[1].substringAfter(": ").split(", ").map { Item(it.toLong()) }.toMutableList(),
                    monkeyIfTrue = monkey[4].substringAfter("monkey ").toInt(),
                    monkeyIfFalse = monkey[5].substringAfter("monkey ").toInt(),
                    operation = monkey[2].substringAfter("new = ")
                )

            }.toMutableList()
            return Monkeys(monkeys)
        }
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        val monkeys = Monkeys.parse(input)
        val me = RelievedMe()

        repeat(20) {
            monkeys.doNextRoundOfTaunting(me)
        }
        return monkeys.levelOfMonkeyBusiness()
    }

    fun part2(input: List<String>): Long {
        val monkeys = Monkeys.parse(input)
        val me = NervousMe()

        repeat(20) {
            monkeys.doNextRoundOfTaunting(me)
        }
        monkeys.display()
        return monkeys.levelOfMonkeyBusiness()
    }

    val testInput = readInput("day11/Day11_test")
    val input = readInput("day11/Day11")

    check(part1(testInput).also { println(it) } == 10605L)
    check(part1(input).also { println(it) } == 99852L)

    check(part2(testInput).also { println(it) } == 2713310158L)
    check(part2(input).also { println(it) } == TODO())
}
