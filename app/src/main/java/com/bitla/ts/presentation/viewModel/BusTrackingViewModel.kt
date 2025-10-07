package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.your_bus_location.YourBusLocation
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class BusTrackingViewModel<T : Any?> :
    ViewModel() {

    private var _loginModel = MutableLiveData<LoginModel>()
    val loginModel: LiveData<LoginModel>
        get() = _loginModel


    var routeId=MutableLiveData<String>("")
    var apiTravelId=MutableLiveData<String>("")
     var serviceTravelDate = MutableLiveData<String>("")
     var serviceBusType = MutableLiveData<String>("")
     var deptTime = MutableLiveData<String>("")
     var origin = MutableLiveData<String>("")
     var destination = MutableLiveData<String>("")
    companion object {
        val TAG: String = BusTrackingViewModel::class.java.simpleName
    }

     val locations = MutableLiveData(mutableListOf(
        LatLng(0.0, 0.0),  // Jaipur, Rajasthan
    ))



    private val _currentLocationIndex = MutableLiveData(0)
    val currentLocationIndex: LiveData<Int>
        get() = _currentLocationIndex

    private val _srcDest = MutableLiveData<String>()
    val srcDest : LiveData<String> get() = _srcDest

    private val _coachNumber = MutableLiveData<String>()
    val coachNumber : LiveData<String> get() = _coachNumber


    private val _deptDateTime = MutableLiveData<String>()
    val deptDateTime : LiveData<String> get() = _deptDateTime

    private val _serviceNumber = MutableLiveData<String>()
    val serviceNumber : LiveData<String> get() = _serviceNumber

    private val _busLocation = MutableLiveData<YourBusLocation>()
    val busLocation: LiveData<YourBusLocation>
        get() = _busLocation

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

  val okHttpClient=  OkHttpClient.Builder().addInterceptor(loggingInterceptor). build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://platform.yourbus.in/")
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    private val apiInterface: ApiInterface = retrofit.create(ApiInterface::class.java)

    fun callYourBusLocation() {
        apiInterface.yourBusFetchBusLocation(apiTravelId.value?:"", routeId.value?:"")
            .enqueue(object : Callback<YourBusLocation> {
                override fun onResponse(call: Call<YourBusLocation>, response: Response<YourBusLocation>) {
                    if (response.isSuccessful) {
                        _busLocation.value = response.body()
                    } else {
                        _errorMessage.value = response.message()
                    }
                }

                override fun onFailure(call: Call<YourBusLocation>, t: Throwable) {
                    _errorMessage.value = t.message
                }
            })
    }



    fun updateLocation()
    {
        _currentLocationIndex.value = (_currentLocationIndex.value?.plus(1))
    }

    fun setSrcDest(sourceDest : String)
    {
        _srcDest.value = sourceDest
    }

    fun setCoachNumber(coach : String)
    {
        _coachNumber.value = coach
    }


    fun setDepartureDateAndTime(deptTimeDate : String)
    {
        _deptDateTime.value = deptTimeDate
    }

    fun setServiceNumber(number : String)
    {
        _serviceNumber.value = number
    }


    fun setLoginData(data : LoginModel)
    {
        _loginModel.value = data
    }

     var job: Job? = null
    fun updateMarkerLocation() {
            if((job == null)) {
                job = viewModelScope.launch {
                    while (isActive) {
                        callYourBusLocation()
                        delay(PreferenceUtils.getLocationApiInterval().toLong())


                    }
                }
            }
    }




}