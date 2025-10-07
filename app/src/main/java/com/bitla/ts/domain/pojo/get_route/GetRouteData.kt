package com.bitla.ts.domain.pojo.get_route

import com.bitla.ts.domain.pojo.create_route.BasicDetailsData
import com.bitla.ts.domain.pojo.create_route.OtherData
import com.bitla.ts.domain.pojo.create_route.ScheduleData
import com.bitla.ts.domain.pojo.update_route.AdditionalInfoData
import com.bitla.ts.domain.pojo.update_route.BoardingConfigData
import com.bitla.ts.domain.pojo.update_route.CommissionData
import com.bitla.ts.domain.pojo.update_route.DroppingConfigData
import com.bitla.ts.domain.pojo.update_route.MultiCityBookingPairData
import com.bitla.ts.domain.pojo.update_route.ViaCitiesData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetRouteData {

    @SerializedName("basic_details")
    @Expose
    var basicDetails: BasicDetailsData ?= null

    @SerializedName("schedule")
    @Expose
    var schedule: ScheduleData ?= null

    @SerializedName("other")
    @Expose
    var other: OtherData ?= null

    @SerializedName("seat_types")
    @Expose
    var seatTypes: String = ""

    @SerializedName("via_cities")
    @Expose
    var viaCities: ArrayList<ViaCitiesData> = arrayListOf()

    @SerializedName("multi_city_booking_pair")
    @Expose
    var multiCityBookingPair: ArrayList<MultiCityBookingPairData> = arrayListOf()

    @SerializedName("commission")
    @Expose
    var commision: CommissionData ?= null

    @SerializedName("boarding_config")
    @Expose
    var boardingConfig: ArrayList<BoardingConfigData> = arrayListOf()

    @SerializedName("dropping_config")
    @Expose
    var droppingConfig: ArrayList<DroppingConfigData> = arrayListOf()

    @SerializedName("additional_info")
    @Expose
    var additionalInfo: AdditionalInfoData ?= null

    @SerializedName("code")
    @Expose
    var code: String = ""
}