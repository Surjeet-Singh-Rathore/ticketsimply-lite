package com.bitla.ts.phase2.dashboardContainer.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ActivityRevenueServiceDetailsBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.revenue_data.AgentSummary
import com.bitla.ts.domain.pojo.revenue_data.AgentWiseRevenue
import com.bitla.ts.domain.pojo.revenue_data.BranchSummary
import com.bitla.ts.domain.pojo.revenue_data.ChannelSummary
import com.bitla.ts.domain.pojo.revenue_data.SeatSold
import com.bitla.ts.domain.pojo.revenue_data.ServiceSummary
import com.bitla.ts.domain.pojo.revenue_data.ServiceWiseRevenueData
import com.bitla.ts.phase2.adapter.parent.NewAgentWiseRevenueAdapter
import com.bitla.ts.phase2.adapter.parent.SeatsSoldAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.viewModel.DashboardRevenueViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible


class RevenueServiceDetailsActivity : BaseActivity(), DialogSingleButtonListener,
    OnItemClickListener {

    private var journeyBy: String? = ""
    private lateinit var binding: ActivityRevenueServiceDetailsBinding
    var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var date: String = ""
    private var agentWiseAdapter: NewAgentWiseRevenueAdapter? = null
    private var branchWiseAdapter: NewAgentWiseRevenueAdapter? = null
    private var otaWiseAdapter: NewAgentWiseRevenueAdapter? = null
    private var seatsSoldAdapter: SeatsSoldAdapter? = null
    private var routeID: String? = ""
    private var fromDate: String? = ""
    private var toDate: String? = ""
    private val dashboardRevenueViewModel by viewModel<DashboardRevenueViewModel<Any?>>()
    private var isFromService: Boolean? = false
    private var id: String? = ""
    private var privilegeResponse: PrivilegeResponseModel? = null


    override fun initUI() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRevenueServiceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDataFromIntent()
        getPref()
        lifecycleScope.launch {
            dashboardRevenueViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        if (isFromService == true) {
            getRevenueRouteDataObserver()
            getRevenueDetails()
        } else {
            getRevenueAgentHubData()
            getRevenueAgentHubObserver()
        }




        binding.summaryImg.setOnClickListener {
            if (binding.summaryInnerCL.visibility == View.VISIBLE) {
                binding.summaryInnerCL.gone()
                binding.summaryImg.animate().rotation(-90.0f).setDuration(500)

            } else {
                binding.summaryInnerCL.visible()
                binding.summaryImg.animate().rotation(0f).setDuration(500)
            }

        }

        binding.branchWiseL.titleTV.text = getString(R.string.branch_wise_revenue)
        binding.otaWiseL.titleTV.text = getString(R.string.ota_wise_revenue)

        binding.agentWiseL.summaryImg.setOnClickListener {
            if (binding.agentWiseL.recycleRV.isVisible) {
                binding.agentWiseL.recycleRV.gone()
                binding.agentWiseL.summaryImg.animate().rotation(-90.0f).setDuration(500)
            } else {
                binding.agentWiseL.recycleRV.visible()
                binding.agentWiseL.summaryImg.animate().rotation(0f).setDuration(500)

            }
        }
        binding.branchWiseL.summaryImg.setOnClickListener {
            if (binding.branchWiseL.recycleRV.isVisible) {
                binding.branchWiseL.recycleRV.gone()
                binding.branchWiseL.summaryImg.animate().rotation(-90.0f).setDuration(500)
            } else {
                binding.branchWiseL.recycleRV.visible()
                binding.branchWiseL.summaryImg.animate().rotation(0f).setDuration(500)


            }
        }
        binding.otaWiseL.summaryImg.setOnClickListener {
            if (binding.otaWiseL.recycleRV.isVisible) {
                binding.otaWiseL.recycleRV.gone()
                binding.otaWiseL.summaryImg.animate().rotation(-90.0f).setDuration(500)
            } else {
                binding.otaWiseL.recycleRV.visible()
                binding.otaWiseL.summaryImg.animate().rotation(0f).setDuration(500)

            }
        }
        binding.seatSoldL.summaryImg.setOnClickListener {
            if (binding.seatSoldL.mainCL.isVisible) {
                binding.seatSoldL.mainCL.gone()
            } else {
                binding.seatSoldL.mainCL.visible()

            }
        }


//        setAgentWiseAdapter(it.agentSummary)
//        setBranchWiseAdapter()
//        setOTAWiseAdapter()
//        setSeatsSoldAdapter()


        binding.layoutToolbar.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getRevenueDetails() {
        binding.progressBar.visible()
        dashboardRevenueViewModel.getRevenueRouteData(
            loginModelPref.api_key,
            fromDate!!,
            toDate!!,
            routeID!!,
            "get_revenue_route_details",
            journeyBy!!

        )
    }


    private fun getRevenueAgentHubData() {
        binding.progressBar.visible()
        if (intent.hasExtra("agentId")) {
            dashboardRevenueViewModel.getRevenueAgentHubDetails(
                loginModelPref.api_key,
                fromDate!!,
                toDate!!,
                "",
                id!!,
                journeyBy!!,
                "get_revenue_agent_hub_details"
            )
        } else {
            dashboardRevenueViewModel.getRevenueAgentHubDetails(
                loginModelPref.api_key,
                fromDate!!,
                toDate!!,
                routeID!!,
                "",
                journeyBy!!,
                "get_revenue_agent_hub_details"
            )
        }
    }


    private fun getDataFromIntent() {
        if (intent.hasExtra("routeId")) {

            id = intent.getIntExtra("agentId", 0).toString()
            routeID = intent.getIntExtra("routeId", 0).toString()

            fromDate = intent.getStringExtra("fromDate") ?: ""
            toDate = intent.getStringExtra("toDate") ?: ""
            isFromService = intent.getBooleanExtra("isFromService", false)
            binding.layoutToolbar.toolbarHeaderText.text = intent.getStringExtra("title") ?: ""
            binding.layoutToolbar.toolbarSubtitle.text = intent.getStringExtra("date") ?: ""
            journeyBy = intent.getStringExtra("journeyBy") ?: ""


        }
    }

    private fun setSeatsSoldAdapter(seatSold: ArrayList<SeatSold>?) {
        seatsSoldAdapter = SeatsSoldAdapter(this, seatSold, privilegeResponse)
        binding.seatSoldL.recycleRV.adapter = seatsSoldAdapter
    }


    private fun setAgentWiseAdapter(agentSummary: ArrayList<AgentWiseRevenue>?) {
        if (agentSummary?.size!! > 0) {
            agentWiseAdapter = NewAgentWiseRevenueAdapter(this, agentSummary, privilegeResponse)
            binding.agentWiseL.recycleRV.adapter = agentWiseAdapter
        }

    }

    private fun setBranchWiseAdapter(branchWiseSummary: ArrayList<AgentWiseRevenue>?) {
        branchWiseAdapter = NewAgentWiseRevenueAdapter(this, branchWiseSummary, privilegeResponse)
        binding.branchWiseL.recycleRV.adapter = branchWiseAdapter
    }

    private fun setOTAWiseAdapter(channelWiseRevenue: ArrayList<AgentWiseRevenue>?) {
        otaWiseAdapter = NewAgentWiseRevenueAdapter(this, channelWiseRevenue, privilegeResponse)
        binding.otaWiseL.recycleRV.adapter = otaWiseAdapter
    }

    override fun isInternetOnCallApisAndInitUI() {
    }


    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        date = getDateDMY(PreferenceUtils.getDashboardCurrentDate()).toString()
        privilegeResponse = getPrivilegeBase()
    }


    override fun onSingleButtonClick(str: String) {
//        toast("$str")
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


    private fun getRevenueRouteDataObserver() {
        dashboardRevenueViewModel.revenueReouteData.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                binding.progressBar.gone()
                when (it.code) {
                    200 -> {
                        binding.dataCL.visible()
                        setData(it)
                    }
                    
                    401 -> {
                       // openUnauthorisedDialog()
                        showUnauthorisedDialog()
                    }
                    
                    else -> {
                        toast("${it.code}")
                    }
                }
            } else
                toast(getString(R.string.server_error))
        })
    }


    private fun getRevenueAgentHubObserver() {
        dashboardRevenueViewModel.revenueAgentHub.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                binding.progressBar.gone()
                when (it.code) {
                    200 -> {
                        binding.dataCL.visible()
                        setAgentHubData(it)
                        
                        
                    }
                    
                    401 -> {
                       // openUnauthorisedDialog()
                        showUnauthorisedDialog()
                    }
                    
                    else -> {
                        toast("${it.code}")
                    }
                }
            } else
                toast(getString(R.string.server_error))
        })
    }

    private fun setAgentHubData(it: ServiceWiseRevenueData) {
        setSummaryData(it.serviceSummary)
        setSeatsSoldAdapter(it.seatSold)
        binding.seatSoldL.root.visible()
        binding.agentWiseL.root.gone()
        binding.branchWiseL.root.gone()
        binding.otaWiseL.root.gone()
    }

    private fun setData(it: ServiceWiseRevenueData) {
        setSummaryData(it.serviceSummary)
        setAgentData(it.agentSummary)
        setBranchData(it.branchSummary)
        setOTAData(it.channelSummary)

    }

    @SuppressLint("SetTextI18n")
    private fun setOTAData(channelSummary: ChannelSummary?) {
        binding.otaWiseL.totalRevenueTV.text =
            getString(R.string.total_revenue_colon) + privilegeResponse?.currency + channelSummary?.totalRevenue?.toDouble()
                ?.convert(
                    privilegeResponse?.currencyFormat
                        ?: this@RevenueServiceDetailsActivity.getString(R.string.indian_currency_format)
                )
        binding.otaWiseL.totalSeatsTV.text =
            getString(R.string.total_seats_with_colon) + channelSummary?.totalSeats?.toString()
        setOTAWiseAdapter(channelSummary?.channelWiseRevenue)
    }

    @SuppressLint("SetTextI18n")
    private fun setBranchData(branchSummary: BranchSummary?) {
        binding.branchWiseL.totalRevenueTV.text =
            getString(R.string.total_revenue_colon) + privilegeResponse?.currency + branchSummary?.totalRevenue?.toDouble()
                ?.convert(
                    privilegeResponse?.currencyFormat
                        ?: this@RevenueServiceDetailsActivity.getString(R.string.indian_currency_format)
                )
        binding.branchWiseL.totalSeatsTV.text =
            getString(R.string.total_seats_with_colon) + branchSummary?.totalSeats?.toString()
        setBranchWiseAdapter(branchSummary?.branchWiseRevenue)

    }

    @SuppressLint("SetTextI18n")
    private fun setAgentData(agentSummary: AgentSummary?) {
        binding.agentWiseL.totalRevenueTV.text =
            getString(R.string.total_revenue_colon) + privilegeResponse?.currency + agentSummary?.totalRevenue?.toDouble()
                ?.convert(
                    privilegeResponse?.currencyFormat
                        ?: this@RevenueServiceDetailsActivity.getString(R.string.indian_currency_format)
                )
        binding.agentWiseL.totalSeatsTV.text =
            getString(R.string.total_seats_with_colon) + agentSummary?.totalSeats?.toString()
        setAgentWiseAdapter(agentSummary?.agentWiseRevenue)
    }

    @SuppressLint("SetTextI18n")
    private fun setSummaryData(serviceSummary: ServiceSummary?) {

        serviceSummary?.apply {
            binding.seatsValueTV.text = this.seats.toString()
            binding.fareValueTV.text =
                privilegeResponse?.currency + this.fare?.toDouble()?.convert(
                    privilegeResponse?.currencyFormat
                        ?: this@RevenueServiceDetailsActivity.getString(R.string.indian_currency_format)
                )

            binding.comissionValueTV.text =
                privilegeResponse?.currency + this.commission?.toDouble()?.convert(
                    privilegeResponse?.currencyFormat
                        ?: this@RevenueServiceDetailsActivity.getString(R.string.indian_currency_format)
                )
            binding.netRevenueValueTV.text =
                privilegeResponse?.currency + this.netRevenue?.toDouble()?.convert(
                    privilegeResponse?.currencyFormat
                        ?: this@RevenueServiceDetailsActivity.getString(R.string.indian_currency_format)
                )
            binding.cancellationChargesValueTV.text =
                privilegeResponse?.currency + this.cancellationCharges?.toDouble()?.convert(
                    privilegeResponse?.currencyFormat
                        ?: this@RevenueServiceDetailsActivity.getString(R.string.indian_currency_format)
                )
            if (isFromService!!) {
                binding.expensesHeadTV.visible()
                binding.expensesValueTV.visible()
                binding.discountHeadTV.gone()
                binding.discountValueTV.gone()
                binding.boardingHeadTV.gone()
                binding.boardingValueTV.gone()
                binding.mealHeadTV.gone()
                binding.mealValueTV.gone()
                binding.expensesValueTV.text =
                    privilegeResponse?.currency + this.expenses?.toDouble()?.convert(
                        privilegeResponse?.currencyFormat
                            ?: this@RevenueServiceDetailsActivity.getString(R.string.indian_currency_format)
                    )
            } else {
                binding.expensesHeadTV.gone()
                binding.expensesValueTV.gone()
                binding.discountHeadTV.visible()
                binding.discountValueTV.visible()
                binding.boardingHeadTV.visible()
                binding.boardingValueTV.visible()
                binding.mealHeadTV.visible()
                binding.mealValueTV.visible()
                binding.discountValueTV.text =
                    privilegeResponse?.currency + this.discount?.toDouble()?.convert(
                        privilegeResponse?.currencyFormat
                            ?: this@RevenueServiceDetailsActivity.getString(R.string.indian_currency_format)
                    )
                binding.boardingValueTV.text =
                    privilegeResponse?.currency + this.boardingFee?.toDouble()?.convert(
                        privilegeResponse?.currencyFormat
                            ?: this@RevenueServiceDetailsActivity.getString(R.string.indian_currency_format)
                    )
                binding.mealValueTV.text =
                    privilegeResponse?.currency + this.mealPrice?.toDouble()?.convert(
                        privilegeResponse?.currencyFormat
                            ?: this@RevenueServiceDetailsActivity.getString(R.string.indian_currency_format)
                    )
            }

        }

    }

    override fun onMenuItemClick(
        itemPosition: Int,
        menuPosition: Int,
        busData: com.bitla.ts.domain.pojo.available_routes.Result
    ) {
    }



}