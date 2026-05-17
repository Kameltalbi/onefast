package com.fastflow.app.domain.onboarding

import com.fastflow.app.domain.model.FastingExperienceLevel
import com.fastflow.app.domain.model.FastingType
import com.fastflow.app.domain.model.OnboardingGoal

enum class PlanDifficulty {
    EASY,
    MODERATE,
    CHALLENGING
}

object FastingPlanRecommender {

    fun recommend(
        goal: OnboardingGoal?,
        experience: FastingExperienceLevel?
    ): FastingType {
        if (goal == OnboardingGoal.RAMADAN_FASTING) {
            return FastingType.SIXTEEN_EIGHT
        }
        return when (experience) {
            FastingExperienceLevel.BEGINNER -> when (goal) {
                OnboardingGoal.LOSE_WEIGHT -> FastingType.SIXTEEN_EIGHT
                OnboardingGoal.BETTER_ENERGY -> FastingType.FOURTEEN_TEN
                OnboardingGoal.HEALTHY_LIFESTYLE -> FastingType.SIXTEEN_EIGHT
                OnboardingGoal.RAMADAN_FASTING -> FastingType.SIXTEEN_EIGHT
                null -> FastingType.SIXTEEN_EIGHT
            }
            FastingExperienceLevel.INTERMEDIATE -> when (goal) {
                OnboardingGoal.LOSE_WEIGHT -> FastingType.EIGHTEEN_SIX
                OnboardingGoal.BETTER_ENERGY -> FastingType.SIXTEEN_EIGHT
                else -> FastingType.SIXTEEN_EIGHT
            }
            FastingExperienceLevel.ADVANCED -> when (goal) {
                OnboardingGoal.LOSE_WEIGHT -> FastingType.EIGHTEEN_SIX
                OnboardingGoal.BETTER_ENERGY -> FastingType.EIGHTEEN_SIX
                OnboardingGoal.HEALTHY_LIFESTYLE -> FastingType.SIXTEEN_EIGHT
                else -> FastingType.OMAD
            }
            null -> FastingType.SIXTEEN_EIGHT
        }
    }

    fun difficulty(plan: FastingType): PlanDifficulty = when (plan) {
        FastingType.TWELVE_TWELVE, FastingType.FOURTEEN_TEN -> PlanDifficulty.EASY
        FastingType.SIXTEEN_EIGHT -> PlanDifficulty.MODERATE
        FastingType.EIGHTEEN_SIX, FastingType.TWENTY_FOUR -> PlanDifficulty.CHALLENGING
        FastingType.OMAD, FastingType.CUSTOM -> PlanDifficulty.CHALLENGING
    }
}
