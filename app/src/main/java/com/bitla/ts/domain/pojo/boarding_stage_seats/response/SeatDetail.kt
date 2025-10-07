package com.bitla.ts.domain.pojo.boarding_stage_seats.response


import com.google.gson.annotations.SerializedName

data class SeatDetail(
    @SerializedName("available")
    val available: Boolean?,
    @SerializedName("background_color")
    val backgroundColor: String?,
    @SerializedName("base_fare_filter")
    val baseFareFilter: Int?,
    @SerializedName("boarded_status")
    val boardedStatus: Int?,
    @SerializedName("col_id")
    val colId: Int?,
    @SerializedName("fare")
    val fare: Double?,
    @SerializedName("gangway_type")
    val gangwayType: String?,
    @SerializedName("is_blocked_seat")
    val isBlockedSeat: Boolean?,
    @SerializedName("is_boarded")
    val isBoarded: Boolean?,
    @SerializedName("is_frequent_traveller")
    val isFrequentTraveller: Boolean?,
    @SerializedName("is_gangway")
    val isGangway: Boolean?,
    @SerializedName("is_gents_seat")
    val isGentsSeat: Boolean?,
    @SerializedName("is_horizontal")
    val isHorizontal: Boolean?,
    @SerializedName("is_in_journey")
    val isInJourney: Boolean?,
    @SerializedName("is_ladies_seat")
    val isLadiesSeat: Boolean?,
    @SerializedName("is_multi_hop")
    val isMultiHop: Boolean?,
    @SerializedName("is_open_tooltip")
    val isOpenTooltip: Boolean?,
    @SerializedName("is_seat")
    val isSeat: Boolean?,
    @SerializedName("is_shifted")
    val isShifted: Boolean?,
    @SerializedName("is_social_distancing")
    val isSocialDistancing: Boolean?,
    @SerializedName("is_updated")
    val isUpdated: Boolean?,
    @SerializedName("max_fare")
    val maxFare: Double?,
    @SerializedName("min_fare")
    val minFare: Double?,
    @SerializedName("number")
    val number: String?,
    @SerializedName("passenger_details")
    val passengerDetails: PassengerDetails?,
    @SerializedName("row_id")
    val rowId: Int?,
    @SerializedName("seat_discount")
    val seatDiscount: Int?,
    @SerializedName("type")
    val type: String?
)