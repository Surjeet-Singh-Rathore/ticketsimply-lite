package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.repository.PrivilegeRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrivilegeDetailsViewModel(private val privilegeRepository: PrivilegeRepository) :
    BaseViewModel() {

    private var apiType: String? = null

    companion object {
        val TAG: String = PrivilegeDetailsViewModel::class.java.simpleName
    }

    private val _privilegeResponseModel = MutableLiveData<PrivilegeResponseModel>()
    val privilegeResponseModel: LiveData<PrivilegeResponseModel>
        get() = _privilegeResponseModel

    val messageSharedFlow = MutableSharedFlow<String>()


    private val _isDashboardDefaultTab = MutableLiveData(false)
    val isDashboardDefaultTab: LiveData<Boolean>
        get() = _isDashboardDefaultTab

    private val _availableRouteCount = MutableLiveData(0)
    val availableRouteCount: LiveData<Int>
        get() = _availableRouteCount


    fun getPrivilegeDetailsApi(
        apiKey: String,
        apiType: String,
        respFormat: String = "",
        locale: String = "",
    ) {


        viewModelScope.launch(Dispatchers.IO) {

            privilegeRepository.getNewPrivilegeDetails(
                apiKey,
                respFormat,
                locale
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _privilegeResponseModel.postValue(it.data)
                    }

                    is NetworkProcess.Failure -> {

                        messageSharedFlow.emit(it.message)

                    }
                }
            }
//            _privilegeResponseModel.postValue(
//                privilegeRepository.getNewPrivilegeDetails(
//                    apiKey,
//                    respFormat,
//                    locale
//                ).body()
//            )
        }
    }

    fun setDashboardDefaultTab(isDefault: Boolean) {
        _isDashboardDefaultTab.postValue(isDefault)
    }

    fun setAvailableRoutesCounts(count: Int) {
        _availableRouteCount.postValue(count)
    }


}