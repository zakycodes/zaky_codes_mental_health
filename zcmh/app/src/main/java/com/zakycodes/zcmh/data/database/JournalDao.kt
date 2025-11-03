package com.zakycodes.zcmh.data.database

import androidx.room.*
import com.zakycodes.zcmh.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    // Get semua journal, sorted by timestamp descending (terbaru dulu)
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllJournals(): Flow<List<JournalEntry>>

    // Get journal by ID
    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getJournalById(id: Int): JournalEntry?

    // Insert journal baru
    @Insert
    suspend fun insertJournal(journal: JournalEntry)

    // Update journal
    @Update
    suspend fun updateJournal(journal: JournalEntry)

    // Delete journal
    @Delete
    suspend fun deleteJournal(journal: JournalEntry)

    // Delete all journals
    @Query("DELETE FROM journal_entries")
    suspend fun deleteAllJournals()
}