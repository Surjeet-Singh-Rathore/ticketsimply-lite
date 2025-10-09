package com.bitla.restaurant_app.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.restaurant_app.presentation.pojo.LoginModel
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.MealCouponDetailsResponse
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.MealCouponStatusResponse
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.ReqBody
import com.bitla.restaurant_app.presentation.repository.MainRepository
import com.bitla.restaurant_app.presentation.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel() : ViewModel() {

    private val mainRepository = MainRepository()
    private val _mealCouponDetailsResponse = MutableLiveData<Event<MealCouponDetailsResponse>>()
    val mealCouponDetailsResponse: LiveData<Event<MealCouponDetailsResponse>>
        get() = _mealCouponDetailsResponse

    private val _mealCouponStatusResponse = MutableLiveData<Event<MealCouponStatusResponse>>()
    val mealCouponStatusResponse: LiveData<Event<MealCouponStatusResponse>>
        get() = _mealCouponStatusResponse

    private val _logoutUserResponse = MutableLiveData<LoginModel>()
    val logoutUserResponse: LiveData<LoginModel>
        get() = _logoutUserResponse

    fun getMealCouponDetailsApi(
        apiKey: String,
        qrValue: String,
        couponCode: String,
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            _mealCouponDetailsResponse.postValue(
                Event(
                    mainRepository.getMealCouponDetailsApi(
                        apiKey,
                        qrValue,
                        couponCode
                    ).body()!!
                )
            )
        }
    }

    fun getMealCouponStatus(updateMealCouponStatusRequest: ReqBody, apiType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _mealCouponStatusResponse.postValue(
                Event(
                    mainRepository.getMealCouponStatus(updateMealCouponStatusRequest).body()!!
                )
            )
        }
    }

    fun logoutApi(
        apiKey: String,
        deviceId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _logoutUserResponse.postValue(
                mainRepository.logoutApi(apiKey, deviceId).body()
            )
        }
    }
}