package com.bitla.ts.domain.pojo.view_reservation


import com.google.gson.annotations.SerializedName

data class RespHash(
    @SerializedName("city_id")
    var cityId: Int,
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("branch_name")
    var branchName: String? = null,
    @SerializedName("passenger_details")
    var passengerDetails: ArrayList<PassengerDetail>,
    @SerializedName("pickup_closed")
    var pickupClosed: Boolean,
    @SerializedName("pnr_group")
    var pnr_group: ArrayList<PnrGroup?>? = null,
    @SerializedName("stage_dep_time")
    var stageDepTime: String? = "",
    @SerializedName("boarded_passengers")
    var boardedPassengers: Int? = 0,
    @SerializedName("total_passengers")
    var totalPassengers: Int? = 0,

)