package com.fastflow.app.data.notification

import com.fastflow.app.domain.model.ChallengeType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeNotifier @Inject constructor(
    private val notificationHelper: NotificationHelper
) {
    fun notifyHalfway(type: ChallengeType, percent: Int) {
        notificationHelper.showChallengeMilestoneNotification(type, percent)
    }

    fun notifyCompleted(type: ChallengeType) {
        notificationHelper.showChallengeCompletedNotification(type)
    }
}
