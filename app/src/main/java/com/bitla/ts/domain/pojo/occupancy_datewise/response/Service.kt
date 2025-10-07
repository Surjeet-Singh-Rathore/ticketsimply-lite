package com.bitla.ts.domain.pojo.occupancy_datewise.response


import com.google.gson.annotations.SerializedName

data class Service(
    @SerializedName("coach_type")
    val coachType: String?,
    @SerializedName("dept_time")
    val deptTime: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("occupancy")
    val occupancy: List<Occupancy?>?,
    @SerializedName("total_seats")
    val totalSeats: Int?,
    @SerializedName("origin")
    val origin: String?,
    @SerializedName("origin_id")
    val originId: String?,
    @SerializedName("destination")
    val destination: String?,
    @SerializedName("destination_id")
    val destinationId: String?,
    @SerializedName("bus_type")
    val busType: String?

)