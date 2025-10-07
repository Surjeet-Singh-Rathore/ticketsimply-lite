package com.bitla.ts.domain.pojo.update_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BoardingConfigData {

    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("id")
    @Expose
    var id: String = ""

    @SerializedName("stage_details")
    @Expose
    var stageDetails: ArrayList<StageDetailsData> = arrayListOf()
}