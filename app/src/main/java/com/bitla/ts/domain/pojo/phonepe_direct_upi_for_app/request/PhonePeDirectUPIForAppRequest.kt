package com.bitla.ts.domain.pojo.phonepe_direct_upi_for_app.request


import com.google.gson.annotations.SerializedName

data class PhonePeDirectUPIForAppRequest(
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("pnr_number")
    val pnrNumber: String?,
    @SerializedName("upi_type")
    val upiType: String?,
    @SerializedName("user_number")
    val userNumber: String?,
    @SerializedName("vpa")
    val vpa: String?
)