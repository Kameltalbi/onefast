package com.fastflow.app.data.repository

import com.fastflow.app.data.local.dao.EarnedBadgeDao
import com.fastflow.app.data.local.dao.FastingSessionDao
import com.fastflow.app.data.local.dao.UserChallengeDao
import com.fastflow.app.data.local.entity.EarnedBadgeEntity
import com.fastflow.app.data.local.entity.FastingSessionEntity
import com.fastflow.app.data.local.entity.UserChallengeEntity
import com.fastflow.app.data.notification.ChallengeNotifier
import com.fastflow.app.domain.challenge.ChallengeCatalog
import com.fastflow.app.domain.challenge.ChallengeProgressCalculator
import com.fastflow.app.domain.model.*
import com.fastflow.app.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChallengeRepositoryImpl @Inject constructor(
    private val userChallengeDao: UserChallengeDao,
    private val earnedBadgeDao: EarnedBadgeDao,
    private val fastingSessionDao: FastingSessionDao,
    private val progressCalculator: ChallengeProgressCalculator,
    private val challengeNotifier: ChallengeNotifier
) : ChallengeRepository {

    override fun observeChallengesOverview(): Flow<List<ChallengeUiModel>> =
        userChallengeDao.observeAll().map { enrollments ->
            buildOverview(enrollments)
        }

    override fun observeEarnedBadges(): Flow<List<EarnedBadge>> =
        earnedBadgeDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun joinChallenge(type: ChallengeType): Result<UserChallenge> {
        return try {
            val existing = userChallengeDao.getActiveByType(type.name)
            if (existing != null) {
                return Result.failure(Exception("Un défi de ce type est déjà en cours"))
            }

            val entity = UserChallengeEntity(
                challengeType = type.name,
                startedAt = System.currentTimeMillis(),
                status = ChallengeStatus.ACTIVE.name,
                completedDays = 0,
                lastNotifiedMilestone = 0
            )
            val id = userChallengeDao.insert(entity)
            val saved = userChallengeDao.getById(id.toInt())!!
            Result.success(saved.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun abandonChallenge(challengeId: Int): Result<Unit> {
        return try {
            val entity = userChallengeDao.getById(challengeId)
                ?: return Result.failure(Exception("Défi introuvable"))
            userChallengeDao.update(
                entity.copy(status = ChallengeStatus.ABANDONED.name)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshActiveChallenges(): List<UserChallenge> {
        val active = userChallengeDao.getActive()
        if (active.isEmpty()) return emptyList()

        val sessions = fastingSessionDao.getCompletedSessions().map(FastingSessionEntity::toDomain)
        val updated = mutableListOf<UserChallenge>()

        for (entity in active) {
            val definition = ChallengeCatalog.get(ChallengeType.valueOf(entity.challengeType))
            val validDays = progressCalculator.countValidDays(
                sessions = sessions,
                definition = definition,
                startedAt = entity.startedAt
            )
            val percent = if (definition.durationDays > 0) {
                (validDays * 100) / definition.durationDays
            } else 0

            var status = ChallengeStatus.ACTIVE
            var milestone = entity.lastNotifiedMilestone

            when {
                progressCalculator.isCompleted(validDays, definition.durationDays) -> {
                    status = ChallengeStatus.COMPLETED
                    awardBadge(definition)
                    if (milestone < 100) {
                        challengeNotifier.notifyCompleted(definition.type)
                        milestone = 100
                    }
                }
                progressCalculator.hasExpired(entity.startedAt, definition.durationDays) -> {
                    status = ChallengeStatus.FAILED
                }
                percent >= 50 && milestone < 50 -> {
                    challengeNotifier.notifyHalfway(definition.type, percent)
                    milestone = 50
                }
            }

            val updatedEntity = entity.copy(
                completedDays = validDays,
                status = status.name,
                lastNotifiedMilestone = milestone
            )
            userChallengeDao.update(updatedEntity)
            updated.add(updatedEntity.toDomain())
        }

        return updated
    }

    private suspend fun awardBadge(definition: ChallengeDefinition) {
        if (!earnedBadgeDao.exists(definition.badgeId)) {
            earnedBadgeDao.insert(
                EarnedBadgeEntity(
                    id = definition.badgeId,
                    challengeType = definition.type.name,
                    earnedAt = System.currentTimeMillis()
                )
            )
        }
    }

    private fun buildOverview(enrollments: List<UserChallengeEntity>): List<ChallengeUiModel> {
        return ChallengeCatalog.all.map { definition ->
            val active = enrollments.find {
                it.challengeType == definition.type.name && it.status == ChallengeStatus.ACTIVE.name
            }
            ChallengeUiModel(
                definition = definition,
                enrollment = active?.toDomain(),
                canJoin = active == null
            )
        }
    }

}
