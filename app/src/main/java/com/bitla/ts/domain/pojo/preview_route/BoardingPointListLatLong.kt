package com.bitla.ts.domain.pojo.preview_route

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BoardingPointListLatLong {
    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("lat_long")
    @Expose
    var latLong: LatLng = LatLng(0.0000,0.0000)
}