package day08

import java.io.File
import dataDir

data class Input(val signals: List<String>, val outputs: List<String>)

fun readFileLineByLine(fileName: String)
        = File(fileName).readLines().map{
            val (signals, output) = it.split(" | ")
            Input(signals.split(" "), output.split(" "))
}

fun partOne(data: List<Input>): Int {
    var count = 0
    for (input in data) {
        for (out in input.outputs) {
            if (out.length in listOf(2, 3, 4, 7)) count += 1
        }
    }

    return count
}

// https://stackoverflow.com/a/55637363
fun String.alphabetized() = String(toCharArray().apply { sort() })

fun decodeLine(signals: List<String>, outputs: List<String>): Int {
    val digToStrOptions: MutableMap<Int, MutableList<String>> = mutableMapOf()
    val digToStr: MutableMap<Int, String> = mutableMapOf()

    for (i in 0..9) digToStrOptions[i] = mutableListOf()
    for (signal in signals) {
        when (signal.length) {
            2 -> digToStr[1] = signal
            3 -> digToStr[7] = signal
            4 -> digToStr[4] = signal
            5 -> {
                digToStrOptions[2]?.add(signal)
                digToStrOptions[3]?.add(signal)
                digToStrOptions[5]?.add(signal)
            }
            6 -> {
                digToStrOptions[0]?.add(signal)
                digToStrOptions[6]?.add(signal)
                digToStrOptions[9]?.add(signal)
            }
            7 -> digToStr[8] = signal
        }
    }

    for (opt in digToStrOptions[3]!!) {
        if (opt.contains(digToStr[1]!![0]) && opt.contains(digToStr[1]!![1])) {
            digToStr[3] = opt
            digToStrOptions[2]!!.remove(opt)
            digToStrOptions[5]!!.remove(opt)
            break
        }
    }

    for (opt in digToStrOptions[6]!!) {
        if (!(opt.contains(digToStr[1]!![0]) && opt.contains(digToStr[1]!![1]))) {
            digToStr[6] = opt
            digToStrOptions[0]!!.remove(opt)
            digToStrOptions[9]!!.remove(opt)
            break
        }
    }

    var c: String = ""
    for (ch in "abcdefg"){
        if (!digToStr[6]!!.contains(ch)) {
            c = ch.toString()
            break
        }
    }
    var b: String = ""
    for (ch in digToStr[4]!!){
        if (!digToStr[3]!!.contains(ch)) {
            b = ch.toString()
            break
        }
    }
    var d: String = ""
    for (ch in digToStr[4]!!){
        if (!digToStr[1]!!.contains(ch) && ch.toString() != b) {
            d = ch.toString()
            break
        }
    }

    for (opt in digToStrOptions[2]!!) {
        if (opt.contains(c)) {
            digToStr[2] = opt
        } else {
            digToStr[5] = opt
        }
    }

    for (opt in digToStrOptions[9]!!) {
        if (opt.contains(d)) {
            digToStr[9] = opt
        } else {
            digToStr[0] = opt
        }
    }

    val str2dig = mutableMapOf<String, Int>()
    for ((digit, code) in digToStr) {
        str2dig[code.alphabetized()] = digit
    }


    var value = 0
    for (output in outputs) {
        value = (value*10) + str2dig[output.alphabetized()]!!
    }
    return value
}

fun partTwo(data: List<Input>): Int {
    var sum = 0
    for (d in data) {
        sum += decodeLine(d.signals, d.outputs)
    }
    return sum
}

fun main() {
    val trainData = readFileLineByLine("$dataDir/day08/test.txt")
    print("Test part one ")
    println(partOne(trainData))

    print("Test part two ")
    println(partTwo(trainData))

    val testData = readFileLineByLine("$dataDir/day08/input.txt")
    print("Part one ")
    println(partOne(testData))

    print("Part two ")
    println(partTwo(testData))
}