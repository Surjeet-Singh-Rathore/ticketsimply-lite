package com.bitla.ts.domain.pojo.route_list

import com.bitla.ts.domain.pojo.route_manager.CitiesListData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RouteListResponse {

    @SerializedName("code")
    @Expose
    var code: String = ""

    @SerializedName("number_of_pages")
    @Expose
    var numberOfPages: String = ""

    @SerializedName("current_page")
    @Expose
    var currentPage: String = ""


    @SerializedName("total_routes")
    @Expose
    var totalRoutes: String = ""

    @SerializedName("active_count")
    @Expose
    var activeCount: Int = 0

    @SerializedName("inactive_count")
    @Expose
    var inactiveCount: Int = 0

    @SerializedName("propsed_count")
    @Expose
    var propsedCount: Int = 0


    @SerializedName("result")
    @Expose
    var result: ArrayList<RouteListData> = arrayListOf()
}