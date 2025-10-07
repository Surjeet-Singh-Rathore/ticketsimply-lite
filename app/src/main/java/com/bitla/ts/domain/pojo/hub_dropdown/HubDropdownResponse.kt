package com.bitla.ts.domain.pojo.hub_dropdown

import com.bitla.ts.domain.pojo.route_list.RouteListData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HubDropdownResponse {

    @SerializedName("code")
    @Expose
    var code: Int ?= null


    @SerializedName("result")
    @Expose
    var result: HubResultData ?= null

}