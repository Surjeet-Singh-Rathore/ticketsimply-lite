package com.bitla.ts.presentation.view.activity.addRateCard.createRateCard

import AddRateCardSingleViewModel
import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.view.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.response.*
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*


class ModifyCreateRateCardActivity : BaseActivity(), OnItemClickListener,
    DialogSingleListButtonListener {

    private lateinit var binding: ActivityModifyCreateRateCardBinding
    private var adapter: ModifyCreateRateCardFareAdapter? = null
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var resID: String? = null
    private var locale: String? = ""
    private var sourceId: String? = null
    private var source: String = ""
    private var destinationId: String? = null
    private var destination: String = ""
    private var busType: String? = null
    private var fareDetailsBranchList = mutableListOf<FetchRouteWiseFareDetail>()
    private var filterFareDetailsList = mutableListOf<FetchRouteWiseFareDetail>()
    private var selectedFromCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var selectedToCityList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var selectedCityPairIdList: MutableList<SpinnerItemsModifyFare> = mutableListOf()
    private var routeWiseFareDetail: FetchRouteWiseFareDetail? = null
    private var fetchRouteWiseFareResponse: FetchRouteWiseFareResponse? = null
    private val addRateCardSingleViewModel by viewModel<AddRateCardSingleViewModel<Any?>>()

    override fun initUI() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityModifyCreateRateCardBinding.inflate(layoutInflater)
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
            val fareDetailsBranchList =
                addRateCardSingleViewModel.branchFetchRouteWiseFareLiveData.value!!
            addRateCardSingleViewModel.setBranchFetchRouteWiseFareResponse(fareDetailsBranchList)

            finish()
        }

        binding.copyAllModifyTV.setOnClickListener {
            
            DialogUtils.showCopyAllModifyAddRateCardDialog(
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
            resID = getString(getString(R.string.updateRateCard_resId))
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

        binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.modify_individual_route_fare)
        binding.updateRatecardToolbar.headerTitleDesc.text = busType

        if (intent.hasExtra(getString(R.string.multistation_fare_response_model))) {
            val temp = intent.getStringExtra(getString(R.string.multistation_fare_response_model)) ?: ""
            fetchRouteWiseFareResponse = stringToJson(temp)
        }

        binding.bulkModifyTV.text = "${fetchRouteWiseFareResponse?.fetchRouteWiseFareDetails?.size.toString()} City Pair"
        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
        
        fareDetailsBranchList.clear()
        fareDetailsBranchList = fetchRouteWiseFareResponse!!.fetchRouteWiseFareDetails
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
//        fareDetailsBranchList = addRateCardSingleViewModel.branchFetchRouteWiseFareLiveData.value!!.fetchRouteWiseFareDetails
//
//        val list = getSelectedCityPairs(selectedFromCityList,selectedToCityList)
//        filterService(list)
        
        if (addRateCardSingleViewModel.selectedOriginIdList.value?.isNotEmpty() == true) {
            selectedFromCityList = addRateCardSingleViewModel.selectedOriginIdList.value!!
        }
        if (addRateCardSingleViewModel.selectedDestinationIdList.value?.isNotEmpty() == true) {
            selectedToCityList = addRateCardSingleViewModel.selectedDestinationIdList.value!!
        }

        
        Timber.d("FareResponseTest= ${fetchRouteWiseFareResponse!!.fetchRouteWiseFareDetails} --- ${selectedFromCityList} == ${selectedToCityList}")
        
        if ((selectedFromCityList.isNotEmpty() && selectedToCityList.isEmpty())
            || (selectedToCityList.isNotEmpty() && selectedFromCityList.isEmpty())
            || (selectedFromCityList.isEmpty() && selectedToCityList.isEmpty())
        ) {
            filterService(fetchRouteWiseFareResponse!!.fetchRouteWiseFareDetails)
        }
        else {
            val list = getSelectedCityPairs(selectedFromCityList, selectedToCityList)
            filterService(list)
        }
    }

    private fun getSelectedCityPairs(fromCityList : MutableList<SpinnerItemsModifyFare>, toCityList: MutableList<SpinnerItemsModifyFare>): ArrayList<FetchRouteWiseFareDetail> {

        val tempFromCityList : ArrayList<FetchRouteWiseFareDetail> = arrayListOf()
        val tempToCityList : ArrayList<FetchRouteWiseFareDetail> = arrayListOf()
        val finalPairCityList : ArrayList<FetchRouteWiseFareDetail> = arrayListOf()
        
        
        for (i in 0 until fareDetailsBranchList.size) {
            for (j in 0 until fromCityList.size) {
                if (fareDetailsBranchList[i].originId == fromCityList[j].id) {
                    tempFromCityList.add(fareDetailsBranchList[i])
                }
            }
        }
        
        for (i in 0 until fareDetailsBranchList.size) {
            for (j in 0 until toCityList.size) {
                if (fareDetailsBranchList[i].destinationId == toCityList[j].id) {
                    tempToCityList.add(fareDetailsBranchList[i])
                }
            }
        }

        val commonValues =  tempFromCityList.intersect(tempToCityList)
        finalPairCityList.addAll(commonValues.toMutableList())
        Timber.d("finalPairCityList == $finalPairCityList == $fareDetailsBranchList")
        return finalPairCityList

    }

    private fun filterService(fareDetailsList: MutableList<FetchRouteWiseFareDetail>) {

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
            }
            else {
                
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
    private fun setAdapter(fareDetailsList: MutableList<FetchRouteWiseFareDetail>) {

//        Timber.d("fareDetailsList - $fareDetailsList")
//        selectedSeatList = addRateCardSingleViewModel.selectedSeat.value!!
//        Timber.d("seatLisX - $selectedSeatList")

        fareDetailsList.forEachIndexed { indexOuter, it1 ->
            it1.fareDetails.forEachIndexed { indexInner, it ->

                it.fare = it.fare
            }
        }

        adapter = ModifyCreateRateCardFareAdapter(
            context = this,
            fareDetailsList,
            onItemClickListener = this
        ) { item ->
            routeWiseFareDetail = item
        }
        binding.fareRV.adapter = adapter
        adapter?.notifyDataSetChanged()
        binding.bulkModifyTV.text = "${fareDetailsList.size} City Pair"

    }

    private fun getSeatFareDetailsListData() {

//        Timber.d("fareDetailsList - $fareDetailsBranchList")
//        Timber.d("fareDetailsList_Size - ${fareDetailsBranchList.size}")

        val intent = Intent()

        addRateCardSingleViewModel.branchFetchRouteWiseFareLiveData.value?.fetchRouteWiseFareDetails = fareDetailsBranchList
        addRateCardSingleViewModel.setBranchFetchRouteWiseFareResponse(addRateCardSingleViewModel.branchFetchRouteWiseFareLiveData.value!!)

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
                        routeWiseFareDetail?.fareDetails?.get(j)?.seatType, true
                    )
                ) {
                    filterFareDetailsList[i].fareDetails[j].fare = routeWiseFareDetail?.fareDetails?.get(j)?.editedFare!!
                }
            }
        }
        adapter?.notifyDataSetChanged()
    }

    override fun onSingleButtonClickListViewFareDetails(list: MutableList<RouteWiseFareDetail>?) {
        for (i in 0 until filterFareDetailsList.size) {
            for (j in 0 until (routeWiseFareDetail?.fareDetails?.size ?: 0)) {
                if (filterFareDetailsList[i].fareDetails[j].seatType.equals(
                        routeWiseFareDetail?.fareDetails?.get(j)?.seatType, true
                    )
                ) {
                    filterFareDetailsList[i].fareDetails[j].fare = routeWiseFareDetail?.fareDetails?.get(j)?.editedFare!!
                }
            }
        }
        adapter?.notifyDataSetChanged()
    }
}

