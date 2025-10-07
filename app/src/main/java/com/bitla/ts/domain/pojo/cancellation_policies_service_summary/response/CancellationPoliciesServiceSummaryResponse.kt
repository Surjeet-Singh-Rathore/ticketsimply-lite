package com.bitla.ts.domain.pojo.cancellation_policies_service_summary.response


import com.google.gson.annotations.SerializedName

data class CancellationPoliciesServiceSummaryResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: List<Result>,
    @SerializedName("message")
    val message: String? = ""
)