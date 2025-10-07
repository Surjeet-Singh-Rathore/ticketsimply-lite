package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.domain_model.DomainModel
import com.bitla.ts.domain.pojo.dynamic_domain.DynamicDomain
import com.bitla.ts.domain.pojo.your_bus_location.YourBusLocation
import com.bitla.ts.domain.repository.DomainRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.regex.Matcher
import java.util.regex.Pattern


class DomainViewModel(private val domainRepository: DomainRepository) : ViewModel() {

    companion object {
        val TAG: String = DomainViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()

    val messageSharedFlow = MutableSharedFlow<String>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _data = MutableLiveData<DomainModel>()
    val dataDomain: LiveData<DomainModel>
        get() = _data

    private val _dataDynamicDomain = MutableLiveData<DynamicDomain>()
    val dataDynamicDomain: LiveData<DynamicDomain>
        get() = _dataDynamicDomain

    private val _validation = MutableLiveData<String>()
    val validationData: LiveData<String>
        get() = _validation


    fun initDynamicDomain() {
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            domainRepository.initDomain().collect{
                when(it){
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _dataDynamicDomain.postValue(
                            it.data
                        )
                    }
                    is NetworkProcess.Failure -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        if (it.message.contains("No address associated with hostname")){
                            messageSharedFlow.emit("Please enter valid domain")
                        }else{
                            messageSharedFlow.emit(it.message)
                        }

                    }
                }
            }

        }
    }


    fun isValidDomain(str: String?): Boolean {
        // Regex to check valid domain name.
        val regex = ("^((?!-)[A-Za-z0-9-]"
                + "{1,63}(?<!-)\\.)"
                + "+[A-Za-z]{2,6}")

        val p: Pattern = Pattern.compile(regex)
        if (str == null) {
            return false
        }
        val m: Matcher = p.matcher(str)
        return m.matches()
    }

    fun validation(domain: String) {
        when {
            domain.isEmpty() -> _validation.postValue("Please enter domain")
            !isValidDomain(domain) -> _validation.postValue("Please enter valid domain")
            else -> _validation.postValue("")
        }
    }

}