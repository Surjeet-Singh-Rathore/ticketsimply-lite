package com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.response


import com.google.gson.annotations.SerializedName

data class Service(
    @SerializedName("destination")
    val destination: String,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("passenger_details")
    var passengerDetails: MutableList<PassengerDetail>,
    @SerializedName("service_no")
    val serviceNo: String
)