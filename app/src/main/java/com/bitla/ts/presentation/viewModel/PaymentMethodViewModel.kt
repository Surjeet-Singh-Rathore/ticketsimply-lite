package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bitla.ts.domain.pojo.destination_pair.SearchModel
import com.google.gson.JsonArray

class PaymentMethodViewModel : ViewModel() {

    val _selectedMethod = MutableLiveData<SearchModel>()
    val selectedMethod = MutableLiveData<String>()

    var sourceId: String = ""
    var pnrNumber: String = ""
    var destinationId: String = ""
    var reservationId: String = ""
    var source: String? = ""
    var destination: String? = ""
    var noOfSeats: String? = ""
    var travelDate: String = ""
    var busType: String? = null
    var deptTime: String? = null
    var arrTime: String? = null
    var deptDate: String? = null
    var arrDate: String? = null
    var boardingPoint: String? = null
    var droppingPoint: String? = null
    var droppingId: String? = ""
    var boardingId: String? = ""
    var seatNumbers: String? = null
    var totalFare = 0.0
    var totalFareString = ""
    var isOnBehalfOfAgent = false
    var selectedBoardingPoint = ""
    var selectedUserCity = ""
    var selectedUser = ""
    var selectedTravelBranch = ""
    var selectedOfflineAgentId = ""
    var selectedOnlineAgentId = ""
    var agentTypeId = ""
    var passengerDetailsList: JsonArray? = null
    var stopRunningApi: Boolean = false


    fun setSelectedMethod(method: String) {
        selectedMethod.value = method
    }
}

