package com.bitla.ts.domain.pojo.reports

import android.os.Parcelable
import com.bitla.ts.domain.pojo.reports.Info

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