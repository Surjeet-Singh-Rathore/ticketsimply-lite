package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model


import com.google.gson.annotations.SerializedName

data class AvailableAppModes(
    @SerializedName("allow_auto_discount")
    val allowAutoDiscount: Boolean?= false,
    @SerializedName("allow_booking_restriction")
    val allowBookingRestriction: Boolean,
    @SerializedName("allow_bp_dp_fare")
    val allowBpDpFare: Boolean? = false,
    @SerializedName("allow_call")
    val allowCall: Boolean,
    @SerializedName("allow_layout_chart_print")
    val allowLayoutChartPrint: Boolean,
    @SerializedName("allow_luggage")
    val allowLuggage: Boolean,
    @SerializedName("allow_modify")
    val allowModify: Boolean,
    @SerializedName("allow_PNR_search")
    val allowPNRSearch: Boolean? = null,
    @SerializedName("allow_passenger_search")
    val allowPassengerSearch: Boolean,
    @SerializedName("allow_reprint")
    var allowReprint: Boolean,
    @SerializedName("allow_sms")
    val allowSms: Boolean,
    @SerializedName("allow_status")
    val allowStatus: Boolean,
    @SerializedName("allow_to_do_partial_payment")
    val allowToDoPartialPayment: Boolean,
    @SerializedName("allow_to_show_pickup_chart_for_past_dates")
    val allowToShowPickupChartForPastDates: Boolean,
    @SerializedName("change_password")
    val changePassword: Boolean,
    @SerializedName("checking_inspector_mode")
    val checkingInspectorMode: Boolean,
    @SerializedName("collections_mode")
    val collectionsMode: Boolean,
    @SerializedName("conductor_mode")
    val conductorMode: Boolean,
    @SerializedName("dispatch_manager_mode")
    val dispatchManagerMode: Boolean,
    @SerializedName("driver_mode")
    val driverMode: Boolean,
    @SerializedName("past_days_to_show_in_pickup_chart")
    val pastDaysToShowInPickupChart: String,
    @SerializedName("ref_booking_number_for_offline_or_branch")
    val refBookingNumberForOfflineOrBranch: Boolean,
    @SerializedName("ref_booking_number_for_online_or_walkin")
    val refBookingNumberForOnlineOrWalkin: Boolean,
    @SerializedName("report_mode")
    val reportMode: Boolean,
    @SerializedName("show_export_to_csv_option_in_charts")
    val showExportToCsvOptionInCharts: Boolean,
    @SerializedName("show_notifications")
    val showNotifications: Boolean,
    @SerializedName("show_reports")
    val showReports: Boolean,
    @SerializedName("show_settings")
    val showSettings: Boolean,
    @SerializedName("boarded_status")
    val boarded_status: Boolean,
    @SerializedName("missing_status")
    val missing_status: Boolean,
    @SerializedName("dropped_off_status")
    val dropped_off_status: Boolean,
    @SerializedName("no_show_status")
    val no_show_status: Boolean,
    @SerializedName("unboarded_status")
    val unboarded_status: Boolean,
    @SerializedName("yet_to_board_status")
    val yet_to_board_status: Boolean,
    @SerializedName("allow_user_to_change_the_boarding_status_only_once")
    val allow_user_to_change_the_the_boarding_status_only_once: Boolean = false,
    @SerializedName("allow_user_to_change_the_check_in_status_to_boarded_status_only")
    val allow_user_to_change_the_check_in_status_to_boarded_status_only: Boolean = false,
    @SerializedName("check_in_status")
    val check_in_status: Boolean ? = false,





)