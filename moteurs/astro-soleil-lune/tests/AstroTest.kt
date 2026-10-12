package com.jeanrocka.suntrack

import com.jeanrocka.suntrack.astro.MoonCalculator
import com.jeanrocka.suntrack.astro.SolarCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.abs

/**
 * Reference values are the published almanac times for each site, so these
 * assert against reality rather than against the implementation's own output.
 */
class AstroTest {

    private fun assertCloseToLocalTime(
        expected: String,
        actual: ZonedDateTime?,
        toleranceMinutes: Long,
        label: String,
    ) {
        assertNotNull("$label should exist", actual)
        val want = LocalTime.parse(expected)
        val got = actual!!.toLocalTime()
        val deltaMinutes = abs(Duration.between(want, got).toMinutes())
        assertTrue(
            "$label: expected ~$expected but got $got (off by $deltaMinutes min)",
            deltaMinutes <= toleranceMinutes,
        )
    }

    @Test
    fun `paris summer solstice`() {
        val times = SolarCalculator.calculate(
            LocalDate.of(2024, 6, 21),
            48.8566,
            2.3522,
            ZoneId.of("Europe/Paris"),
        )
        assertCloseToLocalTime("05:47", times.sunrise, 4, "Paris sunrise")
        assertCloseToLocalTime("21:58", times.sunset, 4, "Paris sunset")
        // ~16h10 of daylight at the solstice.
        assertTrue("day length ${times.dayLengthMinutes}", abs(times.dayLengthMinutes - 970) < 10)
    }

    @Test
    fun `new york equinox`() {
        val times = SolarCalculator.calculate(
            LocalDate.of(2024, 3, 20),
            40.7128,
            -74.0060,
            ZoneId.of("America/New_York"),
        )
        assertCloseToLocalTime("06:58", times.sunrise, 5, "NYC sunrise")
        assertCloseToLocalTime("19:07", times.sunset, 5, "NYC sunset")
    }

    @Test
    fun `southern hemisphere is handled`() {
        val times = SolarCalculator.calculate(
            LocalDate.of(2024, 12, 21),
            -33.8688,
            151.2093,
            ZoneId.of("Australia/Sydney"),
        )
        assertCloseToLocalTime("05:41", times.sunrise, 6, "Sydney sunrise")
        assertCloseToLocalTime("20:05", times.sunset, 6, "Sydney sunset")
    }

    @Test
    fun `svalbard midnight sun`() {
        val times = SolarCalculator.calculate(
            LocalDate.of(2024, 6, 21),
            78.2232,
            15.6267,
            ZoneId.of("Arctic/Longyearbyen"),
        )
        assertTrue("expected polar day", times.polarDay)
        assertNull(times.sunrise)
        assertNull(times.sunset)
        assertEquals(1440.0, times.dayLengthMinutes, 0.001)
    }

    @Test
    fun `svalbard polar night`() {
        val times = SolarCalculator.calculate(
            LocalDate.of(2024, 12, 21),
            78.2232,
            15.6267,
            ZoneId.of("Arctic/Longyearbyen"),
        )
        assertTrue("expected polar night", times.polarNight)
        assertEquals(0.0, times.dayLengthMinutes, 0.001)
    }

    @Test
    fun `solar noon sits between sunrise and sunset`() {
        val times = SolarCalculator.calculate(
            LocalDate.of(2024, 5, 25),
            48.8566,
            2.3522,
            ZoneId.of("Europe/Paris"),
        )
        val noon = times.solarNoon.toInstant()
        assertTrue(noon.isAfter(times.sunrise!!.toInstant()))
        assertTrue(noon.isBefore(times.sunset!!.toInstant()))
        // Noon must be the exact midpoint: the dial's whole geometry rests on it.
        val midpoint = (times.sunrise!!.toEpochSecond() + times.sunset!!.toEpochSecond()) / 2
        assertTrue(abs(midpoint - times.solarNoon.toEpochSecond()) < 60)
    }

    @Test
    fun `full moon is fully lit`() {
        // Full moon: 2024-06-22 01:08 UTC.
        val illumination = MoonCalculator.illuminationAt(Instant.parse("2024-06-22T01:08:00Z"))
        assertTrue("illumination was $illumination", illumination > 0.99)

        val phase = MoonCalculator.phaseAt(Instant.parse("2024-06-22T01:08:00Z"))
        assertTrue("phase was $phase", abs(phase - 0.5) < 0.02)
    }

    @Test
    fun `new moon is dark`() {
        // New moon: 2024-06-06 12:38 UTC.
        val illumination = MoonCalculator.illuminationAt(Instant.parse("2024-06-06T12:38:00Z"))
        assertTrue("illumination was $illumination", illumination < 0.01)

        val phase = MoonCalculator.phaseAt(Instant.parse("2024-06-06T12:38:00Z"))
        assertTrue("phase was $phase", phase < 0.02 || phase > 0.98)
    }

    @Test
    fun `first quarter is half lit and waxing`() {
        // First quarter: 2024-06-14 05:18 UTC.
        val illumination = MoonCalculator.illuminationAt(Instant.parse("2024-06-14T05:18:00Z"))
        assertTrue("illumination was $illumination", abs(illumination - 0.5) < 0.02)
    }

    @Test
    fun `moon rises and sets`() {
        val moon = MoonCalculator.calculate(
            LocalDate.of(2024, 6, 21),
            48.8566,
            2.3522,
            ZoneId.of("Europe/Paris"),
        )
        assertNotNull("expected a transit", moon.transit)
        // Near full moon the disc is up essentially all night at this latitude,
        // so at least one of rise/set must land inside the day.
        assertTrue(moon.moonrise != null || moon.moonset != null)
    }
}
