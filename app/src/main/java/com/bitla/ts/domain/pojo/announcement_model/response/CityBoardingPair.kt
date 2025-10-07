package com.bitla.ts.domain.pojo.announcement_model.response

import com.google.gson.annotations.SerializedName

data class CityBoardingPair(
    @SerializedName("boarding_point")
    val boardingPoint: List<BoardingPoint>,
    @SerializedName("city_id")
    val cityId: Int,
    @SerializedName("city_name")
    val cityName: String
)