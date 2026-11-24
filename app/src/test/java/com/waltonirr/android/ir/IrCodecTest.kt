package com.waltonirr.android.ir

import com.waltonirr.android.data.FanButton
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class IrCodecTest {

    /** Decodes an NEC pattern back to its four bytes, LSB-first. */
    private fun necBytes(pattern: IntArray): List<Int> {
        assertEquals(8880, pattern[0])
        assertEquals(4470, pattern[1])
        assertEquals(2 + 64 + 1, pattern.size)
        val bits = (0 until 32).map { pattern[3 + it * 2] > 1000 }
        return (0 until 4).map { byte -> (0 until 8).sumOf { bit -> if (bits[byte * 8 + bit]) 1 shl bit else 0 } }
    }

    @Test
    fun necStandardSendsComplements() {
        assertEquals(listOf(0x00, 0xFF, 0x45, 0xBA), necBytes(NecEncoder.encode(0x00, 0x45, extended = false)))
    }

    @Test
    fun necExtendedSendsAddressLowThenHigh() {
        assertEquals(listOf(0x34, 0x12, 0x01, 0xFE), necBytes(NecEncoder.encode(0x1234, 0x01, extended = true)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun necRejectsWideAddressWithoutExtended() {
        NecEncoder.encode(0x1234, 0x01, extended = false)
    }

    @Test
    fun waltonPowerMatchesExtractedTiming() {
        // POWER pattern_us from docs/ir-codes/fan_ir_codes.json (NEC bytes D8 7E 08 F7).
        val bits = "00011011" + "01111110" + "00010000" + "11101111"
        val expected = intArrayOf(8880, 4470) + bits.flatMap { listOf(580, if (it == '1') 1620 else 620) } + 580
        assertArrayEquals(expected, FanButton.POWER.pattern)
    }

    @Test
    fun everyWaltonButtonUsesAddressD87E() {
        for (button in FanButton.entries) {
            val c = button.command
            assertEquals(button.name, listOf(0xD8, 0x7E, c, c.inv() and 0xFF), necBytes(button.pattern))
        }
    }
}
