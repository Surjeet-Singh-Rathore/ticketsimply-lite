package com.bitla.ts.domain.pojo.recommended_seats.response


import com.bitla.ts.presentation.view.merge_bus.pojo.CityData
import com.bitla.ts.presentation.view.merge_bus.pojo.PointData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RecommendedSeatsResponse(
    @SerializedName("available_seats")
    val availableSeats: List<String?>?,
    @SerializedName("code")
    val code: Int?,
    @SerializedName("seats")
    val seats: List<Seat?>?,
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("source_info")
    @Expose
    var sourceInfo: CityData? = null,

    @SerializedName("destination_info")
    @Expose
    var destinationInfo: CityData? = null,

    @SerializedName("boarding_point_info")
    @Expose
    var boardingPointInfo: PointData? = null,

    @SerializedName("drop_off_details")
    @Expose
    var dropOffDetails: PointData? = null,

    @SerializedName("is_different_origin")
    @Expose
    var isDifferentOrigin: Boolean? = null,

    @SerializedName("is_different_destination")
    @Expose
    var isDifferentDestination: Boolean? = null,

    @SerializedName("is_different_bp")
    @Expose
    var isDifferentBp: Boolean? = null,

    @SerializedName("is_different_dp")
    @Expose
    var isDifferentDp: Boolean? = null,
)