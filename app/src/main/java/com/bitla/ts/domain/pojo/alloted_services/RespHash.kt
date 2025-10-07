package com.bitla.ts.domain.pojo.alloted_services


import com.google.gson.annotations.SerializedName

data class RespHash(
    @SerializedName("hub_name")
    var hubName: String,
    @SerializedName("services")
    var services: ArrayList<Service>,
    @SerializedName("view_summary")
    var viewSummary: ViewSummary
)