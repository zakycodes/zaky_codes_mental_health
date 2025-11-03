package com.zakycodes.zcmh.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val date: String, // Format: "31 Oktober 2025"
    val timestamp: Long = System.currentTimeMillis()
)