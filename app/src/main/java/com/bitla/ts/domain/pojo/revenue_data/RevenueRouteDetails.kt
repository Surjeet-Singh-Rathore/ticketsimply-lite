package com.bitla.ts.domain.pojo.revenue_data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class RevenueRouteDetails {

    @SerializedName("id")
    @Expose
    var routeId: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("total_booked_seats")
    @Expose
    var totalBookedSeats: Int? = null

    @SerializedName("revenue")
    @Expose
    var revenue: Float? = null

    @SerializedName("gross")
    @Expose
    var gross: Float? = null

    @SerializedName("expenses")
    @Expose
    var expenses: Float? = null

    @SerializedName("deduction")
    @Expose
    var deduction: Int? = null

    @SerializedName("commission")
    @Expose
    var commission: Float? = null
}