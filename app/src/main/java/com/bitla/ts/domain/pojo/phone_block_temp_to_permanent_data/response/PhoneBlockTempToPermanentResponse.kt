package com.bitla.ts.domain.pojo.phone_block_temp_to_permanent_data.response

import com.google.gson.annotations.SerializedName


data class PhoneBlockTempToPermanentResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)
