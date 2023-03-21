package com.example.mynlpapiapplication.network

import com.example.mynlpapiapplication.BuildConfig
import com.example.mynlpapiapplication.data.OpenAISummarizerResponse
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class OpenAISummarizer(
    val apiKey : String = BuildConfig.API_KEY,
    val baseUrl : String = BuildConfig.BASE_URL
) {
    private val client = OkHttpClient()

    var uiUpdater : UIUpdater? = null

    suspend fun summarizeUrl(urlString: String, maxTokens: Int): OpenAISummarizerResponse {
        return withContext(Dispatchers.IO) {
            uiUpdater?.lockupButton()
            val requestBody = JSONObject()
                .put("model", "text-davinci-002")
                .put("prompt", "Give me the summary of the content of this webpage: $urlString")
                .put("temperature", 0.5) // creative freedom on a scale 0.1 to 1.0(Max)
                .put("max_tokens", maxTokens)
                .toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("${baseUrl}completions")
                .header("Authorization", "Bearer $apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            val gson = Gson()
            uiUpdater?.releaseButton()
            gson.fromJson(responseBody, OpenAISummarizerResponse::class.java)
        }
    }

    fun setUIUpdater(updater : UIUpdater) {
        uiUpdater = updater
    }

    interface UIUpdater {
        fun lockupButton()
        fun releaseButton()
    }
}