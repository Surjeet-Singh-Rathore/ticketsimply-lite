package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.alloted_Service_method_name
import com.bitla.ts.data.alloted_service_Dashboard_method
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.alloted_services.*
import com.bitla.ts.domain.pojo.alloted_services.Service
import com.bitla.ts.domain.pojo.alloted_services.request.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.user_list.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.util.*


class SearchAgentActivity : BaseActivity(), OnItemClickListener, OnItemCheckedListener {

    private lateinit var binding: ActivitySearchServiceBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var searchServiceAdapter: SearchServiceAdapter
    private var filterdNamesList = mutableListOf<Service>()
    private var list = mutableListOf<Service>()
    private var allottedServicesResponseModel: AllotedServicesResponseModel? = null
    private var servicesSelectedFalg: Boolean = true
    private var bccId: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var locale: String? = ""
    private var dashboardServiceFilterConf = DashboardServiceFilterConf()
    private var isGroupByHubs = false
    var dialog: AlertDialog? = null
    private var fromDate = ""
    private var toDate = ""
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()
    private val dashboardRevenueViewModel by viewModel<DashboardRevenueViewModel<Any?>>()
    private var isFromAgentRevenue: Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        lifecycleScope.launch {
            blockViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }

        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            dashboardRevenueViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    override fun initUI() {
        binding = ActivitySearchServiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        getPref()
        getParentIntent()

        if (PreferenceUtils.getRevenueFilterList().size > 0) {
            list = PreferenceUtils.getRevenueFilterList()
            invalidateServicesCount()
            setServiceAdapter()
        } else {
            if (isFromAgentRevenue == true) {
                getAllAgentsData()
                agentListObserver()
            } else {
                getAllHubsData()
                hubListObserver()

            }
        }

        binding.btnFilter.gone()







        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
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


        binding.btnSelectServices.setOnClickListener {

            val isAllUnChecked = list.all { !it.isChecked }
            if (isAllUnChecked) {

                if (isFromAgentRevenue!!) {
                    toast(getString(R.string.please_select_atleast_1_agent))
                } else {
                    toast(getString(R.string.please_select_atleast_1_hub))
                }


                return@setOnClickListener
            }

            val intent = Intent()

            allottedServicesResponseModel?.services = list

            val allottedServicesResponseModelToString = allottedServicesResponseModel?.let { it1 ->
                jsonToString(
                    it1
                )
            }

            val dashboardServiceFilterConfToString = jsonToString(dashboardServiceFilterConf)

            intent.putExtra("allotted_service_response", allottedServicesResponseModelToString)
            intent.putExtra(
                getString(R.string.dashboard_service_filter_conf),
                dashboardServiceFilterConfToString
            )

            setResult(SELECT_SERVICE_INTENT_REQUEST_CODE, intent)
            finish()
        }

        binding.toolbarImageLeft.setOnClickListener {
            this.finish()
        }

        binding.chkSelectAll.setOnClickListener {
            if (binding.chkSelectAll.isChecked) {
                list.forEach {
                    it.isChecked = true
                }
                if (isFromAgentRevenue!!) {
                    binding.chkSelectAll.text = getString(R.string.deselect_all_agents)
                } else {
                    binding.chkSelectAll.text = getString(R.string.deselect_all_hubs)
                }
                servicesSelectedFalg = true
            } else {
                list.forEach {
                    it.isChecked = false
                }
                if (isFromAgentRevenue!!) {
                    binding.chkSelectAll.text = getString(R.string.select_all_agents)
                } else {
                    binding.chkSelectAll.text = getString(R.string.select_all_hubs)
                }
                servicesSelectedFalg = false
            }

            if (::searchServiceAdapter.isInitialized) {
                if (filterdNamesList.size > 0) {
                    searchServiceAdapter.addData(filterdNamesList)

                } else {
                    searchServiceAdapter.addData(list)
                }
            }
            invalidateServicesCount()
        }

        setAllottedDetailObserver()

        firebaseLogEvent(
            this,
            ALL_SERVICES,
            loginModelPref.userName,
            loginModelPref.travels_name,
            loginModelPref.role,
            ALL_SERVICES,
            AllService.ALL_SERVICE
        )
    }

    private fun agentListObserver() {
        blockViewModel.userList.observe(this) {
            try {
                if (it.code == 200) {
                    if (it.active_users.isNotEmpty()) {

                        setAllottedListData(it)
                    }
                } else if(it.code == 401){
                    //openUnauthorisedDialog()
                    showUnauthorisedDialog()

                } else {
                    toast(it.message)
                }

            } catch (t: Throwable) {
                Timber.d("exceptionMsgUser ${t.message}")
                toast("An error occurred while fetching Agent List")
            }
        }
    }

    private fun hubListObserver() {
        dashboardRevenueViewModel.hubist.observe(this) {
            try {
                if (it.hub_list.isNotEmpty()) {
                    setAllottedListData(it)
                }

            } catch (t: Throwable) {
                Timber.d("exceptionMsgUser ${t.message}")
                toast("An error occurred while fetching Agent List")
            }
        }
    }

    private fun getAllAgentsData() {
        blockViewModel.userListApi(loginModelPref.api_key, "", "1", "", locale!!, "user_list")
    }

    private fun getAllHubsData() {
        dashboardRevenueViewModel.hubListApi(loginModelPref.api_key, "hub_list")
    }


    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        toDate = PreferenceUtils.getDashboardCurrentDate()

        allottedServicesResponseModel =
            PreferenceUtils.getObject<AllotedServicesResponseModel>("allotted_services_model_dashboard")

        dashboardServiceFilterConf =
            PreferenceUtils.getObject<DashboardServiceFilterConf>(getString(R.string.dashboard_service_filter_conf))
                ?: DashboardServiceFilterConf()

    }

    private fun getParentIntent() {
        if (intent.hasExtra(getString(R.string.dashboardGraphServiceFiter))) {
            val temp = intent.getStringExtra(getString(R.string.dashboardGraphServiceFiter)) ?: ""
            allottedServicesResponseModel =
                stringToJson(temp)
        }

        if (intent.hasExtra(getString(R.string.dashboard_service_filter_conf))) {
            val temp =
                intent.getStringExtra(getString(R.string.dashboard_service_filter_conf)) ?: ""
            dashboardServiceFilterConf = stringToJson(temp)
        }

        if (intent.hasExtra("from_date")) {
            fromDate =
                intent.getStringExtra("from_date") ?: PreferenceUtils.getDashboardCurrentDate()
        }

        if (intent.hasExtra("to_date")) {
            toDate =
                intent.getStringExtra("to_date") ?: PreferenceUtils.getDashboardCurrentDate()


            if (intent.hasExtra("isFromAgentRevenue")) {
                isFromAgentRevenue = intent.getBooleanExtra("isFromAgentRevenue", false)
            }
        }

    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {

        val index = list.indexOfFirst {
            it.routeId == position
        }
        list[index].isChecked = data == "true"
        list.forEach {
            if (!it.isChecked) {
                binding.chkSelectAll.isChecked = false
                if (isFromAgentRevenue!!) {
                    binding.chkSelectAll.text = getString(R.string.select_all_agents)
                } else {
                    binding.chkSelectAll.text = getString(R.string.select_all_hubs)
                }
                return@forEach
            }
        }

        invalidateServicesCount()
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    private fun setServiceAdapter() {

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSelectSearchPassenger.layoutManager = layoutManager
        searchServiceAdapter = SearchServiceAdapter(this, this)
        searchServiceAdapter.addData(list)
        binding.rvSelectSearchPassenger.adapter = searchServiceAdapter
        searchServiceAdapter.notifyDataSetChanged()

    }

    @SuppressLint("DefaultLocale")
    private fun search(text: String) {

        filterdNamesList = mutableListOf()

        for (s in list) {

            if (s.routeId != null && s.number!!.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filterdNamesList.add(s)
            }
        }

        if (::searchServiceAdapter.isInitialized) {
            searchServiceAdapter.addData(filterdNamesList)
        }
    }

    override fun onItemChecked(isChecked: Boolean, view: View, position: Int) {
    }


    private fun callAllottedServiceApi(
        originId: String?,
        destinationId: String?,
        hubId: String?,
        isGroupByHubs: Boolean
    ) {
        if (isNetworkAvailable()) {
            val allotedServiceRequest = AllotedServiceRequest(
                bccId.toString(),
                alloted_Service_method_name,
                format_type,
                ReqBody(
                    loginModelPref.api_key,
                    PreferenceUtils.getDashboardCurrentDate(),
                    origin = originId,
                    destination = destinationId,
                    is_group_by_hubs = isGroupByHubs,
                    is_from_middle_tier = true,
                    locale = locale,
                    hub_id = hubId,
                    view_mode = "report"
                )
            )

            pickUpChartViewModel.getAllottedServicesWithDateChange(
                apiKey = loginModelPref.api_key,
                origin = originId.toString(),
                destination = destinationId.toString(),
                from = fromDate,
                to = toDate,
                hubId = hubId,
                isGroupByHubs = isGroupByHubs,
                viewMode = "report",
                locale = locale ?: "en",
                isFromMiddleTier = true,
                methodName = alloted_service_Dashboard_method
            )

        } else
            noNetworkToast()
    }

    private fun setAllottedDetailObserver() {
        pickUpChartViewModel.allotedDetailResponse.observe(this) { it ->
            if (it.code != null && it.code == 200) {
                if (it?.services?.isNotEmpty() == true) {

                    binding.NoResult.gone()

                    binding.chkSelectAll.visible()
                    binding.rvSelectSearchPassenger.visible()
                    binding.btnSelectServices.visible()

                    allottedServicesResponseModel?.services = it.services
//                    setAllottedListData(it)
                } else {

                    binding.chkSelectAll.gone()
                    binding.rvSelectSearchPassenger.gone()
                    binding.btnSelectServices.gone()

                    binding.NoResult.visible()
                    binding.noResultText.text = it.result?.message
                }
            } else {
                binding.chkSelectAll.gone()
                binding.rvSelectSearchPassenger.gone()
                binding.btnSelectServices.gone()

                binding.NoResult.visible()
                binding.noResultText.text = it.result?.message
            }
        }
    }

    private fun setAllottedListData(userListModel: UserListModel) {

        list.clear()
        binding.chkSelectAll.isChecked = true
        if (isFromAgentRevenue!!) {
            binding.chkSelectAll.text = getString(R.string.deselect_all_agents)
        } else {
            binding.chkSelectAll.text = getString(R.string.deselect_all_hubs)
        }


        val tempList = if (isFromAgentRevenue!!) {
            userListModel.active_users
        } else {
            userListModel.hub_list
        }

        tempList.forEach {
            val data = Service()
            data.routeId = it.id
            data.number = it.label
            list.add(data)

        }

        list.forEach {
            if (!it.isChecked) {
                binding.chkSelectAll.isChecked = false
                if (isFromAgentRevenue!!) {
                    binding.chkSelectAll.text = getString(R.string.select_all_agents)
                } else {
                    binding.chkSelectAll.text = getString(R.string.select_all_hubs)
                }
            }
        }
        invalidateServicesCount()

        setServiceAdapter()

    }

    @SuppressLint("SetTextI18n")
    private fun invalidateServicesCount() {
        var totalCheckedItems = 0
        list.forEach {
            if (it.isChecked) {
                totalCheckedItems++
            }
        }
        if (isFromAgentRevenue!!) {
            binding.tvSelectServices.text =
                "${getString(R.string.select_agents)} ($totalCheckedItems)"
        } else {
            binding.tvSelectServices.text =
                "${getString(R.string.select_hub)} ($totalCheckedItems)"
        }

    }


    override fun onPause() {
        super.onPause()
        PreferenceUtils.putRevenueFilterList(list)
    }



}