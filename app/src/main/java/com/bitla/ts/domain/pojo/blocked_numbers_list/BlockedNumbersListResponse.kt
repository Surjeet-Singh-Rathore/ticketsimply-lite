package com.bitla.ts.domain.pojo.blocked_numbers_list



import com.google.gson.annotations.SerializedName

data class BlockedNumbersListResponse(
    @SerializedName("bocked_number_list")
    val blockedNumberList: ArrayList<BlockedNumber> = arrayListOf(),
    @SerializedName("code")
    val code: Int?
)