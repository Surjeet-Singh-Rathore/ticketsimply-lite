package com.bitla.ts.presentation.view.dashboard

import android.Manifest
import android.annotation.*
import android.content.*
import android.content.pm.*
import android.content.res.*
import android.graphics.*
import android.location.*
import android.location.Location
import android.net.*
import android.os.*
import android.text.*
import android.view.*
import android.widget.ArrayAdapter
import androidx.activity.*
import androidx.annotation.*
import androidx.core.app.*
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.drawerlayout.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.*
import androidx.navigation.fragment.*
import androidx.navigation.ui.*
import androidx.recyclerview.widget.*
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.app.base.TsApplication.Companion.getAppContext
import com.bitla.ts.data.*
import com.bitla.ts.data.db.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.CreditInfoResponse
import com.bitla.ts.domain.pojo.dashboard_model.*
import com.bitla.ts.domain.pojo.dynamic_domain.DynamicDomain
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.remote_config.*
import com.bitla.ts.domain.pojo.user.*
import com.bitla.ts.koin.appModule.ApiModule
import com.bitla.ts.koin.appModule.RepositoryModule
import com.bitla.ts.koin.appModule.ViewModelModule
import com.bitla.ts.koin.networkModule.NetworkModule
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.activity.notifications.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.constants.AccountDetailHamburgerMenu.ACCOUNT_DETAILS_HAMBURGER_MENU
import com.bitla.ts.utils.constants.AppLogout.LOG_OUT
import com.bitla.ts.utils.constants.COUNTRY_CODE
import com.bitla.ts.utils.constants.IS_PINELAB_DEVICE
import com.bitla.ts.utils.constants.LOGIN_ID
import com.bitla.ts.utils.constants.MAIN_MENU
import com.bitla.ts.utils.constants.MainMenu.HAMBURGER_MENU_CLICK
import com.bitla.ts.utils.constants.OCC_CALENDAR
import com.bitla.ts.utils.constants.OPERATOR_NAME
import com.bitla.ts.utils.constants.OccCalendar
import com.bitla.ts.utils.constants.ROLE_NAME
import com.bitla.ts.utils.constants.SETTING.SETTINGS_CLICK
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.security.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getUpdatedApiUrlAddress
import com.bitla.ts.utils.sharedPref.PreferenceUtils.setUpdatedApiUrlAddress
import com.bumptech.glide.*
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.*
import com.google.android.material.bottomnavigation.*
import com.google.android.material.bottomsheet.*
import com.google.android.play.core.appupdate.*
import com.google.android.play.core.install.*
import com.google.android.play.core.install.model.*
import com.google.firebase.messaging.*
import com.google.gson.*
import com.kizitonwose.calendarview.model.*
import com.kizitonwose.calendarview.ui.*
import com.kizitonwose.calendarview.utils.*
import dagger.hilt.android.*
import daysOfWeekFromLocale
import gone
import invisible
import io.sentry.*
import isNetworkAvailable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import noNetworkToast
import onChange
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.ext.android.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import setSafeOnClickListener
import setTextColorRes
import toLoginModel
import toUserModel
import toast
import visible
import java.io.*
import java.text.*
import java.time.*
import java.time.format.*
import java.util.*

@AndroidEntryPoint
class DashboardNavigateActivity : BaseActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    RemoteConfigUpdateHelper.onUpdateCheckClickListener,
    RemoteConfigUpdateHelper.OnLocationApiIntervalFetchListener {

    companion object {
        val TAG: String = DashboardNavigateActivity::class.java.simpleName
        private const val IN_APP_UPDATE_REQUEST_CODE = 1991
        private const val LAUNCH_MAIN_ACTIVITY_CODE = 1000
    }

    private  var lastCounterUserName: String = ""
    private  var lastCounterPassword: String = ""
    private  var lastCounterDomain: String = ""
    private var gpsCityName: String = ""
    private val LOCATION_PERMISSION_REQUEST_CODE = 122
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isNewBookingFromTicketDetails: Boolean = false
    private var endYYMMDD: String? = null
    private var privilegeResponse: PrivilegeResponseModel? = null
    private var updateDetailsList: UpdateCountryListData? = null
    private var allowToViewTheCoachDocument: Boolean = false
    private var agentInstantRecharge: Boolean = false
    private var inAppUpdateTs: Boolean = false
    private var currentCountry: String = ""


    private var isCheckingInspectorDetail: Boolean = false
    private var navHostFragment: NavHostFragment? = null
    private var fcmToken: String? = null
    private var isReLoginClick: Boolean = false
    private val privilegeDetailsViewModel by viewModel<PrivilegeDetailsViewModel>()

    //    lateinit var binding: ActivityDashboardNavigateBinding
    val binding by lazy { ActivityDashboardNavigateBinding.inflate(layoutInflater) }
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var menuList: MutableList<NavMenuModel>
    private lateinit var navigationMenuAdapter: NavigationMenuAdapter
    private var currentUser: LoginModel = LoginModel()
    private val dashboardViewModel by viewModel<DashboardViewModel<Any>>()
    private val userViewModel: UserViewModel by viewModels()
    private var domain: String = ""
    private var username: String = ""
    private var password: String = ""
    private var onChangeUsername: String = ""
    private var onChangePassword: String = ""
    private var onChangeOtp: String = ""
    private var isOtpBasedLogin: Boolean = false
    private var back = true
    private var showManageAgentAccountLinkInAccount: Boolean = false
    private var manageBranchAccounting: Boolean = false

    private var androidUniqueID: String = ""
    private var locale: String? = ""
    private var calenderOpen: Boolean = false
    private var globalSelectedDate = ""
    private var globalSelectEndMonth: YearMonth? = null
    private var occupancyCalendarList =
        arrayListOf<com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.response.Result>()

    private var updateAppDescription = ""
    private var title = ""
    private var isCritical = false
    private var appPackageName = ""
    private var isMannualUpdatePop: Boolean = false
    private lateinit var bottomSheetDialoge: BottomSheetDialog

    private lateinit var installStateUpdatedListener: InstallStateUpdatedListener
    private var updateType: Int = AppUpdateType.IMMEDIATE
    private var appUpdateManager: AppUpdateManager? = null
    private var userList: List<User> = listOf()
    private var selectedShiftId: Int? = null
    private var selectedCounterId: Int? = null
    private var allowToViewVehicleDocBO: Boolean = false

    //     val cityName = MutableLiveData<String>("")
    val cityName = MutableSharedFlow<String>()
    fun showHideBottomBar(bool: Boolean) {
        if (bool) {
            binding.bottomNavView.visible()
        } else {
            binding.bottomNavView.gone()

        }
    }

    fun reduceMarginTop() {
        val containerMain = binding.containerMain
        if (containerMain != null) {
            val layoutParams = containerMain.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin =
                resources.getDimensionPixelSize(R.dimen.dp_40) // or set any px value
            containerMain.layoutParams = layoutParams
        }

    }

    fun increaseMarginTop() {
        val containerMain = binding.containerMain
        if (containerMain != null) {
            val layoutParams = binding.containerMain.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin =
                resources.getDimensionPixelSize(R.dimen.dp_56) // or set any px value
            binding.containerMain.layoutParams = layoutParams
        }

    }

    fun isCheckingInspectorDetailFrag(bool: Boolean) {
        isCheckingInspectorDetail = bool
    }

    fun lastUpdatedOn(text: String) {
        binding.appBar.lastUpdateTV.text = getString(R.string.data_as_on) + " " + text
        binding.appBar.lastUpdateTV.visible()
    }

    private fun setBottomBarOptions() {
        binding.bottomNavView.visible()
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment!!.navController
        binding.bottomNavView.setupWithNavController(navController)

        binding.bottomNavView.setOnItemSelectedListener(this)
        binding.bottomNavView.setOnItemReselectedListener {
            //avoid fragment re-created on bottom navigation view item selected
        }
        //drawer
        binding.appBar.layoutPnr.setOnClickListener(this)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashboard_fragment,
                R.id.dashboard_fragment_tabs,
                R.id.bookings_fragment,
                R.id.pickup_fragment,
                R.id.report_fragment,
                R.id.chk_inspector_fragment,
            ), binding.drawerLayout
        )

        //menu item click handle
        binding.navView.setupWithNavController(navController)

        //
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun isInternetOnCallApisAndInitUI() {
    }


    private fun callOccupancyCalendarApi(startDate: String, endDate: String) {
        binding.openCalender.progressBarCalendar.visible()


        if (isNetworkAvailable()) {

            val req = com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.request.ReqBody(
                apiKey = currentUser.api_key,
                reservationId = -1,
                startDate = startDate,
                endDate = endDate
            )

            dashboardViewModel.occupancyCalendarApi(
                req,
                currentUser.api_key
            )

        } else {
            binding.openCalender.progressBarCalendar.visible()
        }
    }


    private fun setOccupancyCalendarObserver() {
        dashboardViewModel.occupancyCalendarViewModel.observe((this)) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        binding.navigationPB.gone()
                        occupancyCalendarList.clear()
                        for (i in 0 until it.result.size) {
                            occupancyCalendarList.add(
                                com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.response.Result(
                                    it.result[i].day,
                                    it.result[i].occupancy.toDouble().toString(),
                                )
                            )
                        }
                        if (!calenderOpen) {
                            calenderOpen = true
                            binding.openCalender.root.visible()
                            calenderData(occupancyCalendarList)
                        } else {
                            calenderData(occupancyCalendarList)
                        }

                        binding.openCalender.progressBarCalendar.gone()

                    }

                    else -> {
                        toast("$it")
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun calenderData(
        occupancyCalendarList: MutableList<com.bitla.ts.phase2.dashboard_pojo.occupancyCalendarModel.response.Result>,
    ) {
        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth: YearMonth
        var selectedDate: LocalDate? = null

        val selectedDates = mutableSetOf<LocalDate>()
        val today = LocalDate.now()
        val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
        currentMonth = if (globalSelectedDate == "") {
            YearMonth.now()
        } else {
            val date = globalSelectedDate.split("-")
            val temp = "${date[0]}-${date[1]}"
            YearMonth.parse(temp)
        }
        val startMonth = currentMonth.minusMonths(3)
        val endMonth = currentMonth.plusMonths(3)

        if (globalSelectEndMonth == null) {
            globalSelectEndMonth = endMonth
        }

        binding.openCalender.calenderLayout.calendarView.setup(
            startMonth, globalSelectEndMonth!!, daysOfWeek.first()
        )
        binding.openCalender.calenderLayout.calendarView.scrollToMonth(currentMonth)
        class DayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay
            val textView = ChildOccupancyCalendarBinding.bind(view).tvDay
            val occContainer = ChildOccupancyCalendarBinding.bind(view).container
            val tvOccupancy = ChildOccupancyCalendarBinding.bind(view).tvOccupancy

            init {

                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDates.contains(day.date)) {
                            selectedDates.remove(day.date)
                        } else {
                            selectedDates.add(day.date)
                        }
                        binding.openCalender.calenderLayout.calendarView.notifyDayChanged(day)
                    }
                }
            }
        }

        binding.openCalender.calenderLayout.calendarView.dayBinder =
            object : DayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)

                @SuppressLint("SetTextI18n")
                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    container.day = day
                    val textView = container.textView
                    val occContainer = container.occContainer
                    val tvOccupancy = container.tvOccupancy
                    textView.text = day.date.dayOfMonth.toString()
                    tvOccupancy.text = ""

                    val index = occupancyCalendarList.indexOfFirst {
                        it.day == day.date.toString()
                    }
                    if (index != -1) {
                        tvOccupancy.text = "${occupancyCalendarList[index].occupancy}%"

                        setOccupancyColor(
                            this@DashboardNavigateActivity,
                            tvOccupancy,
                            occupancyCalendarList[index].occupancy.toDouble()
                        )

                        if (day.owner == DayOwner.THIS_MONTH) {
                            when {
                                selectedDates.contains(day.date) -> {
                                    textView.setTextColorRes(R.color.black)
//                                textView.setBackgroundResource(R.drawable.layout_rounded_shape_occupancy_red)
                                }

                                today == day.date -> {
                                    textView.setTextColorRes(R.color.black)
                                    occContainer.setBackgroundResource(R.drawable.button_selected_bg)

                                }

                                else -> {
                                    textView.setTextColorRes(R.color.black)
                                    textView.background = null

                                }
                            }
                        }
                    } else {
                        textView.setTextColorRes(R.color.gray)
                        textView.background = null
                        tvOccupancy.setTextColorRes(R.color.gray)
                        tvOccupancy.background = null
                        //container.occContainer.invisible()
                    }
                }
            }
        //        val title = "${monthTitleFormatter.format(today.yearMonth)} ${today.yearMonth.year}"
        //        binding.openCalender.calenderLayout.exOneMonthText.text = title
        binding.openCalender.calenderLayout.calendarView.setOnTouchListener { v, event ->
            !binding.openCalender.calenderLayout.calendarView.isClickable
        }
        binding.openCalender.calenderLayout.calendarView.monthScrollListener = { month ->
            val title = "${monthTitleFormatter.format(month.yearMonth)} ${month.yearMonth.year}"
            binding.openCalender.calenderLayout.exOneMonthText.text = title


            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                binding.openCalender.calenderLayout.calendarView.notifyDateChanged(it)
//                    updateAdapterForDate(null)
            }

        }
        binding.openCalender.calenderLayout.exFiveNextMonthImage.setOnClickListener {
            binding.openCalender.calenderLayout.calendarView.findFirstVisibleMonth()?.let {
                binding.openCalender.calenderLayout.calendarView.smoothScrollToMonth(it.yearMonth.next)
                globalSelectedDate = it.yearMonth.next.toString()
                val startDate =
                    getPrevFullCalenderDate(it.yearMonth.next.toString()).split("#")[0].toString()
                val endDate =
                    getPrevFullCalenderDate(it.yearMonth.next.toString()).split("#")[1].toString()
                callOccupancyCalendarApi(startDate, endDate)

                //Timber.d("globalSelectMonth (it.yearMonth) =>" + it.yearMonth.next.toString())

                if (it.yearMonth.next.toString().contentEquals(globalSelectEndMonth.toString())) {
                    binding.openCalender.calenderLayout.exFiveNextMonthImage.invisible()
                } else {
                    binding.openCalender.calenderLayout.exFiveNextMonthImage.visible()
                }

            }
        }
        binding.openCalender.calenderLayout.exFivePreviousMonthImage.setOnClickListener {
            binding.openCalender.calenderLayout.calendarView.findFirstVisibleMonth()?.let {
                binding.openCalender.calenderLayout.calendarView.smoothScrollToMonth(it.yearMonth.previous)
                globalSelectedDate = it.yearMonth.previous.toString()
                val startDate =
                    getPrevFullCalenderDate(it.yearMonth.previous.toString()).split("#")[0].toString()
                val endDate =
                    getPrevFullCalenderDate(it.yearMonth.previous.toString()).split("#")[1].toString()
                callOccupancyCalendarApi(startDate, endDate)

                /*PreferenceUtils.setPreference("calendarView_previous",it.yearMonth.previous.toString())
                    context.toast(it.yearMonth.previous.toString())
                    clickListener?.onClickOfItem("${it.yearMonth.previous}",0)*/
                if (it.yearMonth.next.toString().contentEquals(globalSelectEndMonth.toString())) {
                    binding.openCalender.calenderLayout.exFiveNextMonthImage.invisible()
                } else {
                    binding.openCalender.calenderLayout.exFiveNextMonthImage.visible()
                }
            }
        }
    }

    private fun landingPageNavigation() {

        when (PreferenceUtils.getString(getString(R.string.landing_page)).toString()) {
            getString(R.string.dashboard) -> {
                setDefaultDashboard()
                back = true
            }

            getString(R.string.booking) -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.bookings_fragment)
                setToolbarTitle(getString(R.string.bookings))
                back = true
            }

            getString(R.string.pickup_chart) -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.pickup_fragment)
                setToolbarTitle(getString(R.string.pickup_chart))
                back = true
            }

            getString(R.string.reports) -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.report_fragment)
                navController.navigate(R.id.report_fragment)
                setToolbarTitle(getString(R.string.reports))
                back = true
            }

            getString(R.string.checking_inspector) -> {
                if (getPrivilegeBase()?.availableAppModes?.checkingInspectorMode != null) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.chk_inspector_fragment)
                    navController.navigate(R.id.chk_inspector_fragment)
                    setToolbarTitle(getString(R.string.checking_inspector))
                    back = true
                } else {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.bookings_fragment)
                    setToolbarTitle(getString(R.string.bookings))
                    back = true
                }

            }

            else -> {
                if (privilegeResponse?.country.equals(
                        "India", true
                    ) && currentUser.role == getString(R.string.role_field_officer)
                ) {
                    if (privilegeResponse?.boLicenses != null && privilegeResponse?.boLicenses?.showBookingAndCollectionTabInTsApp == true && privilegeResponse?.country.equals(
                            "India", true
                        )
                    ) {
                        findNavController(R.id.nav_host_fragment).navigate(R.id.bookings_fragment)
                        setToolbarTitle(getString(R.string.bookings))
                        back = true
                    } else if (privilegeResponse?.showBusMobilityAppDashboard == true) {
                        findNavController(R.id.nav_host_fragment).navigate(R.id.dashboard_fragment)
                        setToolbarTitle(getString(R.string.dashboard))
                        back = true
                    } else {
                        findNavController(R.id.nav_host_fragment).navigate(R.id.pickup_fragment)
                        setToolbarTitle(getString(R.string.pickup_chart))
                        back = true
                    }
                } else {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.bookings_fragment)
                    setToolbarTitle(getString(R.string.bookings))
                    back = true
                }


            }
        }
    }

    override fun initUI() {
        setContentView(binding.root)

        binding.bottomNavView.gone()




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            WindowCompat.setDecorFitsSystemWindows(window, false)
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val ime = insets.getInsets(WindowInsetsCompat.Type.ime()) // 👈 keyboard
                val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

                val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

                view.setPadding(
                    systemBars.left,
                    systemBars.top, // status bar handled visually
                    systemBars.right,
                    if (isKeyboardVisible) ime.bottom else 0
                )


                insets
            }

            ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout) { view, insets ->
                val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

                // Apply padding only to the drawer content
                val drawer = view.findViewById<View>(R.id.nav_view) // or whatever your drawer ID is
                drawer?.setPadding(
                    drawer.paddingLeft,
                    drawer.paddingTop,
                    drawer.paddingRight,
                    navBarHeight
                )


                val addUser =
                    view.findViewById<View>(R.id.layout_add_user_data) // or whatever your drawer ID is
                addUser?.setPadding(
                    drawer.paddingLeft,
                    drawer.paddingTop,
                    drawer.paddingRight,
                    navBarHeight
                )

                // Don’t consume insets — pass them along
                insets
            }


        }


// Set the background color of the status bar (has effect pre-Android 15)

        setSupportActionBar(binding.appBar.toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        getPref()
        appUpdateManager = AppUpdateManagerFactory.create(baseContext)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        //If "new booking" button is pressed on TicketDetailsActivity
        if (intent.getBooleanExtra("newBooking", false)) {
            isNewBookingFromTicketDetails = intent.getBooleanExtra("newBooking", false)
        }


        val currentMonth = YearMonth.now()
        val startYYMMDD = "${currentMonth}-01"

        dashboardViewModel.getEndYYMMDD(startYYMMDD)

        binding.appBar.notificationImg.setOnClickListener {
            //    startActivity(Intent(this, NotificationsActivity::class.java))
            startActivity(Intent(this, NotificationsActivityNew::class.java))
        }
        binding.appBar.calendarImg.setOnClickListener {

            if (binding.navigationPB.visibility != View.VISIBLE) {
                endYYMMDD?.let { it1 -> callOccupancyCalendarApi(startYYMMDD, it1) }
            }

            binding.navigationPB.visible()

            firebaseLogEvent(
                this,
                OCC_CALENDAR,
                currentUser.userName,
                currentUser.travels_name,
                currentUser.role,
                OCC_CALENDAR,
                OccCalendar.OCCUPANCY_CALENDAR
            )
        }

        binding.openCalender.calenderLayout.tvCancel.setOnClickListener {
            calenderOpen = false
            globalSelectedDate = ""
            globalSelectEndMonth = null
            binding.openCalender.root.gone()
        }


        binding.includeCounter.buttonLoginCounter.setSafeOnClickListener {
            if (selectedShiftId == null) {
                toast(getString(R.string.pleaseSelectShift))
                return@setSafeOnClickListener
            }

            if (selectedCounterId == null) {
                toast(getString(R.string.pleaseSelectCounter))
                return@setSafeOnClickListener
            }

            if (isNetworkAvailable()) {
                dashboardViewModel.loginApi(
                    username,
                    password,
                    locale,
                    getDeviceUniqueId(this),
                    selectedShiftId,
                    selectedCounterId,
                    binding.includeCounter.etCounterBalance.text.toString()
                )
            } else {
                noNetworkToast()
            }
        }

        binding.includeCounter.tvCancel.setOnClickListener {
            hideCounterLogin()
        }

        clickListener()
        setNavDrawerListener()
        setUpObserver()
        getAppVersion()
        addVerifyUsers()
        encryptDecryptDeviceId()
        setAddUserTextOnChangeObserver()
        getAllUserObserver()
        userViewModel.getCurrentUser()
        getCurrentUserObserver()
        restartActivityObserver()
        setOccupancyCalendarObserver()
        lifecycleScope.launch {
            dashboardViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }


    private fun requestForLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission is granted. Continue the action or workflow in your app.
                getLastKnownLocation()
            } else {
                toast("Location Permission Denied")

                // Permission denied. Inform the user that the feature requires permission.
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                location?.let {
                    // Use the location object
                    try {
                        val latitude = it.latitude
                        val longitude = it.longitude
                        getCityName(latitude, longitude)
                    } catch (e: Exception) {
                        if (com.google.firebase.messaging.BuildConfig.DEBUG) {
                            e.printStackTrace()
                        }
                    }
                }
            }
    }

    fun getGpsCity(): String {
        return gpsCityName
    }

    private fun getCityName(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocoder = Geocoder(this@DashboardNavigateActivity, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val city = if (addresses != null && addresses.isNotEmpty()) {
                    addresses[0].locality ?: ""
                } else {
                    ""
                }

                gpsCityName = city // Update your variable if required

                withContext(Dispatchers.Main) {
                    cityName.emit(city) // main thread
                }
            } catch (e: IOException) {
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    cityName.emit("") // empty city name if there's an error
                }
            }
        }
    }

    private fun getPref() {

        currentUser = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()


        if (PreferenceUtils.getString(PREF_FCM_TOKEN).isNullOrEmpty()) {
//        get FCM token
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    fcmToken = token
                    //Timber.d("fcmToken-$token")
                    PreferenceUtils.putString(PREF_FCM_TOKEN, token.toString())
                }
            }
        } else {
            fcmToken = PreferenceUtils.getString(PREF_FCM_TOKEN)
        }
    }

    private fun remoteConfigUpdateCheck() {
        RemoteConfigUpdateHelper.with(this).onUpdateCheck(this).check()
        RemoteConfigUpdateHelper.with(this).onLocationIntervalFetch(this).check()
    }

    private fun addVerifyUsers() {
        binding.layoutAddUserData.etDomain.onChange {
            listenTextWatcher()
        }

        binding.layoutAddUserData.etUsername.onChange {
            onChangeUsername = it
            listenTextWatcher()
        }

        binding.layoutAddUserData.etPassword.onChange {
            onChangePassword = it
            listenTextWatcher()
        }

        binding.layoutVerifyOtp.etOtp.onChange {
            onChangeOtp = it
            listenTextWatcherOtp()
        }
        dashboardViewModel.etOnChangeOTP.observe(this) {
            if (it) {
                binding.layoutVerifyOtp.textResendOtp.gone()
                binding.layoutVerifyOtp.btnVerify.text = getString(R.string.verifyAndAdd)
                binding.layoutVerifyOtp.btnVerify.setBackgroundResource(R.drawable.button_selected_bg)
            } else {
                binding.layoutVerifyOtp.textResendOtp.visible()
                binding.layoutVerifyOtp.btnVerify.text = getString(R.string.verify)
                binding.layoutVerifyOtp.btnVerify.setBackgroundResource(R.drawable.button_default_bg)
            }
        }
    }

    private fun listenTextWatcher() {
        dashboardViewModel.etTextWatcher(onChangeUsername, onChangePassword)
    }

    private fun setAddUserTextOnChangeObserver() {
        dashboardViewModel.etOnChange.observe(this) {
            try {
                if (it) {
                    binding.layoutAddUserData.buttonAddUser.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#3CADB5"))
                } else {
                    binding.layoutAddUserData.buttonAddUser.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#AFAFAF"))
                }
            } catch (e: Exception) {
                //Timber.d("e::${e}")
            }
        }

    }

    private fun listenTextWatcherOtp() {
        dashboardViewModel.etTextWatcherOTP(onChangeOtp)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpObserver() {
        try {

            dashboardViewModel.getDate.observe(this) {
                endYYMMDD = it
            }

            dashboardViewModel.loginWithOTP.observe(this) {
                //try {
                closeKeyBoard()
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            val loginModel = it
                            loginModel.domainName =
                                binding.layoutAddUserData.etDomain.text.toString()
                            loginModel.userName =
                                binding.layoutAddUserData.etUsername.text.toString()
                            loginModel.password =
                                binding.layoutAddUserData.etPassword.text.toString()
                            loginModel.isEncryptionEnabled = EncrypDecryp.isEncrypted()

                            removeSourceDest()
                            //remove cached destination pair
                            PreferenceUtils.removeKey(getString(R.string.DESTINATION_PAIR_MODEL_KEY))
                            PreferenceUtils.removeKey(getString(R.string.OLD_COUNT_KEY))
                            PreferenceUtils.removeKey(PREF_DASHBOARD_MODEL_DATA)
                            PreferenceUtils.removeKey(PREF_DASHBOARD_API_MEASURE_TIME)
                            PreferenceUtils.removeKey(PREF_DASHBOARD_NAVIGATE_SCREEN)
                            PreferenceUtils.removeKey("orderBy")


                            userViewModel.insertUserAndRestartActivity(loginModel.toUserModel())
                        }

                        399 -> {
                            openResetDialog()
                        }

                        else -> {
                            it.result.message?.let { it1 -> toast(it1) }
                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
                /*} catch (e: Exception) {
                    Timber.d("Error occurred in dashboardViewModel.data.observe ${e.message}")
                    toast(getString(R.string.opps))
                }*/
            }

            dashboardViewModel.validationData.observe(this) {
                //remove cached destination pair
                PreferenceUtils.removeKey(getString(R.string.DESTINATION_PAIR_MODEL_KEY))
                PreferenceUtils.removeKey(getString(R.string.OLD_COUNT_KEY))
                try {
                    // privilege otp based sign in
                    if (currentUser.otp.isEmpty()) {
                        if (it.isNotEmpty()) toast(it)
                        else {

                            removeSourceDest()

                            if (PreferenceUtils.getPreference(PREF_DOMAIN, "") != null) {
                                val prefDomain = PreferenceUtils.getPreference(PREF_DOMAIN, "")!!
                                if (prefDomain == domain) {
                                    callLoginApi()
                                } else {
                                    dashboardViewModel.isResetUserCall = false
                                    setUpdatedApiUrlAddress(domain)
                                    callDomainApi()
                                }
                            } else callLoginApi()
                        }
                    } else {
                        isOtpBasedLogin = true
                        callLoginApi()
                    }
                } catch (e: Exception) {
                    //Timber.d("Error occurred in dashboardViewModel.validationData.observe ${e.message}")
                    toast(getString(R.string.opps))

                }
            }

            dashboardViewModel.validationDataOtp.observe(this) {
                try {

                    if (it.isNotEmpty()) toast(it)
                    else {
                        if (isNetworkAvailable()) {
                            val otp = binding.layoutVerifyOtp.etOtp.text.toString()
                            val reqBody = com.bitla.ts.domain.pojo.login_with_otp.request.ReqBody(
                                currentUser.phone_number,
                                currentUser.key,
                                otp,
                                locale = locale,
                                device_id = getDeviceUniqueId(this)
                            )

                            if (otp != currentUser.otp) {
                                toast(getString(R.string.invalid_otp))
                            } else {

                                dashboardViewModel.confirmOTP(
                                    reqBody, login_with_otp_method_name
                                )
                                binding.layoutVerifyOtp.etOtp.setText("")
                            }
                        } else {
                            noNetworkToast()
                        }
                    }
                } catch (e: Exception) {
                    //Timber.d("Error occurred in dashboardViewModel.validationDataOtp.observe ${e.message}")
                    toast(getString(R.string.opps))
                }
            }

            dashboardViewModel.dataDynamicDomain.observe(this) {
                try {
                    //Timber.d("dataDomain $it")
                    if (it != null) {
                        if (it.code == 200) {
                            if (it.result?.dailingCode != null) setCountryCodes(it.result?.dailingCode)
                            else {
                                val dialingCode = ArrayList<Int>()
                                dialingCode.add(91)
                                setCountryCodes(dialingCode)
                            }

                            PreferenceUtils.setPreference(
                                PREF_IS_ENCRYPTED,
                                it.result?.isEncrypted ?: false
                            )
                            if (dashboardViewModel.isResetUserCall == true) {
                                updateBaseURL(domain)
                                checkHttpsAndSetupClient(it)

                            } else {
                                updateBaseURL(domain)
                                checkHttpsAndSetupClient(it)
                                dashboardViewModel.loginApi(
                                    username, password, locale, getDeviceUniqueId(this)
                                )
                            }
                        } else {
                            if (it.message != null) toast(it.message)
                        }
                    } else {

                        if (!PreferenceUtils.getString(PREF_EXCEPTION).isNullOrEmpty()) toast(
                            PreferenceUtils.getString(PREF_EXCEPTION)
                        )
                        else toast(getString(R.string.server_error))
                    }
                } catch (e: Exception) {
                    toast(getString(R.string.opps))
                }
            }

            dashboardViewModel.resetUser.observe(this) { loginModel ->
                try {
                    if (loginModel.code == 200) {

                        if (loginModel.is_counter_enabled_by_user == true) {
                            showCounterLogin(loginModel)
                            return@observe
                        }

                        loginModel.domainName = domain
                        loginModel.userName = username
                        loginModel.password = password

                        removeSourceDest()

                        Sentry.configureScope { scope ->
                            scope.setTag(LOGIN_ID, username)
                            scope.setTag(OPERATOR_NAME, loginModel.travels_name ?: "")
                            scope.setTag(ROLE_NAME, loginModel.role)
                            loginModel.dialingCode?.get(0)
                                ?.let { it1 -> scope.setTag(COUNTRY_CODE, it1.toString()) }
                        }

                        PreferenceUtils.setSubAgentRole(loginModel.is_sub_agent_and_user)
                        loginModel.isEncryptionEnabled = EncrypDecryp.isEncrypted()
                        userViewModel.insertUserAndRestartActivity(loginModel.toUserModel())

                    } else if (loginModel.code == 411 && isReLoginClick) {
                        openDeviceRegistrationDialog(loginModel.result.message ?: "")
                    } else {
                        loginModel.result.message?.let { toast(it) }
                    }
                } catch (e: Exception) {
                    //Timber.d("Error occurred in dashboardViewModel.dataLogout.observe ${e.message}")
                    toast(getString(R.string.opps))
                }
                isReLoginClick = false
            }

            dashboardViewModel.loginUser.observe(this) {
                try {
                    closeKeyBoard()

                    //Timber.d("okh isOtpBasedLogin $isOtpBasedLogin response $it")

                    if (it != null) {

                        isOtpBasedLogin = it.otp.isNotEmpty()

                        if (it.code == 200) {

                            if (it.is_counter_enabled_by_user == true) {
                                showCounterLogin(it)
                                return@observe
                            }

                            if (isOtpBasedLogin) {

                                currentUser.otp = it.otp
                                currentUser.key = it.key
                                currentUser.phone_number = it.mobile_number
                                PreferenceUtils.setSubAgentRole(it.is_sub_agent_and_user)
                                updateBaseURL(domain)
                                binding.layoutAddUserData.root.gone()
                                binding.layoutVerifyOtp.root.visible()
                                otpCountDown()
                                binding.layoutVerifyOtp.tvOTPmsg.text =
                                    "${getString(R.string.otp_sent_message)} ${it.mobile_number}"
                            } else {
                                saveAddedUser(it)
                            }
                        } else if (it.code == 399) {
                            openResetDialog()
                        } else if (it.code == 411) {
                            openDeviceRegistrationDialog(it.result.message ?: "")
                        } else {
                            it.result.message?.let { it1 -> toast(it1) }
                        }
                    } else toast(getString(R.string.server_error))
                } catch (e: Exception) {
                    //Timber.d("Error occurred in dashboardViewModel.dataAddUser.observe ${e.message}")
                    toast(getString(R.string.opps))

                }
            }

            privilegeDetailsViewModel.privilegeResponseModel.observe(this) {
                try {
                    if (it != null) {
                        if (it.code == 200) {
                            PreferenceUtils.setPreference(
                                "otp_validation_time", it.configuredLoginValidityTime
                            )
                            privilegeResponse = it
                            manageBranchAccounting =
                                privilegeResponse?.manageBranchAccounting ?: false
                            showManageAgentAccountLinkInAccount =
                                privilegeResponse?.showManageAgentAccountLinkInAccount ?: false
                            allowToViewTheCoachDocument =
                                privilegeResponse?.allowToViewTheCoachDocument ?: false
                            agentInstantRecharge = privilegeResponse?.agentInstantRecharge ?: false

                            callStoreFcm()


                            // PreferenceUtils.putObject(it, PREF_PRIVILEGE_DETAILS,this)
                            PreferenceUtils.putObject(
                                LocalDateTime.now(), PREF_PRIVILEGE_DETAILS_CALLED
                            )


                            putObjectBase(it, PREF_PRIVILEGE_DETAILS)
                            remoteConfigUpdateCheck()
                            setData(it)

                            val role = getUserRole(
                                currentUser,
                                isAgentLogin = privilegeResponse?.isAgentLogin ?: false,
                                this
                            )


                            currentCountry = privilegeResponse?.country ?: ""

                            if (!currentCountry.isNullOrEmpty() && currentCountry.equals(
                                    "india",
                                    true
                                )
                            ) {
                                requestForLocationPermission()
                            }


                            if (role == getString(R.string.role_agent) && privilegeResponse?.allowBookingForAllotedServices == true) {
                                PreferenceUtils.setIsAgentAndAllowBookingForAllotedServices(true)
                            } else {
                                PreferenceUtils.setIsAgentAndAllowBookingForAllotedServices(false)
                            }

                            if (privilegeResponse?.tsPrivileges?.showCreditLimitForAgentsAndSubAgents == true) {
                                callCreditInfoApi()
                            }
                            val allowBimaInTs = it.allowBimaInTs ?: false
                            PreferenceUtils.setPreference("is_bima", allowBimaInTs)
                        }
                    } else {
                        // openUnauthorisedDialog()
                        showUnauthorisedDialog()
                    }
                } catch (e: Exception) {
                    toast(getString(R.string.opps))

                }
            }


            dashboardViewModel.creditInfoData.observe(this) {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            binding.creditLayoutCL.visible()
                            if (it.availableBalance.isNullOrEmpty()) {
                                binding.avlBalanceLL.gone()
                                binding.creditLayoutParent.weightSum = 2F
                            } else {
                                binding.avlBalanceLL.visible()
                                binding.creditLayoutParent.weightSum = 3F
                                binding.tvAvlBalance.text = it.availableBalance
                            }
                            binding.tvTotalCredit.text = it.totalCredit
                            binding.tvAvlCredit.text = it.availableCredit
                            binding.tvLastUpdated.text =
                                getString(R.string.last_updated) + " " + it.lastUpdatedOn
                        }

                        401 -> {
                            binding.creditLayoutCL.gone()
                            showUnauthorisedDialog()
                        }

                        else -> {
                            binding.creditLayoutCL.gone()
                            toast(it.message)
                        }
                    }
                } else {
                    binding.creditLayoutCL.gone()
                    toast(getString(R.string.opps))
                }
            }


        } catch (e: Exception) {
            //Timber.d("An exception occurred in method ${e.message.toString()}")
            toast(getString(R.string.opps))

        }
    }


    private fun checkHttpsAndSetupClient(it: DynamicDomain) {
        if (it.result?.isHttpsSupport == true) {
            PreferenceUtils.setIsHttpsSupport(true)
        } else {
            PreferenceUtils.setIsHttpsSupport(false)
        }

        stopKoin()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(getAppContext())
            modules(listOf(RepositoryModule, ViewModelModule, NetworkModule, ApiModule))
        }
        callResetApi(username, password)
    }

    private fun setData(it: PrivilegeResponseModel) {
        lifecycleScope.launch {
            // Move heavy work off the main thread
            val processedData = withContext(Dispatchers.Default) {
                prepareDataForUI(it)
            }
            // Update UI on the main thread
            updateUIWithPrivilege(processedData)
        }
    }

    // --- CHANGED: Extract heavy logic from setData to this function
    private fun prepareDataForUI(it: PrivilegeResponseModel): PrivilegeResponseModel {
        // Place heavy data processing here if any
        // If no heavy work, just return the object
        return it
    }

    // --- CHANGED: Extract UI update logic from setData to this function
    private fun updateUIWithPrivilege(it: PrivilegeResponseModel) {
        PreferenceUtils.setPreference("otp_validation_time", it.configuredLoginValidityTime)

        PreferenceUtils.setPreference(
            "send_otp_to_customers_to_authenticate_boarding_status",
            it.sendOtpToCustomersToAuthenticateBoardingStatus
        )
        setCurrentCoach(it)


        if (currentUser.role == getString(R.string.role_field_officer) &&
            privilegeResponse?.boLicenses != null && privilegeResponse?.country.equals(
                "India", true
            )
        ) {
            if (privilegeResponse?.boLicenses?.showBookingAndCollectionTabInTsApp == true && privilegeResponse?.showBusMobilityAppDashboard == false) {
                PreferenceUtils.putString(
                    getString(R.string.landing_page),
                    getString(R.string.booking)
                )
            } else if (privilegeResponse?.boLicenses?.showBookingAndCollectionTabInTsApp == false && privilegeResponse?.showBusMobilityAppDashboard == true) {
                PreferenceUtils.putString(
                    getString(R.string.landing_page),
                    getString(R.string.dashboard)
                )
            } else {
                val currentpage = PreferenceUtils.getString(getString(R.string.landing_page))
                if (currentpage.equals(getString(R.string.dashboard)) && privilegeResponse?.showBusMobilityAppDashboard == true) {
                    PreferenceUtils.putString(
                        getString(R.string.landing_page),
                        getString(R.string.dashboard)
                    )
                } else if (currentpage.equals(getString(R.string.booking)) && privilegeResponse?.boLicenses?.showBookingAndCollectionTabInTsApp == true) {
                    PreferenceUtils.putString(
                        getString(R.string.landing_page),
                        getString(R.string.booking)
                    )
                } else if (privilegeResponse?.showBusMobilityAppDashboard == false && privilegeResponse?.boLicenses?.showBookingAndCollectionTabInTsApp == false) {
                    PreferenceUtils.putString(
                        getString(R.string.landing_page),
                        getString(R.string.pickup_chart)
                    )
                } else {
                    PreferenceUtils.putString(getString(R.string.landing_page), currentpage)
                }
            }
        }





        binding.bottomNavView.menu.findItem(R.id.dashboard_fragment).isVisible =
            !(it.allowBookingForAllotedServices != null && it.allowBookingForAllotedServices)


        if (currentUser.role == getString(R.string.role_field_officer) && privilegeResponse?.country.equals(
                "India", true
            )
        ) {
            binding.bottomNavView.menu.findItem(R.id.bookings_fragment).isVisible =
                it.boLicenses?.showBookingAndCollectionTabInTsApp == true
            binding.bottomNavView.menu.findItem(R.id.dashboard_fragment).isVisible =
                it.showBusMobilityAppDashboard == true
        }

        if (it.availableAppModes?.checkingInspectorMode != null && !it.availableAppModes.checkingInspectorMode) {
            binding.bottomNavView.menu.removeItem(R.id.chk_inspector_fragment)
        } else {
            binding.bottomNavView.menu.findItem(R.id.chk_inspector_fragment).isVisible = true
        }
        if (it.availableAppModes?.showReports != null && !it.availableAppModes.showReports) {
            binding.bottomNavView.menu.removeItem(R.id.report_fragment)
        }

        setNavAdapter()
        setBottomBarOptions()
        if (isNewBookingFromTicketDetails) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.bookings_fragment)
            setToolbarTitle(getString(R.string.bookings))
        } else
            landingPageNavigation()
    }

    private fun setCurrentCoach(it: PrivilegeResponseModel) {
        if (it.currentCoachLayout != null) {
            val coachType = when (it.currentCoachLayout) {
                "single" -> "SingleViewSelected"
                "split" -> "SplitViewSelected"
                "web" -> "WebViewSelected"
                else -> "SingleViewSelected"
            }
            PreferenceUtils.setPreference("COACH_VIEW_SELECTION", coachType)
        }
    }

    private fun removeSourceDest() {

        PreferenceUtils.apply {
            removeKey(PREF_SOURCE)
            removeKey(PREF_SOURCE_ID)
            removeKey(PREF_DESTINATION)
            removeKey(PREF_DESTINATION_ID)
            removeKey(PREF_LAST_SEARCHED_SOURCE)
            removeKey(PREF_LAST_SEARCHED_DESTINATION)
            removeKey(PREF_PRIVILEGE_DETAILS_CALLED)
            removeKey("recentOrigin")
        }
    }

    private fun callPrivilegeDetailsApi() {
        lifecycleScope.launch {
            privilegeDetailsViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        privilegeDetailsViewModel.getPrivilegeDetailsApi(
            currentUser.api_key, privilege_details_method_name, format_type, locale!!
        )
    }

    private fun callLoginApi() {

        if (isNetworkAvailable()) {
            dashboardViewModel.loginApi(
                username, password, locale, getDeviceUniqueId(this)
            )

        } else noNetworkToast()
    }

    private fun callDomainApi() {
        if (isNetworkAvailable()) {
            dashboardViewModel.initDynamicDomain(dynamic_domain)
        } else noNetworkToast()
    }

    private fun callStoreFcm() {
        if (isNetworkAvailable()) {
            dashboardViewModel.storeFcmKey(
                apiKey = currentUser.api_key,
                deviceId = getDeviceUniqueId(context = this),
                fcmKey = fcmToken ?: "",
                apiType = store_fcm_key_method
            )
        } else noNetworkToast()
    }

    private fun saveAddedUser(loginModel: LoginModel) {

        loginModel.apply {

            if(binding.layoutAddUserData.etUsername.text.toString().isEmpty() && binding.layoutAddUserData.etPassword.text.toString().isEmpty() && binding.layoutAddUserData.etUsername.text.toString().isEmpty()){
                userName = lastCounterUserName
                password = lastCounterPassword
                domainName = lastCounterDomain
            }else{
                userName = binding.layoutAddUserData.etUsername.text.toString()
                password = binding.layoutAddUserData.etPassword.text.toString()
                domainName = binding.layoutAddUserData.etDomain.text.toString()
            }






            mba_url = getUpdatedApiUrlAddress()
            dialingCode = getCountryCodes()
            isEncryptionEnabled = EncrypDecryp.isEncrypted()
        }

        removeSourceDest()
        userViewModel.insertUserAndRestartActivity(loginModel.toUserModel())
    }

    private fun otpCountDown() {
        if (binding.layoutVerifyOtp.textResendOtp.text == getString(R.string.resend_otp)) {
            object : CountDownTimer(30000, 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    // Used for formatting digit to be in 2 digits only
                    val f = DecimalFormat("00")
                    val sec = millisUntilFinished / 1000 % 60
                    binding.layoutVerifyOtp.textResendOtp.text =
                        "Resend OTP in ${f.format(sec)} sec"
                }

                override fun onFinish() {
                    binding.layoutVerifyOtp.textResendOtp.text = getString(R.string.resend_otp)
                }
            }.start()
        }
    }

    private fun clickListener() {
        binding.tvLogout.setOnClickListener(this)
        binding.closeCounterIV.setOnClickListener(this)
        binding.tvCloseCounter.setOnClickListener(this)
        binding.imageLogout.setOnClickListener(this)
        binding.layoutAddUserData.buttonAddUser.setOnClickListener(this)
        binding.layoutAddUserData.textAddUserCancel.setOnClickListener(this)
        binding.layoutAddUserData.imgCross.setOnClickListener(this)
        binding.layoutVerifyOtp.btnVerify.setOnClickListener(this)
        binding.layoutVerifyOtp.tvCancel.setOnClickListener(this)
        binding.layoutVerifyOtp.tvBack.setOnClickListener(this)
        binding.layoutVerifyOtp.textResendOtp.setOnClickListener(this)
        binding.appBar.layoutPnr.setOnClickListener(this)
        binding.ivSwitchUser.setOnClickListener(this)
        binding.layoutAddUserData.root.setOnClickListener(this)
        binding.layoutVerifyOtp.root.setOnClickListener(this)
        binding.refreshCreditIV.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {

            R.id.tvLogout -> {
                callLogoutApi()
            }

            R.id.closeCounterIV -> {
                callLogoutApi(true)
            }

            R.id.tvCloseCounter -> {
                callLogoutApi(true)
            }

            R.id.image_logout -> {
                callLogoutApi()
            }

            R.id.nav_add_user -> {
                openCloseNavigationDrawer()
                binding.layoutAddUserData.root.visible()
                if (!PreferenceUtils.getPreference(PREF_DOMAIN, "").isNullOrEmpty()) {
                    binding.layoutAddUserData.etDomain.setText(
                        PreferenceUtils.getPreference(
                            PREF_DOMAIN, ""
                        )
                    )
                }
            }

            R.id.button_add_user -> {

                val isUserAlreadyPresent = userList.any {
                    (it.domainName.equals(
                        binding.layoutAddUserData.etDomain.text.toString(),
                        true
                    )) && (it.username.equals(
                        binding.layoutAddUserData.etUsername.text.toString(), true
                    ))
                }

                if (isUserAlreadyPresent) {
                    toast(getString(R.string.user_already_added))
                } else {

                    domain = binding.layoutAddUserData.etDomain.text.toString()
                    username = binding.layoutAddUserData.etUsername.text.toString()
                    password = binding.layoutAddUserData.etPassword.text.toString()

                    dashboardViewModel.validation(domain, username, password)
                }
            }

            R.id.text_add_user_cancel -> {
                makeParamsEmpty()
                binding.layoutAddUserData.root.gone()
            }

            R.id.tv_back -> {
                makeParamsEmpty()
                binding.layoutVerifyOtp.root.gone()
            }

            R.id.tv_cancel -> {
                makeParamsEmpty()
                binding.layoutVerifyOtp.root.gone()
            }

            R.id.imgCross -> {
                domain = ""
                binding.layoutAddUserData.etDomain.setText("")
            }

            R.id.btn_verify -> {
                dashboardViewModel.validationOTP(binding.layoutVerifyOtp.etOtp.text.toString())
            }

            R.id.text_resend_otp -> {
                callLoginApi()
                otpCountDown()
            }

            R.id.layoutPnr -> {
                val intent = Intent(this, PnrSearchActivity::class.java)
                startActivity(intent)
            }

            R.id.ivSwitchUser -> {
                userViewModel.getAllUsers()
            }

            R.id.refreshCreditIV -> {
                callCreditInfoApi()
            }
        }
    }

    //bottom nav
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

    private fun makeParamsEmpty() {
        // domain = ""
        username = ""
        password = ""
        //binding.layoutAddUserData.etDomain.setText("")
        binding.layoutAddUserData.etUsername.setText("")
        binding.layoutAddUserData.etPassword.setText("")
    }

    fun checkForVehicleDocument(bool: Boolean) {
        allowToViewTheCoachDocument = bool
    }

    fun setNavAdapter() {
        menuList = mutableListOf()
        if (manageBranchAccounting || showManageAgentAccountLinkInAccount) {
            menuList.add(
                NavMenuModel(
                    resources.getString(R.string.recharge), R.drawable.ic_recharge
                )
            )
        }

        val role = getUserRole(
            currentUser,
            isAgentLogin = privilegeResponse?.isAgentLogin ?: false,
            this
        )

        if (role == getString(R.string.role_agent) && agentInstantRecharge && privilegeResponse?.rechargeTypes?.instantRecharge == true) {
            menuList.add(
                NavMenuModel(
                    resources.getString(R.string.recharge),
                    R.drawable.ic_recharge
                )
            )
        }
        if (role == getString(R.string.role_field_officer)) {
            allowToViewVehicleDocBO =
                privilegeResponse?.boLicenses?.allowToViewVehicleDocumentOption ?: false
            if (allowToViewVehicleDocBO) {
                menuList.add(
                    NavMenuModel(
                        resources.getString(R.string.vehicle_documents),
                        R.drawable.ic_vehicle_documents_nav_drawer
                    )
                )
            }
        } else {
            if (allowToViewTheCoachDocument) {
                menuList.add(
                    NavMenuModel(
                        resources.getString(R.string.vehicle_documents),
                        R.drawable.ic_vehicle_documents_nav_drawer
                    )
                )
            }
        }


        if (privilegeResponse?.manageRoutesInTsApp == true) {
            menuList.add(
                NavMenuModel(
                    getString(R.string.route_manager),
                    R.drawable.ic_route_manager
                )
            )
        }

        if (privilegeResponse?.country.equals("india", true)) {
            if (showManageAgentAccountLinkInAccount || manageBranchAccounting) {
                menuList.add(
                    NavMenuModel(
                        resources.getString(R.string.manage_account), R.drawable.ic_manage_acc
                    )
                )
            }
        }


        if (privilegeResponse?.isAgentLogin == true && !privilegeResponse?.country.equals(
                "india", true
            )
        ) {
            val index = menuList.indexOfFirst {
                it.title == getString(R.string.account_details)
            }

            if (index == -1) {
                menuList.add(
                    NavMenuModel(
                        getString(R.string.account_details), R.drawable.baseline_account_circle_24
                    )
                )
            }
        }

        if (privilegeResponse?.tsPrivileges?.allowFareChangeForMultipleServices == true) {
            menuList.add(
                NavMenuModel(
                    getString(R.string.fare_change_multiple_services),
                    R.drawable.ic_recharge
                )
            )
        }


        binding.navRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        navigationMenuAdapter = NavigationMenuAdapter(this, menuList) {
            when (it) {
                resources.getString(R.string.recharge) -> {
                    val role = getUserRole(
                        currentUser,
                        isAgentLogin = privilegeResponse?.isAgentLogin ?: false,
                        this
                    )
                    if (role == getString(R.string.role_agent)) {
                        val intent = Intent(this, InstantRechargeActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, RechargeActivity::class.java)
                        this.startActivity(intent)
                    }

                }

                resources.getString(R.string.vehicle_documents) -> {
                    val intent = Intent(this, VehicleDetailsActivity::class.java)
                    this.startActivity(intent)
                }

                getString(R.string.manage_account) -> {
                    val intent = Intent(this, ManageAccountActivity::class.java)
                    startActivity(intent)
                }

                getString(R.string.route_manager) -> {
                    val intent = Intent(this, RouteServiceManagerActivity::class.java)
                    startActivity(intent)
                }


                getString(R.string.account_details) -> {

                    firebaseLogEvent(
                        this,
                        ACCOUNT_DETAILS,
                        currentUser.userName,
                        currentUser.travels_name,
                        currentUser.role,
                        ACCOUNT_DETAILS,
                        ACCOUNT_DETAILS_HAMBURGER_MENU
                    )

                    val intent = Intent(this, AccountDetailsActivity::class.java)
                    startActivity(intent)
                }

                getString(R.string.fare_change_multiple_services) -> {
                    val intent = Intent(this, MultipleServicesManageFareActivity::class.java)
                    startActivity(intent)
                }
            }
            openCloseNavigationDrawer()
        }
        binding.navRecyclerView.adapter = navigationMenuAdapter
    }

    private fun callLogoutApi(closeCounter: Boolean = false) {

        firebaseLogEvent(
            this,
            APP_LOGOUT,
            currentUser.userName,
            currentUser.travels_name,
            currentUser.role,
            APP_LOGOUT,
            LOG_OUT
        )

        if (isNetworkAvailable()) {
            PreferenceUtils.setPreference(PREF_IS_ENCRYPTED, currentUser.isEncryptionEnabled)
            dashboardViewModel.fullLogoutApi(
                currentUser.api_key, getDeviceUniqueId(this), closeCounter
            )

            userViewModel.deleteUserAndRestartActivity(currentUser.toUserModel())
            PreferenceUtils.removeKey(PREF_TRAVEL_DATE)
            removeSourceDest()
        } else noNetworkToast()
    }

    override fun attachBaseContext(newBase: Context?) {
        val newsBase = newBase!!
        val lang: String = PreferenceUtils.getlang()
        super.attachBaseContext(MyContextWrapper.wrap(newsBase, lang))
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (privilegeResponse?.showBusMobilityAppDashboard != null && privilegeResponse?.showBusMobilityAppDashboard == false) {
            finishAffinity()
        } else if (privilegeResponse != null && privilegeResponse?.allowBookingForAllotedServices != null && privilegeResponse?.allowBookingForAllotedServices == false) {
            if (calenderOpen) {
                calenderOpen = false
                globalSelectedDate = ""
                globalSelectEndMonth = null
                binding.openCalender.root.gone()
                binding.bottomNavView.menu.getItem(0).isChecked = true
                setDefaultDashboard()
            } else {
                binding.bottomNavView.menu.getItem(0).isChecked = true
                if (isCheckingInspectorDetail) {
                    isCheckingInspectorDetail = false
                    setToolbarTitle(getString(R.string.checking_inspector))
                    findNavController(R.id.nav_host_fragment).navigate(R.id.chk_inspector_fragment)
                    binding.appBar.notificationImg.gone()

                } else {
                    if (back) {
                        setDefaultDashboard()
                        back = false
                        //binding.bottomNavView.visibility = View.GONE
                    } else {
                        finishAffinity()
                    }
                }
                showHideBottomBar(true)
//                if (isCheckingInspectorDetail) {
//                    isCheckingInspectorDetail= false
//                    setToolbarTitle(getString(R.string.checking_inspector))
//                    findNavController(R.id.nav_host_fragment).navigate(R.id.chk_inspector_fragment)
//                    binding.appBar.notificationImg.gone()
//                } else {
//
//                    if (back) {
//                        toast("3")
//                        setDefaultDashboard()
//                        back = false
//                    } else {
////                finishAffinity()
//                    }
//
//                }
            }
        } else finishAffinity()
    }

    fun setToolbarTitle(title: String) {
        binding.appBar.apply {

            tvTitle.text = title

            if (title == getString(R.string.bookings)) {
                layoutPnr.visible()
                notificationImg.gone()
                calendarImg.gone()
                lastUpdateTV.gone()

            } else if (title == getString(R.string.pickup_chart)) {
                layoutPnr.gone()
                notificationImg.gone()
                calendarImg.gone()
                lastUpdateTV.gone()

            } else if (title == getString(R.string.reports)) {
                layoutPnr.gone()
                notificationImg.gone()
                calendarImg.gone()
                lastUpdateTV.gone()
            } else if (title == getString(R.string.checking_inspector)) {
                layoutPnr.gone()
                notificationImg.gone()
                calendarImg.gone()
                lastUpdateTV.gone()
            } else {
                binding.appBar.layoutPnr.gone()

                if (!privilegeResponse?.country.equals("India", true)) {
                    notificationImg.gone()
                    calendarImg.gone()
                    lastUpdateTV.gone()
                } else {
                    notificationImg.visible()
                    calendarImg.visible()
                }
            }
        }
    }

    private fun openCloseNavigationDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.dashboard_fragment -> {
                PreferenceUtils.putObject(true, "callAPI_onCLICK")
                privilegeDetailsViewModel.setDashboardDefaultTab(true)
                if (::navigationMenuAdapter.isInitialized) {
                    navigationMenuAdapter.menuColorChange(0)
                    setDefaultDashboard()
                    back = true
//                    PreferenceUtils.putString(PREF_DASHBOARD_NAVIGATE_SCREEN, getString(R.string.dashboard))
                }
            }

            R.id.dashboard_fragment_tabs -> {
                if (::navigationMenuAdapter.isInitialized) {
                    setDefaultDashboard()
                    back = true
//                    PreferenceUtils.putString(PREF_DASHBOARD_NAVIGATE_SCREEN, getString(R.string.dashboard))
                }
            }

            R.id.bookings_fragment -> {
                PreferenceUtils.putObject(true, "callAPI_onCLICK")
                PreferenceUtils.putString(PREF_TRAVEL_DATE, getTodayDate())
                setToolbarTitle(getString(R.string.bookings))

                if (::navigationMenuAdapter.isInitialized) {
                    setToolbarTitle(getString(R.string.bookings))
                    navigationMenuAdapter.menuColorChange(1)
                    findNavController(R.id.nav_host_fragment).navigate(R.id.bookings_fragment)
                    back = true
                    PreferenceUtils.putString(
                        PREF_DASHBOARD_NAVIGATE_SCREEN, getString(R.string.bookings)
                    )
                }
            }

            R.id.pickup_fragment -> {
                PreferenceUtils.putObject(true, "callAPI_onCLICK")

                if (::navigationMenuAdapter.isInitialized) {
                    setToolbarTitle(getString(R.string.pickup_chart))
                    navigationMenuAdapter.menuColorChange(2)
                    findNavController(R.id.nav_host_fragment).navigate(R.id.pickup_fragment)
                    binding.appBar.notificationImg.gone()
                    binding.appBar.calendarImg.gone()
                    back = true
                    PreferenceUtils.putString(
                        PREF_DASHBOARD_NAVIGATE_SCREEN, getString(R.string.pick_up_chart)
                    )
                }
            }

            R.id.report_fragment -> {
                PreferenceUtils.putObject(true, "callAPI_onCLICK")

                if (::navigationMenuAdapter.isInitialized) {
                    setToolbarTitle(getString(R.string.reports))
                    navigationMenuAdapter.menuColorChange(3)
                    findNavController(R.id.nav_host_fragment).navigate(R.id.report_fragment)
                    back = true
                    binding.appBar.notificationImg.gone()
                    binding.appBar.calendarImg.gone()
                    PreferenceUtils.putString(
                        PREF_DASHBOARD_NAVIGATE_SCREEN, getString(R.string.reports)
                    )
                }
            }

            R.id.chk_inspector_fragment -> {
                setToolbarTitle(getString(R.string.checking_inspector))
                back = true
                findNavController(R.id.nav_host_fragment).navigate(R.id.chk_inspector_fragment)
                binding.appBar.notificationImg.gone()
            }
        }
        return true
    }

    fun gotoSettings(view: View) {

        firebaseLogEvent(
            this,
            SETTINGS,
            currentUser.userName,
            currentUser.travels_name,
            currentUser.role,
            SETTINGS,
            SETTINGS_CLICK
        )

        val intent = Intent(this, SettingsActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

    private fun encryptDecryptDeviceId() {
        androidUniqueID = getDeviceUniqueId(this)
        androidUniqueID = encryptToBase64(androidUniqueID)
        androidUniqueID = decryptToBase64(androidUniqueID)
    }

    @SuppressLint("SetTextI18n")
    private fun getAppVersion() {
        binding.tvVersion.text = "${getString(R.string.version)} ${BuildConfig.VERSION_NAME}"
    }

    private fun setOperatorLogo(logoURL: String) {
        Glide.with(this).load(logoURL).fitCenter().placeholder(R.drawable.ic_ts_logo)
            .error(R.drawable.ic_ts_logo).into(binding.operatorLogoIV)

    }

    private fun setDefaultDashboard() {
//        val privilegeResponse = getPrivilegeBase()

        binding.appBar.calendarImg.visible()

        lifecycleScope.launch {
            val privilege = getPrivilegeBaseSafely()
            dashboardViewModel.updatePrivileges(privilege)
        }

        dashboardViewModel.privilegesLiveData.observe(this) { privilegeResponse ->

            if (privilegeResponse?.allowToViewTsAppNewDashboard == true) {
                findNavController(R.id.nav_host_fragment).navigate(R.id.dashboard_fragment_tabs)
                setToolbarTitle(getString(R.string.dashboard))
            } else {
                findNavController(R.id.nav_host_fragment).navigate(R.id.dashboard_fragment)
                setToolbarTitle(getString(R.string.dashboard))
                binding.appBar.lastUpdateTV.gone()
                binding.appBar.notificationImg.gone()
                binding.appBar.calendarImg.gone()
            }

            if (privilegeResponse?.country.equals(
                    "India", true
                ) && privilegeResponse?.isAgentLogin == false
            ) {
                binding.appBar.notificationImg.visible()
            } else {
                binding.appBar.notificationImg.gone()
            }
        }
    }

    override fun onCheckRemoteConfigUpdateListener(
        urlApp: String?,
        title: String?,
        description: String?,
        packageName: String?,
        isCritical: Boolean,
        isManualUpdatePop: Boolean,
        inAppUpdateTS: Boolean,
        updateCountryList: String
    ) {

        if (!IS_PINELAB_DEVICE) {
            updateDetailsList =
                Gson().fromJson(updateCountryList, UpdateCountryListData::class.java)

            this.appPackageName = "com.bitla.ticketsimply"
            this.isMannualUpdatePop = updateDetailsList!!.is_manual_update
            var isUpdate = false
            this.isCritical =
                getPrivilegeBase()?.appSubmissionHistory?.android?.isCriticalUpdate ?: false

            val currentCountry = getPrivilegeBase()?.country ?: ""
            if (updateDetailsList!!.is_global_update!!) {
                this.title = updateDetailsList!!.global_update_details!!.title!!
                this.updateAppDescription =
                    updateDetailsList!!.global_update_details!!.description!!
                this.isCritical = updateDetailsList!!.global_update_details!!.is_critical!!
            } else {
                if (!updateDetailsList!!.use_privilege_version_code!!) {
                    for (i in 0 until updateDetailsList!!.country.size) {
                        if (updateDetailsList!!.country[i].country_name.equals(
                                currentCountry, true
                            )
                        ) {
                            this.title = updateDetailsList!!.country[i].title ?: ""
                            this.updateAppDescription =
                                updateDetailsList!!.country[i].description ?: ""
                            isUpdate = updateDetailsList!!.country[i].is_update!!
                            this.isCritical = updateDetailsList!!.country[i].is_critical!!
//                        Timber.d("isCritical - ${this.isCritical}")
                        }
                    }
                    if (getPrivilegeBase()?.isChileApp == true) {
                        this.title = updateDetailsList!!.country[2].title ?: ""
                        this.updateAppDescription = updateDetailsList!!.country[2].description ?: ""
                        isUpdate = updateDetailsList!!.country[2].is_update!!
                        this.isCritical = updateDetailsList!!.country[2].is_critical!!

                    }
                } else {
                    this.title = updateDetailsList!!.global_update_details!!.title!!
                    this.updateAppDescription =
                        updateDetailsList!!.global_update_details!!.description!!
                    isUpdate = true
                }
            }

            if (getPrivilegeBase()?.isChileApp == false) {
                if (updateDetailsList!!.is_global_update!!) {
                    checkInAppUpdate(this.isCritical)
                } else if (isUpdate) {
                    checkInAppUpdate(this.isCritical)
                }
            }
        }

    }

    //    In app Update
    private fun checkInAppUpdate(isCritical: Boolean) {
        try {
            // Returns an intent object that you use to check for an update.
            bottomSheetDialoge = BottomSheetDialog(this, R.style.BottomSheetDialog)
            val bottomSheetAppUpdateBinding =
                BottomSheetAppUpdateBinding.inflate(LayoutInflater.from(this))
            bottomSheetDialoge.setContentView(bottomSheetAppUpdateBinding.root)
            bottomSheetDialoge.setCancelable(false)

            bottomSheetAppUpdateBinding.title.text = title
            try {
                when {
                    updateAppDescription == "" -> {
                        // return an empty spannable if the html is null
                        bottomSheetAppUpdateBinding.description.gone()
                        //viewMiddle.gone()
                    }

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                        // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
                        // we are using this flag to give a consistent behaviour
                        bottomSheetAppUpdateBinding.description.text =
                            Html.fromHtml(updateAppDescription, Html.FROM_HTML_MODE_LEGACY)
                    }

                    else -> {
                        bottomSheetAppUpdateBinding.description.text =
                            Html.fromHtml(updateAppDescription)
                    }
                }
            } catch (e: Exception) {
                toast(e.message.toString())
            }

            if (isCritical) {
                bottomSheetDialoge.setCancelable(false)
                bottomSheetAppUpdateBinding.btnLater.gone()
            } else {
                bottomSheetDialoge.setCancelable(true)
                bottomSheetAppUpdateBinding.btnLater.visible()
                bottomSheetAppUpdateBinding.btnLater.setOnClickListener {
                    bottomSheetDialoge.dismiss()
                }
            }

            bottomSheetAppUpdateBinding.btnRight.setOnClickListener {
                if (isMannualUpdatePop) {
                    try {
                        val tsAppUrl = Uri.parse("market://details?id=$packageName")
                        val myIntent = Intent(Intent.ACTION_VIEW, tsAppUrl)
                        startActivity(myIntent)
                        finish()
                    } catch (e: ActivityNotFoundException) {
                        val tsAppUrl =
                            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                        val tsIntent = Intent(Intent.ACTION_VIEW, tsAppUrl)
                        startActivity(tsIntent)
                        finish()
                    }
                } else if (inAppUpdateTs) {

                    checkAppUpdate()
                } else {
                    checkAppUpdate()
                }
            }
            bottomSheetDialoge.show()
        } catch (e: Exception) {
        }
    }


    private fun checkAppUpdate() {
        val appUpdateInfo = appUpdateManager?.appUpdateInfo
        appUpdateInfo?.addOnSuccessListener {
            handleUpdate(appUpdateManager, appUpdateInfo)
        }
    }

    private fun handleUpdate(manager: AppUpdateManager?, info: Task<AppUpdateInfo>) {

        when (updateType) {
            AppUpdateType.IMMEDIATE -> handleImmediateUpdate(manager, info)
            AppUpdateType.FLEXIBLE -> handleFlexibleUpdate(manager, info)
            else -> throw Exception("Unexpected error")
        }
    }

    private fun handleFlexibleUpdate(
        appUpdateManager: AppUpdateManager?, info: Task<AppUpdateInfo>
    ) {
        if ((info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE || info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) && info.result.isUpdateTypeAllowed(
                AppUpdateType.FLEXIBLE
            )
        ) {
            setUpdateAction(appUpdateManager, info)
        }
    }

    private fun handleImmediateUpdate(
        appUpdateManager: AppUpdateManager?, info: Task<AppUpdateInfo>
    ) {
        if ((info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE || info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) && info.result.isUpdateTypeAllowed(
                AppUpdateType.IMMEDIATE
            )
        ) {
            appUpdateManager?.startUpdateFlowForResult(
                info.result, AppUpdateType.IMMEDIATE, this, IN_APP_UPDATE_REQUEST_CODE
            )
            setUpdateAction(appUpdateManager, info)

        }
    }

    private fun setUpdateAction(manager: AppUpdateManager?, info: Task<AppUpdateInfo>) {
        // Before starting an update, register a listener for updates.
        installStateUpdatedListener = InstallStateUpdatedListener {
            when (it.installStatus()) {
                InstallStatus.FAILED, InstallStatus.UNKNOWN -> {
                    //Timber.d("inAppUpdateTSStatus - info_failed ")
                }

                InstallStatus.PENDING -> {
                    //Timber.d("inAppUpdateTSStatus - info_pending ")
                }

                InstallStatus.CANCELED -> {
                    //Timber.d("inAppUpdateTSStatus - info_canceled ")
                }

                InstallStatus.DOWNLOADING -> {
                    //Timber.d("inAppUpdateTSStatus - info_downloading ")
                }

                InstallStatus.DOWNLOADED -> {
                    //Timber.d("inAppUpdateTSStatus - info_downloaded ")
                    popupSnackbarForCompleteUpdate(manager)

                }

                InstallStatus.INSTALLING -> {
                    //Timber.d("inAppUpdateTSStatus - info_installing ")
                }

                InstallStatus.INSTALLED -> {
                    //Timber.d("inAppUpdateTSStatus - info_installed ")
                    if (::installStateUpdatedListener.isInitialized) {
                        manager?.unregisterListener(installStateUpdatedListener)
                    }
                }

                else -> {
                    //Timber.d("inAppUpdateTSStatus - info_restart ")
                }
            }
        }
        if (::installStateUpdatedListener.isInitialized) {
            manager?.registerListener(installStateUpdatedListener)
            manager?.startUpdateFlowForResult(
                info.result, AppUpdateType.FLEXIBLE, this, IN_APP_UPDATE_REQUEST_CODE
            )
        }
    }


    /** This is needed to handle the result of the manager.startConfirmationDialogForResult request */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IN_APP_UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                toast("Update flow failed!")
                // If the update is cancelled or fails,
                // you can request to start the update again.
//                checkAppUpdate()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == LAUNCH_MAIN_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {

                userViewModel.deleteUserAndRestartActivity(currentUser.toUserModel())
                PreferenceUtils.removeKey(PREF_TRAVEL_DATE)
                removeSourceDest()

                openDomainActivity()
            }
        }
    }

    /* Displays the snackbar notification and call to action. */
    private fun popupSnackbarForCompleteUpdate(appUpdateManager: AppUpdateManager?) {
        appUpdateManager?.completeUpdate()
        val intent = Intent(this@DashboardNavigateActivity, SplashScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()

    }

    override fun onResume() {
        super.onResume()



        setDateLocale(PreferenceUtils.getlang(), this@DashboardNavigateActivity)
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener {

            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate(appUpdateManager)
            } else if (it.installStatus() == InstallStatus.INSTALLED) {
                popupSnackbarForCompleteUpdate(appUpdateManager)
            }
            // If the update is downloaded but not installed,
            // notify the user to complete the update.

            if (it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                if (::installStateUpdatedListener.isInitialized) {
                    appUpdateManager?.registerListener(installStateUpdatedListener)
                    // If an in-app update is already running, resume the update.
                    appUpdateManager?.startUpdateFlowForResult(
                        it, AppUpdateType.IMMEDIATE, this, IN_APP_UPDATE_REQUEST_CODE
                    )
                }
            }

        }
    }

    private fun getCurrentUserObserver() {
        userViewModel.getCurrentUser.observe(this) {
            if (it == null) {
                openDomainActivity()
            } else {
                currentUser = it.toLoginModel()

                domain = currentUser.domainName
                username = currentUser.userName
                password = currentUser.password

                updateBaseURL(domain)
                setLoggedInUserNavDrawerData(it)

                callPrivilegeDetailsApi()
            }
        }
    }

    private fun getAllUserObserver() {
        userViewModel.getAllUsers.observe(this) {

            if (it.isEmpty()) {
                openDomainActivity()
            } else {
                userList = it
                openSwitchUserDialog(userList.toMutableList())
            }
        }
    }

    private fun setLoggedInUserNavDrawerData(user: User) {
        binding.navTextUserName.text = user.name
        binding.tvPosition.text = user.role
        binding.tvTravelsName.text = user.travelsName
        if (!user.name.isNullOrEmpty()) {
            binding.tvShortTitle.text =
                user.name?.uppercase(Locale.getDefault())?.let { getSubString(it, 0, 2) }
        }
        setOperatorLogo(user.logoUrl)

        if (user.shiftName.isNullOrEmpty()) {
            binding.tvShiftName.gone()
            binding.tvCloseCounter.gone()
            binding.closeCounterIV.gone()
        } else {
            binding.tvShiftName.text = "Shift Name: ${user.shiftName}"
            binding.tvCloseCounter.visible()
            binding.closeCounterIV.visible()
        }

        if (user.counterName.isNullOrEmpty()) {
            binding.tvCounterName.gone()
        } else {
            binding.tvCounterName.text = "Counter Name: ${user.counterName}"
        }






        PreferenceUtils.putObject(user.toLoginModel(), PREF_LOGGED_IN_USER)

    }

    private fun restartActivity() {
//            PreferenceUtils.setPreference(PREF_IS_ENCRYPTED, currentUser.isEncryptionEnabled)
        val intent = Intent(this, DashboardNavigateActivity::class.java)
        startActivity(intent)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        finish()
    }

    private fun openDomainActivity() {
        PreferenceUtils.putObject(null, PREF_LOGGED_IN_USER)
        PreferenceUtils.removeKey(PREF_TRAVEL_DATE)
        clearAndSave(this)
        PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
        val intent = Intent(this, DomainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun callResetApi(userN: String, pass: String) {
        if (isNetworkAvailable()) {
            if (binding.clCounter.isVisible) {

                if (selectedShiftId == null) {
                    toast(getString(R.string.pleaseSelectShift))
                    return
                }

                if (selectedCounterId == null) {
                    toast(getString(R.string.pleaseSelectCounter))
                    return
                }

                if (isNetworkAvailable()) {
                    dashboardViewModel.resetApi(
                        username,
                        password,
                        getDeviceUniqueId(this),
                        selectedShiftId,
                        selectedCounterId,
                        binding.includeCounter.etCounterBalance.text.toString().toDouble()
                    )
                } else {
                    noNetworkToast()
                }
            } else {
                if (isNetworkAvailable()) {
                    dashboardViewModel.resetApi(
                        userN, pass, getDeviceUniqueId(this)
                    )
                } else {
                    noNetworkToast()
                }
            }


        } else {
            noNetworkToast()
        }
    }

    private fun openResetDialog() {
        DialogUtils.twoButtonDialog(this,
            getString(R.string.use_here),
            getString(R.string.already_logged_in),
            getString(R.string.cancel),
            getString(R.string.use_here2),
            object : DialogButtonListener {
                override fun onLeftButtonClick() {
                }

                override fun onRightButtonClick() {
                    closeKeyBoard()
                    isReLoginClick = true
                    callResetApi(username, password)
                }
            })
    }

    private fun openSwitchUserDialog(userList: List<User>) {
        dashboardViewModel.isResetUserCall = false
        DialogUtils.switchUserDialog(this, userList, onItemClick = {
            openCloseNavigationDrawer()
            if (userList.size > 1) {
                username = it.username ?: ""
                password = it.password
                domain = it.domainName

                lastCounterUserName = username
                lastCounterPassword = password
                lastCounterDomain= domain
                PreferenceUtils.setPreference(PREF_IS_ENCRYPTED, it.isEncryptionEnabled)

                updateBaseURL(domain)
                dashboardViewModel.isResetUserCall = true
                callDomainApi()
//                callResetApi(username, password)
            }
        }, onAddAccountClick = {
            openCloseNavigationDrawer()
            binding.layoutAddUserData.root.visible()
            binding.layoutAddUserData.etDomain.setText(
                domain
            )
        })
    }

    private fun restartActivityObserver() {
        userViewModel.restartActivity.observe(this) {
            if (it) {
                restartActivity()
            } else {
                toast(getString(R.string.opps))
            }
        }
    }

    private fun openDeviceRegistrationDialog(message: String) {
        DialogUtils.deviceRegistrationDialog(this,
            message,
            true,
            deviceId = getDeviceUniqueId(this),
            object : DialogSingleButtonListener {
                override fun onSingleButtonClick(str: String) {
                    isReLoginClick = true
                    domain = binding.layoutAddUserData.etDomain.text.toString()
                    username = binding.layoutAddUserData.etUsername.text.toString()
                    password = binding.layoutAddUserData.etPassword.text.toString()

                    dashboardViewModel.validation(domain, username, password)
                }

            })
    }


    private fun setNavDrawerListener() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {

                firebaseLogEvent(
                    this@DashboardNavigateActivity,
                    MAIN_MENU,
                    currentUser.userName,
                    currentUser.travels_name,
                    currentUser.role,
                    MAIN_MENU,
                    HAMBURGER_MENU_CLICK
                )
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })

    }

    override fun onLocationIntervalFetchListener(interval: String?) {
        if (!interval.isNullOrEmpty()) {
            PreferenceUtils.setLocationApiInterval(interval)
        }
    }


    private fun callCreditInfoApi() {
        if (isNetworkAvailable()) {
            dashboardViewModel.fetchCreditInfo(
                apiKey = currentUser.api_key
            )
        } else {
            noNetworkToast()
        }
    }


    private fun hideCounterLogin() {
        binding.clCounter.gone()
        binding.layoutAddUserData.root.visible()
        selectedShiftId = null
        selectedCounterId = null
        binding.includeCounter.acSelectShift.setText("")
        binding.includeCounter.acSelectCounter.setText("")
    }

    private fun showCounterLogin(loginModel: LoginModel) {
        binding.clCounter.visible()
        binding.includeCounter.llCounter.visible()
        binding.layoutAddUserData.root.gone()
        if (loginModel.shift_list?.isNotEmpty() == true) {
            binding.includeCounter.acSelectShift.setAdapter(
                ArrayAdapter(
                    this,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    loginModel.shift_list
                )
            )
            binding.includeCounter.acSelectShift.setOnItemClickListener { parent, view, position, id ->
                selectedShiftId = loginModel.shift_list.get(position).id
            }
        }
        if (loginModel.counter_list?.isNotEmpty() == true) {
            binding.includeCounter.acSelectCounter.setAdapter(
                ArrayAdapter(
                    this,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    loginModel.counter_list
                )
            )
            binding.includeCounter.acSelectCounter.setOnItemClickListener { parent, view, position, id ->
                selectedCounterId = loginModel.counter_list.get(position).id
            }
        }
    }
}

