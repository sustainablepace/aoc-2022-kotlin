import kotlin.math.abs
import kotlin.system.measureTimeMillis


data class Beacon(val x: Int, val y: Int, val z: Int) {
    fun manhattan(): Int {
        return abs(x) + abs(y) + abs(z)
    }
}

operator fun Beacon.plus(v: Vector) = Beacon(x = x + v.x, y = y + v.y, z = z + v.z)
operator fun Beacon.minus(v: Vector) = Beacon(x = x - v.x, y = y - v.y, z = z - v.z)

typealias Vector = Beacon

operator fun Vector.times(b: Beacon): Int = x * b.x + y * b.y + z * b.z

data class Matrix(val rows: Triple<Vector, Vector, Vector>) {
    operator fun times(b: Beacon): Beacon =
        Beacon(
            x = rows.first * b,
            y = rows.second * b,
            z = rows.third * b
        )
}

val first = listOf<Matrix>(
    Matrix(
        Triple(
            Vector(1, 0, 0),
            Vector(0, 1, 0),
            Vector(0, 0, 1)
        )
    ),
    Matrix(
        Triple(
            Vector(0, 1, 0),
            Vector(0, 0, 1),
            Vector(1, 0, 0)
        )
    ),
    Matrix(
        Triple(
            Vector(0, 0, 1),
            Vector(1, 0, 0),
            Vector(0, 1, 0)
        )
    )
)
val second = listOf<Matrix>(
    Matrix(
        Triple(
            Vector(1, 0, 0),
            Vector(0, 1, 0),
            Vector(0, 0, 1)
        )
    ),
    Matrix(
        Triple(
            Vector(-1, 0, 0),
            Vector(0, -1, 0),
            Vector(0, 0, 1)
        )
    ),
    Matrix(
        Triple(
            Vector(-1, 0, 0),
            Vector(0, 1, 0),
            Vector(0, 0, -1)
        )
    ),
    Matrix(
        Triple(
            Vector(1, 0, 0),
            Vector(0, -1, 0),
            Vector(0, 0, -1)
        )
    )
)
val third = listOf<Matrix>(
    Matrix(
        Triple(
            Vector(1, 0, 0),
            Vector(0, 1, 0),
            Vector(0, 0, 1)
        )
    ),
    Matrix(
        Triple(
            Vector(0, 0, -1),
            Vector(0, -1, 0),
            Vector(-1, 0, 0)
        )
    )
)

data class Scanner(val num: Int, val beacons: List<Beacon>) {
    fun permutations() = first.flatMap { f ->
        second.flatMap { s ->
            third.map { t ->
                beacons.map { f * (s * (t * it)) }
            }
        }
    }.also {
        assert(it.size == 24)
    }.map {
        Scanner(num, it)
    }

    fun move(v: Vector) = copy(
        beacons = beacons.map {
            it + v
        }
    )

    fun normalize() =
        beacons.reduce { acc, beacon ->
            Vector(
                x = if (beacon.x < acc.x) beacon.x else acc.x,
                y = if (beacon.y < acc.y) beacon.y else acc.y,
                z = if (beacon.z < acc.z) beacon.z else acc.z
            )
        }.let { vector ->
            Scanner(num, beacons.map {
                it - vector
            })
        }

    fun normalizeTo(beacon: Beacon) =
        Scanner(num, beacons.map {
            it - beacon
        })


    fun match(scanner2: Scanner): Pair<Scanner, Vector>? {
        val scanner1 = this
        return scanner2.permutations().map { s2Perm ->
            scanner1 to s2Perm
        }.flatMap { (s1, s2) ->
            s1.beacons.flatMap { b1 ->
                s2.beacons.map { b2 ->
                    b1 to b2
                }
            }.mapNotNull { (b1, b2) ->
                val overlap = s1.beacons.intersect(s2.normalizeTo(b2 - b1).beacons)
                if (overlap.size >= 12) {
                    val vector = b1 - b2
                    Triple(overlap.size, s2.normalizeTo(b2 - b1), vector)
                } else null
            }
        }.let {
            it
        }.maxByOrNull {
            it.first
        }?.let { it.second to it.third }
    }
}


fun parseScanners(input: List<String>): List<Scanner> {
    val scanners = mutableMapOf<Int, MutableList<Beacon>>()
    var key: Int = 0
    input.forEach {
        if (it.contains("scanner")) {
            key = it.filter { it.isDigit() }.toInt()
            scanners[key] = mutableListOf()
        } else if (it != "") {
            scanners[key]?.add(it.split(",").map { it.toInt() }.let { (x, y, z) ->
                Beacon(x, y, z)
            })
        }
    }
    return scanners.values.toList().mapIndexed { index, scanner ->
        Scanner(index, scanner)
    }
}

data class Distance(val scanner1Num: Int, val scanner2Num: Int, val vector: Vector)

fun main() {
    fun List<Scanner>.findLink(scanner: Scanner): Pair<Scanner, Distance>? {
        forEach { currentKnownScanner ->
            val s = currentKnownScanner.match(scanner)
            if (s != null) {
                //println("Found scanner #${s.first.num}")
                return s.let { (scanner, vector) ->
                    val d = Distance(currentKnownScanner.num, scanner.num, vector)
                    scanner to d
                }
            }
        }
        return null
    }

    fun mapOcean(input: List<String>): Pair<List<Scanner>, List<Distance>> {
        val scanners = parseScanners(input)
        val knownScanners = mutableListOf<Scanner>(
            scanners.first()
        )
        val unknownScanners = scanners.takeLast(scanners.size - 1).toMutableList()
        val distances = mutableListOf<Distance>()
        while (unknownScanners.size > 0) {
            val potentialNextScanner = unknownScanners.removeAt(0)
            val foundScanner = knownScanners.findLink(potentialNextScanner)
            if (foundScanner != null) {
                knownScanners.add(foundScanner.first)
                distances.add(foundScanner.second)
            } else {
                unknownScanners.add(potentialNextScanner)
            }
        }
        return knownScanners.toList() to distances.toList()
    }

    fun part1(input: List<String>): Int {
        val (knownScanners, _) = mapOcean(input)

        val allScanners = knownScanners.reduce { acc, scanner ->
            Scanner(0, scanner.beacons.toSet().union(acc.beacons.toSet()).toList())
        }
        return allScanners.beacons.size
    }

    fun part2(input: List<String>): Int {
        val (_, distances) = mapOcean(input)

        return distances.map { it.vector }.toMutableList().let {
            it.add(Vector(0, 0, 0))
            it
        }.toList().let { scannerLocations ->
            scannerLocations.flatMap { v1 ->
                scannerLocations.filterNot { it == v1 }.map { v2 ->
                    (v2 - v1).manhattan()
                }
            }
        }.maxOf { it }
    }

    val testInput = readInput("Day19_test")
    val testInput2 = readInput("Day19_test_2")
    val testInput3 = readInput("Day19_test_3")
    val input = readInput("Day19")

    val scannersEx2 = parseScanners(testInput2)
    assert(scannersEx2.get(0).normalize().beacons.toSet() == scannersEx2.get(1).normalize().beacons.toSet())

    val scannersEx3 = parseScanners(testInput3)
    assert(scannersEx3.get(0).match(scannersEx3.get(1)) != null)

    val scanners = parseScanners(testInput).take(2)
    assert(scanners[0].match(scanners[1]) != null)

    val testSolutionPart1 = part1(testInput)
    println(testSolutionPart1)
    check(testSolutionPart1 == 79)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 326)

    val testSolutionPart2 = part2(testInput)
    println(testSolutionPart2)
    check(testSolutionPart2 == 3621)

    val solutionPart2: Int
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 10630)
}
