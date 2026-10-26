package com.lu4p.fokuslauncher.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.core.app.ActivityScenario
import com.lu4p.fokuslauncher.MainActivity
import com.lu4p.fokuslauncher.R
import com.lu4p.fokuslauncher.data.local.PreferencesManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DrawerSettingsNavigationTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val compose = createEmptyComposeRule()

    @Inject lateinit var preferences: PreferencesManager

    @Before
    fun setUp() {
        hiltRule.inject()
        runBlocking {
            preferences.setHasCompletedOnboarding(true)
            preferences.setDrawerSidebarCategories(false)
        }
    }

    @Test
    fun systemBackFromDrawerSettingsReturnsToHomeWithoutStaleDrawerOrIme() {
        checkSettingsRoundTrip(sidebar = false, toolbarBack = false)
    }

    @Test
    fun toolbarBackFromDrawerSettingsReturnsToHomeWithoutStaleDrawerOrIme() {
        checkSettingsRoundTrip(sidebar = false, toolbarBack = true)
    }

    @Test
    fun sidebarDrawerSettingsReturnsToHomeWithoutStaleDrawer() {
        checkSettingsRoundTrip(sidebar = true, toolbarBack = false)
    }

    private fun checkSettingsRoundTrip(sidebar: Boolean, toolbarBack: Boolean) {
        runBlocking { preferences.setDrawerSidebarCategories(sidebar) }
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            repeat(3) {
                compose.onNodeWithTag("clock_widget").assertIsDisplayed()
                compose.onRoot().performTouchInput { swipeUp() }
                if (!sidebar) {
                    compose.onNodeWithTag("search_bar").performTextInput("no matching app regression")
                }
                compose.onNodeWithTag("settings_button").performClick()
                compose.onNodeWithTag("menu_settings").performClick()
                compose.onNodeWithTag("settings_screen").assertIsDisplayed()
                if (toolbarBack) {
                    var backDescription = ""
                    scenario.onActivity { backDescription = it.getString(R.string.action_back) }
                    compose.onNodeWithContentDescription(backDescription).performClick()
                } else {
                    scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
                }
                compose.waitForIdle()
                compose.onNodeWithTag("settings_button").assertDoesNotExist()
                compose.onNodeWithTag("search_bar").assertDoesNotExist()
                compose.onNodeWithTag("clock_widget").assertIsDisplayed()
                compose.waitUntil(timeoutMillis = 5_000) {
                    var imeVisible = true
                    scenario.onActivity {
                        imeVisible = ViewCompat.getRootWindowInsets(it.window.decorView)
                            ?.isVisible(WindowInsetsCompat.Type.ime()) == true
                    }
                    !imeVisible
                }
            }
        }
    }
}
