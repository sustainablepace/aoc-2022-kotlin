typealias PlannedCourse = List<Command>

fun List<String>.plannedCourse(): PlannedCourse = map { Command.create(it) }
fun PlannedCourse.follow() = fold(Position()) { position, command -> position.follow(command) }
fun PlannedCourse.followNewInterpretation() = fold(CorrectPosition()) { position, command -> position.follow(command) }

data class Command(
    val direction: Direction,
    val x: Int
) {
    companion object {
        fun create(command: String): Command = command.split(' ').let { params ->
            assert(params.size == 2)
            val direction = when(params[0]) {
                "forward" -> Direction.FORWARD
                "down" -> Direction.DOWN
                "up" -> Direction.UP
                else -> throw java.lang.IllegalArgumentException("Invalid direction ${params[0]}")
            }
            Command(direction, params[1].toInt())
        }
    }
}

enum class Direction {
    FORWARD, UP, DOWN
}

data class Position(
    val horizontalPosition: Int = 0,
    val depth: Int = 0
) {
    fun follow(command: Command): Position = when (command.direction) {
        Direction.FORWARD -> Position(horizontalPosition + command.x, depth)
        Direction.UP -> Position(horizontalPosition, depth - command.x)
        Direction.DOWN -> Position(horizontalPosition, depth + command.x)
    }
    fun result(): Int = horizontalPosition * depth
}

data class CorrectPosition(
    val horizontalPosition: Int = 0,
    val depth: Int = 0,
    val aim: Int = 0
) {
    fun follow(command: Command): CorrectPosition = when (command.direction) {
        Direction.FORWARD -> CorrectPosition(horizontalPosition + command.x, depth + aim * command.x, aim)
        Direction.UP -> CorrectPosition(horizontalPosition, depth, aim - command.x)
        Direction.DOWN -> CorrectPosition(horizontalPosition, depth, aim + command.x)
    }
    fun result(): Int = horizontalPosition * depth
}

fun main() {
    fun part1(input: List<String>): Position = input.plannedCourse().follow()

    fun part2(input: List<String>): CorrectPosition = input.plannedCourse().followNewInterpretation()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput).result() == 150)

    val input = readInput("Day02")
    println(part1(input).result())

    check(part2(testInput).result() == 900)
    println(part2(input).result())
}
