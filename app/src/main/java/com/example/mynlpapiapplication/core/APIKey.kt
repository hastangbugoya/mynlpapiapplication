package com.example.mynlpapiapplication.core

import android.util.Log

class APIKey(
    private val myInfo: MySharedPreference?,
    private var key: String? = null,
) {

    constructor(myInfo: MySharedPreference) : this(
        myInfo, null
    )

    fun setAPIKey(_key: String?) {
        key = _key?.trim()
    }

    fun getAPIKey(): String? = key

    fun isNullorEmpty(): Boolean = key.isNullOrEmpty()


    fun resetKey() {
        key = null
    }

    fun getAPIInfo() {
        key = myInfo?.getString("myOpenAIKey")
    }

    fun saveAPIInfo() {
        myInfo?.saveAPIKey( key ?: "")
    }
}