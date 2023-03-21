package com.example.mynlpapiapplication.network

import android.util.Log
import com.example.mynlpapiapplication.BuildConfig
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

class OpenAISummarizer() {
    private val client = OkHttpClient()
    private val apiKey = BuildConfig.API_KEY

    fun summarizeUrl(url: String, maxLength: Int, callback: (JSONObject) -> Unit) {
        var summary = JSONObject()
        var content = ""
        val job = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
//                content = fetchUrlContent(url)
                summary = summarizeText(url, maxLength)
            }
            callback(summary)
        }
    }

    private suspend fun fetchUrlContent(url: String): String {
        val request = Request.Builder()
            .url(url)
            .build()
        var response = CoroutineScope(Dispatchers.IO).async {
                client.newCall(request).execute()
        }.await()
        Log.d ("Meow","URL DATA >>>>>>>>>>>>>>>>>" + response.body?.string() ?: "")
        return response.body?.string() ?: ""
    }

    private fun summarizeText(text: String, maxLength: Int): JSONObject {
        val requestBody = JSONObject()
            .put("model", "text-davinci-002")
            .put("prompt", "Please summarize the following text:")
            .put("temperature", 0.5)
            .put("max_tokens", maxLength)
            .put("url", text)
            .toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""

        val json = JSONObject(responseBody)
//        val summary = json.getJSONArray("choices").getJSONObject(0).getString("text")
        val summary = json.toString()
        return json
    }
}