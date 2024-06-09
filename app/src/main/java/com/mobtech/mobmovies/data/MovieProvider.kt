package com.mobtech.mobmovies.data

data class MovieProvider(
    val id: Int,
    val results: Map<String, ProviderData>
)

data class ProviderData(
    val link: String,
    val flatrate: List<FlatRateData>
)

data class FlatRateData(
    val logo_path: String,
    val provider_id: Int,
    val provider_name: String,
    val display_priority: Int
)

