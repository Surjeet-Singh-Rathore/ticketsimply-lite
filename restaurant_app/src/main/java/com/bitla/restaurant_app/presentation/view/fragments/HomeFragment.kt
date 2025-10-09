package com.bitla.restaurant_app.presentation.view.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bitla.restaurant_app.R
import com.bitla.restaurant_app.databinding.CouponErrorBottomSheetBinding
import com.bitla.restaurant_app.databinding.CouponRedeemedDialogBinding
import com.bitla.restaurant_app.databinding.CouponVerifiedBottomSheetBinding
import com.bitla.restaurant_app.databinding.EnterCouponCodeBottomSheetBinding
import com.bitla.restaurant_app.databinding.FragmentHomeBinding
import com.bitla.restaurant_app.databinding.QrErrorBinding
import com.bitla.restaurant_app.presentation.pojo.LoginModel
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.ReqBody
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.SeatDetails
import com.bitla.restaurant_app.presentation.utils.PreferenceUtils
import com.bitla.restaurant_app.presentation.utils.isNetworkAvailable
import com.bitla.restaurant_app.presentation.utils.toast
import com.bitla.restaurant_app.presentation.view.MainActivity
import com.bitla.restaurant_app.presentation.viewModel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    private lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>

    private val appViewModel by viewModels<MainViewModel>()
    private var currentUser: LoginModel? = null

    private var seatDetails: SeatDetails? = null

    private var isCouponCode: Boolean = false
    private var isCouponAvailed: Boolean = false

    private var apiKey = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

        (activity as MainActivity).setToolBarTitle()
        (activity as MainActivity).showBottomBar()

        binding?.enterCouponCode?.setOnClickListener {
            showEnterCouponCodeBottomSheet()
        }

        binding?.scanQrCode?.setOnClickListener {
            openScanner()
        }
    }

    private fun initUI() {

        setResultLauncher()
        getCurrentUserData()

        apiKey = currentUser?.api_key ?: ""

        setObserver()
    }


    private fun getCurrentUserData() {
        currentUser = PreferenceUtils.getLogin()
    }

    private fun setObserver() {

        appViewModel.mealCouponDetailsResponse.observe(viewLifecycleOwner, Observer {

            it?.getContentIfNotHandled()?.let {
                if (it != null) {
                    seatDetails = it.seatDetails?.get(0)
                    when (it.code) {
                        200 -> showConfirmationBottomSheet()
                        400 -> showCouponErrorBottomSheet(it.result?.message)
                        412 -> requireContext().toast(it.result?.message?:getString(R.string.empty_coupon_code_msg))
                        401 -> (activity as MainActivity).unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}"
                        )

                        else -> requireContext().toast(getString(R.string.server_error))
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }
            }
        })

        appViewModel.mealCouponStatusResponse.observe(viewLifecycleOwner, Observer {

            it?.getContentIfNotHandled()?.let {
                if (it != null) {
                    if (it.status == 200) {
                        showCouponRedeemedDialog()
                    } else if (it.code == 400) {
                        isCouponAvailed = true
                        showCouponErrorBottomSheet()
                    } else {
                        requireContext().toast(getString(R.string.server_error))
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }
            }
        })
    }


    private fun setResultLauncher() {
        barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
            if (result.contents == null) {
                showQrErrorBottomSheet()
            } else {
                checkCouponValidity(result.contents)
            }
        }
    }

    private fun checkCouponValidity(pnrOrCoupon: String) {
        if (requireContext().isNetworkAvailable()) {
            if (isCouponCode) {
                appViewModel.getMealCouponDetailsApi(apiKey, "", pnrOrCoupon)
                isCouponCode = false
            } else {
                appViewModel.getMealCouponDetailsApi(apiKey, pnrOrCoupon, "")
            }
        } else {
            requireContext().toast(getString(R.string.network_not_available))
        }
    }

    private fun updateMealCouponStatus(couponCode: String) {
        if (requireContext().isNetworkAvailable()) {
            val reqBody: ReqBody = ReqBody(apiKey, "", "", couponCode)
//            if (isCouponCode) {
//                reqBody = ReqBody(apiKey, "", "", couponCode)
//                isCouponCode = false
//            } else {
//                reqBody = ReqBody(
//                    apiKey,
//                    seatDetails?.seatNumber ?: "",
//                    seatDetails?.pnrNumber ?: "",
//                    couponCode
//                )
//            }
            appViewModel.getMealCouponStatus(reqBody, "")
        } else {
            requireContext().toast(getString(R.string.network_not_available))
        }
    }

    private fun openScanner() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt(getString(R.string.scanner_msg))
        options.setCameraId(0) // Use a specific camera of the device
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        barcodeLauncher.launch(options)
    }


    private fun showQrErrorBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = QrErrorBinding.inflate(layoutInflater)

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()

        bottomSheetBinding.enterCouponCodeBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
            showEnterCouponCodeBottomSheet()
        }
    }

    private fun showEnterCouponCodeBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = EnterCouponCodeBottomSheetBinding.inflate(layoutInflater)

        bottomSheetBinding.cancelBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetBinding.applyCouponBtn.setOnClickListener {
            bottomSheetDialog.dismiss()

            isCouponCode = true

            val couponCode = bottomSheetBinding.tvEnterCoupon.text.toString()
            checkCouponValidity(couponCode)
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    private fun showConfirmationBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = CouponVerifiedBottomSheetBinding.inflate(layoutInflater)

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()

        bottomSheetBinding.apply {
            tvPassengerName.text = seatDetails?.passengerName
            tvServiceName.text = seatDetails?.serviceName
            tvSeatNumber.text = seatDetails?.seatNumber
            tvCouponCode.text = seatDetails?.couponCode
        }

        bottomSheetBinding.redeemCouponBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
            updateMealCouponStatus(seatDetails?.couponCode!!)
        }
    }

    private fun showCouponRedeemedDialog() {
        val dialogBinding = CouponRedeemedDialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
        val alertDialog = dialogBuilder.create()

        dialogBinding.redeemMessage.text =
            "Coupon ${seatDetails?.couponCode} for Seat No. ${seatDetails?.seatNumber} in service ${seatDetails?.serviceName} has been redeemed."

        alertDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({

            if (isAdded && alertDialog.isShowing) {
                alertDialog.dismiss()
            }
        }, 3000)
    }

    private fun showCouponErrorBottomSheet(msg:String?=null) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = CouponErrorBottomSheetBinding.inflate(layoutInflater)

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()

        bottomSheetBinding.closeBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        if (isCouponAvailed) {
            bottomSheetBinding.tvErrorMessage.text = getString(R.string.coupon_already_avalied)
            isCouponAvailed = false
        }
        if (msg!=null && msg.isNotEmpty()){
            bottomSheetBinding.tvErrorMessage.text = msg
        }
    }
}