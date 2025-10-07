package com.bitla.ts.domain.pojo.city_pair


import com.google.gson.annotations.*

data class Result(
    @SerializedName("d_time")
    val dTime: String,
    @SerializedName("destination_id")
    val destinationId: Int,
    @SerializedName("destination_name")
    val destinationName: String,
    @SerializedName("fare_details")
    var fareDetails: MutableList<FareDetail>,
    @SerializedName("o_day")
    val oDay: Int,
    @SerializedName("o_time")
    val oTime: String,
    @SerializedName("origin_id")
    val originId: Int,
    @SerializedName("origin_name")
    val originName: String,
    @SerializedName("is_checked")
    var isChecked : Boolean = false
)