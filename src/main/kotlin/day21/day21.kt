package day21

import java.io.File
import java.lang.Integer.min
import java.math.BigInteger

class DeterministicDice {
    var value = 1
    var rolls = 0
    fun getNext(): Int {
        val currentValue = value
        value = if (value == 100) 1 else value+1
        rolls++
        return currentValue
    }
}

val searched = mutableMapOf<GameState, Pair<BigInteger, BigInteger>>()

data class GameState(val p1: Int, val s1: Int, val p2: Int, val s2: Int, val p1Turn: Boolean)

fun parse(filename: String): Pair<Int, Int> {
    val lines = File(filename).readLines()
    return Pair(lines[0].last().toString().toInt(), lines[1].last().toString().toInt())
}

fun partOne(s1: Int, s2: Int): Int {
    var score1 = 0
    var score2 = 0
    var p1 = s1-1
    var p2 = s2-1
    val dice = DeterministicDice()
    while (score2 < 1000) {
        for (i in 0 .. 2) {
            p1 += dice.getNext()
        }
        p1 %= 10
        score1 += p1 + 1
        if (score1 >= 1000) break
        for (i in 0 .. 2) {
            p2 += dice.getNext() % 10
        }
        p2 %= 10
        score2 += p2 + 1
    }

    return min(score1, score2) * dice.rolls
}

fun oneRound(p1: Int, s1: Int, p2: Int, s2: Int, p1Turn: Boolean): Pair<BigInteger, BigInteger> {
    if (s1 >= 21) {
        return Pair(BigInteger.ONE, BigInteger.ZERO)
    }
    else if (s2 >= 21) {
        return Pair(BigInteger.ZERO, BigInteger.ONE)
    }

    // if state in searched -> return val
    if (searched.containsKey(GameState(p1, s1, p2, s2, p1Turn))) return searched[GameState(p1, s1, p2, s2, p1Turn)]!!
    if (searched.containsKey(GameState(p2, s2, p1, s1, !p1Turn))) {
        val p = searched[GameState(p2, s2, p1, s1, !p1Turn)]!!
        return Pair(p.second, p.first)
    }

    var wins1 = BigInteger.ZERO
    var wins2 = BigInteger.ZERO
    val possibleAdditions = listOf(3, 4, 5, 6, 7, 8, 9)
    val multipliers = listOf(1, 3, 6, 7, 6, 3, 1)


        possibleAdditions.zip(multipliers).forEach {
                (a, m) -> run{
                    val r: Pair<BigInteger, BigInteger> = if (p1Turn) {
                        oneRound((p1+a)%10, s1+((p1+a)%10)+1, p2, s2, false)
                    } else {
                        oneRound(p1, s1, (p2+a)%10, s2+((p2+a)%10)+1, true)
                    }
                    val multiplier = BigInteger("$m")
                    wins1 += r.first.times(multiplier)
                    wins2 += r.second.times(multiplier)
                }
        }

    searched[GameState(p1, s1, p2, s2, p1Turn)] = Pair(wins1, wins2)
    return Pair(wins1, wins2)
}

fun partTwo(p1: Int, p2: Int): BigInteger {
    val r = oneRound(p1-1, 0, p2-1, 0, true)
    return r.first.max(r.second)
}



fun main() {
    val exampleStarts = parse("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day21/example.txt")
    println("Example part one ${partOne(exampleStarts.first, exampleStarts.second)}")
    println("Example part two ${partTwo(exampleStarts.first, exampleStarts.second)}")


    val inputStarts = parse("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day21/input.txt")
    println("Part one ${partOne(inputStarts.first, inputStarts.second)}")
    println("Part two ${partTwo(inputStarts.first, inputStarts.second)}")
}