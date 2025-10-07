package com.bitla.ts.domain.pojo.merge_bus_seat_mapping.response


import com.google.gson.annotations.SerializedName

data class MergeBusSeatMappingResponse(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("success")
    val success: Boolean?
)