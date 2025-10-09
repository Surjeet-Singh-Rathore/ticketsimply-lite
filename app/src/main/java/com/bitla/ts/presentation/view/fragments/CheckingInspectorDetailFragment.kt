package com.bitla.ts.presentation.view.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.API_CHECKING_INSPECTOR
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.view_reservation_method_name
import com.bitla.ts.databinding.FragmentCheckingInspectorDetailBinding
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.view_reservation.CheckingInspectorRequestBody
import com.bitla.ts.domain.pojo.view_reservation.PassengerDetail
import com.bitla.ts.domain.pojo.view_reservation.UpdatePassengerData
import com.bitla.ts.presentation.adapter.CheckingInspectorDetailAdapter
import com.bitla.ts.presentation.view.dashboard.DashboardNavigateActivity
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.edgeToEdgeFromOnlyBottom
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible


class CheckingInspectorDetailFragment : Fragment(), OnItemClickListener,
    DialogSingleButtonListener {

    private lateinit var binding: FragmentCheckingInspectorDetailBinding
    private var adapter: CheckingInspectorDetailAdapter? = null
    private var busData: Service? = null
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var loginModelPref = LoginModel()
    private var locale: String? = ""
    private var isCompleted: Boolean? = false
    private var passengerList: ArrayList<PassengerDetail> = arrayListOf()
    private var updatedPassengerList: ArrayList<UpdatePassengerData> = arrayListOf()
    private var reqBody : CheckingInspectorRequestBody?= CheckingInspectorRequestBody()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            val data = it!!.getString("data")
            isCompleted = it.getBoolean("is_completed")
            busData = Gson().fromJson<Service>(data.toString(), Service::class.java)
        }


    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as DashboardNavigateActivity).isCheckingInspectorDetailFrag(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding =
            FragmentCheckingInspectorDetailBinding.inflate(inflater, container, false)
        initUi()
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        return binding.root
    }


    private fun startShimmerEffect() {
        binding.apply {
            shimmerDashboardDetails.visible()
            rootLL.gone()
            shimmerDashboardDetails.startShimmer()
            noData.root.gone()
        }
    }

    private fun stopShimmerEffect() {
        binding.apply {
            rootLL.visible()
            shimmerDashboardDetails.startShimmer()
            noData.root.gone()
            shimmerDashboardDetails.gone()
            if (shimmerDashboardDetails.isShimmerStarted) {
                shimmerDashboardDetails.stopShimmer()
            }
        }
    }


    private fun initUi() {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            // Android 15+ (API 34)
////            edgeToEdgeFromOnlyBottom(binding.root)
//        }

        (requireActivity() as DashboardNavigateActivity).showHideBottomBar(false)
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        binding.includeToolbar.toolbarHeaderText.text = busData!!.origin + " - " +busData!!.destination
        binding.includeToolbar.toolbarSubtitle.text = busData!!.travelDate
        binding.includeToolbar.imgBack.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment).popBackStack()
        }
        binding.goBackBT.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment).popBackStack()
        }

        if(isCompleted!!){
            binding.extraCabinTIL.gone()
            binding.remarksTIL.gone()
            binding.goBackBT.gone()
            binding.completeInspectionBT.gone()
        }



        if (requireActivity().isNetworkAvailable()) {
            setPickupChartObserver()
            setCheckingInspectorObserver()
            startShimmerEffect()
            pickUpChartApi()
        } else {
            requireActivity().noNetworkToast()
        }
        setAdapter()
        setClickListerner()
    }

    private fun setCheckingInspectorObserver() {
        pickUpChartViewModel.checkingInspectorData.observe(requireActivity()){
            if(it.code == "200"){
                val bundle = Bundle()
                bundle.putBoolean("refresh_layout",true)
                requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.chk_inspector_fragment,bundle)
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            }else if(it.code == "401"){
                Toast.makeText(requireContext(), it.result.message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setClickListerner() {
        binding.completeInspectionBT.setOnClickListener {
            var maleCount = 0
            var femaleCount = 0
            var totalBoarded = 0
            var totalPassengers =0
            val array = JsonArray()
            array.add("pnr_no")
            array.add("seat_no")
            array.add("boarded_status")
            array.add("gender")

            var jsonArray= JsonArray()
            jsonArray.add(array)

            for (i in 0 until updatedPassengerList.size){
                if(updatedPassengerList[i].gender == "M"){
                    maleCount++
                }
                if(updatedPassengerList[i].gender == "F"){
                    femaleCount++
                }
                if(updatedPassengerList[i].boardedStatus == "YES"){
                    totalBoarded++
                }
                totalPassengers = maleCount+femaleCount
                val array1 = JsonArray()
                array1.add(updatedPassengerList[i].pnrNo)
                array1.add(updatedPassengerList[i].seatNo)
                array1.add(updatedPassengerList[i].boardedStatus)
                array1.add(updatedPassengerList[i].gender)
                jsonArray.add(array1)
            }
            val summaryObj = JsonObject()
            summaryObj.addProperty("boarded",totalBoarded)
            summaryObj.addProperty("Male",maleCount)
            summaryObj.addProperty("Female",femaleCount)


            reqBody!!.boarded_details = jsonArray
            reqBody!!.inspection_summary = summaryObj
            reqBody!!.extra_cabins =  binding.extraCabinET.text.toString().trim()
            reqBody!!.remarks =  binding.remarksET.text.toString().trim()


            DialogUtils.dialogCheckingInspectorUpdate(requireContext(),this,
            maleCount,femaleCount,totalPassengers)


        }
    }

    private fun hitCheckingInspectorApi() {
        pickUpChartViewModel.checkingInspectorApi(
            busData!!.reservationId.toString(),
            loginModelPref.api_key,
            locale!!,
            reqBody!!,
            API_CHECKING_INSPECTOR
        )
    }

    private fun setPickupChartObserver() {
        pickUpChartViewModel.viewReservationResponse.observe(requireActivity()) { it ->
            // binding.includeProgress.progressBar.gone()
            if (it != null) {
                Timber.d("viewReservationResponse $it")
                if (it.code == 200) {
                    stopShimmerEffect()
                    when {
                        it.passengerDetails != null -> {
                            it.passengerDetails.forEach {
                                passengerList.add(it)
                                val data = UpdatePassengerData()
                                data.seatNo = it.seatNumber
                                data.gender = it.sex
                                data.pnrNo = it.pnrNumber
                                updatedPassengerList.add(data)
                            }
                            setAdapter()

                        }

                        else -> {
                            binding.rootLL.gone()
                            binding.noData.root.visible()
                            it.result?.message?.let { it1 -> requireActivity().toast(it1) }
                        }
                    }
                } else if (it.code == 401) {
                    /*DialogUtils.unAuthorizedDialog(
                        requireActivity(),
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    (activity as BaseActivity).showUnauthorisedDialog()

                } else {
                    requireActivity().toast(it.message)
                }
            } else {
                requireActivity().toast(getString(R.string.server_error))
            }
        }
    }


    private fun pickUpChartApi() {



        pickUpChartViewModel.viewReservationCheckingInspectorAPI(
            loginModelPref.api_key,
            busData!!.reservationId.toString(),
            busData!!.originId!!,
            "3",
            locale!!,
            view_reservation_method_name
        )


    }

    private fun setAdapter() {
        adapter = CheckingInspectorDetailAdapter(requireContext(), this, passengerList,isCompleted)
        binding.chkInspectorRV.adapter = adapter
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        when(view.id) {
            R.id.passengerDetails_btnMale -> {
                passengerList[position].sex = "M"
                updatedPassengerList[position].gender = "M"
               // adapter!!.notifyDataSetChanged()

            }
            R.id.passengerDetails_btnFemale -> {
                passengerList[position].sex = "F"
                updatedPassengerList[position].gender = "F"
               // adapter!!.notifyDataSetChanged()


            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {
        passengerList[position].boardedStatus = data == "YES"
        updatedPassengerList[position].boardedStatus = data
       // adapter!!.notifyDataSetChanged()


    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
        TODO("Not yet implemented")
    }

    override fun onSingleButtonClick(str: String) {
        if(str == "check_inspector") {
            hitCheckingInspectorApi()
        }
    }


}