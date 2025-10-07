package com.bitla.ts.presentation.view.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.manage_fare_method_name
import com.bitla.ts.data.service_details_method
import com.bitla.ts.databinding.SheetMultistationEditBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.request.FareDetailsRequest
import com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.request.ManageFareMultiStationRequest
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.FareDetail
import com.bitla.ts.presentation.adapter.MultiStationFareDetailsAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.dashboard.update_rate_card_fragments.MultistationFragment
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.getTodayDate
import com.bitla.ts.utils.common.setDateLocale
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import java.text.SimpleDateFormat
import java.util.*

class BottomModalSheetFragment : BottomSheetDialogFragment(), DialogSingleButtonListener {
    companion object {

        val TAG = BottomModalSheetFragment::class.java.simpleName
    }

    private var privileges: PrivilegeResponseModel? = null
    private var travelDate: String = ""
    lateinit var binding: SheetMultistationEditBinding
    var fromDate: String? = null
    var fromDateDDMMYYYY: String? = null
    var toDate: String? = null
    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private lateinit var mcalendar: Calendar
    private lateinit var fareDetailsResponse: ArrayList<FareDetail>
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    var isRvItemEmpty: Boolean = true
    private var routeId = ""
    private var reservationId = ""
    var myHashMap = HashMap<Int, FareDetailsRequest>()
    var originId = ""
    var destinationId = ""
    private var locale: String? = ""
    private var pinSize = 0
    private var modifyReservation = false
    private var currentCountry: String = ""

    fun newInstance(
        fd: ArrayList<FareDetail>,
        srcDst: String,
        routeId: String,
        reservationId: String,
        originId: String,
        destinationId: String,
    ): BottomModalSheetFragment {
        val bottomSheetFragment = BottomModalSheetFragment()
        val bundle = Bundle()
        bundle.putSerializable("fare details", fd)
        bundle.putSerializable("srcDst", srcDst)
        bundle.putSerializable("routeId", routeId)
        bundle.putSerializable("reservationId", reservationId)
        bundle.putSerializable("originId", originId)
        bundle.putSerializable("destinationId", destinationId)
        bottomSheetFragment.arguments = bundle
        return bottomSheetFragment
    }

    private fun getPref() {
        privileges = (activity as BaseActivity).getPrivilegeBase()
        privileges?.let { privilegeResponseModel ->
            pinSize = privilegeResponseModel.pinCount ?: 6
            modifyReservation = privilegeResponseModel.pinBasedActionPrivileges?.modifyReservation ?: false
            currentCountry = privilegeResponseModel.country ?: ""
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = SheetMultistationEditBinding.inflate(inflater, container, false)
        fareDetailsResponse =
            arguments?.getSerializable("fare details") as ArrayList<FareDetail>

//        fareDetailRequests.forEach {
//            Timber.d(it.seat_type)
//            Timber.d(it.fare.toString())
//        }
        locale = PreferenceUtils.getlang()
        getPref()

        val srcDst = arguments?.getSerializable("srcDst") as String
        binding.destination.text = srcDst
        routeId = arguments?.getSerializable("routeId") as String
        reservationId = arguments?.getSerializable("reservationId") as String
        originId = arguments?.getSerializable("originId") as String
        destinationId = arguments?.getSerializable("destinationId") as String
//        Toast.makeText(requireContext(), routeId + " - " + reservationId, Toast.LENGTH_SHORT).show()
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvFareDetails.layoutManager = layoutManager
        val multistationFairAdapter =
            MultiStationFareDetailsAdapter(requireActivity(), fareDetailsResponse,
                object : MultiStationFareDetailsAdapter.EditTextEmptyListener {
                    override fun isEmpty(isEmpty: Boolean) {
                        changeButtonBackground(fromDate, toDate, isEmpty)
                        isRvItemEmpty = isEmpty
                    }

                    override fun getRvData(position: Int, fareDetailsRequest: FareDetailsRequest) {
                        myHashMap.put(position, fareDetailsRequest)

                    }
                })
        binding.rvFareDetails.adapter = multistationFairAdapter
        travelDate =
            PreferenceUtils.getString(getString(R.string.updateRateCard_travelDate)).toString()
        onClick()
        //createEditTextLayout()
        mcalendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val parser = SimpleDateFormat("dd-MM-yyyy")
        if(!travelDate.isNullOrEmpty()){
            day = travelDate.substringAfterLast("-").replace("0","").toInt()
            year = mcalendar.get(Calendar.YEAR)
            month = travelDate.substringAfter("-").substringBefore("-").replace("0","").toInt() - 1
            fromDate = travelDate
            val inputDate: Date = formatter.parse(travelDate)
            fromDateDDMMYYYY = parser.format(inputDate)
            binding.etFromDateUrc.setText(fromDateDDMMYYYY)

        }else{
            day = mcalendar.get(Calendar.DAY_OF_MONTH)
            year = mcalendar.get(Calendar.YEAR)
            month = mcalendar.get(Calendar.MONTH)
            fromDateDDMMYYYY = getTodayDate()
            val parser = SimpleDateFormat("dd-MM-yyyy")
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            fromDate = formatter.format(parser.parse(fromDateDDMMYYYY))
            binding.etFromDateUrc.setText(fromDateDDMMYYYY)
        }


        changeButtonBackground(fromDate, toDate, isRvItemEmpty)
        setUpObserver()
        return binding.root
    }


    private fun onClick() {

        binding.etFromDateUrc.setOnClickListener {
            openFromDateDialog()
            changeButtonBackground(fromDate, toDate, isRvItemEmpty)

        }
        binding.etToDateUrc.setOnClickListener {
            openToDateDialog()
            changeButtonBackground(fromDate, toDate, isRvItemEmpty)

        }

        binding.appCompatButton.setOnClickListener {
            dismiss()
        }

        binding.appCompatButton2.setOnClickListener {
            authPinDialog()

        }

    }

    private fun authPinDialog() {
        if (modifyReservation && currentCountry.equals("india", true)) {
            DialogUtils.showFullHeightPinInputBottomSheet(
                activity = requireActivity(),
                fragmentManager = childFragmentManager,
                pinSize = pinSize,
                getString(R.string.multistation_fare),
                onPinSubmitted = { pin: String ->
                    callManageMultiStationFareApi(pin)
                },
                onDismiss = null
            )
        } else {
            callManageMultiStationFareApi("")
        }

    }

    @SuppressLint("SetTextI18n")
    private fun openFromDateDialog() {
        val listener =
            DatePickerDialog.OnDateSetListener {
                    _, year, monthOfYear, dayOfMonth,
                ->
                var dateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
                var date = dateFormat.parse("$dayOfMonth-${monthOfYear + 1}-$year")
                binding.etFromDateUrc.setText(dateFormat.format(date).toString())

                fromDate = binding.etFromDateUrc.text.toString()

                fromDateDDMMYYYY = fromDate
                val parser = SimpleDateFormat("dd-MM-yyyy")
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                fromDate = formatter.format(parser.parse(fromDate))
                toDate = null
                binding.etToDateUrc.setText("")
                binding.etToDateUrc.clearFocus()
                changeButtonBackground(fromDate, toDate, isRvItemEmpty)
            }
        setDateLocale(locale!!,requireContext())
        val dpDialog = DatePickerDialog(requireContext(), listener, year, month, day)
        var dateInString: String = getTodayDate()
        var simpleDateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
       /* val calendar = Calendar.getInstance()
        calendar.time = simpleDateFormat.parse(dateInString)
        calendar.add(Calendar.DATE, 28)
        dpDialog.datePicker.maxDate = calendar.timeInMillis*/
        val calendarMinDate = Calendar.getInstance()
        calendarMinDate.time = simpleDateFormat.parse(dateInString)
        dpDialog.datePicker.minDate = calendarMinDate.timeInMillis
        dpDialog.show()
    }

    private fun openToDateDialog() {
        if (fromDate.isNullOrEmpty()) {
            Toast.makeText(
                context,
                requireActivity().getString(R.string.validate_from_date),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val listener =
                DatePickerDialog.OnDateSetListener {
                        _, year, monthOfYear, dayOfMonth,
                    ->
                    var dateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
                    var date = dateFormat.parse("$dayOfMonth-${monthOfYear + 1}-$year")
                    binding.etToDateUrc.setText(dateFormat.format(date).toString())
                    toDate = binding.etToDateUrc.text.toString()
                    val parser = SimpleDateFormat("dd-MM-yyyy")
                    val formatter = SimpleDateFormat("yyyy-MM-dd")
                    toDate = formatter.format(parser.parse(toDate))

                    changeButtonBackground(fromDate, toDate, isRvItemEmpty)
                }
            setDateLocale(locale!!,requireContext())
            val dpDialog = DatePickerDialog(requireContext(), listener, year, month, day)
            var dateInString: String = getTodayDate()
            var simpleDateFormat = SimpleDateFormat(DATE_FORMAT_D_M_Y)
            val calendar = Calendar.getInstance()
            calendar.time = simpleDateFormat.parse(dateInString)
            calendar.add(Calendar.DATE, 28)
            dpDialog.datePicker.maxDate = calendar.timeInMillis
            var calenderMinDate = Calendar.getInstance()
            calenderMinDate.time = simpleDateFormat.parse(fromDateDDMMYYYY)
            dpDialog.datePicker.minDate = calenderMinDate.timeInMillis
            dpDialog.show()
        }
    }

    private fun setUpObserver() {
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        pickUpChartViewModel.changeButtonBackground.observe(this, Observer {
            if (it) {
                binding.appCompatButton2.apply {
                    setBackgroundResource(R.drawable.button_selected_bg)
                    isEnabled = true
                }
            } else {
                binding.appCompatButton2.apply {
                    setBackgroundResource(R.drawable.button_default_bg)
                    isEnabled = false
                }
            }
        })


        pickUpChartViewModel.manageFareMultiStationResponse.observe(viewLifecycleOwner) {
            //Timber.d("manage multistation wise fare  ${it}")
            if (it != null) {
                when (it.code) {
                    200 -> {
                        Toast.makeText(requireContext(), it.result?.message, Toast.LENGTH_SHORT)
                            .show()
                        var aa = MultistationFragment.instance
                        aa?.callMultiStationWiseFairApi()
                        dismiss()

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
                        if (it.result?.message != null)
                            requireContext().toast(it.result.message)
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }

    }

    private fun changeButtonBackground(fromDate: String?, toDate: String?, isEmpty: Boolean) {
        pickUpChartViewModel.changeButtonBackground(fromDate, toDate, isEmpty)
    }

    private fun callManageMultiStationFareApi(authPin: String) {
        val bccId = PreferenceUtils.getBccId().toString()
        val loginModelPref: LoginModel = PreferenceUtils.getLogin()
        Timber.d("callManageMultiStationFareApi: ${myHashMap.size}")

        for ((key, value) in myHashMap) {
            Timber.d("callManageMultiStationFareApi: $key = ${value.seat_type_id} , ${value.seat_type} , ${value.fare}")
        }
        val fareDetailsArrayList = ArrayList<FareDetailsRequest>()
        for ((key, value) in myHashMap) {
            fareDetailsArrayList.add(value)
        }
        val manageFareMultiStationRequest = ManageFareMultiStationRequest(
            bccId, format_type, manage_fare_method_name,
            com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.request.ReqBody(
                loginModelPref.api_key,
                reservationId,
                routeId,
                "multistation",
                originId,
                destinationId,
                fromDate.toString(),
                toDate.toString(),
                fareDetailsArrayList,
                locale = locale,
                authPin = authPin
            )
        )
        if (requireActivity().isNetworkAvailable()) {
          /*  pickUpChartViewModel.manageFareMultiStation(
                loginModelPref.auth_token,
                loginModelPref.api_key, manageFareMultiStationRequest, service_details_method
            )*/

            pickUpChartViewModel.manageFareMultiStation(
             com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.request.ReqBody(
                    loginModelPref.api_key,
                    reservationId,
                    routeId,
                    "multistation",
                    originId,
                    destinationId,
                    fromDate.toString(),
                    toDate.toString(),
                    fareDetailsArrayList,
                    locale = locale,
                    authPin = authPin
                ), service_details_method
            )
        } else {
            requireContext().noNetworkToast()
        }
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