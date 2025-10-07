package com.bitla.ts.presentation.view.fragments

import android.app.*
import android.os.*
import android.text.*
import android.view.*
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.*
import com.bitla.ts.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.city_pair.*
import com.bitla.ts.domain.pojo.city_pair.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.route_manager.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.adapter.RouteManager.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.*
import com.google.android.material.bottomsheet.*
import com.google.common.reflect.*
import com.google.gson.*
import isNetworkAvailable
import org.json.*
import org.koin.androidx.viewmodel.ext.android.*
import toast


class MultiCityBookingFragment : BaseFragment(), DialogAnyClickListener,
    DialogButtonAnyDataListener {

    companion object {
        val TAG = MultiCityBookingFragment::class.java.simpleName
    }

    private var finalJsonForModifyRoute: JsonObject? = null
    private var isCommissionDialogDismissed: Boolean = false
    private var multicityJSONObject: JSONObject? = null
    private lateinit var finalObj1: JsonObject
    private val viewModel by sharedViewModel<RouteManagerViewModel<Any?>>()
    private lateinit var binding: FragmentMultiCityBookingBinding
    private var locale: String = ""
    private var loginModelPref: LoginModel? = null
    private var multiCityBookingAdapter: MultiCityBookingAdapter? = null
    private var seatTypeAdapter: SeatTypeAdapter? = null
    private var updateFareDownAdapter: UpdateFareDownAdapter? = null
    private var citiesPairBody: Result? = null
    private var fareDetailsList: MutableList<FareDetail> = arrayListOf()
    private var finalCityPairList: ArrayList<Result> = arrayListOf()
    private var seatTypes = listOf("")
    private val sourceList: java.util.ArrayList<CitiesListData> = arrayListOf()
    private val destinationList: java.util.ArrayList<CitiesListData> = arrayListOf()
    private var sourcePopupWindow: PopupWindow? = null
    private var sourceNewAdapter: SourceDestinatinAdapter? = null
    private var cityPairList: MutableList<Result> = mutableListOf()
    private var filteredList: List<Result>? = listOf()


    private var setCommissionSeatType: ArrayList<FareDetail> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = FragmentMultiCityBookingBinding.inflate(inflater, container, false)
            initUI()
        }

        return binding.root
    }




    private fun initUI() {

        if (activity != null) {
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        seatTypes = viewModel?.currentSeatTypes?.value?.split(",") ?: arrayListOf()
        for (i in seatTypes.indices) {
            val seatType = FareDetail().apply {
                seatType = seatTypes[i]
                fare = ""
            }
            fareDetailsList.add(seatType)
        }

        val seat_type = FareDetail().apply {
            seatType = "All"
            isChecked = false
        }
        setCommissionSeatType.add(seat_type)

        for (i in seatTypes.indices) {
            val city = FareDetail().apply {
                seatType = seatTypes[i]
                isChecked = false
            }
            setCommissionSeatType.add(city)
        }

        finalObj1 = JsonObject()
        finalObj1 = viewModel.viaCitiesData.value!!



        getPref(finalObj1)
        cityPairsListObserver()
        modifyRouteResponseObserver()

        binding.destinationET.setText(getString(R.string.all_city))
        binding.sourceET.setText(getString(R.string.all_city))
        viewModel._selectedDestinationId.postValue("-1")
        viewModel._selectedSourceId.postValue("-1")



        binding.selectedTV.setOnClickListener {
            val selectedCityPair: ArrayList<Result> = arrayListOf()
            for (cityPair in finalCityPairList) {
                if (cityPair.isChecked) {
                    selectedCityPair.add(cityPair)
                }
            }
            binding.selectedTV.setBackgroundResource(R.drawable.bg_light_blue_round_stroke)
            binding.selectedTV.setTextColor(
                requireContext().getResources().getColor(R.color.colorPrimary)
            )
            binding.unselectedTV.setBackgroundResource(R.drawable.bg_white_grey_stroke_round)
            binding.unselectedTV.setTextColor(
                requireContext().getResources().getColor(R.color.black)
            )
            binding.allTV.setBackgroundResource(R.drawable.bg_white_grey_stroke_round)
            binding.allTV.setTextColor(requireContext().getResources().getColor(R.color.black))

            val selectedList = filteredList?.filter { it.isChecked == true }
            selectedList?.toMutableList()
                ?.let { it1 -> multiCityBookingAdapter?.onFilterApplied(it1) }
        }

        binding.unselectedTV.setOnClickListener {
            val unselectedCityPair: ArrayList<Result> = arrayListOf()
            for (cityPair in finalCityPairList) {
                if (cityPair.isChecked) {

                } else {
                    unselectedCityPair.add(cityPair)
                }
            }
            binding.unselectedTV.setBackgroundResource(R.drawable.bg_light_blue_round_stroke)
            binding.unselectedTV.setTextColor(
                requireContext().getResources().getColor(R.color.colorPrimary)
            )
            binding.selectedTV.setBackgroundResource(R.drawable.bg_white_grey_stroke_round)
            binding.selectedTV.setTextColor(requireContext().getResources().getColor(R.color.black))
            binding.allTV.setBackgroundResource(R.drawable.bg_white_grey_stroke_round)
            binding.allTV.setTextColor(requireContext().getResources().getColor(R.color.black))


            val selectedList = filteredList?.filter { it.isChecked == false }
            selectedList?.toMutableList()
                ?.let { it1 -> multiCityBookingAdapter?.onFilterApplied(it1) }
        }

        binding.allTV.setOnClickListener {
            binding.allTV.setBackgroundResource(R.drawable.bg_light_blue_round_stroke)
            binding.allTV.setTextColor(
                requireContext().getResources().getColor(R.color.colorPrimary)
            )
            binding.unselectedTV.setBackgroundResource(R.drawable.bg_white_grey_stroke_round)
            binding.unselectedTV.setTextColor(
                requireContext().getResources().getColor(R.color.black)
            )
            binding.selectedTV.setBackgroundResource(R.drawable.bg_white_grey_stroke_round)
            binding.selectedTV.setTextColor(requireContext().getResources().getColor(R.color.black))

            filteredList?.toMutableList()
                ?.let { it1 -> multiCityBookingAdapter?.onFilterApplied(it1) }
        }

        binding.previousTV.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.nextTV.setOnClickListener {
            var proceed = true
            for (i in cityPairList) {
                if (i.isChecked) {
                    for (j in i.fareDetails) {
                        if (j.fare == "" || j.fare.matches(Regex("^0+$"))){
                            requireActivity().toast(getString(R.string.fare_should_be_greater_than_0))
                            proceed = false
                            break
                        }
                    }
                }
            }

            if (proceed) {
                prepareJson()
            }
        }


        binding.sourceET.setOnClickListener {
            sourcePopupDialog("Source")
        }

        binding.destinationET.setOnClickListener {
            sourcePopupDialog("Destination")
        }

        viewModel.selectedSourceId.observe(viewLifecycleOwner, Observer {
            filteredList = listOf()

            when {
                it == "-1" && viewModel.selectedDestinationId.value != "-1" -> {
                    filteredList =
                        cityPairList?.filter { it.destinationId.toString() == viewModel.selectedDestinationId.value }
                }

                it != "-1" && viewModel.selectedDestinationId.value == "-1" -> {
                    filteredList =
                        cityPairList?.filter { it.originId.toString() == viewModel.selectedSourceId.value }
                }

                it != "-1" && viewModel.selectedDestinationId.value != "-1" -> {
                    filteredList =
                        cityPairList?.filter { it.originId.toString() == viewModel.selectedSourceId.value && it.destinationId.toString() == viewModel.selectedDestinationId.value }
                }

                else -> {
                    filteredList = cityPairList
                }
            }
            filteredList?.let { it1 -> multiCityBookingAdapter?.onFilterApplied(it1.toMutableList()) }
        })


        viewModel.selectedDestinationId.observe(viewLifecycleOwner, Observer {
            filteredList = listOf()

            when {
                it == "-1" && viewModel.selectedSourceId.value != "-1" -> {
                    filteredList =
                        cityPairList?.filter { it.originId.toString() == viewModel.selectedSourceId.value }
                }

                it != "-1" && viewModel.selectedSourceId.value == "-1" -> {
                    filteredList =
                        cityPairList?.filter { it.destinationId.toString() == viewModel.selectedDestinationId.value }
                }

                it != "-1" && viewModel.selectedSourceId.value != "-1" -> {
                    filteredList =
                        cityPairList?.filter { it.destinationId.toString() == viewModel.selectedDestinationId.value && it.originId.toString() == viewModel.selectedSourceId.value }
                }

                else -> {
                    filteredList = cityPairList
                }
            }



            filteredList?.toMutableList()
                ?.let { it1 -> multiCityBookingAdapter?.onFilterApplied(it1) }

        })
    }

    private fun prepareJson() {
        modifyRouteResponseObserver()
        val tempList: ArrayList<Result> = arrayListOf()
        for (i in 0 until (cityPairList.size?:0)) {
            if (cityPairList[i].isChecked) {
                tempList.add(cityPairList[i])
            }
        }

        if (tempList.size > 0) {
            val selectedCityPairs: ArrayList<String> = arrayListOf()
            val arr = JsonArray()
            for (i in tempList) {
                selectedCityPairs.add("${i.originId}-${i.destinationId}")
                val jsonObj = JsonObject()
                jsonObj.addProperty("origin_id", i.originId)
                jsonObj.addProperty("destination_id", i.destinationId)
                for (j in i.fareDetails) {
                    jsonObj.addProperty(j.seatType, j.fare)
                }
                arr.add(jsonObj)
            }


            val finalObj = JsonObject()
            finalObj.add("multi_city_booking_pair", arr)

            //val finalJsonObject: JsonObject = JsonParser.parseString(finalJson).asJsonObject

            viewModel.multicityFareJsonObject.postValue(finalObj)

            val citiesJson = JSONObject(Gson().toJson(viewModel.viaCitiesData.value))
            val multicityJson = JSONObject(Gson().toJson(finalObj))
            val finallJson = JSONObject()
            finallJson.put("cities", citiesJson.getJSONArray("cities"))
            finallJson.put(
                "multi_city_booking_pair",
                multicityJson.getJSONArray("multi_city_booking_pair")
            )
            finallJson.put("city_pairs", selectedCityPairs.joinToString(","))
            multicityJSONObject = finallJson

            setAgentCommissionDialog()

        } else {
            requireActivity().toast(getString(R.string.please_select_atleast_one_city_pair))
        }


    }

    private fun cityPairsListObserver() {
        viewModel.getCityPairs.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let {
                try {
                    (activity as RouteServiceManagerActivity).hideProgressDialog()
                    // Null check on response and its result
                    if (it.result != null && it.code != null) {
                        when (it.code) {
                            200 -> {
                                cityPairList.addAll(it.result)
                                if (viewModel.isEdit.value == true && viewModel.routeDataArray.value != null) {
                                    for (i in cityPairList) {
                                        for (j in viewModel.routeDataArray.value!!.multiCityBookingPair) {
                                            if (i.originId.toString() == j.originId && i.destinationId.toString() == j.destinationId) {
                                                i.isChecked = true
                                                i.fareDetails = j.fareDetails
                                            }
                                        }
                                    }
                                }

                                val list = it.result
                                try {
                                    var temp: Result? = null
                                    if(viewModel.isEdit.value == true){
                                        var orgId = viewModel.getRouteData.value?.peekContent()?.result?.basicDetails?.originId
                                        var desId = viewModel.getRouteData.value?.peekContent()?.result?.basicDetails?.destId

                                        for (i in 0 until list.size){
                                            if((list[i].originId == orgId?.toInt() && list[i].destinationId == desId?.toInt()) ){
                                                list[i].isChecked = true
                                                temp = list[i]
                                            }
                                        }
                                    }else{
                                        val originId = viewModel.routeJsonObject.value?.getAsJsonObject("basic_details")?.get("origin_id")
                                        val destId = viewModel.routeJsonObject.value?.getAsJsonObject("basic_details")?.get("dest_id")

                                        for (i in 0 until list.size){
                                            if((list[i].originId == originId.toString().toInt() && list[i].destinationId == destId.toString().toInt()) ){
                                                list[i].isChecked = true
                                                temp = list[i]
                                            }
                                        }
                                    }

                                    list.remove(temp)
                                    list.let {
                                        temp?.let { it1 -> it.add(0, it1) }
                                    }

                                }catch (e: Exception){
                                    if(BuildConfig.DEBUG){
                                        e.printStackTrace()
                                    }
                                }

                                getAndSetSourceDestList(list)
                                setMultiCityBookingAdapter(list)
                                for (i in list) {
                                    citiesPairBody = i
                                }


                            }

                            401 -> {
                                (requireActivity() as BaseActivity).showUnauthorisedDialog()
                            }

                            else -> {
                                requireActivity().toast(requireContext().getString(R.string.server_error))
                            }
                        }
                    } else {
                        requireActivity().toast(requireActivity().getString(R.string.server_error))
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    fun getSeatValues(jsonArrayString: String, seatName: String): List<String> {
        val gson = Gson()
        val listType = object : TypeToken<List<JsonObject>>() {}.type
        val jsonArray = gson.fromJson<List<JsonObject>>(jsonArrayString, listType)

        return jsonArray.mapNotNull { jsonObject ->
            jsonObject.get(seatName)?.asString
        }
    }


    private fun getAndSetSourceDestList(result: MutableList<Result>) {


        val allCitiesData = CitiesListData()
        allCitiesData.name = getString(R.string.all_city)
        allCitiesData.id = "-1"
        sourceList?.add(allCitiesData)
        destinationList?.add(allCitiesData)



        result.forEach {
            val data = CitiesListData()
            data.name = it.originName
            data.id = it.originId.toString()
            if (sourceList?.any { it.id == data.id } == false) {
                sourceList?.add(data)
            }

            val destData = CitiesListData()
            destData.name = it.destinationName
            destData.id = it.destinationId.toString()
            if (destinationList?.any { it.id == destData.id } == false) {
                destinationList?.add(destData)
            }
        }


    }


    private fun sourcePopupDialog(from: String) {
        var popupBinding: AdapterSearchBpdpBinding? = null
        popupBinding = AdapterSearchBpdpBinding.inflate(LayoutInflater.from(requireContext()))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        if (from == "Source") {
            sourceNewAdapter =
                SourceDestinatinAdapter(requireContext(), sourceList, this, SOURCE)
            popupBinding.searchRV.adapter = sourceNewAdapter

        } else if (from == "Destination") {
            sourceNewAdapter =
                SourceDestinatinAdapter(requireContext(), destinationList, this, DESTINATION)
            popupBinding.searchRV.adapter = sourceNewAdapter

        }

        popupBinding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                sourceNewAdapter?.filter?.filter(s.toString())
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


        sourcePopupWindow = PopupWindow(
            popupBinding.root,
            binding.viewByDropDownCV.width - 20,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        sourcePopupWindow?.showAsDropDown(binding.sourceET)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sourcePopupWindow?.elevation = 12.0f;
        }



        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            sourcePopupWindow?.dismiss()
            true
        }
    }

    private fun getPref(finalObj: Any) {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()


        callCityPairsAPi(finalObj)

    }

    private fun hitGetRouteDataApi() {
        if (requireContext().isNetworkAvailable()) {
            try {
                if (viewModel.routeId.value != null) {
                    viewModel.getRouteDataApi(
                        loginModelPref?.api_key?:"",
                        locale,
                        "json",
                        viewModel.routeId.value.toString()
                    )
                    (activity as RouteServiceManagerActivity).showProgressDialog()
                }

            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }

        }
    }

    private fun callCityPairsAPi(finalObj: Any) {
        if (requireContext().isNetworkAvailable()) {
            try {
                (activity as RouteServiceManagerActivity).showProgressDialog()
                viewModel.getCityPairApi(
                    apiKey = loginModelPref?.api_key?:"",
                    responseFormat = "json",
                    locale = locale,
                    reqCitiesBody = finalObj
                )
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }

        }
    }


    override fun onResume() {
        super.onResume()

        try {
            val data = viewModel.getRouteData.value?.peekContent()?.result
            val subTitle =
                "${data?.schedule?.departureTime}, ${data?.basicDetails?.originName}- ${data?.basicDetails?.destinationName}"
            (activity as RouteServiceManagerActivity).updateToolbar(
                getString(R.string.via_city_booking_and_fare),
                "",
                subTitle
            )

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }


    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun isNetworkOff() {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    private fun setMultiCityBookingAdapter(list: MutableList<Result>) {
        multiCityBookingAdapter = MultiCityBookingAdapter(
            context = requireContext(),
            resultDataList = list,
            listener = this,
            viewModel
        )
        { item ->
            citiesPairBody = item
        }
        binding.checkboxListRV.adapter = multiCityBookingAdapter
    }

    private fun setAgentCommissionDialog() {
        val bottomSheetSetAgentCommission =
            context?.let { it -> BottomSheetDialog(it, R.style.BottomSheetDialog) }
        val setAgentCommissionDialogBinding =
            BottomSheetSetCommisionsForAgentsBinding.inflate(LayoutInflater.from(context))
        bottomSheetSetAgentCommission?.setContentView(setAgentCommissionDialogBinding.root)

        seatTypeAdapter = SeatTypeAdapter(requireContext(), setCommissionSeatType, this)
        setAgentCommissionDialogBinding.seatTypeRV.adapter = seatTypeAdapter
        bottomSheetSetAgentCommission?.show()


        setAgentCommissionDialogBinding.overallMinusIV.setOnClickListener {
            if(setAgentCommissionDialogBinding.overallValueET.text.toString().isNotEmpty()) {
                var count = setAgentCommissionDialogBinding.overallValueET.text.toString().toFloat()
                if (count > 1.0f) {
                    count--
                    setAgentCommissionDialogBinding.overallValueET.setText(count.toString())
                }
            }
        }

        bottomSheetSetAgentCommission?.setOnDismissListener {
            isCommissionDialogDismissed = true

        }

        setAgentCommissionDialogBinding.overallPlusIV.setOnClickListener {
            if(setAgentCommissionDialogBinding.overallValueET.text.toString().isNotEmpty()) {
                var count = setAgentCommissionDialogBinding.overallValueET.text.toString().toFloat()
                count++
                setAgentCommissionDialogBinding.overallValueET.setText(count.toString())
            }
        }



        try {
            val data = viewModel.getRouteData.value?.peekContent()?.result
            setAgentCommissionDialogBinding.setCommissionTV.text =
                "Set commission for ${data?.schedule?.departureTime}, ${data?.basicDetails?.originName}-${data?.basicDetails?.destinationName}"

            if (viewModel.isEdit.value == true) {
                if (data?.commision?.commisionType.equals("percent", true)) {
                    setAgentCommissionDialogBinding.percentageRB.isChecked = true
                    setAgentCommissionDialogBinding.fixedRB.isChecked = false
                } else {
                    setAgentCommissionDialogBinding.fixedRB.isChecked = true
                    setAgentCommissionDialogBinding.percentageRB.isChecked = false

                }
                setAgentCommissionDialogBinding.overallValueET.setText(data?.commision?.commissionValue)

            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        setAgentCommissionDialogBinding.setAgentCommissionsTV.setOnClickListener {
            bottomSheetSetAgentCommission?.dismiss()
        }

        setAgentCommissionDialogBinding.skipTV.setOnClickListener {
            bottomSheetSetAgentCommission?.dismiss()
            viewModel.commissionJsonData.postValue(null)
            findNavController().navigate(R.id.action_multiCityBookingFragment_to_boardingPointConfigFragment)
        }

        setAgentCommissionDialogBinding.percentageRB.setOnClickListener {
            setAgentCommissionDialogBinding.fixedRB.isChecked = false
        }

        setAgentCommissionDialogBinding.fixedRB.setOnClickListener {
            setAgentCommissionDialogBinding.percentageRB.isChecked = false
        }


        setAgentCommissionDialogBinding.applyTV.setOnClickListener {

            if (setAgentCommissionDialogBinding.overallValueET.text?.toString()
                    ?.toIntOrNull() == 0 ||
                setAgentCommissionDialogBinding.overallValueET.text.isNullOrEmpty()
            ) {

                requireActivity().toast(getString(R.string.agent_commission_should_be_greater_than_0))
            }
            else if (setAgentCommissionDialogBinding.overallValueET.text.toString().isNotBlank()) {
                val basicObj = JsonObject()
                if (setAgentCommissionDialogBinding.percentageRB.isChecked) {
                    basicObj.addProperty("commission_type", "percent")
                } else if (setAgentCommissionDialogBinding.fixedRB.isChecked) {
                    basicObj.addProperty("commission_type", "fixed")
                }

                if (setAgentCommissionDialogBinding.overallTV.isChecked) {
                    basicObj.addProperty(
                        "commission_value",
                        setAgentCommissionDialogBinding.overallValueET.text.toString()
                    )
                } else {
                    basicObj.addProperty("commission_value", "")
                }

                basicObj.addProperty("seat_type", "-1")
                // basicObj.addProperty("commision_value", "increment")
                val commission = JsonObject()
                commission.add("commission", basicObj)

                viewModel.commisionJsonObject.postValue(commission)

                bottomSheetSetAgentCommission?.dismiss()

                if (viewModel.isEdit.value == true) {
                    val comJson = JSONObject(Gson().toJson(commission))
                    multicityJSONObject?.put("commission", comJson.getJSONObject("commission"))
                    val finalJsonString = multicityJSONObject.toString()
                    val mainJsonObject: JsonObject =
                        JsonParser.parseString(finalJsonString).asJsonObject
                    modifyRouteApi(mainJsonObject)
                } else {
                    findNavController().navigate(R.id.action_multiCityBookingFragment_to_boardingPointConfigFragment)

                }
            } else {
                requireActivity().toast(getString(R.string.please_enter_the_commission_amount))
            }
        }
    }

    private fun modifyRouteResponseObserver() {
        viewModel.getModifyRouteStatus.distinctUntilChanged().observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                response.getContentIfNotHandled()?.let {
                    try {
                        (activity as RouteServiceManagerActivity).hideProgressDialog()
                        // Null check on response and its result
                        if (it?.code != null) {
                            when (it.code) {
                                200 -> {
                                    if (viewModel.isEdit.value == true) {
                                        requireActivity().toast(getString(R.string.details_updated_successfully))
                                        (activity as RouteServiceManagerActivity).hideProgressDialog()
                                        findNavController().navigate(R.id.action_multiCityBookingFragment_to_boardingPointConfigFragment)
                                    }
                                }

                                401 -> {
                                    (requireActivity() as BaseActivity).showUnauthorisedDialog()
                                }

                                412 -> {
                                    requireActivity().toast(it.message)

                                }

                                else -> {
                                    requireActivity().toast(requireContext().getString(R.string.server_error))
                                }
                            }
                        } else {
                            requireActivity().toast(requireActivity().getString(R.string.server_error))
                        }
                    } catch (e: Exception) {
                    }
                }
            })
    }


    fun updateAllFare() {

        val bottomSheetUpdateAllFare =
            context?.let { it -> BottomSheetDialog(it, R.style.BottomSheetDialog) }
        val updateAllFareBinding =
            BottomSheetUpdateFareBinding.inflate(LayoutInflater.from(context))
        bottomSheetUpdateAllFare?.setContentView(updateAllFareBinding.root)

        updateFareDownAdapter = UpdateFareDownAdapter(
            context = requireContext(),
            fareDetailsList,
            0
        )
        { item, pos ->
//            fareDetailsList = item
        }
        updateAllFareBinding.seatFareRV.adapter = updateFareDownAdapter

        updateAllFareBinding.updateFareTV.setOnClickListener {
            bottomSheetUpdateAllFare?.dismiss()
        }

        try {
            val data = viewModel.getRouteData.value?.peekContent()?.result
            updateAllFareBinding.serviceNameTV.text =
                "${data?.basicDetails?.serviceNo}, ${data?.schedule?.departureTime}, ${data?.basicDetails?.originName}-${data?.basicDetails?.destinationName}"
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }


        updateAllFareBinding.updateTV.setOnClickListener {


            if (fareDetailsList.size != 0) {
                for (i in 0 until (cityPairList?.size?:0)) {
                    for (j in 0 until (fareDetailsList.size ?: 0)) {
                        if (fareDetailsList[j].seatType.equals(
                                cityPairList[i].fareDetails[j].seatType,
                                true
                            )
                        ) {
                            cityPairList[i].fareDetails[j].fare = fareDetailsList[j].editedFare?:""
                        }
                    }
                }
                multiCityBookingAdapter?.notifyDataSetChanged()

            } else {
                requireContext().toast(getString(R.string.something_went_wrong))
            }


            bottomSheetUpdateAllFare?.dismiss()
        }

        bottomSheetUpdateAllFare?.show()

    }

    override fun onAnyClickListener(type: Int, view: Any, position: Int) {
        when (type) {
            1 -> {
                citiesPairBody?.fareDetails?.get(position)?.fare = view.toString()
            }

            2 -> {
                for (item in setCommissionSeatType) {
                    item.isChecked = true
                }
                seatTypeAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onAnyClickListenerWithExtraParam(
        type: Int,
        view: Any,
        list: Any,
        position: Int,
        outPos: Int
    ) {
    }

    override fun onDataSend(type: Int, file: Any) {

    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {

        when (type) {
            1 -> {
                val selectedData = file as CitiesListData
                when (extra as Int) {
                    SOURCE -> {
                        binding.sourceET.setText(selectedData.name)
                        sourcePopupWindow?.dismiss()
                        binding.destinationET.setText("")
                        viewModel._selectedDestinationId.postValue("-1")
                        viewModel._selectedSourceId.postValue(selectedData.id)
                    }

                    DESTINATION -> {
                        binding.destinationET.setText(selectedData.name)
                        sourcePopupWindow?.dismiss()
                        viewModel._selectedDestinationId.postValue(selectedData.id)
                    }

                }
            }
        }

    }

    private fun modifyRouteApi(finalObj: JsonObject) {
        try {
            if (requireContext().isNetworkAvailable()) {
                viewModel.modifyRouteApi(
                    loginModelPref?.api_key?:"",
                    locale,
                    "json",
                    viewModel.routeId.value.toString(),
                    "3",
                    finalObj
                )
                (activity as RouteServiceManagerActivity).showProgressDialog()
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

    }
}
