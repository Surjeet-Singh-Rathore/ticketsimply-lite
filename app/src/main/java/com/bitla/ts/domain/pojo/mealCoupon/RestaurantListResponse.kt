package com.bitla.ts.domain.pojo.mealCoupon

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RestaurantListResponse {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("restaurant_list")
    @Expose
    var restaurantList: ArrayList<RestaurantList>? = null
}