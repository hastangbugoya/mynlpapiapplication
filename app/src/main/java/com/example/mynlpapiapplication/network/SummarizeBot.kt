package com.example.mynlpapiapplication.network

import com.example.mynlpapiapplication.data.SummarizeResponse
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 *  Waiting for SummarizeBot API key
 */
class SummarizeBot(private val apiKey: String) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://summarizebot.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(SummarizeBotService::class.java)

    fun summarize(text: String): String {
        val requestBody = "{\"url\":\"$text\",\"size\":20}"
        var S = ""
//        val call = service.summarize(apiKey, requestBody)
        try {
            CoroutineScope(Dispatchers.Main).launch() {
                S = withTimeoutOrNull(4000L) {
                    withContext(Dispatchers.IO) {
                        service.summarize(apiKey, requestBody).summary
                    }
                } ?: "Timed out"
            }
        } catch (e: Exception) {
            S = e.toString()
        }
        return S
    }

    private interface SummarizeBotService {
        @Headers(
            "Content-Type: application/json",
            "Accept: application/json"
        )
        @POST("summarize")
        suspend fun summarize(
            @Header("ApiKey") apiKey: String,
            @Body requestBody: String
        ): SummarizeResponse
    }
}



