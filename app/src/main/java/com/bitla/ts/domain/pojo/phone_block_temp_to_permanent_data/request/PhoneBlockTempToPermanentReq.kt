package com.bitla.ts.domain.pojo.phone_block_temp_to_permanent_data.request

import com.google.gson.annotations.SerializedName

data class PhoneBlockTempToPermanentReq(
    @SerializedName("api_key")
    val apiKey: String?,
    @SerializedName("pnr_number")
    val pnrNumber: String?
)