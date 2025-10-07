package com.bitla.ts.domain.pojo.add_rate_card.fetchShowRateCard.response


import com.google.gson.annotations.SerializedName

data class RouteWiseRateCardDetail(
    @SerializedName("coach_type")
    val coachType: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("future_rate_card")
    val futureRateCard: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("rate_card_id")
    val rateCardId: String,
    @SerializedName("rate_card_type")
    val rateCardType: RateCardType,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("updated_by")
    val updatedBy: String,
    @SerializedName("is_delete")
    val isDelete: Boolean = false,
    @SerializedName("last_scheduled_date")
    val lastScheduleDate: String = "",
    var isExpandable: Boolean = false
)