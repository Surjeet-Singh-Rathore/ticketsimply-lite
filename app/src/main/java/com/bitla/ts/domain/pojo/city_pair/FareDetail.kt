package com.bitla.ts.domain.pojo.city_pair


import com.google.gson.annotations.*

data class FareDetail (
    
    @SerializedName("fare")
    var fare: String = "",
    
    @SerializedName("seat_type")
    var seatType: String = "",
    
    @SerializedName("edited_fare")
    @Expose
    var editedFare: String? = null,
    
    @SerializedName("is_checked")
    @Expose
    var isChecked: Boolean = false
)