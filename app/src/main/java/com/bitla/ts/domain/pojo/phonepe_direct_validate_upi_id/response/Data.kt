package com.bitla.ts.domain.pojo.phonepe_direct_validate_upi_id.response


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("name")
    val name: String?,
    @SerializedName("vpa")
    val vpa: String?
)