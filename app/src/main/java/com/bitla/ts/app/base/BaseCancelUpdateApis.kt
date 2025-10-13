package com.bitla.ts.app.base

import android.app.*
import android.content.*
import android.text.*
import android.view.*
import com.bitla.ts.R
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.bulk_ticket_update.request.*
import com.bitla.ts.domain.pojo.cancellation_details_model.request.*
import com.bitla.ts.domain.pojo.cancellation_details_model.request.ReqBody
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.viewModel.*
import com.google.android.material.bottomsheet.*
import isNetworkAvailable
import noNetworkToast

class BaseCancelUpdateApis {
    
    class UserListApiHelper(
        private val activity: Activity,
        private val blockViewModel: BlockViewModel<Any?>,
        private val loginModelPref: LoginModel,
        private val userTypeId: Int,
        private val locale: String,
        private val userListMethodName: String,
    ) {
        fun callUserListApi() {
            if (activity.isNetworkAvailable()) {
                
                blockViewModel.userListApi(
                    apiKey = loginModelPref.api_key,
                    cityId = "",
                    userType = userTypeId.toString(),
                    branchId = "",
                    locale = locale,
                    apiType = userListMethodName
                )
            }
            else {
                activity.noNetworkToast()
            }
        }
    }
    
    class TicketDetailsApiHelper(
        private val activity: Activity,
        private val ticketDetailsViewModel: TicketDetailsViewModel<Any?>,
        private val loginModelPref: LoginModel,
        private val locale: String,
        private val ticketDetailsMethodName: String,
    ) {
        fun callTicketDetailsApi(pnrNumber: Any) {
            if (activity.isNetworkAvailable()) {
                ticketDetailsViewModel.ticketDetailsApi(
                    loginModelPref.api_key,
                    ticketNumber = pnrNumber.toString().substringBefore(" "),
                    jsonFormat = true,
                    isQrScan = false,
                    locale = locale,
                    apiType = ticketDetailsMethodName
                )
            } else {
                activity.noNetworkToast()
            }
        }
    }
    
    class CancellationDetailsApiHelper(
        private val activity: Activity,
        private val cancelTicketViewModel: CancelTicketViewModel<Any?>,
        private val apiKey: String,
        private val locale: String,
        private val operatorApiKey: String,
        private val selectedCancellationType: String,
        private val ticketCancellationPercentage: String,
        private val jsonFormat: String,
        private val cancellationDetailsTicketMethodName: String,
        private val selectedSeatNumber: StringBuilder,
        private val isZeroPercentCancellationCheck: Boolean,
    ) {
        
        fun callCancellationDetailsApi(pnrNumber: Any, isBima: Boolean) {
            if (activity.isNetworkAvailable()) {
                
                val reqBody = createRequestBody(pnrNumber, isBima)
                
                cancelTicketViewModel.getCancellationDetailsApi(
                    cancellationDetailsRequest = reqBody,
                    apiType = cancellationDetailsTicketMethodName
                )
                
            } else {
                activity.noNetworkToast()
            }
        }
        
        private fun createRequestBody(pnrNumber: Any, isBima: Boolean): ReqBody {
            return ReqBody(
                apiKey = apiKey,
                isFromBusOptApp = true,
                locale = locale,
                operatorApiKey = operatorApiKey,
                cancelType = selectedCancellationType,
                ticketCancellationPercentageP = ticketCancellationPercentage,
                passengerDetails = "",
                pnrNumber = pnrNumber.toString(),
                responseFormat = jsonFormat,
                seatNumbers = selectedSeatNumber.toString(),
                zeroPercent = isZeroPercentCancellationCheck,
                isBimaTicket = isBima,
                json_format = jsonFormat
            )
        }
    }
    
    class ZeroCancellationDetailsApiHelper(
        private val activity: Activity,
        private val cancelTicketViewModel: CancelTicketViewModel<Any?>,
        private val apiKey: String,
        private val locale: String,
        private val operatorApiKey: String,
        private val selectedCancellationType: String,
        private val jsonFormat: String,
        private val cancellationDetailsTicketMethodName: String,
        private val selectedSeatNumber: String,
        private val isZeroPercentCancellationCheck: Boolean,
    ) {
        
        fun callZeroCancellationDetailsApi(pnrNumber: String, isBima: Boolean) {
            if (activity.isNetworkAvailable()) {
                
                val reqBody = createRequestBody(pnrNumber, isBima)
                
                cancelTicketViewModel.getZeroCancellationDetailsApi(
                    cancellationDetailsRequest = reqBody,
                    apiType = cancellationDetailsTicketMethodName
                )
                
            } else {
                activity.noNetworkToast()
            }
        }
        
        private fun createRequestBody(pnrNumber: String, isBima: Boolean): ReqBody2 {
            return ReqBody2(
                apiKey = apiKey,
                cancelType = selectedCancellationType,
                isFromBusOptApp = true,
                locale = locale,
                operatorApiKey = operatorApiKey,
                passengerDetails = "",
                pnrNumber = pnrNumber,
                responseFormat = jsonFormat,
                seatNumbers = selectedSeatNumber,
                zeroPercent = isZeroPercentCancellationCheck,
                isBimaTicket = isBima,
                json_format = jsonFormat
            )
        }
    }
    
    class CancelPartialTicketApiHelper(
        private val activity: Activity,
        private val cancelTicketViewModel: CancelTicketViewModel<Any?>,
        private val apiKey: String,
        private val locale: String,
        private val operatorApiKey: String,
        private val selectedCancellationType: String,
        private val jsonFormat: String,
        private val selectedSeatNumber: String,
        private val ticketCancellationPercentage: String,
        private val ticketNumber: String,
        private val travelDate: String,
        private val isZeroPercentCancellationCheck: Boolean,
        private val isCanCancelTicketForUser: Boolean,
        private val isOnbehalfOnlineAgentFlag: Boolean,
        private val cancelOnBehalOf: Int?,
        private val isBimaTicket: Boolean,
        private val sendSms: Boolean,
        private val cancellationDetailsTicketMethodName: String,
        private val authPin: String,
        private val remarks: String
    ) {
        
        fun callCancelPartialTicketApi() {
            
            if (activity.isNetworkAvailable()) {
                val reqBody = createRequestBody()
                
                cancelTicketViewModel.getCancelPartialTicketApi(
                    reqBody,
                    cancellationDetailsTicketMethodName
                )
                
            } else {
                activity.noNetworkToast()
            }
        }
        
        private fun createRequestBody(): com.bitla.ts.domain.pojo.cancel_partial_ticket_model.request.ReqBody {
            return com.bitla.ts.domain.pojo.cancel_partial_ticket_model.request.ReqBody(
                apiKey = apiKey,
                cancelType = selectedCancellationType,
                isFromBusOptApp = true,
                locale = locale,
                operatorApiKey = operatorApiKey,
                passengerDetails = "",
                responseFormat = jsonFormat,
                seatNumbers = selectedSeatNumber,
                ticketCancellationPercentageP = ticketCancellationPercentage,
                ticketNumber = ticketNumber,
                travelDate = travelDate,
                zeroPercent = isZeroPercentCancellationCheck,
                isOnbehalfBookedUser = isCanCancelTicketForUser,
                onbehalf_online_agent_flag = isOnbehalfOnlineAgentFlag,
                onBehalfUserId = cancelOnBehalOf,
                json_format = jsonFormat,
                isBimaTicket = isBimaTicket,
                is_sms_send = sendSms,
                authPin = authPin,
                remarkCancelTicket = remarks
            )
        }
    }
    
    class ConfirmOtpCancelPartialTicketApiHelper(
        private val activity: Activity,
        private val cancelTicketViewModel: CancelTicketViewModel<Any?>,
        private val apiKey: String,
        private val cancelOptKey: String,
        private val cancelOtp: String,
        private val selectedCancellationType: String,
        private val locale: String,
        private val operatorApiKey: String,
        private val jsonFormat: String,
        private val ticketCancellationPercentage: String,
        private val ticketNumber: String,
        private val travelDate: String,
        private val isZeroPercentCancellationCheck: Boolean,
        private val isCanCancelTicketForUser: Boolean,
        private val isOnbehalfOnlineAgentFlag: Boolean,
        private val cancelOnBehalOf: Int?,
        private val isBimaTicket: Boolean,
        private val confirmOtpCancelPartialTicketMethodName: String,
    ) {
        
        fun callConfirmOtpCancelPartialTicketApi(seatNoSelected: String) {
            if (activity.isNetworkAvailable()) {
                val reqBody = createRequestBody(seatNoSelected)
                
                cancelTicketViewModel.getConfirmOtpCancelPartialTicketApi(
                    reqBody,
                    confirmOtpCancelPartialTicketMethodName
                )
            } else {
                activity.noNetworkToast()
            }
        }
        
        private fun createRequestBody(seatNoSelected: String): com.bitla.ts.domain.pojo.confirm_otp_cancel_partial_ticket_model.request.ReqBody {
            return com.bitla.ts.domain.pojo.confirm_otp_cancel_partial_ticket_model.request.ReqBody(
                apiKey = apiKey,
                key = cancelOptKey,
                otp = cancelOtp,
                cancelType = selectedCancellationType,
                isFromBusOptApp = true,
                locale = locale,
                operatorApiKey = operatorApiKey,
                passengerDetails = "",
                responseFormat = jsonFormat,
                seatNumbers = seatNoSelected,
                ticketCancellationPercentageP = ticketCancellationPercentage,
                ticketNumber = ticketNumber,
                travelDate = travelDate,
                zeroPercent = isZeroPercentCancellationCheck,
                isOnbehalfBookedUser = isCanCancelTicketForUser,
                onbehalf_online_agent_flag = isOnbehalfOnlineAgentFlag,
                onBehalfUserId = cancelOnBehalOf,
                isBimaTicket = isBimaTicket,
                json_format = jsonFormat
            )
        }
    }
    
    class BulkTicketUpdateApiHelper(
        private val activity: Activity,
        private val cancelTicketViewModel: CancelTicketViewModel<Any?>,
        private val apiKey: String,
        private val emailId: String?,
        private val ticketNumber: String,
        private val jsonFormat: String,
        private val updateBulkDataList: MutableList<UpdateData>,
        private val passBoardingAt: String?,
        private val passDroppingAt: String?,
        private val locale: String,
        private val remarks: String,
        private val isBimaTicket: Boolean,
        private val bulkTicketUpdateMethodName: String
    ) {
        
        fun callBulkTicketUpdateApi() {
            if (activity.isNetworkAvailable()) {
                val reqBody = createRequestBody()
                
                cancelTicketViewModel.getBulkTicketUpdateApi(
                    bulkTicketUpdateRequestModel = reqBody,
                    apiType = bulkTicketUpdateMethodName
                )
            } else {
                activity.noNetworkToast()
            }
        }
        
        private fun createRequestBody(): com.bitla.ts.domain.pojo.bulk_ticket_update.request.ReqBody {
            return com.bitla.ts.domain.pojo.bulk_ticket_update.request.ReqBody(
                alternateNumber = "",
                apiKey = apiKey,
                email = emailId.toString(),
                isNotifyPassenger = true,
                pnrNumber = ticketNumber,
                primary = "",
                json_format = jsonFormat,
                updateData = updateBulkDataList,
                boardingAt = passBoardingAt,
                dropOff = passDroppingAt,
                locale = locale,
                remarks = remarks,
                isBimaTicket = isBimaTicket
            )
        }
    }
    
    class ServiceApiHelper(
        private val activity: Activity,
        private val sharedViewModel: SharedViewModel<Any?>,
        private val loginModelPref: LoginModel,
        private val reservationId: String?,
        private val sourceId: String,
        private val destinationId: String,
        private val operatorApiKey: String,
        private val locale: String?,
        private val serviceDetailsMethod: String
    ) {
        
        fun callServiceApi() {
            if (activity.isNetworkAvailable()) {
                sharedViewModel.getServiceDetails(
                    reservationId = reservationId.toString(),
                    apiKey = loginModelPref.api_key,
                    originId = sourceId,
                    destinationId = destinationId,
                    operatorApiKey = operatorApiKey,
                    locale = locale ?: "en",
                    apiType = serviceDetailsMethod,
                    excludePassengerDetails = false
                )
            } else {
                activity.noNetworkToast()
            }
        }
    }
    
    companion object  {
        
        fun updateTwoButtonDialog(
            context: Context,
            title: String,
            message: String,
            messageTextColor: Int,
            buttonLeftText: String,
            buttonRightText: String,
            bottomSheetDialog: BottomSheetDialog,
            dialogButtonTagListener: DialogButtonTagListener,
            ) {
            
            val builder = AlertDialog.Builder(context).create()
            val binding: DialogTwoButtonsBinding =
                DialogTwoButtonsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            
            binding.apply {
                tvTitle.text = title
                tvContent.setTextColor(messageTextColor)
                tvContent.text = message
                btnLeft.text = buttonLeftText
                btnRight.text = buttonRightText
                
                btnLeft.setOnClickListener {
                    builder.cancel()
                    dialogButtonTagListener.onLeftButtonClick(binding.btnLeft)
                }
                
                btnRight.setOnClickListener {
                    dialogButtonTagListener.onRightButtonClick(binding.btnRight)
                    
                    bottomSheetDialog.dismiss()
                    builder.cancel()
                }
            }
            
            builder.setView(binding.root)
            builder.show()
        }
        
        fun showCountryPickerBottomsheet(
            context: Context,
            countriesList:
            List<Countries>,
            onItemClickListener: DialogAnyClickListener,
        ) : BottomSheetDialog {
            
            val countryPickerDialog = BottomSheetDialog(context, R.style.DialogStyle)
            countryPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            countryPickerDialog.setCancelable(true)
            
            val binding = BottomsheetCountryCodeBinding.inflate(LayoutInflater.from(context))
            countryPickerDialog.setContentView(binding.root)
            
            
            val adapter = CountryCodeAdapter(
                context,
                onItemClickListener,
                countriesList
            )
            binding.countryRV.adapter = adapter
            
            
            binding.searchIV.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Not used in this example
                }
                
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Not used in this example
                }
                
                override fun afterTextChanged(s: Editable?) {
                    val searchText = s.toString()
                    adapter.filter(searchText)
                }
            })
            
            
            countryPickerDialog.show()
            return countryPickerDialog
        }
    }
}