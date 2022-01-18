package day09

import java.io.File
import dataDir

fun readFileLineByLine(fileName: String)
        = File(fileName).readLines()

data class Point(val x: Int, val y: Int)

fun parseToGrid(parsedData: List<String>): Array<Array<Int>> {
    val grid: Array<Array<Int>> = Array(parsedData.size) { Array(parsedData[0].length) { 0 } }
    for ((x, line) in parsedData.withIndex())
        for ((y, num) in line.withIndex()) {
            grid[x][y] = num.toString().toInt()
        }
    return grid
}

fun getRisks(grid: Array<Array<Int>>): List<Point> {
    val risks = mutableListOf<Point>()
    for ((i, line) in grid.withIndex()) {
        for ((j, num) in line.withIndex()) {
            // left
            if (j >= 1 && num >= grid[i][j-1]) continue
            // right
            if (j+1 < line.size && num >= grid[i][j+1]) continue
            // up
            if (i >= 1 && num >= grid[i-1][j]) continue
            //down
            if (i+1 < grid.size && num >= grid[i+1][j]) continue
            risks.add(Point(i,j))
        }
    }

    return risks
}

fun partOne(grid: Array<Array<Int>>): Int {
    var calculatedRisk = 0
    for (risk in getRisks(grid)) {
        calculatedRisk += grid[risk.x][risk.y]+1
    }

    return calculatedRisk
}

fun explorePoint(grid: Array<Array<Int>>, point: Point): Int {
    val stack = mutableListOf(point)
    val searched = mutableSetOf<Point>()

    while (stack.size > 0) {
        val current = stack.removeAt(0)
        searched.add(current)
        // UP
        var p = Point(current.x - 1, current.y)
        if (current.x > 0 && !searched.contains(p) && grid[p.x][p.y] != 9) stack.add(p)
        // DOWN
        p = Point(current.x+1, current.y)
        if (current.x + 1 < grid.size  && !searched.contains(p) && grid[p.x][p.y] != 9) stack.add(p)
        // LEFT
        p = Point(current.x, current.y-1)
        if (current.y > 0 && !searched.contains(p) && grid[p.x][p.y] != 9) stack.add(p)
        // RIGHT
        p = Point(current.x, current.y+1)
        if (current.y + 1 < grid[0].size && !searched.contains(p) && grid[p.x][p.y] != 9) stack.add(p)
    }

    return searched.size
}

fun partTwo(grid: Array<Array<Int>>): Int {
    val risks = getRisks(grid)
    val sizes = mutableListOf<Int>()

    for (risk in risks)  {
        val size = explorePoint(grid, risk)
        sizes.add(size)
    }

    val sortedSizes = sizes.sortedDescending()
    var basins = 1
    for (i in 0..2) {
        basins *= sortedSizes[i]
    }
    return basins
}


fun main() {
    val trainGrid = readFileLineByLine("$dataDir/day09/test.txt")
    print("Test part one ")
    println(partOne(parseToGrid(trainGrid)))

    print("Test part two ")
    println(partTwo(parseToGrid(trainGrid)))

    val testGrid = readFileLineByLine("$dataDir/day09/input.txt")
    print("Part one ")
    println(partOne(parseToGrid(testGrid)))

    print("Part two ")
    println(partTwo(parseToGrid(testGrid)))
}