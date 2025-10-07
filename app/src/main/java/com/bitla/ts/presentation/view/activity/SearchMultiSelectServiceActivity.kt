package com.bitla.ts.presentation.view.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.alloted_Service_method_name
import com.bitla.ts.data.alloted_service_Dashboard_method
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.OnItemCheckedListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ActivityMultiSearchServiceBinding
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.alloted_services.Service
import com.bitla.ts.domain.pojo.alloted_services.request.AllotedServiceRequest
import com.bitla.ts.domain.pojo.alloted_services.request.ReqBody
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.branch_list_model.BranchListModel
import com.bitla.ts.domain.pojo.branch_list_model.Branchlists
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.adapter.SearchServiceAdapter
import com.bitla.ts.presentation.viewModel.BlockViewModel
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.jsonToString
import com.bitla.ts.utils.constants.ALL_SERVICES
import com.bitla.ts.utils.constants.AllService
import com.bitla.ts.utils.constants.SELECT_SERVICE_INTENT_REQUEST_CODE
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible
import java.util.*


class SearchMultiSelectServiceActivity : BaseActivity(), OnItemClickListener,
    OnItemCheckedListener {

    private lateinit var binding: ActivityMultiSearchServiceBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var searchServiceAdapter: SearchServiceAdapter
    private var filterdNamesList = mutableListOf<Service>()
    private var list = mutableListOf<Service>()
    private var list2 = mutableListOf<Branchlists>()
    private var allottedServicesResponseModel: AllotedServicesResponseModel? = null
    private var allottedServicesResponseModelX: BranchListModel? = null
    private var servicesSelectedFalg: Boolean = true
    private var bccId: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var locale: String? = ""
    private var isGroupByHubs = false
    var dialog: AlertDialog? = null
    private var fromDate = ""
    private var toDate = ""
    private var serviceSize: Int? = 0
    private val blockViewModel by viewModel<BlockViewModel<Any?>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    override fun initUI() {
        binding = ActivityMultiSearchServiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        getPref()
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                  showToast(it)
                }
            }
        }
        setAllottedListData()

//        setAllottedDetailObserver()

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
                toast(getString(R.string.pleaseSelectAtleastOneService))
                return@setOnClickListener
            }
            val intent = Intent()
            allottedServicesResponseModel?.services = list

            val allottedServicesResponseModelToString = allottedServicesResponseModel?.let { it1 ->
                jsonToString(
                    it1
                )
            }

            intent.putExtra("allotted_service_response", allottedServicesResponseModelToString)
            intent.putExtra("allotted_service_size", allottedServicesResponseModel?.services?.size)
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
                binding.chkSelectAll.text = "Deselect All Services"
                servicesSelectedFalg = true
            } else {
                list.forEach {
                    it.isChecked = false
                }
                binding.chkSelectAll.text = "Select All Services"
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

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        toDate = PreferenceUtils.getDashboardCurrentDate()

//        callAllottedServiceApi(
//            originId = "",
//            destinationId = "",
//            hubId = "",
//            isGroupByHubs = false
//        )

        allottedServicesResponseModel =
            PreferenceUtils.getObject<AllotedServicesResponseModel>("allotted_services_model_report")
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
                binding.chkSelectAll.text = "Select All Services"
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

            if (s.routeId != null && s.number!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
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
        originId: String?, destinationId: String?, hubId: String?, isGroupByHubs: Boolean
    ) {
        if (isNetworkAvailable()) {
            AllotedServiceRequest(
                bccId.toString(), alloted_Service_method_name, format_type, ReqBody(
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

        } else noNetworkToast()
    }

    private fun setAllottedDetailObserver() {
        pickUpChartViewModel.allotedDetailResponse.observe(this) { it ->
            if (it.code == 200) {
                if (it?.services?.isNotEmpty() == true) {
                    serviceSize = it.services.size
                    binding.NoResult.gone()
                    binding.chkSelectAll.visible()
                    binding.rvSelectSearchPassenger.visible()
                    binding.btnSelectServices.visible()
                    allottedServicesResponseModel?.services = it.services
                    setAllottedListData()
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

    private fun setAllottedListData() {
        list.clear()
        binding.chkSelectAll.isChecked = true
        binding.chkSelectAll.text = "Deselect All Services"
        val tempList = allottedServicesResponseModel?.services
        tempList?.forEach {
            list.add(it)
        }
        invalidateServicesCount()
        setServiceAdapter()
    }

    private fun invalidateServicesCount() {
        var totalCheckedItems = 0
        list.forEach {
            if (it.isChecked) {
                totalCheckedItems++
            }
        }
        binding.tvSelectServices.text = "Select Services ($totalCheckedItems)"
    }
}