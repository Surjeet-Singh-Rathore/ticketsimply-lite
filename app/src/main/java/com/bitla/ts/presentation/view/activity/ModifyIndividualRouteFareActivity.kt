package com.bitla.ts.presentation.view.activity

import SingleViewModel
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogSingleListButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ActivityModifyIndividualRouteFareBinding
import com.bitla.ts.domain.pojo.SpinnerItemsModifyFare
import com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.response.FetchRouteWiseFareDetail
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.RouteWiseFareDetail
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultiStationWiseFareResponse
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultistationFareDetails
import com.bitla.ts.presentation.adapter.ModifyIndividualRouteFareAdapter
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.stringToJson
import com.bitla.ts.utils.constants.SELECT_SERVICE_INTENT_REQUEST_CODE
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_DESTINATION
import com.bitla.ts.utils.sharedPref.PREF_LAST_SEARCHED_DESTINATION
import com.bitla.ts.utils.sharedPref.PREF_LAST_SEARCHED_SOURCE
import com.bitla.ts.utils.sharedPref.PREF_SOURCE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast


class ModifyIndividualRouteFareActivity : BaseActivity(), OnItemClickListener,
    DialogSingleListButtonListener {
    
    companion object {
        val TAG = ModifyIndividualRouteFareActivity::class.java.simpleName
    }
    
    private lateinit var binding: ActivityModifyIndividualRouteFareBinding
    private var adapter: ModifyIndividualRouteFareAdapter?= null
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var resID: String? = null
    private var locale: String? = ""
    private var routeId: String? = null
    private var sourceId: String? = null
    private var source: String = ""
    private var destinationId: String? = null
    private var destination: String = ""
    private var busType: String? = null
    private var convertedDate: String? = null
    private var multistationFareResponseModel: MultiStationWiseFareResponse? = null
    private val singleViewModel by viewModel<SingleViewModel<Any?>>()
    private var amountTypeValue: String = ""
    private var amountType: String = ""
    private var incOrDecFare: String = ""
    private var selectedChannelId: String = ""
    private var fareDetailsBranchList = mutableListOf<MultistationFareDetails>()
    private var fareDetailsOnlineList = mutableListOf<MultistationFareDetails>()
    private var fareDetailsOtaList = mutableListOf<MultistationFareDetails>()
    private var fareDetailsEBookingList = mutableListOf<MultistationFareDetails>()
    private var filterFareDetailsList = mutableListOf<MultistationFareDetails>()
    private var tempFilterFareDetailsList = mutableListOf<MultistationFareDetails>()
    private var selectedCityPairOriginId: String = ""
    private var selectedCityPairDestinationId: String = ""
    private var selectedFromCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var selectedToCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var selectedCityPairIdList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    
    private var fetchTemplateFareDetailsBranchList = mutableListOf<MultistationFareDetails>()
    private var fetchTemplateFareDetailsOnlineList = mutableListOf<MultistationFareDetails>()
    private var fetchTemplateFareDetailsOtaList = mutableListOf<MultistationFareDetails>()
    private var fetchTemplateFareDetailsEBookingList = mutableListOf<MultistationFareDetails>()
    private var multistationFareDetails: MultistationFareDetails? = null
    override fun initUI() {
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityModifyIndividualRouteFareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        
        getPref()
        
        binding.applyBT.setOnClickListener {
            getSeatFareDetailsListData()
            finish()
        }
        
        binding.cancelBT.setOnClickListener {
            var fareDetailsBranchList = singleViewModel.branchMultiStationFareLiveData.value!!
            singleViewModel.setBranchMultiStationFareResponse(fareDetailsBranchList)
            
            finish()
        }
        
        binding.copyAllModifyTV.setOnClickListener {
            DialogUtils.showCopyAllModifyDialog(
                context = this,
                fareDetailsList = fareDetailsBranchList,
                dialogSingleButtonListener = this
            )
        }
    }
    
    @SuppressLint("SetTextI18n")
    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
        
        resID = PreferenceUtils.getString(getString(R.string.updateRateCard_resId))
        source = PreferenceUtils.getString(getString(R.string.updateRateCard_origin)).toString()
        destination = PreferenceUtils.getString(getString(R.string.updateRateCard_destination)).toString()
        sourceId = PreferenceUtils.getString(getString(R.string.updateRateCard_originId)).toString()
        destinationId = PreferenceUtils.getString(getString(R.string.updateRateCard_destinationId)).toString()
        busType = PreferenceUtils.getString(getString(R.string.updateRateCard_busType))
//
        PreferenceUtils.setPreference(PREF_SOURCE, source)
        PreferenceUtils.setPreference(PREF_DESTINATION, destination)
//            PreferenceUtils.setPreference(PREF_TRAVEL_DATE, getDateDMY(convertedDate!!)!!)
        PreferenceUtils.setPreference(PREF_LAST_SEARCHED_SOURCE, source)
        PreferenceUtils.setPreference(PREF_LAST_SEARCHED_DESTINATION, destination)
        
        binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.modify_individual_route_fare)
        binding.updateRatecardToolbar.headerTitleDesc.text = busType
        
        if (intent.hasExtra(getString(R.string.multistation_fare_response_model))) {
            val temp = intent.getStringExtra(getString(R.string.multistation_fare_response_model)) ?: ""
            if (temp.trim().startsWith("{")) {
                try {
                    multistationFareResponseModel = stringToJson(temp)
                } catch (e: Exception) {
                    toast(getString(R.string.error_occured))
                }
            }
        }
        if (intent.hasExtra(getString(R.string.multistation_fare_fixed_percent))) {
            amountTypeValue = intent.getStringExtra(getString(R.string.multistation_fare_fixed_percent)) ?: ""
        }
        if (intent.hasExtra(getString(R.string.multistation_amount_type))) {
            amountType = intent.getStringExtra(getString(R.string.multistation_amount_type)) ?: ""
        }
        if (intent.hasExtra(getString(R.string.multistation_incOrDec_fare))) {
            incOrDecFare = intent.getStringExtra(getString(R.string.multistation_incOrDec_fare)) ?: ""
        }
        if (intent.hasExtra(getString(R.string.selected_channel_id))) {
            selectedChannelId = intent.getStringExtra(getString(R.string.selected_channel_id)) ?: ""
        }
        if (intent.hasExtra(getString(R.string.updateRateCard_originId))) {
            selectedCityPairOriginId = intent.getStringExtra(getString(R.string.updateRateCard_originId)) ?: ""
        }
        if (intent.hasExtra(getString(R.string.updateRateCard_destinationId))) {
            selectedCityPairDestinationId = intent.getStringExtra(getString(R.string.updateRateCard_destinationId)) ?: ""
        }
        
        binding.bulkModifyTV.text = "${multistationFareResponseModel?.multistation_fare_details?.size.toString()} City Pair"
        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
        
        setModifyIndividualRouteFareAdapter()
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private fun setModifyIndividualRouteFareAdapter() {
        
        when (singleViewModel.selectedChannelIdLiveData.value) {
            getString(R.string.branch) -> {
                if (singleViewModel.selectedBranchTemplateValueLiveData.value?.isNotEmpty() == true){
                    if ((selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty())
                        || (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty())
                        || (selectedFromCityList.isEmpty() && selectedToCityList.isEmpty())
                    ) {
                        fetchTemplateFareDetailsBranchList.clear()

                        singleViewModel.fetchBranchFareTemplateResponse.value?.multistationFareDetails?.let {
                            fetchTemplateFareDetailsBranchList = it
                            filterService(it)
                        }
                    }
                    else {
                        val list = getSelectedCityPairs(selectedFromCityList, selectedToCityList)
                        filterService(list)
                    }
                } else {
                    if ((selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty())
                        || (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty())
                        || (selectedFromCityList.isEmpty() && selectedToCityList.isEmpty())
                    ) {
                        fareDetailsBranchList.clear()

                        singleViewModel.branchMultiStationFareLiveData.value?.multistation_fare_details?.let {
                            fareDetailsBranchList = it
                            filterService(it)
                        }
                    }
                    else {
                        val list = getSelectedCityPairs(selectedFromCityList, selectedToCityList)
                        filterService(list)
                    }
                }

//                Timber.d("fareDetailsList - $fareDetailsBranchList")
            }
            getString(R.string.online_agent) -> {
                
                if (singleViewModel.selectedBranchTemplateValueLiveData.value?.isNotEmpty() == true){
                    if ((selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty())
                        || (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty())
                        || (selectedFromCityList.isEmpty() && selectedToCityList.isEmpty())
                    ) {
                        fetchTemplateFareDetailsOnlineList.clear()

                        singleViewModel.fetchOnlineFareTemplateResponse.value?.multistationFareDetails?.let {
                            fetchTemplateFareDetailsOnlineList = it
                            filterService(it)
                        }
                    }
                    else {
                        val list = getSelectedCityPairs(selectedFromCityList, selectedToCityList)
                        filterService(list)
                    }
                } else {
                    if ((selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty())
                        || (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty())
                        || (selectedFromCityList.isEmpty() && selectedToCityList.isEmpty())
                    ) {
                        fareDetailsOnlineList.clear()

                        singleViewModel.onlineMultiStationFareResponseLiveData.value?.multistation_fare_details?.let {
                            fareDetailsOnlineList = it
                            filterService(it)
                        }
                    }
                    else {
                        val list = getSelectedCityPairs(selectedFromCityList, selectedToCityList)
                        filterService(list)
                    }
                }
            }
            getString(R.string.otas) -> {
                if (singleViewModel.selectedOtaTemplateValueLiveData.value?.isNotEmpty() == true){
                    if ((selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty())
                        || (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty())
                        || (selectedFromCityList.isEmpty() && selectedToCityList.isEmpty())
                    ) {
                        fetchTemplateFareDetailsOtaList.clear()

                        singleViewModel.fetchOtaFareTemplateResponse.value?.multistationFareDetails?.let {
                            fetchTemplateFareDetailsOtaList = it
                            filterService(it)
                        }
                    }
                    else {
                        val list = getSelectedCityPairs(selectedFromCityList, selectedToCityList)
                        filterService(list)
                    }
                } else {
                    
                    if ((selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty())
                        || (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty())
                        || (selectedFromCityList.isEmpty() && selectedToCityList.isEmpty())
                    ) {
                        fareDetailsOtaList.clear()

                        singleViewModel.oTAMultiStationFareResponseLiveData.value?.multistation_fare_details?.let {
                            fareDetailsOtaList = it
                            filterService(it)
                        }
                    }
                    else {
                        val list = getSelectedCityPairs(selectedFromCityList, selectedToCityList)
                        filterService(list)
                    }
                }
                
            }
            getString(R.string.e_booking) -> {
                if (singleViewModel.selectedOtaTemplateValueLiveData.value?.isNotEmpty() == true){
                    
                    if ((selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty())
                        || (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty())
                        || (selectedFromCityList.isEmpty() && selectedToCityList.isEmpty())
                    ) {
                        fetchTemplateFareDetailsEBookingList.clear()

                        singleViewModel.fetchEBookingFareTemplateResponse.value?.multistationFareDetails?.let {
                            fetchTemplateFareDetailsEBookingList = it
                            filterService(it)
                        }
                    }
                    else {
                        val list = getSelectedCityPairs(selectedFromCityList, selectedToCityList)
                        filterService(list)
                    }
                } else {
                    if ((selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty())
                        || (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty())
                        || (selectedFromCityList.isEmpty() && selectedToCityList.isEmpty())
                    ) {
                        fareDetailsEBookingList.clear()

                        singleViewModel.eBookingMultiStationFareResponseLiveData.value?.multistation_fare_details?.let {
                            fareDetailsEBookingList = it
                            filterService(it)
                        }
                    }
                    else {
                        val list = getSelectedCityPairs(selectedFromCityList, selectedToCityList)
                        filterService(list)
                    }
                }
            }
            else -> {
                if (singleViewModel.selectedBranchTemplateValueLiveData.value?.isNotEmpty() == true){
                    if ((selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty())
                        || (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty())
                        || (selectedFromCityList.isEmpty() && selectedToCityList.isEmpty())
                    ) {
                        fetchTemplateFareDetailsBranchList.clear()

                        singleViewModel.fetchBranchFareTemplateResponse.value?.multistationFareDetails?.let {
                            fetchTemplateFareDetailsBranchList = it
                            filterService(it)
                        }
                    }
                    else {
                        val list = getSelectedCityPairs(selectedFromCityList, selectedToCityList)
                        filterService(list)
                    }
                } else {
                    if ((selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty())
                        || (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty())
                        || (selectedFromCityList.isEmpty() && selectedToCityList.isEmpty())
                    ) {
                        fareDetailsBranchList.clear()

                        singleViewModel.branchMultiStationFareLiveData.value?.multistation_fare_details?.let {
                            fareDetailsBranchList = it
                            filterService(it)
                        }
                    }
                    else {
                        val list = getSelectedCityPairs(selectedFromCityList, selectedToCityList)
                        filterService(list)
                    }
                }

//                Timber.d("filterFareDetailsList = $filterFareDetailsList")
//                Timber.d("filterFareDetailsList_2 = $fareDetailsBranchList")
            }
        }
    }
    
    private fun getSelectedCityPairs(fromCityList : MutableList<SpinnerItemsModifyFare>, toCityList: MutableList<SpinnerItemsModifyFare>): ArrayList<MultistationFareDetails> {
        
        val tempFromCityList: ArrayList<MultistationFareDetails> = arrayListOf()
        val tempToCityList: ArrayList<MultistationFareDetails> = arrayListOf()
        val finalPairCityList: ArrayList<MultistationFareDetails> = arrayListOf()
        
        
        for (i in 0 until fareDetailsBranchList.size) {
            for (j in 0 until fromCityList.size) {
                if (fareDetailsBranchList[i].origin_id == fromCityList[j].id) {
                    tempFromCityList.add(fareDetailsBranchList[i])
                }
            }
        }
        
        for (i in 0 until fareDetailsBranchList.size) {
            for (j in 0 until toCityList.size) {
                if (fareDetailsBranchList[i].destination_id == toCityList[j].id) {
                    tempToCityList.add(fareDetailsBranchList[i])
                }
            }
        }
        
        val commonValues = tempFromCityList.intersect(tempToCityList)
        finalPairCityList.addAll(commonValues.toMutableList())
        Timber.d("finalPairCityList == $finalPairCityList == $fareDetailsBranchList")
        return finalPairCityList
    }
    
    private fun filterService( fareDetailsList: MutableList<MultistationFareDetails>) {
        
        if (singleViewModel.selectedCityPairIdList.value?.isNotEmpty() == true) {
            selectedCityPairIdList = singleViewModel.selectedCityPairIdList.value!!
        } else {
            if (singleViewModel.selectedOriginIdList.value?.isNotEmpty() == true) {
                selectedFromCityList = singleViewModel.selectedOriginIdList.value!!
            }
            if (singleViewModel.selectedDestinationIdList.value?.isNotEmpty() == true) {
                selectedToCityList = singleViewModel.selectedDestinationIdList.value!!
            }
        }
        
        fareDetailsList.forEach {
            
            if (selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty()){
                selectedFromCityList.forEachIndexed { index, spinnerItems ->
                    if (it.origin_id == spinnerItems.id) {
                        filterFareDetailsList.add(it)
                    }
                }
            }
            else if (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty()){
                selectedToCityList.forEachIndexed { index, spinnerItems ->
                    if (it.destination_id == spinnerItems.id) {
                        filterFareDetailsList.add(it)
                    }
                }
            }
            else if (selectedCityPairIdList.isNotEmpty()){
                selectedCityPairIdList.forEachIndexed { index, spinnerItems2 ->
                    if (spinnerItems2.id.substringBefore("-") == it.origin_id
                        && spinnerItems2.id.substringAfter("-") == it.destination_id
                    ) {
                        filterFareDetailsList.add(it)
                    }
                }
            } else{
                selectedFromCityList.forEachIndexed { index, spinnerItems ->
                    if (it.origin_id == spinnerItems.id) {
                        filterFareDetailsList.add(it)
                    }
                }
                
                selectedToCityList.forEachIndexed { index, spinnerItems ->
                    if (it.destination_id == spinnerItems.id) {
                        if (filterFareDetailsList.contains(it).not()){
                            filterFareDetailsList.add(it)
                        }
                    }
                }
            }
        }
        
        if (filterFareDetailsList.isNotEmpty()){
            setAdapter(filterFareDetailsList)
        } else {
            setAdapter(fareDetailsList)
        }
    }
    
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun setAdapter(fareDetailsList : MutableList<MultistationFareDetails>) {

//        Timber.d("fareDetailsList - $fareDetailsList")
//        selectedSeatList = singleViewModel.selectedSeat.value!!
//        Timber.d("seatLisX - $selectedSeatList")
        
        fareDetailsList.forEachIndexed {indexOuter, it1 ->
            it1.fareDetails.forEachIndexed {indexInner, it ->
                
                it.fare = it.fare
                
                /*    val newFareRounded = it.fare.toString().toDouble().roundToInt()
                    Timber.d("fareDetailsList - $newFareRounded")
                    if (amountType == "1") {
                        if (incOrDecFare == "0") {
                            selectedSeatList.forEachIndexed { i, e ->
                                if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                    if (amountTypeValue.isNotEmpty()) {
                                        it.fare = ((newFareRounded + newFareRounded * amountTypeValue.toInt() / 100).toString())
                                    }
                                } else {
                                    it.fare = it.fare
                                }
                            }
                        } else {
                            selectedSeatList.forEachIndexed { i, e ->
                                if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                    if (amountTypeValue.isNotEmpty()) {
                                        it.fare = ((newFareRounded - newFareRounded * amountTypeValue.toInt() / 100).toString())
                                    }
                                } else {
                                    it.fare = it.fare
                                }
                            }
                        }
                    } else {
                        if (incOrDecFare == "0") {
    
                            selectedSeatList.forEachIndexed { i, e ->
                                if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                    if (amountTypeValue.isNotEmpty()) {
                                        it.fare = ((newFareRounded + amountTypeValue.toInt()).toString())
                                    }
                                } else {
                                    it.fare = it.fare
                                }
                            }
    
                        } else {
                            selectedSeatList.forEachIndexed { i, e ->
                                if (selectedSeatList[i].routeId == it.id?.toInt()) {
                                    if (amountTypeValue.isNotEmpty()) {
                                        it.fare = ((newFareRounded - amountTypeValue.toInt()).toString())
                                    }
                                } else {
                                    it.fare = it.fare
                                }
                            }
                        }
                    }*/
            }
        }
        
        adapter = ModifyIndividualRouteFareAdapter(
            context = this,
            multistationFareDetailsList = fareDetailsList,
            onItemClickListener = this
        ) { item ->
            multistationFareDetails = item
        }
        binding.fareRV.adapter = adapter
        adapter?.notifyDataSetChanged()
        binding.bulkModifyTV.text = "${fareDetailsList.size} City Pair"
        
    }
    
    private fun getSeatFareDetailsListData() {

//        Timber.d("fareDetailsList - $fareDetailsBranchList")
//        Timber.d("fareDetailsList_Size - ${fareDetailsBranchList.size}")
        
        val intent = Intent()
        
        when (singleViewModel.selectedChannelIdLiveData.value) {
            getString(R.string.branch) -> {
                
                if (singleViewModel.selectedBranchTemplateValueLiveData.value?.isNotEmpty() == true){
                    singleViewModel.fetchBranchFareTemplateResponse.value!!.multistationFareDetails = fetchTemplateFareDetailsBranchList
                    singleViewModel.setFetchBranchFareTemplateResponse(singleViewModel.fetchBranchFareTemplateResponse.value!!)
                } else {
                    singleViewModel.branchMultiStationFareLiveData.value?.multistation_fare_details = fareDetailsBranchList
                    singleViewModel.setBranchMultiStationFareResponse(singleViewModel.branchMultiStationFareLiveData.value!!)
                }
                Timber.d("fareDetailsList - $fareDetailsBranchList")
            }
            getString(R.string.online_agent) -> {
                if (singleViewModel.selectedOnlineTemplateValueLiveData.value?.isNotEmpty() == true){
                    singleViewModel.fetchOnlineFareTemplateResponse.value!!.multistationFareDetails = fetchTemplateFareDetailsOtaList
                    singleViewModel.setFetchOtaFareTemplateResponse(singleViewModel.fetchOnlineFareTemplateResponse.value!!)
                } else {
                    singleViewModel.onlineMultiStationFareResponseLiveData.value!!.multistation_fare_details = fareDetailsOnlineList
                    singleViewModel.setOnlineMultiStationFareResponse(singleViewModel.onlineMultiStationFareResponseLiveData.value!!)
                }
                
            }
            getString(R.string.otas) -> {
                try {
                    if (singleViewModel.selectedOtaTemplateValueLiveData.value?.isNotEmpty() == true){
                        singleViewModel.fetchOtaFareTemplateResponse.value!!.multistationFareDetails = fetchTemplateFareDetailsOtaList
                        singleViewModel.setFetchOtaFareTemplateResponse(singleViewModel.fetchOtaFareTemplateResponse.value!!)
                    } else {
                        singleViewModel.onlineMultiStationFareResponseLiveData.value!!.multistation_fare_details = fareDetailsOnlineList
                        singleViewModel.setOnlineMultiStationFareResponse(singleViewModel.onlineMultiStationFareResponseLiveData.value!!)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                    toast(e.message)
                }

            }
            getString(R.string.e_booking) -> {
                
                if (singleViewModel.selectedOtaTemplateValueLiveData.value?.isNotEmpty() == true){
                    singleViewModel.fetchEBookingFareTemplateResponse.value!!.multistationFareDetails = fetchTemplateFareDetailsEBookingList
                    singleViewModel.setFetchEBookingFareTemplateResponse(singleViewModel.fetchEBookingFareTemplateResponse.value!!)
                } else {
                    singleViewModel.eBookingMultiStationFareResponseLiveData.value!!.multistation_fare_details = fareDetailsEBookingList
                    singleViewModel.setEBookingMultiStationFareResponse(singleViewModel.eBookingMultiStationFareResponseLiveData.value!!)
                }
            }
            else -> {
                if (singleViewModel.selectedBranchTemplateValueLiveData.value?.isNotEmpty() == true){
                    singleViewModel.fetchBranchFareTemplateResponse.value!!.multistationFareDetails = fetchTemplateFareDetailsBranchList
                    singleViewModel.setFetchBranchFareTemplateResponse(singleViewModel.fetchBranchFareTemplateResponse.value!!)
                } else {
                    singleViewModel.branchMultiStationFareLiveData.value?.multistation_fare_details = fareDetailsBranchList
                    singleViewModel.setBranchMultiStationFareResponse(singleViewModel.branchMultiStationFareLiveData.value!!)
                }
//                Timber.d("filterFareDetailsList = $filterFareDetailsList")
//                Timber.d("filterFareDetailsList_2 = $fareDetailsBranchList")
            }
        }
        
        when (singleViewModel.selectedChannelIdLiveData.value) {
            
            getString(R.string.otas) -> {
                singleViewModel.onlineMultiStationFareResponseLiveData.value!!.multistation_fare_details = fareDetailsOnlineList
                singleViewModel.setOnlineMultiStationFareResponse(singleViewModel.onlineMultiStationFareResponseLiveData.value!!)
            }
            getString(R.string.e_booking) -> {
            
            }
            
            else -> {
                singleViewModel.branchMultiStationFareLiveData.value?.multistation_fare_details = fareDetailsBranchList
                singleViewModel.setBranchMultiStationFareResponse(singleViewModel.branchMultiStationFareLiveData.value!!)
            }
        }
        setResult(SELECT_SERVICE_INTENT_REQUEST_CODE, intent)
        finish()
    }
    
    override fun isInternetOnCallApisAndInitUI() {
    }
    
    override fun onClickOfNavMenu(position: Int) {
    }
    
    override fun onClick(view: View, position: Int) {
    }
    
    override fun onButtonClick(view: Any, dialog: Dialog) {
    }
    
    override fun onClickOfItem(data: String, position: Int) {
    }
    
    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }
    
    @SuppressLint("NotifyDataSetChanged")
    override fun onSingleButtonClickList(list: MutableList<MultistationFareDetails>?) {
        when (singleViewModel.selectedChannelIdLiveData.value) {
            getString(R.string.branch) -> {
                for (i in 0 until fareDetailsBranchList.size){
                    for (j in 0 until (multistationFareDetails?.fareDetails?.size ?: 0)){
                        if (fareDetailsBranchList[i].fareDetails[j].seatType.equals(multistationFareDetails?.fareDetails?.get(j)?.seatType, true)) {
                            fareDetailsBranchList[i].fareDetails[j].fare = multistationFareDetails?.fareDetails?.get(j)?.editedFare
                        }
                    }
                }
            }
            getString(R.string.online_agent) -> {
                for (i in 0 until fareDetailsOnlineList.size){
                    for (j in 0 until (multistationFareDetails?.fareDetails?.size ?: 0)){
                        if (fareDetailsOnlineList[i].fareDetails[j].seatType.equals(multistationFareDetails?.fareDetails?.get(j)?.seatType, true)) {
                            fareDetailsOnlineList[i].fareDetails[j].fare = multistationFareDetails?.fareDetails?.get(j)?.editedFare
                        }
                    }
                }
            }
            getString(R.string.otas) -> {
                for (i in 0 until fareDetailsOtaList.size){
                    for (j in 0 until (multistationFareDetails?.fareDetails?.size ?: 0)){
                        if (fareDetailsOtaList[i].fareDetails[j].seatType.equals(multistationFareDetails?.fareDetails?.get(j)?.seatType, true)) {
                            fareDetailsOtaList[i].fareDetails[j].fare = multistationFareDetails?.fareDetails?.get(j)?.editedFare
                        }
                    }
                }
            }
            getString(R.string.e_booking) -> {
                for (i in 0 until fareDetailsEBookingList.size){
                    for (j in 0 until (multistationFareDetails?.fareDetails?.size ?: 0)){
                        if (fareDetailsEBookingList[i].fareDetails[j].seatType.equals(multistationFareDetails?.fareDetails?.get(j)?.seatType, true)) {
                            fareDetailsEBookingList[i].fareDetails[j].fare = multistationFareDetails?.fareDetails?.get(j)?.editedFare
                        }
                    }
                }
            }
            else -> {
                if ((selectedFromCityList.isNotEmpty() || selectedToCityList.isNotEmpty() || selectedCityPairIdList.isNotEmpty())
                ) {
                    for (i in 0 until filterFareDetailsList.size) {
                        for (j in 0 until (multistationFareDetails?.fareDetails?.size ?: 0)) {
                            if (filterFareDetailsList[i].fareDetails[j].seatType.equals(multistationFareDetails?.fareDetails?.get(j)?.seatType, true)
                            ) {
                                filterFareDetailsList[i].fareDetails[j].fare = multistationFareDetails?.fareDetails?.get(j)?.editedFare!!
                            }
                        }
                    }
                }
                else {
                    for (i in 0 until fareDetailsBranchList.size){
                        for (j in 0 until (multistationFareDetails?.fareDetails?.size ?: 0)){
                            if (fareDetailsBranchList[i].fareDetails[j].seatType.equals(multistationFareDetails?.fareDetails?.get(j)?.seatType, true)) {
                                fareDetailsBranchList[i].fareDetails[j].fare = multistationFareDetails?.fareDetails?.get(j)?.editedFare
                            }
                        }
                    }
                }
            }
        }
        adapter?.notifyDataSetChanged()
    }
    
    override fun onSingleButtonClickListFetchFareDetails(list: MutableList<FetchRouteWiseFareDetail>?) {
    }
    
    override fun onSingleButtonClickListViewFareDetails(list: MutableList<RouteWiseFareDetail>?) {
    }
}