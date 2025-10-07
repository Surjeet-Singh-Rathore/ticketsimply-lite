package com.bitla.ts.domain.pojo.route_manager

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CitiesListData {

    @SerializedName("id")
    @Expose
    var id: String = ""

    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("number")
    @Expose
    var number: String = ""

    @SerializedName("id_name")
    @Expose
    var idName: String = ""

    @SerializedName("city_id")
    @Expose
    var cityId: String = ""

    @SerializedName("is_selected")
    @Expose
    var isSelected: Boolean = false

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

    @SerializedName("count")
    @Expose
    var count: String = ""

    @SerializedName("is_checked")
    @Expose
    var isChecked: Boolean = true


}