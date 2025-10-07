package com.bitla.ts.presentation.view.fragments

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.AdapterSearchBpdpBinding
import com.bitla.ts.databinding.BottomSheetEditServiceBinding
import com.bitla.ts.databinding.BottomSheetInactivateServiceBinding
import com.bitla.ts.databinding.FragmentSearchRouteBinding
import com.bitla.ts.domain.pojo.activate_deactivate_route.Message
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.route_list.RouteListData
import com.bitla.ts.domain.pojo.route_manager.CitiesListData
import com.bitla.ts.presentation.adapter.RouteManager.SearchListRouteAdapter
import com.bitla.ts.presentation.adapter.RouteManager.SearchRouteServiceTypesAdapter
import com.bitla.ts.presentation.adapter.SourceDestinatinAdapter
import com.bitla.ts.presentation.view.activity.RouteServiceManagerActivity
import com.bitla.ts.presentation.viewModel.RouteManagerViewModel
import com.bitla.ts.utils.constants.DESTINATION
import com.bitla.ts.utils.constants.SOURCE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import gone
import isNetworkAvailable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import toast
import visible
import kotlin.collections.ArrayList

class SearchRouteFragment : BaseFragment(), DialogAnyClickListener , DialogButtonAnyDataListener {

    companion object {
        val TAG = SearchRouteFragment::class.java.simpleName
    }
    private val handler = Handler(Looper.getMainLooper())
    private var isTypingStopped = false
    // Define the delay before validation (e.g., 1 second)
    private val typingStoppedDelay: Long = 1000
    private var sourceNewAdapter : SourceDestinatinAdapter?= null
    private var citiesList: ArrayList<CitiesListData> = arrayListOf()
    private var sourceCitiesList: ArrayList<CitiesListData> = arrayListOf()
    private var destinationCitiesList: ArrayList<CitiesListData> = arrayListOf()
    private var templist: ArrayList<CitiesListData> = arrayListOf()
    private lateinit var binding: FragmentSearchRouteBinding
    private var filterAdapter: SearchRouteServiceTypesAdapter? = null
    private var searchListAdapter: SearchListRouteAdapter? = null
    private val viewModel by sharedViewModel<RouteManagerViewModel<Any?>>()
    private var locale: String = ""
    private var loginModelPref: LoginModel? = null
    private var sourcePopupWindow: PopupWindow? = null
    private var sourceId = "-1"
    private var destinationId = "-1"
    private var routeList : ArrayList<RouteListData> = arrayListOf()
    private var filterValue : String = ""
    private var filterType : String = "0"
    private var search : String = ""
    private var page : String = ""
    private var perPage : String = ""
    private var filterTypeArray : ArrayList<CitiesListData> = arrayListOf()
    private var routeId: String = ""
    private var activeDeactiveMsg: Message ?= null
    private var activateDeactivateStatus: String = ""
    private val ids = listOf("0", "2", "1")
    private val names = listOf("Active","Proposed","Inactive" )



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(!::binding.isInitialized) {
            binding = FragmentSearchRouteBinding.inflate(inflater, container, false)
            initUI()
        }
        return binding.root
    }

    private fun initUI() {


        getPref()
        citiesListObserver()
        routeListObserver()
        setFilterAdapter()

        binding.sourceET.setOnClickListener {
                sourcePopupDialog(getString(R.string.source))
        }

        binding.destinationET.setOnClickListener{
            if(binding.sourceET.text.toString().isNotBlank()){
                sourcePopupDialog(getString(R.string.destination))
            }else{
                requireActivity().toast(getString(R.string.please_select_source_first))
            }
        }

        binding.searchIV.setOnClickListener{
            if(binding.searchET.isVisible){
                binding.searchET.gone()
            }else{
                binding.searchET.visible()

            }
        }

        binding.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                handler.postDelayed({
                    if (!isTypingStopped) {
                        isTypingStopped = true
                        if(routeList.size > 0){
                            filterList(s.toString())
                        }else{
                            if(binding.searchET.isVisible){
                                requireActivity().toast(getString(R.string.please_select_source_and_destination_first))

                            }
                        }
                    }
                }, typingStoppedDelay)

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                handler.removeCallbacksAndMessages(null)
                isTypingStopped = false

            }
        })
//        binding.createNewServiceTV.setOnClickListener {
//            requireActivity().findNavController(R.id.my_nav_host_fragment).navigate(R.id.action_searchRouteFragment_to_editRouteBasicDetailsFragment)
//        }
    }



    override fun onResume() {
        super.onResume()

        (activity as RouteServiceManagerActivity).updateToolbar("Search Route")
    }

    override fun onPause() {
        super.onPause()

    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun isNetworkOff() {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    private fun setFilterAdapter() {
        filterTypeArray.clear()
        for (i in ids.indices) {
            val city = CitiesListData().apply {
                id = ids[i]
                name = names[i]
                if(i == 0){
                    isSelected = true
                }else{
                    isSelected = false
                }
            }
            filterTypeArray.add(city)
        }

        filterAdapter = SearchRouteServiceTypesAdapter(requireContext(), filterTypeArray,this)
        binding.filterRV.adapter = filterAdapter
    }


    private fun setSearchListAdapter() {
        val privileges = (activity as BaseActivity).getPrivilegeBase()

        searchListAdapter = SearchListRouteAdapter(requireContext(),routeList,this,privileges)
        binding.searchListRV.adapter = searchListAdapter
    }


    private fun filterList(query: String) {
        val filteredList = routeList.filter {
            it.number.startsWith(query,true)
        }
        searchListAdapter?.updateList(filteredList as ArrayList<RouteListData>)
    }

    override fun onAnyClickListener(type: Int, view: Any, position: Int) {
        when(type){
            1->{
                when(view){
                    "moreIV" -> {
                        moreDialog(position)
                        routeId = routeList[position].id
                        if(routeList[position].status == "Active"){
                            activateDeactivateStatus = "3"

                        } else{
                            activateDeactivateStatus = "0"
                        }
                    }

                    "editIV" -> {
                        viewModel.routeId.postValue(position)
                        createNewService(true)

                    }

                    "selctedOption" -> {
                        routeListObserver()
                        filterTypeArray.forEach { i ->
                            i.isSelected = false
                        }
                        filterTypeArray[position].isSelected = true
                        filterAdapter?.notifyDataSetChanged()
                        if(position == 1){
                            filterValue = "2"
                        }else if (position == 0) {
                            filterValue = ""
                        }else{
                            filterValue = "3"
                        }
                        binding.searchET.setText("")
                        getRouteListApi()
                    }
                }
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

    private fun callCitiesListApi(){
        if (requireContext().isNetworkAvailable()){
            try {
                viewModel.getCitiesListApi(loginModelPref?.api_key?:"","json",locale)
            }catch (e: Exception){
                if(BuildConfig.DEBUG){
                    e.printStackTrace()
                }
            }

        }
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

        callCitiesListApi()
        binding.sourceET.setText("All")
        binding.destinationET.setText("All")
        getRouteListApi()

    }

    private fun getRouteListApi(){
        if (requireContext().isNetworkAvailable()){
            try {
                (activity as RouteServiceManagerActivity).showProgressDialog()
                viewModel.getRouteListApi(sourceId,destinationId,loginModelPref?.api_key?:"","json",locale, filterValue,page,perPage,binding.searchET.text.toString(),filterType )
            }catch (e: Exception){
                if(BuildConfig.DEBUG){
                    e.printStackTrace()
                }
            }

        }
    }



    private fun moreDialog(position: Int) {
        val bottomSheetEditCity = context?.let { it -> BottomSheetDialog(it, R.style.BottomSheetDialog) }
        val setEditCityBinding = BottomSheetEditServiceBinding.inflate(LayoutInflater.from(context))
        bottomSheetEditCity?.setContentView(setEditCityBinding.root)
        bottomSheetEditCity?.show()

        val data = routeList[position]
        setEditCityBinding.serviceDescTV.text = "${data.departureTime},${data.originName}-${data.destinationName}"
        setEditCityBinding.serviceNameTV.text = "${data.number}"

        setEditCityBinding.cancelIV.setOnClickListener {
            bottomSheetEditCity?.dismiss()
        }

        if(routeList[position].status.equals("proposed",true) || routeList[position].status.equals("inactive",true)){
            setEditCityBinding.previewRouteTV.gone()
            setEditCityBinding.previewRouteIV.gone()
        }else{
            setEditCityBinding.previewRouteTV.visible()
            setEditCityBinding.previewRouteIV.visible()


        }

        setEditCityBinding.previewRouteTV.setOnClickListener {
            bottomSheetEditCity?.dismiss()
            viewModel.routeId.postValue(routeList[position].id.trim().toInt())
            findNavController().navigate(R.id.previewRouteFragment)
        }

        if(routeList[position].status == "Active"){
            setEditCityBinding.inactiveTV.text = getString(R.string.active)
            setEditCityBinding.inactiveTV.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGreen))
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_active)
            setEditCityBinding.toggleIV.setImageDrawable(drawable)
        }else{
            setEditCityBinding.inactiveTV.text = getString(R.string.inactive)
            setEditCityBinding.inactiveTV.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_toggle_red)
            setEditCityBinding.toggleIV.setImageDrawable(drawable)


        }
        setEditCityBinding.inactiveTV.setOnClickListener {
            inactiveDialog(setEditCityBinding.inactiveTV.text.toString(),position)
            activateDeactivateStatusObserver()
            if(activeDeactiveMsg?.message?.isNotEmpty() == true){
                bottomSheetEditCity?.dismiss()
            }
        }

        setEditCityBinding.toggleIV.setOnClickListener {
            inactiveDialog(setEditCityBinding.inactiveTV.text.toString(), position)
            activateDeactivateStatusObserver()
            if(activeDeactiveMsg?.message?.isNotEmpty() == true){
                bottomSheetEditCity?.dismiss()
            }
        }

        setEditCityBinding.serviceNameTV.setOnClickListener{
            bottomSheetEditCity?.dismiss()
        }
    }

    private fun inactiveDialog(status: String, position: Int){

        val bottomSheetConfirmation = context?.let { it -> BottomSheetDialog(it, R.style.BottomSheetDialog) }
        val setEditCityBinding = BottomSheetInactivateServiceBinding.inflate(LayoutInflater.from(context))
        bottomSheetConfirmation?.setContentView(setEditCityBinding.root)
        bottomSheetConfirmation?.show()

        val data = routeList[position]
        setEditCityBinding.serviceDescTV.text = "${data.departureTime},${data.originName}-${data.destinationName}"


        val activateDeactivateRouteReqBodyObj = JsonObject()
        activateDeactivateRouteReqBodyObj.addProperty("route_id",routeId)
        activateDeactivateRouteReqBodyObj.addProperty("status",activateDeactivateStatus)

        if(status == "Active"){
            setEditCityBinding.serviceNameTV.text = getString(R.string.stop_service)
            setEditCityBinding.confimationTextTV.text = getString(R.string.are_you_sure_you_want_to_stop_this_service)
            setEditCityBinding.alertMsgTV.text = getString(R.string.this_will_suspend_all_scheduled_trips_you_can_reactivate_this_service_any_time)
            setEditCityBinding.yesTV.text = getString(R.string.yes_stop)

        } else if (status == "Inactive"){
            setEditCityBinding.serviceNameTV.text = getString(R.string.activate_service)
            setEditCityBinding.alertMsgTV.text = getString(R.string.this_will_activate_all_scheduled_trips_you_can_stop_this_service_any_time)
            setEditCityBinding.confimationTextTV.text = getString(R.string.are_you_sure_you_want_to_activate_this_service)
            setEditCityBinding.yesTV.text = getString(R.string.yes_start)
        }
        setEditCityBinding.cancelIV.setOnClickListener{
            bottomSheetConfirmation?.dismiss()
        }

        setEditCityBinding.noTV.setOnClickListener {
            bottomSheetConfirmation?.dismiss()
        }

        setEditCityBinding.yesTV.setOnClickListener {
            hitActivateDeactivateApi(activateDeactivateRouteReqBodyObj)
            getRouteListApi()
            bottomSheetConfirmation?.dismiss()
        }



    }

    private fun hitActivateDeactivateApi(activateDeactivateRouteReqBody: Any){
        if (requireContext().isNetworkAvailable()){
            try {
                viewModel.getRouteActivateDeactivateStatusApi(loginModelPref?.api_key?:"",locale,"json",activateDeactivateRouteReqBody )
            }catch (e: Exception){
                if(BuildConfig.DEBUG){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun activateDeactivateStatusObserver(){
        viewModel.getRouteActivateDeactivateStatus.observe(viewLifecycleOwner, Observer { response ->
            (activity as RouteServiceManagerActivity).hideProgressDialog()
            try {
                // Null check on response and its result
                if (response!= null) {
                    when(response.code){
                        200 -> {
                            activeDeactiveMsg = response.result
                        }

                        412 -> {
                            requireActivity().toast(response.message)
                        }
                        else -> {
                            requireActivity().toast(getString(R.string.server_error))

                        }
                    }
                }
            } catch (e: Exception) {
            }
        })

    }

    fun createNewService(isEdit: Boolean) {
        viewModel.isEdit.postValue(isEdit)
        if(!isEdit){
            viewModel.routeId.postValue(null)
        }
        requireActivity().findNavController(R.id.my_nav_host_fragment).navigate(R.id.action_searchRouteFragment_to_editRouteBasicDetailsFragment)

    }

    private fun citiesListObserver() {
        viewModel.getCitiesList.observe(viewLifecycleOwner, Observer { response ->
            try {
                // Null check on response and its result
                if (response?.result != null) {
                    citiesList = response.result
                    val obj = CitiesListData()
                    obj.name = "All"
                    obj.id = "-1"
                    citiesList.add(0,obj)
                    sourceCitiesList = citiesList
                    destinationCitiesList = citiesList
                }
            } catch (e: Exception) {
            }
        })
    }

    private fun routeListObserver() {
        viewModel.getRouteList.observe(viewLifecycleOwner, Observer { response ->
            try {
                (activity as RouteServiceManagerActivity).hideProgressDialog()
                // Null check on response and its result
                when(response.code.toInt()) {
                     200 -> {
                        if (response?.result != null) {
                            routeList = response.result

                            for (i in filterTypeArray){
                                if(i.name.equals("active",true)){
                                    i.count = response.activeCount.toString()
                                }else if(i.name.equals("proposed",true)){
                                    i.count = response.propsedCount.toString()
                                }else{
                                    i.count = response.inactiveCount.toString()
                                }
                            }
                            if(filterAdapter != null){
                                filterAdapter?.notifyDataSetChanged()
                            }

                            if(routeList.isNotEmpty()) {
                                setSearchListAdapter()
                                binding.searchListRV.visible()
                                binding.noRecordTV.gone()
                            }else{
                                binding.searchListRV.gone()
                                binding.noRecordTV.visible()
                            }
                        }
                    }
                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()
                    }
                    else -> {
                        requireActivity().toast(getString(R.string.server_error))
                    }
                }

            } catch (e: Exception) {
            }
        })
    }


    private fun sourcePopupDialog(from: String){
        var popupBinding : AdapterSearchBpdpBinding?= null
        popupBinding = AdapterSearchBpdpBinding.inflate(LayoutInflater.from(requireContext()))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED)

        if (from == getString(R.string.source)) {
            sourceCitiesList.sortBy { it.name }
            sourceNewAdapter = SourceDestinatinAdapter(requireContext(), sourceCitiesList, this, SOURCE)
            popupBinding.searchRV.adapter = sourceNewAdapter

        }else if(from == getString(R.string.destination)){
            destinationCitiesList.sortBy { it.name }
            sourceNewAdapter = SourceDestinatinAdapter(requireContext(), destinationCitiesList, this, DESTINATION)
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

        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        var popupHeight = (screenHeight * 0.4).toInt()



        sourcePopupWindow = PopupWindow(
            popupBinding.root,binding.searchCV.width,popupHeight,
            true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sourcePopupWindow?.elevation = 12.0f;
        }

        sourcePopupWindow?.showAsDropDown(binding.searchCV)

        sourcePopupWindow?.elevation=25f


        popupBinding.root.setOnTouchListener { v: View?, event: MotionEvent? ->
            sourcePopupWindow?.dismiss()
            true
        }
    }

    override fun onDataSend(type: Int, file: Any) {
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {
        when(type){
            1 -> {
                val selectedData = file as CitiesListData
                when(extra as Int){
                    SOURCE -> {
                        binding.sourceET.setText(selectedData.name)
                        sourceId = selectedData.id ?: ""

                        templist = ArrayList(citiesList)
                        if(!selectedData.name.equals("all",true)){
                            templist.removeIf { city ->
                                city.name == selectedData.name
                            }
                        }


                        destinationCitiesList = templist
                        sourcePopupWindow?.dismiss()
                        if(binding.destinationET.text.toString().isNotBlank()){
                            getRouteListApi()
                        }
                    }

                    DESTINATION -> {
                        binding.destinationET.setText(selectedData.name)
                        destinationId = selectedData.id ?: ""

                        templist = ArrayList(citiesList)
                        if(!selectedData.name.equals("all",true)){
                            templist.removeIf { city ->
                            city.name == selectedData.name
                        }}

                        sourceCitiesList = templist
                        sourcePopupWindow?.dismiss()
                        getRouteListApi()
                    }


                }
            }
        }
    }



}
