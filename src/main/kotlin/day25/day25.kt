package day25

import dataDir
import java.io.File

/**
 * You can edit, run, and share this code.
 * play.kotlinlang.org
 */

fun parse(lines: List<String>):Array<Array<Char>> {

    val grid = Array(lines.size){Array(lines[0].length){'.'}}

    for ((i, line) in lines.withIndex()) {
        for ((j, element) in line.withIndex()) grid[i][j] = element
    }
    return grid
}

fun step(grid: Array<Array<Char>>): Array<Array<Char>> {
    val nextGrid = Array(grid.size){Array(grid[0].size){'.'}}
    // solve the >
    for ((i, line) in grid.withIndex()) {
        //printGrid(nextGrid)
        //println()
        for ((j, element) in line.withIndex())  {
            if (element == '.') continue
            if (element == 'v') {
                nextGrid[i][j] = element
                continue
            }
            val nextCell = (j+1) % line.size
            //println("$i $j $nextCell $element ${grid[i][nextCell]} ${nextGrid[i][nextCell]}")
            if (grid[i][nextCell] != '.') nextGrid[i][j] = element
            else {
                //println("Putting > into $i $nextCell")
                nextGrid[i][nextCell] = element
                //nextGrid[i][j] = '.'
            }
        }
    }

    //printGrid(nextGrid)
    //println()

    // solve the v
    for ((i, line) in grid.withIndex()) {
        for ((j, element) in line.withIndex())  {
            if (element != 'v') continue
            val nextCell = (i+1) % grid.size
            if (nextGrid[nextCell][j] == '.' && grid[nextCell][j] != 'v') {
                nextGrid[nextCell][j] = element
                nextGrid[i][j] = '.'
            }
        }
    }

    return nextGrid
}

fun printGrid(grid: Array<Array<Char>>) {
    println("---------------")
    for (line in grid) {
        for (element in line) {
            print(element)
        }
        print("\n")
    }
    println("---------------")
}

fun compareGrids(g1: Array<Array<Char>>, g2: Array<Array<Char>>): Boolean {
    for ((l1, l2) in g1 zip g2)
        for ((e1, e2) in l1 zip l2)
            if (e1 != e2) return false
    return true
}

fun partOne(grid: Array<Array<Char>>): Int {
    var steps = 1
    var oldGrid = grid
    var newGrid = step(grid)

    while(!compareGrids(newGrid, oldGrid)) {
        oldGrid = newGrid
        newGrid = step(oldGrid)
        steps++
    }
    return steps
}

fun main() {
    var grid = parse(File("$dataDir//day25/example.txt").readLines())
    println("Example part one ${partOne(grid)}")

    grid = parse(File("$dataDir//day25/input.txt").readLines())
    println("Part one ${partOne(grid)}")
}