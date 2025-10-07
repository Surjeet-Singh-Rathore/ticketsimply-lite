package com.bitla.ts.domain.pojo.stage_for_city

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StateListResult {


    @SerializedName("stage_details")
    @Expose
    var stageDetails: ArrayList<StageListData> = arrayListOf()




}