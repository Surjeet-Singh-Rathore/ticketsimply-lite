package com.bitla.ts.domain.pojo.dashboard_model.response


import com.google.gson.annotations.SerializedName

data class PendingApiTickets(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("data")
    val data: MutableList<Data>
)