package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.update_coach_type.UpdateCoachType
import com.bitla.ts.domain.pojo.update_coach_type.request.UpdateCoachTypeRequest
import com.bitla.ts.domain.repository.UpdateCoachTypeRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class UpdateCoachTypeViewModel<T : Any?>(private val updateCoachTypeRepository: UpdateCoachTypeRepository) : ViewModel() {

    companion object {
        val TAG: String = UpdateCoachTypeViewModel::class.java.simpleName
    }

    private val _updateCoachType = MutableLiveData<UpdateCoachType>()
    val updateCoachType: LiveData<UpdateCoachType> get() = _updateCoachType

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()


    fun updateCoachTypeApi( updateCoachTypeRequest: UpdateCoachTypeRequest) {
        _loadingState.postValue(LoadingState.LOADED)

        viewModelScope.launch(Dispatchers.IO) {

            updateCoachTypeRepository.updateCoachTypeApi(
                updateCoachTypeRequest = updateCoachTypeRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _updateCoachType.postValue(
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