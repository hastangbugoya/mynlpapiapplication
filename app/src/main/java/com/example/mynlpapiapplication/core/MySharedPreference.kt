package com.example.mynlpapiapplication.core

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class MySharedPreference(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val pref = EncryptedSharedPreferences.create(
        "my_secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveString(key: String, value: String) {
        pref.edit().putString(key, value).apply()
    }

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