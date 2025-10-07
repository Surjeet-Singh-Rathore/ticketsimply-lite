package com.bitla.ts.presentation.view.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.booking_summary_method_name
import com.bitla.ts.data.format_type
import com.bitla.ts.data.is_from_middle_tier
import com.bitla.ts.data.is_middle_tier
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemCheckedListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.notify_passengers_method_name
import com.bitla.ts.data.response_format
import com.bitla.ts.data.view_reservation_method_name
import com.bitla.ts.databinding.FragmentSelectedPassengersBinding
import com.bitla.ts.domain.pojo.SpinnerItems
import com.bitla.ts.domain.pojo.Weekdays
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.booking_summary.Booking
import com.bitla.ts.domain.pojo.booking_summary.request.BookingSummaryRequest
import com.bitla.ts.domain.pojo.booking_summary.request.ReqBody
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.notify_passengers.request.*
import com.bitla.ts.domain.pojo.sms_types.EmployeeTypeOption
import com.bitla.ts.domain.pojo.sms_types.EmployeeTypes
import com.bitla.ts.domain.pojo.sms_types.SmsInputMode
import com.bitla.ts.domain.pojo.sms_types.SmsTemplate
import com.bitla.ts.domain.pojo.view_reservation.request.ViewReservationRequest
import com.bitla.ts.presentation.adapter.BusDelayAdapter
import com.bitla.ts.presentation.adapter.SelectPassengerChildAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.activity.SelectMessageActivity
import com.bitla.ts.presentation.view.activity.SelectedSearchShiftPassengerActivity
import com.bitla.ts.presentation.viewModel.AllPassengersViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.convertFirstCharToUpperCase
import com.bitla.ts.utils.common.getDateDMYY
import com.bitla.ts.utils.common.retrieveBookingList
import com.bitla.ts.utils.common.saveBookingList
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_BUS_TYPE
import com.bitla.ts.utils.sharedPref.PREF_CHECKED_PNR
import com.bitla.ts.utils.sharedPref.PREF_COACH_NUMBER
import com.bitla.ts.utils.sharedPref.PREF_DEPARTURE_TIME
import com.bitla.ts.utils.sharedPref.PREF_EMPLOYEE_TYPE_OPTIONS
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_RESERVATION_ID
import com.bitla.ts.utils.sharedPref.PREF_SELECTED_AVAILABLE_ROUTES
import com.bitla.ts.utils.sharedPref.PREF_SMS_PASSENGER_TYPE
import com.bitla.ts.utils.sharedPref.PREF_SMS_TEMPLATE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.google.android.material.textfield.TextInputEditText
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import onChange
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.util.*
import kotlin.collections.ArrayList

class SelectedPassengersFragment : BaseFragment(), OnItemClickListener, OnItemCheckedListener,
    View.OnClickListener, DialogSingleButtonListener {

    companion object {
        val tag: String = SelectedPassengersFragment::class.java.simpleName
    }

    private val chartType: String = "3" //Fixed (chart type: 3= sort by pnr number)
    lateinit var binding: FragmentSelectedPassengersBinding
    private lateinit var selectedPassengerAdapter: SelectPassengerChildAdapter
    private var bookingList = mutableListOf<Booking>()
    private var checkedPnrList = mutableListOf<String>()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var source: String? = ""
    private var destination: String? = ""
    private var travelDate: String = ""
    private var busType: String? = null
    private var coachNumber: String? = null
    private var deptTime: String? = null
    private var apiKey: String = ""
    private var bccId: String = ""
    private var resId = 0L
    private var loginModelPref = LoginModel()

    private lateinit var busDelayAdapter: BusDelayAdapter
    private var busDelayList: ArrayList<Weekdays> = arrayListOf()
    private var smsTemplate: SmsTemplate? = null
    private var smsPassengerType: String? = null
    private var employeeTypeOption: MutableList<EmployeeTypeOption>? = null
    private var employeeTypes: EmployeeTypes? = null
    private var employeeTypeSpinnerItem: MutableList<SpinnerItems> = mutableListOf()
    private var smsId: Int? = null
    private var employeeId: Int? = null
    private var pnrNos: String = ""
    private var customSms: String? = ""
    private val time: String = "00:00"

    //private val notify: String = "HELLO" // fixed
    private val text: String = "HELLO" // fixed
    private var smsContent: String? = null

    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private val allPassengersViewModel by viewModel<AllPassengersViewModel<Any?>>()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var locale: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectedPassengersBinding.inflate(inflater, container, false)
        init()
        lifecycleScope.launch {
            allPassengersViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        return binding.root
    }

    override fun isInternetOnCallApisAndInitUI() {
        init()
    }

    override fun isNetworkOff() {
    }

    private fun init(){
        binding.layoutChkAll.gone()
        getPref()

        if (employeeTypes != null) {
            employeeTypeOption = employeeTypes?.employee_type_options
            for (i in 0..employeeTypeOption?.size?.minus(1)!!) {
                val spinnerItems =
                    SpinnerItems(
                        employeeTypeOption!![i].id,
                        convertFirstCharToUpperCase(
                            employeeTypeOption!![i].option.replace("_", " ").lowercase()
                        )
                    )
                employeeTypeSpinnerItem.add(spinnerItems)
            }
            if (employeeTypeSpinnerItem.isNotEmpty())
                employeeId = employeeTypeSpinnerItem[0].id
        }

        if (smsTemplate != null && smsPassengerType != null && smsPassengerType == SelectedPassengersFragment.tag) {
            binding.layoutSms.visible()
            binding.layoutNotify.btnEdit.visible()
            binding.layoutPassenger.gone()
            binding.btnNotifySms.visible()
            binding.btnSendSms.gone()

            generateForm(smsTemplate)
        } else {
            binding.layoutSms.gone()
            binding.layoutNotify.btnEdit.gone()
            binding.layoutPassenger.visible()
            binding.btnNotifySms.gone()
            binding.btnSendSms.visible()
        }


        binding.acEmployeeType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                employeeId = employeeTypeSpinnerItem[position].id
            }


        val srcDest = "$source ${getString(R.string.to)} $destination"
        val dateTime = "$travelDate ${getString(R.string.at)} $deptTime"
        binding.apply {
            layoutNotify.tvNotifyTitle.text = getString(R.string.notifySelected)
            layoutNotify.tvDateTime.text = dateTime
            layoutNotify.tvSrcDest.text = srcDest
            layoutNotify.tvCoachNumber.text = coachNumber
        }
        if (smsTemplate != null) {
            allPassengersViewModel.checkboxWatcher(true)
            checkSms()
        }

        clickListener()

        /*if (requireContext().isNetworkAvailable())
            callBookingSummaryApi()
        else
            requireContext().noNetworkToast()*/

        if (requireContext().isNetworkAvailable())
            pickUpChartApi()
        else
            requireContext().noNetworkToast()

        setObserver()

        binding.shiftPassengerSearch.setOnClickListener {
            PreferenceUtils.putString(
                PREF_SMS_PASSENGER_TYPE,
                SelectedPassengersFragment.tag
            )
            val intent = Intent(requireContext(), SelectedSearchShiftPassengerActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

    }
    private fun clickListener() {
        binding.btnSendSms.setOnClickListener(this)
        binding.layoutNotify.btnEdit.setOnClickListener(this)
        binding.btnNotifySms.setOnClickListener(this)
    }

    private fun getPref() {
        locale = PreferenceUtils.getlang()
        sourceId = PreferenceUtils.getSourceId()
        destinationId = PreferenceUtils.getDestinationId()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()

        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key
        if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
            resId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)!!

        if (PreferenceUtils.getObject<SmsTemplate>(PREF_SMS_TEMPLATE) != null)
            smsTemplate = PreferenceUtils.getObject<SmsTemplate>(PREF_SMS_TEMPLATE)

        if (PreferenceUtils.getString(PREF_SMS_PASSENGER_TYPE) != null)
            smsPassengerType = PreferenceUtils.getString(PREF_SMS_PASSENGER_TYPE)

        if (PreferenceUtils.getObject<EmployeeTypes>(PREF_EMPLOYEE_TYPE_OPTIONS) != null)
            employeeTypes =
                PreferenceUtils.getObject<EmployeeTypes>(PREF_EMPLOYEE_TYPE_OPTIONS)!!

        if (PreferenceUtils.getString(PREF_CHECKED_PNR) != null) {
            pnrNos = PreferenceUtils.getString(PREF_CHECKED_PNR)!!

        }

        if (PreferenceUtils.getString(PREF_COACH_NUMBER) != null) {
            coachNumber = PreferenceUtils.getString(PREF_COACH_NUMBER)
        }

        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            if (coachNumber == null)
                coachNumber = result?.number ?: getString(R.string.empty)
            deptTime = result?.dep_time ?: getString(R.string.empty)
            busType = result?.bus_type ?: getString(R.string.empty)
        }
        if (coachNumber == null || coachNumber!!.isEmpty()) {
            if (PreferenceUtils.getString(PREF_COACH_NUMBER) != null) {
                coachNumber = PreferenceUtils.getString(PREF_COACH_NUMBER)
            }
        }
        if (deptTime == null || deptTime!!.isEmpty()) {
            if (PreferenceUtils.getString(PREF_DEPARTURE_TIME) != null) {
                deptTime = PreferenceUtils.getString(PREF_DEPARTURE_TIME)
            }
        }
        if (busType == null || busType!!.isEmpty()) {
            if (PreferenceUtils.getString(PREF_BUS_TYPE) != null) {
                busType = PreferenceUtils.getString(PREF_BUS_TYPE)
            }
        }

    }


    // calling pickup_chart API instead of book_summary
    private fun callBookingSummaryApi() {
        val reqBody = ReqBody(
            loginModelPref.api_key,
            resId.toString(),
            response_format,
            locale = locale
        )
        val bookingSummaryRequest = BookingSummaryRequest(
            bccId, format_type,
            booking_summary_method_name, reqBody
        )


        sharedViewModel.bookingSummaryApi(
            loginModelPref.api_key,
            resId.toString(),
            response_format,
            locale!!,
            booking_summary_method_name
        )

        Timber.d("bookingSummaryRequest $bookingSummaryRequest")
    }


    private fun pickUpChartApi() {
        val viewReservationRequest = ViewReservationRequest(
            bccId,
            format_type,
            view_reservation_method_name,
            com.bitla.ts.domain.pojo.view_reservation.request.ReqBody(
                loginModelPref.api_key,
                chartType,
                is_middle_tier,
                resId.toString(),
                locale = locale
            )
        )

        pickUpChartViewModel.viewReservationAPI(
            loginModelPref.api_key,
            resId.toString(),
            chartType,
            locale!!,
            view_reservation_method_name,
            null
        )
    }

    private fun setObserver() {
        sharedViewModel.loadingState.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                else -> binding.includeProgress.progressBar.gone()
            }
        })

        allPassengersViewModel.etOnChange.observe(requireActivity(), Observer {
            if (!it) {
                binding.btnNotifySms.setBackgroundColor(resources.getColor(R.color.button_default_color))
            } else {
                binding.btnNotifySms.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
        })

        allPassengersViewModel.notifyPassengers.observe(
            requireActivity()
        ) {
            when (it.code) {
                200 -> {
                    requireContext().toast(it.message)
                    requireActivity().finish()
                }
                401 -> {
                    /*DialogUtils.unAuthorizedDialog(
                        requireContext(),
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    (activity as BaseActivity).showUnauthorisedDialog()

                }
                else -> {
                    if (it?.result?.message != null) {
                        it.result.message.let { it1 -> requireContext().toast(it1) }
                    }
                }
            }
        }

        /*sharedViewModel.bookingSummary.observe(viewLifecycleOwner, Observer {
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                Timber.d("responseBodyBookingSummary $it")
                if (it.code == 200) {
                    bookingList = it.result.booking
                    setPassengersAdapter()
                    binding.layoutPassenger.visible()
                    binding.btnSendSms.visible()
                    binding.layoutNoData.gone()
                } else {
                    binding.layoutPassenger.gone()
                    binding.btnSendSms.gone()
                    binding.layoutNoData.visible()
                    binding.tvNoData.text = it.message
                   // it.message.let { it1 -> requireContext().toast(it1) }
                }
            }
            else{
                requireContext().toast(getString(R.string.server_error))
            }
        })*/

        pickUpChartViewModel.viewReservationResponse.observe(viewLifecycleOwner, Observer { it ->
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                Timber.d("viewReservationResponse $it")
                when (it.code) {
                    200 -> {
                        if (it.passengerDetails != null) {
                            it.passengerDetails.forEach {
                                val isPnrChecked = pnrNos.contains(it.pnrNumber)
                                val booking = Booking(
                                    isChecked = isPnrChecked,
                                    seats = it.seatNumber,
                                    ticket_number = it.pnrNumber,
                                    total_bookings = 0,
                                    passenger_name = it.passengerName,
                                )
                                booking.boarding_point = it.stageName
                                bookingList.add(booking)
                            }
                            setPassengersAdapter()
                            if (binding.layoutNotify.btnEdit.isVisible)
                                binding.layoutPassenger.gone()
                            else
                                binding.layoutPassenger.visible()
                            binding.btnSendSms.visible()
                            binding.layoutNoData.gone()
                        } else {
                            binding.layoutPassenger.gone()
                            binding.btnSendSms.gone()
                            binding.layoutNoData.visible()
                            binding.tvNoData.text = getString(R.string.no_data_available)
                        }
                    }
                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }
                    else -> {
                        binding.layoutPassenger.gone()
                        binding.btnSendSms.gone()
                        binding.layoutNoData.visible()
                        it.message.let { it1 -> binding.tvNoData.text = it.message }
                        it.result?.message.let { it1 -> binding.tvNoData.text = it.result?.message }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        })
    }

    private fun setPassengersAdapter() {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvSelectedPassengers.layoutManager = layoutManager
        selectedPassengerAdapter = SelectPassengerChildAdapter(requireActivity(), this, this)
        selectedPassengerAdapter.addData(bookingList)
        binding.rvSelectedPassengers.adapter = selectedPassengerAdapter
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        val tag = view.tag
        if (tag == getString(R.string.total_seats)) {
            if (bookingList.isNotEmpty()) {
                val ticketNo =
                    "${getString(R.string.ticket_no)} ${bookingList[position].ticket_number}"
                val seatNo = bookingList[position].seats
                val bookedSeat = bookingList[position].total_bookings
                val srcDest = "$source - $destination"
                val dateBusType = "${getDateDMYY(travelDate)} | $deptTime | $busType"
                DialogUtils.showSeatsDialog(
                    ticketNo,
                    srcDest,
                    dateBusType,
                    bookedSeat.toString(),
                    seatNo,
                    requireContext()
                )
            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    override fun onItemChecked(isChecked: Boolean, view: View, position: Int) {
        val ticketNo = bookingList[position].ticket_number
        if (isChecked) {
            checkedPnrList.add(ticketNo)
            if (bookingList.isNotEmpty())
                bookingList[position].isChecked = true
        } else {
            checkedPnrList.remove(ticketNo)
            if (bookingList.isNotEmpty())
                bookingList[position].isChecked = false
        }
        saveBookingList(bookingList)
        val checkedPnr = checkedPnrList.toString().replace("[", "").replace("]", "").trim()
        Timber.d("checkedPnrList $checkedPnr")
        PreferenceUtils.putString(PREF_CHECKED_PNR, checkedPnr)

        changeButtonColor()
    }

    private fun changeButtonColor() {
        if (checkedPnrList.isNotEmpty())
            binding.btnSendSms.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        else
            binding.btnSendSms.setBackgroundColor(resources.getColor(R.color.button_default_color))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSendSms -> {
                if (checkedPnrList.isEmpty())
                    requireContext().toast(getString(R.string.validate_passenger))
                else {
                    PreferenceUtils.putString(
                        PREF_SMS_PASSENGER_TYPE,
                        SelectedPassengersFragment.tag
                    )

                    val intent = Intent(requireActivity(), SelectMessageActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
            R.id.btnEdit -> {
                bookingList = retrieveBookingList()
                if (PreferenceUtils.getString(PREF_CHECKED_PNR) != null) {
                    val pnr = PreferenceUtils.getString(PREF_CHECKED_PNR)!!
                    val pnrList = pnr.split(",")
                    for (i in 0..bookingList.size.minus(1)) {
                        for (j in 0..pnrList.size.minus(1)) {
                            if (pnrList[j] == bookingList[i].ticket_number) {
                                checkedPnrList.add(pnrList[j])
                            }
                        }
                    }
                    changeButtonColor()
                }
                setPassengersAdapter()
                binding.layoutSms.gone()
                binding.layoutNotify.btnEdit.gone()
                binding.layoutPassenger.visible()
                binding.btnNotifySms.gone()
                binding.btnSendSms.visible()
            }
            R.id.btnNotifySms -> {
                if (smsId != null) {
                    if (binding.spinner.isVisible && binding.acEmployeeType.text.isEmpty()) {
                        requireContext().toast(getString(R.string.validate_employee_type))
                        allPassengersViewModel.etTextWatcher(binding.acEmployeeType.text.toString())
                    } else if (binding.layoutBoardingPoint.isVisible && binding.etBoardingPoint.text?.isEmpty()!!) {
                        requireContext().toast(getString(R.string.validate_boarding_point))
                    } else if (binding.layoutBusNumber.isVisible && binding.etBusNumber.text?.isEmpty()!!) {
                        requireContext().toast(getString(R.string.validate_bus_number))
                    } else if (binding.layoutDriverName.isVisible && binding.etDriverName.text?.isEmpty()!!) {
                        requireContext().toast(getString(R.string.validate_driver_name))
                    } else if (binding.layoutMobileNumber.isVisible && binding.etMobileNumber.text?.isEmpty()!!) {
                        requireContext().toast(getString(R.string.validate_mobile_number))
                    } else if (binding.layoutBusDelayTraffic.isVisible && binding.spinnerHour.isVisible && binding.acHour.text.isEmpty()) {
                        requireContext().toast(getString(R.string.validate_hours))
                    } else if (binding.layoutBusDelayTraffic.isVisible && binding.acMinutes.text.isEmpty()) {
                        requireContext().toast(getString(R.string.validate_minutes))
                    }
                    /*else if (!binding.chkSmsAll.isChecked) {
                        requireContext().toast(getString(R.string.validate_check_sms))
                    } */
                    else {
                        if (requireContext().isNetworkAvailable()) {
                            val charts = Charts(binding.tvMessage.text.toString(), text)
                            val delay =
                                Delay(
                                    time,
                                    binding.acHour.text.toString(),
                                    binding.acMinutes.text.toString()
                                )
                            val employee = Employee(employeeId.toString())
                            val bus = Bus(binding.etBusNumber.text.toString())
                            val driver = Driver(binding.etDriverName.text.toString())
                            val mobile = Mobile(binding.etMobileNumber.text.toString())
                            val custom = customSms?.let { Custom(it) }
                            val reqBody = employee.let {
                                smsId?.toString()?.let { it1 ->
                                    custom?.let { it2 ->
                                        ReqBody(
                                            apiKey,
                                            bus,
                                            charts,
                                            delay,
                                            driver,
                                            it,
                                            resId.toString(),
                                            mobile,
                                            it1,
                                            it2,
                                            pnrNos.replace(" ", ""),
                                            is_from_middle_tier,
                                            locale = locale
                                        )
                                    }
                                }
                            }
                            val notifyPassengersRequest =
                                reqBody?.let {
                                    NotifyPassengersRequest(
                                        bccId, format_type, notify_passengers_method_name,
                                        it
                                    )
                                }

                            /*notifyPassengersRequest?.let {
                                allPassengersViewModel.notifyPassengersApi(
                                    loginModelPref.auth_token,
                                    loginModelPref.api_key,
                                    it,
                                    notify_passengers_method_name
                                )
                            }*/

                            allPassengersViewModel.notifyPassengersApi(
                                reqBody!!,
                                notify_passengers_method_name
                            )
                        } else
                            requireContext().noNetworkToast()
                    }

                }
            }
        }
    }

    private fun checkSms() {
        binding.chkSmsAll.setOnCheckedChangeListener { buttonView, isChecked ->
            //allPassengersViewModel.checkboxWatcher(isChecked)
            if (isChecked) {
                binding.layoutChkAll.backgroundTintList =
                    resources.getColorStateList(R.color.colorPrimary)
            } else {
                binding.layoutChkAll.backgroundTintList =
                    resources.getColorStateList(R.color.colorDimShadow6)
            }
        }
    }

    private fun generateForm(smsTemplate: SmsTemplate?) {
        smsId = smsTemplate?.sms_id.toString().toDouble().toInt()
        binding.etSelectSms.setText(smsTemplate?.sms_type ?: getString(R.string.notAvailable))


        if (smsTemplate?.sms_content != null && smsTemplate.sms_content.isNotEmpty()) {
            binding.layoutSmsContent.visible()
            binding.tvSmsTitle.text = smsTemplate.sms_type
            smsContent = smsTemplate.sms_content
            if (smsContent != null)
                binding.tvMessage.text = smsContent
            //---- custom sms start ----//
            customSms = if (smsId == 9)
                binding.tvMessage.text.toString()
            else
                getString(R.string.empty)
            //---- custom sms end ----//
        }

        // check for dropdown
        if (smsTemplate!!.sms_input_mode.isNotEmpty()) {
            var defaultOption = ""
            val index = smsTemplate.sms_input_mode.indexOfFirst { it.is_employee_option }
            if (index != -1) {
                defaultOption = smsTemplate.sms_input_mode[index].default_option
            }
            if (smsTemplate.sms_input_mode.any { it.is_employee_option }) {
                generateEmployeeOptionSpinner(defaultOption)
            }
            inputFieldBusNumber()
            inputFieldDriverName()
            inputFieldMobileNumber()
            formDelay()
            inputFieldBoardingPoint()
        }
    }

    private fun inputFieldBoardingPoint() {
        val index =
            smsTemplate?.sms_input_mode?.indexOfFirst { it.name.equals("boarding_point", true) }
        var defaultOption = ""
        if (index != -1) {
            defaultOption = index?.let {
                smsTemplate?.sms_input_mode?.get(it)?.default_option
            }!!
        }
        val oldSmsContent = smsTemplate?.sms_content
        smsContent = smsContent?.let { modifySmsContent(it, "**boarding_point**", defaultOption) }
        binding.tvMessage.text = smsContent

        if (smsTemplate?.sms_input_mode?.any {
                !it.name.isNullOrEmpty() && it.name.equals(
                    "boarding_point",
                    true
                ) && it.is_input_field
            }!!) {
            binding.layoutBoardingPoint.visible()
            boardingPointTextWatcher(defaultOption, oldSmsContent)
        } else
            binding.layoutBoardingPoint.gone()

        onTextChangeListener(binding.etBoardingPoint)
    }

    private fun boardingPointTextWatcher(defaultOption: String, oldSmsContent: String?) {
        binding.etBoardingPoint.onChange {
            if (defaultOption.isNotEmpty())
                smsContent = smsContent?.let { it1 -> modifySmsContent(it1, defaultOption, it) }
            else
                smsContent =
                    oldSmsContent?.let { it1 -> modifySmsContent(it1, "**boarding_point**", it) }


            if (binding.layoutMobileNumber.isVisible)
                smsContent = modifySmsContent(
                    smsContent!!,
                    "**mobile_number**",
                    binding.etMobileNumber.text.toString()
                )

            if (binding.spinnerMinutes.isVisible)
                smsContent = smsContent?.let { it1 ->
                    modifySmsContent(
                        it1,
                        "**minutes**",
                        binding.acMinutes.text.toString()
                    )
                }
            binding.tvMessage.text = smsContent
        }
    }

    private fun formDelay() {
        if (smsTemplate?.sms_input_mode?.any {
                !it.options.isNullOrEmpty()
            }!!) {
            binding.tvBusDelay.visible()
            binding.layoutBusDelayTraffic.visible()
            generateHhMmSpinner(smsTemplate!!.sms_input_mode)
        } else {
            binding.tvBusDelay.gone()
            binding.layoutBusDelayTraffic.gone()
        }

    }

    private fun inputFieldMobileNumber() {
        val index =
            smsTemplate?.sms_input_mode?.indexOfFirst { it.name.equals("mobile_number", true) }
        var defaultOption = ""
        if (index != -1) {
            defaultOption = index?.let {
                smsTemplate?.sms_input_mode?.get(it)?.default_option
            }!!
        }
        val oldSmsContent = smsContent
        smsContent = smsContent?.let { modifySmsContent(it, "**mobile_number**", defaultOption) }
        binding.tvMessage.text = smsContent

        if (smsTemplate?.sms_input_mode?.any {
                !it.name.isNullOrEmpty() && it.name.equals(
                    "mobile_number",
                    true
                ) && it.is_input_field
            }!!) {
            binding.layoutMobileNumber.visible()

            if (smsContent != null) {
                binding.etMobileNumber.onChange {
                    if (defaultOption.isNotEmpty())
                        smsContent = modifySmsContent(smsContent!!, defaultOption, it)
                    else
                        smsContent = modifySmsContent(oldSmsContent!!, "**mobile_number**", it)

                    if (binding.layoutBoardingPoint.isVisible)
                        smsContent = modifySmsContent(
                            smsContent!!,
                            "**boarding_point**",
                            binding.etBoardingPoint.text.toString()
                        )

                    if (binding.spinnerMinutes.isVisible)
                        smsContent = modifySmsContent(
                            smsContent!!,
                            "**minutes**",
                            binding.acMinutes.text.toString()
                        )
                    binding.tvMessage.text = smsContent
                }
            }
        } else
            binding.layoutMobileNumber.gone()

        onTextChangeListener(binding.etMobileNumber)
    }

    private fun inputFieldDriverName() {
        if (smsTemplate?.sms_input_mode?.any {
                !it.name.isNullOrEmpty() && it.name.equals(
                    "driver_name",
                    true
                ) && it.is_input_field
            }!!) {
            binding.layoutDriverName.visible()
        } else
            binding.layoutDriverName.gone()

        onTextChangeListener(binding.etDriverName)
    }

    private fun inputFieldBusNumber() {
        if (smsTemplate?.sms_input_mode?.any {
                !it.name.isNullOrEmpty() && it.name.equals(
                    "bus_number",
                    true
                ) && it.is_input_field
            }!!) {
            binding.layoutBusNumber.visible()
        } else
            binding.layoutBusNumber.gone()

        onTextChangeListener(binding.etBusNumber)
    }

    private fun onTextChangeListener(etText: TextInputEditText) {
        etText.onChange {
            if (it.isEmpty())
                binding.chkSmsAll.isChecked = false
        }
    }

    private fun generateHhMmSpinner(smsInputMode: List<SmsInputMode>) {
        if (smsInputMode.any { it.name.equals("hours", true) }) {
            binding.spinnerHour.visible()
            val index = smsInputMode.indexOfFirst { it.name.equals("hours", true) }
            val optionsHour = smsInputMode[index].options
            var defaultOptionHour = smsInputMode[index].default_option
            smsContent = modifySmsContent(smsContent!!, "**hours**", defaultOptionHour)
            binding.tvMessage.text = smsContent

            binding.acHour.setAdapter(
                ArrayAdapter(
                    requireActivity(),
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    optionsHour
                )
            )

            binding.acHour.setText(defaultOptionHour, false)

            binding.acHour.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    smsContent =
                        modifySmsContent(smsContent!!, defaultOptionHour, optionsHour[position])
                    binding.tvMessage.text = smsContent
                    defaultOptionHour = optionsHour[position]
                }
        } else
            binding.spinnerHour.gone()

        if (smsInputMode.any { it.name.equals("minutes", true) }) {
            val index = smsInputMode.indexOfFirst { it.name.equals("minutes", true) }
            val options = smsInputMode[index].options
            var defaultOption = smsInputMode[index].default_option
            smsContent = modifySmsContent(smsContent!!, "**minutes**", defaultOption)
            binding.tvMessage.text = smsContent
            binding.acMinutes.setAdapter(
                ArrayAdapter(
                    requireActivity(),
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    options
                )
            )
            binding.acMinutes.setText(defaultOption, false)

            binding.acMinutes.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    smsContent = modifySmsContent(smsContent!!, defaultOption, options[position])
                    binding.tvMessage.text = smsContent
                    defaultOption = options[position]
                }

        } else
            binding.spinnerMinutes.gone()
    }

    private fun modifySmsContent(content: String, replace: String, replacedBy: String): String {
        return content.replace(replace, replacedBy)
    }

    private fun generateEmployeeOptionSpinner(defaultOption: String) {
        binding.tvIncludeMobile.visible()
        binding.spinner.visible()
        binding.acEmployeeType.setAdapter(
            ArrayAdapter(
                requireActivity(),
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                employeeTypeSpinnerItem
            )
        )
        if (defaultOption.isNotEmpty())
            binding.acEmployeeType.setText(
                convertFirstCharToUpperCase(
                    defaultOption.lowercase(Locale.getDefault())
                ), false
            )
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}


