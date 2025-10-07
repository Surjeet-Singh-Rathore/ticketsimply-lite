package com.bitla.ts.presentation.view.fragments

import ItemTouchHelperCallback
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.BottomSheetAddViaCitiesBinding
import com.bitla.ts.databinding.FragmentEditRouteViaCitiesBinding
import com.bitla.ts.domain.pojo.get_route.GetRouteData
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.route_manager.CitiesListData
import com.bitla.ts.domain.pojo.update_route.ViaCitiesData
import com.bitla.ts.presentation.adapter.RouteManager.AddViaCitiesAdapter
import com.bitla.ts.presentation.adapter.RouteManager.EditRouteViaCitiesAdapter
import com.bitla.ts.presentation.view.activity.RouteServiceManagerActivity
import com.bitla.ts.presentation.viewModel.RouteManagerViewModel
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import isNetworkAvailable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import toast


class EditRouteViaCitiesFragment : BaseFragment(), DialogButtonAnyDataListener {

    companion object {
        val TAG = EditRouteViaCitiesFragment::class.java.simpleName
    }

    private lateinit var updateAddViaCitiesBindingBinding: BottomSheetAddViaCitiesBinding
    private var oldViaCityDataList: ArrayList<ViaCitiesData> = arrayListOf()
    private var viaCityObj: ViaCitiesData? = null
    private var originObj: ViaCitiesData? = null
    private var destObj: ViaCitiesData? = null
    private var routeData: GetRouteData? = null
    private var viaCitiesList: ArrayList<ViaCitiesData> = arrayListOf()
    private lateinit var binding: FragmentEditRouteViaCitiesBinding
    private var viaCitiesAdapterAdapter: EditRouteViaCitiesAdapter? = null
    private var addViaCitiesAdapter: AddViaCitiesAdapter? = null
    private var locale: String = ""
    private var loginModelPref: LoginModel? = null
    private val viewModel by sharedViewModel<RouteManagerViewModel<Any?>>()
    private var citiesList: ArrayList<CitiesListData> = arrayListOf()
    private var viaCitiesResponse: ArrayList<ViaCitiesData> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = FragmentEditRouteViaCitiesBinding.inflate(inflater, container, false)
            initUI()
        }
        return binding.root

    }

    private fun initUI() {

        getRouteApiObserver()
        citiesListObserver()
        modifyRouteResponseObserver()
        getPref()

        binding.previousTV.setOnClickListener {
            if(viewModel.isEdit.value == false){
                requireActivity().findNavController(R.id.my_nav_host_fragment).navigate(R.id.searchRouteFragment)
            }else{
                findNavController().navigateUp()
            }
        }

        binding.nextTV.setOnClickListener {
            modifyRouteResponseObserver()
            var proceed = false

            for (i in 0 until viaCitiesList.size) {
                if (viaCitiesList[i].day.isEmpty() || viaCitiesList[i].hh.isEmpty() || viaCitiesList[i].mm.isEmpty()) {
                    requireActivity().toast(getString(R.string.please_fill_the_necessary_details))
                    proceed = false
                    break

                }else{
                    proceed = true
                }
            }

            if(proceed){
                if (isTimeDataListValid(viaCitiesList)) {
                    viewModel.viaCitiesList.postValue(viaCitiesList)
                    prepareJson()
                } else {
                    requireActivity().toast(getString(R.string.invalid_date_and_time_please_enter_valid_date_time_with_corresponding_city))
                }
            }
        }
    }

    private fun modifyRouteResponseObserver() {
        viewModel.getModifyRouteStatus.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                response.getContentIfNotHandled()?.let {
                    try {
                        // Null check on response and its result
                        if (it?.code != null) {
                            when (it.code) {
                                200 -> {
                                    if (viewModel.isEdit.value == true) {
                                        requireActivity().toast(it.result?.message)
                                        (activity as RouteServiceManagerActivity).hideProgressDialog()
                                        findNavController().navigate(R.id.action_editRouteViaCitiesFragment_to_multiCityBookingFragment)
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
            })
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        callCitiesListApi()
        hitGetRouteDataApi()
    }

    private fun callCitiesListApi() {
        if (requireContext().isNetworkAvailable()) {
            try {
                viewModel.getCitiesListApi(loginModelPref!!.api_key, format_type, locale)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun hitGetRouteDataApi() {
        if (requireContext().isNetworkAvailable()) {
            try {
                if (viewModel.routeId.value != null) {
                    viewModel.getRouteDataApi(
                        loginModelPref!!.api_key,
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

    private fun isTimeDataListValid(timeDataList: ArrayList<ViaCitiesData>): Boolean {
        for (i in 0 until timeDataList.size - 1) {
            val current = timeDataList[i]
            val next = timeDataList[i + 1]


            // Convert current and next data to integers for comparison
            if (current.hh.isNotBlank() && current.mm.isNotBlank() && current.day.isNotBlank() && next.hh.isNotBlank() && next.mm.isNotBlank() && next.day.isNotBlank()) {
                val currentHours = current.hh.toInt()
                val currentMinutes = current.mm.toInt()
                val currentDay = current.day.trim().toInt()

                val nextHours = next.hh.toInt()
                val nextMinutes = next.mm.toInt()
                val nextDay = next.day.trim().toInt()

                // Check if 'next' is greater than 'current' considering day, hours, and minutes
                if (nextDay < currentDay ||
                    (nextDay == currentDay && nextHours < currentHours) ||
                    (nextDay == currentDay && nextHours == currentHours && nextMinutes <= currentMinutes)
                ) {
                    return false
                }
            }else{
                requireActivity().toast(getString(R.string.please_enter_the_complete_details))

            }
        }
        return true
    }

    private fun getRouteApiObserver() {
        viewModel.getRouteData.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                response.getContentIfNotHandled()?.let {
                    try {
                        (activity as RouteServiceManagerActivity).hideProgressDialog()
                        if (viewModel.isEdit.value == true) {
                            if (it.result != null && it.code != null) {
                                when (it.code) {
                                    200 -> {
                                        routeData = it.result
                                        viewModel.routeDataArray.postValue(routeData)
                                        if(routeData?.viaCities != null && routeData?.viaCities!!.size > 0){
                                            viaCitiesResponse = routeData?.viaCities!!
                                            viewModel.currentSeatTypes.value =
                                                routeData?.viaCities!![0].seatTypes
                                        }else{
                                            viewModel.currentSeatTypes.value = routeData?.seatTypes

                                        }


                                        if(viaCitiesResponse.size > 0){
                                            for (i in viaCitiesResponse.indices) {
                                                val viaCityObj = ViaCitiesData()
                                                viaCityObj.id = viaCitiesResponse[i].id
                                                val cityName =
                                                    citiesList.find { city -> city.id == viaCitiesResponse[i].id }
                                                viaCityObj.name = cityName?.name!!
                                                viaCityObj.isOrigin = viaCitiesResponse[i].isOrigin
                                                viaCityObj.isDestination =
                                                    viaCitiesResponse[i].isDestination
                                                viaCityObj.hh = viaCitiesResponse[i].time.substringBefore(":")
                                                viaCityObj.mm = viaCitiesResponse[i].time.substringAfter(":")
                                                viaCityObj.day = viaCitiesResponse[i].day
                                                viaCitiesList.add(viaCityObj!!)
                                            }
                                        }else{
                                            originObj = ViaCitiesData()
                                            originObj?.id = routeData?.basicDetails!!.originId
                                            originObj?.name = routeData?.basicDetails!!.originName
                                            originObj?.time = routeData?.schedule!!.departureTime
                                            originObj?.hh = routeData?.schedule!!.departureTime.substringBefore(":")
                                            originObj?.mm = routeData?.schedule!!.departureTime.substringAfter(":")
                                            originObj?.isOrigin = true
                                            originObj?.isDestination = false
                                            viaCitiesList.add(originObj!!)

                                            destObj = ViaCitiesData()
                                            destObj?.id = routeData?.basicDetails!!.destId
                                            destObj?.name = routeData?.basicDetails!!.destinationName
                                            destObj?.time = routeData?.schedule!!.arrivalTime
                                            destObj?.hh = routeData?.schedule!!.arrivalTime.substringBefore(":")
                                            destObj?.mm = routeData?.schedule!!.arrivalTime.substringAfter(":")

                                            destObj?.isOrigin = false
                                            destObj?.isDestination = true
                                            viaCitiesList.add(destObj!!)
                                        }



                                        setEditRouteViaCitiesAdapter()

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


                        } else {
                            if (it.result != null && it.code != null) {
                                when (it.code) {
                                    200 -> {
                                        routeData = it.result

                                        originObj = ViaCitiesData()
                                        originObj?.id = routeData?.basicDetails!!.originId
                                        originObj?.name = routeData?.basicDetails!!.originName
                                        originObj?.time = routeData?.schedule!!.departureTime
                                        originObj?.hh = routeData?.schedule!!.departureTime.substringBefore(":")
                                        originObj?.mm = routeData?.schedule!!.departureTime.substringAfter(":")
                                        originObj?.isOrigin = true
                                        originObj?.isDestination = false
                                        viaCitiesList.add(originObj!!)

                                        destObj = ViaCitiesData()
                                        destObj?.id = routeData?.basicDetails!!.destId
                                        destObj?.name = routeData?.basicDetails!!.destinationName
                                        destObj?.time = routeData?.schedule!!.arrivalTime
                                        destObj?.hh = routeData?.schedule!!.arrivalTime.substringBefore(":")
                                        destObj?.mm = routeData?.schedule!!.arrivalTime.substringAfter(":")

                                        destObj?.isOrigin = false
                                        destObj?.isDestination = true
                                        viaCitiesList.add(destObj!!)
                                        setEditRouteViaCitiesAdapter()

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
                        }
                    } catch (e: Exception) {
                    }
                }

            })
    }

    private fun citiesListObserver() {
        viewModel.getCitiesList.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                try {
                    // Null check on response and its result
                    if (response?.result != null && response.code != null) {
                        when (response.code) {
                            200 -> {
                                citiesList = response.result

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
                    Log.e("CitiesListObserver", "Exception in observer: ${e.message}", e)
                }
            })
    }

    override fun onResume() {
        super.onResume()

        if(viewModel.isEdit.value == true){
            (activity as RouteServiceManagerActivity).updateToolbar(getString(R.string.edit_route_via_cities),
                getString(
                    R.string.edit_via_cities
                ))

        }else{
            (activity as RouteServiceManagerActivity).updateToolbar(getString(R.string.edit_route_via_cities),
                getString(
                    R.string.add_via_cities
                ))

        }
    }

    fun navigateBack(){
        if(viewModel.isEdit.value == false){
            requireActivity().findNavController(R.id.my_nav_host_fragment).navigate(R.id.searchRouteFragment)
        }else{
            findNavController().navigateUp()
        }
    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun isNetworkOff() {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    private fun setEditRouteViaCitiesAdapter() {
        viaCitiesAdapterAdapter =
            EditRouteViaCitiesAdapter(requireContext(), viaCitiesList, requireActivity())
        binding.viaCitiesRV.adapter = viaCitiesAdapterAdapter


        val callback = ItemTouchHelperCallback(viaCitiesAdapterAdapter!!,viaCitiesList,citiesList)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.viaCitiesRV)
    }



    @RequiresApi(Build.VERSION_CODES.N)
    fun addViaCities() {

        val bottomSheetAddViaCities =
            context?.let { it -> BottomSheetDialog(it, R.style.DialogStyle) }
         updateAddViaCitiesBindingBinding =
            BottomSheetAddViaCitiesBinding.inflate(LayoutInflater.from(context))
        bottomSheetAddViaCities?.setContentView(updateAddViaCitiesBindingBinding.root)



        if (viewModel.isEdit.value == false) {
            citiesList.removeIf {
                it.id == originObj!!.id
            }
            citiesList.removeIf {
                it.id == destObj!!.id
            }
        }

        updateAddViaCitiesBindingBinding.cancelIV.setOnClickListener {
            bottomSheetAddViaCities?.dismiss()
        }


        if(viaCitiesList.size > 2){
            updateAddViaCitiesBindingBinding.citiesSelectedTV.text = "${viaCitiesList.size-2} Cities Added"
        }else{
            updateAddViaCitiesBindingBinding.citiesSelectedTV.text = "0 Cities Added"
        }


        if (viewModel.isEdit.value == true) {
            for (i in 0 until viaCitiesList.size) {
                citiesList.removeIf {
                    it.id == viaCitiesList[i].id
                }
            }
        }



        addViaCitiesAdapter = AddViaCitiesAdapter(requireContext(), citiesList,this)
        updateAddViaCitiesBindingBinding.checkboxListRV.adapter = addViaCitiesAdapter

        bottomSheetAddViaCities?.show()
        updateAddViaCitiesBindingBinding.searchCityTV.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                filterList(s.toString())
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

        updateAddViaCitiesBindingBinding.addViaCitiesRL.setOnClickListener {
        }

        updateAddViaCitiesBindingBinding.addTV.setOnClickListener {
            selectViaCities(bottomSheetAddViaCities)


        }

    }

    fun changeAddButtonColor(isAdded : Boolean){
        if(::updateAddViaCitiesBindingBinding.isInitialized){
            if(isAdded){
                updateAddViaCitiesBindingBinding.addTV.background = ContextCompat.getDrawable(requireContext(),R.color.colorPrimary)
            }else{
                updateAddViaCitiesBindingBinding.addTV.background = ContextCompat.getDrawable(requireContext(),R.color.light_gray)

            }
        }

    }

    private fun filterList(query: String) {
        val filteredList = citiesList.filter {
            it.name.contains(query,true)
        }
        addViaCitiesAdapter?.updateList(filteredList as ArrayList<CitiesListData>)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun selectViaCities(bottomSheetAddViaCities: BottomSheetDialog?) {
        val tempDeleteList : ArrayList<String> = arrayListOf()

        try {

            for (i in 0 until citiesList.size) {
                if (citiesList[i].isSelected) {
                    val obj = ViaCitiesData()
                    obj.id = citiesList[i].id
                    obj.name = citiesList[i].name
                    obj.hh = ""
                    obj.mm = ""
                    obj.day = ""

                    viaCitiesList.add(1, obj)
                    tempDeleteList.add(obj.id)


                    viaCitiesAdapterAdapter?.notifyDataSetChanged()


                }

            }

            if(viaCitiesList.size > 2){
                bottomSheetAddViaCities?.dismiss()
            }

            for (i in tempDeleteList){
                citiesList.removeIf {
                    it.id.toString() == i
                }
            }






        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

    }


    private fun prepareJson() {
        val obj = JsonObject()

        val jsonArr = JsonArray()
        for (i in 0 until viaCitiesList.size) {
            val ob = JsonObject()
            ob.addProperty("id", viaCitiesList[i].id.toInt())
            ob.addProperty("name", viaCitiesList[i].name)
            ob.addProperty("is_origin", viaCitiesList[i].isOrigin)
            ob.addProperty("is_destination", viaCitiesList[i].isDestination)
            ob.addProperty("day", viaCitiesList[i].day.trim())
            ob.addProperty("time", viaCitiesList[i].hh + ":" + viaCitiesList[i].mm)
            ob.addProperty("seat_type", viewModel.currentSeatTypes.value)
            jsonArr.add(ob)
        }
        obj.add("cities", jsonArr)
        viewModel.viaCitiesData.postValue(obj)
        val gson = Gson()
        val finalJson = gson.toJson(obj)
//        val finalJsonObject: JsonObject = JsonParser.parseString(finalJson).asJsonObject


        if (viewModel.isEdit.value!!) {
            modifyRouteApi(obj)
        } else {
            findNavController().navigate(R.id.action_editRouteViaCitiesFragment_to_multiCityBookingFragment)
        }


    }

    private fun modifyRouteApi(finalObj: JsonObject) {
        try {
            if (requireContext().isNetworkAvailable()) {
                viewModel.modifyRouteApi(
                    loginModelPref!!.api_key,
                    locale,
                    "json",
                    viewModel.routeId.value.toString(),
                    "2",
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

    override fun onDataSend(type: Int, file: Any) {
        when(type) {
            1 -> {
                val list = file as ArrayList<CitiesListData>
                for (i in list){
                    if(i.isSelected){
                        changeAddButtonColor(true)
                        break
                    }else{
                        changeAddButtonColor(false)
                    }
                }
            }
        }
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {
    }


}
