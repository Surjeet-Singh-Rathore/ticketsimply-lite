package com.bitla.ts.presentation.view.merge_bus.pojo

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class CityData {
    @SerializedName("origin_id")
    @Expose
    var originId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("timeHH")
    @Expose
    var timeHH: String? = null

    @SerializedName("timeMM")
    @Expose
    var timeMM: String? = null
}