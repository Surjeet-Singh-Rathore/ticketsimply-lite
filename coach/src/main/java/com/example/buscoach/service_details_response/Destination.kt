package com.example.buscoach.service_details_response


import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Destination: Serializable {

    @SerializedName("id")
    var id: String? = null

    @SerializedName("name")
    var name: String? = null

}
