package day20

import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min

fun gridToString(grid: Array<Array<Char>>, separator: String = "\t"): String {
    var output = ""
    for ((i, line) in grid.withIndex()) {
        val lineString: MutableList<String> = MutableList(0) { "" }
        for (number in line) {
            lineString.add(number.toString());
        }

        output += lineString.joinToString(separator) + "\n"
    }
    return output
}

fun parse(lines: List<String>, empty: Char = '.', sides: Int = 10): Array<Array<Char>> {
    val grid = Array(lines.size+sides) { Array(lines[2].length+sides) { empty } }
    for (i in lines.indices) {
        for ((j, el) in lines[i].withIndex()) grid[i+(sides/2)][j+(sides/2)] = el
    }

    return grid
}

fun pointToNumber(grid: Array<Array<Char>>, x: Int, y: Int, empty: Int = 0): Int {

    val binNum = Array(3) { Array(3) { empty } }
    var row = 0

    for (i in max(0, x-1) .. min(grid.size-1, x+1)) {
        if (i == x) row = 1

        var col = 0
        for (j in max(0, y - 1)..min(grid[0].size - 1, y + 1)) {
            if (j == y) col = 1
            binNum[row][col] = if (grid[i][j] == '#') 1 else 0
            col++
        }
        row++
    }

    var binary = ""
    for (line in binNum)
        for (element in line)
            binary += element
    return binary.toInt(2)
}

fun iteration(grid: Array<Array<Char>>, translate: String): Array<Array<Char>> {
    val newGrid = mutableListOf<String>()
    for (i in grid.indices) {
        var lineString = ""
        for (j in grid[i].indices) {
            lineString += translate[pointToNumber(grid, i, j, if (grid[0][0] == '.') 0 else 1)]
        }
        newGrid.add(lineString)
    }
    val parsedNewGrid = parse(newGrid, empty = translate[if (grid[0][0] == '.') 0 else 511], 8)
    //println(gridToString(parsedNewGrid, ""))
    return parsedNewGrid
}

fun parts(grid: Array<Array<Char>>, translate: String, iterations: Int = 2): Int {
    var iteratedGrid = grid

    for (i in 0 until iterations) {
        iteratedGrid = iteration(iteratedGrid, translate)
    }

    var counter = 0
    for (line in iteratedGrid) counter += line.count { it == '#' }

    return counter
}

fun main() {
    val exampleInput = File("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day20/example.txt").readLines()
    val exampleGrid = parse(exampleInput.subList(2, exampleInput.size))

    val translateMap = exampleInput[0]
    println("Example part one ${parts(exampleGrid, translateMap)}")
    println("Example part two ${parts(exampleGrid, translateMap, 50)}")

    val input = File("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day20/input.txt").readLines()
    val grid = parse(input.subList(2, input.size))

    val translate = input[0]
    println("Part one ${parts(grid, translate)}")
    println("Part one ${parts(grid, translate, 50)}")

}