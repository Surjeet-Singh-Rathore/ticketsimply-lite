package com.bitla.ts.domain.pojo.route_list

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RouteListData {

    @SerializedName("id")
    @Expose
    var id: String = ""

    @SerializedName("number")
    @Expose
    var number: String = ""

    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("origin_id")
    @Expose
    var originId: String = ""

    @SerializedName("reservation_id")
    @Expose
    var reservationId: String = ""

    @SerializedName("destination_id")
    @Expose
    var destinationId: String = ""

    @SerializedName("departure_time")
    @Expose
    var departureTime: String = ""

    @SerializedName("duration")
    @Expose
    var duration: String = ""

    @SerializedName("arrival_time")
    @Expose
    var arrivalTime: String = ""

    @SerializedName("origin_name")
    @Expose
    var originName: String = ""

    @SerializedName("stages_count")
    @Expose
    var stagesCount: String = ""

    @SerializedName("destination_name")
    @Expose
    var destinationName: String = ""

    @SerializedName("bus_type")
    @Expose
    var busType: String = ""

    @SerializedName("to_date")
    @Expose
    var toDate: String = ""

    @SerializedName("from_date")
    @Expose
    var fromDate: String = ""

    @SerializedName("is_modify_enable")
    @Expose
    var isModifyEnable: String = ""

    @SerializedName("status")
    @Expose
    var status: String = ""
}