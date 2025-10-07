package com.example.buscoach.listeners

import com.example.buscoach.service_details_response.PassengerDetails
import com.example.buscoach.service_details_response.SeatDetail
import com.example.buscoach.service_details_response.ServiceDetailsModel

interface SeatSelectListener {
    /*fun onSeatSelection(pnr: String, seatDetail: SeatDetail, commaSeparatedSeatNumber : String, fragmentPosition: Int)
    fun onSeatUnSelection(pnr: String, seatDetail: SeatDetail, commaSeparatedSeatNumber : String, fragmentPosition: Int)*/
    fun onServiceDetailsClicked(fragmentPosition: Int, serviceDetailsModel: ServiceDetailsModel?)

    fun onSeatClick(pnr: String, seatDetail: SeatDetail, fragmentPosition: Int)

    fun selectPreviouslySelectedSeats(pnr: String, fragmentPosition: Int)

    fun onMultiHopSeatClick(parentPosition: Int, childPosition: Int,seatDetail: SeatDetail)
    fun onMultiHopSeatClickNew(parentPosition: Int, childPosition: Int,seatDetail: SeatDetail)

    fun onToolTipForServiceNameClick(fragmentPosition: Int)

    fun onDoneButtonClick()


    fun onCancelButtonClick()
}