package com.waltonirr.android.ir

/**
 * NEC frame: leader mark + space, 32 bits LSB-first (address, address', command, ~command), then a
 * closing mark. Each bit is a mark followed by a short (0) or long (1) space.
 *
 * Timings match the third-party app the Walton codes came from (docs/ir-codes), which is known to
 * work with these fans; they are within NEC tolerance for other receivers too.
 */
object NecEncoder {
    const val FREQUENCY_HZ = 38_000

    private const val LEADER_MARK = 8_880
    private const val LEADER_SPACE = 4_470
    private const val BIT_MARK = 580
    private const val ZERO_SPACE = 620
    private const val ONE_SPACE = 1_620

    fun encode(address: Int, command: Int, extended: Boolean): IntArray {
        require(command in 0..0xFF) { "Command must be 00–FF" }
        val addressBytes = if (extended) {
            require(address in 0..0xFFFF) { "16-bit address must be 0000–FFFF" }
            listOf(address and 0xFF, address shr 8)
        } else {
            require(address in 0..0xFF) { "Address must be 00–FF (use 4 hex digits for a 16-bit address)" }
            listOf(address, address.inv() and 0xFF)
        }
        val bytes = addressBytes + listOf(command, command.inv() and 0xFF)

        val pattern = ArrayList<Int>(2 + 32 * 2 + 1)
        pattern += LEADER_MARK
        pattern += LEADER_SPACE
        for (byte in bytes) {
            for (bit in 0 until 8) {
                pattern += BIT_MARK
                pattern += if ((byte shr bit) and 1 == 1) ONE_SPACE else ZERO_SPACE
            }
        }
        pattern += BIT_MARK
        return pattern.toIntArray()
    }
}
