package com.bitla.ts.presentation.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.add_bp_dp_to_service.request.AddBpDpToServiceRequest
import com.bitla.ts.domain.pojo.add_bp_dp_to_service.response.AddBpDpToServiceResponse
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.merge_bus_seat_mapping.request.MergeBusSeatMappingRequest
import com.bitla.ts.domain.pojo.merge_bus_seat_mapping.request.Seat
import com.bitla.ts.domain.pojo.merge_bus_seat_mapping.request.SeatShiftMap
import com.bitla.ts.domain.pojo.merge_bus_seat_mapping.response.MergeBusSeatMappingResponse
import com.bitla.ts.domain.pojo.merge_bus_shift_passenger.request.MergeBusShiftPassengerRequest
import com.bitla.ts.domain.pojo.merge_bus_shift_passenger.response.MergeBusShiftPassengerResponse
import com.bitla.ts.domain.pojo.merge_service_details.request.MergeServiceDetailsRequest
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.recommended_seats.request.RecommendedSeatsRequest
import com.bitla.ts.domain.pojo.recommended_seats.response.RecommendedSeatsResponse
import com.bitla.ts.domain.pojo.samePNRSeatModel.SamePNRSeatModel
import com.bitla.ts.domain.pojo.samePNRSeatModel.SeatShiftModel
import com.bitla.ts.domain.repository.MergeBusShiftRepository
import com.bitla.ts.utils.Event
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.example.buscoach.multistation_data.MultiHopSeatDetail
import com.example.buscoach.multistation_data.MultiStationRespBody
import com.example.buscoach.service_details_response.SeatDetail
import com.example.buscoach.service_details_response.ServiceDetailsModel
import com.google.gson.GsonBuilder
import containsLeftSideSeatIfLeftSideSeatIsPassedInParam
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import removeNonPairedSeats
import removePNRGroupAndDeselectSeats
import removePNRGroupAndDeselectSeatsNew

class MergeBusSharedViewModel(private val mergeBusShiftRepository: MergeBusShiftRepository): ViewModel() {

    var privileges :PrivilegeResponseModel?= null
    var _reservationIdLeftCoach = MutableLiveData<String>()
    val reservationIdLeftCoach: LiveData<String>
        get() = _reservationIdLeftCoach

    var _reservationIdRightCoach = MutableLiveData<String>()
    val reservationIdRightCoach: LiveData<String>
        get() = _reservationIdRightCoach

    private val _loginModel = MutableLiveData<LoginModel>()
    val loginModel: LiveData<LoginModel>
        get() = _loginModel

    private val _privilegeDetailsModel = MutableLiveData<PrivilegeResponseModel>()
    val privilegeDetailsModel: LiveData<PrivilegeResponseModel>
        get() = _privilegeDetailsModel

    private val _locale = MutableLiveData<String>()
    val locale: LiveData<String>
        get() = _locale

    private val _samePNRSeatModelList = MutableLiveData<MutableList<SamePNRSeatModel>>()
    val samePNRSeatModelList: LiveData<MutableList<SamePNRSeatModel>>
        get() = _samePNRSeatModelList

    val _serviceDetailsLeftCoach = MutableLiveData<ServiceDetailsModel>()
    val serviceDetailsLeftCoach: LiveData<ServiceDetailsModel>
        get() = _serviceDetailsLeftCoach

    private val _recommendedSeatsRightCoach = MutableLiveData<RecommendedSeatsResponse?>()
    val recommendedSeatsRightCoach: LiveData<RecommendedSeatsResponse?>
        get() = _recommendedSeatsRightCoach

    private val _mergeServiceDetailsRightCoach = MutableLiveData<ServiceDetailsModel>()
    val mergeServiceDetailsRightCoach: LiveData<ServiceDetailsModel>
        get() = _mergeServiceDetailsRightCoach

    private val _mergeBusShiftPassenger = MutableLiveData<MergeBusShiftPassengerResponse>()
    val mergeBusShiftPassenger: LiveData<MergeBusShiftPassengerResponse>
        get() = _mergeBusShiftPassenger


    private val _mergeBusSeatMapping = MutableLiveData<MergeBusSeatMappingResponse>()
    val mergeBusSeatMapping: LiveData<MergeBusSeatMappingResponse>
        get() = _mergeBusSeatMapping


    private val _mergeBusSeatMappingRedirection = MutableLiveData<Event<MergeBusSeatMappingResponse>>()
    val mergeBusSeatMappingRedirection: LiveData<Event<MergeBusSeatMappingResponse>>
        get() = _mergeBusSeatMappingRedirection


    private val _addBpDpToService = MutableLiveData<AddBpDpToServiceResponse>()
    val addBpDpToService: LiveData<AddBpDpToServiceResponse>
        get() = _addBpDpToService


    private val _currentSelectedPNR = MutableLiveData<String?>()
    val currentSelectedPNR: LiveData<String?>
        get() = _currentSelectedPNR

    private val _currentSelectedSeat = MutableLiveData<SeatDetail?>()
    val currentSelectedSeat: LiveData<SeatDetail?>
        get() = _currentSelectedSeat

    private val _deSelectSpecificSeatRightCoach = MutableLiveData<SeatDetail>()
    val deSelectSpecificSeatRightCoach: LiveData<SeatDetail>
        get() = _deSelectSpecificSeatRightCoach

    private val _drawBorderAroundPNRGroupLeftCoach = MutableLiveData<SeatDetail>()
    val drawBorderAroundPNRGroupLeftCoach: LiveData<SeatDetail>
        get() = _drawBorderAroundPNRGroupLeftCoach

    private val _deSelectSpecificSeatLeftCoach = MutableLiveData<SeatDetail>()
    val deSelectSpecificSeatLeftCoach: LiveData<SeatDetail>
        get() = _deSelectSpecificSeatLeftCoach


    private val _selectSpecificSeatLeftCoach = MutableLiveData<SeatDetail>()
    val selectSpecificSeatLeftCoach: LiveData<SeatDetail>
        get() = _selectSpecificSeatLeftCoach


    private val _selectSpecificSeatRightCoach = MutableLiveData<SeatDetail>()
    val selectSpecificSeatRightCoach: LiveData<SeatDetail>
        get() = _selectSpecificSeatRightCoach


    private val _isRightCoachServiceApiCalled = MutableLiveData<Boolean>()
    val isRightCoachServiceApiCalled : LiveData<Boolean>
        get() = _isRightCoachServiceApiCalled

     var _shiftSeatsHashMap = LinkedHashMap<SeatDetail, SeatDetail?>()
     val _shiftSeatsHashMapMutableLiveData = MutableLiveData<LinkedHashMap<SeatDetail, SeatDetail?>>()
    val shiftSeatsHashMapLiveData: LiveData<LinkedHashMap<SeatDetail, SeatDetail?>>
        get() = _shiftSeatsHashMapMutableLiveData

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _removeSelectedPNRGroupBorderLeftCoach = MutableLiveData<Boolean>()
    val removeSelectedPNRGroupBorderLeftCoach: LiveData<Boolean>
        get() = _removeSelectedPNRGroupBorderLeftCoach

    val samePNRSeatModelMediatorLiveData = MediatorLiveData<MutableList<SamePNRSeatModel>>()


    var removedPassengersList=MutableLiveData<ArrayList<String>>(arrayListOf())


    private val _multistationSeatData = MutableLiveData<MultiStationRespBody>()
    val multistationSeatData: LiveData<MultiStationRespBody>
        get() = _multistationSeatData

    val _callMultistationApi = MutableLiveData<String?>()
    val callMultistationApi: LiveData<String?>
        get() = _callMultistationApi

    private val _multiHopSeatNumberApiCalledList = MutableLiveData<MutableList<String>>()
    val multiHopSeatNumberApiCalledList : LiveData<MutableList<String>>
        get() = _multiHopSeatNumberApiCalledList

    private var currentMultiHopArray: MutableList<MultiHopSeatDetail> = mutableListOf()
    private val _currentMultiHopArrayMutableLiveData = MutableLiveData<MutableList<MultiHopSeatDetail>>()
    val currentMultiHopArrayLiveData: LiveData<MutableList<MultiHopSeatDetail>>
        get() = _currentMultiHopArrayMutableLiveData


    private val _toggleDoneAndGoBackButtonVisibility = MutableLiveData<Boolean>()
    val toggleDoneAndGoBackButtonVisibility: LiveData<Boolean>
        get() = _toggleDoneAndGoBackButtonVisibility

    private val _isSeatListGeneratedForFirstTime = MutableLiveData<Boolean>(false)
    val isSeatListGeneratedForFirstTime: LiveData<Boolean>
        get() = _isSeatListGeneratedForFirstTime

    init {

        _loginModel.value = PreferenceUtils.getLogin()
        if(privileges != null){
            _privilegeDetailsModel.value = privileges

        }
        _locale.value = PreferenceUtils.getlang()

        samePNRSeatModelMediatorLiveData.addSource(samePNRSeatModelList) {
            samePNRSeatModelMediatorLiveData.value = it
        }
        samePNRSeatModelMediatorLiveData.addSource(shiftSeatsHashMapLiveData)  {

            val parentDefaultBookedSeatList = _samePNRSeatModelList.value ?: mutableListOf()


            for(i in 0 until (parentDefaultBookedSeatList.size)) {

                val samePNRSeatModel = parentDefaultBookedSeatList[i]

                for (j in 0 until  (samePNRSeatModel.seatShiftList.size)) {
                    val seatShiftModel = samePNRSeatModel.seatShiftList[j]

                    val oldSeat = seatShiftModel.oldSeat

                    var isItemPresent = false
                    var item: SeatDetail? = null

                    for ((key, value) in shiftSeatsHashMapLiveData.value ?: linkedMapOf()) {
                        if (key.number.equals(oldSeat.number) && key.passengerDetails?.ticketNo.equals(
                                samePNRSeatModel.pnr
                            )
                        ) {
                            isItemPresent = true

                            if (value != null) {
                                item = value
                            }
                        }
                    }

                    if(item != null) {
                        parentDefaultBookedSeatList[i].seatShiftList[j].newSeat = item

                    } else {
                        parentDefaultBookedSeatList[i].seatShiftList[j].newSeat = null

                    }

                }
            }
            _samePNRSeatModelList.value = parentDefaultBookedSeatList
        }
    }

    fun setLeftCoachReservationId(reservationIdLeftCoach: String) {
        _reservationIdLeftCoach.value = reservationIdLeftCoach
    }

    fun setRightCoachReservationId(reservationIdRightCoach: String) {
        _reservationIdRightCoach.value = reservationIdRightCoach
    }

    fun getServiceDetailsLeftCoach(
        reservationId:String,
        apiKey : String,
        originId: String,
        destinationId: String,
        operatorApiKey : String,
        locale: String,
        excludePassengerDetails : Boolean
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            _serviceDetailsLeftCoach.postValue(
                mergeBusShiftRepository.newGetServiceDetailMergeBus(reservationId,originId,destinationId,apiKey,operatorApiKey,locale,excludePassengerDetails).body()
            )
        }
    }

    fun setCurrentSelectedSeatAndPNR(newPnr: String?, seatDetail: SeatDetail?) {
        _currentSelectedPNR.value = newPnr
        _currentSelectedSeat.value = seatDetail
    }

    fun setIsRightCoachServiceApiCalled(flag: Boolean) {
        _isRightCoachServiceApiCalled.postValue(flag)
    }

    fun isSeatNotShifted(seatNumber: String, pnrNumber: String): Boolean {
        var isSeatNotShifted = false
        for ((key, value) in _shiftSeatsHashMap) {
            if (key.number.equals(seatNumber, true) && key.passengerDetails?.ticketNo.equals(pnrNumber, true)) {
                if(value == null) {
                    isSeatNotShifted = true
                }
            }
        }
        return isSeatNotShifted
    }

    private fun isSeatShifted(seatNumber: String, pnrNumber: String): Boolean {
        var isSeatShifted = false
        for ((key, value) in _shiftSeatsHashMap) {
            if (key.number.equals(seatNumber, true) && key.passengerDetails?.ticketNo.equals(pnrNumber, true)) {
                if(value != null) {
                    isSeatShifted = true
                }
            }
        }
        return isSeatShifted
    }

    fun isPartialShiftingDone(): Boolean {
        var flag = false
        val seatNumbers = currentSelectedSeat.value?.passengerDetails?.seatNumbers?.split(",")

        var isAnySeatShifted = false
        var isAnySeatNotShifted = false

        seatNumbers?.forEach {
            if (isSeatShifted(it, currentSelectedSeat.value?.passengerDetails?.ticketNo ?: "")) {
                isAnySeatShifted = true
                return@forEach
            }
        }

        seatNumbers?.forEach {
            if (isSeatNotShifted(it, currentSelectedSeat.value?.passengerDetails?.ticketNo ?: "")) {
                isAnySeatNotShifted = true
                return@forEach
            }
        }

        if(isAnySeatShifted && isAnySeatNotShifted) {
            flag = true
        }

        return flag
    }

    fun isAnySeatInSamePNRNotShiftedForGivenSeat(seatDetail: SeatDetail): Boolean {
        val seatNumbers = seatDetail.passengerDetails?.seatNumbers?.split(",")

        var isAnySeatNotShifted = false


        seatNumbers?.forEach {
            if (isSeatNotShifted(it, currentSelectedSeat.value?.passengerDetails?.ticketNo ?: "")) {
                isAnySeatNotShifted = true
                return@forEach
            }
        }

        return isAnySeatNotShifted
    }

    fun deSelectSeatIfPNRGroupIsAlreadyShifted() {
        val isPartialShiftingDone = isPartialShiftingDone()

        val samePNRSeatList = mutableListOf<SeatDetail>()

        if(!isPartialShiftingDone) {
            for ((key, value) in _shiftSeatsHashMap) {
                if (key.passengerDetails?.ticketNo.equals(currentSelectedPNR.value, true)) {
                    samePNRSeatList.add(key)
                    //_shiftSeatsMap[key] = null
                }
            }

            samePNRSeatList.forEach {
                deSelectSpecificSeatLeftCoach(it)
                if(_shiftSeatsHashMap[it] != null) {
                    deSelectSpecificSeatRightCoach(_shiftSeatsHashMap[it]!!)
                }
                _shiftSeatsHashMap.remove(it)
            }
        }

        _shiftSeatsHashMapMutableLiveData.value = _shiftSeatsHashMap
    }
    fun deSelectSeatIfPNRGroupIsAlreadyShiftedMultiHop(seatDetail: SeatDetail) {
        val isPartialShiftingDone = isPartialShiftingDone()

        val samePNRSeatList = mutableListOf<SeatDetail>()

        if(!isPartialShiftingDone) {
            currentMultiHopArray.forEach {
                if(it.pnr.equals(seatDetail.passengerDetails?.ticketNo)) {
                    it.isPNRGroupSelected = false

                    for ((key, value) in _shiftSeatsHashMap) {
                        if (key.passengerDetails?.ticketNo.equals(it.pnr, true)) {
                            samePNRSeatList.add(key)
                            //_shiftSeatsMap[key] = null
                        }
                    }
                }

            }


            samePNRSeatList.forEach {
                deSelectSpecificSeatLeftCoach(it)
                if(_shiftSeatsHashMap[it] != null) {
                    deSelectSpecificSeatRightCoach(_shiftSeatsHashMap[it]!!)
                }
                _shiftSeatsHashMap.remove(it)
            }
        }

        _shiftSeatsHashMapMutableLiveData.value = _shiftSeatsHashMap
    }
    fun selectAllSeatsWithSamePNR() {

        serviceDetailsLeftCoach.value?.body?.coachDetails?.seatDetails?.forEach {
            if(currentSelectedPNR.value.equals(it.passengerDetails?.ticketNo, true)) {
                it.isSelected = true
                _shiftSeatsHashMap[it] = null
                selectSpecificSeatLeftCoach(it)
            }
        }
    }

    fun selectAllSeatsWithSamePNRNew() {

        serviceDetailsLeftCoach.value?.body?.coachDetails?.seatDetails?.forEach {

            if(it.isMultiHop == true) {
                it.otherPnrNumber?.forEach { otherPnrNumber ->
                    if (currentSelectedPNR.value.equals(otherPnrNumber, true)) {
                        val item = it
                        item.isSelected = true
                        item.passengerDetails?.ticketNo = currentSelectedPNR.value
                        _shiftSeatsHashMap[item] = null
                        selectSpecificSeatLeftCoach(item)
                    }
                }
            } else {
                if(currentSelectedPNR.value.equals(it.passengerDetails?.ticketNo, true)) {
                    it.isSelected = true
                    _shiftSeatsHashMap[it] = null
                    selectSpecificSeatLeftCoach(it)
                }
            }
            /*if(currentSelectedPNR.value.equals(it.passengerDetails?.ticketNo, true)) {
                it.isSelected = true
                _shiftSeatsHashMap[it] = null
                selectSpecificSeatLeftCoach(it)
            } else {
                it.otherPnrNumber?.forEach { otherPnrNumber ->
                    if (currentSelectedPNR.value.equals(otherPnrNumber, true)) {
                        it.isSelected = true
                        _shiftSeatsHashMap[it] = null
                        selectSpecificSeatLeftCoach(it)
                    }
                }
            }*/

        }
    }

    fun deSelectAllSeatsWithSamePNR(pnr: String?) {
        if(pnr != null) {
            _shiftSeatsHashMap = _shiftSeatsHashMap.removePNRGroupAndDeselectSeats(
                pnr = pnr,
                deSelectLeftSeat = {
                    deSelectSpecificSeatLeftCoach(it)
                },
                deSelectRightSeat = {
                    deSelectSpecificSeatRightCoach(it)
                }
            )
        }

        _shiftSeatsHashMapMutableLiveData.value = _shiftSeatsHashMap
    }


    fun selectOnlyBorderOfParticularPNRGroup(pnrNumber: String) {
        currentMultiHopArray.forEach { item ->

            if(item.pnr.equals(pnrNumber)) {
                item.isPNRGroupSelected = true
            } else {
                item.isPNRGroupSelected = false
            }

        }
    }
    fun selectAllSeatsWithSamePNRMultiHop(multiHopSeat: SeatDetail) {
/*        _multistationSeatData.value?.body?.multi_hop_seat_detail?.forEach {item ->
            if(item.pnr?.equals(multiHopSeat.passengerDetails?.ticketNo, true) == true) {

                item.seat_details?.forEach {
                    _shiftSeatsHashMap[it] = null
                }
            }
        }

        _shiftSeatsHashMapMutableLiveData.value = _shiftSeatsHashMap*/


        currentMultiHopArray.forEach { item ->
            if(multiHopSeat.passengerDetails?.ticketNo.equals(item.pnr, true)) {

                item.isPNRGroupSelected = true

                item.seat_details?.forEach {

                    it.isSelected = true
                    _shiftSeatsHashMap[it] = null
                }
            } else {
                item.isPNRGroupSelected = false
            }
        }

        _shiftSeatsHashMapMutableLiveData.value = _shiftSeatsHashMap
        _currentMultiHopArrayMutableLiveData.value = currentMultiHopArray

    }

    fun deSelectAllSeatsWithSamePNRMultiHop(multiHopSeat: SeatDetail) {

        currentMultiHopArray.forEach { item ->
            if(multiHopSeat.passengerDetails?.ticketNo.equals(item.pnr, true)) {

                item.isPNRGroupSelected = false
                item.seat_details?.forEach {
                    it.isSelected = false
                }

                _shiftSeatsHashMap = _shiftSeatsHashMap.removePNRGroupAndDeselectSeats(
                    pnr = item.pnr ?: "",
                    deSelectLeftSeat = {
                        deSelectSpecificSeatLeftCoach(it)
                    },
                    deSelectRightSeat = {
                        deSelectSpecificSeatRightCoach(it)
                    }
                )
            }
        }

        _shiftSeatsHashMapMutableLiveData.value = _shiftSeatsHashMap
        _currentMultiHopArrayMutableLiveData.value = currentMultiHopArray
    }


    fun deSelectAllSeatsWithSameSeatNumberMultiHop(multiHopSeat: SeatDetail) {

        currentMultiHopArray.forEach { item ->

            item.seat_details?.forEach { subItem ->

     //           if(multiHopSeat.number.equals(subItem.number, true)) {

                    _shiftSeatsHashMap = _shiftSeatsHashMap.removePNRGroupAndDeselectSeatsNew(
                        pnr = item.pnr ?: "",
                        deSelectLeftSeat = {
                            deSelectSpecificSeatLeftCoach(it)
                        },
                        deSelectRightSeat = {
                            deSelectSpecificSeatRightCoach(it)
                        }
                    )
                }
       //     }

        }

        _shiftSeatsHashMapMutableLiveData.value = _shiftSeatsHashMap
        _currentMultiHopArrayMutableLiveData.value = currentMultiHopArray

    }

    fun hasAllSeatsBeenShiftedOfParticularPNR(leftSideSeatDetail: SeatDetail): Boolean {
        var hasAllSeatsBeenShifted = false

        val seatNumbers = leftSideSeatDetail.passengerDetails?.seatNumbers?.split(",")

        if(_shiftSeatsHashMap.isNotEmpty()) {
            seatNumbers?.forEach {
                for ((key, value) in _shiftSeatsHashMap) {
                    if (key.number.equals(it, true) && key.passengerDetails?.ticketNo.equals(leftSideSeatDetail.passengerDetails?.ticketNo, true)) {
                        if (value == null) {
                            hasAllSeatsBeenShifted = false
                            break
                        } else {
                            hasAllSeatsBeenShifted = true
                        }
                    }
                }
            }
        } else {
            hasAllSeatsBeenShifted = false
        }

        return hasAllSeatsBeenShifted
    }

    fun hasNoneSeatsBeenShiftedOfPreviousPNR(): Boolean {
        var hasNoneSeatsBeenShifted = true

        val seatNumbers = currentSelectedSeat.value?.passengerDetails?.seatNumbers?.split(",")

        if(_shiftSeatsHashMap.isNotEmpty()) {
            seatNumbers?.forEach {
                for ((key, value) in _shiftSeatsHashMap) {
                    if (key.number.equals(it, true) && key.passengerDetails?.ticketNo.equals(currentSelectedPNR.value, true)) {
                        if (value != null) {
                            hasNoneSeatsBeenShifted = false
                        }
                    }
                }
            }
        }

        return hasNoneSeatsBeenShifted
    }

    fun selectParticularSeatOrDeSelectWholePNRGroupWhenClickedOnLeftCoach(leftSideSeatDetail: SeatDetail) {
        if(shiftSeatsHashMapLiveData.value?.containsKey(leftSideSeatDetail) == true) {
            /*if (shiftSeats.value?.get(seatDetail) != null) {
                deSelectSpecificSeatRightCoach(shiftSeats.value?.get(seatDetail)!!)
            }
            deSelectSpecificSeatLeftCoach(seatDetail)
            _shiftSeatsMap.remove(seatDetail)
            _shiftSeats.postValue(_shiftSeatsMap)*/

            deSelectAllSeatsWithSamePNR(leftSideSeatDetail.passengerDetails?.ticketNo)
        } else {
            _shiftSeatsHashMap[leftSideSeatDetail] = null
            selectSpecificSeatLeftCoach(leftSideSeatDetail)
            _shiftSeatsHashMapMutableLiveData.postValue(_shiftSeatsHashMap)
        }
    }

    fun selectWholePNRGroupOrDeSelectWholePNRGroupWhenClickedOnLeftCoach(leftSideSeatDetail: SeatDetail) {
        if(shiftSeatsHashMapLiveData.value?.containsLeftSideSeatIfLeftSideSeatIsPassedInParam(leftSideSeatDetail) == true) {
            /*if (shiftSeats.value?.get(seatDetail) != null) {
                deSelectSpecificSeatRightCoach(shiftSeats.value?.get(seatDetail)!!)
            }
            deSelectSpecificSeatLeftCoach(seatDetail)
            _shiftSeatsMap.remove(seatDetail)
            _shiftSeats.postValue(_shiftSeatsMap)*/

            deSelectAllSeatsWithSamePNR(leftSideSeatDetail.passengerDetails?.ticketNo)
        } else {
            if(leftSideSeatDetail.isMultiHop == true) {
                if(multiHopSeatNumberApiCalledList.value?.contains(leftSideSeatDetail.number) == true) {

                } else {
                    callMultiStationApi(
                        seatNumber = leftSideSeatDetail.number
                    )
                }
            } else {
                selectAllSeatsWithSamePNRNew()
            }

        }
    }

    private fun getLeftSideSeat(rightSideSeatDetail: SeatDetail, isMultiHopLayoutVisible: Boolean): SeatDetail? {
        var leftSideSeat: SeatDetail? = null

        for ((key, value) in _shiftSeatsHashMap) {

            if(mergeServiceDetailsRightCoach.value?.body?.isMultihopEnable == true) {

                if(mergeServiceDetailsRightCoach.value?.body?.origin?.id.equals(key.passengerDetails?.originId.toString())
                    && mergeServiceDetailsRightCoach.value?.body?.destination?.id.equals(key.passengerDetails?.destinationId.toString())) {

                    if (value?.number?.equals(rightSideSeatDetail.number) == true) {
                        leftSideSeat = key
                    }
                }

            } else {
                if (value?.number?.equals(rightSideSeatDetail.number) == true) {
                    leftSideSeat = key
                }
            }
        }

        return leftSideSeat
    }

    fun isSeatAlreadySelectedInRightCoach(rightSideSeatDetail: SeatDetail, isMultiHopLayoutVisible: Boolean) {
        val leftSideSeat = getLeftSideSeat(rightSideSeatDetail,  isMultiHopLayoutVisible)

        if(leftSideSeat != null) {

            if(leftSideSeat.passengerDetails?.ticketNo.equals(currentSelectedPNR.value, true)) {

                _shiftSeatsHashMap[leftSideSeat] = null
                deSelectSpecificSeatRightCoach(rightSideSeatDetail)
                _shiftSeatsHashMapMutableLiveData.setValue(_shiftSeatsHashMap)
            } else {
                showToastMessage("The given seat has already been shifted to different PNR")
            }

        } else {
            for ((key, value) in _shiftSeatsHashMap) {
                if(value == null) {
                    _shiftSeatsHashMap[key] = rightSideSeatDetail
                    _shiftSeatsHashMapMutableLiveData.setValue(_shiftSeatsHashMap)
                    selectSpecificSeatRightCoach(rightSideSeatDetail)
                    break
                }
            }
            //shiftSeats(seatDetail)
            //selectSpecificSeatRightCoach(seatDetail)
        }
    }
    private fun deSelectSpecificSeatRightCoach(rightSideSeatDetail: SeatDetail) {
        _deSelectSpecificSeatRightCoach.value = rightSideSeatDetail
    }
    private fun deSelectSpecificSeatLeftCoach(leftSideSeatDetail: SeatDetail) {
        _deSelectSpecificSeatLeftCoach.value = leftSideSeatDetail
    }

    private fun selectSpecificSeatLeftCoach(leftSideSeatDetail: SeatDetail) {
        _selectSpecificSeatLeftCoach.value = leftSideSeatDetail
    }
    private fun selectSpecificSeatRightCoach(rightSideSeatDetail: SeatDetail) {
        _selectSpecificSeatRightCoach.value = rightSideSeatDetail
    }

    fun drawBorderAroundPNRGroupLeftCoach(leftSideSeatDetail: SeatDetail) {
        _drawBorderAroundPNRGroupLeftCoach.value = leftSideSeatDetail
    }

    fun generateSeatList(seatDetailsList: List<SeatDetail>) {

        val tempSamePNRSeatModelList = mutableListOf<SamePNRSeatModel>()
        seatDetailsList.forEach { leftCoachServiceDetailsSeatItem ->

            if(leftCoachServiceDetailsSeatItem.passengerDetails?.ticketNo?.isNotEmpty() == true) {
                val index = tempSamePNRSeatModelList.indexOfFirst {
                    it.pnr == leftCoachServiceDetailsSeatItem.passengerDetails?.ticketNo
                }

                if (index != -1) {
                    val item = tempSamePNRSeatModelList[index]
                    val childIndex = item.seatShiftList.indexOfFirst {
                        it.oldSeat.number == leftCoachServiceDetailsSeatItem.number
                    }

                    if (childIndex != -1) {
                        item.seatShiftList[childIndex].newSeat = null
                    } else {
                        item.seatShiftList.add(
                            SeatShiftModel(
                                oldSeat = leftCoachServiceDetailsSeatItem,
                                newSeat = null
                            )
                        )
                    }
                } else {
                    val seatShiftList = mutableListOf<SeatShiftModel>()
                    seatShiftList.add(
                        SeatShiftModel(
                            oldSeat = leftCoachServiceDetailsSeatItem,
                            newSeat = null
                        )
                    )
                    val samePNRSeatModel = SamePNRSeatModel(
                        pnr = leftCoachServiceDetailsSeatItem.passengerDetails?.ticketNo ?: "",
                        seatShiftList = seatShiftList,
                        destinationName = leftCoachServiceDetailsSeatItem.passengerDetails?.destinationName
                            ?: "",
                        bookedBy = leftCoachServiceDetailsSeatItem.passengerDetails?.bookedBy ?: ""
                    )

                    tempSamePNRSeatModelList.add(samePNRSeatModel)
                }
            }
        }

        val tempList = _samePNRSeatModelList.value ?: mutableListOf()
        tempList.addAll(tempSamePNRSeatModelList)
        _samePNRSeatModelList.postValue(tempList)
    }
    fun generateSeatListNew(seatDetailsList: List<SeatDetail>) {

        val tempSamePNRSeatModelList = mutableListOf<SamePNRSeatModel>()
        seatDetailsList.forEach { leftCoachServiceDetailsSeatItem ->

            if(leftCoachServiceDetailsSeatItem.passengerDetails?.ticketNo?.isNotEmpty() == true) {
                if(leftCoachServiceDetailsSeatItem.isMultiHop == true) {
                    leftCoachServiceDetailsSeatItem.otherPnrNumber?.forEach {otherPnrNumber ->
                        val index = tempSamePNRSeatModelList.indexOfFirst {
                            it.pnr == otherPnrNumber
                        }

                        if (index != -1) {
                            val item = tempSamePNRSeatModelList[index]
                            val childIndex = item.seatShiftList.indexOfFirst {
                                it.oldSeat.number == leftCoachServiceDetailsSeatItem.number
                            }

                            if (childIndex != -1) {
                                item.seatShiftList[childIndex].newSeat = null
                            } else {
                                item.seatShiftList.add(
                                    SeatShiftModel(
                                        oldSeat = leftCoachServiceDetailsSeatItem,
                                        newSeat = null
                                    )
                                )
                            }
                        } else {
                            val seatShiftList = mutableListOf<SeatShiftModel>()
                            seatShiftList.add(
                                SeatShiftModel(
                                    oldSeat = leftCoachServiceDetailsSeatItem,
                                    newSeat = null
                                )
                            )
                            val samePNRSeatModel = SamePNRSeatModel(
                                pnr = otherPnrNumber ?: "",
                                seatShiftList = seatShiftList,
                                destinationName = leftCoachServiceDetailsSeatItem.passengerDetails?.destinationName
                                    ?: "",
                                bookedBy = leftCoachServiceDetailsSeatItem.passengerDetails?.bookedBy
                                    ?: ""
                            )

                            tempSamePNRSeatModelList.add(samePNRSeatModel)
                        }
                    }


                    val index = tempSamePNRSeatModelList.indexOfFirst {
                        it.pnr == leftCoachServiceDetailsSeatItem.passengerDetails?.ticketNo
                    }

                    if (index != -1) {
                        val item = tempSamePNRSeatModelList[index]
                        val childIndex = item.seatShiftList.indexOfFirst {
                            it.oldSeat.number == leftCoachServiceDetailsSeatItem.number
                        }

                        if (childIndex != -1) {
                            item.seatShiftList[childIndex].newSeat = null
                        } else {
                            item.seatShiftList.add(
                                SeatShiftModel(
                                    oldSeat = leftCoachServiceDetailsSeatItem,
                                    newSeat = null
                                )
                            )
                        }
                    } else {
                        val seatShiftList = mutableListOf<SeatShiftModel>()
                        seatShiftList.add(
                            SeatShiftModel(
                                oldSeat = leftCoachServiceDetailsSeatItem,
                                newSeat = null
                            )
                        )
                        val samePNRSeatModel = SamePNRSeatModel(
                            pnr = leftCoachServiceDetailsSeatItem.passengerDetails?.ticketNo ?: "",
                            seatShiftList = seatShiftList,
                            destinationName = leftCoachServiceDetailsSeatItem.passengerDetails?.destinationName
                                ?: "",
                            bookedBy = leftCoachServiceDetailsSeatItem.passengerDetails?.bookedBy
                                ?: ""
                        )

                        tempSamePNRSeatModelList.add(samePNRSeatModel)
                    }
                } else {
                    val index = tempSamePNRSeatModelList.indexOfFirst {
                        it.pnr == leftCoachServiceDetailsSeatItem.passengerDetails?.ticketNo
                    }

                    if (index != -1) {
                        val item = tempSamePNRSeatModelList[index]
                        val childIndex = item.seatShiftList.indexOfFirst {
                            it.oldSeat.number == leftCoachServiceDetailsSeatItem.number
                        }

                        if (childIndex != -1) {
                            item.seatShiftList[childIndex].newSeat = null
                        } else {
                            item.seatShiftList.add(
                                SeatShiftModel(
                                    oldSeat = leftCoachServiceDetailsSeatItem,
                                    newSeat = null
                                )
                            )
                        }
                    } else {
                        val seatShiftList = mutableListOf<SeatShiftModel>()
                        seatShiftList.add(
                            SeatShiftModel(
                                oldSeat = leftCoachServiceDetailsSeatItem,
                                newSeat = null
                            )
                        )
                        val samePNRSeatModel = SamePNRSeatModel(
                            pnr = leftCoachServiceDetailsSeatItem.passengerDetails?.ticketNo ?: "",
                            seatShiftList = seatShiftList,
                            destinationName = leftCoachServiceDetailsSeatItem.passengerDetails?.destinationName
                                ?: "",
                            bookedBy = leftCoachServiceDetailsSeatItem.passengerDetails?.bookedBy
                                ?: ""
                        )

                        tempSamePNRSeatModelList.add(samePNRSeatModel)
                    }
                }
            }
        }

        val tempList = _samePNRSeatModelList.value ?: mutableListOf()
        tempList.addAll(tempSamePNRSeatModelList)
        _samePNRSeatModelList.postValue(tempList)
    }

    private fun showToastMessage(message: String) {
        _toastMessage.value = message
    }

    fun getMergeBusRecommendedSeats(
        recommendedSeatsRequest: RecommendedSeatsRequest
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _recommendedSeatsRightCoach.postValue(
                mergeBusShiftRepository.getMergeBusRecommendedSeats(recommendedSeatsRequest).body()
            )
        }
    }
    fun getMergeServiceDetailsRightCoach(
        mergeServiceDetailsRequest: MergeServiceDetailsRequest
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _mergeServiceDetailsRightCoach.postValue(
                mergeBusShiftRepository.getMergeServiceDetails(mergeServiceDetailsRequest).body()
            )
        }
    }

    fun mergeBusShiftPassenger(
        mergeServiceDetailsRequest: MergeBusShiftPassengerRequest
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _mergeBusShiftPassenger.postValue(
                mergeBusShiftRepository.mergeBusShiftPassenger(mergeServiceDetailsRequest).body()
            )
        }
    }
    fun mergeBusSeatMapping(
        mergeBusSeatMappingRequest: MergeBusSeatMappingRequest
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _mergeBusSeatMapping.postValue(
                mergeBusShiftRepository.mergeBusSeatMapping(mergeBusSeatMappingRequest).body()
            )
            _mergeBusSeatMappingRedirection.postValue(Event(mergeBusShiftRepository.mergeBusSeatMapping(mergeBusSeatMappingRequest).body()!!))
        }
    }

    fun updateSampleData() {
        val tempSamePNRSeatModelList = _samePNRSeatModelList.value ?: mutableListOf()
        multistationSeatData.value?.body?.multi_hop_seat_detail?.forEach { multiHopSeatDetailItem ->

            val isSamePNRDataAlreadyStored = _samePNRSeatModelList.value?.any {
                it.pnr.equals(multiHopSeatDetailItem.pnr)
            } ?: false

            if(isSamePNRDataAlreadyStored) {

                val indexOfTempSamePNRSeatModelList = tempSamePNRSeatModelList.indexOfFirst {
                    it.pnr.equals(multiHopSeatDetailItem.pnr)
                }

                multiHopSeatDetailItem.seat_details?.forEach { oldSeatOfMultiHopSeatDetailItem ->

                }

                multiHopSeatDetailItem.seat_details?.forEach { multiHopSeatDetailOldSeatItem ->


                    var isOldSeatDataAlreadyStored = false

                    tempSamePNRSeatModelList.get(indexOfTempSamePNRSeatModelList).seatShiftList.forEach { tempSamePNRSeatModelSeatShiftListItem->
                        if (multiHopSeatDetailOldSeatItem.number.equals(tempSamePNRSeatModelSeatShiftListItem.oldSeat.number)) {
                            isOldSeatDataAlreadyStored = true
                            return@forEach
                        }
                    }

                    if(!isOldSeatDataAlreadyStored) {
                        tempSamePNRSeatModelList[indexOfTempSamePNRSeatModelList].seatShiftList.add(
                            SeatShiftModel(
                                oldSeat = multiHopSeatDetailOldSeatItem,
                                newSeat = null
                            )
                        )
                    }
                }


                /*multiHopSeatDetailItem.seat_details?.forEach { seatDetail ->

                    if (seatDetail.passengerDetails?.ticketNo?.isNotEmpty() == true) {
                        val index = tempSamePNRSeatModelList.indexOfFirst {
                            it.pnr == seatDetail.passengerDetails?.ticketNo
                        }

                        if (index != -1) {
                            val item = tempSamePNRSeatModelList[index]
                            val childIndex = item.seatShiftList.indexOfFirst {
                                it.oldSeat.number == seatDetail.number
                            }

                            if (childIndex != -1) {
                                item.seatShiftList[childIndex].newSeat = null
                            } else {
                                item.seatShiftList.add(
                                    SeatShiftModel(
                                        oldSeat = seatDetail,
                                        newSeat = null
                                    )
                                )
                            }
                        } else {
                            val seatShiftList = mutableListOf<SeatShiftModel>()
                            seatShiftList.add(
                                SeatShiftModel(
                                    oldSeat = seatDetail,
                                    newSeat = null
                                )
                            )
                            val samePNRSeatModel = SamePNRSeatModel(
                                pnr = seatDetail.passengerDetails?.ticketNo ?: "",
                                seatShiftList = seatShiftList,
                                destinationName = seatDetail.passengerDetails?.destinationName
                                    ?: "",
                                bookedBy = seatDetail.passengerDetails?.bookedBy ?: ""
                            )

                            tempSamePNRSeatModelList.add(samePNRSeatModel)
                        }
                    }
                }*/
            } else {
                val seatShiftList = mutableListOf<SeatShiftModel>()

                multiHopSeatDetailItem.seat_details?.forEach {
                    seatShiftList.add(
                        SeatShiftModel(
                            oldSeat = it,
                            newSeat = null
                        )
                    )
                }

                val samePNRSeatModel = SamePNRSeatModel(
                    pnr = multiHopSeatDetailItem.pnr ?: "",
                    seatShiftList = seatShiftList,
                    destinationName = multiHopSeatDetailItem.seat_details?.get(0)?.passengerDetails?.destinationName
                        ?: "",
                    bookedBy = multiHopSeatDetailItem.seat_details?.get(0)?.passengerDetails?.bookedBy ?: ""
                )

                tempSamePNRSeatModelList.add(samePNRSeatModel)
            }
        }

        /*val aap = _samePNRSeatModelList.value ?: mutableListOf()
        aap.addAll(tempSamePNRSeatModelList)*/
        _samePNRSeatModelList.postValue(tempSamePNRSeatModelList)

        //_samePNRSeatModelList.value?.addAll(tempSamePNRSeatModelList)
    }
    fun addBpDpToService(
        addBpDpToServiceRequest: AddBpDpToServiceRequest
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _addBpDpToService.postValue(
                mergeBusShiftRepository.addBpDpToService(addBpDpToServiceRequest).body()
            )
        }
    }

    fun multistationPassengerDataApi(
        apiKey: String,
        reservationId : String,
        seatNumber : String,
        isBima: Boolean,
        apiType: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _multistationSeatData.postValue(
                mergeBusShiftRepository.getMultiStationSeatDataApi(
                    apiKey = apiKey,
                    reservationId = reservationId,
                    seatNumber = seatNumber,
                    isBima = isBima
                ).body()
            )
        }
    }

    fun getSeatShiftMapForSeatMappingApi(): MutableList<SeatShiftMap?> {
        val seatShiftMapList = mutableListOf<SeatShiftMap?>()
        samePNRSeatModelMediatorLiveData.value?.forEach { samePNRSeatModel->
            val seats = mutableListOf<Seat?>()

            val flag = samePNRSeatModel.seatShiftList.any {
                it.newSeat == null
            }

            if(!flag) {
                samePNRSeatModel.seatShiftList.forEach {seatShiftModel ->

                    val seat = Seat(
                        newSeat = seatShiftModel.newSeat?.number,
                        oldSeat = seatShiftModel.oldSeat.number
                    )
                    seats.add(seat)
                }

                val seatShiftMap = SeatShiftMap(
                    pnrNumber = samePNRSeatModel.pnr,
                    seats = seats
                )

                seatShiftMapList.add(seatShiftMap)
            }

        }

        return seatShiftMapList
    }

    fun deSelectAllSeats() {

        if (shiftSeatsHashMapLiveData.value?.isNotEmpty() == true) {

            val seatDetailList = mutableListOf<SeatDetail>()
            shiftSeatsHashMapLiveData.value?.forEach { parentItem ->

                val flag = seatDetailList.any {
                    parentItem.key.passengerDetails?.ticketNo.equals(it.passengerDetails?.ticketNo)
                }
                if(!flag) {
                    seatDetailList.add(parentItem.key)
                }

            }
            seatDetailList.forEach { item ->

                setCurrentSelectedSeatAndPNR(
                    item.passengerDetails?.ticketNo ?: "",
                    item
                )

                deSelectAllSeatsWithSamePNR(item.passengerDetails?.ticketNo)

            }

            currentMultiHopArray.clear()
            _currentMultiHopArrayMutableLiveData.value = currentMultiHopArray

            _removeSelectedPNRGroupBorderLeftCoach.value = true
        }
   }

    fun callMultiStationApi(seatNumber: String?) {
        _callMultistationApi.postValue(seatNumber)
    }

    fun addSeatNumberToMultiHopSeatNumberApiCalledList(seatNumber: String) {
        val list = multiHopSeatNumberApiCalledList.value ?: mutableListOf()
        list.add(seatNumber)
        _multiHopSeatNumberApiCalledList.postValue(list)
    }

    fun checkForPreviouslyShiftedSeatsAndUpdateUIMultiHop() {

        if(mergeServiceDetailsRightCoach.value?.body?.isMultihopEnable == true) {
            for ((leftSideSeatDetail, rightSideSeatDetail) in shiftSeatsHashMapLiveData.value ?: linkedMapOf()) {

                if(mergeServiceDetailsRightCoach.value?.body?.origin?.id?.equals(leftSideSeatDetail.passengerDetails?.originId.toString()) == true && mergeServiceDetailsRightCoach.value?.body?.destination?.id?.equals(leftSideSeatDetail.passengerDetails?.destinationId.toString()) == true ) {
                    //selectSpecificSeatLeftCoach(leftSideSeatDetail)

                    if (rightSideSeatDetail != null) {
                        selectSpecificSeatRightCoach(rightSideSeatDetail)
                    }
                }
            }
        }

/*
        serviceDetailsLeftCoach.value?.body?.coachDetails?.seatDetails?.forEach { serviceDetailsSeatDetail ->

            if(serviceDetailsSeatDetail.isMultiHop == false) {
                serviceDetailsSeatDetail.passengerDetails?.seatNumbers?.split(",")?.forEach { otherSeatNumber ->

                    shiftSeatsHashMapLiveData.value?.forEach { leftSideSeatDetail, rightSideSeatDetail ->
                        if (otherSeatNumber.equals(leftSideSeatDetail.number) && serviceDetailsSeatDetail.passengerDetails?.ticketNo.equals(leftSideSeatDetail.passengerDetails?.ticketNo)
                        ) {

                            if (rightSideSeatDetail != null) {

                                selectSpecificSeatLeftCoach(leftSideSeatDetail)
                                selectSpecificSeatRightCoach(rightSideSeatDetail)
                            }
                        }
                    }
                }
            }
        }
*/
    }

    fun checkForPreviouslyShiftedSeatsAndUpdateUI() {

        serviceDetailsLeftCoach.value?.body?.coachDetails?.seatDetails?.forEach {  serviceDetailsSeatDetail ->

            if(serviceDetailsSeatDetail.isMultiHop == false) {
                var areAllSeatsShifted = false

                serviceDetailsSeatDetail.passengerDetails?.seatNumbers?.split(",")?.forEach { otherSeatNumber ->

                    for ((leftSideSeatDetail, rightSideSeatDetail) in shiftSeatsHashMapLiveData.value ?: linkedMapOf()) {

                        if (otherSeatNumber.equals(leftSideSeatDetail.number) && serviceDetailsSeatDetail.passengerDetails?.ticketNo.equals(leftSideSeatDetail.passengerDetails?.ticketNo)
                        ) {

                            if (rightSideSeatDetail != null) {
                                areAllSeatsShifted = true

                                selectSpecificSeatLeftCoach(leftSideSeatDetail)
                                selectSpecificSeatRightCoach(rightSideSeatDetail)
                            } else {
                                areAllSeatsShifted = false
                            }
                        }
                    }
                }
                /*if(areAllSeatsShifted) {
                    drawBorderAroundPNRGroupLeftCoach(serviceDetailsSeatDetail)
                }*/
            }
        }
    }


    fun toggleDoneAndGoBackButtonVisibility(showDoneButton: Boolean) {
        _toggleDoneAndGoBackButtonVisibility.value = showDoneButton
    }

    fun setCurrentMultiHopArray(multiHopArray: MutableList<MultiHopSeatDetail>) {

        multiHopArray.forEach { multiHopArrayItem ->

            for ((leftSideSeatDetail, rightSideSeatDetail) in shiftSeatsHashMapLiveData.value ?: linkedMapOf()) {
                if(leftSideSeatDetail.passengerDetails?.ticketNo.equals( multiHopArrayItem.pnr)) {
                    multiHopArrayItem.seat_details?.forEach {
                        if(it.number.equals(leftSideSeatDetail.number) && it.passengerDetails?.ticketNo.equals(leftSideSeatDetail.passengerDetails?.ticketNo)) {
                            if(rightSideSeatDetail == null) {
                                it.isSelected = false
                            } else {
                                it.isSelected = true
                            }
                        }
                    }
                }
            }
        }

        multiHopArray.forEachIndexed { index, item ->
            if(index == 0) {
                item.isPNRGroupSelected = true
            } else {
                item.isPNRGroupSelected = false
            }
        }
        currentMultiHopArray = multiHopArray
        _currentMultiHopArrayMutableLiveData.value = currentMultiHopArray

    }

    fun setIsSeatListGeneratedForFirstTime(flag: Boolean) {
        _isSeatListGeneratedForFirstTime.value = flag
    }

    fun getPNROfNonMultiHopSeatIfMultiHopSeatIsSelected(multiHopSeat: SeatDetail): String? {
        var nonMultiHopPNR: String? = null

        kotlin.run runBlock@{
            serviceDetailsLeftCoach.value?.body?.coachDetails?.seatDetails?.forEach { serviceDetailsSeatDetail ->

                if (multiHopSeat.isMultiHop == true)
                 {
                    multiHopSeat.otherPnrNumber?.forEach { otherPnrNumber ->
                        for((leftSideSeatDetail, rightSideSeatDetail) in shiftSeatsHashMapLiveData.value ?: linkedMapOf()) {
                            //shiftSeatsHashMapLiveData.value?.forEach { leftSideSeatDetail, rightSideSeatDetail ->
                            if (otherPnrNumber.equals(leftSideSeatDetail.passengerDetails?.ticketNo) && leftSideSeatDetail.isMultiHop != true
                            ) {
                                nonMultiHopPNR = leftSideSeatDetail.passengerDetails?.ticketNo
                                return@runBlock
                            }
                        }
                    }

                }
            }
        }

        return nonMultiHopPNR
    }

    fun getNonMultiHopSeatIfMultiHopSeatIsSelected(multiHopSeat: SeatDetail): SeatDetail? {
        var nonMultiHopSeat: SeatDetail? = null

        kotlin.run runBlock@{
            serviceDetailsLeftCoach.value?.body?.coachDetails?.seatDetails?.forEach { serviceDetailsSeatDetail ->

                if (multiHopSeat.isMultiHop == true)
                 {
                    multiHopSeat.otherPnrNumber?.forEach { otherPnrNumber ->
                        for((leftSideSeatDetail, rightSideSeatDetail) in shiftSeatsHashMapLiveData.value ?: linkedMapOf()) {
                            //shiftSeatsHashMapLiveData.value?.forEach { leftSideSeatDetail, rightSideSeatDetail ->
                            if (otherPnrNumber.equals(leftSideSeatDetail.passengerDetails?.ticketNo) && leftSideSeatDetail.isMultiHop != true
                            ) {
                                nonMultiHopSeat = leftSideSeatDetail
                                return@runBlock
                            }
                        }
                    }

                }
            }
        }

        return nonMultiHopSeat
    }

    fun removeNonPairedSeatsFromHashMap() {
        _shiftSeatsHashMap = _shiftSeatsHashMap.removeNonPairedSeats(deSelectLeftSeat = {
                deSelectSpecificSeatLeftCoach(it)
            }
        )

        _shiftSeatsHashMapMutableLiveData.value = _shiftSeatsHashMap
    }

    fun clearRecommendedSeatResponse() {
        _recommendedSeatsRightCoach.value = null
    }
}