package com.bitla.ts.domain.pojo.modify_fare

import com.google.gson.annotations.SerializedName

data class OriginDestList(
    @SerializedName("origin_id")
    var originId: String = "",
    @SerializedName("origin")
    var origin: String = "",
    @SerializedName("destination_id")
    var destinationId: String = "",
    @SerializedName("destination")
    var destination: String = "",
)