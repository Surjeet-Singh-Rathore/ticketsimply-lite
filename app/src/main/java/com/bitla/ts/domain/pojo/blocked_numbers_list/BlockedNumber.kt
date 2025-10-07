package com.bitla.ts.domain.pojo.blocked_numbers_list


import com.google.gson.annotations.SerializedName

data class BlockedNumber(
    @SerializedName("blocked_by")
    val blockedBy: String?,
    @SerializedName("blocked_number")
    val blockedNumber: String?,
    @SerializedName("blocked_on")
    val blockedOn: String?,
    @SerializedName("remarks")
    val remarks: String?
)