package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.starred_reports.StarredReportsResponse
import com.bitla.ts.domain.repository.StarredReportsRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class StarredReportsViewModel<T : Any?>(private val starredReportsRepository: StarredReportsRepository) :
    ViewModel() {

    companion object {
        val TAG: String = StarredReportsViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _starredReportsDetails = MutableLiveData<StarredReportsResponse>()
    val starredReport: LiveData<StarredReportsResponse>
        get() = _starredReportsDetails

    private val _validation = MutableLiveData<Boolean>()
    val validationData: LiveData<Boolean>
        get() = _validation


    private var apiType: String? = null
    val messageSharedFlow = MutableSharedFlow<String>()

    var privilegesLiveData = MutableLiveData<PrivilegeResponseModel?>()

    fun updatePrivileges(privileges: PrivilegeResponseModel?) {
        privilegesLiveData.value = privileges
    }

    /*fun starredReportsApi(
        authorization: String,
        apiKey: String,
        starredReportsRequest: StarredReportsRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(starredReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            _starredReportsDetails.postValue(
                starredReportsRepository.starredReports(
                    authorization,
                    apiKey,
                    starredReportsRequest
                ).body()
            )
        }
    }*/

    fun starredReportsApi(
        apiKey: String,
        recentData : Boolean,
        locale: String,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
       // val json = gson.toJson(starredReportsRequest)

        //
        viewModelScope.launch(Dispatchers.IO) {
            starredReportsRepository.newStarredReports(
                apiKey,
                recentData,locale
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _starredReportsDetails .postValue(
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