package com.bitla.ts.domain.pojo.view_reservation


import com.google.gson.annotations.SerializedName

data class CitySeqOrder(
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("pickup_closed")
    var pickupClosed: Boolean,
    @SerializedName("stage_time")
    var stageTime: String,
    @SerializedName("is_destination")
    var isDestination: Boolean,
    @SerializedName("is_source")
    var isSource: Boolean
) {
    override fun toString(): String {
        return name
    }
}