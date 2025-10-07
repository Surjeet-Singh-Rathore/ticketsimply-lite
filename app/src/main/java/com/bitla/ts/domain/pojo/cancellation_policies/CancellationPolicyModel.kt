package com.bitla.ts.domain.pojo.cancellation_policies

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CancellationPolicyModel {
    @SerializedName("cancellation_policy_id")
    @Expose
    var cancellationPolicyId: Int? = null

    @SerializedName("percent")
    @Expose
    var percent: Int? = null

    @SerializedName("time_limit_from")
    @Expose
    var timeLimitFrom: String? = null

    @SerializedName("time_limit_to")
    @Expose
    var timeLimitTo: String? = null

}
