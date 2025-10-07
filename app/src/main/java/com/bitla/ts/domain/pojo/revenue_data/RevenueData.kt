package com.bitla.ts.domain.pojo.revenue_data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class RevenueData {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("total_service")
    @Expose
    var totalService: Int? = null

    @SerializedName("total_revenue")
    @Expose
    var totalRevenue: Float? = null

    @SerializedName("number_of_pages")
    @Expose
    var numberOfPages: Int? = null

    @SerializedName("current_page")
    @Expose
    var currentPage: Int? = null

    @SerializedName("revenue_details")
    @Expose
    var revenueRouteDetails: ArrayList<RevenueRouteDetails>? = arrayListOf()
}