package com.example.mynlpapiapplication.data

import android.content.Context
import androidx.room.*

@Database(entities = [OpenAISummarizerResponse::class], version = 1)
@TypeConverters(OpenAISummarizerResponseConverters::class)
abstract class MyNLPAPIDatabase : RoomDatabase() {
    abstract fun openAISummarizerResponseDao(): MyNLPAPIDatabaseDAO
    companion object {
        @Volatile
        private var instance: MyNLPAPIDatabase? = null

        fun getInstance(context: Context): MyNLPAPIDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MyNLPAPIDatabase::class.java,
                    "mynlpapiapplication_db"
                ).build().also { instance = it }
            }
        }
    }
}