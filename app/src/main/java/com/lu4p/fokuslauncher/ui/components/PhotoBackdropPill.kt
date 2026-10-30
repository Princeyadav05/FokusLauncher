package com.lu4p.fokuslauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lu4p.fokuslauncher.data.model.PhotoWallpaperOutlineWidthDp

fun Modifier.photoBackdropPill(
        strength: Float,
        horizontalPaddingMin: Dp = 4.dp,
        horizontalPaddingMax: Dp = 18.dp,
        verticalPaddingMin: Dp = 1.5.dp,
        verticalPaddingMax: Dp = 7.5.dp,
        shape: Shape = RoundedCornerShape(50),
): Modifier {
    if (strength <= 0f) return this
    val normalized = (strength / PhotoWallpaperOutlineWidthDp.MAX).coerceIn(0f, 1f)
    val alpha = 0.14f + 0.68f * normalized
    return background(Color.Black.copy(alpha = alpha), shape)
            .photoBackdropPillPadding(
                    strength,
                    horizontalPaddingMin,
                    horizontalPaddingMax,
                    verticalPaddingMin,
                    verticalPaddingMax,
            )
}

/** The padding of [photoBackdropPill] alone, for content layered over a separate pill. */
fun Modifier.photoBackdropPillPadding(
        strength: Float,
        horizontalPaddingMin: Dp = 4.dp,
        horizontalPaddingMax: Dp = 18.dp,
        verticalPaddingMin: Dp = 1.5.dp,
        verticalPaddingMax: Dp = 7.5.dp,
): Modifier {
    if (strength <= 0f) return this
    val normalized = (strength / PhotoWallpaperOutlineWidthDp.MAX).coerceIn(0f, 1f)
    val horizontalPadding =
            horizontalPaddingMin + (horizontalPaddingMax - horizontalPaddingMin) * normalized
    val verticalPadding = verticalPaddingMin + (verticalPaddingMax - verticalPaddingMin) * normalized
    return padding(horizontal = horizontalPadding, vertical = verticalPadding)
}
