package com.bitla.ts.data

import com.bitla.ts.domain.pojo.direction_response.DirectionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface DirectionsService {
    @GET("directions/json")
    fun getDirections(
        @Query("origin") origin: String?,
        @Query("destination") destination: String?,
        @Query("key") apiKey: String?,
        @Query("waypoints") waypoints: String? // Add waypoints parameter
    ): Call<DirectionsResponse?>?
}
