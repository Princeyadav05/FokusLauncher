package com.lu4p.fokuslauncher.ui.home

import com.lu4p.fokuslauncher.data.model.AppInfo
import com.lu4p.fokuslauncher.data.model.FavoriteApp
import com.lu4p.fokuslauncher.data.model.HomeShortcut
import com.lu4p.fokuslauncher.data.model.ShortcutTarget
import com.lu4p.fokuslauncher.data.model.appProfileKey
import com.lu4p.fokuslauncher.data.model.favoriteLauncherShortcutId

enum class HomeAppIconMode {
    TEXT, WITH_LABEL, ICON_ONLY;

    companion object {
        fun fromStored(value: String?): HomeAppIconMode =
            entries.find { it.name == value } ?: TEXT
    }
}

internal fun findHomeFavoriteIconApp(favorite: FavoriteApp, installedApps: List<AppInfo>): AppInfo? =
    findInstalledApp(
        installedApps,
        favorite.packageName,
        favorite.profileKey,
        favoriteLauncherShortcutId(favorite),
    )

/**
 * App row whose Arcticons icon a home shortcut can show. Only plain app targets qualify: launcher
 * shortcut rows (PWAs) resolve to their host app's icon, which would replace the chosen icon with
 * the browser's on the label-less rail.
 */
internal fun findHomeShortcutIconApp(shortcut: HomeShortcut, installedApps: List<AppInfo>): AppInfo? {
    val target = shortcut.target as? ShortcutTarget.App ?: return null
    return findInstalledApp(installedApps, target.packageName, shortcut.profileKey, null)
}

private fun findInstalledApp(
    installedApps: List<AppInfo>,
    packageName: String,
    profileKey: String,
    launcherShortcutId: String?,
): AppInfo? =
    installedApps.find { app ->
        app.packageName == packageName &&
            appProfileKey(app.userHandle) == profileKey &&
            app.launcherShortcutId == launcherShortcutId
    }
