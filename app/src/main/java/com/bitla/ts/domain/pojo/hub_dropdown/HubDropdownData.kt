package com.bitla.ts.domain.pojo.hub_dropdown

import com.bitla.ts.domain.pojo.route_list.RouteListData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HubDropdownData {

    @SerializedName("id")
    @Expose
    var id: String = ""

    @SerializedName("city_id")
    @Expose
    var cityId: String = ""

    @SerializedName("label")
    @Expose
    var label: String = ""
}