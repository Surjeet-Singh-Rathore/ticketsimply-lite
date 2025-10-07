package com.bitla.ts.presentation.view.activity

import android.content.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.core.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.*
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.fare_breakup.request.ChargeDetails
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.reservationOption.*
import com.bitla.ts.presentation.view.fragments.boarding.bpDpBoarding.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.*
import com.google.android.material.tabs.*
import gone
import timber.log.*

class InterBDActivity : BaseActivity(), FragmentListener, DialogSingleButtonListener {

    private var selectedBoardingPoint: String? = null
    private var selectedDroppingPoint: String? = null
    private var selectedBoardingPointId: String? = null
    private var selectedDroppingPointId: String? = null
    private var source: String? = ""
    private var destination: String? = ""
    private var serviceNumber: String = ""
    private var busType: String? = null
    private var depTime: String? = null
    private var travelDate: String? = null
    private var toolbarTitle: String? = null
    private var serviceType: String? = null
    private var tabsList: MutableList<Tabs> = mutableListOf()
    private lateinit var fragmentAdapter: BpDpNewAdapter
    val tabBoarding = Tabs()
    val tabDropping = Tabs()
    private var boardingList = mutableListOf<BoardingPointDetail>()
    private var droppingList = mutableListOf<DropOffDetail>()
    private var boardingOrDropping: String? = null
    private var reservationID: Long? = 0L
    private var loginModelPref: LoginModel = LoginModel()
    private var bccId: Int? = 0
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var boardingPointName: String = ""
    private var droppingPointName: String = ""
    private var isPickupDropoffChargesEnabled : Boolean? = false
    private val pickupChargeDetails : ChargeDetails = ChargeDetails()
    private val dropoffChargeDetails : ChargeDetails = ChargeDetails()
    private lateinit var binding: ActivityInterBdactivityBinding
    private var preSelecteBoarding: String? = ""
    private var preSelectedDropping: String? = ""
    private var rapidTag: String? = ""
    private var navigateTag: String? = null
    private var sourceKey = ""
    private var previousPage = ""
    private var privilegeResponseModel: PrivilegeResponseModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d(" PreSelectedDropping: ")

        if (intent.hasExtra(getString(R.string.navigate_tag))) {
            navigateTag = intent.getStringExtra(getString(R.string.navigate_tag))
        }

        getPref()
        setToolbarTitle()

        tabBoarding.title = getString(R.string.boarding_point)
        tabBoarding.selectedPoint = selectedBoardingPoint
        tabBoarding.selectedPoint = boardingPointName
        tabsList.add(tabBoarding)
        tabDropping.title = getString(R.string.dropping_point)
        tabDropping.selectedPoint = selectedDroppingPoint
        tabDropping.selectedPoint = droppingPointName
        tabsList.add(tabDropping)
        initTab(false)
        onClickListener()
    }

    override fun isInternetOnCallApisAndInitUI() {
        binding.boardingToolbar.tvScan.gone()

        getPref()
        setToolbarTitle()
        initTab(false)
        onClickListener()
    }

    private fun onClickListener() {
        binding.boardingToolbar.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.img_back -> onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (navigateTag != null && navigateTag == ViewReservationActivity.tag) {
            val intent = Intent(this, ViewReservationActivity::class.java)
            intent.putExtra("pickUpResid", reservationID)
            startActivity(intent)
        }
    }

    override fun initUI() {
        binding = ActivityInterBdactivityBinding.inflate(layoutInflater)
        Timber.d(" PreSelectedDropping1: ")

        selectedBoardingPoint = getString(R.string.select_location)
        selectedDroppingPoint = getString(R.string.select_location)
        binding.boardingToolbar.tvScan.gone()
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
    }

    private fun getPref() {
        boardingList.clear()
        droppingList.clear()
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        privilegeResponseModel = getPrivilegeBase()
        source = PreferenceUtils.getSource()
        sourceId = PreferenceUtils.getSourceId()
        destinationId = PreferenceUtils.getDestinationId()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()
        reservationID = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
        if (intent.getStringExtra(getString(R.string.bus_type)) != null) {
            busType = intent.getStringExtra(getString(R.string.bus_type))
        }
        if (intent.getStringExtra(getString(R.string.service_number)) != null)
            serviceNumber = intent.getStringExtra(getString(R.string.service_number))!!
        if (intent.getStringExtra(getString(R.string.dep_time)) != null)
            depTime = intent.getStringExtra(getString(R.string.dep_time))
        if (intent.getStringExtra(getString(R.string.toolbar_title)) != null)
            toolbarTitle = intent.getStringExtra(getString(R.string.toolbar_title))
        if (intent.getStringExtra(getString(R.string.service_type)) != null)
            serviceType = intent.getStringExtra(getString(R.string.service_type))

        Timber.d(" PreSelectedDropping99: ${intent.getStringExtra(getString(R.string.tag))}")

        if (intent.getStringExtra("sourceKey") != null) {
            sourceKey = intent.getStringExtra("sourceKey") ?: ""
        }

        if (intent.getStringExtra("Previous Page") != null) {
            previousPage = intent.getStringExtra("Previous Page") ?: ""
        }
        if (intent.getStringExtra(getString(R.string.tag)) != null)
            rapidTag = intent.getStringExtra(getString(R.string.tag))

        if (intent.getStringExtra("preSelectedBoarding") != null) {
            preSelecteBoarding = intent.getStringExtra("preSelectedBoarding")
            PreferenceUtils.putString("preselectedBPData", preSelecteBoarding)
        }

        if (intent.getStringExtra("PreSelectedDropping") != null) {
            preSelectedDropping = intent.getStringExtra("PreSelectedDropping")
            PreferenceUtils.putString("preselectedDPData", preSelectedDropping)
        }
        Timber.d(" PreSelectedDropping:2 ${PreferenceUtils.getBoarding()}")
        boardingList = PreferenceUtils.getBoarding()!!
        droppingList = PreferenceUtils.getDropping()!!
        isPickupDropoffChargesEnabled = PreferenceUtils.getPreference(PREF_PICKUP_DROPOFF_CHARGES_ENABLED, false) ?: false

    }

    private fun setToolbarTitle() {
        val srcDest = "$source-$destination"
        val subtitle = if (serviceNumber.isNotEmpty())
            "$serviceNumber | ${travelDate?.let { getDateDMYY(it) }} $depTime | $busType"
        else
            "${travelDate?.let { getDateDMYY(it) }} $depTime | $busType"
        // val subtitle = "${travelDate?.let { getDateDMYY(it) }} | $depTime | $busType"
        binding.boardingToolbar.tvCurrentHeader.text =
            toolbarTitle ?: getString(R.string.notAvailable)
        binding.boardingToolbar.toolbarHeaderText.text = srcDest
        binding.boardingToolbar.toolbarSubtitle.text = subtitle
    }

    private fun initTab(isFinish: Boolean) {

        fragmentAdapter =
            BpDpNewAdapter(
                context = this,
                tabList = tabsList,
                fm = this.supportFragmentManager,
                boardingList = boardingList,
                droppingList = droppingList,
                viewpagerPickup = binding.viewpagerPickup,
                sourceKey = sourceKey
            )
        binding.viewpagerPickup.adapter = fragmentAdapter
        binding.viewpagerPickup.currentItem = 0
        binding.tabsViewReservation.setupWithViewPager(binding.viewpagerPickup)

        // custom tabs
        for (i in 0..binding.tabsViewReservation.tabCount.minus(1)) {
            binding.tabsViewReservation.getTabAt(i)?.apply {
                customView = fragmentAdapter.getTabView(i)
            }
        }

        if (boardingList.size > 1 && droppingList.size == 1 && isPickupDropoffChargesEnabled == false) {

            PreferenceUtils.putObject(droppingList[0], SELECTED_DROPPING_DETAIL)
            selectedDroppingPoint = droppingList[0].name
            selectedDroppingPointId = droppingList[0].id.toString()
            tabDropping.title = getString(R.string.dropping_point)
            tabDropping.selectedPoint = selectedDroppingPoint
            binding.tabsViewReservation.getTabAt(1)?.apply {
                (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                    droppingList[0].name
            }
            binding.tabsViewReservation.getTabAt(0)?.apply {
                (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                    selectedBoardingPoint
            }
        }
        else if (boardingList.size == 1 && droppingList.size > 1 && isPickupDropoffChargesEnabled == false) {
            PreferenceUtils.putObject(boardingList[0], SELECTED_BOARDING_DETAIL)
            binding.viewpagerPickup.currentItem = 1
            selectedBoardingPoint = boardingList[0].name
            selectedBoardingPointId = boardingList[0].id.toString()
            tabBoarding.title = getString(R.string.boarding_point)
            binding.tabsViewReservation.getTabAt(0)?.apply {
                (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                    boardingList[0].name
            }
            tabBoarding.selectedPoint = selectedBoardingPoint

            binding.tabsViewReservation.getTabAt(1)?.apply {
                (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                    getString(
                        R.string.select_location
                    )
            }
        }
        else if ((boardingList.size > 1 && droppingList.size > 1) ||
            (boardingList.size >= 1 && droppingList.size >= 1 && isPickupDropoffChargesEnabled == true)
            ) {
            if ((preSelecteBoarding=="true" ||preSelectedDropping== "true") && isPickupDropoffChargesEnabled == false){
                if (preSelecteBoarding== "true"){

                    val selectedBoarding= PreferenceUtils.getObject<BoardingPointDetail>(
                        SELECTED_BOARDING_DETAIL)
                    selectedBoardingPoint = selectedBoarding?.name
                    selectedBoardingPointId = selectedBoarding?.id
                    PreferenceUtils.putObject(selectedBoarding, PRE_SELECTED_BOARDING_DETAIL)
                    binding.viewpagerPickup.currentItem = 1
                    binding.tabsViewReservation.getTabAt(0)?.apply {
                        (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                            selectedBoarding?.name
                    }
                    binding.tabsViewReservation.getTabAt(1)?.apply {
                        (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                            selectedDroppingPoint
                    }
                    Timber.d("bpDpListCheck:8 ${boardingList.size} == ${droppingList.size }")

                }else if(preSelectedDropping== "true"){
                    Timber.d("bpDpListCheck:7 ${boardingList.size} == ${droppingList.size }")

                    val selectedDroping= PreferenceUtils.getObject<DropOffDetail>(
                        SELECTED_DROPPING_DETAIL)
                    PreferenceUtils.putObject(selectedDroping, PRE_SELECTED_DROPPING_DETAIL)
                    selectedDroppingPoint = selectedDroping?.name
                    selectedBoardingPointId = selectedDroping?.id
                    binding.viewpagerPickup.currentItem = 0
                    tabBoarding.selectedPoint = selectedDroppingPoint
                    tabBoarding.title = getString(R.string.boarding_point)
                    binding.tabsViewReservation.getTabAt(0)?.apply {
                        (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                            selectedBoardingPoint
                    }
                    binding.tabsViewReservation.getTabAt(1)?.apply {
                        (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                            selectedDroppingPoint

                    }

                }
            } else {
                Timber.d("bpDpListCheck:4 ${boardingList.size} == ${droppingList.size}")

                binding.viewpagerPickup.currentItem = 0
                binding.tabsViewReservation.getTabAt(1)?.apply {
                    (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                        selectedDroppingPoint
                }
                binding.tabsViewReservation.getTabAt(0)?.apply {
                    (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                        selectedBoardingPoint
                }
                binding.viewpagerPickup.currentItem = 0

                Timber.d("bpDpListCheck:5 ${boardingList.size} == ${droppingList.size}")
            }
        }
        else if (boardingList.size == 1 && droppingList.size == 1 && isPickupDropoffChargesEnabled == false) {
            Timber.d("bpDpListCheck:14 ${boardingList.size} == ${droppingList.size}")

            binding.viewpagerPickup.currentItem = 0

            selectedBoardingPoint = boardingList[0].name
            selectedBoardingPointId = boardingList[0].id.toString()
            tabBoarding.title = getString(R.string.boarding_point)
            binding.tabsViewReservation.getTabAt(0)?.apply {
                (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                    boardingList[0].name
            }
            tabBoarding.selectedPoint = selectedBoardingPoint


            selectedDroppingPoint = droppingList[0].name
            selectedDroppingPointId = droppingList[0].id.toString()
            tabDropping.title = getString(R.string.dropping_point)
            tabDropping.selectedPoint = selectedDroppingPoint
            binding.tabsViewReservation.getTabAt(1)?.apply {
                (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                    droppingList[0].name
            }
        }

        Timber.d("bpDpListCheck:13${intent.getStringExtra(getString(R.string.tag))}")

        if (intent.getStringExtra(getString(R.string.tag)) != null) {
            Timber.d("bpDpListCheck:13${rapidTag}")

            if (rapidTag == getString(R.string.boarding)) {
                binding.viewpagerPickup.currentItem = 0
            } else {
                binding.viewpagerPickup.currentItem = 1
            }
            if (PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL)?.id != "") {
                selectedBoardingPoint = PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL)?.name
                selectedBoardingPointId = PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL)?.id
                tabBoarding.title = getString(R.string.boarding_point)
                binding.tabsViewReservation.getTabAt(0)?.apply {
                    (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                        boardingList[0].name
                }
                tabBoarding.selectedPoint = selectedBoardingPoint
            }
            if (PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL)?.id != "") {
                selectedBoardingPoint =
                    PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL)?.name
                selectedBoardingPointId =
                    PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL)?.id
                tabBoarding.title = getString(R.string.dropping_point)
                binding.tabsViewReservation.getTabAt(1)?.apply {
                    (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                        selectedBoardingPoint
                }
                tabBoarding.selectedPoint = selectedBoardingPoint
            }
        }

        binding.tabsViewReservation.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                val selectedBPDP = tab?.customView?.findViewById(R.id.selectedBPDP) as? TextView
                if (selectedBPDP != null) {
                    TextViewCompat.setTextAppearance(selectedBPDP, R.style.TextStyleBold)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val selectedBPDP: TextView = tab?.customView?.findViewById(R.id.selectedBPDP)!!
                TextViewCompat.setTextAppearance(selectedBPDP, R.style.TextStyleNormalTV)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
//
    }


    override fun selectedPoint(str: String, tag: String, locationId: String?) {
        if (tag != null) {
            if (serviceType != null && serviceType == getString(R.string.proceed)) {
                if (tag == BpDpBoardingPointFragment.tag) {
                    if (boardingList.size > 1 && droppingList.size == 1) {
                        selectedBoardingPoint = str
                        selectedBoardingPointId = locationId
                        boardingList.forEach {
                            if (locationId == it.id.toString()) {
                                PreferenceUtils.putObject(it, SELECTED_BOARDING_DETAIL)
                            }
                        }
                        tabBoarding.title = getString(R.string.boarding_point)
                        tabBoarding.selectedPoint = selectedBoardingPoint
                        binding.tabsViewReservation.getTabAt(0)?.apply {
                            (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                                selectedBoardingPoint
                        }

                        if (isPickupDropoffChargesEnabled == false) {
                            val intent = Intent(this, NewCoachActivity::class.java)
                            intent.putExtra(REDIRECT_FROM, BusDetailsActivity.TAG)
                            intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                            startActivity(intent)
                            finish()
                        }

                    } else {
                        Timber.d("str>3 $selectedDroppingPoint tag $selectedDroppingPointId $locationId")


                        tabBoarding.title = getString(R.string.boarding_point)

                        selectedBoardingPoint = str
                        selectedBoardingPointId = locationId
                        boardingList.forEach {
                            if (locationId == it.id.toString()) {
                                PreferenceUtils.putObject(it, SELECTED_BOARDING_DETAIL)
                            }

                        }


                        tabBoarding.selectedPoint = selectedBoardingPoint
                        binding.tabsViewReservation.getTabAt(0)?.apply {
                            (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                                selectedBoardingPoint
                        }
                        if (preSelectedDropping == "true") {
                            boardingOrDropping = getString(R.string.dropping)

                            if (isPickupDropoffChargesEnabled == false) {
                                val intent = Intent(this, NewCoachActivity::class.java)
                                intent.putExtra(REDIRECT_FROM, BusDetailsActivity.TAG)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            if (PreferenceUtils.getObject<DropOffDetail>(SELECTED_DROPPING_DETAIL)?.id == "") {
                                if (previousPage == "ViewReservationChart")
                                    binding.viewpagerPickup.currentItem = 1
                            } else {

                                val intent = Intent(this, NewCoachActivity::class.java)
                                intent.putExtra(REDIRECT_FROM, BusDetailsActivity.TAG)
                                intent.putExtra("pickupAddressCharge", pickupChargeDetails)
                                intent.putExtra("dropoffAddressCharge", dropoffChargeDetails)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                } else {
                    tabDropping.title = getString(R.string.dropping_point)
                    tabDropping.selectedPoint = selectedDroppingPoint
                    selectedDroppingPoint = str
                    selectedDroppingPointId = locationId
                    droppingList.forEach {
                        if (locationId == it.id.toString()) {
                            PreferenceUtils.putObject(it, SELECTED_DROPPING_DETAIL)
                        }
                    }
                    binding.tabsViewReservation.getTabAt(1)?.apply {
                        (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                            selectedDroppingPoint
                        //view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    }
                    boardingOrDropping = getString(R.string.dropping)
                    val temp =
                        PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL)
                    if (temp?.id == "") {
                        binding.viewpagerPickup.currentItem = 0
                    } else {
                        if (isPickupDropoffChargesEnabled == false) {
                            val intent = Intent(this, NewCoachActivity::class.java)
                            if (navigateTag != null) {
                                intent.putExtra(getString(R.string.navigate_tag), navigateTag)
                            }
                            intent.putExtra(REDIRECT_FROM, BusDetailsActivity.TAG)
                            intent.putExtra("pickupAddressCharge", pickupChargeDetails)
                            intent.putExtra("dropoffAddressCharge", dropoffChargeDetails)
                            startActivity(intent)
                            finish()
                        }
                    }


                }
            } else {
                Timber.d("strTest>$preSelecteBoarding $str tag $tag $locationId")

                if (tag == BpDpBoardingPointFragment.tag) {
                    Timber.d("strTest1>$preSelecteBoarding $str tag $tag $locationId")

                selectedBoardingPoint = str
                selectedBoardingPointId = locationId
                tabBoarding.title = getString(R.string.boarding_point)
                tabBoarding.selectedPoint = selectedBoardingPoint
                boardingList.forEach {
                    if (it.id== locationId){
                        PreferenceUtils.putObject(it,SELECTED_BOARDING_DETAIL)
                    }
                }
                binding.tabsViewReservation.getTabAt(0)?.apply {
                    (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text = selectedBoardingPoint
                    //view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    if (droppingList.size==1){
                        val intent = Intent()
                        intent.putExtra(getString(R.string.boarding_point_key), selectedBoardingPoint)
                        intent.putExtra(
                            getString(R.string.boarding_point_id_key),
                            selectedBoardingPointId
                        )
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            } else {
                Timber.d("strTest2>$preSelecteBoarding $str tag $tag $locationId")

                    selectedDroppingPoint = str
                    selectedDroppingPointId = locationId
                    tabDropping.title = getString(R.string.dropping_point)
                    tabDropping.selectedPoint = selectedDroppingPoint
                    binding.tabsViewReservation.getTabAt(1)?.apply {
                        (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                            selectedDroppingPoint
                        //view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    }
                    droppingList.forEach {
                        if (it.id == locationId) {
                            PreferenceUtils.putObject(it, SELECTED_DROPPING_DETAIL)
                        }
                    }

                    val temp =
                        PreferenceUtils.getObject<BoardingPointDetail>(SELECTED_BOARDING_DETAIL)
                    if (temp?.id == "") {
                        binding.viewpagerPickup.currentItem = 0
                    } else {
                        val intent = Intent()
                        intent.putExtra(
                            getString(R.string.boarding_point_key),
                            selectedBoardingPoint
                        )
                        intent.putExtra(
                            getString(R.string.dropping_point_key),
                            selectedDroppingPoint
                        )
                        intent.putExtra(
                            getString(R.string.boarding_point_id_key),
                            selectedBoardingPointId
                        )
                        intent.putExtra(
                            getString(R.string.dropping_point_id_key),
                            selectedDroppingPointId
                        )
                        setResult(RESULT_OK, intent)
                        //initTab(true)
                        finish()
                    }
                }
            }
        }
    }

    override fun sendPickupDropOffDetails(str: String, value: String, tag: String) {
        if (tag == BpDpBoardingPointFragment.tag) {
            pickupChargeDetails?.apply {
                address = str
                charge = "0.0"
            }
            binding.viewpagerPickup.currentItem = 1
        } else if (tag == BpDpDroppingFragment.tag) {
            dropoffChargeDetails?.apply {
                address = str
                charge = "0.0"
            }

            val intent = Intent(this, NewCoachActivity::class.java)
            if (navigateTag != null) {
                intent.putExtra(getString(R.string.navigate_tag), navigateTag)
            }
            intent.putExtra(REDIRECT_FROM, BusDetailsActivity.TAG)
            intent.putExtra("pickupAddressCharge", pickupChargeDetails)
            intent.putExtra("dropoffAddressCharge", dropoffChargeDetails)
            startActivity(intent)
            finish()
        }
    }

    override fun onSingleButtonClick(str: String) {
    }
}
