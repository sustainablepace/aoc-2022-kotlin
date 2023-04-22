package day11

import readInput

interface Item {
    fun getRemainder(divisor: Divisor): Long
    fun add(num: Item): Item
    fun mul(num: Item): Item
    fun add(num: Long): Item
    fun mul(num: Long): Item
}

@JvmInline
value class ItemWithValue(val worryLevel: Long) : Item {
    override fun getRemainder(divisor: Divisor) = worryLevel % divisor
    override fun add(num: Item) = num.add(worryLevel)
    override fun add(num: Long): Item = ItemWithValue(worryLevel + num)
    override fun mul(num: Item) = num.mul(worryLevel)
    override fun mul(num: Long): Item = ItemWithValue(worryLevel * num)
}

typealias Divisor = Int

class ItemWithRemainder(private val num: Long, divisors: List<Divisor>) : Item {
    private var remainders: MutableMap<Divisor, Long> = mutableMapOf()
    init {
        divisors.forEach {
            remainders[it] = num % it
        }
    }
    override fun getRemainder(divisor: Divisor) = remainders[divisor]!!

    override fun add(num: Item): Item {
        for ((divisor, remainder) in remainders) {
            remainders[divisor] = (remainder + num.getRemainder(divisor)) % divisor
        }
        return this
    }

    override fun add(num: Long): Item {
        for ((divisor, remainder) in remainders) {
            remainders[divisor] = (remainder + num) % divisor
        }
        return this
    }

    override fun mul(num: Item): Item {
        for ((divisor, remainder) in remainders) {
            remainders[divisor] = (remainder * num.getRemainder(divisor)) % divisor
        }
        return this
    }

    override fun mul(num: Long): Item {
        for ((divisor, remainder) in remainders) {
            remainders[divisor] = (remainder * num) % divisor
        }
        return this
    }
}

typealias MonkeyNumber = Int
typealias Operation = String

interface Me<I> {
    fun adjustWorryLevel(item: I): I
}

class RelievedMe : Me<ItemWithValue> {
    override fun adjustWorryLevel(item: ItemWithValue): ItemWithValue = ItemWithValue(item.worryLevel / 3)
}

class NervousMe : Me<ItemWithRemainder> {
    override fun adjustWorryLevel(item: ItemWithRemainder): ItemWithRemainder = item
}

data class Monkey(
    val number: MonkeyNumber,
    private val operation: Operation,
    private val divisor: Divisor,
    private val monkeyIfTrue: MonkeyNumber,
    private val monkeyIfFalse: MonkeyNumber,
    var items: MutableList<Item>,
    var inspections: Long = 0
) {
    fun decideWhereToThrow(item: Item): MonkeyNumber =
        if (item.getRemainder(divisor) == 0L) monkeyIfTrue else monkeyIfFalse

    fun inspectItemsAndWatch(me: Me<Item>): Monkey {
        inspections += items.size
        items = items.map {
            it.inspectAndWatch(me)
        }.toMutableList()
        return this
    }

    private fun Item.inspectAndWatch(me: Me<Item>): Item =
        operation.split(" ").let { (_, operator, right) ->
            when (operator) {
                "+" -> if (right == "old") this.add(this) else this.add(right.toLong())
                else -> if (right == "old") this.mul(this) else this.mul(right.toLong())
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

    fun doNextRoundOfTaunting(me: Me<Item>) {
        monkeys.forEach { it.inspectItemsAndWatch(me).throwItems() }
    }

    private fun Monkey.throwItems() {
        items.forEach {
            monkeys[decideWhereToThrow(it)].items.add(it)
            monkeys[number].items = emptyList<ItemWithValue>().toMutableList()
        }
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        val monkeys = input.chunked(7).map { monkey: List<String> ->
            Monkey(
                number = monkey[0].takeLast(2).first().toString().toInt(),
                divisor = monkey[3].substringAfter("divisible by ").toInt(),
                items = monkey[1].substringAfter(": ").split(", ").map { ItemWithValue(it.toLong()) }.toMutableList(),
                monkeyIfTrue = monkey[4].substringAfter("monkey ").toInt(),
                monkeyIfFalse = monkey[5].substringAfter("monkey ").toInt(),
                operation = monkey[2].substringAfter("new = ")
            )

        }.toMutableList().let {
            Monkeys(it)
        }
        val me = RelievedMe() as Me<Item>

        repeat(20) {
            monkeys.doNextRoundOfTaunting(me)
        }
        return monkeys.levelOfMonkeyBusiness()
    }

    fun part2(input: List<String>): Long {
        val monkeys = input.chunked(7).map { monkey: List<String> ->
            Monkey(
                number = monkey[0].takeLast(2).first().toString().toInt(),
                divisor = monkey[3].substringAfter("divisible by ").toInt(),
                items = monkey[1].substringAfter(": ").split(", ").map { ItemWithRemainder(it.toLong(), listOf(17,3,19,7,2,5,11,13,23)) }.toMutableList(),
                monkeyIfTrue = monkey[4].substringAfter("monkey ").toInt(),
                monkeyIfFalse = monkey[5].substringAfter("monkey ").toInt(),
                operation = monkey[2].substringAfter("new = ")
            )

        }.toMutableList().let {
            Monkeys(it)
        }
        val me = NervousMe() as Me<Item>

        repeat(10000) {
            monkeys.doNextRoundOfTaunting(me)
        }
        return monkeys.levelOfMonkeyBusiness()
    }

    val testInput = readInput("day11/Day11_test")
    val input = readInput("day11/Day11")

    check(part1(testInput).also { println(it) } == 10605L)
    check(part1(input).also { println(it) } == 99852L)

    check(part2(testInput).also { println(it) } == 2713310158L)
    check(part2(input).also { println(it) } == 25935263541L)
}
