package com.bitla.ts.domain.pojo.notification_details_phase_3.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("view_type")
    val viewType: String
)