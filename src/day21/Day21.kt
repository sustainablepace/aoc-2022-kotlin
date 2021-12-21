package day21

import readInput
import kotlin.system.measureTimeMillis

class DeterministicDice {
    private val seq = generateSequence(1) { (it % 100) + 1 }
    var rolls = 0

    fun roll(): Int {
        rolls++
        return seq.take(rolls).toList().last()
    }
}

data class Player(val index: Int, val startSpace: Int, var currentSpace: Int = startSpace, var score: Int = 0)

class GamePart1(private val players: List<Player>, private val die: DeterministicDice, val gameEndsAtPoints: Int = 1000) {
    val isNotOver: Boolean get() = players.maxOf { it.score } < gameEndsAtPoints

    fun nextRound() {
        players.forEach { p ->
            if (players.maxOf { it.score } < gameEndsAtPoints) {
                val points = die.roll() + die.roll() + die.roll()
                p.currentSpace = ((p.currentSpace + points - 1) % 10) + 1
                p.score += p.currentSpace
            }
        }
    }
}

fun parsePlayers(input: List<String>) = input.map {
    it
        .replace("Player ", "")
        .replace(" starting position: ", ",")
        .split(",")
        .let { (player, pos) ->
            Player(player.toInt() - 1, pos.toInt())
        }
}

fun main() {

    fun part1(input: List<String>): Int {
        val players = parsePlayers(input)
        val die = DeterministicDice()
        val game = GamePart1(players, die)
        while (game.isNotOver) {
            game.nextRound()
        }
        return players.first { it.score < game.gameEndsAtPoints }.score * die.rolls
    }

    data class GamePart2(val player1: Player, val player2: Player)
    data class DistinctGameCounter(val numGames: Long, val game: GamePart2) {
        val isOver: Boolean get() = game.player1.score >= 21 || game.player2.score >= 21
    }

    fun part2(input: List<String>): Long {
        val players = parsePlayers(input)

        var games = listOf(
            DistinctGameCounter(
                numGames = 1,
                game = GamePart2(
                    player1 = players[0],
                    player2 = players[1]
                )
            )
        )

        fun DistinctGameCounter.rollPlayer1(occurances: Long, points: Int): DistinctGameCounter {
            val newSquare = (game.player1.currentSpace + points - 1) % 10 + 1
            return DistinctGameCounter(numGames * occurances, game.copy(player1 = game.player1.copy(
                score = game.player1.score + newSquare,
                currentSpace = newSquare
            )))
        }

        fun DistinctGameCounter.rollPlayer2(occurances: Long, points: Int): DistinctGameCounter {
            val newSquare = (game.player2.currentSpace + points - 1) % 10 + 1
            return DistinctGameCounter(numGames * occurances, game.copy(player2 = game.player2.copy(
                score = game.player2.score + newSquare,
                currentSpace = newSquare
            )))
        }
        do {
            games = games.partition { it.isOver }.let { (done, ongoing) ->
                (ongoing.flatMap { ongoingGame ->
                    listOf(
                        ongoingGame.rollPlayer1(1, 3),
                        ongoingGame.rollPlayer1(3, 4),
                        ongoingGame.rollPlayer1(6, 5),
                        ongoingGame.rollPlayer1(7, 6),
                        ongoingGame.rollPlayer1(6, 7),
                        ongoingGame.rollPlayer1(3, 8),
                        ongoingGame.rollPlayer1(1, 9)

                    ).partition { it.isOver }.let { (done, ongoing) ->
                        done + ongoing.flatMap { ongoingGameAfterP1Rolled ->
                            listOf(
                                ongoingGameAfterP1Rolled.rollPlayer2(1, 3),
                                ongoingGameAfterP1Rolled.rollPlayer2(3, 4),
                                ongoingGameAfterP1Rolled.rollPlayer2(6, 5),
                                ongoingGameAfterP1Rolled.rollPlayer2(7, 6),
                                ongoingGameAfterP1Rolled.rollPlayer2(6, 7),
                                ongoingGameAfterP1Rolled.rollPlayer2(3, 8),
                                ongoingGameAfterP1Rolled.rollPlayer2(1, 9)
                            )
                        }
                    }
                } + done).let { doneAndOngoing ->
                    doneAndOngoing.groupBy { it.game }.let { map ->
                        map.map { mapEntry ->
                            DistinctGameCounter(mapEntry.value.sumOf { it.numGames }, mapEntry.key)
                        }
                    }
                }
            }
        } while (games.any { !it.isOver })
        return games.partition { it.game.player1.score >= 21 }.let { (winsP1, winsP2) ->
            val numWinsP1 = winsP1.sumOf { it.numGames }
            val numWinsP2 = winsP2.sumOf { it.numGames }
            if (numWinsP1 > numWinsP2) numWinsP1 else numWinsP2
        }
    }

    val testInput = readInput("day21/Day21_test")
    val input = readInput("day21/Day21")

    println(part1(testInput))
    check(part1(testInput) == 739785)

    val solutionPart1: Int
    val msPart1 = measureTimeMillis {
        solutionPart1 = part1(input)
    }
    println("$solutionPart1 ($msPart1 ms)")
    check(solutionPart1 == 556206)

    println(part2(testInput))
    check(part2(testInput) == 444356092776315L)

    val solutionPart2: Long
    val msPart2 = measureTimeMillis {
        solutionPart2 = part2(input)
    }
    println("$solutionPart2 ($msPart2 ms)")
    check(solutionPart2 == 630797200227453L)
}
