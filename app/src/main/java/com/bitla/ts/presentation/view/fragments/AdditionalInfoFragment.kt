package com.bitla.ts.presentation.view.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.databinding.FragmentAdditionalInfoBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.route_manager.CitiesListData
import com.bitla.ts.presentation.adapter.RouteManager.AdditionalInfoAdapter
import com.bitla.ts.presentation.view.activity.RouteServiceManagerActivity
import com.bitla.ts.presentation.viewModel.RouteManagerViewModel
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import isNetworkAvailable
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import toast
import java.util.*


class AdditionalInfoFragment : BaseFragment(), DialogAnyClickListener {

    companion object {
        val TAG = AdditionalInfoFragment::class.java.simpleName
    }

    private var finalObj= JsonObject()
    private lateinit var binding: FragmentAdditionalInfoBinding
    private var additionalInfoAdapter: AdditionalInfoAdapter? = null
    private var additionalInfoArray: ArrayList<CitiesListData> = arrayListOf()
    private val viewModel by sharedViewModel<RouteManagerViewModel<Any?>>()
    private var loginModelPref: LoginModel? = null

    private var locale: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(!::binding.isInitialized){
            binding = FragmentAdditionalInfoBinding.inflate(inflater, container, false)
            initUI()
        }
        return binding.root
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

    }

    private fun initUI() {

        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

        getPref()
        updateRouteDataObserver()
        setAdditionalInfoAdapter()
        setModifyRouteObserver()


        binding.activeTV.setOnClickListener {
            if(viewModel.isEdit.value == true){
                setModifyRouteObserver()
            }else{
                updateRouteDataObserver()
            }
            createJsonObject("active")

        }
        binding.proposedTV.setOnClickListener {
            if(viewModel.isEdit.value == true){
                setModifyRouteObserver()
            }else{
                updateRouteDataObserver()
            }
            createJsonObject("proposed")

        }
    }

    override fun onResume() {
        super.onResume()
        try {
            val data = viewModel.getRouteData.value?.peekContent()?.result
            val subTitle = "${data?.schedule?.departureTime}, ${data?.basicDetails?.originName}- ${data?.basicDetails?.destinationName}"
            (activity as RouteServiceManagerActivity).updateToolbar(getString(R.string.additional_info),"",subTitle)

        }catch (e: Exception){
            if(BuildConfig.DEBUG){
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


    private fun setModifyRouteObserver() {
        viewModel.getModifyRouteStatus.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                response.getContentIfNotHandled()?.let {
                    (activity as RouteServiceManagerActivity).hideProgressDialog()
                    try {
                        if (it != null) {
                            when (it.code) {
                                200 -> {
                                    context?.toast(it.result?.message)
                                    requireActivity().finish()
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            RouteServiceManagerActivity::class.java
                                        )
                                    )
                                }

                                401 -> {
                                    (activity as BaseActivity).showUnauthorisedDialog()
                                }
                                412 -> {
                                    if(!it.message.isNullOrEmpty()){
                                        requireContext().toast(it.message)
                                    }else{
                                        requireContext().toast(requireActivity().getString(R.string.server_error))
                                    }
                                }

                                else -> {
                                    requireContext().toast(requireActivity().getString(R.string.server_error))
                                }
                            }
                        }
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace()
                        }
                    }
                }
        })

    }

    private fun updateRouteDataObserver(){
        viewModel.getUpdateRouteStatus.observe(viewLifecycleOwner,Observer{
            try {
                (activity as RouteServiceManagerActivity).hideProgressDialog()
                if(it != null) {
                    when(it.code) {
                        200 -> {
                            if(it.result != null && !it.result?.message.isNullOrEmpty()){
                                requireContext().toast(it.result?.message)
                                requireActivity().finish()
                                startActivity(Intent(requireContext(),RouteServiceManagerActivity::class.java))
                            }
                        }
                        401 -> {
                            (activity as BaseActivity).showUnauthorisedDialog()
                        }
                        412 -> {
                            if(!it.message.isNullOrEmpty()){
                                requireContext().toast(it.message)
                            }else{
                                requireContext().toast(requireActivity().getString(R.string.server_error))
                            }
                        }


                        else -> {
                            requireContext().toast(requireActivity().getString(R.string.server_error))
                        }
                    }
                }
            }catch (e: Exception){
                if(BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        })
    }





    private fun setAdditionalInfoAdapter(){
        additionalInfoArray.clear()

        val additionInfoData=viewModel.getRouteData.value?.peekContent()?.result?.additionalInfo

            val gstData = CitiesListData().apply {
                name = "GST"
                idName = "gst"
                isChecked = viewModel.isAcCoach.value == true
                if(viewModel.isEdit.value == true){
                    isChecked = additionInfoData?.gst?:false
                }
            }
            additionalInfoArray.add(gstData)

        val branchData = CitiesListData().apply {
            name = "Branch Booking"
            idName = "branch_booking"
            if(viewModel.isEdit.value == true) {
                isChecked = additionInfoData?.branchBooking?:false
            }
        }
        additionalInfoArray.add(branchData)


        val apiBooking = CitiesListData().apply {
            name = "Api Booking"
            idName = "api_booking"
            if(viewModel.isEdit.value == true) {
                isChecked = additionInfoData?.apiBooking?:false
            }
        }
        additionalInfoArray.add(apiBooking)


        val offlineBooking = CitiesListData().apply {
            name = "Offline Booking"
            idName = "offline_booking"
            if(viewModel.isEdit.value == true) {
                isChecked = additionInfoData?.offlineBooking?:false
            }
        }
        additionalInfoArray.add(offlineBooking)


        val ladiesConfigData = CitiesListData().apply {
            name = "Allow ladies next to gents"
            idName = "allow_ladies_next_to_gents"
            if(viewModel.isEdit.value == true) {
                isChecked = additionInfoData?.allowLadiesNextToGents?:false
            }
        }
        additionalInfoArray.add(ladiesConfigData)


        val gentsConfigData = CitiesListData().apply {
            name = "Allow gents next to ladies"
            idName = "allow_gents_next_to_ladies"
            if(viewModel.isEdit.value == true) {
                isChecked = additionInfoData?.allowGentsNextToLadies?:false
            }
        }
        additionalInfoArray.add(gentsConfigData)





        additionalInfoAdapter = AdditionalInfoAdapter(requireContext(),additionalInfoArray,this)
        binding.additionalInfoRV.adapter = additionalInfoAdapter
    }

    fun routeReview(){
        findNavController().navigate(R.id.previewRouteFragment)
    }



    override fun onAnyClickListener(type: Int, view: Any, position: Int) {
        when(type){
            1 -> {
                additionalInfoArray[position].isChecked = view == true
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

    private fun createJsonObject(status: String){
        val basicObj = JsonObject()
        for (i in additionalInfoArray.indices) {
            basicObj.addProperty(additionalInfoArray[i].idName,additionalInfoArray[i].isChecked)
        }

        finalObj = JsonObject()
        finalObj.add("additional_info",basicObj)
       // finalObj.addProperty("remark",binding.writeHereEV.text.toString())
        finalObj.addProperty("status",status)



        var commisionJson: JSONObject?= null
        val citiesJson = JSONObject(Gson().toJson(viewModel.viaCitiesData.value))
        val multicityJson = JSONObject(Gson().toJson(viewModel.multicityFareJsonObject.value))
        if(viewModel.commisionJsonObject.value != null){
            commisionJson = JSONObject(Gson().toJson(viewModel.commisionJsonObject.value))
        }
        val additionalInfoJson = JSONObject(Gson().toJson(finalObj))

        // Merge the JSON objects
        val finalJson = JSONObject()
        finalJson.put("cities", citiesJson.getJSONArray("cities"))
        finalJson.put("multi_city_booking_pair", multicityJson.getJSONArray("multi_city_booking_pair"))
        if(commisionJson != null){
            finalJson.put("commission", commisionJson?.getJSONObject("commission"))
        }
        finalJson.put("additional_info", additionalInfoJson.getJSONObject("additional_info"))
        finalJson.put("status", status)
       // finalJson.put("remark", binding.writeHereEV.text.toString())

        // Convert the final JSONObject to string if needed
        val finalJsonString = finalJson.toString()
        val mainJsonObject: JsonObject = JsonParser.parseString(finalJsonString).asJsonObject


        Log.e("json",Gson().toJson(mainJsonObject))

        if(viewModel.isEdit.value == true){
            modifyRouteApi(finalObj)
        }else{
            hitUpdateDetailsApi(mainJsonObject)
        }

    }


    private fun modifyRouteApi(jsonObject: JsonObject){
        try {
            if(requireContext().isNetworkAvailable()){
                viewModel.modifyRouteApi(loginModelPref?.api_key?:"",locale,"json",viewModel.routeId.value.toString(),"5",jsonObject)
                (activity as RouteServiceManagerActivity).showProgressDialog()
            }
        }catch (e:Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }

    }




    private fun hitUpdateDetailsApi(mainJsonObject: JsonObject) {
        try {
            if(requireContext().isNetworkAvailable()){
                (activity as RouteServiceManagerActivity).showProgressDialog()
                viewModel.updateRouteApi(loginModelPref?.api_key?:"",locale,"json",viewModel.routeId.value.toString(),mainJsonObject)
            }
        }catch (e: Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }
    }







}
