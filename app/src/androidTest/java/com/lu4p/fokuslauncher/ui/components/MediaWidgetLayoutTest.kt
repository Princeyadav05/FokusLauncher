package com.lu4p.fokuslauncher.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.lu4p.fokuslauncher.data.model.PhotoWallpaperOutlineWidthDp
import com.lu4p.fokuslauncher.media.MediaCustomActionButton
import com.lu4p.fokuslauncher.ui.theme.FokusLauncherTheme
import com.lu4p.fokuslauncher.ui.theme.LocalPhotoWallpaperOutlineWidthDp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MediaWidgetLayoutTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun transportControlsStayLeftAligned() {
        composeTestRule.setContent {
            FokusLauncherTheme {
                MediaWidget(
                    title = "Track",
                    artist = "Artist",
                    isPlaying = true,
                    canSkipToPrevious = true,
                    canSkipToNext = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        val widget = composeTestRule.onNodeWithTag("media_widget").getUnclippedBoundsInRoot()
        val previous = composeTestRule.onNodeWithTag("media_previous").getUnclippedBoundsInRoot()
        assertEquals(widget.left.value, previous.left.value, 1f)
    }

    private fun showWithStrongestBackdrop() {
        composeTestRule.setContent {
            FokusLauncherTheme {
                CompositionLocalProvider(
                    LocalPhotoWallpaperOutlineWidthDp provides PhotoWallpaperOutlineWidthDp.MAX
                ) {
                    MediaWidget(
                        title = "Track",
                        artist = "Artist",
                        isPlaying = true,
                        canSkipToPrevious = true,
                        canSkipToNext = true,
                        outlined = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }

    @Test
    fun backdropPillsStayLeftAligned() {
        showWithStrongestBackdrop()

        val widget = composeTestRule.onNodeWithTag("media_widget").getUnclippedBoundsInRoot()
        val title = composeTestRule.onNodeWithTag("media_title", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val artist = composeTestRule.onNodeWithTag("media_artist", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(widget.left.value, title.left.value, 1f)
        assertEquals(widget.left.value, artist.left.value, 1f)
        assertTrue(title.right < widget.right)
    }

    @Test
    fun iconBackdropsStayInsideTheWidgetWithoutOverlap() {
        showWithStrongestBackdrop()

        val widget = composeTestRule.onNodeWithTag("media_widget").getUnclippedBoundsInRoot()
        val artist = composeTestRule.onNodeWithTag("media_artist", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val previous = composeTestRule.onNodeWithTag("media_previous").getUnclippedBoundsInRoot()
        val playPause = composeTestRule.onNodeWithTag("media_play_pause").getUnclippedBoundsInRoot()
        val backdrop = launcherIconBackdropPadding(PhotoWallpaperOutlineWidthDp.MAX)
        assertEquals((widget.left + backdrop).value, previous.left.value, 1f)
        // Layout rounds to whole pixels, so allow for one density-independent pixel.
        assertTrue(previous.top - backdrop >= artist.bottom - 1.dp)
        assertTrue(playPause.left - backdrop >= previous.right + backdrop - 1.dp)
        assertTrue(previous.bottom + backdrop <= widget.bottom + 1.dp)
    }

    @Test
    fun allControlsKeepTheirSizeOnNarrowScreensWithStrongestBackdrop() {
        val action = MediaCustomActionButton("id", "label", active = false, extras = null)
        composeTestRule.setContent {
            FokusLauncherTheme(fontScale = 1.5f) {
                CompositionLocalProvider(
                    LocalPhotoWallpaperOutlineWidthDp provides PhotoWallpaperOutlineWidthDp.MAX
                ) {
                    MediaWidget(
                        title = "Track",
                        artist = null,
                        isPlaying = true,
                        canSkipToPrevious = true,
                        canSkipToNext = true,
                        like = action,
                        save = action,
                        outlined = true,
                        modifier = Modifier.width(296.dp),
                    )
                }
            }
        }

        val save = composeTestRule.onNodeWithTag("media_save").getUnclippedBoundsInRoot()
        assertEquals(36f, (save.right - save.left).value, 2f)
    }

    @Test
    fun largeLauncherFontScaleScalesControlIconsOnce() {
        composeTestRule.setContent {
            FokusLauncherTheme(fontScale = 1.5f) {
                MediaWidget(
                    title = "Track",
                    artist = null,
                    isPlaying = false,
                    canSkipToPrevious = true,
                    canSkipToNext = true,
                )
            }
        }

        val icon = composeTestRule.onNodeWithTag("media_previous").getUnclippedBoundsInRoot()

        val iconWidth = icon.right - icon.left
        assertEquals(36f, iconWidth.value, 1f)
        assertTrue(iconWidth < 40.dp)
    }
}
