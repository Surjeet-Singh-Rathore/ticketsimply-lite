package com.bitla.ts.domain.pojo.add_bp_dp_to_service.response


import com.google.gson.annotations.SerializedName

data class AddBpDpToServiceResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("message")
    val message: String?
)