package com.bitla.ts.presentation.view.fragments

import android.app.*
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.lifecycle.*
import androidx.navigation.fragment.*
import com.bitla.ts.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.stage_for_city.*
import com.bitla.ts.domain.pojo.update_route.*
import com.bitla.ts.presentation.adapter.RouteManager.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.routeId
import com.bitla.ts.utils.sharedPref.*
import com.google.android.material.bottomsheet.*
import com.google.gson.*
import gone
import isNetworkAvailable
import org.koin.androidx.viewmodel.ext.android.*
import toast
import visible


class BoardingPointConfigFragment : BaseFragment(),DialogAnyClickListener,
    DialogButtonAnyDataListener {
    private var filteredStageList: ArrayList<StageListData> = arrayListOf()
    private var commaSeparatedIds: String = ""
    private var outerPosition: Int = 0
    private var stagePosition: Int = 0
    private var stagesList: java.util.ArrayList<StageDetailsData> = arrayListOf()
    private var mainStagesList: ArrayList<StageListData> = arrayListOf()
    private var boardingList: ArrayList<ViaCitiesData> = arrayListOf()
    private var locale: String = ""
    private var loginModelPref: LoginModel? = null
    private val viewModel by sharedViewModel<RouteManagerViewModel<Any?>>()


    companion object {
        val TAG = BoardingPointConfigFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentConfigureBpDpBinding
    private var boardingPointConfigurationAdapter: BoardingPointConfigurationAdapter? = null
    private var addStageAdapter: AddStageAdapter? = null


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


        if (activity != null) {
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        getPref()
        getRouteDataApi()
        getRouteDataObserver()
        stageDetailsObserver()
        deleteStageObserver()


        val str = Gson().toJson(viewModel.viaCitiesData.value)
        val gson = Gson()
        val jsonObject = gson.fromJson(str, Cities::class.java)
        boardingList = jsonObject.cities
        boardingList.removeAt(boardingList.size - 1)

        /*if(viewModel.isEdit.value == true){
            if((viewModel?.getRouteData?.value?.peekContent()?.result?.boardingConfig?.size?:0) > 0){
                boardingList.clear()
                for (i in viewModel?.getRouteData?.value?.peekContent()?.result?.boardingConfig?: arrayListOf()){
                    val obj = ViaCitiesData()
                    obj.id = i.id
                    obj.name = i.name
                    obj.stageList = i.stageDetails
                    boardingList.add(obj)

                    commaSeparatedIds = i.stageDetails.map { it.defaultStageId }.joinToString(separator = ",")
                }
            }
        }*/


        viewModel.boardingPointList.postValue(boardingList)

        setBoardingPointAdapter(boardingList)

        binding.previousTV.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.droppingPointTV.setOnClickListener {
            if(validateData()) {
                findNavController().navigate(R.id.action_boardingPointConfigFragment_to_droppingPointConfigFragment)
            }
        }

        binding.nextTV.setOnClickListener {
            if(validateData()){
                findNavController().navigate(R.id.action_boardingPointConfigFragment_to_droppingPointConfigFragment)
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
                                    for (i in routeData?.boardingConfig?: arrayListOf()){
                                        for (j in boardingList){
                                            if(i.id == j.id){
                                                j.stageList = i.stageDetails

                                            }
                                        }
                                        commaSeparatedIds = i.stageDetails.map { it.defaultStageId }.joinToString(separator = ",")
                                    }
                                    setBoardingPointAdapter(boardingList)


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
        for (i in viewModel.boardingPointList.value?: arrayListOf()){
            if(i.stageList.size == 0){
                requireActivity().toast(getString(R.string.please_add_atleast_one_stage_in_each_city))
                return false
            }
        }
        return true
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
                                for (i in viewModel.boardingPointList.value?.get(outerPosition)?.stageList?: arrayListOf()){
                                    ids  = viewModel.boardingPointList.value?.get(outerPosition)?.stageList?.map { it.defaultStageId }!!.joinToString(separator = ",")
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
                                        try {
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
                                            // addStageAdapter?.notifyDataSetChanged()
                                        } catch (e: Exception) {
                                            if (BuildConfig.DEBUG) {
                                                e.printStackTrace()
                                            }
                                        }
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


    private fun setBoardingPointAdapter(list: ArrayList<ViaCitiesData>) {
        boardingPointConfigurationAdapter = BoardingPointConfigurationAdapter(requireContext(),this,list)
        binding.citiesRV.adapter = boardingPointConfigurationAdapter
    }

    override fun onAnyClickListener(type: Int, view: Any, position: Int) {
        when(type){
            1->{
                when(view){
                    "addIV" -> {
                        boardingList = viewModel.boardingPointList.value?: arrayListOf()
                        hitStageDetailsListAPI(boardingList[position].id)
                        addStage(position,false)

                    }
                    "editIV" -> {
                        boardingList = viewModel.boardingPointList.value?: arrayListOf()
                        hitStageDetailsListAPI(boardingList[position].id)
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

    override fun onAnyClickListenerWithExtraParam(type: Int, view: Any, list: Any, position: Int,outPos: Int) {
        when(type) {
            2 -> {
                when (view) {
                    "delete_existing_stage" -> {
                        stagesList = (list as ArrayList<StageDetailsData>)
                        stagePosition = position
                        outerPosition = outPos

                        val obj = JsonObject()
                        obj.addProperty("id",(list as ArrayList<StageDetailsData>)[position].id)
                        obj.addProperty("city_id",boardingList[outPos].id)
                        hitDeleteStageApi(obj)
                    }
                }
            }
        }
    }

    private fun addStage(outPos: Int, isEdit: Boolean) {


        val dialogAddStage = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val setAddStageBinding = BottomSheetAddStageBinding.inflate(LayoutInflater.from(context))
        dialogAddStage.setContentView(setAddStageBinding.root)

        val window: Window? = dialogAddStage.window
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setAddStageBinding.cityNameTV.text = boardingList[outPos].name + "(${boardingList[outPos].stageList.size.toString()} Stage)"

        dialogAddStage.setOnDismissListener {
            viewModel.boardingPointList.value?.get(outPos)?.stageList?.removeIf { data ->
                data.defaultStageId == "0" || data.defaultStageId == ""

            }
            boardingPointConfigurationAdapter?.notifyDataSetChanged()
        }

        scrollUpLayoutForSoftKeyboard(dialogAddStage)

        var stageList = viewModel?.boardingPointList?.value?.get(outPos)?.stageList
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
                true,
                false,

            )
        }

        setAddStageBinding.addStageRV.adapter = addStageAdapter
        dialogAddStage?.show()

        if(isEdit){
            setAddStageBinding.addNewStageTV.gone()
        }else{
            setAddStageBinding.addNewStageTV.visible()
        }

        setAddStageBinding.addNewStageTV.setOnClickListener {
            stageList = viewModel?.boardingPointList?.value?.get(outPos)?.stageList
            stageList?.add(StageDetailsData())
            addStageAdapter=null
            addStageAdapter = stageList?.let {
                AddStageAdapter(
                    requireContext(),
                    it,
                    viewModel,
                    requireActivity(),
                    outerPos = outPos,
                    this,
                    true,
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

    private fun scrollUpLayoutForSoftKeyboard(dialogAddStage: BottomSheetDialog) {
        val bottomSheetInternal = dialogAddStage.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheetInternal!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = false


    }

    override fun onDataSend(type: Int, file: Any) {

    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {
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
