package com.bitla.ts.presentation.view.fragments

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.smart_miles_otp_method_name
import com.bitla.ts.data.validate_coupon_method_name
import com.bitla.ts.databinding.FragmentCouponBinding
import com.bitla.ts.domain.pojo.SpinnerItems
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.booking_custom_request.BookingCustomRequest
import com.bitla.ts.domain.pojo.coupon.request.CouponCodeHash
import com.bitla.ts.domain.pojo.coupon.request.CouponRequest
import com.bitla.ts.domain.pojo.coupon.request.DiscountParams
import com.bitla.ts.domain.pojo.coupon.request.PrePostPoneHash
import com.bitla.ts.domain.pojo.coupon.request.PreviousPnrHash
import com.bitla.ts.domain.pojo.coupon.request.PrivilegeCardHash
import com.bitla.ts.domain.pojo.coupon.request.ReqBody
import com.bitla.ts.domain.pojo.coupon.request.SmartMilesHash
import com.bitla.ts.domain.pojo.custom_applied_coupons.AppliedCoupon
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.passenger_details_result.PassengerDetailsResult
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_details_response.SeatDetail
import com.bitla.ts.domain.pojo.smart_miles_otp.request.SmartMilesOtpRequest
import com.bitla.ts.presentation.view.activity.BookingPaymentOptionsActivity
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.viewModel.ValidateCouponViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.retrieveBookingCustomRequest
import com.bitla.ts.utils.common.retrieveGstApplicable
import com.bitla.ts.utils.common.retrieveRouteId
import com.bitla.ts.utils.common.retrieveSelectedExtraSeats
import com.bitla.ts.utils.common.retrieveSelectedPassengers
import com.bitla.ts.utils.common.retrieveSelectedSeatNumber
import com.bitla.ts.utils.common.retrieveSelectedSeats
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_OWN_ROUTE
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_SELECTED_AVAILABLE_ROUTES
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.google.gson.Gson
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.io.Serializable


class CouponFragment : BaseFragment(), DialogSingleButtonListener, View.OnClickListener {
    private var serviceNumber: String = ""
    private var isAgentLogin: Boolean = false
    private var isBima: Boolean = false
    private var isOwnRoute: Boolean = false
    private var isIndividualDiscountApplied: Boolean = false
    private val corpCompanyId: String = "" //fixed
    private val isBimaService: String = "false" //fixed
    private val returnResId: String = "" //fixed (Optional)
    private val isRoundTrip: String = "false" //fixed
    private val returnResSeatsCount: String = "0" //fixed
    private val connectingResSeatsCount: String = "" //fixed
    private val allowPrePostPoneOtherBranch: String = "false" //fixed
    private lateinit var binding: FragmentCouponBinding

    private var smartMilesOtp: String? = null
    private var smartMilesOtpKey: String? = null
    private var checkedCouponType: String? = ""
    var countOffers: Int = 0
    private var privielgeMobileNumber: String = ""
    private var cardNumber: String = ""
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var sourceId: String = ""
    private var destinationId: String = ""
    private var source: String? = ""
    private var destination: String? = ""
    private var travelDate: String = ""
    private var busType: String? = null
    private var deptTime: String? = null
    private var resId: Long? = 0
    private var totalFare: Double = 0.0
    private var totalDiscountAmount: Double = 0.0
    private var noOfSeats: String? = "0"
    private var selectedSeatNo: String? = null
    private var agentType: String? = null
    private var routeId: Int? = null
    private var isGstApplicable: Boolean = false
    private var bookingCustomRequest = BookingCustomRequest()
    private var selectedSeatDetails =
        ArrayList<SeatDetail>()
    private var passengerList: ArrayList<PassengerDetailsResult> = ArrayList()

    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private val validateCouponViewModel by viewModel<ValidateCouponViewModel<Any?>>()
    private var appliedCouponList = mutableListOf<AppliedCoupon>()
    private var toolbarTitle: String = ""
    private var vipCategoryId: String = ""
    private var locale: String? = ""
    private var rutApplied: Boolean = false
    var selectedExtraSeatDetails = ArrayList<SeatDetail>()



    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun isNetworkOff() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCouponBinding.inflate(inflater, container, false)
        getPref()
        getBookingInfo()
        getSeatDetails()
        getPassengersList()
        routeId = retrieveRouteId()
        isGstApplicable = retrieveGstApplicable()
        enableCouponCode()
        enableDiscountAmount()
        if (isGstApplicable)
            binding.gstDetails.root.visible()
        else
            binding.gstDetails.root.gone()

        onClickListener()
        setObserver()

        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if (!privilegeResponseModel.isPrePostpone && privilegeResponseModel.freeTicket == false && isAgentLogin) {
                val intent = Intent(requireContext(), BookingPaymentOptionsActivity::class.java)
                intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                intent.putExtra(
                    getString(R.string.applied_coupons),
                    appliedCouponList as Serializable
                )
                startActivity(intent)
            }

        }

        lifecycleScope.launch {
            validateCouponViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        return binding.root
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        sourceId = PreferenceUtils.getSourceId()
        destinationId = PreferenceUtils.getDestinationId()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()
        loginModelPref = PreferenceUtils.getLogin()
        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            resId = result?.reservation_id?.toLong()
            busType = result?.bus_type ?: getString(R.string.empty)
            deptTime = result?.dep_time ?: getString(R.string.empty)
            serviceNumber = result?.number ?: getString(R.string.empty)
        }
        privilegeResponseModel = (activity as BaseActivity).getPrivilegeBase() as PrivilegeResponseModel

        if (PreferenceUtils.getPreference(PREF_IS_OWN_ROUTE, false) != null)
            isOwnRoute = PreferenceUtils.getPreference(PREF_IS_OWN_ROUTE, false)!!

        if (PreferenceUtils.getPreference("is_bima", false) == true) {
            isBima = true
        }

        if (privilegeResponseModel.isAgentLogin != null)
            isAgentLogin = privilegeResponseModel.isAgentLogin
    }

    private fun getPassengersList() {
        passengerList = retrieveSelectedPassengers()
        passengerList.forEach {
            try {
                totalFare += (it.fare?.toDouble())!!
            } catch (e: Exception) {
                //
            }
        }

        if (passengerList.any { it.discountAmount != null && it.discountAmount?.toDouble()!! > 0 }) {
            isIndividualDiscountApplied = true
            disableDiscountAmount()
        } else
            isIndividualDiscountApplied = false

        if (isIndividualDiscountApplied) {
            checkedCouponType = getString(R.string.discount_amount)

            passengerList.forEach {
                totalDiscountAmount += it.discountAmount?.toDouble()!!
                addAppliedCoupon(totalDiscountAmount.toString())
            }
        }
    }

    private fun getSeatDetails() {
        selectedSeatDetails = retrieveSelectedSeats()
        noOfSeats = selectedSeatDetails.size.toString()
        selectedSeatNo = retrieveSelectedSeatNumber()
        selectedExtraSeatDetails = retrieveSelectedExtraSeats()


        if (selectedSeatDetails.any { it.isExtraSeat } && selectedExtraSeatDetails.size == 0) {
            disableDiscountAmount()
            disableSmartMiles()
            disableCouponCode()
            disablePrepost()
            disableFreeTicket()
            disableGstAmount()
            disablePreviousPnr()
            disablePrivilegeCard()
            discountAmountChange()
        }
    }

    private fun getBookingInfo() {
        bookingCustomRequest = retrieveBookingCustomRequest()
        agentType = bookingCustomRequest.selected_booking_id.toString()

        if (agentType == "12" || agentType == "0")
            agentType = "3"
    }

    fun onClickListener() {
        /* checking below privilege
        * is_vip_booking
          is_free_booking
        *   */
        couponCodeChange()
        prepostChange()
        privilegeCardChange()
        gstDetailsChange()
        smartMilesChnage()
        discountAmountChange()
        quotePreviousPnrChange()
        vipTicketChange()
        // vipTicketFreeChange()
        freeTicketChange()

        binding.layoutCoupons.setOnClickListener(this)
    }

    private fun setObserver() {
        validateCouponViewModel.loadingState.observe(viewLifecycleOwner, Observer { it ->
            Timber.d("LoadingState ${it.status}")
            when (it) {
                LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                else -> binding.includeProgress.progressBar.gone()
            }
        })

        validateCouponViewModel.couponDetails.observe(viewLifecycleOwner) {
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                checkUncheckCoupons(code = it.code)
                when (it.code) {
                    "200" -> {
                        enableDisableCoupons()
                    }
                    "401" -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }
                    else -> {
                        requireContext().toast(it.message)
                    }
                }
                //enableDisableCoupons()
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }

        validateCouponViewModel.smartMilesOtp.observe(viewLifecycleOwner) {
            it
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                Timber.d("smartMilesOtpResponse$it")
                if (it.code == 200) {
                    smartMilesOtp = it.otp
                    smartMilesOtpKey = it.otp_key
                    binding.applySmartMiles.layoutOtp.visible()
                    binding.applySmartMiles.layoutButtons.visible()
                } else {

                    if (it.message != null) {
                        it.message.let { it1 ->
                            requireContext().toast(it1)
                        }
                    }
                    binding.applySmartMiles.checkBox.isChecked = false
                    binding.applySmartMiles.editCode.setText("")
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun checkUncheckCoupons(code: String) {
        if (checkedCouponType != null) {
            when (checkedCouponType) {
                getString(R.string.coupon_code) -> {
                    if (code == "200") {

                        TransitionManager.beginDelayedTransition(
                            binding.couponCode.cardMain,
                            AutoTransition()
                        )
                        binding.couponCode.expandedCard.gone()
                        binding.couponCode.validCoupon.visible()
                        binding.couponCode.editTxt.visible()
                        binding.couponCode.validCoupon.background =
                            resources.getDrawable(R.drawable.tick_img)
                        binding.couponCode.validCoupon.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#00ADB5"))
                    } else {
                        uncheckCouponCode()
                    }
                }

                getString(R.string.pre_postpone_ticket) -> {
                    if (code == "200") {
                        TransitionManager.beginDelayedTransition(
                            binding.prePostTicket.cardMain,
                            AutoTransition()
                        )
                        binding.prePostTicket.expandedCard.gone()
                        binding.prePostTicket.validCoupon.visible()
                        binding.prePostTicket.editTxt.visible()
                        binding.prePostTicket.validCoupon.background =
                            resources.getDrawable(R.drawable.tick_img)
                        binding.prePostTicket.validCoupon.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#00ADB5"))
                    } else {
                        uncheckPrepostPone()
                    }
                }

                getString(R.string.privilege_card) -> {
                    if (code == "200") {
                        TransitionManager.beginDelayedTransition(
                            binding.privilegedCard.cardMain,
                            AutoTransition()
                        )
                        binding.privilegedCard.expandedCard.gone()

                        binding.privilegedCard.validCoupon.visible()
                        binding.privilegedCard.editTxt.visible()
                        binding.privilegedCard.validCoupon.background =
                            resources.getDrawable(R.drawable.tick_img)
                        binding.privilegedCard.validCoupon.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#00ADB5"))
                    } else {
                        uncheckPrivilegeCard()
                    }
                }
                getString(R.string.apply_smart_miles) -> {
                    if (code == "200") {
                        TransitionManager.beginDelayedTransition(
                            binding.applySmartMiles.cardMain,
                            AutoTransition()
                        )
                        binding.applySmartMiles.expandedCard.gone()
                        binding.applySmartMiles.validCoupon.visible()
                        binding.applySmartMiles.editTxt.visible()

                        binding.applySmartMiles.validCoupon.background =
                            resources.getDrawable(R.drawable.tick_img)
                        binding.applySmartMiles.validCoupon.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#00ADB5"))
                    } else {
                        uncheckSmartMiles()
                    }
                }

                getString(R.string.quote_previous_pnr) -> {
                    if (code == "200") {
                        TransitionManager.beginDelayedTransition(
                            binding.quotePreviousPnr.cardMain,
                            AutoTransition()
                        )
                        binding.quotePreviousPnr.expandedCard.gone()

                        binding.quotePreviousPnr.validCoupon.visible()
                        binding.quotePreviousPnr.editTxt.visible()

                        binding.quotePreviousPnr.validCoupon.background =
                            resources.getDrawable(R.drawable.tick_img)
                        binding.quotePreviousPnr.validCoupon.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#00ADB5"))
                    } else {
                        uncheckPreviousPnr()
                    }
                }
            }
        }
    }

    private fun enableDisableCoupons() {
        if (checkedCouponType != null) {
            when (checkedCouponType) {
                getString(R.string.coupon_code) -> {
                    disablePrepost()
                    addAppliedCoupon(binding.couponCode.editCode.text.toString())
                }
                getString(R.string.pre_postpone_ticket) -> {
                    disableCouponCode()
                    addAppliedCoupon(binding.prePostTicket.editCode.text.toString())
                }
                getString(R.string.privilege_card) -> {
                    addAppliedCoupon(binding.privilegedCard.editCode.text.toString())
                    disablePreviousPnr()
                    disableDiscountAmount()
                    disableSmartMiles()
                    if (!binding.couponCode.checkBox.isChecked)
                        disableCouponCode()
                }
                getString(R.string.quote_previous_pnr) -> {
                    val pnrAndPhone =
                        "${binding.quotePreviousPnr.editCode.text.toString()}-${binding.quotePreviousPnr.editCodeSub.text.toString()}"
                    addAppliedCoupon(binding.quotePreviousPnr.editCode.text.toString())
                    disablePrivilegeCard()
                    disableDiscountAmount()
                    disableSmartMiles()
                }
                getString(R.string.discount_amount) -> {
                    addAppliedCoupon(binding.discountAmount.editCode.text.toString())
                    disablePrivilegeCard()
                    disablePreviousPnr()
                    disableSmartMiles()
                }
                getString(R.string.apply_smart_miles) -> {
                    addAppliedCoupon(binding.applySmartMiles.editCode.text.toString())
                    disablePrivilegeCard()
                    disablePreviousPnr()
                    disableDiscountAmount()
                }
            }
        }
    }

    private fun addAppliedCoupon(couponCode: String) {
        //appliedCouponList.clear()
        removeAppliedCoupon(checkedCouponType.toString())
        appliedCouponList.add(AppliedCoupon(couponCode, checkedCouponType.toString()))
        Timber.d("appliedCouponList- $appliedCouponList")

        //passengerDetailsViewModel.setAppliedCoupon(appliedCouponList)
    }

    /*override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.img_back -> {
              //onBackPressed()
            }
            R.id.btnPaymentOptions -> {
                val intent = Intent(requireContext(), BookingPaymentOptionsActivity::class.java)
                intent.putExtra(getString(R.string.toolbar_title), toolbarTitle)
                intent.putExtra(
                    getString(R.string.applied_coupons),
                    appliedCouponList as Serializable
                )
                startActivity(intent)
            }
        }
    }*/



    private fun disablePrepost() {
        binding.prePostTicket.checkBox.isChecked = false
        binding.prePostTicket.checkBox.isEnabled = false
        binding.prePostTicket.cardMain.setBackgroundColor(resources.getColor(R.color.button_color))
        binding.prePostTicket.validCoupon.background = resources.getDrawable(R.drawable.ic_cross)
        binding.prePostTicket.validCoupon.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#ed3237"))
        binding.prePostTicket.validCoupon.visible()
        binding.prePostTicket.editTxt.gone()
        binding.prePostTicket.expandedCard.gone()
    }

    private fun disableCouponCode() {
        binding.couponCode.checkBox.isChecked = false
        binding.couponCode.checkBox.isEnabled = false
        binding.couponCode.cardMain.setBackgroundColor(resources.getColor(R.color.button_color))
        binding.couponCode.validCoupon.background = resources.getDrawable(R.drawable.ic_cross)
        binding.couponCode.validCoupon.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#ed3237"))
        binding.couponCode.validCoupon.visible()
        binding.couponCode.editTxt.gone()
        binding.couponCode.expandedCard.gone()
    }

    private fun disablePrivilegeCard() {
        binding.privilegedCard.checkBox.isChecked = false
        binding.privilegedCard.checkBox.isEnabled = false
        binding.privilegedCard.cardMain.setBackgroundColor(resources.getColor(R.color.button_color))
        binding.privilegedCard.validCoupon.background = resources.getDrawable(R.drawable.ic_cross)
        binding.privilegedCard.validCoupon.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#ed3237"))
        binding.privilegedCard.validCoupon.visible()
        binding.privilegedCard.editTxt.gone()
        binding.privilegedCard.expandedCard.gone()
    }

    private fun disablePreviousPnr() {
        binding.quotePreviousPnr.checkBox.isChecked = false
        binding.quotePreviousPnr.checkBox.isEnabled = false
        binding.quotePreviousPnr.cardMain.setBackgroundColor(resources.getColor(R.color.button_color))
        binding.quotePreviousPnr.validCoupon.background = resources.getDrawable(R.drawable.ic_cross)
        binding.quotePreviousPnr.validCoupon.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#ed3237"))
        binding.quotePreviousPnr.validCoupon.visible()
        binding.quotePreviousPnr.editTxt.gone()
        binding.quotePreviousPnr.expandedCard.gone()
    }

    private fun disableDiscountAmount() {
        binding.discountAmount.checkBox.isChecked = false
        binding.discountAmount.checkBox.isEnabled = false
        binding.discountAmount.cardMain.setBackgroundColor(resources.getColor(R.color.button_color))
        binding.discountAmount.validCoupon.background = resources.getDrawable(R.drawable.ic_cross)
        binding.discountAmount.validCoupon.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#ed3237"))
        binding.discountAmount.validCoupon.visible()
        binding.discountAmount.editTxt.gone()
        binding.discountAmount.expandedCard.gone()
    }

    private fun disableSmartMiles() {
        binding.applySmartMiles.checkBox.isChecked = false
        binding.applySmartMiles.checkBox.isEnabled = false
        binding.applySmartMiles.cardMain.setBackgroundColor(resources.getColor(R.color.button_color))
        binding.applySmartMiles.validCoupon.background = resources.getDrawable(R.drawable.ic_cross)
        binding.applySmartMiles.validCoupon.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#ed3237"))
        binding.applySmartMiles.validCoupon.visible()
        binding.applySmartMiles.editTxt.gone()
        binding.applySmartMiles.expandedCard.gone()
    }

    private fun disableGstAmount() {
        binding.gstDetails.checkBox.isChecked = false
        binding.gstDetails.checkBox.isEnabled = false
        binding.gstDetails.cardMain.setBackgroundColor(resources.getColor(R.color.button_color))
        binding.gstDetails.validCoupon.background = resources.getDrawable(R.drawable.ic_cross)
        binding.gstDetails.validCoupon.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#ed3237"))
        binding.gstDetails.validCoupon.visible()
        binding.gstDetails.editTxt.gone()
        binding.gstDetails.expandedCard.gone()
    }

    private fun disableVipTicket() {
        binding.vipTicket.checkBox.isChecked = false
        binding.vipTicket.checkBox.isEnabled = false
        binding.vipTicket.cardMain.setBackgroundColor(resources.getColor(R.color.button_color))
        binding.vipTicket.validCoupon.visible()
        binding.vipTicket.validCoupon.background = resources.getDrawable(R.drawable.ic_cross)
        binding.vipTicket.validCoupon.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#ed3237"))
    }

    private fun disableFreeTicket() {
        binding.freeTickets.checkBox.isChecked = false
        binding.freeTickets.checkBox.isEnabled = false
        binding.freeTickets.cardMain.setBackgroundColor(resources.getColor(R.color.button_color))
        binding.freeTickets.validCoupon.visible()
        binding.freeTickets.validCoupon.background = resources.getDrawable(R.drawable.ic_cross)
        binding.freeTickets.validCoupon.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#ed3237"))
    }

    private fun enablePrePost() {
        binding.prePostTicket.checkBox.isEnabled = true
        binding.prePostTicket.cardMain.setBackgroundColor(resources.getColor(R.color.white))
        binding.prePostTicket.validCoupon.gone()
    }

    private fun enableCouponCode() {
        Timber.d("test couponCode:")
        if (!PreferenceUtils.getSelectedCoupon().isNullOrEmpty()|| !PreferenceUtils.getString("AutoDiscountCouponCode").isNullOrEmpty()){
            PreferenceUtils.getSelectedCoupon()?.forEach {
                Timber.d("test couponCode:3- ${PreferenceUtils.getString("AutoDiscountCouponCode")}")
                if (it.auto_discount_coupon!=null && it.auto_discount_coupon!=""  ){
                    Timber.d("test couponCode:3")

                    rutApplied= true
                }
            }
            if (!PreferenceUtils.getString("AutoDiscountCouponCode").isNullOrEmpty()){
                Timber.d("test couponCode4")

                rutApplied= true
            }
        }
        if (!rutApplied){
            binding.couponCode.checkBox.isEnabled = true
            binding.couponCode.cardMain.setBackgroundColor(resources.getColor(R.color.white))
            binding.couponCode.validCoupon.gone()
        }else{
            disableCouponCode()
        }

    }

    private fun enablePrivilegeCard() {
        binding.privilegedCard.checkBox.isEnabled = true
        binding.privilegedCard.cardMain.setBackgroundColor(resources.getColor(R.color.white))
        binding.privilegedCard.validCoupon.gone()
    }

    private fun enablePreviousPnr() {
        binding.quotePreviousPnr.checkBox.isEnabled = true
        binding.quotePreviousPnr.cardMain.setBackgroundColor(resources.getColor(R.color.white))
        binding.quotePreviousPnr.validCoupon.gone()
    }

    private fun enableDiscountAmount() {
        if (!PreferenceUtils.getSelectedCoupon().isNullOrEmpty()|| !PreferenceUtils.getString("AutoDiscountCouponCode").isNullOrEmpty()){
            Timber.d("test couponCode:3- ${PreferenceUtils.getSelectedCoupon()}")

            PreferenceUtils.getSelectedCoupon()?.forEach {
                Timber.d("test couponCode:3- ${it.auto_discount_coupon}")
                if (it.auto_discount_coupon!=null && it.auto_discount_coupon!=""  ){
                    Timber.d("test couponCode:3")

                    rutApplied= true
                }
            }
            if (!PreferenceUtils.getString("AutoDiscountCouponCode").isNullOrEmpty()){
                Timber.d("test couponCode4")

                rutApplied= true
            }
        }
        if (!rutApplied){
            binding.discountAmount.checkBox.isEnabled = true
            binding.discountAmount.cardMain.setBackgroundColor(resources.getColor(R.color.white))
            binding.discountAmount.validCoupon.gone()
        }else{
            disableDiscountAmount()
        }



    }

    private fun enableGstAmount() {
        binding.gstDetails.checkBox.isEnabled = true
        binding.gstDetails.cardMain.setBackgroundColor(resources.getColor(R.color.white))
        binding.gstDetails.validCoupon.gone()
    }

    private fun enableSmartMiles() {
        binding.applySmartMiles.checkBox.isEnabled = true
        binding.applySmartMiles.cardMain.setBackgroundColor(resources.getColor(R.color.white))
        binding.applySmartMiles.validCoupon.gone()
    }

    private fun enableFreeTicket() {
        binding.freeTickets.checkBox.isEnabled = true
        binding.freeTickets.cardMain.setBackgroundColor(resources.getColor(R.color.white))
        binding.freeTickets.validCoupon.gone()
    }

    private fun enableVipTicket() {
        binding.vipTicket.checkBox.isEnabled = true
        binding.vipTicket.cardMain.setBackgroundColor(resources.getColor(R.color.white))
        binding.vipTicket.validCoupon.gone()
    }


    private fun couponCodeChange() {
        val couponCode = binding.couponCode
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if (privilegeResponseModel.allowToDoOpenTicketCoupon && !isAgentLogin)
                couponCode.root.visible()
            else
                couponCode.root.gone()
        }

        couponCode.checkBox.setOnClickListener {
            if (!couponCode.editTxt.isVisible) {
                if (couponCode.expandedCard.isVisible) {
                    TransitionManager.beginDelayedTransition(
                        couponCode.cardMain,
                        AutoTransition()
                    )
                    couponCode.expandedCard.gone()
                    couponCode.editCode.setText("")

                } else {
                    TransitionManager.beginDelayedTransition(
                        couponCode.cardMain,
                        AutoTransition()
                    )
                    couponCode.expandedCard.visible()

                }
            } else {
                couponCode.editCode.setText("")
                couponCode.editTxt.gone()
                countOffers--
                couponCode.validCoupon.gone()
            }
        }

        couponCode.editCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (count > 0) {
                    couponCode.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.tick_img,
                        0
                    )
                } else {
                    couponCode.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    )
                }

            }
        })

        couponCode.applyBtn.setOnClickListener {
            checkedCouponType = getString(R.string.coupon_code)
            if (couponCode.editCode.text?.isNotEmpty()!!) {
                /*      TransitionManager.beginDelayedTransition(
                         couponCode.cardMain,
                         AutoTransition()
                     )
                    couponCode.expandedCard.gone()
                     couponCode.validCoupon.visible()
                     couponCode.editTxt.visible()
                     binding.couponCode.validCoupon.background =
                         resources.getDrawable(R.drawable.tick_img)
                     binding.couponCode.validCoupon.backgroundTintList =
                         ColorStateList.valueOf(Color.parseColor("#00ADB5"))*/

                countOffers++
                //closeKeyBoard()
                if (requireContext().isNetworkAvailable()) {
                    val couponCodeHash = CouponCodeHash()
                    couponCodeHash.couponCode = couponCode.editCode.text.toString()
                    couponCodeHash.reservationId = resId.toString()

                    val discountParams = DiscountParams()
                    discountParams.couponCodeHash = couponCodeHash

                    validateCouponCode(discountParams)
                } else
                    requireContext().noNetworkToast()
            } else
                requireContext().toast(getString(R.string.enter_coupon_code))
        }

        couponCode.cancelBtn.setOnClickListener {
            uncheckCouponCode()
        }

        couponCode.editTxt.setOnClickListener {
            TransitionManager.beginDelayedTransition(
                couponCode.cardMain,
                AutoTransition()
            )
            couponCode.expandedCard.visible()
        }

        couponCode.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                enablePrePost()
                removeAppliedCoupon(getString(R.string.coupon_code))
            }
        }
    }

    private fun uncheckCouponCode() {
        TransitionManager.beginDelayedTransition(
            binding.couponCode.cardMain,
            AutoTransition()
        )
        binding.couponCode.expandedCard.gone()
        binding.couponCode.editCode.setText("")
        binding.couponCode.editTxt.gone()
        binding.couponCode.validCoupon.gone()
        binding.couponCode.checkBox.setChecked(false)
        countOffers--
    }

    private fun uncheckVipTicket() {
        TransitionManager.beginDelayedTransition(
            binding.vipTicket.cardMain,
            AutoTransition()
        )
        binding.vipTicket.expandedCard.gone()
        binding.vipTicket.acVip.setText("")
        binding.vipTicket.editTxt.gone()
        binding.vipTicket.validCoupon.gone()
        binding.vipTicket.checkBox.isChecked = false
    }

    private fun removeAppliedCoupon(couponType: String) {
        val index = appliedCouponList.indexOfFirst { it.coupon_type == couponType }
        if (index != -1) {
            appliedCouponList.removeAt(index)
        }
    }


    private fun validateCouponCode(discountParams: DiscountParams) {
        val reqBody = ReqBody()
        reqBody.apiKey = loginModelPref.api_key
        reqBody.discountParams = discountParams
        reqBody.locale = locale

        val couponRequest = CouponRequest()
        couponRequest.bccId = bccId.toString()
        couponRequest.format = format_type
        couponRequest.methodName = validate_coupon_method_name
        couponRequest.reqBody = reqBody

        /*validateCouponViewModel.validateCouponApi(
            authorization = loginModelPref.auth_token,
            apiKey = loginModelPref.api_key,
            couponRequest = couponRequest,
            apiType = validate_coupon_method_name
        )*/

        validateCouponViewModel.validateCouponApi(
            couponRequest = reqBody,
            apiType = validate_coupon_method_name
        )

        Timber.d("couponRequest ${Gson().toJson(couponRequest)}")

    }

    private fun prepostChange() {
        val prePostTicket = binding.prePostTicket
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if (privilegeResponseModel.isPrePostpone)
                prePostTicket.root.visible()
            else
                prePostTicket.root.gone()
        }
        prePostTicket.editCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (count > 0) {
                    prePostTicket.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.tick_img,
                        0
                    )
                } else {
                    prePostTicket.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    )
                }

            }
        })

        prePostTicket.applyBtn.setOnClickListener {
            checkedCouponType = getString(R.string.pre_postpone_ticket)
            when {
                prePostTicket.editCode.text!!.isEmpty() -> requireContext().toast(getString(R.string.enter_ticket_number))
                else -> {
                    /*       TransitionManager.beginDelayedTransition(
                               prePostTicket.cardMain,
                               AutoTransition()
                           )
                           prePostTicket.expandedCard.gone()
                           prePostTicket.validCoupon.visible()
                           prePostTicket.editTxt.visible()
                           binding.prePostTicket.validCoupon.background =
                               resources.getDrawable(R.drawable.tick_img)
                           binding.prePostTicket.validCoupon.backgroundTintList =
                               ColorStateList.valueOf(Color.parseColor("#00ADB5"))*/

                    countOffers++

                    //closeKeyBoard()
                    if (requireContext().isNetworkAvailable()) {
                        val prePostPoneHash = PrePostPoneHash()
                        prePostPoneHash.pnrNumber = prePostTicket.editCode.text.toString()
                        prePostPoneHash.agentType = agentType
                        prePostPoneHash.isBimaService = isBimaService
                        prePostPoneHash.originId = sourceId
                        prePostPoneHash.destinationId = destinationId
                        prePostPoneHash.noOfSeats = noOfSeats
                        prePostPoneHash.corpCompanyId = corpCompanyId
                        prePostPoneHash.allowPrePostPoneOtherBranch = allowPrePostPoneOtherBranch

                        val discountParams = DiscountParams()
                        discountParams.prePostPoneHash = prePostPoneHash

                        validateCouponCode(discountParams)
                    } else
                        requireContext().noNetworkToast()
                }
            }

        }

        prePostTicket.cancelBtn.setOnClickListener {
            uncheckPrepostPone()
        }

        prePostTicket.editTxt.setOnClickListener {
            TransitionManager.beginDelayedTransition(
                prePostTicket.cardMain,
                AutoTransition()
            )
            prePostTicket.expandedCard.visible()
        }

        binding.prePostTicket.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            TransitionManager.beginDelayedTransition(
                prePostTicket.cardMain,
                AutoTransition()
            )
            if (!isChecked) {
                removeAppliedCoupon(getString(R.string.pre_postpone_ticket))
                binding.prePostTicket.editTxt.gone()
                binding.prePostTicket.validCoupon.gone()
                binding.prePostTicket.expandedCard.gone()
                binding.prePostTicket.editCode.setText("")
                if (!binding.freeTickets.checkBox.isChecked && !binding.privilegedCard.checkBox.isChecked)
                    enableCouponCode()
            } else {
                binding.prePostTicket.expandedCard.visible()
            }
        }
    }

    private fun uncheckPrepostPone() {
        TransitionManager.beginDelayedTransition(
            binding.prePostTicket.cardMain,
            AutoTransition()
        )
        binding.prePostTicket.expandedCard.gone()
        binding.prePostTicket.editCode.setText("")
        binding.prePostTicket.editTxt.gone()
        binding.prePostTicket.validCoupon.gone()
        binding.prePostTicket.checkBox.isChecked = false
        countOffers--
    }

    private fun privilegeCardChange() {
        val privilegeCard = binding.privilegedCard

        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if (privilegeResponseModel.allowPrivilegeCardBookings && !isAgentLogin)
                privilegeCard.root.visible()
            else
                privilegeCard.root.gone()
        }

        privilegeCard.checkBox.setOnClickListener {
            if (privilegeCard.editTxt.isVisible) {
                privilegeCard.editCode.setText("")
                privilegeCard.editTxt.gone()
                countOffers--
                privilegeCard.validCoupon.gone()
            }
        }

        privilegeCard.editCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (count > 0) {
                    privilegeCard.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.tick_img,
                        0
                    )
                } else {
                    privilegeCard.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    )
                }

            }
        })

        privilegeCard.applyBtn.setOnClickListener {
            checkedCouponType = getString(R.string.privilege_card)
            val cardOrMobileNumber = privilegeCard.editCode.text.toString()
            if (cardOrMobileNumber.isEmpty())
                requireContext().toast(getString(R.string.validate_card_number))
            else {
                /*    TransitionManager.beginDelayedTransition(
                        privilegeCard.cardMain,
                        AutoTransition()
                    )
                    privilegeCard.expandedCard.gone()

                    privilegeCard.validCoupon.visible()
                    privilegeCard.editTxt.visible()
                    binding.privilegedCard.validCoupon.background =
                        resources.getDrawable(R.drawable.tick_img)
                    binding.privilegedCard.validCoupon.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#00ADB5"))*/

                countOffers++

                //closeKeyBoard()
                if (requireContext().isNetworkAvailable()) {
                    if (isNumeric(cardOrMobileNumber)) {
                        privielgeMobileNumber = cardOrMobileNumber.trim()
                        cardNumber = getString(R.string.empty)
                    } else {
                        cardNumber = cardOrMobileNumber.trim()
                        privielgeMobileNumber = getString(R.string.empty)
                    }

                    val privilegeCardHash = PrivilegeCardHash()
                    privilegeCardHash.cardNumber = cardNumber
                    privilegeCardHash.resId = resId.toString()
                    privilegeCardHash.mobileNumber = privielgeMobileNumber
                    privilegeCardHash.selectedSeats = selectedSeatNo
                    privilegeCardHash.returnResId = returnResId
                    privilegeCardHash.isRoundtrip = isRoundTrip
                    privilegeCardHash.returnResSeatsCount = returnResSeatsCount
                    privilegeCardHash.connectingResSeatsCount = connectingResSeatsCount

                    val discountParams = DiscountParams()
                    discountParams.privilegeCardHash = privilegeCardHash

                    validateCouponCode(discountParams)
                } else
                    requireContext().noNetworkToast()

            }

        }

        privilegeCard.cancelBtn.setOnClickListener {
            uncheckPrivilegeCard()
        }

        privilegeCard.editTxt.setOnClickListener {
            TransitionManager.beginDelayedTransition(
                privilegeCard.cardMain,
                AutoTransition()
            )
            privilegeCard.expandedCard.visible()
        }

        binding.privilegedCard.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            TransitionManager.beginDelayedTransition(
                privilegeCard.cardMain,
                AutoTransition()
            )
            if (!isChecked) {
                removeAppliedCoupon(getString(R.string.privilege_card))
                binding.privilegedCard.editTxt.gone()
                binding.privilegedCard.validCoupon.gone()
                binding.privilegedCard.expandedCard.gone()
                privilegeCard.editCode.setText("")
                Timber.d("checkedCouponType $checkedCouponType")
                if (checkedCouponType != getString(R.string.vip_ticket) && checkedCouponType != getString(
                        R.string.free_ticket
                    )
                ) {
                    enablePreviousPnr()
                    if (!isIndividualDiscountApplied)
                        enableDiscountAmount()
                    enableSmartMiles()
                    enableCouponCode()
                }
            } else {
                binding.privilegedCard.expandedCard.visible()
            }
        }

    }

    private fun uncheckPrivilegeCard() {
        TransitionManager.beginDelayedTransition(
            binding.privilegedCard.cardMain,
            AutoTransition()
        )
        binding.privilegedCard.expandedCard.gone()
        binding.privilegedCard.editCode.setText("")
        binding.privilegedCard.editTxt.gone()
        binding.privilegedCard.validCoupon.gone()
        binding.privilegedCard.checkBox.setChecked(false)
        countOffers--
    }

    private fun gstDetailsChange() {
        val gstDetails = binding.gstDetails


        if (!isAgentLogin && isGstApplicable) {
            gstDetails.root.visible()
        } else {
            gstDetails.root.gone()
        }

        gstDetails.checkBox.setOnClickListener {
            if (gstDetails.editTxt.isVisible) {
                gstDetails.editCode.setText("")
                gstDetails.editTxt.gone()
                countOffers--
                gstDetails.validCoupon.gone()
            }
        }

        gstDetails.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            TransitionManager.beginDelayedTransition(
                gstDetails.cardMain,
                AutoTransition()
            )
            if (!isChecked) {
                removeAppliedCoupon(getString(R.string.gst_details))
                binding.gstDetails.editTxt.gone()
                binding.gstDetails.validCoupon.gone()
                binding.gstDetails.expandedCard.gone()
                gstDetails.editCode.setText("")
                gstDetails.editCodeSub.setText("")
            } else {
                gstDetails.expandedCard.visible()
            }
        }

        gstDetails.editCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (count > 0) {
                    gstDetails.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.tick_img,
                        0
                    )
                } else {
                    gstDetails.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    )
                }

            }
        })

        gstDetails.editCodeSub.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (count > 0) {
                    gstDetails.editCodeSub.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.tick_img,
                        0
                    )
                } else {
                    gstDetails.editCodeSub.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    )
                }

            }
        })

        gstDetails.applyBtn.setOnClickListener {
            checkedCouponType = getString(R.string.gst_details)
            val gstNo = gstDetails.editCode.text.toString()
            val companyName = gstDetails.editCodeSub.text.toString()
            if (gstNo.isEmpty())
                requireContext().toast(getString(R.string.enter_gst_number))
            else if (companyName.isEmpty())
                requireContext().toast(getString(R.string.enter_company_name))
            else {
                TransitionManager.beginDelayedTransition(
                    gstDetails.cardMain,
                    AutoTransition()
                )
                gstDetails.expandedCard.gone()
                gstDetails.validCoupon.visible()
                gstDetails.editTxt.visible()

                binding.gstDetails.validCoupon.background =
                    resources.getDrawable(R.drawable.tick_img)
                binding.gstDetails.validCoupon.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#00ADB5"))

                countOffers++

                addAppliedCoupon(binding.gstDetails.editCode.text.toString())
            }

        }

        gstDetails.cancelBtn.setOnClickListener {

            TransitionManager.beginDelayedTransition(
                gstDetails.cardMain,
                AutoTransition()
            )
            gstDetails.expandedCard.gone()
            gstDetails.editCode.setText("")
            gstDetails.editTxt.gone()
            gstDetails.validCoupon.gone()
            gstDetails.checkBox.setChecked(false)
            countOffers--


        }

        gstDetails.editTxt.setOnClickListener {
            TransitionManager.beginDelayedTransition(
                gstDetails.cardMain,
                AutoTransition()
            )
            gstDetails.expandedCard.visible()
        }


    }

    private fun smartMilesChnage() {
        val smartMiles = binding.applySmartMiles

        if (privilegeResponseModel != null) {

            privilegeResponseModel?.let {
                if (it.applySmartMiles) {
                    if (!isAgentLogin) {
                        smartMiles.root.visible()
                    } else {
                        smartMiles.root.gone()
                    }
                } else {
                    smartMiles.root.gone()
                }
            }
        } else {
            requireContext().toast(getString(R.string.server_error))
        }



        smartMiles.checkBox.setOnClickListener {
            if (smartMiles.editTxt.isVisible) {
                smartMiles.editCode.setText("")
                smartMiles.editTxt.gone()
                countOffers--
                smartMiles.validCoupon.gone()
            }
        }

        smartMiles.editCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (count > 0) {
                    smartMiles.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.tick_img,
                        0
                    )
                } else {
                    smartMiles.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    )
                }

            }
        })

        smartMiles.btnSendOtp.setOnClickListener {
            checkedCouponType = getString(R.string.apply_smart_miles)
            if (smartMiles.editCode.text!!.isEmpty()) {
                requireContext().toast(getString(R.string.enter_smart_miles_number))
            } else {
                if (requireContext().isNetworkAvailable()) {
                    getSmartMilesOtpApi()
                } else
                    requireContext().noNetworkToast()
            }
        }

        smartMiles.applyBtn.setOnClickListener {
            checkedCouponType = getString(R.string.apply_smart_miles)
            when {
                smartMiles.editCode.text!!.isEmpty() -> {
                    requireContext().toast(getString(R.string.enter_smart_miles_number))
                }
                smartMiles.editOtp.text!!.isEmpty() -> {
                    requireContext().toast(getString(R.string.enter_otp))
                }
                smartMiles.editOtp.text.toString() != smartMilesOtp -> {
                    requireContext().toast(getString(R.string.invalid_otp))
                }
                else -> {
                    /*   TransitionManager.beginDelayedTransition(
                           smartMiles.cardMain,
                           AutoTransition()
                       )
                       smartMiles.expandedCard.gone()
                       smartMiles.validCoupon.visible()
                       smartMiles.editTxt.visible()

                       binding.applySmartMiles.validCoupon.background =
                           resources.getDrawable(R.drawable.tick_img)
                       binding.applySmartMiles.validCoupon.backgroundTintList =
                           ColorStateList.valueOf(Color.parseColor("#00ADB5"))*/

                    countOffers++

                    if (requireContext().isNetworkAvailable()) {
                        val smartMilesHash = SmartMilesHash()
                        smartMilesHash.otp = smartMiles.editOtp.text.toString()
                        smartMilesHash.phoneNumber = smartMiles.editCode.text.toString()
                        smartMilesHash.otpKey = smartMilesOtpKey

                        val discountParams = DiscountParams()
                        discountParams.smartMilesHash = smartMilesHash

                        validateCouponCode(discountParams)
                    } else
                        requireContext().noNetworkToast()

                    smartMiles.editOtp.setText("")
                    smartMiles.layoutOtp.gone()
                    smartMiles.layoutButtons.gone()

                }
            }

        }

        smartMiles.cancelBtn.setOnClickListener {
            uncheckSmartMiles()
        }

        smartMiles.editTxt.setOnClickListener {
            TransitionManager.beginDelayedTransition(
                smartMiles.cardMain,
                AutoTransition()
            )
            smartMiles.expandedCard.visible()
        }

        binding.applySmartMiles.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            TransitionManager.beginDelayedTransition(
                smartMiles.cardMain,
                AutoTransition()
            )
            if (!isChecked) {
                removeAppliedCoupon(getString(R.string.apply_smart_miles))
                binding.applySmartMiles.editTxt.gone()
                binding.applySmartMiles.validCoupon.gone()
                binding.applySmartMiles.expandedCard.gone()
                binding.applySmartMiles.editCode.setText("")
                binding.applySmartMiles.editOtp.setText("")
                if (checkedCouponType != getString(R.string.vip_ticket) && checkedCouponType != getString(
                        R.string.free_ticket
                    )
                ) {
                    enablePreviousPnr()
                    if (!isIndividualDiscountApplied)
                        enableDiscountAmount()
                    enablePrivilegeCard()
                }
            } else {
                binding.applySmartMiles.expandedCard.visible()
                binding.applySmartMiles.editCode.setText("")
            }
        }
    }

    private fun uncheckSmartMiles() {
        TransitionManager.beginDelayedTransition(
            binding.applySmartMiles.cardMain,
            AutoTransition()
        )
        binding.applySmartMiles.editOtp.setText("")
        binding.applySmartMiles.layoutOtp.gone()
        binding.applySmartMiles.layoutButtons.gone()
        binding.applySmartMiles.expandedCard.gone()
        binding.applySmartMiles.editCode.setText("")
        binding.applySmartMiles.editTxt.gone()
        binding.applySmartMiles.validCoupon.gone()
        binding.applySmartMiles.checkBox.isChecked = false
        countOffers--
    }

    private fun getSmartMilesOtpApi() {
        val mobileNumber = binding.applySmartMiles.editCode.text.toString()
        val reqBody = com.bitla.ts.domain.pojo.smart_miles_otp.request.ReqBody(
            loginModelPref.api_key,
            mobileNumber,
            locale = locale
        )
        val smartMilesOtpRequest = SmartMilesOtpRequest(
            bccId.toString(), format_type,
            smart_miles_otp_method_name, reqBody
        )

        /*validateCouponViewModel.smartMilesOtpApi(
            authorization = loginModelPref.auth_token,
            apiKey = loginModelPref.api_key,
            smartMilesOtpRequest = smartMilesOtpRequest,
            apiType = smart_miles_otp_method_name
        )*/

        validateCouponViewModel.smartMilesOtpApi(
            smartMilesOtpRequest = reqBody,
            apiType = smart_miles_otp_method_name
        )
    }

    private fun discountAmountChange() {
        val discountAmount = binding.discountAmount
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if (isAgentLogin || (agentType != null && agentType == "1" || agentType == "2") || isBima || (isOwnRoute && privilegeResponseModel.isAllowedToEditFare) || (!isOwnRoute && privilegeResponseModel.isAllowedToEditFareForOtherRoute))
                discountAmount.root.gone()
            else {
                if ((privilegeResponseModel.isDiscountOnTotalAmount != null && privilegeResponseModel.isDiscountOnTotalAmount == true && privilegeResponseModel.isAllowDiscountWhileBooking && isOwnRoute)
                    || (privilegeResponseModel.isDiscountOnTotalAmount != null && privilegeResponseModel.isDiscountOnTotalAmount == true && privilegeResponseModel.isAllowDiscountWhileBookingForOtherRoute && !isOwnRoute)
                )
                    discountAmount.root.visible()
                else
                    discountAmount.root.gone()
            }
        }


        discountAmount.checkBox.setOnClickListener {
            if (discountAmount.editTxt.isVisible) {
                discountAmount.editCode.setText("")
                discountAmount.editTxt.gone()
                countOffers--
                discountAmount.validCoupon.gone()
            }
        }

        discountAmount.editCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (count > 0) {
                    discountAmount.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.tick_img,
                        0
                    )
                } else {
                    discountAmount.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    )
                }

            }
        })

        discountAmount.applyBtn.setOnClickListener {
            checkedCouponType = getString(R.string.discount_amount)
            if (discountAmount.editCode.text!!.isNotEmpty()) {
                TransitionManager.beginDelayedTransition(
                    discountAmount.cardMain,
                    AutoTransition()
                )
                discountAmount.expandedCard.gone()

                discountAmount.validCoupon.visible()
                discountAmount.editTxt.visible()
                binding.discountAmount.validCoupon.background =
                    resources.getDrawable(R.drawable.tick_img)
                binding.discountAmount.validCoupon.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#00ADB5"))
                countOffers++
                enableDisableCoupons()
            } else
                requireContext().toast(getString(R.string.enter_discount_amount))
        }

        discountAmount.cancelBtn.setOnClickListener {
            TransitionManager.beginDelayedTransition(
                discountAmount.cardMain,
                AutoTransition()
            )
            discountAmount.expandedCard.gone()
            discountAmount.editCode.setText("")
            discountAmount.editTxt.gone()
            discountAmount.validCoupon.gone()
            discountAmount.checkBox.setChecked(false)
            countOffers--


        }

        discountAmount.editTxt.setOnClickListener {
            TransitionManager.beginDelayedTransition(
                discountAmount.cardMain,
                AutoTransition()
            )
            discountAmount.expandedCard.visible()
        }

        binding.discountAmount.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            TransitionManager.beginDelayedTransition(
                discountAmount.cardMain,
                AutoTransition()
            )
            if (!isChecked) {
                removeAppliedCoupon(getString(R.string.discount_amount))
                binding.discountAmount.editTxt.gone()
                binding.discountAmount.validCoupon.gone()
                binding.discountAmount.expandedCard.gone()
                binding.discountAmount.editCode.setText("")
                enablePreviousPnr()
                enableSmartMiles()
                enablePrivilegeCard()
            } else {
                binding.discountAmount.expandedCard.visible()
            }
        }
    }

    private fun quotePreviousPnrChange() {
        val quotePreviousPnr = binding.quotePreviousPnr
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if (privilegeResponseModel.applyQuotePreviousPnrDiscount && !isAgentLogin)
                quotePreviousPnr.root.visible()
            else
                quotePreviousPnr.root.gone()
        }
        quotePreviousPnr.checkBox.setOnClickListener {
            if (quotePreviousPnr.editTxt.isVisible) {
                quotePreviousPnr.editCode.setText("")
                quotePreviousPnr.editTxt.gone()
                countOffers--
                quotePreviousPnr.validCoupon.gone()
            }
        }

        quotePreviousPnr.editCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (count > 0) {
                    quotePreviousPnr.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.tick_img,
                        0
                    )
                } else {
                    quotePreviousPnr.editCode.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    )
                }

            }
        })

        quotePreviousPnr.editCodeSub.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (count > 0) {
                    quotePreviousPnr.editCodeSub.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.tick_img,
                        0
                    )
                } else {
                    quotePreviousPnr.editCodeSub.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    )
                }

            }
        })

        quotePreviousPnr.applyBtn.setOnClickListener {
            checkedCouponType = getString(R.string.quote_previous_pnr)
            when {
                quotePreviousPnr.editCode.text!!.isEmpty() -> requireContext().toast(getString(R.string.enter_pnr_number))
                //quotePreviousPnr.editCodeSub.text!!.isEmpty() -> requireContext().toast(getString(R.string.enter_phone_number))
                else -> {
                    /* TransitionManager.beginDelayedTransition(
                         quotePreviousPnr.cardMain,
                         AutoTransition()
                     )
                     quotePreviousPnr.expandedCard.gone()

                     quotePreviousPnr.validCoupon.visible()
                     quotePreviousPnr.editTxt.visible()

                     binding.quotePreviousPnr.validCoupon.background =
                         resources.getDrawable(R.drawable.tick_img)
                     binding.quotePreviousPnr.validCoupon.backgroundTintList =
                         ColorStateList.valueOf(Color.parseColor("#00ADB5"))*/
                    countOffers++

                    //closeKeyBoard()
                    if (requireContext().isNetworkAvailable()) {
                        val previousPnrHash = PreviousPnrHash()
                        previousPnrHash.previousPnr = quotePreviousPnr.editCode.text.toString()
                        previousPnrHash.phoneNumber = quotePreviousPnr.editCodeSub.text.toString()
                        previousPnrHash.origin = sourceId
                        previousPnrHash.destination = destinationId
                        previousPnrHash.selectedSeats = selectedSeatNo?.trim()
                        previousPnrHash.resId = resId
                        previousPnrHash.totalFare = totalFare
                        previousPnrHash.bookingType = agentType
                        previousPnrHash.routeId = routeId

                        val discountParams = DiscountParams()
                        discountParams.previousPnrHash = previousPnrHash

                        validateCouponCode(discountParams)
                    } else
                        requireContext().noNetworkToast()
                }
            }

        }

        quotePreviousPnr.cancelBtn.setOnClickListener {
            uncheckPreviousPnr()
        }

        quotePreviousPnr.editTxt.setOnClickListener {
            TransitionManager.beginDelayedTransition(
                quotePreviousPnr.cardMain,
                AutoTransition()
            )
            quotePreviousPnr.expandedCard.visible()
        }

        binding.quotePreviousPnr.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            TransitionManager.beginDelayedTransition(
                quotePreviousPnr.cardMain,
                AutoTransition()
            )
            if (!isChecked) {
                removeAppliedCoupon(getString(R.string.quote_previous_pnr))
                binding.quotePreviousPnr.editTxt.gone()
                binding.quotePreviousPnr.validCoupon.gone()
                binding.quotePreviousPnr.expandedCard.gone()
                binding.quotePreviousPnr.editCode.setText("")
                binding.quotePreviousPnr.editCodeSub.setText("")
                if (checkedCouponType != getString(R.string.vip_ticket) && checkedCouponType != getString(
                        R.string.free_ticket
                    )
                ) {
                    if (!isIndividualDiscountApplied)
                        enableDiscountAmount()
                    enableSmartMiles()
                    enablePrivilegeCard()
                }
            } else {
                binding.quotePreviousPnr.expandedCard.visible()
            }
        }

    }

    private fun uncheckPreviousPnr() {
        TransitionManager.beginDelayedTransition(
            binding.quotePreviousPnr.cardMain,
            AutoTransition()
        )
        binding.quotePreviousPnr.expandedCard.gone()
        binding.quotePreviousPnr.editCode.setText("")
        binding.quotePreviousPnr.editTxt.gone()
        binding.quotePreviousPnr.validCoupon.gone()
        binding.quotePreviousPnr.checkBox.isChecked = false
        countOffers--
    }


    private fun vipTicketChange() {
        val vipTicket = binding.vipTicket
        val vipCategoryList = mutableListOf<SpinnerItems>()
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if (privilegeResponseModel.vipCategoryList != null) {
                try {
                    privilegeResponseModel.vipCategoryList.forEach {
                        val spinnerItems =
                            SpinnerItems(it.id.toString().toDouble().toInt(), it.name.toString())
                        vipCategoryList.add(spinnerItems)
                    }
                } catch (e: Exception) {
                    Timber.d("exceptionMsg ${e.message}")
                }
            }
            if (privilegeResponseModel.isVipBooking && !isAgentLogin && agentType != "1" && agentType != "2")
                vipTicket.root.visible()
            else
                vipTicket.root.gone()
        }

        if (vipCategoryList.isNotEmpty()) {
            vipCategoryId = "${vipCategoryList[0].id}:${vipCategoryList[0].value}"
            binding.vipTicket.acVip.setText(vipCategoryList[0].value)
            binding.vipTicket.acVip.setAdapter(
                ArrayAdapter<SpinnerItems>(
                    requireContext(),
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    vipCategoryList
                )
            )

            binding.vipTicket.acVip.setOnItemClickListener { adapterView, view, i, l ->
                vipCategoryId = "${vipCategoryList[i].id}:${vipCategoryList[i].value}"
            }
        }

        vipTicket.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //checkedCouponType = getString(R.string.vip_ticket)
                //addAppliedCoupon(getString(R.string.empty))
                if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
                    if (privilegeResponseModel.isVipBooking) {
                        disablePrepost()
                        disableFreeTicket()
                        disableGstAmount()
                        disableDiscountAmount()
                        disablePreviousPnr()
                        disablePrivilegeCard()
                        disableSmartMiles()
                        disableCouponCode()
                        vipTicket.expandedCard.visible()
                    }
                }
            } else {
                vipTicket.expandedCard.gone()
                removeAppliedCoupon(getString(R.string.vip_ticket))
                if (!selectedSeatDetails.any { it.isExtraSeat } && selectedExtraSeatDetails.size == 0) {
                    enableCouponCode()
                    enablePrePost()
                    enablePrivilegeCard()
                    enableSmartMiles()
                    if (!isIndividualDiscountApplied)
                        enableDiscountAmount()
                    enablePreviousPnr()
                    enableFreeTicket()
                    enableGstAmount()
                }
            }
        }

        vipTicket.applyBtn.setOnClickListener {
            checkedCouponType = getString(R.string.vip_ticket)
            addAppliedCoupon(vipCategoryId)
            vipTicket.expandedCard.gone()
        }

        vipTicket.cancelBtn.setOnClickListener {
            uncheckVipTicket()
        }
    }

    fun vipTicketFreeChange() {
        val vipTicketFree = binding.vipTicketFree

        vipTicketFree.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->

        }
    }

    private fun freeTicketChange() {
        val freeTicket = binding.freeTickets
        if (::privilegeResponseModel.isInitialized && privilegeResponseModel != null) {
            if (privilegeResponseModel.freeTicket == true)
                freeTicket.root.visible()
            else
                freeTicket.root.gone()
        }
        freeTicket.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkedCouponType = getString(R.string.free_ticket)
                addAppliedCoupon(getString(R.string.empty))
                disableCouponCode()
                disablePrepost()
                disableVipTicket()
                disableGstAmount()
                disableDiscountAmount()
                disablePreviousPnr()
                disablePrivilegeCard()
                disableSmartMiles()
            } else {
                removeAppliedCoupon(getString(R.string.free_ticket))
                enableCouponCode()
                enablePrePost()
                enablePrivilegeCard()
                enableSmartMiles()
                if (!isIndividualDiscountApplied)
                    enableDiscountAmount()
                enablePreviousPnr()
                enableVipTicket()
                enableGstAmount()
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            //finish()
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.layoutCoupons->
            {
                if (binding.layoutCouponsHidden.isVisible) {
                    androidx.transition.TransitionManager.beginDelayedTransition(
                        binding.cardCoupons,
                        androidx.transition.AutoTransition()
                    )
                    binding.layoutCouponsHidden.gone()
                    binding.imgArrowUpCoupons.setImageResource(R.drawable.ic_arrow_down)
                } else
                {
                    androidx.transition.TransitionManager.beginDelayedTransition(
                        binding.cardCoupons,
                        androidx.transition.AutoTransition()
                    )
                    binding.layoutCouponsHidden.visible()
                    binding.imgArrowUpCoupons.setImageResource(R.drawable.ic_arrow_up)
                }
            }
        }
    }

}