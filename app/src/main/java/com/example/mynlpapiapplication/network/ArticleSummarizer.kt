package com.example.mynlpapiapplication.network

import com.example.mynlpapiapplication.BuildConfig
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Still not working
 */
class ArticleSummarizer() {
    val baseUrl1 = "https://api.openai.com/v1/"
    private val apiKey = BuildConfig.API_KEY
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl1)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(Gpt3Api::class.java)

    suspend fun summarizeArticle(myUrl: String, myMaxLength: Int = 100): String {
        return withContext(Dispatchers.IO) {
            val response = service.summarizeArticle(
                apiKey,
                SummarizeArticleRequest(url = myUrl, maxLength = myMaxLength, prompt = "\"Please summarize the article below:\\n\\n${myUrl}\\n\\nSummary:\"")
            ).execute()
            response.body()?.summary ?: ""
        }
    }

    private interface Gpt3Api {
        @Headers("Content-Type: application/json")
        @POST("models/davinci-002/completions")
        fun summarizeArticle(
            @Header("Authorization") apiKey: String,
            @Body request: SummarizeArticleRequest
        ): Call<SummarizeArticleResponse>

    }
}

private data class SummarizeArticleRequest(
    @SerializedName("prompt")
    val prompt: String = "Please summarize the article below:\n\n{url}\n\nSummary:",
    @SerializedName("temperature")
    val temperature: Double = 0.5,
    @SerializedName("max_tokens")
    val maxTokens: Int = 5,
    @SerializedName("n")
    val n: Int = 1,
    @SerializedName("stop")
    val stop: List<String> = listOf("\n\n"),
    @SerializedName("url")
    val url: String,
    @SerializedName("maxLength")
    val maxLength: Int
)

private data class SummarizeArticleResponse(
    @SerializedName("choices")
    val choices: List<Choice>
) {

    data class Choice(
        @SerializedName("text")
        val text: String,
        @SerializedName("finish_reason")
        val finishReason: String
    )

    val summary: String
        get() = choices.firstOrNull()?.text ?: ""

}
