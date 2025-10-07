package com.bitla.ts.presentation.view.dashboard.ViewReservationFragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.data.listener.OnSeatSelectionListener
import com.bitla.ts.data.multiple_shift_passenger_method_name
import com.bitla.ts.data.operator_api_key
import com.bitla.ts.data.service_details_method
import com.bitla.ts.data.view_reservation_method_name
import com.bitla.ts.databinding.FragmentShiftPassengersBinding
import com.bitla.ts.databinding.SheetdialogbulkshiftBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.multiple_shift_passenger.request.Data
import com.bitla.ts.domain.pojo.multiple_shift_passenger.request.ReqBody
import com.bitla.ts.domain.pojo.service_details_response.SeatDetail
import com.bitla.ts.domain.pojo.view_reservation.PassengerDetail
import com.bitla.ts.presentation.adapter.AutoShiftAdapter
import com.bitla.ts.presentation.adapter.BulkListleftAdapter
import com.bitla.ts.presentation.adapter.BulkShiftListAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.activity.SeatShiftingSuccessfulActivity
import com.bitla.ts.presentation.view.activity.reservationOption.SelectServiceActivity
import com.bitla.ts.presentation.view.fragments.AllCoachFragment
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.presentation.viewModel.ShiftPassengerViewModel
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.setDateLocale
import com.bitla.ts.utils.constants.BULK_SHIFT_PASSENGER
import com.bitla.ts.utils.constants.ShiftPax
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ShiftPassengersFragment : Fragment(), View.OnClickListener,
    OnSeatSelectionListener, OnItemClickListener, OnItemPassData,
    DialogSingleButtonListener {
    private lateinit var binding: FragmentShiftPassengersBinding
    private var travelDate: String = ""
    private var apiSelected: String = ""
    private var actualTravelDate: String = ""
    private var bccId: Int? = 0
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var passengerlist2 = arrayListOf<PassengerDetail>()
    private var oldSeatList: ArrayList<String> = arrayListOf()
    private var newSeatlist: ArrayList<String> = arrayListOf()
    private var seatMap = mutableMapOf<String, String>()
    private var oldTicketNumberMap = mutableMapOf<String, String>()
    private var data = arrayListOf<Data>()
    private var autoselectedData = arrayListOf<Data>()
    private var autoUnselectedData = arrayListOf<Data>()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()

    private var oldreservationId: String = ""
    private var newReservationID: String = ""
    private var seatShiftOption: String = ""
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var loginModelPref: LoginModel = LoginModel()
    private val shiftPassengerViewModel by viewModel<ShiftPassengerViewModel<Any?>>()
    private var newTravelDate: String = ""
    private lateinit var commonCoach: AllCoachFragment
    private var dateSelected = ""
    private var serviceName = ""
    private var servicenumber = ""

    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private var bulkShift = PreferenceUtils.getString("BulkShiftBack")
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var bulkShiftingadapter: BulkShiftListAdapter
    private lateinit var autoShiftAdapter: AutoShiftAdapter
    private lateinit var bulkShiftingLeftAdapter: BulkListleftAdapter
    private var moveToExtra = false
    private var smsPassenger = false
    private val newextraseat = arrayListOf<String>()
    val oldseat = arrayListOf<String>()
    private var proceedOnclick = false
    private var selectionType: Int = 1
    private var passengerNameMap = mutableMapOf<String, String>()
    private var droppingmap = mutableMapOf<String, String>()
    private var boardingMpd = mutableMapOf<String, String>()
    private lateinit var mcalendar: Calendar
    private var locale: String? = ""
    private var isBimaTicket: Boolean = false
    
    
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentShiftPassengersBinding.inflate(layoutInflater)
        getPrefs()
        val dataAvailable = PreferenceUtils.getPreference("dataAvailable", true)
        if (dataAvailable!!) {
            binding.scrollView3.visible()
            binding.NoResult.gone()
        } else {
            binding.scrollView3.gone()
            binding.NoResult.visible()
        }
        viewReservationObserver()
        multiShiftPassengerObserver()


        binding.etName.setText("$servicenumber $serviceName ${getString(R.string.current_service)}")

        mcalendar = Calendar.getInstance()
        day = mcalendar.get(Calendar.DAY_OF_MONTH)
        year = mcalendar.get(Calendar.YEAR)
        month = mcalendar.get(Calendar.MONTH)
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val sdf2 = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())
        dateSelected = sdf2.format(Date())

        binding.etSelectDateShiftPasngr.setText("")

        binding.etselectService.setOnClickListener(this)
        binding.etSelectDateShiftPasngr.setOnClickListener(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        lifecycleScope.launch {
            shiftPassengerViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
    }
    
    private fun getPrefs() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        sourceId = PreferenceUtils.getString("ViewReservation_OriginId")!!
        destinationId = PreferenceUtils.getString("ViewReservation_DestinationId")!!
        loginModelPref = PreferenceUtils.getLogin()
        serviceName = PreferenceUtils.getString("ViewReservation_name").toString()
        servicenumber = PreferenceUtils.getString("ViewReservation_number").toString()
        oldreservationId = PreferenceUtils.getString("reservationid") ?: ""

        if (PreferenceUtils.getPreference("is_bima", false) == true) {
            isBimaTicket = true
        }
       Timber.d("12124 : $bccId , $sourceId , $destinationId , $travelDate , $newTravelDate , $oldreservationId")
        PreferenceUtils.putString("SHIFT_Traveldate", travelDate)
        PreferenceUtils.putString("SHIFT_originId", sourceId)
        PreferenceUtils.putString("SHIFT_destinationId", destinationId)
    }

    @SuppressLint("SetTextI18n")
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                seatMap.clear()

                PreferenceUtils.setPreference("seatwiseFare", "fromBulkShiftPassenger")

                val returnApiSelected =
                    result.data?.getStringExtra("ApiNameSelected").toString()
                apiSelected = returnApiSelected
                binding.etselectService.text = apiSelected
                newReservationID = PreferenceUtils.getString("BulkShiftBack_resId").toString()

                seatShiftOption =
                    PreferenceUtils.getPreference("shiftTypeOption", "").toString()

                bulkShift = PreferenceUtils.getString("BulkShiftBack")
                Timber.d("extraSeatCheck:flow 1: $bulkShift")
                new()

                if (seatShiftOption == "") {
                    selectionType = 0
                    binding.etselectService.text = ""
                }


                if (seatShiftOption == "manually") {
                    selectionType = 1
                    pickUpChartApi("3")

                    binding.manualLayout.visible()
                    binding.btnProceed.visible()
                    binding.autoLayout.gone()


                }
                if (seatShiftOption == "seats") {
                    Timber.d("extraSeatCheck:flow 2: $bulkShift")
                    selectionType = 2
                    binding.autoLayout.visible()
                    binding.autoHead.text = getString(R.string.auto_select_seats)
                    autoShiftApi(newReservationID)
                    autoShiftObserver()
                }
                if (seatShiftOption == "row") {
                    selectionType = 3
                    Timber.d("extraSeatCheck:flow 3: $bulkShift")
                    binding.autoLayout.visible()
                    binding.autoHead.text = getString(R.string.auto_select_seats)


                    autoShiftApi(newReservationID)
                    autoShiftObserver()
                }

                binding.viewCoachLayout.setOnClickListener {
                    binding.coachLayoutView.visible()
                    binding.closeCoachLayout.setOnClickListener {
                        binding.coachLayoutView.gone()
                    }
                    binding.coachLayoutView.setOnClickListener {
                        binding.coachLayoutView.gone()
                    }
                }
                binding.viewCoachLayoutAuto.setOnClickListener {
                    binding.coachLayoutView.visible()
                    binding.closeCoachLayout.setOnClickListener {
                        binding.coachLayoutView.gone()
                    }
                    binding.coachLayoutView.setOnClickListener {
                        binding.coachLayoutView.gone()
                    }
                }
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                val sdf2 = SimpleDateFormat("yyyy-MM-dd")
                val currentDate = sdf.format(Date())

                if (bulkShift == "yes") {
                    Timber.d("extraSeatCheck:flow 4: $bulkShift")
                    binding.checkSmsPassengerWithNewDetails.setOnCheckedChangeListener { buttonView, isChecked ->
                        smsPassenger = isChecked
                    }
                    binding.checkShiftRemainingSeats.setOnCheckedChangeListener { buttonView, isChecked ->
                        moveToExtra = isChecked

                        Timber.d("extraSeatCheck:flow 5: ${autoselectedData.isNullOrEmpty()} == ${seatMap.size}")
                        if (autoselectedData.isNullOrEmpty() || seatMap.isEmpty()) {
                            proceedOnclick = isChecked

                            proceedBtnObserver(isChecked)
                        } else {
                            proceedOnclick = true

                            proceedBtnObserver(true)
                        }
                    }

                    binding.etName.setText("$servicenumber $serviceName ${getString(R.string.current_service)}")

                    val date = PreferenceUtils.getString("shiftPassenger_selectedDate")!!.split("-")
                    val selectedYear = date[0]
                    val selectedMonth = date[1]
                    val selectedDate = date[2]
                    binding.etSelectDateShiftPasngr.setText("${selectedDate}/${selectedMonth}/${selectedYear}")
                    binding.etselectService.text = PreferenceUtils.getString("BulkShiftBack_apiNamr")
                    newReservationID = PreferenceUtils.getString("BulkShiftBack_resId").toString()
                    seatShiftOption =
                        PreferenceUtils.getPreference("shiftTypeOption", "").toString()

                } else
                {
                    Timber.d("extraSeatCheck:flow 6: $bulkShift")
                    binding.etSelectDateShiftPasngr.setText(currentDate)
                    PreferenceUtils.putString("shiftPassenger_selectedDate", dateSelected)
                }

                binding.btnProceed.setOnClickListener {
                    data.clear()
                    newextraseat.clear()
                    oldseat.clear()
                    Timber.d("extraSeatCheck:flow 7: $bulkShift")

                    if (moveToExtra) {
                        Timber.d("extraSeatCheck:flow 8: $bulkShift")

                        if (oldTicketNumberMap.isEmpty()) {
                            val oldseatnumber = oldTicketNumberMap.keys.toList()
                            val pnr = oldTicketNumberMap.values.toList()
                            Timber.d("extraSeatCheck:flow 9: $bulkShift")
                            oldseat.addAll(oldseatnumber)
                            for (i in 0..oldseatnumber.size.minus(1)) {
                                if (oldseatnumber[i].contains("Ex-")){
                                    newextraseat.add(oldseatnumber[i])
                                }else{
                                    newextraseat.add("Ex-${oldseatnumber[i]}")
                                }
                            }
                            for (i in 0..oldseatnumber.size.minus(1)) {
                                data.add(Data(newextraseat[i], oldseatnumber[i], pnr[i]))
                            }
                            Timber.d("extraSeatCheck:flow 10: $bulkShift")

                            bottomSheet(oldseat, newextraseat)


                        } else {

                            val keys = seatMap.keys.toList()
                            val values = seatMap.values.toList()
                            val oldseatnumber = oldTicketNumberMap.keys.toMutableList()
                            val finalOldSeatNumber = arrayListOf<String>()
                            val pnr = mutableListOf<String>()

                            if (autoselectedData.size > 0) {
                                Timber.d("extraSeatCheck:flow 12: $bulkShift")
                                for (i in 0..autoselectedData.size.minus(1)) {
                                    oldSeatList.add(autoselectedData[i].old_seat_number)
                                    newSeatlist.add(autoselectedData[i].new_seat_number)
                                }
                            }
                            Timber.d("extraSeatCheck:flow 13.1: ${seatMap.size}, ${oldseatnumber.size} ")
                            for (i in 0..seatMap.size.minus(1)) {
                                if (oldseatnumber.contains(keys[i])) {
                                    oldseatnumber.remove(keys[i])
                                }
                                if (values[i] == " " || values[i] == "") {
                                    if (keys[i].contains("Ex-")){
                                        newextraseat.add(keys[i])
                                    }else{
                                        newextraseat.add("Ex-${keys[i]}")
                                    }
                                } else {
                                    newextraseat.add(values[i])
                                }


                            }
                            for (i in 0..oldseatnumber.size.minus(1)) {
                                Timber.d("extraSeatCheck:flow 2 14:${oldseatnumber}")

                                if (oldseatnumber[i].contains("Ex-")){
                                    newextraseat.add(oldseatnumber[i])
                                }else{
                                    newextraseat.add("Ex-${oldseatnumber[i]}")
                                }
                            }

                            finalOldSeatNumber.addAll(keys)
                            finalOldSeatNumber.addAll(oldseatnumber)
                            for (i in 0..finalOldSeatNumber.size.minus(1)) {

                                pnr.add(oldTicketNumberMap.getValue(finalOldSeatNumber[i]))
                                data.add(Data(newextraseat[i], finalOldSeatNumber[i], pnr[i]))
                            }

                            Timber.d("extraSeatCheck:flow 14: $newextraseat \n $finalOldSeatNumber \n $pnr")

                            bottomSheet(finalOldSeatNumber, newextraseat)
                        }
                    } else {
                        Timber.d("extraSeatCheck:flow 15: ")
                        if (!proceedOnclick) {
                            requireContext().toast(getString(R.string.please_fill_all_the_required_details))
                        } else {

                            Timber.d("extraSeatCheck:flow 16: ")

                            oldSeatList.clear()
                            newSeatlist.clear()
                            data.clear()
                            val keys = seatMap.keys.toList()
                            val values = seatMap.values.toList()
                            var mapSize = seatMap.size
                            if (seatMap.isNotEmpty()) {
                                Timber.d("extraSeatCheck:flow 17: ")

                                for (i in 0..seatMap.size.minus(1)) {
                                    oldSeatList.add(keys[i])
                                    newSeatlist.add(values[i])

                                }
                            }

                            if (seatMap.isNotEmpty()) {
                                Timber.d("extraSeatCheck:flow 19: $oldSeatList ")

                                for (j in 0..seatMap.size.minus(1)) {
                                    if (oldSeatList.contains(keys[j])) {
                                        for (i in 0..seatMap.size.minus(1)) {

                                            if (oldSeatList[i] == (keys[j])) {
                                                newSeatlist.removeAt(i)
                                                newSeatlist.add(i, values[j])
                                            }
                                        }
                                        Timber.d("extraSeatCheck:flow 20: $oldSeatList ")
                                    } else {
                                        Timber.d("extraSeatCheck:flow 21: $oldSeatList ")
                                        oldSeatList.add(keys[j])
                                        newSeatlist.add(values[j])
                                    }


                                }
                            }
                            val oldrejectseat = arrayListOf<String>()
                            val newrejectseat = arrayListOf<String>()

                            for (i in 0..oldSeatList.size.minus(1)) {


                                try {
                                    val ticket =
                                        oldTicketNumberMap.getValue(oldSeatList[i])
                                            .filterNot { it.isWhitespace() }
                                    data.add(Data(newSeatlist[i], oldSeatList[i], ticket))
                                } catch (e: NoSuchElementException) {
                                    oldrejectseat.add(oldSeatList[i])
                                    newrejectseat.add(newSeatlist[i])


                                }
                            }

                            for (i in 0..oldrejectseat.size.minus(1)) {
                                if (oldSeatList.contains(oldrejectseat[i])) {
                                    oldSeatList.remove(oldrejectseat[i])
                                    newSeatlist.remove(newrejectseat[i])
                                }
                            }

                            Timber.d("extraSeatCheck:flow 22: $oldSeatList ")

                            bottomSheet(oldSeatList, newSeatlist)

                        }

                    }
                }


            }
        }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.etSelectDate_shift_pasngr -> {
                openDateDialog()
            }
            R.id.etselect_service -> {
                if (!binding.etSelectDateShiftPasngr.text.isNullOrEmpty()) {
                    if (PreferenceUtils.getPreference("dataAvailable", false)!!) {
                        PreferenceUtils.setPreference("fromTicketDetail", false)

                        val intent = Intent(requireContext(), SelectServiceActivity::class.java)
                        intent.putExtra("selectionType", selectionType)
                        intent.putExtra("isFromPickupChart", true)
                        resultLauncher.launch(intent)
                    } else {
                        requireContext().toast(getString(R.string.all_seatsMay_have_been_cancelled))
                    }
                }else
                    requireContext().toast(requireContext().getString(R.string.validate_date))
            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun openDateDialog() {
        val listener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth
                ->
                if (monthOfYear == 9 || monthOfYear == 10 || monthOfYear == 11) {
                    if (dayOfMonth <= 9) {
                        binding.etSelectDateShiftPasngr.setText("$dayOfMonth/${monthOfYear + 1}/$year")
                        dateSelected = "$year-${monthOfYear + 1}-0${dayOfMonth}"

                    } else {
                        binding.etSelectDateShiftPasngr.setText("$dayOfMonth/${monthOfYear + 1}/$year")
                        dateSelected = "$year-${monthOfYear + 1}-${dayOfMonth}"
                    }

                } else {
                    if (dayOfMonth <= 9) {
                        binding.etSelectDateShiftPasngr.setText("$dayOfMonth/0${monthOfYear + 1}/$year")
                        dateSelected = "$year-0${monthOfYear + 1}-0${dayOfMonth}"
                    } else {
                        binding.etSelectDateShiftPasngr.setText("$dayOfMonth/0${monthOfYear + 1}/$year")
                        dateSelected = "$year-0${monthOfYear + 1}-${dayOfMonth}"
                    }

                }

                Timber.d("monthOfYwear  : $dateSelected")
                PreferenceUtils.putString("shiftPassenger_selectedDate", dateSelected)


            }
        setDateLocale(locale!!,requireContext())
        val dpDialog = DatePickerDialog(requireContext(), listener, year, month, day)
        dpDialog.show()
    }

    private fun multiShiftPassengerAPI(
        newReservationId: String,
        shiftToExtra: Int,
        smsNumber: Int
    ) {
        if (requireContext().isNetworkAvailable()) {
            binding.mainProgressBar.visible()

            shiftPassengerViewModel.multiShiftPassengerAPI(
                
                ReqBody(
                    api_key = loginModelPref.api_key,
                    data = data,
                    shift_to_extra_seats = shiftToExtra,
                    new_res_id = newReservationId,
                    old_res_id = oldreservationId,
                    remarks = binding.textInputEditTextRemarks.text.toString(),
                    to_send_sms = smsNumber,
                    locale = locale,
                    is_bima_service = isBimaTicket
                ),
                multiple_shift_passenger_method_name
            )

        } else requireContext().noNetworkToast()
    }

    private fun multiShiftPassengerObserver() {
        shiftPassengerViewModel.multiShiftPassengerResponse.observe(viewLifecycleOwner) {
            binding.mainProgressBar.gone()
            if (it != null) {
                when {
                    it.code == "200" -> {

                        firebaseLogEvent(
                            requireContext(),
                            BULK_SHIFT_PASSENGER,
                            loginModelPref.userName,
                            loginModelPref.travels_name,
                            loginModelPref.role,
                            BULK_SHIFT_PASSENGER,
                            ShiftPax.BULK_SHIFT_PASSENGER
                        )

                        val oldSeatNumbers = arrayListOf<String>()
                        val tempNewSeatNumber = arrayListOf<String>()
                        val ticketNumber = arrayListOf<String>()
                        val passengerName = arrayListOf<String>()
                        val boardingFrom = arrayListOf<String>()
                        val droppingAt = arrayListOf<String>()
                        for (i in 0..data.size.minus(1)) {
                            oldSeatNumbers.add(data[i].old_seat_number)
                            tempNewSeatNumber.add(data[i].new_seat_number)
                            ticketNumber.add(data[i].ticket_number)
                            if (passengerNameMap.containsKey(oldSeatNumbers[i])) {
                                passengerName.add(passengerNameMap.getValue(oldSeatNumbers[i]))
                                droppingAt.add(droppingmap.getValue(oldSeatNumbers[i]))
                                boardingFrom.add(boardingMpd.getValue(oldSeatNumbers[i]))
                            }
                        }
                        val intent = Intent(context, SeatShiftingSuccessfulActivity::class.java)
                        intent.putExtra("fromActivity", false)
                        intent.putExtra("oldSeatNumbers", oldSeatNumbers.toString())
                        intent.putExtra("tempNewSeatNumber", tempNewSeatNumber.toString())
                        intent.putExtra("pnrList", ticketNumber.toString())
                        intent.putExtra("boardingfromList", boardingFrom.toString())
                        intent.putExtra("dropingFromList", droppingAt.toString())
                        intent.putExtra("PassengerNameList", passengerName.toString())

                        intent.putExtra(
                            "oldServiceNumberShift",
                            "${servicenumber}?${actualTravelDate}"
                        )
                        intent.putExtra("oldserviceDate", passengerName.toString())
                        intent.putExtra("newServiceDate", passengerName.toString())

                        intent.putExtra("PassengerNameList", passengerName.toString())
                        requireContext().startActivity(intent)
                        requireActivity().finish()
                        if (it.message != null) {
                            it.message.let { it1 -> requireContext().toast(it1) }
                        }

                    }
                    it.code == "401" -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }
                    it.message != null -> {
                        it.message.let { it1 -> requireContext().toast(it1) }
                    }
                }
            } else
                requireContext().toast(getString(R.string.server_error))
        }
    }


    private fun manualAdapter(availableRoutes: MutableList<PassengerDetail>) {

        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.manualList.isNestedScrollingEnabled = false
        binding.manualList.layoutManager = layoutManager
        bulkShiftingadapter =
            BulkShiftListAdapter(requireContext(), availableRoutes, this, this)
        binding.manualList.adapter = bulkShiftingadapter


    }

    private fun autoAdapter(oldlist: MutableList<PassengerDetail>, newfixList: MutableList<Data>) {

        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.autoList.layoutManager = layoutManager
        autoShiftAdapter =
            AutoShiftAdapter(requireContext(), oldlist, newfixList, this)
        binding.autoList.adapter = autoShiftAdapter


    }

    private fun autoShiftApi(res: String) {
        if (requireContext().isNetworkAvailable()) {
            binding.mainProgressBar.visible()

            if (seatShiftOption == "seats") {
                shiftPassengerViewModel.autoShiftAPI(
                    com.bitla.ts.domain.pojo.auto_shift.request.ReqBody(
                        api_key = loginModelPref.api_key,
                        auto_macth_by = "seat",
                        new_res_id = res,
                        old_res_id = oldreservationId,
                        locale = locale
                    ), service_details_method
                )
            } else {

                shiftPassengerViewModel.autoShiftAPI(
                    com.bitla.ts.domain.pojo.auto_shift.request.ReqBody(
                        api_key = loginModelPref.api_key,
                        auto_macth_by = "seat",
                        new_res_id = res,
                        old_res_id = oldreservationId,
                        locale = locale
                    ), service_details_method
                )
            }
        } else requireContext().noNetworkToast()


    }


    private fun autoShiftObserver() {
        shiftPassengerViewModel.autoShiftResponse.observe(
            requireActivity()
        ) {
            autoselectedData.clear()
            autoUnselectedData.clear()

            if (it.code == 200) {
                for (i in 0..it.result.size.minus(1)) {
                    var resultData = arrayListOf<com.bitla.ts.domain.pojo.auto_shift.Result>()
                    val ticketNumber = it.result[i].ticket_number
                    val newSeat = it.result[i].new_seat_number
                    val oldSeat = it.result[i].old_seats_number
                    if (it.result[i].new_seat_number == "") {
                        autoUnselectedData.add(Data(newSeat, oldSeat, ticketNumber))
                    } else {
                        autoselectedData.add(Data(newSeat, oldSeat, ticketNumber))
                    }
                }
                Timber.d("autoselectedseats: ${autoselectedData}, $autoUnselectedData")
                pickUpChartApi("3")

            } else
                requireContext().toast(getString(R.string.opps))

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

    override fun releaseTicket(ticketNumber: String, ReleaseTicket: String) {

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

    override fun selectedSeatCount(selectedSeats: ArrayList<SeatDetail>) {

    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        
    }

    override fun onClickOfItem(data: String, position: Int) {
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onItemData(view: View, str1: String, str2: String) {
        seatMap += Pair(str2, str1)
        if (str1 == "") {
            if (seatMap.values.contains(str1)) {
                seatMap.remove(str2)
            }
        }
        Timber.d("onclick true: $seatMap == $str1")

        if (seatMap.isNotEmpty()) {
            if (seatMap.size == 1) {
                val key = seatMap.keys.toList()

                if (seatMap.getValue(key[0]) == " " || seatMap.getValue(key[0]) == "") {
                    proceedOnclick = false
                    proceedBtnObserver(false)
                } else {
                    proceedOnclick = true
                    proceedBtnObserver(true)
                }
            }
            else {
                val key = seatMap.keys.toList()

                for (i in 0..seatMap.size.minus(1)) {
                    if (seatMap.getValue(key[i]) == " " || seatMap.getValue(key[i]) == "") {
                        proceedOnclick = false
                        proceedBtnObserver(false)
                    } else {
                        proceedOnclick = true
                        proceedBtnObserver(true)

                    }
                }
            }
        }
        else {
            if (moveToExtra) {
                proceedOnclick = true

                proceedBtnObserver(true)
            } else {
                proceedOnclick = false

                proceedBtnObserver(false)
            }
        }
    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
    }


    private fun new() {
        sharedViewModel.getServiceDetails(
            reservationId = newReservationID,
            apiKey = loginModelPref.api_key,
            originId = sourceId,
            destinationId = destinationId,
            operatorApiKey = operator_api_key,
            locale = locale ?: "en",
            apiType = service_details_method,
            excludePassengerDetails = false
        )

        sharedViewModel.serviceDetails.observe(requireActivity(), {
            if (it.code == 200) {
                    binding.coachLayoutFragment.layoutCoachSingleShift.visible()
                    commonCoach =
                        childFragmentManager.findFragmentById(R.id.layout_coach_single_shift) as AllCoachFragment
                    if (::commonCoach.isInitialized)
                        commonCoach.setCoachData(it.body)
                    commonCoach.binding.layoutBlockAllSeats.gone()

                binding.coachLayoutFragment.coachProgressBar.gone()

                commonCoach.binding.apply {
                    selectallseats.gone()
                    selectAllSeatsToUnblock.gone()
                    layoutBlockAllSeats.gone()
                    manifestBtn.gone()
                }

            } else
                it.message?.let { it1 -> Timber.d( it1) }
        })
    }

    override fun onSingleButtonClick(str: String) {

        if (moveToExtra) {
            if (smsPassenger) {
                multiShiftPassengerAPI(newReservationID, 1, newextraseat.size)
            } else {
                multiShiftPassengerAPI(newReservationID, 1, 0)
            }

        } else {
            if (smsPassenger) {
                multiShiftPassengerAPI(newReservationID, 0, newSeatlist.size)
            } else {
                multiShiftPassengerAPI(newReservationID, 0, 0)
            }
        }

        if (str == getString(R.string.unauthorized)) {
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }


    private fun pickUpChartApi(chartTypeSelected: String) {
        if (requireContext().isNetworkAvailable()) {
            pickUpChartViewModel.viewReservationAPI(
                apiKey = loginModelPref.api_key,
                resId = oldreservationId,
                chartType = chartTypeSelected,
                locale = locale ?: "",
                apiType = view_reservation_method_name,
                newPickUpChart = null
            )
        } else requireContext().noNetworkToast()

    }

    private fun viewReservationObserver() {
        pickUpChartViewModel.viewReservationResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                if (!passengerlist2.isNullOrEmpty()) {
                    passengerlist2.clear()
                }
                passengerlist2 = it.passengerDetails
                binding.checkShiftRemainingSeats.visible()
                binding.checkSmsPassengerWithNewDetails.visible()
                binding.textInputRemarks.visible()
                binding.btnProceed.visible()
                if (seatShiftOption == "manually") {
                    binding.mainProgressBar.gone()
                    if (it.passengerDetails != null) {
                        for (i in 0..it.passengerDetails.size.minus(1)) {
                            oldTicketNumberMap +=
                                Pair(
                                    it.passengerDetails[i].seatNumber,
                                    it.passengerDetails[i].pnrNumber
                                )
                        }

                        manualAdapter(passengerlist2)
                    }

                }
                if (seatShiftOption == "seats" || seatShiftOption == "row") {
                    val extraseat: ArrayList<PassengerDetail> = arrayListOf()

                    proceedOnclick = true

                    binding.mainProgressBar.gone()
                    if (it.passengerDetails != null) {
                        for (i in 0..it.passengerDetails.size.minus(1)) {

                            oldTicketNumberMap +=
                                Pair(
                                    it.passengerDetails[i].seatNumber,
                                    it.passengerDetails[i].pnrNumber
                                )

                            if (it.passengerDetails[i].seatNumber.lowercase().contains("ex")) {
                                extraseat.add(it.passengerDetails[i])
                            }
                        }
                    }

                    val autoSelectpassenger: ArrayList<PassengerDetail> = arrayListOf()
                    val autoSelectpassengerSorted: ArrayList<PassengerDetail> = arrayListOf()
                    val autoUnSelectpassenger: ArrayList<PassengerDetail> = arrayListOf()
                    oldSeatList.clear()
                    if (autoselectedData.size > 0) {
                        proceedOnclick= true
                        proceedBtnObserver(true)

                        binding.autoLayout.visible()
                        if (autoselectedData != null) {
                            for (j in 0..autoselectedData.size.minus(1)) {
                                if (it.passengerDetails != null) {
                                    for (i in 0..it.passengerDetails.size.minus(1)) {

                                        if (autoselectedData[j].old_seat_number == passengerlist2[i].seatNumber) {
                                            autoSelectpassenger.add(passengerlist2[i])
                                            oldSeatList.add(passengerlist2[i].seatNumber)
                                        }

                                    }
                                }
                            }

                            autoAdapter(autoSelectpassenger, autoselectedData)
                        }

                    } else {
                        proceedOnclick= false
                        proceedBtnObserver(false)

                        binding.autoLayout.gone()

                    }


                    if (!extraseat.isNullOrEmpty()) {
                        autoUnSelectpassenger.addAll(extraseat)
                    }

                    if (autoUnselectedData.size > 0) {
                        binding.manualLayout.visible()
                        binding.mainProgressBar.gone()
                        if (passengerlist2 != null) {
                            for (i in 0..passengerlist2.size.minus(1)) {
                                for (j in 0..autoUnselectedData.size.minus(1)) {

                                    if (passengerlist2[i].seatNumber == autoUnselectedData[j].old_seat_number) {
                                        autoUnSelectpassenger.add(passengerlist2[i])
                                    }
                                }
                            }
                        }
                    }
                    if (autoUnSelectpassenger.size > 0) {
                        manualAdapter(autoUnSelectpassenger)
                        binding.manualLayout.visible()

                    } else {
                        binding.manualLayout.gone()
                        binding.viewCoachLayoutAuto.visible()

                    }

                }
                boardingMpd.clear()
                droppingmap.clear()
                passengerNameMap.clear()
                if (it.passengerDetails != null) {
                    for (i in 0..it.passengerDetails.size.minus(1)) {
                        actualTravelDate = it.passengerDetails[i].actualTravelDate
                        boardingMpd += Pair(
                            it.passengerDetails[i].seatNumber,
                            it.passengerDetails[i].boardingCity

                        )
                        droppingmap += Pair(
                            it.passengerDetails[i].seatNumber,
                            it.passengerDetails[i].droppingCity
                        )
                        passengerNameMap += Pair(
                            it.passengerDetails[i].seatNumber,
                            it.passengerDetails[i].passengerName
                        )
                    }
                }


            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun proceedBtnObserver(check: Boolean) {
        if (check) {
            binding.btnProceed.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        } else {
            binding.btnProceed.setBackgroundColor(resources.getColor(R.color.button_default_color))
        }
    }

    fun bottomSheet(oldList: ArrayList<String>, newList: ArrayList<String>) {

        val toService = binding.etselectService.text
        val bottomSheetDialoge = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val binding = SheetdialogbulkshiftBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialoge.setContentView(binding.root)


        layoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.rvOldListOne.layoutManager = layoutManager
        bulkShiftingLeftAdapter =
            BulkListleftAdapter(requireContext(), oldList, newList, this)
        binding.rvOldListOne.adapter = bulkShiftingLeftAdapter

        binding.goBack.setOnClickListener {
            bottomSheetDialoge.dismiss()
        }
        binding.sheetProceed.setOnClickListener {
            bottomSheetDialoge.dismiss()


            val oldseat = oldList.toString().replace("[", "").replace("]", "").trim()
            val newseat = newList.toString().replace("[", "").replace("]", "").trim()

            DialogUtils.shiftPassengerDialog(
                requireContext(),
                getString(R.string.confirmShiftingPassenger),
                getString(R.string.shiftPassengerContent),
                getString(R.string.from_service),
                "$servicenumber $serviceName",
                getString(R.string.to_service),
                toService.toString(),
                newseat,
                oldseat,
                getString(R.string.goBack),
                getString(R.string.confirmShifting),
                this
            )

        }
        bottomSheetDialoge.show()
    }
}
