package com.bitla.restaurant_app.presentation.pojo.reports

import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
class Info :Parcelable{
    @SerializedName("depurture_time")
    @Expose
    var depurtureTime: String? = null

    @SerializedName("service_name")
    @Expose
    var serviceName: String? = null

    @SerializedName("origin")
    @Expose
    var origin: String? = null

    @SerializedName("destination")
    @Expose
    var destination: String? = null

    @SerializedName("meal_coupon")
    @Expose
    var mealCoupon: String? = null

    @SerializedName("meal_price")
    @Expose
    var totalFare: String? = null
}