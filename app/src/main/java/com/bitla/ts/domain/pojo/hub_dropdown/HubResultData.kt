package com.bitla.ts.domain.pojo.hub_dropdown

import com.bitla.ts.domain.pojo.route_list.RouteListData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HubResultData {

    @SerializedName("hub_list")
    @Expose
    var hubList: ArrayList<HubDropdownData> = arrayListOf()
}