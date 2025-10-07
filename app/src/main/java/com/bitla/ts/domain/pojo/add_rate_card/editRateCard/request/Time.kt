package com.bitla.ts.domain.pojo.add_rate_card.editRateCard.request


import com.google.gson.annotations.SerializedName

data class Time(
    @SerializedName("apply_for")
    val applyFor: ApplyFor,
    @SerializedName("category")
    val category: String,
    @SerializedName("inc_or_dec")
    val incOrDec: String,
    @SerializedName("time")
    val time: String
)