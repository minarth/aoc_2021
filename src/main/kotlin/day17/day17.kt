package day17

import java.lang.Integer.max

data class Trench(val startX: Int, val endX: Int, val startY: Int, val endY: Int)

fun testConfig(x: Int, y: Int, trench: Trench): Int {
    var posX = 0
    var posY = 0
    var velX = x
    var velY = y
    var maxY = -1
    while (posY > trench.endY) {
        posX += velX
        posY += velY
        maxY = max(maxY, posY)
        if (velX != 0)
            velX += if (velX > 0) -1 else 1
        velY -= 1

//        println("$posX $posY $velX $velY")
//        println(trench.endY .. trench.startY)
//        println(posY in trench.endY .. trench.startY)
//        println(trench.startX .. trench.endX)
//        println(posX in trench.startX .. trench.endX)
        if (posY in trench.endY .. trench.startY && posX in trench.startX .. trench.endX) {
            return max(maxY, 0)
        }
    }

    return -1
}

fun partOne(trench: Trench): Int {
    var currentBest = -1
    for (x in 0 .. trench.endX) {
        for (y in 0 .. -1*trench.endY) {
            val maxY = testConfig(x, y, trench)
            if (maxY > 0) {
                currentBest = max(currentBest, maxY)
            }
        }
    }
    return currentBest
}

fun partTwo(trench: Trench) : Int {
    var count = 0
    for (x in 0 .. trench.endX) {
        for (y in trench.endY .. -1*trench.endY) {
            //println("Testing $x $y")
            if (testConfig(x, y, trench) >= 0) {
                //println("$x $y $count")
                count++
            }
        }
    }
    return count
}

fun main() {
    println("Example test")
    val trench = Trench(20, 30, -5, -10)
    println("Example part one ${partOne(trench)}")

    println("Example part two ${partTwo(trench)}")

    val inputTrench = Trench(192, 251, -59, -89)

    println("Part one ${partOne(inputTrench)}")
    println("Part two ${partTwo(inputTrench)}")
}