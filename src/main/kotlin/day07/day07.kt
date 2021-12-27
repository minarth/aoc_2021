package day07

import java.io.File
import kotlin.math.abs
import kotlin.math.min

fun readFileLineByLine(fileName: String)
        = File(fileName).readLines()

fun parse(input:String): MutableList<Int> {
    val positions: MutableList<Int> = mutableListOf<Int>()
    for (i in input.split(",")) {
        positions.add(i.toInt())
    }
    return positions
}

fun partOne(positions: MutableList<Int>): Int {
    val maximalPosition = positions.maxOrNull() ?: 0
    var minimalDistance = positions.size * maximalPosition
    for (i in 0..maximalPosition) {
        var currentDistance = 0
        for (position in positions) {
            currentDistance += abs(i - position)
        }
        minimalDistance = min(minimalDistance, currentDistance)
    }
    return minimalDistance
}


fun partTwo(positions: MutableList<Int>): Int {
    val maximalPosition = positions.maxOrNull() ?: 0
    var minimalDistance = positions.size * (1..maximalPosition).sum()
    for (i in 0..maximalPosition) {
        var currentDistance = 0
        for (position in positions) {
            val n = abs(i - position)
            currentDistance += (1..n).sum()
        }
        //println("Position $i with cost $currentDistance")
        minimalDistance = min(minimalDistance, currentDistance)
    }
    return minimalDistance
}

fun main() {
    print("Test one ")
    println(partOne(parse("16,1,2,0,4,2,7,1,2,14")))

    print("Test two ")
    println(partTwo(parse("16,1,2,0,4,2,7,1,2,14")))

    val positions = readFileLineByLine("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day07/input.txt")[0]
    print("Part one ")
    println(partOne(parse(positions)))

    print("Part two ")
    println(partTwo(parse(positions)))
}