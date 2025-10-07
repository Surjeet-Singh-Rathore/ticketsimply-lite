package com.bitla.ts.domain.pojo.dashboard_model.response


import com.google.gson.annotations.SerializedName

data class PendingETickets(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("data")
    val data: MutableList<Data>
)