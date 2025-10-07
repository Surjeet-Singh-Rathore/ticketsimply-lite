package com.bitla.ts.domain.pojo.stage_for_city

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StageListResponse {

    @SerializedName("code")
    @Expose
    var code: Int = 0

    @SerializedName("result")
    @Expose
    var result: StateListResult?= null


}