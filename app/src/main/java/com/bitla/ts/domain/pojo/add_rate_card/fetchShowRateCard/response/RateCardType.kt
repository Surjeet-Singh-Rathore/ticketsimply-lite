package com.bitla.ts.domain.pojo.add_rate_card.fetchShowRateCard.response


import com.google.gson.annotations.SerializedName

data class RateCardType(
    @SerializedName("cms")
    val cms: Boolean,
    @SerializedName("fare")
    val fare: Boolean,
    @SerializedName("time")
    val time: Boolean
)