package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.move_to_extra_seat.MoveToExtraSeat
import com.bitla.ts.domain.repository.MoveQuotaBlockSeatRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MoveQuotaBlockSeatViewModel<T : Any?>(private val moveQuotaBlockSeatRepository: MoveQuotaBlockSeatRepository) :
    ViewModel() {

    companion object {
        val TAG: String = MoveQuotaBlockSeatViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState


    private val _moveQuotaSeat = MutableLiveData<MoveToExtraSeat>()
    val moveQuotaSeat: LiveData<MoveToExtraSeat>
        get() = _moveQuotaSeat


    val messageSharedFlow = MutableSharedFlow<String>()


    fun moveQuotaSeatApi(
        blockingNumber: String,
        oldSeatNumber:String,
        newSeatNumber:String,
        apiKey:String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            moveQuotaBlockSeatRepository.moveQuotaBlockSeatApi(
                blockingNumber,oldSeatNumber,newSeatNumber,apiKey
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _moveQuotaSeat.postValue(
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