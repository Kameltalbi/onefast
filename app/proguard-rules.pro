# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep data classes
-keep class com.fastflow.app.domain.model.** { *; }
-keep class com.fastflow.app.data.local.entity.** { *; }
-keep class com.fastflow.app.data.billing.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Billing
-keep class com.android.billingclient.** { *; }
