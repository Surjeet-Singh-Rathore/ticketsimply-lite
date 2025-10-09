package com.bitla.restaurant_app.presentation.pojo.mealCoupon

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RestaurantList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("restaurant_name")
    @Expose
    var restaurantName: String? = null
}