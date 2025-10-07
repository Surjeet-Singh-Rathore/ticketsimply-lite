package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.service_stages.ServiceStageResponseModel
import com.bitla.ts.domain.repository.ServiceStageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServiceStageViewModel<T : Any?>(private val serviceStageRepository: ServiceStageRepository) :
    ViewModel() {
    private val _stageDetails = MutableLiveData<ServiceStageResponseModel>()
    val stageDetails: LiveData<ServiceStageResponseModel>
        get() = _stageDetails

    fun serviceStageDetailsApi(
        resId: String,
        apiKey: String,
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            _stageDetails.postValue(
                serviceStageRepository.newLatLongApi(
                    resId, apiKey
                ).body()
            )
        }
    }
}