package day03

import readInput

typealias ItemType = Char
fun ItemType.priority() = if (isUpperCase()) code - 65 + 27 else code - 97 + 1

typealias Compartment = Set<ItemType>

typealias MultiCompartmentRucksack = List<Compartment>
fun MultiCompartmentRucksack.findDuplicate() = reduce { acc, cur -> acc.intersect(cur) }.first()
fun String.twoCompartmentRucksack(): MultiCompartmentRucksack = listOf(
    slice(0 until length / 2).toSet(),
    slice(length / 2 until length).toSet()
)

typealias SingleCompartmentRucksack = Compartment
fun String.singleCompartmentRucksack(): SingleCompartmentRucksack = toSet()

fun main() {
    fun part1(input: List<String>): Int = input
        .map(String::twoCompartmentRucksack)
        .map(MultiCompartmentRucksack::findDuplicate)
        .sumOf(ItemType::priority)

    fun part2(input: List<String>): Int = input
        .map(String::singleCompartmentRucksack)
        .chunked(3)
        .map(List<SingleCompartmentRucksack>::findDuplicate)
        .sumOf(ItemType::priority)

    val testInput = readInput("day03/Day03_test")
    val input = readInput("day03/Day03")

    check(part1(testInput).also { println(it) } == 157)
    check(part1(input).also { println(it) } == 7553)

    check(part2(testInput).also { println(it) } == 70)
    check(part2(input).also { println(it) } == 2758)
}
