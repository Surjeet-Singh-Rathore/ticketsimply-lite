package com.bitla.ts.presentation.view.activity.reservationOption

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.appcompat.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
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

class SelectServiceActivity : BaseActivity(), OnItemClickListener,
    DialogSingleButtonListener, OnItemPassData {

    private lateinit var binding: ActivitySelectServiceBinding

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private val availableRoutesViewModel by viewModel<AvailableRoutesViewModel<Any?>>()

    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var source: String? = ""
    private var destination: String? = ""
    private var oldreservationId: Long? = 0L
    private var ymdDate: String = ""
    private var availableRoutesList = mutableListOf<Result>()
    private var travelDate: String = ""
    private var newTravelDate: String = ""
    private var selectedApi: String = ""
    private var apiNumber: String = ""
    private var selectedReservationId: String = ""
    private var fromAtivity = PreferenceUtils.getPreference("fromTicketDetail", false)
    private var selectionType: Int = 0
    private lateinit var serviceAdapter: SelectServiceAdapter
    private var tempList = arrayListOf<Result>()
    private var serviceListFilter = arrayListOf<Result>()
    private var locale: String? = ""
    private var isBima: Boolean? = null
    private var parentTravelId = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbarImageLeft.setOnClickListener {
            PreferenceUtils.setPreference("shiftTypeOption", "")
            onBackPressed()
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
        initUI()
    }


    override fun initUI() {
        binding = ActivitySelectServiceBinding.inflate(layoutInflater)

        getPrefs()
        availableRoutesApi()
        availableRoutesObserver()
        binding.etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                serviceListFilter.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())
                
                if (searchText.isNotEmpty()) {
                    for (i in 0..availableRoutesList.size.minus(1)) {
                        
                        if (availableRoutesList[i].number.lowercase(Locale.getDefault())
                                .contains(searchText)
                        ) {
                            if (!availableRoutesList[i].is_service_blocked) {
                                serviceListFilter.add(availableRoutesList[i])
                            }
                        }
                    }
                    binding.rvApi.adapter?.notifyDataSetChanged()
                    
                } else {
                    serviceListFilter.clear()
                    for (i in 0..availableRoutesList.size.minus(1)) {
                        if (!availableRoutesList[i].is_service_blocked) {
                            serviceListFilter.add(availableRoutesList[i])
                        }
                    }
                    binding.rvApi.adapter?.notifyDataSetChanged()
                }
                return false
            }
        })
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        lifecycleScope.launch {
            availableRoutesViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                    showToast(it)
                }
            }
        }
    }


    private fun selectServiceAdapter(availableRoutes: MutableList<Result>) {
        layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        
        binding.rvApi.layoutManager = layoutManager
        serviceAdapter = SelectServiceAdapter(
            context = this@SelectServiceActivity,
            searchList = availableRoutes,
            onItemClickListener = this,
            onItemPassData = this
        )
        binding.rvApi.adapter = serviceAdapter
    }


    private fun getPrefs() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        oldreservationId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)

        newTravelDate = PreferenceUtils.getString("shiftPassenger_selectedDate")!!

        sourceId = PreferenceUtils.getString("SHIFT_originId")!!
        destinationId = PreferenceUtils.getString("SHIFT_destinationId")!!

        selectionType = intent.getIntExtra(
            "selectionType", 0
        )
        
        val isFromPickupChart = intent.getBooleanExtra(
            "isFromPickupChart", false
        )

        if (!isFromPickupChart){
            if (PreferenceUtils.getPreference("is_bima", false)!!) {
                isBima = true
            }
            
            parentTravelId = PreferenceUtils.getString("parent_travel_id") ?: ""
        }
        
//        Timber.d("isBima_SelectService - $isBima = $parentTravelId")
    }

    private fun availableRoutesApi() {
        
        if (this.isNetworkAvailable()) {
            availableRoutesViewModel.availableRoutesApi(
                apiKey = loginModelPref.api_key,
                originId = sourceId,
                destinationId = destinationId,
                showInJourneyServices = response_format,
                isCsShared = false,
                operatorkey = operator_api_key,
                responseFormat = format_type,
                travelDate = newTravelDate,
                showOnlyAvalServices = "true",
                locale = locale!!,
                apiType = available_routes_method_name,
                appBimaEnabled = isBima ?: false,
            )
        } else this.noNetworkToast()
        
        
    }

    private fun availableRoutesObserver() {
        availableRoutesViewModel.dataAvailableRoutes.observe(this, Observer { it ->
            
            binding.progressBarList.gone()
            tempList.clear()
            serviceListFilter.clear()
            
            if (it != null) {
                when (it.code) {
                    200 -> {
                        PreferenceUtils.putObject(it, PREF_AVAILABLE_ROUTES_RESPONSE)
                        availableRoutesList = it.result
                        
                        tempList.addAll(availableRoutesList)
                        
                        if (tempList.size == 0) {
                            binding.NoResult.visible()
                        } else {
                            for (i in 0 until availableRoutesList.size) {
                                
                                if (isBima == true) {
                                    if (!tempList[i].is_service_blocked && tempList[i].is_bima == true) {
                                        if (parentTravelId.toInt() == tempList[i].parent_travel_id) {
                                            serviceListFilter.add(tempList[i])
                                        }
                                    }
                                } else {
                                    if (!tempList[i].is_service_blocked) {
                                        serviceListFilter.add(tempList[i])
                                    }
                                }
                            }
                            
                            binding.NoResult.gone()
                        }
                        
                        selectServiceAdapter(serviceListFilter)
                    }
                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }
                    else -> {
                        if (it.message != null) {
                            it.message.let { it1 -> toast(it1) }
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        })
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {

        if (view != null) {
            Timber.d("selectiontype $position")

            val intent = Intent()
            selectedReservationId = position.toString()

            intent.putExtra("ApiNameSelected", selectedApi)
            intent.putExtra("selectedReservation", selectedReservationId)
            setResult(RESULT_OK, intent)
            PreferenceUtils.setPreference("shiftPassenger_tab", 2)
            PreferenceUtils.putString("BulkShiftBack_apiNamr", selectedApi)
            PreferenceUtils.putString("BulkShiftBack_resId", selectedReservationId)
            PreferenceUtils.putString("BulkShiftBack", "yes")

            if (fromAtivity!!) {

                finish()

            } else {
                DialogUtils.dialogShiftOption(
                    this,
                    selectedApi,
                    apiNumber,
                    this,
                    selectionType
                )

            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        } else {
            val shiftTypeOption = PreferenceUtils.getPreference("shiftTypeOption", "")

//            toast("$shiftTypeOption")

            firebaseLogEvent(
                this,
                SHIFT_PAX_SEAT_OPTIONS,
                loginModelPref.userName,
                loginModelPref.travels_name,
                loginModelPref.role,
                SHIFT_PAX_SEAT_OPTIONS,
                "$shiftTypeOption"
            )
            finish()
        }
    }

    override fun onItemData(view: View, str1: String, str2: String) {
        apiNumber = str2
        selectedApi = str1
    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
    }
}


