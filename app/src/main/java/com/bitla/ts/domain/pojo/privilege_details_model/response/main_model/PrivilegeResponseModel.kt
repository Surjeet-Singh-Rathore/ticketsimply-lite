package com.bitla.ts.domain.pojo.privilege_details_model.response.main_model


import com.bitla.ts.domain.pojo.app_submission_history.*
import com.bitla.ts.domain.pojo.instant_recharge.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.child_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.testttt.*
import com.bitla.ts.domain.pojo.redelcom.*
import com.google.gson.annotations.*

data class PrivilegeResponseModel(
    @SerializedName("able_to_cancel_onbehalf_other_user")
    val ableToCancelOnbehalfOtherUser: Boolean,
    /*  @SerializedName("agent_cancellation_report")
      val agentCancellationReport: Boolean,*/
    @SerializedName("agent_recharge_report")
    val agentRechargeReport: Boolean,
//    @SerializedName("agent_transaction_report")
//    val agentTransactionReport: Boolean,
    @SerializedName("allow_agent_discount_configuration_for_route_wise")
    val allowAgentDiscountConfigurationForRouteWise: Boolean,
//    @SerializedName("allow_agent_to_give_discount_more_than_his_commission")
//    val allowAgentToGiveDiscountMoreThanHisCommission: Boolean,
    @SerializedName("allow_associate_seat_boarding_status_in_mobility_app")
    val allowAssociateSeatBoardingStatusInMobilityApp: Boolean,
    @SerializedName("allow_bluetooth_print")
    val allowBluetoothPrint: Boolean,
    /*@SerializedName("allow_booking_after_departure_time")
    val allowBookingAfterDepartureTime: Boolean,*/
//    @SerializedName("allow_branch_accounting")
//    val allowBranchAccounting: Boolean,
    @SerializedName("allow_bulk_cancellation")
    val allowBulkCancellation: Boolean,
    @SerializedName("allow_bulk_shifting")
    val allowBulkShifting: Boolean,
    @SerializedName("allow_cash_credit_options_in_booking")
    val allowCashCreditOptionsInBooking: Boolean,
    @SerializedName("allow_conveniece_charges_in_mobility")
    val allowConvenieceChargesInMobility: Boolean,
    @SerializedName("allow_discount_for_agents")
    val allowDiscountForAgents: Boolean,
    @SerializedName("allow_fare_updation_for_confirmed_ticket")
    val allowFareUpdationForConfirmedTicket: Boolean,
    @SerializedName("allow_multiple_quota")
    val allowMultipleQuota: Boolean,
    @SerializedName("allow_onbehalf_of_booking_or_blocking")
    val allowOnbehalfOfBookingOrBlocking: Boolean,
    @SerializedName("allow_other_details_in_update_ticket")
    val allowOtherDetailsInUpdateTicket: Boolean,
    @SerializedName("allow_phone_blocking_ticket_onbehalf_online_agent")
    val allowPhoneBlockingTicketOnbehalfOnlineAgent: Boolean,
    @SerializedName("allow_provision_to_select_multiple_boarding_and_dropoff_point")
    val allowProvisionToSelectMultipleBoardingAndDropoffPoint: Boolean,
    @SerializedName("allow_provision_to_select_multiple_boarding_and_dropoff_points")
    val allowProvisionToSelectMultipleBoardingAndDropoffPoints: Boolean,
    @SerializedName("allow_rapid_booking_flow")
    val allowRapidBookingFlow: Boolean = false,
    @SerializedName("allow_route_rate_cards")
    val allowRouteRateCards: Boolean,
    @SerializedName("allow_time_blocking_for_offline_agent")
    val allowTimeBlockingForOfflineAgent: Boolean,
    @SerializedName("allow_time_blocking_for_offline_agent_for_other_routes")
    val allowTimeBlockingForOfflineAgentForOtherRoutes: Boolean,
    @SerializedName("allow_time_blocking_for_online_agent_for_other_routes")
    val allowTimeBlockingForOnlineAgentForOtherRoutes: Boolean,
    @SerializedName("allow_time_blocking_for_other_routes")
    val allowTimeBlockingForOtherRoutes: Boolean,
    @SerializedName("allow_to_accept_multi_currencies")
    val allowToAcceptMultiCurrencies: Boolean,
    @SerializedName("allow_to_approve_recharge_request")
    val allowToApproveRechargeRequest: Boolean,
    @SerializedName("allow_to_book_extra_seat_for_other_route")
    val allowToBookExtraSeatForOtherRoute: Boolean,
    @SerializedName("allow_to_book_extra_seat_on_behalf_of_offline_or_online_agent_for_other_route")
    val allowToBookExtraSeatOnBehalfOfOfflineOrOnlineAgentForOtherRoute: Boolean,
    @SerializedName("allow_to_book_extra_seats")
    val allowToBookExtraSeats: Boolean,
    @SerializedName("allow_to_move_booked_seat_to_extra_seat")
    val allowToMoveBookedSeatToExtraSeat: Boolean,
    @SerializedName("allow_to_book_extra_seats_in_empty_service")
    val allowToBookExtraSeatsInEmptyService: Boolean,
    @SerializedName("allow_to_book_extra_seats_on_behalf_of_online_offline_agents")
    val allowToBookExtraSeatsOnBehalfOfOnlineOfflineAgents: Boolean,
    @SerializedName("allow_to_cancel_onbehalf_booked_user")
    val allowToCancelOnbehalfBookedUser: Boolean,
    @SerializedName("allow_to_capture_pass_and_crew_temp")
    val allowToCapturePassAndCrewTemp: Boolean,
    @SerializedName("allow_to_configure_payment_options_in_booking_page")
    val allowToConfigurePaymentOptionsInBookingPage: Boolean,
    @SerializedName("allow_deposit_options_in_booking")
    val allowDepositOptionsInBooking: Boolean,
    @SerializedName("allow_to_configure_seat_wise_fare")
    val allowToConfigureSeatWiseFare: Boolean? = null,
    @SerializedName("allow_to_copy_the_passenger_details_for_extra_seat")
    val allowToCopyThePassengerDetailsForExtraSeat: Boolean,
    @SerializedName("allow_to_create_adhoc_driver")
    val allowToCreateAdhocDriver: Boolean? = null,
    @SerializedName("allow_to_edit_passenger_details_in_edit_chart")
    val allowToEditPassengerDetailsInEditChart: Boolean,
    @SerializedName("allow_to_enter_passport_details")
    val allowToEnterPassportDetails: Boolean,
    @SerializedName("allow_to_enter_the_luggage_details")
    val allowToEnterTheLuggageDetails: Boolean,
    @SerializedName("allow_to_extend_fare_for_services")
    val allowToExtendFareForServices: Boolean,
    @SerializedName("allow_to_raise_recharge_request")
    val allowToRaiseRechargeRequest: Boolean,
    @SerializedName("allow_to_release_api_tentative_blocked_tickets")
    val allowToReleaseApiTentativeBlockedTickets: Boolean? = null,
    @SerializedName("allow_to_send_sms_in_pnr_search_page")
    val allowToSendSmsInPnrSearchPage: Boolean? = null,
    @SerializedName("allow_to_send_sms_on_booking")
    val allowToSendSmsOnBooking: Boolean,
    @SerializedName("allow_to_send_whatsapp_messages")
    val allowToSendWhatsappMessages: Boolean? = null,
    @SerializedName("allow_to_show_company_details_in_the_bookings_page")
    val allowToShowCompanyDetailsInTheBookingsPage: Boolean,
    @SerializedName("allow_to_show_whatsapp_checkbox_in_booking_page")
    val allowToShowWhatsappCheckboxInBookingPage: Boolean,
    @SerializedName("allow_to_update_fuel_filling_details_from_bus_mobility_app")
    val allowToUpdateFuelFillingDetailsFromBusMobilityApp: Boolean,
    @SerializedName("allow_to_view_the_coach_document")
    val allowToViewTheCoachDocument: Boolean,
    @SerializedName("agent_instant_recharge")
    val agentInstantRecharge: Boolean,
    @SerializedName("allow_update_details_option_in_reservation_chart")
    val allowUpdateDetailsOptionInReservationChart: Boolean,
    @SerializedName("allow_updation_of_fare_in_update_ticket")
    val allowUpdationOfFareInUpdateTicket: Boolean,
    @SerializedName("allow_viewing_the_remark_in_bus_mobility")
    val allowViewingTheRemarkInBusMobility: Boolean,
    @SerializedName("allow_wallet_and_upi_options_in_booking_page")
    val allowWalletAndUpiOptionsInBookingPage: Boolean,
    @SerializedName("allow_to_show_upi_payment_option_in_booking_page")
    val allowToShowUpiPaymentOptionInBookingPage: Boolean,
    @SerializedName("app_passenger_detail_config")
    val appPassengerDetailConfig: AppPassengerDetailConfig? = null,
    @SerializedName("available_app_modes")
    val availableAppModes: AvailableAppModes? = null,
    @SerializedName("user_city")
    val user_city: UserCityStageModel? = null,
    @SerializedName("booking_options")
    val bookingOptions: BookingOptions,
    @SerializedName("branch_discount_type")
    val branchDiscountType: String,
    /*  @SerializedName("branches")
      val branches: List<List<Any>>,*/

    @SerializedName("bus_classification_sys_conf")
    val busClassificationSysConf: Boolean,
    @SerializedName("bus_service_report")
    val busServiceReport: Boolean,
//    @SerializedName("can_user_see_different_branch")
//    val canUserSeeDifferentBranch: Boolean,
    @SerializedName("cargo_booking_report")
    val cargoBookingReport: Boolean,
    @SerializedName("code")
    val code: Int,
    @SerializedName("convenience_charge_percent")
    val convenienceChargePercent: Double,
    @SerializedName("country")
    var country: String,
    @SerializedName("currency")
    var currency: String,
    @SerializedName("currency_code")
    val currencyCode: String,
    @SerializedName("currency_types")
    val currencyTypes: List<CurrencyType>,
    @SerializedName("discount_categories")
    val discountCategories: List<DiscountCategory>,
    @SerializedName("fuel_utility_mode")
    val fuelUtilityMode: Boolean,
    @SerializedName("fuel_utility_report")
    val fuelUtilityReport: Boolean,
    @SerializedName("handle_block_allow_from_reservation_Charts")
    val handleBlockAllowFromReservationCharts: Boolean,
    @SerializedName("is_additional_fare")
    val isAdditionalFare: Boolean? = null,
    @SerializedName("is_agent_login")
    val isAgentLogin: Boolean,
    @SerializedName("bo_licenses")
    val boLicenses: BoLicenses? = null,
    @SerializedName("is_allow_booking_after_travel_date")
    val isAllowBookingAfterTravelDate: Boolean,
    @SerializedName("is_allow_branch_booking")
    val isAllowBranchBooking: Boolean,
    @SerializedName("is_allow_cancelled_ticket_in_pnr_search")
    val isAllowCancelledTicketInPnrSearch: Boolean,
    @SerializedName("is_allow_discount_for_on_behalf_of_agents")
    val isAllowDiscountForOnBehalfOfAgents: Boolean,
    @SerializedName("is_allow_discount_while_booking")
    val isAllowDiscountWhileBooking: Boolean,
    @SerializedName("is_allow_discount_while_booking_for_other_route")
    val isAllowDiscountWhileBookingForOtherRoute: Boolean,
    @SerializedName("is_allow_free_booking_for_agents")
    val isAllowFreeBookingForAgents: Boolean,
    @SerializedName("is_allow_group_by_branch")
    val isAllowGroupByBranch: Boolean,
    @SerializedName("is_allow_offline_agent_booking")
    val isAllowOfflineAgentBooking: Boolean,
    @SerializedName("is_allow_online_agent_booking")
    val isAllowOnlineAgentBooking: Boolean,
    @SerializedName("is_allow_online_agent_booking_for_other_routes")
    val isAllowOnlineAgentBookingForOtherRoutes: Boolean,
    @SerializedName("is_allow_partial_release_for_time_blocking_ticket")
    val isAllowPartialReleaseForTimeBlockingTicket: Boolean,
    @SerializedName("is_allow_shift_passenger_after_travel_date")
    val isAllowShiftPassengerAfterTravelDate: Boolean,
    @SerializedName("is_allowed_to_edit_fare")
    val isAllowedToEditFare: Boolean,
    @SerializedName("is_allowed_to_edit_fare_for_other_route")
    val isAllowedToEditFareForOtherRoute: Boolean,
    @SerializedName("is_booking_ref_num")
    val isBookingRefNum: Boolean,
    @SerializedName("is_branch_discount")
    val isBranchDiscount: Boolean,
    @SerializedName("is_can_block_unblock_seats")
    val isCanBlockUnblockSeats: Boolean,
    @SerializedName("is_chile_app")
    val isChileApp: Boolean,
    @SerializedName("is_confirm_onbehalf_of_pending_ticket")
    val isConfirmOnbehalfOfPendingTicket: Boolean,
    @SerializedName("is_discount_on_total_amount")
    val isDiscountOnTotalAmount: Boolean? = null,
    @SerializedName("allow_to_do_open_ticket_coupon")
    val allowToDoOpenTicketCoupon: Boolean = false,
    @SerializedName("apply_quote_previous_pnr_discount")
    val applyQuotePreviousPnrDiscount: Boolean = false,
    @SerializedName("is_edit_fare_limit_configuration")
    val isEditFareLimitConfiguration: Boolean,
    @SerializedName("is_edit_reservation")
    val isEditReservation: Boolean,
    @SerializedName("is_free_booking")
    val isFreeBooking: Boolean,
    @SerializedName("is_permanent_phone_booking")
    val isPermanentPhoneBooking: Boolean,
    @SerializedName("is_phone_booking")
    val isPhoneBooking: Boolean,
    @SerializedName("allow_prepostpone")
    val isPrePostpone: Boolean,
    @SerializedName("allow_privilege_card_bookings")
    val allowPrivilegeCardBookings: Boolean,
    @SerializedName("is_service_wise_city_pickup_closure_report")
    val isServiceWiseCityPickupClosureReport: Boolean,
    @SerializedName("is_vip_a_free_booking")
    val isVipAFreeBooking: Boolean,
    @SerializedName("is_vip_booking")
    val isVipBooking: Boolean,
    @SerializedName("locale")
    val locale: String,
    @SerializedName("manage_branch_accounting")
    val manageBranchAccounting: Boolean,
    @SerializedName("manage_customer_announcement")
    val manageCustomerAnnouncement: Boolean,
    @SerializedName("notify_option")
    val notifyOption: Boolean? = false,
    @SerializedName("occupancy_report")
    val occupancyReport: Boolean,
    @SerializedName("online_branch_agent_collection_by_issue_date")
    val onlineBranchAgentCollectionByIssueDate: Boolean,
    @SerializedName("operator_name")
    val operatorName: String,
    @SerializedName("others_payment_option")
    val othersPaymentOption: List<OthersPaymentOption>,
    @SerializedName("passenger_configs")
    val passengerConfigs: PassengerConfigs,
    @SerializedName("passenger_detail_config")
    val passengerDetailConfig: PassengerDetailConfig,
    @SerializedName("phone_num_validation_count")
    val phoneNumValidationCount: Int?,
    @SerializedName("phone_number_digits")
    val phoneNumberDigits: String,
    @SerializedName("phone_number_starting_digits")
    val phoneNumberStartingDigits: String,
    @SerializedName("restrict_user_to_access_update_details_tab")
    val restrictUserToAccessUpdateDetailsTab: Boolean,
    @SerializedName("return_journey")
    val returnJourney: Boolean,
    @SerializedName("send_otp_to_customers_to_authenticate_boarding_status")
    val sendOtpToCustomersToAuthenticateBoardingStatus: Boolean,
    @SerializedName("send_qr_code_to_customers_to_authenticate_boarding_status")
    val sendQrCodeToCustomersToAuthenticateBoardingStatus: Boolean,
    @SerializedName("show_bus_mobility_app_dashboard")
    val showBusMobilityAppDashboard: Boolean,
    @SerializedName("show_checking_inspector_report")
    val showCheckingInspectorReport: Boolean,
    @SerializedName("show_discount_category_in_print_ticket")
    val showDiscountCategoryInPrintTicket: Boolean,
    @SerializedName("Show_Inclusive_tax_Fare_in_the_mobility_app_booking_page")
    val showInclusiveTaxFareInTheMobilityAppBookingPage: Boolean,
    @SerializedName("show_lock_link_in_reservation_charts")
    val showLockLinkInReservationCharts: Boolean,
    @SerializedName("show_manage_agent_account_link_in_account")
    val showManageAgentAccountLinkInAccount: Boolean,
    @SerializedName("show_nationlity_and_id_card_in_booking")
    val showNationlityAndIdCardInBooking: Boolean,
    @SerializedName("show_pending_api_bookings_link_in_home_page")
    val showPendingApiBookingsLinkInHomePage: Boolean? = null,
    @SerializedName("show_pending_confirmation_link_in_home_page")
    val showPendingConfirmationLinkInHomePage: Boolean? = null,
    @SerializedName("show_prefill_data_in_booking")
    val showPrefillDataInBooking: Boolean,
    @SerializedName("show_remarks_field_in_the_release_ticket_popup")
    val showRemarksFieldInTheReleaseTicketPopup: Boolean,
    @SerializedName("show_seat_fare_on_the_coach")
    val showSeatFareOnTheCoach: Boolean? = false,
    @SerializedName("show_view_chart_link_in_the_search_results")
    val showViewChartLinkInTheSearchResults: Boolean,
    @SerializedName("ticket_booked_by_you")
    val ticketBookedByYou: Boolean,
    @SerializedName("update_crew_toolkit_checklist")
    val updateCrewToolkitChecklist: Boolean,
    @SerializedName("update_passenger_travel_status")
    val updatePassengerTravelStatus: Boolean,
    @SerializedName("update_phone_booking_ticket")
    val updatePhoneBookingTicket: Boolean,
    @SerializedName("update_ticket_updation_of_fare_for_phone_blocked_tickets")
    val updateTicketUpdationOfFareForPhoneBlockedTickets: Boolean,
    @SerializedName("user_wise_collection_report")
    val userWiseCollectionReport: Boolean,
    @SerializedName("validate_bus_crew_updation_for_coachs")
    val validateBusCrewUpdationForCoachs: Boolean,
    @SerializedName("validate_coach_number")
    val validateCoachNumber: Boolean,
    @SerializedName("validate_remarks_for_boarding_stage_in_mobility_app")
    val validateRemarksForBoardingStageInMobilityApp: Boolean,
    @SerializedName("view_collection_chart")
    val viewCollectionChart: Boolean,
    @SerializedName("wallet_payment_options")
    val walletPaymentOptions: MutableList<WalletPaymentOption>,
    @SerializedName("vip_category_list")
    val vipCategoryList: MutableList<VipCategoryList>,
    @SerializedName("web_address_url")
    val webAddressUrl: String,
    @SerializedName("bulk_updation_of_tickets")
    val bulkUpdationOfTickets: Boolean? = null,
    @SerializedName("show_passenger_search_in_home_page_for_users")
    val showPassengerSearchInHomePageForUsers: Boolean? = null,
    @SerializedName("show_pickup_chart_based_on_hubs_configuration")
    val showPickupChartBasedOnHubsConfiguration: Boolean,
    @SerializedName("allow_to_close_pick_up_by_city")
    val allowToClosePickupByCity: Boolean,
    @SerializedName("is_can_block_seats")
    val isCanBlockSeats: Boolean? = null,
    @SerializedName("is_can_unblock_seats")
    val isCanUnblockSeats: Boolean? = null,
    @SerializedName("booking_after_doj")
    val bookingAfterDoj: String? = null,
    @SerializedName("result")
    val result: Result,
    @SerializedName("allow_booking")
    val allowBooking: Boolean? = null,
    @SerializedName("allow_bima_in_ts")
    val allowBimaInTs: Boolean? = null,
    @SerializedName("free_ticket")
    val freeTicket: Boolean? = null,
    @SerializedName("restrict_booking")
    val restrictBooking: Boolean,
    @SerializedName("apply_smart_miles")
    val applySmartMiles: Boolean,
    @SerializedName("allow_to_switch_single_page_booking")
    var allowToSwitchSinglePageBooking: Boolean,
    @SerializedName("single_page_chart_block_unblock")
    var singlePageChartBlockUnblock: Boolean,
    @SerializedName("is_city_wise_bp_dp_display")
    var isCityWiseBpDpDisplay: Boolean,
    @SerializedName("pickup_chart_print_for_ts_app")
    var pickupChartPrint: Boolean,
    @SerializedName("dailing_code")
    var dialingCode: ArrayList<Int>,
    @SerializedName("currency_format")
    var currencyFormat: String?,
    @SerializedName("ping_rate")
    val pingRate: Map<String, String>,
    @SerializedName("recharge_payment_status_type")
    val rechargePaymentStatusType: MutableList<RechargePaymentStatus>,
    @SerializedName("configured_login_validity_time")
    var configuredLoginValidityTime: String,
    @SerializedName("hub_details")
    var hubDetails: ArrayList<HubDetails>? = null,
    @SerializedName("allow_to_view_ts_app_new_dashboard")
    var allowToViewTsAppNewDashboard: Boolean = false,
    @SerializedName("is_redelcom_print_enabled")
    var isRedelcomPrintEnable: Boolean,
    @SerializedName("allow_payment_through_redelcom_enabled")
    var allowPaymentThroughRedelcomEnabled: Boolean,
    @SerializedName("is_auto_discount_rut_enable")
    var isAutoDiscountRutEnable: Boolean? = false,
    @SerializedName("partial_payment_limit_type")
    var partialPaymentLimitType: String,
    @SerializedName("partial_payment_limit_value")
    var partialPaymentLimitValue: String?,
    @SerializedName("allow_quick_bookings_for_ts_mobile_app")
    var isAllowQuickBookingsForTSMobileApp: Boolean? = false,
    @SerializedName("freeze_meal_selection")
    var freezeMealSelection: Boolean = false,
    @SerializedName("bulk_cancellation_config")
    var bulkCancellationConfig: String? = null,
    @SerializedName("show_pickup_van_chart_tab_in_reservation_chart")
    var showPickupVanChartTabInReservationChart: Boolean,
    @SerializedName("allow_to_do_phone_blocking")
    var allowToDoPhoneBlocking: Boolean,
    @SerializedName("app_submission_history")
    var appSubmissionHistory: AppSubmissionHistory,
    @SerializedName("allow_qoala_insurance")
    var allowQoalaInsurance: Boolean,
    @SerializedName("insurance_mandatory_for_bookings")
    var insuranceMandatoryForBookings: Boolean,
    @SerializedName("enable_insurance_checkbox_for_booking")
    var enableInsuranceCheckboxForBooking: Boolean,
    @SerializedName("display_passenger_details_by_mobile")
    var displayPassengerDetailsByMobile: Boolean,
    @SerializedName("allow_to_show_phonepe_direct_upi_payment_option_in_booking_page")
    var allowToShowPhonePeDirectUPIPaymentOptionInBookingPage: Boolean?,
    @SerializedName("phonepe_direct_upi_options")
    var phonePeDirectUPIOptions: String?,
    @SerializedName("allow_user_to_use_pinelab_devices_for_upi_payment")
    var allowUserToUsePinelabDevicesForUpiPayment: Boolean,
    @SerializedName("allow_rapid_booking_flow_by_selecting_seats")
    var allowRapidBookingFlowBySelectingSeats: Boolean = false,
    @SerializedName("rapid_booking_with_mot_coupon_in_ts_app")
    var rapidBookingWithMotCouponInTsApp: Boolean? = false,
    @SerializedName("apply_role_or_branch_discount_at_time_of_booking")
    var applyRoleOrBranchDiscountAtTimeOfBooking: Boolean? = false,
    @SerializedName("restrict_or_hide_skip_verification_option_in_ts_app")
    var restrictOrHideSkipVerificationOptionInTsApp: Boolean = false,
    @SerializedName("remove_pre_selection_option_in_the_booking_when_the_phone_is_permanent_phone")
    var removePreSelectionOptionInTheBooking: Boolean = false,
    @SerializedName("phone_block_release_time")
    var phoneBlockReleaseTime: String?,
    @SerializedName("release_time_policies_options")
    var releaseTimePoliciesOptions: String,
    @SerializedName("allow_edit_fare")
    val isAllowedEditFare: Boolean,
    @SerializedName("route_wise_booking_memo_for_ts_app")
    var routeWiseBookingMemoForTsApp: Boolean = false,
    @SerializedName("allow_booking_for_alloted_services")
    var allowBookingForAllotedServices: Boolean = false,
    val is_confirm_ota_booking: Boolean? = false,
    @SerializedName("allow_to_view_past_date_services")
    val allowToViewPastDateServices: Boolean? = false,
    @SerializedName("allow_to_view_departed_services")
    val allowToViewDepartedServices: Boolean? = false,
    @SerializedName("allow_user_to_update_remarks_multiple_times")
    var allowUserToUpdateRemarksMultipleTimes: Boolean? = false,
    @SerializedName("show_update_remarks_link_in_the_ticket_search")
    var showUpdateRemarksLinkInTheTicketSearch: Boolean? = false,
    @SerializedName("allow_to_do_next_and_previous_dates_services")
    var allowToDoNextAndPreviousDatesServices: Boolean? = false,
    @SerializedName("enable_new_owner_dashboard_with_business_metrics")
    var enableNewOwnerDashboardWithBusinessMetrics: Boolean? = false,
    @SerializedName("allow_to_do_fare_customization_for_seat_types")
    var allowToDoFareCustomizationForSeatTypes: Boolean? = false,
    @SerializedName("hide_commission_and_tieup_commission_in_route_level")
    var hideCommissionAndTieupCommissionInRouteLevel: Boolean? = false,
    @SerializedName("allow_to_update_fare_from_reservation_level_with_templates")
    var allowToUpdateFareTemplates: Boolean? = false,
    @SerializedName("allow_to_apply_discount_on_booking_page_with_percentage_values")
    var allowToApplyDiscountOnBookingPageWithPercentage: Boolean? = false,
    @SerializedName("show_countries_isd_codes_selection")
    var showCountriesIsdCodesSelection: Boolean? = false,
    @SerializedName("ezetap_user_name")
    var ezetapUserName: String? = "",
    @SerializedName("api_key")
    var ezetapApiKey: String? = "",
    @SerializedName("is_ezetap_enabled_in_ts_app")
    var isEzetapEnabledInTsApp: Boolean,
    @SerializedName("allow_to_show_frequent_traveller_tag")
    val allowToShowFrequentTravellerTag: Boolean = false,
    @SerializedName("chart_shared_privilege")
    val chartSharedPrivilege: MutableList<ChartSharedPrivilege>? = null,
    @SerializedName("agent_recharge_pg_type")
    val agentRechargePgType: ArrayList<PgData> = arrayListOf(),
    @SerializedName("recharge_types")
    val rechargeTypes: RechargeTypes? = null,
    @SerializedName("configured_amount_type")
    var configuredAmountType: String? = null,
    @SerializedName("payment_status_report")
    var paymentStatusReport: Boolean? = null,
    @SerializedName("allow_to_show_new_flow_in_ts_app")
    val allowToShowNewFlowInTsApp: Boolean? = false,
    @SerializedName("allow_to_add_blacklist_numbers")
    var allowToAddBlacklistNumbers: Boolean,
    @SerializedName("allow_multiple_cities_option_in_hubs")
    var allowMultipleCitiesOptionInHubs: Boolean = false,
    @SerializedName("manage_routes_in_ts_app")
    var manageRoutesInTsApp: Boolean = false,
    @SerializedName("modify_routes_in_ts_app")
    var modifyRoutesInTsApp: Boolean = false,
    @SerializedName("meal_report")
    var mealReport: Boolean,
    @SerializedName("merge_bus_on_ts_app")
    var mergeBusOnTsApp: Boolean,
    @SerializedName("enable_coach_level_reporting") // enable_coach_level_reporting
    var enableCoachLevelReporting: Boolean =  false,
    @SerializedName("current_coach_layout")
    var currentCoachLayout: String?,
    @SerializedName("allow_to_move_specific_seats_related_to_a_pnr")
    var allowToMoveSpecificSeatsRelatedToAPnr: Boolean? = false,
    @SerializedName("allow_upi_for_direct_pg_booking_for_agents")
    var allowUpiForDirectPgBookingForAgents: Boolean,
    @SerializedName("is_paytm_pos_enabled_in_ts_app")
    var isPaytmPosEnabled: Boolean,
    @SerializedName("allow_to_show_self_trip_audit_form_under_pickup_chart")
    var allowToShowSelfTripAuditFormUnderPickupChart: Boolean?= false,
    @SerializedName("allow_to_apply_current_user_role_branch_discount")
    var allowToApplyCurrentUserRoleBranchDiscount: Boolean,
    @SerializedName("allow_pin_based_actions")
    var allowPinBasedActions: Boolean? = false,
    @SerializedName("pin_based_action_privileges")
    var pinBasedActionPrivileges: PinBasedActionPrivileges? = null,
    @SerializedName("pin_count")
    var pinCount: Int = 6,
    @SerializedName("allow_upi_for_direct_pg_booking_for_branch_user")
    var allowUpiForDirectPgBookingForBranchUser: Boolean,
    @SerializedName("easebuzz_sub_payment_options")
    var easebuzzSubPaymentOptions: EasebuzzSubPaymentOptions?,
    @SerializedName("allow_users_to_skip_fare_template_name_while_creating")
    var allowUsersToSkipFareTemplateNameWhileCreating: Boolean? = false,
    @SerializedName("hide_fare_for_booked_seats_in_the_single_booking_page_for_the_user")
    var hideFareBookedSeats: Boolean? = false,
    @SerializedName("hide_fare_for_blocked_seats_in_the_single_booking_page_for_the_user")
    var blockedSeatsHideFare: Boolean? = false,
    @SerializedName("ts_privileges")
    var tsPrivileges: TsPrivileges? = null,
    @SerializedName("occupancy_forecast_report")
    var occupancyForecastReport: Boolean? = null,
    @SerializedName("agent_payment_options")
    var agentPaymentOptions: List<String>?,
    @SerializedName("agent_sub_payment_options")
    var agentSubPaymentOptions: List<String>?
)






