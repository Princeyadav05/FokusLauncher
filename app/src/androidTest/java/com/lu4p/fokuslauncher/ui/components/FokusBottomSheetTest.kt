package com.lu4p.fokuslauncher.ui.components

import android.graphics.Color
import android.os.Build
import android.view.Window
import android.view.WindowInsetsController
import androidx.compose.material3.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.lu4p.fokuslauncher.ui.theme.FokusLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class FokusBottomSheetTest {
    @get:Rule val composeTestRule = createComposeRule()

    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    @Test
    fun contextMenu_keepsNavigationBarTransparentWithoutContrastScrim() {
        var sheetWindow: Window? = null
        composeTestRule.setContent {
            FokusLauncherTheme {
                FokusBottomSheet(onDismissRequest = {}) {
                    val view = LocalView.current
                    SideEffect {
                        sheetWindow = (view.parent as? DialogWindowProvider)?.window
                    }
                    Text("Context menu")
                }
            }
        }
        composeTestRule.runOnIdle {
            assertNotNull("The sheet must have its own dialog window", sheetWindow)
            val window = requireNotNull(sheetWindow)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                assertFalse("The dialog must not add a three-button navigation scrim",
                        window.isNavigationBarContrastEnforced)
            }
            @Suppress("DEPRECATION")
            assertEquals(Color.TRANSPARENT, window.navigationBarColor)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                assertEquals(0, requireNotNull(window.insetsController).systemBarsAppearance and
                        (WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS or
                                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS))
            }
            assertFalse(WindowCompat.getInsetsController(window, window.decorView)
                    .isAppearanceLightNavigationBars)
        }
    }
}
