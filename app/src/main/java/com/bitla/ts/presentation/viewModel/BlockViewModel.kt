package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.SpinnerItems
import com.bitla.ts.domain.pojo.block_configuration_model.BlockConfigurationModel
import com.bitla.ts.domain.pojo.block_seats.BlockSeatModel
import com.bitla.ts.domain.pojo.block_seats.request.ReqBody__1
import com.bitla.ts.domain.pojo.branch_list_model.BranchListModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.quota_blocking_tooltip_Info_model.response.QuotaBlockingTooltipInfoResponse
import com.bitla.ts.domain.pojo.unblock_seat.UnBlockSeatModel
import com.bitla.ts.domain.pojo.unblock_seat.request.ReqBody
import com.bitla.ts.domain.pojo.user_list.UserListModel
import com.bitla.ts.domain.repository.BlockRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber


class BlockViewModel<T : Any?>(private val blockRepository: BlockRepository) : ViewModel() {

    companion object {
        val TAG: String = BlockViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _blockDetails = MutableLiveData<BlockConfigurationModel>()
    val blockDetails: LiveData<BlockConfigurationModel>
        get() = _blockDetails

    private val _userList = MutableLiveData<UserListModel>()
    val userList: LiveData<UserListModel>
        get() = _userList

    private val _branchList = MutableLiveData<BranchListModel>()
    val branchList: LiveData<BranchListModel>
        get() = _branchList

    private val _blockSeats = MutableLiveData<BlockSeatModel>()
    val blockSeats: LiveData<BlockSeatModel>
        get() = _blockSeats

    private val _unblockSeats = MutableLiveData<UnBlockSeatModel>()
    val unblockSeats: LiveData<UnBlockSeatModel>
        get() = _unblockSeats

    private val _validation = MutableLiveData<String>()
    val validationData: LiveData<String>
        get() = _validation

    private val _changeButtonBackground = MutableLiveData<Boolean>()
    val changeButtonBackground: LiveData<Boolean>
        get() = _changeButtonBackground

    /*  private val _blockTypeList = MutableLiveData<SpinnerItems>()
      val blockData: LiveData<SpinnerItems>
          get() = _blockTypeList*/


    private val _quotaBlockingTooltipInfoResponse =
        MutableLiveData<QuotaBlockingTooltipInfoResponse>()
    val quotaBlockingTooltipInfoResponse: LiveData<QuotaBlockingTooltipInfoResponse>
        get() = _quotaBlockingTooltipInfoResponse

    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()

    var privilegesLiveData = MutableLiveData<PrivilegeResponseModel?>()

    fun updatePrivileges(privileges: PrivilegeResponseModel?) {
        privilegesLiveData.value = privileges
    }

    /*fun blockConfigurationApi(
        authorization: String,
        apiKey: String,
        blockConfigRequest: BlockConfigRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(blockConfigRequest)
        Timber.d("taggy", "blockConfigApi: " + json.toString())
        viewModelScope.launch(Dispatchers.IO) {
            _blockDetails.postValue(
                blockRepository.blockConfig(
                    authorization,
                    apiKey,
                    blockConfigRequest
                ).body()
            )
        }
    }*/

    fun blockConfigurationApi(
        apiKey: String,
        locale: String,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        /*val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(blockConfigRequest)
        Timber.d("taggy", "blockConfigApi: " + json.toString())*/
        viewModelScope.launch(Dispatchers.IO) {
            blockRepository.newBlockConfig(
                apiKey,
                locale
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _blockDetails.postValue(
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

    fun userListApi(
        apiKey: String,
        cityId: String,
        userType: String,
        branchId: String,
        locale: String,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            blockRepository.newUserList(
                apiKey = apiKey,
                cityId = cityId,
                userType = userType,
                branch_id = branchId,
                locale = locale
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _userList.postValue(
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


    fun branchListApi(
        apiKey: String,
        locale: String,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            blockRepository.newBranchList(
                apiKey,
                locale
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _branchList.postValue(
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


    fun blockSeatsApi(
        request: ReqBody__1, apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            blockRepository.newBlockSeats(request).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _blockSeats.postValue(
                            it.data
                        )
                    }

                    is NetworkProcess.Failure -> {
                        messageSharedFlow.emit(it.message)
                    }
                }
            }

        }


    }


    fun unblockSeatsApi(
        request: ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = gson.toJson(request)
        viewModelScope.launch(Dispatchers.IO) {

            blockRepository.newUnBlockSeats(request).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _unblockSeats.postValue(
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

    fun getQuotaBlockingTooltipInfo(
        apiKey: String,
        reservationId: String,
        seatNumber: String,
        locale: String,
        apiType: String,
    ) {
        this.apiType = apiType
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            blockRepository.quotaBlockingTooltipInfo(
                apiKey = apiKey,
                resId = reservationId,
                seatNumber = seatNumber,
                locale = locale,
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _quotaBlockingTooltipInfoResponse .postValue(
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

    // fields validation
    fun validation(
        selectedSeats: String?,
        selectedUserTypeList: MutableList<SpinnerItems>,
        userTypeId: Int,
        branchId: String?,
        userId: String?,
        agentId: String?,
        blockTypeValue: String?,
        fromDate: String?,
        toDate: String?,
        timeHH: String,
        timeMM: String,
        isAllowMultipleQuota: Boolean
    ) {
        if (isAllowMultipleQuota == true) {
            if (selectedUserTypeList.size == 0) {
                _validation.postValue("Please select usertype")
            } else if (selectedUserTypeList.size == 1) {
                when {
                    userTypeId == 1 -> { // user type Onl-Agt
                        when {
                            agentId == null -> _validation.postValue("Please select agent")
                        }
                    }

                    userTypeId == 12 -> { // user type USER
                        when {
                            branchId == null -> _validation.postValue("Please select branch")
                            userId == null -> _validation.postValue("Please select user")
                        }
                    }
                }
            }
            when {
                selectedSeats == null -> _validation.postValue("Please select seat(s)")
                selectedUserTypeList.size == 0 -> _validation.postValue("Please select usertype")
                //userTypeId == 0 -> _validation.postValue("Please select user type")

                blockTypeValue == "Select block type" -> _validation.postValue("Please select block type")
                blockTypeValue == "temporary" ->
                    when {
                        fromDate == null -> _validation.postValue("Please select From date")
                        fromDate == "From date" -> _validation.postValue("Please select From date")
                        fromDate == "" -> _validation.postValue("Please select From date")
                        toDate == null -> _validation.postValue("Please select to date")
                        toDate == "To date" -> _validation.postValue("Please select To date")
                        toDate == "" -> _validation.postValue("Please select To date")
                        else -> _validation.postValue("")
                    }

                blockTypeValue == "custom" ->
                    when {
                        fromDate == null -> _validation.postValue("Please select From date")
                        fromDate == "From date" -> _validation.postValue("Please select From date")
                        fromDate == "" -> _validation.postValue("Please select From date")
                        toDate == null -> _validation.postValue("Please select To date")
                        toDate == "To date" -> _validation.postValue("Please select To date")
                        toDate == "" -> _validation.postValue("Please select To date")
                        else -> _validation.postValue("")
                    }

                blockTypeValue == "permanent" ->
                    _validation.postValue("")

                blockTypeValue == "none" -> _validation.postValue("")


                //else -> _validation.postValue("")
            }
        } else {
            when {
                selectedSeats == null -> _validation.postValue("Please select seat(s)")
                selectedUserTypeList.size == 0 -> _validation.postValue("Please select usertype")
                //userTypeId == 0 -> _validation.postValue("Please select user type")

                blockTypeValue == "Select block type" -> _validation.postValue("Please select block type")
                blockTypeValue == "temporary" ->
                    when {
                        fromDate == null -> _validation.postValue("Please select From date")
                        fromDate == "From date" -> _validation.postValue("Please select From date")
                        fromDate == "" -> _validation.postValue("Please select From date")
                        toDate == null -> _validation.postValue("Please select to date")
                        toDate == "To date" -> _validation.postValue("Please select To date")
                        toDate == "" -> _validation.postValue("Please select To date")
                        else -> _validation.postValue("")
                    }

                blockTypeValue == "custom" ->
                    when {
                        fromDate == null -> _validation.postValue("Please select From date")
                        fromDate == "From date" -> _validation.postValue("Please select From date")
                        fromDate == "" -> _validation.postValue("Please select From date")
                        toDate == null -> _validation.postValue("Please select To date")
                        toDate == "To date" -> _validation.postValue("Please select To date")
                        toDate == "" -> _validation.postValue("Please select To date")
                        else -> _validation.postValue("")
                    }

                blockTypeValue == "permanent" ->
                    _validation.postValue("")

                blockTypeValue == "none" -> _validation.postValue("")


                //else -> _validation.postValue("")
            }
            when {
                userTypeId == 1 -> { // user type Onl-Agt
                    when {
                        agentId == null -> _validation.postValue("Please select agent")
                        //blockTypeValue == "Select block type" -> _validation.postValue("Please select block type")
                        //fromDate == null -> _validation.postValue("Please select from date")
                        //toDate == null -> _validation.postValue("Please select to date")
                        //else -> _validation.postValue("")
                    }
                }

                userTypeId == 12 -> { // user type USER
                    when {
                        branchId == null -> _validation.postValue("Please select branch")
                        userId == null -> _validation.postValue("Please select user")
                        //blockTypeValue == "Select block type" -> _validation.postValue("Please select block type")
                        //fromDate == null -> _validation.postValue("Please select from date")
                        //toDate == null -> _validation.postValue("Please select to date")
                        //else -> _validation.postValue("")
                    }
                }
            }
        }
        _validation.postValue("")

    }

    // change button color after validating all fields
    fun changeButtonBackground(
        selectedSeats: String?,
        selectedUserTypeList: MutableList<SpinnerItems>,
        userTypeId: Int,
        branchId: String?,
        userId: String?,
        agentId: String?,
        blockTypeValue: String?,
        fromDate: String?,
        toDate: String?,
        timeHH: String,
        timeMM: String,
        isAllowMultipleQuota: Boolean
    ) {
        if (isAllowMultipleQuota == true) {
            if (selectedUserTypeList.size == 0) {
                _changeButtonBackground.postValue(false)
            } else if (selectedUserTypeList.size == 1) {
                if (userTypeId == 1) {
                    _changeButtonBackground.postValue(true)

                }
                if (userTypeId == 12) { // user type USER
                    if (branchId != null) {
                        if (userId == null)
                            _changeButtonBackground.postValue(false)
                        else _changeButtonBackground.postValue(true)
                    } else _changeButtonBackground.postValue(true)
                } else _changeButtonBackground.postValue(true)
            }
            when {
                selectedSeats == null -> _validation.postValue("Please select seat(s)")
                selectedUserTypeList.size == 0 -> _validation.postValue("Please select usertype")
                //userTypeId == 0 -> _validation.postValue("Please select user type")

                blockTypeValue == "Select block type" -> _validation.postValue("Please select block type")
                blockTypeValue == "temporary" ->
                    when {
                        fromDate == null -> _validation.postValue("Please select From date")
                        fromDate == "From date" -> _validation.postValue("Please select From date")
                        fromDate == "" -> _validation.postValue("Please select From date")
                        toDate == null -> _validation.postValue("Please select to date")
                        toDate == "To date" -> _validation.postValue("Please select To date")
                        toDate == "" -> _validation.postValue("Please select To date")
                        else -> _validation.postValue("")
                    }

                blockTypeValue == "custom" ->
                    when {
                        fromDate == null -> _validation.postValue("Please select From date")
                        fromDate == "From date" -> _validation.postValue("Please select From date")
                        fromDate == "" -> _validation.postValue("Please select From date")
                        toDate == null -> _validation.postValue("Please select To date")
                        toDate == "To date" -> _validation.postValue("Please select To date")
                        toDate == "" -> _validation.postValue("Please select To date")
                        else -> _validation.postValue("")
                    }

                blockTypeValue == "permanent" ->
                    _validation.postValue("")

                blockTypeValue == "none" -> _validation.postValue("")


                //else -> _validation.postValue("")
            }
        } else {
            when {
                selectedSeats == null -> _changeButtonBackground.postValue(false)
                selectedUserTypeList.size == 0 -> _changeButtonBackground.postValue(false)
                selectedUserTypeList.size > 0 -> _changeButtonBackground.postValue(true)

                //userTypeId == 0 -> _validation.postValue("Please select user type")

                blockTypeValue == "Select block type" -> _changeButtonBackground.postValue(false)
                blockTypeValue == "temporary" ->
                    when {
                        fromDate == null -> _changeButtonBackground.postValue(false)
                        fromDate == "From date" -> _changeButtonBackground.postValue(false)
                        fromDate == "" -> _changeButtonBackground.postValue(false)
                        toDate == null -> _changeButtonBackground.postValue(false)
                        toDate == "To date" -> _changeButtonBackground.postValue(false)
                        toDate == "" -> _changeButtonBackground.postValue(false)
                        else -> _changeButtonBackground.postValue(true)
                    }

                blockTypeValue == "custom" ->
                    when {
                        fromDate == null -> _changeButtonBackground.postValue(false)
                        fromDate == "From date" -> _changeButtonBackground.postValue(false)
                        fromDate == "" -> _changeButtonBackground.postValue(false)
                        toDate == null -> _changeButtonBackground.postValue(false)
                        toDate == "To date" -> _changeButtonBackground.postValue(false)
                        toDate == "" -> _changeButtonBackground.postValue(false)
                        else -> _changeButtonBackground.postValue(true)
                    }
                /*blockTypeValue == "permanent" ->
                    when {
                        timeHH == "" -> _changeButtonBackground.postValue(false)
*//*
                        timeHH.toInt() >= "24".toInt() -> _validation.postValue("Please enter valid time")
                        timeHH.toInt() < "0".toInt() -> _validation.postValue("Please enter valid time")
*//*
                        timeMM == "" -> _changeButtonBackground.postValue(false)
*//*
                        timeMM.toInt() >= "60".toInt() -> _validation.postValue("Please enter valid time")
                        timeMM.toInt() < "0".toInt() -> _validation.postValue("Please enter valid time")
*//*
                        else -> _changeButtonBackground.postValue(true)
                    }*/
                blockTypeValue == "none" -> _changeButtonBackground.postValue(true)


                //else -> _validation.postValue("")
            }
            when {
                userTypeId == 1 -> { // user type Onl-Agt
                    when {
                        agentId == null -> _changeButtonBackground.postValue(false)
                    }
                }

                userTypeId == 12 -> { // user type USER
                    when {
                        branchId == null -> _changeButtonBackground.postValue(false)
                        userId == null -> _changeButtonBackground.postValue(false)
                    }
                }

            }
        }
        _changeButtonBackground.postValue(true)
    }

    fun changeButtonBackground1(
        selectedSeats: String?,
        selectedUserTypeList: MutableList<SpinnerItems>,
        userTypeId: Int,
        branchId: String?,
        userId: String?,
        agentId: String?,
        blockTypeValue: String?,
        fromDate: String?,
        toDate: String?,
        timeHH: String,
        timeMM: String,
        isAllowMultipleQuota: Boolean
    ) {
        var isCorrect: Boolean = false
        if (isAllowMultipleQuota) {

            if (selectedSeats.equals(null) || selectedUserTypeList.size == 0) {
                isCorrect = false
            } else if (selectedUserTypeList.size == 1) {
                if (userTypeId == 1 || userTypeId == 5 || userTypeId == 6 || userTypeId == 7 || userTypeId == 11 || userTypeId == 14 || userTypeId == 2) {// user type Onl-Agt
                    isCorrect = true
                } else if (userTypeId == 12 && branchId != null && userId != null) {
                    isCorrect = true
                }
            } else if (selectedUserTypeList.size >= 2) {
                Timber.d("userTypecheck: $userTypeId")
                var isMale = false
                var isFemale = false
                selectedUserTypeList.forEach {

                    if (it.value.equals("FEMALE", ignoreCase = true)
                    ) {
                        isFemale = true
                    } else if (it.value.equals("MALE", ignoreCase = true)) {
                        isMale = true
                    }
                }
                isCorrect = !(isFemale && isMale)

            }
            if (isCorrect) {
                if (blockTypeValue.equals("Select block type")) {
                    isCorrect = false

                } else if (blockTypeValue.equals("temporary") || blockTypeValue == "custom") {
                    if (fromDate.equals(null) || fromDate.equals("") || fromDate.equals("From date")
                        || toDate.equals(null) || toDate.equals("") || toDate.equals("To date")
                    ) {
                        isCorrect = false

                    } else {
                        isCorrect = true
                    }
                } else if (blockTypeValue.equals("permanent")) {
                    isCorrect = true

                } else if (blockTypeValue.equals("none")) {
                    isCorrect = true
                }

            }
        } else {
            if (selectedSeats == null || selectedUserTypeList.size == 0 || userTypeId == 0) {
                isCorrect = false
            } else
                isCorrect = true
            if (userTypeId == 1) {// user type Onl-Agt
                if (agentId == null) {
                    isCorrect = false
                } else
                    isCorrect = true
            } else if (userTypeId == 12) {
                if (branchId == null) {
                    isCorrect = false

                } else {
                    if (userId == null) {
                        isCorrect = false

                    } else
                        isCorrect = true
                }
            }
            if (isCorrect) {
                if (blockTypeValue.equals("Select block type")) {
                    isCorrect = false

                } else if (blockTypeValue.equals("temporary") || blockTypeValue == "custom") {
                    if (fromDate.equals(null) || fromDate.equals("") || fromDate.equals("From date")
                        || toDate.equals(null) || toDate.equals("") || toDate.equals("To date")
                    ) {
                        isCorrect = false

                    } else {
                        isCorrect = true
                    }
                } else if (blockTypeValue.equals("permanent")) {
                    isCorrect = true

                } else if (blockTypeValue.equals("none")) {
                    isCorrect = true
                }
            }


        }
        _changeButtonBackground.postValue(isCorrect)
    }

    fun validation1(
        selectedSeats: String?,
        selectedUserTypeList: MutableList<SpinnerItems>,
        userTypeId: Int,
        branchId: String?,
        userId: String?,
        agentId: String?,
        blockTypeValue: String?,
        fromDate: String?,
        toDate: String?,
        timeHH: String,
        timeMM: String,
        isAllowMultipleQuota: Boolean
    ) {

        var isCorrect: Boolean = false
        if (isAllowMultipleQuota) {
            if (selectedSeats.equals(null) || selectedUserTypeList.size == 0) {
                _validation.postValue("Please select seat(s)")
                isCorrect = false
            } else if (selectedUserTypeList.size == 1) {
                if (userTypeId == 1) {// user type Onl-Agt
                    isCorrect = true
                } else if (userTypeId == 12) {
                    if (branchId == null) {
                        isCorrect = true
                    } else {
                        if (userId == null) {
                            _validation.postValue("Please select User")
                            isCorrect = false
                        } else
                            isCorrect = true
                    }
                } else
                    isCorrect = true
            } else if (selectedUserTypeList.size >= 2) {
                isCorrect = true
            }
            if (isCorrect) {
                if (blockTypeValue.equals("Select block type")) {
                    _validation.postValue("Please select block type")
                    isCorrect = false
                } else if (blockTypeValue.equals("temporary") || blockTypeValue == "custom") {
                    if (fromDate.equals(null) || fromDate.equals("") || fromDate.equals("From date")
                        || toDate.equals(null) || toDate.equals("") || toDate.equals("To date")
                    ) {
                        _validation.postValue("Please Enter Valid Date")
                        return
                    } else {
                        isCorrect = true
                    }
                } else if (blockTypeValue.equals("permanent")) {
                    isCorrect = true
                }
            }

        } else {
            if (selectedSeats == null) {
                _validation.postValue("Please select seat(s)")
                isCorrect = false
            } else
                isCorrect = true
            if (selectedUserTypeList.size == 0 || userTypeId == 0) {
                _validation.postValue("Please select usertype")
                isCorrect = false
            } else
                isCorrect = true
            if (userTypeId == 1) {// user type Onl-Agt
                if (agentId == null) {
                    _validation.postValue("Please select agent")
                    isCorrect = false
                } else
                    isCorrect = true
            } else if (userTypeId == 12) {
                if (branchId == null) {
                    _validation.postValue("Please select branch")
                    isCorrect = false

                } else {
                    if (userId == null) {
                        _validation.postValue("Please select user")
                        isCorrect = false

                    } else
                        isCorrect = true
                }
            }
            if (isCorrect) {
                if (blockTypeValue.equals("Select block type")) {
                    _validation.postValue("Please select block type")
                    isCorrect = false
                } else if (blockTypeValue.equals("temporary") || blockTypeValue == "custom") {
                    if (fromDate.equals(null) || fromDate.equals("") || fromDate.equals("From date")
                        || toDate.equals(null) || toDate.equals("") || toDate.equals("To date")
                    ) {
                        _validation.postValue("Please Enter Valid Date")
                        return
                    } else {
                        isCorrect = true
                    }
                } else if (blockTypeValue.equals("permanent")) {
                    isCorrect = true
                }
            }
        }
        if (isCorrect)
            _validation.postValue("")
        else _validation.postValue("Please enter correct information")
    }
}