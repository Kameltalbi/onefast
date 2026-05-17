package com.fastflow.app.data.notification

import com.fastflow.app.domain.model.ramadan.RamadanTimings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RamadanNotificationScheduler @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) {

    fun scheduleForTimings(timings: RamadanTimings, hydrationEnabled: Boolean) {
        cancelRamadanAlarms()
        val now = System.currentTimeMillis()

        scheduleIfFuture(
            AlarmScheduler.ACTION_RAMADAN_IFTAR,
            AlarmScheduler.REQUEST_CODE_RAMADAN_IFTAR,
            timings.maghribMillis,
            now
        )
        scheduleIfFuture(
            AlarmScheduler.ACTION_RAMADAN_SUHOOR,
            AlarmScheduler.REQUEST_CODE_RAMADAN_SUHOOR,
            timings.fajrMillis,
            now
        )

        if (!hydrationEnabled) return

        val nextFajr = if (now < timings.fajrMillis) {
            timings.fajrMillis
        } else {
            timings.fajrMillis + 24 * 60 * 60 * 1000L
        }

        val hydrationOffsets = listOf(
            30 * 60 * 1000L to AlarmScheduler.REQUEST_CODE_RAMADAN_HYDRATION_1,
            2 * 60 * 60 * 1000L to AlarmScheduler.REQUEST_CODE_RAMADAN_HYDRATION_2,
            4 * 60 * 60 * 1000L to AlarmScheduler.REQUEST_CODE_RAMADAN_HYDRATION_3
        )
        for ((offset, code) in hydrationOffsets) {
            val trigger = timings.maghribMillis + offset
            if (trigger > now && trigger < nextFajr) {
                scheduleIfFuture(AlarmScheduler.ACTION_RAMADAN_HYDRATION, code, trigger, now)
            }
        }

        val preSuhoor = nextFajr - 90 * 60 * 1000L
        scheduleIfFuture(
            AlarmScheduler.ACTION_RAMADAN_HYDRATION,
            AlarmScheduler.REQUEST_CODE_RAMADAN_HYDRATION_4,
            preSuhoor,
            now
        )
    }

    fun cancelRamadanAlarms() {
        alarmScheduler.cancelRamadanAlarms()
    }

    private fun scheduleIfFuture(action: String, requestCode: Int, triggerAt: Long, now: Long) {
        if (triggerAt > now) {
            alarmScheduler.scheduleAlarm(action, requestCode, triggerAt)
        }
    }
}
