package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.get_coach_details.request.CoachDetailsRequest
import com.bitla.ts.domain.pojo.get_coach_details.response.CoachDetailsResponse
import com.bitla.ts.domain.pojo.get_coach_documents.request.CoachDocumentsRequest
import com.bitla.ts.domain.pojo.get_coach_documents.response.CoachDocumentsResponse
import com.bitla.ts.domain.repository.VehicleDetailsRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class VehicleDetailsViewModel<T : Any?>(private val vehicleDetailsRepository: VehicleDetailsRepository) :
    ViewModel() {

    companion object {
        val TAG: String = VehicleDetailsViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _getCoachDetails = MutableLiveData<CoachDetailsResponse>()
    val getCoachDetails: LiveData<CoachDetailsResponse>
        get() = _getCoachDetails

    private val _getCoachDocuments = MutableLiveData<CoachDocumentsResponse>()
    val getCoachDocuments: LiveData<CoachDocumentsResponse>
        get() = _getCoachDocuments

    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()


    fun getCoachDetailsApi(
        coachDetailsRequest: CoachDetailsRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            vehicleDetailsRepository.getCoachDetails(
                coachDetailsRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _getCoachDetails.postValue(
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


    fun getCoachDocumentsApi(
        coachDocumentsRequest: CoachDocumentsRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            vehicleDetailsRepository.getCoachDocuments(
                coachDocumentsRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _getCoachDocuments.postValue(
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