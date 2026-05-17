package com.fastflow.app.data.ramadan

import com.fastflow.app.data.ramadan.AladhanApiService.AladhanTimingsDto
import com.fastflow.app.data.ramadan.AladhanApiService.HijriDate
import com.fastflow.app.domain.model.ramadan.RamadanTimings
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RamadanTimingsParser @Inject constructor() {

    fun parse(dto: AladhanTimingsDto, hijri: HijriDate?): Result<RamadanTimings> {
        val fajrStr = dto.fajr?.substringBefore(" ")?.trim()
        val maghribStr = dto.maghrib?.substringBefore(" ")?.trim()
        if (fajrStr.isNullOrBlank() || maghribStr.isNullOrBlank()) {
            return Result.failure(Exception("Horaires Fajr/Maghrib invalides"))
        }

        return try {
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            val fajr = parseTimeToday(fajrStr, today, zone)
            val maghrib = parseTimeToday(maghribStr, today, zone)

            Result.success(
                RamadanTimings(
                    dateKey = today.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    fajrMillis = fajr,
                    maghribMillis = maghrib,
                    hijriDay = hijri?.day,
                    hijriMonth = hijri?.month?.ar ?: hijri?.month?.en
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseTimeToday(time: String, date: LocalDate, zone: ZoneId): Long {
        val localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm"))
        return LocalDateTime.of(date, localTime).atZone(zone).toInstant().toEpochMilli()
    }
}
