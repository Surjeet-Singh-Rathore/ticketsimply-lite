package com.bitla.ts.presentation.view.merge_bus.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import retrofit2.http.Query

class AddBpDpRequest {

    @SerializedName("api_key")
    @Expose
    private var apiKey: String=""


    @SerializedName("res_id")
    @Expose
    private var resId: String=""

    @SerializedName("boarding_time")
    @Expose
    private var boardingTime: String=""

    @SerializedName("dept_time")
    @Expose
    private var droppingTime: String=""

    @SerializedName("pnr_number")
    @Expose
    private var pnrNumber: String=""
}