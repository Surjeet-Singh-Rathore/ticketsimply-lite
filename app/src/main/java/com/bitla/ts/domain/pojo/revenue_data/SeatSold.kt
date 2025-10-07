package com.bitla.ts.domain.pojo.revenue_data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class SeatSold {
    @SerializedName("service_name")
    @Expose
    var serviceName: String? = null

    @SerializedName("fare")
    @Expose
    var fare: Float? = null

    @SerializedName("seats")
    @Expose
    var seats: Int? = null
}