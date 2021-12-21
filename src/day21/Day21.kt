package day21

import day21.Player.Companion.createPlayers
import readInput
import kotlin.system.measureTimeMillis

data class Player private constructor(
    val index: Int,
    val startSpace: Int,
    var currentSpace: Int = startSpace,
    var score: Int = 0
) {
    companion object {
        fun createPlayers(input: List<String>) = input.map {
            it
                .replace("Player ", "")
                .replace(" starting position: ", ",")
                .split(",")
                .let { (player, pos) ->
                    Player(player.toInt() - 1, pos.toInt())
                }
        }
    }
}

fun Player.move(points: Int): Player =
    ((currentSpace + points - 1) % 10 + 1).let { newSpace ->
        copy(
            score = score + newSpace,
            currentSpace = newSpace
        )
    }

class GamePart1(val players: List<Player>, val gameEndsAtPoints: Int = 1000) {

    class DeterministicDie {
        private val seq = generateSequence(1) { (it % 100) + 1 }
        var rolls = 0

        fun roll(): Int {
            rolls++
            return seq.take(rolls).toList().last()
        }
    }

    val die: DeterministicDie = DeterministicDie()
    val isFinished: Boolean get() = players.maxOf { it.score } >= gameEndsAtPoints

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

data class GamePart2(val player1: Player, val player2: Player)

data class DistinctGameCounter(val numIdenticalGames: Long, val game: GamePart2) {
    val isFinished: Boolean get() = game.player1.score >= 21 || game.player2.score >= 21

    fun afterPlayer1Rolled(occurences: Long, points: Int): DistinctGameCounter =
        copy(
            numIdenticalGames = numIdenticalGames * occurences,
            game = game.copy(player1 = game.player1.move(points))
        )

    fun afterPlayer2Rolled(occurences: Long, points: Int): DistinctGameCounter =
        copy(
            numIdenticalGames = numIdenticalGames * occurences,
            game = game.copy(player2 = game.player2.move(points))
        )
}

fun main() {

    fun part1(input: List<String>): Int =
        GamePart1(
            players = createPlayers(input)
        ).run {
            while (!isFinished) {
                nextRound()
            }
            players.first { it.score < gameEndsAtPoints }.score * die.rolls
        }

    fun part2(input: List<String>): Long =
        generateSequence(createPlayers(input).let { (p1, p2) ->
            listOf(
                DistinctGameCounter(
                    numIdenticalGames = 1,
                    game = GamePart2(
                        player1 = p1,
                        player2 = p2
                    )
                )
            )
        }) { games ->
            games.partition {
                it.isFinished
            }.let { (finishedGames, gamesInProgress) ->
                (finishedGames + gamesInProgress.flatMap { gameInProgress ->
                    listOf(
                        gameInProgress.afterPlayer1Rolled(1, 3),
                        gameInProgress.afterPlayer1Rolled(3, 4),
                        gameInProgress.afterPlayer1Rolled(6, 5),
                        gameInProgress.afterPlayer1Rolled(7, 6),
                        gameInProgress.afterPlayer1Rolled(6, 7),
                        gameInProgress.afterPlayer1Rolled(3, 8),
                        gameInProgress.afterPlayer1Rolled(1, 9)
                    ).partition { it.isFinished }.let { (finishedGamesAfterPlayer1Rolled, gamesInProgressAfterPlayer1Rolled) ->
                        finishedGamesAfterPlayer1Rolled + gamesInProgressAfterPlayer1Rolled.flatMap { gameInProgressAfterPlayer1Rolled ->
                            listOf(
                                gameInProgressAfterPlayer1Rolled.afterPlayer2Rolled(1, 3),
                                gameInProgressAfterPlayer1Rolled.afterPlayer2Rolled(3, 4),
                                gameInProgressAfterPlayer1Rolled.afterPlayer2Rolled(6, 5),
                                gameInProgressAfterPlayer1Rolled.afterPlayer2Rolled(7, 6),
                                gameInProgressAfterPlayer1Rolled.afterPlayer2Rolled(6, 7),
                                gameInProgressAfterPlayer1Rolled.afterPlayer2Rolled(3, 8),
                                gameInProgressAfterPlayer1Rolled.afterPlayer2Rolled(1, 9)
                            )
                        }
                    }
                }).let { allGames ->
                    allGames.groupBy { it.game }.let { groups ->
                        groups.map { (game, distinctGameCounters) ->
                            DistinctGameCounter(distinctGameCounters.sumOf { it.numIdenticalGames }, game)
                        }
                    }
                }
            }
        }.first {
            it.all { it.isFinished }
        }.let {
            it.partition { it.game.player1.score >= 21 }.let { (winsP1, winsP2) ->
                val numWinsP1 = winsP1.sumOf { it.numIdenticalGames }
                val numWinsP2 = winsP2.sumOf { it.numIdenticalGames }
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
