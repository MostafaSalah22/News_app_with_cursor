package com.example.firstcursorapp.data.repository

import com.example.firstcursorapp.data.model.NewsSource
import com.example.firstcursorapp.data.remote.NewsApiService

class NewsRepository(
    private val api: NewsApiService
) {
    suspend fun getSources(country: String, apiKey: String): Result<List<NewsSource>> = try {
        val response = api.getSources(country = country, apiKey = apiKey)
        val list = response.results ?: emptyList()
        Result.success(list)
    } catch (t: Throwable) {
        Result.failure(t)
    }
}


