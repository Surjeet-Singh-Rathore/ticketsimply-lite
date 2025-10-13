package com.bitla.ts.presentation.view.activity.ticketDetails
import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.BookingSummaryBinding
import com.bitla.ts.databinding.LayoutBookTicketsBinding
import com.bitla.ts.domain.pojo.booking_summary_details.BookingSummaryResponse
import com.bitla.ts.domain.pojo.booking_summary_details.Detail
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.viewModel.BookingSummaryViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_RESERVATION_ID
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible

class BookingSummaryActivity : BaseActivity(), DialogSingleButtonListener {

    private lateinit var binding: BookingSummaryBinding
    private var loginModelPref: LoginModel = LoginModel()
    private var resID: String? = null
    private val bookingSummaryDetailsViewModel by viewModel<BookingSummaryViewModel<Any?>>()
    private var totalSeatsBooking: String? = null
    private var totalRevenueBooking: String? = null
    private var seatsBooking: String? = null
    private var dash: String? = null
    private var revenueBooking: String? = null
    private var revenueDashBooking: String? = null
    private var bookingSummaryTitle: String? = null
    private var currecny=""
    private var currencyFormat=""

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        resID = PreferenceUtils.getPreference(
            PREF_RESERVATION_ID, 0L
        ).toString()

        currecny=getPrivilegeBase()?.currency?:""
        currencyFormat=getPrivilegeBase()?.currencyFormat?:this.getString(R.string.indian_currency_format)
    }

    @SuppressLint("LongLogTag", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BookingSummaryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        totalSeatsBooking = getString(R.string.total_seats_booking_summary)
        totalRevenueBooking = getString(R.string.total_revenue_booking_summary)
        seatsBooking = getString(R.string.seats_booking_summary)
        dash = ""
        revenueBooking = getString(R.string.revenue_booking_summary)
        revenueDashBooking = getString(R.string.revenue_dash)
        bookingSummaryTitle = getString(R.string.booking_summary)
        getPref()
        val textviewToolbar: TextView = binding.layoutToolbar1.toolbarHeaderText
        textviewToolbar.text = bookingSummaryTitle
        binding.OTAWiseL.tvBookingTypeHeader.text = getString(R.string.ota_booking_summary)
        binding.branchWiseL.tvBookingTypeHeader.text = getString(R.string.agent_booking_summary)
        binding.agentWiseL.tvBookingTypeHeader.text = getString(R.string.branch_booking_summary)
        binding.eBookingWiseL.tvBookingTypeHeader.text = getString(R.string.eBooking_booking_summary)
        binding.layoutToolbar1.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.OTAWiseL.summaryImg.setOnClickListener {
            if (binding.OTAWiseL.summaryRV.isVisible) {
                binding.OTAWiseL.summaryRV.gone()
                binding.OTAWiseL.summaryImg.animate().rotation(0f).setDuration(500)

            } else {
                binding.OTAWiseL.summaryRV.visible()
                binding.OTAWiseL.summaryImg.animate().rotation(-180.0f).setDuration(500)
            }

        }

        binding.agentWiseL.summaryImg.setOnClickListener {
            if (binding.agentWiseL.summaryRV.isVisible) {
                binding.agentWiseL.summaryRV.gone()
                binding.agentWiseL.summaryImg.animate().rotation(0f).setDuration(500)

            } else {
                binding.agentWiseL.summaryRV.visible()
                binding.agentWiseL.summaryImg.animate().rotation(-180.0f).setDuration(500)
            }

        }
        binding.branchWiseL.summaryImg.setOnClickListener {
            if (binding.branchWiseL.summaryRV.isVisible) {
                binding.branchWiseL.summaryRV.gone()
                binding.branchWiseL.summaryImg.animate().rotation(0f).setDuration(500)

            } else {
                binding.branchWiseL.summaryRV.visible()
                binding.branchWiseL.summaryImg.animate().rotation(-180.0f).setDuration(500)
            }

        }
        binding.eBookingWiseL.summaryImg.setOnClickListener {
            if (binding.eBookingWiseL.summaryRV.isVisible) {
                binding.eBookingWiseL.summaryRV.gone()
                binding.eBookingWiseL.summaryImg.animate().rotation(0f).setDuration(500)

            } else {
                binding.eBookingWiseL.summaryRV.visible()
                binding.eBookingWiseL.summaryImg.animate().rotation(-180.0f).setDuration(500)
            }

        }
        resID?.let {
            bookingSummaryDetailsViewModel.bookingSummaryDetailsAPI(
                loginModelPref.api_key,
                it
            )
        }

        bookingSummaryDetailsViewModel.bookingSummaryDetails.observe(this) {

            try {
                if (it != null) {
                    binding.progressBar.gone()
                    when (it.code) {
                        200 -> {

                            setBookingSummaryObserver(it)
                        }

                        401 -> {
                            DialogUtils.unAuthorizedDialog(
                                this,
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )
                        }

                        else -> {
                            R.layout.layout_book_tickets
                            val bindingBookTickets =
                                LayoutBookTicketsBinding.inflate(layoutInflater)
                            bindingBookTickets.noData.isVisible
                            bindingBookTickets.tvNoService.text = it.message.toString()
                            bindingBookTickets.tvNoService.isVisible

                        }

                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                toast("An error occurred")
            }

        }
    }

    private fun ToSpan(text: String, textToBoldEndingIndex: Int): Spannable {
        val stringToSpan: Spannable =
            SpannableString(text)

        stringToSpan.setSpan(
            StyleSpan(Typeface.BOLD),
            textToBoldEndingIndex, text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        stringToSpan.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    applicationContext!!,
                    R.color.blackish
                )
            ),
            0,
            textToBoldEndingIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return stringToSpan
    }


    override fun initUI() {
    }
    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    private fun setBookingSummaryObserver(it: BookingSummaryResponse) {
        Timber.d(totalSeatsBooking + it?.totalSeats)

        val totalSeats = totalSeatsBooking + " " + it?.totalSeats
        val totalRevenue = totalRevenueBooking + it?.totalRevenue
        val seatsOTA = seatsBooking + " " + it?.otaData?.seatCount
        val revenueOTA = revenueBooking + it?.otaData?.revenue
        val dashSeatsOTA = "$seatsBooking $dash"
        val dashRevenueOTA = "$revenueDashBooking $dash"

        val seatsAgent = seatsBooking + " " + it?.agentData?.seatCount
        val revenueAgent = revenueBooking + it?.agentData?.revenue
        val dashSeatsAgent = "$seatsBooking $dash"
        val dashRevenueAgent = "$revenueDashBooking $dash"

        val seatsBranch = seatsBooking + " " + it?.branchData?.seatCount
        val revenueBranch = revenueBooking + it?.branchData?.revenue
        val dashSeatsBranch = "$seatsBooking $dash"
        val dashRevenueBranch = "$revenueDashBooking $dash"

        val seatsEBooking = seatsBooking + " " + it?.eBookingData?.seatCount
        val revenueEBooking = revenueBooking + it?.eBookingData?.revenue
        val dashSeatsEBooking = "$seatsBooking $dash"
        val dashRevenueEBooking = "$revenueDashBooking $dash"


        val totalSeatsSpan = totalSeatsBooking?.length?.let { it1 -> ToSpan(totalSeats, it1) }
        val totalRevenueSpan = totalRevenueBooking?.length?.let { it1 -> ToSpan(totalRevenue, it1) }

        val seatsOTASpan = seatsBooking?.length?.let { it1 -> ToSpan(seatsOTA, it1) }
        val revenueOTASpan = revenueBooking?.length?.let { it1 -> ToSpan(revenueOTA, it1) }
        val seatsDashOTASpan = seatsBooking?.length?.let { it1 -> ToSpan(dashSeatsOTA, it1) }
        val revenueDashOTASpan = revenueDashBooking?.length?.let { it1 ->
            ToSpan(dashRevenueOTA,
                it1
            )
        }

        val seatsBranchSpan = seatsBooking?.length?.let { it1 -> ToSpan(seatsBranch, it1) }
        val revenueBranchSpan = revenueBooking?.length?.let { it1 -> ToSpan(revenueBranch, it1) }
        val seatsDashBranchSpan = seatsBooking?.length?.let { it1 -> ToSpan(dashSeatsBranch, it1) }
        val revenueDashBranchSpan =
            revenueDashBooking?.length?.let { it1 -> ToSpan(dashRevenueBranch, it1) }

        val seatsAgentSpan = seatsBooking?.length?.let { it1 -> ToSpan(seatsAgent, it1) }
        val revenueAgentSpan = revenueBooking?.length?.let { it1 -> ToSpan(revenueAgent, it1) }
        val seatsDashAgentSpan = seatsBooking?.length?.let { it1 -> ToSpan(dashSeatsAgent, it1) }
        val revenueDashAgentSpan =
            revenueDashBooking?.length?.let { it1 -> ToSpan(dashRevenueAgent, it1) }

        val seatsEBookingSpan = seatsBooking?.length?.let { it1 -> ToSpan(seatsEBooking, it1) }
        val revenueEBookingSpan = revenueBooking?.length?.let { it1 ->
            ToSpan(revenueEBooking,
                it1
            )
        }
        val seatsDashEBookingSpan = seatsBooking?.length?.let { it1 ->
            ToSpan(dashSeatsEBooking,
                it1
            )
        }
        val revenueDashEBookingSpan =
            revenueDashBooking?.length?.let { it1 -> ToSpan(dashRevenueEBooking, it1) }


        binding.TopTextTV.text = totalSeatsSpan
        binding.TopTextTV1.text = totalRevenueSpan
        //OTA
        if (it?.otaData?.seatCount == null || it?.otaData?.revenue == null) {
            if (it?.otaData?.seatCount == null) {
                binding.OTAWiseL.tvSeats.text = seatsDashOTASpan
            }
            if (it?.otaData?.revenue == null) {
                binding.OTAWiseL.tvRevenue.text = revenueDashOTASpan
            }
        } else {
            binding.OTAWiseL.tvSeats.text = seatsOTASpan
            binding.OTAWiseL.tvRevenue.text = revenueOTASpan
        }

        //Agent
        if (it?.agentData?.seatCount == null || it?.agentData?.revenue == null) {
            if (it?.agentData?.seatCount == null) {
                binding.branchWiseL.tvSeats.text = seatsDashAgentSpan

            }
            if (it?.otaData?.revenue == null) {
                binding.branchWiseL.tvRevenue.text = revenueDashAgentSpan

            }
        } else {
            binding.branchWiseL.tvSeats.text = seatsAgentSpan
            binding.branchWiseL.tvRevenue.text = revenueAgentSpan

        }
        //Branch

        if (it?.branchData?.seatCount == null || it?.branchData?.revenue == null) {
            if (it?.branchData?.seatCount == null) {
                binding.agentWiseL.tvSeats.text = seatsDashBranchSpan
            }
            if (it?.otaData?.revenue == null) {
                binding.agentWiseL.tvRevenue.text = revenueDashBranchSpan
            }
        } else {
            binding.agentWiseL.tvSeats.text = seatsBranchSpan
            binding.agentWiseL.tvRevenue.text = revenueBranchSpan
        }

        //E-Booking
        if (it?.eBookingData?.seatCount == null || it?.eBookingData?.revenue == null) {
            if (it?.eBookingData?.seatCount == null) {
                binding.eBookingWiseL.tvSeats.text = seatsDashEBookingSpan
            }
            if (it?.otaData?.revenue == null) {
                binding.eBookingWiseL.tvRevenue.text = revenueDashEBookingSpan
            }
        } else {
            binding.eBookingWiseL.tvSeats.text = seatsEBookingSpan
            binding.eBookingWiseL.tvRevenue.text = revenueEBookingSpan
        }
    }


    override fun isInternetOnCallApisAndInitUI() {
    }

    override fun onSingleButtonClick(str: String) {

    }
}


