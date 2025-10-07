package com.bitla.ts.domain.pojo.unblock_seat.request


import com.google.gson.annotations.SerializedName

data class SearchbusParams(
    @SerializedName("from")
    var from: String?,
    @SerializedName("to")
    var to: String?
)