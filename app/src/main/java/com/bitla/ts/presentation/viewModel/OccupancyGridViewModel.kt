package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitla.ts.data.activate_deactivate_service
import com.bitla.ts.data.active_inactive_services
import com.bitla.ts.data.occupancy_datewise
import com.bitla.ts.domain.pojo.activate_deactivate_service.request.ActivateDeactivateServiceRequest
import com.bitla.ts.domain.pojo.activate_deactivate_service.response.ActivateDeactivateServiceResponse
import com.bitla.ts.domain.pojo.active_inactive_services.request.ActiveInactiveServicesRequest
import com.bitla.ts.domain.pojo.active_inactive_services.response.ActiveInactiveServicesResponse
import com.bitla.ts.domain.pojo.occupancy_datewise.request.OccupancyDateWiseRequest
import com.bitla.ts.domain.pojo.occupancy_datewise.response.OccupancyDateWiseResponse
import com.bitla.ts.domain.repository.OccupancyGridRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.getRetrofitErrorMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class OccupancyGridViewModel<T : Any?>(private val occupancyGridRepository: OccupancyGridRepository) :
    BaseViewModel(), Callback<T> {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _occupancyDateWiseResponse = MutableLiveData<OccupancyDateWiseResponse?>()
    val occupancyDateWiseResponse: LiveData<OccupancyDateWiseResponse?>
        get() = _occupancyDateWiseResponse

    private val _activeInactiveServices = MutableLiveData<ActiveInactiveServicesResponse?>()
    val activeInactiveServices: LiveData<ActiveInactiveServicesResponse?>
        get() = _activeInactiveServices

    private val _activateDeactivateService = MutableLiveData<ActivateDeactivateServiceResponse?>()
    val activateDeactivateService: LiveData<ActivateDeactivateServiceResponse??>
        get() = _activateDeactivateService


    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()


    override fun onResponse(call: Call<T>, response: Response<T>) {
        try {
            if (response.isSuccessful) {
                when (apiType) {
                    occupancy_datewise -> _occupancyDateWiseResponse.postValue(response.body() as OccupancyDateWiseResponse)
                    active_inactive_services -> _activeInactiveServices.postValue(response.body() as ActiveInactiveServicesResponse)
                    activate_deactivate_service -> _activateDeactivateService.postValue(response.body() as ActivateDeactivateServiceResponse)
                }
                _loadingState.postValue(LoadingState.LOADED)
            } else {
                val message = getRetrofitErrorMsg(response.errorBody())
                _loadingState.postValue(LoadingState.error(message))
            }
        } catch (e: Exception) {
            _loadingState.postValue(LoadingState.error(e.message))
            Timber.d("ExceptionMsg ${e.message}")
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        _loadingState.postValue(LoadingState.error(t.message))
    }

    fun occupancyDateWiseApi(occupancyDateWiseRequest: OccupancyDateWiseRequest, apiType: String) {
        this.apiType = apiType
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            occupancyGridRepository.getOccupancyDateWise(
                occupancyDateWiseRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _occupancyDateWiseResponse.postValue(
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

    fun activateDeactivateServiceApi(
        apikey: String?,
        activateDeactivateServiceRequest: ActivateDeactivateServiceRequest,
        apiType: String
    ) {
        this.apiType = apiType
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            occupancyGridRepository.activateDeactivateServices(
                apikey = apikey,
                activateDeactivateServiceRequest = activateDeactivateServiceRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _activateDeactivateService.postValue(
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

    fun activeInactiveServicesApi(
        activeInactiveServicesRequest: ActiveInactiveServicesRequest,
        apiType: String
    ) {
        this.apiType = apiType
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _activeInactiveServices.postValue(
                occupancyGridRepository.getActiveInactiveServices(
                    activeInactiveServicesRequest
                ).body()
            )
        }
    }
}