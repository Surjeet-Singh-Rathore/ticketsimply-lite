package com.bitla.ts.presentation.view.merge_bus.pojo

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class ShiftToServicesListResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("exact_route_services")
    @Expose
    var exactRouteServices: ArrayList<ExactRouteService>? = ArrayList()

    @SerializedName("message")
    @Expose
    var message: String? = null

}