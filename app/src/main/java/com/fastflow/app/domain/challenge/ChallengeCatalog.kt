package com.fastflow.app.domain.challenge

import com.fastflow.app.domain.model.ChallengeDefinition
import com.fastflow.app.domain.model.ChallengeType

object ChallengeCatalog {
    val all: List<ChallengeDefinition> = listOf(
        ChallengeDefinition(
            type = ChallengeType.SEVEN_DAYS,
            durationDays = 7,
            minFastingHours = 16f,
            badgeId = "badge_7_days"
        ),
        ChallengeDefinition(
            type = ChallengeType.THIRTY_DAYS,
            durationDays = 30,
            minFastingHours = 16f,
            badgeId = "badge_30_days"
        ),
        ChallengeDefinition(
            type = ChallengeType.RAMADAN,
            durationDays = 30,
            minFastingHours = 14f,
            badgeId = "badge_ramadan"
        ),
        ChallengeDefinition(
            type = ChallengeType.SUMMER_BODY,
            durationDays = 30,
            minFastingHours = 18f,
            badgeId = "badge_summer"
        )
    )

    fun get(type: ChallengeType): ChallengeDefinition =
        all.first { it.type == type }
}
