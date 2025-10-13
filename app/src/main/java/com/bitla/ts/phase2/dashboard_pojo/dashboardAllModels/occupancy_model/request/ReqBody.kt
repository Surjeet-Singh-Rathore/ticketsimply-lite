package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.occupancy_model.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("destination_id")
    val destination: Int,
    @SerializedName("from")
    var from: String,
    @SerializedName("origin_id")
    val originId: Int,
    @SerializedName("service_id")
    val serviceId: String,
    @SerializedName("sort_by")
    val sortBy: String,
    @SerializedName("to")
    var to: String,
    @SerializedName("api_type")
    val apiType: Int,
    @SerializedName("is_3days_data")
    var is3daysData: Boolean? = false

)