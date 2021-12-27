package day13

import java.io.File
import kotlin.math.max

data class Fold(val coordinates: Int, val xFold: Boolean)

data class Input(val grid: Array<Array<String>>, val folds: List<Fold>)

fun parse(input: List<String>): Input {
    val folds = mutableListOf<Fold>()
    var rows = 0
    var cols = 0
    var inputBreak = 0

    for ((i,line) in input.withIndex()) {
        if (line == "") {
            inputBreak = i
            break
        }
        val (x,y) = line.split(",".toRegex())
        rows = max(y.toInt(), rows)
        cols = max(x.toInt(), cols)
    }

    val grid = Array(rows+1) { Array(cols+1) { "." } }

    for ((i, line) in input.withIndex()) {
        if (i < inputBreak) {
            val (x, y) = line.split(",".toRegex())
            grid[y.toInt()][x.toInt()] = "#"
        } else if (i > inputBreak) {
            val instrs = line.split(" ".toRegex())
            val (x, coord) = instrs[2].split("=".toRegex())
            folds.add(Fold(coord.toInt(), x == "x"))
        }
    }

    return Input(grid, folds)
}

fun gridToString(grid: Array<Array<String>>, separator: String = "\t"): String {
    var output = ""
    for ((i, line) in grid.withIndex()) {
        val lineString: MutableList<String> = MutableList(0) { "" }
        for (number in line) {
            lineString.add(number);
        }

        output += lineString.joinToString(separator) + "\n"
    }
    return output
}

fun foldGrid(grid: Array<Array<String>>, fold: Fold): Array<Array<String>> {
    val foldedGrid = if (fold.xFold) {
        Array(grid.size) { Array(fold.coordinates) { "." } }
    } else {
        Array(fold.coordinates) { Array(grid[0].size) { "." } }
    }

    for((y, line) in foldedGrid.withIndex()) {
        for ((x, el) in line.withIndex()) {
            foldedGrid[y][x] = grid[y][x]
        }
    }


    if (fold.xFold) {
        for ((y,line) in grid.withIndex()) {
            for (x in fold.coordinates+1 until line.size) {
                if (grid[y][x] == "#") {
                    foldedGrid[y][fold.coordinates-(x-fold.coordinates)] = "#"
                }
            }
        }
    } else {
        for (y in fold.coordinates+1 until grid.size) {
            for (x in 0 until grid[y].size) {
                if (grid[y][x] == "#") {
                    foldedGrid[fold.coordinates-(y-fold.coordinates)][x] = "#"
                }
            }
        }
    }

    return foldedGrid
}

fun partOne(inputData: Input): Int {
    val newGrid = foldGrid(inputData.grid, inputData.folds[0])
    var count = 0
    for (line in newGrid) for (el in line) if (el == "#") count++

    return count
}

fun partTwo(inputData: Input) {
    var newGrid = inputData.grid
    for (fold in inputData.folds) {
        newGrid = foldGrid(newGrid, fold)
    }

    println(gridToString(newGrid, ""))
}

fun main() {
    val exampleInput = parse(File("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day13/example.txt").readLines())
    print("Example input ")
    println(partOne(exampleInput))

    val testInput = parse(File("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day13/input.txt").readLines())
    print("Test input ")
    println(partOne(testInput))

    println("PART TWO")
    partTwo(testInput)

}