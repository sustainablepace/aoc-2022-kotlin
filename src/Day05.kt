data class Line(val start: Point, val end: Point) {
    fun pointsInLineOnlyHorizontal(): List<Point> {
        return if(start.x == end.x) {
            (Math.min(start.y, end.y)..Math.max(start.y, end.y)).map { y ->
                Point(start.x, y)
            }
        } else if(start.y == end.y) {
            (Math.min(start.x, end.x)..Math.max(start.x, end.x)).map { x ->
                Point(x, start.y)
            }
        } else emptyList()
    }
    fun pointsInLine(): List<Point> {
        return if(start.x == end.x) {
            (Math.min(start.y, end.y)..Math.max(start.y, end.y)).map { y ->
                Point(start.x, y)
            }
        } else if(start.y == end.y) {
            (Math.min(start.x, end.x)..Math.max(start.x, end.x)).map { x ->
                Point(x, start.y)
            }
        } else {
            assert(start.x != end.x)
            assert(start.y != end.y)
            if(start.x < end.x && start.y < end.y) {
                (0..end.x-start.x).map { d ->
                    Point(start.x+d, start.y+d)
                }
            }
            else if(start.x < end.x && start.y > end.y) {
                (0..end.x-start.x).map { d ->
                    Point(start.x+d, start.y-d)
                }
            }
            else if(start.x > end.x && start.y > end.y) {
                (0..start.x-end.x).map { d ->
                    Point(start.x-d, start.y-d)
                }
            }
            else if(start.x > end.x && start.y < end.y) {
                (0..start.x-end.x).map { d ->
                    Point(start.x-d, start.y+d)
                }
            } else throw IllegalArgumentException("should not happen")
        }
    }
}

data class Point(val x: Int, val y: Int)
fun parse(input: List<String>): List<Line> {
    val pointList = input.map { it.split("->") }
    return pointList.map { points ->
        assert(points.size == 2)
        val p1 = points[0].trim().split(",").map { it.toInt() }
        assert(p1.size == 2)
        val p2 = points[1].trim().split(",").map { it.toInt() }
        assert(p2.size == 2)
        Line(Point(p1[0], p1[1]), Point(p2[0], p2[1]))
    }
}
fun main() {
    fun part1(input: List<String>): Int {
        val lines = parse(input)
        val points = lines.flatMap { line -> line.pointsInLineOnlyHorizontal() }.groupBy { it }.count { it.value.size > 1 }
        return points
    }

    fun part2(input: List<String>): Int {
        val lines = parse(input)
        val points = lines.flatMap { line -> line.pointsInLine() }.groupBy { it }.count { it.value.size > 1 }
        return points
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    println(part1(testInput))
    check(part1(testInput) == 5)

    val input = readInput("Day05")
    println(part1(input))

    println(part2(testInput))
    check(part2(testInput) == 12)
    println(part2(input))
}
