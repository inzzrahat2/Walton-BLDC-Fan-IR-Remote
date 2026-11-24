package com.waltonirr.android.data

import android.content.Context
import androidx.core.content.edit

/** What this phone last sent: power toggle and speed (0 = none chosen yet). */
data class FanState(val on: Boolean = false, val level: Int = 0)

/** Persists [FanState] across app restarts. SharedPreferences: two values, read once at launch. */
class FanStateStore(context: Context) {
    private val prefs = context.getSharedPreferences("fan_state", Context.MODE_PRIVATE)

    fun load() = FanState(prefs.getBoolean(KEY_ON, false), prefs.getInt(KEY_LEVEL, 0))

    fun save(state: FanState) = prefs.edit {
        putBoolean(KEY_ON, state.on)
        putInt(KEY_LEVEL, state.level)
    }

    private companion object {
        const val KEY_ON = "on"
        const val KEY_LEVEL = "level"
    }
}
