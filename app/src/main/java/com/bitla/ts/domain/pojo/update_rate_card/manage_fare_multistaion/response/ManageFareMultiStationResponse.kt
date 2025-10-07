package com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.response

import com.google.gson.annotations.SerializedName

data class ManageFareMultiStationResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: Result?,
)
