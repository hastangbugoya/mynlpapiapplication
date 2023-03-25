package com.example.mynlpapiapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MyNLPAPIDatabaseDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpenAISummarizerResponse(item: OpenAISummarizerResponse)

    @Query("DELETE FROM openai_summarizer_response WHERE id = :searchId")
    suspend fun deleteByID(searchId : Long) : Int

    @Query("SELECT * FROM openai_summarizer_response")
    suspend fun getAll() : MutableList<OpenAISummarizerResponse>?
    // Add other methods as needed
}