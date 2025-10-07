package com.bitla.ts.presentation.view.activity


import android.content.*
import android.os.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.reservationOption.*
import com.bitla.ts.presentation.view.ticket_details_compose.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible

class SeatShiftingSuccessfulActivity : BaseActivity(), DialogSingleButtonListener {

    companion object {
        val TAG: String = ShiftPassengerActivity::class.java.simpleName
        private lateinit var binding: ActivitySeatShiftingSuccessfulBinding
    }

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var shiftlistAdapter: ShiftListAdapter
    private lateinit var bccId: String
    private lateinit var apiKey: String
    private var loginModelPref: LoginModel = LoginModel()
    private val ticketDetailsViewModel by viewModel<TicketDetailsViewModel<Any?>>()
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var locale: String? = ""
    private var resId: String? = ""
    private var country:String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUI()
        lifecycleScope.launch {
            ticketDetailsViewModel.messageSharedFlow.collect{
                if (it.isNotEmpty()){
                  showToast(it)
                }
            }
        }

    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    override fun initUI() {
        binding = ActivitySeatShiftingSuccessfulBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        binding.toolbarPassengerDetails.toolbarSubtitle.gone()
        getPref()
        PreferenceUtils.setPreference("shiftPassenger_tab", 0)

        val fromActivity = intent.getBooleanExtra("fromActivity", false)
        val oldServiceDetail = intent.getStringExtra("oldServiceNumberShift")?.split("?")
        val newServiceDetail =
            PreferenceUtils.getPreference("ApiNumberSelected", "nothing")?.split("?")
        Timber.d("shiftsomehinf: $oldServiceDetail, $newServiceDetail")



        val oldNumber = oldServiceDetail?.get(0)
        if (!oldServiceDetail.isNullOrEmpty() && oldServiceDetail.size > 1) {
            val oldTravelDate = oldServiceDetail.get(1)
            binding.tvShiftDateTime.text = oldTravelDate
        }
        val newNumber = newServiceDetail?.get(0)
        var newTravelDate = newServiceDetail?.get(1)
        newTravelDate = newTravelDate!!.replace("/","-")

        binding.newServiceNumber.text = newNumber
        binding.oldServiceNumber.text = oldNumber
        binding.tvShiftDateTimeNew.text = newTravelDate

//        Timber.d("serviceNumbers: old${oldServiceNumber}, new${newServiceNumber}")

        if (fromActivity) {
            binding.shiftPassengerProceedBtn.visible()
            val pnrNumber = intent.getStringExtra("pnrList")
            callTicketDetailsApi(pnrNumber!!)
            setTicketDetailsObserver()
            binding.shiftPassengerProceedBtn.setOnClickListener {
                val intent=Intent(this, TicketDetailsActivityCompose::class.java)
                intent.putExtra(getString(R.string.TICKET_NUMBER), pnrNumber)
                startActivity(intent)
                finish()
            }


        } else {
            binding.shiftPassengerProceedBtn.gone()

            val oldSeatNumber = intent.getStringExtra("oldSeatNumbers")!!
            val newSeatNumber = intent.getStringExtra("tempNewSeatNumber")!!
            val ticket = intent.getStringExtra("pnrList")!!
            val boardingfrom = intent.getStringExtra("boardingfromList")!!
            val droppingAt = intent.getStringExtra("dropingFromList")!!
            val passengerName = intent.getStringExtra("PassengerNameList")!!

            multiple(
                replaceBracketsString(oldSeatNumber),
                replaceBracketsString(ticket),
                replaceBracketsString(passengerName),
                replaceBracketsString(boardingfrom),
                replaceBracketsString(droppingAt),
                replaceBracketsString(newSeatNumber)
            )
        }


        binding.toolbarPassengerDetails.toolbarHeaderText.text =
            getString(R.string.shifted_successful)
        binding.toolbarPassengerDetails.imgBack.setOnClickListener {
            if (fromActivity) {
                val intent = Intent(this, NewCoachActivity::class.java)
                intent.putExtra("fromTicketDetails", true)
                startActivity(intent)
            } else {
                val intent = Intent(this, ViewReservationActivity::class.java)
                intent.putExtra("pickUpResid", resId)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val fromActivity = intent.getBooleanExtra("fromActivity", false)

        if (fromActivity) {
            val intent = Intent(this, NewCoachActivity::class.java)
            intent.putExtra("fromTicketDetails", true)
            startActivity(intent)
        } else {
            val intent = Intent(this, ViewReservationActivity::class.java)
            intent.putExtra("pickUpResid", resId)
            startActivity(intent)
        }
    }

    fun multiple(
        oldSeatNumber: String,
        ticket: String,
        passengerName: String,
        boardingfrom: String,
        droppingAt: String,
        newSeatNumber: String
    ) {
        if (oldSeatNumber.contains(",")) {
            val ticktarray = ticket.split(",")
            val passengerarray = passengerName.split(",")
            val boardingarray = boardingfrom.split(",")
            val droppingarray = droppingAt.split(",")
            val oldseatList = oldSeatNumber.split(",")
            val newList = newSeatNumber.split(",")
            
            adapter(
                oldSeat = oldseatList,
                newSeat = newList,
                boardingPoint = boardingarray,
                droppingPont = droppingarray,
                pnr = ticktarray,
                extraSeat = false,
                passengerName = passengerarray
            )
        } else {
            val ticktarray = arrayListOf<String>()
            val passengerarray = arrayListOf<String>()
            val boardingarray = arrayListOf<String>()
            val droppingarray = arrayListOf<String>()
            val oldSeatList = arrayListOf<String>()
            val newList = arrayListOf<String>()
            ticktarray.add(ticket)
            passengerarray.add(passengerName)
            boardingarray.add(boardingfrom)
            droppingarray.add(droppingAt)
            oldSeatList.add(oldSeatNumber)
            newList.add(newSeatNumber)
            adapter(
                oldSeat = oldSeatList,
                newSeat = newList,
                boardingPoint = boardingarray,
                droppingPont = droppingarray,
                pnr = ticktarray,
                extraSeat = false,
                passengerName = passengerarray
            )
        }
    }

    private fun adapter(
        oldSeat: List<String>,
        newSeat: List<String>,
        boardingPoint: List<String>,
        droppingPont: List<String>,
        pnr: List<String>,
        extraSeat: Boolean,
        passengerName: List<String>
    ) {

        layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.shiftDetails.layoutManager = layoutManager
        shiftlistAdapter =
            ShiftListAdapter(
                context = this,
                oldSeatList = oldSeat,
                newSeatList = newSeat,
                boardingPoint = boardingPoint,
                droppingPoint = droppingPont,
                pnr = pnr,
                extraSeat = extraSeat,
                passengerNameList = passengerName
            )
        binding.shiftDetails.adapter = shiftlistAdapter
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key
        locale = PreferenceUtils.getlang()
        country = getPrivilegeBase()?.country ?: ""
    }

    private fun callTicketDetailsApi(pnrNumber: String) {
        if (this.isNetworkAvailable()) {
            ticketDetailsViewModel.ticketDetailsApi(
                loginModelPref.api_key,
                pnrNumber,
                true,
                false, locale!!,
                ticket_details_method_name
            )

        } else
            this.noNetworkToast()
    }


    private fun setTicketDetailsObserver() {
        ticketDetailsViewModel.dataTicketDetails.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        val detail = it.body.passengerDetails!!
                        val boardingPoint = arrayListOf<String>()
                        val droppingPoint = arrayListOf<String>()
                        val pnrNumber = arrayListOf<String>()
                        val passengerName = arrayListOf<String>()
                        val moveToExtra = intent.getBooleanExtra("moveToExtra", false)
                        val oldSeatNumbers = intent.getStringExtra("oldSeatNumbers")
                        val tempNewSeatNumber = intent.getStringExtra("tempNewSeatNumber")
                        resId = it.body.reservationId.toString()
                        for (i in 0..detail.size.minus(1)) {
                            boardingPoint.add(it.body.origin)
                            droppingPoint.add(it.body.destination!!)
                            pnrNumber.add(it.body.ticketNumber!!)
                            passengerName.add(detail[i]?.name!!)
                        }
                        var finalOldseatList = ArrayList<String>()
                        var finalNewSeatList = ArrayList<String>()
                        if (oldSeatNumbers!!.contains(",")) {

                            val oldseatList = oldSeatNumbers.split(",") as ArrayList
                            val newList = tempNewSeatNumber?.split(",") as ArrayList
                            finalOldseatList.clear()
                            finalNewSeatList.clear()
                            finalNewSeatList = newList
                            finalOldseatList = oldseatList
                        } else {
                            finalOldseatList.clear()
                            finalNewSeatList.clear()
                            finalOldseatList.add(oldSeatNumbers)
                            finalNewSeatList.add(tempNewSeatNumber!!)
                        }


                        adapter(
                            finalOldseatList,
                            finalNewSeatList,
                            boardingPoint,
                            droppingPoint,
                            pnrNumber,
                            moveToExtra,
                            passengerName
                        )

                        PreferenceUtils.apply {
                            setPreference(PREF_SOURCE, it.body.origin)
                            setPreference(PREF_DESTINATION, it.body.destination)
                            setPreference(PREF_TRAVEL_DATE, it.body.travelDate)
                            setPreference(PREF_SOURCE_ID, it.body.originId.toString())
                            setPreference(PREF_DESTINATION_ID, it.body.destinationId.toString())
                            setPreference(PREF_BUS_TYPE, it.body.busType)
                            setPreference(PREF_DEPARTURE_TIME, it.body.depTime)
                        }

                    }
                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }
                    else -> {

                    }
                }

            } else {
                toast(getString(R.string.opps))
            }
        }
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
