package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.BpDpService.request.BpDpServiceRequest
import com.bitla.ts.domain.pojo.BpDpService.response.BpDpServiceResponse
import com.bitla.ts.domain.pojo.account_info.request.AgentAccountInfoRequest
import com.bitla.ts.domain.pojo.account_info.response.AgentAccountInfoRespnse
import com.bitla.ts.domain.pojo.revenue_data.RevenueData
import com.bitla.ts.domain.pojo.revenue_data.ServiceWiseRevenueData
import com.bitla.ts.domain.pojo.user_list.UserListModel
import com.bitla.ts.domain.repository.AgentAccountInfoRepository
import com.bitla.ts.domain.repository.DashboardRevenueRepository
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

class DashboardRevenueViewModel<T : Any?>(private val dashboardRevenueRepository: DashboardRevenueRepository) :
    ViewModel(),
    Callback<T> {

    companion object {
        val TAG: String = DashboardRevenueViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private var apiType: String? = null


    private val _revenueData = MutableLiveData<RevenueData>()
    val revenueData: LiveData<RevenueData>
        get() = _revenueData

    private val _revenueRouteData = MutableLiveData<ServiceWiseRevenueData>()
    val revenueReouteData: LiveData<ServiceWiseRevenueData>
        get() = _revenueRouteData


    private val _revenueAgentHubData = MutableLiveData<ServiceWiseRevenueData>()
    val revenueAgentHub: LiveData<ServiceWiseRevenueData>
        get() = _revenueAgentHubData


    private val _hubList = MutableLiveData<UserListModel>()
    val hubist: LiveData<UserListModel>
        get() = _hubList

    val messageSharedFlow = MutableSharedFlow<String>()



    fun getRevenueData(
        apiKey: String,
        from: String,
        to: String,
        routeId: String,
        journeyBy: String,
        pagination: String,
        perPage: String,
        page: String,
        filter: String,
        agentId:String,
        hubId:String,
        apiType:String
    ) {
        this.apiType = apiType
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            dashboardRevenueRepository.getRevenueData(apiKey, from,to,routeId,journeyBy,pagination,page,perPage,filter,agentId,hubId).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _revenueData.postValue(
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

    fun getRevenueRouteData(
        apiKey: String,
        from: String,
        to: String,
        routeId: String,
        apiType:String,
        journeyBY:String
    ) {
        this.apiType = apiType
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            dashboardRevenueRepository.getRevenueRouteDetails(apiKey, from,to,routeId,journeyBY) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _revenueRouteData .postValue(
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



    fun getRevenueAgentHubDetails(
        apiKey: String,
        from: String,
        to: String,
        hubId: String,
        agentId: String,
        journeyBy: String,
        apiType:String
    ) {
        this.apiType = apiType
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            dashboardRevenueRepository.getRevenueAgentHubDetails(apiKey, from,to,agentId,hubId,journeyBy).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _revenueAgentHubData .postValue(
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


    fun hubListApi(
        apiKey: String,
        apiType: String
    ) {
        this.apiType = apiType
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            dashboardRevenueRepository.hubList(
                apiKey = apiKey
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _hubList .postValue(
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






    override fun onResponse(call: Call<T>, response: Response<T>) {

    }

    override fun onFailure(call: Call<T>, t: Throwable) {

    }





}