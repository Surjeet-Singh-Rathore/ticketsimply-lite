package com.bitla.ts.presentation.view.merge_bus.pojo

import com.google.api.SourceInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class BpDpValidationData {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("source_info")
    @Expose
    var sourceInfo: CityData? = null

    @SerializedName("destination_info")
    @Expose
    var destinationInfo: CityData? = null

    @SerializedName("boarding_point_info")
    @Expose
    var boardingPointInfo: PointData? = null

    @SerializedName("drop_off_details")
    @Expose
    var dropOffDetails: PointData? = null

    @SerializedName("is_different_origin")
    @Expose
    var isDifferentOrigin: Boolean? = null

    @SerializedName("is_different_destination")
    @Expose
    var isDifferentDestination: Boolean? = null

    @SerializedName("is_different_bp")
    @Expose
    var isDifferentBp: Boolean? = null

    @SerializedName("is_different_dp")
    @Expose
    var isDifferentDp: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
}