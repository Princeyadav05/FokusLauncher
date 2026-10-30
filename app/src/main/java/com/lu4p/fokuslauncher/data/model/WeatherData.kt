package com.lu4p.fokuslauncher.data.model

/**
 * Simple weather data for the home screen widget.
 */
data class WeatherData(
    val temperature: Int = 0,
    val description: String = "",
    /** Open-Meteo-style icon code (e.g. `01d`, `10n`) for mapping to a themed weather icon. */
    val iconCode: String = "",
    /**
     * Consolidated air quality index from Open-Meteo when requested (US or European AQI, per the
     * air quality scale setting). Null when AQI was not fetched or the air-quality call failed.
     */
    val aqi: Int? = null,
    val lastUpdated: Long = 0L
)
