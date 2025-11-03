package com.zakycodes.zcmh.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zakycodes.zcmh.data.database.AppDatabase
import com.zakycodes.zcmh.data.model.JournalEntry
import com.zakycodes.zcmh.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JournalRepository

    val allJournals: StateFlow<List<JournalEntry>>

    private val _selectedJournal = MutableStateFlow<JournalEntry?>(null)
    val selectedJournal: StateFlow<JournalEntry?> = _selectedJournal

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog

    init {
        val journalDao = AppDatabase.getDatabase(application).journalDao()
        repository = JournalRepository(journalDao)

        allJournals = repository.allJournals.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun insertJournal(title: String, content: String) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val currentDate = dateFormat.format(Date())

            val journal = JournalEntry(
                title = title,
                content = content,
                date = currentDate
            )

            repository.insertJournal(journal)
        }
    }

    fun updateJournal(journal: JournalEntry) {
        viewModelScope.launch {
            repository.updateJournal(journal)
        }
    }

    fun deleteJournal(journal: JournalEntry) {
        viewModelScope.launch {
            repository.deleteJournal(journal)
            _showDeleteDialog.value = false
        }
    }

    fun selectJournal(journal: JournalEntry?) {
        _selectedJournal.value = journal
    }

    fun showDeleteConfirmation() {
        _showDeleteDialog.value = true
    }

    fun dismissDeleteDialog() {
        _showDeleteDialog.value = false
    }

    // Gratitude prompts
    fun getGratitudePrompts(): List<String> {
        return listOf(
            "Apa 3 hal kecil yang membuat saya tersenyum hari ini?",
            "Siapa yang membuat hari saya lebih baik, dan kenapa?",
            "Apa pencapaian kecil yang saya syukuri hari ini?",
            "Apa yang membuat saya merasa aman atau nyaman hari ini?",
            "Apa hal baik tentang diri saya yang saya syukuri?"
        )
    }
}