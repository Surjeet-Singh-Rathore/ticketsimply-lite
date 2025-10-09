package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.all_reports.AllReports
import com.bitla.ts.domain.pojo.all_reports.all_report_request.AllReportRequest
import com.bitla.ts.domain.pojo.all_reports.new_response.BookedByYouNewResponse
import com.bitla.ts.domain.pojo.all_reports.new_response.bus_service_collection_summary_report_data.BusServiceCollectionReportResponse
import com.bitla.ts.domain.pojo.all_reports.new_response.checking_inspector_report_data.CheckingInspectorReportResponse
import com.bitla.ts.domain.pojo.all_reports.new_response.group_by_branch_report_data.group_by_branch_report_response.GroupByBranchReportResponse
import com.bitla.ts.domain.pojo.all_reports.new_response.occupany_report_data.OccupancyNewResponse
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.all_reports.new_response.service_wise_city_pickup_report_data.ServiceWiseCityPickupReportResponse
import com.bitla.ts.domain.pojo.routewise_booking_memo.RouteWiseResponse
import com.bitla.ts.domain.repository.AllReportsRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AllReportsViewModel<T : Any?>(private val allReportsRepository: AllReportsRepository) :
    ViewModel() {

    companion object {
        val TAG: String = AllReportsViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _allReportsDetails = MutableLiveData<AllReports>()
    val allReports: LiveData<AllReports>
        get() = _allReportsDetails

    private val _newOccupancyReportDetail = MutableLiveData<OccupancyNewResponse>()
    val newOccupancyReportDetail: LiveData<OccupancyNewResponse>
        get() = _newOccupancyReportDetail

    private val _ticketBookedByYouNewResp = MutableLiveData<BookedByYouNewResponse>()
    val ticketBookedByYouNewResp: LiveData<BookedByYouNewResponse>
        get() = _ticketBookedByYouNewResp


    private val _paymentStatusReportResp = MutableLiveData<BookedByYouNewResponse>()
    val paymentStatusReportResp: LiveData<BookedByYouNewResponse>
        get() = _paymentStatusReportResp

    private val _routeWiseMemoDetails = MutableLiveData<RouteWiseResponse>()
    val routeWiseMemo: LiveData<RouteWiseResponse>
        get() = _routeWiseMemoDetails

//    private val _validation = MutableLiveData<Boolean>()
//    val validationData: LiveData<Boolean>
//        get() = _validation

    private val _serviceWiseCityPickupReport = MutableLiveData<ServiceWiseCityPickupReportResponse>()
    val serviceWiseCityPickupReport: LiveData<ServiceWiseCityPickupReportResponse>
        get() = _serviceWiseCityPickupReport

    private val _checkingInspectorReport = MutableLiveData<CheckingInspectorReportResponse>()
    val checkingInspectorReport: LiveData<CheckingInspectorReportResponse>
        get() = _checkingInspectorReport

    private val _busServiceCollectionReport = MutableLiveData<BusServiceCollectionReportResponse>()
    val busServiceCollectionReport: LiveData<BusServiceCollectionReportResponse>
        get() = _busServiceCollectionReport

    private val _groupByBranchReport = MutableLiveData<GroupByBranchReportResponse>()
    val groupByBranchReport: LiveData<GroupByBranchReportResponse>
        get() = _groupByBranchReport

    private var apiType: String? = null
    val messageSharedFlow = MutableSharedFlow<String>()

    var privilegesLiveData = MutableLiveData<PrivilegeResponseModel?>()

    fun updatePrivileges(privileges: PrivilegeResponseModel?) {
        privilegesLiveData.value = privileges
    }

    fun allReportsApi(
        authorization: String,
        apiKey: String,
        allReportsRequest: AllReportRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.allReports(
                authorization,
                apiKey,
                allReportsRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _allReportsDetails.postValue(
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

    fun userCollectionDetailApi(
        apiKey: String,
        locale : String,
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.userCollectionDetailsAPi(
                apiKey,locale,
                allReportsRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _allReportsDetails.postValue(
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

    fun checkingInspectorReportApi(
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.checkingInspectorReportApi(
                allReportsRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _allReportsDetails.postValue(
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

    fun fuelTransactionDetailApi(
        apiKey: String,
        locale: String,
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.fuelTransactionDetailApi(
                apiKey,
                locale,
                allReportsRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _allReportsDetails.postValue(
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

    fun occupancyReportApi(
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.occupancyReportApi(
                allReportsRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _allReportsDetails .postValue(
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
    fun occupancyReportApiViewOnly(
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.occupancyReportApiNew(
                allReportsRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _newOccupancyReportDetail .postValue(
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

    fun routeWiseBookingMemoApi(
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.routeWiseBookingMemoApi(
                allReportsRequest.apiKey!!,allReportsRequest.travelDate!!,allReportsRequest.isStarredReport!!,allReportsRequest.isPdfDownload!!,allReportsRequest.routeId!!,allReportsRequest.locale!!
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _routeWiseMemoDetails .postValue(
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

    fun busServiceCollectionApi(
        apiKey: String,
        locale: String,
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.busServiceCollectionApi(
                apiKey,locale,allReportsRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _allReportsDetails .postValue(
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

    fun groupByBranchReportApi(
        apiKey: String,
        locale: String,
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.groupByBranchReportApi(
                apiKey,locale,allReportsRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _allReportsDetails.postValue(
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

    fun groupByBranchNewReportApi(
        apiKey: String,
        locale: String,
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.groupByBranchNewReportApi(
                apiKey,locale,allReportsRequest
            ).collect {
                when(it){
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _groupByBranchReport.postValue(
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

    fun cargoReportApi(
        apiKey: String,
        locale: String,
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.cargoBookingReportApi(
                apiKey,locale,allReportsRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _allReportsDetails.postValue(
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


    fun ticketsBookedByYouApi(
        apiKey: String,
        locale: String,
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.ticketsBookedByYouApi(
                apiKey,locale,allReportsRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _allReportsDetails  .postValue(
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

    fun ticketBookedByYouNewApi(
        apiKey: String,
        locale: String,
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.ticketsBookedByYouNewApi(
                apiKey,locale,allReportsRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _ticketBookedByYouNewResp .postValue(
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


    fun paymentStatusReportApi(
        apiKey: String,
        locale: String,
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            if (!allReportsRequest.isPdfDownload!!) {
                allReportsRepository.paymentStatusReportApi(
                    apiKey, locale, allReportsRequest
                ).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _paymentStatusReportResp  .postValue(
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

        fun paymentStatusReportDownloadApi(
            apiKey: String,
            locale: String,
            allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
            apiType: String
        ) {

            _loadingState.postValue(LoadingState.LOADING)
            val gson = GsonBuilder().disableHtmlEscaping().create()
            val json = gson.toJson(allReportsRequest)

            viewModelScope.launch(Dispatchers.IO) {
                allReportsRepository.paymentStatusReportDownloadApi(
                    apiKey,locale,allReportsRequest
                ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _allReportsDetails.postValue(
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

    fun serviceWisePickupClosureReportNewApi(
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ){
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.serviceWisePickupClosureReportNewApi(
                allReportsRequest
            ).collect {
                when(it){
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _serviceWiseCityPickupReport.postValue(
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

    fun serviceWiseCityPickup(
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.serviceWiseCityPickup(
                allReportsRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _allReportsDetails.postValue(
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


    fun checkingInspectorReportNewApi(
        apiKey: String,
        locale: String,
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.checkingInspectorReportNewApi(
                apiKey, locale, allReportsRequest
            ).collect {
                when(it){
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _checkingInspectorReport.postValue(
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

    fun busServiceCollectionNewApi(
        apiKey: String,
        locale: String,
        coachId: String,
        allReportsRequest: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allReportsRequest)

        viewModelScope.launch(Dispatchers.IO) {
            allReportsRepository.busServiceCollectionNewApi(
                apiKey, locale, coachId, allReportsRequest
            ).collect {
                when(it){
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _busServiceCollectionReport.postValue(
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