package com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request


import com.google.gson.annotations.SerializedName

data class ChannelType(
    @SerializedName("branch")
    val branch: Boolean,
    @SerializedName("api")
    val api: Boolean,
    @SerializedName("ebooking")
    val ebooking: Boolean,
    @SerializedName("online")
    val online: Boolean
)