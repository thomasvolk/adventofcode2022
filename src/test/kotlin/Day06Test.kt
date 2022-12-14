package net.t53k

import org.junit.jupiter.api.Test
import java.net.URL
import kotlin.test.assertEquals

class Day06Test {
    private val inputFile: URL = PuzzleInput.loadFile("/Day06-input.txt")

    @Test
    fun testDay06part1() {
        assertEquals(7, Day06.firstPacketMarkerPosition("mjqjpqmgbljsphdztnvjfqwrcgsmlb"))
        assertEquals(5, Day06.firstPacketMarkerPosition("bvwbjplbgvbhsrlpgdmjqwftvncz"))
        assertEquals(6, Day06.firstPacketMarkerPosition("nppdvjthqldpwncqszvftbrmjlhg"))
        assertEquals(11, Day06.firstPacketMarkerPosition("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"))
        assertEquals(10, Day06.firstPacketMarkerPosition("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"))

        assertEquals(1953, Day06.firstPacketMarkerPosition(inputFile))
    }

    @Test
    fun testDay06part2() {
        assertEquals(19, Day06.firstMessageMarkerPosition("mjqjpqmgbljsphdztnvjfqwrcgsmlb"))
        assertEquals(2301, Day06.firstMessageMarkerPosition(inputFile))
    }
}