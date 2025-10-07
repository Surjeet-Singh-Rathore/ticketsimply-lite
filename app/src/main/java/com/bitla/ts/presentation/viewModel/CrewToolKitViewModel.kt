package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.crew_delete_image.CrewDeleteImage
import com.bitla.ts.domain.pojo.crew_toolkit.CrewToolKIt
import com.bitla.ts.domain.pojo.crew_toolkit.request.ReqBody
import com.bitla.ts.domain.pojo.crew_update.UpdateCrew
import com.bitla.ts.domain.pojo.crew_upload_image.CrewUploadImage
import com.bitla.ts.domain.repository.CrewToolKitRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CrewToolKitViewModel<T : Any?>(private val crewToolKitRepository: CrewToolKitRepository) :
    ViewModel() {

    companion object {
        val TAG: String = CrewToolKitViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _fetchToolKit = MutableLiveData<CrewToolKIt>()
    val fetchToolKit: LiveData<CrewToolKIt>
        get() = _fetchToolKit

    private val _uploadCrewImage = MutableLiveData<CrewUploadImage>()
    val uploadCrewImage: LiveData<CrewUploadImage>
        get() = _uploadCrewImage

    private val _deleteCrewImage = MutableLiveData<CrewDeleteImage>()
    val deleteCrewImage: LiveData<CrewDeleteImage>
        get() = _deleteCrewImage

    private val _updateCrewList = MutableLiveData<UpdateCrew>()
    val updateCrewList: LiveData<UpdateCrew>
        get() = _updateCrewList


    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()


    /*fun fetchToolKit(
        authorization: String,
        apiKey: String,
        toolKitRequest: CrewToolKitRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            _fetchToolKit.postValue(
                crewToolKitRepository.fetchCrewCheckList(
                    authorization,
                    apiKey,
                    crewToolKitRequest = toolKitRequest
                ).body()
            )
        }
    }  */

    fun fetchToolKit(
        toolKitRequest: ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            crewToolKitRepository.newFetchCrewCheckList(
                crewToolKitRequest = toolKitRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _fetchToolKit.postValue(
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

    /*fun uploadCrewImageApi(
        authorization: String,
        apiKey: String,
        bccId: RequestBody,
        methodName: RequestBody,
        format: RequestBody,
        apiKey1: RequestBody,
        resId: RequestBody,
        goodsId: RequestBody,
        goodsImageId: RequestBody,
        goodsImage: MultipartBody.Part,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            _uploadCrewImage.postValue(
                crewToolKitRepository.uploadCrewImage(
                    authorization,
                    apiKey,
                    bccId,
                    methodName,
                    format,
                    apiKey1,
                    resId,
                    goodsId,
                    goodsImageId,
                    goodsImage
                ).body()
            )
        }
    }*/

    fun uploadCrewImageApi(
        apiKey: String,
        locale: String,
        format: RequestBody,
        resId: RequestBody,
        goodsId: RequestBody,
        goodsImageId: RequestBody,
        goodsImage: MultipartBody.Part,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            crewToolKitRepository.newUploadCrewImage(
                apiKey,
                locale,
                format,
                resId,
                goodsId,
                goodsImageId,
                goodsImage
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _uploadCrewImage .postValue(
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

    /*fun deleteCrewImageApi(
        authorization: String,
        apiKey: String,
        crewDeleteImageRequest: CrewDeleteImageRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            _deleteCrewImage.postValue(
                crewToolKitRepository.deleteCrewImage(
                    authorization,
                    apiKey,
                    deleteImageRequest = crewDeleteImageRequest
                ).body()
            )
        }
    }  */

    fun deleteCrewImageApi(
        locale: String,
        crewDeleteImageRequest: com.bitla.ts.domain.pojo.crew_delete_image.request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            crewToolKitRepository.newDeleteCrewImage(
                locale,
                deleteImageRequest = crewDeleteImageRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _deleteCrewImage .postValue(
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

  /*  fun updateCrewCheckListApi(
        authorization: String,
        apiKey: String,
        updateCrewRequest: UpdateCrewRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            _updateCrewList.postValue(
                crewToolKitRepository.updateCrewCheckList(
                    authorization,
                    apiKey,
                    updateCrewRequest = updateCrewRequest
                ).body()
            )
        }
    } */

    fun updateCrewCheckListApi(
        apiKey: String,
        locale: String,
        updateCrewRequest: com.bitla.ts.domain.pojo.crew_update.request.ReqBody,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            crewToolKitRepository.newUpdateCrewCheckList(
                apiKey,
                locale,
                updateCrewRequest = updateCrewRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _updateCrewList .postValue(
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