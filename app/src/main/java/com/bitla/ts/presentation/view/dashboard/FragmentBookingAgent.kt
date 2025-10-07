package com.bitla.ts.presentation.view.dashboard

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.activity.result.contract.*
import androidx.core.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.bitla.tscalender.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import toast
import visible
import java.text.*
import java.util.*


class FragmentBookingAgent : BaseFragment(), View.OnClickListener, SlyCalendarDialog.Callback {

    private val showOnlyAvailableServices: String = "true" //fixed
    private val showInJourneyServices: String = "true" // fixed

    private lateinit var binding: LayoutFragmentBookingAgentBinding
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private val availableRoutesViewModel by viewModel<AvailableRoutesViewModel<Any?>>()
    private val privilegeDetailsViewModel by sharedViewModel<PrivilegeDetailsViewModel>()
    private lateinit var busDetailsAgentAdapter: BusDetailsAgentAdapter
    private var dateList = mutableListOf<StageData>()
    private var availableRoutesList = mutableListOf<Result>()
    private var privilegeResponse: PrivilegeResponseModel? = null
    private var loginModelPref: LoginModel = LoginModel()
    private var sourceId: String? = "0"
    private var destinationId: String = "0" //0 Fixed for all dropping points
    private var source: String? = ""
    private var destination: String? = ""
    private var ymdDate: String = ""
    private var travelDate: String = ""
    private var locale: String? = ""
    private var convertedDate: String? = null
    private var sevenDaysDate: String = getTodayDate()
    private var isAllowBpDpFare = false
    private var cityStagingIdOnTrue: String? = null
    private var cityStagingNameOnTrue: String? = null

    private var pagination: Boolean = true
    private var perPage: Int = 10
    private var pageNumber: Int = 1
    private var totalPage: Int = 1
    private var bookingAfterDoj: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutFragmentBookingAgentBinding.inflate(inflater, container, false)
        getPref()
        initUi()
        onClickListener()
        setCalenderList()
        callAvailableRoutesForAgent()
        setObserver()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getPref()
    }

    private fun initUi() {
        ymdDate = getTodayDate()
        sevenDaysDate = getTodayDate()
        val originText = "${getString(R.string.from)}: $source"
        binding.tvFromOrigin.text = originText
        getScrollViewPosition()

        lifecycleScope.launch {
            availableRoutesViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    requireActivity().showToast(it)
                }
            }
        }
    }

    private fun getScrollViewPosition() {
        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1)
                        .measuredHeight - v.measuredHeight &&
                    scrollY > oldScrollY
                ) {
                    onNestedScrollView(true)
                }
            }
        })
    }

    private fun onNestedScrollView(isNestedScroll: Boolean) {
        pageNumber++
        if (pageNumber < totalPage.plus(1)) {
            binding.rvBusDetails.gone()
            if (isNestedScroll) {
                binding.paginationProgress.progressBar.visible()
                binding.includeProgress.progressBar.gone()
            }else
            {
                binding.paginationProgress.progressBar.gone()
                binding.includeProgress.progressBar.visible()
            }
            callAvailableRoutesForAgent()
        }
    }

    private fun getPref() {
        locale = PreferenceUtils.getlang()
 /*       sourceId = PreferenceUtils.getString(PREF_SOURCE_ID) ?: "0"
        source = PreferenceUtils.getSource()*/
        destination = PreferenceUtils.getDestination()
        travelDate = getTodayDate()
        loginModelPref = PreferenceUtils.getLogin()
        privilegeResponse = (activity as BaseActivity).getPrivilegeBase()

        isAllowBpDpFare = privilegeResponse?.availableAppModes?.allowBpDpFare ?: false

        if (privilegeResponse?.user_city != null) {
            sourceId = privilegeResponse?.user_city?.defaultStageId  ?: "0"
            source = privilegeResponse?.user_city?.defaultStageName
        }

        PreferenceUtils.putString(AGENT_SELECTED_SOURCE, source)
        PreferenceUtils.putString(AGENT_SELECTED_SOURCE_ID, sourceId)

        saveTravelDate()

        if (privilegeResponse?.bookingAfterDoj == null) {
            bookingAfterDoj = 0
        } else {
            bookingAfterDoj =
                if (privilegeResponse?.bookingAfterDoj?.trim()?.isEmpty() == true) {
                    0
                } else {
                    privilegeResponse?.bookingAfterDoj?.trim()?.toInt() ?: 0
                }
        }
    }

    private fun saveTravelDate() {
        if (travelDate.isNotEmpty())
            PreferenceUtils.setPreference(PREF_TRAVEL_DATE, travelDate)
    }

    private fun callAvailableRoutesForAgent() {
        if (pageNumber == 1) {
            binding.includeProgress.progressBar.visible()
            binding.rvBusDetails.gone()
        } else {
            if (availableRoutesList.isNotEmpty()) {
                binding.paginationProgress.progressBar.visible()
                binding.rvBusDetails.visible()
            }
        }
        binding.layoutNoData.gone()

        var isBima: Boolean? = null
        if (PreferenceUtils.getPreference("is_bima", false) == true) {
            isBima = true
        }
        var isCsShared: Boolean? = null
        if (isBima == true) {
            isCsShared = true
        }

        if (requireContext().isNetworkAvailable()) {
            availableRoutesViewModel.availableRoutesForAgent(
                loginModelPref.api_key,
                sourceId ?: "0",
                destinationId,
                showInJourneyServices,
                isCsShared ?: false,
                operator_api_key,
                format_type,
                ymdDate,
                showOnlyAvailableServices,
                locale ?: "",
                pagination.toString(),
                perPage.toString(),
                pageNumber.toString()
            )
        } else
            requireContext().noNetworkToast()
    }


    private fun setCalenderList() {
        pickUpChartViewModel.getNextCalenderDates(sevenDaysDate, ymdDate)
        ymdDate = if (travelDate.isNotEmpty()) {
            inputFormatToOutput(
                travelDate,
                DATE_FORMAT_D_M_Y,
                DATE_FORMAT_Y_M_D
            )
        } else {
            inputFormatToOutput(
                getTodayDate(),
                DATE_FORMAT_D_M_Y,
                DATE_FORMAT_Y_M_D
            )
        }
    }

    private fun onClickListener() {
        binding.layoutAllDroppingPoints.setOnClickListener(this)
        binding.swipeBusDetails.setOnRefreshListener {
            pageNumber = 1
            callAvailableRoutesForAgent()
            binding.swipeBusDetails.isRefreshing = false
        }
    }

    private fun setObserver() {
        pickUpChartViewModel.listOfDates.observe(viewLifecycleOwner) {
            dateList = it
            setDatesAdapter()
        }

        availableRoutesViewModel.dataAvailableRoutes.observe(viewLifecycleOwner) {
            binding.includeProgress.progressBar.gone()
            binding.paginationProgress.progressBar.gone()
            PreferenceUtils.removeKey(PREF_AVAILABLE_ROUTES_ITEM_IS_SERVICE_BLOCKED)
            if (it != null) {
                if (it.code == 200) {
                    totalPage = it.number_of_pages ?: 1
                    pageNumber = it.current_page ?: 1
                    if (pageNumber == 1)
                        availableRoutesList.clear()

                    if (it.result != null) {
                        it.result.forEach {
                            if (!it.is_service_blocked) {
                                if (pagination && pageNumber > 1)
                                    availableRoutesList.add(it)
                                else {
                                    availableRoutesList.add(it)
                                }
                            }
                        }
                    }
                    privilegeDetailsViewModel.setAvailableRoutesCounts(it.total_count ?: 0)
                    PreferenceUtils.putObject(it, PREF_AVAILABLE_ROUTES_RESPONSE)

                    if (availableRoutesList.isNullOrEmpty())
                        onNestedScrollView(false)
                    if (it.result.isNullOrEmpty() || (pageNumber > totalPage && availableRoutesList.isEmpty())) {
                        binding.rvBusDetails.gone()
                        binding.layoutNoData.visible()
                        if (!it.message.isNullOrEmpty())
                            binding.tvNoService.text = it.message
                        else
                            binding.tvNoService.text = getString(R.string.no_service_available)
                    } else {
                        setAvailableRoutes(availableRoutesList)
                        binding.rvBusDetails.visible()
                        binding.layoutNoData.gone()
                        setBusDetailsAdapter()
                        sevenDaysDate = travelDate
                        availableRoutesViewModel.getNextCalenderDates(sevenDaysDate, travelDate)
                    }

                } else if (it.code == 401) {
                    privilegeDetailsViewModel.setAvailableRoutesCounts(0)
                    availableRoutesList.clear()
                   /* DialogUtils.unAuthorizedDialog(
                        requireContext(),
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    (activity as BaseActivity).showUnauthorisedDialog()

                } else {
                    privilegeDetailsViewModel.setAvailableRoutesCounts(0)
                    availableRoutesList.clear()
                    binding.rvBusDetails.gone()
                    binding.layoutNoData.visible()
                    binding.tvNoService.text = it.message
                }
            } else {
                privilegeDetailsViewModel.setAvailableRoutesCounts(0)
                availableRoutesList.clear()
                requireContext().toast(getString(R.string.server_error))
            }

        }

        availableRoutesViewModel.bpDpDetails.observe(viewLifecycleOwner) {
            binding.includeProgress.progressBar.gone()

            if (it != null) {
                if (!it.boarding_point_details.isNullOrEmpty() && !it.drop_off_details.isNullOrEmpty()) {
                    PreferenceUtils.putObject(
                        it.boarding_point_details[0],
                        SELECTED_BOARDING_DETAIL
                    )
                    PreferenceUtils.putObject(it.drop_off_details[0], SELECTED_DROPPING_DETAIL)

                    PreferenceUtils.putObject(
                        it.boarding_point_details[0],
                        AGENT_SELECTED_BOARDING_DETAIL
                    )
                    PreferenceUtils.putObject(it.drop_off_details[0], AGENT_SELECTED_DROPPING_DETAIL)


                    val intent = Intent(requireContext(), NewCoachActivity::class.java)
                    intent.putExtra(IS_FROM_AGENT, true)
                    intent.putExtra(REDIRECT_FROM, BusDetailsActivity.TAG)
                    startActivity(intent)
                } else if (it.code == 401) {
                    /*DialogUtils.unAuthorizedDialog(
                        requireContext(),
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    (activity as BaseActivity).showUnauthorisedDialog()

                } else {
                    requireContext().toast(getString(R.string.something_went_wrong))
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }

        }
    }

    private fun setBusDetailsAdapter() {
        binding.rvBusDetails.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        busDetailsAgentAdapter =
            BusDetailsAgentAdapter(
                requireContext(),
                this,
                availableRoutesList,
                privilegeResponse!!
            )
        binding.rvBusDetails.adapter = busDetailsAgentAdapter
    }

    private fun setDatesAdapter() {
        binding.rvDateDetails.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDateDetails.adapter =
            MyBookingsDatesAdapter(
                context = requireContext().applicationContext,
                onItemClickListener = this,
                menuList = dateList,
                isShowCalendar = true
            )
    }

    override fun isInternetOnCallApisAndInitUI() {
        pageNumber = 1
        callAvailableRoutesForAgent()
    }

    override fun isNetworkOff() {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }

    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            when (view.tag) {
                getString(R.string.tag_book_seat) -> {
                    if (position < availableRoutesList.size) {

                        val isApplyBPDPFare = availableRoutesList[position].is_apply_bp_dp_fare
                        PreferenceUtils.putObject(isApplyBPDPFare, IS_APPLY_BP_DP_FARE)

                        PreferenceUtils.putObject(
                            availableRoutesList[position], PREF_SELECTED_AVAILABLE_ROUTES
                        )
                        if (destinationId == "0") {
                            val subtitle =
                                "${getDateDMMM(travelDate)} | ${availableRoutesList[position].dep_time} | ${availableRoutesList[position].bus_type}"
                            val intent = Intent(
                                activity,
                                InterCityAgentActivity::class.java
                            )
                            intent.putExtra(
                                getString(R.string.res_id),
                                availableRoutesList[position].reservation_id
                            )
                            intent.putExtra(
                                getString(R.string.from_city),
                                binding.tvFromOrigin.text.toString()
                            )
                            intent.putExtra(
                                getString(R.string.nav_header_subtitle),
                                subtitle
                            )
                            PreferenceUtils.setPreference(
                                PREF_RESERVATION_ID,
                                availableRoutesList[position].reservation_id
                            )
                            startActivity(intent)
                        } else {
                            callBpDpDetails(availableRoutesList[position].reservation_id.toInt())
                        }
                    }
                }

                getString(R.string.open_calender) -> {
                        var minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
                        if (privilegeResponse?.isAllowBookingAfterTravelDate ==  true) {
                            val calendar: Calendar = Calendar.getInstance()
                            calendar.add(Calendar.DATE, -1 * bookingAfterDoj)
                            minDate = stringToDate(
                                inputFormatToOutput(
                                    calendar.time.toString(),
                                    DATE_FORMAT_EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY,
                                    DATE_FORMAT_D_M_Y
                                ), DATE_FORMAT_D_M_Y
                            )
                        }


                    SlyCalendarDialog()
                        .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                        .setMinDate(minDate)
                        .setSingle(true)
                        .setFirstMonday(false)
                        .setCallback(this)
                        .show(requireFragmentManager(), TAG)
                }

                getString(R.string.dates) -> {
                    if (position < dateList.size)
                        ymdDate = inputFormatToOutput(
                            dateList[position].title,
                            DATE_FORMAT_MMM_DD_EEE_YYYY,
                            DATE_FORMAT_Y_M_D
                        ).replace("1970", getCurrentYear())
                    travelDate = getDateDMY(ymdDate) ?: ""
                    saveTravelDate()
                    pageNumber = 1
                    callAvailableRoutesForAgent()
                }

                else -> {
                    if (position < dateList.size)
                        ymdDate = inputFormatToOutput(
                            dateList[position].title,
                            DATE_FORMAT_MMM_DD_EEE_YYYY,
                            DATE_FORMAT_Y_M_D
                        ).replace("1970", getCurrentYear())
                }
            }

        }
    }

    private fun callBpDpDetails(reservationId: Int) {
        if (requireContext().isNetworkAvailable()) {
            binding.layoutNoData.gone()
            binding.includeProgress.progressBar.visible()
            availableRoutesViewModel.getBpDpDetails(
                loginModelPref.api_key,
                sourceId ?: "0",
                destinationId,
                reservationId.toString()
            )
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.layoutAllDroppingPoints -> {
                val intent = Intent(
                    activity,
                    InterCityAgentActivity::class.java
                )
                intent.putExtra(AGENT_FROM_ALL_DROPPING_POINTS, true)
                getResult.launch(intent)
            }
        }
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val agentFromAllDroppingPoints =
                    it.data?.getBooleanExtra(AGENT_FROM_ALL_DROPPING_POINTS, false)
                if (agentFromAllDroppingPoints == true) {
                    destination = PreferenceUtils.getDestination()
                    destinationId = PreferenceUtils.getDestinationId()
                    binding.tvAllDroppingPoints.text = destination
                } else {
                    binding.tvAllDroppingPoints.text = getString(R.string.all_dropping_points)
                    destinationId = "0"
                }
                pageNumber = 1
                callAvailableRoutesForAgent()
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
        if (firstDate != null) {
            if (secondDate == null) {
                firstDate.set(Calendar.HOUR_OF_DAY, hours)
                firstDate.set(Calendar.MINUTE, minutes)
                travelDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(firstDate.time)
                ymdDate = travelDate

                convertedDate = SimpleDateFormat(
                    DATE_FORMAT_Y_M_D,
                    Locale.getDefault()
                ).format(firstDate.time)
                sevenDaysDate = travelDate
                pickUpChartViewModel.getNextCalenderDates(sevenDaysDate, travelDate)
                saveTravelDate()
                setCalenderList()
                pageNumber = 1
                callAvailableRoutesForAgent()
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }


    companion object {
        val TAG: String? = FragmentBookingAgent::class.simpleName
    }
}