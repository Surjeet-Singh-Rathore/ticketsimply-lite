package com.bitla.restaurant_app.presentation.pojo.reports

import android.os.Parcelable

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
class ReportData:Parcelable {

    @SerializedName("travel_date")
    @Expose
    var travelDate: String? = null

    @SerializedName("info")
    @Expose
    var info: ArrayList<Info>? = null
}