package com.bitla.ts.domain.pojo.block_unblock_reservation

import com.google.gson.annotations.SerializedName

data class ServiceBlockReasonListResp (
    @SerializedName("code")
    var code: Int,

    @SerializedName("message")
    var message: String,

    @SerializedName("reasons")
    var reasons: MutableList<ReasonList>
)