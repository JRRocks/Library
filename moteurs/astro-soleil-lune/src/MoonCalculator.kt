package com.jeanrocka.suntrack.astro

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Moon phase, illumination and rise/set/transit times.
 *
 * Uses the classic truncated lunar theory (main ecliptic perturbation terms),
 * which lands within a couple of arcminutes -- far tighter than the ~1 px the
 * dial can actually resolve. Rise and set are found by sampling the topocentric
 * altitude across the local day and interpolating the horizon crossings, which
 * sidesteps the convergence problems an analytic solution hits on days when the
 * moon barely grazes the horizon.
 */
object MoonCalculator {

    /** Altitude of the moon's centre at rise/set: parallax minus refraction. */
    private const val HORIZON_ALTITUDE = 0.125

    private const val SAMPLE_MINUTES = 10

    /** Padding either side of the local day when hunting for the transit. */
    private const val PAD_MINUTES = 120

    fun calculate(date: LocalDate, latitude: Double, longitude: Double, zone: ZoneId): MoonTimes {
        val startOfDay = date.atStartOfDay(zone)

        val phase = phaseAt(startOfDay.plusHours(12).toInstant())
        val illumination = illuminationAt(startOfDay.plusHours(12).toInstant())

        // Sample a window wider than the day itself. A lunar day is ~24h50m, so
        // roughly one calendar day in 25 contains no transit at all; padding
        // both ends guarantees an interior peak exists to be found.
        val padSteps = PAD_MINUTES / SAMPLE_MINUTES
        val daySteps = 24 * 60 / SAMPLE_MINUTES
        val first = -padSteps
        val last = daySteps + padSteps

        val altitudes = HashMap<Int, Double>((last - first + 1) * 2)
        for (i in first..last) {
            val instant = startOfDay.plusMinutes(i.toLong() * SAMPLE_MINUTES).toInstant()
            altitudes[i] = topocentricAltitude(instant, latitude, longitude)
        }

        // Rise and set are only reported when they fall inside the local day.
        var moonrise: ZonedDateTime? = null
        var moonset: ZonedDateTime? = null
        for (i in 0 until daySteps) {
            val a = altitudes.getValue(i) - HORIZON_ALTITUDE
            val b = altitudes.getValue(i + 1) - HORIZON_ALTITUDE
            if (a < 0 && b >= 0 && moonrise == null) {
                moonrise = interpolateCrossing(startOfDay, i, a, b, zone)
            } else if (a >= 0 && b < 0 && moonset == null) {
                moonset = interpolateCrossing(startOfDay, i, a, b, zone)
            }
        }

        // Transit = the first interior local maximum. Taking a plain global
        // maximum would happily return a window edge, which is not a transit.
        var peak: Int? = null
        for (i in (first + 1) until last) {
            if (altitudes.getValue(i) > altitudes.getValue(i - 1) &&
                altitudes.getValue(i) >= altitudes.getValue(i + 1)
            ) {
                peak = i
                break
            }
        }

        val transit = peak?.let { p ->
            // Refine by fitting a parabola through the peak and its neighbours.
            val y0 = altitudes.getValue(p - 1)
            val y1 = altitudes.getValue(p)
            val y2 = altitudes.getValue(p + 1)
            val denom = y0 - 2 * y1 + y2
            val shift = if (abs(denom) < 1e-9) 0.0 else 0.5 * (y0 - y2) / denom
            startOfDay.plusSeconds(Math.round((p + shift) * SAMPLE_MINUTES * 60.0))
        }

        return MoonTimes(
            phase = phase,
            illumination = illumination,
            phaseName = nameFor(phase),
            moonrise = moonrise,
            moonset = moonset,
            transit = transit,
        )
    }

    private fun interpolateCrossing(
        startOfDay: ZonedDateTime,
        index: Int,
        a: Double,
        b: Double,
        zone: ZoneId,
    ): ZonedDateTime {
        val fraction = a / (a - b)
        val minutes = (index + fraction) * SAMPLE_MINUTES
        return startOfDay.plusSeconds(Math.round(minutes * 60.0)).withZoneSameInstant(zone)
    }

    // ---- phase -------------------------------------------------------------

    /** 0 = new, 0.25 = first quarter, 0.5 = full, 0.75 = last quarter. */
    fun phaseAt(instant: Instant): Double {
        val d = daysSinceJ2000(instant)
        val moonLon = moonEcliptic(d).longitude
        val sunLon = sunEclipticLongitude(d)
        var elongation = (moonLon - sunLon) % 360.0
        if (elongation < 0) elongation += 360.0
        return elongation / 360.0
    }

    /** Lit fraction of the visible disc, 0..1. */
    fun illuminationAt(instant: Instant): Double {
        val d = daysSinceJ2000(instant)
        val moon = moonEcliptic(d)
        val sunLon = sunEclipticLongitude(d)
        // Elongation measured on the sphere, so the moon's ecliptic latitude
        // is accounted for rather than assumed zero.
        val elongation = Math.toDegrees(
            acos(
                (cos(Math.toRadians(moon.longitude - sunLon)) * cos(Math.toRadians(moon.latitude)))
                    .coerceIn(-1.0, 1.0)
            )
        )
        val phaseAngle = 180.0 - elongation
        return (1.0 + cos(Math.toRadians(phaseAngle))) / 2.0
    }

    private fun nameFor(phase: Double): MoonPhase = when {
        phase < 0.0213 || phase >= 0.9787 -> MoonPhase.NEW
        phase < 0.2287 -> MoonPhase.WAXING_CRESCENT
        phase < 0.2713 -> MoonPhase.FIRST_QUARTER
        phase < 0.4787 -> MoonPhase.WAXING_GIBBOUS
        phase < 0.5213 -> MoonPhase.FULL
        phase < 0.7287 -> MoonPhase.WANING_GIBBOUS
        phase < 0.7713 -> MoonPhase.LAST_QUARTER
        else -> MoonPhase.WANING_CRESCENT
    }

    // ---- position ----------------------------------------------------------

    private class Ecliptic(val longitude: Double, val latitude: Double, val distance: Double)

    // The Schlyter orbital elements below (N, i, w, a, e, M) are anchored to
    // "Epoch 2000.0 = Jan 0.0 2000 UT" (= 1999-12-31T00:00:00Z), which is 1.5
    // days before the more familiar J2000.0 (2000-01-01T12:00:00Z). Using the
    // J2000.0 offset here left the mean anomaly ~19.6 degrees behind and threw
    // off phase/illumination/rise-set by the same margin.
    private fun daysSinceJ2000(instant: Instant): Double =
        instant.toEpochMilli() / 86400000.0 - 10956.0

    private fun norm360(v: Double): Double {
        var x = v % 360.0
        if (x < 0) x += 360.0
        return x
    }

    private fun sunEclipticLongitude(d: Double): Double {
        val w = 282.9404 + 4.70935e-5 * d
        val m = norm360(356.0470 + 0.9856002585 * d)
        val e = 0.016709 - 1.151e-9 * d
        val mRad = Math.toRadians(m)
        val eccAnomaly = m + Math.toDegrees(e * sin(mRad) * (1.0 + e * cos(mRad)))
        val eaRad = Math.toRadians(eccAnomaly)
        val x = cos(eaRad) - e
        val y = sqrt(1.0 - e * e) * sin(eaRad)
        val trueAnomaly = Math.toDegrees(atan2(y, x))
        return norm360(trueAnomaly + w)
    }

    /** Sun's mean longitude, reused for sidereal time. */
    private fun sunMeanLongitude(d: Double): Double {
        val w = 282.9404 + 4.70935e-5 * d
        val m = 356.0470 + 0.9856002585 * d
        return norm360(w + m)
    }

    private fun moonEcliptic(d: Double): Ecliptic {
        val n = norm360(125.1228 - 0.0529538083 * d)
        val i = 5.1454
        val w = norm360(318.0634 + 0.1643573223 * d)
        val a = 60.2666
        val e = 0.054900
        val m = norm360(115.3654 + 13.0649929509 * d)

        // Kepler, iterated -- the moon's eccentricity is large enough that the
        // single-step approximation used for the sun is not good enough here.
        var eccAnomaly = m + Math.toDegrees(e * sin(Math.toRadians(m)) * (1.0 + e * cos(Math.toRadians(m))))
        repeat(5) {
            val eaRad = Math.toRadians(eccAnomaly)
            val delta = (eccAnomaly - Math.toDegrees(e * sin(eaRad)) - m) /
                (1.0 - e * cos(eaRad))
            eccAnomaly -= delta
        }

        val eaRad = Math.toRadians(eccAnomaly)
        val xv = a * (cos(eaRad) - e)
        val yv = a * sqrt(1.0 - e * e) * sin(eaRad)
        val r0 = sqrt(xv * xv + yv * yv)
        val v = Math.toDegrees(atan2(yv, xv))

        val vwRad = Math.toRadians(v + w)
        val nRad = Math.toRadians(n)
        val iRad = Math.toRadians(i)

        val xh = r0 * (cos(nRad) * cos(vwRad) - sin(nRad) * sin(vwRad) * cos(iRad))
        val yh = r0 * (sin(nRad) * cos(vwRad) + cos(nRad) * sin(vwRad) * cos(iRad))
        val zh = r0 * sin(vwRad) * sin(iRad)

        var lon = Math.toDegrees(atan2(yh, xh))
        var lat = Math.toDegrees(atan2(zh, sqrt(xh * xh + yh * yh)))
        var dist = r0

        // Principal perturbations from the sun. Dropping these costs up to
        // ~1.3 degrees of longitude, which is a visible error on the dial.
        val ls = sunMeanLongitude(d)
        val ms = norm360(356.0470 + 0.9856002585 * d)
        val lm = norm360(n + w + m)
        val elong = norm360(lm - ls)
        val f = norm360(lm - n)

        val mmR = Math.toRadians(m)
        val msR = Math.toRadians(ms)
        val dR = Math.toRadians(elong)
        val fR = Math.toRadians(f)

        lon += -1.274 * sin(mmR - 2 * dR) +
            0.658 * sin(2 * dR) -
            0.186 * sin(msR) -
            0.059 * sin(2 * mmR - 2 * dR) -
            0.057 * sin(mmR - 2 * dR + msR) +
            0.053 * sin(mmR + 2 * dR) +
            0.046 * sin(2 * dR - msR) +
            0.041 * sin(mmR - msR) -
            0.035 * sin(dR) -
            0.031 * sin(mmR + msR) -
            0.015 * sin(2 * fR - 2 * dR) +
            0.011 * sin(mmR - 4 * dR)

        lat += -0.173 * sin(fR - 2 * dR) -
            0.055 * sin(mmR - fR - 2 * dR) -
            0.046 * sin(mmR + fR - 2 * dR) +
            0.033 * sin(fR + 2 * dR) +
            0.017 * sin(2 * mmR + fR)

        dist += -0.58 * cos(mmR - 2 * dR) - 0.46 * cos(2 * dR)

        return Ecliptic(norm360(lon), lat, dist)
    }

    /**
     * Moon altitude in degrees, corrected for the observer sitting on the
     * surface rather than at the Earth's centre. The parallax correction is
     * close to a full degree and therefore decides rise/set to within an hour.
     */
    private fun topocentricAltitude(instant: Instant, latitude: Double, longitude: Double): Double {
        val d = daysSinceJ2000(instant)
        val moon = moonEcliptic(d)

        val ecl = Math.toRadians(23.4393 - 3.563e-7 * d)
        val lonRad = Math.toRadians(moon.longitude)
        val latRad = Math.toRadians(moon.latitude)

        val xh = cos(lonRad) * cos(latRad)
        val yh = sin(lonRad) * cos(latRad)
        val zh = sin(latRad)

        val xe = xh
        val ye = yh * cos(ecl) - zh * sin(ecl)
        val ze = yh * sin(ecl) + zh * cos(ecl)

        val ra = Math.toDegrees(atan2(ye, xe))
        val dec = Math.toDegrees(atan2(ze, sqrt(xe * xe + ye * ye)))

        // Sidereal time from the sun's mean longitude (GMST0 = Ls + 180).
        val utHours = (d - Math.floor(d)) * 24.0
        val gmst = sunMeanLongitude(d) + 180.0 + utHours * 15.0
        val hourAngle = Math.toRadians(norm360(gmst + longitude - ra))

        val obsLat = Math.toRadians(latitude)
        val decRad = Math.toRadians(dec)
        val geocentric = Math.toDegrees(
            asin(sin(obsLat) * sin(decRad) + cos(obsLat) * cos(decRad) * cos(hourAngle))
        )

        val parallax = Math.toDegrees(asin(1.0 / moon.distance))
        return geocentric - parallax * cos(Math.toRadians(geocentric))
    }
}
