package day15

import dataDir
import day09.Point
import day09.parseToGrid
import java.io.File
import java.util.*

data class SortedPoint(val x: Int, val y: Int, var cost: Int, var heuristics: Int) {
    constructor(x: Int, y: Int) : this(x, y, 0, 0)
}

fun getNeighbours(grid: Array<Array<Int>>, point: SortedPoint, end: Point): List<SortedPoint> {
    val neighbors = mutableListOf<SortedPoint>()
    if (point.x > 0) neighbors.add(SortedPoint(point.x-1, point.y))
    if (point.x < grid.size - 1) neighbors.add(SortedPoint(point.x+1, point.y))
    if (point.y > 0) neighbors.add(SortedPoint(point.x, point.y-1))
    if (point.y < grid[0].size - 1) neighbors.add(SortedPoint(point.x, point.y+1))

    for (n in neighbors) {
        n.cost = point.cost + grid[n.x][n.y]
        n.heuristics = getHeuristics(n, end)
    }

    return neighbors
}

fun getHeuristics(point: SortedPoint, end: Point): Int {
    return kotlin.math.abs(point.x - end.x) + kotlin.math.abs(point.y - end.y)
}

fun aStar(grid: Array<Array<Int>>, start: SortedPoint, end: Point): Int {
    val candidates: PriorityQueue<SortedPoint> = PriorityQueue{x: SortedPoint, y:SortedPoint -> (x.cost+x.heuristics) - (y.cost+y.heuristics)}

    val searched = mutableSetOf<Point>()

    candidates.add(start)

    while (candidates.size > 0) {
        val candidate = candidates.poll()
        searched.add(Point(candidate.x, candidate.y))

        if (candidate.x == end.x && candidate.y == end.y) return candidate.cost

        for (neig in getNeighbours(grid, candidate, end)) {
            if (Point(neig.x, neig.y) !in searched) {
                candidates.add(neig)
            }
        }

    }

    return -1
}

fun aStar(grid: Array<Array<Int>>): Int {
    val start = SortedPoint(0, 0)
    val end = Point(grid.size-1, grid[0].size-1)
    start.cost = 0
    start.heuristics = getHeuristics(start, end)

    return aStar(grid, start, end)
}


fun generateFullGrid(grid: Array<Array<Int>>): Array<Array<Int>> {
    val fullGrid = Array(grid.size*5) { Array(grid[0].size*5) { 0 } }
    for ((i, line) in grid.withIndex())
        for ((j, num) in line.withIndex()) {
            for (xExpansion in 0..4) {
                for (yExpansion in 0 .. 4) {
                    if (xExpansion == 0 && yExpansion == 0)
                        fullGrid[i][j] = num
                    else {
                        val newNum = (num + xExpansion + yExpansion) % 9
                        fullGrid[xExpansion*grid.size + i][yExpansion*grid[0].size + j] = if (newNum == 0) 9 else newNum
                    }
                }
            }
        }
    return fullGrid
}

fun main() {
    val exampleGrid = parseToGrid(File("$dataDir/day15/example.txt").readLines())

    println("Example part one ${aStar(exampleGrid)}")
    println("Example part two ${aStar(generateFullGrid(exampleGrid))}")

    val inputGrid = parseToGrid(File("$dataDir/day15/input.txt").readLines())

    println("Part one ${aStar(inputGrid)}")
    println("Part two ${aStar(generateFullGrid(inputGrid))}")

}