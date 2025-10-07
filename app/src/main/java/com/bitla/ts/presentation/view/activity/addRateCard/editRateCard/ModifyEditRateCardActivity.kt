package com.bitla.ts.presentation.view.activity.addRateCard.editRateCard

import AddRateCardSingleViewModel
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogSingleListButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ActivityModifyEditRateCardBinding
import com.bitla.ts.domain.pojo.SpinnerItemsModifyFare
import com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.response.FetchRouteWiseFareDetail
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.RouteWiseFareDetail
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.ViewRateCardResponse
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultistationFareDetails
import com.bitla.ts.presentation.adapter.ModifyEditRateCardFareAdapter
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.stringToJson
import com.bitla.ts.utils.constants.SELECT_SERVICE_INTENT_REQUEST_CODE
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_DESTINATION
import com.bitla.ts.utils.sharedPref.PREF_LAST_SEARCHED_DESTINATION
import com.bitla.ts.utils.sharedPref.PREF_LAST_SEARCHED_SOURCE
import com.bitla.ts.utils.sharedPref.PREF_SOURCE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.*
import visible


class ModifyEditRateCardActivity : BaseActivity(), OnItemClickListener,
    DialogSingleListButtonListener {

    private lateinit var binding: ActivityModifyEditRateCardBinding
    private var adapter: ModifyEditRateCardFareAdapter? = null
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var resID: String? = null
    private var locale: String? = ""
    private var sourceId: String? = null
    private var source: String = ""
    private var destinationId: String? = null
    private var destination: String = ""
    private var busType: String? = null
    private var fareDetailsBranchList = mutableListOf<RouteWiseFareDetail>()
    private var filterFareDetailsList = mutableListOf<RouteWiseFareDetail>()
    private var selectedFromCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var selectedToCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var selectedCityPairIdList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var routeWiseFareDetail: RouteWiseFareDetail? = null
    private var fetchRouteWiseFareResponse: ViewRateCardResponse? = null
    
    private val addRateCardSingleViewModel by viewModel<AddRateCardSingleViewModel<Any?>>()
    private var fromViewRateCard = false
    override fun initUI() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityModifyEditRateCardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        WindowCompat.setDecorFitsSystemWindows(window, false)

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
            val fareDetailsBranchList = addRateCardSingleViewModel.branchViewRateCardLiveData.value!!
            addRateCardSingleViewModel.setBranchViewRateResponse(fareDetailsBranchList)

            finish()
        }

        binding.copyAllModifyTV.setOnClickListener {
            DialogUtils.showCopyAllModifyEditAddRateCardDialog(
                context = this,
                fareDetailsList = fareDetailsBranchList,
                dialogSingleButtonListener = this
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getPref() {

        PreferenceUtils.apply {
            bccId = getBccId()
            locale = getlang()
            loginModelPref = getLogin()
            source = getString(getString(R.string.updateRateCard_origin)).toString()
            destination = getString(getString(R.string.updateRateCard_destination)).toString()
            sourceId = getString(getString(R.string.updateRateCard_originId)).toString()
            destinationId = getString(getString(R.string.updateRateCard_destinationId)).toString()
            busType = getString(getString(R.string.updateRateCard_busType))
            setPreference(PREF_SOURCE, source)
            setPreference(PREF_DESTINATION, destination)
            setPreference(PREF_LAST_SEARCHED_SOURCE, source)
            setPreference(PREF_LAST_SEARCHED_DESTINATION, destination)
        }
        
        /*if (intent.hasExtra(getString(R.string.multistation_fare_response_model))) {
            val temp = intent.getStringExtra(getString(R.string.multistation_fare_response_model)) ?: ""
            fetchRouteWiseFareResponse = stringToJson(temp)
        }*/

        if (PreferenceUtils.getString(getString(R.string.multistation_fare_response_model)) != null) {
            val temp = PreferenceUtils.getString(getString(R.string.multistation_fare_response_model)) ?: ""
            fetchRouteWiseFareResponse = stringToJson(temp)
        }

        fromViewRateCard = intent.getBooleanExtra(getString(R.string.from_view_rate_card), false)


        if (!fromViewRateCard) {
            binding.apply {
                updateRatecardToolbar.textHeaderTitle.text = getString(R.string.modify_individual_route_fare)
                applyBT.visible()
                cancelBT.visible()
                copyAllModifyTV.visible()
            }
        } else {
            binding.apply {
                updateRatecardToolbar.textHeaderTitle.text = getString(R.string.modified_fare)
                applyBT.gone()
                cancelBT.gone()
                copyAllModifyTV.gone()
            }
        }

        binding.updateRatecardToolbar.headerTitleDesc.text = busType
        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
        
        fareDetailsBranchList.clear()
        fareDetailsBranchList = fetchRouteWiseFareResponse!!.routeWiseFareDetails

        setModifyIndividualRouteFareAdapter()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setModifyIndividualRouteFareAdapter() {

//        if (addRateCardSingleViewModel.selectedOriginIdList.value?.isNotEmpty() == true) {
//            selectedFromCityList = addRateCardSingleViewModel.selectedOriginIdList.value!!
//        }
//        if (addRateCardSingleViewModel.selectedDestinationIdList.value?.isNotEmpty() == true) {
//            selectedToCityList = addRateCardSingleViewModel.selectedDestinationIdList.value!!
//        }
//
//        fareDetailsBranchList.clear()
//        fareDetailsBranchList = addRateCardSingleViewModel.branchViewRateCardLiveData.value!!.routeWiseFareDetails
//
//        val list = getSelectedCityPairs(selectedFromCityList,selectedToCityList)
//
//        Timber.d("getSelectedCityPairs size: ${list.size}")
//        filterService(list)
        
        if (addRateCardSingleViewModel.selectedOriginIdList.value?.isNotEmpty() == true) {
            selectedFromCityList = addRateCardSingleViewModel.selectedOriginIdList.value!!
        }
        if (addRateCardSingleViewModel.selectedDestinationIdList.value?.isNotEmpty() == true) {
            selectedToCityList = addRateCardSingleViewModel.selectedDestinationIdList.value!!
        }
        
        Timber.d("FareResponseTest= ${fetchRouteWiseFareResponse!!.routeWiseFareDetails} --- ${selectedFromCityList} == ${selectedToCityList}")
        
        
        if ((selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty())
            || (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty())
            || (selectedFromCityList.isEmpty() && selectedToCityList.isEmpty())
        ) {
            filterService(fetchRouteWiseFareResponse!!.routeWiseFareDetails)
        }
        else {
            val list = getSelectedCityPairs(selectedFromCityList, selectedToCityList)
            filterService(list)
        }

    }


    private fun getSelectedCityPairs(fromCityList : MutableList<SpinnerItemsModifyFare>, toCityList: MutableList<SpinnerItemsModifyFare>): ArrayList<RouteWiseFareDetail> {

        val tempFromCityList : ArrayList<RouteWiseFareDetail> = arrayListOf()
        val tempToCityList : ArrayList<RouteWiseFareDetail> = arrayListOf()
        val finalPairCityList : ArrayList<RouteWiseFareDetail> = arrayListOf()


        for (i in 0 until fareDetailsBranchList.size){
            for (j in 0 until fromCityList.size){
                if(fareDetailsBranchList[i].originId == fromCityList[j].id){
                    tempFromCityList.add(fareDetailsBranchList[i])
                }
            }
        }

        for (i in 0 until fareDetailsBranchList.size){
            for (j in 0 until toCityList.size){
                if(fareDetailsBranchList[i].destinationId == toCityList[j].id){
                    tempToCityList.add(fareDetailsBranchList[i])
                }
            }
        }

        val commonValues =  tempFromCityList.intersect(tempToCityList)
        finalPairCityList.addAll(commonValues.toMutableList())
        return finalPairCityList

    }

    private fun filterService(fareDetailsList: MutableList<RouteWiseFareDetail>) {

        if (addRateCardSingleViewModel.selectedCityPairIdList.value?.isNotEmpty() == true) {
            selectedCityPairIdList = addRateCardSingleViewModel.selectedCityPairIdList.value!!
        } else {
            if (addRateCardSingleViewModel.selectedOriginIdList.value?.isNotEmpty() == true) {
                selectedFromCityList = addRateCardSingleViewModel.selectedOriginIdList.value!!
            }
            if (addRateCardSingleViewModel.selectedDestinationIdList.value?.isNotEmpty() == true) {
                selectedToCityList = addRateCardSingleViewModel.selectedDestinationIdList.value!!
            }
        }

        fareDetailsList.forEach {

            if (selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty()) {
                selectedFromCityList.forEachIndexed { index, spinnerItems ->
                    if (it.originId == spinnerItems.id) {
                        filterFareDetailsList.add(it)
                    }
                }
            } else if (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty()) {
                selectedToCityList.forEachIndexed { index, spinnerItems ->
                    if (it.destinationId == spinnerItems.id) {
                        filterFareDetailsList.add(it)
                    }
                }
            } else if (selectedCityPairIdList.isNotEmpty()) {
                selectedCityPairIdList.forEachIndexed { index, spinnerItems2 ->
                    if (spinnerItems2.id.substringBefore("-") == it.originId
                        && spinnerItems2.id.substringAfter("-") == it.destinationId
                    ) {
                        filterFareDetailsList.add(it)
                    }
                }
            } else {
                filterFareDetailsList.add(it)
                
//                selectedFromCityList.forEachIndexed { index, spinnerItems ->
//                    if (it.originId == spinnerItems.id) {
//                        filterFareDetailsList.add(it)
//                    }
//                }
//
//                selectedToCityList.forEachIndexed { index, spinnerItems ->
//                    if (it.destinationId == spinnerItems.id) {
//                        if (filterFareDetailsList.contains(it).not()) {
//                            filterFareDetailsList.add(it)
//                        }
//                    }
//                }
            }
        }

        if (filterFareDetailsList.isNotEmpty()) {
            setAdapter(filterFareDetailsList)
        } else {
            setAdapter(fareDetailsList)
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun setAdapter(fareDetailsList: MutableList<RouteWiseFareDetail>) {
        fareDetailsList.forEachIndexed { indexOuter, it1 ->
            it1.fareDetails.forEachIndexed { indexInner, it ->

                it.fare = it.fare
            }
        }

        if (fromViewRateCard) {

            adapter = ModifyEditRateCardFareAdapter(
                context = this,
                routeWiseFareDetailList = fareDetailsList,
                onItemClickListener = this,
                isFromModifyViewRateCard = true
            ) { item ->
                routeWiseFareDetail = item
            }

            binding.fareRV.adapter = adapter
            adapter?.notifyDataSetChanged()
            binding.bulkModifyTV.text = "${fareDetailsList.size} City Pair"
        } else {
            adapter = ModifyEditRateCardFareAdapter(
                context = this,
                routeWiseFareDetailList = fareDetailsList,
                onItemClickListener = this,
                isFromModifyViewRateCard = false
            ) { item ->
                routeWiseFareDetail = item
            }

            binding.fareRV.adapter = adapter
            adapter?.notifyDataSetChanged()
            binding.bulkModifyTV.text = "${fareDetailsList.size} City Pair"
        }
    }

    private fun getSeatFareDetailsListData() {
        val intent = Intent()
        addRateCardSingleViewModel.branchViewRateCardLiveData.value?.routeWiseFareDetails = fareDetailsBranchList
        addRateCardSingleViewModel.setBranchViewRateResponse(addRateCardSingleViewModel.branchViewRateCardLiveData.value!!)
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
    }

    override fun onSingleButtonClickListFetchFareDetails(list: MutableList<FetchRouteWiseFareDetail>?) {
        for (i in 0 until filterFareDetailsList.size) {
            for (j in 0 until (routeWiseFareDetail?.fareDetails?.size ?: 0)) {
                if (filterFareDetailsList[i].fareDetails[j].seatType.equals(
                        routeWiseFareDetail?.fareDetails?.get(
                            j
                        )?.seatType, true
                    )
                ) {
                    filterFareDetailsList[i].fareDetails[j].fare =
                        routeWiseFareDetail?.fareDetails?.get(j)?.editedFare!!
                }
            }
        }
        adapter?.notifyDataSetChanged()
    }

    override fun onSingleButtonClickListViewFareDetails(list: MutableList<RouteWiseFareDetail>?) {
        for (i in 0 until filterFareDetailsList.size) {
            for (j in 0 until (routeWiseFareDetail?.fareDetails?.size ?: 0)) {
                if (filterFareDetailsList[i].fareDetails[j].seatType.equals(
                        routeWiseFareDetail?.fareDetails?.get(
                            j
                        )?.seatType, true
                    )
                ) {
                    filterFareDetailsList[i].fareDetails[j].fare =
                        routeWiseFareDetail?.fareDetails?.get(j)?.editedFare!!
                }
            }
        }
        adapter?.notifyDataSetChanged()
    }
}

