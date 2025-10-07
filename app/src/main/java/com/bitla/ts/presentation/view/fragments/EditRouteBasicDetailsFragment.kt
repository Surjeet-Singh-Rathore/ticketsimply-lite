package com.bitla.ts.presentation.view.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.DEFAULT_HOURS
import com.bitla.ts.data.DEFAULT_MINUTES
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.AdapterSearchBpdpBinding
import com.bitla.ts.databinding.FragmentEditRouteBasicDetailsBinding
import com.bitla.ts.domain.pojo.Weekdays
import com.bitla.ts.domain.pojo.coach_type.CoachTypeListData
import com.bitla.ts.domain.pojo.create_route.BasicDetailsData
import com.bitla.ts.domain.pojo.create_route.OtherData
import com.bitla.ts.domain.pojo.create_route.ScheduleData
import com.bitla.ts.domain.pojo.get_route.GetRouteData
import com.bitla.ts.domain.pojo.hub_dropdown.HubDropdownData
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.route_manager.CitiesListData
import com.bitla.ts.domain.pojo.update_route.ViaCitiesData
import com.bitla.ts.presentation.adapter.SourceDestinatinAdapter
import com.bitla.ts.presentation.view.activity.RouteServiceManagerActivity
import com.bitla.ts.presentation.viewModel.RouteManagerViewModel
import com.bitla.ts.utils.common.isFirstDateBeforeSecond
import com.bitla.ts.utils.common.routeId
import com.bitla.ts.utils.constants.COACH_TYPE
import com.bitla.ts.utils.constants.DESTINATION
import com.bitla.ts.utils.constants.HUB
import com.bitla.ts.utils.constants.SOURCE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.gson.JsonObject
import gone
import isNetworkAvailable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import toast
import visible
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class EditRouteBasicDetailsFragment : BaseFragment(), DialogButtonAnyDataListener {

    companion object {
        val TAG = EditRouteBasicDetailsFragment::class.java.simpleName
    }

    private var finalJsonObject: JsonObject? = null
    private var destinationId: String = ""
    private var selectedCoachId: String = ""
    private var coachList: java.util.ArrayList<CoachTypeListData> = arrayListOf()
    private var selectedHubId: String = ""
    private var hubList: ArrayList<HubDropdownData> = arrayListOf()
    private var sourceNewAdapter: SourceDestinatinAdapter? = null
    private var hubAdapter: SourceDestinatinAdapter? = null
    private var coachTypeAdapter: SourceDestinatinAdapter? = null
    private lateinit var binding: FragmentEditRouteBasicDetailsBinding
    private val viewModel by sharedViewModel<RouteManagerViewModel<Any?>>()
    private var locale: String = ""
    private var loginModelPref: LoginModel? = null
    private var sourcePopupWindow: PopupWindow? = null
    private var sourceId = ""
    private var citiesList: ArrayList<CitiesListData> = arrayListOf()
    private var sourceCitiesList: ArrayList<CitiesListData> = arrayListOf()
    private var destinationCitiesList: ArrayList<CitiesListData> = arrayListOf()
    private var templist: ArrayList<CitiesListData> = arrayListOf()
    private var weekdays: ArrayList<Weekdays> = arrayListOf()
    private var alternateDayService: Boolean = false
    private var routeData: GetRouteData? = null
    private var originObj: ViaCitiesData? = null
    private var basicDetails: BasicDetailsData? = null
    private var scheduleDetails: ScheduleData? = null
    private var otherData: OtherData? = null
    private var finalDuration: String = "00:00"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditRouteBasicDetailsBinding.inflate(inflater, container, false)
        initUI()
        return binding.root
    }

    private fun initUI() {

        colorCharacterInText(binding.basicDetailsTV,
            getString(R.string.basic_details_star), '*', Color.RED)
        colorCharacterInText(binding.scheduleTV, getString(R.string.schedule_star), '*', Color.RED)

        getPref()
        citiesListObserver()
        getHubListObserver()
        coachTypesObserver()
        createRouteObserver()
        coachTypesObserver()
        getRouteDataObserver()
        modifyRouteResponseObserver()
        setWeekdays()
        clickListener()
        getCoachTypesApi()



        binding.arrivalMinutesET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val startTime =
                    binding.departureHoursET.text.toString() + ":" + binding.departureMinutesET.text.toString()
                val endTime =
                    binding.arrivalHoursET.text.toString() + ":" + binding.arrivalMinutesET.text.toString()
                calculateDuration(startTime, endTime)

            }
        })

        binding.arrivalHoursET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val startTime =
                    binding.departureHoursET.text.toString() + ":" + binding.departureMinutesET.text.toString()
                val endTime =
                    binding.arrivalHoursET.text.toString() + ":" + binding.arrivalMinutesET.text.toString()
                calculateDuration(startTime, endTime)

            }
        })

        binding.serviceEndDateET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isNotEmpty()){
                    binding.clearTV.visible()
                }else{
                    binding.clearTV.gone()

                }

            }
        })

        binding.clearTV.setOnClickListener {
            binding.serviceEndDateET.setText("")
        }

        binding.departureHoursET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val startTime =
                    binding.departureHoursET.text.toString() + ":" + binding.departureMinutesET.text.toString()
                val endTime =
                    binding.arrivalHoursET.text.toString() + ":" + binding.arrivalMinutesET.text.toString()
                calculateDuration(startTime, endTime)

            }
        })

        binding.departureMinutesET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val startTime =
                    binding.departureHoursET.text.toString() + ":" + binding.departureMinutesET.text.toString()
                val endTime =
                    binding.arrivalHoursET.text.toString() + ":" + binding.arrivalMinutesET.text.toString()
                calculateDuration(startTime, endTime)

            }
        })

        binding.fromTV.setOnClickListener {
            sourcePopupDialog(requireActivity().getString(R.string.source))
        }
        binding.toTV.setOnClickListener {
            sourcePopupDialog(requireActivity().getString(R.string.destination))
        }
        binding.hubET.setOnClickListener {
            showHubDropDownWindow()
        }

        binding.serviceStartDateET.setOnClickListener {
            openDatePickerDialog(binding.serviceStartDateET, true)
        }
        binding.serviceEndDateET.setOnClickListener {
            openDatePickerDialog(binding.serviceEndDateET, false)

        }
        binding.departureHoursET.setOnClickListener {
            openHoursMinsPickerDialog(DEFAULT_HOURS, binding.departureHoursET)
        }

        binding.arrivalHoursET.setOnClickListener {
            openHoursMinsPickerDialog(DEFAULT_HOURS, binding.arrivalHoursET)

        }

        binding.departureMinutesET.setOnClickListener {
            openHoursMinsPickerDialog(DEFAULT_MINUTES, binding.departureMinutesET)
        }

        binding.arrivalMinutesET.setOnClickListener {
            openHoursMinsPickerDialog(DEFAULT_MINUTES, binding.arrivalMinutesET)
        }

        binding.allowCancellationET.setOnClickListener {
            openYesOrNoDialog(binding.allowCancellationET)
        }

        binding.isRapidBookingET.setOnClickListener {
            openYesOrNoDialog(binding.isRapidBookingET)
        }
        binding.allowGentsET.setOnClickListener {
            openYesOrNoDialog(binding.allowGentsET)
        }

        binding.coachTypeET.setOnClickListener {
            showCoachTypesDropDown()
        }




        binding.alternateDayCB.setOnClickListener {
            val selectedDays = getSelectedWeekdays()
            if (binding.alternateDayCB.isChecked && selectedDays != "1111111") {
                requireContext().toast(getString(R.string.selected_all_days_for_alternate_service))
                binding.alternateDayCB.isChecked = false
            }
            alternateDayService = binding.alternateDayCB.isChecked
        }




        binding.nextTV.setOnClickListener {
            if (viewModel.isEdit.value == true) {
                modifyRouteResponseObserver()
            }
            if (checkValidation()) {
                if(checkAdvanceBookingData()){
                    createJsonObject()

                }
            }


        }

        binding.previousTV.setOnClickListener {
            findNavController().navigateUp()
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

    private fun clickListener() {
        binding.linearlayoutweekdays.layoutSunday.setOnClickListener {
            if (binding.alternateDayCB.isChecked) {
                requireContext().toast(getString(R.string.alternate_day_is_selected_you_can_t_unselect_day))

            } else {
                setDaysBackground(
                    0,
                    binding.linearlayoutweekdays.layoutSunday,
                    binding.linearlayoutweekdays.tvSunday
                )
            }
        }
        binding.linearlayoutweekdays.layoutMonday.setOnClickListener {
            if (binding.alternateDayCB.isChecked) {
                requireContext().toast(getString(R.string.alternate_day_is_selected_you_can_t_unselect_day))

            } else {
                setDaysBackground(
                    1,
                    binding.linearlayoutweekdays.layoutMonday,
                    binding.linearlayoutweekdays.tvMonday
                )
            }
        }
        binding.linearlayoutweekdays.layoutTuesday.setOnClickListener {
            if (binding.alternateDayCB.isChecked) {
                requireContext().toast(getString(R.string.alternate_day_is_selected_you_can_t_unselect_day))

            } else {
                setDaysBackground(
                    2,
                    binding.linearlayoutweekdays.layoutTuesday,
                    binding.linearlayoutweekdays.tvTuesday
                )
            }
        }
        binding.linearlayoutweekdays.layoutWednesday.setOnClickListener {
            if (binding.alternateDayCB.isChecked) {
                requireContext().toast(getString(R.string.alternate_day_is_selected_you_can_t_unselect_day))

            } else {
                setDaysBackground(
                    3,
                    binding.linearlayoutweekdays.layoutWednesday,
                    binding.linearlayoutweekdays.tvWednesday
                )
            }
        }
        binding.linearlayoutweekdays.layoutThursday.setOnClickListener {
            if (binding.alternateDayCB.isChecked) {
                requireContext().toast(getString(R.string.alternate_day_is_selected_you_can_t_unselect_day))

            } else {
                setDaysBackground(
                    4,
                    binding.linearlayoutweekdays.layoutThursday,
                    binding.linearlayoutweekdays.tvThursday
                )
            }
        }
        binding.linearlayoutweekdays.layoutFriday.setOnClickListener {
            if (binding.alternateDayCB.isChecked) {
                requireContext().toast(getString(R.string.alternate_day_is_selected_you_can_t_unselect_day))

            } else {
                setDaysBackground(
                    5,
                    binding.linearlayoutweekdays.layoutFriday,
                    binding.linearlayoutweekdays.tvFriday
                )
            }

        }
        binding.linearlayoutweekdays.layoutSaturday.setOnClickListener {
            if (binding.alternateDayCB.isChecked) {
                requireContext().toast(getString(R.string.alternate_day_is_selected_you_can_t_unselect_day))

            } else {
                setDaysBackground(
                    6,
                    binding.linearlayoutweekdays.layoutSaturday,
                    binding.linearlayoutweekdays.tvSaturday
                )
            }
        }
    }

    private fun calculateDuration(startTimee: String, endTimee: String) {

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        try {
            val startTime = timeFormat.parse(startTimee)
            val endTime = timeFormat.parse(endTimee)

            if (startTime != null && endTime != null) {
                var durationInMillis = endTime.time - startTime.time
                if (durationInMillis < 0) {
                    // Add one day in milliseconds (24 hours * 60 minutes * 60 seconds * 1000 milliseconds)
                    durationInMillis += 24 * 60 * 60 * 1000
                }
                // Convert milliseconds to hours and minutes
                val hours = TimeUnit.MILLISECONDS.toHours(durationInMillis)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60

                val durationString = String.format("%02d hrs %02d mins", hours, minutes)
                finalDuration = String.format("%02d:%02d", hours, minutes)

                binding.durationET.setText(durationString)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openHoursMinsPickerDialog(size: Int, ediText: AppCompatEditText) {
        // Create an array of hours from 00 to 24
        val hours = Array(size) { i -> String.format("%02d", i) }

        val inflater = requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_window, null)


        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        var popupHeight = (screenHeight * 0.4).toInt()

        if (coachList.size <= 10) {
            popupHeight = LinearLayout.LayoutParams.WRAP_CONTENT
        }
        // Create the PopupWindow
        val popupWindow =
            PopupWindow(popupView, ediText.width, popupHeight, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(12.0f);
        }

        // Set up the ListView in the PopupWindow
        val listView = popupView.findViewById<ListView>(R.id.listView)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, hours)
        listView.adapter = adapter

        // Handle ListView item click
        listView.setOnItemClickListener { _, _, position, _ ->

            ediText.setText(hours[position])
            popupWindow.dismiss()
        }

        // Show the PopupWindow below the EditText
        popupWindow.showAsDropDown(ediText, 0, 0)
    }

    private fun openYesOrNoDialog(ediText: AppCompatEditText) {
        // Create an array of hours from 00 to 24
        val hours = arrayListOf("Yes", "No")

        val inflater = requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_window, null)

        // Create the PopupWindow
        val popupWindow =
            PopupWindow(popupView, ediText.width, LinearLayout.LayoutParams.WRAP_CONTENT, true)

        // Set up the ListView in the PopupWindow
        val listView = popupView.findViewById<ListView>(R.id.listView)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, hours)
        listView.adapter = adapter

        // Handle ListView item click
        listView.setOnItemClickListener { _, _, position, _ ->
            ediText.setText(hours[position])
            popupWindow.dismiss()
        }

        // Show the PopupWindow below the EditText
        popupWindow.showAsDropDown(ediText, 0, 0)
    }


    private fun openDatePickerDialog(editText: AppCompatEditText, isStartDate: Boolean) {

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val date = dateFormat.format(calendar.time)
                if (isStartDate) {
                    if (binding.serviceEndDateET.text.toString().isBlank()) {
                        editText.setText(date)
                    } else if (isFirstDateBeforeSecond(
                            date,
                            binding.serviceEndDateET.text.toString()
                        )
                    ) {
                        editText.setText(date)
                    } else {
                        requireActivity().toast(getString(R.string.start_date_cannot_be_greater_than_end_date))
                    }
                } else {
                    editText.setText(date)

                }

            },
            year, month, day
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()

    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

        callCitiesListApi()
        callHubListApi()
        if (viewModel.isEdit.value!!) {
            getRouteDataApi()
        }


    }

    private fun getRouteDataApi() {
        if (requireContext().isNetworkAvailable()) {
            try {
                routeId = viewModel.routeId.value
                viewModel.getRouteDataApi(
                    loginModelPref!!.api_key,
                    locale,
                    "json",
                    routeId.toString()
                )
                (activity as RouteServiceManagerActivity).showProgressDialog()
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }

        }
    }

    private fun callCitiesListApi() {
        if (requireContext().isNetworkAvailable()) {
            try {
                viewModel.getCitiesListApi(loginModelPref!!.api_key, "json", locale)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }

        }
    }

    private fun callHubListApi() {
        if (requireContext().isNetworkAvailable()) {
            try {
                viewModel.getHubDropdownApi(loginModelPref!!.api_key, locale, "json")
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }

        }
    }


    private fun sourcePopupDialog(from: String) {
        var popupBinding: AdapterSearchBpdpBinding? = null
        popupBinding = AdapterSearchBpdpBinding.inflate(LayoutInflater.from(requireContext()))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        if (from == "Source") {
            sourceCitiesList.sortBy { it.name }

            sourceNewAdapter =
                SourceDestinatinAdapter(requireContext(), sourceCitiesList, this, SOURCE)
            popupBinding.searchRV.adapter = sourceNewAdapter

        } else if (from == "Destination") {
            destinationCitiesList.sortBy { it.name }

            sourceNewAdapter =
                SourceDestinatinAdapter(requireContext(), destinationCitiesList, this, DESTINATION)
            popupBinding.searchRV.adapter = sourceNewAdapter

        }

        popupBinding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                sourceNewAdapter?.filter?.filter(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
            }
        })

        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        var popupHeight = (screenHeight * 0.4).toInt()



        sourcePopupWindow = PopupWindow(
            popupBinding.root, binding.fromTV.width, popupHeight,
            true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sourcePopupWindow?.elevation = 12.0f;
        }

        sourcePopupWindow?.showAsDropDown(binding.fromTV)

        sourcePopupWindow?.elevation = 25f


        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            sourcePopupWindow?.dismiss()
            true
        }
    }

    private fun showHubDropDownWindow() {
        var popupBinding: AdapterSearchBpdpBinding? = null
        popupBinding = AdapterSearchBpdpBinding.inflate(LayoutInflater.from(requireContext()))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val list: ArrayList<CitiesListData> = arrayListOf()
        for (i in 0 until hubList.size) {
            val obj = CitiesListData()
            obj.id = hubList[i].id
            obj.name = hubList[i].label ?: ""
            obj.cityId = hubList[i].cityId ?: ""
            if (obj.name.isNotBlank()) {
                list.add(obj)
            }
        }

        hubAdapter = SourceDestinatinAdapter(requireContext(), list, this, HUB)
        popupBinding.searchRV.adapter = hubAdapter


        popupBinding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                hubAdapter?.filter?.filter(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
            }
        })

        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        var popupHeight = (screenHeight * 0.3).toInt()

        if (hubList.size <= 10) {
            popupHeight = FrameLayout.LayoutParams.WRAP_CONTENT
        }


        sourcePopupWindow = PopupWindow(
            popupBinding.root, binding.boardingCV.width, popupHeight,
            true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sourcePopupWindow?.elevation = 12.0f;
        }

        sourcePopupWindow?.showAsDropDown(binding.boardingCV)

        sourcePopupWindow?.elevation = 25f


        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            sourcePopupWindow?.dismiss()
            true
        }
    }

    private fun showCoachTypesDropDown() {
        var popupBinding: AdapterSearchBpdpBinding? = null
        popupBinding = AdapterSearchBpdpBinding.inflate(LayoutInflater.from(requireContext()))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val list: ArrayList<CitiesListData> = arrayListOf()
        for (i in 0 until coachList.size) {
            val obj = CitiesListData()
            obj.id = coachList[i].id
            obj.name = coachList[i].label
            list.add(obj)
        }

        coachTypeAdapter = SourceDestinatinAdapter(requireContext(), list, this, COACH_TYPE)
        popupBinding.searchRV.adapter = coachTypeAdapter


        popupBinding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                coachTypeAdapter?.filter?.filter(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
            }
        })


        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        var popupHeight = (screenHeight * 0.5).toInt()

        if (coachList.size <= 10) {
            popupHeight = LinearLayout.LayoutParams.WRAP_CONTENT
        }


        sourcePopupWindow = PopupWindow(
            popupBinding.root, binding.coachTypeTIL.width, popupHeight,
            true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sourcePopupWindow?.elevation = 12.0f;
        }

        sourcePopupWindow?.showAsDropDown(binding.coachTypeET)

        sourcePopupWindow?.elevation = 25f


        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            sourcePopupWindow?.dismiss()
            true
        }
    }


    override fun onResume() {
        super.onResume()

        (activity as RouteServiceManagerActivity).updateToolbar(getString(R.string.edit_route_basic_details))


    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun isNetworkOff() {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    private fun citiesListObserver() {
        viewModel.getCitiesList.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                try {
                    // Null check on response and its result
                    if (response?.result != null && response.code != null) {
                        when (response.code) {
                            200 -> {
                                citiesList = response.result
                                sourceCitiesList = citiesList
                                destinationCitiesList = citiesList
                            }

                            401 -> {
                                (requireActivity() as BaseActivity).showUnauthorisedDialog()
                            }

                            else -> {
                                requireActivity().toast(requireContext().getString(R.string.server_error))
                            }
                        }
                    } else {
                        requireActivity().toast(requireActivity().getString(R.string.server_error))
                    }
                } catch (e: Exception) {
                    Log.e("CitiesListObserver", "Exception in observer: ${e.message}", e)
                }
            })
    }

    private fun getHubListObserver() {
        viewModel.getHubDropdown.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                try {
                    // Null check on response and its result
                    if (response?.result != null && response.code != null) {
                        when (response.code) {
                            200 -> {
                                hubList = response.result!!.hubList

                            }

                            401 -> {
                                (requireActivity() as BaseActivity).showUnauthorisedDialog()
                            }

                            else -> {
                                requireActivity().toast(requireContext().getString(R.string.server_error))
                            }
                        }
                    } else if(response?.result == null && response.code != null) {
                        when (response.code) {
                            200 -> {
                                return@Observer
                            }

                            401 -> {
                                (requireActivity() as BaseActivity).showUnauthorisedDialog()
                            }

                            else -> {
                                requireActivity().toast(requireContext().getString(R.string.server_error))
                            }
                        }
                    } else {
                        requireActivity().toast(requireActivity().getString(R.string.server_error))
                    }
                } catch (e: Exception) {
                    Log.e("CitiesListObserver", "Exception in observer: ${e.message}", e)
                }
            })
    }

    override fun onDataSend(type: Int, file: Any) {

    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {
        when (type) {
            1 -> {
                val selectedData = file as CitiesListData
                when (extra as Int) {
                    SOURCE -> {
                        binding.fromTV.setText(selectedData.name)
                        sourceId = selectedData.id
                        binding.departureTimeTV.setText(getString(R.string.starting_time_from) + selectedData.name)


                        templist = ArrayList(citiesList)
                        templist.removeIf { city ->
                            city.name == selectedData.name
                        }

                        destinationCitiesList = templist
                        sourcePopupWindow?.dismiss()
                    }

                    DESTINATION -> {
                        binding.toTV.setText(selectedData.name)
                        binding.arrivalTimeTV.setText(getString(R.string.arrival_time_at) + selectedData.name)
                        if (viewModel.isEdit.value == true) {
                            (activity as RouteServiceManagerActivity).updateToolbar(
                                getString(R.string.edit_route_basic_details),
                                "Edit : ${routeData?.basicDetails!!.serviceNo}",
                                "${binding.fromTV.text.toString()}-${binding.toTV.text.toString()}"
                            )

                        } else {
                            (activity as RouteServiceManagerActivity).updateToolbar(
                                getString(R.string.edit_route_basic_details),
                                getString(R.string.new_service),
                                "${binding.fromTV.text.toString()}-${binding.toTV.text.toString()}"
                            )

                        }

                        destinationId = selectedData.id

                        templist = ArrayList(citiesList)
                        templist.removeIf { city ->
                            city.name == selectedData.name
                        }

                        sourceCitiesList = templist
                        sourcePopupWindow?.dismiss()
                    }

                    HUB -> {
                        binding.hubET.setText(selectedData.name)
                        selectedHubId =
                            if ((activity as BaseActivity).getPrivilegeBase()?.allowMultipleCitiesOptionInHubs == true) {
                                selectedData.id
                            } else {
                                selectedData.cityId
                            }
                        sourcePopupWindow?.dismiss()
                    }

                    COACH_TYPE -> {
                        binding.coachTypeET.setText(selectedData.name)
                        selectedCoachId = selectedData.id
                        sourcePopupWindow?.dismiss()
                    }


                }
            }
        }

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
            view.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bg_stroke_light_grey_little_round
            )
            textView.setTextColor(Color.parseColor("#9b9b9b"))
            weekdays[index].isSelected = false

        }
    }

    fun setWeekdays() {
        weekdays.clear()
        weekdays.add(Weekdays("Sun", true))
        weekdays.add(Weekdays("Mon", true))
        weekdays.add(Weekdays("Tue", true))
        weekdays.add(Weekdays("Wed", true))
        weekdays.add(Weekdays("Thu", true))
        weekdays.add(Weekdays("Fri", true))
        weekdays.add(Weekdays("Sat", true))
        binding.linearlayoutweekdays.tvSunday.text = weekdays[0].day
        binding.linearlayoutweekdays.tvMonday.text = weekdays[1].day
        binding.linearlayoutweekdays.tvTuesday.text = weekdays[2].day
        binding.linearlayoutweekdays.tvWednesday.text = weekdays[3].day
        binding.linearlayoutweekdays.tvThursday.text = weekdays[4].day
        binding.linearlayoutweekdays.tvFriday.text = weekdays[5].day
        binding.linearlayoutweekdays.tvSaturday.text = weekdays[6].day
    }


    private fun getCoachTypesApi() {
        if (requireContext().isNetworkAvailable()) {
            try {
                viewModel.getCoachTypeApi(loginModelPref!!.api_key, locale, "json")
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }

        }
    }

    private fun getRouteDataObserver() {
        viewModel.getRouteData.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                response.getContentIfNotHandled()?.let {
                    (activity as RouteServiceManagerActivity).hideProgressDialog()

                    try {
                        (activity as RouteServiceManagerActivity).hideProgressDialog()

                        if (it.result != null && it.code != null) {
                            when (it.code) {
                                200 -> {
                                    routeData = it.result

                                    (activity as RouteServiceManagerActivity).updateToolbar(
                                        getString(R.string.edit_route_basic_details),
                                        "Edit : ${routeData?.basicDetails!!.serviceNo}",
                                        "${routeData?.basicDetails?.originName}-${routeData?.basicDetails?.destinationName}"
                                    )

                                    updateDataFields()
                                }

                                401 -> {
                                    (requireActivity() as BaseActivity).showUnauthorisedDialog()
                                }

                                else -> {
                                    requireActivity().toast(requireContext().getString(R.string.server_error))
                                }
                            }
                        } else {
                            requireActivity().toast(requireActivity().getString(R.string.server_error))
                        }
                    } catch (e: Exception) {
                        Log.e("CitiesListObserver", "Exception in observer: ${e.message}", e)
                    }
                }

            })
    }

    private fun updateDataFields() {
        basicDetails = routeData?.basicDetails
        scheduleDetails = routeData?.schedule
        otherData = routeData?.other
        val originName = basicDetails!!.originName
        sourceId = basicDetails?.originId!!
        templist = ArrayList(citiesList)
        templist.removeIf { city ->
            city.name == originName
        }
        destinationCitiesList = templist
        val destinationName = basicDetails!!.destinationName
        destinationId = basicDetails?.destId!!

        templist = ArrayList(citiesList)
        templist.removeIf { city ->
            city.name == originName
        }

        sourceCitiesList = templist
        //  val coach = coachList.find { coach -> coach.id == basicDetails?.coachId }
        val coachName = basicDetails?.coachName
        selectedCoachId = basicDetails?.coachId?:""
        //val hub = hubList.find { hub -> hub.id == basicDetails?.hubId }
        val hubName = basicDetails?.hubName
        selectedHubId = basicDetails?.hubId?:""
        val depHrs = scheduleDetails?.departureTime?.substringBefore(":")
        val depMins = scheduleDetails?.departureTime?.substringAfter(":")
        val arrHrs = scheduleDetails?.arrivalTime?.substringBefore(":")
        val arrMins = scheduleDetails?.arrivalTime?.substringAfter(":")
        val duration =
            scheduleDetails?.duration?.substringBefore(":") + " hrs " + scheduleDetails?.duration?.substringAfter(
                ":"
            ) + " mins"
        val fromDate = scheduleDetails?.fromDate
        val toDate = scheduleDetails?.toDate


        binding.let {
            it.fromTV.setText(originName)
            it.toTV.setText(destinationName)
            it.coachTypeET.setText(coachName)
            it.uniqueServiceNameET.setText(basicDetails?.serviceName)
            it.serviceNoET.setText(basicDetails?.serviceNo)
            it.otaDisplayNameET.setText(basicDetails?.otaName)
            it.hubET.setText(hubName)
            it.departureTimeTV.setText(getString(R.string.starting_time_from) + originName)
            it.arrivalTimeTV.setText(getString(R.string.arrival_time_at) + destinationName)
            it.departureHoursET.setText(depHrs)
            it.departureMinutesET.setText(depMins)
            it.arrivalHoursET.setText(arrHrs)
            it.arrivalMinutesET.setText(arrMins)
            it.durationET.setText(duration)
            if (fromDate!!.isNotEmpty()) {
                it.serviceStartDateET.setText(fromDate)
            }
            if (toDate!!.isNotEmpty()) {
                it.serviceEndDateET.setText(toDate)
            }
            if (scheduleDetails?.alternateDayService!!.toString() == "true") {
                it.alternateDayCB.isChecked = true
            } else {
                it.alternateDayCB.isChecked = false
            }
            it.advanceBookingET.setText(scheduleDetails?.advanceBooking)
            val daysViews = listOf(
                Pair(it.linearlayoutweekdays.layoutSunday, it.linearlayoutweekdays.tvSunday),
                Pair(it.linearlayoutweekdays.layoutMonday, it.linearlayoutweekdays.tvMonday),
                Pair(it.linearlayoutweekdays.layoutTuesday, it.linearlayoutweekdays.tvTuesday),
                Pair(it.linearlayoutweekdays.layoutWednesday, it.linearlayoutweekdays.tvWednesday),
                Pair(it.linearlayoutweekdays.layoutThursday, it.linearlayoutweekdays.tvThursday),
                Pair(it.linearlayoutweekdays.layoutFriday, it.linearlayoutweekdays.tvFriday),
                Pair(it.linearlayoutweekdays.layoutSaturday, it.linearlayoutweekdays.tvSaturday)
            )

            scheduleDetails?.days?.forEachIndexed { index, char ->
                if (index < daysViews.size) {
                    val (layout, textView) = daysViews[index]
                    val isSelected = char == '1'
                    layout.setBackgroundColor(
                        if (isSelected) Color.parseColor("#00adb5") else Color.parseColor("#ffffff")
                    )
                    textView.setTextColor(
                        if (isSelected) Color.parseColor("#ffffff") else Color.parseColor("#9b9b9b")
                    )
                    weekdays[index].isSelected = isSelected
                }
            }
            if (otherData?.allowCancellation == "true") {
                it.allowCancellationET.setText("Yes")
            } else {
                it.allowCancellationET.setText("No")
            }
            if (otherData?.allowGentsNextToLadies == "true") {
                it.allowGentsET.setText("Yes")
            } else {
                it.allowGentsET.setText("No")
            }
            if (otherData?.isRapidBooking == "true") {
                it.isRapidBookingET.setText("Yes")
            } else {
                it.isRapidBookingET.setText("No")
            }
        }


    }

    private fun coachTypesObserver() {
        viewModel.getCoachTypeList.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                try {
                    // Null check on response and its result
                    if (response != null && response.result != null && response.code != null) {
                        when (response.code) {
                            200 -> {
                                coachList = response.result!!.coachTypes


                            }

                            401 -> {
                                (requireActivity() as BaseActivity).showUnauthorisedDialog()
                            }

                            else -> {
                                requireActivity().toast(requireContext().getString(R.string.server_error))
                            }
                        }
                    } else {
                        requireActivity().toast(requireActivity().getString(R.string.server_error))
                    }
                } catch (e: Exception) {
                    Log.e("CitiesListObserver", "Exception in observer: ${e.message}", e)
                }
            })
    }

    private fun createRouteObserver() {
        viewModel.getCreateRouteStatus.distinctUntilChanged().observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                response.getContentIfNotHandled()?.let {
                    try {
                        // Null check on response and its result
                        if (it?.code != null) {
                            when (it.code) {
                                200 -> {
                                    viewModel.routeId.postValue(it.id)
                                    viewModel.isAcCoach.postValue(it.isAcCoach)
                                    viewModel.routeJsonObject.postValue(finalJsonObject)
                                    viewModel.currentSeatTypes.postValue(it.seatType)
                                    requireActivity().toast(getString(R.string.route_created_successfully))
                                    (activity as RouteServiceManagerActivity).hideProgressDialog()
                                    findNavController().navigate(R.id.action_editRouteBasicDetailsFragment_to_editRouteViaCitiesFragment)

                                }

                                412 -> {
                                    if (!it.message.isNullOrEmpty()) {
                                        requireActivity().toast(it.message)
                                    }
                                }

                                401 -> {
                                    (requireActivity() as BaseActivity).showUnauthorisedDialog()
                                }

                                else -> {
                                    requireActivity().toast(requireContext().getString(R.string.server_error))
                                }
                            }
                        } else {
                            requireActivity().toast(requireActivity().getString(R.string.server_error))
                        }
                    } catch (e: Exception) {
                        Log.e("CitiesListObserver", "Exception in observer: ${e.message}", e)
                    }
                }
            })
    }

    private fun modifyRouteResponseObserver() {
        viewModel.getModifyRouteStatus.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                response.getContentIfNotHandled()?.let {
                    try {
                        if (it != null) {
                            when (it.code) {
                                200 -> {
                                    context?.toast(it.result?.message)
                                    (activity as RouteServiceManagerActivity).hideProgressDialog()
                                    findNavController().navigate(R.id.action_editRouteBasicDetailsFragment_to_editRouteViaCitiesFragment)


                                }

                                412 -> {
                                    if (!it.message.isNullOrEmpty()) {
                                        requireActivity().toast(it.message)
                                    }
                                }

                                401 -> {
                                    (activity as BaseActivity).showUnauthorisedDialog()
                                }

                                else -> {
                                    requireContext().toast(requireActivity().getString(R.string.server_error))
                                }
                            }
                        }
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace()
                        }
                    }
                }
            })
    }


    private fun createJsonObject() {


        try {
            val basicObj = JsonObject()
            basicObj.addProperty("origin_id", sourceId.toInt())
            basicObj.addProperty("dest_id", destinationId.toInt())
            basicObj.addProperty("service_no", binding.serviceNoET.text.toString())
            basicObj.addProperty("service_name", binding.uniqueServiceNameET.text.toString())
            basicObj.addProperty("coach_id", selectedCoachId.toInt())
            if(!selectedHubId.isNullOrEmpty()) {
                basicObj.addProperty("hub_id", selectedHubId.toInt())
            }
            basicObj.addProperty("ota_name", binding.otaDisplayNameET.text.toString())


            var depTime =
                binding.departureHoursET.text.toString() + ":" + binding.departureMinutesET.text.toString()
            var arrivalTime =
                binding.arrivalHoursET.text.toString() + ":" + binding.arrivalMinutesET.text.toString()

            val scheduleObj = JsonObject()
            scheduleObj.addProperty("departure_time", depTime)
            scheduleObj.addProperty("arrival_time", arrivalTime)
            scheduleObj.addProperty("duration", finalDuration)
            scheduleObj.addProperty("from_date", binding.serviceStartDateET.text.toString())
            scheduleObj.addProperty("to_date", binding.serviceEndDateET.text.toString())
            scheduleObj.addProperty("days", getSelectedWeekdays())
            scheduleObj.addProperty("advance_booking", binding.advanceBookingET.text.toString().replaceFirst("^0+(?!$)".toRegex(), ""))
            scheduleObj.addProperty("alternate_day_service", alternateDayService)

            val other = JsonObject()

            var allow_cancellation = false
            var is_rapid_booking = false
            var allow_gents_next_to_ladies = false

            allow_cancellation = binding.allowCancellationET.text.toString().equals("yes", true)
            is_rapid_booking = binding.isRapidBookingET.text.toString().equals("yes", true)
            allow_gents_next_to_ladies = binding.allowGentsET.text.toString().equals("yes", true)

            other.addProperty("allow_cancellation", allow_cancellation)
            other.addProperty("is_rapid_booking", is_rapid_booking)
            other.addProperty("allow_gents_next_to_ladies", allow_gents_next_to_ladies)

            val finalObj = JsonObject()
            finalObj.add("basic_details", basicObj)
            finalObj.add("schedule", scheduleObj)
            finalObj.add("other", other)

            finalJsonObject = finalObj
            if (viewModel.isEdit.value!!) {
                modifyRouteApi(finalObj)
            } else {
                hitCreateRouteApi(finalObj)
            }
        } catch (e: Exception) {
            requireActivity().toast(getString(R.string.server_error))
        }


    }


    private fun modifyRouteApi(finalObj: JsonObject) {
        try {
            if (requireContext().isNetworkAvailable()) {
                viewModel.modifyRouteApi(
                    loginModelPref!!.api_key,
                    locale,
                    "json",
                    viewModel.routeId.value.toString(),
                    "1",
                    finalObj
                )
//                (activity as RouteServiceManagerActivity).showProgressDialog()
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

    }


    private fun hitCreateRouteApi(finalObj: JsonObject) {
        try {
            if (requireContext().isNetworkAvailable()) {
                viewModel.createRouteApi(loginModelPref!!.api_key, locale, "json", finalObj)
//                (activity as RouteServiceManagerActivity).showProgressDialog()
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

    }

    private fun checkValidation(): Boolean {
        when {
            binding.fromTV.text.toString().isEmpty() -> {
                requireActivity().toast(getString(R.string.please_select_from_city))
            }

            binding.toTV.text.toString().isEmpty() -> {
                requireActivity().toast(getString(R.string.please_select_to_city_))

            }

            binding.coachTypeET.text.toString().isEmpty() || selectedCoachId.isEmpty() -> {
                requireActivity().toast(getString(R.string.please_select_coach_type))

            }

            binding.serviceNoET.text.toString().isEmpty() -> {
                requireActivity().toast(getString(R.string.please_enter_service_number))
            }

            binding.hubET.text.toString().isBlank()  && !hubList.isNullOrEmpty() -> {
                requireActivity().toast(getString(R.string.please_select_hub_))
            }

            binding.departureHoursET.text.toString().isEmpty() -> {
                requireActivity().toast(getString(R.string.please_select_departure_hours_))

            }

            binding.departureMinutesET.text.toString().isEmpty() -> {
                requireActivity().toast(getString(R.string.please_select_departure_minutes_))


            }

            binding.arrivalHoursET.text.toString().isEmpty() -> {
                requireActivity().toast(getString(R.string.please_select_arrival_hours))

            }

            binding.arrivalMinutesET.text.toString().isEmpty() -> {
                requireActivity().toast(getString(R.string.please_select_arrival_minutes))

            }

            binding.serviceStartDateET.text.toString().isEmpty() -> {
                requireActivity().toast(getString(R.string.please_select_service_start_date))

            }

            binding.advanceBookingET.text.toString().isEmpty() -> {
                requireActivity().toast(getString(R.string.please_enter_advance_booking_days))

            }

            binding.serviceEndDateET.text.toString().isNotEmpty() && viewModel.isEdit.value == false && !isFirstDateBeforeSecond(
                binding.serviceStartDateET.text.toString(),
                binding.serviceEndDateET.text.toString()
            ) -> {
                requireActivity().toast(getString(R.string.start_date_should_be_lesser_than_end_date))
            }

            else -> {
                return true
            }
        }

        return false
    }

    fun checkAdvanceBookingData(): Boolean {
        val inputText = binding.advanceBookingET.text.toString()
        var proceed = false
        if (inputText.isNotEmpty()) {
            val trimmedText = inputText.trimStart('0')

            if (trimmedText.isEmpty()) {
                 requireActivity().toast(getString(R.string.advance_booking_date_cannot_be_0))
                proceed = false
            } else {
                val number = trimmedText.toIntOrNull()
                if (number == 0) {
                    requireActivity().toast(getString(R.string.advance_booking_date_cannot_be_0))
                    proceed = false
                } else {
                    proceed =  true
                }
            }
        }
        return proceed



    }

    fun colorCharacterInText(textView: TextView, text: String, charToColor: Char, color: Int) {
        // Create a SpannableString from the text
        val spannableString = SpannableString(text)

        // Find all occurrences of the character to color
        var startIndex = 0
        while (startIndex < text.length) {
            val index = text.indexOf(charToColor, startIndex)
            if (index == -1) break

            // Apply the ForegroundColorSpan to the character
            spannableString.setSpan(
                ForegroundColorSpan(color),
                index,
                index + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            startIndex = index + 1
        }

        // Set the spannable string to the TextView
        textView.text = spannableString
    }


}
