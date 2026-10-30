package com.lu4p.fokuslauncher.data.util

import android.content.res.Resources
import com.lu4p.fokuslauncher.data.model.AirQualityScale
import java.util.Locale

object AirQualityScaleHelper {

    /**
     * Regions that use the European AQI: EEA member and cooperating countries, the UK and the
     * European microstates. Every other region uses the US AQI.
     */
    private val EUROPEAN_AQI_REGIONS =
            setOf(
                    "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR", "DE", "GR", "HU",
                    "IE", "IT", "LV", "LT", "LU", "MT", "NL", "PL", "PT", "RO", "SK", "SI", "ES",
                    "SE", "IS", "LI", "NO", "CH", "TR", "AL", "BA", "ME", "MK", "RS", "XK", "GB",
                    "AD", "MC", "SM", "VA",
            )

    fun useUsAqi(override: AirQualityScale = AirQualityScale.SYSTEM_DEFAULT): Boolean =
            useUsAqi(override, systemRegion())

    fun useUsAqi(override: AirQualityScale, region: String): Boolean {
        return when (override) {
            AirQualityScale.US -> true
            AirQualityScale.EUROPEAN -> false
            AirQualityScale.SYSTEM_DEFAULT -> region.uppercase(Locale.ROOT) !in EUROPEAN_AQI_REGIONS
        }
    }

    private fun systemRegion(): String {
        val locales = Resources.getSystem().configuration.locales
        val primary = if (locales.size() > 0) locales[0] else Locale.getDefault()
        return primary.country
    }
}
