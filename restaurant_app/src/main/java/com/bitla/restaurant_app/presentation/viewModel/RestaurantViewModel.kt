package com.bitla.restaurant_app.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.RestaurantListResponse
import com.bitla.restaurant_app.presentation.repository.RestaurantRepository
import com.bitla.restaurant_app.presentation.pojo.allotedServiceDirect.AllotedDirctRequest.AllotedDirectRequest
import com.bitla.restaurant_app.presentation.pojo.allotedServiceDirect.AllotedDirctResponse.AllotedDirectResponse
import com.bitla.restaurant_app.presentation.pojo.reports.ReportsResponse
import com.bitla.restaurant_app.presentation.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestaurantViewModel() : ViewModel() {

    private val restaurantRepository = RestaurantRepository()

    private val _restaurantListResponse = MutableLiveData<RestaurantListResponse>()
    val restaurantListResponse: LiveData<RestaurantListResponse>
        get() = _restaurantListResponse

    var restaurantId: String = ""
    var serviceId: String = "-1"
    var restaurantName: String = ""
    var serviceName: String = ""


    private val _dataAllotedServiceDirect = MutableLiveData<AllotedDirectResponse>()
    val dataAllotedServiceDirect: LiveData<AllotedDirectResponse>
        get() = _dataAllotedServiceDirect


    private val _reportsResponse = MutableLiveData<Event<ReportsResponse>>()
    val reportsResponse: LiveData<Event<ReportsResponse>>
        get() = _reportsResponse

    fun getRestaurantListApi(
        apiKey: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _restaurantListResponse.postValue(
                restaurantRepository.getRestaurantListApi(
                    apiKey
                ).body()
            )
        }
    }


    fun allotedServiceApiDirect(
        allotedRequest: AllotedDirectRequest
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _dataAllotedServiceDirect.postValue(
                restaurantRepository.newAllotedService(
                    allotedRequest
                ).body()
            )
        }
    }


    fun getReportsApi(
        apiKey: String,
        respFormat: String,
        isPdfDownload: Boolean,
        fromDate: String,
        toDate: String,
        locale: String,
        page: Int,
        perPage: Int,
        pagination: Boolean,
        restaurantId: String,
        serviceId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _reportsResponse.postValue(
                Event(
                    restaurantRepository.getReportApi(
                        apiKey, respFormat, isPdfDownload, fromDate, toDate, locale,
                        page, perPage, pagination, restaurantId, serviceId
                    ).body()!!
                )
            )
        }
    }
}