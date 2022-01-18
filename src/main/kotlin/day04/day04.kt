package day04

import java.io.File
import dataDir

fun readFileLineByLine(fileName: String)
        = File(fileName).readLines()

class Board(unparsed: List<String>) {
    private val board: Array<Array<Int>> = Array(5) { Array(5) { 0 } }
    private val marked: Array<Array<Boolean>> = Array(5) { Array(5) { false } }

    init {
        for ((i, line) in unparsed.withIndex()) {
            if (line.trim() != "")
                for ((j, number) in line.trim().split("\\s+".toRegex()).withIndex()) {
                    board[i][j] = number.toInt()
                }
        }
    }

    fun isFinished(): Boolean {
        for ((i, line) in marked.withIndex()) {
            if (line.all { it }) return true
        }

        for (col in 0 until 5) {
            for ((i, line) in marked.withIndex()) {
                if (!line[col]) break
                else if (i == 4) return true
            }
        }
        return false
    }

    fun mark(searchedNumber: Int) {
        for ((i, line) in board.withIndex()) {
            for ((j, number) in line.withIndex()) {
                if (number == searchedNumber) {
                    marked[i][j] = true
                    return
                }
            }
        }
    }

    override fun toString(): String {
        var output: String = ""
        for ((i, line) in board.withIndex()) {
            val lineString: MutableList<String> = MutableList(0) { "" }
            val booLineString: MutableList<String> = MutableList(0) { "" }
            for ((j, number) in line.withIndex()) {
                if (marked[i][j])
                    lineString.add("-")
                else lineString.add(number.toString())
                booLineString.add(marked[i][j].toString())
            }

            output += lineString.joinToString("\t")
            output += "\t|\t" + booLineString.joinToString("\t") + "\n"
        }
        return output
    }

    fun getScore(): Int {
        var mark = 0
        var unmark = 0
        for ((i, line) in board.withIndex()) {
            for ((j, number) in line.withIndex()) {
                if (marked[i][j]) mark += number
                else unmark += number
            }
        }
        //println("$mark * $unmark")
        return unmark
    }
}

fun parseBoards(unparsed: List<String>): List<Board> {
    val boards = mutableListOf<Board>()
    for (i in 0 until (unparsed.size - 1) / 6) {
        boards.add(Board(unparsed.slice(i*5+2+i .. (i+1)*5+1+i)))
    }

    return boards
}

fun partOne(calledNums: List<Int>, boards: List<Board>): Int {
    for (number in calledNums) {
        for (board in boards) {
            board.mark(number)
            if (board.isFinished()) {
                return board.getScore() * number
            }
        }
    }
    return -1
}

fun partTwo(calledNums: List<Int>, boards: List<Board>): Int {
    var finishedCount = 0
    var searchingBoards = boards
    for (number in calledNums) {
        val unfinishedBoards = arrayListOf<Board>()
        for (board in searchingBoards) {
            board.mark(number)
            if (board.isFinished()) {
                finishedCount += 1

                if (finishedCount == boards.size) {
                    return board.getScore() * number
                }
            } else {
                unfinishedBoards.add(board)
            }

        }
        searchingBoards = unfinishedBoards
    }
    return -1
}

fun main() {
    val trainInput = readFileLineByLine("$dataDir/day04/test.txt")
    val calledNums = trainInput[0].split(",".toRegex()).map { it.toInt() }
    val boards = parseBoards(trainInput)

    print("Test one ")
    println(partOne(calledNums, boards))
    print("Test two ")
    println(partTwo(calledNums, boards))

    val testInput = readFileLineByLine("$dataDir/day04/input.txt")
    val calledTestNums = testInput[0].split(",".toRegex()).map { it.toInt() }
    val testBoards = parseBoards(testInput)

    print("Part one ")
    println(partOne(calledTestNums, testBoards))
    print("Part two ")
    println(partTwo(calledTestNums, testBoards))
}
