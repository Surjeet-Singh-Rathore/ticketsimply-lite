package com.bitla.ts.domain.pojo.my_bookings.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("filter")
    val filter: MutableList<Filter>,
    @SerializedName("sub_agent_details")
    val subAgentDetails: SubAgentDetails,
    @SerializedName("total_amount")
    val totalAmount: String
)