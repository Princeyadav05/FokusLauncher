package com.lu4p.fokuslauncher.ui.home

import com.lu4p.fokuslauncher.data.model.AppInfo
import com.lu4p.fokuslauncher.data.model.FavoriteApp
import com.lu4p.fokuslauncher.data.model.HomeShortcut
import com.lu4p.fokuslauncher.data.model.ShortcutTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeFavoriteIconsTest {
    private val host = AppInfo("com.example.browser", "Browser", null)
    private val shortcut = host.copy(label = "Web app", launcherShortcutId = "pwa-1")

    @Test
    fun hostFavoriteDoesNotPickShortcutIcon() {
        assertEquals(host, findHomeFavoriteIconApp(FavoriteApp("Browser", host.packageName), listOf(shortcut, host)))
        assertNull(findHomeFavoriteIconApp(FavoriteApp("Browser", host.packageName), listOf(shortcut)))
    }

    @Test
    fun shortcutFavoritePicksOnlyMatchingShortcut() {
        val favorite = FavoriteApp("Web app", host.packageName, iconPackage =
            ShortcutTarget.encode(ShortcutTarget.LauncherShortcut(host.packageName, "pwa-1")))
        assertEquals(shortcut, findHomeFavoriteIconApp(favorite, listOf(host, shortcut)))
    }

    @Test
    fun appShortcutPicksHostNotPwaRow() {
        val appShortcut = HomeShortcut(target = ShortcutTarget.App(host.packageName))
        assertEquals(host, findHomeShortcutIconApp(appShortcut, listOf(shortcut, host)))
        assertNull(findHomeShortcutIconApp(appShortcut, listOf(shortcut)))
    }

    @Test
    fun shortcutInOtherProfileHasNoIconApp() {
        val workShortcut = HomeShortcut(target = ShortcutTarget.App(host.packageName), profileKey = "10")
        assertNull(findHomeShortcutIconApp(workShortcut, listOf(host)))
    }

    @Test
    fun onlyAppShortcutsHaveIconApp() {
        listOf(
            ShortcutTarget.LauncherShortcut(host.packageName, "pwa-1"),
            ShortcutTarget.DeepLink("intent:#Intent;package=${host.packageName};end"),
            ShortcutTarget.PhoneDial,
            ShortcutTarget.WidgetPage,
            ShortcutTarget.UnlockPrivateSpace,
        ).forEach { target ->
            assertNull(findHomeShortcutIconApp(HomeShortcut(target = target), listOf(host, shortcut)))
        }
    }

    @Test
    fun homeIconModeDefaultsToTextForMissingOrUnknownValues() {
        assertEquals(HomeAppIconMode.TEXT, HomeAppIconMode.fromStored(null))
        assertEquals(HomeAppIconMode.TEXT, HomeAppIconMode.fromStored("unknown"))
        assertEquals(HomeAppIconMode.WITH_LABEL, HomeAppIconMode.fromStored("WITH_LABEL"))
        assertEquals(HomeAppIconMode.ICON_ONLY, HomeAppIconMode.fromStored("ICON_ONLY"))
    }
}
