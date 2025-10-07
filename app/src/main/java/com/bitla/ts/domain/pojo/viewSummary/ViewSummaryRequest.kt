package com.bitla.ts.domain.pojo.viewSummary

import com.google.gson.annotations.SerializedName
import retrofit2.http.Query


class ViewSummaryRequest(

    @SerializedName("is_group_by_hubs")
    val is_group_by_hubs: Boolean,
    @SerializedName("hub_id")
    val hub_id: Int?,
    @SerializedName("api_key")
    val api_key: String,
    @SerializedName("travel_date")
    val travel_date: String,
    @SerializedName("is_from_middle_tier")
    val is_from_middle_tier: Boolean,
    @SerializedName("view_summary")
    val view_summary: Boolean,
    @SerializedName("origin")
    val origin: Int?,
    @SerializedName("destination")
    val destination: Int?,
    @SerializedName("locale")
    val locale: String?,
)