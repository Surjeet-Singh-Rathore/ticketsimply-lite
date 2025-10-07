package com.bitla.ts.domain.pojo.merge_service_details.response


import com.google.gson.annotations.SerializedName

data class SeatDetail(
    @SerializedName("available")
    val available: Boolean?,
    @SerializedName("base_fare_filter")
    val baseFareFilter: Int?,
    @SerializedName("col_id")
    val colId: Int?,
    @SerializedName("fare")
    val fare: Int?,
    @SerializedName("gangway_type")
    val gangwayType: String?,
    @SerializedName("is_blocked_seat")
    val isBlockedSeat: Boolean?,
    @SerializedName("is_gangway")
    val isGangway: Boolean?,
    @SerializedName("is_gents_seat")
    val isGentsSeat: Boolean?,
    @SerializedName("is_his_booking")
    val isHisBooking: Boolean?,
    @SerializedName("is_horizontal")
    val isHorizontal: Boolean?,
    @SerializedName("is_ladies_seat")
    val isLadiesSeat: Boolean?,
    @SerializedName("is_seat")
    val isSeat: Boolean?,
    @SerializedName("is_social_distancing")
    val isSocialDistancing: Boolean?,
    @SerializedName("max_fare")
    val maxFare: Int?,
    @SerializedName("min_fare")
    val minFare: Int?,
    @SerializedName("number")
    val number: String?,
    @SerializedName("row_id")
    val rowId: Int?,
    @SerializedName("type")
    val type: String?
)