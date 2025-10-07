package com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response


import com.google.gson.annotations.SerializedName

data class CitySeqOrder(
    @SerializedName("id")
    val id: Int?=null,
    @SerializedName("name")
    val name: String,

    @SerializedName("is_destination")
    val isDestination: Boolean?=null,

    @SerializedName("is_source")
    val isSource: Boolean?=null,

    @SerializedName("pickup_closed")
    val pickupClosed: Boolean?=null,
    @SerializedName("stage_time")
    val stageTime: String?=null
)