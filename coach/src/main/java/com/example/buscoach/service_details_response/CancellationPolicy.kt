package com.example.buscoach.service_details_response


import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CancellationPolicy: Serializable {

    @SerializedName("cancellation_policy_id")
    var cancellationPolicyId: Int? = null

    @SerializedName("percent")
    var percent: Int? = null

    @SerializedName("time_limit_from")
    var timeLimitFrom: String? = null

    @SerializedName("time_limit_to")
    var timeLimitTo: String? = null

}
