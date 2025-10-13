package com.bitla.ts.utils.common


import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.location.Geocoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.InputFilter
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Base64.NO_WRAP
import android.util.Base64.decode
import android.util.Base64.encodeToString
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bitla.mba.morningstartravels.mst.pojo.service_details.SeatDetail
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.TsApplication
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.domain.pojo.Countries
import com.bitla.ts.domain.pojo.DateFilterRadioItem
import com.bitla.ts.domain.pojo.ErrorResponse
import com.bitla.ts.domain.pojo.SpinnerItems
import com.bitla.ts.domain.pojo.booking.StageData
import com.bitla.ts.domain.pojo.booking_custom_request.BookingCustomRequest
import com.bitla.ts.domain.pojo.booking_summary.Booking
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.passenger_details_result.PassengerDetailsResult
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.HubDetails
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_details_response.PassengerDetails
import com.bitla.ts.domain.pojo.service_details_response.StageDetail
import com.bitla.ts.utils.constants.DATE_FORMAT_12
import com.bitla.ts.utils.constants.DATE_FORMAT_24
import com.bitla.ts.utils.constants.DATE_FORMAT_DD_MMM_HH_MM
import com.bitla.ts.utils.constants.DATE_FORMAT_D_MMMM_Y
import com.bitla.ts.utils.constants.DATE_FORMAT_D_MMM_Y
import com.bitla.ts.utils.constants.DATE_FORMAT_D_MON
import com.bitla.ts.utils.constants.DATE_FORMAT_D_MON2
import com.bitla.ts.utils.constants.DATE_FORMAT_D_MON4
import com.bitla.ts.utils.constants.DATE_FORMAT_D_MONTH
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_YY
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y_H_M
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y_H_M_S
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y_SLASH
import com.bitla.ts.utils.constants.DATE_FORMAT_E_D_MMM_Y
import com.bitla.ts.utils.constants.DATE_FORMAT_E_D_M_Y
import com.bitla.ts.utils.constants.DATE_FORMAT_E_D_M_YY
import com.bitla.ts.utils.constants.DATE_FORMAT_MMM_DD
import com.bitla.ts.utils.constants.DATE_FORMAT_YMDTHMS
import com.bitla.ts.utils.constants.DATE_FORMAT_YYYY
import com.bitla.ts.utils.constants.DATE_FORMAT_Y_M_D
import com.bitla.ts.utils.constants.DATE_FORMAT_Y_M_D_SLASH
import com.bitla.ts.utils.constants.DEFAULT_TEXT_SIZE
import com.bitla.ts.utils.constants.LARGE_TEXT_SIZE
import com.bitla.ts.utils.constants.LOGIN_ID
import com.bitla.ts.utils.constants.OPERATOR_NAME
import com.bitla.ts.utils.constants.ROLE_NAME
import com.bitla.ts.utils.constants.SMALL_TEXT_SIZE
import com.bitla.ts.utils.constants.XLARGE_TEXT_SIZE
import com.bitla.ts.utils.sharedPref.PREF_CURRENT_BIOMETRIC_USER
import com.bitla.ts.utils.sharedPref.PREF_DOMAIN
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_LOCALE
import com.bitla.ts.utils.sharedPref.PREF_LOGGED_IN_USER
import com.bitla.ts.utils.sharedPref.PREF_LOGO
import com.bitla.ts.utils.sharedPref.PREF_TRAVEL_DATE
import com.bitla.ts.utils.sharedPref.PREF_USER_NAME
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.suspendCancellableCoroutine
import layoutInflater
import okhttp3.ResponseBody
import org.json.JSONObject
import setMaxLength
import timber.log.Timber
import toast
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.Reader
import java.io.StringWriter
import java.io.Writer
import java.lang.reflect.Type
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


var selectedSeatList: MutableList<SeatDetail> = mutableListOf()
var selectedSeatListReturn: MutableList<SeatDetail> = mutableListOf()
var agentList: MutableList<SpinnerItems> = mutableListOf()
var branchList: MutableList<SpinnerItems> = mutableListOf()
var userList: MutableList<SpinnerItems> = mutableListOf()
var blockTypeList: MutableList<SpinnerItems> = mutableListOf()
var bookingList: MutableList<Booking> = mutableListOf()
var cityList: MutableList<com.bitla.ts.domain.pojo.city_details.response.Result> = mutableListOf()
var bookingCustomRequest = BookingCustomRequest()
var seatDetailList = ArrayList<com.bitla.ts.domain.pojo.service_details_response.SeatDetail>()
var selectedExtraSeatDetails =
    ArrayList<com.bitla.ts.domain.pojo.service_details_response.SeatDetail>()
var selectedSeatNo: String? = null
var routeId: Int? = null
var isGstApplicable: Boolean = false
var passengerList: ArrayList<PassengerDetailsResult> = ArrayList()
var availableRoutesList: MutableList<com.bitla.ts.domain.pojo.available_routes.Result> =
    mutableListOf()

var availableSeatList = mutableListOf<String>()
var boardingPointList = mutableListOf<StageDetail>()
var droppingPointList = mutableListOf<StageDetail>()
var passengerDetailsList = mutableListOf<PassengerDetails>()
var busStageList = mutableListOf<StageData>()
var idTypesItemList = mutableListOf<SpinnerItems>()
var bookingType = ""
private var firebaseAnalytics: FirebaseAnalytics? = null

fun setSelectedSeats(selectedSeats: MutableList<SeatDetail>) {
    selectedSeatList = selectedSeats
}

fun getSelectedSeats(): MutableList<SeatDetail> {
    return selectedSeatList
}

fun setSelectedSeatsReturn(selectedSeats: MutableList<SeatDetail>) {
    selectedSeatListReturn = selectedSeats
}fun appTextSize(context: Context?, size: Int):Int {
      val textScaleFactor = when (context?.let { PreferenceUtils.getTextSize(it) }) {
        SMALL_TEXT_SIZE -> {
            (size * 0.85).toInt()
        }
        DEFAULT_TEXT_SIZE -> {
            (size * 1).toInt()
        }
        LARGE_TEXT_SIZE -> { (size * 1.15).toInt()
        }
        XLARGE_TEXT_SIZE -> {
            (size * 1.3).toInt()
        }
        else -> {
            (size * 1).toInt()
        }
    }
    return textScaleFactor
}

fun getSelectedSeatsReturn(): MutableList<SeatDetail> {
    return selectedSeatListReturn
}

fun setAvailableRoutes(availableRoutes: MutableList<com.bitla.ts.domain.pojo.available_routes.Result>) {
    availableRoutesList = availableRoutes
}

fun getAvailableRoutes(): MutableList<com.bitla.ts.domain.pojo.available_routes.Result> {
    return availableRoutesList
}

// block branch
fun saveBranchList(list: MutableList<SpinnerItems>) {
    branchList = list
}

fun retrieveBranchList(): MutableList<SpinnerItems> {
    return branchList
}

// block agent
fun saveAgentList(list: MutableList<SpinnerItems>) {
    agentList = list
}


fun retrieveAgentList(): MutableList<SpinnerItems> {
    return agentList
}


// block user
fun saveUserList(list: MutableList<SpinnerItems>) {
    userList = list
}

fun retrieveUserList(): MutableList<SpinnerItems> {
    return userList
}

//block type
fun saveBlockType(list: MutableList<SpinnerItems>) {
    blockTypeList = list
}

fun retrieveBlockType(): MutableList<SpinnerItems> {
    return blockTypeList
}


fun saveCityList(list: MutableList<com.bitla.ts.domain.pojo.city_details.response.Result>) {
    cityList = list
}

fun retrieveCityList(): MutableList<com.bitla.ts.domain.pojo.city_details.response.Result> {
    return cityList
}


fun saveBookingList(list: MutableList<Booking>) {
    bookingList = list
}

fun retrieveBookingList(): MutableList<Booking> {
    return bookingList
}

fun saveBookingCustomRequest(bookingRequest: BookingCustomRequest) {
    bookingCustomRequest = bookingRequest
}

fun retrieveBookingCustomRequest(): BookingCustomRequest {
    return bookingCustomRequest
}

fun setSelectSeats(selectedSeatDetail: ArrayList<com.bitla.ts.domain.pojo.service_details_response.SeatDetail>) {
    seatDetailList = selectedSeatDetail
}

fun retrieveSelectedSeats(): ArrayList<com.bitla.ts.domain.pojo.service_details_response.SeatDetail> {
    return seatDetailList
}


fun setSelectExtraSeats(selectedExtraSeatDetail: ArrayList<com.bitla.ts.domain.pojo.service_details_response.SeatDetail>) {
    selectedExtraSeatDetails = selectedExtraSeatDetail
}

fun retrieveSelectedExtraSeats(): ArrayList<com.bitla.ts.domain.pojo.service_details_response.SeatDetail> {
    return selectedExtraSeatDetails
}

fun setSelectSeatNumber(seatNo: String) {
    selectedSeatNo = seatNo
}

fun retrieveSelectedSeatNumber(): String? {
    return selectedSeatNo
}

fun setRouteId(route_id: Int, gstApplicable: Boolean?) {
    routeId = route_id
    if (gstApplicable != null) {
        isGstApplicable = gstApplicable
    }
}

fun retrieveRouteId(): Int? {
    return routeId
}

fun retrieveGstApplicable(): Boolean {
    return isGstApplicable
}

fun setSelectedPassengers(passengers: ArrayList<PassengerDetailsResult>) {
    passengerList = passengers
}

fun getAllCountries(context: Context): List<Countries> {
    val countries = mutableListOf<Countries>()
    try {
        val inputStream: InputStream = context.assets!!.open("countries.json")
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()

        val json = String(buffer, Charsets.UTF_8)
        val gson = Gson()
        val listType: Type = object : TypeToken<List<Countries>>() {}.type
        countries.addAll(gson.fromJson(json, listType))
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return countries
}

fun countryFlag(code: String) = code
    .uppercase()
    .split("")
    .filter { it.isNotBlank() }
    .map { it.codePointAt(0) + 0x1F1A5 }
    .joinToString("") { String(Character.toChars(it)) }

fun retrieveSelectedPassengers(): ArrayList<PassengerDetailsResult> {
    return passengerList
}

fun availableSeats(availableSeats: MutableList<String>) {
    availableSeatList = availableSeats
}

fun getAvailableSeats(): MutableList<String> {
    return availableSeatList
}

fun setBoardingList(boardings: MutableList<StageDetail>) {
    boardingPointList = boardings
}

fun getBoardingList(): MutableList<StageDetail> {
    return boardingPointList
}

fun setDroppingList(droppings: MutableList<StageDetail>) {
    droppingPointList = droppings
}

fun getDroppingList(): MutableList<StageDetail> {
    return droppingPointList
}

fun setPassengerDetails(passengerDetails: MutableList<PassengerDetails>) {
    passengerDetailsList = passengerDetails
}

fun getPassengerDetails(): MutableList<PassengerDetails> {
    return passengerDetailsList
}

fun setBusStageData(busStage: MutableList<StageData>) {
    busStageList = busStage
}

fun getBusStageData(): MutableList<StageData> {
    return busStageList
}

fun setCountryCodes(countryCodes: ArrayList<Int>) {
    PreferenceUtils.putCountryCodes(countryCodes)
}

fun getCountryCodes(): ArrayList<Int> {
    return PreferenceUtils.getCountryCodes()
}

fun setIdTypesList(idTypes: MutableList<SpinnerItems>) {
    idTypesItemList = idTypes
}

fun getArrivalTime(hour: Int, minutes: Int): String {
    val df = SimpleDateFormat("HH:mm")
    val c = Calendar.getInstance()
    c.add(Calendar.HOUR_OF_DAY, hour)
    c.add(Calendar.MINUTE, minutes)
    return inputFormatToOutput(df.format(c.time).toString(), DATE_FORMAT_24, DATE_FORMAT_12)
}

fun convertTo12HourFormat(inputTime: String): String {
    val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val date = inputFormat.parse(inputTime)
    return outputFormat.format(date).toUpperCase()
}


fun getIdTypesList(): MutableList<SpinnerItems> {
    return idTypesItemList
}

fun getTodayDate(): String {
    val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y, Locale.getDefault())
    return sdf.format(Date())
}

fun getTodayDateWithTime(): String {
    val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y_H_M, Locale.getDefault())
    return sdf.format(Date())
}

fun getTodayDateWithTimeWithSec(): String {
    val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y_H_M_S, Locale.getDefault())
    return sdf.format(Date())
}

//fun getTodayDateyyyy(): String {
//    val sdf = SimpleDateFormat(DATE_FORMAT_y_M_D)
//    return sdf.format(Date())
//}

fun getCurrentYear(): String {
    val sdf = SimpleDateFormat(DATE_FORMAT_YYYY)
    return sdf.format(Date())
}

fun getDateFormatFromCalendar(clickedDayCalendar: Calendar): String {
    val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y)
    return sdf.format(clickedDayCalendar.time)
}

fun getTomorrowDate(): String {
    val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y)
    var currentDate = Date()
    currentDate.date = currentDate.date.plus(1)
    return sdf.format(currentDate)
}

fun getYesterdayDate(): String {
    val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y)
    val currentDate = Date()
    currentDate.date = currentDate.date.minus(1)
    return sdf.format(currentDate)
}

fun getTwoDaysBack(date: String, days: Int = 2): String {
    val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y)
    val newDate = convertDateFormat(date, "dd-MM-yyyy", "MM/dd/yyyy")
    val currentDate = Date(newDate)
    currentDate.date = currentDate.date.minus(days)
    return sdf.format(currentDate)
}

fun getDateDifference(
    startDateStr: String,
    endDateStr: String,
    dateFormat: String = DATE_FORMAT_D_M_Y
): Long {
    val sdf = SimpleDateFormat(dateFormat)
    val startDate: Date = sdf.parse(startDateStr)
    val endDate: Date = sdf.parse(endDateStr)

    val differenceInMillis = endDate.time - startDate.time
    return TimeUnit.MILLISECONDS.toDays(differenceInMillis)
}

fun getPreviousssDate(date: String): String {
    var dateYmd = ""
    try {
        val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y)
        val newDate = convertDateFormat(date, "dd-MM-yyyy", "MM/dd/yyyy")
        val currentDate = Date(newDate)
        currentDate.date = currentDate.date.minus(1)
        dateYmd = sdf.format(currentDate)
        return dateYmd
    } catch (e: Exception) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
    }
    return dateYmd

}

fun convertDateFormat(date: String, currentFormat: String, newFormat: String): String {
    var spf = SimpleDateFormat(currentFormat)
    val newDate = spf.parse(date)
    spf = SimpleDateFormat(newFormat)
    val date1 = spf.format(newDate)
    return date1
}

fun getDateYMD(dateDMY: String): String {
    var dateYmd = ""
    try {
        if (dateDMY.isNotEmpty()) {
            val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_M_Y)
            val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D)
            dateYmd = sdfYMD.format(sdfDMY.parse(dateDMY))
            return dateYmd
        }
    } catch (e: Exception) {
        Timber.d("exceptionMsg ${e.message}")
    }
    return dateYmd
}


fun getDateMD(dateYMD: String): String? {
    val sdfDMY = SimpleDateFormat(DATE_FORMAT_Y_M_D)
    val sdfMD = SimpleDateFormat(DATE_FORMAT_D_MONTH)
    return sdfMD.format(sdfDMY.parse(dateYMD))
}

fun getDateEDMY(dateYMD: String): String? {
    val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D)
    val sdfEDMY = SimpleDateFormat(DATE_FORMAT_E_D_M_Y)
    return sdfEDMY.format(sdfYMD.parse(dateYMD))
}

fun getDateEDMYY(dateYMD: String): String? {
    val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D)
    val sdfEDMYY = SimpleDateFormat(DATE_FORMAT_E_D_M_YY)
    return sdfEDMYY.format(sdfYMD.parse(dateYMD))
}

fun getDateDMYY(dateYMD: String): String? {
    var convertedDate = ""
    if (dateYMD != null && dateYMD.isNotEmpty()) {
        if (dateYMD.contains("-")) {
            val dateSubString = dateYMD.substringBefore("-")
            if (dateSubString.length == 4) { //yyyy-mm-dd
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D) //yyyy-MM-dd
                val sdfDMYY = SimpleDateFormat(DATE_FORMAT_D_M_YY) //dd MMM yyyy
                convertedDate = sdfDMYY.format(sdfYMD.parse(dateYMD))
            } else { // dd/mm/yyyy
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_D_M_Y) // dd-MM-yyyy
                val sdfDMYY = SimpleDateFormat(DATE_FORMAT_D_M_YY) //dd MMM yyyy
                convertedDate = sdfDMYY.format(sdfYMD.parse(dateYMD))
            }
        } else if (dateYMD.contains("/")) {
            val dateSubString = dateYMD.substringBefore("/")
            if (dateSubString.length == 4) { // yyyy/mm/dd
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D_SLASH)  // yyyy/MM/dd
                val sdfDMYY = SimpleDateFormat(DATE_FORMAT_D_M_YY) //dd MMM yyyy
                convertedDate = sdfDMYY.format(sdfYMD.parse(dateYMD))
            } else { // dd/mm/yyyy
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_D_M_Y_SLASH) // dd/MM/yyyy
                val sdfDMYY = SimpleDateFormat(DATE_FORMAT_D_M_YY) //dd MMM yyyy
                convertedDate = sdfDMYY.format(sdfYMD.parse(dateYMD))
            }
        }
    }
    return convertedDate
}

fun getDateDMY(dateYMD: String): String? {
    var returnDate = dateYMD  // dd-mm-yyyy
    if (dateYMD != null && dateYMD.isNotEmpty()) {
        if (dateYMD.contains("-")) {
            val dateSubString = dateYMD.substringBefore("-")
            if (dateSubString.length == 4) { //yyyy-mm-dd
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D) //yyyy-MM-dd
                val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_M_Y) //dd-MM-yyyy
                returnDate = sdfDMY.format(sdfYMD.parse(dateYMD))
            }
        } else if (dateYMD.contains("/")) {
            val dateSubString = dateYMD.substringBefore("/")
            if (dateSubString.length == 4) { // yyyy/mm/dd
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D_SLASH)  // yyyy/MM/dd
                val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_M_Y) // dd-MM-yyyy
                returnDate = sdfDMY.format(sdfYMD.parse(dateYMD))
            } else { // dd/mm/yyyy
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_D_M_Y_SLASH) // dd/MM/yyyy
                val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_M_Y) // dd-MM-yyyy
                returnDate = sdfDMY.format(sdfYMD.parse(dateYMD))
            }
        }
    }
    return returnDate
}

fun getDateMMMM(dateYMD: String): String? {
    var returnDate = dateYMD
    if (dateYMD != null && dateYMD.isNotEmpty()) {
        if (dateYMD.contains("-")) {
            val dateSubString = dateYMD.substringBefore("-")
            if (dateSubString.length == 4) { //yyyy-mm-dd
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D) //yyyy-MM-dd
                val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_MMMM_Y) //dd MMMM yyyy
                returnDate = sdfDMY.format(sdfYMD.parse(dateYMD))
            } else { // dd/mm/yyyy
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_D_M_Y) // dd-MM-yyyy
                val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_MMMM_Y) //dd MMMM yyyy
                returnDate = sdfDMY.format(sdfYMD.parse(dateYMD))
            }
        } else if (dateYMD.contains("/")) {
            val dateSubString = dateYMD.substringBefore("/")
            if (dateSubString.length == 4) { // yyyy/mm/dd
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D_SLASH)  // yyyy/MM/dd
                val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_MMMM_Y) //dd MMMM yyyy
                returnDate = sdfDMY.format(sdfYMD.parse(dateYMD))
            } else { // dd/mm/yyyy
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_D_M_Y_SLASH) // dd/MM/yyyy
                val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_MMMM_Y) //dd MMMM yyyy
                returnDate = sdfDMY.format(sdfYMD.parse(dateYMD))
            }
        }
    }
    return returnDate
}

fun getDateMMM(dateDMY: String): String? {
    var returnDate = dateDMY
    if (dateDMY != null && dateDMY.isNotEmpty()) {
        if (dateDMY.contains("-")) {
            val dateSubString = dateDMY.substringBefore("-")
            if (dateSubString.length == 4) { //yyyy-mm-dd
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D) //yyyy-MM-dd
                val sdfMMM = SimpleDateFormat(DATE_FORMAT_D_MMM_Y) //dd MMMM yyyy
                returnDate = sdfMMM.format(sdfYMD.parse(dateDMY))
            } else { // dd/mm/yyyy
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_D_M_Y) // dd-MM-yyyy
                val sdfMMM = SimpleDateFormat(DATE_FORMAT_D_MMM_Y) //dd MMMM yyyy
                returnDate = sdfMMM.format(sdfYMD.parse(dateDMY))
            }
        } else if (dateDMY.contains("/")) {
            val dateSubString = dateDMY.substringBefore("/")
            if (dateSubString.length == 4) { // yyyy/mm/dd
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D_SLASH)  // yyyy/MM/dd
                val sdfMMM = SimpleDateFormat(DATE_FORMAT_D_MMM_Y) //dd MMMM yyyy
                returnDate = sdfMMM.format(sdfYMD.parse(dateDMY))
            } else { // dd/mm/yyyy
                val sdfYMD = SimpleDateFormat(DATE_FORMAT_D_M_Y_SLASH) // dd/MM/yyyy
                val sdfMMM = SimpleDateFormat(DATE_FORMAT_D_MMM_Y) //dd MMMM yyyy
                returnDate = sdfMMM.format(sdfYMD.parse(dateDMY))
            }
        }
    }
    return returnDate
}


fun getPreviousDate(journeyDate: String): String {
    val sdfYMD = SimpleDateFormat(DATE_FORMAT_D_M_Y)
    val sdfDM = SimpleDateFormat(DATE_FORMAT_D_MON2)
    var currentDate = sdfYMD.parse(journeyDate)
    currentDate.date = currentDate.date.minus(1)
    return sdfDM.format(currentDate)
}

fun getNextDate(journeyDate: String): String {
    val sdfYMD = SimpleDateFormat(DATE_FORMAT_D_M_Y)
    val sdfDM = SimpleDateFormat(DATE_FORMAT_D_MON2)
    var currentDate = sdfYMD.parse(journeyDate)
    currentDate.date = currentDate.date.plus(1)
    return sdfDM.format(currentDate)
}

fun getDateDMMM(journeyDate: String): String {
    if (journeyDate == "") {
        return ""
    }
    try {
        val sdfYMD = SimpleDateFormat(DATE_FORMAT_D_M_Y)
        val sdfDM = SimpleDateFormat(DATE_FORMAT_D_MON2)
        var currentDate = sdfYMD.parse(journeyDate)
        return sdfDM.format(currentDate)
    }catch (e: Exception){
        return ""
    }

}

fun getNextDate2(journeyDate: String): String {
    val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D)
    val sdfYMD2 = SimpleDateFormat(DATE_FORMAT_D_M_YY)
    var currentDate = sdfYMD.parse(journeyDate)
//    currentDate.date = currentDate.date.plus(1)
    return sdfYMD2.format(currentDate)
}

fun getNextDate3(journeyDate: String): String {
    val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D)
    val sdfYMD2 = SimpleDateFormat(DATE_FORMAT_D_M_Y)
    var currentDate = sdfYMD.parse(journeyDate)
    currentDate.date = currentDate.date.plus(1)
    return sdfYMD2.format(currentDate)
}

fun getNextDate7(journeyDate: String): String {

    var c = Calendar.getInstance()

    val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D)
    val sdfYMD2 = SimpleDateFormat(DATE_FORMAT_D_M_Y)
    var currentDate = sdfYMD2.parse(journeyDate)
    c.setTime(currentDate)
    c.add(Calendar.DAY_OF_MONTH, 6);
    var new = sdfYMD.format(c.time)

    return new
//    return (sdfYMD2.format(currentDate))
}

fun isPreviousDate(journeyDate: String): Boolean {
    val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D)
    var currentDate = sdfYMD.format(Date())
    return currentDate == journeyDate
}

fun isNextDate(journeyDate: String): Boolean {
    val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D)
    var currentDate = Date()
    currentDate.date = currentDate.date.plus(30)
    return sdfYMD.format(currentDate) == journeyDate
}

fun amPmToTwentyFour(time: String): String? {
   return try {
       val sdf12 = SimpleDateFormat(DATE_FORMAT_12)
       val sdf24 = SimpleDateFormat(DATE_FORMAT_24)
       sdf24.format(sdf12.parse(time))
    }catch (ex:Exception){
        time
    }

}


fun inputFormatToEDMY(inputDate: String, inputFormat: String): String? {
    val sdfYMD = SimpleDateFormat(inputFormat)
    val sdfEDMY = SimpleDateFormat(DATE_FORMAT_E_D_M_Y)
    return sdfEDMY.format(sdfYMD.parse(inputDate))
}


fun getDateDMY2(dateYMD: String): String? {
    val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D)
    val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_MON2)
    return sdfDMY.format(sdfYMD.parse(dateYMD))

}

fun getDateDMY3(dateYMD: String): String? {
    val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D)
    val sdfDMY = SimpleDateFormat("MMM dd")
    return sdfDMY.format(sdfYMD.parse(dateYMD))

}

fun getDateMMMDD(dateYMD: String): String? {
    val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D)
    val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_MON4)
    return sdfDMY.format(sdfYMD.parse(dateYMD))

}

fun getDateDDMMM(dateYMD: String): String? {
    val sdfYMD = SimpleDateFormat(DATE_FORMAT_D_M_Y_SLASH)
    val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_MON)
    return sdfDMY.format(sdfYMD.parse(dateYMD))

}


fun inputFormatToDD_MMM_HH_MM(inputDate: String, inputFormat: String): String? {
    val sdfYMD = SimpleDateFormat(inputFormat)
    val sdfEDMY = SimpleDateFormat(DATE_FORMAT_DD_MMM_HH_MM)
    return sdfEDMY.format(sdfYMD.parse(inputDate))
}



fun inputFormatToHMTDMY(inputDate: String, inputFormat: String): String? {
 return try {
        val sdfYMD = SimpleDateFormat(inputFormat)
        val sdfEDMY = SimpleDateFormat(DATE_FORMAT_YMDTHMS)
        return sdfEDMY.format(sdfYMD.parse(inputDate))
    }catch (_:Exception){
        inputDate
    }

}

fun inputFormatToEDMMMY(inputDate: String, inputFormat: String): String? {
    val sdfYMD = SimpleDateFormat(inputFormat)
    val sdfEDMY = SimpleDateFormat(DATE_FORMAT_E_D_MMM_Y)
    return sdfEDMY.format(sdfYMD.parse(inputDate))
}

fun inputFormatToOutput(inputDate: String, inputFormat: String, outputFormat: String): String {
    return try {
        var convertedDate = ""
        if (inputDate.isNotEmpty()) {
            val sdfInput = SimpleDateFormat(inputFormat, Locale.getDefault())
            val sdfOutput = SimpleDateFormat(outputFormat,Locale.getDefault())
            convertedDate = sdfOutput.format(sdfInput.parse(inputDate))
        }
        convertedDate
    } catch (e: Exception) {
        Timber.d("exceptionMsg ${e.message}")
        inputDate
    }
}

fun stringToDate(dateInString: String, dateFormat: String): Date? {
    return try {
        SimpleDateFormat(dateFormat, Locale.getDefault()).parse(dateInString)
    } catch (ex: Exception) {
        Date()
    }

}

fun stringToDateEnglish(dateInString: String, dateFormat: String): Date? {
    val date: Date? = SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(dateInString)
    return date
}

fun thFormatDateOutput(inputDate: String): String {
    var format = SimpleDateFormat(DATE_FORMAT_Y_M_D)
    val date1 = format.parse(inputDate)
    val date = format.format(date1)

    format =
        if (date.endsWith("01") || date.endsWith("1") && !date.endsWith("11")) SimpleDateFormat(
            "d'st' MMMM yyyy",
            Locale.getDefault()
        ) else if (date.endsWith(
                "02"
            ) || date.endsWith("2") && !date.endsWith("12")
        ) SimpleDateFormat("d'nd' MMMM yyyy") else if (date.endsWith("03") || date.endsWith("3") && !date.endsWith(
                "13"
            )
        ) SimpleDateFormat(
            "d'rd' MMMM yyyy"
        ) else SimpleDateFormat("d'th' MMMM yyyy")

    return format.format(date1)
}

fun thFormatDateMMMOutput(inputDate: String): String {
    try {
        var format = SimpleDateFormat(DATE_FORMAT_Y_M_D)
        val date1 = format.parse(inputDate)
        val date = format.format(date1)

        format =
            if (date.endsWith("01") || date.endsWith("1") && !date.endsWith("11")) SimpleDateFormat(
                "d'st' MMM yyyy"
            ) else if (date.endsWith(
                    "02"
                ) || date.endsWith("2") && !date.endsWith("12")
            ) SimpleDateFormat("d'nd' MMM yyyy") else if (date.endsWith("03") || date.endsWith("3") && !date.endsWith(
                    "13"
                )
            ) SimpleDateFormat(
                "d'rd' MMM yyyy"
            ) else SimpleDateFormat("d'th' MMM yyyy")

        return format.format(date1)
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }

}

fun isPastDate(myDate: String): Boolean {
    val isOutdated: Boolean
    val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y)
    val strDate = sdf.parse(myDate)
    isOutdated = Date().after(strDate)
    return isOutdated
}

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    return format.format(date)
}

@SuppressLint("HardwareIds")
fun getLogFileName(): String {
    return "${Build.BRAND}_${Build.MODEL}_${
        Settings.Secure.getString(
            TsApplication.getAppContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }"
}

/*
*
*
*
* */
fun changeColorCode(colorCode: String?, view: View, type: Int, buttonTextColor: String?) {
    try {
        if (colorCode == null || colorCode.length < 7 || buttonTextColor == null || buttonTextColor.length < 7) return

        when (type) {
            0 -> {
                val textView = view as TextView
                textView.setTextColor(Color.parseColor(colorCode))
            }

            10 -> {
                val textView = view as TextView
                textView.setTextColor(Color.parseColor(colorCode))
                for (drawable in textView.compoundDrawables) {
                    if (drawable != null) {
                        drawable.colorFilter =
                            PorterDuffColorFilter(
                                Color.parseColor(colorCode),
                                PorterDuff.Mode.SRC_IN
                            )
                    }
                }
            }

            20 -> {
                val textView = view as TextView
                textView.setBackgroundColor(Color.parseColor(colorCode))
            }

            1 -> {
                val button = view as Button
                button.setTextColor(Color.parseColor(buttonTextColor))
                button.setBackgroundColor(Color.parseColor(colorCode))
                button.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorCode))

            }

            11 -> {
                val button = view as Button
                button.setTextColor(Color.parseColor(buttonTextColor))
                button.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorCode))
            }

            13 -> {
                val radioButton = view as RadioButton
                radioButton.setBackgroundColor(Color.parseColor(colorCode))
            }

            14 -> {
                val radioButton = view as RadioButton
                radioButton.buttonTintList = ColorStateList.valueOf(Color.parseColor(colorCode))
            }

            2 -> {
                val spinner = view as Spinner
            }

            3 -> {
                val toolbar = view as Toolbar
                toolbar.setBackgroundColor(Color.parseColor(colorCode))
            }

            4 -> {
                val linearLayout = view as LinearLayout
                linearLayout.setBackgroundColor(Color.parseColor(colorCode))
            }

            5 -> {
                val tabLayout = view as TabLayout
                tabLayout.setSelectedTabIndicatorColor(Color.parseColor(colorCode))
                tabLayout.setTabTextColors(R.color.colorBlack, Color.parseColor(colorCode))

            }

            51 -> {
                val tabLayout = view as TabLayout
                tabLayout.setSelectedTabIndicatorColor(Color.parseColor(colorCode))

            }

            6 -> {
                val cardView = view as CardView
                cardView.setBackgroundColor(Color.parseColor(colorCode))
            }

            7 -> {
                val imageView = view as ImageView
                imageView.setColorFilter(
                    Color.parseColor(colorCode),
                    android.graphics.PorterDuff.Mode.MULTIPLY
                );

            }
        }

    } catch (e: Exception) {
        Timber.d("exceptionMessage ${e.message}")
    }
}


fun getRetrofitErrorMsg(errorBody: ResponseBody?): String {
    val response = try {
        val gson = Gson()
        val type = object : TypeToken<ErrorResponse>() {}.type
        val errorResponse: ErrorResponse? = gson.fromJson(errorBody!!.charStream(), type)
        errorResponse?.message ?: "Server Error"
    } catch (e: Exception) {
        Timber.d("exceptionMsg ${e.message}")
        e.message
    }
    return response!!
}

fun isEmailValid(email: CharSequence?): Boolean {
    val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
    val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher: Matcher = pattern.matcher(email)
    return matcher.matches()
}

fun isNumberOrAlphaValid(string: CharSequence?): Boolean {
    val expression = "^{2,4}$"
    val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher: Matcher = pattern.matcher(string)
    return matcher.matches()
}


fun convertFirstCharToUpperCase(text: String): String {
    var message = text
    val charArray = message.toCharArray()
    var foundSpace = true
    for (i in charArray.indices) {
        if (Character.isLetter(charArray[i])) {
            if (foundSpace) {
                charArray[i] = Character.toUpperCase(charArray[i])
                foundSpace = false
            }
        } else {
            foundSpace = true
        }
    }
    message = String(charArray)
    return message
}


fun getUserRole(loginModel: LoginModel, isAgentLogin: Boolean, context: Context): String {
    var role = context.getString(R.string.user)
    if (loginModel.role != null) {
        role = when {
            isAgentLogin -> context.getString(R.string.agent)
            loginModel.role.equals(
                "Field Officer",
                true
            ) -> context.getString(R.string.role_field_officer)

            else -> context.getString(R.string.user)
        }
    }
    return role
}


fun setDateLocale(locale: String, context: Context) {
    val languageToLoad = locale

    val locale = Locale(languageToLoad)
    Locale.setDefault(locale)
    val config = Configuration()
    config.locale = locale
    context.resources.updateConfiguration(config, context.resources.getDisplayMetrics());
}

//if (loginModel.role != null) {
//    var loginRole= loginModel.role
//
//    if (loginRole.equals("Agent",true)) loginRole= context.getString(R.string.agent)
//    else if (loginRole.equals("Field Officer",true)) context.getString(R.string.role_field_officer)
//    else if (loginRole.equals("user",true)) context.getString(R.string.user)
//
//    Timber.d("agentlogin:0 ${isAgentLogin}")
//
//    role = when {
//        isAgentLogin -> context.getString(R.string.agent)
//        loginRole.equals(context.getString(R.string.role_field_officer),true) -> context.getString(R.string.role_field_officer)
//        else -> context.getString(R.string.user)
//    }
//}

//firebase events
fun firebaseLogEvent(
    context: Context,
    logEventName: String,
    loginId: String?,
    operatorName: String?,
    roleName: String?,
    eventKey: String,
    eventValue: String
) {

    firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    firebaseAnalytics?.logEvent(logEventName) {
        param(LOGIN_ID, loginId.toString())
        param(OPERATOR_NAME, operatorName.toString())
        param(ROLE_NAME, roleName.toString())
        param(eventKey, eventValue)

    }
}

fun roundOffDecimal(number: Double): Double? {
    val otherSymbols = DecimalFormatSymbols(Locale.ENGLISH)
    otherSymbols.setDecimalSeparator('.');

    val df = DecimalFormat("#.##", otherSymbols)
    df.roundingMode = RoundingMode.CEILING
    return df.format(number).toDouble()
}


fun getDateDMYfromAny(dateYMD: String): String? {
    var returnDate = dateYMD
    if (dateYMD.contains("-")) {
        val dateSubString = dateYMD.substringBefore("-")
        if (dateSubString.length == 4) {
            val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D)
            val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_M_Y)
            returnDate = sdfDMY.format(sdfYMD.parse(dateYMD))
        }
    } else if (dateYMD.contains("/")) {
        val dateSubString = dateYMD.substringBefore("/")
        returnDate = if (dateSubString.length == 4) {
            val sdfYMD = SimpleDateFormat(DATE_FORMAT_Y_M_D_SLASH)
            val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_M_Y)
            sdfDMY.format(sdfYMD.parse(dateYMD))
        } else {
            val sdfYMD = SimpleDateFormat(DATE_FORMAT_D_M_Y_SLASH)
            val sdfDMY = SimpleDateFormat(DATE_FORMAT_D_M_Y)
            sdfDMY.format(sdfYMD.parse(dateYMD))
        }
    }
    return returnDate
}

fun getServerDateFormat(date: String): String {
    return when {
        date.indexOf('/') == 2 -> "dd/MM/yyyy"
        date.indexOf('/') == 4 -> "yyyy/MM/dd"
        date.indexOf('-') == 2 -> "dd-MM-yyyy"
        date.indexOf('-') == 4 -> "yyyy-MM-dd"
        else -> "dd/MM/yyyy"
    }
}

fun clearAndSave(context: Context) {
    try {
        var currentUser = PreferenceUtils.getLogin()
        val lastLang = PreferenceUtils.getlang()

        val biometricLinkedUser =
            PreferenceUtils.getObject<LoginModel>(PREF_CURRENT_BIOMETRIC_USER)
        val domain =
            PreferenceUtils.getPreference(
                PREF_DOMAIN,
                context.getString(R.string.empty)
            )!!
        val logo =
            PreferenceUtils.getPreference(
                PREF_LOGO,
                context.getString(R.string.empty)
            )!!

        PreferenceUtils.clearAllPreferences()
        PreferenceUtils.setlang(lastLang)

        if (lastLang == "in") {
            PreferenceUtils.putString(PREF_LOCALE, "id")
        } else {
            PreferenceUtils.putString(PREF_LOCALE, "en")
        }
        PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
        PreferenceUtils.setPreference(PREF_DOMAIN, domain)

        PreferenceUtils.setPreference(PREF_USER_NAME, currentUser.userName)
        PreferenceUtils.setPreference(PREF_LOGO, logo)
        PreferenceUtils.putObject(
            biometricLinkedUser,
            PREF_CURRENT_BIOMETRIC_USER
        )

        PreferenceUtils.putObject(null, PREF_LOGGED_IN_USER)
        PreferenceUtils.removeKey(PREF_TRAVEL_DATE)

    } catch (e: Exception) {
        Timber.d("Error occurred in dashboardViewModel.dataFullLogout.observe ${e.message}")
    }
}

fun getCurrentOperatorLogo(): String {
    return PreferenceUtils.getPreference(PREF_LOGO, "") ?: ""
}

fun updateBaseURL(baseURL: String) {
    PreferenceUtils.setUpdatedApiUrlAddress(baseURL)
    PreferenceUtils.setPreference(PREF_DOMAIN, baseURL)
}

fun encryptToBase64(str: String): String {
    return encodeToString(str.toByteArray(), NO_WRAP)
}

fun decryptToBase64(str: String): String {
    return String(decode(str.toByteArray(), NO_WRAP))
}

fun getDeviceUniqueId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}

fun getDeviceModel(): String {
    return Build.MODEL
}

fun getDeviceName(context: Context): String {
    return Settings.Global.getString(context.contentResolver, "device_name")
}

fun getDeviceAndroidAPIVersion(): String {
    return Build.VERSION.RELEASE.toString()

}

fun getAppVersionCode(context: Context): String {
    var versionCode = ""
    try {
        val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val longVersionCode: Long = PackageInfoCompat.getLongVersionCode(pInfo)
        versionCode = longVersionCode.toString()

    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return versionCode
}

fun getAppVersion(context: Context): String {
    var result = ""
    try {
        result = context.packageManager?.getPackageInfo(context.packageName, 0)?.versionName ?: ""
        result = result.replace("[a-zA-Z]|=".toRegex(), "")
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return result
}

fun checkIfFragmentAttached(fragment: Fragment?, context: Context?): Boolean {
    return fragment?.isAdded == true && context != null
}


fun Double.convert(currencyFormat: String?): String {
    try {
        if (this == null || this == 0.0 || this.toString().isEmpty())
            return "0.0"
        var currencyWithDash = ""
        val format = if (currencyFormat?.contains("-") == true) {
            currencyWithDash = currencyFormat
            DecimalFormat(currencyFormat.replace("-", ","))
        } else
            DecimalFormat(currencyFormat)

        format.isDecimalSeparatorAlwaysShown = false
        return if (currencyWithDash.contains("-"))
            format.format(this).toString().replace(",", ".")
        else
            format.format(this).toString()
    } catch (e: Exception) {
        return "0.0"
    }
}

fun changeBaseUrl(domain: String?, mbaUrl: String?): String {
    val stagingList = getStagingList()
    val idnList = getIdnList()

    return if (mbaUrl != null && stagingList.any { it == domain })
        "chilestg-r5.ticketsimply.us"
    else if (mbaUrl != null && idnList.any { it == domain })
        "chilestg-r5.ticketsimply.us"
    else {
        if (domain?.endsWith(".id")!!)
            "mba.ticketsimply.id"
        else
            "mba.ticketsimply.com"
    }
}

fun getIdnList(): MutableList<String> {
    val idnList = mutableListOf<String>()
    idnList.add("idn-stg.ticketsimply.id")
    idnList.add("idn-mba.ticketsimply.id")
    return idnList
}

fun getStagingList(): MutableList<String> {
    val stagingList = mutableListOf<String>()
    stagingList.add("siri-r6.ticketsimply.co.in")
    stagingList.add("srs-qa1.ticketsimply.co.in")
    return stagingList
}


fun getCurrencyFormat(context: Context, currencyFormat: String?): String {
    return currencyFormat?.ifEmpty { context.getString(R.string.indian_currency_format) }
        ?: context.getString(R.string.indian_currency_format)
}

fun getPhoneNumber(passPhone: String?, country: String?): String {
    var finalMobileNumber = passPhone.toString()
    try {
        if (passPhone != null && passPhone?.isNotEmpty() == true) {
            if (country.equals("India", true)) {
                if (passPhone.length == 10) {
                    finalMobileNumber = passPhone
                } else {
                    when {
                        passPhone?.get(0) == '+' -> {
                            finalMobileNumber = passPhone?.removePrefix("+").toString().trim()
                        }

                        passPhone?.get(0) == '0' -> {
                            finalMobileNumber = passPhone?.removePrefix("0").toString().trim()
                        }

                        passPhone?.get(0) == '9'
                                && passPhone?.get(1) == '1'
                                && passPhone?.get(2) == '-' -> {
                            if (passPhone.length > 10) {
                                finalMobileNumber = passPhone?.removePrefix("91-").toString().trim()
                            } else {
                                finalMobileNumber = passPhone
                            }
                        }

                        passPhone?.get(0) == '9'
                                && passPhone?.get(1) == '1' -> {
                            if (passPhone.length > 10) {
                                finalMobileNumber = passPhone?.removePrefix("91").toString().trim()
                            } else {
                                finalMobileNumber = passPhone
                            }
                        }
                    }
                }
            } else if (country?.lowercase() == "indonesia") {
                when {
                    passPhone?.get(0) == '+' -> {
                        finalMobileNumber = passPhone?.removePrefix("+").toString().trim()
                    }

                    passPhone?.get(0) == '0' -> {
                        finalMobileNumber = passPhone?.removePrefix("0").toString().trim()
                    }

                    passPhone?.get(0) == '6'
                            && passPhone?.get(1) == '2'
                            && passPhone?.get(2) == '-' -> {
                        finalMobileNumber = passPhone?.removePrefix("62-").toString().trim()
                    }

                    passPhone?.get(0) == '6'
                            && passPhone?.get(1) == '2' -> {
                        finalMobileNumber = passPhone?.removePrefix("62").toString().trim()
                    }
                }
            } else if (country?.lowercase() == "chile") {
                when {
                    passPhone?.get(0) == '+' -> {
                        finalMobileNumber = passPhone?.removePrefix("+").toString().trim()
                    }

                    passPhone?.get(0) == '0' -> {
                        finalMobileNumber = passPhone?.removePrefix("0").toString().trim()
                    }

                    passPhone?.get(0) == '5'
                            && passPhone?.get(1) == '6'
                            && passPhone?.get(2) == '-' -> {
                        finalMobileNumber = passPhone?.removePrefix("56-").toString().trim()
                    }

                    passPhone?.get(0) == '5'
                            && passPhone?.get(1) == '6' -> {
                        finalMobileNumber = passPhone?.removePrefix("56").toString().trim()
                    }
                }
            }
        }
        //Timber.d("finalMobileNumberCheck:: $finalMobileNumber")
    } catch (_: Exception) {
    }
    return finalMobileNumber
}


fun getBitmapFromView(context: Context, view: View, defaultColor: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(
        view.width, view.height, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    canvas.drawColor(ContextCompat.getColor(context, R.color.white))
    //canvas.drawColor(defaultColor)
    view.draw(canvas)
    return bitmap
}

fun getImageUri(inContext: Activity, inImage: Bitmap): Uri? {

    val path: String = MediaStore.Images.Media.insertImage(
        inContext.contentResolver,
        inImage,
        "Title" + System.currentTimeMillis(),
        null
    )
    return Uri.parse(path)
}

fun getDashboardChartColor(context: Context, color: Int): Int {
    val chartColor: Int

    if (color >= 50) {
        chartColor = ContextCompat.getColor(context, R.color.booked_tickets)
    } else if (color >= 30) {
        chartColor = ContextCompat.getColor(context, R.color.cancelled_tickets)
    } else if (color >= 10) {
        chartColor = ContextCompat.getColor(context, R.color.colorAvailableLadies)
    } else {
        chartColor = ContextCompat.getColor(context, R.color.colorButton)
    }
    return chartColor
}

fun convertDateYYYYMMDDtoDDMMYY(inputDate: String) = try {
    val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy")
    val date: Date = inputFormat.parse(inputDate)
    outputFormat.format(date)
} catch (e: Exception) {
    inputDate
}



fun setBookingType1(type: String) {
    bookingType = type
}

fun getBookingType1(): String {
    return bookingType
}

fun setGlideImage(context: Context, url: String, imageView: ImageView) {
    Glide.with(context)
        .load(url)
        .fitCenter()
        .into(imageView)
}

fun isFilePermissionGranted(activity: Activity): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                activity,
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                activity,
                WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
                0
            )
            false
        } else {
            true
        }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        true
    } else {
        false
    }
}

fun shareView(activity: Activity, view: View) {
    if (isFilePermissionGranted(activity)) {
        /*val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_STREAM,
                getImageUri(
                    activity,
                    getBitmapFromView(
                        activity,
                        view,
                        R.color.colorPrimary
                    )
                )
            )
            type = "image/"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        activity.startActivity(shareIntent)*/
        try {
            val bitmap = getBitmapFromView(activity, view, R.color.colorPrimary)
            val imageUri = getImageUriFromBitmap(activity, bitmap)

            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            activity.startActivity(shareIntent)
        }catch (e:Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }


    }

}

fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
    val cachePath = File(context.cacheDir, "images")
    cachePath.mkdirs()
    val file = File(cachePath, "shared_image.png")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

fun getLast31DayDate(d: Date): Date {
    val c = Calendar.getInstance()
    c.time = d
    c.add(Calendar.DATE, -31)
    d.time = c.time.time
    val date = c.time
    return date

}

fun getNext31DayDate(d: Date): Date {
    val c = Calendar.getInstance()
    c.time = d
    c.add(Calendar.DATE, 30)
    d.time = c.time.time
    val date = c.time
    return date

}


fun getGoogleMapKey(context: Context): String {
    val ai: ApplicationInfo = context.packageManager
        .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    val value = ai.metaData["googleMapKey"]
    return value.toString()
}

fun dateToString(date: Date, outputFormat: String): String {
    val formatter = SimpleDateFormat(outputFormat, getAppLocale())
    return formatter.format(date.time)
}

fun setOccupancyColor(context: Context, textView: TextView, data: Double) {
    if (data <= 30.0) {
        textView.backgroundTintList = ColorStateList.valueOf(
            context.resources.getColor(
                R.color.colorRed2
            )
        )
        textView.setTextColor(context.resources.getColor(R.color.white))

    } else if (data in 30.1..50.0) {
        textView.backgroundTintList = ColorStateList.valueOf(
            context.resources.getColor(
                R.color.lightest_yellow
            )
        )
        textView.setTextColor(context.resources.getColor(R.color.gray_shade_a))
    } else if (data in 50.1..70.0) {
        textView.backgroundTintList = ColorStateList.valueOf(
            context.resources.getColor(
                R.color.color_03_review_02_moderate
            )
        )
        textView.setTextColor(context.resources.getColor(R.color.white))

    } else if (data >= 70.1) {
        textView.backgroundTintList = ColorStateList.valueOf(
            context.resources.getColor(
                R.color.booked_tickets
            )
        )
        textView.setTextColor(context.resources.getColor(R.color.white))

    }
}

fun get31stDate(d: Date): Date {
    val c = Calendar.getInstance()
    c.time = d
    c.add(Calendar.DATE, 31)
    d.time = c.time.time
    val date = c.time
    return date
}


fun adjustFontScale(context: Context) {
    val configuration = context.resources.configuration
    if (configuration.fontScale != 1.0f) {

        configuration.fontScale = 1.0f
        val metrics: DisplayMetrics = context.resources.displayMetrics
        val wm: WindowManager =
            context.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        (context as AppCompatActivity).baseContext.resources.updateConfiguration(
            configuration,
            metrics
        )
    }
}

fun milliSecondsToStringDate(milliSeconds: Long, outputDateFormat: String): String {

    val formatter: DateFormat = SimpleDateFormat(outputDateFormat)
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    calendar.timeInMillis = milliSeconds
    return (formatter.format(calendar.time))

}

fun stringToMilliSeconds(dateInString: String, dateFormat: String): Long {
    val sdf = SimpleDateFormat(dateFormat)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val date = sdf.parse(dateInString)
    return date.time
}

fun setSingleDateCalendar(
    context: Context,
    todayDate: String,
    fromDate: String?,
    //toDate: String?,
    isBeforeFromDateSelection: Boolean,
    isAfterFromDateSelection: Boolean,
    fragmentManager: FragmentManager,
    tag: String,
    onDatesSelected: ((fromDate: String) -> Unit),
    onCancel: (() -> Unit)

) {
    val fromDateMilliseconds = stringToMilliSeconds(fromDate ?: todayDate, DATE_FORMAT_Y_M_D)

    val dateValidatorMin: CalendarConstraints.DateValidator =
        DateValidatorPointForward.from(fromDateMilliseconds)
    val dateValidatorMax: CalendarConstraints.DateValidator =
        DateValidatorPointBackward.before(fromDateMilliseconds)

    val calendarValidatorsList = mutableListOf<CalendarConstraints.DateValidator>()

    if (!isBeforeFromDateSelection || !isAfterFromDateSelection) {
        if (!isBeforeFromDateSelection) {
            calendarValidatorsList.add(dateValidatorMin)
        }

        if (!isAfterFromDateSelection) {
            calendarValidatorsList.add(dateValidatorMax)
        }
    }

    val validators: CalendarConstraints.DateValidator =
        CompositeDateValidator.allOf(calendarValidatorsList)

    val constraintsBuilder = CalendarConstraints.Builder()

    if (calendarValidatorsList.isNotEmpty()) {
        constraintsBuilder.setValidator(validators)
    }

    val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.MaterialCalendarTheme)
            //.setTitleText("Select dates")
            .setCalendarConstraints(constraintsBuilder.build())
            .setSelection(fromDateMilliseconds)
            .build()

    datePicker.show(fragmentManager, tag)
    datePicker.addOnPositiveButtonClickListener {
        val x = datePicker.selection
        if (x != null) {
            val selectedFromDate = milliSecondsToStringDate(x, DATE_FORMAT_Y_M_D)
            onDatesSelected.invoke(selectedFromDate)
        }
    }

    datePicker.addOnNegativeButtonClickListener {
        onCancel.invoke()
    }

    datePicker.addOnCancelListener {
        onCancel.invoke()
    }
}

fun setDoubleDateCalendar(
    context: Context,
    todayDate: String,
    fromDate: String?,
    toDate: String?,
    isBeforeFromDateSelection: Boolean,
    isAfterToDateSelection: Boolean,
    fragmentManager: FragmentManager,
    tag: String,
    onDatesSelected: ((fromDate: String, toDate: String) -> Unit),
    onCancel: (() -> Unit)

) {
    val fromDateMilliseconds = stringToMilliSeconds(fromDate ?: todayDate, DATE_FORMAT_Y_M_D)
    val todayDateMilliseconds = stringToMilliSeconds(todayDate ?: todayDate, DATE_FORMAT_Y_M_D)
    var toDateMilliseconds: Long? = null
    if (toDate != null) {
        toDateMilliseconds = stringToMilliSeconds(toDate ?: todayDate, DATE_FORMAT_Y_M_D)
    }


    val dateValidatorMin: CalendarConstraints.DateValidator =
        DateValidatorPointForward.from(fromDateMilliseconds)
    val dateValidatorMax: CalendarConstraints.DateValidator =
        DateValidatorPointBackward.before(toDateMilliseconds ?: todayDateMilliseconds)

    val calendarValidatorsList = mutableListOf<CalendarConstraints.DateValidator>()

    if (!isBeforeFromDateSelection || !isAfterToDateSelection) {
        if (!isBeforeFromDateSelection) {
            calendarValidatorsList.add(dateValidatorMin)
        }

        if (!isAfterToDateSelection && toDate != null) {
            calendarValidatorsList.add(dateValidatorMax)
        }
    }

    val validators: CalendarConstraints.DateValidator =
        CompositeDateValidator.allOf(calendarValidatorsList)

    val constraintsBuilder = CalendarConstraints.Builder()

    if (calendarValidatorsList.isNotEmpty()) {
        constraintsBuilder.setValidator(validators)
    }

    val datePicker =
        MaterialDatePicker.Builder.dateRangePicker()
            .setTheme(R.style.MaterialCalendarFullscreenTheme)
            //.setTitleText("Select dates")
            .setCalendarConstraints(constraintsBuilder.build())
            .setSelection(
                androidx.core.util.Pair(
                    fromDateMilliseconds,
                    toDateMilliseconds
                )
            )
            .build()

    datePicker.show(fragmentManager, tag)
    datePicker.addOnPositiveButtonClickListener {
        val selectedFromDate = milliSecondsToStringDate(it.first, DATE_FORMAT_Y_M_D)
        val selectedToDate = milliSecondsToStringDate(it.second, DATE_FORMAT_Y_M_D)
        onDatesSelected.invoke(selectedFromDate, selectedToDate)
    }

    datePicker.addOnNegativeButtonClickListener {
        onCancel.invoke()
    }

    datePicker.addOnCancelListener {
        onCancel.invoke()
    }
}

/*fun getPreviousDayMilliSecs(milliSeconds: Long): Long {
    val c = Calendar.getInstance()
    c.time = Date(milliSeconds)
    c.add(Calendar.DATE, -1)
    return c.time.time
}

fun getNextDayMilliSecs(milliSeconds: Long): Long {
    val c = Calendar.getInstance()
    c.time = Date(milliSeconds)
    c.add(Calendar.DATE, 1)
    return c.time.time
}*/


fun getDateFilter(
    context: Context,
    todayDate: String,
    fromDate: String?,
    toDate: String?,
    dateInputFormat: String,
    dateOutputFormat: String,
    isCustomDateFilterSelected: Boolean,
    isCustomDateRangeFilterSelected: Boolean
): MutableList<DateFilterRadioItem> {

    val filterList = mutableListOf<DateFilterRadioItem>()

    filterList.add(
        DateFilterRadioItem(
            0,
            context.getString(R.string.yesterday),
            getYesterdayDateString(todayDate, dateInputFormat, dateOutputFormat),
            todayDate = todayDate,
            fromDate = getYesterdayDateString(todayDate, DATE_FORMAT_Y_M_D, DATE_FORMAT_Y_M_D),
            toDate = getYesterdayDateString(todayDate, dateInputFormat, DATE_FORMAT_Y_M_D)
        )
    )
    filterList.add(
        DateFilterRadioItem(
            1,
            context.getString(R.string.today),
            getCurrentFormattedDate(todayDate, dateInputFormat, dateOutputFormat),
            todayDate = todayDate,
            fromDate = getCurrentFormattedDate(todayDate, dateInputFormat, DATE_FORMAT_Y_M_D),
            toDate = getCurrentFormattedDate(todayDate, dateInputFormat, DATE_FORMAT_Y_M_D)
        )
    )
    filterList.add(
        DateFilterRadioItem(
            2,
            context.getString(R.string.tomorrow),
            getTomorrowDateString(todayDate, dateInputFormat, dateOutputFormat),
            todayDate = todayDate,
            fromDate = getTomorrowDateString(todayDate, dateInputFormat, DATE_FORMAT_Y_M_D),
            toDate = getTomorrowDateString(todayDate, dateInputFormat, DATE_FORMAT_Y_M_D)
        )
    )
    filterList.add(
        DateFilterRadioItem(
            3,
            context.getString(R.string.last_seven_days),
            "${
                getLast7DaysString(
                    todayDate,
                    dateInputFormat,
                    dateOutputFormat
                )
            } - ${getCurrentFormattedDate(todayDate, dateInputFormat, dateOutputFormat)}",
            todayDate = todayDate,
            fromDate = getLast7DaysString(
                todayDate,
                DATE_FORMAT_Y_M_D,
                DATE_FORMAT_Y_M_D
            ),
            toDate = getCurrentFormattedDate(todayDate, dateInputFormat, DATE_FORMAT_Y_M_D)
        )
    )
    filterList.add(
        DateFilterRadioItem(
            4,
            context.getString(R.string.last_thirty_days),
            "${
                getLast30DaysString(
                    todayDate,
                    dateInputFormat,
                    dateOutputFormat
                )
            } - ${getCurrentFormattedDate(todayDate, dateInputFormat, dateOutputFormat)}",
            todayDate = todayDate,
            fromDate = getLast30DaysString(
                todayDate,
                dateInputFormat,
                DATE_FORMAT_Y_M_D
            ),
            toDate = todayDate
        )
    )
    val fDate = fromDate
    val tDate = toDate
    if (fDate != null && isCustomDateFilterSelected) {
        filterList.add(
            DateFilterRadioItem(
                5,
                context.getString(R.string.custom_date),
                getCurrentFormattedDate(fDate, dateInputFormat, dateOutputFormat),
                todayDate = todayDate,
                fromDate = getCurrentFormattedDate(fDate, dateInputFormat, DATE_FORMAT_Y_M_D),
                toDate = toDate
            )
        )
    } else {
        filterList.add(
            DateFilterRadioItem(
                5,
                context.getString(R.string.custom_date),
                context.getString(R.string.not_specified),
                todayDate = todayDate,
                fromDate = todayDate,
                toDate = todayDate
            )
        )
    }
    if (fDate != null && tDate != null && isCustomDateRangeFilterSelected) {
        filterList.add(
            DateFilterRadioItem(
                6,
                context.getString(R.string.custom_date_range),
                "${
                    getCurrentFormattedDate(
                        fDate,
                        dateInputFormat,
                        dateOutputFormat
                    )
                } - ${getCurrentFormattedDate(tDate, dateInputFormat, dateOutputFormat)}",
                todayDate = todayDate,
                fromDate = fromDate,
                toDate = toDate
            )
        )
    } else {
        filterList.add(
            DateFilterRadioItem(
                6,
                context.getString(R.string.custom_date_range),
                context.getString(R.string.not_specified),
                todayDate = todayDate,
                fromDate = todayDate,
                toDate = null
            )
        )
    }
    return filterList
}

fun getYesterdayDateString(
    date: String,
    inputDateFormat: String,
    outputDateFormat: String
): String {
    val d = stringToDate(date, inputDateFormat) ?: Date()
    val c = Calendar.getInstance()
    c.time = d
    c.add(Calendar.DATE, -1)
    d.time = c.time.time
    val newDate = c.time

    return dateToString(newDate, outputDateFormat)
}

fun getCurrentFormattedDate(
    date: String,
    inputDateFormat: String,
    outputDateFormat: String
): String {
    val inputFormat: DateFormat = SimpleDateFormat(inputDateFormat, getAppLocale())
    val outputFormat: DateFormat = SimpleDateFormat(outputDateFormat, getAppLocale())
    val date: Date = inputFormat.parse(date)
    return outputFormat.format(date)
}

fun getAppLocale(): Locale {
    try {
        val language = PreferenceUtils.getlang()

        if(language.isNotEmpty()) {
            return Locale(language)
        } else {
            return Locale.getDefault()
        }
    } catch (e: Exception) {
        return Locale.getDefault()
    }
}

fun getTomorrowDateString(date: String, inputDateFormat: String, outputDateFormat: String): String {
    val d = stringToDate(date, inputDateFormat) ?: Date()
    val c = Calendar.getInstance()
    c.time = d
    c.add(Calendar.DATE, 1)
    d.time = c.time.time
    val newDate = c.time

    return dateToString(newDate, outputDateFormat)
}

fun getLast7DaysString(date: String, inputDateFormat: String, outputDateFormat: String): String {
    val d = stringToDate(date, inputDateFormat) ?: Date()
    val c = Calendar.getInstance()
    c.time = d
    c.add(Calendar.DATE, -6)
    d.time = c.time.time
    val newDate = c.time

    return dateToString(newDate, outputDateFormat)
}

fun getLast30DaysString(date: String, inputDateFormat: String, outputDateFormat: String): String {
    val d = stringToDate(date, inputDateFormat) ?: Date()
    val c = Calendar.getInstance()
    c.time = d
    c.add(Calendar.DATE, -30)
    d.time = c.time.time
    val newDate = c.time

    return dateToString(newDate, outputDateFormat)
}

fun getDaysDifference(date1: String, date2: String, dateFormat: String): Int {
    try {
        val sdf = SimpleDateFormat(dateFormat)
        val d1 = sdf.parse(date1)
        val d2 = sdf.parse(date2)
        val difference_In_Time = d2.time - d1.time
        val difference_In_Days = (TimeUnit.MILLISECONDS.toDays(difference_In_Time) % 365)
        return difference_In_Days.toInt()
    }catch (e: Exception){
        if(BuildConfig.DEBUG){
            e.printStackTrace()
        }
        return 0

    }

}

fun dashboardDateSetText(
    textView: TextView,
    fromDate: String,
    toDate: String?,
    inputDateFormat: String
) {
    var text = ""
    if (toDate != null) {
        if (toDate != "") {
            text = "${
                getCurrentFormattedDate(
                    fromDate,
                    inputDateFormat,
                    DATE_FORMAT_MMM_DD
                )
            } - ${getCurrentFormattedDate(toDate, inputDateFormat, DATE_FORMAT_MMM_DD)}"
        } else {
            text = getCurrentFormattedDate(fromDate, inputDateFormat, DATE_FORMAT_D_MMM_Y)
        }
    } else {
        text = getCurrentFormattedDate(fromDate, inputDateFormat, DATE_FORMAT_D_MMM_Y)
    }

    textView.text = text
}

fun hubsListToStringList(hubsList: MutableList<HubDetails>): MutableList<String> {
    val newList = mutableListOf<String>()
    hubsList.forEach {
        newList.add(it.label ?: "")
    }
    return newList
}

fun jsonToString(obj: Any): String {
    return Gson().toJson(obj)
}

inline fun <reified T> stringToJson(str: String?): T {
    return Gson().fromJson(str, T::class.java)
}

fun maxDigitPreventAfterDecimal(etAddPercentage: TextInputEditText) {

    etAddPercentage.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

    var count = -1

    etAddPercentage.setMaxLength(8)

    etAddPercentage.addTextChangedListener(object : TextWatcher {
        override fun onTextChanged(arg0: CharSequence?, arg1: Int, arg2: Int, arg3: Int) {}
        override fun beforeTextChanged(arg0: CharSequence?, arg1: Int, arg2: Int, arg3: Int) {}

        override fun afterTextChanged(arg0: Editable) {
            if (arg0.isNotEmpty()) {
                val str: String = etAddPercentage.text.toString()

                etAddPercentage.setOnKeyListener { v, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        count--
                        val fArray = arrayOfNulls<InputFilter>(1)
                        fArray[0] = InputFilter.LengthFilter(8)
                        etAddPercentage.filters = fArray
                        //change the editText maximum length to 10.
                        //If we didn't change this the editText maximum length will
                        //be number of digits we previously entered.
                    }
                    false
                }
                val t = str[arg0.length - 1]
                if (t == '.') {
                    count = 0
                }
                if (count >= 0) {
                    if (count == 1) {
                        val fArray = arrayOfNulls<InputFilter>(1)
                        fArray[0] = InputFilter.LengthFilter(arg0.length)
                        etAddPercentage.filters = fArray
                    }
                    count++
                }
            }
        }
    })
}


class InputFilterMinMax : InputFilter {
    private var min: Double
    private var max: Double

    constructor(min: Double, max: Double) {
        this.min = min
        this.max = max
    }

    constructor(min: String, max: String) {
        this.min = min.toDouble()
        this.max = max.toDouble()
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input: Double = (dest.toString() + source.toString()).toDouble()
            if (isInRange(min, max, input)) {
                return null
            }
        } catch (nfe: NumberFormatException) {
        }
        return ""
    }

    private fun isInRange(a: Double, b: Double, c: Double): Boolean {
        return if (b > a) c >= a && c <= b else c >= b && c <= a
    }
}

//fun hasLocationPermission(context: Context) =
//    EasyPermissions.hasPermissions(
//        context,
//        Manifest.permission.ACCESS_FINE_LOCATION
//    )
//
//fun requestLocationPermission(fragment: Fragment){
//    EasyPermissions.requestPermissions(
//        fragment,
//        "This application cannot work without Location Permission",
//        PERMISSION_LOCATION_REQUEST_CODE,
//        Manifest.permission.ACCESS_FINE_LOCATION
//    )
//}
//
//fun hasBackgroundLocationPermission(context: Context): Boolean {
//    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//        return EasyPermissions.hasPermissions(
//            context,
//            Manifest.permission.ACCESS_BACKGROUND_LOCATION
//        )
//    }
//    return true
//}
//
//fun requestBackgroundLocationPermission(fragment: Fragment){
//    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//        EasyPermissions.requestPermissions(
//            fragment,
//            "Background location permission is essential to this application. Without it we will not be able to provide you with our service.",
//            PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE,
//            Manifest.permission.ACCESS_BACKGROUND_LOCATION
//        )
//    }
//}


fun getPrevFullCalenderDate(yearMonth: String): String {
    var yearMonthArr = yearMonth.split("-")
    val inputMonth = yearMonthArr[1].toInt()
    val inputYear = yearMonthArr[0].toInt()
    var leap = when {
        inputYear.toInt() % 4 == 0 -> {
            when {
                inputYear.toInt() % 100 == 0 -> inputYear.toInt() % 400 == 0
                else -> true
            }
        }

        else -> false
    }
    val monthDayList = when {
        leap -> {
            arrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        }

        else -> {
            arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        }
    }
    val cMonth = when {
        inputMonth >= 10 -> {
            inputMonth.toString()
        }

        else -> "0${inputMonth}"
    }
    val startDate = "${yearMonth}-01"
    val monthEndDate = monthDayList[inputMonth - 1]
    val endDate = "${inputYear}-${cMonth}-${monthEndDate}"

    return "$startDate#$endDate"
}


fun getJourneyTime(
    srcDate: String?,
    srcTime: String?,
    destDate: String?,
    destTime: String?
): String {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a")
    val date1 = sdf.parse("$srcDate $srcTime")
    val date2 = sdf.parse("$destDate $destTime")
    cal1.time = date1!!
    cal2.time = date2!!

    return getHoursDiff(cal1, cal2)
}


fun getHoursDiff(c1: Calendar, c2: Calendar): String {
    val d1 = c1.time
    val d2 = c2.time
    val mils = d1.time - d2.time

    val hrs = (mils / (1000 * 60 * 60)).toInt()
    return millisToTimer(mils)!!
}


fun millisToTimer(milliseconds: Long): String? {
    var finalTimerString = ""
    var secondsString = ""

    // Convert total duration into time
    val hours = (milliseconds / (1000 * 60 * 60)).toInt()
    val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
    val seconds = (milliseconds % (1000 * 60 * 60)
            % (1000 * 60) / 1000).toInt()

    // Add hours if there
    if (hours > 0) {
        finalTimerString = hours.toString() + "h"
    }



    finalTimerString = if (minutes == 0) {
        finalTimerString
    } else {
        finalTimerString + " " + minutes + "m"
    }


    // return timer string
    return finalTimerString
}


fun View.setEnabledDisableView(context: Context, enabled: Boolean) {
    isEnabled = enabled
    if (this is ViewGroup)
        (0 until childCount).map(::getChildAt).forEach {
            it.alpha = 0.8f
            it.setEnabledDisableView(context, enabled)
        }
}

fun handleError(
    context: Context,
    errorBody: ResponseBody?,
    anyDataListener: DialogButtonAnyDataListener
) {

    when {
        errorBody != null -> {
            try {
                val obj = JSONObject(errorBody.string())
                if (obj.has("message")) {
                    context.toast(obj.getString("message"))
                } else {
                    context.toast(obj.getString("error"))
                }
                anyDataListener.onDataSend(1, "")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        else -> {
            Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT)
                .show()
        }
    }
}

fun setTextViewDrawableColor(context: Context, textView: TextView, color: Int) {
    for (drawable in textView.compoundDrawables) {
        if (drawable != null) {
            drawable.colorFilter =
                PorterDuffColorFilter(context.getColor(color), PorterDuff.Mode.SRC_IN)
        }
    }
}

fun getValidatedNumber(text: String): String {
    // Start by filtering out unwanted characters like commas and multiple decimals
    val filteredChars = text.filterIndexed { index, c ->
        c in "0123456789" ||                      // Take all digits
                (c == '.' && text.indexOf('.') == index)  // Take only the first decimal
    }
    // Now we need to remove extra digits from the input
    return if (filteredChars.contains('.')) {
        val beforeDecimal = filteredChars.substringBefore('.')
        val afterDecimal = filteredChars.substringAfter('.')
        beforeDecimal.take(8) + "." + afterDecimal.take(2)    // If decimal is present, take first 3 digits before decimal and first 2 digits after decimal
    } else {
        filteredChars.take(8)                     // If there is no decimal, just take the first 3 digits
    }
}

fun convertAmountToWords(amount: Double): String {
    val units = arrayOf("", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine")
    val teens = arrayOf(
        "Ten",
        "Eleven",
        "Twelve",
        "Thirteen",
        "Fourteen",
        "Fifteen",
        "Sixteen",
        "Seventeen",
        "Eighteen",
        "Nineteen"
    )
    val tens = arrayOf(
        "",
        "",
        "Twenty",
        "Thirty",
        "Forty",
        "Fifty",
        "Sixty",
        "Seventy",
        "Eighty",
        "Ninety"
    )
    val thousands = arrayOf("", "Thousand", "Million", "Billion")

    if (amount == 0.0) {
        return "Zero Rupees"
    }

    val sb = StringBuilder()
    var remainingAmount = amount.toLong()
    var count = 0

    while (remainingAmount > 0) {
        val part = remainingAmount % 1000
        if (part > 0) {
            val partWords = convertPartToWords(part.toInt(), units, teens, tens)
            if (count > 0) {
                sb.insert(0, "${partWords} ${thousands[count]} ")
            } else {
                sb.insert(0, partWords)
            }
        }
        remainingAmount /= 1000
        count++
    }

    sb.append(" Rupees")
    return sb.toString()
}

fun convertPartToWords(
    part: Int,
    units: Array<String>,
    teens: Array<String>,
    tens: Array<String>
): String {
    val sb = StringBuilder()

    val hundreds = part / 100
    val tensAndOnes = part % 100

    if (hundreds > 0) {
        sb.append("${units[hundreds]} Hundred")
        if (tensAndOnes > 0) {
            sb.append(" and ")
        }
    }

    if (tensAndOnes < 10) {
        sb.append(units[tensAndOnes])
    } else if (tensAndOnes < 20) {
        sb.append(teens[tensAndOnes - 10])
    } else {
        val tensDigit = tensAndOnes / 10
        val onesDigit = tensAndOnes % 10
        sb.append("${tens[tensDigit]}")
        if (onesDigit > 0) {
            sb.append(" ${units[onesDigit]}")
        }
    }

    return sb.toString()
}

fun amountToWordsIndianFormat(number: Long): String {
    if (number == 0L) return "Zero Rupees"

    val units = arrayOf("", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen")

    val tens = arrayOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")

    fun numToWords(n: Int, suffix: String): String {
        if (n == 0) return ""
        return if (n < 20) units[n] + " " + suffix else tens[n / 10] + " " + units[n % 10] + " " + suffix
    }

    val crore = (number / 10000000).toInt()
    val lakh = ((number / 100000) % 100).toInt()
    val thousand = ((number / 1000) % 100).toInt()
    val hundred = ((number / 100) % 10).toInt()
    val rest = (number % 100).toInt()

    val result = StringBuilder()

    if (crore > 0) result.append(numToWords(crore, "Crore "))
    if (lakh > 0) result.append(numToWords(lakh, "Lakh "))
    if (thousand > 0) result.append(numToWords(thousand, "Thousand "))
    if (hundred > 0) result.append(units[hundred] + " Hundred ")

    if (rest > 0) {
        if (result.isNotEmpty()) result.append("and ")
        result.append(numToWords(rest, ""))
    }

    result.append("Rupees")
    return result.toString().trim()
}

class FileDownloader(
    private val cacheDir: File,
    private val listener: OnDownloadCompleteListener? = null
) : AsyncTask<String, Int, File>() {

    interface OnDownloadCompleteListener {
        fun onDownloadComplete(file: File?)
    }

    override fun doInBackground(vararg params: String): File? {
        val fileUrl = params[0]
        val fileName = params[1]
        var file: File? = null

        try {
            val url = URL(fileUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()

            // Check if the response code indicates success
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val fileLength = connection.contentLength
                val inputStream: InputStream = connection.inputStream

                // Create a file in the app's internal storage
                /*  val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
                  if (!directory.exists()) {
                      directory.mkdirs()
                  }*/

                val directory = File(cacheDir, "downloads")
                if (!directory.exists()) {
                    directory.mkdirs()
                }

                file = File(directory, fileName)
                val outputStream = FileOutputStream(file)

                val buffer = ByteArray(4096)
                var bytesRead: Int
                var totalBytesRead: Long = 0

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                    // Update progress if needed
                    // publishProgress((totalBytesRead * 100 / fileLength).toInt())
                }

                outputStream.close()
                inputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }

    override fun onPostExecute(file: File?) {
        super.onPostExecute(file)
        listener?.onDownloadComplete(file)
    }
}

/*fun printDocumentWiFi(context: Context, file: File, fileName: String) {
    if (!context.isFinishing) {
        val manager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val adapter = DocumentPrintAdapter(file)
        val attributes = PrintAttributes.Builder().build()
        manager.print(fileName, adapter, attributes)
    }
}*/

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }


}

fun richText(normalText: String, boldText: String): Spanned? {
    return Html.fromHtml("$normalText <strong>$boldText</strong>")
}


fun getCurrentTimeStamp(): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val currentDateAndTime = sdf.format(Date())
    return currentDateAndTime
}

fun getDayPrefixFromDate(inputDate: String?, inputDateFormat: String): String {
    val dt1 = SimpleDateFormat(inputDateFormat).parse(inputDate)
    var outputFormat: DateFormat = SimpleDateFormat("EEE")
    return outputFormat.format(dt1)
}

fun parseDoubleCommon(s: String?): Double? {
    return if (s.isNullOrEmpty()) 0.0 else s.toDouble()
}

fun isAppInstalled(applicationContext: Context, packageName: String): Boolean {
    val pm: PackageManager = applicationContext.packageManager
    return try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}


fun decryptMerchantId(encryptedMerchantId: String, dateYMD: String, subDomain: String): String {
    var subdomainAndServerDate =
        subDomain.trim() + dateYMD.replace("-", "").trim()
    var encypt = encryptToBase64(subdomainAndServerDate).trim()
    var decodedMerchantid = decryptToBase64(encryptedMerchantId).trim()
    decodedMerchantid = decodedMerchantid.replace(encypt, "")
    return decodedMerchantid
}

fun blinkText(textTitle: TextView) {
    val blinkAnimation = ObjectAnimator.ofFloat(textTitle, "alpha", 0f, 1f)
    blinkAnimation.duration = 500 // Duration for one cycle of the blink
    blinkAnimation.repeatMode = ObjectAnimator.REVERSE
    blinkAnimation.repeatCount = ObjectAnimator.INFINITE // Repeat forever

    // Start the animation
    blinkAnimation.start()
}

fun blinkingSpanString(title: String, tv: TextView) {
    val text = title
    val spannableString = SpannableString(text)

    // Identify the range of the text to blink
    val start = text.indexOf("*")
    val end = start + "*".length

    // Initially set the text to be transparent
    val blinkSpan = ForegroundColorSpan(Color.TRANSPARENT)
    spannableString.setSpan(blinkSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    // Find the TextView and set the SpannableString as its text
    tv.text = spannableString

    // Create a ValueAnimator to animate the blinking effect
    val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 500 // duration of one blink cycle (500ms)
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE

        addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            val color =
                if (alpha > 0.5f) Color.TRANSPARENT else Color.BLACK // Change to desired color

            // Update the span with the new color
            val newSpan = ForegroundColorSpan(color)
            spannableString.setSpan(newSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Update the TextView with the new SpannableString
            tv.text = spannableString
        }
    }

    // Start the animator
    animator.start()
}


fun showCustomToast(context: Context, message: String, view1: Activity) {
    // Inflate the custom layout
    val inflater: LayoutInflater = context.layoutInflater
    val layout: View = inflater.inflate(
        R.layout.custom_toast_layout,
        view1.findViewById(R.id.custom_toast_container)
    )

    // Find the TextView and ImageView within the custom layout and set their values
    val toastText: TextView = layout.findViewById(R.id.toast_text)
    toastText.text = message
    val toastIcon: TextView = layout.findViewById(R.id.toast_icon)
    // toastIcon.setImageResource(R.drawable.ic_toast_icon) // Set your desired icon

    // Create the Toast object
    with(Toast(context)) {
        duration = Toast.LENGTH_LONG
        view = layout
        show()
    }
}

inline fun <reified T: Any> fetchAPIDataLocally(context: Context): T {
    val inputStream: InputStream = context.resources.openRawResource(com.bitla.ts.R.raw.sample_response)
    val writer: Writer = StringWriter()
    val buffer = CharArray(1024)
    try {
        val reader: Reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        var n: Int
        while (reader.read(buffer).also { n = it } != -1) {
            writer.write(buffer, 0, n)
        }
    } finally {
        inputStream.close()
    }

    val jsonString: String = writer.toString()
    return stringToJson(jsonString)
}



 fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
    // below line is use to generate a drawable.
    val vectorDrawable = ContextCompat.getDrawable(
        context, vectorResId
    )

    // below line is use to set bounds to our vector
    // drawable.
    vectorDrawable!!.setBounds(
        0, 0, vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight
    )

    // below line is use to create a bitmap for our
    // drawable which we have added.
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // below line is use to add bitmap in our canvas.
    val canvas = Canvas(bitmap)

    // below line is use to draw our
    // vector drawable in canvas.
    vectorDrawable.draw(canvas)

    // after generating our bitmap we are returning our
    // bitmap.
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun calculateBearing(start: LatLng, end: LatLng): Float {
    val lat1 = Math.toRadians(start.latitude)
    val lon1 = Math.toRadians(start.longitude)
    val lat2 = Math.toRadians(end.latitude)
    val lon2 = Math.toRadians(end.longitude)

    val dLon = lon2 - lon1
    val y = sin(dLon) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
    return Math.toDegrees(atan2(y, x)).toFloat()
}





@RequiresApi(Build.VERSION_CODES.TIRAMISU)
suspend fun getCityFromLocation(context: Context, location: LatLng) : String? =
    suspendCancellableCoroutine<String?> { cancellableContinuation ->
        location?.let { loc ->
            Geocoder(context).getFromLocation(
                loc.latitude, loc.longitude, 1
            ) { list -> // Geocoder.GeocodeListener
                list.firstOrNull()?.let { address ->
                    cancellableContinuation.resumeWith(
                        Result.success(
                            address.locality
                        )
                    )
                }
            }
        }
    }
 fun openHoursMinsPickerDialog(activity: Activity,context: Context,size: Int, ediText: AppCompatEditText,withoutFormat: Boolean = false) {
    // Create an array of hours from 00 to 24

     var hours : Array<String> ?= null
     if(withoutFormat){
         hours = Array(size) { i -> String.format("%2d", i+1) }

     }else{
         hours = Array(size) { i -> String.format("%02d", i) }
     }

    val inflater = activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val popupView = inflater.inflate(R.layout.popup_window, null)

    // Create the PopupWindow

     val displayMetrics = DisplayMetrics()
     val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
     windowManager.defaultDisplay.getMetrics(displayMetrics)
     val screenHeight = displayMetrics.heightPixels

     var popupHeight = (screenHeight * 0.5).toInt()

     if(size <= 10){
         popupHeight =  LinearLayout.LayoutParams.WRAP_CONTENT
     }

     val popupWindow =
        PopupWindow(popupView, ediText.width, popupHeight, true)

     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         popupWindow.elevation = 12.0f;
     }


    // Set up the ListView in the PopupWindow
    val listView = popupView.findViewById<ListView>(R.id.listView)
    val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, hours)
    listView.adapter = adapter

    // Handle ListView item click
    listView.setOnItemClickListener { _, _, position, _ ->

        ediText.setText(hours[position])
        popupWindow.dismiss()
    }

    // Show the PopupWindow below the EditText
    popupWindow.showAsDropDown(ediText, 0, 0)
}



fun isFirstDateBeforeSecond(firstDate: String, secondDate: String): Boolean {
    // Define the date format
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    // Parse the dates
    val date1: Date? = dateFormat.parse(firstDate)
    val date2: Date? = dateFormat.parse(secondDate)

    // Return true if date1 is before date2, false otherwise
    return date1?.before(date2) == true
}
fun isEndDateBeforeStart(firstDate: String, secondDate: String): Boolean {
    // Define the date format
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    // Parse the dates
    val date1: Date? = dateFormat.parse(firstDate)
    val date2: Date? = dateFormat.parse(secondDate)

    // Return true if date1 is before date2, false otherwise
    return date1?.before(date2) == true
}

fun savePrivilegeDataToFile(`object`: PrivilegeResponseModel, context: Context) {
    val jsonString = GsonBuilder().create().toJson(`object`)

    context.openFileOutput("privilege_data", Context.MODE_PRIVATE).use { outputStream ->
        outputStream.write(jsonString.toByteArray())
    }
}

fun getPrivilegeDataFromFile(context: Context) : PrivilegeResponseModel? {
     try {
        // Read the JSON string from the file
        val jsonString = context.openFileInput("privilege_data").bufferedReader().use { it.readText() }

        // Convert the JSON string back to an object
        return GsonBuilder().create().fromJson(jsonString, PrivilegeResponseModel::class.java)
    } catch (e: FileNotFoundException) {
        // Handle case where the file doesn't exist
       return null
    }
}


fun forceShowKeyboard(editText: EditText) {
    editText.post {
        editText.requestFocus()
        editText.setSelection(editText.text.length) // Move cursor to end if text exists

        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}

fun formatCurrencyWithSymbol(amount: String, fontSize: Int, currencySymbol: String): SpannableString {
    val fullText = "$currencySymbol$amount"
    val spannableString = SpannableString(fullText)

    spannableString.setSpan(
        AbsoluteSizeSpan(fontSize, true),
        0, currencySymbol.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    return spannableString
}

fun showAlertProgressDialog(context: Context): AlertDialog {
    val progressDialog = AlertDialog.Builder(context,R.style.TransparentDialog)
        .setView(LayoutInflater.from(context).inflate(R.layout.dialog_alert_progress, null))
        .setCancelable(false)
        .create()

    return progressDialog

}


fun edgeToEdge(rootView: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {



        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime()) // 👈 keyboard

            val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            view.setPadding(
                systemBars.left,
                systemBars.top, // status bar handled visually
                systemBars.right,
                if (isKeyboardVisible) ime.bottom else systemBars.bottom
            )
            insets
        }
    }
}

fun edgeToEdgeFromOnlyTop(rootView: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime()) // 👈 keyboard

            val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            view.setPadding(
                systemBars.left,
                systemBars.top, // status bar handled visually
                systemBars.right,
                if (isKeyboardVisible) ime.bottom else 0
            )
            insets
        }
    }
}

fun edgeToEdgeFromOnlyBottom(rootView: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime()) // 👈 keyboard

            val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            view.setPadding(
                systemBars.left,
                0, // status bar handled visually
                systemBars.right,
                if (isKeyboardVisible) ime.bottom else systemBars.bottom
            )
            insets
        }
    }
}

fun edgeToEdgeFabButton(activity: Activity,widget: View,dp : Int){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false) // Enables edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(widget) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = bottomInset + dp.toDp // add extra margin if needed
            view.layoutParams = layoutParams
            insets
        }

    }
}



fun Int.toPx(context: Context): Int =
    (this * context.resources.displayMetrics.density).toInt()

val Int.toDp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()


fun sharePdf(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent,
            context.getString(R.string.share_pdf_using)))
    }catch (e: Exception){
        if(BuildConfig.DEBUG){
            e.printStackTrace()
            context.toast(context.getString(R.string.something_went_wrong_please_try_again))
        }
    }

}

 fun printPDF(context: Activity,file: File) {

     if (context.isFinishing || context.isDestroyed) {
         return
     }
    try {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "Document_${System.currentTimeMillis()}"

        printManager.print(jobName, object : PrintDocumentAdapter() {
            override fun onLayout(oldAttributes: PrintAttributes?,
                                  newAttributes: PrintAttributes?,
                                  cancellationSignal: CancellationSignal?,
                                  callback: LayoutResultCallback?,
                                  extras: Bundle?) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }

                val info = PrintDocumentInfo.Builder(file.name)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build()

                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(pages: Array<out PageRange>?,
                                 destination: ParcelFileDescriptor?,
                                 cancellationSignal: CancellationSignal?,
                                 callback: WriteResultCallback?) {
                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null

                try {
                    inputStream = FileInputStream(file)
                    outputStream = FileOutputStream(destination?.fileDescriptor)

                    inputStream.copyTo(outputStream)

                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                } finally {
                    inputStream?.close()
                    outputStream?.close()
                }
            }
        }, null)
    } catch (e: Exception) {
        context.showToast("Failed to print: ${e.message}")
    }
}






