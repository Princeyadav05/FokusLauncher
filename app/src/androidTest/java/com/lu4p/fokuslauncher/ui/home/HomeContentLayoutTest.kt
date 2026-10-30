package com.lu4p.fokuslauncher.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lu4p.fokuslauncher.data.model.PhotoWallpaperOutlineWidthDp
import com.lu4p.fokuslauncher.ui.components.NoteWidget
import com.lu4p.fokuslauncher.ui.components.OutlinedText
import com.lu4p.fokuslauncher.ui.theme.FokusLauncherTheme
import com.lu4p.fokuslauncher.ui.theme.LocalPhotoWallpaperOutlineWidthDp
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeContentLayoutTest {
    @get:Rule val composeTestRule = createComposeRule()

    private fun showHome(outlined: Boolean, fontScale: Float, height: Dp = 400.dp) {
        composeTestRule.setContent {
            FokusLauncherTheme(fontScale = fontScale) {
                HomeContentLayout(modifier = Modifier.requiredSize(width = 320.dp, height = height)) {
                    Box(Modifier.fillMaxWidth().height(80.dp).testTag("header"))
                    NoteWidget(
                            text = (1..1000).joinToString("\n") { "A long note, line $it" },
                            outlined = outlined,
                            modifier = Modifier.layoutId(HomeContentSlot.Note).testTag("preview"),
                    )
                    Box(Modifier.fillMaxWidth().height(80.dp).testTag("controls"))
                    Spacer(Modifier.layoutId(HomeContentSlot.Gap))
                    Box(Modifier.fillMaxWidth().height(160.dp)
                            .layoutId(HomeContentSlot.Favorites).testTag("favorites"))
                }
            }
        }
        val header = composeTestRule.onNodeWithTag("header").fetchSemanticsNode().boundsInRoot
        val note = composeTestRule.onNodeWithTag("preview").fetchSemanticsNode().boundsInRoot
        val controls = composeTestRule.onNodeWithTag("controls").fetchSemanticsNode().boundsInRoot
        val favorites = composeTestRule.onNodeWithTag("favorites").fetchSemanticsNode().boundsInRoot
        assertTrue(note.top >= header.bottom)
        assertTrue(note.bottom <= controls.top)
        assertTrue(controls.bottom < favorites.top)
    }

    @Test
    fun longNoteUsesTheSpaceAboveFavorites() {
        showHome(false, 1f, height = 1000.dp)
        val controls = composeTestRule.onNodeWithTag("controls").fetchSemanticsNode().boundsInRoot
        val favorites = composeTestRule.onNodeWithTag("favorites").fetchSemanticsNode().boundsInRoot
        val unusedSpace = with(composeTestRule.density) { (favorites.top - controls.bottom).toDp() }
        assertTrue("The note left $unusedSpace unused", unusedSpace < 64.dp)
    }

    @Test
    fun outlinedTextStaysInsideItsPillWhenHeightIsLimited() {
        var layout: TextLayoutResult? = null
        composeTestRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                    LocalDensity provides Density(density.density, fontScale = 1f),
                    LocalPhotoWallpaperOutlineWidthDp provides PhotoWallpaperOutlineWidthDp.MAX,
            ) {
                OutlinedText(
                        text = AnnotatedString((1..30).joinToString("\n") { "Line $it" }),
                        style = TextStyle(fontSize = 20.sp, lineHeight = 24.sp),
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { layout = it },
                        // Fits five lines, but only four once the pill padding is taken out.
                        modifier = Modifier.width(240.dp).height(130.dp).testTag("pill"),
                )
            }
        }
        composeTestRule.waitForIdle()
        val pill = composeTestRule.onNodeWithTag("pill").fetchSemanticsNode().boundsInRoot
        val verticalPadding = with(composeTestRule.density) { 7.5.dp.toPx() }
        assertTrue(layout!!.size.height + 2 * verticalPadding <= pill.height + 1f)
    }

    @Test fun longPreviewReservesSpaceForControlsAndFavorites() = showHome(false, 1f)

    @Test fun largeTextPreviewReservesSpaceForControlsAndFavorites() = showHome(false, 2f)

    @Test fun outlinedPreviewReservesSpaceForControlsAndFavorites() = showHome(true, 2f)
}
