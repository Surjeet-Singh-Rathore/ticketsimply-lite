package com.bitla.ts.domain.pojo.update_trip_status


import com.google.gson.annotations.SerializedName

data class UpdateTripReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("res_id")
    val resId: String,
    
    )