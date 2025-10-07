package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.view.*
import androidx.core.view.WindowCompat
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.available_routes.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.available_routes.request.*
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.*
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.util.*


class SearchActivity : BaseActivity(), OnItemClickListener, DialogSingleButtonListener {

    override fun initUI() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            WindowCompat.setDecorFitsSystemWindows(window, false) // Enables edge-to-edge
            edgeToEdge(binding.root)
        }
        lifecycleScope.launch {
            availableRoutesViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }

    private var filterType: String = ""
    private val TAG: String = SearchActivity::class.java.simpleName

    private var searchAdapter: SearchAdapter? = null
    private lateinit var layoutManagerCity: RecyclerView.LayoutManager
    private lateinit var searchList: MutableList<SearchModel>

    private var originList: MutableList<Origin> = mutableListOf()
    private var destinationList: MutableList<Destination> = mutableListOf()
    private var agentList: MutableList<SpinnerItems> = mutableListOf()
    private var branchList: MutableList<SpinnerItems> = mutableListOf()
    private var userList: MutableList<SpinnerItems> = mutableListOf()
    private var filterdNames: MutableList<SearchModel> = mutableListOf()
    private var cityList: MutableList<com.bitla.ts.domain.pojo.city_details.response.Result> =
        mutableListOf()
    private val showOnlyAvailableServices: String = "true" //fixed
    private val showInJourneyServices: String = "true" // fixed
    private val availableRoutesViewModel by viewModel<AvailableRoutesViewModel<Any?>>()
    var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var serviceList: MutableList<Result> = mutableListOf()

    private lateinit var binding: ActivitySearchBinding
    private var locale: String? = ""
    private var isAllowMultistationBlockedService: Boolean = false
    private var multistationAllowedTime: String = ""
    private var allBranch: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_search)
        if (intent.hasExtra("filter_type")) {
            filterType = intent.getStringExtra("filter_type") ?: ""
        }

        if (intent.hasExtra("all_branch")) {
            allBranch = intent.getStringExtra("all_branch") ?: ""
        }

//        if (intent.hasExtra("all_user")) {
//            allUser = intent.getStringExtra("all_user") ?: ""
//        }

        init()



        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (::searchList.isInitialized)
                    search(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
            }
        })
    }

    override fun isInternetOnCallApisAndInitUI() {
    }


    override fun onClickOfNavMenu(position: Int) {

    }

    @SuppressLint("DefaultLocale")
    private fun search(text: String) {
        filterdNames = mutableListOf<SearchModel>()

        for (s in searchList) {
            if (s.name != null && s.name!!.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filterdNames.add(s)
            }
        }
        if (searchAdapter != null) searchAdapter?.filterList(filterdNames)
    }

    private fun init() {

        locale = PreferenceUtils.getlang()

        try {
            if (PreferenceUtils.getOriginCity() != null) {
                originList = PreferenceUtils.getOriginCity()!!
                originList.sortWith(compareBy<Origin> { it.name })
            }
            if (PreferenceUtils.getDestinationCity() != null) {
                destinationList = PreferenceUtils.getDestinationCity()!!
                destinationList.sortWith(compareBy<Destination> { it.name })
            }

            val citySelectionType: String =
                intent.getStringExtra(getString(R.string.CITY_SELECTION_TYPE))!!

            val previousScreen: String = intent.getStringExtra(PREVIOUS_SCREEN) ?: ""


            if (citySelectionType == getString(R.string.SOURCE_SELECTION)) {
                binding.toolbarHeaderText.text = getString(R.string.selectSource)
                searchList = mutableListOf()
                //searchList = originList
                for (i in 0..originList.size.minus(1)) {
                    val cityModel = SearchModel()
                    cityModel.id = originList[i].id.toString()
                    cityModel.name = originList[i].name
                    searchList.add(cityModel)
                }
                originList.sortBy { it.name ?: "".lowercase() }
                searchList.sortBy { it.name ?: "".lowercase() }
                setCityAdapter()
            } else if (citySelectionType == getString(R.string.DESTINATION_SELECTION)) {
                binding.toolbarHeaderText.text = getString(R.string.selectDestination)
                searchList = mutableListOf()
                //searchList = destinationList
                if (destinationList.isNotEmpty()) {
                    for (i in 0..destinationList.size.minus(1)) {
                        var cityModel = SearchModel()
                        cityModel.id = destinationList[i].id.toString()
                        cityModel.name = destinationList[i].name
                        searchList.add(cityModel)
                    }

                    destinationList.sortBy { it.name ?: "".lowercase() }
                    searchList.sortBy { it.name ?: "".lowercase() }
                    setCityAdapter()
                }
            } else if (citySelectionType == getString(R.string.selectAgent)) {
                binding.toolbarHeaderText.text = getString(R.string.selectAgent)
                searchList = mutableListOf()
                agentList = retrieveAgentList()

                if (filterType == getString(R.string.all)) {
//                    val searchModel = SearchModel()
//                    searchModel.id = "-1"
//                    searchModel.name = getString(R.string.all)
//                    if (previousScreen.isEmpty())
//                        searchList.add(searchModel)

                    agentList.forEach {
                        if (filterType == getString(R.string.all)) {
                            val searchModel = SearchModel()
                            searchModel.id = it.id
                            searchModel.name = it.value
                            searchList.add(searchModel)
                        }
                    }
                } else {
                    /*val searchModel = SearchModel()
                    searchModel.id = "-1"
                    searchModel.name = getString(R.string.all)
                    searchList.add(searchModel)*/

                    agentList.forEach {
                        if (filterType.equals(it.type, true)) {
                            val searchModel = SearchModel()
                            searchModel.id = it.id
                            searchModel.name = it.value
                            searchList.add(searchModel)
                        }

                    }
//                    agentList.sortBy { it.value!!.lowercase() }
//                    searchList.sortBy { it.name!!.lowercase() }
                }

                setCityAdapter()
            } else if (citySelectionType == getString(R.string.selectBranch)) {
                binding.toolbarHeaderText.text = getString(R.string.selectBranch)
                searchList = mutableListOf()

                branchList = retrieveBranchList()


                if (filterType == getString(R.string.all)) {
//                    val searchModel = SearchModel()
//                    searchModel.id = "-1"
//                    searchModel.name = getString(R.string.all)
//                    if (previousScreen.isEmpty())
//                        searchList.add(searchModel)


                    branchList.forEach {
                        if (filterType == getString(R.string.all)) {
                            val searchModel = SearchModel()
                            searchModel.id = it.id
                            searchModel.name = it.value
                            searchModel.branchDiscount =
                                it.branch_discount.toString().toDoubleOrNull()
                            searchList.add(searchModel)
                        }
                    }
                } else {
                    branchList.forEach {
                        if (filterType.equals("") || filterType.equals(getString(R.string.all))) {
                            val searchModel = SearchModel()
                            searchModel.id = it.id
                            searchModel.name = it.value
                            searchModel.branchDiscount =
                                it.branch_discount.toString().toDoubleOrNull()
                            searchList.add(searchModel)
                        } else if (filterType.equals(it.type, true)) {
                            val searchModel = SearchModel()
                            searchModel.id = it.id
                            searchModel.name = it.value
                            searchModel.branchDiscount =
                                it.branch_discount.toString().toDoubleOrNull()
                            searchList.add(searchModel)
                        }
                    }
                    if(searchList.isNullOrEmpty()){
                        binding.noResultLayout.visible()
                        binding.mainLayout.gone()
                    }

                    branchList.sortBy { it.value.lowercase() }
                    searchList.sortBy { it.name!!.lowercase() }
                }
                setCityAdapter()
            } else if (citySelectionType == getString(R.string.selectUser)) {
                binding.toolbarHeaderText.text = getString(R.string.selectUser)
                searchList = mutableListOf()

                userList = retrieveUserList()
                userList.forEach {
                    val searchModel = SearchModel()
                    searchModel.id = it.id
                    searchModel.name = it.value
                    searchModel.roleDiscount = it.role_discount.toString().toDoubleOrNull()
                    searchList.add(searchModel)
                }
                userList.sortBy { it.value.lowercase() }
                searchList.sortBy { it.name!!.lowercase() }
                setCityAdapter()
            } else if (citySelectionType == getString(R.string.CITY_SELECTION)) {
                binding.toolbarHeaderText.text = getString(R.string.select_city)
                searchList = mutableListOf()

                cityList = retrieveCityList()

                cityList.forEach {
                    val searchModel = SearchModel()
                    searchModel.id = it.id
                    searchModel.name = it.name
                    searchList.add(searchModel)
                }
                cityList.sortBy { it.name!!.lowercase() }
                searchList.sortBy { it.name!!.lowercase() }
                setCityAdapter()
            } else if (citySelectionType == getString(R.string.select_service)) {
                binding.toolbarHeaderText.text = getString(R.string.select_service)
                val destination = PreferenceUtils.getString(PREF_DESTINATION)
                val destinationID = PreferenceUtils.getString(PREF_DESTINATION_ID)
                val origin = PreferenceUtils.getString(PREF_SOURCE)
                val originId = PreferenceUtils.getString(PREF_SOURCE_ID)
                val travleDate = PreferenceUtils.getString(PREF_TRAVEL_DATE)
                val convertedDate = getDateYMD(travleDate!!)
                setAvailableRoutesApiObserver()
                binding.noResultLayout.gone()
                binding.mainLayout.gone()
                binding.progerssLayout.visible()
                availableRoutesApi(destinationID!!, originId!!, convertedDate)


            } else binding.toolbarHeaderText.text = getString(R.string.empty)

            clickListener()

        } catch (e: Exception) {
            toast(getString(R.string.opps))
        }
    }

    private fun clickListener() {
        binding.toolbarImageLeft.setOnClickListener(this)
    }

    private fun setCityAdapter() {
        layoutManagerCity = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCity.layoutManager = layoutManagerCity
        searchAdapter = SearchAdapter(
            this,
            this,
            searchList
        )
        binding.rvCity.adapter = searchAdapter
    }

    override fun onClick(view: View, position: Int) {
        try {
            if (view != null && position != null) {
                var searchedName: String? = null
                var searchedId: String? = null
                var busType: String? = null
                var depTime: String? = null
                var isApplyBpDpFare: Boolean = false
                var roleDiscount: Double? = null
                var branchDiscount: Double? = null

                if (binding.toolbarHeaderText.text == getString(R.string.selectSource)) {
                    if (filterdNames != null && filterdNames.isEmpty()) {
                        // get data from normal list if city not searched
                        searchedName = originList[position].name!!
                        searchedId = originList[position].id.toString()
                    } else {
                        // get data from filtered list if city searched
                        searchedName = filterdNames[position].name!!
                        searchedId = filterdNames[position].id.toString()
                    }

                    returnSelectedDetails(
                        searchedName,
                        searchedId
                    )

                } else if ((binding.toolbarHeaderText.text == getString(R.string.selectDestination))) {
                    if (filterdNames != null && filterdNames.isEmpty()) {
                        searchedName = destinationList[position].name!!
                        searchedId = destinationList[position].id.toString()
                    } else {
                        searchedName = filterdNames[position].name!!
                        searchedId = filterdNames.get(position).id.toString()
                    }
                    returnSelectedDetails(
                        searchedName,
                        searchedId
                    )

                } else if ((binding.toolbarHeaderText.text == getString(R.string.selectAgent))) {

                    if (filterdNames != null && filterdNames.isNotEmpty()) {
                        searchedName = filterdNames[position].name!!
                        searchedId = filterdNames[position].id.toString()
                    } else if (filterType != "" && filterType != getString(R.string.all)) {
                        searchedName = searchList[position].name!!
                        searchedId = searchList[position].id!!.toString()
                    } else if (filterdNames != null && filterdNames.isEmpty()) {
                        searchedName = searchList[position].name!!
                        searchedId = searchList[position].id!!.toString()
                    } else {
                        searchedName = filterdNames[position].name!!
                        searchedId = filterdNames[position].id.toString()
                    }
                    returnSelectedDetails(
                        searchedName,
                        searchedId
                    )

                } else if ((binding.toolbarHeaderText.text == getString(R.string.selectBranch))) {
                    if (filterdNames != null && filterdNames.isNotEmpty()) {
                        searchedName = filterdNames[position].name!!
                        searchedId = filterdNames[position].id.toString()
                        branchDiscount = filterdNames[position].branchDiscount
                    } else if (filterType != "" && filterType != getString(R.string.all)) {
                        searchedName = searchList[position].name!!
                        searchedId = searchList[position].id!!.toString()
                    } else if (filterdNames != null && filterdNames.isEmpty()) {
                        searchedName = searchList[position].name
                        searchedId = searchList[position].id.toString()
                        branchDiscount = searchList[position].branchDiscount
                    } else {
                        searchedName = filterdNames[position].name ?: ""
                        searchedId = filterdNames[position].id.toString()
                        branchDiscount = filterdNames[position].branchDiscount
                    }

                    returnSelectedDetails(
                        searchedName = searchedName.toString(),
                        searchedId = searchedId,
                        discountValue = branchDiscount.toString()
                    )
                } else if ((binding.toolbarHeaderText.text == getString(R.string.selectUser))) {

                    if (filterdNames != null && filterdNames.isEmpty()) {
                        searchedName = userList[position].value
                        searchedId = userList[position].id.toString()
                        roleDiscount = userList[position].role_discount
                    } else {
                        searchedName = filterdNames[position].name ?: ""
                        searchedId = filterdNames[position].id.toString()
                        roleDiscount = filterdNames[position].roleDiscount
                    }

                    returnSelectedDetails(
                        searchedName = searchedName,
                        searchedId = searchedId,
                        discountValue = roleDiscount.toString()
                    )

                } else if ((binding.toolbarHeaderText.text == getString(R.string.select_city))) {

                    if (filterdNames != null && filterdNames.isEmpty()) {
                        searchedName = cityList[position].name
                        searchedId = cityList[position].id.toString()
                    } else {
                        searchedName = filterdNames[position].name!!
                        searchedId = filterdNames[position].id.toString()
                    }

                    if (searchedName != null) {
                        returnSelectedDetails(
                            searchedName,
                            searchedId
                        )
                    }
                } else if ((binding.toolbarHeaderText.text == getString(R.string.select_service))) {
                    var temp = listOf<String>()
                    if (view.tag.toString().contains("serviceSelection")) {
                        temp = view.tag.toString().split("|")
                    }
                    var boardingList = mutableListOf<BoardingPointDetail>()
                    var droppingList = mutableListOf<DropOffDetail>()
                    serviceList.sortBy { it.number.lowercase() }
                    var tempOrigin = 0L
                    availableRoutesList.forEach {
                        if (it.id.toString() == temp[2]) {
                            searchedName = it.number
                            searchedId = it.id.toString()
                            tempOrigin = it.reservation_id.toLong()
                            boardingList = it.boarding_point_details as MutableList
                            droppingList = it.drop_off_details as MutableList
                            busType = it.bus_type
                            depTime = it.dep_time
                            PreferenceUtils.putObject(it.is_apply_bp_dp_fare, IS_APPLY_BP_DP_FARE)
                        }
                    }
                    PreferenceUtils.putBoarding(boardingList)
                    PreferenceUtils.putDropping(droppingList)

                    returnService(
                        searchedName!!,
                        searchedId!!,
                        busType!!,
                        depTime!!,
                        tempOrigin.toString()
                    )
                    saveSelectedAvailable(tempOrigin.toInt())
                    if (searchedName != null) {
                        returnService(
                            searchedName!!,
                            searchedId!!,
                            busType!!,
                            depTime!!,
                            tempOrigin.toString()
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    private fun returnService(
        cityName: String,
        cityId: String,
        bustype: String,
        depType: String,
        serviceNumber: String
    ) {
        val returnIntent = Intent()
        returnIntent.putExtra(
            getString(R.string.SELECTED_CITY_TYPE),
            binding.toolbarHeaderText.text
        )
        returnIntent.putExtra(getString(R.string.SELECTED_CITY_NAME), cityName)
        returnIntent.putExtra(getString(R.string.SELECTED_CITY_ID), cityId)

        returnIntent.putExtra(getString(R.string.bus_type), bustype)
        returnIntent.putExtra(getString(R.string.dep_time), depType)
        returnIntent.putExtra(getString(R.string.service_number), serviceNumber)
        returnIntent.putExtra(getString(R.string.toolbar_title), "${getString(R.string.booking)}")
        returnIntent.putExtra(getString(R.string.service_type), getString(R.string.proceed))

        setResult(Activity.RESULT_OK, returnIntent)
//       onBackPressed()
        finish()
    }

    //    private fun returnService(cityName: String, cityId: String) {
//        val returnIntent = Intent()
//        returnIntent.putExtra(
//            getString(R.string.SELECTED_CITY_TYPE),
//            binding.toolbarHeaderText.text
//        )
//        Timber.d("selectedCityNameCityID: $cityName $cityId ")
//
//        returnIntent.putExtra(getString(R.string.SELECTED_CITY_NAME), cityName)
//        returnIntent.putExtra(getString(R.string.SELECTED_CITY_ID), cityId)
//
//
//
//        setResult(Activity.RESULT_OK, returnIntent)
////       onBackPressed()
//        finish()
//    }
    private fun saveSelectedAvailable(position: Int) {
        availableRoutesList.forEach {
            if (it.reservation_id.toInt() == position) {
                PreferenceUtils.putObject(it.is_apply_bp_dp_fare, SERVICE_IS_APPLY_BP_DP_FARE)

                PreferenceUtils.setPreference("is_bima", it.is_bima)
                PreferenceUtils.putObject(
                    it, PREF_SELECTED_AVAILABLE_ROUTES
                )
                PreferenceUtils.putString((PREF_COACH_NUMBER), it.number)
            }
        }
        PreferenceUtils.setPreference(
            PREF_RESERVATION_ID,
            position
        )
    }

    private fun returnSelectedDetails(
        searchedName: String,
        searchedId: String,
        discountValue: String? = null
    ) {
        val returnIntent = Intent()
        returnIntent.putExtra(
            getString(R.string.SELECTED_SEARCHED_TYPE),
            binding.toolbarHeaderText.text
        )
        returnIntent.putExtra(getString(R.string.SELECTED_SEARCHED_NAME), searchedName)
        returnIntent.putExtra(getString(R.string.SELECTED_SEARCHED_ID), searchedId)

        returnIntent.putExtra(
            getString(R.string.SELECTED_CITY_TYPE),
            binding.toolbarHeaderText.text
        )
        returnIntent.putExtra(getString(R.string.SELECTED_CITY_NAME), searchedName)
        returnIntent.putExtra(getString(R.string.SELECTED_DISCOUNT_VALUE), discountValue)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
//        onBackPressed()
    }


    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(
        itemPosition: Int,
        menuPosition: Int,
        busData: Result
    ) {

    }


    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.toolbar_image_left -> onBackPressed()
        }

    }

    private fun availableRoutesApi(destinationId: String, sourceId: String, ymdDate: String) {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        var isBima: Boolean? = null
        if (PreferenceUtils.getPreference("is_bima", false) == true) {
            isBima = true
        }
        var isCsShared: Boolean? = null
        if (isBima == true) {
            isCsShared = true
        }
        val ymdDat = getDateYMD(ymdDate)
//        val availableRoutesRequest = AvailableRoutesRequest(
//            bccId.toString(), format_type, available_routes_method_name,
//            ReqBody(
//                api_key = loginModelPref.api_key,
//                destination_id = destinationId,
//                locale = locale,
//                operator_api_key = operator_api_key,
//                origin_id = sourceId,
//                show_injourney_services = showInJourneyServices,
//                show_only_available_services = showOnlyAvailableServices,
//                travel_date = ymdDat,
//                response_format = response_format,
//                is_cs_shared = isCsShared ?: false,
//                app_bima_enabled = isBima!!
//            )
//        )
//        Timber.d("availableRoutesRequest $availableRoutesRequest")

        if (isNetworkAvailable()) {
            availableRoutesViewModel.availableRoutesApi(
                apiKey = loginModelPref.api_key,
                originId = sourceId,
                destinationId = destinationId,
                showInJourneyServices = showInJourneyServices,
                isCsShared = isCsShared ?: false,
                operatorkey = operator_api_key,
                responseFormat = format_type,
                travelDate = ymdDate,
                showOnlyAvalServices = showOnlyAvailableServices,
                locale = locale ?: "en",
                apiType = available_routes_method_name,
                appBimaEnabled = isBima ?: false
            )
        } else
            noNetworkToast()
    }

    private fun setAvailableRoutesApiObserver() {
        availableRoutesViewModel.dataAvailableRoutes.observe(this, Observer {
//            binding.includeProgress.progressBar.gone()
//            stopShimmerEffect()
            Timber.d("availabletest", "responseBody dataAvailableRoutes $it")
            binding.progerssLayout.gone()

            if (it != null) {
                if (it.code == 200) {

                    PreferenceUtils.putObject(it, PREF_AVAILABLE_ROUTES_RESPONSE)
                    if (it.result.isNullOrEmpty()) {
                        binding.noResultLayout.visible()
                        binding.mainLayout.gone()
                        binding.noResultText.text = "${it.message}"
                    } else {
                        val list: MutableList<Result> = it.result
                        availableRoutesList.clear()
                        availableRoutesList = list
                        searchList = mutableListOf()
                        serviceList = list
                        for (i in 0..availableRoutesList.size.minus(1)) {
                            var cityModel = SearchModel()
                            cityModel.id = availableRoutesList[i].id.toString()
                            cityModel.name = availableRoutesList[i].number
                            cityModel.isAllowMultistationBlockedService =
                                availableRoutesList[i].is_allow_multistation_blocked_service
                            cityModel.multistationAllowedTime =
                                availableRoutesList[i].multistation_allowed_time
                            searchList.add(cityModel)

                            Timber.d("isAllowMultistationBlockedService: $searchList}")

                        }
                        searchList.sortBy { it.name!!.lowercase() }
                        setCityAdapter()
                        binding.noResultLayout.gone()
                        binding.mainLayout.visible()
                    }

                } else if (it.code == 401) {
                    /*DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    showUnauthorisedDialog()

                } else {
                    availableRoutesList.clear()
                    binding.noResultLayout.visible()
                    binding.noResultText.text = "${it.message}"
                    binding.mainLayout.gone()
                }
            } else {
                toast(getString(R.string.server_error))
            }

        })
    }

    override fun onSingleButtonClick(str: String) {
    }
}