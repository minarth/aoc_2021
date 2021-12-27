package day06

fun parse(input:String): Array<Long> {
    val counts: Array<Long> = Array(9) { 0 };
    for (i in input.split(",")) {
        counts[i.toInt()] += 1.toLong()

    }
    return counts
}

fun step(counts: Array<Long>): Array<Long> {

    val nextCounts: Array<Long> = Array(9) { 0 };
    for (timer in 8 downTo 1) {
        nextCounts[timer-1] += counts[timer]
    }
    nextCounts[8] += counts[0]
    nextCounts[6] += counts[0]

    return nextCounts;
}

fun part(counts: Array<Long>, rounds: Long): Long {
    var cnts = counts
    for (i in 0 until rounds) {
        cnts = step(cnts)
    }

    return cnts.sum()
}

fun main() {
    val counts = parse("3,4,3,1,2")
    print("Test part one ")
    println(part(counts, 80))

    print("Part one ")
    val testCounts = parse("1,1,1,1,1,5,1,1,1,5,1,1,3,1,5,1,4,1,5,1,2,5,1,1,1,1,3,1,4,5,1,1,2,1,1,1,2,4,3,2,1,1,2,1,5,4,4,1,4,1,1,1,4,1,3,1,1,1,2,1,1,1,1,1,1,1,5,4,4,2,4,5,2,1,5,3,1,3,3,1,1,5,4,1,1,3,5,1,1,1,4,4,2,4,1,1,4,1,1,2,1,1,1,2,1,5,2,5,1,1,1,4,1,2,1,1,1,2,2,1,3,1,4,4,1,1,3,1,4,1,1,1,2,5,5,1,4,1,4,4,1,4,1,2,4,1,1,4,1,3,4,4,1,1,5,3,1,1,5,1,3,4,2,1,3,1,3,1,1,1,1,1,1,1,1,1,4,5,1,1,1,1,3,1,1,5,1,1,4,1,1,3,1,1,5,2,1,4,4,1,4,1,2,1,1,1,1,2,1,4,1,1,2,5,1,4,4,1,1,1,4,1,1,1,5,3,1,4,1,4,1,1,3,5,3,5,5,5,1,5,1,1,1,1,1,1,1,1,2,3,3,3,3,4,2,1,1,4,5,3,1,1,5,5,1,1,2,1,4,1,3,5,1,1,1,5,2,2,1,4,2,1,1,4,1,3,1,1,1,3,1,5,1,5,1,1,4,1,2,1")
    println(part(testCounts, 80))

    print("Test part two ")
    println(part(counts, 256))

    print("Part two ")
    println(part(testCounts, 256))
}