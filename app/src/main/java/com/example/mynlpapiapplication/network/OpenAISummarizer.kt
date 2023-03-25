package com.example.mynlpapiapplication.network

import android.content.Context
import android.util.Log
import com.example.mynlpapiapplication.BuildConfig
import com.example.mynlpapiapplication.R
import com.example.mynlpapiapplication.data.OpenAISummarizerResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class OpenAISummarizer(
    val baseUrl: String = BuildConfig.BASE_URL
) {
    private val client = OkHttpClient()
    var uiUpdater: UIUpdater? = null
    suspend fun summarizeUrl(
        context: Context,
        apiKey: String,
        urlString: String,
        maxTokens: Int,
        temperature: Double,
        runOn: CoroutineDispatcher
    ): OpenAISummarizerResponse {
        return withContext(runOn) {
            uiUpdater?.lockupButton()

            val requestBody = JSONObject()
                .put("model", "text-davinci-002")
                .put("prompt", context.resources.getString(R.string.url_prompt).format(urlString))
                .put("temperature", temperature) // creative freedom on a scale 0.1 to 1.0(Max)
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

            uiUpdater?.releaseButton()

            try {
                Gson().fromJson(responseBody, OpenAISummarizerResponse::class.java).apply {
                    code = response.code
                    sendTime = response.sentRequestAtMillis
                    responseTime = response.receivedResponseAtMillis
                    success = response.isSuccessful
                    requestString = urlString
                }
            } catch (e: Exception) {
                Log.d(
                    "Meow", when (e) {
                        is JsonSyntaxException -> "JSON syntax ${e.toString()}"
                        else -> e.toString()
                    }
                )
                OpenAISummarizerResponse()
            }
        }
    }

    fun setUIUpdater(updater: UIUpdater) {
        uiUpdater = updater
    }

    interface UIUpdater {
        fun lockupButton()
        fun releaseButton()
    }
}