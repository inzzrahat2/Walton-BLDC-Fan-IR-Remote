package com.waltonirr.android.data

import com.waltonirr.android.ir.NecEncoder

/** NEC-extended address of Walton Super Saver-IR fans (sent as bytes D8 7E). */
const val WALTON_ADDRESS = 0x7ED8

/**
 * Walton Super Saver-IR remote buttons and their NEC command bytes, extracted from a third-party
 * fan remote app (see docs/ir-codes/README.md).
 */
enum class FanButton(val label: String, val command: Int) {
    POWER("Power", 0x08),
    LED("LED", 0x07),
    SPEED_1("1", 0x01),
    SPEED_2("2", 0x02),
    SPEED_3("3", 0x03),
    SPEED_4("4", 0x04),
    SPEED_5("5", 0x05),
    SPEED_6("6", 0x15),
    SPEED_7("7", 0x13),
    TIMER_2H("2H", 0x09),
    TIMER_4H("4H", 0x10),
    ECO("Eco", 0x14),
    REVERSE("Reverse", 0x12);

    /** Microsecond on/off pattern for ConsumerIrManager, built on first use. */
    val pattern: IntArray by lazy { NecEncoder.encode(WALTON_ADDRESS, command, extended = true) }

    companion object {
        val speeds = listOf(SPEED_1, SPEED_2, SPEED_3, SPEED_4, SPEED_5, SPEED_6, SPEED_7)
    }
}
