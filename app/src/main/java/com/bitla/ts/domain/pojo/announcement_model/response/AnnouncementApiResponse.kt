package com.bitla.ts.domain.pojo.announcement_model.response

import com.google.gson.annotations.SerializedName

data class AnnouncementApiResponse(
    @SerializedName("announcement _languages")
    val announcementLanguages: List<String>,
    @SerializedName("message")
    val message: List<String>,
    @SerializedName("city_boarding_pair")
    val cityBoardingPair: List<CityBoardingPair>,
    @SerializedName("code")
    val code: Int,
    @SerializedName("reason_type")
    val reasonType: ReasonType,
    @SerializedName("result")
    val result: com.bitla.ts.domain.pojo.announcement_model.response.Result
)