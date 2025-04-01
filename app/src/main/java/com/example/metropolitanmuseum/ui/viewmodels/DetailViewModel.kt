package com.example.metropolitanmuseum.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metropolitanmuseum.data.model.ArtObject
import com.example.metropolitanmuseum.data.repository.MuseumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: MuseumRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val objectId: Int = checkNotNull(savedStateHandle["objectId"])

    private val _artObject = MutableStateFlow<ArtObject?>(null)
    val artObject = _artObject.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    init {
        loadArtObjectDetails()
        checkIfFavorite()
    }

    private fun loadArtObjectDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _artObject.value = repository.getObjectDetails(objectId)
            } catch (e: Exception) {
                _error.value = "Error while loading details: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun checkIfFavorite() {
        viewModelScope.launch {
            try {
                _isFavorite.value = repository.isFavorite(objectId)
            } catch (e: Exception) {

            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentObject = _artObject.value ?: return@launch
            try {
                if (_isFavorite.value) {
                    repository.removeFromFavorites(objectId)
                } else {
                    repository.addToFavorites(currentObject)
                }
                _isFavorite.value = !_isFavorite.value
            } catch (e: Exception) {
                _error.value = "Error while updating favorites: ${e.message}"
            }
        }
    }
}