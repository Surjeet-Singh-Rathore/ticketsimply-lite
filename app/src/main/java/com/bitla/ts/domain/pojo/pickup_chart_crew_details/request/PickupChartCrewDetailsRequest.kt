package com.bitla.ts.domain.pojo.pickup_chart_crew_details.request


import com.google.gson.annotations.SerializedName

data class PickupChartCrewDetailsRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val reqBody: ReqBody

)