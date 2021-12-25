// learning by reading others code.
// this solution is inspired by https://github.com/SebastianAigner/advent-of-code-2021/blob/master/src/main/kotlin/Day02.kt

package day02

import java.io.File

// this is needed so I can return two values from readLines.map
data class Command(val cmd: String, val value: Int)

fun readFileLineByLine(fileName: String)
        = File(fileName).readLines().map {
            val parsed = it.split(" ")
            val (cmd, value) = parsed   // Don't understand what would happen, if parsed.len != 2
            Command(cmd, value.toInt())
        }
fun partOne(): Int {
    val cmds = readFileLineByLine("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day02/input.txt")
    var depth: Int = 0
    var horizontal: Int = 0
    for (cmd in cmds) {
        when (cmd.cmd) {
            "up" -> depth -= cmd.value
            "down" -> depth += cmd.value
            "forward" -> horizontal += cmd.value
        }
    }

    return depth*horizontal
}

fun partTwo(): Int {
    val cmds = readFileLineByLine("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day02/input.txt")
    var depth: Int = 0
    var horizontal: Int = 0
    var aim: Int = 0

    for (cmd in cmds) {
        when (cmd.cmd) {
            "up" -> aim -= cmd.value
            "down" -> aim += cmd.value
            "forward" -> {
                horizontal += cmd.value
                depth += aim * cmd.value
            }
        }
    }
    return depth*horizontal
}

fun main() {
    println(partOne())
    println(partTwo())
}