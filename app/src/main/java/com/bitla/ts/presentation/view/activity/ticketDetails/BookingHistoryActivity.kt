package com.bitla.ts.presentation.view.activity.ticketDetails

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.response_format
import com.bitla.ts.data.show_booking_history_method_name
import com.bitla.ts.databinding.ActivityBookingHistoryBinding
import com.bitla.ts.domain.pojo.booking_history.request.ShowBookingHistoryRequest
import com.bitla.ts.domain.pojo.booking_history.response.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.adapter.BookingHistoryDateAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.viewModel.BookingOptionViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast

class BookingHistoryActivity : BaseActivity(), DialogSingleButtonListener {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var binding: ActivityBookingHistoryBinding
    private lateinit var showBookingHistoryAdapter: BookingHistoryDateAdapter

    private lateinit var bccId: String
    private lateinit var apiKey: String
    private var loginModelPref: LoginModel = LoginModel()
    private val bookingOptionViewModel by viewModel<BookingOptionViewModel<Any?>>()
    private var pnrNumber: String = ""
    private lateinit var showBookingHistoryList: MutableList<Result>
    private var bookingHistoryFoundCount: Int = 0
    private var locale = PreferenceUtils.getlang()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getPref()

        if (intent.hasExtra(getString(R.string.pnr_number))) {
            pnrNumber = intent.getStringExtra(getString(R.string.pnr_number))
                ?: getString(R.string.empty)

        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        callShowBookingHistoryApi()
        setShowBookingHistoryObserve()
        lifecycleScope.launch {
            bookingOptionViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }

    }

    override fun isInternetOnCallApisAndInitUI() {
        callShowBookingHistoryApi()
    }

    override fun initUI() {
        binding = ActivityBookingHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key

    }

    private fun setBookHistoryDateAdapter(showBookingHistoryList: MutableList<Result>) {

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvBookingHistory.layoutManager = layoutManager
        showBookingHistoryAdapter = BookingHistoryDateAdapter(
            this, this,
            showBookingHistoryList as ArrayList<Result>
        )
        binding.rvBookingHistory.adapter = showBookingHistoryAdapter
    }


    // Call show booking history api
    private fun callShowBookingHistoryApi() {
        if (isNetworkAvailable()) {
            val reqBody =
                com.bitla.ts.domain.pojo.booking_history.request.ReqBody(
                    apiKey,
                    pnrNumber,
                    response_format,
                    locale = locale
                )
            val showBookingHistoryRequest = ShowBookingHistoryRequest(
                bccId, format_type, show_booking_history_method_name, reqBody
            )

            /*bookingOptionViewModel.showBookingHistoryApi(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                showBookingHistoryRequest,
                show_booking_history_method_name
            )*/
            bookingOptionViewModel.showBookingHistoryApi(
                loginModelPref.api_key,
                pnrNumber,
                response_format,
                locale,
                show_booking_history_method_name
            )

        } else
            noNetworkToast()
    }

    @SuppressLint("SetTextI18n")
    private fun setShowBookingHistoryObserve() {

        bookingOptionViewModel.showBookingHistory.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {

                        showBookingHistoryList = it.result
                        setBookHistoryDateAdapter(showBookingHistoryList)

                        Timber.d("messageShowBookingHistory-${it.result}")

                        repeat(it.result.size) { it ->
                            bookingHistoryFoundCount = it
                        }

                        binding.tvBookingHistoryFound.text =
                            "${bookingHistoryFoundCount + 1} ${getString(R.string.found)}"
                        binding.toolbarTitle.text =
                            "${getString(R.string.booking_history)}-${pnrNumber}"

                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }

                    454 -> {
                        toast(it.message)
                    }

                    else -> {
                        toast(getString(R.string.something_went_wrong))
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}