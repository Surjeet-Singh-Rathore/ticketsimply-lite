package com.bitla.ts.domain.pojo.book_with_extra_seat.response

import com.google.gson.annotations.SerializedName


data class Result(
    @SerializedName("bluetooth_print_template")
    val bluetoothPrintTemplate: String,
    @SerializedName("boarding_details")
    val boardingDetails: BoardingDetails,
    @SerializedName("booked_by")
    val bookedBy: String,
    @SerializedName("bus_type")
    val busType: String,
    @SerializedName("custom_print_ticket")
    val customPrintTicket: String,
    @SerializedName("dep_time")
    val depTime: String,
    @SerializedName("dest_id")
    val destId: Int,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("extra_seat_response")
    val extraSeatResponse: ExtraSeatResponse,
    @SerializedName("insurance_trans_details")
    val insuranceTransDetails: InsuranceTransDetails,
    @SerializedName("is_reprint_check")
    val isReprintCheck: Boolean,
    @SerializedName("issued_on")
    val issuedOn: String,
    @SerializedName("mobile_terms_and_conditions")
    val mobileTermsAndConditions: String,
    @SerializedName("no_of_seats")
    val noOfSeats: Int,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("origin_id")
    val originId: Int,
    @SerializedName("passenger_details")
    val passengerDetails: List<PassengerDetail>,
    @SerializedName("payment_initiatives")
    val paymentInitiatives: String,
    @SerializedName("res_id")
    val resId: Long,
    @SerializedName("seat_block_time_configuration")
    val seatBlockTimeConfiguration: Int,
    @SerializedName("seat_numbers")
    val seatNumbers: String,
    @SerializedName("service_number")
    val serviceNumber: String,
    @SerializedName("terminal_id")
    val terminalId: Any,
    @SerializedName("terminal_ref_no")
    val terminalRefNo: String,
    @SerializedName("ticket_number")
    val ticketNumber: String,
    @SerializedName("ticket_status")
    val ticketStatus: String,
    @SerializedName("total_fare")
    val totalFare: Double,
    @SerializedName("total_insurance_amount")
    val totalInsuranceAmount: String,
    @SerializedName("travel_date")
    val travelDate: String,
    @SerializedName("ts_app_print_template")
    val tsAppPrintTemplate: String
)