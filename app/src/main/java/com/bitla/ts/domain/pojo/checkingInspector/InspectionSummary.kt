package com.bitla.ts.domain.pojo.view_reservation


import com.google.gson.annotations.SerializedName
import org.json.JSONArray

data class InspectionSummary(
    @SerializedName("boarded")
    var boarded: String = "",
    @SerializedName("Male")
    var Male: String="",
    @SerializedName("Female")
    var Female: String = ""
)