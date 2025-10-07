package com.bitla.ts.domain.pojo.self_audit_data

import com.bitla.ts.domain.pojo.announcement_model.response.BoardingPoint
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class BoardingData {
    @SerializedName("city_name")
    @Expose
    var cityName: String? = null

    @SerializedName("boarding_points")
    @Expose
    var boardingPoints: List<BoardingPoint>? = null
}
