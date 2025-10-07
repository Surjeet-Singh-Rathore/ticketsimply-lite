package com.bitla.ts.presentation.view.activity.addRateCard.viewRateCard

import AddRateCardSingleViewModel
import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.*
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.add_rate_card.editRateCard.request.*
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.request.*
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.*
import com.bitla.ts.domain.pojo.alloted_services.Service
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.activity.addRateCard.editRateCard.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.dialog.DialogUtils.Companion.dismissProgressDialog
import com.bitla.ts.utils.dialog.DialogUtils.Companion.showProgressDialog
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import toast
import visible

class ViewCreatedRateCardActivity : BaseActivity(), DialogButtonListener,
    DialogSingleButtonListener, OnItemClickListener {

    private var currencySymbol: String = ""
    private lateinit var binding: ActivityViewCreatedRateCardBinding
    private val addRateCardViewModel by viewModel<AddRateCardViewModel<Any?>>()
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var resID: String? = null
    private var sourceId: String? = null
    private var source: String = ""
    private var destinationId: String? = null
    private var destination: String = ""
    private var busType: String? = null
    private var convertedDate: String? = null
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private var locale: String? = ""
    private var routeId: String? = null
    private var currency = ""
    private var viewRateCardFareResponse: ViewRateCardResponse? = null
    private val addRateCardSingleViewModel by viewModel<AddRateCardSingleViewModel<Any?>>()
    private var routeWiseFareDetailsList = mutableListOf<RouteWiseFareDetail>()
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private var seatListFare = mutableListOf<Service>()
    private var branchCityWiseFareList: MutableList<CityWiseFare> = mutableListOf()
    private var tempSeatListFare: MutableList<Service> = mutableListOf()   // for duplicate seat
    private var selectedCityPairOriginId: String = ""
    private var selectedCityPairDestinationId: String = ""
    private var rateCardId = ""
    private var adapterCmsn: ModifyCommissionDetailsAdapter?= null
    private var cityWiseCmsnList = mutableListOf<CityWiseCmsn>()
    private var fareDetailsBranchList = mutableListOf<RouteWiseFareDetail>()
    private var adapterFareDetails: ModifyEditRateCardFareAdapter? = null
    private var isHideCommissionTab: Boolean? = false
    
    override fun isInternetOnCallApisAndInitUI() {
    }

    override fun initUI() {
        binding = ActivityViewCreatedRateCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        init()
        lifecycleScope.launch {
            addRateCardViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }

        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()

        PreferenceUtils.apply {
            routeId = getString(getString(R.string.routeId))
            resID = getString(getString(R.string.updateRateCard_resId))
            source = getString(getString(R.string.updateRateCard_origin)).toString()
            destination = getString(getString(R.string.updateRateCard_destination)).toString()
            sourceId = getString(getString(R.string.updateRateCard_originId)).toString()
            destinationId = getString(getString(R.string.updateRateCard_destinationId)).toString()
            busType = getString(getString(R.string.updateRateCard_busType))
            convertedDate = getString(getString(R.string.updateRateCard_travelDate)) ?: ""
        }
        binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.view_rate_cardX)
        binding.updateRatecardToolbar.headerTitleDesc.text = busType

        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase()
            currency = privilegeResponseModel?.currency ?: ""
            
            if (privilegeResponseModel?.hideCommissionAndTieupCommissionInRouteLevel!=null) {
                isHideCommissionTab = privilegeResponseModel?.hideCommissionAndTieupCommissionInRouteLevel
            }
        }
        
        if (isHideCommissionTab == false) {
            binding.parentLayoutCms.visible()
        } else {
            binding.parentLayoutCms.gone()
        }
        
        rateCardId = intent.getStringExtra(getString(R.string.rate_card_id)) ?: ""
        callViewRateCardApi(rateCardId)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun init() {
        showProgressDialog(this)
        getPref()
        onClick()
        setViewRateCardObserver()
    }

    private fun setTimeData(it: ViewRateCardResponse?) {
        binding.tvTimeValue.text = it?.routeWiseTimeDetails?.time

        val collectApplyForList = ArrayList<String>()
        var finalApplyFor= ""

        if (it?.routeWiseTimeDetails?.applyFor?.arrival == true)
            collectApplyForList.add("Arrival")

        if (it?.routeWiseTimeDetails?.applyFor?.departure == true)
            collectApplyForList.add("Departure")

        if (it?.routeWiseTimeDetails?.applyFor?.bp == true)
            collectApplyForList.add("BP")

        if (it?.routeWiseTimeDetails?.applyFor?.dp == true)
            collectApplyForList.add("DP")

        collectApplyForList.forEach {
            finalApplyFor += "$it, "
        }

        binding.tvApplyForValueTime.text = finalApplyFor.dropLast(2)
        binding.tvIncDecTimeValue.text = it?.routeWiseTimeDetails?.incOrDec
    }

    private fun callViewRateCardApi(rateCardIdX: String) {

        if (isNetworkAvailable()) {
            addRateCardViewModel.viewRateCardApi(
                ViewRateCardReqBody(
                    apiKey = loginModelPref.api_key,
                    rateCardId = rateCardIdX,
                    locale = locale ?: ""
                ),
            )
        } else noNetworkToast()
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun setViewRateCardObserver() {

        addRateCardViewModel.viewRateCardResponse.observe(this) {

            dismissProgressDialog()

            if (it != null) {
                when (it.code) {
                    200 -> {
                        viewRateCardFareResponse = it
                        routeWiseFareDetailsList = it.routeWiseFareDetails
                        cityWiseCmsnList = it.routeWiseCmsnDetails.cityWiseCmsn
                        fareDetailsBranchList - it.routeWiseFareDetails

                        addRateCardSingleViewModel.setBranchViewRateResponse(it)

                        binding.rateCardNameValue.text = it.rateCardName
                        binding.startDateValue.text = ":      ${it.fromDate}"
                        binding.endDateValue.text = ":      ${it.toDate}"

                        setTimeData(viewRateCardFareResponse)

                        routeWiseFareDetailsList.forEach {
                            val cityWiseFare = CityWiseFare(
                                originId = it.originId,
                                destinationId = it.destinationId,
                                originName = it.originName,
                                destinationName = it.destinationName,
                                fareDetails = it.fareDetails
                            )
                            branchCityWiseFareList.add(cityWiseFare)
                        }

                        // fare seats
                        if (routeWiseFareDetailsList.isNotEmpty()) {
                            routeWiseFareDetailsList.forEach { it ->
                                it.fareDetails.forEach {
                                    val seatModel = Service()
                                    seatModel.routeId = it.id.toInt()
                                    seatModel.number = it.seatType
                                    tempSeatListFare.add(seatModel)
                                }
                            }
                        }
                        val uniqueSeatList = tempSeatListFare.toSet()
                        uniqueSeatList.forEach {
                            seatListFare.add(it)
                        }

                        setViewAdapter()
                    }

                    411 -> {
                        if (it.message != null) {
                            it.message.let { it1 -> this@ViewCreatedRateCardActivity.toast(it1) }
                        }
                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this@ViewCreatedRateCardActivity,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }

                    else -> {
                        if (it.result?.message != null) {
                            it.result.message.let { it1 ->
                                this@ViewCreatedRateCardActivity.toast(
                                    it1
                                )
                            }
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setViewAdapter() {
        setModifyFareRouteFareAdapter(routeWiseFareDetailsList)
        setModifyCommissionAdapter(cityWiseCmsnList)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setModifyFareRouteFareAdapter(fareDetailsList:  MutableList<RouteWiseFareDetail>) {

        fareDetailsList.forEachIndexed { indexOuter, it1 ->
            it1.fareDetails.forEachIndexed { indexInner, it ->
                it.fare = it.fare
            }
        }

        adapterFareDetails = ModifyEditRateCardFareAdapter(
            context = this,
            routeWiseFareDetailList = fareDetailsList,
            onItemClickListener = this,
            isFromModifyViewRateCard = true
        ) { item ->

        }

        binding.fareRV.adapter = adapterFareDetails
        adapterFareDetails?.notifyDataSetChanged()
        binding.bulkModifyTVFare.text = "${fareDetailsList.size} City Pair"

    }
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun setModifyCommissionAdapter(fareDetailsList:  MutableList<CityWiseCmsn>) {

//        Timber.d("fareDetailsList - $fareDetailsList")
//        selectedSeatList = addRateCardSingleViewModel.selectedSeat.value!!
//        Timber.d("seatLisX - $selectedSeatList")

        fareDetailsList.forEachIndexed { indexOuter, it1 ->
            it1.cmsnDetails.forEachIndexed { indexInner, it ->
                it.cmsn = it.cmsn
            }
        }

        adapterCmsn = ModifyCommissionDetailsAdapter(
            context = this,
            routeWiseCmsnDetailsList = fareDetailsList,
        )

        binding.commissionRV.adapter = adapterCmsn
        adapterCmsn?.notifyDataSetChanged()
        binding.bulkModifyTV.text = "${fareDetailsList.size} City Pair"
    }


    private fun setIntent() {
        val intent = Intent(this, ModifyEditRateCardActivity::class.java)

        intent.apply {
            putExtra(
                getString(R.string.updateRateCard_originId),
                selectedCityPairOriginId.toString()
            )
            putExtra(
                getString(R.string.updateRateCard_destinationId),
                selectedCityPairDestinationId.toString()
            )
            putExtra(
                getString(R.string.from_view_rate_card),
                true
            )
        }

       startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun onClick() {

        if (privilegeResponseModel != null) {

            privilegeResponseModel?.let {
                if (it.currency.isNotEmpty()) {
                    currencySymbol = it.currency
                }
            }
        } else {
            toast(getString(R.string.server_error))
        }

        binding.apply {

            modifyFareBT.setOnClickListener {
                setIntent()
//                Timber.d("origin_des_id - $selectedCityPairOriginId  - $selectedCityPairDestinationId")
//                Timber.d("fareSeatId - $seatId")
            }

            imgArrowRateCard.setOnClickListener {
                if (childRateCardDetails.isVisible) {
                    childRateCardDetails.gone()
                    firstV.gone()
                    imgArrowRateCard.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    childRateCardDetails.visible()
                    firstV.visible()
                    imgArrowRateCard.setImageResource(R.drawable.ic_arrow_up_24)
                }
            }

            imgArrowFare.setOnClickListener {
                if (childLayoutFare.isVisible) {
                    childLayoutFare.gone()
                    lineViewFare.gone()
                    imgArrowFare.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    childLayoutFare.visible()
                    lineViewFare.visible()
                    imgArrowFare.setImageResource(R.drawable.ic_arrow_up_24)
                }
            }

            imgArrowTime.setOnClickListener {
                if (childLayoutTime.isVisible) {
                    childLayoutTime.gone()
                    lineViewTime.gone()
                    imgArrowTime.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    childLayoutTime.visible()
                    lineViewTime.visible()
                    imgArrowTime.setImageResource(R.drawable.ic_arrow_up_24)
                }
            }

            imgArrowCms.setOnClickListener {
                if (childLayoutCms.isVisible) {
                    childLayoutCms.gone()
                    lineViewCmsn.gone()
                    imgArrowCms.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    childLayoutCms.visible()
                    lineViewCmsn.visible()
                    imgArrowCms.setImageResource(R.drawable.ic_arrow_up_24)
                }
            }
        }
    }


    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(this@ViewCreatedRateCardActivity)
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onLeftButtonClick() {
    }

    override fun onRightButtonClick() {
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }
}