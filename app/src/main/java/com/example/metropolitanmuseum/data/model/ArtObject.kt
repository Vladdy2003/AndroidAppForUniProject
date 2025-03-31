package com.example.metropolitanmuseum.data.model

data class ArtObject(
    val objectID: Int = 0,
    val title: String = "",
    val primaryImage: String = "",
    val additionalImages: List<String> = emptyList(),
    val department: String = "",
    val objectName: String = "",
    val artistDisplayName: String = "",
    val medium: String = "",
    val period: String = "",
    val culture: String = "",
    val objectDate: String = ""
)