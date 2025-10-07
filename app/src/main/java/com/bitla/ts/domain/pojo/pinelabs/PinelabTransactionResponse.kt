package com.bitla.ts.domain.pojo.pinelabs

import com.bitla.ts.domain.pojo.employees_details.response.Employee
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class PinelabTransactionResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("responseCode")
    var responseCode: Int?= null,
    @SerializedName("data")
    var data: DataResponse?= null
) : Serializable