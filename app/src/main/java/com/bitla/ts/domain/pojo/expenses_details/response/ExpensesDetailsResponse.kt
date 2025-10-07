package com.bitla.ts.domain.pojo.expenses_details.response


import com.google.gson.annotations.SerializedName

data class ExpensesDetailsResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: Result,
    @SerializedName("message")
    val message: String?
)