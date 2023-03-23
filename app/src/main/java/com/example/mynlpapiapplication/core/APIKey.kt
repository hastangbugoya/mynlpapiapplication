package com.example.mynlpapiapplication.core

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
        key = myInfo?.getString("myKey")
    }

    fun saveAPIInfo() {
        myInfo?.saveString("myKey", key ?: "")
    }
}