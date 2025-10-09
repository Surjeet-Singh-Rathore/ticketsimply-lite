package com.bitla.restaurant_app.presentation.pojo.reports

import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
class Result : Parcelable {
    @SerializedName("coupon_redeemed")
    @Expose
    var couponRedeemed: String? = null

    @SerializedName("net_revenue")
    @Expose
    var netRevenue: String? = null

    @SerializedName("total_services")
    @Expose
    var totalServices: Int? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<ReportData>? = arrayListOf()
}