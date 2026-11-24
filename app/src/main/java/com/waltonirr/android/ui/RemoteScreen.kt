package com.waltonirr.android.ui

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.waltonirr.android.data.FanButton
import com.waltonirr.android.data.FanState
import com.waltonirr.android.ui.theme.FanColors
import kotlinx.coroutines.launch

/**
 * IR is one-way, so the fan's real state is unknown. The hero card shows what this phone last sent
 * (power toggled, speed chosen), which matches the fan as long as only this phone is used.
 * [initial] is restored from storage and every change goes to [onStateChange] to be saved.
 */
@Composable
fun RemoteScreen(
    hasEmitter: Boolean,
    initial: FanState,
    onStateChange: (FanState) -> Unit,
    send: suspend (FanButton) -> String?,
) {
    var state by remember { mutableStateOf(initial) }
    val on = state.on
    val level = state.level
    var pulse by remember { mutableIntStateOf(0) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val view = LocalView.current

    val press: (FanButton) -> Unit = { button ->
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        pulse++
        val next = when (button) {
            FanButton.POWER -> state.copy(on = !state.on)
            in FanButton.speeds -> FanState(on = true, level = FanButton.speeds.indexOf(button) + 1)
            else -> state
        }
        if (next != state) {
            state = next
            onStateChange(next)
        }
        scope.launch { send(button)?.let { snackbar.showSnackbar(it) } }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(FanColors.BgTop, FanColors.Bg, FanColors.Bg))),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Header(hasEmitter, pulse)
            if (!hasEmitter) NoEmitterCard()
            HeroCard(on, level, onPower = { press(FanButton.POWER) })
            SpeedSelector(if (on) level else 0, onSelect = press)

            SectionLabel("Timer")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Tile(FanIcons.Timer, "2 Hours", FanColors.Accent, { press(FanButton.TIMER_2H) }, Modifier.weight(1f))
                Tile(FanIcons.Timer, "4 Hours", FanColors.Accent, { press(FanButton.TIMER_4H) }, Modifier.weight(1f))
            }

            SectionLabel("Modes")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Tile(FanIcons.Eco, "Eco", FanColors.Eco, { press(FanButton.ECO) }, Modifier.weight(1f))
                Tile(FanIcons.Led, "LED", FanColors.Led, { press(FanButton.LED) }, Modifier.weight(1f))
                Tile(FanIcons.Reverse, "Reverse", FanColors.Reverse, { press(FanButton.REVERSE) }, Modifier.weight(1f))
            }
        }
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).navigationBarsPadding())
    }
}

@Composable
private fun Header(hasEmitter: Boolean, pulse: Int) {
    // Flashes the status dot each time a code is sent.
    val glow = remember { Animatable(0f) }
    LaunchedEffect(pulse) {
        if (pulse > 0) {
            glow.snapTo(1f)
            glow.animateTo(0f, tween(500))
        }
    }
    val dot = if (hasEmitter) FanColors.Eco else FanColors.Power

    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("WALTON", color = FanColors.TextMuted, fontSize = 12.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Medium)
            Text("Super Saver", color = FanColors.Text, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
        }
        Row(
            Modifier
                .clip(CircleShape)
                .background(FanColors.Surface)
                .border(1.dp, FanColors.Outline, CircleShape)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(14.dp)) {
                Box(
                    Modifier
                        .size(14.dp)
                        .graphicsLayer { alpha = glow.value; scaleX = 1f + glow.value; scaleY = 1f + glow.value }
                        .background(dot.copy(alpha = 0.4f), CircleShape),
                )
                Box(Modifier.size(8.dp).background(dot, CircleShape))
            }
            Spacer(Modifier.width(8.dp))
            Text(if (hasEmitter) "IR ready" else "No IR", color = FanColors.Text, fontSize = 13.sp)
        }
    }
}

@Composable
private fun NoEmitterCard() {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(FanColors.Power.copy(alpha = 0.12f))
            .padding(16.dp),
    ) {
        Text("No IR blaster found", color = FanColors.Power, fontWeight = FontWeight.SemiBold)
        Text(
            "This phone can't send infrared. Use a phone with an IR blaster (most Xiaomi, Redmi and POCO phones).",
            color = FanColors.Text, fontSize = 13.sp,
        )
    }
}

@Composable
private fun HeroCard(on: Boolean, level: Int, onPower: () -> Unit) {
    val shape = RoundedCornerShape(32.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.linearGradient(listOf(FanColors.SurfaceHigh, FanColors.Surface)))
            .border(1.dp, FanColors.Outline, shape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FanGraphic(on, level, Modifier.size(92.dp))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                if (on) "RUNNING" else "OFF",
                color = if (on) FanColors.Accent else FanColors.TextMuted,
                fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold,
            )
            // Shrinks rather than wraps on narrow phones.
            BasicText(
                when {
                    !on -> "Standby"
                    level > 0 -> "Speed $level"
                    else -> "On"
                },
                style = TextStyle(color = FanColors.Text, fontWeight = FontWeight.Bold),
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(minFontSize = 16.sp, maxFontSize = 28.sp),
            )
        }
        Spacer(Modifier.width(8.dp))
        PowerButton(on, onPower)
    }
}

@Composable
private fun PowerButton(on: Boolean, onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    val bg by animateColorAsState(if (on) FanColors.Power else FanColors.SurfaceHigh, label = "powerBg")
    val fg by animateColorAsState(if (on) Color.White else FanColors.Power, label = "powerFg")
    Box(
        Modifier
            .pressScale(source)
            .size(64.dp)
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, FanColors.Power.copy(alpha = 0.5f), CircleShape)
            .clickable(source, ripple(), onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(FanIcons.Power, "Power", tint = fg, modifier = Modifier.size(30.dp))
    }
}

/** Three-blade fan that spins faster with [level] and eases to a stop when off. */
@Composable
private fun FanGraphic(on: Boolean, level: Int, modifier: Modifier) {
    val target = if (on) 120f + level.coerceAtLeast(1) * 110f else 0f
    val degreesPerSecond by animateFloatAsState(target, tween(1500, easing = FastOutSlowInEasing), label = "spin")
    var angle by remember { mutableFloatStateOf(0f) }
    val spinning = degreesPerSecond > 0.5f
    LaunchedEffect(spinning) {
        if (!spinning) return@LaunchedEffect
        var last = -1L
        while (true) {
            withFrameNanos { now ->
                if (last >= 0) angle = (angle + degreesPerSecond * (now - last) / 1e9f) % 360f
                last = now
            }
        }
    }
    val blade = remember {
        Path().apply {
            moveTo(50f, 50f)
            cubicTo(45f, 40f, 40f, 22f, 49f, 13f)
            cubicTo(57f, 5f, 69f, 12f, 67f, 25f)
            cubicTo(65f, 36f, 57f, 44f, 50f, 50f)
            close()
        }
    }
    val bladeBrush = if (on) {
        Brush.linearGradient(listOf(FanColors.Accent, FanColors.AccentDeep), Offset(50f, 10f), Offset(50f, 50f))
    } else {
        Brush.linearGradient(listOf(FanColors.TextMuted, FanColors.SurfaceHigh), Offset(50f, 10f), Offset(50f, 50f))
    }
    val ringColor = if (on) FanColors.Accent else FanColors.TextMuted

    Canvas(modifier) {
        val s = size.minDimension / 100f
        scale(s, pivot = Offset.Zero) {
            val c = Offset(50f, 50f)
            drawCircle(ringColor.copy(alpha = 0.08f), 49f, c)
            drawCircle(ringColor.copy(alpha = 0.25f), 49f, c, style = Stroke(1.2f))
            for (i in 0 until 3) {
                rotate(angle + i * 120f, pivot = c) { drawPath(blade, bladeBrush) }
            }
            drawCircle(FanColors.Bg, 8f, c)
            drawCircle(ringColor, 4.5f, c)
        }
    }
}

@Composable
private fun SpeedSelector(level: Int, onSelect: (FanButton) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel("Speed")
        Row(Modifier.fillMaxWidth().height(112.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FanButton.speeds.forEachIndexed { index, button ->
                val n = index + 1
                SpeedBar(n, filled = n <= level, selected = n == level, onClick = { onSelect(button) }, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SpeedBar(n: Int, filled: Boolean, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val source = remember { MutableInteractionSource() }
    val fill by animateColorAsState(
        if (filled) lerp(FanColors.Accent, FanColors.AccentDeep, (n - 1) / 6f) else FanColors.SurfaceHigh,
        label = "bar$n",
    )
    Column(
        modifier
            .fillMaxHeight()
            .pressScale(source)
            .clickable(source, indication = null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f + 0.7f * n / 7f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(fill),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "$n",
            color = if (selected) FanColors.Text else FanColors.TextMuted,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun Tile(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit, modifier: Modifier) {
    val source = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier
            .pressScale(source)
            .clip(shape)
            .background(FanColors.Surface)
            .border(1.dp, FanColors.Outline, shape)
            .clickable(source, ripple(color = tint), onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(40.dp).background(tint.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, color = FanColors.Text, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        color = FanColors.TextMuted,
        fontSize = 12.sp,
        letterSpacing = 2.sp,
        fontWeight = FontWeight.SemiBold,
        style = MaterialTheme.typography.labelMedium,
    )
}

/** Shrinks slightly while pressed. */
@Composable
private fun Modifier.pressScale(source: MutableInteractionSource): Modifier {
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, label = "press")
    return graphicsLayer { scaleX = scale; scaleY = scale }
}
