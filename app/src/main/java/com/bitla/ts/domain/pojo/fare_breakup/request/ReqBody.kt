package com.bitla.ts.domain.pojo.fare_breakup.request

import com.bitla.ts.domain.pojo.book_ticket_full.request.BookingDetails
import com.bitla.ts.domain.pojo.book_ticket_full.request.PassengerGstDetails
import com.bitla.ts.domain.pojo.book_ticket_full.request.PrivilegeCardHash
import com.bitla.ts.domain.pojo.book_ticket_full.request.SmartMilesHash
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReqBody {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("res_id")
    @Expose
    var resId: String? = null

    @SerializedName("origin")
    @Expose
    var origin: String? = null

    @SerializedName("destination")
    @Expose
    var destination: String? = null

    @SerializedName("boarding_at")
    @Expose
    var boardingAt: String? = null

    @SerializedName("drop_off")
    @Expose
    var dropOff: String? = null

    @SerializedName("no_of_seats")
    @Expose
    var noOfSeats: Int? = null

    @SerializedName("is_from_middle_tier")
    @Expose
    var isMiddleTier: Boolean? = true

    @SerializedName("is_round_trip")
    @Expose
    var isRoundTrip: Boolean? = null

    @SerializedName("is_bima_service")
    @Expose
    var isBima: String? = "false"

    @SerializedName("seat_numbers")
    @Expose
    var seatNumbers: List<String>? = null

    @SerializedName("return_seat_numbers")
    @Expose
    var returnSeatNumbers: List<String>? = null

    @SerializedName("passenger_titles")
    @Expose
    var passengerTitles: PassengerTitles? = null

    @SerializedName("return_boarding_at")
    @Expose
    var returnBoardingAt: String? = null

    @SerializedName("return_dropoff")
    @Expose
    var returnDropoff: String? = null

    @SerializedName("offer_coupon")
    @Expose
    var offerCoupon: String? = null

    @SerializedName("promo_coupon")
    @Expose
    var promoCoupon: String? = null

    @SerializedName("use_smart_miles")
    @Expose
    var useSmartMiles: String? = null

    @SerializedName("priv_card_number")
    @Expose
    var privCardNumber: String? = null

    @SerializedName("previous_pnr_details")
    @Expose
    var previousPnrDetails: PreviousPnrDetails? = null

    @SerializedName("coupon_details")
    @Expose
    var couponDetails: List<Any>? = null

    @SerializedName("payment_type")
    @Expose
    var paymentType: String? = null
    
    @SerializedName("agent_payment_type")
    @Expose
    var agentPaymentType: String? = null

    @SerializedName("booking_details")
    @Expose
    var bookingDetails: BookingDetails? = null

    @SerializedName("smart_miles_hash")
    @Expose
    var smartMilesHash: SmartMilesHash? = null

    @SerializedName("coupon_code")
    @Expose
    var couponCode: String? = null

    @SerializedName("promotion_coupon")
    @Expose
    var promotionCoupon: String? = null

    @SerializedName("auto_discount_coupon")
    @Expose
    var auto_discount_coupon: String? = null

    @SerializedName("card_number")
    @Expose
    var cardNumber: String? = null

    @SerializedName("pre_post_pone_pnr")
    @Expose
    var prePostPonePnr: String? = null

    @SerializedName("is_match_prepostpone_amount")
    @Expose
    var isMatchPrepostponeAmount: String? = null

    @SerializedName("allow_pre_post_pone_other_branch")
    @Expose
    var allowPrePostPoneOtherBranch: String? = null

    @SerializedName("corp_company_id")
    @Expose
    var corpCompanyId: String? = null

    @SerializedName("privilege_card_hash")
    @Expose
    var privilegeCardHash: PrivilegeCardHash? = null

    @SerializedName("is_free_booking_allowed")
    @Expose
    var isFreeBookingAllowed: String? = null

    @SerializedName("is_vip_ticket")
    @Expose
    var isVipTicket: String? = null

    @SerializedName("vip_booking_category")
    @Expose
    var vipBookingCategory: String? = null


    @SerializedName("discount_on_total_amount")
    @Expose
    var discountOnTotalAmount: String? = null

    @SerializedName("total_discount_amount")
    @Expose
    var totalDiscountAmount: String? = null

    @SerializedName("discount_amount")
    @Expose
    var discountAmount: String? = null

    @SerializedName("fare")
    @Expose
    var editFare: List<EditFare>? = null

    @SerializedName("additional_fare")
    @Expose
    var additionalFare: List<AdditionalFare>? = null

    @SerializedName("seat_wise_discount")
    @Expose
    var seatWiseFare: List<SeatWiseFare>? = null

    @SerializedName("agent_type")
    @Expose
    var agentType: String? = null

    @SerializedName("extra_seat_fare")
    @Expose
    var extraSeatFare: List<ExtraSeatFare>? = null

    @SerializedName("is_extra_seat")
    @Expose
    var isExtraSeat: Boolean? = null

    @SerializedName("locale")
    @Expose
    var locale: String? = ""

    @SerializedName("is_insurance_enabled")
    @Expose
    var isInsuranceEnabled: Boolean = false

    @SerializedName("passenger_gst_details")
    var passengerGstDetails: PassengerGstDetails? = null

    @SerializedName("pickup_details")
    var pickupChargeDetails: ChargeDetails? = null

    @SerializedName("dropoff_details")
    var dropoffChargeDetails: ChargeDetails? = null
}