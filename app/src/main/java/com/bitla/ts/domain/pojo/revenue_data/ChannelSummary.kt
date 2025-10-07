package com.bitla.ts.domain.pojo.revenue_data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class ChannelSummary {
    @SerializedName("total_seats")
    @Expose
    var totalSeats: Int? = null

    @SerializedName("total_revenue")
    @Expose
    var totalRevenue: Float? = null

    @SerializedName("channel_wise_revenue")
    @Expose
    var channelWiseRevenue: ArrayList<AgentWiseRevenue>? = arrayListOf()
}