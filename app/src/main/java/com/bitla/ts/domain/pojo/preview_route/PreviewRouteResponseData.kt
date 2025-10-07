package com.bitla.ts.domain.pojo.preview_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PreviewRouteResponseData {

    @SerializedName("origin")
    @Expose
    val origin: String = ""

    @SerializedName("origin_lat")
    @Expose
    val originLat: String = ""

    @SerializedName("origin_long")
    @Expose
    val originLong: String = ""

    @SerializedName("destination")
    @Expose
    val destination: String = ""

    @SerializedName("dest_lat")
    @Expose
    val destLat: String = ""

    @SerializedName("dest_long")
    @Expose
    val destLong: String = ""

    @SerializedName("boarding_list")
    @Expose
    val boardingList: ArrayList<StageData> = arrayListOf()

    @SerializedName("dropping_list")
    @Expose
    val droppingList: ArrayList<StageData> = arrayListOf()
}