package com.bitla.ts.presentation.view.activity.reservationOption.extendedFare

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.extend_fare_method_name
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.operator_api_key
import com.bitla.ts.data.service_details_method
import com.bitla.ts.databinding.DialogCalendarBinding
import com.bitla.ts.databinding.LayoutActivityExtendedFairBinding
import com.bitla.ts.domain.pojo.Weekdays
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.extend_fare.ExtendFareRequestModel
import com.bitla.ts.domain.pojo.extend_fare.request.RequestBody
import com.bitla.ts.domain.pojo.extend_fare.request.RequestBodyExtendFarePojo
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.adapter.ExtendedWeekdaysAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.viewModel.ExtendFareViewModel
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getDateDMYY
import com.bitla.ts.utils.common.getTodayDate
import com.bitla.ts.utils.common.setDateLocale
import com.bitla.ts.utils.common.stringToDate
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.bitla.tscalender.SlyCalendarDialog
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import okhttp3.internal.toImmutableList
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ExtendedFair : BaseActivity(), OnItemClickListener, SlyCalendarDialog.Callback,
    DialogSingleButtonListener, OnDayClickListener {

    private var privileges: PrivilegeResponseModel? = null
    private var daysTempList:ArrayList<EventDay>?= arrayListOf()
    private lateinit var binding: LayoutActivityExtendedFairBinding
    val TAG = ExtendedFair::class.java.simpleName

    private var loginModelPref = LoginModel()
    private var apiKey: String = ""
    private var bccId: String = ""
    private var resId: Long = 0

    private var weekdays: ArrayList<Weekdays> = arrayListOf()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var weekdaysAdapter: ExtendedWeekdaysAdapter
    private val extendFareViewModel by viewModel<ExtendFareViewModel<Any?>>()
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var fromDate: String? = null
    private var toDate: String? = null
    private var applyDate: String? = null
    private var performDate: String? = null
    private var dateType: String? = null
    private var constFormat = "true"
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var route_Id: Int? = null
    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private lateinit var mcalendar: Calendar
    private var serviceNumber: String? = null
    private var locale: String? = ""
    private var dayList: ArrayList<EventDay>? = arrayListOf()

    private var shouldExtendFareSetting : Boolean = false
    private var pinSize = 0
    private var country: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mcalendar = Calendar.getInstance()
        day = mcalendar.get(Calendar.DAY_OF_MONTH)
        year = mcalendar.get(Calendar.YEAR)
        month = mcalendar.get(Calendar.MONTH)

    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    override fun initUI() {
        setDateLocale(PreferenceUtils.getlang(), this@ExtendedFair)
        binding = LayoutActivityExtendedFairBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }


        //setWeekdaysAdapter()
        setWeekdays()
        getIntentData()
        getPref()
        setButtonObservable()
        callServiceApi()
        setUpObserver()
        // setNetworkConnectionObserver

        binding.extendedFairToolbar.imageOptionLayout.gone()


        serviceNumber = intent.getStringExtra("serviceNumber")

        binding.editMultistationFare.setOnClickListener {
            val intent = Intent(this, MultistationFare::class.java)
            intent.putExtra("serviceNumber", serviceNumber)
            startActivity(intent)


        }
        binding.copyallRadio.setOnClickListener {
            onRadioButtonClicked(binding.copyallRadio)
            setButtonObservable()
        }

        binding.copyfareOnlyRadio.setOnClickListener {
            onRadioButtonClicked(binding.copyfareOnlyRadio)
            setButtonObservable()
        }

        binding.selectionTypeRG.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.range_radio -> {
                    setRangeViews()
                }
                R.id.multiple_radio -> {
                    setMultipleViews()
                }
            }
        }


        clickListener()
//        binding.includeHeader.textHeaderTitle.text = "Current location of AP-39-TB-5037 "
//        binding.includeHeader.headerTitleDesc.text = "15 Sep 2020 - Bangalore - Tvm 2+2, Semi Sleeper,AC, LED (40 seats), SEA BIRD"
//        binding.includeHeader.imageHeaderRightImage.setOnClickListener(View.OnClickListener {
//        })

        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            extendFareViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }

    private fun setMultipleViews() {
        if (binding.copyfareOnlyRadio.isChecked) {
            binding.layoutMultipleDate.visible()
            binding.layoutFromdate.gone()
            binding.layoutTodate.gone()
            binding.layoutApplydate.gone()
            binding.textView4.gone()
            binding.linearLayout.gone()
        } else {
            binding.layoutMultipleDate.visible()
            binding.layoutFromdate.gone()
            binding.layoutTodate.gone()
            binding.layoutApplydate.gone()
            binding.textView4.gone()
            binding.linearLayout.gone()
        }

    }

    private fun setRangeViews() {
        binding.textView4.visible()
        binding.linearLayout.visible()
        if (binding.copyfareOnlyRadio.isChecked) {
            binding.layoutFromdate.gone()
            binding.layoutTodate.gone()
            binding.layoutApplydate.visible()
            binding.layoutMultipleDate.gone()
        } else {

            binding.layoutFromdate.visible()
            binding.layoutTodate.visible()
            binding.layoutApplydate.gone()
            binding.layoutMultipleDate.gone()

        }

    }


    private fun clickListener() {
        binding.linearlayoutweekdays.layoutSunday.setOnClickListener(this)
        binding.linearlayoutweekdays.layoutMonday.setOnClickListener(this)
        binding.linearlayoutweekdays.layoutTuesday.setOnClickListener(this)
        binding.linearlayoutweekdays.layoutWednesday.setOnClickListener(this)
        binding.linearlayoutweekdays.layoutThursday.setOnClickListener(this)
        binding.linearlayoutweekdays.layoutFriday.setOnClickListener(this)
        binding.linearlayoutweekdays.layoutSaturday.setOnClickListener(this)
        binding.createRateCard.setOnClickListener(this)
        binding.layoutFromdate.setOnClickListener(this)
        binding.layoutTodate.setOnClickListener(this)
        binding.layoutSelectDate.setOnClickListener(this)
        binding.layoutApplydate.setOnClickListener(this)
        binding.extendedFairToolbar.toolbarImageLeft.setOnClickListener(this)
        binding.layoutMultipleDate.setOnClickListener(this)
    }
    /*private fun setWeekdaysAdapter() {
        val daysArray = DateFormatSymbols.getInstance(Locale.getDefault()).shortWeekdays
        daysArray.forEach {
            if (it.isNotEmpty()) {
                val days = Weekdays(it, false)
                weekdays.add(days)
            }
        }


        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvWeekdays.layoutManager = layoutManager
        weekdaysAdapter =
            ExtendedWeekdaysAdapter(this, this, weekdays)
        binding.rvWeekdays.adapter = weekdaysAdapter

    }*/

    @SuppressLint("LogNotTimber")
    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.toolbar_image_left -> {
                finish()
            }
            R.id.layout_sunday -> {
                val txtview = findViewById<TextView>(R.id.tvSunday)
                setDaysBackground(0, v, txtview)
            }
            R.id.layout_monday -> {
                val txtview = findViewById<TextView>(R.id.tvMonday)
                setDaysBackground(1, v, txtview)
            }
            R.id.layout_tuesday -> {
                val txtview = findViewById<TextView>(R.id.tvTuesday)
                setDaysBackground(2, v, txtview)
            }
            R.id.layout_wednesday -> {
                val txtview = findViewById<TextView>(R.id.tvWednesday)
                setDaysBackground(3, v, txtview)
            }
            R.id.layout_thursday -> {
                val txtview = findViewById<TextView>(R.id.tvThursday)
                setDaysBackground(4, v, txtview)
            }
            R.id.layout_friday -> {
                val txtview = findViewById<TextView>(R.id.tvFriday)
                setDaysBackground(5, v, txtview)
            }
            R.id.layout_saturday -> {
                val txtview = findViewById<TextView>(R.id.tvSaturday)
                setDaysBackground(6, v, txtview)
            }
            R.id.create_rateCard -> {
                //callExtendFareApi()
                fromDate = binding.tvFromDate.text.toString()
                toDate = binding.tvToDate.text.toString()
                applyDate = binding.tvApplyDate.text.toString()
                performDate = binding.tvSelectDate.text.toString()
                val isMultipleDates = binding.multipleRadio.isChecked
                if (binding.copyallRadio.isChecked && binding.rangeRadio.isChecked) {
                    extendFareViewModel.validation(
                        fromDate,
                        toDate,
                        null,
                        performDate,
                        "all",
                        binding.tvMultipleDate.text.toString(), isMultipleDates
                    )
                } else if (binding.copyfareOnlyRadio.isChecked && binding.rangeRadio.isChecked) {
                    extendFareViewModel.validation(
                        null,
                        null,
                        applyDate,
                        performDate,
                        "fare",
                        binding!!.tvMultipleDate.text.toString(),
                        isMultipleDates
                    )
                } else if (binding.copyallRadio.isChecked && binding.multipleRadio.isChecked) {
                    extendFareViewModel.validation(
                        null,
                        null,
                        applyDate,
                        performDate,
                        "fare",
                        binding.tvMultipleDate.text.toString(),
                        isMultipleDates
                    )
                } else if (binding.copyfareOnlyRadio.isChecked && binding.multipleRadio.isChecked) {
                    extendFareViewModel.validation(
                        null,
                        null,
                        applyDate,
                        performDate,
                        "fare",
                        binding!!.tvMultipleDate.text.toString(),
                        isMultipleDates
                    )
                } else {
                    toast("Please select relevant radio button")
                }
            }
            R.id.layout_fromdate -> {
                dateType = getString(R.string.fromDate)
                if (binding.tvToDate.text != getString(R.string.toDate)) {
                    binding.tvToHint.gone()
                    binding.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline)
                    binding.tvToDate.text = getString(R.string.toDate)
                    val scale = resources.displayMetrics.density
                    val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                    binding.tvToDate.setPadding(paddingtLeftRightinDp, 0, paddingtLeftRightinDp, 0)
                }
                SlyCalendarDialog()
                    .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setMinDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setSingle(true)
                    .setFirstMonday(false)
                    .setCallback(this)
                    .show(supportFragmentManager, TAG)
                fromDate = binding.tvFromDate.text.toString()

            }
            R.id.layout_todate -> {
                dateType = getString(R.string.toDate)
                val fromDate: String = binding.tvFromDate.text.toString()
                val toDate1: String = binding.tvToDate.text.toString()

                if (fromDate == getString(R.string.fromDate)) {
                    toast("Please select from date")
                } else {

                    if (fromDate != getString(R.string.fromDate) && toDate1 != getString(R.string.toDate)) // both the dates already selected
                    {
                        SlyCalendarDialog()
                            .setStartDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setEndDate(stringToDate(toDate1, DATE_FORMAT_D_M_Y))
                            .setMinDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setSingle(false)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(supportFragmentManager, TAG)
                    } else if (fromDate != getString(R.string.fromDate)) // only from date selected
                    {
                        SlyCalendarDialog()
                            .setStartDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setMinDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setSingle(false)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(supportFragmentManager, TAG)
                    } else {
                        SlyCalendarDialog()
                            .setSingle(false)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(supportFragmentManager, TAG)
                    }
                }
                toDate = binding.tvToDate.text.toString()

            }
            R.id.layout_select_date -> {
                dateType = getString(R.string.select_date)
                /*var startDate: String = ""
                if (getDateDMY("1970-01-01") != null) {
                    startDate = getDateDMY("1970-01-01").toString()
                } else {
                    startDate = getTodayDate()
                }
                SlyCalendarDialog()
                    .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setMinDate(stringToDate(startDate, DATE_FORMAT_D_M_Y))
                    .setSingle(true)
                    .setFirstMonday(false)
                    .setCallback(this)
                    .show(supportFragmentManager, TAG)
                */
                openDateDialog()
                performDate = binding.tvSelectDate.text.toString()
                setButtonObservable()

            }

            R.id.layout_applydate -> {
                dateType = getString(R.string.apply_date)
                SlyCalendarDialog()
                    .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setMinDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setSingle(true)
                    .setFirstMonday(false)
                    .setCallback(this)
                    .show(supportFragmentManager, TAG)
                applyDate = binding.tvApplyDate.text.toString()
                setButtonObservable()

            }

            R.id.layout_multiple_date -> {
                showCalendar()
            }
        }
    }


    fun showCalendar() {


        val dialog = Dialog(this)
        dialog.setCancelable(true)
        val dialogBinding = DialogCalendarBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        val calendar= Calendar.getInstance()
        calendar.add(Calendar.DATE,-1)
        dialogBinding.calendarView.setMinimumDate(calendar)

        val calendars: MutableList<Calendar> = ArrayList()
        daysTempList!!.clear()
        for (i in 0 until dayList!!.size) {
            calendars.add(i, dayList!![i].calendar)
            daysTempList!!.add(dayList!![i])
        }


        dialogBinding.calendarView.selectedDates = calendars.toImmutableList()
        dialogBinding.calendarView.selectedDates = calendars

        dialogBinding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                if (daysTempList!!.contains(eventDay)) {
                    daysTempList!!.remove(eventDay)
                } else {
                    if (daysTempList!!.size < 30) {
                        daysTempList!!.add(eventDay)
                    } else {
                        toast(baseContext.getString(R.string.can_not_select_more_then_30_seats))
                        daysTempList!!.remove(eventDay)
                    }
                }
            }
        })

        dialogBinding.cancelTV.setOnClickListener {

            dialog.cancel()
        }


        dialogBinding.doneTV.setOnClickListener {
            var dates: ArrayList<String>? = arrayListOf()
            binding.tvMultipleDate.text = ""
            dialog.cancel()
            dayList!!.clear()
            if (daysTempList?.size!! > 0) {
                for (i in 0 until daysTempList!!.size) {
                    val format1 = SimpleDateFormat("dd-MM-yyyy")

                    var inActiveDate: String? = null

                    try {
                        inActiveDate = format1.format(daysTempList!![i].calendar.time)
                        println(inActiveDate)
                    } catch (e1: ParseException) {
                        e1.printStackTrace()
                    }
                    dates!!.add(inActiveDate!!)
                    dayList!!.add(daysTempList!![i])


                }
            }

            if (dates!!.size > 2) {
                binding.moreTV.visible()
                binding.moreTV.text="+"+(dates.size - 2).toString() + " more"
                binding.tvMultipleDate.apply {
                    visibility = View.VISIBLE
                    text = dates[0] + ", " + dates[1]
                }
            } else {
                binding.moreTV.gone()
                binding.moreTV.text=""
                for (i in 0 until dates.size) {
                    if (binding.tvMultipleDate.text.toString().isEmpty()) {
                        binding.tvMultipleDate.text = dates[i]
                    } else {
                        binding.tvMultipleDate.text =
                            binding.tvMultipleDate.text.toString() + ", " + dates[i]
                    }
                }
            }

        }







}


override fun onClickOfNavMenu(position: Int) {
}

override fun onClick(view: View, position: Int) {
}

override fun onButtonClick(view: Any, dialog: Dialog) {
    TODO("Not yet implemented")
}

override fun onClickOfItem(data: String, position: Int) {
}

override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
}

private fun onRadioButtonClicked(v: View) {
    when (v.id) {
        R.id.copyall_radio -> {

            binding.copyfareOnlyRadio.isChecked = false
            if (binding.rangeRadio.isChecked) {
                binding.layoutFromdate.visible()
                binding.layoutTodate.visible()
                binding.layoutApplydate.gone()
                binding.layoutMultipleDate.gone()
            } else {
                binding.layoutFromdate.gone()
                binding.layoutTodate.gone()
                binding.layoutApplydate.gone()
                binding.layoutMultipleDate.visible()
            }
        }
        R.id.copyfare_only_radio -> {
            binding.copyallRadio.isChecked = false
            if (binding.rangeRadio.isChecked) {
                binding.layoutFromdate.gone()
                binding.layoutTodate.gone()
                binding.layoutApplydate.visible()
                binding.layoutMultipleDate.gone()
            } else {
                binding.layoutFromdate.gone()
                binding.layoutTodate.gone()
                binding.layoutApplydate.gone()
                binding.layoutMultipleDate.visible()
            }
        }
    }
}

fun setWeekdays() {
    weekdays.add(Weekdays("Sun", true))
    weekdays.add(Weekdays("Mon", true))
    weekdays.add(Weekdays("Tue", true))
    weekdays.add(Weekdays("Wed", true))
    weekdays.add(Weekdays("Thu", true))
    weekdays.add(Weekdays("Fri", true))
    weekdays.add(Weekdays("Sat", true))
    findViewById<TextView>(R.id.tvSunday).text = weekdays[0].day
    findViewById<TextView>(R.id.tvMonday).text = weekdays[1].day
    findViewById<TextView>(R.id.tvTuesday).text = weekdays[2].day
    findViewById<TextView>(R.id.tvWednesday).text = weekdays[3].day
    findViewById<TextView>(R.id.tvThursday).text = weekdays[4].day
    findViewById<TextView>(R.id.tvFriday).text = weekdays[5].day
    findViewById<TextView>(R.id.tvSaturday).text = weekdays[6].day
}

private fun setDaysBackground(
    index: Int,
    view: View,
    textView: TextView
) {
    if (!weekdays[index].isSelected) {
        // Change this color for selection
        view.setBackgroundColor(Color.parseColor("#00adb5"))
        textView.setTextColor(Color.parseColor("#ffffff"))
        weekdays[index].isSelected = true
    } else {
        view.setBackgroundColor(Color.parseColor("#ffffff"))
        textView.setTextColor(Color.parseColor("#9b9b9b"))
        weekdays[index].isSelected = false

    }
}

private fun getSelectedWeekdays(): String {
    var days: String = ""
    weekdays.forEach {
        days += if (it.isSelected) {
            "1"
        } else
            "0"
    }
    return days
}

@SuppressLint("SetTextI18n", "LogNotTimber")
private fun setUpObserver() {

    extendFareViewModel.loadingState.observe(this) {
        when (it) {
            LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
            LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
            else -> {
                it.msg?.let { it1 -> toast(it1) }
                binding.includeProgress.progressBar.gone()
            }
        }

    }

    extendFareViewModel.extendFare.observe(this) {
        binding.includeProgress.progressBar.gone()
        if (it != null) {
            when (it.code) {
                200 -> {
                    DialogUtils.extendFareRateCard(
                        this,
                        getString(R.string.successfully_created_rate_card)
                    )
                }
                401 -> {
                    /*DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    showUnauthorisedDialog()

                }
                else -> {
                    if (it.message != null) {
                        it.message?.let { it1 -> toast(it1) }
                    }
                }
            }
        } else {
            toast(getString(R.string.server_error))
        }
    }

    sharedViewModel.serviceDetails.observe(this) {
        binding.includeProgress.progressBar.gone()
        stopShimmerEffect()
        if (it != null) {
            if (it.code == 200) {
                val mainOpId = it.body.routeId.toString()
                route_Id = it.body.routeId
                val origin = it.body.origin?.name
                val destination = it.body.destination?.name
                var arrivalDate = it.body.travelDate
                val arrivalTime = it.body.arrTime
                val departureTime = it.body.depTime
                val busType = it.body.busType
                val serviceNumber = it.body.number

                arrivalDate = arrivalDate?.replace("/", "-")
                arrivalDate = arrivalDate?.let { it1 ->
                    getDateDMYY(
                        it1
                    )
                }


                binding.extendedFairToolbar.textHeaderTitle.text =
                    resources.getString(R.string.extend_fare_settings)
                binding.extendedFairToolbar.headerTitleDesc.text =
                    "$serviceNumber | $arrivalDate | $arrivalTime-$departureTime | $origin-$destination $busType"

            } else
                it.message?.let { it1 -> toast(it1) }
        } else {
            toast(getString(R.string.server_error))
        }
    }

    extendFareViewModel.validationData.observe(this) {
        if (it == getString(R.string.empty)) {
            pinAuthDialogBox()
        } else {
            toast(it)
        }
    }

    extendFareViewModel.changeButtonBackground.observe(this) {
        if (it) {
            binding.createRateCard.apply {
                setBackgroundResource(R.drawable.button_selected_bg)
                isEnabled = true
            }
        } else {
            binding.createRateCard.apply {
                setBackgroundResource(R.drawable.button_default_bg)
                isEnabled = false
            }
        }
    }


}

    private fun pinAuthDialogBox() {
        if (shouldExtendFareSetting && country.equals("india", true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = this,
                fragmentManager = supportFragmentManager,
                pinSize,
                getString(R.string.extend_fare),
                onPinSubmitted = { pin: String ->
                    callExtendFareApi(pin)
                },
                onDismiss = null
            )
        } else {
            callExtendFareApi("")
        }
    }

private fun callExtendFareApi(authPin: String) {
    if (isNetworkAvailable()) {
        val extend_fair = RequestBodyExtendFarePojo()
        extend_fair.route_id = route_Id
        if (binding.copyallRadio.isChecked && !binding.multipleRadio.isChecked) {
            extend_fair.start_date = binding.tvFromDate.text.toString()
            extend_fair.end_date = binding.tvToDate.text.toString()
            extend_fair.copy_type = "all"

            extend_fair.apply_date = null
        } else if (binding.copyfareOnlyRadio.isChecked && !binding.multipleRadio.isChecked) {
            extend_fair.start_date = null
            extend_fair.end_date = null
            extend_fair.apply_date = binding.tvApplyDate.text.toString()
            extend_fair.copy_type = "fare"
        } else if (binding.copyallRadio.isChecked && binding.multipleRadio.isChecked) {
            extend_fair.start_date = ""
            extend_fair.end_date = ""
            extend_fair.apply_date = ""
            extend_fair.copy_type = "all"
            if (dayList?.size!! > 0) {
                for (i in 0 until dayList!!.size) {
                    val format1 = SimpleDateFormat("dd-MM-yyyy")
                    var inActiveDate: String? = null
                    try {
                        inActiveDate = format1.format(dayList!![i].calendar.time)
                        println(inActiveDate)
                    } catch (e1: ParseException) {
                        e1.printStackTrace()
                    }

                    extend_fair.multipleDates.add(inActiveDate!!)
                }
            }

        } else if (binding.copyfareOnlyRadio.isChecked && binding.multipleRadio.isChecked) {
            extend_fair.start_date = ""
            extend_fair.end_date = ""
            extend_fair.apply_date = ""
            extend_fair.copy_type = "fare"
            if (dayList?.size!! > 0) {
                for (i in 0 until dayList!!.size) {
                    val format1 = SimpleDateFormat("dd-MM-yyyy")
                    var inActiveDate: String? = null
                    try {
                        inActiveDate = format1.format(dayList!![i].calendar.time)
                        println(inActiveDate)
                    } catch (e1: ParseException) {
                        e1.printStackTrace()
                    }

                    extend_fair.multipleDates.add(inActiveDate!!)
                }
            }


        }

        extend_fair.pick_from_date = binding.tvSelectDate.text.toString()

        if(binding.multipleRadio.isChecked){
            weekdays.clear()
            setWeekdays()
        }
        extend_fair.weekly_schedule_copy = getSelectedWeekdays()

        val req_body = RequestBody()
        req_body.api_key = apiKey
        req_body.extend_fare = extend_fair
        req_body.auth_pin = authPin

        val extendFairRequestModel = ExtendFareRequestModel()
        extendFairRequestModel.bccId = bccId
        extendFairRequestModel.format = format_type
        extendFairRequestModel.methodName = extend_fare_method_name
        extendFairRequestModel.req_body = req_body

        extendFareViewModel.extendFareApi(
            req_body,
            extend_fare_method_name
        )
    } else {
        noNetworkToast()
    }
}

private fun getPref() {
    privileges = getPrivilegeBase()
    loginModelPref = PreferenceUtils.getLogin()
    bccId = PreferenceUtils.getBccId().toString()
    apiKey = loginModelPref.api_key
    locale = PreferenceUtils.getlang()

    privileges?.let { privilegeResponseModel ->
        pinSize = privilegeResponseModel.pinCount ?: 6
        shouldExtendFareSetting = privilegeResponseModel.pinBasedActionPrivileges?.extendFareSetting ?: false
        country = privilegeResponseModel.country
    }
}

override fun onCancelled() {

}

override fun onDataSelected(
    firstDate: Calendar?,
    secondDate: Calendar?,
    hours: Int,
    minutes: Int
) {
    val scale = resources.displayMetrics.density

    if (firstDate != null) {
        if (secondDate == null) {
            firstDate.set(Calendar.HOUR_OF_DAY, hours)
            firstDate.set(Calendar.MINUTE, minutes)


            if (dateType != null && dateType == getString(R.string.fromDate)) {
                fromDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(firstDate.time)
                binding.tvFromDate.visible()
                binding.tvFromHint.visible()
                binding.tvFromDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)

                binding.tvFromDate.text = fromDate
                val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
                val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                binding.tvFromDate.setPadding(
                    paddingtLeftRightinDp,
                    paddingtTopinDp,
                    paddingtLeftRightinDp,
                    0
                )
            } else if (dateType != null && dateType == getString(R.string.select_date)) {
                val selectDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(firstDate.time)
                binding.tvSelectDate.visible()
                binding.tvSelectDateHint.visible()
                binding.tvSelectDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)

                binding.tvSelectDate.text = selectDate
                val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
                val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                binding.tvSelectDate.setPadding(
                    paddingtLeftRightinDp,
                    paddingtTopinDp,
                    paddingtLeftRightinDp,
                    0
                )
            } else if (dateType != null && dateType == getString(R.string.apply_date)) {
                val applyDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(firstDate.time)
                binding.tvApplyDate.visible()
                binding.tvApplyDateHint.visible()
                binding.tvApplyDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)

                binding.tvApplyDate.text = applyDate
                val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
                val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                binding.tvApplyDate.setPadding(
                    paddingtLeftRightinDp,
                    paddingtTopinDp,
                    paddingtLeftRightinDp,
                    0
                )
            } else {
                toDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(firstDate.time)

                binding.tvToDate.text = toDate
                binding.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                binding.tvToHint.visible()
                val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
                val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                binding.tvToDate.setPadding(
                    paddingtLeftRightinDp,
                    paddingtTopinDp,
                    paddingtLeftRightinDp,
                    0
                )
            }

        } else {
            binding.tvToHint.visible()
            binding.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
            fromDate = SimpleDateFormat(
                DATE_FORMAT_D_M_Y,
                Locale.getDefault()
            ).format(firstDate.time)
            binding.tvFromDate.text = fromDate


            toDate = SimpleDateFormat(
                DATE_FORMAT_D_M_Y,
                Locale.getDefault()
            ).format(secondDate.time)
            binding.tvToDate.text = toDate
            val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
            val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
            binding.tvToDate.setPadding(
                paddingtLeftRightinDp,
                paddingtTopinDp,
                paddingtLeftRightinDp,
                0
            )
        }
    } else {
        binding.tvFromDate.setBackgroundResource(R.drawable.header_gradient_bg_underline)
        binding.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
        binding.tvFromDate.gone()
        binding.tvToHint.gone()
    }
    setButtonObservable()
}

private fun getIntentData() {
    sourceId = intent.getStringExtra("originID").toString()
    destinationId = intent.getStringExtra("destinationID").toString()
    resId = intent.getLongExtra("reservationID", 0)

    PreferenceUtils.putString("reservationid", resId.toString())
    PreferenceUtils.putString("ViewReservation_OriginId", sourceId)
    PreferenceUtils.putString("ViewReservation_DestinationId", destinationId)

}

@SuppressLint("LogNotTimber")
private fun callServiceApi() {

    if (isNetworkAvailable()) {
        startShimmerEffect()
        /* sharedViewModel.getServiceDetails(
             loginModelPref.auth_token,
             loginModelPref.api_key, serviceDetailsRequest, service_details_method
         )*/
        sharedViewModel.getServiceDetails(
            reservationId = resId.toString(),
            apiKey = loginModelPref.api_key,
            originId = sourceId,
            destinationId = destinationId,
            operatorApiKey = operator_api_key,
            locale = locale ?: "",
            apiType = service_details_method,
            excludePassengerDetails = false
        )
    } else {
        noNetworkToast()
    }
}

private fun setButtonObservable() {
    fromDate = binding.tvFromDate.text.toString()
    toDate = binding.tvToDate.text.toString()
    applyDate = binding.tvApplyDate.text.toString()
    performDate = binding.tvSelectDate.text.toString()
    val isMultipleDates = binding.multipleRadio.isChecked

    if (binding.copyallRadio.isChecked && binding.rangeRadio.isChecked) {
        extendFareViewModel.changeButtonBackground(
            fromDate,
            toDate,
            null,
            performDate,
            "all",
            null,
            isMultipleDates
        )
    } else if (binding.copyfareOnlyRadio.isChecked && binding.rangeRadio.isChecked) {
        extendFareViewModel.changeButtonBackground(
            null,
            null,
            applyDate,
            performDate,
            "fare",
            null,
            isMultipleDates
        )
    } else if (binding.copyallRadio.isChecked && binding.multipleRadio.isChecked) {
        extendFareViewModel.changeButtonBackground(
            null,
            null,
            applyDate,
            performDate,
            "all",
            binding.tvMultipleDate.toString(),
            isMultipleDates
        )
    } else if (binding.copyfareOnlyRadio.isChecked && binding.multipleRadio.isChecked) {
        extendFareViewModel.changeButtonBackground(
            null,
            null,
            applyDate,
            performDate,
            "fare",
            binding.tvMultipleDate.toString(),
            isMultipleDates
        )
    } else {
        toast("Please select relevant radio button")
    }
}

@SuppressLint("SimpleDateFormat")
private fun openDateDialog() {
    val scale = resources.displayMetrics.density

    val listener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth
            ->
            val dateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
            val date = dateFormat.parse("$dayOfMonth-${monthOfYear + 1}-$year")
            binding.tvSelectDate.text = dateFormat.format(date).toString()
            performDate = binding.tvSelectDate.text.toString()
            binding.tvSelectDate.visible()
            binding.tvSelectDateHint.visible()
            val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
            val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
            binding.tvSelectDate.setPadding(
                paddingtLeftRightinDp,
                paddingtTopinDp,
                paddingtLeftRightinDp,
                0
            )
            setButtonObservable()
        }
    val dpDialog = DatePickerDialog(this, listener, year, month, day)
    val dateInString: String = getTodayDate()
    val simpleDateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
    val calendar = Calendar.getInstance()
    calendar.time = simpleDateFormat.parse(dateInString)
    calendar.add(Calendar.DATE, 10)
    dpDialog.datePicker.maxDate = System.currentTimeMillis()
    dpDialog.show()
}

private fun startShimmerEffect() {
    binding.shimmerExtendFare.visible()
    binding.extendedFairToolbar.root.gone()
    binding.scrollView2.gone()
    binding.shimmerExtendFare.startShimmer()
}

private fun stopShimmerEffect() {
    binding.shimmerExtendFare.gone()
    binding.extendedFairToolbar.root.visible()
    binding.scrollView2.visible()
    if (binding.shimmerExtendFare.isShimmerStarted) {
        binding.shimmerExtendFare.stopShimmer()
    }
}

override fun onSingleButtonClick(str: String) {
    if (str == getString(R.string.unauthorized)) {
        PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}

override fun onDayClick(eventDay: EventDay) {

}
}