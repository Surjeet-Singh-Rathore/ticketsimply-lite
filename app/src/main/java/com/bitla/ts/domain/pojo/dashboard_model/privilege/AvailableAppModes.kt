package com.bitla.ts.domain.pojo.dashboard_model.privilege

data class AvailableAppModes(

    val conductor_mode: Boolean,
    val driver_mode: Boolean,
    val checking_inspector_mode: Boolean,
    val dispatch_manager_mode: Boolean,
    val collections_mode: Boolean,
    val allow_reprint: Boolean,
    val allow_PNR_search: Boolean,
    val allow_passenger_search: Boolean,
    val show_reports: Boolean,
    val show_notifications: Boolean,
    val show_settings: Boolean,
    val change_password: Boolean,
    val allow_call: Boolean,
    val allow_sms: Boolean,
    val allow_modify: Boolean,
    val allow_luggage: Boolean,
    val allow_status: Boolean,
    val allow_auto_discount: Boolean,
    val ref_booking_number_for_offline_or_branch: Boolean,
    val ref_booking_number_for_online_or_walkin: Boolean,
    val allow_layout_chart_print: Boolean,
    val show_export_to_csv_option_in_charts: Boolean?= null,
    val allow_to_do_partial_payment: Boolean,
    val allow_booking_restriction: Boolean,
    val allow_bp_dp_fare: Boolean,
    val allow_to_show_pickup_chart_for_past_dates: Boolean,
    val past_days_to_show_in_pickup_chart: String



)