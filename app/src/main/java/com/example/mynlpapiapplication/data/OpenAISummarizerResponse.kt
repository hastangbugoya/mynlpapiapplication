package com.example.mynlpapiapplication.data

import com.google.gson.annotations.SerializedName

data class OpenAISummarizerResponse(
    @SerializedName("choices")
    val choices: List<Choice>?,
    @SerializedName("error")
    val error : String? = ""
) {
    data class Choice(
        @SerializedName("text")
        val text: String,
        @SerializedName("finish_reason")
        val finishReason: String,
        @SerializedName("index")
        val index: Int
    )
}
