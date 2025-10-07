package com.bitla.ts.domain.pojo.block_unblock_reservation


import com.google.gson.annotations.SerializedName

data class BlockUnblockReservation(
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String
)