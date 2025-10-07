package com.bitla.ts.domain.pojo.update_route

import com.bitla.ts.domain.pojo.city_pair.FareDetail
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MultiCityBookingPairData {

    @SerializedName("origin_id")
    @Expose
    var originId: String = ""

    @SerializedName("destination_id")
    @Expose
    var destinationId: String = ""

    @SerializedName("fare_details")
    @Expose
    var fareDetails: ArrayList<FareDetail>  = arrayListOf()


}