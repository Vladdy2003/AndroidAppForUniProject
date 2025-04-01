package com.example.metropolitanmuseum.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metropolitanmuseum.data.model.ArtObject
import com.example.metropolitanmuseum.data.repository.MuseumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MainScreenViewModel(private val repository: MuseumRepository) : ViewModel() {
    private val _objectIds = MutableStateFlow<List<Int>>(emptyList())
    val objectIds = _objectIds.asStateFlow()

    private val _objectDetails = MutableStateFlow<Map<Int, ArtObject>>(emptyMap())
    val objectDetails = _objectDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // List of pre-verified IDs that we know exist
    private val guaranteedIds = listOf(
        11417, 12544, 14939, 436535, 437133, 438814, 35633, 39901, 44819, 45734,
        205264, 226051, 436101, 437287, 437304, 437309, 437310, 437311, 437312, 437862
    )

    init {
        loadInitialObjects()
    }

    private fun loadInitialObjects() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _objectIds.value = emptyList()
            _objectDetails.value = emptyMap()

            try {
                // Starting with a set of guaranteed IDs to ensure we see content
                _objectIds.value = guaranteedIds.toList()

                //Loading details for guaranteed IDs
                guaranteedIds.forEach { id ->
                    try {
                        val details = repository.getObjectDetails(id)
                        _objectDetails.update { currentMap ->
                            currentMap + (id to details)
                        }
                        delay(100)
                    } catch (e: Exception) {
                        Log.e("MainScreenViewModel", "Error loading guaranteed ID $id: ${e.message}")
                    }
                }

                // After loading the guaranteed IDs, we start with sequential IDs
                if (_objectDetails.value.isNotEmpty()) {
                    // If we have objects loaded, we mark loading as false
                    _isLoading.value = false
                } else {
                    // If we couldn't load any objects, we show an error
                    _error.value = "Could not load any artwork. Please check your connection."
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("MainScreenViewModel", "Error in initial load: ${e.message}", e)
                _error.value = "Failed to load artworks: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // This will be called when the user scrolls down.
    fun loadMoreObjects() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Loading random objects from known ranges
                val randomRanges = listOf(1000..5000, 10000..15000, 20000..25000, 30000..35000, 40000..45000)
                val randomRange = randomRanges.random()
                val randomIds = (1..20).map { randomRange.random() }

                val newIds = mutableListOf<Int>()

                for (id in randomIds) {
                    try {
                        val details = repository.getObjectDetails(id)
                        _objectDetails.update { currentMap ->
                            currentMap + (id to details)
                        }
                        newIds.add(id)
                        delay(100)
                    } catch (e: Exception) {
                        Log.d("MainScreenViewModel", "ID $id not found: ${e.message}")
                    }
                }

                if (newIds.isNotEmpty()) {
                    _objectIds.update { currentIds ->
                        currentIds + newIds
                    }
                }

            } catch (e: Exception) {
                Log.e("MainScreenViewModel", "Error loading more objects: ${e.message}", e)
                _error.value = "Failed to load more artworks: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retryLoading() {
        loadInitialObjects()
    }
}