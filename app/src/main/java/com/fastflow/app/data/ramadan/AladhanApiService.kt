package com.fastflow.app.data.ramadan

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AladhanApiService @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    suspend fun fetchTimingsByCity(city: String, country: String): Result<AladhanData> =
        withContext(Dispatchers.IO) {
            val url = "https://api.aladhan.com/v1/timingsByCity?city=${city.encode()}&country=${country.encode()}&method=2"
            fetch(url)
        }

    suspend fun fetchTimingsByCoordinates(lat: Double, lon: Double): Result<AladhanData> =
        withContext(Dispatchers.IO) {
            val url = "https://api.aladhan.com/v1/timings?latitude=$lat&longitude=$lon&method=2"
            fetch(url)
        }

    private fun fetch(url: String): Result<AladhanData> {
        return try {
            val request = Request.Builder().url(url).get().build()
            val response = client.newCall(request).execute()
            val body = response.body?.string()
                ?: return Result.failure(Exception("Réponse vide"))
            if (!response.isSuccessful) {
                return Result.failure(Exception("Erreur API ${response.code}"))
            }
            val parsed = gson.fromJson(body, AladhanResponse::class.java)
            val data = parsed.data
                ?: return Result.failure(Exception("Horaires introuvables"))
            if (data.timings == null) {
                return Result.failure(Exception("Horaires introuvables"))
            }
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun String.encode(): String = java.net.URLEncoder.encode(this, "UTF-8")

    data class AladhanResponse(val data: AladhanData?)
    data class AladhanData(
        val timings: AladhanTimingsDto?,
        val date: AladhanDate?
    )
    data class AladhanDate(val hijri: HijriDate?)
    data class HijriDate(
        val day: String?,
        @SerializedName("month") val month: HijriMonth?
    )
    data class HijriMonth(val en: String?, val ar: String?)

    data class AladhanTimingsDto(
        @SerializedName("Fajr") val fajr: String?,
        @SerializedName("Maghrib") val maghrib: String?
    )
}
