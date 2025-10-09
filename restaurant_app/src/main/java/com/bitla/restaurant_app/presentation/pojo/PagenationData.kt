package com.bitla.restaurant_app.presentation.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PagenationData {

    @SerializedName("position")
    @Expose
    var position : Int = 0

    @SerializedName("is_selected")
    @Expose
    var isSelected: Boolean = false


}