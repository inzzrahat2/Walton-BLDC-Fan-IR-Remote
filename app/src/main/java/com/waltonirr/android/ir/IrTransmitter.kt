package com.waltonirr.android.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class IrTransmitter(context: Context) {
    private val manager: ConsumerIrManager? = context.getSystemService(ConsumerIrManager::class.java)

    // transmit() blocks for the length of the signal; overlapping calls garble each other.
    private val mutex = Mutex()

    val hasEmitter: Boolean = runCatching { manager?.hasIrEmitter() == true }.getOrDefault(false)

    /** Returns a user-readable error, or null once the pattern has been sent. */
    suspend fun send(pattern: IntArray, frequencyHz: Int = NecEncoder.FREQUENCY_HZ): String? {
        val ir = manager?.takeIf { hasEmitter } ?: return "This phone has no IR blaster"
        return mutex.withLock {
            withContext(Dispatchers.IO) {
                runCatching { ir.transmit(frequencyHz, pattern) }.exceptionOrNull()?.let { it.message ?: "IR transmit failed" }
            }
        }
    }
}
