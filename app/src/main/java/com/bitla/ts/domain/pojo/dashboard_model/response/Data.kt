package com.bitla.ts.domain.pojo.dashboard_model.response


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("doj")
    val doj: String?,
    @SerializedName("expiry_time")
    val expiryTime: Any,
    @SerializedName("pnr_number")
    val pnrNumber: String? = "",
    @SerializedName("tkt_count")
    val tktCount: Int?
)