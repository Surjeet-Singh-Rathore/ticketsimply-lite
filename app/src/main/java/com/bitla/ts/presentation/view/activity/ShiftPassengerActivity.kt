package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.content.*
import android.os.*
import android.text.*
import android.text.style.*
import android.view.*
import android.widget.*
import androidx.activity.result.contract.*
import androidx.core.content.*
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.domain.pojo.ticket_details.response.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.reservationOption.*
import com.bitla.ts.presentation.view.fragments.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.text.*
import java.util.*


class ShiftPassengerActivity : BaseActivity(), View.OnClickListener, OnItemClickListener,
    AdapterView.OnItemSelectedListener,
    OnItemCheckListener, DialogSingleButtonListener, OnSeatSelectionListener {

    companion object {
        val TAG: String = ShiftPassengerActivity::class.java.simpleName
        private lateinit var binding: ShiftPassengerActivityBinding
    }

    private var country: String? = null
    private lateinit var selectMultipleSeatItemList: MutableList<SelectMultipleSeatModel>

    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private lateinit var mcalendar: Calendar
    private var shiftTo = "shift to"
    private lateinit var spannableShiftTo: SpannableString
    private lateinit var shiftSeat: String
    private var isClick = 0
    private lateinit var selectedSeats: String
    private var dateSelected = ""
    private var apiSelected: String = ""
    private var oldServiceName = ""
    private var oldServiceDetails = ""
    private var newServiceDetails = ""
    private var ticketNumber = ""
    private var originDestination = ""

    //    private var oldServiceDate = ""
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private val singleShiftPassenger by viewModel<ShiftPassengerViewModel<Any?>>()
    private lateinit var apiKey: String

    private lateinit var loginModelPref: LoginModel
    private var reservationId: Long = 0L
    private var noofSeats: Int = 0
    private var oldSeatNumbers = ""
    private var pnrNumber = ""
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var serviceName: String = ""
    private lateinit var bccId: String
    private var userTypeList: MutableList<SpinnerItems> = mutableListOf()
    private var isAllowMultipleQuota: Boolean = true
    private var selectedSeatList: MutableList<SpinnerItems> = mutableListOf()
    private var selectedReservationId: String? = null
    private var finalResId = ""
    private var checked = false
    private var fixed = ""
    private var seat2 = ""
    private var tempNewSeatNumber = ""
    private var locale: String? = ""
    private lateinit var commonCoachsingle: AllCoachFragment
    private var isPartialShift = false
    private var sameServiceName = ""
    private var isBimaTicket: Boolean = false
    private var shouldTicketShift: Boolean = false
    private var pinSize = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mcalendar = Calendar.getInstance()
        day = mcalendar.get(Calendar.DAY_OF_MONTH)
        year = mcalendar.get(Calendar.YEAR)
        month = mcalendar.get(Calendar.MONTH)

        oldServiceDetails = PreferenceUtils.getString("oldServiceNumberShiftACTIVITY")!!
        oldServiceName = PreferenceUtils.getString("SHIFT_servicename")!!
        ticketNumber = intent.getStringExtra("service_ticketno")!!.substringBefore(" ")
        sameServiceName = PreferenceUtils.getString("SHIFT_same_serviceName")!!
        isPartialShift = intent.getBooleanExtra("partial_shift", false)

        getPref()
        shiftPassengerObserver()
        setObserver()

        binding.apply {
            etextra.setText(oldSeatNumbers)
            etFromService.setText(" $oldServiceName ${getString(R.string.current_service)}")
            etdToday.setOnClickListener(this@ShiftPassengerActivity)
            edtSelectSeatNo.setOnClickListener(this@ShiftPassengerActivity)
            edtToService.setOnClickListener(this@ShiftPassengerActivity)
            toolbarPassengerDetails.toolbarImageLeft.setOnClickListener(this@ShiftPassengerActivity)
            shiftPassengerProceedBtn.setOnClickListener(this@ShiftPassengerActivity)
            mainLayout.setOnClickListener(this@ShiftPassengerActivity)
            coachProgressBar.setOnClickListener(this@ShiftPassengerActivity)
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    @SuppressLint("SimpleDateFormat")
    override fun initUI() {
        binding = ShiftPassengerActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        spannableShiftTo = SpannableString(shiftTo)
        spannableShiftTo.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.colorPrimary)),
            0,
            4,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )


        binding.checkShiftRemainingSeats.setOnClickListener {
            if (binding.edtToService.text.isNullOrEmpty()) {
                binding.checkShiftRemainingSeats.isChecked = false
                toast(getString(R.string.please_select_new_service))
            } else {
                if (binding.checkShiftRemainingSeats.isChecked) {
                    binding.edtSelectSeatNoLayout.gone()
                    binding.moveToextraSeatLayout.visible()
                    binding.tvMoreSlected.gone()
                    checked = true

                    proceedBtnObserver(true)
                } else {
                    binding.edtSelectSeatNoLayout.visible()
                    binding.moveToextraSeatLayout.gone()
                    binding.shiftPassengerProceedBtn.setBackgroundColor(resources.getColor(R.color.button_default_color))
                    selectedSeatList.clear()
                    binding.edtSelectSeatNo.setText("")
                    checked = false
                }
            }
        }

//        get and set current date
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val sdf2 = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())
//        dateSelected = sdf2.format(Date())
        binding.etdToday.setText(getDateDMY(PreferenceUtils.getTravelDate()))
        dateSelected = getDateYMD(binding.etdToday.text.toString())

        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            singleShiftPassenger.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun openDateDialog() {
        val listener =
            OnDateSetListener { _, year, monthOfYear, dayOfMonth
                ->
                if (monthOfYear == 9 || monthOfYear == 10 || monthOfYear == 11) {
                    if (dayOfMonth <= 9) {
                        binding.etdToday.setText("$dayOfMonth/${monthOfYear + 1}/$year")
                        dateSelected = "$year-${monthOfYear + 1}-0${dayOfMonth}"

                    } else {
                        binding.etdToday.setText("$dayOfMonth/${monthOfYear + 1}/$year")
                        dateSelected = "$year-${monthOfYear + 1}-${dayOfMonth}"
                    }

                } else {
                    if (dayOfMonth <= 9) {
                        binding.etdToday.setText("$dayOfMonth/0${monthOfYear + 1}/$year")
                        dateSelected = "$year-0${monthOfYear + 1}-0${dayOfMonth}"
                    } else {
                        binding.etdToday.setText("$dayOfMonth/0${monthOfYear + 1}/$year")
                        dateSelected = "$year-0${monthOfYear + 1}-${dayOfMonth}"
                    }

                }
                openActivityForResult()

                Timber.d("monthOfYwear  : ${monthOfYear}")


            }
        val dpDialog = DatePickerDialog(this, listener, year, month, day)
        dpDialog.show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.etdToday -> {
                binding.apply {
                    openDateDialog()

                }
            }

            R.id.edt_selectSeatNo -> {
                if (isClick == 0) {
//                    binding.rvSelectMultipleSeats.visible()
                    isClick++
                } else {
//                    binding.rvSelectMultipleSeats.gone()
                    isClick = 0
                }
            }

            R.id.layout_selectSeatNo -> {
//                binding.rvSelectMultipleSeats.gone()
//                binding.tvMoreSeats.visible()
                binding.tvAvailableSeats.visible()
            }

            R.id.edt_toService -> {
                openActivityForResult()
            }

            R.id.toolbar_image_left -> onBackPressed()
            R.id.coach_progress_bar -> {
                Timber.d("")
            }


            R.id.shiftPassenger_proceedBtn -> {

                if (checked) {
                    var seatarray = listOf<String>()

                    seat2 = binding.etextra.text.toString()

                    if (noofSeats == 1) {
                        if (seat2.contains(",")) {
                            toast("Please enter 1 seat Number")
                        } else {
                            shiftSeat = "Shifting ${oldSeatNumbers} seats to Ex-(${seat2})"
                            proceedBtnObserver(true)
                            tempNewSeatNumber = fixed
                            shiftDialogbox(seat2, oldSeatNumbers)

                        }
                    } else {
                        if (seat2.contains(",")) {
                            seatarray = seat2.split(",")
                        }

                        if (seatarray.size == noofSeats) {
                            if (seatarray.contains("")) {
                                toast("please enter a valid seat number")
                            } else {
                                val final = replaceBracketsString(seatarray.toString())

                                shiftSeat = "Shifting ${oldSeatNumbers} seats to Ex-(${final})"
                                proceedBtnObserver(true)
                                tempNewSeatNumber = fixed
                                shiftDialogbox(final, oldSeatNumbers)
                            }


                        } else {
                            toast("Please Enter seat numbers correctly")

                        }
                    }

                } else {
                    if (selectedSeatList.size != noofSeats || binding.edtToService.text.isNullOrEmpty()) {
                        toast("please fill all details")
                    } else {
                        fixed = replaceBracketsString(selectedSeatList.toString())
                        shiftSeat = ("$oldSeatNumbers $spannableShiftTo $fixed")
                        proceedBtnObserver(true)
                        tempNewSeatNumber = fixed
                        shiftDialogbox(fixed, oldSeatNumbers)
                    }
                }
            }
        }
    }


    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val returnApiSelected = result.data?.getStringExtra("ApiNameSelected").toString()
                selectedReservationId = result.data?.getStringExtra("selectedReservation")
                apiSelected = returnApiSelected
                binding.edtToService.setText(apiSelected)
                callServiceApi()
                selectedSeatList.clear()
                binding.edtSelectSeatNo.setText("")
            }
        }

    private fun proceedBtnObserver(check: Boolean) {
        if (binding.edtToService.text.isNullOrEmpty()) {
            binding.shiftPassengerProceedBtn.setBackgroundColor(resources.getColor(R.color.button_default_color))
        } else {
            if (check) {
                binding.shiftPassengerProceedBtn.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            } else {
                binding.shiftPassengerProceedBtn.setBackgroundColor(resources.getColor(R.color.button_default_color))

            }
        }
    }


    private fun openActivityForResult() {
        if (checked) {
            binding.edtSelectSeatNo.text.clear()
            selectedSeatList.clear()
            binding.tvMoreSlected.gone()
            binding.edtSelectSeatNo.clearFocus()
            binding.shiftPassengerProceedBtn.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        } else {
            binding.edtSelectSeatNo.text.clear()
            selectedSeatList.clear()
            binding.tvMoreSlected.gone()
            binding.edtSelectSeatNo.clearFocus()
            if (selectedSeatList.isEmpty() || binding.edtToService.text.isNullOrEmpty()) {
                binding.shiftPassengerProceedBtn.setBackgroundColor(resources.getColor(R.color.button_default_color))
            } else {
                binding.shiftPassengerProceedBtn.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            }
        }

        PreferenceUtils.setPreference("fromTicketDetail", true)
        PreferenceUtils.putString("shiftPassenger_selectedDate", dateSelected)
        val intent = Intent(this, SelectServiceActivity::class.java)
        resultLauncher.launch(intent)

    }


    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {

        val checkedSeats = view as CheckBox

        if (checkedSeats.isChecked) {
            binding.mainLayout.setOnClickListener {
//                binding.rvSelectMultipleSeats.gone()
//                binding.tvMoreSeats.visible()
                binding.tvAvailableSeats.visible()
            }
        } else {
            binding.edtSelectSeatNo.setText("")
        }

        selectedSeats = selectMultipleSeatItemList[position].title
        binding.edtSelectSeatNo.setText(selectedSeats)

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onItemCheck(item: PassengerDetail?) {
    }

    override fun onItemUncheck(item: PassengerDetail?) {
    }


    private fun seatSpinner() {
        /*spinnerAdapter =
        binding.sUserType.adapter = spinnerAdapter*/
        binding.edtSelectSeatNo.onItemSelectedListener = this

        binding.edtSelectSeatNo.setAdapter(
            SelectUserTypeArrayAdapter(this,
                R.layout.spinner_dropdown_item_witch_checkbox,
                R.id.tvItem,
                userTypeList,

                selectedSeatList,
                isAllowMultipleQuota,
                object : SelectUserTypeArrayAdapter.ItemClickListener {
                    override fun onSelected(position: Int, item: SpinnerItems) {

                        Timber.d("selectedSeatListSize ${selectedSeatList.size} noofSeats $noofSeats")

                        if (selectedSeatList.size >= noofSeats) {
                            binding.edtSelectSeatNo.clearFocus()
                            toast(getString(R.string.no_more_seats_can_be_selected))
                        } else {
                            if (selectedSeatList.contains(item).not())
                                selectedSeatList.add(item)
                        }
                        if (selectedSeatList.size > 0) {
                            binding.edtSelectSeatNo.setText(
                                selectedSeatList[selectedSeatList.size.minus(
                                    1
                                )].toString()
                            )
                        }
                        invalidateCount()

                    }


                    override fun onDeselect(position: Int, item: SpinnerItems) {
                        if (selectedSeatList.contains(item))
                            selectedSeatList.remove(item)
                        binding.edtSelectSeatNo.setText(
                            selectedSeatList.firstOrNull().toString().replace("null", "")
                        )
                        if (selectedSeatList.size == 1) {
                            val selectedPosition = userTypeList.indexOfFirst {
                                it.value == selectedSeatList[0].value
                            }
                        }
                        Timber.d("count12: ${selectedSeatList}")
                        invalidateCount()

                    }
                })
        )


        binding.edtSelectSeatNo.setOnItemClickListener { adapterView, view, position, l ->
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    private fun invalidateCount() {
        if (selectedSeatList.size == noofSeats) {
            proceedBtnObserver(true)
        } else {
            proceedBtnObserver(false)
        }


        if (selectedSeatList.size > 1) {
            binding.tvMoreSlected.apply {
                visibility = View.VISIBLE
                text = "+ ${selectedSeatList.size - 1} ${getString(R.string.more)}"
            }
        } else {
            binding.tvMoreSlected.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId().toString()
        locale = PreferenceUtils.getlang()
        apiKey = loginModelPref.api_key
        sourceId = PreferenceUtils.getString("SHIFT_originId")?:""
        destinationId = PreferenceUtils.getString("SHIFT_destinationId")?:""
        reservationId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)?:0L
//        noofSeats = PreferenceUtils.getString("TicketDetail_noOfSeats")!!.toInt()
//        oldSeatNumbers = PreferenceUtils.getString("TicketDetail_SeatNumbes")!!
       
        if (PreferenceUtils.getPreference("is_bima", false) == true) {
            isBimaTicket = true
        }
        pnrNumber = PreferenceUtils.getString("SHIFT_SeatPnrNumber")?.substringBefore(" ") ?: ""
        originDestination = PreferenceUtils.getString("SHIFT_origin_destination") ?: ""

        getPrivilegeBase()?.let { privilegeResponseModel ->
            shouldTicketShift = privilegeResponseModel.pinBasedActionPrivileges?.ticketShifting ?: false
            pinSize = privilegeResponseModel.pinCount ?: 6
            country = privilegeResponseModel.country
        }
        if (country.equals("indonesia", true))
            binding.relativeLayout6.gone()
        else
            binding.relativeLayout6.visible()
        
        
        if (isPartialShift) {
            binding.apply {
                layoutToService.isEnabled = false
                layoutToday.isEnabled = false
                checkShiftRemainingSeats.gone()
                edtToService.setText(" $oldServiceName ${getString(R.string.same_service)}")
                toolbarPassengerDetails.toolbarHeaderText.text =
                    getString(R.string.shift_to_same_service)
            }
            oldSeatNumbers = PreferenceUtils.getString("TicketDetail_SeatNumber_sameService") ?: ""
            noofSeats = 1
            binding.toolbarPassengerDetails.toolbarSubtitle.text =
                "${getString(R.string.shift_pnr)} $pnrNumber \n${getString(R.string.seat_no)}: $oldSeatNumbers"
            callServiceApi()
        } else {
            binding.apply {
                layoutToService.isEnabled = true
                layoutToday.isEnabled = true
                checkShiftRemainingSeats.isEnabled = true
                checkShiftRemainingSeats.visible()
                toolbarPassengerDetails.toolbarHeaderText.text =
                    getString(R.string.shift_passengers)
            }
            if (!PreferenceUtils.getString("TicketDetail_noOfSeats").isNullOrEmpty())
                noofSeats = PreferenceUtils.getString("TicketDetail_noOfSeats")?.toInt() ?: 0
            else
                noofSeats = 1
            oldSeatNumbers = PreferenceUtils.getString("TicketDetail_SeatNumbes") ?: ""
            binding.toolbarPassengerDetails.toolbarSubtitle.text =
                "${getString(R.string.shift_pnr)} $pnrNumber \n${getString(R.string.seat_no)}: $oldSeatNumbers"
        }
        
        if (isBimaTicket || isPartialShift) {
            binding.checkShiftRemainingSeats.gone()
        } else {
            binding.checkShiftRemainingSeats.visible()
        }
        
    }

    private fun callServiceApi() {
        if (selectedReservationId == null) {
            finalResId = reservationId.toString()
        } else {
            finalResId = selectedReservationId as String
        }

        if (isNetworkAvailable()) {
            sharedViewModel.getServiceDetails(
                reservationId = finalResId,
                apiKey = loginModelPref.api_key,
                originId = sourceId,
                destinationId = destinationId,
                operatorApiKey = operator_api_key,
                locale = locale!!,
                apiType = service_details_method,
                excludePassengerDetails = false,
                appBimaEnabled = isBimaTicket
            )
        } else {
            noNetworkToast()
        }
    }

    private fun setObserver() {
        sharedViewModel.serviceDetails.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                if (country.equals("Indonesia", true)
                    || country.equals("Malaysia", true)
                    || country.equals("Vietnam", true)
                ) {
                    addCoach(it)
                } else {
                    userTypeList.clear()
                    val availseat = ArrayList<SeatDetail>()
                    serviceName = "${it.body.number} ${it.body.origin?.name} -${it.body.destination?.name} "

                    val totalseats = it.body.coachDetails?.seatDetails

                    if (!totalseats.isNullOrEmpty()) {
                        for (i in 0..totalseats.size?.minus(1)!!) {
                            if (totalseats[i].available!!) {
                                availseat.add(totalseats[i])
                            }
                        }
                    }

                    if (availseat.isNotEmpty()) {
                        for (j in 0..availseat.size.minus(1)) {
                            userTypeList.add(
                                SpinnerItems(
                                    availseat[j].rowId!!,
                                    availseat[j].number
                                )
                            )
                        }
                    }

                    seatSpinner()
                }
                newServiceDetails = "${it.body.number}?${it.body.travelDate}"
            } else
                it?.message?.let { it1 -> Timber.d(it1) }

        })
    }

    private fun addCoach(serviceDetailsModel: ServiceDetailsModel) {
        if (serviceDetailsModel.code == 200) {

            serviceName =
                "${serviceDetailsModel.body.number} ${serviceDetailsModel.body.origin?.name} -${serviceDetailsModel.body.destination?.name} "

            serviceDetailsModel.body.isFromShiftPassenger = true
            serviceDetailsModel.body.shiftPassengerCount = noofSeats
            serviceDetailsModel.body.routeId?.let { it1 ->
                setRouteId(
                    it1,
                    serviceDetailsModel.body.isGstApplicable
                )
            }

            binding.coachProgressBar.gone()
            binding.layoutCoachSingle.visible()
            commonCoachsingle =
                supportFragmentManager.findFragmentById(R.id.layout_coach_single) as AllCoachFragment
            if (::commonCoachsingle.isInitialized) {
                commonCoachsingle.setCoachData(serviceDetailsModel.body)

                commonCoachsingle.binding.apply {
                    selectallseats.gone()
                    selectAllSeatsToUnblock.gone()
                    layoutBlockAllSeats.gone()
                    manifestBtn.gone()
                }


            }
            commonCoachsingle.binding.layoutBlockAllSeats.gone()

        }
    }


    private fun singleShiftPassengerAPI(extraseats: String, newSeat: String, authPin: String) {
        if (this.isNetworkAvailable()) {

            binding.coachProgressBar.visible()

            singleShiftPassenger.singleShiftPassengerAPI(

                com.bitla.ts.domain.pojo.singleShiftPassenger.request.ReqBody(
                    api_key = loginModelPref.api_key,
                    extra_seat_nos = extraseats,
                    old_seat_numbers = oldSeatNumbers,
                    remarks = binding.textInputEditTextRemarks.text.toString(),
                    reservation_id = finalResId,
                    seat_count = noofSeats.toString(),  //total Number of seats
                    seat_number = newSeat,
                    ticket_number = ticketNumber,
                    to_send_sms = selectedSeatList.size.toString(), //seat count
                    partial_shift = isPartialShift,
                    locale = locale,
                    is_bima_service = isBimaTicket,
                    auth_pin = authPin
                ),
                resend_otp_and_qr_code_method_name
            )
        } else this.noNetworkToast()
    }

    private fun shiftDialogbox(newSeat: String, oldSeat: String) {
        var list = ""
        tempNewSeatNumber = newSeat
        if (checked) {
            list = "Ex-($newSeat)"
        } else {
            list = newSeat
        }
        DialogUtils.shiftPassengerDialog(
            context = this@ShiftPassengerActivity,
            title = getString(R.string.confirmShiftingPassenger),
            message = getString(R.string.shiftPassengerContent),
            fromHeader = getString(R.string.from_service),
            fromSubtitle = "$oldServiceName $originDestination",
            toHeader = getString(R.string.to_service),
            toSubtitle = binding.edtToService.text.toString(),
            newSeat = list,
            oldSeat = oldSeat,
            buttonLeftText = getString(R.string.goBack),
            buttonRightText = getString(R.string.confirmShifting),
            singleButtonListener = this@ShiftPassengerActivity
        )
    }

    private fun pinAuthDialogBox(extraseats: String, newSeat: String) {
        if (shouldTicketShift && country.equals("india", true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = this,
                fragmentManager = supportFragmentManager,
                pinSize = pinSize,
                getString(R.string.shift_passengers),
                onPinSubmitted = { pin: String ->
                    singleShiftPassengerAPI(extraseats, newSeat, pin)
                },
                onDismiss = null
            )
        } else {
            singleShiftPassengerAPI(extraseats, newSeat, "")
        }
    }

    private fun shiftPassengerObserver() {

        singleShiftPassenger.singleShiftPassengerResponse.observe(this) {
            if (it != null) {
                binding.coachProgressBar.gone()
                when (it.code) {
                    200 -> {
                        firebaseLogEvent(
                            this,
                            PNR_WISE_SHIFT_PASSENGER,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            PNR_WISE_SHIFT_PASSENGER,
                            ShiftPax.PNR_WISE_SHIFT_PASSENGER
                        )
                        PreferenceUtils.setPreference(PREF_UPDATE_COACH,true)
                        val intent = Intent(this, SeatShiftingSuccessfulActivity::class.java)
                        newServiceDetails = PreferenceUtils.getPreference("ApiNumberSelected", newServiceDetails)!!
                        intent.apply {
                            putExtra("pnrList", pnrNumber)
                            putExtra("oldServiceName", oldServiceName)
                            putExtra("newServiceName", serviceName)
                            putExtra("originDestination", originDestination)
                            putExtra("moveToExtra", checked)
                            putExtra("fromActivity", true)
                            putExtra("oldSeatNumbers", oldSeatNumbers)
                            putExtra("tempNewSeatNumber", tempNewSeatNumber)
                            putExtra("oldServiceNumberShift", oldServiceDetails)
                        }
              
                        PreferenceUtils.setPreference("ApiNumberSelected", newServiceDetails)
                        this.startActivity(intent)
                        finish()
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
                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> toast(it1) }
                        }
                    }
                }

            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (checked) {
            if (noofSeats == 1) {
                Timber.d("exterseatssomething: ture")
                pinAuthDialogBox(seat2, "")
            } else {
                Timber.d("exterseatssomething: false")
                val finalextra = replaceBracketsString(seat2)
                pinAuthDialogBox(finalextra, "")
            }
        } else {
            Timber.d("exterseats checked: $selectedSeatList")
            val finalSelectedSeats = replaceBracketsString(selectedSeatList.toString())
            val final = finalSelectedSeats.replace(" ", "")
            Timber.d("exterseats checked: $final")
            pinAuthDialogBox("", final)
        }
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }


    }

    override fun selectedSeatCount(selectedSeatDetails: ArrayList<SeatDetail>) {
        if (selectedSeatDetails.size == noofSeats) {
            proceedBtnObserver(true)
        } else {
            proceedBtnObserver(false)
        }

        selectedSeatList.clear()
        selectedSeatDetails.forEach { it1 ->
            if (it1.number != null) {
                val index = selectedSeatList.indexOfFirst { it.value == it1.number }
                if (index != -1) {
                    selectedSeatList.removeAt(index)
                } else {
                    try {
                        // id is not being used  therefor putting it as a constant  ( spinnerItems.id= 0)
                        val spinnerItems = SpinnerItems(0, it1.number)
                        selectedSeatList.add(spinnerItems)
                    } catch (e: Exception) {
                        toast(getString(R.string.this_seat_cannot_be_selected))
                    }

                }
            }
        }
    }

    override fun onSeatSelection(
        selectedSeatDetails: ArrayList<SeatDetail>,
        finalSeatNumber: ArrayList<String?>,
        totalSum: Double,
        isAllSeatSelected: Boolean,
        isSeatLongPress: Boolean?
    ) {
    }

    override fun unSelectAllSeats() {

    }

    override fun unblockSeat(
        seatNumber: String,
        selectionType: String,
        fromDate: String?,
        toDate: String?,
        remarks: String?
    ) {

    }

    override fun editSeatFare(seatNumber: String, newFare: String) {

    }

    override fun bookExtraSeats(isChecked: Boolean?, isSeatSelected: Boolean?) {

    }

    override fun moveExtraSeat(isChecked: Boolean) {

    }

    override fun releaseTicket(ticketNumber: String, releaseTicket: String) {

    }

    override fun callPassenger(ticketNumber: String, contactNumber: String) {

    }

    override fun checkBoardedStatus(
        status: Boolean,
        passengerName: String,
        pnrNum: String,
        seatNumber: String,
        view: View
    ) {
    }
}
