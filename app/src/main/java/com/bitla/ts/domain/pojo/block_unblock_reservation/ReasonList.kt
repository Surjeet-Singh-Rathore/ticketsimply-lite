package com.bitla.ts.domain.pojo.block_unblock_reservation

import com.google.gson.annotations.SerializedName

data class ReasonList(
    @SerializedName("id")
    var id: String,

    @SerializedName("value")
    var value: String,
)
