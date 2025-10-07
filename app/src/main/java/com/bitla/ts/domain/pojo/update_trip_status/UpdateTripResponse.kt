package com.bitla.ts.domain.pojo.update_trip_status


import com.google.gson.annotations.SerializedName

data class UpdateTripResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    
    )