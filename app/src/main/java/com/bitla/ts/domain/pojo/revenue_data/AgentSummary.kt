package com.bitla.ts.domain.pojo.revenue_data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class AgentSummary {
    @SerializedName("total_seats")
    @Expose
    var totalSeats: Int? = null

    @SerializedName("total_revenue")
    @Expose
    var totalRevenue: Float? = null

    @SerializedName("agent_wise_revenue")
    @Expose
    var agentWiseRevenue: ArrayList<AgentWiseRevenue>? = arrayListOf()
}