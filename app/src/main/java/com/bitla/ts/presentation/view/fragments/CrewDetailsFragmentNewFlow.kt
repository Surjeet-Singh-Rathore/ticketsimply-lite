package com.bitla.ts.presentation.view.fragments

import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.util.*
import android.view.*
import androidx.activity.result.contract.*
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.all_coach.request.*
import com.bitla.ts.domain.pojo.all_coach.request.ReqBody
import com.bitla.ts.domain.pojo.all_coach.response.*
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.employees_details.request.*
import com.bitla.ts.domain.pojo.employees_details.response.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.pickup_chart_crew_details.response.PickupChartCrewDetailsResponse
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.service_allotment.request.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.dialog.DialogUtils.Companion.popupWindowX
import com.bitla.ts.utils.sharedPref.*
import gone
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible


class CrewDetailsFragmentNewFlow : BaseFragment(), View.OnClickListener, DialogSingleButtonListener,
    OnItemCheckMultipleItemListener, DialogButtonAnyDataListener {
    
    private var sendBusInfoCheckBox = false
    private lateinit var binding: FragmentCrewDetailsNewFlowBinding
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    
    //    private lateinit var spinnerItems: SpinnerItems
    private var spinnerItemsScanned: SpinnerItems? = null
    
    private var driver1List: MutableList<Employee> = mutableListOf()
    private var driver2List: MutableList<Employee> = mutableListOf()
    private var driver3List: MutableList<Employee> = mutableListOf()
    private var cleanerList: MutableList<Employee> = mutableListOf()
    private var checkingInspectorList: MutableList<Employee> = mutableListOf()
    
    //    private var collectionPersonList: MutableList<SpinnerItems> = mutableListOf()
    private var contractorList: MutableList<Employee> = mutableListOf()
//    private var coachList: MutableList<Employee> = mutableListOf()
    
    private var coachList: MutableList<AllCoach> = mutableListOf()
    
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    
    private val currentCheckedItem: MutableList<SearchModel> = ArrayList()
    private var isSeatClick = 0
    private val selectedChartOperatedBy = StringBuilder()
    private lateinit var selectMultipleSeatsAdapter: SelectMultipleChartOperatorAdapter
    
    private var chartOperatedList = mutableListOf<SearchModel>()
    
    private var selectedDriver1Id = ""
    private var selectedDriver2Id = ""
    private var selectedDriver3Id = ""
    private var selectedCleanerId = ""
    private var selectedCheckInspectorId = ""
    private var selectedCoachId = ""
    private var selectedContracterId = ""
    private var resID: Long? = 0
    private var selectedScanType: String? = ""
    private var isAllowToCreateAdhocDriver: Boolean? = false
    private var isValidateBusCrewUpdationForCoachs: Boolean? = false
    private var isFromChile: Boolean = false
    private var checkList = SparseBooleanArray()
    private var selectedReturnCrewDriverId: String? = null
    private var selectedReturnCrewDriverName: String? = null
    private var isFromOnActivityResult = false
    private var locale: String? = null
    private var isShowContractorAsAttendent: String = "false"
    private var country: String? = null

    private var isAllowToAutoFillCrewDetails: Boolean? = false
    private var crewDetails: PickupChartCrewDetailsResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCrewDetailsNewFlowBinding.inflate(inflater, container, false)
        changeUpdateBtnBehavior()

        initUi()

        return binding.root
    }
    
    override fun isInternetOnCallApisAndInitUI() {
        initUi()
    }
    
    override fun isNetworkOff() {
    }
    
    private fun initUi() {
        
        checkList.append(0, false)
        checkList.append(1, false)
        checkList.append(2, false)
        checkList.append(3, false)
        resID = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
        getPref()
        enableCoachSelection(false)
        
        if (!isFromOnActivityResult && isValidateBusCrewUpdationForCoachs == true) {
            driver1List.clear()
            driver2List.clear()
            driver3List.clear()
            checkingInspectorList.clear()
            cleanerList.clear()
            coachList.clear()
            callAllCoachApi()
            
        } else if (!isFromOnActivityResult && isValidateBusCrewUpdationForCoachs == false) {
            callEmployeesDetailsApi()
            callAllCoachApi()
            
        }
        
        setAllCoachObserver()
        setPickupChartCrewDetailsObserver()
        setEmployeesDetailsObserver()
        updateServiceAllotmentObserver()
        
        callPickupChartCrewDetailsApi()
        setSeatNoListAdapter()
        clickListener()

        binding.autoFillDetailsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                setCrewDetailsData()
            } else {
                clearCrewDetailsData()
            }
        }
        
        binding.apply {
            
            layoutCoach.setEndIconOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listXCoach = coachList,
                    listX = null,
                    viewX = binding.acSelectCoach,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = COACH_UPDATE_DETAILS
                )
            }
            
            acSelectCoach.setOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listXCoach = coachList,
                    listX = null,
                    viewX = binding.acSelectCoach,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = COACH_UPDATE_DETAILS
                )
            }
            
            layoutDriver1.setEndIconOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listXCoach = null,
                    listX = driver1List,
                    viewX = binding.acDriver1,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = DRIVER_1_UPDATE_DETAILS
                )
            }
            
            acDriver1.setOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listXCoach = null,
                    listX = driver1List,
                    viewX = binding.acDriver1,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = DRIVER_1_UPDATE_DETAILS
                )
            }
            
            layoutDriver2.setEndIconOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listX = driver2List,
                    viewX = binding.acDriver2,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = DRIVER_2_UPDATE_DETAILS
                )
            }
            
            acDriver2.setOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listX = driver2List,
                    viewX = binding.acDriver2,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = DRIVER_2_UPDATE_DETAILS
                )
            }
            
            layoutDriver3.setEndIconOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listX = driver3List,
                    viewX = binding.acDriver3,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = DRIVER_3_UPDATE_DETAILS
                )
            }
            
            acDriver3.setOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listX = driver3List,
                    viewX = binding.acDriver3,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = DRIVER_3_UPDATE_DETAILS
                )
            }
            
            layoutCleaner.setEndIconOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listX = cleanerList,
                    viewX = binding.acCleaner,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = CLEANER_UPDATE_DETAILS
                )
            }
            
            acCleaner.setOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listX = cleanerList,
                    viewX = binding.acCleaner,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = CLEANER_UPDATE_DETAILS
                )
            }
            
            layoutContractor.setEndIconOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listX = contractorList,
                    viewX = binding.acContractor,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = CONDUCTOR_UPDATE_DETAILS
                )
            }
            
            acContractor.setOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listX = contractorList,
                    viewX = binding.acContractor,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = CONDUCTOR_UPDATE_DETAILS
                )
            }
            
            layoutCheckingInspector.setEndIconOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listX = checkingInspectorList,
                    viewX = binding.acCheckingInspector,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = CHECKING_INSPECTOR_UPDATE_DETAILS
                )
            }
            
            acCheckingInspector.setOnClickListener {
                DialogUtils.setSearchDataPopupDialog(
                    context = requireContext(),
                    listX = checkingInspectorList,
                    viewX = binding.acCheckingInspector,
                    dialogButtonAnyDataListener = this@CrewDetailsFragmentNewFlow,
                    type = CHECKING_INSPECTOR_UPDATE_DETAILS
                )
            }
            
            
            acChartOperatedBy.setOnClickListener {
                
                if (binding.acSelectCoach.text.toString()
                        .isEmpty() && isValidateBusCrewUpdationForCoachs == true
                ) {
                    requireContext().toast(getString(R.string.pleaseSelectCoach))
                    return@setOnClickListener
                }
                
                if (isSeatClick == 0) {
                    isSeatClick++
                    binding.rvChartOperatedBy.visible()
                    Timber.d("itemData- $selectedChartOperatedBy")
                    selectedChartOperatedBy.clear()
                } else {
                    binding.rvChartOperatedBy.gone()
                    isSeatClick = 0
                    selectedChartOperatedBy.clear()
                    for (i in 0 until currentCheckedItem.size) {
                        selectedChartOperatedBy.append(currentCheckedItem[i].name)
                        if (i < currentCheckedItem.size - 1) {
                            selectedChartOperatedBy.append(",")
                        }
                    }
                    binding.acChartOperatedBy.setText(selectedChartOperatedBy)
                }
            }
            
            acChartOperatedByLayout.setEndIconOnClickListener {
                
                if (binding.acSelectCoach.text.toString()
                        .isEmpty() && isValidateBusCrewUpdationForCoachs == true
                ) {
                    requireContext().toast(getString(R.string.pleaseSelectCoach))
                    return@setEndIconOnClickListener
                }
                binding.rvChartOperatedBy.gone()
                
                if (isSeatClick == 0) {
                    isSeatClick++
                    binding.rvChartOperatedBy.visible()
                    selectedChartOperatedBy.clear()
                } else {
                    binding.rvChartOperatedBy.gone()
                    isSeatClick = 0
                    selectedChartOperatedBy.clear()
                    for (i in 0 until currentCheckedItem.size) {
                        selectedChartOperatedBy.append(currentCheckedItem[i].name)
                        if (i < currentCheckedItem.size - 1) {
                            selectedChartOperatedBy.append(",")
                        }
                    }
                    binding.acChartOperatedBy.setText(selectedChartOperatedBy)
                    
                }
            }
            
            btnUpdate.setOnClickListener {
                if (binding.acSelectCoach.text.toString().isEmpty()
                    && isValidateBusCrewUpdationForCoachs == true
                ) {
                    requireContext().toast(getString(R.string.pleaseSelectCoach))
                    return@setOnClickListener
                }
                btnUpdate.setBackgroundResource(R.drawable.shape_not_filled)
                btnUpdate.isEnabled = false
                callServiceAllotmentApi()
                rvChartOperatedBy.gone()
            }
        }
        
    }

    private fun enableCoachSelection(enable: Boolean) {
        binding.apply {
            acSelectCoach.isEnabled = enable
            layoutCoach.isEnabled = enable
        }
    }
    
    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
        
        if ((activity as BaseActivity).getPrivilegeBase() != null) {
            val privilegeResponseModel: PrivilegeResponseModel =
                (activity as BaseActivity).getPrivilegeBase() as PrivilegeResponseModel
            
            privilegeResponseModel.apply {
                if (validateBusCrewUpdationForCoachs != null) {
                    isValidateBusCrewUpdationForCoachs = validateBusCrewUpdationForCoachs
                }
                if (isChileApp!=null){
                    isFromChile = isChileApp
                }
                if (isFromChile){
                    binding.layoutDriver3Main.visible()
                    binding.layoutDriver3PhoneMain.visible()
                }else{
                    binding.layoutDriver3Main.gone()
                    binding.layoutDriver3PhoneMain.gone()
                }
                
                if(notifyOption == true){
                    binding.sendSmsCB.visible()
                }else{
                    binding.sendSmsCB.gone()
                    
                }

                isAllowToAutoFillCrewDetails = tsPrivileges?.allowToAutoFillPreviousTripCrewDetails
            }
            privilegeResponseModel.apply {
                if (allowToCreateAdhocDriver != null) {
                    isAllowToCreateAdhocDriver = allowToCreateAdhocDriver
                }
            }

//            if (privilegeResponseModel.isShowContractorAsAttendentInBusMobility != null) {
//                isShowContractorAsAttendent = privilegeResponseModel.isShowContractorAsAttendentInBusMobility
//            }
            
            if (!privilegeResponseModel.country.isNullOrEmpty()) {
                country = privilegeResponseModel.country
            }

            if (loginModelPref.role.equals(requireContext().getString(R.string.role_field_officer), true)
                && privilegeResponseModel.boLicenses?.allowToUpdateVehicleExpenses == true)
            {
                binding.clickBlocker.visible()
            } else {
                binding.clickBlocker.gone()
            }
        }
        
        if (isAllowToCreateAdhocDriver == true) {
            binding.layoutAdhocDriver.visible()
        } else {
            binding.layoutAdhocDriver.gone()
        }
        
        if (country != null && country.equals(INDIA, true)) {
            binding.layoutContractor.hint = requireContext().getString(R.string.conductor)
        } else {
            binding.layoutContractor.hint = requireContext().getString(R.string.contractor)
        }
    }
    
    private fun clickListener() {
        
        binding.crossDriver3.setOnClickListener(this)
        binding.crossCleaner.setOnClickListener(this)
        binding.crossConstractor.setOnClickListener(this)
        binding.crossInspecter.setOnClickListener(this)
        binding.crossDriver2.setOnClickListener(this)
        binding.crossDriver1.setOnClickListener(this)
        binding.crossCoach.setOnClickListener(this)
        binding.layoutAdhocDriver.setOnClickListener(this)
        
    }
    
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                isFromOnActivityResult = true
                selectedReturnCrewDriverId =
                    result.data?.getStringExtra(getString(R.string.idFromCrewDetails)).toString()
                selectedReturnCrewDriverName =
                    result.data?.getStringExtra(getString(R.string.nameFromCrewDetails)).toString()
                val crewDetailsAllCoach = result.data?.getBundleExtra("coachListBundle")
                    ?.getSerializable("coachListArrayList") as ArrayList<Employee?>?
                Timber.d("coachListArrayList  $selectedReturnCrewDriverName")
                if (isValidateBusCrewUpdationForCoachs == true) {
                    if (result.data?.getBooleanExtra("ALLCOACH", false) == true) {
                        binding.acSelectCoach.setText(selectedReturnCrewDriverName)
                        selectedCoachId = selectedReturnCrewDriverId.toString()
                        clearList()
                        binding.acDriver1.setText("")
                        selectedDriver1Id = ""
                        binding.acDriver2.setText("")
                        selectedDriver2Id = ""
                        if (isFromChile){
                            binding.acDriver3.setText("")
                            selectedDriver3Id = ""
                        }
                        binding.acContractor.setText("")
                        selectedContracterId = ""
                        binding.acCleaner.setText("")
                        selectedCleanerId = ""
                        binding.acCheckingInspector.setText("")
                        selectedCheckInspectorId = ""
                        
                        
                        if (crewDetailsAllCoach?.isNotEmpty() == true) {
                            crewDetailsAllCoach.forEach {
                                if (it != null) {
                                    when (it.employeeType.toString()) {
                                        getString(R.string.driver) -> {
                                            driver1List.add(it)
                                            driver2List.add(it)
                                            if (isFromChile){
                                                driver3List.add(it)}
                                        }
                                        getString(R.string.contractor2) -> {
                                            contractorList.add(it)
                                        }
                                        getString(R.string.cleaner2) -> {
                                            cleanerList.add(it)
                                        }
                                        getString(R.string.checking_inspector) -> {
                                            checkingInspectorList.add(it)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }// else {
                when (result.data?.getStringExtra(getString(R.string.crewDetailsKey))) {
                    
                    getString(R.string.coach) -> {
                        binding.acSelectCoach.setText(selectedReturnCrewDriverName)
                        selectedCoachId = selectedReturnCrewDriverId.toString()
                    }
                    getString(R.string.driver_1) -> {
                        binding.acDriver1.setText(selectedReturnCrewDriverName)
                        selectedDriver1Id = selectedReturnCrewDriverId.toString()
                    }
                    
                    getString(R.string.driver_2) -> {
                        binding.acDriver2.setText(selectedReturnCrewDriverName)
                        selectedDriver2Id = selectedReturnCrewDriverId.toString()
                    }
                    getString(R.string.driver_3) -> {
                        binding.acDriver3.setText(selectedReturnCrewDriverName)
                        selectedDriver3Id = selectedReturnCrewDriverId.toString()
                    }
                    getString(R.string.contractor2) -> {
                        binding.acContractor.setText(selectedReturnCrewDriverName)
                        selectedContracterId = selectedReturnCrewDriverId.toString() // selectedCleanerId
                    }
                    getString(R.string.conductor2) -> {
                        binding.acContractor.setText(selectedReturnCrewDriverName)
                        selectedContracterId = selectedReturnCrewDriverId.toString()
                    }
                    getString(R.string.cleaner2) -> {
                        binding.acCleaner.setText(selectedReturnCrewDriverName)
                        selectedCleanerId = selectedReturnCrewDriverId.toString()
                    }
                    getString(R.string.checking_inspector) -> {
                        binding.acCheckingInspector.setText(selectedReturnCrewDriverName)
                        selectedCheckInspectorId =
                            selectedReturnCrewDriverId.toString() // selectedCheckInspectorId
                    }
                }
                driver1Cross()
                driver2Cross()
                driver3Cross()
                cleanerCross()
                contracterCross()
                inspecterCross()
                coachCross()
            }
        }
    
    private fun clearList() {
        driver1List.clear()
        driver2List.clear()
        driver3List.clear()
        cleanerList.clear()
        contractorList.clear()
        checkingInspectorList.clear()
        
    }
    
    private fun changeUpdateBtnBehavior() {
        binding.apply {
            acSelectCoach.addTextChangedListener(textWatcher)
            acDriver1.addTextChangedListener(textWatcher)
            acDriver2.addTextChangedListener(textWatcher)
            acDriver3.addTextChangedListener(textWatcher)
            acCleaner.addTextChangedListener(textWatcher)
            acContractor.addTextChangedListener(textWatcher)
            acChartOperatedBy.addTextChangedListener(textWatcher)
            acCheckingInspector.addTextChangedListener(textWatcher)
        }
    }
    
    private fun callAllCoachApi() {
        AllCoachRequest(
            bccId.toString(),
            format_type,
            all_coach_method_name,
            ReqBody(
                loginModelPref.api_key,
                true,
                resID.toString(),
                locale = locale
            )
        )
        
        /*pickUpChartViewModel.getAllCoach(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            allCoachRequest,
            all_coach_method_name
        )*/
        
        pickUpChartViewModel.getAllCoach(
            loginModelPref.api_key,
            resID.toString(),
            locale ?: "en",
            all_coach_method_name
        )
    }
    
    
    private fun callServiceAllotmentApi() {
        Timber.d("selectedChartOperatedBy - ${selectedChartOperatedBy.toString().lowercase().replace(" ", "")}")
        
        sendBusInfoCheckBox = binding.sendSmsCB.isChecked
        
        ServiceAllotmentRequest(
            bccId.toString(),
            format_type,
            update_service_allotment_method_name,
            com.bitla.ts.domain.pojo.service_allotment.request.ReqBody(
                apiKey = loginModelPref.api_key,
                reservationId = resID.toString(),
                coachId = selectedCoachId,
                driver1 = selectedDriver1Id,
                driver2 = selectedDriver2Id,
                driver3 = selectedDriver3Id,
                cleaner = selectedCleanerId, // selectedCleanerId
                checkingInspector = selectedCheckInspectorId, // selectedCheckInspectorId
                chartOperatedBy = selectedChartOperatedBy.toString().lowercase().replace(" ", ""), // chartOperatedBy
                contractor = selectedContracterId,
                locale = locale
            )
        )
        Timber.d("itemData2- $selectedChartOperatedBy")
        
        /*  pickUpChartViewModel.updateServiceAllotmentDetails(
              loginModelPref.auth_token,
              loginModelPref.api_key,
              serviceAllotmentRequest,
              update_service_allotment_method_name
          )*/
        
        pickUpChartViewModel.updateServiceAllotmentDetails(
            resID ?: 0,
            com.bitla.ts.domain.pojo.service_allotment.request.ReqBody(
                apiKey = loginModelPref.api_key,
                reservationId = resID.toString(),
                coachId = selectedCoachId,
                driver1 = selectedDriver1Id,
                driver2 = selectedDriver2Id,
                driver3 = selectedDriver3Id,
                cleaner = selectedCleanerId, // selectedCleanerId
                checkingInspector = selectedCheckInspectorId, // selectedCheckInspectorId
                chartOperatedBy = selectedChartOperatedBy.toString().lowercase().replace(" ", ""), // chartOperatedBy
                contractor = selectedContracterId,
                locale = locale,
                sendBusInfo = sendBusInfoCheckBox
            )
        )
    }
    
    private fun updateServiceAllotmentObserver() {
        
        pickUpChartViewModel.serviceAllotmentResponse.observe(requireActivity()) {
            try {
                Timber.d("serviceAllotmentResponse $it")
                if (it != null) {
                    when {
                        it.code == 200 -> {
                            if (it?.message != null) {
                                it?.message?.let { it1 -> requireContext().toast(it1) }
                            }
                            requireActivity().finish()
                        }
                        it.code == 401 -> {
                            DialogUtils.unAuthorizedDialog(
                                requireContext(),
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )
                        }
                        it.code == 411 -> {
                            if (it.result?.message != null) {
                                it?.result.message?.let { it1 -> requireContext().toast(it1) }
                            }
                        }
                        it?.message != null -> {
                            it?.message?.let { it1 -> requireContext().toast(it1) }
                        }
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                requireContext().toast(requireContext().getString(R.string.opps))
                Timber.d("An error occurred at updateServiceAllotmentObserver(): ${t.message}")
            }
        }
    }
    
    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }
    
    override fun onClick(v: View?) {
        
        when (v?.id) {
            R.id.layoutAdhocDriver -> {
                val intent = Intent(requireContext(), AddDriverActivity::class.java)
                startActivity(intent)
            }
            R.id.cross_driver_3 ->{
                binding.acDriver3.clearFocus()
                binding.crossDriver3Layout.gone()
                binding.acDriver3.text.clear()
                selectedDriver3Id= ""
            }
            R.id.cross_driver_2 ->{
                binding.acDriver2.clearFocus()
                binding.crossDriver2Layout.gone()
                binding.acDriver2.text.clear()
                selectedDriver2Id= ""
            }
            R.id.cross_driver_1 ->{
                binding.acDriver1.clearFocus()
                binding.crossDriver1Layout.gone()
                binding.acDriver1.text.clear()
                selectedDriver1Id = ""
            }
            R.id.cross_cleaner -> {
                binding.acCleaner.clearFocus()
                binding.crossCleanerLayout.gone()
                binding.acCleaner.text.clear()
                selectedCleanerId = ""
            }
            R.id.cross_constractor -> {
                binding.acContractor.clearFocus()
                binding.crossConstractorLayout.gone()
                binding.acContractor.text.clear()
                selectedContracterId = ""
            }
            R.id.cross_inspecter -> {
                binding.acCheckingInspector.clearFocus()
                binding.crossInspecterLayout.gone()
                binding.acCheckingInspector.text.clear()
                selectedCheckInspectorId = ""
            }
            R.id.cross_coach -> {
                binding.acSelectCoach.clearFocus()
                binding.crossCoachLayout.gone()
                binding.acSelectCoach.text.clear()
                selectedCoachId = ""

                binding.autoFillDetailsCheckbox.gone()
                binding.lastLocationLayout.gone()
            }
            
        }
    }
    
    private fun setSeatNoListAdapter() {
        val searchModel2 = SearchModel()
        val searchModel = SearchModel()
        searchModel.name = "Driver 1"
        searchModel.id = 0
        val searchModel1 = SearchModel()
        searchModel1.name = "Driver 2"
        searchModel1.id = 1
        if (isFromChile){
            searchModel2.name = "Driver 3"
            searchModel2.id = 2
        }
        val searchModel3 = SearchModel()
        searchModel3.name = "Cleaner"
        searchModel3.id = 3
        val searchModel4 = SearchModel()
        if (country != null && country.equals(INDIA, true)) {
            searchModel4.name = requireContext().getString(R.string.conductor)
        } else {
            searchModel4.name = requireContext().getString(R.string.contractor)
        }
        searchModel4.id = 4
        
        chartOperatedList.apply {
            add(searchModel)
            add(searchModel1)
            if (isFromChile){
                add(searchModel2)
            }
            add(searchModel3)
            add(searchModel4)
        }
        
        binding.rvChartOperatedBy.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        selectMultipleSeatsAdapter =
            SelectMultipleChartOperatorAdapter(requireContext(), chartOperatedList, checkList, this)
        binding.rvChartOperatedBy.adapter = selectMultipleSeatsAdapter

//        if (chartOperatedList.size>0){
//          binding.acChartOperatedBy.setText("${chartOperatedList[0]}",false)
//        }
    }
    
    override fun onItemCheck(item: SearchModel) {
        val index = currentCheckedItem.indexOfFirst {
            it.id == item.id
        }
        if (index != -1) {
            currentCheckedItem[index] = item
        } else {
            currentCheckedItem.add(item)
        }
        selectedChartOperatedBy.clear()
        
        for (i in 0 until currentCheckedItem.size) {
            selectedChartOperatedBy.append(currentCheckedItem[i].name)
            if (i < currentCheckedItem.size - 1) {
                selectedChartOperatedBy.append(",")
            }
        }
        binding.acChartOperatedBy.setText(selectedChartOperatedBy)
    }
    
    override fun onItemUncheck(item: SearchModel) {
        val index = currentCheckedItem.indexOfFirst {
            it.id == item.id
        }
        if (index != -1) {
            currentCheckedItem.removeAt(index)
        }
        
        selectedChartOperatedBy.clear()
        
        for (i in 0 until currentCheckedItem.size) {
            selectedChartOperatedBy.append(currentCheckedItem[i].name)
            if (i < currentCheckedItem.size - 1) {
                selectedChartOperatedBy.append(",")
            }
        }
        binding.acChartOperatedBy.setText(selectedChartOperatedBy)
    }
    
    private fun callPickupChartCrewDetailsApi() {

        pickUpChartViewModel.pickupChartCrewDetailsApi(
            apiKey = loginModelPref.api_key,
            reservationId = resID.toString(),
            apiType = pickup_chart_crew_details,
            locale = locale.toString(),
            selectedCoachId
        )
    }
    
    private fun setPickupChartCrewDetailsObserver() {
        
        pickUpChartViewModel.pickupChartCrewDetailsResponse.observe(requireActivity()) { it ->
            
            try {
                Timber.d("setPickupChartCrewDetailsObserver: ${it}")
                if (it != null) {
                    when (it.code) {
                        200 -> {

                            crewDetails = it

                            if (selectedCoachId.isEmpty()) {
                                binding.acSelectCoach.setText(it.coach,false)
                                setCrewDetailsData()
                                callEmployeesDetailsApi()
                            }

                            if(isAllowToAutoFillCrewDetails == true) {
                                binding.lastLocationLayout.visible()
                                if(selectedCoachId.isNotEmpty()) {
                                    binding.autoFillDetailsCheckbox.visible()
                                }
                                if(it.lastLocation.isNotEmpty()) {
                                    binding.lastLocationTV.text = it.lastLocation
                                } else {
                                    binding.lastLocationTV.text = requireContext().getString(R.string.notAvailable)
                                }
                                if(binding.autoFillDetailsCheckbox.isChecked) {
                                    setCrewDetailsData()
                                }
                            } else {
                                binding.autoFillDetailsCheckbox.gone()
                                binding.lastLocationLayout.gone()
                            }

                        }
                        401 -> {
                            DialogUtils.unAuthorizedDialog(
                                requireContext(),
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )
                        }
                        else -> {
                            if (!it.result?.message.equals(
                                    "No data available",
                                    true
                                ) && !it.result?.message.equals("id-No data Available", true)
                            ) {
                                binding.autoFillDetailsCheckbox.gone()
                                binding.lastLocationLayout.gone()
                                it.result?.message?.let { it1 -> requireContext().toast(it1) }
                            }
                            
                        }
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                requireContext().toast(requireContext().getString(R.string.opps))
                Timber.d("An error occurred at setPickupChartCrewDetailsObserver(): ${t.message}")
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
    }
    
    override fun onButtonClick(view: Any, dialog: Dialog) {
    }
    
    private fun coachCross() {
        if (binding.acSelectCoach.text.isNotEmpty()) {
            binding.crossCoachLayout.visible()
        } else {
            binding.crossCoachLayout.gone()
        }
    }
    
    private fun driver1Cross(){
        if (binding.acDriver1.text.isNotBlank()){
            binding.crossDriver1Layout.visible()
        }else{
            binding.crossDriver1Layout.gone()
        }
    }
    private fun driver2Cross() {
        if (binding.acDriver2.text.isNotBlank()) {
            binding.crossDriver2Layout.visible()
        } else {
            binding.crossDriver2Layout.gone()
        }
    }
    
    private fun driver3Cross(){
        if (binding.acDriver3.text.isNotBlank()){
            binding.crossDriver3Layout.visible()
        }else{
            binding.crossDriver3Layout.gone()
        }
    }
    private fun cleanerCross(){
        if (binding.acCleaner.text.isNotBlank()){
            binding.crossCleanerLayout.visible()
        }else{
            binding.crossCleanerLayout.gone()
        }
    }
    private fun inspecterCross(){
        if (binding.acCheckingInspector.text.isNotBlank()){
            binding.crossInspecterLayout.visible()
        }else{
            binding.crossInspecterLayout.gone()
        }
    }
    private fun contracterCross(){
        if (binding.acContractor.text.isNotBlank()){
            binding.crossConstractorLayout.visible()
        }else{
            binding.crossConstractorLayout.gone()
        }
    }
    
    
    private fun getPrefQrCode() {
        try {
            getPreviousScannerQr()
            val scannedDriverId =
                PreferenceUtils.getPreference(getString(R.string.scannedUserId), "")
            val scannedDriverName =
                PreferenceUtils.getPreference(getString(R.string.scannedUserName), "")
            selectedScanType = PreferenceUtils.getPreference("selectedScanType", "")
            
            
            if (!scannedDriverId.isNullOrEmpty() && !scannedDriverName.isNullOrEmpty()) {
                
                spinnerItemsScanned =
                    SpinnerItems(scannedDriverId.toInt(), scannedDriverName.toString())
                if (spinnerItemsScanned != null) {
                    
                    if (PreferenceUtils.getPreference(
                            "selectedScanType",
                            ""
                        ) == getString(R.string.scan_coach)
                    ) {
                        val index = coachList.indexOfFirst {
                            it.id.toInt() == spinnerItemsScanned!!.id
                        }
                        if (index != -1) {
                            binding.acSelectCoach.setText(spinnerItemsScanned!!.value, false)
                            selectedCoachId = spinnerItemsScanned!!.id.toString()
                            PreferenceUtils.putObject(
                                spinnerItemsScanned,
                                getString(R.string.scan_coach)
                            )
                            callShowSuccessfulDialog()
                        } else {
                            requireContext().toast(requireContext().getString(R.string.coach_not_found))
                        }
                    } else if (PreferenceUtils.getPreference(
                            "selectedScanType",
                            ""
                        ) == getString(R.string.scan_driver_1)
                    ) {
                        val index = driver1List.indexOfFirst {
                            it.id == spinnerItemsScanned!!.id
                        }
                        if (index != -1) {
                            binding.acDriver1.setText(spinnerItemsScanned!!.value, false)
                            selectedDriver1Id = spinnerItemsScanned!!.id.toString()
                            PreferenceUtils.putObject(
                                spinnerItemsScanned,
                                getString(R.string.scan_driver_1)
                            )
                            callShowSuccessfulDialog()
                            
                        } else {
                            requireContext().toast(requireContext().getString(R.string.driver_1_not_found))
                        }
                    } else if (PreferenceUtils.getPreference(
                            "selectedScanType",
                            ""
                        ) == getString(R.string.scan_driver_2)
                    ) {
                        val index = driver2List.indexOfFirst {
                            it.id == spinnerItemsScanned!!.id
                        }
                        if (index != -1) {
                            binding.acDriver2.setText(spinnerItemsScanned!!.value, false)
                            selectedDriver2Id = spinnerItemsScanned!!.id.toString()
                            PreferenceUtils.putObject(
                                spinnerItemsScanned,
                                getString(R.string.scan_driver_2)
                            )
                            callShowSuccessfulDialog()
                            
                        } else {
                            requireContext().toast(requireContext().getString(R.string.driver_2_not_found))
                        }
                    } else if (isFromChile) {
                        if (PreferenceUtils.getPreference(
                                "selectedScanType",
                                ""
                            ) == getString(R.string.scan_driver_3)
                        ) {
                            val index = driver3List.indexOfFirst {
                                it.id == spinnerItemsScanned!!.id
                            }
                            if (index != -1) {
                                binding.acDriver3.setText(spinnerItemsScanned!!.value, false)
                                selectedDriver3Id = spinnerItemsScanned!!.id.toString()
                                PreferenceUtils.putObject(
                                    spinnerItemsScanned,
                                    getString(R.string.scan_driver_3)
                                )
                                callShowSuccessfulDialog()
                                
                            } else {
                                requireContext().toast(requireContext().getString(R.string.driver_3_not_found))
                            }
                        }} else if (PreferenceUtils.getPreference(
                            "selectedScanType",
                            ""
                        ) == getString(R.string.scan_cleaner)
                    ) {
                        val index = cleanerList.indexOfFirst {
                            it.id == spinnerItemsScanned?.id
                        }
                        if (index != -1) {
                            binding.acCleaner.setText(spinnerItemsScanned!!.value, false)
                            selectedCleanerId = spinnerItemsScanned!!.id.toString()
                            PreferenceUtils.putObject(
                                spinnerItemsScanned,
                                getString(R.string.scan_cleaner)
                            )
                            callShowSuccessfulDialog()
                            
                        } else {
                            requireContext().toast(requireContext().getString(R.string.cleaner_not_found))
                        }
                    } else if (PreferenceUtils.getPreference(
                            "selectedScanType",
                            ""
                        ) == getString(R.string.scan_contractor)
                    ) {
                        val index = contractorList.indexOfFirst {
                            it.id == spinnerItemsScanned!!.id
                        }
                        if (index != -1) {
                            binding.acContractor.setText(spinnerItemsScanned!!.value, false)
                            selectedContracterId = spinnerItemsScanned!!.id.toString()
                            PreferenceUtils.putObject(
                                spinnerItemsScanned,
                                getString(R.string.scan_contractor)
                            )
                            callShowSuccessfulDialog()
                            
                        } else {
                            requireContext().toast(requireContext().getString(R.string.contractor_not_found))
                        }
                    }
                }
                driver1Cross()
                driver2Cross()
                driver3Cross()
                cleanerCross()
                contracterCross()
                inspecterCross()
//                coachCross()
            }
        } catch (e: Exception) {
            requireContext().toast(requireContext().getString(R.string.opps))
            Timber.d("An error occurred at getPreviousScannerQr(): ${e.message}")
            PreferenceUtils.removeKey(getString(R.string.scannedUserName))
            PreferenceUtils.removeKey(getString(R.string.scannedUserId))
            PreferenceUtils.removeKey("selectedScanType")
        }
        
    }
    
    private fun getPreviousScannerQr() {
        try {
            
            if (PreferenceUtils.getObject<SpinnerItems>(getString(R.string.scan_coach)) != null) {
                requireContext().toast("jyvj")
                val getCoachQr: SpinnerItems? =
                    PreferenceUtils.getObject<SpinnerItems>(getString(R.string.scan_coach))
                val index = coachList.indexOfFirst {
                    it.id.toInt()  == getCoachQr?.id
                }
                if (index != -1) {
                    binding.acSelectCoach.setText(getCoachQr?.value, false)
                    selectedCoachId = getCoachQr?.id.toString()
//                    coachCross()
                }
            }
            if (PreferenceUtils.getObject<SpinnerItems>(getString(R.string.scan_driver_1)) != null) {
                val getCoachQr: SpinnerItems? =
                    PreferenceUtils.getObject<SpinnerItems>(getString(R.string.scan_driver_1))
                val index = driver1List.indexOfFirst {
                    it.id == getCoachQr?.id
                }
                if (index != -1) {
                    binding.acDriver1.setText(getCoachQr?.value, false)
                    selectedDriver1Id = getCoachQr?.id.toString()
                    driver1Cross()
                }
            }
            if (PreferenceUtils.getObject<SpinnerItems>(getString(R.string.scan_driver_2)) != null) {
                val getCoachQr: SpinnerItems? =
                    PreferenceUtils.getObject<SpinnerItems>(getString(R.string.scan_driver_2))
                val index = driver2List.indexOfFirst {
                    it.id == getCoachQr?.id
                }
                if (index != -1) {
                    binding.acDriver2.setText(getCoachQr?.value, false)
                    selectedDriver2Id = getCoachQr?.id.toString()
                    driver2Cross()
                }
            }
            if (isFromChile) {
                if (PreferenceUtils.getObject<SpinnerItems>(getString(R.string.scan_driver_3)) != null) {
                    val getCoachQr: SpinnerItems? =
                        PreferenceUtils.getObject<SpinnerItems>(getString(R.string.scan_driver_3))
                    val index = driver3List.indexOfFirst {
                        it.id == getCoachQr?.id
                    }
                    if (index != -1) {
                        binding.acDriver3.setText(getCoachQr?.value, false)
                        driver3Cross()
                        selectedDriver3Id = getCoachQr?.id.toString()
                    }
                }
            }
            if (PreferenceUtils.getObject<SpinnerItems>(getString(R.string.scan_cleaner)) != null) {
                val getCoachQr: SpinnerItems? =
                    PreferenceUtils.getObject<SpinnerItems>(getString(R.string.scan_cleaner))
                val index = cleanerList.indexOfFirst {
                    it.id == getCoachQr?.id
                }
                if (index != -1) {
                    binding.acCleaner.setText(getCoachQr?.value, false)
                    selectedCleanerId = getCoachQr?.id.toString()
                    cleanerCross()
                }
            }
            if (PreferenceUtils.getObject<SpinnerItems>(getString(R.string.scan_contractor)) != null) {
                val getCoachQr: SpinnerItems? =
                    PreferenceUtils.getObject<SpinnerItems>(getString(R.string.scan_contractor))
                val index = contractorList.indexOfFirst {
                    it.id == getCoachQr?.id
                }
                if (index != -1) {
                    binding.acContractor.setText(getCoachQr?.value, false)
                    selectedContracterId = getCoachQr?.id.toString()
                    contracterCross()
                }
            }
        } catch (t: Throwable) {
            requireContext().toast(getString(R.string.opps))
            Timber.d("An error occurred at getPrefQrCode(): ${t.message}")
        }
        
    }
    
    private fun callShowSuccessfulDialog() {
        DialogUtils.successfulMsgDialog(
            requireContext(),
            requireContext().getString(R.string.successfully_added_details_with_tickmark)
        )
        PreferenceUtils.removeKey(getString(R.string.scannedUserName))
        PreferenceUtils.removeKey(getString(R.string.scannedUserId))
        PreferenceUtils.removeKey("selectedScanType")
    }
    
    private fun setAllCoachObserver() {
        
        pickUpChartViewModel.allCoachResponse.observe(requireActivity()) { it ->
            
            binding.progressBarList.gone()
            
            try {
                Timber.d("${it.allCoaches}")
                
                if (it != null) {
                    if (it.code == 200) {
                        val kotlinCoachList = it.allCoaches?.let { list ->
                            ArrayList(list)
                        } ?: arrayListOf()

                        val reversedList = mutableListOf<AllCoach>()
                        for (i in kotlinCoachList.size - 1 downTo 0) {
                            reversedList.add(kotlinCoachList[i])
                        }
                        reversedList.forEach {

                            coachList.add(it)

                            if (it.name.equals(binding.acSelectCoach.text.toString(), true)) {
                                selectedReturnCrewDriverName = it.name
                                selectedCoachId = it.id.toString()

                                clearList()

                                if (it.employees.isNullOrEmpty().not()) {
                                    it.employees?.forEach {
                                        if (it != null) {
                                            when (it.employeeType.toString()) {
                                                getString(R.string.driver) -> {
                                                    driver1List.add(it)
                                                    driver2List.add(it)
                                                    if (isFromChile){
                                                        driver3List.add(it)}

                                                }

                                                getString(R.string.conductor2) -> {
                                                    contractorList.add(it)
                                                }
                                                getString(R.string.contractor2) -> {
                                                    contractorList.add(it)
                                                }
                                                getString(R.string.cleaner2) -> {
                                                    cleanerList.add(it)
                                                }
                                                getString(R.string.checking_inspector) -> {
                                                    checkingInspectorList.add(it)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        getPrefQrCode()
                        enableCoachSelection(true)
                        
                    } else if (it.code == 401) {
                        DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )
                    } else {
                        getPrefQrCode()
                        if (it?.result?.message != null) {
                            it?.result?.message?.let { it1 -> requireActivity().toast(it1) }
                        }
                    }
                } else {
                    getPrefQrCode()
                    
                    requireContext().toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                requireContext().toast(getString(R.string.opps))
                Timber.d("An error occurred at setAllCoachObserver(): ${t.message}")
            }
        }
    }
    
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        
        }
        
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        
        }
        
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (
                binding.acSelectCoach.text.toString().isEmpty().not() ||
                binding.acDriver1.text.toString().isEmpty().not() ||
                binding.acDriver2.text.toString().isEmpty().not() ||
                binding.acDriver3.text.toString().isEmpty().not() ||
                binding.acCleaner.text.toString().isEmpty().not() ||
                binding.acContractor.text.toString().isEmpty().not() ||
                (binding.acChartOperatedBy.text.toString().isEmpty().not() &&
                        selectedDriver1Id.isNotEmpty() ||
                        selectedDriver2Id.isNotEmpty() ||
                        selectedDriver3Id.isNotEmpty() ||
                        selectedCleanerId.isNotEmpty() ||
                        selectedCheckInspectorId.isNotEmpty() ||
                        selectedCoachId.isNotEmpty())
            ) {
                binding.btnUpdate.setBackgroundResource(R.drawable.button_selected_bg)
                binding.btnUpdate.isEnabled = true
                
            } else {
                binding.btnUpdate.setBackgroundResource(R.drawable.button_default_bg)
                binding.btnUpdate.isEnabled = false
            }
        }
    }
    
    private fun callEmployeesDetailsApi() {
        EmployeesDetailsRequest(
            
            bccId = bccId.toString(),
            format = format_type,
            methodName = employee_details_method_name,
            reqBody = com.bitla.ts.domain.pojo.employees_details.request.ReqBody(
                loginModelPref.api_key,
                response_format,
                locale = locale
            )
        )
        
        /*pickUpChartViewModel.getEmployeesDetails(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            employeesDetailsRequest,
            employee_details_method_name
        )*/
        
        pickUpChartViewModel.getEmployeesDetails(
            apiKey = loginModelPref.api_key,
            apiType = employee_details_method_name,
            locale = locale.toString()
        )
    }
    
    private fun setEmployeesDetailsObserver() {
        
        pickUpChartViewModel.employeesDetailsResponse.observe(requireActivity()) { it ->
            
            binding.progressBarList.gone()
            
            try {
                if (it != null) {
                    
                    if (it.code == 200) {
                        isShowContractorAsAttendent = "fff"
                        
                        it.employees.forEach {

                            if (it.employeeType == "DRIVER"){

                                driver1List.add(it)
                                driver2List.add(it)
                            }


                            if (isFromChile) {
                                if (it.employeeType == getString(R.string.driver))
                                    driver3List.add(it)
                            }
                            
                            if (it.employeeType == getString(R.string.cheking_inspector))
                                checkingInspectorList.add(it)
                            
                            if (it.employeeType == getString(R.string.contractor2))
                                contractorList.add(it)
                            
                            if (it.employeeType == getString(R.string.conductor2))
                                contractorList.add(it)
                            
                            if (it.employeeType == getString(R.string.cleaner2))
                                cleanerList.add(it)
                        }
                        if (isValidateBusCrewUpdationForCoachs == false) {
                            getPrefQrCode()
                        }
//                        setCoachListAdapter(coachList)
//                        setDriver1ListAdapter(driver1List)
//                        setDriver2ListAdapter(driver2List)
//                        setDriver3ListAdapter(driver3List)
//                        setConductorListAdapter(contractorList)
//                        setCleanerListAdapter(cleanerList)
//                        setCheckingInspectorListAdapter(checkingInspectorList)
                        
                    } else if (it.code == 401) {
                        DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )
                    } else {
                        if (isValidateBusCrewUpdationForCoachs == false) {
                            getPrefQrCode()
                        }
                    }
                } else {
                    if (isValidateBusCrewUpdationForCoachs == false) {
                        getPrefQrCode()
                    }
                    requireContext().toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                requireContext().toast(requireContext().getString(R.string.opps))
                Timber.d("An error occurred at setEmployeesDetailsObserver(): ${t.message}")
            }
        }
    }
    
    override fun onDataSend(type: Int, file: Any) {
    }
    
    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {
        
        Timber.d("checkData = type= $type == file $file == extra $extra")
        
        when(type){
            1 -> {
                val selectedData = file as Origin
                when((extra as Int)) {
                    COACH_UPDATE_DETAILS -> {
                        binding.acSelectCoach.setText(selectedData.name)
                        selectedCoachId = selectedData.id.toString()
                        popupWindowX?.dismiss()
                        coachCross()

                        if(isAllowToAutoFillCrewDetails == true) {
                            callPickupChartCrewDetailsApi()
                        }
                    }
                    DRIVER_1_UPDATE_DETAILS -> {
                        binding.acDriver1.setText(selectedData.name)
                        selectedDriver1Id = selectedData.id.toString()
                        popupWindowX?.dismiss()
                        driver1Cross()
                    }
                    DRIVER_2_UPDATE_DETAILS -> {
                        binding.acDriver2.setText(selectedData.name)
                        selectedDriver2Id = selectedData.id.toString()
                        popupWindowX?.dismiss()
                        driver2Cross()
                    }
                    DRIVER_3_UPDATE_DETAILS -> {
                        binding.acDriver3.setText(selectedData.name)
                        selectedDriver3Id = selectedData.id.toString()
                        popupWindowX?.dismiss()
                        driver3Cross()
                    }
                    CLEANER_UPDATE_DETAILS -> {
                        binding.acCleaner.setText(selectedData.name)
                        selectedCleanerId = selectedData.id.toString()
                        popupWindowX?.dismiss()
                        cleanerCross()
                    }
                    CONDUCTOR_UPDATE_DETAILS -> {
                        binding.acContractor.setText(selectedData.name)
                        selectedContracterId = selectedData.id.toString()
                        popupWindowX?.dismiss()
                        contracterCross()
                    }
                    CHECKING_INSPECTOR_UPDATE_DETAILS -> {
                        binding.acCheckingInspector.setText(selectedData.name)
                        selectedCheckInspectorId = selectedData.id.toString()
                        popupWindowX?.dismiss()
                        inspecterCross()
                    }
                }
            }
        }
    }

    private fun setCrewDetailsData() {

        binding.apply {
            acDriver1.setText(crewDetails?.driver1, false)
            acDriver2.setText(crewDetails?.driver2, false)
            acDriver3.setText(crewDetails?.driver3, false)

            acCleaner.setText(crewDetails?.cleaner, false)
            acContractor.setText(crewDetails?.attendent, false)
            acChartOperatedBy.setText(crewDetails?.chartOperatedBy)
            acCheckingInspector.setText(crewDetails?.checkingInspector, false)
        }

        driver1Cross()
        driver2Cross()
        driver3Cross()
        cleanerCross()
        contracterCross()
        inspecterCross()
        coachCross()

        selectedDriver1Id = crewDetails?.driver1Id.toString()
        selectedDriver2Id = crewDetails?.driver2Id.toString()
        if (isFromChile) {
            selectedDriver3Id = crewDetails?.driver3Id.toString()
        }
        selectedCleanerId = crewDetails?.cleanerId.toString()
        selectedContracterId = crewDetails?.attendentId.toString()
        selectedCheckInspectorId = crewDetails?.checkingInspectorId.toString()

        selectedChartOperatedBy.clear()
        selectedChartOperatedBy.append(crewDetails?.chartOperatedBy)

//        Timber.d("chartOperatedBy - ${crewDetails?.chartOperatedBy}")
//        Timber.d("chartOperatedBy-selected - $selectedChartOperatedBy")

        val chartOperatedBy = crewDetails?.chartOperatedBy?.split(",")
        currentCheckedItem.clear()

        // if (!isFromChile){
        chartOperatedBy?.forEach {
            if (it.equals(requireContext().getString(R.string.driver_1), true)
                || it.equals(requireContext().getString(R.string.driver_1_no_space), true)
            ) {
                checkList.append(0, true)
                val searchModel = SearchModel()
                searchModel.name = "Driver 1"
                searchModel.id = 0
                onItemCheck(searchModel)
                //currentCheckedItem.add(searchModel)
            } else if (it.equals(requireContext().getString(R.string.driver_2_no_space), true)) {
                checkList.append(1, true)
                val searchModel = SearchModel()
                searchModel.name = "Driver 2"
                searchModel.id = 1
                onItemCheck(searchModel)
                //currentCheckedItem.add(searchModel)
            } else if (it.equals(
                    requireContext().getString(R.string.driver_3_no_space),
                    true
                )
            ) {
                checkList.append(2, true)
                val searchModel = SearchModel()
                searchModel.name = "Driver 3"
                searchModel.id = 2
                onItemCheck(searchModel)
                //currentCheckedItem.add(searchModel)
            } else if (it.equals(
                    "Cleaner",
                    true
                )
            ) {
                if(isFromChile){
                    checkList.append(3, true)
                    val searchModel = SearchModel()
                    searchModel.name = "Cleaner"
                    searchModel.id = 3
                    onItemCheck(searchModel)
                    //currentCheckedItem.add(searchModel)
                }else{
                    checkList.append(2, true)
                    val searchModel = SearchModel()
                    searchModel.name = "Cleaner"
                    searchModel.id = 2
                    onItemCheck(searchModel)
                    //currentCheckedItem.add(searchModel)
                }

            } else if (it.equals(
                    requireContext().getString(R.string.contractor),
                    true
                )
            ) {
                if(isFromChile){
                    checkList.append(4, true)
                    val searchModel = SearchModel()
                    if (country != null && country.equals(INDIA, true)) {
                        searchModel.name = requireContext().getString(R.string.conductor)
                    } else {
                        searchModel.name = requireContext().getString(R.string.contractor)
                    }
                    searchModel.id = 4
                    onItemCheck(searchModel)
                    //currentCheckedItem.add(searchModel)
                }else{
                    checkList.append(3, true)
                    val searchModel = SearchModel()
                    if (country != null && country.equals(INDIA, true)) {
                        searchModel.name = requireContext().getString(R.string.conductor)
                    } else {
                        searchModel.name = requireContext().getString(R.string.contractor)
                    }
                    searchModel.id = 3
                    onItemCheck(searchModel)
                    //currentCheckedItem.add(searchModel)
                }

            }
            // }
        }
    }

    private fun clearCrewDetailsData() {

        binding.apply {
            acDriver1.text.clear()
            acDriver2.text.clear()
            acDriver3.text.clear()
            acCleaner.text.clear()
            acContractor.text.clear()
            acChartOperatedBy.text.clear()
            acCheckingInspector.text.clear()

            crossDriver1Layout.gone()
            crossDriver2Layout.gone()
            crossDriver3Layout.gone()
            crossCleanerLayout.gone()
            crossConstractorLayout.gone()
            crossInspecterLayout.gone()
        }

        selectedDriver1Id = ""
        selectedDriver2Id = ""
        selectedDriver3Id = ""
        selectedCleanerId = ""
        selectedCheckInspectorId = ""
        selectedContracterId = ""

        currentCheckedItem.clear()
        checkList.apply {
            append(0, false)
            append(1, false)
            append(2, false)
            append(3, false)
        }
        selectedChartOperatedBy.clear()
        selectMultipleSeatsAdapter.notifyDataSetChanged()
    }
}