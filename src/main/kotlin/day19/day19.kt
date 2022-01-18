package day19

import java.io.File
import java.lang.Integer.max
import kotlin.math.abs
import dataDir

data class Point(val x: Int, val y: Int, val z: Int)

fun pToArray(p: Point) = intArrayOf(p.x, p.y, p.z)

fun parse(filename: String): MutableMap<Int, MutableList<Point>> {
    val data = File(filename).readLines()
    val scanner = mutableMapOf<Int, MutableList<Point>>()
    var currentScanner = 0
    for (line in data) {
        if (line == "") continue
        else if (line.startsWith("---")) {
            currentScanner = line.split("scanner ".toRegex())[1].split(" ".toRegex())[0].toInt()
            scanner[currentScanner] = mutableListOf()
        } else {
            val parsedLine = line.split(",".toRegex())
            val (x,y,z) = parsedLine
            scanner[currentScanner]?.add(Point(x.toInt(), y.toInt(), z.toInt()))
        }
    }

    return scanner
}

fun distance(p1: Point, p2: Point): Int {
    // calculate manhattan distance
    return abs(p1.x - p2.x) + abs(p1.y - p2.y) + abs(p1.z - p2.z)
}

fun distances(points: MutableList<Point>): Array<Array<Int>> {
    val dists: Array<Array<Int>> = Array(points.size) { Array(points.size) { 0 } }

    for ((i, p1) in points.withIndex()) {
        for ((j, p2) in points.withIndex()) {
            dists[i][j] = distance(p1, p2)
        }
    }

    return dists
}


fun matchDistances(d1: Array<Array<Int>>, d2: Array<Array<Int>>): MutableMap<Int, Int> {
    val pointMatches = mutableMapOf<Int, Int>()

    for ((i, suspect) in d1.withIndex()) {
        for ((j, matcher) in d2.withIndex()) {
            var matches = 0
            for (element in suspect) if (matcher.contains(element)) matches++
            if (matches >= 12) {
                pointMatches[i] = j
            }
        }
    }
    return pointMatches
}

fun diffPoints(p1: Point, p2: Point): Array<Int> {
    val a = Array<Int>(3) {0}
    a[0] = p1.x - p2.x
    a[1] = p1.y - p2.y
    a[2] = p1.z - p2.z

    return a
}


fun translateCoordinates(p1: List<Point>, p2: List<Point>, matches: MutableMap<Int, Int>): Pair<MutableList<Point>, Point> {
    val translate = mutableListOf(-1, -1, -1)
    val directions = mutableListOf(-1, -1, -1)
    val iterableKeys = matches.keys.toList()
    val translatedPoints = mutableListOf<Point>()
    val origin = mutableListOf(0, 0, 0)
    for (i in iterableKeys.indices) {
        val diff = diffPoints(p1[iterableKeys[i]], p1[iterableKeys[i+1]])
        if (abs(diff[0]) != abs(diff[1]) && abs(diff[1]) != abs(diff[2])) {
            val diff2 = diffPoints(p2[matches[iterableKeys[i]]!!], p2[matches[iterableKeys[i+1]]!!])

            for (x in diff.indices)
                for (y in diff2.indices)
                    if (abs(diff[x]) == abs(diff2[y])) {
                        translate[x] = y
                        directions[x] = if (diff[x] == diff2[y]) 1 else -1
                        break
                    }


            val p1a = pToArray(p1[iterableKeys[i]])
            val p2a = pToArray(p2[matches[iterableKeys[i]]!!])
            for (j in translate.indices) {
                origin[j] = p1a[j] - (p2a[translate[j]] * directions[j])
            }
            for ((k,v) in matches) {
                val p = p2[v]
                val tmpP = pToArray(p)
                val newTmpP = pToArray(p)
                for (axis in newTmpP.indices) {
                    newTmpP[axis] = (tmpP[translate[axis]] + (origin[axis] * directions[axis])) * directions[axis]
                }
            }

            for (p in p2) {
                val tmpP = pToArray(p)
                val newTmpP = pToArray(p)
                for (axis in newTmpP.indices) {
                    newTmpP[axis] = (tmpP[translate[axis]] + (origin[axis] * directions[axis])) * directions[axis]
                }

                translatedPoints.add(Point(newTmpP[0], newTmpP[1], newTmpP[2]))

            }
            break
        }
    }
    return Pair(translatedPoints, Point(origin[0], origin[1], origin[2]))
}

fun parts(scanners: MutableMap<Int, MutableList<Point>>): Pair<Int, Int> {
    val translatedScanners = mutableMapOf<Int, MutableList<Point>>()
    translatedScanners[0] = scanners[0]!!
    val explored = mutableSetOf<Int>()
    val exploreScanners = mutableListOf(0)
    val pointsToOrigin = mutableSetOf<Point>()

    val origins = mutableListOf(Point(0,0,0))

    while (exploreScanners.size > 0 && explored.size < scanners.size) {
        val i = exploreScanners.removeFirst()
        explored.add(i)
        for (j in scanners.keys) {
            if (j != i && !explored.contains(j)) {
                val iScanner = translatedScanners[i]!!
                val jScanner = if (translatedScanners.containsKey(j)) translatedScanners[j]!! else scanners[j]!!

                val m = matchDistances(distances(iScanner), distances(jScanner))
                if (m.isNotEmpty()) {
                    val (translated, newOrigin) = translateCoordinates(iScanner, jScanner, m)
                    origins.add(newOrigin)
                    translatedScanners[j] = translated
                    exploreScanners.add(j)
                }
            }
        }

    }


    for (pList in translatedScanners.values) {
        for (p in pList) {
            pointsToOrigin.add(p)
        }
    }

    val originDistances = distances(origins)
    var maximalDist = 0
    for (line in originDistances)
        for (el in line) maximalDist = max(maximalDist, el)

    return Pair(pointsToOrigin.size, maximalDist)
}

fun main() {
    var scanners = parse("$dataDir/day19/example.txt")
    matchDistances(distances(scanners[0]!!), distances((scanners[1]!!)))

    println(parts(scanners))

    scanners = parse("$dataDir/day19/input.txt")
    println(parts(scanners))
}

