package com.lu4p.fokuslauncher.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

internal enum class HomeContentSlot { Note, Favorites, Gap }

private val FavoriteLabelsTop = HorizontalAlignmentLine(::minOf)
private val FavoriteLabelsStart = VerticalAlignmentLine(::minOf)
private val FavoriteLabelsEnd = VerticalAlignmentLine(::maxOf)

/** Narrowing the note beside a shortcut rail costs more than a smaller space would give. */
private val MinSpaceBesideRail = 40.dp

/** Marks the favorite labels, so [HomeContentLayout] can use the space a taller rail leaves above them. */
internal fun Modifier.favoriteLabelLines(): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(
            placeable.width,
            placeable.height,
            mapOf(
                    FavoriteLabelsTop to 0,
                    FavoriteLabelsStart to 0,
                    FavoriteLabelsEnd to placeable.width,
            ),
    ) { placeable.placeRelative(0, 0) }
}

/**
 * Reserve favorites and other widgets before measuring the note's flexible preview. When the note
 * does not fit above a shortcut rail, it and the widgets below it narrow to the width of the
 * favorite labels and continue beside the rail, down to the top of the labels.
 */
@Composable
internal fun HomeContentLayout(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val gaps = measurables.indices.filter { measurables[it].layoutId == HomeContentSlot.Gap }
        val note = measurables.indexOfFirst { it.layoutId == HomeContentSlot.Note }
        val favorites = measurables.indexOfFirst { it.layoutId == HomeContentSlot.Favorites }
        val belowNote =
                if (note >= 0) {
                    (note + 1 until (gaps.firstOrNull() ?: measurables.size)).filter { it != favorites }
                } else {
                    emptyList()
                }
        var remainingHeight = constraints.maxHeight

        fun measure(index: Int, maxHeight: Int = remainingHeight, itemConstraints: Constraints = constraints) {
            val placeable = measurables[index].measure(
                    itemConstraints.copy(minHeight = 0, maxHeight = maxHeight),
            )
            placeables[index] = placeable
            remainingHeight = (remainingHeight - placeable.height).coerceAtLeast(0)
        }

        if (favorites >= 0) measure(favorites)
        measurables.indices.forEach { index ->
            if (index != favorites && index != note && index !in gaps && index !in belowNote) {
                measure(index)
            }
        }

        var besideRailStart = 0
        var besideRailWidth = constraints.maxWidth
        var spaceBesideRail = 0
        if (note >= 0) {
            // Keep some breathing room between the preview/widgets and the app list.
            val gapSpace = 16.dp.roundToPx() * gaps.size
            val spaceAboveLabels = placeables.getOrNull(favorites)?.let { it[FavoriteLabelsTop] }
                    ?.takeIf { it != AlignmentLine.Unspecified } ?: 0
            if (spaceAboveLabels >= MinSpaceBesideRail.roundToPx()) {
                val belowHeight = belowNote.sumOf { measurables[it].maxIntrinsicHeight(constraints.maxWidth) }
                val noteHeight = measurables[note].maxIntrinsicHeight(constraints.maxWidth)
                if (noteHeight > remainingHeight - belowHeight - gapSpace) {
                    val labels = placeables[favorites]!!
                    besideRailStart = labels[FavoriteLabelsStart]
                    besideRailWidth = labels[FavoriteLabelsEnd] - besideRailStart
                    spaceBesideRail = spaceAboveLabels
                }
            }
            val itemConstraints =
                    if (spaceBesideRail > 0) Constraints.fixedWidth(besideRailWidth) else constraints
            belowNote.forEach { measure(it, itemConstraints = itemConstraints) }
            val noteSpace = remainingHeight + spaceBesideRail
            measure(note, noteSpace - gapSpace.coerceAtMost(noteSpace), itemConstraints)
        }

        // Beside a rail the gap is negative: it moves the favorites back up to their usual place.
        val freeHeight =
                if (spaceBesideRail > 0) constraints.maxHeight - placeables.sumOf { it?.height ?: 0 }
                else remainingHeight
        val gapHeights = IntArray(measurables.size)
        gaps.forEachIndexed { index, gap ->
            gapHeights[gap] = freeHeight / gaps.size +
                    if (index < freeHeight % gaps.size) 1 else 0
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            var y = 0
            placeables.forEachIndexed { index, placeable ->
                if (placeable != null) {
                    if (spaceBesideRail > 0 && (index == note || index in belowNote)) {
                        placeable.place(besideRailStart, y)
                    } else {
                        placeable.placeRelative(0, y)
                    }
                    y += placeable.height
                } else {
                    y += gapHeights[index]
                }
            }
        }
    }
}
