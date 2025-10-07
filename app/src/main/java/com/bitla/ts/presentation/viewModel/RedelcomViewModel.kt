package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.redelcom.ResponseBodyPG
import com.bitla.ts.domain.repository.RedelcomRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class RedelcomViewModel<T : Any?>(private val redelcomRepository: RedelcomRepository) : ViewModel() {

    companion object {
        val TAG: String = RedelcomViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    private var apiType: String? = null
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _data = MutableLiveData<ResponseBodyPG>()
    val redelComData: LiveData<ResponseBodyPG>
        get() = _data

    val messageSharedFlow = MutableSharedFlow<String>()




    // domain api middle tier
    fun redelcomPgStatusApi(
        apiKey: String,
        locale: String,
        pnrNumber: String,
        terminalId: String,
    ) {
      //  _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            redelcomRepository.RedelcomPgStatus(apiKey,locale,pnrNumber,terminalId) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _data.postValue(
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