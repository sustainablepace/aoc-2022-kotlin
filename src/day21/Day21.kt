package day21

import readInput
import kotlin.system.measureTimeMillis

interface Die {
    val rolls: Int
    fun roll(): Int
}

class DeterministicDice : Die {
    val seq = generateSequence(1) { (it % 100) + 1 }
    override var rolls = 0

    override fun roll(): Int {
        rolls++
        return seq.take(rolls).toList().last()
    }
}

data class QuantumDice(var rolls: Int = 0) {
    fun roll(): List<Pair<QuantumDice, Int>> {
        rolls++
        return listOf(
            QuantumDice(rolls) to 1,
            QuantumDice(rolls) to 2,
            QuantumDice(rolls) to 3,
        )
    }
}

data class Player(val index: Int, val startSpace: Int, var currentSpace: Int = startSpace, var score: Int = 0)

class Game(val players: List<Player>, val die: Die, val gameEndsAtPoints: Int = 1000) {
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

data class Game2(val players: List<Player>, val die: QuantumDice, val turn: Int = 0, val gameEndsAtPoints: Int = 1000) {
    val isNotOver: Boolean get() = players.maxOf { it.score } < gameEndsAtPoints

    fun nextRound(): List<Game2> {
        return players[turn].let { p ->
            die.roll().flatMap { (secondDie, firstScore) ->
                secondDie.roll().flatMap { (thirdDie, secondScore) ->
                    thirdDie.roll().map { (finalDie, thirdScore) ->
                        val points = firstScore + secondScore + thirdScore
                        val players = players.map { it.copy() }
                        players[p.index].currentSpace = ((players[p.index].currentSpace + points - 1) % 10) + 1
                        players[p.index].score += players[p.index].currentSpace
                        val game = copy(players = players, die = finalDie, turn = if (turn == 0) 1 else 0)
                        game
                    }
                }
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
        val game = Game(players, die)
        while (game.isNotOver) {
            game.nextRound()
        }
        return players.first { it.score < game.gameEndsAtPoints }.score * die.rolls
    }

    data class Score(val score1: Int = 0, val square1: Int, val score2: Int = 0, val square2: Int)
    data class G2(val numGames: Long = 1, val score: Score) {
        val isOver: Boolean get() = score.score1 >= 21 || score.score2 >= 21
    }

    fun part2(input: List<String>): Long {
        val players = parsePlayers(input)

        var games = listOf(
            G2(
                score = Score(
                    square1 = players[0].startSpace,
                    square2 = players[1].startSpace
                )
            )
        )

        fun G2.g2Player1(occurances: Long, points: Int): G2 {
            val newSquare = (score.square1 + points - 1) % 10 + 1
            return G2(numGames * occurances, score.copy(score1 = score.score1 + newSquare, square1 = newSquare))
        }

        fun G2.g2Player2(occurances: Long, points: Int): G2 {
            val newSquare = (score.square2 + points - 1) % 10 + 1
            return G2(numGames * occurances, score.copy(score2 = score.score2 + newSquare, square2 = newSquare))
        }
        do {
            games = games.partition { it.isOver }.let { (done, ongoing) ->
                (ongoing.flatMap {
                    listOf(
                        it.g2Player1(1, 3),
                        it.g2Player1(3, 4),
                        it.g2Player1(6, 5),
                        it.g2Player1(7, 6),
                        it.g2Player1(6, 7),
                        it.g2Player1(3, 8),
                        it.g2Player1(1, 9)

                    ).partition {it.isOver}.let { (done, ongoing) ->
                        done + ongoing.flatMap {
                            listOf(
                                it.g2Player2(1, 3),
                                it.g2Player2(3, 4),
                                it.g2Player2(6, 5),
                                it.g2Player2(7, 6),
                                it.g2Player2(6, 7),
                                it.g2Player2(3, 8),
                                it.g2Player2(1, 9)
                            )
                        }
                    }
                } + done).let { doneAndOngoing ->
                    doneAndOngoing.groupBy { it.score }.let {
                        it.map {
                            G2(it.value.sumOf { it.numGames }, it.key)
                        }
                    }
                }
            }
        } while (games.any { !it.isOver })
        return games.partition { it.score.score1 >= 21 }.let { (winsP1, winsP2) ->
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
