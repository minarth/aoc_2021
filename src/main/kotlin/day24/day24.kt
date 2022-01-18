package day24

import java.io.File
import dataDir

data class CMD(val cmd: String, val a: String, val b: String?, val c: Int?)

fun getSecondValue(c: CMD, memory: Map<String, Int>): Int {
    if (c.c != null) return c.c
    return memory[c.b]!!
}

fun parse(lines: List<String>): MutableList<CMD> {
    val program = mutableListOf<CMD>()
    for (line in lines) {
        val regexI = """([a-z]+) ([w|x|y|z]) ([-]?\d+)""".toRegex()
        val regexCh = """([a-z]+) ([w|x|y|z]) ([w|x|y|z])""".toRegex()
        val regexInput = """([a-z]+) ([w|x|y|z])""".toRegex()
        if (regexI.find(line) != null) {
            val g = regexI.find(line)!!.groupValues
            program.add(CMD(g[1], g[2], null, g[3].toInt()))
        } else if (regexCh.find(line) != null){
            val g = regexCh.find(line)!!.groupValues
            program.add(CMD(g[1], g[2], g[3], null))
        } else {
            val g = regexInput.find(line)!!.groupValues
            program.add(CMD(g[1], g[2], null, null))
        }
    }

    return program
}

fun evaluate(program: List<CMD>, input: String): MutableMap<String, Int> {
    val memory = mutableMapOf("w" to 0, "x" to 0, "y" to 0, "z" to 0)
    var inputPointer = input.length - 1
    for (line in program) {
        when (line.cmd) {
            "inp" -> memory[line.a] = input[inputPointer--].toString().toInt()
            "add" -> memory[line.a] = memory[line.a]!! + getSecondValue(line, memory)
            "mul" -> memory[line.a] = memory[line.a]!! * getSecondValue(line, memory)
            "div" -> memory[line.a] = memory[line.a]!! / getSecondValue(line, memory)
            "mod" -> memory[line.a] = memory[line.a]!! % getSecondValue(line, memory)
            "eql" -> memory[line.a] = if (memory[line.a] == getSecondValue(line, memory)) 1 else 0
        }
        println("After $line -> $memory")
    }
    return memory
}


fun main() {
    var program = parse(File("$dataDir/day24/example.txt").readLines())
    //println(evaluate(program, "31"))

    program = parse(File("$dataDir/day24/example2.txt").readLines())
    //println(evaluate(program, "9"))

    // Eventually solved on paper

}