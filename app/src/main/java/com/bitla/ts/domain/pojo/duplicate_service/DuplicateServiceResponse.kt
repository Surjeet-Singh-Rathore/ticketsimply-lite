package com.bitla.ts.domain.pojo.duplicate_service

import com.bitla.ts.domain.pojo.route_list.RouteListData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DuplicateServiceResponse {

    @SerializedName("code")
    @Expose
    val code: String = ""

    @SerializedName("number_of_pages")
    @Expose
    val numberOfPages: String = ""

    @SerializedName("current_page")
    @Expose
    val currentPage: String = ""

    @SerializedName("total_routes")
    @Expose
    val totalRoutes: String = ""

    @SerializedName("result")
    @Expose
    val result: ArrayList<RouteListData> = arrayListOf()
}