package com.bitla.ts.domain.pojo.create_stage_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateStageResponse {

    @SerializedName("code")
    @Expose
    var code: Int ?= null

    @SerializedName("result")
    @Expose
    var result: StageResult ?= null

    @SerializedName("message")
    @Expose
    var message: String = ""


}