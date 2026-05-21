package com.fastflow.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fastflow.app.domain.model.CoachQuota
import com.fastflow.app.domain.model.SubscriptionTier
import com.fastflow.app.domain.model.CommunityProfile
import com.fastflow.app.domain.model.NotificationPreferences
import com.fastflow.app.domain.model.ramadan.RamadanSettings
import com.fastflow.app.domain.model.ramadan.RamadanTimings
import java.util.UUID
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onefast_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val NOTIFICATION_PERMISSION_ASKED = booleanPreferencesKey("notification_permission_asked")
        private val EXACT_ALARM_PERMISSION_ASKED = booleanPreferencesKey("exact_alarm_permission_asked")
        private val DEFAULT_FASTING_TYPE = stringPreferencesKey("default_fasting_type")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val HYDRATION_REMINDERS = booleanPreferencesKey("hydration_reminders")
        private val HYDRATION_GOAL_ML = intPreferencesKey("hydration_goal_ml")
        private val HYDRATION_GLASS_ML = intPreferencesKey("hydration_glass_ml")
        private val TWO_HOURS_LEFT_ENABLED = booleanPreferencesKey("two_hours_left_enabled")
        private val FAT_BURN_ENABLED = booleanPreferencesKey("fat_burn_enabled")
        private val STREAK_MILESTONES_ENABLED = booleanPreferencesKey("streak_milestones_enabled")
        private val QUIET_HOURS_ENABLED = booleanPreferencesKey("quiet_hours_enabled")
        private val QUIET_START_HOUR = intPreferencesKey("quiet_start_hour")
        private val QUIET_END_HOUR = intPreferencesKey("quiet_end_hour")
        private val LAST_STREAK_NOTIFIED = intPreferencesKey("last_streak_notified")
        private val COACH_QUESTIONS_DATE = longPreferencesKey("coach_questions_date")
        private val COACH_QUESTIONS_COUNT = intPreferencesKey("coach_questions_count")
        private val IS_PREMIUM_USER = booleanPreferencesKey("is_premium_user")
        private val SUBSCRIPTION_TIER = stringPreferencesKey("subscription_tier")
        private val TARGET_WEIGHT_KG = floatPreferencesKey("target_weight_kg")
        private val DISMISSED_HEALTH_ALERTS = stringPreferencesKey("dismissed_health_alerts")
        private val FATIGUE_MENTION_TIMESTAMPS = stringPreferencesKey("fatigue_mention_timestamps")
        private val AUTO_START_ENABLED = booleanPreferencesKey("auto_start_enabled")
        private val AUTO_START_HOUR = intPreferencesKey("auto_start_hour")
        private val AUTO_START_MINUTE = intPreferencesKey("auto_start_minute")
        private val LAST_AUTO_START_DAY = longPreferencesKey("last_auto_start_day")
        private val CUSTOM_FASTING_HOURS = intPreferencesKey("custom_fasting_hours")
        private val CUSTOM_EATING_HOURS = intPreferencesKey("custom_eating_hours")
        private val USER_HEIGHT_CM = floatPreferencesKey("user_height_cm")
        private val USER_AGE = intPreferencesKey("user_age")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val ONBOARDING_GOAL = stringPreferencesKey("onboarding_goal")
        private val FASTING_EXPERIENCE = stringPreferencesKey("fasting_experience")
        private val APP_LANGUAGE = stringPreferencesKey("app_language")
        private val COMMUNITY_USER_ID = stringPreferencesKey("community_user_id")
        private val COMMUNITY_DISPLAY_NAME = stringPreferencesKey("community_display_name")
        private val COMMUNITY_SHARE_ANONYMOUSLY = booleanPreferencesKey("community_share_anonymously")
        private val COMMUNITY_PROFILE_SETUP = booleanPreferencesKey("community_profile_setup")
        private val HEALTH_WRITE_WEIGHT_ENABLED = booleanPreferencesKey("health_write_weight_enabled")
        private val LAST_HEALTH_SYNC_AT = longPreferencesKey("last_health_sync_at")
        private val RAMADAN_ENABLED = booleanPreferencesKey("ramadan_enabled")
        private val RAMADAN_CITY = stringPreferencesKey("ramadan_city")
        private val RAMADAN_COUNTRY = stringPreferencesKey("ramadan_country")
        private val RAMADAN_LAT = stringPreferencesKey("ramadan_lat")
        private val RAMADAN_LON = stringPreferencesKey("ramadan_lon")
        private val RAMADAN_HYDRATION = booleanPreferencesKey("ramadan_hydration")
        private val RAMADAN_FAJR_CACHE = longPreferencesKey("ramadan_fajr_cache")
        private val RAMADAN_MAGHRIB_CACHE = longPreferencesKey("ramadan_maghrib_cache")
        private val RAMADAN_DATE_CACHE = stringPreferencesKey("ramadan_date_cache")
        private val RAMADAN_HIJRI_DAY = stringPreferencesKey("ramadan_hijri_day")
        private val RAMADAN_HIJRI_MONTH = stringPreferencesKey("ramadan_hijri_month")
    }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun getNotificationPermissionAskedOnce(): Boolean =
        dataStore.data.map { it[NOTIFICATION_PERMISSION_ASKED] ?: false }.first()

    suspend fun setNotificationPermissionAsked(asked: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_PERMISSION_ASKED] = asked
        }
    }

    suspend fun getExactAlarmPermissionAskedOnce(): Boolean =
        dataStore.data.map { it[EXACT_ALARM_PERMISSION_ASKED] ?: false }.first()

    suspend fun setExactAlarmPermissionAsked(asked: Boolean) {
        dataStore.edit { preferences ->
            preferences[EXACT_ALARM_PERMISSION_ASKED] = asked
        }
    }

    val defaultFastingType: Flow<String?> = dataStore.data.map { preferences ->
        preferences[DEFAULT_FASTING_TYPE]
    }

    suspend fun setDefaultFastingType(type: String) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_FASTING_TYPE] = type
        }
    }

    suspend fun getDefaultFastingTypeOnce(): String? = defaultFastingType.first()

    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    val hydrationRemindersEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HYDRATION_REMINDERS] ?: true
    }

    suspend fun setHydrationRemindersEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[HYDRATION_REMINDERS] = enabled
        }
    }

    val hydrationGoalMl: Flow<Int> = dataStore.data.map { preferences ->
        preferences[HYDRATION_GOAL_ML] ?: 2000
    }

    val hydrationGlassMl: Flow<Int> = dataStore.data.map { preferences ->
        preferences[HYDRATION_GLASS_ML] ?: 250
    }

    suspend fun getHydrationGoalMlOnce(): Int = hydrationGoalMl.first()

    suspend fun getHydrationGlassMlOnce(): Int = hydrationGlassMl.first()

    suspend fun setHydrationGoalMl(ml: Int) {
        dataStore.edit { preferences ->
            preferences[HYDRATION_GOAL_ML] = ml.coerceIn(500, 5000)
        }
    }

    suspend fun setHydrationGlassMl(ml: Int) {
        dataStore.edit { preferences ->
            preferences[HYDRATION_GLASS_ML] = ml.coerceIn(100, 500)
        }
    }

    val autoStartEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[AUTO_START_ENABLED] ?: false
    }

    suspend fun setAutoStartEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_START_ENABLED] = enabled
        }
    }

    val autoStartHour: Flow<Int> = dataStore.data.map { preferences ->
        preferences[AUTO_START_HOUR] ?: 20
    }

    val autoStartMinute: Flow<Int> = dataStore.data.map { preferences ->
        preferences[AUTO_START_MINUTE] ?: 0
    }

    suspend fun setAutoStartTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[AUTO_START_HOUR] = hour.coerceIn(0, 23)
            preferences[AUTO_START_MINUTE] = minute.coerceIn(0, 59)
        }
    }

    suspend fun getLastAutoStartDay(): Long {
        return dataStore.data.map { it[LAST_AUTO_START_DAY] ?: 0L }.first()
    }

    suspend fun setLastAutoStartDay(dayStartMillis: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_AUTO_START_DAY] = dayStartMillis
        }
    }

    val customFastingHours: Flow<Int> = dataStore.data.map { preferences ->
        preferences[CUSTOM_FASTING_HOURS] ?: 16
    }

    suspend fun getCustomFastingHoursOnce(): Int = customFastingHours.first()

    suspend fun setCustomFastingHours(hours: Int) {
        dataStore.edit { preferences ->
            preferences[CUSTOM_FASTING_HOURS] = hours.coerceIn(1, 23)
            preferences[CUSTOM_EATING_HOURS] = (24 - hours.coerceIn(1, 23)).coerceAtLeast(1)
        }
    }

    val userHeightCm: Flow<Float?> = dataStore.data.map { preferences ->
        preferences[USER_HEIGHT_CM]
    }

    suspend fun setUserHeightCm(height: Float) {
        dataStore.edit { preferences ->
            preferences[USER_HEIGHT_CM] = height.coerceIn(100f, 250f)
        }
    }

    suspend fun getUserHeightCmOnce(): Float? = userHeightCm.first()

    suspend fun setUserAge(age: Int) {
        dataStore.edit { preferences ->
            preferences[USER_AGE] = age.coerceIn(16, 99)
        }
    }

    suspend fun getUserAgeOnce(): Int? =
        dataStore.data.map { it[USER_AGE] }.first()

    suspend fun setOnboardingGoal(goal: String) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_GOAL] = goal
        }
    }

    suspend fun setFastingExperience(level: String) {
        dataStore.edit { preferences ->
            preferences[FASTING_EXPERIENCE] = level
        }
    }

    val onboardingGoal: Flow<String?> = dataStore.data.map { it[ONBOARDING_GOAL] }

    val fastingExperience: Flow<String?> = dataStore.data.map { it[FASTING_EXPERIENCE] }

    suspend fun getOnboardingGoalOnce(): String? = onboardingGoal.first()

    suspend fun getFastingExperienceOnce(): String? = fastingExperience.first()

    val userEmail: Flow<String?> = dataStore.data.map { it[USER_EMAIL] }

    suspend fun getUserEmailOnce(): String? = userEmail.first()

    suspend fun setUserEmail(email: String) {
        dataStore.edit { preferences ->
            val trimmed = email.trim()
            if (trimmed.isEmpty()) {
                preferences.remove(USER_EMAIL)
            } else {
                preferences[USER_EMAIL] = trimmed
            }
        }
    }

    val appLanguage: Flow<String?> = dataStore.data.map { preferences ->
        preferences[APP_LANGUAGE]
    }

    suspend fun getAppLanguageOnce(): String? = appLanguage.first()

    suspend fun setAppLanguage(languageTag: String) {
        dataStore.edit { preferences ->
            preferences[APP_LANGUAGE] = languageTag
        }
    }

    val notificationPreferences: Flow<NotificationPreferences> = dataStore.data.map { prefs ->
        NotificationPreferences(
            enabled = prefs[NOTIFICATIONS_ENABLED] ?: true,
            hydrationEnabled = prefs[HYDRATION_REMINDERS] ?: true,
            twoHoursLeftEnabled = prefs[TWO_HOURS_LEFT_ENABLED] ?: true,
            fatBurnEnabled = prefs[FAT_BURN_ENABLED] ?: true,
            streakMilestonesEnabled = prefs[STREAK_MILESTONES_ENABLED] ?: true,
            quietHoursEnabled = prefs[QUIET_HOURS_ENABLED] ?: true,
            quietStartHour = prefs[QUIET_START_HOUR] ?: 22,
            quietEndHour = prefs[QUIET_END_HOUR] ?: 7
        )
    }

    suspend fun getNotificationPreferencesOnce(): NotificationPreferences =
        notificationPreferences.first()

    suspend fun updateNotificationPreferences(update: NotificationPreferences) {
        dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = update.enabled
            prefs[HYDRATION_REMINDERS] = update.hydrationEnabled
            prefs[TWO_HOURS_LEFT_ENABLED] = update.twoHoursLeftEnabled
            prefs[FAT_BURN_ENABLED] = update.fatBurnEnabled
            prefs[STREAK_MILESTONES_ENABLED] = update.streakMilestonesEnabled
            prefs[QUIET_HOURS_ENABLED] = update.quietHoursEnabled
            prefs[QUIET_START_HOUR] = update.quietStartHour.coerceIn(0, 23)
            prefs[QUIET_END_HOUR] = update.quietEndHour.coerceIn(0, 23)
        }
    }

    suspend fun getLastStreakNotifiedOnce(): Int =
        dataStore.data.map { it[LAST_STREAK_NOTIFIED] ?: 0 }.first()

    suspend fun setLastStreakNotified(streak: Int) {
        dataStore.edit { prefs ->
            prefs[LAST_STREAK_NOTIFIED] = streak
        }
    }

    val subscriptionTier: Flow<SubscriptionTier> = dataStore.data.map { prefs ->
        resolveSubscriptionTier(prefs)
    }

    suspend fun getSubscriptionTierOnce(): SubscriptionTier =
        subscriptionTier.first()

    suspend fun setSubscriptionTier(tier: SubscriptionTier) {
        dataStore.edit { prefs ->
            prefs[SUBSCRIPTION_TIER] = tier.name
            prefs[IS_PREMIUM_USER] = tier.hasAtLeast(SubscriptionTier.PREMIUM)
        }
    }

    suspend fun isPremiumUserOnce(): Boolean =
        getSubscriptionTierOnce().hasAtLeast(SubscriptionTier.PREMIUM)

    suspend fun isProUserOnce(): Boolean =
        getSubscriptionTierOnce().hasAtLeast(SubscriptionTier.PRO)

    suspend fun setPremiumUser(premium: Boolean) {
        setSubscriptionTier(
            if (premium) SubscriptionTier.PREMIUM else SubscriptionTier.FREE
        )
    }

    private fun resolveSubscriptionTier(prefs: Preferences): SubscriptionTier {
        prefs[SUBSCRIPTION_TIER]?.let { stored ->
            return SubscriptionTier.fromName(stored)
        }
        return if (prefs[IS_PREMIUM_USER] == true) {
            SubscriptionTier.PREMIUM
        } else {
            SubscriptionTier.FREE
        }
    }

    suspend fun getRemainingCoachQuestionsOnce(): Int {
        val todayStart = startOfDayMillis()
        val prefs = dataStore.data.first()
        val storedDate = prefs[COACH_QUESTIONS_DATE] ?: 0L
        val count = if (storedDate >= todayStart) prefs[COACH_QUESTIONS_COUNT] ?: 0 else 0
        return (CoachQuota.FREE_DAILY_LIMIT - count).coerceAtLeast(0)
    }

    suspend fun incrementCoachQuestionCount() {
        val todayStart = startOfDayMillis()
        dataStore.edit { prefs ->
            val storedDate = prefs[COACH_QUESTIONS_DATE] ?: 0L
            val current = if (storedDate >= todayStart) prefs[COACH_QUESTIONS_COUNT] ?: 0 else 0
            prefs[COACH_QUESTIONS_DATE] = todayStart
            prefs[COACH_QUESTIONS_COUNT] = current + 1
        }
    }

    val targetWeightKg: Flow<Float?> = dataStore.data.map { prefs ->
        prefs[TARGET_WEIGHT_KG]
    }

    suspend fun setTargetWeightKg(weight: Float?) {
        dataStore.edit { prefs ->
            if (weight == null) {
                prefs.remove(TARGET_WEIGHT_KG)
            } else {
                prefs[TARGET_WEIGHT_KG] = weight.coerceIn(40f, 250f)
            }
        }
    }

    suspend fun getTargetWeightKgOnce(): Float? = targetWeightKg.first()

    suspend fun getDismissedHealthAlertsOnce(): Set<String> {
        val raw = dataStore.data.map { it[DISMISSED_HEALTH_ALERTS] ?: "" }.first()
        return raw.split(",").filter { it.isNotBlank() }.toSet()
    }

    suspend fun dismissHealthAlert(alertId: String) {
        dataStore.edit { prefs ->
            val current = prefs[DISMISSED_HEALTH_ALERTS] ?: ""
            val ids = current.split(",").filter { it.isNotBlank() }.toMutableSet()
            ids.add(alertId)
            prefs[DISMISSED_HEALTH_ALERTS] = ids.joinToString(",")
        }
    }

    suspend fun recordFatigueMention() {
        dataStore.edit { prefs ->
            val current = prefs[FATIGUE_MENTION_TIMESTAMPS] ?: ""
            val timestamps = current.split(",").filter { it.isNotBlank() }.toMutableList()
            timestamps.add(System.currentTimeMillis().toString())
            val weekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L
            val pruned = timestamps.filter { it.toLongOrNull()?.let { ts -> ts >= weekAgo } == true }
            prefs[FATIGUE_MENTION_TIMESTAMPS] = pruned.joinToString(",")
        }
    }

    suspend fun getFatigueMentionsSince(sinceMillis: Long): Int {
        val raw = dataStore.data.map { it[FATIGUE_MENTION_TIMESTAMPS] ?: "" }.first()
        return raw.split(",").count { ts ->
            ts.toLongOrNull()?.let { it >= sinceMillis } == true
        }
    }

    val communityProfile: Flow<CommunityProfile> = dataStore.data.map { prefs ->
        val userId = prefs[COMMUNITY_USER_ID] ?: ""
        val displayName = prefs[COMMUNITY_DISPLAY_NAME] ?: ""
        val anonymous = prefs[COMMUNITY_SHARE_ANONYMOUSLY] ?: false
        val setup = prefs[COMMUNITY_PROFILE_SETUP] ?: false
        CommunityProfile(
            userId = userId,
            displayName = displayName,
            shareAnonymously = anonymous,
            isSetupComplete = setup && displayName.isNotBlank()
        )
    }

    suspend fun getOrCreateCommunityUserId(): String {
        val prefs = dataStore.data.first()
        val existing = prefs[COMMUNITY_USER_ID]
        if (!existing.isNullOrBlank()) return existing
        val newId = UUID.randomUUID().toString()
        dataStore.edit { it[COMMUNITY_USER_ID] = newId }
        return newId
    }

    suspend fun updateCommunityProfile(displayName: String, shareAnonymously: Boolean) {
        getOrCreateCommunityUserId()
        dataStore.edit { prefs ->
            prefs[COMMUNITY_DISPLAY_NAME] = displayName.trim()
            prefs[COMMUNITY_SHARE_ANONYMOUSLY] = shareAnonymously
            prefs[COMMUNITY_PROFILE_SETUP] = displayName.isNotBlank()
        }
    }

    suspend fun getCommunityProfileOnce(): CommunityProfile = communityProfile.first()

    val healthWriteWeightEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[HEALTH_WRITE_WEIGHT_ENABLED] ?: true
    }

    suspend fun setHealthWriteWeightEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[HEALTH_WRITE_WEIGHT_ENABLED] = enabled
        }
    }

    suspend fun getHealthWriteWeightEnabledOnce(): Boolean =
        healthWriteWeightEnabled.first()

    suspend fun setLastHealthSyncAt(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[LAST_HEALTH_SYNC_AT] = timestamp
        }
    }

    suspend fun getLastHealthSyncAtOnce(): Long? {
        val value = dataStore.data.map { it[LAST_HEALTH_SYNC_AT] }.first()
        return value
    }

    val ramadanSettings: Flow<RamadanSettings> = dataStore.data.map { prefs ->
        RamadanSettings(
            enabled = prefs[RAMADAN_ENABLED] ?: false,
            city = prefs[RAMADAN_CITY] ?: "Paris",
            country = prefs[RAMADAN_COUNTRY] ?: "France",
            latitude = prefs[RAMADAN_LAT]?.toDoubleOrNull(),
            longitude = prefs[RAMADAN_LON]?.toDoubleOrNull(),
            hydrationRemindersEnabled = prefs[RAMADAN_HYDRATION] ?: true
        )
    }

    suspend fun getRamadanSettingsOnce(): RamadanSettings = ramadanSettings.first()

    suspend fun setRamadanSettings(settings: RamadanSettings) {
        dataStore.edit { prefs ->
            prefs[RAMADAN_ENABLED] = settings.enabled
            prefs[RAMADAN_CITY] = settings.city.trim()
            prefs[RAMADAN_COUNTRY] = settings.country.trim()
            if (settings.latitude != null && settings.longitude != null) {
                prefs[RAMADAN_LAT] = settings.latitude.toString()
                prefs[RAMADAN_LON] = settings.longitude.toString()
            } else {
                prefs.remove(RAMADAN_LAT)
                prefs.remove(RAMADAN_LON)
            }
            prefs[RAMADAN_HYDRATION] = settings.hydrationRemindersEnabled
        }
    }

    suspend fun setRamadanTimingsCache(timings: RamadanTimings) {
        dataStore.edit { prefs ->
            prefs[RAMADAN_FAJR_CACHE] = timings.fajrMillis
            prefs[RAMADAN_MAGHRIB_CACHE] = timings.maghribMillis
            prefs[RAMADAN_DATE_CACHE] = timings.dateKey
            timings.hijriDay?.let { prefs[RAMADAN_HIJRI_DAY] = it } ?: prefs.remove(RAMADAN_HIJRI_DAY)
            timings.hijriMonth?.let { prefs[RAMADAN_HIJRI_MONTH] = it } ?: prefs.remove(RAMADAN_HIJRI_MONTH)
        }
    }

    suspend fun getRamadanTimingsCacheOnce(): RamadanTimings? {
        val prefs = dataStore.data.first()
        val fajr = prefs[RAMADAN_FAJR_CACHE] ?: return null
        val maghrib = prefs[RAMADAN_MAGHRIB_CACHE] ?: return null
        val date = prefs[RAMADAN_DATE_CACHE] ?: return null
        return RamadanTimings(
            dateKey = date,
            fajrMillis = fajr,
            maghribMillis = maghrib,
            hijriDay = prefs[RAMADAN_HIJRI_DAY],
            hijriMonth = prefs[RAMADAN_HIJRI_MONTH]
        )
    }

    private fun startOfDayMillis(): Long {
        return java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
