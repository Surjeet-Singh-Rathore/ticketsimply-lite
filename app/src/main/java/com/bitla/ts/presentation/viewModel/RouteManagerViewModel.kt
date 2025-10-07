package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.activate_deactivate_route.ActivateDeactivateResponse
import com.bitla.ts.domain.pojo.city_pair.CityPairResponse
import com.bitla.ts.domain.pojo.coach_type.CoachTypeListResponse
import com.bitla.ts.domain.pojo.create_route.CreateRouteResponse
import com.bitla.ts.domain.pojo.create_stage_data.CreateStageResponse
import com.bitla.ts.domain.pojo.delete_stage.DeleteStageResponse
import com.bitla.ts.domain.pojo.duplicate_service.DuplicateServiceResponse
import com.bitla.ts.domain.pojo.get_route.GetRouteData
import com.bitla.ts.domain.pojo.get_route.GetRouteResponse
import com.bitla.ts.domain.pojo.hub_dropdown.HubDropdownResponse
import com.bitla.ts.domain.pojo.modify_route.ModifyRouteResponse
import com.bitla.ts.domain.pojo.preview_route.PreviewRouteResponse
import com.bitla.ts.domain.pojo.route_list.RouteListResponse
import com.bitla.ts.domain.pojo.route_manager.CitiesListResponse
import com.bitla.ts.domain.pojo.stage_for_city.StageListData
import com.bitla.ts.domain.pojo.stage_for_city.StageListResponse
import com.bitla.ts.domain.pojo.update_route.UpdateRouteResponse
import com.bitla.ts.domain.pojo.update_route.ViaCitiesData
import com.bitla.ts.domain.repository.RouteManagerRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.Event
import com.bitla.ts.utils.LoadingState
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class RouteManagerViewModel<T : Any?>(private val routeManagerRepository: RouteManagerRepository) : ViewModel() {

    var isViewInitialized = false

    private val _loadingState = MutableLiveData<LoadingState>()

    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _getCitiesList = MutableLiveData<CitiesListResponse>()
    val getCitiesList: LiveData<CitiesListResponse> = _getCitiesList

    private val _getRouteList = MutableLiveData<RouteListResponse>()

    val getRouteList: LiveData<RouteListResponse>
        get() = _getRouteList

    private val _getHubDropdown = MutableLiveData<HubDropdownResponse>()

    val getHubDropdown: LiveData<HubDropdownResponse>
            get() = _getHubDropdown

    private val _getCoachTypeList = MutableLiveData<CoachTypeListResponse>()

    val getCoachTypeList: LiveData<CoachTypeListResponse>
        get() = _getCoachTypeList

    private val _getCityPair = MutableLiveData<Event<CityPairResponse>>()

    val getCityPairs: LiveData<Event<CityPairResponse>>
        get() = _getCityPair

    private val _getStageList = MutableLiveData<StageListResponse>()

    val getStageList: LiveData<StageListResponse>
        get() = _getStageList

    private val _getRouteActivateDeactivateStatus = MutableLiveData<ActivateDeactivateResponse>()

    val getRouteActivateDeactivateStatus: LiveData<ActivateDeactivateResponse>
        get() = _getRouteActivateDeactivateStatus

    val _getCreateRouteStatus = MutableLiveData<Event<CreateRouteResponse>>()

    val getCreateRouteStatus: LiveData<Event<CreateRouteResponse>>
        get() = _getCreateRouteStatus


    private val _getUpdateRouteStatus = MutableLiveData<UpdateRouteResponse>()


    val routeId = MutableLiveData<Int>()
    val commissionJsonData = MutableLiveData<JsonObject>()
    val viaCitiesList = MutableLiveData<ArrayList<ViaCitiesData>>()
    val routeJsonObject = MutableLiveData<JsonObject>()
    val routeDataArray = MutableLiveData<GetRouteData>()
    val commisionJsonObject = MutableLiveData<JsonObject>()
    val multicityFareJsonObject = MutableLiveData<JsonObject>()
    val currentSeatTypes = MutableLiveData<String>()
    val viaCitiesData = MutableLiveData<JsonObject>()
    val boardingPointJson = MutableLiveData<JsonObject>()
    val droppingPointJson = MutableLiveData<JsonObject>()
    val stageList = MutableLiveData<ArrayList<StageListData>>()
    val boardingPointList = MutableLiveData<ArrayList<ViaCitiesData>>()
    val droppingPointList = MutableLiveData<ArrayList<ViaCitiesData>>()
    val toolbarTitle = MutableLiveData<String>()
    val toolbarSubtitle = MutableLiveData<String>()
    val isAcCoach = MutableLiveData<Boolean>()



    val isEdit = MutableLiveData<Boolean>()

    val getUpdateRouteStatus: LiveData<UpdateRouteResponse>
        get() = _getUpdateRouteStatus


    private val _getRouteData = MutableLiveData<Event<GetRouteResponse>>()

    val getRouteData: LiveData<Event<GetRouteResponse>>
        get() = _getRouteData

    private val _createStageData = MutableLiveData<CreateStageResponse>()

    val createStageData: LiveData<CreateStageResponse>
        get() = _createStageData


    private val _getModifyRouteStatus = MutableLiveData<Event<ModifyRouteResponse>>()

    val getModifyRouteStatus: LiveData<Event<ModifyRouteResponse>>
        get() = _getModifyRouteStatus


    private val _getDeleteStageStatus = MutableLiveData<Event<DeleteStageResponse>>()

    val getDeleteStageStatus: LiveData<Event<DeleteStageResponse>>
        get() = _getDeleteStageStatus


    private val _getDuplicateServiceResponse = MutableLiveData<DuplicateServiceResponse>()

    val getDuplicateServiceResponse: LiveData<DuplicateServiceResponse>
        get() = _getDuplicateServiceResponse

    private val _getPreviewRouteResponse = MutableLiveData<PreviewRouteResponse>()

    val getPreviewRouteResponse: LiveData<PreviewRouteResponse>
        get() = _getPreviewRouteResponse

     var _selectedSourceId = MutableLiveData<String>("-1")

    val selectedSourceId: LiveData<String>
        get() = _selectedSourceId


    var _selectedDestinationId = MutableLiveData<String>("-1")

    val selectedDestinationId: LiveData<String>
        get() = _selectedDestinationId


    val errorMessage = MutableSharedFlow<String>()


    fun getCitiesListApi(
        apiKey: String,
        responseFormat: String,
        locale: String

    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
                routeManagerRepository.getCities(apiKey, responseFormat, locale).collect{
                    when(it){
                        is NetworkProcess.Loading->{}
                        is NetworkProcess.Success->{
                            _getCitiesList.postValue(it.data)
                        }
                        is NetworkProcess.Failure->{}
                    }
                }
        }
    }


    fun getRouteListApi(
        originId : String,
        destinationId : String,
        apiKey: String,
        responseFormat: String,
        locale: String,
        filterValue: String,
        page: String,
        perPage: String,
        search: String,
        filterType : String
    ) {
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
                routeManagerRepository.getRouteList(originId,destinationId,apiKey,responseFormat,locale,filterValue,page,perPage,search,filterType).collect{
                    when(it){
                        is NetworkProcess.Loading->{}
                        is NetworkProcess.Success->{
                            _getRouteList.postValue(it.data)
                        }
                        is NetworkProcess.Failure->{}
                    }
                }

        }
    }

    fun getHubDropdownApi(
        apiKey: String,
        locale: String,
        responseFormat: String

    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
                routeManagerRepository.getHubDropDown(apiKey, locale,responseFormat).collect{
                    when(it){
                        is NetworkProcess.Loading->{}
                        is NetworkProcess.Success->{
                            _getHubDropdown.postValue((it.data))
                        }
                        is NetworkProcess.Failure->{}
                    }
                }

        }
    }

    fun getCoachTypeApi(
        apiKey: String,
        locale: String,
        responseFormat: String

    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
                routeManagerRepository.getCoachType(apiKey, locale,responseFormat).collect{
                    when(it){
                        is NetworkProcess.Loading->{}
                        is NetworkProcess.Success->{
                            _getCoachTypeList.postValue(it.data)
                        }
                        is NetworkProcess.Failure->{}
                    }
                }

        }
    }

    fun getCityPairApi(
        apiKey: String,
        responseFormat: String,
        locale: String,
        reqCitiesBody: Any
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
                routeManagerRepository.getCityPair(apiKey,responseFormat,locale,reqCitiesBody).collect{
                    when(it){
                        is NetworkProcess.Loading->{}
                        is NetworkProcess.Success->{
                            _getCityPair.postValue(Event(it.data))
                        }
                        is NetworkProcess.Failure->{}
                    }
                }

        }
    }


    fun getStageListApi(
        cityId: String,
        apiKey: String,
        operatorApiKey: String,
        responseFormat: String,
        locale: String,
        routeId: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
                routeManagerRepository.getStage(cityId,apiKey,operatorApiKey,responseFormat,locale,routeId).collect{
                    when(it){
                        is NetworkProcess.Loading->{}
                        is NetworkProcess.Success->{
                            _getStageList.postValue(it.data)
                        }
                        is NetworkProcess.Failure->{}
                    }
                }

        }
    }

    fun getRouteActivateDeactivateStatusApi(
        apiKey: String,
        locale: String,
        responseFormat: String,
        activateDeactivateRouteReqBody: Any
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
                routeManagerRepository.getRouteActivateDeactivateStatus(apiKey,locale,responseFormat,activateDeactivateRouteReqBody).collect{
                    when(it){
                        is NetworkProcess.Loading->{}
                        is NetworkProcess.Success->{
                            _getRouteActivateDeactivateStatus.postValue(it.data)
                        }
                        is NetworkProcess.Failure->{}
                    }
                }

        }
    }

    fun createRouteApi(
        apiKey: String,
        locale: String,
        responseFormat: String,
        createRouteRequestBody: Any
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
                routeManagerRepository.createRoute(apiKey,locale,responseFormat,createRouteRequestBody).collect{
                    when(it){
                        is NetworkProcess.Loading->{}
                        is NetworkProcess.Success->{
                            _getCreateRouteStatus.postValue(Event(it.data))
                        }
                        is NetworkProcess.Failure->{}
                    }
                }

        }
    }

    fun updateRouteApi(
        apiKey: String,
        locale: String,
        responseFormat: String,
        routeId: String,
        updateRouteRequestBody: JsonObject?
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
                routeManagerRepository.updateRoute(apiKey,locale,responseFormat,routeId,updateRouteRequestBody).collect{
                    when(it){
                        is NetworkProcess.Loading->{}
                        is NetworkProcess.Success->{
                            _getUpdateRouteStatus.postValue(it.data)
                        }
                        is NetworkProcess.Failure->{}
                    }
                }

        }
    }

    fun getRouteDataApi(
        apiKey: String,
        locale: String,
        responseFormat: String,
        routeId: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {

                routeManagerRepository.getRouteData(apiKey,locale,responseFormat,routeId).collect{
                    when(it){
                        is NetworkProcess.Loading->{}
                        is NetworkProcess.Success->{
                            _getRouteData.postValue(Event(it.data))
                        }
                        is NetworkProcess.Failure->{}
                    }
                }
        }
    }


    fun modifyRouteApi(
        apiKey: String,
        locale: String,
        responseFormat: String,
        routeId: String,
        step: String,
        modifyRouteBody: Any
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            routeManagerRepository.modifyRoute(apiKey,locale,responseFormat,routeId,step,modifyRouteBody).collect{
                when(it){
                    is NetworkProcess.Loading->{}
                    is NetworkProcess.Success->{
                        _getModifyRouteStatus.postValue(Event(it.data))
                    }
                    is NetworkProcess.Failure->{
                        errorMessage.emit(it.message)
                    }
                }
            }

        }
    }

    fun createStageApi(
        apiKey: String,
        locale: String,
        responseFormat: String,
        routeId: String,
        body: Any
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            routeManagerRepository.createRouteStagesApi(apiKey,locale,responseFormat,routeId,body).collect{
                when(it){
                    is NetworkProcess.Loading->{}
                    is NetworkProcess.Success->{
                        _createStageData.postValue(it.data)
                    }
                    is NetworkProcess.Failure->{}
                }
            }
            
        }
    }


    fun deleteStageApi(
        apiKey: String,
        locale: String,
        operatorApiKey: String,
        responseFormat: String,
        reqBody: Any
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
                routeManagerRepository.deleteStage(apiKey,locale,operatorApiKey,responseFormat,reqBody).collect{
                    when(it){
                        is NetworkProcess.Loading->{}
                        is NetworkProcess.Success->{
                            _getDeleteStageStatus.postValue(Event(it.data))
                        }
                        is NetworkProcess.Failure->{}
                    }
                }
        }
    }

    /*fun duplicateServiceApi(
        apiKey: String,
        locale: String,
        operatorApiKey: String,
        responseFormat: String,
        routeId: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            _getDuplicateServiceResponse.postValue(
                routeManagerRepository.duplicateService(apiKey,locale,operatorApiKey,responseFormat,routeId).body()
            )
        }
    }*/

    fun previewRouteApi(
        apiKey: String,
        locale: String,
        routeId: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
                routeManagerRepository.previewRoute(apiKey,locale,routeId).collect{
                    when(it){
                        is NetworkProcess.Loading->{}
                        is NetworkProcess.Success->{
                            _getPreviewRouteResponse.postValue(it.data)
                        }
                        is NetworkProcess.Failure->{}
                    }
                }
        }
    }




}