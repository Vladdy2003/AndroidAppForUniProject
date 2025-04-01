package com.example.metropolitanmuseum.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.metropolitanmuseum.data.model.ArtObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val objectID: Int,
    val title: String,
    val primaryImage: String,
    val additionalImages: String,
    val department: String,
    val objectName: String,
    val artistDisplayName: String,
    val medium: String,
    val period: String,
    val culture: String,
    val objectDate: String
)

fun FavoriteEntity.toArtObject(): ArtObject {
    val type = object : TypeToken<List<String>>() {}.type
    val additionalImagesArray: List<String> = Gson().fromJson(additionalImages, type)
    return ArtObject(
        objectID = objectID,
        title = title,
        primaryImage = primaryImage,
        additionalImages = additionalImagesArray,
        department = department,
        objectName = objectName,
        artistDisplayName = artistDisplayName,
        medium = medium,
        period = period,
        culture = culture,
        objectDate = objectDate
    )
}

fun ArtObject.toFavoriteEntity(): FavoriteEntity {
    return FavoriteEntity(
        objectID = objectID,
        title = title,
        primaryImage = primaryImage,
        additionalImages = Gson().toJson(additionalImages),
        department = department,
        objectName = objectName,
        artistDisplayName = artistDisplayName,
        medium = medium,
        period = period,
        culture = culture,
        objectDate = objectDate
    )
}