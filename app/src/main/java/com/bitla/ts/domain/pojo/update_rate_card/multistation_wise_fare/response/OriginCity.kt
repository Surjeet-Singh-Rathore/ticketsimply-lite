package com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response


import com.google.gson.annotations.SerializedName

data class OriginCity(
    @SerializedName("city")
    val city: String,
    @SerializedName("id")
    val id: String
)