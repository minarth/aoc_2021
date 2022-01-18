package day11

import day09.Point
import java.io.File
import dataDir

fun parse(input: List<String>): Array<Array<Int>> {
    val grid: Array<Array<Int>> = Array(10) { Array(10) { 0 } }
    for ((i, line) in input.withIndex())
        for ((j, num) in line.withIndex()) {
            grid[i][j] = num.toString().toInt()
        }

    return grid
}

fun incrementSurrounding(grid: Array<Array<Int>>, p: Point):  Set<Point>{
    // https://stackoverflow.com/a/5802694
    val startPosX: Int = if (p.x - 1 < 0) p.x else p.x - 1
    val startPosY: Int = if (p.y - 1 < 0) p.y else p.y - 1
    val endPosX: Int = if (p.x + 1 > 9) p.x else p.x + 1
    val endPosY: Int = if (p.y + 1 > 9) p.y else p.y + 1
    val flash = mutableSetOf<Point>()
    for (i in startPosX..endPosX) {
        for (j in startPosY..endPosY) {
            grid[i][j] += 1
            if (grid[i][j] > 9) flash.add(Point(i,j))
        }
    }
    return flash
}

fun step(grid: Array<Array<Int>>): Int {
    val searched = mutableSetOf<Point>()
    var flash = mutableSetOf<Point>()

    for ((i, line) in grid.withIndex()) {
        for ((j, num) in line.withIndex()) {
            grid[i][j] = num + 1
            if (num + 1 > 9) flash.add(Point(i, j))
        }
    }

    while (flash.size > 0) {
        val newPoints = mutableSetOf<Point>()
        for (f in flash) {
            if (!searched.contains(f)) {
                newPoints.addAll(incrementSurrounding(grid, f))
                searched.add(f)
            }
        }

        newPoints.filter { !searched.contains(it) }
        flash = newPoints
    }
    var flashes = 0
    for ((i, line) in grid.withIndex()) {
        for ((j, num) in line.withIndex()) {
            if (num > 9) {
                grid[i][j] = 0
                flashes++
            }
        }
    }

    return flashes
}

fun partOne(grid: Array<Array<Int>>): Int {
    var flashes = 0
    for (i in 0 until 100) {
        flashes += step(grid)
    }
    return flashes
}

fun partTwo(grid: Array<Array<Int>>): Int {
    var i = 0
    while (step(grid) != 100) {
        i++
    }
    return i+1
}

fun main() {
    val exampleData = File("$dataDir/day11/example.txt").readLines()
    print("Example data ")
    println(partOne(parse(exampleData)))
    print("Example part two ")
    println(partTwo(parse(exampleData)))

    val testData = File("$dataDir/day11/input.txt").readLines()
    print("Part one ")
    println(partOne(parse(testData)))
    print("Part two ")
    println(partTwo(parse(testData)))
}