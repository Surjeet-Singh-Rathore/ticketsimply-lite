package com.example.buscoach.service_details_response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Body: Serializable {

    @SerializedName("number")
    var number: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("origin")
    var origin: Origin? = null

    @SerializedName("destination")
    var destination: Destination? = null

    @SerializedName("travel_date")
    var travelDate: String? = null

    @SerializedName("route_id")
    var routeId: Int? = null

    @SerializedName("phone_blocking_hour")
    var phoneBlockingHour: String? = null

    @SerializedName("available_seats")
    var availableSeats: Int? = null

    @SerializedName("dep_time")
    var depTime: String? = null

    @SerializedName("pay_gay_type")
    var payGayType: List<PayGayType>? = null

    @SerializedName("arr_time")
    var arrTime: String? = null

    @SerializedName("duration")
    var duration: String? = null

    @SerializedName("bus_type")
    var busType: String? = null

    @SerializedName("via")
    var via: Any? = null

    @SerializedName("cost")
    var cost: String? = null

    @SerializedName("coupon_code")
    var couponCode: String? = null

    @SerializedName("is_child_fare")
    var isChildFare: Boolean? = null

    @SerializedName("coach_details")
    var coachDetails: CoachDetails? = null

    @SerializedName("extra_seat_details")
    var extraSeatDetails: MutableList<ExtraSeatDetail>? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("stage_details")
    var stageDetails: MutableList<StageDetail>? = null

    @SerializedName("cancellation_policies")
    var cancellationPolicies: List<CancellationPolicy>? = null

    @SerializedName("legend_details")
    var legendDetails: ArrayList<LegendDetail>? = null

    @SerializedName("intr_legend_details")
    var intrLegendDetails: IntrLegendDetails? = null

    @SerializedName("is_hotel_lead_privilege")
    var isHotelLeadPrivilege: Boolean? = null

    @SerializedName("is_pick_up_lead_privilege")
    var isPickUpLeadPrivilege: Boolean? = null

    @SerializedName("is_drop_off_lead_privilege")
    var isDropOffLeadPrivilege: Boolean? = null

    @SerializedName("is_day_visit_lead_privilege")
    var isDayVisitLeadPrivilege: Boolean? = null

    @SerializedName("is_service_tax_applicable")
    var isServiceTaxApplicable: Boolean? = null

    @SerializedName("is_gst_applicable")
    var isGstApplicable: Boolean? = null

    @SerializedName("is_coach_layout_hide")
    var isCoachLayoutHide: Boolean? = null

    @SerializedName("st_percent")
    var stPercent: String? = null

    @SerializedName("is_own_route")
    var isOwnRoute: Boolean? = null

    @SerializedName("branch_discount_amt")
    var branchDiscountAmt: Int? = null

    @SerializedName("branch_discount_type")
    var branchDiscountType: Int? = null

    @SerializedName("convenience_charge_percent")
    var convenienceChargePercent: Int? = null

    @SerializedName("do_not_apply_eticket_discount")
    var doNotApplyEticketDiscount: Boolean? = null

    @SerializedName("id_types_arr")
    var idTypesArr: List<List<String>>? = null

    @SerializedName("nationality_list")
    var nationalityList: List<List<String>>? = null

    @SerializedName("social_distancing_guaranteed")
    var socialDistancingGuaranteed: Boolean? = null

    @SerializedName("all_fare_details")
    var allFareDetails: List<Any>? = null

    @SerializedName("is_snack_available")
    var isSnackAvailable: Boolean? = null

    @SerializedName("booking_type_seat_counts")
    var bookingTypeSeatCounts: BookingTypeSeatCounts? = null

    @SerializedName("account_balance")
    var accountBalance: String? = null


    @SerializedName("is_meal_required")
    var isMealRequired: Boolean? = null

    @SerializedName("meal_count")
    val mealCount: Int? = null

    @SerializedName("is_meal_no_type")
    val isMealNoType: Boolean? = null

    @SerializedName("selected_meal_types")
    val selectedMealTypes: Any? = null

    @SerializedName("is_from_shift_passenger")
    var isFromShiftPassenger: Boolean = false

    @SerializedName("shift_passenger_count")
    var shiftPassengerCount: Int = 0

    @SerializedName("discount_configuration")
    var discountConfiguration: DiscountConfiguration? = null

    @SerializedName("reservation_id")
    val reservationId: String = "0"

    @SerializedName("can_block_seat")
    var canBlockSeat: Boolean? = false

    @SerializedName("can_unblock_seat")
    var canUnblockSeat: Boolean? = false

    @SerializedName("is_bima")
    var isBima: Boolean = false

    @SerializedName("is_multihop_enable")
    var isMultihopEnable: Boolean? = false
}
