package day14

import java.io.File
import java.util.Collections.max
import java.util.Collections.min

data class Input(val template: List<Char>, val rules: Map<CharPair, Char>)
data class CharPair(val a: Char, val b: Char)

fun countChars(input: List<Char>): MutableMap<Char, Long> {
    val charsMap = mutableMapOf<Char, Long>()
    input.forEach{
        charsMap[it] = charsMap.getOrDefault(it, 0) + 1
    }
    return charsMap
}

fun mergeCounters(cntrA: Map<Char, Long>, cntrB: MutableMap<Char, Long>): MutableMap<Char, Long> {
    for ((k,v) in cntrA) {
        cntrB[k] = v + cntrB.getOrDefault(k, 0)
    }
    return cntrB
}

fun parse(input: List<String>): Input {
    val rules = mutableMapOf<CharPair, Char>()
    for (i in 2 until input.size) {
        val (from, to) = input[i].split(" -> ".toRegex())
        rules[CharPair(from[0], from[1])] = to[0]
    }
    return Input(input[0].toList(), rules)
}

fun step(template: List<Char>, rules: Map<CharPair, Char>, depth: Int): MutableMap<Char, Long> {
    if (depth == 0) {
        val cntr = countChars(template)
        cntr[template.last()] = cntr.getOrDefault(template.last(), 0) - 1
        return cntr
    }
    var stepCntr = mutableMapOf<Char, Long>()
    for (i in 0 until template.size-1) {
        val a = template[i]
        val b = template[i+1]
        if (depth == 40) {
            println("Expanding $a$b of $template")
        }
        val cntr = mergeCounters(
            step(listOf(a, rules[CharPair(a,b)]!!), rules, depth-1),
            step(listOf(rules[CharPair(a,b)]!!, b), rules, depth-1)
            )
        stepCntr = mergeCounters(cntr, stepCntr)
    }

    return stepCntr
}

fun partOne(inputData: Input, steps: Int): Long {
    val template =  inputData.template
    val counts = step(template, inputData.rules, steps)
    counts[template.last()] = counts.getOrDefault(template.last(), 0) + 1

    val maxCount = max(counts.values)
    val minCount = min(counts.values)

    return maxCount - minCount
}

fun partTwo(inputData: Input, steps: Int): Long {
    val counter = mutableMapOf<Char, Long>()
    var template = mutableMapOf<CharPair, Long>()

    for (i in 0 until inputData.template.size-1) {
        val a = inputData.template[i]
        val b = inputData.template[i+1]
        val p = CharPair(a,b)
        template[p] = template.getOrDefault(p, 0) + 1
        counter[a] = counter.getOrDefault(a, 0) + 1
    }
    counter[inputData.template.last()] = counter.getOrDefault(inputData.template.last(), 0) + 1

    for (i in 0 until steps) {
        val newTemplate = mutableMapOf<CharPair, Long>()
        for ((k, v) in template) {
            val insertion = inputData.rules[CharPair(k.a, k.b)]
            val p1 = CharPair(k.a, insertion!!)
            val p2 = CharPair(insertion!!, k.b)
            newTemplate[p1] = newTemplate.getOrDefault(p1, 0L) + v
            newTemplate[p2] = newTemplate.getOrDefault(p2, 0L) + v
            counter[insertion] = counter.getOrDefault(insertion, 0) + v
        }
        template = newTemplate
    }

    return max(counter.values) - min(counter.values)
}

fun main() {
    val exampleData = parse(File("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day14/example.txt").readLines())
    println("Example")
    println("Part one ${partOne(exampleData, 10)}")

    val testData = parse(File("/home/martin/Development/hobby/aoc_2021/src/main/kotlin/day14/input.txt").readLines())
    println("TEST")
    println("Part one ${partOne(testData, 10)}")

    // After failing with strings and recursion and meditating on this for few hours, I went to subreddit to find inspiration
    // I got it mostly from python solutions (sadly, kotlin ones are unreadable for me)
    // It's even more sad I use this approach already on day 6
    println("Part two ${partTwo(testData, 40)}")

}