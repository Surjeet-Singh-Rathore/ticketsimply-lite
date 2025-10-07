package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.BpDpService.request.BpDpServiceRequest
import com.bitla.ts.domain.pojo.BpDpService.response.BpDpServiceResponse
import com.bitla.ts.domain.pojo.account_info.request.AgentAccountInfoRequest
import com.bitla.ts.domain.pojo.account_info.response.AgentAccountInfoRespnse
import com.bitla.ts.domain.repository.AgentAccountInfoRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AgentAccountInfoViewModel<T : Any?>(private val agentAccountInfoRepository: AgentAccountInfoRepository) :
    BaseViewModel() {

    companion object {
        val TAG: String = AgentAccountInfoViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _agentInfo = MutableLiveData<AgentAccountInfoRespnse>()
    val agentInfo: LiveData<AgentAccountInfoRespnse>
        get() = _agentInfo
    private var apiType: String? = null

    private val _bpDpService = MutableLiveData<BpDpServiceResponse>()
    val bpDpService: LiveData<BpDpServiceResponse>
        get() = _bpDpService

    val messageSharedFlow = MutableSharedFlow<String>()



    fun agentAccountInfoAPI(
        agentAccountInfoRequest: AgentAccountInfoRequest,
        agentId: String = "",
        branchId: String = "",
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        /*viewModelScope.launch(Dispatchers.IO) {
            _agentInfo.postValue(
                agentAccountInfoRepository.agentAccountInfo(
                    authorization,
                    apiKey,
                    agentAccountInfoRequest
                ).body()
            )
        }*/

        viewModelScope.launch(Dispatchers.IO) {
            agentAccountInfoRepository.getAgentAccountBalanceInfo(
                agentAccountInfoRequest,
                agentId, branchId
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _agentInfo .postValue(
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




    fun newBpDpService(
        reservationId: String,
        apiKey: String,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            agentAccountInfoRepository.newBpDpService(
                reservationId,
                apiKey
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _bpDpService.postValue(
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

    /* fun getNextCalenderDates(date: String, travelDate: String) {
         var dateObjArrayList = mutableListOf<StageData>()
         val calendar: Calendar = Calendar.getInstance()
         val arrayOfData = date.split("-").map { it.toInt() }
         calendar.set(arrayOfData[2], arrayOfData[1].minus(1), arrayOfData[0])
         var i = 0
         val days = 7
         val formatter = SimpleDateFormat(DATE_FORMAT_DD_MMM, Locale.ENGLISH)
         while (i < days) {
             i++
             dateObjArrayList.add(StageData(formatter.format(calendar.time), true, false, "DATES"))
             calendar.add(Calendar.DAY_OF_MONTH, 1)
         }


         if (dateObjArrayList.any { it.title == inputFormatToOutput(travelDate,
                 DATE_FORMAT_D_M_Y, DATE_FORMAT_DD_MMM
             ) }) {
             val selectedDate = StageData("${inputFormatToOutput(travelDate,
                 DATE_FORMAT_D_M_Y, DATE_FORMAT_DD_MMM
             )}", true, true, "DATES")

             dateObjArrayList.forEachIndexed { index, stageData ->
                 if(selectedDate.title == stageData.title)
                 {
                     dateObjArrayList[index] = selectedDate
                 }
             }
         }
         *//*else {
            val selectedDate = StageData(dateObjArrayList[0].title, true, true, "DATES")
            dateObjArrayList[0] = selectedDate
        }*//*
        dates.postValue(dateObjArrayList)
    }*/
}