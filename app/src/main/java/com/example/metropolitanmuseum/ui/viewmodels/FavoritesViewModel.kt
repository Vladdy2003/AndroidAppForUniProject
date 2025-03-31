package com.example.metropolitanmuseum.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metropolitanmuseum.data.model.ArtObject
import com.example.metropolitanmuseum.data.repository.MuseumRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: MuseumRepository) : ViewModel() {
    val favorites: StateFlow<List<ArtObject>> = repository.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeFromFavorites(objectID: Int) {
        viewModelScope.launch {
            repository.removeFromFavorites(objectID)
        }
    }
}