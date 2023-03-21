package com.example.mynlpapiapplication.data

import com.google.gson.annotations.SerializedName

data class SummarizeResponse(
     @SerializedName("summary")
     val summary: String? = "",
     @SerializedName("error")
     val error: String? = ""
)
