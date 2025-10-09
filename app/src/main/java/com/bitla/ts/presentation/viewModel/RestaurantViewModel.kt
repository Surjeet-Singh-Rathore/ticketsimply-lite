package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.mealCoupon.RestaurantListResponse
import com.bitla.ts.domain.pojo.reports.ReportsResponse
import com.bitla.ts.domain.repository.RestaurantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestaurantViewModel(private val restaurantRepository: RestaurantRepository) : ViewModel() {



    private val _restaurantListResponse = MutableLiveData<RestaurantListResponse>()
    val restaurantListResponse: LiveData<RestaurantListResponse>
        get() = _restaurantListResponse

    var restaurantId: String = ""
    var serviceId: String = "-1"
    var restaurantName: String = ""
    var serviceName: String = ""



    private val _reportsResponse = MutableLiveData<ReportsResponse>()
    val reportsResponse: LiveData<ReportsResponse>
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
                    restaurantRepository.getReportApi(
                        apiKey, respFormat, isPdfDownload, fromDate, toDate, locale,
                        page, perPage, pagination, restaurantId, serviceId
                    ).body()!!
                )

        }
    }
}