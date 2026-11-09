package com.jeanrocka.suntrack.astro

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

/**
 * Sunrise / sunset / solar noon via the NOAA solar position algorithm.
 *
 * Pure arithmetic, no network and no Android dependencies, so the dial keeps
 * working offline and can be unit-tested on the JVM. Accuracy is well under a
 * minute for temperate latitudes, degrading near the poles where the sun grazes
 * the horizon -- exactly where the polar-day/night flags take over anyway.
 */
object SolarCalculator {

    /** Zenith angle of the sun's centre at rise/set, including refraction. */
    private const val ZENITH_OFFICIAL = 90.833

    fun calculate(date: LocalDate, latitude: Double, longitude: Double, zone: ZoneId): SunTimes {
        // Anchor on the UTC date that contains local noon. Deriving the Julian
        // day straight from the local date would slip a day for zones far from
        // Greenwich, which is what makes naive implementations return
        // yesterday's sunset east of the date line.
        val localNoon = date.atTime(LocalTime.NOON).atZone(zone)
        val jd = julianDay(localNoon.withZoneSameInstant(ZoneId.of("UTC")).toLocalDate())

        // First pass gives an approximate noon; the second pass evaluates the
        // orbital terms *at* that noon, which is what NOAA's own spreadsheet does.
        val tRough = julianCenturies(jd)
        val noonRough = 720.0 - 4.0 * longitude - equationOfTime(tRough)
        val t = julianCenturies(jd + noonRough / 1440.0)

        val eqTime = equationOfTime(t)
        val declination = sunDeclination(t)
        val solarNoonMinutes = 720.0 - 4.0 * longitude - eqTime

        val hourAngle = sunriseHourAngle(latitude, declination)

        val solarNoon = utcMinutesToZoned(jd, solarNoonMinutes, zone)

        if (hourAngle == null) {
            // The sun never reaches the horizon: decide which side we are on by
            // testing the sun's altitude at local solar noon.
            val noonAltitude = 90.0 - abs(latitude - declination)
            val polarDay = noonAltitude > (90.0 - ZENITH_OFFICIAL)
            return SunTimes(
                sunrise = null,
                sunset = null,
                solarNoon = solarNoon,
                dayLengthMinutes = if (polarDay) 1440.0 else 0.0,
                polarDay = polarDay,
                polarNight = !polarDay,
                declinationDeg = declination,
            )
        }

        val haMinutes = 4.0 * Math.toDegrees(hourAngle)
        val sunrise = utcMinutesToZoned(jd, solarNoonMinutes - haMinutes, zone)
        val sunset = utcMinutesToZoned(jd, solarNoonMinutes + haMinutes, zone)

        return SunTimes(
            sunrise = sunrise,
            sunset = sunset,
            solarNoon = solarNoon,
            dayLengthMinutes = 2.0 * haMinutes,
            polarDay = false,
            polarNight = false,
            declinationDeg = declination,
        )
    }

    /**
     * Altitude of the sun's centre in degrees, without refraction, at
     * [solarMinutes] of true solar time (720 = solar noon, 0 = solar midnight).
     */
    fun altitudeDeg(solarMinutes: Double, latitude: Double, declinationDeg: Double): Double {
        val hourAngle = Math.toRadians(solarMinutes / 4.0 - 180.0)
        val lat = Math.toRadians(latitude)
        val dec = Math.toRadians(declinationDeg)
        return Math.toDegrees(asin(sin(lat) * sin(dec) + cos(lat) * cos(dec) * cos(hourAngle)))
    }

    // ---- orbital mechanics -------------------------------------------------

    /** Julian day at 00:00 UT of the given (UTC) calendar date. */
    internal fun julianDay(date: LocalDate): Double {
        var year = date.year
        var month = date.monthValue
        if (month <= 2) {
            year -= 1
            month += 12
        }
        val a = floor(year / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (year + 4716)) +
            floor(30.6001 * (month + 1)) +
            date.dayOfMonth + b - 1524.5
    }

    private fun julianCenturies(jd: Double): Double = (jd - 2451545.0) / 36525.0

    private fun geomMeanLongSun(t: Double): Double {
        var l0 = 280.46646 + t * (36000.76983 + t * 0.0003032)
        l0 %= 360.0
        if (l0 < 0) l0 += 360.0
        return l0
    }

    private fun geomMeanAnomalySun(t: Double): Double =
        357.52911 + t * (35999.05029 - 0.0001537 * t)

    private fun eccentricityEarthOrbit(t: Double): Double =
        0.016708634 - t * (0.000042037 + 0.0000001267 * t)

    private fun sunEqOfCenter(t: Double): Double {
        val m = Math.toRadians(geomMeanAnomalySun(t))
        return sin(m) * (1.914602 - t * (0.004817 + 0.000014 * t)) +
            sin(2 * m) * (0.019993 - 0.000101 * t) +
            sin(3 * m) * 0.000289
    }

    private fun sunApparentLong(t: Double): Double {
        val trueLong = geomMeanLongSun(t) + sunEqOfCenter(t)
        val omega = 125.04 - 1934.136 * t
        return trueLong - 0.00569 - 0.00478 * sin(Math.toRadians(omega))
    }

    private fun obliquityCorrection(t: Double): Double {
        val seconds = 21.448 - t * (46.815 + t * (0.00059 - t * 0.001813))
        val e0 = 23.0 + (26.0 + seconds / 60.0) / 60.0
        val omega = 125.04 - 1934.136 * t
        return e0 + 0.00256 * cos(Math.toRadians(omega))
    }

    /** Sun's declination in degrees. */
    internal fun sunDeclination(t: Double): Double {
        val e = Math.toRadians(obliquityCorrection(t))
        val lambda = Math.toRadians(sunApparentLong(t))
        return Math.toDegrees(asin(sin(e) * sin(lambda)))
    }

    /** Difference between apparent and mean solar time, in minutes. */
    private fun equationOfTime(t: Double): Double {
        val epsilon = Math.toRadians(obliquityCorrection(t))
        val l0 = Math.toRadians(geomMeanLongSun(t))
        val e = eccentricityEarthOrbit(t)
        val m = Math.toRadians(geomMeanAnomalySun(t))

        var y = tan(epsilon / 2.0)
        y *= y

        val eTime = y * sin(2 * l0) -
            2 * e * sin(m) +
            4 * e * y * sin(m) * cos(2 * l0) -
            0.5 * y * y * sin(4 * l0) -
            1.25 * e * e * sin(2 * m)

        return Math.toDegrees(eTime) * 4.0
    }

    /**
     * Hour angle from solar noon to sunrise, in radians, or null when the sun
     * stays entirely above or below the horizon for the whole day.
     */
    private fun sunriseHourAngle(latitude: Double, declination: Double): Double? {
        val latRad = Math.toRadians(latitude)
        val decRad = Math.toRadians(declination)
        val cosH = cos(Math.toRadians(ZENITH_OFFICIAL)) /
            (cos(latRad) * cos(decRad)) - tan(latRad) * tan(decRad)
        if (cosH > 1.0 || cosH < -1.0) return null
        return acos(cosH)
    }

    private fun utcMinutesToZoned(jd: Double, minutesUtc: Double, zone: ZoneId): ZonedDateTime {
        // jd is 00:00 UT; convert back to an epoch instant and add the offset.
        val epochSeconds = (jd - 2440587.5) * 86400.0 + minutesUtc * 60.0
        return Instant.ofEpochMilli(Math.round(epochSeconds * 1000.0)).atZone(zone)
    }
}
