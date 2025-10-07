package com.bitla.ts.domain.pojo.revenue_data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class ChannelWiseRevenue {
    @SerializedName("channel_name")
    @Expose
    var channelName: String? = null

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