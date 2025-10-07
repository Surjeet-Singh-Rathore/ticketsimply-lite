package com.bitla.mba.morningstartravels.mst.pojo.service_details

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CancellationPolicy {
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