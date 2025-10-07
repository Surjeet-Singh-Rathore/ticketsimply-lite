
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bitla.ts.domain.pojo.SpinnerItemsModifyFare
import com.bitla.ts.domain.pojo.alloted_services.Service
import com.bitla.ts.domain.pojo.service_details_response.Body
import com.bitla.ts.domain.pojo.update_rate_card.fetch_fare_template.response.FetchFareTemplateResponse
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultiStationWiseFareResponse
import com.bitla.ts.presentation.viewModel.BaseViewModel

/**
 * Created by Surjeet Rathore on 14/12/22.
 */
open class SingleViewModel<T : Any?> :
    BaseViewModel() {
    private val _mealInfoLiveData = MutableLiveData<Body>()

//    ----------branch------------
    private val _branchMultiStationFareLiveData = MutableLiveData<MultiStationWiseFareResponse>()
    private val _selectedChannelId = MutableLiveData<String>()
    private val _amountTypeBranch = MutableLiveData<Int>()
    private val _incDecAmountBranch = MutableLiveData<String>()
    private val _selectedIncOrDecBranch = MutableLiveData<Int>()
    private val _includeSeatWiseCheckBranch = MutableLiveData<Boolean>()
    private val _templateValueBranch = MutableLiveData<String>()

//    ----------online agent------------
    private val _onlineMultiStationFareResponseLiveData = MutableLiveData<MultiStationWiseFareResponse>()
    private val _amountTypeOnline = MutableLiveData<Int>()
    private val _incDecAmountOnline = MutableLiveData<String>()
    private val _selectedIncOrDecOnline = MutableLiveData<Int>()
    private val _includeSeatWiseCheckOnline = MutableLiveData<Boolean>()
    private val _templateValueOnline = MutableLiveData<String>()

    //    ----------Ota agent------------
    private val _otaMultiStationFareResponseLiveData = MutableLiveData<MultiStationWiseFareResponse>()
    private val _amountTypeOta = MutableLiveData<Int>()
    private val _incDecAmountOta = MutableLiveData<String>()
    private val _selectedIncOrDecOta = MutableLiveData<Int>()
    private val _includeSeatWiseCheckOta = MutableLiveData<Boolean>()
    private val _templateValueOta = MutableLiveData<String>()

    //    ----------e booking------------
    private val _eBookingMultiStationFareResponseLiveData = MutableLiveData<MultiStationWiseFareResponse>()
    private val _amountTypeEBooking = MutableLiveData<Int>()
    private val _incDecAmountEBooking = MutableLiveData<String>()
    private val _selectedIncOrDecEBooking = MutableLiveData<Int>()
    private val _includeSeatWiseCheckEBooking = MutableLiveData<Boolean>()
    private val _templateValueEBooking = MutableLiveData<String>()

    private val _manageFareApiChannelName = MutableLiveData<String>()
    private val _selectedList = MutableLiveData<MutableList<Service>>()

    // ------------------city pair------------
    private val _selectedOriginId = MutableLiveData<MutableList<SpinnerItemsModifyFare>>()
    private val _selectedDestinationId = MutableLiveData<MutableList<SpinnerItemsModifyFare>>()
    private val _selectedCityPairId = MutableLiveData<MutableList<SpinnerItemsModifyFare>>()

    //-----------------Fetch Template-----------------
    private val _fetchBranchFareTemplateResponse = MutableLiveData<FetchFareTemplateResponse>()
    private val _fetchOnlineFareTemplateResponse = MutableLiveData<FetchFareTemplateResponse>()
    private val _fetchOtaFareTemplateResponse = MutableLiveData<FetchFareTemplateResponse>()
    private val _fetchEBookingFareTemplateResponse = MutableLiveData<FetchFareTemplateResponse>()

    val fetchBranchFareTemplateResponse: LiveData<FetchFareTemplateResponse>
        get() = _fetchBranchFareTemplateResponse

    fun setFetchBranchFareTemplateResponse(fareTemplateResponse: FetchFareTemplateResponse) {
        _fetchBranchFareTemplateResponse.postValue(fareTemplateResponse)
    }

    val fetchOnlineFareTemplateResponse: LiveData<FetchFareTemplateResponse>
        get() = _fetchOnlineFareTemplateResponse
    fun setFetchOnlineFareTemplateResponse(fareTemplateResponse: FetchFareTemplateResponse) {
        _fetchOnlineFareTemplateResponse.postValue(fareTemplateResponse)
    }

    val fetchOtaFareTemplateResponse: LiveData<FetchFareTemplateResponse>
        get() = _fetchOtaFareTemplateResponse
    fun setFetchOtaFareTemplateResponse(fareTemplateResponse: FetchFareTemplateResponse) {
        _fetchOtaFareTemplateResponse.postValue(fareTemplateResponse)
    }

    val fetchEBookingFareTemplateResponse: LiveData<FetchFareTemplateResponse>
        get() = _fetchEBookingFareTemplateResponse
    fun setFetchEBookingFareTemplateResponse(fareTemplateResponse: FetchFareTemplateResponse) {
        _fetchEBookingFareTemplateResponse.postValue(fareTemplateResponse)
    }


    val selectedBranchTemplateValueLiveData: LiveData<String>
        get() = _templateValueBranch
    fun setTemplateValueBranch(templateValue: String) {
        _templateValueBranch.postValue(templateValue)
    }

    val selectedOnlineTemplateValueLiveData: LiveData<String>
        get() = _templateValueOnline
    fun setTemplateValueOnline(templateValue: String) {
        _templateValueOnline.postValue(templateValue)
    }

    val selectedOtaTemplateValueLiveData: LiveData<String>
        get() = _templateValueOta
    fun setTemplateValueOta(templateValue: String) {
        _templateValueOta.postValue(templateValue)
    }

    val selectedEBookingTemplateValueLiveData: LiveData<String>
        get() = _templateValueEBooking
    fun setTemplateValueEBooking(templateValue: String) {
        _templateValueEBooking.postValue(templateValue)
    }



    //----------------City Pair---------------------
    val selectedOriginIdList: LiveData<MutableList<SpinnerItemsModifyFare>>
        get() = _selectedOriginId

    fun setSelectedOriginIdList(selectedSeat: MutableList<SpinnerItemsModifyFare>) {
        _selectedOriginId.postValue(selectedSeat)
    }

    val selectedDestinationIdList: LiveData<MutableList<SpinnerItemsModifyFare>>
        get() = _selectedDestinationId

    fun setSelectedDestinationIdList(selectedSeat: MutableList<SpinnerItemsModifyFare>) {
        _selectedDestinationId.postValue(selectedSeat)
    }

    val selectedCityPairIdList: LiveData<MutableList<SpinnerItemsModifyFare>>
        get() = _selectedCityPairId

    fun setSelectedCityPairIdList(selectedSeat: MutableList<SpinnerItemsModifyFare>) {
        _selectedCityPairId.postValue(selectedSeat)
    }


    val selectedSeat: LiveData<MutableList<Service>>
        get() = _selectedList

    fun setSelectedSeat(selectedSeat: MutableList<Service>) {
        _selectedList.postValue(selectedSeat)
    }

    val mealInfoLiveData: LiveData<Body>
        get() = _mealInfoLiveData

    fun setMealInfo(mealInfo: Body) {
        _mealInfoLiveData.postValue(mealInfo)
    }

    val manageFareApiChannelName: LiveData<String>
        get() = _manageFareApiChannelName
    fun setManageFareApiChannelName(manageFareApiChannelName: String) {
        _manageFareApiChannelName.postValue(manageFareApiChannelName)
    }

    val selectedChannelIdLiveData: LiveData<String>
        get() = _selectedChannelId
    fun setChannelId(channelId: String) {
        _selectedChannelId.postValue(channelId)
    }


    // ----------------------Branch------------------

    val branchMultiStationFareLiveData: LiveData<MultiStationWiseFareResponse>
        get() = _branchMultiStationFareLiveData
    fun setBranchMultiStationFareResponse(branchMultiStationFareResponse: MultiStationWiseFareResponse) {
        _branchMultiStationFareLiveData.postValue(branchMultiStationFareResponse)
    }

    val amountTypeBranch: LiveData<Int>
        get() = _amountTypeBranch
    fun setAmountTypeBranch(amountType: Int) {
        _amountTypeBranch.postValue(amountType)
    }

    val incDecAmountBranch: LiveData<String>
        get() = _incDecAmountBranch
    fun setIncDecAmountBranch(incDecAmount: String) {
        _incDecAmountBranch.postValue(incDecAmount)
    }

    val selectedIncOrDecBranch: LiveData<Int>
        get() = _selectedIncOrDecBranch
    fun setSelectedIncOrDecBranch(selectedIncOrDec: Int) {
        _selectedIncOrDecBranch.postValue(selectedIncOrDec)
    }

    val includeSeatWiseCheckBranch: LiveData<Boolean>
        get() = _includeSeatWiseCheckBranch
    fun setIncludeSeatWiseCheckBranch(includeSeatWiseCheck: Boolean) {
        _includeSeatWiseCheckBranch.postValue(includeSeatWiseCheck)
    }

//    ----------------------Online Agent---------------------
    val onlineMultiStationFareResponseLiveData: LiveData<MultiStationWiseFareResponse>
        get() = _onlineMultiStationFareResponseLiveData
    fun setOnlineMultiStationFareResponse(onlineMultiStationFareResponse: MultiStationWiseFareResponse) {
        _onlineMultiStationFareResponseLiveData.postValue(onlineMultiStationFareResponse)
    }

    val amountTypeOnline: LiveData<Int>
        get() = _amountTypeOnline
    fun setAmountTypeOnline(amountType: Int) {
        _amountTypeOnline.postValue(amountType)
    }

    val incDecAmountOnline: LiveData<String>
        get() = _incDecAmountOnline
    fun setIncDecAmountOnline(incDecAmount: String) {
        _incDecAmountOnline.postValue(incDecAmount)
    }

    val selectedIncOrDecOnline: LiveData<Int>
        get() = _selectedIncOrDecOnline
    fun setSelectedIncOrDecOnline(selectedIncOrDec: Int) {
        _selectedIncOrDecOnline.postValue(selectedIncOrDec)
    }

    val includeSeatWiseCheckOnline: LiveData<Boolean>
        get() = _includeSeatWiseCheckOnline
    fun setIncludeSeatWiseCheckOnline(includeSeatWiseCheck: Boolean) {
        _includeSeatWiseCheckOnline.postValue(includeSeatWiseCheck)
    }


    // ---------------OTA-----------------------
    val oTAMultiStationFareResponseLiveData: LiveData<MultiStationWiseFareResponse>
        get() = _otaMultiStationFareResponseLiveData
    fun setOtaMultiStationFareResponse(otaMultiStationFareResponse: MultiStationWiseFareResponse) {
        _otaMultiStationFareResponseLiveData.postValue(otaMultiStationFareResponse)
    }

    val incDecAmountOta: LiveData<String>
        get() = _incDecAmountOta
    fun setIncDecAmountOta(incDecAmount: String) {
        _incDecAmountOta.postValue(incDecAmount)
    }

    val amountTypeOta: LiveData<Int>
        get() = _amountTypeOta
    fun setAmountTypeOta(amountType: Int) {
        _amountTypeOta.postValue(amountType)
    }

    val selectedIncOrDecOta: LiveData<Int>
        get() = _selectedIncOrDecOta
    fun setSelectedIncOrDecOta(selectedIncOrDec: Int) {
        _selectedIncOrDecOta.postValue(selectedIncOrDec)
    }

    val includeSeatWiseCheckOta: LiveData<Boolean>
        get() = _includeSeatWiseCheckOta
    fun setIncludeSeatWiseCheckOta(includeSeatWiseCheck: Boolean) {
        _includeSeatWiseCheckOta.postValue(includeSeatWiseCheck)
    }


    // ---------------E-booking-----------------------
    val eBookingMultiStationFareResponseLiveData: LiveData<MultiStationWiseFareResponse>
        get() = _eBookingMultiStationFareResponseLiveData
    fun setEBookingMultiStationFareResponse(eBookingMultiStationFareResponse: MultiStationWiseFareResponse) {
        _eBookingMultiStationFareResponseLiveData.postValue(eBookingMultiStationFareResponse)
    }

    val incDecAmountEBooking: LiveData<String>
        get() = _incDecAmountEBooking
    fun setIncDecAmountEBooking(incDecAmount: String) {
        _incDecAmountEBooking.postValue(incDecAmount)
    }

    val amountTypeEBooking: LiveData<Int>
        get() = _amountTypeEBooking
    fun setAmountTypeEBooking(amountType: Int) {
        _amountTypeEBooking.postValue(amountType)
    }

    val selectedIncOrDecEBooking: LiveData<Int>
        get() = _selectedIncOrDecEBooking
    fun setSelectedIncOrDecEBooking(selectedIncOrDec: Int) {
        _selectedIncOrDecEBooking.postValue(selectedIncOrDec)
    }

    val includeSeatWiseCheckEBooking: LiveData<Boolean>
        get() = _includeSeatWiseCheckEBooking
    fun setIncludeSeatWiseCheckEBooking(includeSeatWiseCheck: Boolean) {
        _includeSeatWiseCheckEBooking.postValue(includeSeatWiseCheck)
    }


}