package com.bitla.ts.domain.pojo.service_summary

import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
class SummaryData : Parcelable{
    @SerializedName("label")
    @Expose
    var label: String? = null

    @SerializedName("amount")
    @Expose
    var amount: Double? = null

    @SerializedName("count")
    @Expose
    var count: Int? = null

    @SerializedName("details")
    @Expose
    var details: ArrayList<SummaryData?>? = null

}