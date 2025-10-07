package com.bitla.ts.presentation.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.databinding.FragmentMultipleServicesManageFareBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.multiple_services_manage_fare.request.*
import com.bitla.ts.domain.pojo.seat_types.SeatTypes
import com.bitla.ts.presentation.adapter.SeatTypesAdapter
import com.bitla.ts.presentation.view.activity.MultipleServicesManageFareActivity
import com.bitla.ts.presentation.viewModel.MultipleServicesManageFareViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import isNetworkAvailable
import noNetworkToast
import onChange
import org.koin.androidx.viewmodel.ext.android.viewModel
import setMaxValueWithDecimal
import toast
import visible
import java.util.Calendar

class MultipleServicesManageFareFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentMultipleServicesManageFareBinding

    private val viewModel by viewModel<MultipleServicesManageFareViewModel>()

    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String = ""

    private var isAllServicesSelected = false
    private var selectedServicesCount = ""
    private var selectedServices = ""
    private var srcDest = ""
    private var travelDate = ""
    private var originId = ""
    private var destinationId = ""

    private lateinit var mCalendar: Calendar
    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private var fromDate: String = ""
    private var toDate: String = ""

    private var isAllChannelsSelected: Boolean = false
    private var isBranchSelected: Boolean = false
    private var isOnlineAgentSelected: Boolean = false
    private var isApiSelected: Boolean = false
    private var isETicketSelected: Boolean = false
    private lateinit var channelType: MultipleServicesChannelType

    private var amountType: Int = 2
    private var incOrDec: Int = 1
    private var currencySymbol: String = ""

    private var seatTypesList: MutableList<SeatTypes> = mutableListOf()
    private lateinit var seatTypesAdapter: SeatTypesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
            binding = FragmentMultipleServicesManageFareBinding.inflate(inflater, container, false)
            initUI()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val dateDMY = getDateDMY(travelDate)
        val headerText = "$srcDest ($dateDMY)"
        (activity as MultipleServicesManageFareActivity).setToolbarText(headerText)
    }

    private fun initUI() {
        isAllServicesSelected = arguments?.getBoolean("isAllServicesSelected") == true
        selectedServicesCount = arguments?.getString("selectedServicesCount").toString()
        selectedServices = arguments?.getString("selectedServices").toString()
        srcDest = arguments?.getString("sourceDestination").toString()
        travelDate = arguments?.getString("travelDate").toString()
        originId = arguments?.getString("originId").toString()
        destinationId = arguments?.getString("destinationId").toString()

        fromDate = travelDate
        toDate = travelDate

        if (isAllServicesSelected) {
            selectedServices = "-1"
        } else {
            if (selectedServices.isNotEmpty()) {
                selectedServices = selectedServices.substring(0, selectedServices.length.minus(1))
            }
        }

        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        val privilegeResponseModel = (activity as BaseActivity).getPrivilegeBase()
        if (privilegeResponseModel != null) {
            if (privilegeResponseModel.currency.isNotEmpty()) {
                currencySymbol = privilegeResponseModel.currency
            }
        }

        mCalendar = Calendar.getInstance()
        day = mCalendar.get(Calendar.DAY_OF_MONTH)
        month = mCalendar.get(Calendar.MONTH)
        year = mCalendar.get(Calendar.YEAR)

        channelType = MultipleServicesChannelType(
            branch = true,
            ota = true,
            ebooking = true,
            online = true
        )

        clickListener()

        callSeatTypesApi()
        seatTypesObserver()
        manageFareObserver()
    }

    private fun clickListener() {
        binding.cbAllSeatTypeAmount.setOnClickListener(this)
        binding.btnSaveModifiedFare.setOnClickListener(this)

        binding.etAllAmount.onChange {
            binding.cbAllSeatTypeAmount.isChecked = false
            setAmountLength()
        }

        binding.rgChannelTypes.setOnCheckedChangeListener { group, _ ->
            val selectedId = group.checkedRadioButtonId
            if (selectedId != -1) {
                val radio = group.findViewById<RadioButton>(selectedId)
                handleChannelTypeSelection(radio.text.toString())
                requireActivity().toast(radio.text.toString())
            }
        }

        setupChannelSelectionListeners()

        setupAmountTypeListeners()



        binding.rgAmountType.setOnCheckedChangeListener { group, checkedId ->
            amountType = if (checkedId == R.id.rbPercentage) {
                1
            } else {
                2
            }

            binding.root.clearFocus()
            binding.etAllAmount.setText("")
            binding.cbAllSeatTypeAmount.isChecked = false
            enableDisableSaveBtn(false)

            seatTypesList.forEach { it.amount = "" }
            seatTypesAdapter.updateAmountType(amountType)
        }

        binding.rgIncDec.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbIncrease -> {
                    incOrDec = 0
                    binding.amountTypeCL.visible()
                }

                R.id.rbDecrease -> {
                    incOrDec = 1
                    binding.amountTypeCL.visible()
                }

                R.id.rbSetAmount -> {
                    incOrDec = 2
                    binding.amountTypeCL.gone()
                }
            }
        }
    }

    private fun setupAmountTypeListeners() {
        val radioButtons = listOf(
            binding.rbIncrease,
            binding.rbDecrease,
            binding.rbSetAmount
        )

        var listener: CompoundButton.OnCheckedChangeListener? = null


         listener = CompoundButton.OnCheckedChangeListener { selectedButton, isChecked ->
            if (isChecked) {
                when (selectedButton) {
                    binding.rbIncrease -> {
                        incOrDec = 0
                        binding.amountTypeCL.visible()
                    }
                    binding.rbDecrease -> {
                        incOrDec = 1
                        binding.amountTypeCL.visible()
                    }
                    binding.rbSetAmount -> {
                        incOrDec = 2
                        binding.amountTypeCL.gone()
                    }
                }

                // Uncheck all other radio buttons
                radioButtons.filterNot { it == selectedButton }.forEach {
                    it.setOnCheckedChangeListener(null)
                    it.isChecked = false
                    it.setOnCheckedChangeListener(listener)
                }
            }
        }

        // Attach the listener to all radio buttons
        radioButtons.forEach {
            it.setOnCheckedChangeListener(listener)
        }
    }

    private fun setupChannelSelectionListeners() {
        val radioButtons = listOf(
            binding.rbAllChannels,
            binding.rbBranch,
            binding.rbOnlineAgent,
            binding.rbApi,
            binding.rbETicket
        )

        // Declare listener as a variable so we can reference it later
        var listener: CompoundButton.OnCheckedChangeListener? = null

        listener = CompoundButton.OnCheckedChangeListener { selectedButton, isChecked ->
            if (isChecked) {
                val selectedRadio = selectedButton as RadioButton

                // Uncheck all others
                radioButtons.filterNot { it == selectedRadio }.forEach {
                    it.setOnCheckedChangeListener(null)
                    it.isChecked = false
                    it.setOnCheckedChangeListener(listener)
                }

                // Handle selection
                handleChannelTypeSelection(selectedRadio.text.toString())
                requireActivity().toast(selectedRadio.text.toString())
            }
        }

        // Attach listener to each radio button
        radioButtons.forEach {
            it.setOnCheckedChangeListener(listener)
        }
    }



    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cbAllSeatTypeAmount -> {
                if (binding.cbAllSeatTypeAmount.isChecked) {
                    seatTypesList.forEach { it.amount = binding.etAllAmount.text.toString() }
                } else {
                    seatTypesList.forEach { it.amount = "" }
                }
                enableDisableSaveBtn(binding.cbAllSeatTypeAmount.isChecked)
                seatTypesAdapter.notifyDataSetChanged()

                closeKeyBoard()
                binding.root.clearFocus()
            }

            R.id.btnSaveModifiedFare -> {
                callManageFareApi()
            }
        }
    }

    private fun setAmountLength() {
        if (amountType == 1) {
            binding.etAllAmount.setMaxValueWithDecimal(100.00, 2)
        } else {
            binding.etAllAmount.setMaxValueWithDecimal(999999.99, 2)
        }

        val amt = binding.etAllAmount.text.toString()
        binding.cbAllSeatTypeAmount.isEnabled =
            amt.isNotEmpty() && amt.toDoubleOrNull() != 0.0 && amt != "."
    }

    private fun handleChannelTypeSelection(selectedChannel: String) {
        isAllChannelsSelected = false
        isBranchSelected = false
        isOnlineAgentSelected = false
        isApiSelected = false
        isETicketSelected = false

        when (selectedChannel) {
            binding.rbAllChannels.text.toString() -> {
                isAllChannelsSelected = true
            }

            binding.rbBranch.text.toString() -> {
                isBranchSelected = true
            }

            binding.rbOnlineAgent.text.toString() -> {
                isOnlineAgentSelected = true
            }

            binding.rbApi.text.toString() -> {
                isApiSelected = true
            }

            binding.rbETicket.text.toString() -> {
                isETicketSelected = true
            }
        }

        channelType = if (isAllChannelsSelected) {
            MultipleServicesChannelType(
                branch = true,
                ota = true,
                ebooking = true,
                online = true
            )
        } else {
            MultipleServicesChannelType(
                branch = isBranchSelected,
                ota = isApiSelected,
                ebooking = isETicketSelected,
                online = isOnlineAgentSelected
            )
        }
    }

    private fun callSeatTypesApi() {
        if (requireContext().isNetworkAvailable()) {
            binding.progressBar.visible()

            viewModel.getSeatTypes(
                apiKey = loginModelPref.api_key,
                routeIds = selectedServices,
                originId = if (isAllServicesSelected) originId else null,
                destinationId = if (isAllServicesSelected) destinationId else null,
                travelDate = if (isAllServicesSelected) travelDate else null,
            )
        } else {
            requireContext().noNetworkToast()
        }
    }

    private fun seatTypesObserver() {
        viewModel.seatTypesData.observe(requireActivity()) {
            binding.progressBar.gone()

            if (it != null) {
                when (it.code) {
                    200 -> {
                        if (it.seatTypes.isNotEmpty()) {
                            seatTypesList.addAll(it.seatTypes)

                            setSeatTypesAdapter()
                        }
                    }

                    401 -> {
                        showUnauthorisedDialog()
                    }

                    else -> {
                        requireContext().toast(it.message)
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun setSeatTypesAdapter() {
        binding.rvSeatTypes.layoutManager = GridLayoutManager(requireContext(), 3)

        seatTypesAdapter =
            SeatTypesAdapter(requireContext(), seatTypesList, amountType) { position, amount ->
                onAmountChange(position, amount)
            }
        binding.rvSeatTypes.adapter = seatTypesAdapter
    }

    private fun onAmountChange(position: Int, amount: String) {
        seatTypesList[position].amount = amount

        var enableSaveBtn = false
        seatTypesList.forEach {
            if (it.amount.isNotEmpty() && it.amount != "." && it.amount.toDoubleOrNull() != 0.0) {
                enableSaveBtn = true
                return@forEach
            }
        }

        binding.cbAllSeatTypeAmount.isChecked = false
        enableDisableSaveBtn(enableSaveBtn)
    }

    private fun enableDisableSaveBtn(enable: Boolean = false) {
        if (enable) {
            binding.btnSaveModifiedFare.isEnabled = true
            binding.btnSaveModifiedFare.backgroundTintList =
                AppCompatResources.getColorStateList(requireContext(), R.color.colorPrimary)
        } else {
            binding.btnSaveModifiedFare.isEnabled = false
            binding.btnSaveModifiedFare.backgroundTintList =
                AppCompatResources.getColorStateList(requireContext(), R.color.colorShadow)
        }
    }

    private fun callManageFareApi() {
        if (requireContext().isNetworkAvailable()) {
            binding.progressBar.visible()

            val fareDetails = mutableListOf<FareDetails>()
            seatTypesList.forEach {
                if (it.amount.isNotEmpty()) {
                    val fareDetail = FareDetails(
                        it.id ?: 0,
                        it.seatType ?: "",
                        it.amount
                    )
                    fareDetails.add(fareDetail)
                }
            }

            val reqBody = ReqBody(
                loginModelPref.api_key,
                locale,
                true,
                channelType,
                fromDate,
                toDate,
                incOrDec,
                if (incOrDec == 2) null else amountType,
                selectedServices,
                fareDetails,
                if (isAllServicesSelected) originId else null,
                if (isAllServicesSelected) destinationId else null
            )

            viewModel.multipleServicesManageFares(reqBody)
        } else {
            requireContext().noNetworkToast()
        }
    }

    private fun manageFareObserver() {
        viewModel.multipleServicesManageFares.observe(requireActivity()) {
            binding.progressBar.gone()

            if (it != null) {
                when (it.code) {
                    200 -> {
                        requireContext().toast(it.message)

                        requireActivity().findNavController(R.id.nav_host_fragment_multiple_services_fare)
                            .navigate(R.id.actionManageFareToServiceList)
                    }

                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()
                    }

                    else -> {
                        requireContext().toast(it.message)
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    override fun isInternetOnCallApisAndInitUI() {}

    override fun isNetworkOff() {}

    override fun onButtonClick(view: Any, dialog: Dialog) {}
}