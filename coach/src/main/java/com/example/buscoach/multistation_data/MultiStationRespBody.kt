package com.example.buscoach.multistation_data

import com.example.buscoach.service_details_response.PassengerDetails

data class MultiStationRespBody(
    val body: Body,
    val code: Int ?= null,
    val message:String? = null,
    val result: Result
)
data class Body(
    val selected_seat_number: String,
    val multi_hop_seat_detail: MutableList<MultiHopSeatDetail> = mutableListOf()
)