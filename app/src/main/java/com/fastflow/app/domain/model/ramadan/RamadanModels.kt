package com.fastflow.app.domain.model.ramadan

data class RamadanSettings(
    val enabled: Boolean = false,
    val city: String = "Paris",
    val country: String = "France",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val hydrationRemindersEnabled: Boolean = true
)

data class RamadanTimings(
    val dateKey: String,
    val fajrMillis: Long,
    val maghribMillis: Long,
    val hijriDay: String? = null,
    val hijriMonth: String? = null
) {
    fun isCurrentlyFasting(now: Long = System.currentTimeMillis()): Boolean =
        now >= fajrMillis && now < maghribMillis

    fun millisUntilMaghrib(now: Long = System.currentTimeMillis()): Long =
        (maghribMillis - now).coerceAtLeast(0)

    fun millisUntilFajr(now: Long = System.currentTimeMillis()): Long {
        val nextFajr = if (now < fajrMillis) {
            fajrMillis
        } else {
            fajrMillis + 24 * 60 * 60 * 1000L
        }
        return (nextFajr - now).coerceAtLeast(0)
    }

    fun fastingHoursUntilIftar(now: Long = System.currentTimeMillis()): Float {
        val remaining = millisUntilMaghrib(now)
        return remaining / 3_600_000f
    }
}

enum class RamadanNextEvent {
    IFTAR,
    SUHOOR
}
