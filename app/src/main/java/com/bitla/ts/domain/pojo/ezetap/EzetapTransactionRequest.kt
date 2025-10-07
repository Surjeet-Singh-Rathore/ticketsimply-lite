package com.bitla.ts.domain.pojo.ezetap

import com.bitla.ts.domain.pojo.employees_details.response.Employee
import com.bitla.ts.domain.pojo.pinelabs.DataResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class EzetapTransactionRequest(
    var apiKey: String="",
    var ticketNumber: String = "",
    var merchantName: String?= "",
    var userName: String?= null,
    var currencyCode: String?= null,
    var appMode: String?= null,
    var payMode: String?= null,
    var amount:String?=null


) : Serializable