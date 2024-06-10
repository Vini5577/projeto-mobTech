package com.mobtech.mobmovies.service

import com.mobtech.mobmovies.data.PersonData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PersonApiService {

    @GET("person/{person_id}")
    fun getPersonDetails(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Call<PersonData>

}