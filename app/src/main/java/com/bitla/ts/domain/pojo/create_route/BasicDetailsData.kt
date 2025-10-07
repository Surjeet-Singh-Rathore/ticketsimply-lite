package com.bitla.ts.domain.pojo.create_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BasicDetailsData {

    @SerializedName("origin_id")
    @Expose
    var originId: String = ""

    @SerializedName("origin_name")
    @Expose
    var originName: String = ""

    @SerializedName("destination_name")
    @Expose
    var destinationName: String = ""

    @SerializedName("dest_id")
    @Expose
    var destId: String = ""

    @SerializedName("service_no")
    @Expose
    var serviceNo: String = ""

    @SerializedName("service_name")
    @Expose
    var serviceName: String = ""

    @SerializedName("ota_name")
    @Expose
    var otaName: String = ""

    @SerializedName("coach_id")
    @Expose
    var coachId: String = ""

    @SerializedName("hub_id")
    @Expose
    var hubId: String = ""

    @SerializedName("hub_name")
    @Expose
    var hubName: String = ""

    @SerializedName("coach_name")
    @Expose
    var coachName: String = ""
}