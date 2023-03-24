package com.example.mynlpapiapplication.data

import com.google.gson.annotations.SerializedName

data class OpenAISummarizerResponse(
    @SerializedName("choices")
    val choices: List<Choice>?,
    @SerializedName("error")
    val error: Error?,
    @SerializedName("usage")
    val usage : Usage?,
    var code : Int = 0,
    var success : Boolean
) {
    constructor() : this(null,null,null,0, false)

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
    data class Usage(
        @SerializedName("prompt_tokens")
        val prompt_tokens : Int = 0,
        @SerializedName("completion_tokens")
        val completion_tokens : Int = 0,
        @SerializedName("total_tokens")
        val total_tokens : Int = 0
    )
}
