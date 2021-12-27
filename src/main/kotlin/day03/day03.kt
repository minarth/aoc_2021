package day03

import java.io.File

fun readFileLineByLine(fileName: String)
        = File(fileName).readLines()

fun partOne(instructions: List<String>): Int {
    var ones = IntArray(instructions[0].length){0}
    var instrLength: Int = 0
    for (line in instructions) {
        instrLength += 1
        for ((idx, digit) in line.withIndex()) {
            if (digit == '1') {
                ones[idx] += 1
            }
        }
    }

    var gamma = IntArray(instructions[0].length){0}
    var epsilon = IntArray(instructions[0].length){0}

    for ((idx, one) in ones.withIndex()) {
        if (one >= instrLength/2) {
            gamma[idx] = 1
        } else {
            epsilon[idx] = 1
        }
    }

    val decGamma = gamma.joinToString("").toInt(2)
    val decEpsilon = epsilon.joinToString("").toInt(2)
    return decGamma * decEpsilon
}

fun getNums(instructions: List<List<Int>>, available: BooleanArray, position: Int): Boolean {
    var nums = 0
    var length = 0
    for ((idx, line) in instructions.withIndex()) {
        if (available[idx]) {
            length += 1
            nums += if (line[position] == 1) 1 else 0
        }
    }

    return nums >= (length/2.0)
}

fun partTwo(instructions: List<List<Int>>): Int {
    val oxygenGenerator = BooleanArray(instructions.size){true}
    val co2Scrubber = BooleanArray(instructions.size){true}

    // oxygen
    for (position in 0 until instructions[0].size) {
        var nums = getNums(instructions, oxygenGenerator, position)
        for ((idx, instr) in instructions.withIndex()) {
            if ((instr[position] == 0 && nums) || (instr[position] == 1 && !nums)) {
                oxygenGenerator[idx] = false
            }
        }

        if (oxygenGenerator.count{ it } == 1) {
            break
        }
    }

    // oxygen
    for (position in 0 until instructions[0].size) {
        val nums = getNums(instructions, co2Scrubber, position)
        for ((idx, instr) in instructions.withIndex()) {
            if ((instr[position] == 1 && nums) || (instr[position] == 0 && !nums)) {
                co2Scrubber[idx] = false
            }
        }

        if (co2Scrubber.count{ it } == 1) {
            break
        }
    }

    var oxygenGeneratorRating: List<Int> = List<Int>(1){0}
    var co2ScrubberRating: List<Int> = List<Int>(1){0}

    for ((idx, instr) in instructions.withIndex()) {
        if (oxygenGenerator[idx]) oxygenGeneratorRating = instr
        if (co2Scrubber[idx]) co2ScrubberRating = instr
    }

    return oxygenGeneratorRating.joinToString("").toInt(2) * co2ScrubberRating.joinToString("").toInt(2)

}

private fun <E> List<E>.reduce(operation: (acc: List<Int>, List<Int>) -> Unit) {
    // todo
}

fun main() {
    val trainInput = readFileLineByLine("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day03/test.txt")
    val testInput = readFileLineByLine("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day03/input.txt")
    println(partOne(trainInput))
    println(partOne(testInput))
    println(partTwo(trainInput.map { itLine -> itLine.map { it.toString().toInt() } }))
    println(partTwo(testInput.map { itLine -> itLine.map { it.toString().toInt() } }))
}