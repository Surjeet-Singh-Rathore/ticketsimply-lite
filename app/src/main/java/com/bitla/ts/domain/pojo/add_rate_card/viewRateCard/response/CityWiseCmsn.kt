package com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response


import com.google.gson.annotations.SerializedName

data class CityWiseCmsn(
    @SerializedName("cmsn_details")
    val cmsnDetails: MutableList<CmsnDetail>,
    @SerializedName("destination_id")
    val destinationId: String,
    @SerializedName("destination_name")
    val destinationName: String,
    @SerializedName("origin_id")
    val originId: String,
    @SerializedName("origin_name")
    val originName: String,
    var isExpandable: Boolean = false
)