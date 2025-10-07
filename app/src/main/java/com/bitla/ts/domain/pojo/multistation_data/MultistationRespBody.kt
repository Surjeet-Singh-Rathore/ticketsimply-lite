package com.bitla.ts.domain.pojo.multistation_data

import com.example.buscoach.multistation_data.Result

data class MultistationRespBody(
    val passenger_details: ArrayList<PassengerDetail> = arrayListOf(),
    val code: Int ?= null,
    val message:String? = null,
    val result: Result
)
