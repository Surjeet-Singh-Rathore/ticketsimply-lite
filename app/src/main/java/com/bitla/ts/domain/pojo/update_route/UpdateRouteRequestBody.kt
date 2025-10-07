package com.bitla.ts.domain.pojo.update_route

import com.bitla.ts.domain.pojo.stage_for_city.StageListData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateRouteRequestBody {

    @SerializedName("via_cities")
    @Expose
    var viaCities: ArrayList<ViaCitiesData> = arrayListOf()

    @SerializedName("multi_city_booking_pair")
    @Expose
    var multiCityBookingPair: ArrayList<MultiCityBookingPairData> = arrayListOf()

    @SerializedName("commission")
    @Expose
    var commission: ArrayList<CommissionData> = arrayListOf()

    @SerializedName("boarding_config")
    @Expose
    var boardingConfig: ArrayList<BoardingConfigData> = arrayListOf()

    @SerializedName("dropping_config")
    @Expose
    var droppingConfig: ArrayList<DroppingConfigData> = arrayListOf()

    @SerializedName("additional_info")
    @Expose
    var additionalInfo: ArrayList<AdditionalInfoData> = arrayListOf()


}