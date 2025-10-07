package com.bitla.ts.domain.pojo.multiple_services_manage_fare.request

import com.google.gson.annotations.SerializedName

data class MultipleServicesChannelType(
    @SerializedName("branch")
    val branch: Boolean,

    @SerializedName("ota")
    val ota: Boolean,

    @SerializedName("ebooking")
    val ebooking: Boolean,

    @SerializedName("online")
    val online: Boolean
)