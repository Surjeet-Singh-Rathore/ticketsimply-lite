package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.booking_summary.BookingSummary
import com.bitla.ts.domain.pojo.cancellation_policies_service_summary.response.CancellationPoliciesServiceSummaryResponse
import com.bitla.ts.domain.pojo.collection_summary.CollectionSummary
import com.bitla.ts.domain.pojo.delete_recent_search.DeleteRecentSearch
import com.bitla.ts.domain.pojo.delete_recent_search.request.ReqBody
import com.bitla.ts.domain.pojo.drag_drop_remarks_update.response.DragDropRemarksUpdateResponse
import com.bitla.ts.domain.pojo.fetch_notification.request.FetchNotificationModel
import com.bitla.ts.domain.pojo.fetch_notification.request.NotificationFilter
import com.bitla.ts.domain.pojo.frequent_traveller_model.response.FrequentTravellerDataResponse
import com.bitla.ts.domain.pojo.notificationDetails.GetNotificationDetails
import com.bitla.ts.domain.pojo.notificationDetails.request.NotificationDetailsRequest
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response.QuickBookServiceDetailsResponse
import com.bitla.ts.domain.pojo.recent_search.RecentSearchModel
import com.bitla.ts.domain.pojo.released_summary.ReleasedSummary
import com.bitla.ts.domain.pojo.service_details_response.ServiceDetailsModel
import com.bitla.ts.domain.pojo.service_summary.ServiceSummaryModel
import com.bitla.ts.domain.pojo.update_notification.UpdateNotificationModel
import com.bitla.ts.domain.pojo.update_notification.request.UpdateNotificationRequest
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultiStationWiseFareResponse
import com.bitla.ts.domain.repository.SharedRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class SharedViewModel<T : Any?>(private val sharedRepository: SharedRepository) :
    BaseViewModel(),
    Callback<T> {

    companion object {
        val TAG: String = SharedViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState


    private val _notificationData =
        MutableLiveData<ArrayList<com.bitla.ts.domain.pojo.fetch_notification.Data>>()
    val notificationListData: LiveData<ArrayList<com.bitla.ts.domain.pojo.fetch_notification.Data>>
        get() = _notificationData

    private val _notificationFilter = MutableLiveData<List<NotificationFilter>>()
    val notificationFilterData: LiveData<List<NotificationFilter>>
        get() = _notificationFilter

    private val _serviceDetails = MutableLiveData<ServiceDetailsModel>()
    val serviceDetails: LiveData<ServiceDetailsModel>
        get() = _serviceDetails

    private val _serviceDetailsByRouteId = MutableLiveData<ServiceDetailsModel>()
    val serviceDetailsByRouteId: LiveData<ServiceDetailsModel>
        get() = _serviceDetailsByRouteId

    private val _bookingSummary = MutableLiveData<BookingSummary>()
    val bookingSummary: LiveData<BookingSummary>
        get() = _bookingSummary

    private val _serviceSummary = MutableLiveData<ServiceSummaryModel>()
    val serviceSummary: LiveData<ServiceSummaryModel>
        get() = _serviceSummary

    private val _collectionSummary = MutableLiveData<CollectionSummary>()
    val collectionSummary: LiveData<CollectionSummary>
        get() = _collectionSummary

    private val _releasedSummary = MutableLiveData<ReleasedSummary>()
    val releasedSummary: LiveData<ReleasedSummary>
        get() = _releasedSummary

    private val _cancellationPoliciesServiceSummary =
        MutableLiveData<CancellationPoliciesServiceSummaryResponse>()
    val cancellationPoliciesServiceSummary: LiveData<CancellationPoliciesServiceSummaryResponse>
        get() = _cancellationPoliciesServiceSummary

    private val _fetchNotificationModel = MutableLiveData<FetchNotificationModel>()
    val fetchNotificationModel: LiveData<FetchNotificationModel>
        get() = _fetchNotificationModel

    private val _notificationDetailsModel = MutableLiveData<GetNotificationDetails>()
    val notificationDetailsModel: LiveData<GetNotificationDetails>
        get() = _notificationDetailsModel

    private val _updateNotificationModel = MutableLiveData<UpdateNotificationModel>()
    val updateNotificationModel: LiveData<UpdateNotificationModel>
        get() = _updateNotificationModel

    private val _quickBookServiceDetails = MutableLiveData<QuickBookServiceDetailsResponse>()
    val quickBookServiceDetails: LiveData<QuickBookServiceDetailsResponse>
        get() = _quickBookServiceDetails


    private val _dragDropRemarks = MutableLiveData<DragDropRemarksUpdateResponse>()
    val dragDropRemarks: LiveData<DragDropRemarksUpdateResponse>
        get() = _dragDropRemarks

    private val _frequentData = MutableLiveData<FrequentTravellerDataResponse>()
    val frequentData: LiveData<FrequentTravellerDataResponse>
        get() = _frequentData


    var multistationFareDetails : MutableLiveData<MultiStationWiseFareResponse> = MutableLiveData()

    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()

    var privilegesLiveData = MutableLiveData<PrivilegeResponseModel?>()

    fun updatePrivileges(privileges: PrivilegeResponseModel?) {
        privilegesLiveData.value = privileges
    }


    /*    fun recentSearchApi(
            authorization: String,
            apiKey: String,
            recentSearchRequest: RecentSearchRequest,
            apiType: String
        ) {

            _loadingState.postValue(LoadingState.LOADING)

            viewModelScope.launch(Dispatchers.IO) {
                val response = _dataRecentSearch.postValue(
                    sharedRepository.recentSearch(
                        authorization,
                        apiKey,
                        recentSearchRequest
                    ).body()
                )
            }
        } */

    fun getNotificationList(
        seatDetails: ArrayList<com.bitla.ts.domain.pojo.fetch_notification.Data>,
    ) {
        _notificationData.postValue(seatDetails)
    }

    fun getNotificationFilterList(seatDetails: List<NotificationFilter>) {
        _notificationFilter.postValue(seatDetails)
    }


    fun getFilterType(label: String): Int {
        var filterType = 0
        when (label) {
            "All" -> {
                filterType = -1
            }

            "MIS" -> {
                filterType = 1
            }

            "Booking / Cancellation" -> {
                filterType = 2

            }

            "Blocking" -> {
                filterType = 3

            }

            "General" -> {
                filterType = 4

            }

            else -> return -1
        }
        return filterType

    }

    fun getQuickBookServiceDetail(
        reservationId: String,
        apiKey: String,
        originId: String,
        destinationId: String,
        operatorApiKey: String,
        locale: String,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            sharedRepository.getQuickBookServiceDetail(
                reservationId,
                originId,
                destinationId,
                apiKey,
                operatorApiKey,
                locale
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _quickBookServiceDetails.postValue(
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

    fun getServiceDetails(
        reservationId: String,
        apiKey: String,
        originId: String,
        destinationId: String,
        operatorApiKey: String,
        locale: String,
        apiType: String,
        appBimaEnabled: Boolean? = null,
        excludePassengerDetails: Boolean
    ) {
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            sharedRepository.newGetServiceDetail(
                reservationId = reservationId,
                origin = originId,
                destinationId = destinationId,
                apiKey = apiKey,
                operator_api_key = operatorApiKey,
                locale = locale,
                app_bima_enabled = appBimaEnabled ?: false,
                excludePassengerDetails
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _serviceDetails.postValue(
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

    fun getServiceDetailsByRouteId(
        routeId: String,
        apiKey: String,
        originId: String,
        destinationId: String,
        travelDate: String,
        operatorApiKey: String,
        locale: String,
        apiType: String,
        excludePassengerDetails: Boolean
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            sharedRepository.getServiceDetailsByRouteId(
                routeId,
                originId,
                destinationId,
                travelDate,
                apiKey,
                operatorApiKey,
                locale,
                excludePassengerDetails
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _serviceDetailsByRouteId.postValue(
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

    fun getBpDpServiceDetails(
        reservationId: String,
        apiKey: String,
        operator_api_key: String,
        origin: String,
        destinationId: String,
        locale: String,
        boardingAt: String,
        dropOff: String,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            sharedRepository.newGetBpDpServiceDetails(
                reservationId = reservationId,
                origin = origin,
                destinationId = destinationId,
                apiKey = apiKey,
                operator_api_key = operator_api_key,
                locale = locale,
                boardingAt = boardingAt,
                dropOff = dropOff
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _serviceDetails.postValue(
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


    /*    fun bookingSummaryApi(
            authorization: String,
            apiKey: String, request: BookingSummaryRequest, apiType: String
        ) {

            _loadingState.postValue(LoadingState.LOADING)

            viewModelScope.launch(Dispatchers.IO) {
                _bookingSummary.postValue(
                    sharedRepository.bookingSummary(authorization, apiKey, request).body()
                )
            }
        } */

    fun bookingSummaryApi(
        apiKey: String,
        reservationId: String,
        responseFormat: String,
        locale: String,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            sharedRepository.newBookingSummary(apiKey, reservationId, responseFormat, locale)
                .collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _bookingSummary.postValue(
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


    fun serviceSummaryApi(
        apiKey: String,
        locale: String,
        reservationId: String,
        reservationFormat: Boolean,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {


            sharedRepository.newServiceSummary(apiKey, locale, reservationId, reservationFormat)
                .collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _serviceSummary.postValue(
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

    /* fun collectionSummaryApi(
         authorization: String,
         apiKey: String, request: BookingSummaryRequest, apiType: String
     ) {

         _loadingState.postValue(LoadingState.LOADING)

         viewModelScope.launch(Dispatchers.IO) {
             _collectionSummary.postValue(
                 sharedRepository.collectionSummary(authorization, apiKey, request).body()
             )
         }
     } */

    fun collectionSummaryApi(
        apiKey: String,
        reservationId: String,
        responseFormat: String,
        locale: String,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
                sharedRepository.newCollectionSummary(apiKey, reservationId, responseFormat, locale).collect {
                    when (it) {
                        is NetworkProcess.Loading -> {}
                        is NetworkProcess.Success -> {
                            _loadingState.postValue(LoadingState.LOADED)
                            _collectionSummary.postValue(
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

    /*  fun releasedSummaryApi(
          authorization: String,
          apiKey: String, request: BookingSummaryRequest, apiType: String
      ) {

          _loadingState.postValue(LoadingState.LOADING)

          viewModelScope.launch(Dispatchers.IO) {
              _releasedSummary.postValue(
                  sharedRepository.releasedSummary(authorization, apiKey, request).body()
              )
          }
      } */

    fun releasedSummaryApi(
        apiKey: String,
        reservationId: String,
        responseFormat: String,
        locale: String,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            sharedRepository.newReleasedSummary(apiKey, reservationId, responseFormat, locale).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _releasedSummary.postValue(
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


    fun cancellationPoliciesServiceSummmary(
        apiKey: String,
        locale: String,
        responseFormat: Boolean,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            sharedRepository.newCancellationPoliciesServiceSummary(
                apiKey,
                locale,
                responseFormat
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _cancellationPoliciesServiceSummary  .postValue(
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

    fun newFetchNotifications(
        apiKey: String,
        apiType: String,
        pagination: Boolean = false,
        perPage: Int,
        page: Int,
        filterType: String,
        readType: Int,
        currentDay: Int
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            sharedRepository.newFetchNotification(
                apiKey,
                pagination,
                perPage,
                page,
                filterType,
                currentDay,
                readType
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _fetchNotificationModel.postValue(
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

    fun getNotificationDetails(
        authorization: String,
        apiKey: String, request: NotificationDetailsRequest, apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            sharedRepository.notificationDetails(authorization, apiKey, request).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _notificationDetailsModel .postValue(
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

    fun newGetNotificationDetails(
        apiKey: String, notificationId: Int, apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            sharedRepository.newNotificationDetails(apiKey, notificationId).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _notificationDetailsModel .postValue(
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

    fun updateNotification(
        authorization: String,
        apiKey: String, request: UpdateNotificationRequest, apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            sharedRepository.updateNotification(authorization, apiKey, request).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _updateNotificationModel .postValue(
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

    fun dragDropRemarksUpdate(
        reqBody: com.bitla.ts.domain.pojo.drag_drop_remarks_update.request.DragDropRemarksUpdateRequest,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            sharedRepository.dragDropRemarksUpdate(reqBody).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _dragDropRemarks.postValue(
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

    fun frequentTravellersListApi(
        apiKey: String,
        resId: String,
        locale: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            sharedRepository.frequentTravellersApi(
                apiKey,
                resId,
                locale
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _frequentData .postValue(
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