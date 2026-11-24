package com.waltonirr.android

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.waltonirr.android.data.FanStateStore
import com.waltonirr.android.ir.IrTransmitter
import com.waltonirr.android.ui.RemoteScreen
import com.waltonirr.android.ui.theme.FanRemoteTheme

class MainActivity : ComponentActivity() {
    private val ir by lazy { IrTransmitter(applicationContext) }
    private val store by lazy { FanStateStore(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Always-dark UI: light status/navigation bar icons regardless of system theme.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            FanRemoteTheme {
                RemoteScreen(
                    hasEmitter = ir.hasEmitter,
                    initial = remember { store.load() },
                    onStateChange = store::save,
                    send = { ir.send(it.pattern) },
                )
            }
        }
    }
}
