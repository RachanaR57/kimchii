package org.app.project.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.app.project.data.EntryRepository
import org.app.project.model.Entry

class EntryListViewModel(
    private val repository: EntryRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val entries: StateFlow<List<Entry>> =
        _query
            .flatMapLatest { q ->
                if (q.isBlank()) repository.observeAll()
                else repository.search(q)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun addEntry(entry: Entry) {
        viewModelScope.launch {
            repository.add(entry)
        }
    }

    fun deleteEntry(id: String) {
        viewModelScope.launch {
            repository.softDelete(id)
        }
    }

    suspend fun getEntry(id: String): Entry? = repository.getById(id)

    fun updateEntry(entry: Entry) {
        viewModelScope.launch {
            repository.update(entry)
        }
    }
}