package com.bitla.ts.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitla.ts.domain.pojo.book_ticket.BookTicketModel
import com.bitla.ts.domain.pojo.book_ticket.request.ReqBody
import com.bitla.ts.domain.pojo.rapid_booking.RapidBookingModel
import com.bitla.ts.domain.repository.BookTicketRepository
import com.bitla.ts.koin.networkModule.NetworkProcess
import com.bitla.ts.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class BookTicketViewModel<T : Any?>(private val bookTicketRepository: BookTicketRepository) :
    ViewModel() {

    companion object {
        val TAG: String = BookTicketViewModel::class.java.simpleName
    }

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _dataBookTicket = MutableLiveData<BookTicketModel>()
    val dataBookTicket: LiveData<BookTicketModel>
        get() = _dataBookTicket

    private val _rapidBooking = MutableLiveData<RapidBookingModel>()
    val rapidBooking: LiveData<RapidBookingModel>
        get() = _rapidBooking


    private var apiType: String? = null

    val messageSharedFlow = MutableSharedFlow<String>()



    /*fun bookTicketApi(
        authorization: String,
        apiKey: String,
        bookTicketRequest: BookTicketRequest,
        apiType: String
    ) {
      
        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            _dataBookTicket.postValue(
                bookTicketRepository.bookTicket(
                    authorization,
                    apiKey,
                    bookTicketRequest
                ).body()
            )
        }
    } */

    fun bookTicketApi(
        bookTicketRequest: ReqBody,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookTicketRepository.newBookTicket(
                bookTicketRequest
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _dataBookTicket.postValue(
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


    fun rapidBookingApi(
        reqBody: Any,
        apiType: String
    ) {

        _loadingState.postValue(LoadingState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            bookTicketRepository.rapidBooking(
                reqBody
            ).collect {
                when (it) {
                    is NetworkProcess.Loading -> {}
                    is NetworkProcess.Success -> {
                        _loadingState.postValue(LoadingState.LOADED)
                        _rapidBooking.postValue(
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