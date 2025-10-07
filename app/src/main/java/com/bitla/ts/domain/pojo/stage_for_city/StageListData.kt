package com.bitla.ts.domain.pojo.stage_for_city

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StageListData {

    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("id")
    @Expose
    var id: String = ""

    @SerializedName("contact_number")
    @Expose
    var contactNumber: String = ""

    @SerializedName("person")
    @Expose
    var person: String = ""

    @SerializedName("address_line1")
    @Expose
    var addressLine1: String = ""

    @SerializedName("address_line2")
    @Expose
    var addressLine2: String = ""

    @SerializedName("landmark")
    @Expose
    var landmark: String = ""

    @SerializedName("location_url")
    @Expose
    var locationUrl: String = ""

    @SerializedName("latitude")
    @Expose
    var latitude: String = ""

    @SerializedName("longitude")
    @Expose
    var longitude: String = ""

}