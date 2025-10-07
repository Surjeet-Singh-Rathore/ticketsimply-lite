package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.*
import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.book_extra_seat.*
import com.bitla.ts.domain.pojo.book_ticket_full.*
import com.bitla.ts.domain.pojo.book_ticket_full.request.*
import com.bitla.ts.domain.pojo.book_ticket_full.request.ReqBody
import com.bitla.ts.domain.pojo.book_with_extra_seat.response.*
import com.bitla.ts.domain.pojo.booking_history.response.*
import com.bitla.ts.domain.pojo.campaigns_and_promotions_discount.request.*
import com.bitla.ts.domain.pojo.campaigns_and_promotions_discount.response.*
import com.bitla.ts.domain.pojo.ezetap.*
import com.bitla.ts.domain.pojo.fare_breakup.response.*
import com.bitla.ts.domain.pojo.getCouponDiscount.*
import com.bitla.ts.domain.pojo.getCouponDiscount.Response.*
import com.bitla.ts.domain.pojo.getPrefillPassenger.*
import com.bitla.ts.domain.pojo.paytm_pos_integration.paytm_pos_txn_status_api.request.PaytmPosTxnStatusRequest
import com.bitla.ts.domain.pojo.photo_block_tickets.response.*
import com.bitla.ts.domain.pojo.pinelabs.*
import com.bitla.ts.domain.pojo.rapid_booking.request.*
import com.bitla.ts.domain.pojo.rutDiscountDetails.request.*
import com.bitla.ts.domain.pojo.rutDiscountDetails.response.*
import com.bitla.ts.domain.pojo.shortRouteCityPair.*
import com.bitla.ts.domain.pojo.upi_check_status.response.*
import com.bitla.ts.domain.pojo.upi_create_qr.response.*
import com.bitla.ts.domain.pojo.validate_otp_wallets.*
import com.bitla.ts.domain.pojo.wallet_otp_generation.*
import com.bitla.ts.domain.pojo.your_bus_location.YourBusLocation
import com.bitla.ts.domain.repository.*
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.*
import com.google.gson.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class BookingOptionViewModel<T : Any?>(private val bookingRepository: BookingRepository) :
    ViewModel() {

    companion object {
        val TAG: String = BookingOptionViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _fareBreakup = MutableLiveData<FareBreakupResponse>()
    val fareBreakup: LiveData<FareBreakupResponse>
        get() = _fareBreakup

    private val _pinelabTransaction = MutableLiveData<PinelabTransactionResponse>()
    val pinelabTransactionData: LiveData<PinelabTransactionResponse>
        get() = _pinelabTransaction

    private val _shortRouteCityPair = MutableLiveData<ShortRouteCityPairApiResponse>()
    val shortRouteCityPair: LiveData<ShortRouteCityPairApiResponse>
        get() = _shortRouteCityPair

    private val _bookTicketFull = MutableLiveData<BookTicketFullResponse>()
    val bookTicketFull: LiveData<BookTicketFullResponse>
        get() = _bookTicketFull

    private val _confirmPhoneBlockTicket = MutableLiveData<ConfirmPhoneBlockTicketResponse>()
    val confirmPhoneBlockTicket: LiveData<ConfirmPhoneBlockTicketResponse>
        get() = _confirmPhoneBlockTicket

    private val _showBookingHistory = MutableLiveData<ShowBookingHistoryResponse>()
    val showBookingHistory: LiveData<ShowBookingHistoryResponse>
        get() = _showBookingHistory

    private val _bookExtraSeat = MutableLiveData<BookExtraSeatResponse>()
    val bookExtraSeat: LiveData<BookExtraSeatResponse>
        get() = _bookExtraSeat

    private val _bookSeatWithExtraSeat = MutableLiveData<BookSeatWithExtraSeatResponse>()
    val bookSeatWithExtraSeat: LiveData<BookSeatWithExtraSeatResponse>
        get() = _bookSeatWithExtraSeat

    private val _walletOtpGeneration = MutableLiveData<WalletOtpGenerationModel>()
    val walletOtpGeneration: LiveData<WalletOtpGenerationModel>
        get() = _walletOtpGeneration

    private val _validateWalletOtp = MutableLiveData<ValidateOtpWalletsModel>()
    val validateWalletOtp: LiveData<ValidateOtpWalletsModel>
        get() = _validateWalletOtp

    private val _upiCreateQRCodeResponse = MutableLiveData<UPICreateQRCodeResponse>()
    val upiCreateQRCodeObserver: LiveData<UPICreateQRCodeResponse>
        get() = _upiCreateQRCodeResponse

    private val _upiTranxStatusResponse = MutableLiveData<UpiTranxStatusResponse>()
    val upiTranxStatusObserver: LiveData<UpiTranxStatusResponse>
        get() = _upiTranxStatusResponse

    private val _getCouponDetails = MutableLiveData<GetCouponDetailResponse>()
    val getCouponDetails: LiveData<GetCouponDetailResponse>
        get() = _getCouponDetails

    private val _getRutDiscount = MutableLiveData<RutDiscountResponse>()
    val getRutDiscount: LiveData<RutDiscountResponse>
        get() = _getRutDiscount
    private val _getPrefillPassenger = MutableLiveData<JsonElement>()
    val getPrefillPassenger: LiveData<JsonElement>
        get() = _getPrefillPassenger

    //    private val _generateQrCode = MutableLiveData<BookTicketFullResponse>()
//    val generateQrCode: LiveData<BookTicketFullResponse>
//        get() = _bookTicketFull
    private var apiType: String? = null

    private val _campaignsAndPromotionsDiscount =
        MutableLiveData<CampaignsAndPromotionsDiscountResponse>()
    val campaignsAndPromotionsDiscount: LiveData<CampaignsAndPromotionsDiscountResponse>
        get() = _campaignsAndPromotionsDiscount
    private val _ezetapTransaction = MutableLiveData<EzetapTransactionResponse>()
    val ezetapTransactionData: LiveData<EzetapTransactionResponse>
        get() = _ezetapTransaction

    val messageSharedFlow = MutableSharedFlow<String>()



    private val _paytmPosTxnStatusResponse = MutableLiveData<BookTicketFullResponse?>()
    val paytmPosTxnStatusResponse: LiveData<BookTicketFullResponse?>
        get() = _paytmPosTxnStatusResponse

    private val _confirmPhonePeV2PendingSeatResponse = MutableLiveData<String>()
    val confirmPhonePeV2PendingSeatResponse: LiveData<String>
        get() = _confirmPhonePeV2PendingSeatResponse


    /* fun fareBreakupApi(
         authorization: String,
         apiKey: String,
         fareBreakupRequest: FareBreakupRequest,
         apiType: String
     ) {
       
         _loadingState.postValue(LoadingState.LOADING)

         viewModelScope.launch(Dispatchers.IO) {
             _fareBreakup.postValue(
                 bookingRepository.fareBreakup(
                     authorization,
                     apiKey,
                     fareBreakupRequest = fareBreakupRequest
                 ).body()
             )
         }
     } */

    fun fareBreakupApi(
        fareBreakupRequest: com.bitla.ts.domain.pojo.fare_breakup.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.newFareBreakup(
                fareBreakupRequest = fareBreakupRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _fareBreakup.postValue(
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

    fun pinelabStatusApi(
        reqBody: ReqBodyPinelab,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.pineLabStatusApi(
                reqBody
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _pinelabTransaction .postValue(
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

    fun shortRouteCityPairAPI(
        apiKey: String,
        resId: String,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.shortRouteCityPairAPI(
                apiKey, resId = resId
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _shortRouteCityPair.postValue(
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

    /*fun bookTicketFullApi(
        authorization: String,
        apiKey: String,
        bookTicketFullRequest: BookTicketFullRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _bookTicketFull.postValue(
                bookingRepository.bookTicketFull(
                    authorization,
                    apiKey,
                    bookTicketFullRequest = bookTicketFullRequest
                ).body()
            )
        }
    } */

    fun bookTicketFullApi(
        reqBody: ReqBody,
        apiType: String
    ) {


        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.newBookTicketFull(
                reqBody
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _bookTicketFull.postValue(
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


    fun bookTicketWithInsurance(
        reqBody: ReqBodyWithInsurance,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.bookTicketWithInsurance(
                reqBody
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _bookTicketFull.postValue(
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

    fun bookTicketWithRapidBooking(
        reqBody: RapidBookingRequest,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.bookTicketWithRapidBooking(
                reqBody
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _bookTicketFull.postValue(
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


    /*    fun showBookingHistoryApi(
            authorization: String,
            apiKey: String,
            showBookingHistoryRequest: ShowBookingHistoryRequest,
            apiType: String
        ) {

            _loadingState.postValue(LoadingState.LOADING)

            viewModelScope.launch(Dispatchers.IO) {
                _showBookingHistory.postValue(
                    bookingRepository.showBookingHistory(
                        authorization,
                        apiKey,
                        showBookingHistoryRequest
                    ).body()
                )
            }
        }*/

    fun showBookingHistoryApi(
        apiKey: String,
        pnr_number: String,
        response_format: String,
        locale: String,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.newShowBookingHistory(
                apiKey,
                pnr_number,
                response_format,
                locale
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _showBookingHistory.postValue(
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


    /* fun confirmPhoneBlockTicketApi(
         authorization: String,
         apiKey: String,
         confirmPhoneBlockTicketReq: ConfirmPhoneBlockTicketReq,
         apiType: String
     ) {
       
         _loadingState.postValue(LoadingState.LOADING)

         viewModelScope.launch(Dispatchers.IO) {
             _confirmPhoneBlockTicket.postValue(
                 bookingRepository.confirmPhoneBlockTicket(
                     authorization,
                     apiKey,
                     confirmPhoneBlockTicketReq
                 ).body()
             )
         }
     } */

    fun confirmPhoneBlockTicketApi(
        confirmPhoneBlockTicketReq: com.bitla.ts.domain.pojo.photo_block_tickets.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.newConfirmPhoneBlockTicket(
                confirmPhoneBlockTicketReq
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _confirmPhoneBlockTicket .postValue(
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

    fun confirmBimaPhoneBlockTicketApi(
        confirmPhoneBlockTicketReq: com.bitla.ts.domain.pojo.photo_block_tickets.request.ReqBody,
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.newConfirmBimaPhoneBlockTicket(
                confirmPhoneBlockTicketReq
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _confirmPhoneBlockTicket.postValue(
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

    /*fun bookExtraSeatApi(
        authorization: String,
        apiKey: String,
        bookExtraSeatRequest: BookExtraSeatRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _bookExtraSeat.postValue(
                bookingRepository.bookExtraSeat(
                    authorization,
                    apiKey,
                    bookExtraSeatRequest = bookExtraSeatRequest
                ).body()
            )
        }
    }  */

    fun bookExtraSeatApi(
        bookExtraSeatRequest: com.bitla.ts.domain.pojo.book_extra_seat.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {

            bookingRepository.newBookExtraSeat(
                bookExtraSeatRequest = bookExtraSeatRequest
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _bookExtraSeat .postValue(
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

    fun bookSeatWithExtraSeatApi(
        bookExtraSeatRequest: com.bitla.ts.domain.pojo.book_with_extra_seat.request.BookTicketWithExtraSeatRequest,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.newBookWithExtraSeat(
                reqBody = bookExtraSeatRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _bookSeatWithExtraSeat.postValue(
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

    fun walletOtpGenerationApi(
        reqBody: com.bitla.ts.domain.pojo.wallet_otp_generation.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.newWalletOtpGeneration(
                reqBody
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _walletOtpGeneration .postValue(
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


    fun validateWalletOtpApi(
        req_body: com.bitla.ts.domain.pojo.validate_otp_wallets.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.validateOtpWallet(
                req_body
            ) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _validateWalletOtp .postValue(
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

    fun upiCreateQrCodeApi(
        reqBody: com.bitla.ts.domain.pojo.upi_create_qr.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.upiCreateQrCode(
                reqBody
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _upiCreateQRCodeResponse.postValue(
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


    fun upiTranxStatusApi(
        reqBody: com.bitla.ts.domain.pojo.upi_check_status.request.ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.upiTranxStatus(
                reqBody
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _upiTranxStatusResponse.postValue(
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
    
    fun getPayStatOfAgentInsRechargStatusApi(
        apiKey:String,
        pnrNumber:String,
//        amount:String,
        phone:String,
        isFromAgentRecharge:String
    ) {
        
        _loadingState.postValue(LoadingState.LOADING)
        
        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.getAgentUpiTranxStatus(
                apiKey = apiKey,
                pnrNumber = pnrNumber,
//                amount = amount,
                phone = phone,
                isFromAgentRecharge = isFromAgentRecharge,
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _upiTranxStatusResponse.postValue(
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

    fun getBranchUpiTranxStatusApi(
        apiKey:String,
        pnrNumber:String,
        branchPhone:String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.getBranchUpiTranxStatus(
                apiKey = apiKey,
                pnrNumber = pnrNumber,
                branchPhone = branchPhone
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _upiTranxStatusResponse.postValue(
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

    fun getCouponDetails(
        getCouponDetailsRequest: GetCouponDiscountRequest
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.getCouponDiscount(getCouponDetailsRequest).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _getCouponDetails.postValue(
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

    fun getRutDiscount(
        rutDiscountRequest: RutDiscountRequest
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.getRutDiscountDetails(rutDiscountRequest) .collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _getRutDiscount.postValue(
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

    fun getPrefillPassenger(
        getPrefillPassengerRequest: GetPrefillPassengerRequest
    ) {

        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.getPrefillPassenger(getPrefillPassengerRequest).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _getPrefillPassenger.postValue(
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

    fun campaignsAndPromotionsDiscount(
        campaignsAndPromotionsDiscountRequest: CampaignsAndPromotionsDiscountRequest?
    ) {
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.campaignsAndPromotionsDiscount(
                campaignsAndPromotionsDiscountRequest
            ).collect {
            when (it) {
                is NetworkProcess.Loading -> {}
                is NetworkProcess.Success -> {
                    _loadingState.postValue(LoadingState.LOADED)
                    _campaignsAndPromotionsDiscount.postValue(
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


    fun ezetapStatusApi(
        reqBody: ReqBodyEzetapStatus,
        apiType: String
    ) {
        this.apiType = apiType
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.ezetapStatusApi(
                reqBody
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _ezetapTransaction    .postValue(
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


    fun paytmPosTxnStatusApi(
        paytmPosTxnStatusRequest: PaytmPosTxnStatusRequest,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _paytmPosTxnStatusResponse.postValue(
                bookingRepository.getPaytmPosTxnStatusApi(
                    paytmPosTxnStatusRequest
                ).body()
            )
        }
    }

    fun confirmPhonePeV2PendingSeat(
        pnrNumber: String
    ) {
        _loadingState.postValue(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            bookingRepository.confirmPhonePeV2PendingSeat(
                pnrNumber
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _confirmPhonePeV2PendingSeatResponse.postValue(
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
}