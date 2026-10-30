package com.lu4p.fokuslauncher.data.model

import androidx.annotation.StringRes
import com.lu4p.fokuslauncher.R

enum class AirQualityScale(@param:StringRes val labelRes: Int) {
    SYSTEM_DEFAULT(R.string.settings_air_quality_scale_system),
    EUROPEAN(R.string.settings_air_quality_scale_european),
    US(R.string.settings_air_quality_scale_us);

    companion object {
        fun fromString(value: String?): AirQualityScale =
                entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: SYSTEM_DEFAULT
    }
}
