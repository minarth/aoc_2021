package day23

import day20.gridToString
import java.io.File
import java.lang.Math.abs
import java.util.*


// amphipods list will contain only the ones, that are not in their proper column
data class Configuration(val energy: Int, val grid: Array<Array<Char>>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Configuration

        if (!grid.contentDeepEquals(other.grid)) return false

        return true
    }

    override fun hashCode(): Int {
        return grid.contentDeepHashCode()
    }
}

data class Position(val type: Char, val x: Int, val y: Int)

val targetYs = mapOf('A' to 3, 'B' to 5, 'C' to 7, 'D' to 9)
val energies = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)

fun parse(lines: List<String>): Configuration {
    val grid = Array(3){Array(13) {' '} }
    for ((i, line) in lines.subList(1, 4).withIndex()) {
        for ((j, element) in line.withIndex())
            //if (element != '#' && !element.isWhitespace())
                grid[i][j] = element
    }
    return Configuration(0, grid)
}

// -1 if no path
// number of steps otherwise
fun openPath(grid: Array<Array<Char>>, startX: Int, startY: Int, endX: Int, endY:Int ): Int {
    var x = startX
    var y = startY
    var steps =  0
    if (startX == endX && startY == endY) return 0

    while (x > 0) {
        x--
        steps++
        if (grid[x][y] != '.') return -1
    }

    while (y > endY) {
        y--
        steps++
        if (grid[x][y] != '.') return -1
    }

    while (y < endY) {
        y++
        steps++
        if (grid[x][y] != '.') return -1
    }

    while (x < endX) {
        x++
        steps++
        if (grid[x][y] != '.') return -1
    }

    return steps
}

fun newConfiguration(c: Configuration, oldPos: Position, newPos: Position, energy: Int): Configuration {
    val grid = Array(c.grid.size){Array(c.grid[0].size) {' '} }
    for ((i, line) in c.grid.withIndex())
        for ((j, element) in line.withIndex()) {
            if (i == oldPos.x && j == oldPos.y) grid[i][j] = '.'
            else if (i == newPos.x && j == newPos.y) grid[i][j] = newPos.type
            else grid[i][j] = element
        }

    return Configuration(c.energy + energy, grid)
}

fun goToRoom(grid: Array<Array<Char>>, candidate: Position): Boolean {
    val targetY = targetYs[candidate.type]!!
    for (i in 1 until grid.size)
        if (grid[i][targetY] != '.' && grid[i][targetY] != candidate.type) return false

    return true
}

// MODIFY THE DAY 15 A-Star search
fun getNextActions(c: Configuration): List<Configuration> {
    val neighbors = mutableListOf<Configuration>()

    // for every amphi in non target position
    val amphiToMove = mutableListOf<Position>()
    for ((i, line) in c.grid.withIndex())
        for ((j, element) in line.withIndex())
            // I should also skip already well placed amphis.. trying without
            if (element.isLetter() && (openPath(c.grid, i, j, 0, j) >= 0)) {
                //println("Amphi to move ${Position(element, i, j)}")
                amphiToMove.add(Position(element, i, j))
            }

    // possible positions outside of rooms are x = 0, y = (1,2,4,6,8,9,10)
    val possiblePositionsInHall = listOf(1,2,4,6,8,10,11)
    for (candidate in amphiToMove) {
        val targetY = targetYs[candidate.type]!!
        val emptyOrRoommateInRoom = goToRoom(c.grid, candidate)
        if (emptyOrRoommateInRoom && candidate.y == targetY) continue // already in place

        val steps = openPath(c.grid, candidate.x, candidate.y, 1, targetY)
        if (emptyOrRoommateInRoom && steps > -1) {
            //println("Path to room")

            for (x in c.grid.size-1 downTo 1) {
                if (c.grid[x][targetY] == '.') neighbors.add(newConfiguration(c,
                    candidate,
                    Position(candidate.type, x, targetY),
                    (steps+(x-1)) * energies[candidate.type]!!))
            }

        } else if (candidate.x != 0) {
            // no path to room and candidate not in hallway
            for (pos in possiblePositionsInHall) {
                val path = openPath(c.grid, candidate.x, candidate.y, 0, pos)
                if (path > 0) {
                    val newC = newConfiguration(c,
                        candidate,
                        Position(candidate.type, 0, pos),
                        path * energies[candidate.type]!!)

                    neighbors.add(newC)
                }
            }
        }
    }

    return neighbors
}


fun getHeuristics(c: Configuration): Int {
    var heur = 0
    for ((i, line) in c.grid.withIndex())
        for ((j, element) in line.withIndex())
            if (element.isLetter() && j != targetYs[element]) {
                heur += (abs(j - targetYs[element]!!) + i + 1) * energies[element]!!
            }

    return heur
}

fun aStar(initConf: Configuration): Int {
    val candidates: PriorityQueue<Configuration> = PriorityQueue{ x: Configuration, y: Configuration
        -> (x.energy+getHeuristics(x)) - (y.energy+getHeuristics(y))}

    val searched = mutableSetOf<Configuration>()

    candidates.add(initConf)

    while (candidates.size > 0) {
        val candidate = candidates.poll()
        searched.add(candidate)
        if (getHeuristics(candidate) == 0) return candidate.energy

        for (neig in getNextActions(candidate)) {
            if (neig !in searched) {
                candidates.add(neig)
            }
        }

    }
    return -1
}

fun partTwo(c: Configuration): Int {
    val grid = Array(5){Array(13) {' '} }
    val firstAppend = "  #D#C#B#A#  "
    val secondAppend = "  #D#B#A#C#  "
    for ((i, line) in c.grid.withIndex())
        for ((j, element) in line.withIndex())
            if (i == 2) {
                grid[i][j] = firstAppend[j]
                grid[i+1][j] = secondAppend[j]
                grid[i+2][j] = element
            } else grid[i][j] = element

    return aStar(Configuration(0, grid))
}


fun main() {
    var amphipods = parse(File("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day23/example.txt").readLines())
    println("Example part one: ${aStar(amphipods)}")
    println("Example part two: ${partTwo(amphipods)}")

    amphipods = parse(File("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day23/input.txt").readLines())
    println("Part one: ${aStar(amphipods)}")
    println("Part two: ${partTwo(amphipods)}")
}