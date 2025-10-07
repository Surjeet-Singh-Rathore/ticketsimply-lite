package com.bitla.ts.presentation.viewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.service_occupancy_details_popup.ServiceOccupancyDetails
import com.bitla.ts.domain.repository.ServiceOccupancyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServiceOccupancyViewModel <T : Any?>(private val serviceOccupancyRepository: ServiceOccupancyRepository) : ViewModel(){
    private val _serviceWiseOccupancyDetails = MutableLiveData<ServiceOccupancyDetails?>()
    val serviceWiseOccupancyDetails: LiveData<ServiceOccupancyDetails?>
        get() = _serviceWiseOccupancyDetails

    fun serviceOccupancyDetailsApi(
        apiKey: String,
        routeId : String,
        fromDate: String,
        toDate : String
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            _serviceWiseOccupancyDetails.postValue(
                serviceOccupancyRepository.newServiceOccupancyDetails(
                    apiKey,
                    routeId,
                    fromDate,
                    toDate
                ).body()
            )
        }
    }
}