
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bitla.ts.domain.pojo.SpinnerItemsModifyFare
import com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.response.FetchRouteWiseFareResponse
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.ViewRateCardResponse
import com.bitla.ts.domain.pojo.alloted_services.Service
import com.bitla.ts.presentation.viewModel.BaseViewModel


open class AddRateCardSingleViewModel<T : Any?> : BaseViewModel() {

    private val _viewRateCardFareLiveData = MutableLiveData<ViewRateCardResponse>()
    private val _fetchRouteWiseFareLiveData = MutableLiveData<FetchRouteWiseFareResponse>()
    private val _amountTypeBranch = MutableLiveData<Int>()
    private val _incDecAmountBranch = MutableLiveData<String>()
    private val _selectedIncOrDecBranch = MutableLiveData<Int>()
    private val _includeSeatWiseCheckBranch = MutableLiveData<Boolean>()
    private val _selectedList = MutableLiveData<MutableList<Service>>()

    // ------------------city pair------------
    private val _selectedOriginId = MutableLiveData<MutableList<SpinnerItemsModifyFare>>()
    private val _selectedDestinationId = MutableLiveData<MutableList<SpinnerItemsModifyFare>>()
    private val _selectedCityPairId = MutableLiveData<MutableList<SpinnerItemsModifyFare>>()



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


    // ----------------------Branch------------------

    val branchViewRateCardLiveData: LiveData<ViewRateCardResponse>
        get() = _viewRateCardFareLiveData
    fun setBranchViewRateResponse(viewRateCardFareResponse: ViewRateCardResponse) {
        _viewRateCardFareLiveData.postValue(viewRateCardFareResponse)
    }



    val branchFetchRouteWiseFareLiveData: LiveData<FetchRouteWiseFareResponse>
        get() = _fetchRouteWiseFareLiveData
    fun setBranchFetchRouteWiseFareResponse(fetchRouteWiseFareResponse: FetchRouteWiseFareResponse) {
        _fetchRouteWiseFareLiveData.postValue(fetchRouteWiseFareResponse)
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

}