package com.example.metropolitanmuseum.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metropolitanmuseum.data.model.ArtObject
import com.example.metropolitanmuseum.data.repository.MuseumRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(val repository: MuseumRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Int>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Map for storing object details
    private val _objectDetails = MutableStateFlow<Map<Int, ArtObject>>(emptyMap())
    val objectDetails = _objectDetails.asStateFlow()

    init {
        viewModelScope.launch {
            @OptIn(FlowPreview::class)
            searchQuery
                .debounce(500)
                .collect { query ->
                    if (query.length >= 2) {
                        performSearch(query)
                    } else if (query.isEmpty()) {
                        _searchResults.value = emptyList()
                        _objectDetails.value = emptyMap()
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchArtObjects() {
        val query = _searchQuery.value
        if (query.isBlank()) return

        viewModelScope.launch {
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        _isLoading.value = true
        _error.value = null
        _searchResults.value = emptyList()
        _objectDetails.value = emptyMap()

        try {
            Log.d("SearchViewModel", "Searching for: $query")
            val result = repository.searchObjects(query)
            Log.d("SearchViewModel", "Result: ${result.total} items, IDs: ${result.objectIDs}")
            _searchResults.value = result.objectIDs ?: emptyList()
        } catch (e: Exception) {
            Log.e("SearchViewModel", "Error searching: ${e.message}", e)
            _error.value = "Error searching: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun loadObjectDetails(objectId: Int) {
        if (_objectDetails.value.containsKey(objectId)) return

        viewModelScope.launch {
            try {
                val details = repository.getObjectDetails(objectId)
                _objectDetails.update { currentMap ->
                    currentMap + (objectId to details)
                }
                Log.d("SearchViewModel", "Loaded details for object $objectId: ${details.title}")
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error loading details for object $objectId: ${e.message}", e)
            }
        }
    }
}