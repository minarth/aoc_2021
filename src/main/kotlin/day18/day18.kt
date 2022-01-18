package day18

import java.io.File
import java.lang.Long.max
import dataDir

// so this is basically unbalanced binary tree with some "weird" operations
// god I miss python

data class Edge(var value: Int?, var number: Number?) {
    constructor(value: Int) : this(value, null)
    constructor(number: Number) : this(null, number)

    override fun toString(): String {
        if (value != null) return value.toString()
        if (number != null) return number.toString()
        return "#"
    }
}

class Number(var left: Edge, var right: Edge){
    // The NODE class
    fun addValueLeft(value: Int) {
        var searched = this
        while (searched.left.value == null) searched = searched.left.number!!
        searched.left.value = searched.left.value!! + value
    }

    fun addValueRight(value: Int) {
        var searched = this
        while (searched.right.value == null) searched = searched.right.number!!
        searched.right.value = searched.right.value!! + value
    }

    fun getValues(): Pair<Int, Int> {
        return Pair(left.value!!, right.value!!)
    }

    override fun toString(): String {
        return "[$left,$right]"
    }
}

class Tree {
    var parents = mutableMapOf<Number, Number>() // child -> parent
    var root = Number(Edge(null, null), Edge(null, null))
    var actionHappened: Boolean = false

    fun initialize(input: List<Char>) {
        val parsed = parseInput(input)
        root = Number(parsed.first, parsed.second)
        parents = mutableMapOf<Number, Number>()
        fillParents(root)
    }

    fun parseValue(input: List<Char>): Int {
        // println("Parsing value ${input.joinToString("")}")
        for (ch in input) {
            if (ch.isDigit()) return ch.toString().toInt()
        }
        return -1
    }

    fun parseInput(input: List<Char>): Pair<Edge, Edge> {
        if (input.size == 5) {
            return Pair(Edge(input[1].toString().toInt()), Edge(input[3].toString().toInt()))
        }

        var bracketsCount = 0
        var left: Edge = Edge(-2)
        var right: Edge = Edge(-2)
        for ((i, char) in input.withIndex()) {
            if (char == '[') bracketsCount++
            if (char == ']') bracketsCount--
            if (char == ',' && bracketsCount == 1) {
                // println("Parsing input ${input.joinToString("")}")
                if (i >= 5) {
                    val pi = parseInput(input.subList(1, i))
                    left = Edge(Number(pi.first, pi.second))
                }
                else {
                    left = Edge(parseValue(input.subList(1, i)))
                }
                if (input.size - (i+1) >= 5) {
                    val pi = parseInput(input.subList(i + 1, input.size-1))
                    right = Edge(Number(pi.first, pi.second))
                } else {
                    right = Edge(parseValue(input.subList(i+1, input.size-1)))
                }
            }
        }
        return Pair(left, right)
    }

    fun fillParents(node: Number) {
        if (node.left.number != null) {
            parents[node.left.number!!] = node
            fillParents(node.left.number!!)
        }

        if (node.right.number != null) {
            parents[node.right.number!!] = node
            fillParents(node.right.number!!)
        }
    }

    fun refillParents() {
        parents = mutableMapOf<Number, Number>()
        fillParents(root)
    }

    fun rightAdd(addition: String) = rightAdd(addition.toList())

    fun rightAdd(addition: List<Char>) {
        val parsed = parseInput(addition)
        rightAdd(Number(parsed.first, parsed.second))
    }

    fun rightAdd(addition: Number) {
        // Operation of ROOT + addition
        val newRoot = Number(Edge(root), Edge(addition))
        parents[root] = newRoot
        parents[addition] = newRoot
        root = newRoot
        reduce()
    }

    fun reduce() {
        do {
            //println("Reducing $root")
            actionHappened = false
            explode(root, 0)
            split(root)
            refillParents()
        } while(actionHappened)
    }

    fun explodeLeft(node: Number, value: Int) {
        val searched = mutableSetOf<Number>(node)
        // go up until you find possible left edge to explore and add on the most right side
        var currentNode: Number? = parents[node]
        while (currentNode != null) {
            //println("Current node $currentNode")
            //println(currentNode.left.number != null)
            //println(!searched.contains(currentNode.left.number))
            //println((currentNode.left.value != null))
            if (currentNode.left.number != null && !searched.contains(currentNode.left.number)) {
                currentNode.left.number!!.addValueRight(value)
                break
            } else if (currentNode.left.value != null) {
                currentNode.left.value = currentNode.left.value!! + value
                break
            }
            searched.add(currentNode)
            currentNode = parents[currentNode]
        }
    }

    fun explodeRight(node: Number, value: Int) {
        // this LEFT/RIGHT stuff has to have better form of notation/passing left/right as argument or something.
        val searched = mutableSetOf<Number>(node)
        // go up until you find possible RIGHT edge to explore and add on the most LEFT side
        var currentNode: Number? = parents[node]
        while (currentNode != null) {
            if (currentNode.right.number != null && !searched.contains(currentNode.right.number)) {
                currentNode.right.number!!.addValueLeft(value)
                break
            } else if (currentNode.right.value != null) {
                currentNode.right.value = currentNode.right.value!! + value
                break
            }
            searched.add(currentNode)
            currentNode = parents[currentNode]
        }
    }

    fun explode(node: Number, depth: Int) {

        if (actionHappened) return

        if (depth == 3) {
            // if left number found, explode and return to the root to traverse back
            if (node.left.number != null && !actionHappened) {
                //println("Left explosion ${node.left} ${node}")

                val values = node.left.number!!.getValues()
                if (node.right.value != null)
                    node.right.value = node.right.value!! + values.second
                else
                    node.right.number!!.addValueLeft(values.second)
                explodeLeft(node, values.first)
                node.left = Edge(0)
                actionHappened = true
            }

            if (node.right.number != null && !actionHappened) {
                //println("Right explosion ${node.right}")
                val values = node.right.number!!.getValues()
                if (node.left.value != null)
                    node.left.value = node.left.value!! + values.first
                else
                    node.left.number!!.addValueRight(values.first)

                //node.addValueLeft(values.first)
                explodeRight(node, values.second)
                node.right = Edge(0)
                actionHappened = true
            }
        } else {
            if (node.left.number != null) {
                explode(node.left.number!!, depth+1)
            }
            if (node.right.number != null) {
                explode(node.right.number!!, depth+1)
            }
        }

    }

    fun split(node: Number) {
        if (actionHappened) return

        if (node.left.value != null && node.left.value!! >= 10 && !actionHappened) {
            //println("Making left split")
            val division = node.left.value!! / 2
            val remainder = node.left.value!! % 2
            node.left = Edge(Number(Edge(division), Edge(division + remainder)))
            actionHappened = true
        } else if (node.left.number != null && !actionHappened) {
            split(node.left.number!!)
        }

        if (node.right.value != null && node.right.value!! >= 10 && !actionHappened)  {
            //println("Making right split")
            val division = node.right.value!! / 2
            val remainder = node.right.value!! % 2
            node.right = Edge(Number(Edge(division), Edge(division + remainder)))
            actionHappened = true
        } else if (node.right.number != null && !actionHappened) {
            split(node.right.number!!)
        }
    }

    fun magnitude(): Long {
        return  magnitude(root)
    }

    fun magnitude(node: Number): Long {
        var mag = 0L
        if (node.left.value != null) mag += node.left.value!! * 3
        else mag += magnitude(node.left.number!!) * 3

        if (node.right.value != null) mag += node.right.value!! * 2
        else mag += magnitude(node.right.number!!) * 2

        return mag
    }

    override fun toString(): String {
        return "Snailfish Number: $root\n    with structure ${parents}"
    }
}

fun partOne(filePath: String): Long {
    val data = File(filePath).readLines()
    val tree = Tree()
    tree.initialize(data[0].toList())
    for (i in 1 until data.size) {
        //println("$i: ${tree.root} + ${data[i]}")
        tree.rightAdd(data[i])
    }

    val magnitude = tree.magnitude()
    //println("Example number ${tree.root}")
    //println("Example magnitude $magnitude")

    return magnitude
}

fun partTwo(filePath: String): Long {
    val data = File(filePath).readLines()
    var maximalMagnitude = 0L
    for (i in data.indices) {
        for (j in data.indices) {
            val tree = Tree()
            tree.initialize(data[i].toList())
            tree.rightAdd(data[j])
            maximalMagnitude = max(maximalMagnitude, tree.magnitude())
        }
    }
    return maximalMagnitude
}

fun main() {
    println("Example  one part one ${partOne("$dataDir/day18/example.txt")}")
    println("Example  two part one ${partOne("$dataDir/day18/example2.txt")}")
    println("Example  two part two ${partTwo("$dataDir/day18/example2.txt")}")
    println("Part one ${partOne("$dataDir/day18/input.txt")}")
    println("Part two ${partTwo("$dataDir/day18/input.txt")}")

}