package com.example.metropolitanmuseum.data.remote

import com.example.metropolitanmuseum.data.model.ArtObject
import com.example.metropolitanmuseum.data.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MetMuseumApiService {
    @GET("public/collection/v1/search")
    suspend fun searchObjects(@Query("q") query: String): SearchResult

    @GET("public/collection/v1/objects/{objectID}")
    suspend fun getObjectDetails(@Path("objectID") objectID: Int): ArtObject
}
