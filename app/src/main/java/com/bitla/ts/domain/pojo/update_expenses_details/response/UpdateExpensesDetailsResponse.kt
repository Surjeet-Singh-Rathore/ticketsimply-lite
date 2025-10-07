package com.bitla.ts.domain.pojo.update_expenses_details.response


import com.google.gson.annotations.SerializedName

data class UpdateExpensesDetailsResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String?
)