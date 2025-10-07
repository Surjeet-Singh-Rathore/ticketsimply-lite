package com.bitla.ts.domain.pojo.phonepe_direct_validate_upi_id.response


import com.google.gson.annotations.SerializedName

data class PhonePeDirectValidateUpiIdResponse(
    @SerializedName("code")
    val code: String?,
    @SerializedName("data")
    val data: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("success")
    val success: Boolean?
)