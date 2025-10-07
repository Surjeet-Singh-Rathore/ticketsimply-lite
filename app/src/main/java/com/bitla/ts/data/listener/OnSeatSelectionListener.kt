package com.bitla.ts.data.listener

import android.view.View
import com.bitla.ts.domain.pojo.service_details_response.SeatDetail

interface OnSeatSelectionListener {
    fun onSeatSelection(
        selectedSeatDetails: ArrayList<SeatDetail>,
        finalSeatNumber: ArrayList<String?>,
        totalSum: Double,
        isAllSeatSelected: Boolean,
        isSeatLongPress: Boolean? = null,
    )

    fun unSelectAllSeats()
    fun unblockSeat(seatNumber: String, selectionType: String, fromDate:String?, toDate:String?, remarks: String?)
    fun editSeatFare(seatNumber:String,newFare:String)
    fun bookExtraSeats(isChecked : Boolean?=null, isSeatSelected:Boolean?=null)
    fun moveExtraSeat(isChecked : Boolean)
    fun releaseTicket(ticketNumber: String, releaseTicket: String)
    fun callPassenger(ticketNumber: String, contactNumber: String)
    fun checkBoardedStatus(
        status: Boolean,
        passengerName: String,
        pnrNum: String,
        seatNumber: String,
        view: View
    )

    abstract fun selectedSeatCount(selectedSeats : ArrayList<SeatDetail>)
}


