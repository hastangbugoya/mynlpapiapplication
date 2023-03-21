package com.example.mynlpapiapplication.network

import com.example.mynlpapiapplication.data.SummarizeResponse
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
/**
 *  Waiting for SummarizeBot API key
 */
class SummarizeBotClient() {
    private val retrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.summarizebot.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val service : SummarizeBotService by lazy {
        retrofit.create(SummarizeBotService::class.java)
    }

    fun getSummary(apiKey : String, url : String, size : Int, callback: (String?) -> Unit) {
        try {
            CoroutineScope(Dispatchers.Main).launch {
                val summary = withContext(Dispatchers.IO) {
                    val response = service.summarize(apiKey, url, size)
                    when {
                        response.isSuccessful -> response.body().toString()
                        else -> "Error ${response.code()}: ${
                            when(response.code()){
                                400 -> "Bad Request"
                                else -> "Some other error"
                            }}"
                    }
                }
                callback(summary)
            }
        } catch (e: HttpException) {
            callback("Exception : $e")
        }
    }
}

interface SummarizeBotService {
    @GET("summarize")
    suspend fun summarize(
        @Query("apiKey") apiKey: String,
        @Query("url") url: String,
        @Query("size") size: Int
    ): Response<SummarizeResponse>
}