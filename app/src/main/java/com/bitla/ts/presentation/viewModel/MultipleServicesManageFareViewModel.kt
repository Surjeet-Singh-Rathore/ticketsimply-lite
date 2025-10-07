package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.*
import com.bitla.ts.domain.pojo.available_routes.AvailableRoutesModel
import com.bitla.ts.domain.pojo.multiple_services_manage_fare.request.ReqBody
import com.bitla.ts.domain.pojo.multiple_services_manage_fare.response.Response
import com.bitla.ts.domain.pojo.seat_types.SeatTypesResponse
import com.bitla.ts.domain.repository.MultipleServicesManageFareRepository
import kotlinx.coroutines.*

class MultipleServicesManageFareViewModel(private val multipleServicesManageFareRepository: MultipleServicesManageFareRepository) :
    ViewModel() {

    private val _availableServiceList = MutableLiveData<AvailableRoutesModel>()
    val availableServiceList: LiveData<AvailableRoutesModel>
        get() = _availableServiceList

    private val _seatTypesData = MutableLiveData<SeatTypesResponse>()
    val seatTypesData: LiveData<SeatTypesResponse>
        get() = _seatTypesData

    private val _multipleServicesManageFares = MutableLiveData<Response>()
    val multipleServicesManageFares: LiveData<Response>
        get() = _multipleServicesManageFares


    fun getAvailableServiceList(
        originId: String,
        destinationId: String,
        travelDate: String,
        apiKey: String,
        pagination: Boolean,
        page: Int?,
        perPage: Int?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _availableServiceList.postValue(
                multipleServicesManageFareRepository.availableServiceList(
                    originId,
                    destinationId,
                    travelDate,
                    apiKey,
                    pagination,
                    page,
                    perPage
                ).body()
            )
        }
    }

    fun getSeatTypes(
        apiKey: String,
        routeIds: String,
        originId: String?,
        destinationId: String?,
        travelDate: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _seatTypesData.postValue(
                multipleServicesManageFareRepository.getSeatTypes(
                    apiKey,
                    routeIds,
                    originId,
                    destinationId,
                    travelDate
                ).body()
            )
        }
    }

    fun multipleServicesManageFares(
        reqBody: ReqBody
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _multipleServicesManageFares.postValue(
                multipleServicesManageFareRepository.multipleServicesManageFares(
                    reqBody
                ).body()
            )
        }
    }
}