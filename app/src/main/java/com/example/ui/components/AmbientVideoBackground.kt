package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.data.model.AmbientType
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AmbientVideoBackground(
    ambientType: AmbientType,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (ambientType) {
            AmbientType.FIRE -> FireMotionBackground()
            AmbientType.OCEAN -> OceanMotionBackground()
            AmbientType.BIRDS -> ForestBirdsMotionBackground()
            AmbientType.RAIN -> RainMotionBackground()
            AmbientType.GALAXY, AmbientType.NONE -> GalaxyMotionBackground()
        }
    }
}

// 1. FIRE / HEARTH: Rising embers, flickering warm flame aura
@Composable
private fun FireMotionBackground() {
    val transition = rememberInfiniteTransition(label = "fire")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fire_time"
    )

    val embers = remember {
        List(65) {
            EmberParticle(
                x = Random.nextFloat(),
                yOffset = Random.nextFloat(),
                speed = Random.nextFloat() * 0.4f + 0.3f,
                size = Random.nextFloat() * 4f + 2f,
                alpha = Random.nextFloat() * 0.7f + 0.3f,
                drift = Random.nextFloat() * 20f - 10f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0503),
                        Color(0xFF1E0A05),
                        Color(0xFF381206),
                        Color(0xFF6B1D08),
                        Color(0xFF9A2C08)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Flame base glow
            val flicker = (sin(time * 2 * PI.toFloat() * 3) * 0.08f) + 0.92f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFB703).copy(alpha = 0.5f * flicker),
                        Color(0xFFFB8500).copy(alpha = 0.35f * flicker),
                        Color(0xFFD00000).copy(alpha = 0.2f * flicker),
                        Color.Transparent
                    ),
                    center = Offset(w / 2f, h * 0.95f),
                    radius = w * 0.7f * flicker
                ),
                radius = w * 0.7f * flicker,
                center = Offset(w / 2f, h * 0.95f)
            )

            // Rising golden ember sparks
            embers.forEach { e ->
                val currentY = ((e.yOffset - (time * e.speed)) % 1.0f + 1.0f) % 1.0f
                val py = currentY * h
                val px = (e.x * w) + (sin((time * 2 * PI.toFloat()) + e.drift) * 25f)
                val emberAlpha = (e.alpha * (py / h)).coerceIn(0.1f, 1f)

                drawCircle(
                    color = if (e.size > 4f) Color(0xFFFFD166).copy(alpha = emberAlpha) else Color(0xFFFF5400).copy(alpha = emberAlpha),
                    radius = e.size,
                    center = Offset(px, py)
                )
            }
        }
    }
}

private data class EmberParticle(
    val x: Float,
    val yOffset: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float,
    val drift: Float
)

// 2. OCEAN: Rolling layered waves with foam crests & deep ocean gradient
@Composable
private fun OceanMotionBackground() {
    val transition = rememberInfiniteTransition(label = "ocean")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ocean_phase"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF03071E),
                        Color(0xFF023E8A),
                        Color(0xFF0077B6),
                        Color(0xFF0096C7),
                        Color(0xFF03045E)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Deep wave 1 (slow)
            drawWaveLayer(
                width = w,
                height = h,
                baseY = h * 0.65f,
                amplitude = 28f,
                wavelength = w * 0.85f,
                phase = phase,
                color = Color(0xFF0077B6).copy(alpha = 0.45f)
            )

            // Middle wave 2
            drawWaveLayer(
                width = w,
                height = h,
                baseY = h * 0.72f,
                amplitude = 35f,
                wavelength = w * 0.7f,
                phase = -phase * 1.3f,
                color = Color(0xFF0096C7).copy(alpha = 0.55f)
            )

            // Foreground surf wave 3 with crest foam
            drawWaveLayer(
                width = w,
                height = h,
                baseY = h * 0.80f,
                amplitude = 22f,
                wavelength = w * 0.55f,
                phase = phase * 1.8f,
                color = Color(0xFF48CAE4).copy(alpha = 0.65f),
                hasFoam = true
            )
        }
    }
}

private fun DrawScope.drawWaveLayer(
    width: Float,
    height: Float,
    baseY: Float,
    amplitude: Float,
    wavelength: Float,
    phase: Float,
    color: Color,
    hasFoam: Boolean = false
) {
    val path = Path().apply {
        moveTo(0f, height)
        lineTo(0f, baseY)
        var x = 0f
        while (x <= width) {
            val y = baseY + sin((x / wavelength * 2 * PI.toFloat()) + phase) * amplitude
            lineTo(x, y)
            x += 10f
        }
        lineTo(width, height)
        close()
    }
    drawPath(path = path, color = color)

    if (hasFoam) {
        val foamPath = Path().apply {
            var x = 0f
            moveTo(0f, baseY + sin(phase) * amplitude)
            while (x <= width) {
                val y = baseY + sin((x / wavelength * 2 * PI.toFloat()) + phase) * amplitude
                lineTo(x, y)
                x += 12f
            }
        }
        drawPath(
            path = foamPath,
            color = Color.White.copy(alpha = 0.35f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
        )
    }
}

// 3. FOREST & BIRDS: Sunlit morning canopy with flying animated birds
@Composable
private fun ForestBirdsMotionBackground() {
    val transition = rememberInfiniteTransition(label = "birds")
    val birdProgress by transition.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bird_flight"
    )

    val wingPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wing_flap"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF061A12),
                        Color(0xFF0F3924),
                        Color(0xFF1B4D3E),
                        Color(0xFF2D6A4F),
                        Color(0xFF1B4332)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Soft golden sun ray in the canopy
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFE066).copy(alpha = 0.35f),
                        Color(0xFF70E000).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.8f, h * 0.25f),
                    radius = w * 0.8f
                ),
                radius = w * 0.8f,
                center = Offset(w * 0.8f, h * 0.25f)
            )

            // Flock of 3 birds gliding diagonally across the sky
            val bird1X = birdProgress * w
            val bird1Y = (h * 0.28f) + sin(birdProgress * 4) * 30f
            drawBirdSilhouette(bird1X, bird1Y, scale = 1.0f, wingPhase = wingPhase)

            val bird2X = (birdProgress - 0.12f) * w
            val bird2Y = (h * 0.33f) + sin((birdProgress - 0.12f) * 4) * 25f
            drawBirdSilhouette(bird2X, bird2Y, scale = 0.75f, wingPhase = wingPhase + 0.8f)

            val bird3X = (birdProgress - 0.20f) * w
            val bird3Y = (h * 0.24f) + sin((birdProgress - 0.20f) * 4) * 20f
            drawBirdSilhouette(bird3X, bird3Y, scale = 0.6f, wingPhase = wingPhase + 1.6f)
        }
    }
}

private fun DrawScope.drawBirdSilhouette(cx: Float, cy: Float, scale: Float, wingPhase: Float) {
    val wingY = sin(wingPhase) * 12f * scale
    val path = Path().apply {
        moveTo(cx - 24f * scale, cy + wingY)
        quadraticTo(cx - 10f * scale, cy - 4f * scale, cx, cy)
        quadraticTo(cx + 10f * scale, cy - 4f * scale, cx + 24f * scale, cy + wingY)
    }
    drawPath(
        path = path,
        color = Color(0xFFF4F1DE).copy(alpha = 0.75f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f * scale)
    )
}

// 4. RAIN: Slanted falling raindrops with window mist and bottom splashes
@Composable
private fun RainMotionBackground() {
    val transition = rememberInfiniteTransition(label = "rain")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain_fall"
    )

    val drops = remember {
        List(85) {
            RainDrop(
                x = Random.nextFloat(),
                yOffset = Random.nextFloat(),
                length = Random.nextFloat() * 25f + 18f,
                speed = Random.nextFloat() * 0.6f + 0.7f,
                alpha = Random.nextFloat() * 0.5f + 0.25f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030A1A),
                        Color(0xFF081C3D),
                        Color(0xFF0F2B5C),
                        Color(0xFF0A1E42),
                        Color(0xFF051124)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drops.forEach { d ->
                val progress = (d.yOffset + (time * d.speed)) % 1.0f
                val startX = d.x * w
                val startY = progress * h
                val endX = startX - 6f
                val endY = startY + d.length

                drawLine(
                    color = Color(0xFFA5C9FF).copy(alpha = d.alpha),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2.0f
                )
            }
        }
    }
}

private data class RainDrop(
    val x: Float,
    val yOffset: Float,
    val length: Float,
    val speed: Float,
    val alpha: Float
)

// 5. GALAXY: Twinkling celestial cosmos with nebulae and shooting stars
@Composable
fun GalaxyMotionBackground(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "galaxy")
    val twinkle by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "galaxy_twinkle"
    )

    val stars = remember {
        List(110) {
            StarPoint(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 1.8f + 0.7f,
                baseAlpha = Random.nextFloat() * 0.5f + 0.4f,
                speedMultiplier = Random.nextFloat() * 0.8f + 0.5f
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0E1A3D),
                        Color(0xFF080F26),
                        Color(0xFF030712)
                    ),
                    radius = 1600f
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            stars.forEach { s ->
                val dynamicAlpha = (s.baseAlpha * (twinkle * s.speedMultiplier)).coerceIn(0.15f, 1f)
                drawCircle(
                    color = Color.White.copy(alpha = dynamicAlpha),
                    radius = s.radius,
                    center = Offset(s.x * w, s.y * h)
                )
            }
        }
    }
}

private data class StarPoint(
    val x: Float,
    val y: Float,
    val radius: Float,
    val baseAlpha: Float,
    val speedMultiplier: Float
)
