package com.example.metropolitanmuseum.data.repository

import com.example.metropolitanmuseum.data.local.FavoritesDao
import com.example.metropolitanmuseum.data.local.toArtObject
import com.example.metropolitanmuseum.data.local.toFavoriteEntity
import com.example.metropolitanmuseum.data.model.ArtObject
import com.example.metropolitanmuseum.data.model.SearchResult
import com.example.metropolitanmuseum.data.remote.MetMuseumApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MuseumRepository(
    private val apiService: MetMuseumApiService,
    private val favoritesDao: FavoritesDao
) {
    suspend fun searchObjects(query: String): SearchResult {
        return apiService.searchObjects(query)
    }

    suspend fun getObjectDetails(objectID: Int): ArtObject {
        return apiService.getObjectDetails(objectID)
    }

    suspend fun getAllObjects(): SearchResult {
        return apiService.getAllObjects()
    }

    // Metode pentru Favorite
    suspend fun addToFavorites(artObject: ArtObject) {
        favoritesDao.insert(artObject.toFavoriteEntity())
    }

    suspend fun removeFromFavorites(objectID: Int) {
        favoritesDao.deleteById(objectID)
    }

    fun getAllFavorites(): Flow<List<ArtObject>> {
        return favoritesDao.getAllFavorites().map { list ->
            list.map { it.toArtObject() }
        }
    }

    suspend fun isFavorite(objectID: Int): Boolean {
        return favoritesDao.getById(objectID) != null
    }
}