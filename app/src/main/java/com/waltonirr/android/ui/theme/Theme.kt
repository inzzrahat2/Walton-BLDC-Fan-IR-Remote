package com.waltonirr.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** One fixed dark palette; the window background in res/values/colors.xml must match [Bg]. */
object FanColors {
    val Bg = Color(0xFF0B1120)
    val BgTop = Color(0xFF15213A)
    val Surface = Color(0xFF17223A)
    val SurfaceHigh = Color(0xFF1F2C48)
    val Outline = Color(0x1FFFFFFF)
    val Accent = Color(0xFF38BDF8)
    val AccentDeep = Color(0xFF6366F1)
    val Power = Color(0xFFF43F5E)
    val Eco = Color(0xFF34D399)
    val Led = Color(0xFFFBBF24)
    val Reverse = Color(0xFFA78BFA)
    val Text = Color(0xFFF1F5F9)
    val TextMuted = Color(0xFF8B9AB4)
}

private val Scheme = darkColorScheme(
    primary = FanColors.Accent,
    onPrimary = FanColors.Bg,
    background = FanColors.Bg,
    onBackground = FanColors.Text,
    surface = FanColors.Surface,
    onSurface = FanColors.Text,
    inverseSurface = FanColors.SurfaceHigh,
    inverseOnSurface = FanColors.Text,
    error = FanColors.Power,
)

@Composable
fun FanRemoteTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, content = content)
}
