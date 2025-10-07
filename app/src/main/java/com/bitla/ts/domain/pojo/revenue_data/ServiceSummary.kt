package com.bitla.ts.domain.pojo.revenue_data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class ServiceSummary {
    @SerializedName("seats")
    @Expose
    var seats: Int? = null

    @SerializedName("commission")
    @Expose
    var commission: Float? = null


    @SerializedName("fare")
    @Expose
    var fare: String? = null

    @SerializedName("expenses")
    @Expose
    var expenses: Float? = null

    @SerializedName("cancellation_charges")
    @Expose
    var cancellationCharges: Float? = null

    @SerializedName("net_revenue")
    @Expose
    var netRevenue: Float? = null


    @SerializedName("discount")
    @Expose
    var discount: Float? = null

    @SerializedName("boarding_fee")
    @Expose
    var boardingFee: Float? = null

    @SerializedName("meal_price")
    @Expose
    var mealPrice: Float? = null

}