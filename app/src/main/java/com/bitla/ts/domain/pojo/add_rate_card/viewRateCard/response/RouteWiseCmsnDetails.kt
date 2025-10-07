package com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response


import com.google.gson.annotations.SerializedName

data class RouteWiseCmsnDetails(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("city_wise_cmsn")
    val cityWiseCmsn: MutableList<CityWiseCmsn>,
    @SerializedName("cmsn")
    val cmsn: String,
    @SerializedName("inc_or_dec")
    val incOrDec: String,
    @SerializedName("seat_types")
    val seatTypes: String,
    @SerializedName("type")
    val type: String,

)