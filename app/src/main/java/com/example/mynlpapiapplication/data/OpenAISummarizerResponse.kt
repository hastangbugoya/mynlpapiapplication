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

    fun getSendDateString(): String? = if (sendTime == 0L) {
        SimpleDateFormat("MM/dd/yy hh:mm:ss.SSS", Locale.getDefault()).format(Date(sendTime))
    } else {
        null
    }

}

class OpenAISummarizerResponseConverters {
    @TypeConverter
    fun fromChoiceList(choiceList: List<OpenAISummarizerResponse.Choice>): String {
        val gson = Gson()
        return gson.toJson(choiceList)
    }

    @TypeConverter
    fun toChoiceList(choiceListString: String): List<OpenAISummarizerResponse.Choice>? {
        if (choiceListString.isNullOrEmpty()) {
            return null
        }
        val gson = Gson()
        return gson.fromJson(
            choiceListString,
            object : TypeToken<List<OpenAISummarizerResponse.Choice>>() {}.type
        )
    }

    @TypeConverter
    fun fromError(error: OpenAISummarizerResponse.Error): String {
        val gson = Gson()
        return gson.toJson(error)
    }

    @TypeConverter
    fun toError(error: String): OpenAISummarizerResponse.Error? {
        if (error.isNullOrEmpty()) {
            return null
        }
        val gson = Gson()
        return gson.fromJson(
            error,
            object : TypeToken<List<OpenAISummarizerResponse.Choice>>() {}.type
        )
    }

    @TypeConverter
    fun fromUsage(usage: OpenAISummarizerResponse.Usage): String {
        val gson = Gson()
        return gson.toJson(usage)
    }

    @TypeConverter
    fun toUsage(usage: String): OpenAISummarizerResponse.Usage? {
        if (usage.isNullOrEmpty()) {
            return null
        }
        val gson = Gson()
        return gson.fromJson(
            usage,
            object : TypeToken<List<OpenAISummarizerResponse.Usage>>() {}.type
        )
    }

    @TypeConverter
    fun fromChoice(usage: OpenAISummarizerResponse.Choice): String {
        val gson = Gson()
        return gson.toJson(usage)
    }

    @TypeConverter
    fun toChoice(usage: String): OpenAISummarizerResponse.Choice {
        val gson = Gson()
        return gson.fromJson(usage, object : TypeToken<OpenAISummarizerResponse.Choice>() {}.type)
    }

    @TypeConverter
    fun toTrimmedString(original: String): String {
        return original.trim()
    }

}
