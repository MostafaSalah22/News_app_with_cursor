package com.example.firstcursorapp.data.model

import com.google.gson.annotations.SerializedName

data class NewsSourcesResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("totalResults") val totalResults: Int?,
    @SerializedName("results") val results: List<NewsSource>?
)

data class NewsSource(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("icon") val icon: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: List<String>?,
    @SerializedName("url") val url: String?,
    @SerializedName("website") val website: String?,
    @SerializedName("link") val link: String?
)


