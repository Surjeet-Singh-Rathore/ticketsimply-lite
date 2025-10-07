package com.bitla.ts.presentation.view.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.edit_chart_method_name
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.operator_api_key
import com.bitla.ts.data.service_details_method
import com.bitla.ts.databinding.BottomsheetCountryCodeBinding
import com.bitla.ts.databinding.FragmentEmptySeatsBinding
import com.bitla.ts.domain.pojo.Countries
import com.bitla.ts.domain.pojo.available_routes.BoardingPointDetail
import com.bitla.ts.domain.pojo.available_routes.DropOffDetail
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.edit_chart.request.EditChartRequest
import com.bitla.ts.domain.pojo.edit_chart.request.ReqBody
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_details.request.BPDPReqBody
import com.bitla.ts.domain.pojo.service_details.request.BpDpServiceDetailsRequest
import com.bitla.ts.domain.pojo.service_details_response.PassengerDetails
import com.bitla.ts.domain.pojo.service_details_response.SeatDetail
import com.bitla.ts.domain.pojo.service_details_response.StageDetail
import com.bitla.ts.presentation.adapter.CountryCodeAdapter
import com.bitla.ts.presentation.view.activity.InterBDActivity
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.activity.SelectBoardingDroppingPointActivity
import com.bitla.ts.presentation.viewModel.EditChartViewModel
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.availableSeats
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getAllCountries
import com.bitla.ts.utils.common.getAvailableSeats
import com.bitla.ts.utils.common.getBoardingList
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.common.getDroppingList
import com.bitla.ts.utils.common.setPassengerDetails
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_BOARDING_STAGE_DETAILS
import com.bitla.ts.utils.sharedPref.PREF_DROPPING_STAGE_DETAILS
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_SELECTED_AVAILABLE_ROUTES
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.sharedPref.SELECTED_BOARDING_DETAIL
import com.bitla.ts.utils.sharedPref.SELECTED_DROPPING_DETAIL
import com.bitla.ts.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import onChange
import org.koin.androidx.viewmodel.ext.android.viewModel
import setMaxLength
import timber.log.Timber
import toast
import visible
import java.io.Serializable


class EmptySeatsFragment : BaseFragment(), View.OnClickListener, DialogSingleButtonListener,
    DialogAnyClickListener {

    private var countrylist: List<Countries> = arrayListOf()
    private var countryPickerDialog: BottomSheetDialog? = null
    private var privilegePhoneValidationCount: Int = 0
    private var allowToEditPassengerDetailsInEditChart: Boolean = false
    private var name: String? = null
    private var phoneNo: String? = null
    private var title: String = "Mr"
    private var seatNo: String? = null
    private var seatFareWithoutSymbol: String = "0.0"
    private var isClickBoardingPoint: Boolean = false
    lateinit var binding: FragmentEmptySeatsBinding
    private var availableSeatList = mutableListOf<String>()

    private var loginModelPref: LoginModel = LoginModel()
    private var bccId: Int? = 0
    private var source: String? = ""
    private var sourceId: String = ""
    private var destination: String? = ""
    private var destinationId: String = ""
    private var travelDate: String = ""
    private var busType: String? = null
    private var depTime: String? = null
    private var resId: Long? = null

    private var boardingPoint: String = ""
    private var droppingPoint: String = ""
    private var boardingPointId: Int = 0
    private var droppingPointId: Int = 0
    private lateinit var droppingStageDetail: StageDetail
    private lateinit var boardingStageDetail: StageDetail
    private var currency = ""
    private var currencyFormat = ""
    private var boardingList: MutableList<StageDetail>? = null
    private var droppingList: MutableList<StageDetail>? = null
    private val editChartViewModel by viewModel<EditChartViewModel<Any?>>()
    private var locale: String? = ""
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var bpdp: Boolean = false
    private var privilegeResponseModel: PrivilegeResponseModel?= null

    override fun onResume() {
        super.onResume()

        if (isClickBoardingPoint) {
            if (PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS) != null) {
                droppingStageDetail =
                    PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)!!
                droppingPoint = droppingStageDetail.name!!
                droppingPointId = droppingStageDetail.id ?: 0
            }
            if (PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS) != null) {
                boardingStageDetail =
                    PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)!!
                boardingPoint = boardingStageDetail.name!!
                boardingPointId = boardingStageDetail.id ?: 0
            }
            Timber.d("onResume boardingPoint $boardingPoint droppingPoint $droppingPoint")
            if (boardingPoint.isNotEmpty() && droppingPoint.isNotEmpty()) {
                binding.acBoardingPoint.setText(boardingPoint)
                binding.acDroppingPoint.setText(droppingPoint)
            }

            watchFields()
            if ((activity as BaseActivity).getPrivilegeBase() != null) {
                val privilegeResponse = (activity as BaseActivity).getPrivilegeBase()
                privilegeResponse?.let {
                    privilegeResponse.let {
                        currency = privilegeResponse.currency
                        currencyFormat =
                            getCurrencyFormat(requireContext(), privilegeResponse.currencyFormat)
                    }
                    if (privilegeResponse.availableAppModes?.allowBpDpFare == true) {
                        bpdp = true
                        callBpDpServiceApi(boardingPointId.toString(), droppingPointId.toString())
                    } else {
                        bpdp = false
                        watchFields()
                    }
                }
            }


        }

        isClickBoardingPoint = false
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmptySeatsBinding.inflate(inflater, container, false)

        getPref()
        initUi()
        clickListener()
        setObserver()

        binding.etName.onChange {
            name = binding.etName.text.toString()
            watchFields()
        }
        Timber.d("maxlength:: $privilegePhoneValidationCount")
        if (privilegePhoneValidationCount != 0) {
            Timber.d("maxlength:1: $privilegePhoneValidationCount")
            binding.etPhoneNumber.setMaxLength(privilegePhoneValidationCount)
        }
        binding.etPhoneNumber.onChange {
            phoneNo = binding.etCountryCode.text.toString()+binding.etPhoneNumber.text.toString()
            watchFields()
        }

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
            editChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
        getPref()
        initUi()
        clickListener()
        setObserver()
    }

    override fun isNetworkOff() {
    }

    private fun clickListener() {
        binding.acBoardingPoint.setOnClickListener(this)
        binding.acDroppingPoint.setOnClickListener(this)
        binding.btnUpdateChart.setOnClickListener(this)
    }

    private fun callBpDpServiceApi(boarding: String, dropping: String) {
        val serviceDetailsRequest = BpDpServiceDetailsRequest(
            bccId.toString(), service_details_method, format_type,
            BPDPReqBody(
                id = resId.toString(),
                api_key = loginModelPref.api_key,
                operator_api_key = operator_api_key,
                locale = locale,
                origin_id = sourceId,
                destination_id = destinationId,
                json_format = "true",
                boarding_at = boarding,
                drop_off = dropping,
                remarks = ""
            )
        )

        if (requireActivity().isNetworkAvailable()) {
            sharedViewModel.getBpDpServiceDetails(
                reservationId = resId.toString(),
                apiKey = loginModelPref.api_key,
                origin = sourceId,
                destinationId = destinationId,
                operator_api_key = operator_api_key,
                locale = "$locale",
                apiType = service_details_method,
                boardingAt = boarding,
                dropOff = dropping,
            )
            observer()
            Timber.d("serviceDetailsRequest $serviceDetailsRequest")

        } else {
            requireActivity().noNetworkToast()
        }
    }

    private fun observer() {
        sharedViewModel.serviceDetails.observe(this, Observer {
            binding.includeProgress.progressBar.gone()
            Timber.d("responseBodyServiceDetails $it")

            if (it.code == 200) {

                boardingList = mutableListOf()
                droppingList = mutableListOf()

                val availableSeatList2 = mutableListOf<String>()
                val passengerList2 = mutableListOf<PassengerDetails>()
//                    stageDetails = it.body.stageDetails!!
                val seatDetails: List<SeatDetail>? = it?.body?.coachDetails?.seatDetails
                seatDetails?.forEach { it ->
                    if (it != null) {
                        if (it.available!! && it.isBlocked == false)
                            availableSeatList2.add(
                                "${it.number} ($currency${
                                    it.fare.toString().toDouble().convert(currencyFormat)
                                })"
                            )
                        if (it.passengerDetails != null) {
                            passengerList2.add(it.passengerDetails!!)
                        }
                    } else {
                        requireContext().toast(getString(R.string.server_error))
                    }
                }
                seatNo = ""
                binding.acSelectSeat.text.clear()
                availableSeats(availableSeats = availableSeatList2)
                setPassengerDetails(passengerDetails = passengerList2)
//                    for (i in 0..it.body.stageDetails?.size!!.minus(1)) {
//                        if (it?.body?.stageDetails!![i].cityId.toString() == sourceId) {
//                            boardingList.add(it?.body?.stageDetails!![i])
////                            generateBoardingList(i)
//                        } else {
////                            generateDroppingList(i)
//                        }
//                    }

                initUi()
//                    val availableSeatList = mutableListOf<String>()
//                    val passengerList = mutableListOf<PassengerDetails>()
            } else if (it.code == 401) {
                /*DialogUtils.unAuthorizedDialog(
                    requireContext(),
                    "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                    this
                )*/
                (activity as BaseActivity).showUnauthorisedDialog()

            } else
                it.message?.let { it1 -> requireContext().toast(it1) }
        })

    }

//    private fun generateBoardingList(i: Int) {
//        if (boardingList != null) {
//            boardingList?.add(stageDetails[i])
//            setBoardingList(boardings = boardingList!!)
//            Timber.d("boardingList ${boardingList?.size}")
//        }
//    }
//
//    private fun generateDroppingList(i: Int) {
//        if (droppingList != null) {
//            droppingList?.add(stageDetails[i])
//            setDroppingList(droppings = droppingList!!)
//            Timber.d("droppingList ${droppingList?.size}")
//        }
//
//    }


    private fun initUi() {
        /*if(allowToEditPassengerDetailsInEditChart == false){
            binding.btnUpdateChart.gone()
        } else {
            binding.btnUpdateChart.visible()
        }*/

        countrylist = getAllCountries(requireContext())
        availableSeatList = getAvailableSeats()
        Timber.d("editchartEdit: $availableSeatList")
        val seatLeft =
            "${availableSeatList.size} ${getString(R.string.seats)} ${getString(R.string.left)}"

        binding.tvTotalAvailableSeats.text = seatLeft
        binding.acSelectSeat.setAdapter(
            ArrayAdapter(
                requireActivity(),
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                availableSeatList
            )
        )

        binding.acTitle.setAdapter(
            ArrayAdapter(
                requireActivity(),
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                resources.getStringArray(R.array.genderTitleArray)
            )
        )


        binding.acSelectSeat.setOnItemClickListener { parent, view, position, id ->
            val seatPrice = "${getString(R.string.ticket_price_for_selected_seats_is)} ${
                availableSeatList[position].substringAfter("(").substringBefore(")")
            }"
            seatFareWithoutSymbol =
                availableSeatList[position].substringAfter(currency)
                    .substringBefore(")").trim()
            seatNo = availableSeatList[position].substringBefore("(").trim()
            binding.tvSeatPrice.text = seatPrice
            watchFields()
        }

        binding.acTitle.setOnItemClickListener { parent, view, position, id ->
            title = resources.getStringArray(R.array.genderTitleArray)[position].toString()

        }

        binding.etCountryCode.setOnClickListener{
            showCountryPickerBottomsheet()
        }
    }

    private fun showCountryPickerBottomsheet() {
        countryPickerDialog = BottomSheetDialog(requireContext(),R.style.DialogStyle)
        countryPickerDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        countryPickerDialog!!.setCancelable(true)

        val binding = BottomsheetCountryCodeBinding.inflate(LayoutInflater.from(context))
        countryPickerDialog!!.setContentView(binding.root)


        val adapter = CountryCodeAdapter(requireContext(),this,countrylist)
        binding.countryRV.adapter = adapter


        binding.searchIV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used in this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used in this example
            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString()
                adapter.filter(searchText)
            }
        })


        countryPickerDialog!!.show()
    }
    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
        source = PreferenceUtils.getSource()
        sourceId = PreferenceUtils.getSourceId()
        destination = PreferenceUtils.getDestination()
        destinationId = PreferenceUtils.getDestinationId()
        travelDate = PreferenceUtils.getTravelDate()
        Timber.d("reservationCheck:: $sourceId: $source")
        Timber.d("reservationCheck:: $destinationId: $destination")

        if (PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL) != null) {
            val oldBoarding =
                PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL)!!
            binding.acBoardingPoint.setText(oldBoarding.name)
            boardingPoint = oldBoarding.name
            if (!oldBoarding.id.isNullOrEmpty())
                boardingPointId = oldBoarding.id.toInt()


        }
        if (PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL) != null) {
            val oldDropping = PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL)!!
            binding.acDroppingPoint.setText(oldDropping.name)
            droppingPoint = oldDropping.name
            if (!oldDropping.id.isNullOrEmpty())
                droppingPointId = oldDropping.id.toInt()
        }


        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            busType = result?.bus_type ?: getString(R.string.empty)
            depTime = result?.dep_time ?: getString(R.string.empty)
            resId = (result?.reservation_id ?: 0L)

            Timber.d("rervationCheck:: $resId")

        }
        if ((activity as BaseActivity).getPrivilegeBase() != null) {
            privilegeResponseModel = (activity as BaseActivity).getPrivilegeBase()

            if (privilegeResponseModel?.phoneNumValidationCount != null) {
                privilegePhoneValidationCount = privilegeResponseModel?.phoneNumValidationCount?:10
            }
            if (privilegeResponseModel?.allowToEditPassengerDetailsInEditChart != null) {
                allowToEditPassengerDetailsInEditChart =
                    privilegeResponseModel?.allowToEditPassengerDetailsInEditChart?:false
            }
        }

        if (privilegeResponseModel != null) {

            privilegeResponseModel?.let {
                currency = it.currency
            }
        } else {
            requireActivity().toast(getString(R.string.server_error))
        }
    }

    private fun setObserver() {
        editChartViewModel.loadingState.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                else -> binding.includeProgress.progressBar.gone()
            }
        })


        editChartViewModel.editChart.observe(viewLifecycleOwner, Observer {
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                Timber.d("responseBodyEditChart $it")
                when (it.code) {
                    200 -> {
                        it.message.let { it1 -> requireContext().toast(it1) }
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

                    else -> it.message.let { it1 -> requireContext().toast(it1) }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        })

        editChartViewModel.validationData.observe(viewLifecycleOwner, Observer {
            Timber.d("validateFields $it")
            if (it.isNotEmpty()) {
                requireContext().toast(it)
            } else {
                if (requireContext().isNetworkAvailable())
                    callEditChartApi()
                else
                    requireContext().noNetworkToast()
            }
        })

        editChartViewModel.etOnChange.observe(viewLifecycleOwner, Observer {
            Timber.d("watchFields $it")
            if (!it) {
                binding.btnUpdateChart.setBackgroundResource(R.drawable.button_default_bg)
            } else {
                binding.btnUpdateChart.setBackgroundResource(R.drawable.button_selected_bg)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.acDroppingPoint -> {
                if (binding.acBoardingPoint.text.toString().isEmpty())
                    requireContext().toast(getString(R.string.please_select_boarding_point_first))
            }

            R.id.acBoardingPoint -> {
                if (!bpdp) {
                    if (binding.acBoardingPoint.text?.isNullOrEmpty() == true) {
                        PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
                    }
                    if (binding.acDroppingPoint.text?.isNullOrEmpty() == true) {
                        PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)
                    }
                    isClickBoardingPoint = true
                    val intent =
                        Intent(requireActivity(), SelectBoardingDroppingPointActivity::class.java)
                    intent.putExtra(getString(R.string.tag), getString(R.string.boarding))
                    intent.putExtra(getString(R.string.boarding), getBoardingList() as Serializable)
                    intent.putExtra(getString(R.string.dropping), getDroppingList() as Serializable)
                    intent.putExtra(getString(R.string.bus_type), busType)
                    intent.putExtra(getString(R.string.dep_time), depTime)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        getString(R.string.edit_chart)
                    )
                    startActivity(intent)
                } else {
                    Timber.d("BoardingPointListCheck: ${getBoardingList().size}")
                    isClickBoardingPoint = true
                    val intent =
                        Intent(requireActivity(), InterBDActivity::class.java)
                    intent.putExtra(getString(R.string.tag), getString(R.string.boarding))
                    intent.putExtra(getString(R.string.boarding), getBoardingList() as Serializable)
                    intent.putExtra(getString(R.string.dropping), getDroppingList() as Serializable)
                    intent.putExtra(getString(R.string.bus_type), busType)
                    intent.putExtra(getString(R.string.dep_time), depTime)
                    intent.putExtra(
                        getString(R.string.toolbar_title),
                        getString(R.string.edit_chart)
                    )
                    startActivity(intent)
                }
            }

            R.id.btnUpdateChart -> {
                name = binding.etName.text.toString()
                phoneNo = binding.etCountryCode.text.toString()+binding.etPhoneNumber.text.toString()
                validateFields()
            }
        }
    }

    private fun callEditChartApi() {
        val reqBody = ReqBody(
            adult_fare = seatFareWithoutSymbol,
            api_key = loginModelPref.api_key,
            boarding_at = boardingPointId.toString(),
            drop_off = droppingPointId.toString(),
            from = sourceId,
            name = name ?: getString(R.string.empty),
            phone_number = phoneNo ?: getString(R.string.empty),
            res_id = resId!!,
            seat_number = seatNo!!,
            title = title,
            to = destinationId,
            travel_date = travelDate,
            locale = locale
        )
        val editChartRequest = EditChartRequest(
            bcc_id = bccId.toString(),
            format = format_type,
            method_name = edit_chart_method_name,
            req_body = reqBody
        )



        editChartViewModel.editChartApi(
            editChartRequest = reqBody,
            apiType = edit_chart_method_name
        )

        Timber.d("editChartRequest $editChartRequest}")
    }

    private fun validateFields() {
        editChartViewModel.validation(
            seatNo = seatNo,
            name = name,
            phoneNo = phoneNo,
            phoneNoCount = privilegePhoneValidationCount,
            boardingPoint = boardingPoint,
            droppingPoint = droppingPoint,
            validateSeatNo = getString(R.string.validate_seat),
            validateName = getString(R.string.validate_name),
            validatePhoneNo = getString(R.string.validate_phone_no),
            validateBoardingPoint = getString(R.string.validate_boarding_point),
            validateDroppingPoint = getString(R.string.validate_drop_off)
        )
    }

    private fun watchFields() {
        editChartViewModel.etTextWatcher(
            seatNo = seatNo,
            name = name,
            phoneNo = phoneNo,
            phoneNoCount = privilegePhoneValidationCount,
            boardingPoint = boardingPoint,
            droppingPoint = droppingPoint
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

    override fun onAnyClickListener(type: Int, view: Any, position: Int) {
        if(type == 0){
            countryPickerDialog?.dismiss()
            val  selectedCountryCode = view as String
            binding.etCountryCode.setText(selectedCountryCode)

        }
    }

    override fun onAnyClickListenerWithExtraParam(
        type: Int,
        view: Any,
        list: Any,
        position: Int,
        outPos: Int
    ) {
        TODO("Not yet implemented")
    }
}