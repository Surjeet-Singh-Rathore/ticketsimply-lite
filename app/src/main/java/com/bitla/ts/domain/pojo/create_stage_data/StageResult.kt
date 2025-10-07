package com.bitla.ts.domain.pojo.create_stage_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StageResult {

    @SerializedName("code")
    @Expose
    var code: Int ?= null

    @SerializedName("message")
    @Expose
    var message: String = ""


}