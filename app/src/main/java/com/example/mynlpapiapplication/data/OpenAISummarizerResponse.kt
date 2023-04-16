package com.example.mynlpapiapplication.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "openai_summarizer_response")
data class OpenAISummarizerResponse(
    @SerializedName("id")
    var id: String?,
    @SerializedName("choices")
    var choices: MutableList<Choice>? = null,
    @SerializedName("error")
    var error: Error? = null,
    @SerializedName("usage")
    var usage: Usage? = null,
    var requestString: String?,
    var temperature: Double,
    var maxTokens: Int,
    var responseTime: Long,
    var sendTime: Long,
    var code: Int,
    var success: Boolean,
    var markedForDelete: Boolean,
    @PrimaryKey(autoGenerate = true)
    var responseId: Long
) {
    constructor() : this(
        null,
        null,
        null,
        null,
        null,
        0.5,
        50,
        0,
        0,
        0,
        false,
        false,
        0
    )

    data class Choice(
        @SerializedName("text")
        val responseText: String,
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
        val prompt_tokens: Int = 0,
        @SerializedName("completion_tokens")
        val completion_tokens: Int = 0,
        @SerializedName("total_tokens")
        val total_tokens: Int = 0
    )

    fun getTurnaroundTime(): Long = responseTime - sendTime

    fun getSendDateString(): String? =
        SimpleDateFormat("MM/dd/YYYY hh:mm:ss.SSS", Locale.getDefault()).format(Date(sendTime))

    fun putInfo(
        urlString: String,
        maxTokens: Int,
        temperature: Double,
        code: Int,
        sentRequestAtMillis: Long,
        receivedResponseAtMillis: Long,
        isSuccessful: Boolean
    ): OpenAISummarizerResponse {
        requestString = urlString
        this.maxTokens = maxTokens
        this.temperature = temperature
        this.code = code
        this.sendTime = sentRequestAtMillis
        this.responseTime = receivedResponseAtMillis
        this.success = isSuccessful
        return this
    }

}

class OpenAISummarizerResponseConverters {
    @TypeConverter
    fun fromChoiceList(choiceList: List<OpenAISummarizerResponse.Choice>): String =
        Gson().toJson(choiceList)

    @TypeConverter
    fun toChoiceList(choiceListString: String): List<OpenAISummarizerResponse.Choice>? =
        if (choiceListString.isEmpty()) {
            null
        } else {
            Gson().fromJson(
                choiceListString,
                object : TypeToken<List<OpenAISummarizerResponse.Choice>>() {}.type
            )
        }

    @TypeConverter
    fun fromError(error: OpenAISummarizerResponse.Error): String = Gson().toJson(error)

    @TypeConverter
    fun toError(error: String): OpenAISummarizerResponse.Error? = if (error.isEmpty()) {
        null
    } else {
        Gson().fromJson(
            error,
            object : TypeToken<List<OpenAISummarizerResponse.Choice>>() {}.type
        )
    }

    @TypeConverter
    fun fromUsage(usage: OpenAISummarizerResponse.Usage): String = Gson().toJson(usage)

    @TypeConverter
    fun toUsage(usage: String): OpenAISummarizerResponse.Usage? = if (usage.isEmpty()) {
        null
    } else {
        Gson().fromJson(
            usage,
            OpenAISummarizerResponse.Usage::class.java
        )
    }

    @TypeConverter
    fun fromChoice(usage: OpenAISummarizerResponse.Choice): String = Gson().toJson(usage)

    @TypeConverter
    fun toChoice(usage: String): OpenAISummarizerResponse.Choice =
        Gson().fromJson(usage, OpenAISummarizerResponse.Choice::class.java)

    @TypeConverter
    fun toTrimmedString(original: String): String {
        return original.trim()
    }
}
