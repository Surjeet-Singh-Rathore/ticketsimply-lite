package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bitla.ts.domain.pojo.add_driver.request.ReqBody
import com.bitla.ts.domain.pojo.add_driver.response.*
import com.bitla.ts.domain.pojo.all_coach.response.*
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctRequest.*
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.*
import com.bitla.ts.domain.pojo.alloted_services.*
import com.bitla.ts.domain.pojo.alloted_services.request.*
import com.bitla.ts.domain.pojo.announcement_details_model.response.*
import com.bitla.ts.domain.pojo.announcement_model.response.*
import com.bitla.ts.domain.pojo.block_unblock_reservation.*
import com.bitla.ts.domain.pojo.bulkCancelOtpConfirmtion.request.*
import com.bitla.ts.domain.pojo.bulkCancelOtpConfirmtion.response.*
import com.bitla.ts.domain.pojo.bulk_cancellation.*
import com.bitla.ts.domain.pojo.city_pickup_by_chart_stage.response.*
import com.bitla.ts.domain.pojo.coach_list.CoachListResponse
import com.bitla.ts.domain.pojo.collection_details.*
import com.bitla.ts.domain.pojo.collection_details.trip_collection.TripCollectionDetailsData
import com.bitla.ts.domain.pojo.employees_details.response.*
import com.bitla.ts.domain.pojo.expenses_details.response.*
import com.bitla.ts.domain.pojo.lock_chart.response.*
import com.bitla.ts.domain.pojo.luggage_details.response.FetchLuggageDetailsResponse
import com.bitla.ts.domain.pojo.luggage_details.response.LuggageOptionsDetailsResponse
import com.bitla.ts.domain.pojo.pickUpVanChart.*
import com.bitla.ts.domain.pojo.pickup_chart_crew_details.response.*
import com.bitla.ts.domain.pojo.pickup_chart_pdf_url.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.sendOtpAndQrCode.*
import com.bitla.ts.domain.pojo.service_allotment.response.*
import com.bitla.ts.domain.pojo.update_boarded_status.response.*
import com.bitla.ts.domain.pojo.update_expenses_details.response.*
import com.bitla.ts.domain.pojo.update_rate_card.create_fare_template.response.*
import com.bitla.ts.domain.pojo.update_rate_card.fetch_fare_template.response.*
import com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.response.*
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.*
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_commission.response.*
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.response.*
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.by_seat_type.response.*
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.response.*
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_time.response.*
import com.bitla.ts.domain.pojo.update_trip_status.UpdateTripResponse
import com.bitla.ts.domain.pojo.viewSummary.*
import com.bitla.ts.domain.pojo.view_reservation.*
import com.bitla.ts.domain.repository.*
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.*
import com.google.gson.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.*

class PickUpChartViewModel<T : Any?>(private val pickUpRepository: PickUpRepository) :
    BaseViewModel() {


    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _allotedDetailResponse = MutableLiveData<AllotedServicesResponseModel>()
    val allotedDetailResponse: LiveData<AllotedServicesResponseModel>
        get() = _allotedDetailResponse

    private val _blockUnblockReservationResponse = MutableLiveData<BlockUnblockReservation>()
    val blockUnblockReservationResponse: LiveData<BlockUnblockReservation>
        get() = _blockUnblockReservationResponse

    private val _serviceBlockReasonsListResponse = MutableLiveData<ServiceBlockReasonListResp>()
    val serviceBlockReasonsListResponse: LiveData<ServiceBlockReasonListResp>
        get() = _serviceBlockReasonsListResponse


    private val _lockChartResponse = MutableLiveData<LockChartResponse>()
    val lockChartResponse: LiveData<LockChartResponse>
        get() = _lockChartResponse

    private val _viewReservationResponse = MutableLiveData<ViewReservationResponseModel>()
    val viewReservationResponse: LiveData<ViewReservationResponseModel>
        get() = _viewReservationResponse

    private val _pickUpVanResponse = MutableLiveData<JsonElement>()
    val pickUpVanResponse: LiveData<JsonElement>
        get() = _pickUpVanResponse


    private val _updateBoardedStatusResponse = MutableLiveData<UpdateBoardedStatusResponseModel>()
    val updateBoardedStatusResponse: LiveData<UpdateBoardedStatusResponseModel>
        get() = _updateBoardedStatusResponse


    private val _updateBoardedStatusResponseCorgo =
        MutableLiveData<com.bitla.ts.domain.pojo.update_boarded_status.request.response_cargo.UpdateBoardedStatusResponseModel>()
    val updateBoardedStartusCargo: LiveData<com.bitla.ts.domain.pojo.update_boarded_status.request.response_cargo.UpdateBoardedStatusResponseModel>
        get() = _updateBoardedStatusResponseCorgo


    private val _pickUpChatPdfResponse = MutableLiveData<PickUpChartPdfResponseModel>()
    val pickUpChatPdfResponse: LiveData<PickUpChartPdfResponseModel>
        get() = _pickUpChatPdfResponse

    private val _bulkCabcelationResponse = MutableLiveData<BulkCancellationResponseModel>()
    val bulkCancellationResponse: LiveData<BulkCancellationResponseModel>
        get() = _bulkCabcelationResponse

    private val _bulkCancelOtpVerificationResponse =
        MutableLiveData<BulkCancelOtpVerificationResponse>()
    val bulkCancelOtpVerificationResponse: LiveData<BulkCancelOtpVerificationResponse>
        get() = _bulkCancelOtpVerificationResponse

    private val _sendOtpAndQrCodeResponse = MutableLiveData<SendOtqAndQrCodeResponseModel>()
    val sendOtpAndQrCodeResponse: LiveData<SendOtqAndQrCodeResponseModel>
        get() = _sendOtpAndQrCodeResponse

    private val _updateRateCardFareResponse = MutableLiveData<UpdateRateCardFareResponse>()
    val updateRateCardFareResponse: LiveData<UpdateRateCardFareResponse>
        get() = _updateRateCardFareResponse

    private val _updateRateCardTimeResponse = MutableLiveData<UpdateRateCardTimeResponse>()
    val updateRateCardTimeResponse: LiveData<UpdateRateCardTimeResponse>
        get() = _updateRateCardTimeResponse

    private val _updateRateCardCommissionResponse =
        MutableLiveData<UpdateRateCardCommissionResponse>()
    val updateRateCardCommissionResponse: LiveData<UpdateRateCardCommissionResponse>
        get() = _updateRateCardCommissionResponse

    private val _fetchMultiStatioWiseFareResponse = MutableLiveData<MultiStationWiseFareResponse>()
    val fetchMultiStatioWiseFareResponse: LiveData<MultiStationWiseFareResponse> get() = _fetchMultiStatioWiseFareResponse

    private val _fetchFareTemplateResponse = MutableLiveData<FetchFareTemplateResponse>()
    val fetchFareTemplateResponse: LiveData<FetchFareTemplateResponse> get() = _fetchFareTemplateResponse

    private val _createFareTemplateResponse =
        MutableLiveData<CreateFareTemplateResponse>()
    val createFareTemplateResponse: LiveData<CreateFareTemplateResponse>
        get() = _createFareTemplateResponse

    private val _manageMultiStatioWiseFareApi =
        MutableLiveData<ManageFareMultiStationResponse>()
    val manageFareMultiStationResponse: LiveData<ManageFareMultiStationResponse>
        get() = _manageMultiStatioWiseFareApi

    private val _updateRateCardSeatWiseResponse = MutableLiveData<UpdateRateCardSeatWiseResponse>()
    val updateRateCardSeatWiseResponse: LiveData<UpdateRateCardSeatWiseResponse>
        get() = _updateRateCardSeatWiseResponse

    private val _updateRateCardPerSeatResponse = MutableLiveData<UpdateRateCardPerSeatResponse>()
    val updateRateCardPerSeatResponse: LiveData<UpdateRateCardPerSeatResponse>
        get() = _updateRateCardPerSeatResponse

    private val _changeButtonBackground = MutableLiveData<Boolean>()
    val changeButtonBackground: LiveData<Boolean>
        get() = _changeButtonBackground

    private val _collectionDetailsResponse = MutableLiveData<CollectionDetailsResponse>()
    val collectionDetailsResponse: LiveData<CollectionDetailsResponse>
        get() = _collectionDetailsResponse


    private val _tripCollectionDetailsResponse = MutableLiveData<TripCollectionDetailsData>()
    val tripCollectionDetailsResponse: LiveData<TripCollectionDetailsData>
        get() = _tripCollectionDetailsResponse


    private val _addADHOCDriverResponse = MutableLiveData<AddADHOCDriverResponse>()
    val addADHOCDriverResponse: LiveData<AddADHOCDriverResponse>
        get() = _addADHOCDriverResponse

    private val _employeesDetailsResponse = MutableLiveData<EmployeesDetailsResponse>()
    val employeesDetailsResponse: LiveData<EmployeesDetailsResponse>
        get() = _employeesDetailsResponse

    private val _expensesDetailsResponse = MutableLiveData<ExpensesDetailsResponse>()
    val expensesDetailsResponse: LiveData<ExpensesDetailsResponse>
        get() = _expensesDetailsResponse

    private val _updateExpensesDetailsResponse = MutableLiveData<UpdateExpensesDetailsResponse>()
    val updateExpensesDetailsResponse: LiveData<UpdateExpensesDetailsResponse>
        get() = _updateExpensesDetailsResponse

    private val _serviceAllotmentResponse = MutableLiveData<ServiceAllotmentResponse>()
    val serviceAllotmentResponse: LiveData<ServiceAllotmentResponse>
        get() = _serviceAllotmentResponse

    private val _allCoachResponse = MutableLiveData<AllCoachResponse>()
    val allCoachResponse: LiveData<AllCoachResponse>
        get() = _allCoachResponse

    private val _announcementApiResponse = MutableLiveData<AnnouncementApiResponse>()
    val announcementApiResponse: LiveData<AnnouncementApiResponse>
        get() = _announcementApiResponse

    private val _announcementDetailsApiResponse = MutableLiveData<AnnoucementDetailsResponse>()
    val announcementDetailsApiResponse: LiveData<AnnoucementDetailsResponse>
        get() = _announcementDetailsApiResponse

    private val _pickupChartCrewDetailsResponse = MutableLiveData<PickupChartCrewDetailsResponse>()
    val pickupChartCrewDetailsResponse: LiveData<PickupChartCrewDetailsResponse>
        get() = _pickupChartCrewDetailsResponse

    private val _cityPickupChartByStageResponse = MutableLiveData<CityPickupChartByStageResponse>()
    val cityPickupChartByStageResponse: LiveData<CityPickupChartByStageResponse>
        get() = _cityPickupChartByStageResponse

    private val _dataAllotedServiceDirect = MutableLiveData<AllotedDirectResponse>()
    val dataAllotedServiceDirect: LiveData<AllotedDirectResponse>
        get() = _dataAllotedServiceDirect

    private val _checkInspectorData = MutableLiveData<checkingInspectorResponseBody>()
    val checkingInspectorData: LiveData<checkingInspectorResponseBody>
        get() = _checkInspectorData

    private val _viewSummaryDirect = MutableLiveData<ViewSummaryResonse>()
    val viewSummaryDirect: LiveData<ViewSummaryResonse>
        get() = _viewSummaryDirect

    private val _updateTripStatusData = MutableLiveData<UpdateTripResponse>()
    val updateTripStatusData: LiveData<UpdateTripResponse>
        get() = _updateTripStatusData

    private val _coachList = MutableLiveData<CoachListResponse>()
    val coachList: LiveData<CoachListResponse>
        get() = _coachList

    private val _updateLuggageOption = MutableLiveData<LuggageOptionsDetailsResponse>()
    val updateLuggageOption: LiveData<LuggageOptionsDetailsResponse> get() = _updateLuggageOption

    private val _fetchLuggageDetailsResponse = MutableLiveData<FetchLuggageDetailsResponse>()
    val fetchLuggageDetailsResponse: LiveData<FetchLuggageDetailsResponse> get() = _fetchLuggageDetailsResponse

    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()

    var privilegesLiveData = MutableLiveData<PrivilegeResponseModel?>()

    fun updatePrivileges(privileges: PrivilegeResponseModel?) {
        privilegesLiveData.value = privileges
    }

    fun allotedServiceAPI(
        authorization: String,
        apiKey: String,
        allotedServiceRequest: AllotedServiceRequest,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            pickUpRepository.allotedService(
                authorization,
                apiKey,
                allotedServiceRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _allotedDetailResponse.postValue(
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

    fun allotedServiceApiDirect(
        allotedRequest: AllotedDirectRequest,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(allotedRequest)
        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newAllotedService(
                allotedRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _dataAllotedServiceDirect.postValue(
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

    fun getAllottedServicesWithDateChange(
        apiKey: String,
        origin: String,
        destination: String,
        from: String,
        to: String,
        hubId: String?,
        isGroupByHubs: Boolean,
        viewMode: String,
        locale: String,
        isFromMiddleTier: Boolean,
        methodName: String
    ) {
        this.apiType = methodName
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.getAllottedServicesWithDateChange(
                apiKey,
                origin,
                destination,
                from,
                to,
                hubId,
                isGroupByHubs,
                viewMode,
                locale,
                isFromMiddleTier
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _allotedDetailResponse.postValue(
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

//    fun allotedServiceApiDirect(
//        is_group_by_hubs: Boolean,
//        hub_id: Int?,
//        travel_date: String,
//        Apikey: String,
//        page: Int?= null,
//        per_page: Int?= null,
//        view_mode: String,
//        pagination: Boolean,
//        origin: Int?,
//        destination: Int?
//    ) {
//      
//        _loadingState.postValue(LoadingState.LOADING)
//
//        viewModelScope.launch(Dispatchers.IO) {
//            _dataAllotedServiceDirect.postValue(
//                pickUpRepository.getNewAllotedService(
//                   is_group_by_hubs, hub_id, travel_date, Apikey, page, per_page, view_mode, pagination, origin, destination
//                ).body()
//            )
//        }
//    }

    fun getCoachList(apiKey: String, routeId: String) {
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.getCoachList(apiKey, routeId).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _coachList.postValue(it.data)
                    }
                    is NetworkProcess.Failure -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        messageSharedFlow.emit(it.message)
                    }
                }
            }
        }
    }

    fun checkingInspectorApi(
        resId: String,
        apiKey: String,
        locale: String,
        reqBody: CheckingInspectorRequestBody,
        apiType: String

    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.checkingInspectorApi(
                resId, apiKey, locale, reqBody
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _checkInspectorData .postValue(
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

    fun viewSummaryApi(
        viewSummaryRequest: ViewSummaryRequest,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(viewSummaryRequest)
        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newViewSummary(
                viewSummaryRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _viewSummaryDirect  .postValue(
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


    fun blockUnblockAPI(
        blockUnblockRequest: com.bitla.ts.domain.pojo.block_unblock_reservation.request.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newBlockUnblockReservationService(
                blockUnblockRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _blockUnblockReservationResponse.postValue(
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


    fun getServiceBlockReasonsListApi(
        apiKey: String
    ) {
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.getServiceBlockReasonsList(
                apiKey
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _serviceBlockReasonsListResponse.postValue(
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


    fun lockChartAPI(
        lockChartRequest: com.bitla.ts.domain.pojo.lock_chart.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newLockChartService(
                lockChartRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _lockChartResponse .postValue(
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


    fun addADHOCDriverAPI(
        addADHOCDriverRequest: ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newAddADHOCDriverService(
                addADHOCDriverRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _addADHOCDriverResponse.postValue(
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


    fun getEmployeesDetails(
        apiKey: String,
        apiType: String,
        locale: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newGetEmployeesDetails(
                apiKey,
                locale
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _employeesDetailsResponse.postValue(
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


    fun getExpensesDetails(
        apiKey: String,
        reservationId: String,
        locale: String,
        respFormat: String,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newGetExpensesDetails(
                apiKey,
                reservationId,
                locale,
                respFormat
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _expensesDetailsResponse.postValue(
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


    fun updateExpensesDetails(
        updateExpensesDetailsRequest: com.bitla.ts.domain.pojo.update_expenses_details.request.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            pickUpRepository.newUpdateExpensesDetails(
                updateExpensesDetailsRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _updateExpensesDetailsResponse.postValue(
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

    fun updateServiceAllotmentDetails(
        id: Long,
        reqBody: com.bitla.ts.domain.pojo.service_allotment.request.ReqBody
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newUpdateServiceAllotment(
                id,
                reqBody
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _serviceAllotmentResponse.postValue(
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


    fun getAllCoach(
        apiKey: String,
        reservationId: String,
        locale: String,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newGetAllCoach(
                apiKey,
                reservationId,
                locale,
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _allCoachResponse.postValue(
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


    fun viewReservationAPI(
        apiKey: String,
        resId: String,
        chartType: String,
        locale: String,
        apiType: String,
        newPickUpChart: Boolean?
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            pickUpRepository.newViewReservationService(
                apiKey = apiKey,
                resId = resId,
                chartType = chartType,
                locale = locale,
                newPickupChart = newPickUpChart
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _viewReservationResponse.postValue(
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

    fun getPickUpVanChartApi(
        pickUpVanRequest: PickUpVanRequest
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.getPickUpVanChart(
                pickUpVanRequest
            )  .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _pickUpVanResponse .postValue(
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

    fun viewReservationCheckingInspectorAPI(
        apiKey: String,
        resId: String,
        cityId: Int,
        chartType: String,
        locale: String,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            pickUpRepository.newViewReservationServiceForCheckingInspector(
                apiKey,
                resId,
                cityId,
                chartType,
                locale
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _viewReservationResponse.postValue(
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


    fun updateBoardedStatusAPI(
        updateBoardedStatusRequest: com.bitla.ts.domain.pojo.update_boarded_status.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            pickUpRepository.newUpdateBoardedStatusService(
                updateBoardedStatusRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _updateBoardedStatusResponse.postValue(
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

    fun vanChartStatusApi(
        apiKey: String,
        pnrNumber: String,
        seatNumber: String,
        vanChart: Boolean,
        locale: String,
        vanChartStatusChangeRequest: VanChartStatusChangeRequest
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.vanChartUpdatedStatus(
                apiKey, pnrNumber, seatNumber, vanChart, locale, vanChartStatusChangeRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _updateBoardedStatusResponse.postValue(
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


    fun updateBoardedStatusCargoAPI(
        updateBoardedStartusCargo: com.bitla.ts.domain.pojo.update_boarded_status.request.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newUpdateBoardedStatusServiceCargo(
                updateBoardedStartusCargo
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _updateBoardedStatusResponseCorgo.postValue(
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


    fun pickUpChartPdfAPI(
        pickupChartPdfRequest: com.bitla.ts.domain.pojo.pickup_chart_pdf_url.request.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            pickUpRepository.newPickUpChartPdfService(
                pickupChartPdfRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _pickUpChatPdfResponse.postValue(
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


    fun bulkCancellationAPI(
        bulkCancellationRequest: com.bitla.ts.domain.pojo.bulk_cancellation.request.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newBulkCancellationService(
                bulkCancellationRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _bulkCabcelationResponse.postValue(
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

    fun bulkCancelOtpVerificationApi(
        bulkCancellationVerificationRequest: BulkCancelVerificationRequest,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            pickUpRepository.newBulkCancelVerification(
                bulkCancellationVerificationRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _bulkCancelOtpVerificationResponse.postValue(
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


    fun resendOtpAndQrCodeAPI(
        sendOtpAndQrCodeRequest: com.bitla.ts.domain.pojo.sendOtpAndQrCode.request.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newResendOtpAndQrCodeService(
                sendOtpAndQrCodeRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _sendOtpAndQrCodeResponse.postValue(
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


    fun collectionDetailsAPI(
        collectionDetailsRequest: com.bitla.ts.domain.pojo.collection_details.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            pickUpRepository.newCollectionDetailsService(
                collectionDetailsRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _collectionDetailsResponse.postValue(
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

    fun tripCollectionDetailsAPI(
        collectionDetailsRequest: com.bitla.ts.domain.pojo.collection_details.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.tripCollectionDetailsService(
                collectionDetailsRequest
            ) .collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _tripCollectionDetailsResponse.postValue(
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


    fun updateRateCardFareApi(
        updateRateCardFareRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newUpdateRateCardFareService(
                updateRateCardFareRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _updateRateCardFareResponse.postValue(
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

    fun updateRateCardFareApiNew(
        updateRateCardFareRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_fare.request.ReqBodyNew,
        apiType: String,
    ) {
        this.apiType = apiType
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newUpdateRateCardFare(
                updateRateCardFareRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _updateRateCardFareResponse.postValue(
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

    fun updateRateCardTimeApi(
        updateRateCardTimeRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_time.request.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.updateRateCardTimeService(
                updateRateCardTimeRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _updateRateCardTimeResponse.postValue(
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


    fun updateRateCardCommissionApi(
        updateRateCardCommissionRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_commission.request.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newUpdateRateCardCommissionService(
                updateRateCardCommissionRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _updateRateCardCommissionResponse.postValue(
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

    fun fetchMultiStatioWiseFareApi(
        multiStationWiseFareRequest: com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.request.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newFetchMultiStatioWiseFareService(
                multiStationWiseFareRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _fetchMultiStatioWiseFareResponse.postValue(
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
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(multiStationWiseFareRequest)
        Timber.d("PickUpChartViewModel", "multiStationWiseFareRequest: " + json.toString())
    }

    fun fetchFareTemplateDetailsApi(
        fareTemplateRequest: com.bitla.ts.domain.pojo.update_rate_card.fetch_fare_template.request.ReqBody,
        apiType: String,
    ) {
        this.apiType = apiType
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.fetchFareTemplateApi(
                fareTemplateRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _fetchFareTemplateResponse.postValue(
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
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(fareTemplateRequest)
        Timber.d("PickUpChartViewModel", "multiStationWiseFareRequest: " + json.toString())
    }

    fun createFareTemplateApi(
        createFareTemplateReqBody: com.bitla.ts.domain.pojo.update_rate_card.create_fare_template.request.ReqBody,
    ) {
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.createFareTemplateService(
                createFareTemplateReqBody
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _createFareTemplateResponse .postValue(
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


    fun manageFareMultiStation(
        manageFareMultiStationRequest: com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.request.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newManageMultiStatioWiseFareApi(
                manageFareMultiStationRequest
            )  .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _manageMultiStatioWiseFareApi .postValue(
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
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(manageFareMultiStationRequest)
        Timber.d("PickUpChartViewModel", "manageFareMultiStationApi: " + json.toString())
    }


    fun updateRateCardSeatWiseApi(
        updateRateCardSeatWiseRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.by_seat_type.request.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newUpdateRateCardSeatWiseService(
                updateRateCardSeatWiseRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _updateRateCardSeatWiseResponse .postValue(
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


    fun updateRateCardSeatWisePerSeatApi(
        updateRateCardPerSeatRequest: com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.request.ReqBody,
        apiType: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.updateRateCardSeatWisePerSeatService(
                updateRateCardPerSeatRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _updateRateCardPerSeatResponse.postValue(
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


    fun announcementRequestAPI(
        announcementApiRequest: com.bitla.ts.domain.pojo.announcement_model.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            4
            pickUpRepository.newAnnouncementRequest(
                announcementApiRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _announcementApiResponse.postValue(
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


    fun announcementDetailsRequestApi(
        announcementDetailsApiRequest: com.bitla.ts.domain.pojo.announcement_details_model.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newAnnouncementDetailsRequest(
                announcementDetailsApiRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _announcementDetailsApiResponse.postValue(
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

    /* fun cityPickupChartByStage(
         authorization: String,
         apiKey: String,
         cityPickupChartByStageRequest: CityPickupChartByStageRequest,
         apiType: String
     ) {

         _loadingState.postValue(LoadingState.LOADING)
         viewModelScope.launch(Dispatchers.IO) {
             _cityPickupChartByStageResponse.postValue(
                 pickUpRepository.cityPickupChartByStageService(
                     authorization,
                     apiKey,
                     cityPickupChartByStageRequest
                 ).body()
             )
         }
     }  */

    fun cityPickupChartByStage(
        cityPickupChartByStageRequest: com.bitla.ts.domain.pojo.city_pickup_by_chart_stage.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.newCityPickupChartByStageService(
                cityPickupChartByStageRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _cityPickupChartByStageResponse .postValue(
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

    fun pickupChartCrewDetailsApi(
        apiKey: String,
        reservationId: String,
        apiType: String,
        locale: String,
        coachId: String = ""
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            pickUpRepository.newPickupChartCrewDetails(
                apiKey,
                reservationId,
                locale,
                coachId
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _pickupChartCrewDetailsResponse.postValue(
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

    fun updateTripStatusApi(
        reqBody:Any
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            pickUpRepository.updateTripStatusApi(
                reqBody
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _updateTripStatusData.postValue(
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

    fun changeButtonBackground(
        fromDate: String?,
        toDate: String?,
        isEmpty: Boolean
    ) {
        val isCorrect: Boolean =
            !((fromDate.equals(null) || fromDate.equals("") || fromDate.equals("From date")
                    || toDate.equals(null) || toDate.equals("") || toDate.equals("To date")) || isEmpty)

        _changeButtonBackground.postValue(isCorrect)
    }

    fun updateLuggageOptionIntlApi(
        reqBody: com.bitla.ts.domain.pojo.luggage_details.request.ReqBody
    ) {
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.updateLuggageOptionIntlApi(
                reqBody = reqBody
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _updateLuggageOption.postValue(
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

    fun fetchLuggageDetailsIntlApi(
        apiKey: String,
        pnrNumber: String
    ) {
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            pickUpRepository.fetchLuggageDetailsIntlApi(apiKey, pnrNumber).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _fetchLuggageDetailsResponse.postValue(
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