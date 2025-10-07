package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.eta.Eta
import com.bitla.ts.domain.pojo.eta.Request.ReqBody
import com.bitla.ts.domain.repository.EtaRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class EtaViewModel<T : Any?>(private val etaRepository: EtaRepository) :
    ViewModel() {

    companion object {
        val TAG: String = EtaViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _etaDetails = MutableLiveData<Eta>()
    val eta: LiveData<Eta>
        get() = _etaDetails

    private val _validation = MutableLiveData<Boolean>()
    val validationData: LiveData<Boolean>
        get() = _validation


    private var apiType: String? = null
    val messageSharedFlow = MutableSharedFlow<String>()


    /*  fun etaApi(
          authorization: String,
          apiKey: String,
          etaRequest: EtaRequest,
          apiType: String
      ) {

          _loadingState.postValue(LoadingState.LOADING)
          val gson = GsonBuilder().disableHtmlEscaping().create()
          val json = gson.toJson(etaRequest)
          Timber.d("tag", "Agent Recharge API: " + json.toString())
          viewModelScope.launch(Dispatchers.IO) {
              _etaDetails.postValue(
                  etaRepository.eta(
                      authorization,
                      apiKey,
                      etaRequest
                  ).body()
              )
          }
      }*/

    fun etaApi(
        etaRequest: ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(etaRequest)
        viewModelScope.launch(Dispatchers.IO) {
            etaRepository.newEta(
                etaRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _etaDetails .postValue(
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