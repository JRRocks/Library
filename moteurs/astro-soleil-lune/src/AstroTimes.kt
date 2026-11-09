package com.jeanrocka.suntrack.astro

import java.time.ZonedDateTime

/**
 * Solar events for one local day at one location.
 *
 * [sunrise] and [sunset] are null during polar day/night, when the sun never
 * crosses the horizon. Callers must branch on [polarDay] / [polarNight] rather
 * than assuming the times exist.
 */
data class SunTimes(
    val sunrise: ZonedDateTime?,
    val sunset: ZonedDateTime?,
    val solarNoon: ZonedDateTime,
    val dayLengthMinutes: Double,
    val polarDay: Boolean,
    val polarNight: Boolean,
    /** Sun's declination at solar noon, in degrees. Needed to place the sun in the sky. */
    val declinationDeg: Double,
)

/** The eight conventional lunar phase buckets. */
enum class MoonPhase {
    NEW,
    WAXING_CRESCENT,
    FIRST_QUARTER,
    WAXING_GIBBOUS,
    FULL,
    WANING_GIBBOUS,
    LAST_QUARTER,
    WANING_CRESCENT,
}

/**
 * Lunar state for a single instant.
 *
 * [phase] runs 0..1 over a synodic month: 0 = new, 0.25 = first quarter,
 * 0.5 = full, 0.75 = last quarter. [illumination] is the lit fraction of the
 * visible disc (0..1) and is *not* a linear function of [phase].
 *
 * [transit] is when the moon is highest in the sky; the dial uses it to place
 * the moon marker the same way solar noon places the sun. It is null on days
 * where no transit falls inside the local calendar day.
 */
data class MoonTimes(
    val phase: Double,
    val illumination: Double,
    val phaseName: MoonPhase,
    val moonrise: ZonedDateTime?,
    val moonset: ZonedDateTime?,
    val transit: ZonedDateTime?,
) {
    /** True while the moon is filling out, i.e. new -> full. */
    val waxing: Boolean get() = phase < 0.5
}
