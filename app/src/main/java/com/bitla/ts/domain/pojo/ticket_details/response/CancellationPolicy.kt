package com.bitla.ts.domain.pojo.ticket_details.response


import com.google.gson.annotations.SerializedName

data class CancellationPolicy(
    @SerializedName("cancellation_policy_id")
    val cancellationPolicyId: Int,
    @SerializedName("percent")
    val percent: Int,
    @SerializedName("time_limit_from")
    val timeLimitFrom: String,
    @SerializedName("time_limit_to")
    val timeLimitTo: String
)