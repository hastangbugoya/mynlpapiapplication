package com.example.mynlpapiapplication.core

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class MySharedPreference(context: Context) {
    private val masterKeyAlias  = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val pref = EncryptedSharedPreferences.create(
        context,
        "my_secure_prefs",
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveString(key: String, value: String) {
        Log.d("Meow", ">> $key - $value")
        pref.edit().putString(key, value).apply()
    }

    fun saveAPIKey(key : String?) {
        pref.edit().putString("myOpenAIKey", key?.trim() ?: "no key").apply()
    }

    fun saveLastURL(s : String) {
        saveString("lastURL", s)
    }

    fun getLastURL() : String = pref.getString("lastURL", "") ?: ""

    fun saveLastSummary(s : String) {
        pref.edit().putString("lastResult",s).apply()
    }

    fun getLastSummary() : String = pref.getString("lastResult","") ?: ""

    fun saveMaxTokens(max : Int) {
        pref.edit().putInt("maxTokens", max).apply()
    }

    fun getMaxTokens() : Int = pref.getInt("maxTokens", 50)

    fun saveTemperature(temp : Double) {
        pref.edit().putFloat("temperature", temp.toFloat()).commit()
    }

    fun getTemperature() : Double = pref.getFloat("temperature", 0.5F).toDouble()

    fun getString(key: String): String? {
        return pref.getString(key, null)
    }

    fun clearData() {
        pref.edit().clear().apply()
    }

    companion object {
        private var instance: MySharedPreference? = null

        fun getInstance(context: Context): MySharedPreference {
            if (instance == null) {
                instance = MySharedPreference(context)
            }
            return instance!!
        }
    }
}