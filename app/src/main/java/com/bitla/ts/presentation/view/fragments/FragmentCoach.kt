//package com.bitla.ts.view.fragments
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.res.ColorStateList
//import android.graphics.Color
//import android.graphics.Typeface
//import android.os.Bundle
//import android.util.Log
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.fragment.app.Fragment
//import com.bitla.ts.domain.pojo.booking.SeatStatusData
//import com.bitla.ts.utils.common.getSelectedSeats
//import com.bitla.ts.R
//import com.bitla.ts.databinding.LayoutCoachViewBinding
//import com.bitla.ts.listener.OnSeatSelectionListener
//import com.bitla.ts.domain.pojo.service_details_response.Body
//import com.bitla.ts.domain.pojo.service_details_response.DriverPosition
//import com.bitla.ts.domain.pojo.service_details_response.SeatDetail
//import com.bitla.ts.view.activity.CoachActivity
//import gone
//import visible
//import java.util.*
//import kotlin.collections.ArrayList
//
//
//class FragmentCoach : Fragment(), View.OnClickListener, CoachActivity.CoachDataInterface {
//    private val TAG: String = FragmentCoach::class.java.simpleName
//    private var selectedFilterPrice: String = ""
//    private var isSocialDistancingGuaranteed: Boolean = false
//    private var selectedTabColor: String? = ""
//    private var upperAvalibility: Boolean? = false
//    var seatInfoDetails = ArrayList<SeatDetail>()
//    var seatInfoL = ArrayList<SeatDetail>()
//    var seatInfoU = ArrayList<SeatDetail>()
//    var selectedSeatDetails = ArrayList<SeatDetail>()
//    var selectedSeatColor: String = ""
//    var buttonTextColor: Int = 0
//    private var lowerAvailableSeat = 0
//    private var upperAvailableSeat = 0
//
//    private var totalSeats = mutableListOf<SeatDetail>()
//
//    private lateinit var onSeatSelectionListener: OnSeatSelectionListener
//
//    var coachLayoutJsonUTemp = ArrayList<ArrayList<SeatDetail>>()
//    var coachLayoutJsonLTemp = ArrayList<ArrayList<SeatDetail>>()
//    private var response = Body()
//
//    private var hasUpperBirth: Boolean = false
//    private var maxcolid = 0
//    private var rows: Int = 0
//    private lateinit var chkAllSeats: CheckBox
//    private lateinit var layoutBlockAllSeats: LinearLayout
//
//    private lateinit var lowertab: Button
//    private lateinit var uppertab: Button
//    private var STATUS_AVAILABLE = 0
//    private lateinit var binding: LayoutCoachViewBinding
//
//    companion object {
//        const val LOWER_STATUS_AVAILABLE = 1
//        const val UPPER_STATUS_AVAILABLE = 3
//        const val STATUS_BOOKED = 2
//        const val STATUS_SOCIAL_DISTANCING = 4
//        const val STATUS_BLOCKED = 5
//    }
//
//
//    // TS-operator
//    private var data =
//        "{\"all_fare_details\":[1399,1999,1999,1799,1799],\"arr_time\":\"6:00\",\"available_seats\":21,\"bus_type\":\"2+1, Air Suspension Sleeper/Seater,AC\",\"cancellation_policies\":[{\"cancellation_policy_id\":28,\"percent\":50,\"time_limit_from\":\"0 hours\",\"time_limit_to\":\"12 hours\"},{\"cancellation_policy_id\":29,\"percent\":75,\"time_limit_from\":\"12 hours\",\"time_limit_to\":\"24 hours\"}],\"coach_details\":{\"available_seats\":21,\"driver_position\":\"Left\",\"no_of_cols\":7,\"no_of_rows\":14,\"seat_details\":[{\"available\":false,\"col_id\":2,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":1,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":3,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":1,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":4,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":1,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":5,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":1,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":6,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":1,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":7,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":1,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":8,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":1,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":9,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":1,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":10,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":1,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":2,\"discount_amount\":0.0,\"fare\":1999.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":1999.0,\"min_fare\":1999.0,\"net_amount\":1999.0,\"number\":\"A\",\"row_id\":2,\"rowSpan\":2,\"type\":\"Side Upper Berth\"},{\"available\":false,\"col_id\":3,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":2,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":4,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"L1\",\"row_id\":2,\"rowSpan\":2,\"type\":\"Side Lower Berth\"},{\"available\":false,\"col_id\":5,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":2,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":6,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":2,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":7,\"discount_amount\":0.0,\"fare\":1399.0,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":true,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":1399.0,\"min_fare\":1399.0,\"net_amount\":1399.0,\"number\":\"2\",\"row_id\":2,\"rowSpan\":1,\"type\":\"Semi Sleeper\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":8,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"1\",\"row_id\":2,\"rowSpan\":1,\"type\":\"Semi Sleeper\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":9,\"discount_amount\":0.0,\"fare\":1799.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":1799.0,\"min_fare\":1799.0,\"net_amount\":1799.0,\"number\":\"B\",\"row_id\":2,\"rowSpan\":2,\"type\":\"Double Upper Berth\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":10,\"discount_amount\":0.0,\"fare\":1799.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":1799.0,\"min_fare\":1799.0,\"net_amount\":1799.0,\"number\":\"C\",\"row_id\":2,\"rowSpan\":2,\"type\":\"Double Upper Berth\"},{\"available\":false,\"col_id\":3,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":3,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":5,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":3,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":6,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":3,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":7,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"4\",\"row_id\":3,\"rowSpan\":1,\"type\":\"Semi Sleeper\"},{\"available\":false,\"background_color\":\"#FF65BC\",\"col_id\":8,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":true,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"3\",\"row_id\":3,\"rowSpan\":1,\"type\":\"Semi Sleeper\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":2,\"discount_amount\":0.0,\"fare\":1999.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":1999.0,\"min_fare\":1999.0,\"net_amount\":1999.0,\"number\":\"D\",\"row_id\":4,\"rowSpan\":2,\"type\":\"Side Upper Berth\"},{\"available\":false,\"col_id\":3,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":4,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":4,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"L2\",\"row_id\":4,\"rowSpan\":2,\"type\":\"Side Lower Berth\"},{\"available\":false,\"col_id\":5,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":4,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":6,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":4,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":7,\"discount_amount\":0.0,\"fare\":1399.0,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":true,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":1399.0,\"min_fare\":1399.0,\"net_amount\":1399.0,\"number\":\"6\",\"row_id\":4,\"rowSpan\":1,\"type\":\"Semi Sleeper\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":8,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"5\",\"row_id\":4,\"rowSpan\":1,\"type\":\"Semi Sleeper\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":9,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":false,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"E\",\"row_id\":4,\"rowSpan\":2,\"type\":\"Double Upper Berth\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":10,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":false,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"F\",\"row_id\":4,\"rowSpan\":2,\"type\":\"Double Upper Berth\"},{\"available\":false,\"col_id\":3,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":5,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":5,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":5,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":6,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":5,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":7,\"discount_amount\":0.0,\"fare\":1399.0,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":true,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":1399.0,\"min_fare\":1399.0,\"net_amount\":1399.0,\"number\":\"8\",\"row_id\":5,\"rowSpan\":1,\"type\":\"Semi Sleeper\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":8,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"7\",\"row_id\":5,\"rowSpan\":1,\"type\":\"Semi Sleeper\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":2,\"discount_amount\":0.0,\"fare\":1999.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":1999.0,\"min_fare\":1999.0,\"net_amount\":1999.0,\"number\":\"G\",\"row_id\":6,\"rowSpan\":2,\"type\":\"Side Upper Berth\"},{\"available\":false,\"col_id\":3,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":6,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":4,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"L3\",\"row_id\":6,\"rowSpan\":2,\"type\":\"Side Lower Berth\"},{\"available\":false,\"col_id\":5,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":6,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":6,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":6,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":7,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"10\",\"row_id\":6,\"rowSpan\":1,\"type\":\"Semi Sleeper\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":8,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"9\",\"row_id\":6,\"rowSpan\":1,\"type\":\"Semi Sleeper\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":9,\"discount_amount\":0.0,\"fare\":1799.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":1799.0,\"min_fare\":1799.0,\"net_amount\":1799.0,\"number\":\"H\",\"row_id\":6,\"rowSpan\":2,\"type\":\"Double Upper Berth\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":10,\"discount_amount\":0.0,\"fare\":1799.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":1799.0,\"min_fare\":1799.0,\"net_amount\":1799.0,\"number\":\"I\",\"row_id\":6,\"rowSpan\":2,\"type\":\"Double Upper Berth\"},{\"available\":false,\"col_id\":3,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":7,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":5,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":7,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":6,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":7,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"background_color\":\"#FF65BC\",\"col_id\":7,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":true,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"12\",\"row_id\":7,\"rowSpan\":1,\"type\":\"Semi Sleeper\"},{\"available\":false,\"background_color\":\"#FF65BC\",\"col_id\":8,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":true,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"11\",\"row_id\":7,\"rowSpan\":1,\"type\":\"Semi Sleeper\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":2,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":false,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"J\",\"row_id\":8,\"rowSpan\":2,\"type\":\"Side Upper Berth\"},{\"available\":false,\"col_id\":3,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":8,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":4,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"L4\",\"row_id\":8,\"rowSpan\":2,\"type\":\"Side Lower Berth\"},{\"available\":false,\"col_id\":5,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":8,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":6,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":8,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"background_color\":\"#FF65BC\",\"col_id\":7,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":true,\"is_lower\":true,\"isReservable\":false,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"L7\",\"row_id\":8,\"rowSpan\":2,\"type\":\"Double Lower Berth\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":8,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"L8\",\"row_id\":8,\"rowSpan\":2,\"type\":\"Double Lower Berth\"},{\"available\":false,\"background_color\":\"#FF65BC\",\"col_id\":9,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":true,\"is_lower\":false,\"isReservable\":false,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"K\",\"row_id\":8,\"rowSpan\":2,\"type\":\"Double Upper Berth\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":10,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":false,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"L\",\"row_id\":8,\"rowSpan\":2,\"type\":\"Double Upper Berth\"},{\"available\":false,\"col_id\":3,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":9,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":5,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":9,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":6,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":9,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":2,\"discount_amount\":0.0,\"fare\":1999.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":1999.0,\"min_fare\":1999.0,\"net_amount\":1999.0,\"number\":\"M\",\"row_id\":10,\"rowSpan\":2,\"type\":\"Side Upper Berth\"},{\"available\":false,\"col_id\":3,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":10,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"background_color\":\"#A15241\",\"col_id\":4,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":true,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"L5\",\"row_id\":10,\"rowSpan\":2,\"type\":\"Side Lower Berth\"},{\"available\":false,\"col_id\":5,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":10,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":6,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":10,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":7,\"discount_amount\":0.0,\"fare\":1799.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":1799.0,\"min_fare\":1799.0,\"net_amount\":1799.0,\"number\":\"L9\",\"row_id\":10,\"rowSpan\":2,\"type\":\"Double Lower Berth\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":8,\"discount_amount\":0.0,\"fare\":1799.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":1799.0,\"min_fare\":1799.0,\"net_amount\":1799.0,\"number\":\"L10\",\"row_id\":10,\"rowSpan\":2,\"type\":\"Double Lower Berth\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":9,\"discount_amount\":0.0,\"fare\":1799.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":1799.0,\"min_fare\":1799.0,\"net_amount\":1799.0,\"number\":\"N\",\"row_id\":10,\"rowSpan\":2,\"type\":\"Double Upper Berth\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":10,\"discount_amount\":0.0,\"fare\":1799.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":1799.0,\"min_fare\":1799.0,\"net_amount\":1799.0,\"number\":\"O\",\"row_id\":10,\"rowSpan\":2,\"type\":\"Double Upper Berth\"},{\"available\":false,\"col_id\":3,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":11,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":5,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":11,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":6,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":11,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":2,\"discount_amount\":0.0,\"fare\":1999.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":1999.0,\"min_fare\":1999.0,\"net_amount\":1999.0,\"number\":\"P\",\"row_id\":12,\"rowSpan\":2,\"type\":\"Side Upper Berth\"},{\"available\":false,\"col_id\":3,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":12,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":4,\"discount_amount\":0.0,\"fare\":1999.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":1999.0,\"min_fare\":1999.0,\"net_amount\":1999.0,\"number\":\"L6\",\"row_id\":12,\"rowSpan\":2,\"type\":\"Side Lower Berth\"},{\"available\":false,\"col_id\":5,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":12,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":6,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":12,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":7,\"discount_amount\":0.0,\"fare\":1799.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":1799.0,\"min_fare\":1799.0,\"net_amount\":1799.0,\"number\":\"L11\",\"row_id\":12,\"rowSpan\":2,\"type\":\"Double Lower Berth\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":8,\"discount_amount\":0.0,\"fare\":1799.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":1799.0,\"min_fare\":1799.0,\"net_amount\":1799.0,\"number\":\"L12\",\"row_id\":12,\"rowSpan\":2,\"type\":\"Double Lower Berth\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":9,\"discount_amount\":0.0,\"fare\":1799.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":1799.0,\"min_fare\":1799.0,\"net_amount\":1799.0,\"number\":\"Q\",\"row_id\":12,\"rowSpan\":2,\"type\":\"Double Upper Berth\"},{\"available\":true,\"background_color\":\"#FFFFFF\",\"col_id\":10,\"discount_amount\":0.0,\"fare\":1799.0,\"isBerth\":true,\"isBreak\":false,\"is_gangway\":false,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":false,\"isReservable\":true,\"is_seat\":false,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":true,\"max_fare\":1799.0,\"min_fare\":1799.0,\"net_amount\":1799.0,\"number\":\"R\",\"row_id\":12,\"rowSpan\":2,\"type\":\"Double Upper Berth\"},{\"available\":false,\"col_id\":3,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":13,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":5,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":13,\"rowSpan\":1,\"type\":\"Gangway\"},{\"available\":false,\"col_id\":6,\"isBerth\":false,\"isBreak\":false,\"is_gangway\":true,\"is_gents_seat\":false,\"is_horizontal\":false,\"is_ladies_seat\":false,\"is_lower\":true,\"isReservable\":false,\"is_seat\":true,\"is_selected\":false,\"is_social_distancing\":false,\"isUpper\":false,\"max_fare\":0.0,\"min_fare\":0.0,\"number\":\"\",\"row_id\":13,\"rowSpan\":1,\"type\":\"Gangway\"}],\"total_seats\":42},\"convenience_charge_percent\":0.0,\"cost\":\"1469,2099,1889\",\"coupon_code\":\"\",\"dep_time\":\"22:45\",\"destination\":{\"id\":17,\"name\":\"Faizabad\"},\"do_not_apply_eticket_discount\":true,\"duration\":\"07:15\",\"e_ticket_discount\":\"0.0 %\",\"id_types_arr\":[[\"Pan Card\",1.0,0.0,0.0],[\"D/L\",2.0,0.0,0.0],[\"Passport\",3.0,0.0,0.0],[\"Voter ID\",4.0,0.0,0.0],[\"Aadhar Card\",5.0,0.0,0.0],[\"Ration Card\",6.0,0.0,0.0],[\"RUT\",7.0,0.0,0.0],[\"DNI\",8.0,0.0,0.0],[\"CI\",9.0,0.0,0.0],[\"NIC\",10.0,0.0,0.0]],\"intr_legend_details\":{\"Available\":{\"Available\":\"#FFFFFF\"},\"Blocked\":{\"Blocked\":\"#C3CF3C\"},\"Blocked_L\":{\"Blocked(L)\":\"#C3CF3C, #FF65BC\"},\"E_Quota\":{\"E-Quota\":\"#00AA00\"},\"Offline_Agent_C\":{\"Offline Agent(C)\":\"#A0248B\"},\"Onhold\":{\"Onhold\":\"#00AEF0\"},\"Onhold_Ladies\":{\"Onhold Ladies\":\"#00AEF0, #FF65BC\"},\"Quota\":{\"Quota\":\"#E94F4F\"},\"Reserved_Api\":{\"Reserved(Api)\":\"#0D848D\"},\"Reserved_Branch\":{\"Reserved(Branch)\":\"#A0248B\"},\"Reserved_E_tic\":{\"Reserved(E-tic)\":\"#A15241\"},\"Reserved_L\":{\"Reserved(L)\":\"#FF65BC\"},\"Reserved_OA\":{\"Reserved(OA)\":\"#FCD0C1\"},\"Selected\":{\"Selected\":\"#FFC526\"},\"VIP\":{\"VIP\":\"#FFCCCC\"}},\"is_child_fare\":false,\"is_coach_layout_hide\":false,\"is_day_visit_lead_privilege\":false,\"is_drop_off_lead_privilege\":false,\"is_gst_applicable\":true,\"is_hotel_lead_privilege\":false,\"is_pick_up_lead_privilege\":false,\"is_service_tax_applicable\":true,\"social_distancing_guaranteed\":false,\"legend_details\":{\"Reserved(Branch)\":\"#A0248B\",\"Reserved(OA)\":\"#FCD0C1\",\"Reserved(Api)\":\"#0D848D\",\"Reserved(E-tic)\":\"#A15241\",\"Reserved(L)\":\"#FF65BC\",\"Quota\":\"#E94F4F\",\"E-Quota\":\"#00AA00\",\"Available\":\"#FFFFFF\",\"Blocked\":\"#C3CF3C\",\"Onhold\":\"#00AEF0\",\"Onhold Ladies\":\"#00AEF0, #FF65BC\",\"Blocked(L)\":\"#C3CF3C, #FF65BC\",\"Offline Agent(C)\":\"#A0248B\",\"VIP\":\"#FFCCCC\",\"Selected\":\"#FFC526\"},\"name\":\"\",\"nationality_list\":[[\"India\",0.0]],\"number\":\"Jhunjhunu-Gorakhpur2\",\"origin\":{\"id\":2,\"name\":\"Agra\"},\"pay_gay_type\":[{\"pay_gay_type_id\":\"32-6-12-UPI\",\"pay_gay_type_name\":\"Upi\",\"transaction_charges\":0.0,\"transaction_type\":\"NONE\"},{\"pay_gay_type_id\":\"32-13-1-CREDIT_CARD\",\"pay_gay_type_name\":\"Credit Card\",\"transaction_charges\":0.0,\"transaction_type\":\"NONE\"},{\"pay_gay_type_id\":\"32-6-2-DEBIT_CARD\",\"pay_gay_type_name\":\"Debit Card\",\"transaction_charges\":0.0,\"transaction_type\":\"NONE\"},{\"pay_gay_type_id\":\"32-1-3-NET_BANKING\",\"pay_gay_type_name\":\"Net Banking\",\"transaction_charges\":0.0,\"transaction_type\":\"NONE\"},{\"pay_gay_type_id\":\"32-6-4-PAYTM\",\"pay_gay_type_name\":\"Paytm Wallet\",\"transaction_charges\":0.0,\"transaction_type\":\"NONE\"},{\"pay_gay_type_id\":\"32-5-4-MOBIKWIK\",\"pay_gay_type_name\":\"Mobikwik Wallet\",\"transaction_charges\":0.0,\"transaction_type\":\"NONE\"}],\"phone_blocking_hour\":\"\",\"route_id\":49,\"stage_details\":[{\"city\":\"Agra\",\"city_id\":2,\"contact_numbers\":\"7023225366 7023365364\",\"contact_persons\":\"Gk travels\",\"id\":377,\"is_next_day\":\"NO_LABEL\",\"is_pick_up\":false,\"name\":\"Namner Chauraha Near Eid gah bus Stand\",\"pin_code\":\"000000\",\"state\":33,\"time\":\"22:45\",\"travel_date\":\"23/04/2021\",\"type\":0},{\"city\":\"Faizabad\",\"city_id\":17,\"contact_numbers\":\"7835857000 9680227000\",\"contact_persons\":\"GK Travels\",\"id\":332,\"is_next_day\":\"NO_LABEL\",\"is_pick_up\":false,\"name\":\"Near Devkali Bypass Panchwati Hotel\",\"pin_code\":\"000000\",\"state\":33,\"time\":\"06:00\",\"travel_date\":\"24/04/2021\",\"type\":1}],\"status\":\"Update\",\"travel_date\":\"23/04/2021\",\"via\":[\"Lucknow\",\" Faizabad\",\" Basti\",\" Khalilabad\",\" Nawalgarh\",\" Sikar\",\" Ringas\",\" Jaipur (Rajasthan)\",\" Mehandipur Balaji\",\" Bharatpur\",\" Agra\",\" \"]}"
//
//    //New data
//    private var data1 =
//        "{\"number\":\"Android Auto SD Multihop Enable\",\"name\":\"TX Charges\",\"origin\":{\"id\":18,\"name\":\"Bangalore\"},\"destination\":{\"id\":21,\"name\":\"Ananthapur\"},\"travel_date\":\"14/04/2021\",\"route_id\":538,\"phone_blocking_hour\":\"\",\"available_seats\":33,\"dep_time\":\"09:00 PM\",\"pay_gay_type\":[{\"pay_gay_type_id\":\"32-6-12-UPI\",\"pay_gay_type_name\":\"Upi\",\"transaction_type\":\"PERCENTAGE\",\"transaction_charges\":5.0},{\"pay_gay_type_id\":\"32-13-1-CREDIT_CARD\",\"pay_gay_type_name\":\"Credit Card\",\"transaction_type\":\"PERCENTAGE\",\"transaction_charges\":5.0},{\"pay_gay_type_id\":\"32-6-2-DEBIT_CARD\",\"pay_gay_type_name\":\"Debit Card\",\"transaction_type\":\"PERCENTAGE\",\"transaction_charges\":5.0},{\"pay_gay_type_id\":\"32-1-3-NET_BANKING\",\"pay_gay_type_name\":\"Net Banking\",\"transaction_type\":\"PERCENTAGE\",\"transaction_charges\":5.0},{\"pay_gay_type_id\":\"32-6-4-PAYTM\",\"pay_gay_type_name\":\"Paytm Wallet\",\"transaction_type\":\"PERCENTAGE\",\"transaction_charges\":5.0},{\"pay_gay_type_id\":\"32-5-4-MOBIKWIK\",\"pay_gay_type_name\":\"Mobikwik Wallet\",\"transaction_type\":\"PERCENTAGE\",\"transaction_charges\":5.0}],\"arr_time\":\"12:30 AM\",\"duration\":\"03:30\",\"bus_type\":\"2+2, Volvo Seater,AC, Video\",\"via\":[\"Ananthapur\",\" Kurnool\",\" \"],\"cost\":\"10000\",\"coupon_code\":\"\",\"is_child_fare\":false,\"coach_details\":{\"no_of_rows\":10,\"no_of_cols\":6,\"total_seats\":36,\"available_seats\":33,\"driver_position\":\"Left\",\"coach_number\":null,\"seat_details\":[{\"available\":false,\"is_ladies_seat\":false,\"is_gents_seat\":true,\"number\":\"2\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":1,\"col_id\":1,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#E94F4F\",\"is_social_distancing\":false,\"boarded_status\":0,\"passenger_details\":{\"name\":\"Xxxx\",\"age\":0,\"gender\":\"M\",\"origin_id\":18,\"origin_name\":\"Bangalore\",\"destination_id\":19,\"destination_name\":\"Hyderabad\",\"from_to\":\"From Bangalore To Hyderabad\",\"blocking_time\":null,\"seat_fare\":\"\",\"ticket_no\":\"921282 \",\"booking_fare\":\"\",\"phone_num\":\"\",\"boarding_stage\":\"Boarding stage: Madiwala  - 09:00 PM (1st Day)\",\"landmark\":\"Madiwala\",\"drop_off_stage\":\"Mehdipatnam - 06:30 AM (2nd Day)\",\"seat_no\":\"2\",\"booked_by\":\"Souvik Chaudhury on 14/04/2021 11:11 AM\",\"remarks\":\"Via-Mobility App\",\"can_cancel\":false,\"can_cancel_ticket_for_user\":false,\"can_shift_ticket\":true,\"is_phone_block\":false,\"no_of_seats\":1,\"seat_numbers\":\"2\",\"dep_time\":\"15:30\",\"travel_date\":\"14/04/2021\",\"booked_date\":\"14/04/2021\",\"status\":0,\"is_his_booking\":false,\"booked_by_id\":500,\"travel_branch_id\":94}},{\"available\":false,\"is_ladies_seat\":false,\"is_gents_seat\":true,\"number\":\"1\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":1,\"col_id\":2,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#005E38\",\"is_social_distancing\":true},{\"available\":false,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"\",\"type\":\"Gangway\",\"fare\":null,\"row_id\":1,\"col_id\":3,\"is_seat\":true,\"is_gangway\":true,\"is_horizontal\":false,\"min_fare\":0.0,\"max_fare\":0.0,\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"5\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":1,\"col_id\":4,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"6\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":1,\"col_id\":5,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":false,\"is_ladies_seat\":false,\"is_gents_seat\":true,\"number\":\"3\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":2,\"col_id\":1,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#005E38\",\"is_social_distancing\":true},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"4\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":2,\"col_id\":2,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":false,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"\",\"type\":\"Gangway\",\"fare\":null,\"row_id\":2,\"col_id\":3,\"is_seat\":true,\"is_gangway\":true,\"is_horizontal\":false,\"min_fare\":0.0,\"max_fare\":0.0,\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"8\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":2,\"col_id\":4,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"7\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":2,\"col_id\":5,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"12\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":3,\"col_id\":1,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"11\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":3,\"col_id\":2,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":false,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"\",\"type\":\"Gangway\",\"fare\":null,\"row_id\":3,\"col_id\":3,\"is_seat\":true,\"is_gangway\":true,\"is_horizontal\":false,\"min_fare\":0.0,\"max_fare\":0.0,\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"10\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":3,\"col_id\":4,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"9\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":3,\"col_id\":5,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"13\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":4,\"col_id\":1,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"14\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":4,\"col_id\":2,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":false,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"\",\"type\":\"Gangway\",\"fare\":null,\"row_id\":4,\"col_id\":3,\"is_seat\":true,\"is_gangway\":true,\"is_horizontal\":false,\"min_fare\":0.0,\"max_fare\":0.0,\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"15\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":4,\"col_id\":4,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"16\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":4,\"col_id\":5,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"20\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":5,\"col_id\":1,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"19\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":5,\"col_id\":2,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":false,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"\",\"type\":\"Gangway\",\"fare\":null,\"row_id\":5,\"col_id\":3,\"is_seat\":true,\"is_gangway\":true,\"is_horizontal\":false,\"min_fare\":0.0,\"max_fare\":0.0,\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"18\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":5,\"col_id\":4,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"17\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":5,\"col_id\":5,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"21\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":6,\"col_id\":1,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"22\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":6,\"col_id\":2,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":false,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"\",\"type\":\"Gangway\",\"fare\":null,\"row_id\":6,\"col_id\":3,\"is_seat\":true,\"is_gangway\":true,\"is_horizontal\":false,\"min_fare\":0.0,\"max_fare\":0.0,\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"23\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":6,\"col_id\":4,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"24\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":6,\"col_id\":5,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"28\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":7,\"col_id\":1,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"27\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":7,\"col_id\":2,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":false,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"\",\"type\":\"Gangway\",\"fare\":null,\"row_id\":7,\"col_id\":3,\"is_seat\":true,\"is_gangway\":true,\"is_horizontal\":false,\"min_fare\":0.0,\"max_fare\":0.0,\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"26\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":7,\"col_id\":4,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"25\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":7,\"col_id\":5,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"29\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":8,\"col_id\":1,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"30\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":8,\"col_id\":2,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":false,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"\",\"type\":\"Gangway\",\"fare\":null,\"row_id\":8,\"col_id\":3,\"is_seat\":true,\"is_gangway\":true,\"is_horizontal\":false,\"min_fare\":0.0,\"max_fare\":0.0,\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"31\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":8,\"col_id\":4,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"32\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":8,\"col_id\":5,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"36\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":9,\"col_id\":1,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"35\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":9,\"col_id\":2,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":false,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"\",\"type\":\"Gangway\",\"fare\":null,\"row_id\":9,\"col_id\":3,\"is_seat\":true,\"is_gangway\":true,\"is_horizontal\":false,\"min_fare\":0.0,\"max_fare\":0.0,\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"34\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":9,\"col_id\":4,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false},{\"available\":true,\"is_ladies_seat\":false,\"is_gents_seat\":false,\"number\":\"33\",\"type\":\"Semi Sleeper\",\"fare\":10000.0,\"row_id\":9,\"col_id\":5,\"is_seat\":true,\"is_gangway\":false,\"is_horizontal\":false,\"min_fare\":10000.0,\"max_fare\":10000.0,\"background_color\":\"#FFFFFF\",\"is_social_distancing\":false}]},\"extra_seat_details\":null,\"status\":\"Update\",\"stage_details\":[{\"id\":3868,\"name\":\"Majestic Kbs Near Majestic Hotel\",\"type\":0,\"time\":\"08:00 PM\",\"address\":\"KSRTC bus station\",\"landmark\":\"Bus Stand\",\"city_id\":18,\"city\":\"Bangalore\",\"state\":16,\"contact_numbers\":\"9987456321\",\"contact_persons\":\"yusuf1\",\"is_next_day\":\"NO_LABEL\",\"pin_code\":\"560031\",\"is_pick_up\":false,\"latitude\":null,\"longitude\":null,\"travel_date\":\"14/04/2021\"},{\"id\":3869,\"name\":\"Madiwala\",\"type\":0,\"time\":\"09:00 PM\",\"address\":\"police station\",\"landmark\":\"Madiwala\",\"city_id\":18,\"city\":\"Bangalore\",\"state\":16,\"contact_numbers\":\"999999998\",\"contact_persons\":\"sssss\",\"is_next_day\":\"NO_LABEL\",\"pin_code\":\"560036\",\"is_pick_up\":false,\"latitude\":null,\"longitude\":null,\"travel_date\":\"14/04/2021\"},{\"id\":3870,\"name\":\"hebbal\",\"type\":0,\"time\":\"10:00 PM\",\"address\":\"Gujarat Bank\",\"landmark\":\"Karnavati Nagar\",\"city_id\":18,\"city\":\"Bangalore\",\"state\":16,\"contact_numbers\":\"123\",\"contact_persons\":\"Vinay Patel\",\"is_next_day\":\"NO_LABEL\",\"pin_code\":\"000000\",\"is_pick_up\":false,\"latitude\":null,\"longitude\":null,\"travel_date\":\"14/04/2021\"},{\"id\":3874,\"name\":\"ananthapur \",\"type\":1,\"time\":\"12:30 AM\",\"address\":\"near police station\",\"landmark\":\"Bus Stop\",\"city_id\":21,\"city\":\"Ananthapur\",\"state\":1,\"contact_numbers\":\"98898989898\",\"contact_persons\":\"Unique travels\",\"is_next_day\":\"NO_LABEL\",\"pin_code\":\"000000\",\"is_pick_up\":false,\"latitude\":null,\"longitude\":null,\"travel_date\":\"15/04/2021\"}],\"cancellation_policies\":[{\"cancellation_policy_id\":139,\"percent\":100,\"time_limit_from\":\"0 hours\",\"time_limit_to\":\"6 hours\"},{\"cancellation_policy_id\":140,\"percent\":50,\"time_limit_from\":\"6 hours\",\"time_limit_to\":\"24 hours\"}],\"legend_details\":{\"Reserved(Branch)\":\"#E94F4F\",\"Reserved(OA)\":\"#A0248B\",\"Reserved(Api)\":\"#004A93\",\"Reserved(E-tic)\":\"#FF65BC\",\"Reserved(L)\":\"#fff200\",\"Quota\":\"#005E38\",\"E-Quota\":\"#7373B1\",\"Available\":\"#FFFFFF\",\"Available(L) \":\"#FFC526\",\"Blocked\":\"#00AEF0\",\"Onhold\":\"#5E0607\",\"Onhold Ladies\":\"#5E0607, #fff200\",\"Blocked(L)\":\"#00AEF0, #fff200\",\"VIP\":\"#A0248B\",\"Social Distance Quota\":\"#005E38\",\"Selected\":\"#F47B24\"},\"intr_legend_details\":{\"Reserved_Branch\":{\"Reserved(Branch)\":\"#E94F4F\"},\"Reserved_OA\":{\"Reserved(OA)\":\"#A0248B\"},\"Reserved_Api\":{\"Reserved(Api)\":\"#004A93\"},\"Reserved_E_tic\":{\"Reserved(E-tic)\":\"#FF65BC\"},\"Reserved_L\":{\"Reserved(L)\":\"#fff200\"},\"Quota\":{\"Quota\":\"#005E38\"},\"E_Quota\":{\"E-Quota\":\"#7373B1\"},\"Available\":{\"Available\":\"#FFFFFF\"},\"Available_L\":{\"Available(L) \":\"#FFC526\"},\"Blocked\":{\"Blocked\":\"#00AEF0\"},\"Onhold\":{\"Onhold\":\"#5E0607\"},\"Onhold_Ladies\":{\"Onhold Ladies\":\"#5E0607, #fff200\"},\"Blocked_L\":{\"Blocked(L)\":\"#00AEF0, #fff200\"},\"VIP\":{\"VIP\":\"#A0248B\"},\"Social Distance Quota\":{\"Social Distance Quota\":\"#005E38\"},\"Selected\":{\"Selected\":\"#F47B24\"}},\"is_hotel_lead_privilege\":false,\"is_pick_up_lead_privilege\":false,\"is_drop_off_lead_privilege\":false,\"is_day_visit_lead_privilege\":false,\"is_service_tax_applicable\":false,\"is_gst_applicable\":true,\"is_coach_layout_hide\":false,\"st_percent\":\"5\",\"is_own_route\":true,\"branch_discount_amt\":0.0,\"branch_discount_type\":2,\"convenience_charge_percent\":0.0,\"do_not_apply_eticket_discount\":true,\"id_types_arr\":[[\"Pan Card\",1,0,0],[\"D/L\",2,0,0],[\"Passport\",3,0,0],[\"Voter ID\",4,0,0],[\"Aadhar Card\",5,0,0],[\"Ration Card\",6,0,0],[\"RUT\",7,0,0],[\"DNI\",8,0,0],[\"CI\",9,0,0],[\"Emp ID\",10,0,0]],\"nationality_list\":[[\"India\",0]],\"social_distancing_guaranteed\":true,\"all_fare_details\":[10000.0],\"is_snack_available\":true,\"booking_type_seat_counts\":{\"total_seats_count\":36,\"available_seats_count\":33,\"booked_seats_count\":1,\"phone_booking_count\":0,\"quota_blocked_count\":2}}"
//
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        try {
//            onSeatSelectionListener = activity as OnSeatSelectionListener
//        } catch (e: Exception) {
//            throw ClassCastException("${activity.toString()} must implement onSeatSelectionListener")
//        }
//    }
//
//    override fun onCreateView(inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?): View? {
//       // val root = inflater.inflate(R.layout.layout_coach_view, container, false)
//
//        binding = LayoutCoachViewBinding.inflate(inflater,container,false)
//        val view : View  = binding.root
//
//        lowertab = binding.lowertab
//        uppertab = binding.uppertab
//        chkAllSeats = binding.chkAllSeats
//        layoutBlockAllSeats = binding.layoutBlockAllSeats
//
//        checkAllSeats()
//
//        clickListener()
//
//        return view
//    }
//
//    private fun checkAllSeats() {
//        chkAllSeats.setOnCheckedChangeListener { buttonView, isChecked ->
//            Timber.d(TAG, "isChecked $isChecked")
//            if (::layoutBlockAllSeats.isInitialized) {
//                if (isChecked) {
//                    selectAllSeats()
//                    chkAllSeats.text = getString(R.string.selectedAllSeat)
//                    layoutBlockAllSeats.backgroundTintList =
//                        resources.getColorStateList(R.color.colorPrimary)
//                } else {
//                    selectedSeatDetails.clear()
//                    buildCoachLayout()
//                    chkAllSeats.text = getString(R.string.selectAllSeat)
//                    layoutBlockAllSeats.backgroundTintList =
//                        resources.getColorStateList(R.color.colorDimShadow6)
//                    onSeatSelectionListener.unSelectAllSeats()
//                }
//            }
//        }
//    }
//
//    private fun clickListener() {
//        lowertab.setOnClickListener(this)
//        uppertab.setOnClickListener(this)
//    }
//
//
//    override fun onClick(v: View?) {
//        if (v?.tag != null) {
//            val seatStatusData = v.tag as SeatStatusData
//            if (seatStatusData.status == UPPER_STATUS_AVAILABLE || seatStatusData.status == LOWER_STATUS_AVAILABLE) {
//                seatSelection(v)
//            } else if (seatStatusData.status == STATUS_BOOKED) {
//                Toast.makeText(activity, "This seat is booked.", Toast.LENGTH_SHORT).show()
//            } else if (seatStatusData.status == STATUS_BLOCKED) {
//                seatSelection(v)
//            }
//        } else if (v?.id == R.id.lowertab) {
//            onLowerTabSelection()
//        } else if (v?.id == R.id.uppertab) {
//            onUpperTabSelection()
//        }
//    }
//
//    private fun onUpperTabSelection() {
//        lowertab.setBackgroundResource(R.drawable.layout_rounded_shape_left_unselected)
//        uppertab.setBackgroundResource(R.drawable.layout_rounded_shape_right)
//        uppertab.setTextColor(context?.resources?.getColor(R.color.white)!!)
//        lowertab.setTextColor(context?.resources?.getColor(R.color.colorDimShadow6)!!)
//
//        binding.layoutSeatUpper.visible()
//        binding.layoutSeatlower.gone()
//    }
//
//    private fun onLowerTabSelection() {
//        binding.layoutSeatlower.visible()
//        binding.layoutSeatUpper.gone()
//
//        lowertab.setBackgroundResource(R.drawable.layout_rounded_shape_left_selected)
//        uppertab.setBackgroundResource(R.drawable.layout_rounded_shape_right_unselected)
//        lowertab.setTextColor(context?.resources?.getColor(R.color.white)!!)
//        uppertab.setTextColor(context?.resources?.getColor(R.color.colorDimShadow6)!!)
//    }
//
//    @SuppressLint("ResourceType")
//    private fun serviceDetailsResponse(response: Body) {
//        if (response.origin != null) {
//            this.response = response
//            val columns: Int = response.coachDetails?.noOfCols?.plus(3)!!
//            rows = response.coachDetails?.noOfRows?.plus(4)!!
//            Timber.d("${CoachActivity.TAG} size", "${response.coachDetails?.seatDetails?.size}")
//
//            maxcolid = 0
//            generateSeatTypes()
//            coachlistMake(binding.layoutSeatlower, coachLayoutJsonLTemp, true, maxcolid + 2, rows, response)
//            if (hasUpperBirth) {
//                coachlistMake(binding.layoutSeatUpper,
//                    coachLayoutJsonUTemp,
//                    false,
//                    maxcolid + 2,
//                    rows,
//                    response)
//                binding.loweruppertab.visible()
//            } else {
//                binding.loweruppertab.gone()
//            }
//
//            lowertab.text = context?.getString(R.string.lower)
//            uppertab.text = context?.getString(R.string.upper)
//        }
//        selectedSeatColor = response.legendDetails?.selected.toString()
//        selectedSeatColor = resources.getString(R.color.colorSelected)
//    }
//
//    private fun generateSeatTypes() {
//        var coachLayoutJsonUSubTemp = ArrayList<SeatDetail>()
//        var coachLayoutJsonLSubTemp = ArrayList<SeatDetail>()
//        var uCount = 0
//        var lCount = 0
//        var rowidchckU = 1
//        var rowidchckL = 1
//        for (i in 0..response.coachDetails?.seatDetails?.size?.minus(1)!!) {
//            val isSeatAvailable: Boolean? = response.coachDetails?.seatDetails?.get(i)?.available
//            val type: String? =
//                response.coachDetails?.seatDetails?.get(i)?.type?.toLowerCase(Locale.ENGLISH)
//            val rowid: Int? = response.coachDetails?.seatDetails?.get(i)?.rowId
//            val colid: Int = response.coachDetails?.seatDetails?.get(i)?.colId!!
//            val seatDetail: SeatDetail? = response.coachDetails?.seatDetails?.get(i)
//            var berthTextPos: Boolean = false
//
//
//            if (maxcolid < colid) {
//                maxcolid = colid
//            }
//
//            if (type?.contains("berth") == true || type?.contains("ub") == true || type?.contains("lb") == true || type?.contains(
//                    "window single lower") == true || type?.contains("window single lower") == true) {
//                berthTextPos = true
//                seatDetail!!.isBerth = true
//                seatDetail.rowSpan = 2
//                seatDetail.isSeat = false
//            } else {
//                berthTextPos = false
//                seatDetail!!.isBerth = false
//                seatDetail.rowSpan = 1
//            }
//            seatDetail.isReservable = seatDetail.available
//            seatDetail.isUpper = false
//            if (type?.contains("upper") == true || type?.equals("ub") == true || (!seatDetail.floorType.isNullOrBlank() && seatDetail.floorType?.contains(
//                    "2") == true)) {
//                seatDetail.isUpper = true
//                upperAvalibility = seatDetail.isUpper
//                hasUpperBirth = true
//            }
//            seatDetail.isLower = !seatDetail.isUpper!!
//            seatDetail.isGangway = seatDetail.isGangway == true || type?.contains("Gang",
//                true) == true || type?.contains(".GY", true) == true || type?.contains("Break",
//                true) == true || type?.contains("Un Reservable Seat", true) == true
//            seatDetail.isBreak = type?.equals("Break", true) == true
//            if (seatDetail.isUpper == true || seatDetail.isGangway == true) {
//                if (seatDetail.isReservable == true) {
//                    uCount++
//                }
//                if (rowidchckU == rowid) {
//                    coachLayoutJsonUSubTemp.add(seatDetail)
//                    if (rowid == 0) {
//                        coachLayoutJsonUTemp.add(rowid, coachLayoutJsonUSubTemp)
//                    } else {
//                        coachLayoutJsonUTemp.add(rowid - 1, coachLayoutJsonUSubTemp)
//                    }
//                } else {
//                    rowidchckU = rowid!!
//                    coachLayoutJsonUSubTemp = ArrayList()
//                    coachLayoutJsonUSubTemp.add(seatDetail)
//                    if (rowid == 0) coachLayoutJsonUTemp.add(rowid, coachLayoutJsonUSubTemp)
//                    else coachLayoutJsonUTemp.add(rowid - 1, coachLayoutJsonUSubTemp)
//                }
//
//                if (isSeatAvailable!!) {
//                    upperAvailableSeat++
//                }
//            }
//            if (seatDetail.isLower == true) {
//                if (seatDetail.isReservable == true) lCount++
//                if (rowidchckL == rowid) {
//                    coachLayoutJsonLSubTemp.add(seatDetail)
//                    if (rowid == 0) coachLayoutJsonLTemp.add(rowid, coachLayoutJsonLSubTemp)
//                    else coachLayoutJsonLTemp.add(rowid - 1, coachLayoutJsonLSubTemp)
//                } else {
//                    rowidchckL = rowid!!
//                    if (rowid == 0) coachLayoutJsonLTemp.add(rowid, coachLayoutJsonLSubTemp)
//                    else coachLayoutJsonLTemp.add(rowid - 1, coachLayoutJsonLSubTemp)
//                    coachLayoutJsonLSubTemp = ArrayList()
//                    coachLayoutJsonLSubTemp.add(seatDetail)
//                }
//                if (response.coachDetails!!.noOfRows!!.minus(1) == rowidchckL && !coachLayoutJsonLTemp.contains(
//                        coachLayoutJsonLSubTemp)) {
//                    coachLayoutJsonLTemp.add(coachLayoutJsonLSubTemp)
//                }
//
//                if (isSeatAvailable!!) {
//                    lowerAvailableSeat++
//                }
//
//            }
//
//            if (isSeatAvailable!!) {
//                totalSeats.add(response.coachDetails?.seatDetails!![i])
//            }
//        }
//    }
//
//
//    @SuppressLint("SetTextI18n")
//    fun coachlistMake(layout: ViewGroup,
//        listArray: ArrayList<ArrayList<SeatDetail>>,
//        isLower: Boolean,
//        noOfCol: Int,
//        noOfRow: Int,
//        fullServiceResponse: Body) {
//        layout.removeAllViews()
//        layout.removeAllViewsInLayout()
//
//        val seatInfo = ArrayList<SeatDetail>()
//
//        val gridLayout = GridLayout(activity)
//        gridLayout.alignmentMode = GridLayout.ALIGN_BOUNDS
//        gridLayout.columnCount = noOfCol
//        gridLayout.rowCount = noOfRow.plus(1)
//
//        STATUS_AVAILABLE = if (isLower) {
//            LOWER_STATUS_AVAILABLE
//        } else {
//            UPPER_STATUS_AVAILABLE
//        }
//        var count = 0
//
//        val driverPosition = getDriverPosition(listArray, fullServiceResponse)
//
//        if (!driverPosition.isDriverExistInSeat) {
//            val titleText1 = TextView(activity)
//            titleText1.setBackgroundResource(R.drawable.ic_driver)
//
//            gridLayout.addView(titleText1, 0)
//            val param1 = GridLayout.LayoutParams()
//            param1.setGravity(Gravity.CENTER)
//            param1.width = 100
//            param1.height = 100
//
//            val driverposcol: Int = driverPosition.col ?: -1
//
//            param1.columnSpec = GridLayout.spec(driverposcol)
//            param1.rowSpec = GridLayout.spec(driverPosition.row)
//            titleText1.layoutParams = param1
//        }
//
//        var lowerAvailability = false
//
//        for (i in 0..listArray.size.minus(1)) {
//            var listsubitem = ArrayList<SeatDetail>()
//            listsubitem = listArray[i]
//
//            for (j in 0..listsubitem.size.minus(1)) {
//                val available = listsubitem[j].available
//                val is_ladies_seat = listsubitem[j].isLadiesSeat
//                val is_gents_seat = listsubitem[j].isGentsSeat
//                val seatnumber = listsubitem[j].number
//                val isgangway = listsubitem[j].isGangway
//                val isHorizontal = listsubitem[j].isHorizontal
//                val isBerth = listsubitem[j].isBerth
//                val isSocial = listsubitem[j].isSocialDistancing
//                val fare = listsubitem[j].fare
//                val seatColor = listsubitem[j].backgroundColor
//
//                val row: Int = listsubitem[j].rowId ?: -1
//                val col: Int = listsubitem[j].colId ?: -1
//
//                val titleText = TextView(activity)
//                titleText.setPadding(10, 10, 10, 20)
//                titleText.gravity = Gravity.CENTER
//                titleText.textSize = 12F
//                val typeface: Typeface = Typeface.create("Noto Sans", Typeface.BOLD)
//                titleText.typeface = typeface
//
//                if (isgangway == false) {
//                    titleText.text = seatnumber
//                    count++
//                    titleText.id = count
//                    seatInfo.add(count.minus(1), listsubitem[j])
//                    if (available == true) {
//                        context?.resources?.getColor(R.color.un_select_color)?.let {
//                            titleText.setTextColor(it)
//                        }
//                        if (isLower) {
//                            lowerAvailability = true
//                        }
//                        if (isHorizontal != null && isHorizontal) titleText.setBackgroundResource(R.drawable.ic_sleeper_horizontal_available)
//                        else if (isBerth == true) titleText.setBackgroundResource(R.drawable.ic_sleeper_available)
//                        else titleText.setBackgroundResource(R.drawable.ic_seater_available)
//
//
//                        if (isSocialDistancingGuaranteed && isSocial) {
//                            titleText.setBackgroundColor(resources.getColor(R.color.colorSocialDistancing))
//                            titleText.text = "X"
//                            val seatStatus = SeatStatusData()
//                            seatStatus.status = STATUS_SOCIAL_DISTANCING
//                            seatStatus.value = fare.toString()
//                            titleText.tag = seatStatus
//                        } else if (is_ladies_seat == true) {
//                            val seatStatus = SeatStatusData()
//                            seatStatus.status = STATUS_AVAILABLE
//                            seatStatus.value = fare.toString()
//                            seatStatus.seatNumber = seatnumber
//                            titleText.tag = seatStatus
//                        } else if (is_gents_seat != null && is_gents_seat == true) {
//                            val seatStatus = SeatStatusData()
//                            seatStatus.status = STATUS_AVAILABLE
//                            seatStatus.value = fare.toString()
//                            seatStatus.seatNumber = seatnumber
//                            titleText.tag = seatStatus
//                        } else {
//
//                            val seatStatus = SeatStatusData()
//                            seatStatus.status = STATUS_AVAILABLE
//                            seatStatus.value = fare.toString()
//                            seatStatus.seatNumber = seatnumber
//                            titleText.tag = seatStatus
//                        }
//                        if (selectedFilterPrice.isNotEmpty() && selectedFilterPrice != "ALL") {
//                            seatFilterColor(titleText,
//                                j,
//                                selectedFilterPrice,
//                                isHorizontal,
//                                isBerth,
//                                fullServiceResponse,
//                                seatnumber,
//                                is_ladies_seat,
//                                is_gents_seat)
//                        }
//                        try {
//                            if (selectedSeatDetails.size > 0 && seatnumber != null) {
//                                for (pos in 0..selectedSeatDetails.size.minus(-1)) {
//                                    if (selectedSeatDetails[pos].number == seatnumber) {
//
//                                        onSeatSelectionChangeSeatUi(titleText,
//                                            selectedSeatDetails[pos])
//                                        break
//                                    }
//                                }
//                            }
//                        } catch (e: Exception) {
//                            e.message
//                        }
//
//                    } else {
//                        try {
//                            if (isHorizontal != null && isHorizontal) titleText.setBackgroundResource(
//                                R.drawable.ic_sleeper_horizontal_selected)
//                            else if (isBerth!!) titleText.setBackgroundResource(R.drawable.ic_sleeper_selected)
//                            else titleText.setBackgroundResource(R.drawable.ic_seater_selected)
//                            titleText.backgroundTintList =
//                                ColorStateList.valueOf(Color.parseColor(seatColor))
//
//                            if (isSocialDistancingGuaranteed && isSocial) {
//                                context?.resources?.getColor(R.color.un_select_color)?.let {
//                                    titleText.setTextColor(it)
//                                }
//                                titleText.setBackgroundColor(resources.getColor(R.color.colorSocialDistancing))
//                                titleText.text = "X"
//                                val seatStatus = SeatStatusData()
//                                seatStatus.status = STATUS_SOCIAL_DISTANCING
//                                seatStatus.value = fare.toString()
//                                titleText.tag = seatStatus
//
//                            } else if (is_ladies_seat == true) {
//                                context?.resources?.getColor(R.color.un_select_color)?.let {
//                                    titleText.setTextColor(it)
//                                }
//
//                                val seatStatus = SeatStatusData()
//                                seatStatus.status = STATUS_BOOKED
//                                seatStatus.value = fare.toString()
//                                titleText.tag = seatStatus
//                            } else {
//                                context?.resources?.getColor(R.color.un_select_color)?.let {
//                                    titleText.setTextColor(it)
//                                }
//
//                                titleText.setTextColor(Color.WHITE)
//                                val seatStatus = SeatStatusData()
//                                seatStatus.status = STATUS_BOOKED
//                                seatStatus.value = fare.toString()
//                                titleText.tag = seatStatus
//
//                            }
//                        } catch (e: Exception) {
//                            e.message
//                        }
//                    }
//
//                } else {
//                    context?.resources?.getColor(R.color.un_select_color)?.let {
//                        titleText.setTextColor(it)
//                    }
//
//                    if (seatnumber?.contains("DR_IMG")!!) {
//                        titleText.setBackgroundResource(R.drawable.ic_driver)
//                    } else if (seatnumber.contains("TV_IMG")) {
//                        titleText.setBackgroundResource(R.drawable.television)
//                    } else if (seatnumber.contains("PA_IMG")) {
//                        titleText.setBackgroundResource(R.drawable.restaurant)
//                    } else if (seatnumber.contains("WR_IMG")) {
//                        titleText.setBackgroundResource(R.drawable.wash_room)
//                    } else if (seatnumber.contains("SM_IMG")) {
//                        titleText.setBackgroundResource(R.drawable.smoking_area)
//                    } else if (seatnumber.contains("ST_IMG")) {
//                        titleText.setBackgroundResource(R.drawable.stair)
//                    }
//                }
//                if (driverPosition.isDriverExistInSeat) {
//                    gridLayout.addView(titleText, i)
//                } else {
//                    // gridLayout.addView(titleText,i.plus(1))
//                    gridLayout.addView(titleText, i)
//                }
//                val param = GridLayout.LayoutParams()
//                //here
//                if (isBerth == true) param.height = 210
//                else param.height = 100
//
//                if (isHorizontal == true) {
//                    param.width = 210
//                    param.height = 100
//                } else if (isgangway != false) {
//                    param.width = 65
//                    param.width = 65
//                } else {
//                    param.width = 100
//                }
//                param.rightMargin = 5
//                param.topMargin = 5
//                param.leftMargin = 5
//                param.bottomMargin = 5
//                param.setGravity(Gravity.CENTER)
//                if (isHorizontal == true) param.columnSpec = GridLayout.spec(col, 2)
//                else param.columnSpec = GridLayout.spec(col)
//
//                if (isBerth == true) {
//                    if (isHorizontal == true) {
//                        param.rowSpec = GridLayout.spec(row.plus(1))
//                    } else {
//                        param.rowSpec = GridLayout.spec(row.plus(1), 2)
//                    }
//                } else param.rowSpec = GridLayout.spec(row.plus(1))
//
//                titleText.layoutParams = param
//                titleText.setOnClickListener(this)
//
//
//
//                if (i < listsubitem.size.minus(1)) totalSeats[i].seatView = titleText
//            }
//        }
//
//        if (isLower) {
//            seatInfoL = seatInfo
//            if (lowertab.isSelected) lowertab.text =
//                "${context?.getString(R.string.lower)} ($lowerAvailableSeat)"
//            else lowertab.text = "${context?.getString(R.string.lower)}"
//
//
//        } else {
//            seatInfoU = seatInfo
//        }
//        layout.addView(gridLayout)
//        layout.setBackgroundResource(R.drawable.available_border)
//        binding.seatlayout.visible()
//    }
//
//    private fun getDriverPosition(seatDetails: ArrayList<ArrayList<SeatDetail>>,
//        serviceResponse: Body): DriverPosition {
//        var maxcolcount: Int = 0
//        var mincolcount: Int = 0
//        val driverPosition: DriverPosition = DriverPosition()
//        var colcount: Int = 0
//
//        for (i in 0..seatDetails.size.minus(1)) {
//            val listsubitem = seatDetails.get(i)
//
//            for (j in 0..listsubitem.size.minus(1)) {
//                val isgangway = listsubitem[j].isGangway
//                val number = listsubitem[j].number
//                val col: Int = listsubitem[j].colId ?: -1
//
//                if (number?.contains("DR_IMG")!!) {
//                    val row: Int = listsubitem[j].rowId ?: -1
//
//                    driverPosition.col = listsubitem[j].colId
//                    driverPosition.row = row
//                    driverPosition.isDriverExistInSeat = true
//
//                    return driverPosition
//                }
//
//                if (serviceResponse.coachDetails?.driverPosition != null && serviceResponse.coachDetails?.driverPosition?.toLowerCase(
//                        Locale.ENGLISH)?.contains("left")!!) {
//                    if (colcount < col) {
//                        if (isgangway == false) {
//                            colcount = col
//                            driverPosition.col = colcount
//                            driverPosition.row = 0
//                            driverPosition.isDriverExistInSeat = false
//                        }
//                    }
//                } else {
//                    if (colcount == 0) {
//                        if (isgangway == false) {
//                            colcount = col
//                            driverPosition.col = colcount
//                            driverPosition.row = 0
//                            driverPosition.isDriverExistInSeat = false
//                        }
//                    }
//                }
//            }
//        }
//        return driverPosition
//    }
//
//    private fun seatFilterColor(view: TextView,
//        i: Int,
//        selectedFilterPrice: String,
//        isHorizontal: Boolean?,
//        isBerth: Boolean?,
//        fullServiceResponse: Body,
//        seatNumber: String?,
//        is_ladies_seat: Boolean?,
//        is_gents_seat: Boolean?) {
//        if (view.tag == LOWER_STATUS_AVAILABLE) {
//            seatInfoDetails = seatInfoL
//        } else if (view.tag == UPPER_STATUS_AVAILABLE) {
//            seatInfoDetails = seatInfoU
//        }
//
//        for (i in 0..fullServiceResponse.coachDetails?.seatDetails?.size!!.minus(1)) {
//            if (seatNumber == fullServiceResponse.coachDetails?.seatDetails!![i].number) {
//                if (fullServiceResponse.coachDetails?.seatDetails!![i].number!!.isEmpty() || fullServiceResponse.coachDetails?.seatDetails!![i].isGangway!!) continue
//                else {
//                    if (selectedFilterPrice.toDouble() == fullServiceResponse.coachDetails?.seatDetails!![i].fare) {
//                        when {
//                            isHorizontal!! -> view.setBackgroundResource(R.drawable.ic_sleeper_horizontal_available)
//                            isBerth!! -> view.setBackgroundResource(R.drawable.ic_sleeper_available)
//                            else -> view.setBackgroundResource(R.drawable.ic_seater_available)
//                        }
//                        view.isEnabled = true
//                    } else {
//                        when {
//                            isHorizontal!! -> view.setBackgroundResource(R.drawable.ic_sleeper_horizontal_available)
//                            isBerth!! -> view.setBackgroundResource(R.drawable.ic_sleeper_available)
//                            else -> view.setBackgroundResource(R.drawable.ic_seater_available)
//                        }
//                        view.isEnabled = true
//                        view.alpha = 0.2f
//                    }
//                }
//            }
//
//        }
//    }
//
//
//    fun seatSelection(v: View) {
//        try {
//            val seatStatusData = v.tag as SeatStatusData
//            if (seatStatusData.status == LOWER_STATUS_AVAILABLE) {
//                seatInfoDetails = seatInfoL
//            } else if (seatStatusData.status == UPPER_STATUS_AVAILABLE) {
//                seatInfoDetails = seatInfoU
//            }
//
//            seatNumberAddRemove(seatInfoDetails.get(v.id.minus(1)),
//                v,
//                seatStatusData.value as String)
//
//
//        } catch (e: Exception) {
//            Timber.d("$TAG exceptionMsg", e.message!!)
//        } finally {
//        }
//    }
//
//
//    private fun seatNumberAddRemove(currentseat: SeatDetail, view: View, price: String) {
//
//        Timber.d(TAG, "selectedSeatDetails==> before ${selectedSeatDetails.size}")
//
//        if (selectedSeatDetails.size > 0) {
//
//            var seatIndex = -1
//            for (i in 0..selectedSeatDetails.size.minus(1)) {
//                val seatnumber = selectedSeatDetails[i].number
//                if (currentseat.number.equals(seatnumber, false)) {
//                    seatIndex = i
//                }
//            }
//            if (seatIndex == -1) {
//                var returnJourney = false
//                try {
//                    var onwardSeats = getSelectedSeats()
//                    /* if (AppData.isRoundTrip && returnDate.isNotEmpty() && isRound && (selectedSeatDetails.size == onwardSeats.size)) returnJourney =
//                         true*/
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                selectedSeatDetails.add(currentseat)
//                onSeatSelectionChangeSeatUi(view, currentseat)
//            } else {
//                selectedSeatDetails.removeAt(seatIndex)
//                onSeatUnSelectionChangeSeatUi(view, currentseat)
//                view.background.colorFilter = null
//                chkAllSeats.text = getString(R.string.selectAllSeat)
//                layoutBlockAllSeats.backgroundTintList =
//                    resources.getColorStateList(R.color.colorDimShadow6)
//            }
//        } else {
//            selectedSeatDetails.add(currentseat)
//            onSeatSelectionChangeSeatUi(view, currentseat)
//        }
//        if (selectedSeatDetails.size == totalSeats.size) getSelectedSeatNumbers(view, true)
//        else getSelectedSeatNumbers(view, false)
//    }
//
//    private fun onSeatSelectionChangeSeatUi(view: View, seatDetails: SeatDetail) {
//        Timber.d(TAG,
//            "seatNumber ${seatDetails.number} isBerth ${seatDetails.isBerth} isHorizontal ${seatDetails.isHorizontal}")
//        when {
//            seatDetails.isHorizontal!! -> view.setBackgroundResource(R.drawable.ic_sleeper_horizontal_selected)
//            seatDetails.isBerth!! -> view.setBackgroundResource(R.drawable.ic_sleeper_selected)
//            else -> view.setBackgroundResource(R.drawable.ic_seater_selected)
//        }
//
//        if (view is TextView) {
//            view.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorSelected))
//            context?.resources?.getColor(R.color.white)?.let { view.setTextColor(it) }
//        } else {
//            view.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorSelected))
//            val tv: TextView = view as TextView
//            context?.resources?.getColor(R.color.white)?.let { tv.setTextColor(it) }
//        }
//
//    }
//
//
//    private fun onSeatUnSelectionChangeSeatUi(view: View, seatDetails: SeatDetail) {
//        when {
//            seatDetails.isHorizontal!! -> view.setBackgroundResource(R.drawable.ic_sleeper_horizontal_available)
//            seatDetails.isBerth!! -> view.setBackgroundResource(R.drawable.ic_sleeper_available)
//            else -> view.setBackgroundResource(R.drawable.ic_seater_available)
//        }
//
//        if (view is TextView) {
//            view.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.un_select_color))
//            context?.resources?.getColor(R.color.un_select_color)?.let { view.setTextColor(it) }
//        } else {
//            view.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.un_select_color))
//            val tv: TextView = view as TextView
//            context?.resources?.getColor(R.color.un_select_color)?.let { tv.setTextColor(it) }
//        }
//
//    }
//
//    private fun selectAllSeats() {
//        totalSeats.forEach {
//            Timber.d(TAG, "totalSeats ${it.seatView}")
//            selectedSeatDetails.add(it)
//        }
//
//        buildCoachLayout()
////        onSeatSelectionListener.onSeatSelection(selectedSeatDetails, true)
//    }
//
//    private fun buildCoachLayout() {
//        coachlistMake(binding.layoutSeatlower, coachLayoutJsonLTemp, true, maxcolid + 2, rows, response)
//        if (hasUpperBirth) {
//            coachlistMake(binding.layoutSeatUpper,
//                coachLayoutJsonUTemp,
//                false,
//                maxcolid + 2,
//                rows,
//                response)
//            binding.loweruppertab.visible()
//        } else {
//            binding.loweruppertab.gone()
//        }
//    }
//
//
//    private fun getSelectedSeatNumbers(view: View, isAllSeatSelected: Boolean) {
//        if (selectedSeatDetails.size > 0) {
//            var selectedSeatString: String = ""
//            var totalSelectedSeatFare: Double = 0.0
//            for (i in 0..selectedSeatDetails.size.minus(1)) {
//                selectedSeatDetails[i].seatStatusData = view.tag as SeatStatusData
//                if (selectedSeatDetails[i].number == selectedSeatDetails[i].seatStatusData?.seatNumber) {
//                    selectedSeatDetails[i].seatCount = view.id
//                }
//
//                selectedSeatDetails[i].seatView = view
//                val seatnumber = selectedSeatDetails[i].number
//                val selectedSeatFare = selectedSeatDetails[i].fare.toString()
//                selectedSeatString = if (i == 0) {
//                    seatnumber + ""
//                } else {
//                    "$selectedSeatString,$seatnumber"
//                }
//                totalSelectedSeatFare = totalSelectedSeatFare.plus(selectedSeatFare.toDouble())
//            }
//        } else {
//            // No seat Selected
//        }
//
//
//        onSeatSelectionListener.onSeatSelection(selectedSeatDetails, isAllSeatSelected)
//    }
//
//
//    override fun setCoachData(serviceDetails: Body) {
//        serviceDetailsResponse(serviceDetails)
//    }
//}