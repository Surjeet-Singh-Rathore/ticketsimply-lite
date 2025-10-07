package com.bitla.ts.domain.pojo.service_routes_list.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("arr_time")
    val arrTime: String,
    @SerializedName("bus_type")
    val busType: String,
    @SerializedName("dep_time")
    val depTime: String,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("destination_id")
    val destinationId: Int,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_service_blocked")
    val is_service_blocked: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("origin_id")
    val originId: Int,
    @SerializedName("reservation_id")
    val reservation_id: Long,
    @SerializedName("route_id")
    val routeId: Int
)