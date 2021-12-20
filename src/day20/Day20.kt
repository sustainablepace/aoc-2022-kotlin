package day20

import readInput
import kotlin.system.measureTimeMillis

typealias Algorithm = String
typealias Image = List<String>

fun Image.grow(numPixels: Int): Image {
    val nDarkPixels= (0 until numPixels).map { '.' }.joinToString("")

    return mutableListOf<String>().let { newImage ->
        repeat(numPixels) {
            newImage.add(nDarkPixels + first().map { '.' }.joinToString("") + nDarkPixels)
        }
        this.forEach {
            newImage.add(nDarkPixels + it + nDarkPixels)
        }
        repeat(numPixels) {
            newImage.add(nDarkPixels + first().map { '.' }.joinToString("") + nDarkPixels)
        }
        newImage
    }
}

fun Image.print() {
    map {
        println(it)
    }
    println("")
}

fun Image.enhance(algorithm: Algorithm): Image {
    val numPixels = 10
    val grownImage = grow(numPixels)

    return grownImage.mapIndexed { y, row ->
        val windowsPrevious = if (y > 0) {
            grownImage[y - 1].windowed(3){ pixels ->
                pixels.map { if(it=='#') '1' else '0' }.joinToString("")
            }
        } else {
            row.map { "000" }.take(row.length-2)
        }
        val windowsCurrent = row.windowed(3) { pixels ->
            pixels.map { if(it=='#') '1' else '0' }.joinToString("")
        }
        val windowsNext = if (y < grownImage.size-1) {
            grownImage[y + 1].windowed(3){ pixels ->
                pixels.map { if(it=='#') '1' else '0' }.joinToString("")
            }
        } else {
            row.map { "000" }.take(row.length-2)
        }
        windowsCurrent.mapIndexed { index, window ->
            val num = (windowsPrevious[index] + window + windowsNext[index]).toInt(2)
            algorithm[num]
        }.joinToString("")
    }
}

fun Image.crop(numPixels: Int): Image {
    return let {
        it.drop(numPixels).dropLast(numPixels).map {
            it.drop(numPixels-2).dropLast(numPixels-2)
        }
    }
}
fun Image.countLitPixels(): Int {
    return sumOf { it.count { it == '#'} }
}

fun main() {

    fun part1(input: List<String>): Int {
        val (algorithm, image) = input.filter { it.isNotBlank() }.let {
            it.first() to it.takeLast(it.size - 1)
        }

        val enhancedImage = image.enhance(algorithm).enhance(algorithm).crop(18) // don't know why 18, it works
        enhancedImage.print()

        return enhancedImage.countLitPixels()
    }

    fun part2(input: List<String>): Int {
        val (algorithm, image) = input.filter { it.isNotBlank() }.let {
            it.first() to it.takeLast(it.size - 1)
        }

        var enhancedImage: Image = image
        repeat(25) {
            enhancedImage = enhancedImage.enhance(algorithm).enhance(algorithm).crop(18) // don't know why 18, it works
        }
        enhancedImage.print()

        return enhancedImage.countLitPixels()
    }

    val testInput = readInput("day20/Day20_test")
    val input = readInput("day20/Day20")

    println(part1(testInput))
    check(part1(testInput) == 35)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 5498)

    println(part2(testInput))
    check(part2(testInput) == 3351)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 16014)

}
