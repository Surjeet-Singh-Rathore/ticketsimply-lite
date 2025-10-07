package com.bitla.ts.domain.pojo.self_audit_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Rating {
    @SerializedName("rating_title")
    @Expose
    var ratingTitle: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("rating_options")
    @Expose
    var ratingOptions: List<RatingOption>? = null
}