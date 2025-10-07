package com.bitla.ts.domain.pojo.ticket_details_phase_3.response


import com.bitla.ts.domain.pojo.ticket_details.response.InsuranceDetails
import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("arr_date")
    val arrDate: String?,
    @SerializedName("arr_time")
    val arrTime: String?,
    @SerializedName("barcode_value")
    val barcodeValue: String?,
    @SerializedName("boarding_details")
    val boardingDetails: BoardingDetails?,
    @SerializedName("booked_at")
    val bookedAt: String?,
    @SerializedName("booking_source")
    val bookingSource: String?,
    @SerializedName("bus_type")
    val busType: String?,
    @SerializedName("can_cancel")
    val canCancel: Boolean?,
    @SerializedName("can_cancel_ticket_for_agent")
    val canCancelTicketForAgent: Boolean?,
    @SerializedName("can_cancel_ticket_for_user")
    val canCancelTicketForUser: Boolean?,
    @SerializedName("can_confirm_phone_block")
    val canConfirmPhoneBlock: Boolean?,
    @SerializedName("can_release_phone_block")
    val canReleasePhoneBlock: Boolean?,
    @SerializedName("can_shift_ticket")
    val canShiftTicket: Boolean?,
    @SerializedName("coach_number")
    val coachNumber: String?,
    @SerializedName("convenience_charge_amount")
    val convenienceChargeAmount: Double?,
    @SerializedName("date_format")
    val dateFormat: String?,
    @SerializedName("dep_time")
    val depTime: String?,
    @SerializedName("destination")
    val destination: String?,
    @SerializedName("destination_id")
    val destinationId: Int?,
    @SerializedName("drop_off_details")
    val dropOffDetails: DropOffDetails?,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("insurance_trans_details")
    val insuranceTransDetails: InsuranceDetails?,// Here I am using old Insurance Details because if I will use new insurance then I will have to make major changes in QoalaInsuranceAdapter
    @SerializedName("is_allow_cancellation_type_as_fixed_or_percentage")
    val isAllowCancellationTypeAsFixedOrPercentage: Boolean?,
    @SerializedName("is_allow_to_alter_cancel_percent")
    val isAllowToAlterCancelPercent: Boolean?,
    @SerializedName("is_allow_to_print_barcode")
    val isAllowToPrintBarcode: Boolean?,
    @SerializedName("is_confirm_ota_booking")
    val isConfirmOtaBooking: Boolean?,
    @SerializedName("is_coupon_created_ticket")
    val isCouponCreatedTicket: Boolean?,
    @SerializedName("is_coupon_used")
    val isCouponUsed: Boolean?,
    @SerializedName("is_e_phone_booking")
    val isEPhoneBooking: Boolean?,
    @SerializedName("is_eticket")
    val isEticket: Boolean?,
    @SerializedName("is_onbehalf_online_ticket")
    val isOnbehalfOnlineTicket: Boolean?,
    @SerializedName("is_reprint_check")
    val isReprintCheck: Boolean?,
    @SerializedName("is_update_ticket")
    val isUpdateTicket: Boolean?,
    @SerializedName("is_zero_percent_cancellation")
    val isZeroPercentCancellation: Boolean?,
    @SerializedName("no_of_seats")
    val noOfSeats: Int?,
    @SerializedName("origin")
    val origin: String?,
    @SerializedName("origin_id")
    val originId: Int?,
    @SerializedName("partial_payment_details")
    val partialPaymentDetails: PartialPaymentDetails?,
    @SerializedName("passenger_details")
    val passengerDetails: List<PassengerDetail?>?,
    @SerializedName("qr_code")
    val qrCode: String?,
    @SerializedName("terminal_ref_qr_code")
    val terminalRefQrCode: String?,
    @SerializedName("qr_code_data")
    val qrCodeData: String?,
    @SerializedName("refund_types")
    val refundTypes: List<RefundType?>?,
    @SerializedName("remarks")
    val remarks: String?,
    @SerializedName("reservation_id")
    val reservationId: Long?,
    @SerializedName("seat_numbers")
    val seatNumbers: String?,
    @SerializedName("service_number")
    val serviceNumber: String?,
    @SerializedName("service_tax_amount")
    val serviceTaxAmount: Double?,
    @SerializedName("sharing_pdf_link")
    val sharingPdfLink: String?,
    @SerializedName("sms_ticket_hash")
    val smsTicketHash: String?,
    @SerializedName("terminal_ref_no")
    val terminalRefNo: String?,
    @SerializedName("ticket_fare")
    val ticketFare: String?,
    @SerializedName("ticket_number")
    val ticketNumber: String?,
    @SerializedName("ticket_status")
    val ticketStatus: String?,
    @SerializedName("total_fare")
    val totalFare: String?,
    @SerializedName("transaction_fare")
    val transactionFare: String?,
    @SerializedName("total_net_amount")
    val totalNetAmount: String?,
    @SerializedName("travel_date")
    val travelDate: String?,
    @SerializedName("ticket_booked_by")
    val ticketBookedBy: String?,
    @SerializedName("issued_by")
    val issuedBy: String?,
    @SerializedName("ts_app_print_template")
    val tsAppPrintTemplate: String?,
    @SerializedName("is_pay_at_bus_ticket")
    val isPayAtBusTicket: Boolean = false ,
    @SerializedName("trip_counts")
    val tripCounts: String?,
    @SerializedName("is_bima_ticket")
    val isBimaTicket: Boolean?,
    @SerializedName("print_count")
    val printCount: Int? = 0,
    @SerializedName("service_by")
    val serviceBy: String?,
    @SerializedName("pickup_address")
    val pickupAddress: String?,
    @SerializedName("pickup_charge")
    val pickupCharge: Double?,
    @SerializedName("dropoff_address")
    val dropoffAddress: String?,
    @SerializedName("dropoff_charge")
    val dropoffCharge: Double?,
    @SerializedName("reprint_charges")
    val reprintCharges: Double?
)