package com.bitla.ts.domain.pojo.notifications.response

import com.google.gson.annotations.SerializedName

data class NotificationFilter(
    @SerializedName("label")
    val navigationFilterTitle: String,
    @SerializedName("mage")
    val navigationFilterImage: Int,
)