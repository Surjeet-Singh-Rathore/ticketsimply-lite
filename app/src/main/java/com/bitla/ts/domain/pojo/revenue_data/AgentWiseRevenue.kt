package com.bitla.ts.domain.pojo.revenue_data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class AgentWiseRevenue {

    @SerializedName("agent_name", alternate = ["branch_name","channel_name"])
    @Expose
    var agentName: String? = null

    @SerializedName("booked_seats")
    @Expose
    var bookedSeats: Int? = null

    @SerializedName("fare")
    @Expose
    var fare: Float? = null

    @SerializedName("commision")
    @Expose
    var commision: Float? = null

    @SerializedName("cancellation")
    @Expose
    var cancellation: Float? = null
}