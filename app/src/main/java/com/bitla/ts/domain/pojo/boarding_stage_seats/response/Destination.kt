package com.bitla.ts.domain.pojo.boarding_stage_seats.response


import com.google.gson.annotations.SerializedName

data class Destination(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)