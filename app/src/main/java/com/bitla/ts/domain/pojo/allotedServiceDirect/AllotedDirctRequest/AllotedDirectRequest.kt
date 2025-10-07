package com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctRequest

import com.google.gson.annotations.SerializedName
import retrofit2.http.Query

data class AllotedDirectRequest (
    @SerializedName("is_group_by_hubs")
    val is_group_by_hubs: Boolean,
    @SerializedName("hub_id")
    val hub_id: Int?,
    @SerializedName("api_key")
    val api_key: String,
    @SerializedName("travel_date")
    val travel_date: String,
    @SerializedName("page")
    val page: Int?,
    @SerializedName("per_page")
    val per_page: Int?,
    @SerializedName("view_mode")
    val view_mode: String = "",
    @SerializedName("pagination")
    val pagination: Boolean,
    @SerializedName("origin")
    val origin: String? = "",
    @SerializedName("destination")
    val destination: String? = "",
    @SerializedName("locale")
    val locale: String?,
    @SerializedName("is_checking_inspector")
    val isCheckingInspector: Boolean?= false,
    @SerializedName("service_filter")
    val serviceFilter: String?  ,
    @SerializedName("res_id")
    val res_id: String? = ""
    )