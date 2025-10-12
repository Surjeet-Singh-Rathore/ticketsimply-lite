package com.bitla.ts.presentation.view.activity.reservationOption

import com.bitla.ts.R
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.BuildConfig
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.available_routes_method_name
import com.bitla.ts.data.bulk_cancellation_method_name
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.FragmentDataListener
import com.bitla.ts.data.listener.VarArgListener
import com.bitla.ts.data.operator_api_key
import com.bitla.ts.data.response_format
import com.bitla.ts.databinding.ActivityViewReservationBinding
import com.bitla.ts.domain.pojo.available_routes.BoardingPointDetail
import com.bitla.ts.domain.pojo.available_routes.DropOffDetail
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.update_trip_status.UpdateTripReqBody
import com.bitla.ts.domain.pojo.view_reservation.CitySeqOrder
import com.bitla.ts.presentation.adapter.ViewReservationAdapter
import com.bitla.ts.presentation.view.activity.BusTrackingActivity
import com.bitla.ts.presentation.view.activity.InterBDActivity
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.activity.NewCoachActivity
import com.bitla.ts.presentation.view.activity.ViewPdfActivity
import com.bitla.ts.presentation.view.activity.ticketDetails.BookingSummaryActivity
import com.bitla.ts.presentation.view.activity.ticketDetails.StagingSummaryActivity
import com.bitla.ts.presentation.view.dashboard.DashboardNavigateActivity
import com.bitla.ts.presentation.viewModel.AvailableRoutesViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.FileDownloader
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.getUserRole
import com.bitla.ts.utils.common.printPDF
import com.bitla.ts.utils.common.sharePdf
import com.bitla.ts.utils.constants.BULK_CANCEL_TAB_CLICKS
import com.bitla.ts.utils.constants.BulkCancelTabClicks
import com.bitla.ts.utils.constants.COLLECTION_TAB_CLICKS
import com.bitla.ts.utils.constants.CollectionTabClicks
import com.bitla.ts.utils.constants.DOWNLOAD_ICON
import com.bitla.ts.utils.constants.DownloadIcon
import com.bitla.ts.utils.constants.ETA_CLICK
import com.bitla.ts.utils.constants.EtaClick
import com.bitla.ts.utils.constants.PICKUP_CHART_BOOK
import com.bitla.ts.utils.constants.PickUpChartBook
import com.bitla.ts.utils.constants.STORAGE_PERMISSION_CODE
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.downloadPdf.DownloadPdf
import com.bitla.ts.utils.sharedPref.PREF_DESTINATION
import com.bitla.ts.utils.sharedPref.PREF_DESTINATION_ID
import com.bitla.ts.utils.sharedPref.PREF_IS_APPLY_BPDP_FARE
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_NEW_BUS_LOCATION_ADDED_POPUP_DISPLAYED
import com.bitla.ts.utils.sharedPref.PREF_PICKUP_DROPOFF_CHARGES_ENABLED
import com.bitla.ts.utils.sharedPref.PREF_RESERVATION_ID
import com.bitla.ts.utils.sharedPref.PREF_SELECTED_AVAILABLE_ROUTES
import com.bitla.ts.utils.sharedPref.PREF_SOURCE
import com.bitla.ts.utils.sharedPref.PREF_SOURCE_ID
import com.bitla.ts.utils.sharedPref.PREF_UPDATE_COACH
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.sharedPref.SELECTED_BOARDING_DETAIL
import com.bitla.ts.utils.sharedPref.SELECTED_DROPPING_DETAIL
import com.bitla.ts.utils.showToast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.showAlignBottom
import gone
import isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.io.File
import com.bitla.ts.presentation.view.activity.reservationOption.CurrentLocationActivity



class ViewReservationActivity : BaseActivity(), DialogSingleButtonListener, VarArgListener,
    FragmentDataListener {
    companion object {
        val tag: String = ViewReservationActivity::class.java.simpleName
    }

    private var isTripComplete: String? = ""
    private var isAgentLogin: Boolean = false
    private var isAllowBooking: Boolean? = false
    private lateinit var binding: ActivityViewReservationBinding
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private val availableRoutesViewModel by viewModel<AvailableRoutesViewModel<Any?>>()
    private var bccId: Int? = 0
    private var serviceData: String? = null
    private var serviceName: String? = null
    private var serviceNumber: String? = null
    private var coachNumber: String? = null
    private var resId: Long? = null
    private var travelDate: String? = null
    private var loginModelPref: LoginModel = LoginModel()
    private var myDownload: Long = 0
    private var url: String? = null
    private var tabPosition = 0
    private var locale: String? = ""
    private val sourceList = mutableListOf<CitySeqOrder>()
    private val destinationList = mutableListOf<CitySeqOrder>()
    private val showOnlyAvailableServices: String = "true" //fixed
    private val showInJourneyServices: String = "true" // fixed
    private var srcDestDialog: AlertDialog? = null
    private var pdfFile: File? = null
    private var isWifiPrintClick: Boolean = false
    private var privilegeResponse: PrivilegeResponseModel? = null
    private var country = ""
    private var routeId = ""
    private var deptTime = ""
    private var auditType: String = ""
    private var tripSheetCollectionOptionsInTSAppReservationChart: Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickUpchartPDFObserver()
        setUpdateTripStatusObserver()
        getBusLocationAddedPopup()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun isInternetOnCallApisAndInitUI() {
        try {
            // Remove this check that references binding before it's initialized
            // binding?.updateRatecardToolbar?.imageOptionLayout?.visible()
            getPref()
            tabPosition = PreferenceUtils.getPreference("shiftPassenger_tab", 0) ?: 0
            PreferenceUtils.removeKey(PREF_PICKUP_DROPOFF_CHARGES_ENABLED)
            initUI()
            // Move this to after binding is initialized
            binding.updateRatecardToolbar.busEta.visible()
            binding.updateRatecardToolbar.imageOptionLayout.visible()
        } catch (e: Exception) {
            Timber.e(e, "Error in isInternetOnCallApisAndInitUI")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initUI() {
        binding = ActivityViewReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        if (this.isNetworkAvailable()) {
            binding.updateRatecardToolbar.imageOptionLayout.visible()
            getPref()
            tabPosition = PreferenceUtils.getPreference("shiftPassenger_tab", 0)!!
            initTab()
            binding.updateRatecardToolbar.busEta.visible()

            binding.updateRatecardToolbar.busEta.setOnClickListener {
                gotoBusTrackingPage()
            }



        } else {
            this.noNetworkToast()
        }

        lifecycleScope.launch {
            availableRoutesViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
    }

    private fun gotoBusTrackingPage() {
        if (privilegeResponse?.country.equals("India", ignoreCase = true)) {
            val intent = Intent(this, CurrentLocationActivity::class.java)
            startActivity(intent)
        } else {
            PreferenceUtils.setPreference(PREF_RESERVATION_ID, resId)
            // val intent = Intent(this, CurrentLocationActivity::class.java)
            val intent = Intent(this, BusTrackingActivity::class.java)
            intent.putExtra(
                "toolbarSubHeader",
                "$serviceName"
            )
            intent.putExtra(
                "coachNumber",
                "$coachNumber"
            )
            intent.putExtra(
                "serviceNumber",
                "$serviceNumber"
            )

            intent.putExtra(
                "routeId",
                routeId
            )

            intent.putExtra(
                "deptDateTime",
                deptTime
            )

            startActivity(intent)

            firebaseLogEvent(
                this,
                ETA_CLICK,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                ETA_CLICK,
                EtaClick.ETA_OPTIONS_CLICKS_VIEW_RESERVATION
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initTab() {
        val tabsList: MutableList<Tabs> = mutableListOf()

        val tabpassengerlist = Tabs()
        tabpassengerlist.title = getString(R.string.passenger_list)


        tabsList.add(tabpassengerlist)


        //binding!!.viewpagerPickup.postDelayed({ binding!!.viewpagerPickup.setCurrentItem(0, true) }, 100)


        if (getPrivilegeBase() != null) {
            privilegeResponse = getPrivilegeBase()
            privilegeResponse?.let {

                if (privilegeResponse?.allowBulkCancellation == true) {

                    val tabbulk = Tabs()
                    tabbulk.title = getString(R.string.bulk_cancel)

                    tabsList.add(tabbulk)
                }
                if (privilegeResponse?.allowBulkShifting == true) {
                    val tabshift = Tabs()
                    tabshift.title = getString(R.string.shift_passengers)

                    tabsList.add(tabshift)
                }
                if (privilegeResponse?.viewCollectionChart == true) {
                    val tabcollection = Tabs()
                    tabcollection.title = getString(R.string.collection)
                    tabsList.add(tabcollection)

                }

                isAllowBooking = privilegeResponse?.allowBooking
            }
        } else {
            toast(getString(R.string.server_error))
        }



        if (tabsList.size > 2) {
            binding.tabsViewReservation.tabMode = TabLayout.MODE_SCROLLABLE
        }


        val fragmentAdapter = ViewReservationAdapter(this, tabsList, country, privilegeResponse?.tsPrivileges?.groupByPnrPickupChart ?: false, tripSheetCollectionOptionsInTSAppReservationChart, this)
        binding.viewpagerPickup.adapter = fragmentAdapter
        if (binding.viewpagerPickup.adapter != null) {
            TabLayoutMediator(
                binding.tabsViewReservation,
                binding.viewpagerPickup
            ) { tab, position ->
                // Customize tab labels if needed
                tab.text = when (tabsList[position].title) {
                    getString(R.string.passenger_list) -> {
                        getString(R.string.passenger_list)
                    }

                    getString(R.string.bulk_cancel) -> {
                        getString(R.string.bulk_cancel)
                    }

                    getString(R.string.shift_passengers) -> {
                        getString(R.string.shift_passengers)
                    }

                    getString(R.string.collection) -> {
                        if (tripSheetCollectionOptionsInTSAppReservationChart == true && country.equals(
                                "india",
                                true
                            )
                        ) {
                            getString(R.string.trip_sheet_collection)
                        } else {
                            getString(R.string.collection)
                        }
                    }

                    else -> {
                        getString(R.string.passenger_list)
                    }
                }
                val drawableResId = when (tabsList[position].title) {
                    getString(R.string.passenger_list) -> R.drawable.ic_pickup_list_dashboard
                    getString(R.string.bulk_cancel) -> R.drawable.ic_pickup_cancel_dashboard
                    getString(R.string.shift_passengers) -> R.drawable.ic_pickup_shift_passenger_dashboard
                    getString(R.string.collection) -> R.drawable.ic_pickup_collection_dashboard
                    else -> 0
                }
                tab.icon = if (drawableResId != 0) {
                    AppCompatResources.getDrawable(this, drawableResId)
                } else {
                    null
                }
            }.attach()
        }else {
            Timber.e("ViewPager adapter is null, cannot attach TabLayoutMediator")
            return
        }
//        binding.viewpagerPickup.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabsViewReservation))
        binding.updateRatecardToolbar.headerTitleDesc.text = serviceData
        binding.updateRatecardToolbar.textHeaderTitle.text = serviceName
        binding.updateRatecardToolbar.imgDownload.visible()
        binding.updateRatecardToolbar.imgDownload.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_dots_grey
            )
        )
        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }

        binding.updateRatecardToolbar.imgDownload.setOnClickListener {
            firebaseLogEvent(
                context = this,
                logEventName = DOWNLOAD_ICON,
                loginId = loginModelPref.userName,
                operatorName = loginModelPref.travels_name,
                roleName = loginModelPref.role,
                eventKey = DOWNLOAD_ICON,
                eventValue = DownloadIcon.DOWNLOAD_ICON
            )
            /*checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val intent = Intent(this,ViewPdfActivity::class.java)
            intent.putExtra("service_name",serviceName)
            intent.putExtra("service_data",serviceData)
            startActivity(intent)*/
            showPopupMenu()
        }

        binding.updateRatecardToolbar.imgBook.setOnClickListener {
        }

        if (tabPosition == 2) {

            binding.viewpagerPickup.currentItem = 2

        }


        val tabStrip: LinearLayout = binding.tabsViewReservation.getChildAt(0) as LinearLayout

        for (i in 0 until tabStrip.childCount) {

            if (i == 1) {
                tabStrip.getChildAt(i).setOnClickListener {

                    firebaseLogEvent(
                        this,
                        BULK_CANCEL_TAB_CLICKS,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        BULK_CANCEL_TAB_CLICKS,
                        BulkCancelTabClicks.VIEW_RESERVATION
                    )
                }
            } else if (i == 3) {
                tabStrip.getChildAt(i).setOnClickListener {

                    firebaseLogEvent(
                        this,
                        COLLECTION_TAB_CLICKS,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        COLLECTION_TAB_CLICKS,
                        CollectionTabClicks.VIEW_RESERVATION
                    )
                }
            }

        }

        binding.viewpagerPickup.currentItem = 0

        // Apply initial styles
        binding.tabsViewReservation.post {
            val initialTab = binding.tabsViewReservation.getTabAt(binding.viewpagerPickup.currentItem)
            initialTab?.let { tab ->
                val tabView = tab.customView
                val textView = tabView?.findViewById<TextView>(R.id.tvTab)
                val iconView = tabView?.findViewById<ImageView>(R.id.ivTabIcon)

                textView?.setTypeface(null, Typeface.BOLD)
                textView?.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                if (iconView != null) {
                    setIconColor(iconView, true)
                }
            }
        }

        for (i in 0..binding.tabsViewReservation.tabCount.minus(1)) {
            val tab = binding.tabsViewReservation.getTabAt(i)!!

            val customTabView = LayoutInflater.from(this).inflate(R.layout.slide_tab, null)

            val tabTextView = customTabView.findViewById<TextView>(R.id.tvTab)
            val tabIconView = customTabView.findViewById<ImageView>(R.id.ivTabIcon)

            tabTextView.text = tab.text

            if (country.equals("india", ignoreCase = true)) {
                val drawableResId = when (i) {
                    0 -> R.drawable.ic_pickup_list_dashboard
                    1 -> R.drawable.ic_pickup_cancel_dashboard
                    2 -> R.drawable.ic_pickup_shift_passenger_dashboard
                    3 -> R.drawable.ic_pickup_collection_dashboard
                    else -> 0
                }
                if (drawableResId != 0) {
                    tabIconView.setImageResource(drawableResId)
                    tabIconView.visible()
                } else {
                    tabIconView.gone()
                }
            } else {
                tabIconView.gone()
            }

            tab.customView = customTabView

            if (tabPosition == 2) {
                if (i == 2) {
                    binding.updateRatecardToolbar.imgDownload.gone()
                    tabTextView.setTypeface(null, Typeface.BOLD)
                }
            } else {
                binding.updateRatecardToolbar.imgDownload.visible()
                if (i == 0) {
                    tabTextView.setTypeface(null, Typeface.BOLD)
                }
            }
        }
        binding.tabsViewReservation.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                binding.viewpagerPickup.currentItem = tab?.position ?: 0

                for (i in 0 until binding.tabsViewReservation.tabCount) {
                    val tabView = binding.tabsViewReservation.getTabAt(i)?.customView
                    val icon = tabView?.findViewById<ImageView>(R.id.ivTabIcon)
                    if (icon != null) {
                        setIconColor(icon, tab?.position == i)
                    }
                }

                binding.updateRatecardToolbar.imgDownload.apply {
                    when (tab?.position) {
                        2 -> gone()
                        0 -> visible()
                        else -> gone()
                    }
                }

                val tabView = tab?.customView
                val text: TextView? = tabView?.findViewById(R.id.tvTab)
                text?.setTypeface(null, Typeface.BOLD)
                val color = ContextCompat.getColor(this@ViewReservationActivity, R.color.colorPrimary)
                text?.setTextColor(color)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val tabView = tab?.customView
                val icon = tabView?.findViewById<ImageView>(R.id.ivTabIcon)
                setIconColor(icon!!, false)
                val text: TextView? = tabView.findViewById(R.id.tvTab)
                text?.setTypeface(null, Typeface.NORMAL)
                val color = ContextCompat.getColor(this@ViewReservationActivity, R.color.gray_shade_a)
                text?.setTextColor(color)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun setIconColor(imageView: ImageView, isSelected: Boolean) {
        val color = if (isSelected) {
            ContextCompat.getColor(this, R.color.colorPrimary) // Replace with your primary blue color
        } else {
            ContextCompat.getColor(this, R.color.gray_shade_a) // Replace with your grey color
        }
        imageView.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    private fun checkPermission(permission: String) {
        val permissionResult = DownloadPdf.checkPermission(permission, this)
        if (Build.VERSION.SDK_INT >= 33) {
            pickUpchartPDFapi()
            binding.includeProgress.progressBar.visible()
        } else {
            if (permissionResult) {
                pickUpchartPDFapi()
                binding.includeProgress.progressBar.visible()
            } else {
                DownloadPdf.onRequestPermissionsResult(STORAGE_PERMISSION_CODE, permission, this)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showPopupMenu() {
        val popup = PopupMenu(this, binding.updateRatecardToolbar.imgDownload)
        popup.inflate(R.menu.custom_menu)
        popup.gravity = Gravity.RIGHT
        if (Build.VERSION.SDK_INT > 28) {
            popup.setForceShowIcon(true)
        }
        popup.menu.getItem(3).isVisible = isAllowBooking != null && isAllowBooking!!
        /* hide the visibility of booking summary & stage summary for build_6.12
        popup.menu.getItem(1).isVisible = privilegeResponse?.country.equals("India", true)
        popup.menu.getItem(2).isVisible = privilegeResponse?.country.equals("India", true)*/
        popup.menu.getItem(1).isVisible = false
        popup.menu.getItem(2).isVisible = false

        isTripComplete = PreferenceUtils.getString("is_trip_complete")
        // var isTripComplete = "completed"


        if (!isAgentLogin && privilegeResponse?.country.equals("India", ignoreCase = true)
            && (isTripComplete.equals("pending",true) || isTripComplete.equals("completed",true))
        ) {
            popup.menu.getItem(5).isVisible = true
            if(isTripComplete.equals("completed",true)){
                val s = popup.menu.getItem(5).title.toString()
                val spannableString = androidx.core.text.buildSpannedString {
                    append(s, android.text.style.ForegroundColorSpan(Color.LTGRAY), 0)
                }
                val itemToChange = popup.menu.findItem(R.id.newTripCompletedUi) // Find the specific MenuItem
                itemToChange.icon = ContextCompat.getDrawable(this, R.drawable.ic_trip_complete_grey)
                popup.menu.getItem(5).title = spannableString
            }else{
                val s = popup.menu.getItem(5).title.toString()
                val spannableString = androidx.core.text.buildSpannedString {
                    append(s, android.text.style.ForegroundColorSpan(Color.BLACK), 0)
                }
                val itemToChange = popup.menu.findItem(R.id.newTripCompletedUi) // Find the specific MenuItem
                itemToChange.icon = ContextCompat.getDrawable(this, R.drawable.ic_trip_complete)
                popup.menu.getItem(5).title = spannableString
            }
        }else{
            popup.menu.getItem(5).isVisible = false

        }
        // Set a listener for menu item clicks
        popup.setOnMenuItemClickListener { item -> onMenuItemClick(item) }
        // Show the PopupMenu
        popup.show()
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.downloadMI -> {
                // Handle Option 1 click
                // Implement your logic here
                auditType = getString(R.string.download_pickup_chart_text)
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                isWifiPrintClick = false
            }

            R.id.newTripCompletedUi -> {
                if(isTripComplete.equals("pending",true)){
                    showAlertDialog()
                }
            }

            R.id.viewTicketMI -> {
                // Handle Option 2 click
                // Implement your logic here
                auditType = getString(R.string.empty)
                val intent = Intent(this, ViewPdfActivity::class.java)
                intent.putExtra("service_data", serviceData)
                PreferenceUtils.setPreference(PREF_RESERVATION_ID, resId)
                startActivity(intent)
            }
            R.id.bookingSummary -> {
                // Handle Option 2 click
                // Implement your logic here
                val intent = Intent(this, BookingSummaryActivity::class.java)
                // intent.putExtra("service_data",serviceData)
                startActivity(intent)
            }
            R.id.stageSummary -> {
                // Handle Option 2 click
                // Implement your logic here
                val intent = Intent(this, StagingSummaryActivity::class.java)
                // intent.putExtra("service_data",serviceData)
                startActivity(intent)
            }
           /* R.id.wifiPrint -> {
                isWifiPrintClick = true
                auditType = getString(R.string.wifi_print_text)
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }*/

            R.id.newBookingMI -> {
                firebaseLogEvent(
                    context = this,
                    logEventName = PICKUP_CHART_BOOK,
                    loginId = loginModelPref.userName,
                    operatorName = loginModelPref.travels_name,
                    roleName = loginModelPref.role,
                    eventKey = PICKUP_CHART_BOOK,
                    eventValue = PickUpChartBook.PICKUP_CHART_BOOK
                )

                if (isAgentLogin && privilegeResponse?.allowBookingForAllotedServices == true) {
                    intent = Intent(this, DashboardNavigateActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("newBooking", true)
                    startActivity(intent)
                    finish()
                } else {
                    sourceList.clear()
                    destinationList.clear()

                    if (!PreferenceUtils.getCitySeqOrder().isNullOrEmpty()) {
                        val citySeqOrder = PreferenceUtils.getCitySeqOrder()

                        citySeqOrder?.forEach {
                            if (it.isSource)
                                sourceList.add(it)

                            if (it.isDestination)
                                destinationList.add(it)
                        }
                    }

                    if (sourceList.isNotEmpty() && destinationList.isNotEmpty())
                        srcDestDialog =
                            DialogUtils.dialogSrcDestSelection(
                                sourceList,
                                destinationList,
                                this,
                                this
                            )
                }
            }

            // Handle other menu item clicks if needed
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceUtils.removeKey("is_trip_complete")
    }

    private fun setUpdateTripStatusObserver(){
        pickUpChartViewModel.updateTripStatusData.observe(this){
            if(it != null){
                when(it.code){
                    200 ->{
                        if(it.message != null){
                            toast(it.message)
                            PreferenceUtils.putString("is_trip_complete","completed")
                        }
                    }
                    411 -> {
                        showUnauthorisedDialog()
                    }
                    else -> {
                        toast(getString(R.string.server_error))
                    }

                }
            }
        }
    }

    private fun showAlertDialog() {
        // Step 1: Create an AlertDialog Builder
        val builder = AlertDialog.Builder(this)

        // Step 2: Set the title and message for the dialog
        builder.setTitle(getString(R.string.trip_complete_))
        builder.setMessage(getString(R.string.do_you_want_to_complete_this_trip))

        // Step 3: Set Positive and Negative buttons (Optional)
        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            // Handle the positive button click
            val reqBody = UpdateTripReqBody(loginModelPref.api_key,resId.toString())
            pickUpChartViewModel.updateTripStatusApi(reqBody)
            dialog.dismiss() // Dismiss the dialog
        }

        builder.setNegativeButton(getString(R.string.go_back)) { dialog, _ ->
            // Handle the negative button click
            dialog.dismiss() // Dismiss the dialog
        }

        // Step 4: Create and Show the dialog
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        serviceData = PreferenceUtils.getString("ViewReservation_data")
        serviceName = PreferenceUtils.getString("ViewReservation_name")
        serviceNumber = PreferenceUtils.getString("ViewReservation_number")
        coachNumber = PreferenceUtils.getString("ViewReservation_coach_number")
        travelDate = PreferenceUtils.getString("ViewReservation_date")
//        resId = PreferenceUtils.getString("reservationid")
        resId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
        val role = getUserRole(loginModelPref, privilegeResponse?.isAgentLogin ?: false, this)
        isAgentLogin = role == getString(R.string.agent)

        country = getPrivilegeBase()?.country ?: ""
        tripSheetCollectionOptionsInTSAppReservationChart = if (role == getString(R.string.role_field_officer)) {
            getPrivilegeBase()?.boLicenses?.tripSheetCollectionOptionInTSAppReservationChart ?: false
        } else {
            getPrivilegeBase()?.tsPrivileges?.tripSheetCollectionOptionsInTSAppReservationChart
                ?: false
        }

        routeId = intent.getStringExtra("routeId").toString()
        deptTime = intent.getStringExtra("deptTime").toString()

    }

    private fun pickUpchartPDFapi() {
        if (!isWifiPrintClick) {
            toast(getString(R.string.start_downloading))
        }

        if (isNetworkAvailable()) {
            pickUpChartViewModel.pickUpChartPdfAPI(
                com.bitla.ts.domain.pojo.pickup_chart_pdf_url.request.ReqBody(
                    api_key = loginModelPref.api_key,
                    res_id = resId.toString(),
                    travel_date = travelDate ?: "",
                    locale = locale,
                    audit_type = auditType
                ),
                bulk_cancellation_method_name
            )
        } else
            noNetworkToast()
    }

    private fun pickUpchartPDFObserver() {
        pickUpChartViewModel.pickUpChatPdfResponse.observe(this) {
            Timber.d("viewReservation $it")

            binding.includeProgress.progressBar.gone()

            if (it != null) {
                if (it.status == 200) {
                    if (isWifiPrintClick) {
                        preparingPrintDocuments(it.pdf_url)
//                        val intent = Intent(this, ViewAndPrintPdfActivity::class.java)
//                        startActivity(intent)
//                        finish()
                    } else {
                        DownloadPdf.downloadReportPdf(this, it.pdf_url)
                    }
                } else if (it.status == 401) {
                    /*DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    showUnauthorisedDialog()

                } else {
                    if (it?.result?.message != null) {
                        it.result?.message?.let { it1 -> toast(it1) }
                    }

                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        availableRoutesViewModel.dataAvailableRoutes.observe(this, Observer {
            binding.includeProgress.progressBar.gone()
            val dialogProgress = srcDestDialog?.findViewById<ProgressBar>(R.id.dialog_progress_bar)
            dialogProgress?.gone()

            try {
                if (it != null) {
                    if (it.code == 200) {
                        if (it.result.isNotEmpty()) {
                            val index = it.result.indexOfFirst { it.reservation_id == resId }
                            if (index != -1) {

                                val isApplyBpdpFare =
                                    PreferenceUtils.getPreference(PREF_IS_APPLY_BPDP_FARE, false)

                                if (isApplyBpdpFare != null && isApplyBpdpFare) {
                                    if (srcDestDialog != null)
                                        srcDestDialog!!.dismiss()
                                    val bpDpBoarding: MutableList<BoardingPointDetail> =
                                        it.result[index].boarding_point_details as MutableList<BoardingPointDetail>
                                    val bpDpDropping: MutableList<DropOffDetail> =
                                        it.result[index].drop_off_details as MutableList<DropOffDetail>
                                    val isPickupDropoffChargesEnabled = it.result[index].pickup_dropoff_charges_enabled

                                    if (bpDpBoarding.isEmpty() || bpDpDropping.isEmpty()) {
                                        val intent = Intent(this, NewCoachActivity::class.java)
                                        intent.putExtra(getString(R.string.navigate_tag), tag)
                                        startActivity(intent)
                                    } else {
                                        val busType = it.result[index].bus_type
                                        val depTime = it.result[index].dep_time
                                        val serviceNumber = it.result[index].number
                                        PreferenceUtils.putBoarding(bpDpBoarding)
                                        PreferenceUtils.putDropping(bpDpDropping)

                                        PreferenceUtils.putObject(
                                            bpDpBoarding[0],
                                            SELECTED_BOARDING_DETAIL
                                        )
                                        PreferenceUtils.putObject(
                                            bpDpDropping[0],
                                            SELECTED_DROPPING_DETAIL
                                        )
                                        PreferenceUtils.setPreference(PREF_PICKUP_DROPOFF_CHARGES_ENABLED, isPickupDropoffChargesEnabled)

                                        if (srcDestDialog != null)
                                            srcDestDialog!!.dismiss()

                                        val intent = Intent(this, InterBDActivity::class.java)
                                        intent.putExtra(getString(R.string.bus_type), busType)
                                        intent.putExtra(getString(R.string.dep_time), depTime)
                                        intent.putExtra(
                                            getString(R.string.service_number),
                                            serviceNumber
                                        )
                                        intent.putExtra("PreSelectedDropping", "true")
                                        intent.putExtra("preSelectedBoarding", "true")
                                        intent.putExtra(
                                            getString(R.string.toolbar_title),
                                            "${getString(R.string.booking)}"
                                        )
                                        intent.putExtra(
                                            getString(R.string.service_type),
                                            getString(R.string.proceed)
                                        )
                                        intent.putExtra(getString(R.string.navigate_tag), tag)
                                        intent.putExtra("Previous Page", "ViewReservationChart")
                                        startActivity(intent)
                                    }
                                } else {
                                    PreferenceUtils.setPreference(PREF_UPDATE_COACH, true)

                                    val intent = Intent(this, NewCoachActivity::class.java)
                                    PreferenceUtils.setPreference(PREF_RESERVATION_ID, resId)
                                    intent.putExtra(getString(R.string.navigate_tag), tag)
                                    startActivity(intent)
                                }
                            } else
                                toast(getString(R.string.route_not_configured))
                        }
                    } else
                        toast(it.message)
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun preparingPrintDocuments(url: String) {
        val downloadUrl = url
        var fileName = url.substringAfterLast("/")
        if (!fileName.contains(".pdf")) {
            fileName += ".pdf"
        }

        val downloader =
            FileDownloader(cacheDir, object : FileDownloader.OnDownloadCompleteListener {
                override fun onDownloadComplete(file: File?) {
                    if (file != null) {
                       // printDocumentWiFi(this@ViewReservationActivity, file, fileName)
                        sharePdf(this@ViewReservationActivity,file)
                        pdfFile = file
                        binding.includeProgress.progressBar.gone()
                    } else {
                        toast(getString(R.string.error_loading_pdf_please_try_again))
                        binding.includeProgress.progressBar.gone()
                    }
                }
            })

        downloader.execute(downloadUrl, fileName)
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onButtonClick(vararg args: Any) {
        try {
            if (args.isNotEmpty()) {
                when (args[0]) {
                    getString(R.string.proceed) -> {
                        val selectedSrcPosition = args[1].toString().toInt()
                        val selectedDestPosition = args[2].toString().toInt()

                        // Add bounds checking to prevent IndexOutOfBoundsException
                        if (selectedSrcPosition >= 0 && selectedSrcPosition < sourceList.size &&
                            selectedDestPosition >= 0 && selectedDestPosition < destinationList.size) {

                            val srcId = sourceList[selectedSrcPosition].id
                            val destId = destinationList[selectedDestPosition].id

                            val src = sourceList[selectedSrcPosition].name.substringBefore("-").trim()
                            val dest = destinationList[selectedDestPosition].name.substringBefore("-").trim()

                            PreferenceUtils.putString(PREF_SOURCE_ID, srcId.toString())
                            PreferenceUtils.putString(PREF_DESTINATION_ID, destId.toString())
                            PreferenceUtils.putString(PREF_SOURCE, src)
                            PreferenceUtils.putString(PREF_DESTINATION, dest)

                            availableRoutesApi(srcId, destId)
                        } else {
                            Timber.e("Invalid list positions: src=$selectedSrcPosition, dest=$selectedDestPosition")
                            toast(getString(R.string.server_error))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in onButtonClick")
        }
    }

    private fun availableRoutesApi(srcId: Any, destId: Any) {
        binding.includeProgress.progressBar.visible()
        var isBima: Boolean? = null
        if (PreferenceUtils.getPreference("is_bima", false) == true) {
            isBima = true
        }
        var isCsShared: Boolean? = null
        if (isBima == true) {
            isCsShared = true
        }

        if (isNetworkAvailable()) {
            availableRoutesViewModel.availableRoutesApi(
                apiKey = loginModelPref.api_key,
                originId = srcId.toString(),
                destinationId = destId.toString(),
                showInJourneyServices = showInJourneyServices,
                isCsShared = isCsShared ?: false,
                operatorkey = operator_api_key,
                responseFormat = response_format,
                travelDate = travelDate.toString(),
                showOnlyAvalServices = showOnlyAvailableServices,
                locale = locale ?: "",
                apiType = available_routes_method_name,
                appBimaEnabled = false
            )
        } else
            noNetworkToast()
    }

    override fun onUpdateFragment(isBookVisible: Boolean) {
        // "isBookVisible" has been removed because it should be true for all the cases.
        //if (isAllowBooking != null && isAllowBooking!! && isBookVisible)
        /* if (isAllowBooking != null && isAllowBooking!!)
             binding.updateRatecardToolbar.imgBook.visible()
         else
             binding.updateRatecardToolbar.imgBook.gone()*/
    }


    private fun getBusLocationAddedPopup() {

        if (PreferenceUtils.getString(PREF_NEW_BUS_LOCATION_ADDED_POPUP_DISPLAYED) == "false"
            && country.equals(getString(R.string.indonesia), true)) {

            val balloon = Balloon.Builder(this)
                .setLayout(R.layout.popup_bus_location_added)
//            .setWidthRatio(1.0f)
                .setWidth(BalloonSizeSpec.WRAP)
                .setHeight(BalloonSizeSpec.WRAP)
//            .setTextColorResource(R.color.white)
//            .setTextSize(15f)
//            .setIconDrawableResource(R.drawable.ic_new_bus_location_added)
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setArrowSize(10)
                .setArrowPosition(0.5f)
                .setPadding(12)
                .setMargin(5)
                .setCornerRadius(8f)
//            .setBackgroundColorResource(R.color.black)
                .setBalloonAnimation(BalloonAnimation.ELASTIC)
                .setLifecycleOwner(this)
                .build()

            binding.updateRatecardToolbar.busEta.showAlignBottom(balloon)

            binding.updateRatecardToolbar.newBusLocationSmallLogo.visibility = View.VISIBLE

            PreferenceUtils.putString(PREF_NEW_BUS_LOCATION_ADDED_POPUP_DISPLAYED, "true")
        }
    }

}