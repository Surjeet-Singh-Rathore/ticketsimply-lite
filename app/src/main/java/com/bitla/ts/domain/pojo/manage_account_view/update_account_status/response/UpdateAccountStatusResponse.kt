package com.bitla.ts.domain.pojo.manage_account_view.update_account_status.response


import com.google.gson.annotations.SerializedName

data class UpdateAccountStatusResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)