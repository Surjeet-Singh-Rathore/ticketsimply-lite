package com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response

import com.google.gson.annotations.SerializedName

data class QuickBookServiceDetailsResponse(
    @SerializedName("boarding_stages")
    val boardingStages: List<BoardingStage>,
    @SerializedName("booked")
    val booked: Int,
    @SerializedName("city_seq_order")
    val citySeqOrder: List<CitySeqOrder>? = null,
    @SerializedName("code")
    val code: Int,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("destination_id")
    val destinationId: Int,
    @SerializedName("dropoff_stages")
    val dropoffStages: List<DropoffStage>,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("origin_id")
    val originId: Int,
    @SerializedName("quota_blocked")
    val quotaBlocked: Int,
    @SerializedName("result")
    val result: List<Result>,
    @SerializedName("total_seats")
    val totalSeats: Int,
    @SerializedName("message")
    val message: String?
)