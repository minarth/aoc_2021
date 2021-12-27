package day05

import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min

data class Vent(val startX: Int, val startY: Int, val endX: Int, val endY: Int)


fun readFileLineByLine(fileName: String)
        = File(fileName).readLines().map {
    val parsed = it.split(" -> ")
    val (start, end) = parsed
    val (startX, startY) = start.split(",")
    val (endX, endY) = end.split(",")
    Vent(startY.toInt(), startX.toInt(), endY.toInt(),  endX.toInt())
}


fun gridToString(grid: Array<Array<Int>>): String {
    var output: String = ""
    for ((i, line) in grid.withIndex()) {
        val lineString: MutableList<String> = MutableList(0) { "" }
        for (number in line) {
            lineString.add(number.toString());
        }

        output += lineString.joinToString("\t") + "\n"
    }
    return output
}


fun createGrid(vents: List<Vent>, diagonal: Boolean = false): Array<Array<Int>> {
    var minX: Int = vents[0].startX;
    var minY: Int = vents[0].startY;
    var maxX: Int = vents[0].startX;
    var maxY: Int = vents[0].startY;

    for (vent in vents) {
        maxX = max(maxX, max(vent.startX, vent.endX));
        minX = min(minX, min(vent.startX, vent.endX));
        maxY = max(maxY, max(vent.startY, vent.endY));
        minY = min(minY, min(vent.startY, vent.endY));
    }

    var grid: Array<Array<Int>> = Array(maxX+1) { Array(maxY+1) { 0 } }
    for (vent in vents) {
        if (vent.startX == vent.endX) {
            for (y in min(vent.startY, vent.endY)..max(vent.startY, vent.endY)) {
                val x = vent.startX
                grid[vent.startX][y] += 1
            }
        } else if (vent.startY == vent.endY) {
            for (x in min(vent.endX,vent.startX)..max(vent.startX, vent.endX)) {
                val y = vent.startY
                grid[x][vent.startY] += 1
            }
        } else if (diagonal) {
            var x = vent.startX;
            var y = vent.startY;
            grid[x][y] += 1;

            while (x != vent.endX && y != vent.endY) {
                x += if (vent.startX < vent.endX) 1 else -1
                y += if (vent.startY < vent.endY) 1 else -1
                grid[x][y] += 1
            }
        }
    }
    return grid;
}

fun part(grid: Array<Array<Int>>): Int {
    var crossings = 0
    for (line in grid)
        for (number in line)
            if (number > 1) crossings += 1

    return crossings
}

fun main() {
    val vents = readFileLineByLine("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day05/test.txt")
    val grid = createGrid(vents)
    print("Test example ")
    println(part(grid))
    print("Test example part two ")
    println(part(createGrid(vents, true)))

    val testVents = readFileLineByLine("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day05/input.txt")
    val testGrid = createGrid(testVents)
    print("Part one ")
    println(part(testGrid))
    print("Part two ")
    // 19908 too low
    println(part(createGrid(testVents, true)))
}