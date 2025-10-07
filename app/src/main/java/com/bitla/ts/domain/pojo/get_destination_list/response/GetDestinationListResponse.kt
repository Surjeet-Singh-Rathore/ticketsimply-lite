package com.bitla.ts.domain.pojo.get_destination_list.response


import com.google.gson.annotations.SerializedName

data class GetDestinationListResponse(
    @SerializedName("boarding_details")
    val boardingDetails: BoardingDetails?,
    @SerializedName("city_list")
    val cityList: List<City>?
)