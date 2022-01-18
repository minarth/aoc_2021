package day22

import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min
import java.lang.Math.abs
import java.math.BigInteger
import dataDir

data class CMD(val on: Boolean, val x: IntRange, val y: IntRange, val z: IntRange)

data class Cube(val on: Boolean, val x1: Int, val x2: Int, val y1: Int, val y2: Int, val z1: Int, val z2: Int)

fun parse(lines: List<String>): List<CMD> {
    val parsedCmds = mutableListOf<CMD>()
    for (line in lines) {
        val regex = """(on|off) x=([-]?\d+)..([-]?\d+),y=([-]?\d+)..([-]?\d+),z=([-]?\d+)..([-]?\d+)""".toRegex()
        val g = regex.find(line)!!.groupValues
        parsedCmds.add(CMD(g[1] == "on",
            g[2].toInt()..g[3].toInt(),
            g[4].toInt()..g[5].toInt(),
            g[6].toInt()..g[7].toInt()))
    }
    return parsedCmds
}

fun evaluateCmdRestricted(grid: Array<Array<Array<Boolean>>>, cmd: CMD, shift: Int) {
    for (x in max(-50, cmd.x.first) .. min(50, cmd.x.last))
        for (y in max(-50, cmd.y.first) .. min(50, cmd.y.last))
            for (z in max(-50, cmd.z.first) .. min(50, cmd.z.last))
                grid [x+shift][y+shift][z+shift] = cmd.on
}

fun sumOnCubes(grid: Array<Array<Array<Boolean>>>): BigInteger {
    var ons = BigInteger.ZERO
    for (plane in grid)
        for (line in plane)
            for (cube in line)
                if (cube) ons++

    return ons
}

fun partOne(cmds: List<CMD>): BigInteger {
    val grid = Array(101) { Array(101) { Array(101) {false} } }

    for (cmd in cmds) {
        evaluateCmdRestricted(grid, cmd, 50)
    }

    return sumOnCubes(grid)
}


fun findIntersection(c1: CMD, c2: CMD): CMD? {
    //  c1 is for cubes already evaluated
    //  c2 is newly introduced cmd
    // I will return negative cube for true/true inputs, to mitigate counting intersection twice
    // also I will return neg cube for true/false, since it turns off the intersection
    // true/true -> false | so interesection is not counted twice
    // true/false -> false | so intersection is not counted at all
    // false/true -> true | to reverse -1 of this previously turned off intersection
    // false/false -> true | to not discount this intersection twice

    // ranges seems slow

    val interX = c1.x.intersect(c2.x)
    val interY = c1.y.intersect(c2.y)
    val interZ = c1.z.intersect(c2.z)
    if (interX.isNotEmpty() && interY.isNotEmpty() && interZ.isNotEmpty()) {
        return CMD(!c1.on, // && c2.on,
            interX.minOrNull()!!..interX.maxOrNull()!!,
            interY.minOrNull()!!..interY.maxOrNull()!!,
            interZ.minOrNull()!!..interZ.maxOrNull()!!)
    }

    return null
}

fun findIntersection(c1: Cube, c2: Cube): Cube? {
    //  c1 is for cubes already evaluated
    //  c2 is newly introduced cmd
    // I will return negative cube for true/true inputs, to mitigate counting intersection twice
    // also I will return neg cube for true/false, since it turns off the intersection
    // true/true -> false | so interesection is not counted twice
    // true/false -> false | so intersection is not counted at all
    // false/true -> true | to reverse -1 of this previously turned off intersection
    // false/false -> true | to not discount this intersection twice

    val x1 = max(c1.x1, c2.x1)
    val x2 = min(c1.x2, c2.x2)
    val y1 = max(c1.y1, c2.y1)
    val y2 = min(c1.y2, c2.y2)
    val z1 = max(c1.z1, c2.z1)
    val z2 = min(c1.z2, c2.z2)
    if (x1 <= x2 && y1 <= y2 && z1 <= z2)
        return Cube(!c1.on, x1, x2, y1, y2, z1, z2)

    return null
}

fun cmdToCube(cmd: CMD): Cube {
    return Cube(cmd.on, cmd.x.first, cmd.x.last, cmd.y.first, cmd.y.last, cmd.z.first, cmd.z.last)
}

fun partTwo(cmds: List<CMD>): BigInteger {
    // brute force no good
    // time to do the smart solution, hoped that it will not be necessary

    // CMD can define command as well as cube
    val cubes = mutableListOf<Cube>()
    for ((i, cmd) in cmds.withIndex()) {
        val newCubes = mutableListOf<Cube>()
        val cmdCube = cmdToCube(cmd)
        //println("Running $cmdCube ($i out of ${cmds.size})")
        for (cube in cubes) {
            //println()
            //println("Looking for intersection with $cube")
            val i = findIntersection(cube, cmdCube)
            if (i != null){
                //println("Intersection found $i")
                newCubes.add(i)
            }
        }

        cubes.addAll(newCubes)
        if (cmd.on) cubes.add(cmdCube)

    }

    var count = BigInteger.ZERO
    for (cube in cubes) {
        var x = BigInteger.valueOf(if (cube.on) 1 else -1)
        x *= BigInteger.valueOf((cube.x2 - cube.x1 + 1).toLong())
        x *= BigInteger.valueOf((cube.y2 - cube.y1 + 1).toLong())
        x *= BigInteger.valueOf((cube.z2 - cube.z1 + 1).toLong())
        count += x
    }
    return count
}

fun main() {
    var cmds = parse(File("$dataDir/day22/example.txt").readLines())
    println("Small example part one ${partOne(cmds)}")
    partTwo(cmds)

    cmds = parse(File("$dataDir/day22/example2.txt").readLines())
    println("Large example part one ${partOne(cmds)}")
    println("Large example part two ${partTwo(cmds.subList(0, cmds.size-2))}")

    cmds = parse(File("$dataDir/day22/example3.txt").readLines())
    println("The largest example part one ${partOne(cmds)}")
    println("The largest example part two ${partTwo(cmds)}")


    cmds = parse(File("$dataDir/day22/input.txt").readLines())
    println("Part one ${partOne(cmds)}")
    println("Part one ${partTwo(cmds)}")
}