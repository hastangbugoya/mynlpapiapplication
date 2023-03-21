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

class OpenAISummarizer() {
    private val client = OkHttpClient()
    private val apiKey = BuildConfig.API_KEY

    suspend fun summarizeUrl(url: String, maxLength: Int) : OpenAISummarizerResponse {
        return withContext(Dispatchers.IO) {
            summarizeText(url, maxLength)
        }
    }

    private fun summarizeText(text: String, maxTokens: Int): OpenAISummarizerResponse {
        val requestBody = JSONObject()
            .put("model", "text-davinci-002")
            .put("prompt", "Give me the summary of the content of this webpage: $text")
            .put("temperature", 0.5)
            .put("max_tokens", maxTokens)
            .toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        val gson = Gson()
        return gson.fromJson(responseBody, OpenAISummarizerResponse::class.java)
    }
}