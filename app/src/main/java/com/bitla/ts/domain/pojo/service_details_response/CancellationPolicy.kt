package com.bitla.ts.domain.pojo.service_details_response


import com.google.gson.annotations.SerializedName


class CancellationPolicy {

    @SerializedName("cancellation_policy_id")
    var cancellationPolicyId: Int? = null

    @SerializedName("percent")
    var percent: Int? = null

    @SerializedName("time_limit_from")
    var timeLimitFrom: String? = null

    @SerializedName("time_limit_to")
    var timeLimitTo: String? = null

}
