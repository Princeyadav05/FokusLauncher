package com.lu4p.fokuslauncher.data.util

import com.lu4p.fokuslauncher.data.model.AirQualityScale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AirQualityScaleHelperTest {

    @Test
    fun `automatic uses european aqi in european regions`() {
        listOf("DE", "FR", "GB", "CH", "NO", "de").forEach { region ->
            assertFalse(region, AirQualityScaleHelper.useUsAqi(AirQualityScale.SYSTEM_DEFAULT, region))
        }
    }

    @Test
    fun `automatic uses us aqi outside europe`() {
        listOf("IN", "US", "CN", "BR", "AU", "").forEach { region ->
            assertTrue(region, AirQualityScaleHelper.useUsAqi(AirQualityScale.SYSTEM_DEFAULT, region))
        }
    }

    @Test
    fun `explicit scale overrides the region`() {
        assertTrue(AirQualityScaleHelper.useUsAqi(AirQualityScale.US, "DE"))
        assertFalse(AirQualityScaleHelper.useUsAqi(AirQualityScale.EUROPEAN, "IN"))
    }

    @Test
    fun `fromString falls back to automatic`() {
        assertEquals(AirQualityScale.US, AirQualityScale.fromString("us"))
        assertEquals(AirQualityScale.EUROPEAN, AirQualityScale.fromString("EUROPEAN"))
        assertEquals(AirQualityScale.SYSTEM_DEFAULT, AirQualityScale.fromString(null))
        assertEquals(AirQualityScale.SYSTEM_DEFAULT, AirQualityScale.fromString("bogus"))
    }
}
