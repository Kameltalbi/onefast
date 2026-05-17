package com.fastflow.app.data.repository

import com.fastflow.app.data.local.dao.FastingSessionDao
import com.fastflow.app.data.local.entity.FastingSessionEntity
import com.fastflow.app.domain.model.FastingSession
import com.fastflow.app.domain.model.FastingStatus
import com.fastflow.app.domain.model.FastingType
import com.fastflow.app.domain.model.WeeklyFastingDay
import com.fastflow.app.domain.repository.FastingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class FastingRepositoryImpl @Inject constructor(
    private val dao: FastingSessionDao
) : FastingRepository {

    override suspend fun startFasting(
        fastingType: FastingType,
        customFastingHours: Int?
    ): Result<FastingSession> {
        return try {
            val currentSession = dao.getCurrentSession()
            if (currentSession != null) {
                return Result.failure(Exception("Une session de jeûne est déjà en cours"))
            }

            val fastingHours = fastingType.resolveFastingHours(customFastingHours)
            val startTime = System.currentTimeMillis()
            val endTimeExpected = startTime + fastingType.getFastingDurationMillis(customFastingHours)

            val session = FastingSessionEntity(
                startTime = startTime,
                endTimeExpected = endTimeExpected,
                fastingType = fastingType.name,
                fastingHoursActual = fastingHours,
                status = FastingStatus.FASTING.name
            )

            val id = dao.insert(session)
            val savedSession = dao.getById(id.toInt())

            savedSession?.let {
                Result.success(it.toDomain())
            } ?: Result.failure(Exception("Erreur lors de la création de la session"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pauseFasting(sessionId: Int): Result<FastingSession> {
        return try {
            val session = dao.getById(sessionId)
                ?: return Result.failure(Exception("Session non trouvée"))

            if (session.status != FastingStatus.FASTING.name) {
                return Result.failure(Exception("La session n'est pas en cours"))
            }

            val updatedSession = session.copy(
                status = FastingStatus.PAUSED.name,
                pausedAt = System.currentTimeMillis()
            )

            dao.update(updatedSession)
            Result.success(updatedSession.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resumeFasting(sessionId: Int): Result<FastingSession> {
        return try {
            val session = dao.getById(sessionId)
                ?: return Result.failure(Exception("Session non trouvée"))

            if (session.status != FastingStatus.PAUSED.name) {
                return Result.failure(Exception("La session n'est pas en pause"))
            }

            val pauseDuration = session.pausedAt?.let {
                System.currentTimeMillis() - it
            } ?: 0L

            val updatedSession = session.copy(
                status = FastingStatus.FASTING.name,
                totalPausedDuration = session.totalPausedDuration + pauseDuration,
                pausedAt = null,
                endTimeExpected = session.endTimeExpected + pauseDuration
            )

            dao.update(updatedSession)
            Result.success(updatedSession.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun stopFasting(sessionId: Int): Result<FastingSession> {
        return try {
            val session = dao.getById(sessionId)
                ?: return Result.failure(Exception("Session non trouvée"))

            val endTime = System.currentTimeMillis()
            val totalPaused = if (session.status == FastingStatus.PAUSED.name) {
                session.pausedAt?.let { endTime - it + session.totalPausedDuration }
                    ?: session.totalPausedDuration
            } else {
                session.totalPausedDuration
            }

            val updatedSession = session.copy(
                status = FastingStatus.COMPLETED.name,
                endTimeActual = endTime,
                totalPausedDuration = totalPaused,
                pausedAt = null
            )

            dao.update(updatedSession)
            Result.success(updatedSession.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentSession(): FastingSession? {
        return dao.getCurrentSession()?.toDomain()
    }

    override fun observeCurrentSession(): Flow<FastingSession?> {
        return dao.observeCurrentSession().map { it?.toDomain() }
    }

    override suspend fun getSessionById(id: Int): FastingSession? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun getAllSessions(): List<FastingSession> {
        return dao.getAllSessions().map { it.toDomain() }
    }

    override suspend fun getCompletedSessions(): List<FastingSession> {
        return dao.getCompletedSessions().map { it.toDomain() }
    }

    override suspend fun getWeeklyFastingStats(): List<WeeklyFastingDay> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.DAY_OF_YEAR, -6)

        val weekStart = calendar.timeInMillis
        val sessions = dao.getCompletedSessionsSince(weekStart)
        val dayFormat = SimpleDateFormat("EEE", Locale.FRENCH)

        return (0..6).map { offset ->
            val dayCal = Calendar.getInstance()
            dayCal.timeInMillis = weekStart
            dayCal.add(Calendar.DAY_OF_YEAR, offset)
            val dayStart = dayCal.timeInMillis
            dayCal.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = dayCal.timeInMillis

            val hours = sessions
                .filter { it.startTime in dayStart until dayEnd }
                .sumOf { session ->
                    val end = session.endTimeActual ?: session.endTimeExpected
                    ((end - session.startTime - session.totalPausedDuration) / 3_600_000.0)
                }
                .toFloat()

            WeeklyFastingDay(
                dayLabel = dayFormat.format(dayStart).replaceFirstChar { it.uppercase() },
                hoursFasted = hours
            )
        }
    }

    override suspend fun deleteSession(sessionId: Int): Result<Unit> {
        return try {
            dao.deleteById(sessionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
