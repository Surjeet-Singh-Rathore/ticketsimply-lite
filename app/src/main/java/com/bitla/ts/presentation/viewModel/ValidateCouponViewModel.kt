package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.coupon.CouponResponse
import com.bitla.ts.domain.pojo.smart_miles_otp.SmartMilesOtp
import com.bitla.ts.domain.pojo.smart_miles_otp.request.ReqBody
import com.bitla.ts.domain.repository.CouponRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class ValidateCouponViewModel<T : Any?>(private val couponRepository: CouponRepository) :
    ViewModel() {

    companion object {
        val TAG: String = ValidateCouponViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _couponDetails = MutableLiveData<CouponResponse>()
    val couponDetails: LiveData<CouponResponse>
        get() = _couponDetails

    private val _smartMilesOtp = MutableLiveData<SmartMilesOtp>()
    val smartMilesOtp: LiveData<SmartMilesOtp>
        get() = _smartMilesOtp

    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()


    /*fun validateCouponApi(
        authorization: String,
        apiKey: String,
        couponRequest: CouponRequest,
        apiType: String
    ) {

      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _couponDetails.postValue(
                couponRepository.validateCoupon(
                    authorization,
                    apiKey,
                    couponRequest = couponRequest
                ).body()
            )
        }
    }*/

    fun validateCouponApi(
        couponRequest: com.bitla.ts.domain.pojo.coupon.request.ReqBody,
        apiType: String
    ) {


        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            couponRepository.newValidateCoupon(
                couponRequest = couponRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _couponDetails.postValue(
                            it.data
                        )
                    }

                    is NetworkProcess.Failure -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        messageSharedFlow.emit(it.message)
                    }
                }
            }
        }
    }

    /*fun smartMilesOtpApi(
        authorization: String,
        apiKey: String,
        smartMilesOtpRequest: SmartMilesOtpRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _smartMilesOtp.postValue(
                couponRepository.smartMilesOtp(
                    authorization,
                    apiKey,
                    smartMilesOtpRequest = smartMilesOtpRequest
                ).body()
            )
        }
    } */

    fun smartMilesOtpApi(
        smartMilesOtpRequest: ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            couponRepository.newSmartMilesOtp(
                smartMilesOtpRequest = smartMilesOtpRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _smartMilesOtp.postValue(
                            it.data
                        )
                    }

                    is NetworkProcess.Failure -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        messageSharedFlow.emit(it.message)
                    }
                }
            }

        }
    }
}