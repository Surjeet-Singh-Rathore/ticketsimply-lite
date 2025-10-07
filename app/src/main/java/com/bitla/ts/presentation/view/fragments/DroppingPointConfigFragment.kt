package com.bitla.ts.presentation.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.databinding.BottomSheetAddStageBinding
import com.bitla.ts.databinding.FragmentConfigureBpDpBinding
import com.bitla.ts.domain.pojo.BoardingConfigJsonData
import com.bitla.ts.domain.pojo.BoardingDroppingFinalPojo
import com.bitla.ts.domain.pojo.BoardingDroppingStage
import com.bitla.ts.domain.pojo.Cities
import com.bitla.ts.domain.pojo.DroppingConfigJsonData
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.stage_for_city.StageListData
import com.bitla.ts.domain.pojo.update_route.StageDetailsData
import com.bitla.ts.domain.pojo.update_route.ViaCitiesData
import com.bitla.ts.presentation.adapter.RouteManager.AddStageAdapter
import com.bitla.ts.presentation.adapter.RouteManager.BoardingPointConfigurationAdapter
import com.bitla.ts.presentation.view.activity.RouteServiceManagerActivity
import com.bitla.ts.presentation.viewModel.RouteManagerViewModel
import com.bitla.ts.utils.common.routeId
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import gone
import isNetworkAvailable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import toast
import visible

class DroppingPointConfigFragment : BaseFragment() , DialogAnyClickListener{

    companion object {
        val TAG = DroppingPointConfigFragment::class.java.simpleName
    }
    private var outerPosition: Int = 0
    private var stagePosition: Int = 0
    private var stagesList: java.util.ArrayList<StageDetailsData> = arrayListOf()
    private var mainStagesList: ArrayList<StageListData> = arrayListOf()
    private var filteredStageList: ArrayList<StageListData> = arrayListOf()
    private var droppingPointList: ArrayList<ViaCitiesData> = arrayListOf()
    private var locale: String = ""
    private var loginModelPref: LoginModel? = null
    private lateinit var binding: FragmentConfigureBpDpBinding
    private var boardingPointConfigurationAdapter: BoardingPointConfigurationAdapter? = null
    private var addStageAdapter: AddStageAdapter? = null
    private val viewModel by sharedViewModel<RouteManagerViewModel<Any?>>()
    private var commaSeparatedIds: String = ""



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(!::binding.isInitialized){
            binding = FragmentConfigureBpDpBinding.inflate(inflater, container, false)
            initUI()
        }
        return binding.root
    }

    private fun initUI() {

        binding.addBpcTV.text = requireContext().getString(R.string.dropping_point_configuration)
        binding.boardingPointTV.background = null
        binding.droppingPointTV.setBackgroundResource(R.drawable.bg_white_little_round_solid)
        getPref()
        getRouteDataApi()
        getRouteDataObserver()
        stageDetailsObserver()
        createStageObserver()
        modifyRouteResponseObserver()
        deleteStageObserver()


        val str = Gson().toJson(viewModel.viaCitiesData.value)
        val gson = Gson()
        val jsonObject = gson.fromJson(str, Cities::class.java)
        droppingPointList = jsonObject.cities
        if(droppingPointList.isNotEmpty()){
            droppingPointList.removeAt(0)

        }

        /*if(viewModel.isEdit.value == true){
            if((viewModel?.getRouteData?.value?.peekContent()?.result?.droppingConfig?.size?:0) > 0){
                droppingPointList.clear()
                for (i in viewModel?.getRouteData?.value?.peekContent()?.result?.droppingConfig?: arrayListOf()){
                    val obj = ViaCitiesData()
                    obj.id = i.id
                    obj.name = i.name
                    obj.stageList = i.stageDetails
                    droppingPointList.add(obj)

                    commaSeparatedIds = i.stageDetails.map { it.defaultStageId }.joinToString(separator = ",")

                }
            }

        }*/

        if(viewModel.droppingPointList.value.isNullOrEmpty()){
            viewModel.droppingPointList.postValue(droppingPointList)
        }else{
            droppingPointList = viewModel.droppingPointList.value?: arrayListOf()
        }



        setBoardingPointAdapter(droppingPointList)

        binding.previousTV.setOnClickListener {
            viewModel.droppingPointList.postValue(droppingPointList)
            findNavController().navigateUp()
        }
        binding.boardingPointTV.setOnClickListener {
            viewModel.droppingPointList.postValue(droppingPointList)
            findNavController().navigateUp()
        }




        binding.nextTV.setOnClickListener {
            if(viewModel.isEdit.value == true){
                modifyRouteResponseObserver()
            }
            if(validateData()){
                prepareJson()
            }
            //findNavController().navigate(R.id.action_droppingPointConfigFragment_to_additionalInfoFragment)

        }



    }

    private fun getRouteDataApi() {
        if (requireContext().isNetworkAvailable()) {
            try {
                routeId = viewModel.routeId.value
                viewModel.getRouteDataApi(
                    loginModelPref!!.api_key,
                    locale,
                    format_type,
                    routeId.toString()
                )
                (activity as RouteServiceManagerActivity).showProgressDialog()
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }

        }
    }


    private fun getRouteDataObserver() {
        viewModel.getRouteData.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                response.getContentIfNotHandled()?.let {
                    (activity as RouteServiceManagerActivity).hideProgressDialog()

                    try {
                        (activity as RouteServiceManagerActivity).hideProgressDialog()

                        if (it.result != null && it.code != null) {
                            when (it.code) {
                                200 -> {
                                    val routeData = it.result
                                    for (i in routeData?.droppingConfig?: arrayListOf()){
                                        for (j in droppingPointList){
                                            if(i.id == j.id){
                                                j.stageList = i.stageDetails

                                            }
                                        }
                                        commaSeparatedIds = i.stageDetails.map { it.defaultStageId }.joinToString(separator = ",")
                                    }
                                    setBoardingPointAdapter(droppingPointList)


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
                }

            })
    }


    private fun validateData(): Boolean {
        for (i in viewModel.droppingPointList.value?: arrayListOf()){
            if(i.stageList.size == 0){
                requireActivity().toast(getString(R.string.please_add_atleast_one_stage_in_each_city))
                return false
            }
        }
        return true
    }

    private fun setBoardingPointAdapter(list: ArrayList<ViaCitiesData>) {
        boardingPointConfigurationAdapter = BoardingPointConfigurationAdapter(
            requireContext(),
            this,
            list
        )
        binding.citiesRV.adapter = boardingPointConfigurationAdapter
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
    }

    private fun hitStageDetailsListAPI(cityId : String) {
        try {
            if (requireContext().isNetworkAvailable()) {
                viewModel.getStageListApi(cityId,loginModelPref?.api_key?:"","","json",locale,"")
            }
        }catch (e: Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }

    }




    private fun stageDetailsObserver() {
        viewModel.getStageList.observe(viewLifecycleOwner, Observer {
            try {
                if(it != null){
                    when(it.code){
                        200 ->{
                            mainStagesList = it.result?.stageDetails?: arrayListOf()
                            filteredStageList = mainStagesList

                            val tempStageList = commaSeparatedIds.split(",")
                            for (i in tempStageList){
                                filteredStageList.removeIf { data ->
                                    data.id == i
                                }
                            }
                            var ids = ""
                            for (i in viewModel.droppingPointList.value?.get(outerPosition)?.stageList?: arrayListOf()){
                                ids  = viewModel.droppingPointList.value!![outerPosition].stageList.map { it.defaultStageId }.joinToString(separator = ",")
                            }
                            val temppStageList = ids.split(",")
                            for (i in temppStageList){
                                filteredStageList.removeIf { data ->
                                    data.id == i
                                }
                            }
                            viewModel.stageList.postValue(filteredStageList)


                        }

                        401 ->{
                            (activity as BaseActivity).showUnauthorisedDialog()

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
        })

    }

    private fun deleteStageObserver() {
        viewModel.getDeleteStageStatus.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { response ->
                response.getContentIfNotHandled()?.let {
                    try {
                        if (it != null) {
                            when (it.code) {
                                200 -> {
                                    requireActivity().toast(it.result?.message)
                                    val ob = StageListData()
                                    ob.id = stagesList[stagePosition].defaultStageId
                                    ob.name = stagesList[stagePosition].name
                                    viewModel.stageList.value?.add(0, ob)
                                    stagesList.removeAt(stagePosition)
                                    addStageAdapter?.notifyItemRemoved(stagePosition)
                                    addStageAdapter?.notifyItemRangeChanged(
                                        stagePosition,
                                        stagesList.size
                                    )
                                    addStageAdapter?.notifyDataSetChanged()

                                }

                                401 -> {
                                    (activity as BaseActivity).showUnauthorisedDialog()

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

    private fun createStageObserver() {
        viewModel.createStageData.observe(viewLifecycleOwner, Observer {
                try {
                    (activity as RouteServiceManagerActivity).hideProgressDialog()
                    if(it != null){
                        when(it.code){
                            200 ->{
                               if(!it.result?.message.isNullOrEmpty()){
                                   requireActivity().toast(it.result?.message)
                                   findNavController().navigate(R.id.action_droppingPointConfigFragment_to_additionalInfoFragment)

                               }
                            }

                            401 ->{
                                (activity as BaseActivity).showUnauthorisedDialog()

                            }
                            else -> {
                                if(!it.message.isNullOrEmpty()){
                                    requireContext().toast(it.message?:"")
                                }else{
                                    requireContext().toast(requireActivity().getString(R.string.server_error))

                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace()
                    }
                }

        })

    }

    override fun onResume() {
        super.onResume()
        try {
            val data = viewModel.getRouteData.value?.peekContent()?.result
            val subTitle = "${data?.schedule?.departureTime}, ${data?.basicDetails?.originName}- ${data?.basicDetails?.destinationName}"
            (activity as RouteServiceManagerActivity).updateToolbar(getString(R.string.configure_bp_dp),"",subTitle)

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

    private fun setDroppingPointAdapter() {
        val list: ArrayList<ViaCitiesData> = arrayListOf()
        boardingPointConfigurationAdapter = BoardingPointConfigurationAdapter(
            requireContext(),
            this,
            list)
        binding.citiesRV.adapter = boardingPointConfigurationAdapter
    }

    override fun onAnyClickListener(type: Int, view: Any, position: Int) {
        when(type){
            1->{
                when(view){
                    "addIV" -> {
                        droppingPointList = viewModel?.droppingPointList?.value?: arrayListOf()
                        hitStageDetailsListAPI(droppingPointList[position].id)
                        addStage(position,false)

                    }
                    "editIV" -> {
                        droppingPointList = viewModel?.droppingPointList?.value?: arrayListOf()
                        hitStageDetailsListAPI(droppingPointList[position].id)
                        addStage(position,true)
                    }
                }
            }
            2->{
                when(view){
                    "notify" -> {
                        boardingPointConfigurationAdapter?.notifyDataSetChanged()
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
        when(type) {
            2 -> {
                when (view) {
                    "delete_existing_stage" -> {
                        stagesList = (list as ArrayList<StageDetailsData>)
                        stagePosition = position
                        outerPosition = outPos
                        val obj = JsonObject()
                        obj.addProperty("id",(list as ArrayList<StageDetailsData>)[position].id)
                        obj.addProperty("city_id",droppingPointList[outPos].id)
                        hitDeleteStageApi(obj)
                        /*  (list as ArrayList<StageDetailsData>).removeAt(position)
                          addStageAdapter?.notifyItemRemoved(position)
                          addStageAdapter?.notifyItemRangeChanged(position, list.size)*/
                    }
                }
            }
        }

    }



    private fun addStage(outPos: Int, isEdit: Boolean) {
        val dialogAddStage = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val setAddStageBinding = BottomSheetAddStageBinding.inflate(LayoutInflater.from(context))
        dialogAddStage.setContentView(setAddStageBinding.root)


        setAddStageBinding.cityNameTV.text = droppingPointList[outPos].name + "(${droppingPointList[outPos].stageList.size.toString()} Stage)"

        dialogAddStage.setOnDismissListener {
            viewModel?.droppingPointList?.value?.get(outPos)?.stageList?.removeIf { data ->
                data.defaultStageId == "0" || data.defaultStageId == ""

            }
            boardingPointConfigurationAdapter?.notifyDataSetChanged()
        }


        dialogAddStage.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheetInternal?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0  // Ensure full expansion

                // Optional: Handle state changes or slide events
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        // Handle state changes (optional)
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        // Handle slide events (optional)
                    }
                })
            }
        }

        var stageList = viewModel?.droppingPointList?.value?.get(outPos)?.stageList
        stageList?.removeIf { data ->
            data.defaultStageId == "0" || data.defaultStageId == ""
        }





        addStageAdapter = stageList?.let {
            AddStageAdapter(
                requireContext(),
                it,
                viewModel,
                requireActivity(),
                outerPos = outPos,
                this,
                false,
                false,
            )
        }
        setAddStageBinding.addStageRV.adapter = addStageAdapter
        dialogAddStage.show()

        if(isEdit){
            setAddStageBinding.addNewStageTV.gone()
        }else{
            setAddStageBinding.addNewStageTV.visible()

        }

        setAddStageBinding.addNewStageTV.setOnClickListener {
            stageList = viewModel?.droppingPointList?.value?.get(outPos)?.stageList
            stageList?.add(StageDetailsData())
            addStageAdapter=null
            addStageAdapter = stageList?.let { it1 ->
                AddStageAdapter(
                    requireContext(),
                    it1,
                    viewModel,
                    requireActivity(),
                    outerPos = outPos,
                    this,
                    false,
                    true,
                )
            }
            setAddStageBinding.addStageRV.adapter = addStageAdapter
            boardingPointConfigurationAdapter?.notifyDataSetChanged()



        }




        setAddStageBinding.cityNameTV.setOnClickListener{
            dialogAddStage?.dismiss()
        }


    }

    private fun prepareJson(){
        try {
            val boardingList = viewModel.boardingPointList.value
            val droppingList= viewModel.droppingPointList.value

            val boardingArr : ArrayList<BoardingDroppingStage> = arrayListOf()
            val droppingArr : ArrayList<BoardingDroppingStage> = arrayListOf()
            for ( i in boardingList?: arrayListOf()){
                val boarding = BoardingDroppingStage(i.name,i.id.toInt(),i.stageList)
                boardingArr.add(boarding)
            }

            for (i in droppingList?: arrayListOf()){
                val dropping = BoardingDroppingStage(i.name,i.id.toInt(),i.stageList)
                droppingArr.add(dropping)
            }

            val boardingConfig = BoardingConfigJsonData(boardingArr)
            val droppingConfig = DroppingConfigJsonData(droppingArr)

            val bgson = Gson()
            val bjson = bgson.toJson(boardingConfig)

            val dgson = Gson()
            val djson = dgson.toJson(droppingConfig)

            val boardingJsonObject: JsonObject = JsonParser.parseString(bjson).asJsonObject
            val droppingJsonObject: JsonObject = JsonParser.parseString(djson).asJsonObject


            viewModel.boardingPointJson.postValue(boardingJsonObject)
            viewModel.droppingPointJson.postValue(droppingJsonObject)



            val finalObj = BoardingDroppingFinalPojo(boardingArr,droppingArr)

            val gson = Gson()
            val finalJson = gson.toJson(finalObj)
            val finalJsonObject: JsonObject = JsonParser.parseString(finalJson).asJsonObject

            Log.e("final",finalJson)



            if(viewModel.isEdit.value == true){
                modifyRouteApi(finalJsonObject)
            }else{
                hitCreateStagesApi(finalJsonObject)

            }





        }catch (e: Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }

    }

    private fun modifyRouteResponseObserver(){
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
                                        findNavController().navigate(R.id.action_droppingPointConfigFragment_to_additionalInfoFragment)
                                    }
                                }
                                401 -> {
                                    (requireActivity() as BaseActivity).showUnauthorisedDialog()
                                }

                                else -> {
                                    if(!it.message.isNullOrEmpty()){
                                        requireContext().toast(it.message?:"")
                                    }else{
                                        requireContext().toast(requireActivity().getString(R.string.server_error))

                                    }
                                }
                            }
                        } else {
                            requireActivity().toast(requireActivity().getString(R.string.server_error))
                        }
                    } catch (e: Exception) {
                        Log.e("CitiesListObserver", "Exception in observer: ${e.message}", e)
                    }
                }
            })
    }

    private fun hitCreateStagesApi(finalObj : JsonObject){
        try {
            if(requireContext().isNetworkAvailable()){
                (activity as RouteServiceManagerActivity).showProgressDialog()
                viewModel.createStageApi(loginModelPref?.api_key?:"",locale,"json",viewModel.routeId.value.toString(),finalObj)
            }
        }catch (e: Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }

    }

    private fun modifyRouteApi(finalObj : JsonObject){
        try {
            if(requireContext().isNetworkAvailable()){
                viewModel.modifyRouteApi(loginModelPref?.api_key?:"",locale,"json", viewModel.routeId.value.toString(),"4",finalObj)
                (activity as RouteServiceManagerActivity).showProgressDialog()
            }
        }catch (e:Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }

    }

    private fun hitDeleteStageApi(reqBody: JsonObject){
        if(requireContext().isNetworkAvailable()){
            try {
                viewModel.deleteStageApi(loginModelPref?.api_key?:"",locale,"","json",reqBody)
            }catch (e: Exception){
                if(BuildConfig.DEBUG){
                    e.printStackTrace()
                }
            }
        }
    }



}
