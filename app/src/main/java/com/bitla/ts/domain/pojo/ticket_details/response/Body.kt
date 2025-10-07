package com.bitla.ts.domain.pojo.ticket_details.response


import PartialPaymentDetail
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("age")
    val age: Int,
    @SerializedName("arr_date")
    val arrDate: String? = "",
    @SerializedName("arr_time")
    val arrTime: String? = "",
    @SerializedName("release_datetime")
    val releaseDatetime: String? = "",
    @SerializedName("ts_app_print_template")
    val bluetoothPrintTemplate: String,
    @SerializedName("transaction_fare")
    val transactionFare: String,
    @SerializedName("total_net_amount")
    val totalNetAmount: String,

    @SerializedName("boarding_details")
    val boardingDetails: BoardingDetails? = null,
    @SerializedName("booked_at")
    val bookedAt: String? = "",
    @SerializedName("bus_type")
    val busType: String? = "",
    @SerializedName("cancellation_policies")
    val cancellationPolicies: List<CancellationPolicy>,
    @SerializedName("coach_number")
    val coachNumber: String? = "",
    @SerializedName("convenience_charge_amount")
    val convenienceChargeAmount: Double,
    @SerializedName("date_format")
    val dateFormat: String? = "",
    @SerializedName("dep_time")
    val depTime: String? = "",
    @SerializedName("destination")
    val destination: String? = "",
    @SerializedName("destination_id")
    val destinationId: Int,
    @SerializedName("discount_category")
    val discountCategory: Any,
    @SerializedName("drop_off_details")
    val dropOffDetails: DropOffDetails? = null,
    @SerializedName("duration")
    val duration: String? = "",
    @SerializedName("gst_details")
    val gstDetails: GstDetails,
    @SerializedName("is_allow_cancellation_type_as_fixed_or_percentage")
    val isAllowCancellationTypeAsFixedOrPercentage: Boolean,
    @SerializedName("is_allow_to_alter_cancel_percent")
    val isAllowToAlterCancelPercent: Boolean,
    @SerializedName("is_coupon_created_ticket")
    val isCouponCreatedTicket: Boolean,
    @SerializedName("is_coupon_used")
    val isCouponUsed: Boolean,
    @SerializedName("is_e_phone_booking")
    val isEPhoneBooking: Boolean,
    @SerializedName("is_eticket")
    val isEticket: Boolean,
    @SerializedName("is_onbehalf_online_ticket")
    val isOnbehalfOnlineTicket: Boolean,
    @SerializedName("refund_types")
    val refundTypes: MutableList<RefundType?>? = null,
    @SerializedName("is_partial_cancelled")
    val isPartialCancelled: Boolean,
    @SerializedName("is_update_ticket")
    val isUpdateTicket: Boolean,
    @SerializedName("is_used_prepost_open_ticket")
    val isUsedPrepostOpenTicket: Boolean,
    @SerializedName("is_zero_percent_cancellation")
    val isZeroPercentCancellation: Boolean,
    @SerializedName("mobile_terms_and_conditions")
    val mobileTermsAndConditions: String,
    @SerializedName("no_of_seats")
    val noOfSeats: Int,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("origin_id")
    val originId: Int,
    @SerializedName("passenger_details")
    val passengerDetails: MutableList<PassengerDetail?>? = null,
    @SerializedName("qr_code")
    val qrCode: String,
    @SerializedName("reservation_id")
    val reservationId: Long,
    @SerializedName("round_trip_number")
    val roundTripNumber: Any,
    @SerializedName("seat_numbers")
    val seatNumbers: String? = "",
    @SerializedName("service_number")
    val serviceNumber: String? = "",
    @SerializedName("service_requests")
    val serviceRequests: ServiceRequests,
    @SerializedName("service_tax_amount")
    val serviceTaxAmount: Double?,
    @SerializedName("snack_total_price")
    val snackTotalPrice: Int?,
    @SerializedName("ticket_lead_detail")
    val ticketLeadDetail: TicketLeadDetail? = null,
    @SerializedName("ticket_number")
    val ticketNumber: String? = "",
    @SerializedName("ticket_status")
    val ticketStatus: String? = "",
    @SerializedName("total_fare")
    val totalFare: Any?,
    @SerializedName("travel_date")
    val travelDate: String? = "",
    @SerializedName("wallet_booking")
    val walletBooking: Boolean,
    @SerializedName("sharing_pdf_link")
    val sharingPdfLink: String? = "",
    @SerializedName("sms_ticket_hash")
    val smsTicketHash: String? = "",
    @SerializedName("booking_source")
    val booking_source: String,
    @SerializedName("insurance_trans_details")
    val insuranceTransDetails: InsuranceDetails?,
    @SerializedName("qr_code_data")
    val qrCodeData: String,
    @SerializedName("partial_payment_details")
    @Expose
    var partialPaymentDetails: PartialPaymentDetail? = null,

    @SerializedName("remarks")
    val remarks: String?,

    @SerializedName("terminal_ref_no")
    val terminalRefNo: String?,

    @SerializedName("is_pay_at_bus_ticket")
    val isPayAtBusTicket: Boolean = false,

    @SerializedName("refund_amount")
    val refundAmount: Double? = 0.0,

    @SerializedName("cancellation_charges")
    val cancellationCharges: Double? = 0.0,

    @SerializedName("is_confirm_ota_booking")
    val isConfirmOtaBooking: Boolean = false,

    @SerializedName("allow_to_release_api_tentative_blocked_tickets")
    val allowToReleaseApiTentativeBlockedTickets: Boolean = false,

    @SerializedName("is_allow_to_print_barcode")
    val isAllowToPrintBarcode: Boolean = false,
    @SerializedName("barcode_value")
    val barcodeValue: String? = null,
    @SerializedName("is_bima_ticket")
    var isBimaTicket: Boolean? = false,

    @SerializedName("is_api_ticket")
    val isApiTicket: Boolean
)

