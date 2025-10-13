package com.bitla.ts.presentation.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.TsApplication
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemAdapterClick
import com.bitla.ts.databinding.BottomSheetCheckInspectorFilterBinding
import com.bitla.ts.databinding.FragmentCheckingInspectorBinding
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctRequest.AllotedDirectRequest
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.adapter.CheckingInspectorAdapter
import com.bitla.ts.presentation.view.activity.CityDetailsActivity
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.dashboard.DashboardNavigateActivity
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.edgeToEdgeFabButton
import com.bitla.ts.utils.common.setDateLocale
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.isActivityIsLive
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.text.SimpleDateFormat
import java.util.*

class CheckingInspectorFragment : Fragment(), DialogSingleButtonListener,
    OnItemAdapterClick {

    private var formattedCurrentDate: String = ""
    private var displayDate: String = ""
    private var currentDate: String = ""
    private var btnValidate = false
    private var dialogBinding: BottomSheetCheckInspectorFilterBinding? = null
    private var binding: FragmentCheckingInspectorBinding? = null
    private var pendingAdapter: CheckingInspectorAdapter? = null
    private var completedAdapter: CheckingInspectorAdapter? = null
    private var temOrigin = ""
    private var temDestination = ""
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var availableRoutesList =
        mutableListOf<Service>()
    private var tempList =
        arrayListOf<Service>()
    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String? = ""
    private var pendingInspectionList: ArrayList<Service> = arrayListOf()
    private var completedInspectionList: ArrayList<Service> = arrayListOf()
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var originData = ""
    private var destinationData = ""
    private var origindataID: String = "0"
    private var destinationdataID: String = "0"
    private var travelDate: String = ""
    private var travelSelection: String? = ""
    private var finaloriginID: String = ""
    private var finaldestinationId: String = ""
    private var destinationChangeCheck = false
    private var activeCheck = false
    private var hubChangeCheck = false
    private var originChangeCheck = false
    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private var dateSelected = ""
    private var lastOriginName = ""
    private var lastOriginID = ""
    private var lastDestinationName = ""
    private var lastDestinationId = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        (requireActivity() as DashboardNavigateActivity).showHideBottomBar(true)

        if (binding == null) {
            binding =
                FragmentCheckingInspectorBinding.inflate(inflater, container, false)
            initUi()
        }
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.fragments[0]
        Timber.d("current frag id : ${currentFragment.id}")
        allotedObserver()



        return binding!!.root
    }

    private fun initUi() {
        val mcalendar = Calendar.getInstance()
        day = mcalendar.get(Calendar.DAY_OF_MONTH)
        year = mcalendar.get(Calendar.YEAR)
        month = mcalendar.get(Calendar.MONTH)
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val sdf2 = SimpleDateFormat("yyyy-MM-dd")
        currentDate = sdf.format(Date())
        dateSelected = sdf2.format(Date())
        formattedCurrentDate = sdf2.format(Date())
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

        (activity as? DashboardNavigateActivity)?.increaseMarginTop()


        binding?.btnFilter?.let { edgeToEdgeFabButton(requireActivity(), it, 50) }

        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        if (requireActivity().isNetworkAvailable()) {
            allotedDirectService()
        } else {
            requireActivity().noNetworkToast()
        }
        setClickListeners()

    }

    private fun setClickListeners() {
        binding!!.btnFilter.setOnClickListener {
            showFilterDialog()
        }


    }

    private fun showFilterDialog() {
        btnValidate = false
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        dialogBinding = BottomSheetCheckInspectorFilterBinding.inflate(layoutInflater)
        bottomSheetDialog!!.setContentView(dialogBinding!!.root)
        bottomSheetDialog!!.setCancelable(true)

        if (originData != "") {
            dialogBinding!!.selectionFromCity.text = originData
        }
        if (destinationData != "") {
            dialogBinding!!.selectionToCity.text = destinationData
        }



        if (displayDate == "") {
            dialogBinding!!.dateET.setText(currentDate)
        } else {
            dialogBinding!!.dateET.setText(displayDate)
        }


        dialogBinding!!.selectionFromCity.setOnClickListener {
            PreferenceUtils.setPreference("TravelSelection", "OriginCity")
            val intent = Intent(requireContext(), CityDetailsActivity::class.java)
            intent.putExtra("is_checking_inspector", true)
            resultLaunch.launch(intent)
        }

        dialogBinding!!.selectionToCity.setOnClickListener {
            PreferenceUtils.setPreference("TravelSelection", "DestinationCity")
            val intent = Intent(requireContext(), CityDetailsActivity::class.java)
            intent.putExtra("is_checking_inspector", true)
            resultLaunch.launch(intent)
        }

        dialogBinding!!.btnApply.setOnClickListener {
            if (btnValidate) {
                btnValidate = false

                allotedDirectService()
                bottomSheetDialog!!.dismiss()
            }

        }

        dialogBinding!!.tvCancel.setOnClickListener {
            finaloriginID = ""
            finaldestinationId = ""
            originData = ""
            destinationData = ""
            dialogBinding!!.selectionToCity.text = "All"
            dialogBinding!!.selectionFromCity.text = "All"
            dialogBinding!!.dateET.setText(currentDate)
            displayDate = ""
            dateSelected = formattedCurrentDate
            PreferenceUtils.setPreference("selectedCityOrigin", "All")
            PreferenceUtils.setPreference("selectedCityIdOrigin", "0")
            PreferenceUtils.setPreference("selectedCityDestination", "All")
            PreferenceUtils.setPreference("selectedCityIdDestination", "0")
            PreferenceUtils.setPreference("TravelSelection", "none")
            dialogBinding!!.btnApply.isEnabled = true
            btnValidate = true
            dialogBinding!!.btnApply.backgroundTintList = ColorStateList.valueOf(
                requireContext().resources.getColor(
                    R.color.colorPrimary
                )
            )


        }

        dialogBinding!!.dateET.setOnClickListener {
            openDateDialog()
        }

        bottomSheetDialog!!.show()
    }


    private fun startShimmerEffect() {
        binding?.apply {
            shimmerDashboardDetails.visible()
            roolLL.gone()
            shimmerDashboardDetails.startShimmer()
            noData.root.gone()
        }
    }

    private fun stopShimmerEffect() {
        binding?.apply {
            roolLL.visible()
            shimmerDashboardDetails.startShimmer()
            noData.root.gone()
            shimmerDashboardDetails.gone()
            if (shimmerDashboardDetails.isShimmerStarted) {
                shimmerDashboardDetails.stopShimmer()
            }
        }
    }
    
    var resultLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val returnOriginCityName =
                    result.data?.getStringExtra("CityOriginCityName").toString()
                val returnDestinationCityName =
                    result.data?.getStringExtra("selectedCityDestination").toString()
                val returnOriginCityID =
                    result.data?.getStringExtra("selectedCityIdOrigin")
                val returnDestinationCityID =
                    result.data?.getStringExtra("selectedCityIdDestination")
                val travel =
                    result.data?.getStringExtra("TravelSelection")

                originData = returnOriginCityName
                origindataID = returnOriginCityID!!
                destinationData = returnDestinationCityName
                destinationdataID = returnDestinationCityID!!
                travelSelection = travel
                dialogBinding!!.btnApply.isEnabled = true
                btnValidate = true
                dialogBinding!!.btnApply.backgroundTintList = ColorStateList.valueOf(
                    requireContext().resources.getColor(
                        R.color.colorPrimary
                    )
                )



                PreferenceUtils.setPreference("selectedCityOrigin", originData)
                PreferenceUtils.setPreference("selectedCityDestination", destinationData)
                if (origindataID == "0") {
                    origindataID = ""
                    finaldestinationId = destinationdataID
                    finaloriginID = origindataID

                    PreferenceUtils.setPreference("selectedCityIdDestination", destinationdataID)
                    PreferenceUtils.setPreference("selectedCityIdOrigin", origindataID)
                    Timber.d("searchlist7", "${finaloriginID}, ")


                } else if (destinationdataID == "0") {
                    destinationdataID = ""
                    finaldestinationId = destinationdataID
                    finaloriginID = origindataID

                    PreferenceUtils.setPreference("selectedCityIdDestination", destinationdataID)
                    PreferenceUtils.setPreference("selectedCityIdOrigin", origindataID)
                    Timber.d("searchlist6", "${finaloriginID}, ")


                } else if (destinationdataID == "0" && origindataID == "0") {
                    origindataID = ""
                    destinationdataID = ""
                    finaloriginID = origindataID
                    finaldestinationId = destinationdataID


                    PreferenceUtils.setPreference("selectedCityIdDestination", destinationdataID)
                    PreferenceUtils.setPreference("selectedCityIdOrigin", origindataID)
                    Timber.d("searchlist5", "${finaloriginID}, ")


                } else if (origindataID.isNotEmpty()) {
                    finaldestinationId = destinationdataID
                    finaloriginID = origindataID

                    PreferenceUtils.setPreference("selectedCityIdDestination", destinationdataID)
                    PreferenceUtils.setPreference("selectedCityIdOrigin", origindataID)
                    Timber.d("searchlist4", "${finaloriginID}, ")

                } else if(destinationdataID.isNotEmpty()){
                    finaldestinationId = destinationdataID
                    finaloriginID = origindataID

                    PreferenceUtils.setPreference("selectedCityIdDestination", destinationdataID)
                    PreferenceUtils.setPreference("selectedCityIdOrigin", origindataID)
                }
                Timber.d("searchlist3", "${finaloriginID}, ")

                if (travelSelection == "OriginCity") {
                    //binding.childReservationBottomSheet.selectionFromCity.text = originData
                    dialogBinding!!.selectionFromCity.text = originData
                    originChangeCheck = temOrigin != finaloriginID
                } else if (travelSelection == "DestinationCity") {
                    destinationChangeCheck = temDestination != finaldestinationId
                    //binding.childReservationBottomSheet.selectionToCity.text = destinationData
                    dialogBinding!!.selectionToCity.text = destinationData

                }


            }

        }

    private fun validateButton(
        originChange: Boolean,
        destinationChange: Boolean,
    ) {
        Timber.d("valiidate: $originChange, $destinationChange, $activeCheck, $hubChangeCheck")
        if (originChange || destinationChange) {
            dialogBinding!!.btnApply.isEnabled = true
            btnValidate = true
            dialogBinding!!.btnApply.backgroundTintList = ColorStateList.valueOf(
                requireContext().resources.getColor(
                    R.color.colorPrimary
                )
            )
        } else {
            btnValidate = false
            dialogBinding!!.btnApply.isEnabled = false
            dialogBinding!!.btnApply.backgroundTintList = ColorStateList.valueOf(
                requireContext().resources.getColor(
                    R.color.button_default_color
                )
            )
        }

    }


    private fun allotedObserver() {
        pickUpChartViewModel.dataAllotedServiceDirect.observe(requireActivity()) { it ->
            requireActivity().isActivityIsLive {
                if (it != null) {
                    stopShimmerEffect()
                    when (it.code) {
                        200 -> {
                            pendingInspectionList.clear()
                            completedInspectionList.clear()
                            availableRoutesList = it.services?.toMutableList() ?: mutableListOf()

                            if (availableRoutesList.size > 0) {
                                binding!!.noData.root.gone()
                                binding!!.root.visible()
                            } else {
                                binding!!.noData.root.visible()
                                binding!!.root.gone()
                            }


                            for (i in 0 until availableRoutesList.size) {
                                if (availableRoutesList[i].inspectionStatus!!) {
                                    completedInspectionList.add(availableRoutesList[i])
                                } else {
                                    pendingInspectionList.add(availableRoutesList[i])
                                }

                            }
                            if (pendingInspectionList.size > 0) {
                                setPendingAdapter()
                                binding!!.pendingInspectionTV.text =
                                    getString(R.string.pending_inspections) + "(" + pendingInspectionList.size.toString() + ")"
                                binding!!.pendingInspectionTV.visible()
                            } else {
                                binding!!.pendingInspectionTV.gone()
                            }
                            if (completedInspectionList.size > 0) {
                                setCompletedAdapter()
                                binding!!.completedInspectionTV.text =
                                    getString(R.string.completed_inspections) + "(" + completedInspectionList.size.toString() + ")"
                                binding!!.completedInspectionTV.visible()
                            } else {
                                binding!!.completedInspectionTV.gone()

                            }

                            PreferenceUtils.putObject(it, PREF_AVAILABLE_ROUTES_RESPONSE)
                            tempList.addAll(availableRoutesList)


                        }

                        401 -> {
                            /*DialogUtils.unAuthorizedDialog(
                                requireContext(),
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            (activity as BaseActivity).showUnauthorisedDialog()

                        }

                        else -> {
                            binding!!.noData.root.visible()
                            binding!!.noData.tvNoData.text = it.result?.message ?: ""
                            binding!!.roolLL.gone()

                        }

                    }
                } else {
                    requireActivity().toast(getString(R.string.server_error))
                }
            }
        }
    }

    private fun allotedDirectService() {
        Timber.d("service called")
        startShimmerEffect()
        pickUpChartViewModel.allotedServiceApiDirect(
            AllotedDirectRequest(
                false, null, loginModelPref.api_key, travel_date = dateSelected,
                null, null, "", false, finaloriginID, finaldestinationId, locale, true
            ,null),
            alloted_Service_method_name
        )
    }

    @SuppressLint("SetTextI18n")
    private fun openDateDialog() {
        val listener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth
                ->
                if (monthOfYear == 9 || monthOfYear == 10 || monthOfYear == 11) {
                    if (dayOfMonth <= 9) {
                        dialogBinding!!.dateET.setText("$dayOfMonth/${monthOfYear + 1}/$year")
                        displayDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                        dateSelected = "$year-${monthOfYear + 1}-0${dayOfMonth}"

                    } else {
                        dialogBinding!!.dateET.setText("$dayOfMonth/${monthOfYear + 1}/$year")
                        displayDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                        dateSelected = "$year-${monthOfYear + 1}-${dayOfMonth}"
                    }

                } else {
                    if (dayOfMonth <= 9) {
                        dialogBinding!!.dateET.setText("$dayOfMonth/0${monthOfYear + 1}/$year")
                        dateSelected = "$year-0${monthOfYear + 1}-0${dayOfMonth}"
                    } else {
                        dialogBinding!!.dateET.setText("$dayOfMonth/0${monthOfYear + 1}/$year")
                        dateSelected = "$year-0${monthOfYear + 1}-${dayOfMonth}"
                    }

                }
                btnValidate = true
                dialogBinding!!.btnApply.backgroundTintList = ColorStateList.valueOf(
                    requireContext().resources.getColor(
                        R.color.colorPrimary
                    )
                )
                Timber.d("monthOfYwear  : ${dateSelected}")
                PreferenceUtils.putString("shiftPassenger_selectedDate", dateSelected)
            }
        setDateLocale(locale!!, requireContext())
        val dpDialog = DatePickerDialog(requireContext(), listener, year, month, day)
        dpDialog.show()
    }


    private fun setPendingAdapter() {
        pendingAdapter =
            CheckingInspectorAdapter(requireContext(), this, pendingInspectionList, true)
        binding!!.pendingRV.adapter = pendingAdapter
    }

    override fun onResume() {
        super.onResume()
//        (requireActivity() as DashboardNavigateActivity).isCheckingInspectorDetailFrag(false)


    }

    private fun setCompletedAdapter() {
        completedAdapter = CheckingInspectorAdapter(
            requireActivity(),
            this,
            completedInspectionList, false
        )
        binding!!.completedRV.adapter = completedAdapter
    }


    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onItemClick(bool: Boolean, view: View, position: Int) {
        when (view.id) {
            R.id.inspectTV -> {
                if (bool) {
                    val data = pendingInspectionList[position]
                    val gson = Gson().toJson(data)
                    Timber.d("json", gson)
                    val bundle = Bundle()
                    bundle.putString("data", gson)
                    bundle.putBoolean("is_completed", false)
                    requireActivity().findNavController(R.id.nav_host_fragment)
                        .navigate(R.id.chk_inspector_detail_fragment, bundle)
                } else {
                    val data = completedInspectionList[position]
                    val gson = Gson().toJson(data)
                    Timber.d("json", gson)
                    val bundle = Bundle()
                    bundle.putString("data", gson)
                    bundle.putBoolean("is_completed", true)
                    requireActivity().findNavController(R.id.nav_host_fragment)
                        .navigate(R.id.chk_inspector_detail_fragment, bundle)
                }

            }
        }

    }


}