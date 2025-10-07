package com.bitla.ts.domain.pojo.update_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ViaCitiesData {

    @SerializedName("id")
    @Expose
    var id: String = ""

    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("is_origin")
    @Expose
    var isOrigin: Boolean = false

    @SerializedName("is_destination")
    @Expose
    var isDestination: Boolean = false

    @SerializedName("day")
    @Expose
    var day: String = ""

    @SerializedName("time")
    @Expose
    var time: String = ""

    @SerializedName("hh")
    @Expose
    var hh: String = ""

    @SerializedName("mm")
    @Expose
    var mm: String = ""

    @SerializedName("seat_type")
    @Expose
    var seatTypes: String = ""

    @SerializedName("stage_list")
    @Expose
    var stageList: ArrayList<StageDetailsData> = arrayListOf()


}