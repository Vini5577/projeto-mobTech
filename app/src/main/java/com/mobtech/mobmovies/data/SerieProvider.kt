package com.mobtech.mobmovies.data

data class SerieProvider(
    val id: Int,
    val results: Map<String, SerieProviderData>
)

data class SerieProviderData(
    val link: String,
    val flatrate: List<SerieFlatRateData>
)

data class SerieFlatRateData(
    val logo_path: String,
    val provider_id: Int,
    val provider_name: String,
    val display_priority: Int
)