package com.bitla.ts.presentation.view.dashboard.update_rate_card_fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.manage_fare_method_name
import com.bitla.ts.data.operator_api_key
import com.bitla.ts.data.response_format
import com.bitla.ts.data.service_details_method
import com.bitla.ts.databinding.DialogModifyFareCityFilterBinding
import com.bitla.ts.databinding.DialogProgressBarBinding
import com.bitla.ts.databinding.FragmentMultistationBinding
import com.bitla.ts.domain.pojo.destination_pair.SearchModel
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_details.request.ServiceDetailsRequest
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.request.ReqBody
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultistationFareDetails
import com.bitla.ts.presentation.adapter.MultistationFareListAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.view.activity.SearchOriginDestinationActivity
import com.bitla.ts.presentation.view.fragments.BottomModalSheetFragment
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.presentation.viewModel.SharedViewModel
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

class MultistationFragment : Fragment(), DialogSingleButtonListener {

    private var fromCityName: String = ""
    private var destCityName: String = ""
    private lateinit var filterDialogBinding: DialogModifyFareCityFilterBinding
    private var filterDialog: AlertDialog? = null
    lateinit var binding: FragmentMultistationBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var multistationFairAdapter: MultistationFareListAdapter
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var multistationFareDetails = mutableListOf<MultistationFareDetails>()
    private var filteredMultistationList = mutableListOf<MultistationFareDetails>()
    private var resID: String? = null
    private var routeId: String? = null
    private var sourceId: String? = null
    private var source: String = ""
    private var destinationId: String? = null
    private var destination: String = ""
    private var loginModelPref: LoginModel = LoginModel()
    private var bccId: Int? = 0
    private var travelDate: String? = null

    private var currency: String = ""
    private var currencyFormat: String = ""
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var locale: String? = ""
    private var originList: ArrayList<SearchModel> = arrayListOf()
    private var destList: ArrayList<SearchModel> = arrayListOf()


    companion object {
        var instance: MultistationFragment? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMultistationBinding.inflate(inflater, container, false)
        getPref()
        multiStationWiseFareObserver()
        showProgressDialog(requireContext())
        callServiceApi()
        setClickListeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.multistationSearch.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()

        resID = PreferenceUtils.getString(getString(R.string.updateRateCard_resId))
        source = PreferenceUtils.getString(getString(R.string.updateRateCard_origin)).toString()
        destination =
            PreferenceUtils.getString(getString(R.string.updateRateCard_destination)).toString()
        sourceId = PreferenceUtils.getString(getString(R.string.updateRateCard_originId)).toString()
        destinationId =
            PreferenceUtils.getString(getString(R.string.updateRateCard_destinationId)).toString()
        travelDate =
            PreferenceUtils.getString(getString(R.string.updateRateCard_travelDate)).toString()

        if ((activity as BaseActivity).getPrivilegeBase() != null) {
            privilegeResponseModel = (activity as BaseActivity).getPrivilegeBase() as PrivilegeResponseModel
            currency = privilegeResponseModel.currency
            currencyFormat =
                getCurrencyFormat(requireContext(), privilegeResponseModel.currencyFormat)
        }
    }

    private fun multiStationWiseFareObserver() {
        pickUpChartViewModel.fetchMultiStatioWiseFareResponse.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            if (it != null) {
                if (it.code != null) {
                    when (it.code) {
                        200 -> {
                            BottomModalSheetFragment()
                            multistationFareDetails = it.multistation_fare_details
                            binding.multistationProgressbar.gone()
                            layoutManager =
                                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                            multistationFareDetails = it.multistation_fare_details

                            setMultistationAdapter(multistationFareDetails)
                            val uniqueOriginNames = linkedSetOf<String>()
                            val uniqueDestNames = linkedSetOf<String>()
                            for (i in 0 until multistationFareDetails.size) {
                                val name = multistationFareDetails[i].origin_name
                                if (uniqueOriginNames.add(name)) {
                                    val obj = SearchModel()
                                    obj.name = name
                                    obj.id = multistationFareDetails[i].origin_id
                                    originList.add(obj)
                                }

                                val name1 = multistationFareDetails[i].destination_name

                                if (uniqueDestNames.add(name1)) {
                                    val obj2 = SearchModel()
                                    obj2.name = name1
                                    obj2.id = multistationFareDetails[i].destination_id
                                    destList.add(obj2)
                                }


                            }

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
                            requireContext().toast(requireActivity().getString(R.string.opps))
                        }
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error_please_try_again))
                }

            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun setMultistationAdapter(list: MutableList<MultistationFareDetails>) {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvMultistaionFares.layoutManager = layoutManager
        multistationFairAdapter =
            MultistationFareListAdapter(
                context = requireActivity(),
                searchList = list,
                routeId = routeId.toString(),
                reservationId = resID,
                currency = currency,
                currencyFormat = currencyFormat
            )
        binding.rvMultistaionFares.adapter = multistationFairAdapter
    }

    fun callMultiStationWiseFairApi() {
        if (requireContext().isNetworkAvailable()) {
            val loginModelPref = PreferenceUtils.getLogin()
            pickUpChartViewModel.fetchMultiStatioWiseFareApi(
                ReqBody(
                    apiKey = loginModelPref.api_key,
                    reservation_id = resID.toString(),
                    date = travelDate.toString(),
                    channelId = "",
                    templateId = "",
                    locale = locale
                ),
                manage_fare_method_name
            )

        } else requireContext().noNetworkToast()
    }

    private fun showFilterDialog() {
        filterDialog = AlertDialog.Builder(requireContext()).create()
        filterDialogBinding =
            DialogModifyFareCityFilterBinding.inflate(LayoutInflater.from(requireContext()))
        filterDialog?.setView(filterDialogBinding.root)

        if (fromCityName.isNotEmpty()) {
            filterDialogBinding.etSource.setText(fromCityName)
        }
        if (destCityName.isNotEmpty()) {
            filterDialogBinding.etDestination.setText(destCityName)
        }

        filterDialogBinding.etSource.setOnClickListener {
            PreferenceUtils.putOriginDestList(originList)
            val intent = Intent(requireActivity(), SearchOriginDestinationActivity::class.java)
            intent.putExtra("type", "source")
            startActivityForResult(intent, 1001)


        }
        filterDialogBinding.etDestination.setOnClickListener {
            PreferenceUtils.putOriginDestList(destList)
            val intent = Intent(requireActivity(), SearchOriginDestinationActivity::class.java)
            intent.putExtra("type", "destination")
            startActivityForResult(intent, 1002)


        }
        filterDialogBinding.btnProcceed.setOnClickListener {

            if (fromCityName.isNotEmpty() && destCityName.isNotEmpty()) {
                filteredMultistationList.clear()
                for (i in 0 until multistationFareDetails.size) {
                    if (multistationFareDetails[i].origin_name.contains(
                            fromCityName,
                            true
                        ) && multistationFareDetails[i].destination_name.contains(
                            destCityName,
                            true
                        )
                    ) {
                        filteredMultistationList.add(multistationFareDetails[i])
                    }
                }
                setMultistationAdapter(filteredMultistationList)
                filterDialog?.dismiss()
                requireActivity().toast(getString(R.string.filter_applied))

            } else if (fromCityName.isNotEmpty() && destCityName.isEmpty()) {
                filteredMultistationList.clear()
                for (i in 0 until multistationFareDetails.size) {
                    if (multistationFareDetails[i].origin_name.contains(fromCityName, true)) {
                        filteredMultistationList.add(multistationFareDetails[i])
                    }
                }
                setMultistationAdapter(filteredMultistationList)
                filterDialog?.dismiss()
                requireActivity().toast(getString(R.string.filter_applied))


            } else if (fromCityName.isEmpty() && destCityName.isNotEmpty()) {
                filteredMultistationList.clear()
                for (i in 0 until multistationFareDetails.size) {
                    if (multistationFareDetails[i].destination_name.contains(destCityName, true)) {
                        filteredMultistationList.add(multistationFareDetails[i])
                    }
                }
                setMultistationAdapter(filteredMultistationList)
                filterDialog?.dismiss()
                requireActivity().toast(getString(R.string.filter_applied))


            } else {
                setMultistationAdapter(multistationFareDetails)
                filterDialog?.dismiss()
                requireActivity().toast(getString(R.string.filter_applied))
            }
        }
        filterDialogBinding.clearTV.setOnClickListener {
            filterDialogBinding.etSource.setText("")
            filterDialogBinding.etDestination.setText("")
            fromCityName = ""
            destCityName = ""

        }

        filterDialogBinding.tvCancel.setOnClickListener {
            filterDialog?.dismiss()
        }

        filterDialog?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val type = data?.getStringExtra(getString(R.string.SELECTED_SEARCHED_TYPE))
            if (type == "source") {
                val fromCityId =
                    data.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                fromCityName =
                    data.getStringExtra(getString(R.string.SELECTED_CITY_NAME)).toString()
                filterDialogBinding.etSource.setText(fromCityName)

            } else {
                val destCityId =
                    data!!.getStringExtra(getString(R.string.SELECTED_CITY_ID)).toString()
                destCityName =
                    data.getStringExtra(getString(R.string.SELECTED_CITY_NAME)).toString()
                filterDialogBinding.etDestination.setText(destCityName)
            }
        }
    }

    private fun callServiceApi() {
        if (requireContext().isNetworkAvailable()) {
            
            sharedViewModel.getServiceDetails(
                reservationId = resID.toString(),
                apiKey = loginModelPref.api_key,
                originId = sourceId.toString(),
                destinationId = destinationId.toString(),
                operatorApiKey = operator_api_key,
                locale = locale!!,
                apiType = service_details_method,
                excludePassengerDetails = false
            )
            serviceDetailsApiObserver()
        } else requireContext().noNetworkToast()
    }

    private fun serviceDetailsApiObserver() {
        sharedViewModel.serviceDetails.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        routeId = it.body.routeId.toString()
                        PreferenceUtils.putString(getString(R.string.routeId), routeId)
                        callMultiStationWiseFairApi()

                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> it.message?.let { it1 ->
                        Toast.makeText(
                            requireContext(),
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else
                requireContext().toast(getString(R.string.server_error))
        }
    }

    fun showProgressDialog(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.Style_Dialog_Rounded_littl_Corner)
        val dialogBinding = DialogProgressBarBinding.inflate(LayoutInflater.from(context))
        builder.setView(dialogBinding.root)
        DialogUtils.progressDialog = builder.create()
        DialogUtils.progressDialog!!.setCancelable(false)
        DialogUtils.progressDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        DialogUtils.progressDialog!!.show()
    }

    fun dismissProgressDialog() {
        if (DialogUtils.progressDialog != null && DialogUtils.progressDialog!!.isShowing) {
            DialogUtils.progressDialog!!.dismiss()
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
}