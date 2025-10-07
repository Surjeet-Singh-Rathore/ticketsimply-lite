package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model

import com.google.gson.annotations.SerializedName

data class TsPrivileges(
    @SerializedName("allow_to_auto_fill_previous_trip_crew_details_on_update_details_page_on_coach_number_selection")
    var allowToAutoFillPreviousTripCrewDetails: Boolean? = false,
    @SerializedName("update_fare_for_phone_blocked_or_confirmed_tickets")
    var updateFareForPhoneBlockedOrConfirmedTickets: Boolean? = false,
    @SerializedName("allow_to_display_customer_phone_number")
    var allowToDisplayCustomerPhoneNumber: Boolean? = true,
    @SerializedName("allow_easebuzz_in_ts")
    var allowEasebuzzInTs: Boolean? = false,
    @SerializedName("thermal_print_for_ts_app")
    var thermalPrintForTsApp: Boolean? = false,
    @SerializedName("edit_fare_mandatory_for_agent_user")
    var editFareMandatoryForAgentUser: Boolean? = false,
    @SerializedName("trip_sheet_collection_option_in_ts_app_reservation_chart")
    var tripSheetCollectionOptionsInTSAppReservationChart: Boolean? = false,
    @SerializedName("allow_promotion_offer_coupon_in_booking_page")
    var allowPromotionOfferCouponInBookingPage: Boolean = false,
    @SerializedName("allow_users_to_view_ticket")
    var allowUsersToViewTicket: Boolean? = false,
    @SerializedName("allow_service_blocking_reasons_in_single_page_block_unblock")
    var allowServiceBlockingReasonsList: Boolean? = false,
    @SerializedName("allow_quota_blocking_by_drag_drop_in_the_coach_layout")
    var allowQuotaBlockingByDragDropInTheCoachLayout: Boolean? = false,
    @SerializedName("allow_fare_change_for_multiple_services")
    var allowFareChangeForMultipleServices: Boolean = false,

    @SerializedName("allow_all_to_all_search_in_ts_mobile_app")
    var allowAllToAllSearchInTsMobileApp: Boolean = false,

    @SerializedName("show_credit_limit_for_agents_and_sub_agents")
    var showCreditLimitForAgentsAndSubAgents: Boolean? = false,
    @SerializedName("allow_gender_default_ho_quota_blocked_seat")
    var allowGenderDefaultHoQuotaBlockedSeat: Boolean? = false,
    @SerializedName("update_luggage_details_post_confirmation")
    var updateLuggageDetailsPostConfirmation: Boolean? = false,
    @SerializedName("allow_to_show_remarks_for_cancel_ticket")
    var allowToShowRemarksForCancelTicket: Boolean? = false,
    @SerializedName("qoala_image_v1")
    var qoalaImageV1: String? = null,
    @SerializedName("allow_to_view_pickup_van_charts_agent")
    var allowToViewPickupVanChartsAgent: Boolean? = false,
    @SerializedName("group_by_pickup_van_chart")
    var groupByPickupVanChart: Boolean?= false,
    @SerializedName("is_allow_passenger_details_update_while_confirming_phone_block_seats")
    var isAllowPassengerDetailsUpdateWhileConfirmingPhoneBlockSeats: Boolean?= false,
    @SerializedName("is_allow_phone_blocked_ticket_confirm_on_behalf_of_online_offline_agent")
    var isAllowPhoneBlockedTicketConfirmOnBehalfOfOnlineOfflineAgent: Boolean?= false,
    @SerializedName("booking_payment_options")
    var bookingPaymentOptions: List<String>?,
    @SerializedName("default_payment")
    var defaultPayment: DefaultPayment? = null,
    @SerializedName("group_by_pnr_pickup_chart")
    var groupByPnrPickupChart: Boolean? = false,
    @SerializedName("allow_phonepe_v2_in_ts_app")
    var allowPhonePeV2InTsApp: Boolean? = false,
    @SerializedName("upi_sub_payment_options")
    var upiSubPaymentOptions: List<String>?,
)

data class DefaultPayment(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("name")
    var name: String? = null
)
