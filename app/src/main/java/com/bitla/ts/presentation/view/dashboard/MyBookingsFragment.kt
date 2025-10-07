package com.bitla.ts.presentation.view.dashboard

import org.koin.androidx.viewmodel.ext.android.viewModel as viewModel1
import android.annotation.*
import android.content.*
import android.os.*
import android.view.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.my_bookings.response.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.ticket_details_compose.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getLogin
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getPrivilege
import com.bitla.tscalender.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import timber.log.*
import toast
import visible
import java.text.*
import java.util.*


class MyBookingsFragment : SlyCalendarDialog.Callback, BaseUpdateCancelTicket(),
    View.OnClickListener,
    OnItemClickListener,
    DialogSingleButtonListener, DialogButtonListener, OnPnrListener {

//    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private var sevenDaysDate: String = getTodayDate()
    private lateinit var binding: LayoutMybookingsFragmentBinding
    private lateinit var myBookingsAdapter: MyBookingsAdapter
    var travelDate: String = ""
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var cancelTicketSheet: CancelTicketSheet
    private lateinit var editPassengerSheet: EditPassengerSheet


    private var ymdDate: String = ""
    private var oldPosition: Int = 0
    private lateinit var mcalendar: Calendar
    private val myBookingsViewModel by viewModel1<MyBookingsViewModel<Any>>()
    var dateList = mutableListOf<StageData>()
    var myBookingsList = mutableListOf<Data>()
    var myBookingsListCpy = mutableListOf<Data>()
    var myBookingsFilterList = mutableListOf<Filter>()
    private val ticketDetailsViewModel by viewModel1<TicketDetailsViewModel<Any?>>()
    private var filterType = "None"
    private var initFilterDialog = true
    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String? = ""
    private var created = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = LayoutMybookingsFragmentBinding.inflate(inflater, container, false)
        Timber.d(":fragmentVisibility: true")
        created = true
        getPref()
        setDateLocale(PreferenceUtils.getlang(), requireContext())
        locale = PreferenceUtils.getlang()
        loginModelPref = getLogin()

        cancelTicketSheet =
            childFragmentManager.findFragmentById(R.id.layoutCancelTicketSheet) as CancelTicketSheet

        editPassengerSheet =
            childFragmentManager.findFragmentById(R.id.layoutEditPassengerSheet) as EditPassengerSheet





        edgeToEdgeFabButton(requireActivity(),binding.btnFilter,65)



        firebaseLogEvent(
            context = requireContext(),
            logEventName = MY_BOOKINGS,
            loginId = loginModelPref.userName,
            operatorName = loginModelPref.travels_name,
            roleName = loginModelPref.role,
            eventKey = MY_BOOKINGS,
            eventValue = "My Bookings"
        )
        lifecycleScope.launch {
            myBookingsViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        return binding.root
    }

    private fun getPref() {
        /*lifecycleScope.launch {
            if (fetchPrivilege() != null)
                privilegeResponseModel = fetchPrivilege()
        }*/
//        privilegeResponseModel = (activity as BaseActivity).getPrivilegeBase()

        lifecycleScope.launch {
            val privilege = (activity as BaseActivity).getPrivilegeBaseSafely()
            myBookingsViewModel.updatePrivileges(privilege)
        }
    }




    @Override
    override fun setMenuVisibility(visible: Boolean) {
        super.setMenuVisibility(visible)
        if (created) {
            created = false
            if (visible) {
                if (requireContext().isNetworkAvailable()) {
                    //lifecycleScope.launch(Dispatchers.IO){
                    callMyBookingsApi()

                    //}
                    getMyBookingsApi()
                    getDates()
                    swipeRefreshLayout()
                }
                Timber.d(":fragmentVisibility: falseeee")
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d(":fragmentVisibility: frue")

        mcalendar = Calendar.getInstance()
        travelDate = getTodayDate()
        ymdDate = SimpleDateFormat("yyyy-MM-dd")
            .format(SimpleDateFormat("dd-MM-yyyy").parse(travelDate))

        binding.tvDate.text = getDateMMMM(ymdDate)
        filterType = getString(R.string.none)

        getDates()
        onClickListener()
    }

    private fun onClickListener() {
        binding.btnFilter.setOnClickListener(this)
    }

    private fun getDates() {
        if (isAttachedToActivity()) {
            // get next seven days date with current date
            myBookingsViewModel.getNextCalenderDates(sevenDaysDate, travelDate)

            myBookingsViewModel.listOfDates.observe(requireActivity(), Observer {
                dateList = it
                if (isAttachedToActivity())
                    setDatesAdapterMethod()
            })
        }
    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    private fun swipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            startShimmerEffect()
            callMyBookingsApi()
            getMyBookingsApi()
        }
    }

    private fun setDatesAdapterMethod() {
        if (dateList.isNotEmpty()) {
            dateList.forEach { it.isSelected = false }
            dateList[oldPosition].isSelected = true
        }
        binding.rvDates.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDates.adapter =
            MyBookingsDatesAdapter(
                context = requireActivity(),
                onItemClickListener = this,
                menuList = dateList,
                isShowCalendar = true
            )
    }


    private fun getMyBookingsApi() {

        myBookingsViewModel.loadingState.observe(requireActivity()) {
            Timber.d("LoadingState ${it.status}")
            when (it) {
                LoadingState.LOADING -> startShimmerEffect()
            }
        }

        myBookingsViewModel.dataMyBookings.observe(requireActivity()) {
            if (it != null) {
                stopShimmerEffect()
                binding.swipeRefreshLayout.isRefreshing = false

                when (it.code) {
                    200 -> {
                        if (it.result == null) {
                            binding.myBookingNoSearchResult.visible()
                            binding.swipeRefreshLayout.isRefreshing = false
                            stopShimmerEffect()

                        } else {

                            myBookingsList = it.result.data as MutableList<Data>
                            myBookingsListCpy = it.result.data
                            if (it.result.filter != null && it.result.filter.isNotEmpty())
                                binding.btnFilter.visible()
                            else
                                binding.btnFilter.gone()


                            binding.rvBookings.visible()
                            if (isAttachedToActivity())
                                setMyBookingsAdapter()
                            setDatesAdapter()
                            if (isAttachedToActivity())
                                populateRvBookingByFilterType(filterType)
                            //binding.myBookingNoSearchResult.gone()
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
                        binding.rvBookings.gone()
                        binding.btnFilter.gone()
                        binding.myBookingNoSearchResult.visible()
                        try {
                            if (it.message != null) {
                                binding.tvNoData.text = it.message.toString()
                            } else {
                                binding.tvNoData.text =
                                    requireContext().getString(R.string.no_bookings_yet)
                            }
                        } catch (e: Exception) {
//                              handle
                        }
                    }
                }

            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }


    private fun setDatesAdapter() {
//        getDatessub()
        getDates()
//        val stageData = StageData()
//        stageData.layoutType = "test"
//        searchList.add(stageData)
//
//        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
//        binding.rvDates.layoutManager = layoutManager
//        nextDateAdapter =
//            NextDateAdapter(requireActivity(), this, searchList)
//        binding.rvDates.adapter = nextDateAdapter
    }

    private fun getDatessub() {
        myBookingsViewModel.listOfDates.observe(requireActivity(), Observer {
            dateList = it as ArrayList<StageData>
            binding.rvDates.layoutManager =
                LinearLayoutManager(
                    activity?.applicationContext!!,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            binding.rvDates.adapter =
                DatesOnlyAdapter(requireContext(), this, it)
        })
    }


    private fun callMyBookingsApi() {
        if (requireContext().isNetworkAvailable()) {
            binding.myBookingNoSearchResult.gone()
            
            myBookingsViewModel.myBookingsApi(
                loginModelPref.api_key,
                "hash", ymdDate, ymdDate, locale!!,
                tickets_booked_by_you_method_name
            )
        } else requireContext().noNetworkToast()
    }

    private fun setMyBookingsAdapter() {
        myBookingsViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.rvBookings.layoutManager = layoutManager
            myBookingsAdapter =
                MyBookingsAdapter(
                    context = requireActivity(),
                    onItemClickListener = this,
                    onPnrListener = this,
                    dataList = myBookingsListCpy,
                    privilegeResponseModel = privilegeResponse
                )
            binding.rvBookings.adapter = myBookingsAdapter
        }
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            if (view.tag == getString(R.string.edit)) {
                updatePassengersDialog(position)
            }
            if (view.tag == resources.getString(R.string.open_calender)) {
                
                SlyCalendarDialog()
                    .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setMinDate(stringToDate("1970-01-01", DATE_FORMAT_D_M_Y))
                    .setSingle(true)
                    .setFirstMonday(false)
                    .setCallback(this)
                    .show(requireFragmentManager(), view.tag.toString())

            }
            if (view.tag == "DATES") {
                Timber.d("Position : $position")
                oldPosition = position
                ymdDate = inputFormatToOutput(
                    dateList[position].title,
                    DATE_FORMAT_MMM_DD_EEE_YYYY,
                    DATE_FORMAT_Y_M_D
                ).replace("1970", getCurrentYear())
                travelDate = getDateDMY(ymdDate)!!
                binding.tvDate.text = getDateMMMM(ymdDate)

//                Timber.d("hhh travelDate ${travelDate} == ${ymdDate} == ${dateList[position].title}")
                callMyBookingsApi()
            }
        }
    }

    override fun onClickOfItem(data: String, position: Int) {
    }

    override fun onMenuItemClick(
        itemPosition: Int,
        menuPosition: Int,
        busData: Result,
    ) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnFilter -> {

                myBookingsViewModel.dataMyBookings.observe(requireActivity()) {
                    if (it != null) {
                        when (it.code) {
                            200 -> {
                                myBookingsFilterList = it.result.filter
                            }

                            401 -> {
                                /*DialogUtils.unAuthorizedDialog(
                                    requireContext(),
                                    "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                    this
                                )*/
                                (activity as BaseActivity).showUnauthorisedDialog()

                            }
                        }
                    } else {
                        requireContext().toast(getString(R.string.server_error))
                    }

                }

                DialogUtils.filterDialog(
                    context = requireActivity(),
                    searchList = myBookingsFilterList,
                    title = getString(R.string.filter_by),
                    btnText = getString(R.string.apply_filter),
                    initDialog = initFilterDialog,
                    isCancelBtnVisible = true,
                    dialogSingleButtonListener = this,
                    dialogSingleButtonListenerCancel = this
                )
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
        filterType = str
        initFilterDialog = false

        firebaseLogEvent(
            context = requireContext(),
            logEventName = FILTER_SELECTION,
            loginId = loginModelPref.userName,
            operatorName = loginModelPref.travels_name,
            roleName = loginModelPref.role,
            eventKey = FILTER_SELECTION,
            eventValue = filterType
        )

        when (str) {
            getString(R.string.none) -> {
                callMyBookingsApi()
            }

            getString(R.string.cancel) -> {
                if (myBookingsFilterList.isNotEmpty())
                    myBookingsFilterList.removeAt(0)
            }

            getString(R.string.unauthorized) -> {
                //clearAndSave(requireContext())
                PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()

            }
            else -> {
                if (myBookingsFilterList.isNotEmpty())
                    myBookingsFilterList.removeAt(0)
                myBookingsListCpy =
                    myBookingsList.filter { it.ticketStatus == str } as MutableList<Data>

                if (myBookingsListCpy.isEmpty()) {
                    binding.myBookingNoSearchResult.visible()
                    binding.rvBookings.gone()

                } else {
                    myBookingsViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
                        binding.myBookingNoSearchResult.gone()
                        binding.rvBookings.visible()
                        layoutManager = LinearLayoutManager(
                            /* context = */ activity,
                            /* orientation = */ LinearLayoutManager.VERTICAL,
                            /* reverseLayout = */ false
                        )
                        binding.rvBookings.layoutManager = layoutManager
                        myBookingsAdapter =
                            MyBookingsAdapter(
                                context = requireActivity(),
                                onItemClickListener = this,
                                onPnrListener = this,
                                dataList = myBookingsListCpy,
                                privilegeResponseModel = privilegeResponse
                            )
                        binding.rvBookings.adapter = myBookingsAdapter
                    }

                }
            }
        }
    }


    override fun onLeftButtonClick() {

    }

    override fun onRightButtonClick() {

    }

    override fun onCancelled() {

    }

    override fun onDataSelected(
        firstDate: Calendar?,
        secondDate: Calendar?,
        hours: Int,
        minutes: Int,
    ) {
        try {
            val dmyDate = SimpleDateFormat(
                DATE_FORMAT_D_M_Y,
                Locale.getDefault()
            ).format(firstDate?.time)
            sevenDaysDate = dmyDate
            myBookingsViewModel.getNextCalenderDates(sevenDaysDate, travelDate)
            if (isAttachedToActivity())
                setDatesAdapterMethod()

            val fromDate = SimpleDateFormat(
                DATE_FORMAT_Y_M_D,
                Locale.getDefault()
            ).format(firstDate?.time)

            ymdDate = fromDate
            travelDate = getDateDMY(ymdDate)!!
            binding.tvDate.text = getDateMMMM(ymdDate)
            callMyBookingsApi()

        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
            //lifecycleScope.launch(Dispatchers.IO){
            callMyBookingsApi()

            //}
        }
    }

    /*
    * this method to used for start Shimmer Effect
    * */
    private fun startShimmerEffect() {
        binding.shimmerMyBooking.visible()
        binding.myBookingContainer.gone()
        binding.myBookingNoSearchResult.gone()
        binding.shimmerMyBooking.startShimmer()
    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
        binding.shimmerMyBooking.gone()
        binding.myBookingContainer.visible()
        if (binding.shimmerMyBooking.isShimmerStarted) {
            binding.shimmerMyBooking.stopShimmer()
        }
    }

    private fun populateRvBookingByFilterType(str: String) {
        when (str) {
            getString(R.string.none) -> {
                //callMyBookingsApi()
            }

            getString(R.string.cancel) -> {
                //myBookingsFilterList.removeAt(0)
            }

            getString(R.string.unauthorized) -> {
                //clearAndSave(requireContext())
                PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()

            }

            else -> {

                //myBookingsFilterList.removeAt(0)
                myBookingsListCpy =
                    myBookingsList.filter { it.ticketStatus == str } as MutableList<Data>

                if (myBookingsListCpy.isEmpty()) {
                    binding.myBookingNoSearchResult.visible()
                    binding.rvBookings.gone()
                } else {
                    myBookingsViewModel.privilegesLiveData.observe(requireActivity()) { privilegeResponse ->
                        binding.myBookingNoSearchResult.gone()
                        binding.rvBookings.visible()
                        layoutManager = LinearLayoutManager(
                            /* context = */ activity,
                            /* orientation = */ LinearLayoutManager.VERTICAL,
                            /* reverseLayout = */ false)
                        binding.rvBookings.layoutManager = layoutManager
                        myBookingsAdapter =
                            MyBookingsAdapter(
                                context = requireActivity(),
                                onItemClickListener = this,
                                onPnrListener = this,
                                dataList = myBookingsListCpy,
                                privilegeResponseModel = privilegeResponse
                            )
                        binding.rvBookings.adapter = myBookingsAdapter
                    }
                }
            }
        }
    }

    override fun onPnrSelection(tag: String, pnr: Any, doj: Any?) {
        when (tag) {
            getString(R.string.edit_passenger_details) -> {
                editPassengerSheet.showEditPassengersSheet(pnr)

                firebaseLogEvent(
                    context = requireContext(),
                    logEventName = EDIT_PASSENGER_DETAILS,
                    loginId = loginModelPref.userName,
                    operatorName = loginModelPref.travels_name,
                    roleName = loginModelPref.role,
                    eventKey = EDIT_PASSENGER_DETAILS,
                    eventValue = "Edit Passenger Details - MyBooking"
                )
            }

            getString(R.string.view_ticket) -> {
//                val intent = if(privilegeResponseModel?.country.equals("India", true) || privilegeResponseModel?.country.equals("Indonesia", true)) {
//                    Intent(requireContext(), TicketDetailsActivityCompose::class.java)
//                } else {
//                    Intent(requireContext(), TicketDetailsActivity::class.java)
//                }
                val intent=Intent(requireContext(), TicketDetailsActivityCompose::class.java)
                intent.putExtra(getString(R.string.TICKET_NUMBER), pnr.toString())
                intent.putExtra("returnToDashboard", false)
                startActivity(intent)

                firebaseLogEvent(
                    context = requireContext(),
                    logEventName = VIEW_BOOKING,
                    loginId = loginModelPref.userName,
                    operatorName = loginModelPref.travels_name,
                    roleName = loginModelPref.role,
                    eventKey = VIEW_BOOKING,
                    eventValue = "View Booking - MyBooking"
                )
            }

            else -> {

                cancelTicketSheet.showTicketCancellationSheet(pnr)

                firebaseLogEvent(
                    context = requireContext(),
                    logEventName = CANCEL_TICKET,
                    loginId = loginModelPref.userName,
                    operatorName = loginModelPref.travels_name,
                    roleName = loginModelPref.role,
                    eventKey = CANCEL_TICKET,
                    eventValue = "Cancel Ticket - MyBooking"
                )
            }
        }
    }
}




