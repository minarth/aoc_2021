package day16

import java.io.File
import java.util.Collections.max
import java.util.Collections.min
import dataDir

data class Packet(val version: Int, val typeId: Int,
                  var packets: MutableList<Packet> = mutableListOf(),  // for Ops packets
                  var value: Long = -1,                                 // for Literal packets
                  )

fun hex2bin(hex: String): MutableList<Char> {
    val bin = mutableListOf<Char>()
    for (ch in hex) {
        val ch2bin = when (ch) {
            '0' -> listOf('0', '0', '0', '0')
            '1' -> listOf('0', '0', '0', '1')
            '2' -> listOf('0', '0', '1', '0')
            '3' -> listOf('0', '0', '1', '1')
            '4' -> listOf('0', '1', '0', '0')
            '5' -> listOf('0', '1', '0', '1')
            '6' -> listOf('0', '1', '1', '0')
            '7' -> listOf('0', '1', '1', '1')
            '8' -> listOf('1', '0', '0', '0')
            '9' -> listOf('1', '0', '0', '1')
            'A' -> listOf('1', '0', '1', '0')
            'B' -> listOf('1', '0', '1', '1')
            'C' -> listOf('1', '1', '0', '0')
            'D' -> listOf('1', '1', '0', '1')
            'E' -> listOf('1', '1', '1', '0')
            'F' -> listOf('1', '1', '1', '1')
            else -> listOf()
        }
        bin.addAll(ch2bin)
    }

    return bin
}

fun removeN(packet: MutableList<Char>, n: Int) {
    for (i in 0 .. n) packet.removeFirst()
}

fun readPacket(packet: MutableList<Char>): Packet {
    val version = packet.slice(0..2).joinToString("").toInt(2)
    val typeId = packet.slice(3..5).joinToString("").toInt(2)
    // println("Processing packet $version $typeId ${packet.joinToString("")} ${packet.size}")
    val rootPacket =
        when (typeId) {
            4 -> {
                val binRepr = mutableListOf<Char>()
                for (i in 6..packet.size step 5) {
                    val control = packet[i].toString().toInt()
                    binRepr.addAll(packet.slice(i+1 .. i+4))
                    if (control == 0) {
                        removeN(packet, i+4)
                        break
                    }
                }
                Packet(version, typeId, value = binRepr.joinToString("").toLong(2))
            }
            else -> {
                val lengthTypeId = packet[6].toString().toInt()
                val subPackets = mutableListOf<Packet>()
                when (lengthTypeId) {
                    0 -> {
                        val length = packet.slice(7 .. 21).joinToString("").toInt(2)
                        // println("Length defined sub packets $length ${21+length}")
                        val subPacket = packet.slice(22 .. 21+length).toMutableList()

                        // todo error catch
                        while (true) {
                            subPackets.add(readPacket(subPacket))
                            if (subPacket.size < 11) break
                        }
                        removeN(packet, 21+length)
                    }
                    1 -> {
                        val subPacketsNumber = packet.slice(7 .. 17).joinToString("").toInt(2)
                        removeN(packet, 17)
                        for (i in 0 until subPacketsNumber) {
                            // println("Next subpacket substring ${packet.joinToString("")}")
                            subPackets.add(readPacket(packet))
                        }

                    }
                }

                Packet(version, typeId, subPackets)
            }
        }

    return rootPacket
}

fun partOne(packet: Packet): Int {
    if (packet.typeId == 4) {
        return packet.version
    }

    var subPacketsSum = 0
    for (subPacket in packet.packets) {
        subPacketsSum += partOne(subPacket)
    }

    return subPacketsSum + packet.version
}

fun partTwo(packet: Packet): Long {
    val result = when (packet.typeId) {
        0 -> {
            var sum = 0L
            for (sub in packet.packets) sum += partTwo(sub)
            sum
        }
        1 -> {
            var prod = 1L
            for (sub in packet.packets) prod *= partTwo(sub)
            prod
        }
        2 -> {
            val values = mutableListOf<Long>()
            for (sub in packet.packets) values.add(partTwo(sub))
            min(values)
        }
        3 -> {
            val values = mutableListOf<Long>()
            for (sub in packet.packets) values.add(partTwo(sub))
            max(values)
        }
        4 -> packet.value
        5 -> {
            val s1 = partTwo(packet.packets[0])
            val s2 = partTwo(packet.packets[1])
            if (s1 > s2) 1 else 0
        }
        6 ->{
            val s1 = partTwo(packet.packets[0])
            val s2 = partTwo(packet.packets[1])
            if (s1 < s2) 1 else 0
        }
        7  -> {
            val s1 = partTwo(packet.packets[0])
            val s2 = partTwo(packet.packets[1])
            if (s1 == s2) 1 else 0
        }
        else -> throw Exception("Missed type")
    }

    return result
}

fun main() {
    val packet = hex2bin("D2FE28")
    println("Parsing examples")
    println("First example ${readPacket(packet)}")
    println("Second example ${readPacket(hex2bin("38006F45291200"))}")
    println("Third example ${readPacket(hex2bin("EE00D40C823060"))}")

    println("\nSum examples")
    println(partOne(readPacket(hex2bin("8A004A801A8002F478"))))
    println(partOne(readPacket(hex2bin("620080001611562C8802118E34"))))
    println(partOne(readPacket(hex2bin("C0015000016115A2E0802F182340"))))
    println(partOne(readPacket(hex2bin("A0016C880162017C3686B18A3D4780"))))

    val testInput = File("$dataDir/day16/input.txt").readLines()[0]
    println("Part one ${partOne(readPacket(hex2bin(testInput)))}")


    println("Examples for part two")
    val examples = listOf("C200B40A82", "04005AC33890", "880086C3E88112", "CE00C43D881120", "D8005AC2A8F0",
        "F600BC2D8F", "9C005AC2F8F0", "9C0141080250320F1802104A08")
    //for (ex in examples) {
    //    println(partTwo(readPacket(hex2bin(ex))))
    //}

    println("Part two ${partTwo(readPacket(hex2bin(testInput)))}")

}