package com.fastflow.app.data.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.fastflow.app.domain.model.HealthConnectStatus
import com.fastflow.app.domain.model.HealthSyncSnapshot
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class HealthConnectManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val permissions: Set<String> = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getWritePermission(WeightRecord::class)
    )

    fun getConnectStatus(): HealthConnectStatus = when (HealthConnectClient.getSdkStatus(context)) {
        HealthConnectClient.SDK_AVAILABLE -> HealthConnectStatus.AVAILABLE
        HealthConnectClient.SDK_UNAVAILABLE -> HealthConnectStatus.NOT_INSTALLED
        HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED ->
            HealthConnectStatus.UPDATE_REQUIRED
        else -> HealthConnectStatus.NOT_SUPPORTED
    }

    fun getClientOrNull(): HealthConnectClient? =
        if (getConnectStatus() == HealthConnectStatus.AVAILABLE) {
            HealthConnectClient.getOrCreate(context)
        } else null

    suspend fun getGrantedPermissions(): Set<String> {
        val client = getClientOrNull() ?: return emptySet()
        return client.permissionController.getGrantedPermissions()
    }

    fun hasAllPermissions(granted: Set<String>): Boolean = granted.containsAll(permissions)

    suspend fun readSnapshot(lastSyncAt: Long?): HealthSyncSnapshot {
        val status = getConnectStatus()
        val client = getClientOrNull()
            ?: return HealthSyncSnapshot(status = status)

        val granted = client.permissionController.getGrantedPermissions()
        if (!hasAllPermissions(granted)) {
            return HealthSyncSnapshot(
                status = status,
                hasPermissions = false,
                lastSyncAt = lastSyncAt
            )
        }

        val now = Instant.now()
        val zone = ZoneId.systemDefault()
        val startOfToday = now.atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()
        val weekAgo = now.minus(7, ChronoUnit.DAYS)

        val stepsToday = readSteps(client, startOfToday, now)
        val stepsWeek = readSteps(client, weekAgo, now)
        val stepsWeekAvg = if (stepsWeek > 0) stepsWeek / 7 else 0L

        return HealthSyncSnapshot(
            status = status,
            hasPermissions = true,
            stepsToday = stepsToday,
            stepsWeekAvg = stepsWeekAvg,
            sleepLastNightHours = readLastSleepHours(client, now),
            restingHeartRate = readLatestHeartRate(client, weekAgo, now),
            activeCaloriesToday = readActiveCalories(client, startOfToday, now),
            weightFromHealthKg = readLatestWeightKg(client, weekAgo, now),
            lastSyncAt = System.currentTimeMillis()
        )
    }

    suspend fun writeWeightKg(weightKg: Float): Result<Unit> {
        val client = getClientOrNull()
            ?: return Result.failure(Exception("Health Connect indisponible"))

        val granted = client.permissionController.getGrantedPermissions()
        val writePerm = HealthPermission.getWritePermission(WeightRecord::class)
        if (!granted.contains(writePerm)) {
            return Result.failure(Exception("Permission écriture poids non accordée"))
        }

        return try {
            val now = Instant.now()
            val zoneOffset = ZoneOffset.systemDefault().rules.getOffset(now)
            val record = WeightRecord(
                weight = androidx.health.connect.client.units.Mass.kilograms(weightKg.toDouble()),
                time = now,
                zoneOffset = zoneOffset
            )
            client.insertRecords(listOf(record))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun readSteps(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): Long {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            response.records.sumOf { it.count }
        } catch (_: Exception) {
            0L
        }
    }

    private suspend fun readLastSleepHours(
        client: HealthConnectClient,
        now: Instant
    ): Float? {
        return try {
            val start = now.minus(2, ChronoUnit.DAYS)
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, now)
                )
            )
            val session = response.records.maxByOrNull { it.endTime.epochSecond }
                ?: return null
            val durationMs = session.endTime.toEpochMilli() - session.startTime.toEpochMilli()
            (durationMs / 3_600_000f * 10).roundToInt() / 10f
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun readLatestHeartRate(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): Int? {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            response.records
                .flatMap { it.samples }
                .maxByOrNull { it.time.epochSecond }
                ?.beatsPerMinute
                ?.toInt()
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun readActiveCalories(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): Double {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = ActiveCaloriesBurnedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            response.records.sumOf { it.energy.inKilocalories }
        } catch (_: Exception) {
            0.0
        }
    }

    private suspend fun readLatestWeightKg(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): Float? {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = WeightRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            response.records
                .maxByOrNull { it.time.epochSecond }
                ?.weight
                ?.inKilograms
                ?.toFloat()
        } catch (_: Exception) {
            null
        }
    }
}
