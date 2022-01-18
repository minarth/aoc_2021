package day10

import java.io.File
import dataDir

data class Score(val corrupted: Int, val incomplete: Long)

fun check(line: String): Score {
    val stack = mutableListOf<Char>()
    for (ch in line) {
        if (ch == ')') {
            val last = stack.removeLast()
            if (last != '(') return Score(3, 0)
        } else if (ch == '>') {
            val last = stack.removeLast()
            if (last != '<') return Score(25137, 0)
        } else if (ch == ']') {
            val last = stack.removeLast()
            if (last != '[') return Score(57, 0)
        } else if (ch == '}') {
            val last = stack.removeLast()
            if (last != '{') return Score(1197, 0)
        } else {
            stack.add(ch)
        }
    }

    var incomplete = 0L
    for (ch in stack.reversed()) {
        incomplete = incomplete*5 + when (ch) {
            '(' -> 1
            '[' -> 2
            '{' -> 3
            '<' -> 4
            else -> 0
        }
    }
    return Score(0, incomplete)
}

fun part(data: List<String>): Score {
    var corrupted = 0
    val incomplete = mutableListOf<Long>()
    for ((i, line) in data.withIndex()) {
        val lineScore = check(line)
        corrupted += lineScore.corrupted
        if (lineScore.incomplete > 0) incomplete.add(lineScore.incomplete)

    }
    return Score(corrupted, incomplete.sorted()[incomplete.size/2])
}

fun main() {
    val exampleData = File("$dataDir/day10/example.txt").readLines()
    print("Example input ")
    println(part(exampleData))

    val inputData = File("$dataDir/day10/input.txt").readLines()
    print("Real input ")
    println(part(inputData))
}