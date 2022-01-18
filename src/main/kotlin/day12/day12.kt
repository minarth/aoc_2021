package day12

import java.io.File
import dataDir

data class Path(val path: String, val smallCavern: Boolean)

fun createGraph(input: List<String>): Map<String, MutableList<String>> {
    val graph = mutableMapOf<String, MutableList<String>>()
    for (line in input) {
        val (from, to) = line.split("-".toRegex())
        if (!graph.containsKey(from)) {
            graph[from] = mutableListOf<String>()
        }
        if (!graph.containsKey(to)) {
            graph[to] = mutableListOf<String>()
        }


        graph[from]!!.add(to)
        graph[to]!!.add(from)
    }

    return graph
}

fun breadthSearch(graph: Map<String, MutableList<String>>): Int {
    val paths = mutableSetOf<String>()
    var candidates = mutableSetOf<String>("start")
    while (candidates.size > 0) {
        val newCandidates = mutableSetOf<String>()
        for (candidate in candidates) {
            val currentNode = candidate.split(",".toRegex()).last()
            if (currentNode == "end") {
                paths.add(candidate)
            } else {
                for (nextNode in graph[currentNode]!!) {
                    if (nextNode.toLowerCase() == nextNode && candidate.contains(nextNode))
                        continue
                    newCandidates.add("$candidate,$nextNode")
                }
            }
        }

        candidates = newCandidates
    }
    return paths.size
}


fun breadthSearchAdvanced(graph: Map<String, MutableList<String>>): Int {
    val paths = mutableSetOf<Path>()
    var candidates = mutableSetOf(Path("start", false))
    var i = 0
    while (candidates.size > 0 && i < 30) {
        val newCandidates = mutableSetOf<Path>()
        for (candidate in candidates) {
            val currentNode = candidate.path.split(",".toRegex()).last()
            if (currentNode == "end") {
                paths.add(candidate)
            } else {
                for (nextNode in graph[currentNode]!!) {
                    if (nextNode.toLowerCase() == nextNode && candidate.path.contains(nextNode) && candidate.smallCavern)
                        continue
                    if (nextNode == "start") continue

                    newCandidates.add(Path(candidate.path + "," + nextNode, candidate.smallCavern ||
                        nextNode.toLowerCase() == nextNode && candidate.path.contains(nextNode)))
                }
            }
        }
        i++
        candidates = newCandidates
    }
    return paths.size
}

fun main() {
    var exampleGraph = createGraph(File("$dataDir/day12/example0.txt").readLines())
    print("Example one part one ")
    println(breadthSearch(exampleGraph))
    print("Example one part two ")
    println(breadthSearchAdvanced(exampleGraph))

    exampleGraph = createGraph(File("$dataDir/day12/example1.txt").readLines())
    print("Example two part one ")
    println(breadthSearch(exampleGraph))
    print("Example two part two ")
    println(breadthSearchAdvanced(exampleGraph))

    exampleGraph = createGraph(File("$dataDir/day12/example2.txt").readLines())
    print("Example three part one ")
    println(breadthSearch(exampleGraph))
    print("Example three part two ")
    println(breadthSearchAdvanced(exampleGraph))

    val inputGraph = createGraph(File("$dataDir/day12/input.txt").readLines())
    print("Part one ")
    println(breadthSearch(inputGraph))
    print("Part two ")
    println(breadthSearchAdvanced(inputGraph))
}