package com.example.mynlpapiapplication.data

import com.google.gson.annotations.SerializedName

data class OpenAISummarizerResponse(
    @SerializedName("choices")
    val choices: List<Choice>?,
    @SerializedName("error")
    val error: Error?,
    var code : Int,
    var success : Boolean
) {
    data class Choice(
        @SerializedName("text")
        val text: String,
        @SerializedName("finish_reason")
        val finishReason: String,
        @SerializedName("index")
        val index: Int
    )

    data class Error(
        @SerializedName("message")
        val message: String?,
        @SerializedName("type")
        val type: String?,
        @SerializedName("param")
        val param: String?,
        @SerializedName("code")
        val code: String?
    )
}
