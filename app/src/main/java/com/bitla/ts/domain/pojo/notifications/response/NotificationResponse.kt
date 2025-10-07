package com.bitla.ts.domain.pojo.notifications.response

import com.google.gson.annotations.SerializedName

data class NotificationResponse(

    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String?,
    @SerializedName("result")
    val result: Result?,
)