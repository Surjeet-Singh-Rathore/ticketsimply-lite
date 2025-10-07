package com.bitla.ts.domain.pojo.notification_details_phase_3.response


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("is_map_show")
    val isMapShow: Boolean?,
    @SerializedName("label")
    val label: String,
    @SerializedName("latt")
    val latt: Double?,
    @SerializedName("long")
    val long: Int?,
    @SerializedName("value")
    val value: String
)