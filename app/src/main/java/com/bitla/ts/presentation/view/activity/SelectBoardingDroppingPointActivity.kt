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
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.fare_breakup.request.ChargeDetails
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.fragments.boarding.*
import com.bitla.ts.presentation.view.passenger_payment.*
import com.bitla.ts.presentation.view.passenger_payment_show_new_flow.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.ADDRESS_PATTERN_CHECK
import com.bitla.ts.utils.sharedPref.*
import com.google.android.material.tabs.*
import gone
import timber.log.*
import toast
import visible
import java.io.*

class SelectBoardingDroppingPointActivity : BaseActivity(), FragmentListener {
    companion object {
        val tag: String = SelectBoardingDroppingPointActivity::class.java.simpleName
    }

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
    private lateinit var binding: ActivitySelectBoardingDroppingPointBinding
    private var tabsList: MutableList<Tabs> = mutableListOf()
    private lateinit var fragmentAdapter: BoardingDroppingAdapter
    val tabBoarding = Tabs()
    val tabDropping = Tabs()

    private var boardingList = mutableListOf<StageDetail>()
    private var droppingList = mutableListOf<StageDetail>()
    private var boardingOrDropping: String? = null
    private var isPickupDropoffChargesEnabled : Boolean? = false
    private val pickupChargeDetails : ChargeDetails = ChargeDetails()
    private val dropoffChargeDetails : ChargeDetails = ChargeDetails()
    private var privilegeResponseModel: PrivilegeResponseModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onClickListener()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun onClickListener() {
        binding.boardingToolbar.imgBack.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.img_back -> onBackPressed()
        }
    }

    override fun initUI() {
        binding = ActivitySelectBoardingDroppingPointBinding.inflate(layoutInflater)

        if (intent.getSerializableExtra(getString(R.string.boarding)) != null)
            boardingList =
                intent.getSerializableExtra(getString(R.string.boarding)) as MutableList<StageDetail>
        if (intent.getSerializableExtra(getString(R.string.dropping)) != null)
            droppingList =
                intent.getSerializableExtra(getString(R.string.dropping)) as MutableList<StageDetail>
        if (intent.getStringExtra(getString(R.string.tag)) != null)
            boardingOrDropping = intent.getStringExtra(getString(R.string.tag))
        if (intent.getStringExtra(getString(R.string.bus_type)) != null)
            busType = intent.getStringExtra(getString(R.string.bus_type))
        if (intent.getStringExtra(getString(R.string.service_number)) != null)
            serviceNumber = intent.getStringExtra(getString(R.string.service_number))!!
        if (intent.getStringExtra(getString(R.string.dep_time)) != null)
            depTime = intent.getStringExtra(getString(R.string.dep_time))
        if (intent.getStringExtra(getString(R.string.toolbar_title)) != null)
            toolbarTitle = intent.getStringExtra(getString(R.string.toolbar_title))
        if (intent.getStringExtra(getString(R.string.service_type)) != null)
            serviceType = intent.getStringExtra(getString(R.string.service_type))

        Timber.d("boardingList ${boardingList.size} droppingList ${droppingList.size} boardingOrDropping $boardingOrDropping")
        if (boardingList.isNotEmpty() && boardingList.size == 1) {
            selectedBoardingPoint = boardingList[0].name
            selectedBoardingPointId = boardingList[0].id.toString()
            PreferenceUtils.putObject(boardingList[0], PREF_BOARDING_STAGE_DETAILS)
        } else
            selectedBoardingPoint = getString(R.string.select_location)
        if (droppingList.isNotEmpty() && droppingList.size == 1) {
            selectedDroppingPoint = droppingList[0].name
            selectedDroppingPointId = droppingList[0].id.toString()
            PreferenceUtils.putObject(droppingList[0], PREF_DROPPING_STAGE_DETAILS)
        } else
            selectedDroppingPoint = getString(R.string.select_location)
        getPref()
        setToolbarTitle()
        initTab(false)
        binding.boardingToolbar.tvScan.gone()
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
    }

    private fun getPref() {
        travelDate = PreferenceUtils.getTravelDate()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        privilegeResponseModel = getPrivilegeBase()
        isPickupDropoffChargesEnabled = PreferenceUtils.getPreference(PREF_PICKUP_DROPOFF_CHARGES_ENABLED, false) ?: false
    }

    private fun setToolbarTitle() {
        val srcDest = "$source-$destination"
        val subtitle = if (serviceNumber.isNotEmpty())
            "$serviceNumber | ${travelDate?.let { getDateDMYY(it) }} $depTime | $busType"
        else
            "${travelDate?.let { getDateDMYY(it) }} $depTime | $busType"
        if (toolbarTitle != null) {
            binding.boardingToolbar.tvCurrentHeader.visible()
            binding.boardingToolbar.tvCurrentHeader.text =
                toolbarTitle ?: getString(R.string.notAvailable)
        } else
            binding.boardingToolbar.tvCurrentHeader.gone()
        binding.boardingToolbar.toolbarHeaderText.text = srcDest
        binding.boardingToolbar.toolbarSubtitle.text = subtitle
    }

    private fun initTab(isFinish: Boolean) {
        var boardingPointName =
            PreferenceUtils.getObject<StageDetail>(PREF_BOARDING_STAGE_DETAILS)?.name
        var droppingPointName =
            PreferenceUtils.getObject<StageDetail>(PREF_DROPPING_STAGE_DETAILS)?.name
        tabBoarding.title = getString(R.string.boarding_point)
        tabBoarding.selectedPoint = selectedBoardingPoint
        if (boardingPointName != null) {
            tabBoarding.selectedPoint = boardingPointName
        }
        tabsList.add(tabBoarding)

        tabDropping.title = getString(R.string.dropping_point)
        tabDropping.selectedPoint = selectedDroppingPoint
        if (droppingPointName != null) {
            tabDropping.selectedPoint = droppingPointName
        }
        tabsList.add(tabDropping)

        fragmentAdapter =
            BoardingDroppingAdapter(
                this,
                tabsList,
                this.supportFragmentManager,
                boardingList,
                droppingList,
                binding.viewpagerPickup
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
        setDefaultTab()
        binding.tabsViewReservation.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                var selectedBPDP: TextView = tab?.customView?.findViewById(R.id.selectedBPDP)!!
                TextViewCompat.setTextAppearance(selectedBPDP, R.style.TextStyleBold)

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                var selectedBPDP: TextView = tab?.customView?.findViewById(R.id.selectedBPDP)!!
                TextViewCompat.setTextAppearance(selectedBPDP, R.style.TextStyleNormalTV)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        if (isFinish)
            finish()
    }

    private fun setDefaultTab() {
        if ((boardingOrDropping != null && boardingOrDropping == getString(R.string.boarding)
            && boardingList.size > 1) || (isPickupDropoffChargesEnabled == true && boardingOrDropping != null
                    && boardingOrDropping == getString(R.string.boarding) && boardingList.size >= 1)
        ) {
            binding.viewpagerPickup.currentItem = 0
            binding.tabsViewReservation.getTabAt(1)?.apply {
                var selectedBPDP: TextView = customView?.findViewById(R.id.selectedBPDP)!!
                TextViewCompat.setTextAppearance(selectedBPDP, R.style.TextStyleNormalTV)
            }
        } else {
            binding.viewpagerPickup.currentItem = 1
            binding.tabsViewReservation.getTabAt(0)?.apply {
                var selectedBPDP: TextView = customView?.findViewById(R.id.selectedBPDP)!!
                TextViewCompat.setTextAppearance(selectedBPDP, R.style.TextStyleNormalTV)
            }
        }
    }

    override fun selectedPoint(str: String, tag: String, locationId: String?) {
        //tabsList.clear()
        if ((tag == BoardingPointFragment.tag && boardingList.size > 1) || (isPickupDropoffChargesEnabled == true &&
                    tag == BoardingPointFragment.tag && boardingList.size >= 1)) {
            selectedBoardingPoint = str
            selectedBoardingPointId = locationId
            tabBoarding.title = getString(R.string.boarding_point)
            tabBoarding.selectedPoint = selectedBoardingPoint
            binding.tabsViewReservation.getTabAt(0)?.apply {
                (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                    selectedBoardingPoint
                //view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }

            if (droppingList.size == 1 && isPickupDropoffChargesEnabled == false) {
                if (serviceType != null && serviceType == getString(R.string.proceed)) {
                    
                    if (privilegeResponseModel?.allowToShowNewFlowInTsApp == true
                    ) {
                        //humsafar
                        val intent = Intent(this, PassengerPaymentNewFlowActivity::class.java)
                        intent.putExtra(getString(R.string.boarding), boardingList as Serializable)
                        intent.putExtra(getString(R.string.dropping), droppingList as Serializable)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, PassengerPaymentActivity::class.java)
                        intent.putExtra(NEW_BOOK_BLOCK_CHECK, true)
                        intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                        startActivity(intent)
                    }
                    
                } else {
                    val intent = Intent().apply {
                        putExtra(getString(R.string.boarding_point_key), selectedBoardingPoint)
                        putExtra(getString(R.string.dropping_point_key), selectedDroppingPoint)
                        putExtra(getString(R.string.boarding_point_id_key), selectedBoardingPointId)
                        putExtra(getString(R.string.dropping_point_id_key), selectedDroppingPointId)
                    }
                    setResult(RESULT_OK, intent)
                    //initTab(true)
                    finish()
                }
            }
        }
        else {
            selectedDroppingPoint = str
            selectedDroppingPointId = locationId
            tabDropping.title = getString(R.string.dropping_point)
            tabDropping.selectedPoint = selectedDroppingPoint
            binding.tabsViewReservation.getTabAt(1)?.apply {
                (customView?.findViewById<TextView>(R.id.selectedBPDP)!!).text =
                    selectedDroppingPoint
                //view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            boardingOrDropping = getString(R.string.dropping)

            if (tabBoarding.selectedPoint != getString(R.string.select_location) && !tabBoarding.selectedPoint.isNullOrEmpty() && tabDropping.selectedPoint != getString(
                    R.string.select_location
                )
            ) {
                
                if (serviceType != null && serviceType == getString(R.string.proceed)) {
                    if (privilegeResponseModel?.allowToShowNewFlowInTsApp == true
                    ) {
                        //humsafar
                        val intent = Intent(this, PassengerPaymentNewFlowActivity::class.java)
                        intent.putExtra(getString(R.string.boarding), boardingList as Serializable)
                        intent.putExtra(getString(R.string.dropping), droppingList as Serializable)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, PassengerPaymentActivity::class.java)
                        intent.putExtra(NEW_BOOK_BLOCK_CHECK, true)
                        intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                        intent.putExtra("pickupAddressCharge", pickupChargeDetails)
                        intent.putExtra("dropoffAddressCharge", dropoffChargeDetails)
                        startActivity(intent)
                    }
                } else {
                    val intent = Intent()
                    intent.putExtra(getString(R.string.boarding_point_key), selectedBoardingPoint)
                    intent.putExtra(getString(R.string.dropping_point_key), selectedDroppingPoint)
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
            } else {
                if (isPickupDropoffChargesEnabled == true)
                    binding.viewpagerPickup.currentItem = 0
            }
        }
    }

    override fun sendPickupDropOffDetails(str: String, value: String, tag: String) {
        if (tag == BoardingPointFragment.tag) {
            pickupChargeDetails?.apply {
                address = str
                charge = if(str == "" || !str.matches(ADDRESS_PATTERN_CHECK.toRegex())) "0.0" else value
            }
        } else if (tag == DroppingFragment.tag) {
            dropoffChargeDetails?.apply {
                address = str
                charge = if(str == "" || !str.matches(ADDRESS_PATTERN_CHECK.toRegex())) "0.0" else value
            }
        }
    }

}