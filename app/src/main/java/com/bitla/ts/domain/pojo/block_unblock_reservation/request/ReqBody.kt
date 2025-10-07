package com.bitla.ts.domain.pojo.block_unblock_reservation.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    var apiKey: String,
    @SerializedName("reason")
    var reason: String,
    @SerializedName("reservation_id")
    var reservationId: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?,
    var is_encrypted:Boolean?=null,
    @SerializedName("auth_pin")
    var authPin: String,
    @SerializedName("blocking_reason")
    var blockingReason: String? = null
)