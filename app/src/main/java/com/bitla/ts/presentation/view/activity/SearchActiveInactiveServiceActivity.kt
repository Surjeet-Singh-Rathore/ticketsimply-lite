package com.bitla.ts.presentation.view.activity

import android.content.Intent
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.activate_deactivate_service
import com.bitla.ts.data.active_inactive_services
import com.bitla.ts.databinding.ActivityActiveInactiveServiceBinding
import com.bitla.ts.domain.pojo.activate_deactivate_service.request.ActivateDeactivateServiceRequest
import com.bitla.ts.domain.pojo.active_inactive_services.request.ActiveInactiveServicesRequest
import com.bitla.ts.domain.pojo.active_inactive_services.response.ActiveInactiveServicesResponse
import com.bitla.ts.domain.pojo.active_inactive_services.response.Service
import com.bitla.ts.domain.pojo.alloted_services.AllotedServicesResponseModel
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.adapter.SearchActiveInactiveServiceAdapter
import com.bitla.ts.presentation.viewModel.OccupancyGridViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.jsonToString
import com.bitla.ts.utils.common.stringToJson
import com.bitla.ts.utils.constants.ACTIVE_SERVICE
import com.bitla.ts.utils.constants.INACTIVE_SERVICE
import com.bitla.ts.utils.constants.SELECT_SERVICE_INTENT_REQUEST_CODE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible
import java.util.*

class SearchActiveInactiveServiceActivity : BaseActivity() {

    private lateinit var binding: ActivityActiveInactiveServiceBinding
    private val occupancyGridViewModel by viewModel<OccupancyGridViewModel<Any?>>()
    private var loginModelPref: LoginModel = LoginModel()
    private var fromDate: String = ""
    private var activeServiceList = mutableListOf<Service?>()
    private var inactiveServiceList = mutableListOf<Service?>()
    private var filteredActiveServiceList = mutableListOf<Service?>()
    private var filteredInactiveServiceList = mutableListOf<Service?>()
    private lateinit var searchActiveServiceAdapter: SearchActiveInactiveServiceAdapter
    private lateinit var searchInactiveServiceAdapter: SearchActiveInactiveServiceAdapter
    private var activeInactiveServicesResponse: ActiveInactiveServicesResponse? = null
    private var allottedServicesResponseModel: AllotedServicesResponseModel? = null
    private var isFilterActiveInactiveServicesFromAllottedServiceList = true

    override fun initUI() {
        binding = ActivityActiveInactiveServiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        getPref()
        getParentIntent()
        initAdapterData()
        lifecycleScope.launch {
            occupancyGridViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        hideLoader()
        //callActiveInactiveServiceApi()
        //setObserver()

        binding.rbActive.setOnClickListener {
            setActiveServicesData()
            binding.etSearch.setText("")
            invalidateAllServiceCheckbox()
            /*activeServiceList.forEach {
                it?.isChecked = true
            }*/
        }

        binding.rbInactive.setOnClickListener {
            setInactiveServicesData()
            binding.etSearch.setText("")
            invalidateAllServiceCheckbox()
            /*inactiveServiceList.forEach {
                it?.isChecked = true
            }*/
        }

        binding.btnSelectServices.setOnClickListener {
            //callActivateDeactivateServiceApi()
            onServicesSelected()
        }

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

        binding.chkSelectAll.setOnClickListener {
            if(binding.rbActive.isChecked) {
                if (binding.chkSelectAll.isChecked) {
                    activeServiceList.forEach {
                        it?.isChecked = true
                    }
                    binding.chkSelectAll.text = getString(R.string.deselect_all_services)
                } else {
                    activeServiceList.forEach {
                        it?.isChecked = false
                    }
                    binding.chkSelectAll.text = getString(R.string.select_all_services)
                }

                if (::searchActiveServiceAdapter.isInitialized) {
                    if (filteredActiveServiceList.size > 0) {
                        searchActiveServiceAdapter.addData(filteredActiveServiceList)

                    } else {
                        searchActiveServiceAdapter.addData(activeServiceList)
                    }
                }
            } else {
                if (binding.chkSelectAll.isChecked) {
                    inactiveServiceList.forEach {
                        it?.isChecked = true
                    }
                    binding.chkSelectAll.text = getString(R.string.deselect_all_services)
                } else {
                    inactiveServiceList.forEach {
                        it?.isChecked = false
                    }
                    binding.chkSelectAll.text = getString(R.string.select_all_services)
                }

                if (::searchInactiveServiceAdapter.isInitialized) {
                    if (filteredInactiveServiceList.size > 0) {
                        searchInactiveServiceAdapter.addData(filteredInactiveServiceList)

                    } else {
                        searchInactiveServiceAdapter.addData(inactiveServiceList)
                    }
                }
            }
        }

        binding.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }

    }

    private fun initAdapterData() {

        if (activeInactiveServicesResponse?.activeService?.isNotEmpty() == true) {

            binding.NoResult.gone()

            binding.chkSelectAll.visible()
            binding.rvService.visible()
            binding.btnSelectServices.visible()

            activeServiceList = activeInactiveServicesResponse?.activeService?.toMutableList() ?: mutableListOf()
            inactiveServiceList = activeInactiveServicesResponse?.inactiveService?.toMutableList() ?: mutableListOf()
            setActiveServicesData()

        } else {
            binding.chkSelectAll.gone()
            binding.rvService.gone()
            binding.btnSelectServices.gone()

            binding.NoResult.visible()
            binding.noResultText.text = activeInactiveServicesResponse?.message
        }
    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        fromDate = PreferenceUtils.getDashboardCurrentDate()
        allottedServicesResponseModel =
            PreferenceUtils.getObject<AllotedServicesResponseModel>("allotted_services_model_dashboard")
    }

    private fun getParentIntent() {
        if (intent.hasExtra("from_date")) {
            fromDate =
                intent.getStringExtra("from_date") ?: PreferenceUtils.getDashboardCurrentDate()
        }

        if (intent.hasExtra("active_inactive_services_response")) {
            val  response =
                intent.getStringExtra("active_inactive_services_response") ?: PreferenceUtils.getDashboardCurrentDate()

            activeInactiveServicesResponse = stringToJson(response)
        }

        if (intent.hasExtra("filter_active_inactive_services_from_allotted_service_list")) {

            isFilterActiveInactiveServicesFromAllottedServiceList =
                intent.getBooleanExtra("filter_active_inactive_services_from_allotted_service_list", true)

        }
    }

    private fun callActiveInactiveServiceApi() {

        val activeInactiveServicesRequest = ActiveInactiveServicesRequest(
            apiKey = loginModelPref.api_key,
            from = fromDate,
            to = fromDate
        )

        occupancyGridViewModel.activeInactiveServicesApi(
            activeInactiveServicesRequest,
            active_inactive_services
        )

        showLoader()
    }

    private fun setObserver() {
        occupancyGridViewModel.activeInactiveServices.observe(this) {
            hideLoader()
            if (it?.code == 200) {
                if (it.activeService?.isNotEmpty() == true) {

                    binding.NoResult.gone()

                    binding.chkSelectAll.visible()
                    binding.rvService.visible()
                    binding.btnSelectServices.visible()

                    activeServiceList = it.activeService?.toMutableList() ?: mutableListOf()
                    inactiveServiceList = it.inactiveService?.toMutableList() ?: mutableListOf()
                    setActiveServiceAdapter()
                } else {

                    binding.chkSelectAll.gone()
                    binding.rvService.gone()
                    binding.btnSelectServices.gone()

                    binding.NoResult.visible()
                    binding.noResultText.text = it.message
                }
            } else {
                binding.chkSelectAll.gone()
                binding.rvService.gone()
                binding.btnSelectServices.gone()

                binding.NoResult.visible()
                binding.noResultText.text = it?.message
            }
        }

        occupancyGridViewModel.activateDeactivateService.observe(this) {
            hideLoader()
            if (it != null) {
                if (it.code == 200) {
                    toast(it.message)
                    finish()
                } else {
                    toast(it.message)
                }

            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun setActiveServicesData() {
        invalidateAllServiceCheckbox()

        //invalidateServicesCount()

        setActiveServiceAdapter()
    }

    private fun setInactiveServicesData() {

        invalidateAllServiceCheckbox()

        //invalidateServicesCount()

        setInactiveServiceAdapter()
    }

    /*private fun invalidateServicesCount() {
        var totalCheckedItems = 0
        list.forEach {
            if (it.isChecked) {
                totalCheckedItems++
            }
        }
        binding.tvSelectServices.text = "${getString(R.string.select_services)} ($totalCheckedItems)"

    }*/


    private fun setActiveServiceAdapter() {

        searchActiveServiceAdapter =
            SearchActiveInactiveServiceAdapter(this) { serviceId, isChecked ->
                val index = activeServiceList.indexOfFirst {
                    it?.serviceId == serviceId
                }
                if (index != -1) {
                    activeServiceList[index]?.isChecked = isChecked
                }

                invalidateAllServiceCheckbox()
            }
        searchActiveServiceAdapter.addData(activeServiceList)

        binding.rvService.adapter = searchActiveServiceAdapter

    }

    private fun setInactiveServiceAdapter() {

        searchInactiveServiceAdapter =
            SearchActiveInactiveServiceAdapter(this) { serviceId, isChecked ->
                val index = inactiveServiceList.indexOfFirst {
                    it?.serviceId == serviceId
                }
                if (index != -1) {
                    inactiveServiceList[index]?.isChecked = isChecked
                }

                invalidateAllServiceCheckbox()
            }

        searchInactiveServiceAdapter.addData(inactiveServiceList)

        binding.rvService.adapter = searchInactiveServiceAdapter

    }

    private fun callActivateDeactivateServiceApi() {
        val serviceList =
            mutableListOf<com.bitla.ts.domain.pojo.activate_deactivate_service.request.Service?>()
        if (binding.rbActive.isChecked) {
            activeServiceList.forEach {
                if (it?.isChecked == false) {
                    val item = com.bitla.ts.domain.pojo.activate_deactivate_service.request.Service(
                        activate = false,
                        serviceId = it.serviceId
                    )
                    serviceList.add(item)
                }
            }
        } else {
            inactiveServiceList.forEach {
                if (it?.isChecked == false) {
                    val item = com.bitla.ts.domain.pojo.activate_deactivate_service.request.Service(
                        activate = true,
                        serviceId = it.serviceId
                    )
                    serviceList.add(item)
                }
            }
        }
        val activateDeactivateServiceRequest = ActivateDeactivateServiceRequest(
            serviceList = serviceList
        )
        occupancyGridViewModel.activateDeactivateServiceApi(
            apikey = loginModelPref.api_key,
            activateDeactivateServiceRequest = activateDeactivateServiceRequest,
            apiType = activate_deactivate_service
        )

        showLoader()
    }

    private fun search(text: String) {

        filteredActiveServiceList = mutableListOf()
        filteredInactiveServiceList = mutableListOf()

        if (binding.rbActive.isChecked) {
            for (s in activeServiceList) {

                if (s?.serviceId != null && s.serviceName!!.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    filteredActiveServiceList.add(s)
                }
            }

            if (::searchActiveServiceAdapter.isInitialized) {
                searchActiveServiceAdapter.addData(filteredActiveServiceList)
            }
        } else {
            for (s in inactiveServiceList) {

                if (s?.serviceId != null && s.serviceName!!.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    filteredInactiveServiceList.add(s)
                }
            }

            if (::searchInactiveServiceAdapter.isInitialized) {
                searchInactiveServiceAdapter.addData(filteredInactiveServiceList)
            }
        }
    }

    private fun showLoader() {
        binding.progressBar.visible()
    }

    private fun hideLoader() {
        binding.progressBar.gone()
    }

    private fun onServicesSelected() {
        val intent = Intent()

        if(activeInactiveServicesResponse != null) {
            val serviceIdList = arrayListOf<String?>()
            if (binding.rbActive.isChecked) {
                val isAllUnChecked = activeServiceList.all { it?.isChecked == false }
                if (isAllUnChecked) {
                    toast(getString(R.string.pleaseSelectAtleastOneService))
                    return
                }
                activeServiceList.forEach {
                    if (it?.isChecked == true) {
                        serviceIdList.add(it.routeId ?: "")
                    }
                }

                activeInactiveServicesResponse?.activeService = activeServiceList

                activeInactiveServicesResponse?.inactiveService?.forEach {
                    it?.isChecked = false
                }

                intent.putExtra(
                    "service_filter_type",
                    ACTIVE_SERVICE
                )
            } else {
                val isAllUnChecked = inactiveServiceList.all { it?.isChecked == false }
                if (isAllUnChecked) {
                    toast(getString(R.string.pleaseSelectAtleastOneService))
                    return
                }


                inactiveServiceList.forEach {
                    if (it?.isChecked == true) {
                        serviceIdList.add(it.routeId ?: "")
                    }
                }

                activeInactiveServicesResponse?.inactiveService = inactiveServiceList

                activeInactiveServicesResponse?.activeService?.forEach {
                    it?.isChecked = false
                }

                intent.putExtra(
                    "service_filter_type",
                    INACTIVE_SERVICE
                )
            }

            //intent.putStringArrayListExtra("selected_service_id_list", serviceIdList)
            intent.putExtra(
                "active_inactive_services_response",
                jsonToString(activeInactiveServicesResponse!!)
            )
            setResult(SELECT_SERVICE_INTENT_REQUEST_CODE, intent)
            finish()
        }
    }

    private fun invalidateAllServiceCheckbox() {
        var flag = true
        if(binding.rbActive.isChecked) {
            activeServiceList.forEach {
                if (it?.isChecked == false) {
                    flag = false
                    return@forEach
                }
            }
        } else {
            inactiveServiceList.forEach {
                if (it?.isChecked == false) {
                    flag = false
                    return@forEach
                }
            }
        }
        if(flag) {
            binding.chkSelectAll.isChecked = true
            binding.chkSelectAll.text = getString(R.string.deselect_all_services)
        } else {

            binding.chkSelectAll.isChecked = false
            binding.chkSelectAll.text = getString(R.string.select_all_services)
        }
    }
}