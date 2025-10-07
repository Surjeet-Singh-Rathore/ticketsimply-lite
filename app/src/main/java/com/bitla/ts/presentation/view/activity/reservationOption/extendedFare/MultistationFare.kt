package com.bitla.ts.presentation.view.activity.reservationOption.extendedFare

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.LayoutActivityMultistationFareBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_details.request.ServiceDetailsRequest
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.request.ReqBody
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultistationFareDetails
import com.bitla.ts.presentation.adapter.MultistationFareListAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.fragments.BottomModalSheetFragment
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible

class MultistationFare : BaseActivity(), OnItemClickListener, DialogSingleButtonListener {

    private lateinit var binding: LayoutActivityMultistationFareBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var multistationFairAdapter: MultistationFareListAdapter
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var multistationFareDetails = mutableListOf<MultistationFareDetails>()
    private var routeId: String? = null
    private var serviceNumber: String? = null
    private var reservationId: String? = null
    private var travelDate: String? = null
    private var sourceId: String? = null
    private var destinationId: String? = null

    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var currency: String = ""
    private var currencyFormat: String = ""
    private var locale: String? = ""
    
    override fun isInternetOnCallApisAndInitUI() {
    }

    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = LayoutActivityMultistationFareBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        binding.multistationFareToolbar.imageOptionLayout.visible()
        locale = PreferenceUtils.getlang()

        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel
            currency = privilegeResponseModel.currency
            currencyFormat = getCurrencyFormat(this, privilegeResponseModel.currencyFormat)
        }

        binding.multistationProgressbar.visible()
        binding.includeProgress.progressBar.visible()
        reservationId = PreferenceUtils.getString("reservationid")
        travelDate = PreferenceUtils.getString("ViewReservation_date").toString()
        sourceId = PreferenceUtils.getString("ViewReservation_OriginId").toString()
        destinationId = PreferenceUtils.getString("ViewReservation_DestinationId").toString()

        serviceNumber = intent.getStringExtra("serviceNumber")
        binding.multistationFareToolbar.textHeaderTitle.text = "Multistation Fares "
        binding.multistationFareToolbar.headerTitleDesc.text = "Service $serviceNumber"
        binding.multistationFareToolbar.imageHeaderRightImage.setOnClickListener {
            onBackPressed()
        }

        callServiceApi()
        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                   showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                   showToast(it)
                }
            }
        }
    }

    private fun multiStationWiseFareObserver() {
        pickUpChartViewModel.fetchMultiStatioWiseFareResponse.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        binding.multistationProgressbar.gone()
                        binding.includeProgress.progressBar.gone()

                        BottomModalSheetFragment()
                        multistationFareDetails = it.multistation_fare_details

                        layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        binding.rvMultistaionFares.layoutManager = layoutManager
                        multistationFairAdapter =
                            MultistationFareListAdapter(
                                this,
                                multistationFareDetails,
                                routeId.toString(),
                                reservationId,
                                currency,
                                currencyFormat
                            )
                        binding.rvMultistaionFares.adapter = multistationFairAdapter
                        binding.multistationSearch.gone()
                    }
                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }
                    else -> {
                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> toast(it1) }
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun callMultiStationWiseFairApi() {
        if (this.isNetworkAvailable()) {

            val bccId = PreferenceUtils.getBccId()
            val loginModelPref = PreferenceUtils.getLogin()

            /*pickUpChartViewModel.fetchMultiStatioWiseFareApi(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                multiStationWiseFareRequest,
                manage_fare_method_name
            )*/

            pickUpChartViewModel.fetchMultiStatioWiseFareApi(
                ReqBody(
                    apiKey = loginModelPref.api_key,
                    reservation_id = reservationId.toString(),
                    date = travelDate.toString(),
                    channelId = "",
                    templateId = "",
                    locale = locale
                ),
                manage_fare_method_name
            )


            multiStationWiseFareObserver()
        } else this.noNetworkToast()
    }

    private fun callServiceApi() {
        if (this.isNetworkAvailable()) {

            val bccId = PreferenceUtils.getBccId().toString()
            val loginModelPref = PreferenceUtils.getLogin()

            val serviceDetailsRequest = ServiceDetailsRequest(
                bccId, service_details_method, format_type,
                com.bitla.ts.domain.pojo.service_details.request.ReqBody(
                    reservationId.toString(),
                    loginModelPref.api_key,
                    operator_api_key,
                    locale,
                    sourceId.toString(),
                    destinationId.toString(),
                    response_format
                )
            )
            /*sharedViewModel.getServiceDetails(
                loginModelPref.auth_token,
                loginModelPref.api_key, serviceDetailsRequest, service_details_method
            )*/

            sharedViewModel.getServiceDetails(
                reservationId.toString(),
                loginModelPref.api_key,
                sourceId.toString(),destinationId.toString(), operator_api_key,locale!!, service_details_method,excludePassengerDetails = false
            )
            serviceDetailsApiObserver()
        } else this.noNetworkToast()
    }

    private fun serviceDetailsApiObserver() {
        sharedViewModel.serviceDetails.observe(this) {

            if (it.code == 200) {
                routeId = it.body.routeId.toString()
                callMultiStationWiseFairApi()
            } else
                it.message?.let { it1 ->
                    Toast.makeText(
                        this,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }


}