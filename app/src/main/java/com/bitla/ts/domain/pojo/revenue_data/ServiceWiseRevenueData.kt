package com.bitla.ts.domain.pojo.revenue_data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class ServiceWiseRevenueData {

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("route_id")
    @Expose
    var routeId: String? = null

    @SerializedName("service_summary", alternate = ["summary"])
    @Expose
    var serviceSummary: ServiceSummary? = null

    @SerializedName("agent_summary")
    @Expose
    var agentSummary: AgentSummary? = null

    @SerializedName("branch_summary")
    @Expose
    var branchSummary: BranchSummary? = null

    @SerializedName("channel_summary")
    @Expose
    var channelSummary: ChannelSummary? = null

    @SerializedName("seat_sold")
    @Expose
    var seatSold: ArrayList<SeatSold>? = arrayListOf()

}