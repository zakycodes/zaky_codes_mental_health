package com.zakycodes.zcmh.data.repository

import com.zakycodes.zcmh.data.database.JournalDao
import com.zakycodes.zcmh.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow

class JournalRepository(private val journalDao: JournalDao) {

    val allJournals: Flow<List<JournalEntry>> = journalDao.getAllJournals()

    suspend fun getJournalById(id: Int): JournalEntry? {
        return journalDao.getJournalById(id)
    }

    suspend fun insertJournal(journal: JournalEntry) {
        journalDao.insertJournal(journal)
    }

    suspend fun updateJournal(journal: JournalEntry) {
        journalDao.updateJournal(journal)
    }

    suspend fun deleteJournal(journal: JournalEntry) {
        journalDao.deleteJournal(journal)
    }

    suspend fun deleteAllJournals() {
        journalDao.deleteAllJournals()
    }
}