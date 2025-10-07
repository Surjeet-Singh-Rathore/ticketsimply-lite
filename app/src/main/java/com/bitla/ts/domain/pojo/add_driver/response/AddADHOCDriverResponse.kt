package com.bitla.ts.domain.pojo.add_driver.response


import com.google.gson.annotations.SerializedName

data class AddADHOCDriverResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String?
)