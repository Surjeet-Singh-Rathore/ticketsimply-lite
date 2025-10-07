package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.appcompat.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.booking_summary.*
import com.bitla.ts.domain.pojo.booking_summary.request.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible

class SelectedSearchShiftPassengerActivity : BaseActivity(), OnItemClickListener,
    SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener,
    OnItemCheckedListener, View.OnClickListener, DialogSingleButtonListener {
    private val chartType: String = "3" //Fixed (chart type: 3= sort by pnr number)
    private lateinit var selectedSearchShiftPassengerBinding: ActivitySelectedSearchShiftPassengerBinding
    private lateinit var searchAdapter: SelectPassengerChildAdapter
    private lateinit var layoutManagerCity: RecyclerView.LayoutManager
    private var bookingList = mutableListOf<Booking>()
    private var apiKey: String = ""
    private var bccId: String = ""
    private var resId: Long = 0L
    private var loginModelPref = LoginModel()
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var checkedPnrList = mutableListOf<String>()
    private var locale: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        getPref()
        initUI()
        clickListener()

        if (isNetworkAvailable()) {
            pickUpChartApi()
        } else {
            noNetworkToast()
        }

        setObserver()
        setPassengersAdapter()
    }

    override fun isInternetOnCallApisAndInitUI() {
        getPref()
        initUI()
        clickListener()

        if (isNetworkAvailable()) {
            pickUpChartApi()
        } else {
            noNetworkToast()
        }

        setObserver()
        setPassengersAdapter()
    }

    @SuppressLint("SetTextI18n")
    override fun initUI() {
        selectedSearchShiftPassengerBinding =
            ActivitySelectedSearchShiftPassengerBinding.inflate(layoutInflater)
        val view = selectedSearchShiftPassengerBinding.root
        setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(selectedSearchShiftPassengerBinding.root)
        }
        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    private fun clickListener() {
        selectedSearchShiftPassengerBinding.btnSendSms.setOnClickListener(this)
        selectedSearchShiftPassengerBinding.etSearch.setOnQueryTextListener(this)
        selectedSearchShiftPassengerBinding.toolbarImageLeft.setOnClickListener(this)

    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key
        if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
            resId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)!!
    }


    private fun callBookingSummaryApi() {
        val reqBody = ReqBody(
            loginModelPref.api_key,
            resId.toString(),
            response_format,
            locale = locale
        )
        val bookingSummaryRequest = BookingSummaryRequest(
            bccId, format_type,
            booking_summary_method_name, reqBody
        )


        sharedViewModel.bookingSummaryApi(
            loginModelPref.api_key,
            resId.toString(),
            response_format,
            locale!!,
            booking_summary_method_name
        )

        Timber.d("bookingSummaryRequest $bookingSummaryRequest")
    }

    private fun pickUpChartApi() {

        pickUpChartViewModel.viewReservationAPI(
            apiKey = loginModelPref.api_key,
            resId = resId.toString(),
            chartType = chartType,
            locale = locale ?: "en",
            apiType = view_reservation_method_name,
            newPickUpChart = null
        )

    }

    private fun setObserver() {
        sharedViewModel.loadingState.observe(this) {
            when (it) {
                LoadingState.LOADING -> selectedSearchShiftPassengerBinding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> selectedSearchShiftPassengerBinding.includeProgress.progressBar.gone()
                else -> selectedSearchShiftPassengerBinding.includeProgress.progressBar.gone()
            }
        }

        pickUpChartViewModel.viewReservationResponse.observe(this, Observer { it ->
            // binding.includeProgress.progressBar.gone()
            if (it != null) {
                Timber.d("viewReservationResponse $it")
                if (it.code == 200) {
                    when {
                        it.passengerDetails != null -> {
                            it.passengerDetails.forEach {
                                val booking = Booking(
                                    isChecked = false,
                                    seats = it.seatNumber,
                                    ticket_number = it.pnrNumber,
                                    total_bookings = 0,
                                    passenger_name = it.passengerName
                                )
                                booking.boarding_point = it.stageName
                                bookingList.add(booking)
                            }
                            setPassengersAdapter()

                        }
                        it.code == 401 -> {
                            /*DialogUtils.unAuthorizedDialog(
                                this,
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            showUnauthorisedDialog()

                        }
                        else -> {
                            it.result?.message?.let { it1 -> toast(it1) }
                        }
                    }
                } else {
                    toast(it.message)
                }
            } else {
                toast(getString(R.string.server_error))
            }
        })
    }

    private fun setPassengersAdapter() {
        layoutManagerCity = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        selectedSearchShiftPassengerBinding.rvSelectSearchPassenger.layoutManager =
            layoutManagerCity
        searchAdapter = SelectPassengerChildAdapter(this, this, this)
        searchAdapter.addData(bookingList)
        selectedSearchShiftPassengerBinding.rvSelectSearchPassenger.adapter = searchAdapter
        searchAdapter.notifyDataSetChanged()

    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {
    }

    override fun onMenuItemClick(
        itemPosition: Int,
        menuPosition: Int,
        busData: Result
    ) {

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.toolbar_image_left -> onBackPressed()

            R.id.btnSendSms -> {
                if (checkedPnrList.isEmpty())
                    toast(getString(R.string.validate_passenger))
                else {
                    val intent = Intent(this, SelectMessageActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchAdapter.filter.filter(newText)
        return false

    }

    override fun onItemChecked(isChecked: Boolean, view: View, position: Int) {
        val ticketNo = bookingList[position].ticket_number
        if (isChecked)
            checkedPnrList.add(ticketNo)
        else
            checkedPnrList.remove(ticketNo)

        saveBookingList(bookingList)
        val checkedPnr = checkedPnrList.toString().replace("[", "").replace("]", "").trim()
        Timber.d("checkedPnrList $checkedPnr")
        PreferenceUtils.putString(PREF_CHECKED_PNR, checkedPnr)
        changeButtonColor()
    }

    private fun changeButtonColor() {
        if (checkedPnrList.isNotEmpty())
            selectedSearchShiftPassengerBinding.btnSendSms.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        else
            selectedSearchShiftPassengerBinding.btnSendSms.setBackgroundColor(resources.getColor(R.color.button_default_color))
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
