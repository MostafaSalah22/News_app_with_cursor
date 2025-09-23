package com.example.firstcursorapp.data.remote

import com.example.firstcursorapp.data.model.NewsSourcesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("sources")
    suspend fun getSources(
        @Query("country") country: String,
        @Query("apikey") apiKey: String
    ): NewsSourcesResponse
}


