package com.bitla.ts.presentation.adapter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.EditPassengerSheet
import com.bitla.ts.databinding.PassengerStageAdapterBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.view_reservation.PassengerDetail
import com.bitla.ts.domain.pojo.view_reservation.PassengerDetailX
import com.bitla.ts.domain.pojo.view_reservation.RespHash
import com.bitla.ts.presentation.adapter.NewSortByAdaper.PassengerSortSublistAdapter
import com.bitla.ts.presentation.view.activity.ticketDetails.TicketDetailsActivity
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.getCountryCodes
import com.bitla.ts.utils.common.getPhoneNumber
import com.bitla.ts.utils.common.getUserRole
import com.bitla.ts.utils.constants.BOARDED_YES_NO
import com.bitla.ts.utils.constants.CALL_OPTION_CLICKS
import com.bitla.ts.utils.constants.LUGGAGE_OPTION_CLICK
import com.bitla.ts.utils.constants.VIEW_TICKET
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bumptech.glide.Glide
import gone
import setSafeOnClickListener
import toast
import visible

class PassengerStageAdapter(
    private val context: Context,
    private var searchList: ArrayList<RespHash>?,
    private var passengerSearchList: ArrayList<PassengerDetail>?,
    private val isParentVisible: Boolean,
    private var chartType: String,
    private val currency: String,
    private val currencyFormat: String,
    private val neededCountry: String,
    private val editPassengerSheet: EditPassengerSheet,
    private val privilegeResponse: PrivilegeResponseModel?,
    private val actionStageDetails: (stageName: String, landmark: String) -> Unit,
    private val closeChartAction: (cityId: String, reservationId: String) -> Unit,
    private val boardedSwitchActionClicked: (dialogue: Boolean, boardedSwitch: SwitchCompat, statusText: TextView, seatNumber: String, passengerName: String, pnrNumber: String, remarks: String) -> Unit,
    private val boardedSwitchMultiSeatActionClicked: ((dialogue: Boolean, seatNumber: List<String>, pnrNumber: String, remarks: String) -> Unit)? = null,
    private val actionModifyPass: (seatNumber: String, pnrNumber: String) -> Unit,
    private val actionLuggagePass: (seatNumber: String, passengerName: String, pnrNumber: String, passengerAge: String, passengerStatus: String, passengerSex: String) -> Unit,
    private val actionLuggageMultiSeatPass: ((passengerName: String, passengerAge: String, passengerSex: String, seatNumbers: List<String>, pnrNumber: String, pnrStatus: String) -> Unit)? = null,
    private val actionLuggageOptionPass: (pnrNumber: String) -> Unit,
    private val onCallClickListener: ((phoneNumber: String) -> Unit)? = null,
    private val onIvrCallClick: (position: Int) -> Unit,
    private val onPnrClick: (pnrNumber: String) -> Unit,


    ) :

    RecyclerView.Adapter<PassengerStageAdapter.ViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    private var loginModelPref: LoginModel = LoginModel()
    private var countryList = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PassengerStageAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        loginModelPref = PreferenceUtils.getLogin()
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (isParentVisible) {
            searchList!!.size
        } else {
            passengerSearchList!!.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (isParentVisible) {
            holder.oldLayout.gone()
            holder.newLayout.visible()
            val searchModel: RespHash = searchList!!.get(position)
            if (isParentVisible) {
                holder.header.visible()
                holder.tvDateTime.text = toUnderline(searchModel.name)
                val stationPassengerList = arrayListOf<PassengerDetailX?>()
                if (searchModel.pnr_group != null) {
                    searchModel.pnr_group?.forEach {
                        if (it?.passenger_details != null) {
                            stationPassengerList.addAll(it.passenger_details)
                        }
                    }
                }
                var count = 0
                for (i in 0 until stationPassengerList.size) {
                    if (stationPassengerList[i]?.status == 2) {
                        count++
                    }
                }
//                holder.boardedPassengerCount.text =
//                    "${context.getString(R.string.count)} : ${stationPassengerList.size}"

                holder.boardedPassengerCount.text =
                    context.getString(R.string.boarded_passenger_count, count, stationPassengerList.size)

                if (searchModel.pickupClosed) {
                    //holder.closeChat.visible()
                    holder.closeChat.isClickable = false
                    holder.closeChat.backgroundTintList = ColorStateList.valueOf(
                        context.resources.getColor(
                            R.color.button_default_color
                        )
                    )
                    holder.closeChat.text = context.getString(R.string.chart_closed)
                } else {
                    if (privilegeResponse != null) {
                        privilegeResponse?.let {
                            if (privilegeResponse.allowToClosePickupByCity) {
                                if (searchModel.pickupClosed) {
                                    holder.closeChat.gone()
                                } else {
                                    /*if (chartType == "1") {
                                        holder.closeChat.visible()
                                    } else {
                                        holder.closeChat.gone()
                                    }*/
                                    holder.closeChat.backgroundTintList = ColorStateList.valueOf(
                                        context.resources.getColor(
                                            R.color.colorPrimary
                                        )
                                    )
                                    holder.closeChat.isClickable = true
                                    holder.closeChat.setSafeOnClickListener {
                                        holder.closeChat.tag =
                                            context.getString(R.string.close_chart)
                                        closeChartAction.invoke(
                                            searchModel.cityId.toString(),
                                            searchModel.id.toString()
                                        )
                                    }
                                }

                            } else {
                                holder.closeChat.gone()
                            }
                        }
                    } else {
                        context.toast(context.getString(R.string.server_error))
                    }
                }
            } else {
                holder.header.gone()
            }
            val layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            val childSortSublistAdapter = PassengerSortSublistAdapter(
                context,
                searchModel.pnr_group ?: arrayListOf(),
                privilegeResponse,
                neededCountry,
                editPassengerSheet,
                boardedSwitchActionClicked = { dialogue: Boolean, boardedSwitch: SwitchCompat, statusText: TextView, seatNumber: String, passengerName: String, pnrNumber: String, remarks: String ->
                    boardedSwitchActionClicked.invoke(
                        dialogue,
                        boardedSwitch,
                        statusText,
                        seatNumber,
                        passengerName,
                        pnrNumber,
                        remarks
                    )
                },
                boardedSwitchMultiSeatActionClicked = { dialogue: Boolean, seatNumber: List<String>, pnrNumber: String, remarks: String ->
                    boardedSwitchMultiSeatActionClicked?.invoke(
                        dialogue,
                        seatNumber,
                        pnrNumber,
                        remarks
                    )
                },
                actionModify = { seatNumber, pnrNumber ->
                    actionModifyPass.invoke(seatNumber, pnrNumber)
                },
                actionLuggageClick = { seatNumber: String, passengerName: String, pnrNumber: String, passengerAge: String, passengerStatus: String, passengerSex: String ->
                    actionLuggagePass.invoke(
                        seatNumber,
                        passengerName,
                        pnrNumber,
                        passengerAge,
                        passengerStatus,
                        passengerSex
                    )

                },
                actionLuggageMultiSeat = { passengerName: String, passengerAge: String, passengerSex: String, seatNumbers: List<String>, pnrNumber: String, pnrStatus: String ->
                    actionLuggageMultiSeatPass?.invoke(
                        passengerName,
                        passengerAge,
                        passengerSex,
                        seatNumbers,
                        pnrNumber,
                        pnrStatus
                    )
                },
                onCallClickListener = onCallClickListener,
                actionLuggageOptionClick = { pnrNumber: String ->
                    actionLuggageOptionPass.invoke(pnrNumber)
                },
            ) {
                onPnrClick.invoke(it)
            }
            holder.rvNestedItems.layoutManager = layoutManager
            holder.rvNestedItems.adapter = childSortSublistAdapter
            holder.rvNestedItems.setRecycledViewPool(viewPool)


            holder.expandCollapseButton.setOnClickListener {
                if (holder.rvNestedItems.isVisible) {
                    holder.rvNestedItems.gone()
                    holder.expandCollapseButton.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context, R.drawable.ic_arrow_down
                        )
                    )
                } else {
                    holder.rvNestedItems.visible()
                    holder.expandCollapseButton.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context, R.drawable.ic_arrow_up
                        )
                    )
                }
            }

            holder.rootView.setOnClickListener {
                if (holder.rvNestedItems.isVisible) {
                    holder.rvNestedItems.gone()
                    holder.expandCollapseButton.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context, R.drawable.ic_arrow_down
                        )
                    )
                } else {
                    holder.rvNestedItems.visible()
                    holder.expandCollapseButton.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context, R.drawable.ic_arrow_up
                        )
                    )
                }
            }

            holder.ivCloseChart.setOnClickListener {
                holder.ivCloseChart.tag =
                    context.getString(R.string.close_chart)
                closeChartAction.invoke(
                    searchModel.cityId.toString(),
                    searchModel.id.toString()
                )
            }


            holder.tvDateTime.setOnClickListener {
                holder.tvDateTime.tag = context.getString(R.string.stage_info)
                if (searchModel.pnr_group?.isNotEmpty() == true && searchModel.pnr_group?.get(0)?.passenger_details?.isNotEmpty() == true)
                    actionStageDetails.invoke(
                        searchModel.name,
                        searchModel.pnr_group?.get(0)?.passenger_details?.get(0)?.landmark ?: ""
                    )

            }

            if (privilegeResponse?.allowToClosePickupByCity == true) {
                holder.ivCloseChart.visible()
            } else {
                holder.ivCloseChart.gone()
            }
            if(neededCountry.equals("India", true)) {
                holder.rvNestedItems.visible()
                holder.ivCall.gone()
                holder.expandCollapseButton.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context, R.drawable.ic_arrow_up
                    )
                )
            }

        } else {
            holder.oldLayout.visible()
            holder.newLayout.gone()

            val passengerSearchModel: PassengerDetail = passengerSearchList!!.get(position)

            //Old view

            holder.detailedpart.gone()
            holder.imageexpandless.gone()

            if (passengerSearchModel?.isPartiallyBooked == true)
                holder.tvPartiallyBooked.visible()
            else
                holder.tvPartiallyBooked.gone()

            if (!passengerSearchModel.droppingPoint.isNullOrEmpty()) {
                holder.dropOffPoint.visible()
                holder.dropOffPoint.setText(("${context.getString(R.string.drop_off_at)}: ${passengerSearchModel.droppingPoint}"))
            } else {
                holder.dropOffPoint.gone()
            }
            holder.passengerName.text = passengerSearchModel.passengerName
            if (passengerSearchModel.isPhoneBooking) {
                holder.pnrNumber.setText("${context.getString(R.string.pnr)}: ${passengerSearchModel.pnrNumber}(P)")
            } else {
                holder.pnrNumber.setText("${context.getString(R.string.pnr)}: ${passengerSearchModel.pnrNumber}")
            }

            val bookedBy = passengerSearchModel.bookedBy.split(",")
            holder.bookedBy.setText(
                "${context.getString(R.string.booked_by)}: ${bookedBy[0]} ${
                    if (passengerSearchModel.isPayAtBus != null && passengerSearchModel.isPayAtBus!!) context.getString(
                        R.string.payAtBus
                    ) else ""
                }"
            )

            if (passengerSearchModel.isPayAtBus != null && passengerSearchModel.isPayAtBus!! && passengerSearchModel.onBehalfOfBookedByUserOrAgent != null) {
                holder.tvCollectedBy.visible()
                holder.tvCollectedBy.text = "${context.getString(R.string.collected_by)} : ${
                    passengerSearchModel.onBehalfOfBookedByUserOrAgent?.substringBefore(",")
                }"
            } else
                holder.tvCollectedBy.gone()

            holder.collection.text = (passengerSearchModel.ticketFare)?.convert(currencyFormat)
            holder.seatnumber.text = passengerSearchModel.seatNumber

            if (passengerSearchModel.gstAmount > 0.0) {
                holder.gstText.visible()
            } else {
                holder.gstText.gone()

            }
            if (passengerSearchModel.remarks.isNotEmpty()) {
                holder.remaks.visible()
                holder.remaks.text = passengerSearchModel.remarks
            } else {
                holder.remaks.gone()
            }

            if (privilegeResponse?.allowToShowFrequentTravellerTag == true) {
                if (passengerSearchModel.tripCounts == null || passengerSearchModel.tripCounts == 0) {
                    holder.totalTrip.gone()
                    holder.friquentTraveller.gone()

                } else {
//                if (passengerSearchModel.totalTrip!! >= 5) {
//                    holder.friquentTraveller.visible()
//                } else {
//                    holder.friquentTraveller.gone()
//                }

                    holder.friquentTraveller.visible()
                    holder.totalTrip.visible()
                    holder.totalTrip.text =
                        "${context.getString(R.string.total_trips)}${passengerSearchModel.tripCounts}"
                }
            } else {
                holder.totalTrip.gone()
                holder.friquentTraveller.gone()
            }

            if (!passengerSearchModel.bookingSrcImage.isNullOrEmpty()) {
                Glide.with(context).load(passengerSearchModel.bookingSrcImage)
                    .into(holder.busBookingIcon)
            } else {
                holder.busBookingIcon.gone()
            }
            if (passengerSearchModel.isPhoneBooking) {
                holder.phoneBookingIcon.visible()
            } else {
                holder.phoneBookingIcon.gone()
            }
            when (passengerSearchModel.status) {
                0 -> {
                    holder.yetToBoard.setText(context.getString(R.string.yet_to_board))
                    holder.yetToBoard.setTextColor(context.resources.getColor(R.color.colorRed2))
                }

                1 -> {
                    holder.yetToBoard.setText(context.getString(R.string.unboarded_status))
                    holder.yetToBoard.setTextColor(context.resources.getColor(R.color.colorRed2))
                }

                2 -> {
                    holder.yetToBoard.setText(context.getString(R.string.boarded_status))
                    holder.yetToBoard.setTextColor(context.resources.getColor(R.color.colorPrimary))
                }

                3 -> {
                    holder.yetToBoard.setText(context.getString(R.string.no_show))
                    holder.yetToBoard.setTextColor(context.resources.getColor(R.color.black))
                }

                4 -> {
                    holder.yetToBoard.setText(context.getString(R.string.missing_status))
                    holder.yetToBoard.setTextColor(context.resources.getColor(R.color.color_03_review_02_moderate))
                }

                5 -> {
                    holder.yetToBoard.setText(context.getString(R.string.dropped_off))
                    holder.yetToBoard.setTextColor(context.resources.getColor(R.color.colorPrimary))
                }
            }

            holder.checkBoarded.isChecked = passengerSearchModel.status == 2
            holder.checkBoarded.isClickable = true
            holder.callPassenger.isClickable = true
            holder.imageexpandmore.isClickable = true
            holder.checkBoarded.setOnClickListener {
                boardedSwitchActionClicked.invoke(
                    false,
                    holder.checkBoarded,
                    holder.yetToBoard,
                    passengerSearchModel.seatNumber,
                    passengerSearchModel.passengerName,
                    passengerSearchModel.pnrNumber,
                    ""
                )
                firebaseLogEvent(
                    context,
                    BOARDED_YES_NO,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    BOARDED_YES_NO,
                    "Boarded [Y/N] Clicks - ViewReservation"
                )
            }
            holder.callPassenger.setOnClickListener {
                if (passengerSearchModel.phoneNumber.contains("*") || passengerSearchModel.phoneNumber.isNullOrEmpty()) {
                    context.toast(context.getString(R.string.phone_number_is_not_visible))

                } else {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CALL_PHONE
                        ) == PackageManager.PERMISSION_DENIED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.CALL_PHONE),
                            200
                        )
                    } else {
                        if (privilegeResponse != null) {
                            if (privilegeResponse?.country != null) {
                                val countryName = privilegeResponse.country

                                if (getCountryCodes() != null && getCountryCodes().isNotEmpty())
                                    countryList = getCountryCodes()

                                val telNo =
                                    getPhoneNumber(
                                        passPhone = passengerSearchModel.phoneNumber,
                                        countryName
                                    )
                                if (countryList.isNotEmpty()) {
                                    val finalTelNo = "+${countryList[0]}$telNo"
                                    val intent =
                                        Intent(Intent.ACTION_CALL, Uri.parse("tel:${finalTelNo}"))
                                    context.startActivity(intent)
                                }
                            }
                        }


                    }
                }

                firebaseLogEvent(
                    context,
                    CALL_OPTION_CLICKS,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    CALL_OPTION_CLICKS,
                    "Call Option Clicks - ViewReservation"
                )
            }
            holder.imageexpandmore.setOnClickListener {
                holder.detailedpart.visibility = View.VISIBLE
                holder.imageexpandless.visibility = View.VISIBLE
                holder.imageexpandmore.visibility = View.GONE

                holder.layoutstatus.setOnClickListener {

                    boardedSwitchActionClicked.invoke(
                        true,
                        holder.checkBoarded,
                        holder.yetToBoard,
                        passengerSearchModel.seatNumber,
                        passengerSearchModel.passengerName,
                        passengerSearchModel.pnrNumber,
                        ""
                    )
                    holder.checkBoarded.tag = "Status"

                }
                holder.layoutModify.setOnClickListener {
                    actionModifyPass.invoke(
                        passengerSearchModel.seatNumber,
                        passengerSearchModel.pnrNumber
                    )
                }

                holder.layoutluggage.setOnClickListener {
                    actionLuggagePass.invoke(
                        passengerSearchModel.seatNumber,
                        passengerSearchModel.passengerName,
                        passengerSearchModel.pnrNumber,
                        passengerSearchModel.passengerAge.toString(),
                        passengerSearchModel.status.toString(),
                        passengerSearchModel.sex
                    )


                    firebaseLogEvent(
                        context,
                        LUGGAGE_OPTION_CLICK,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        LUGGAGE_OPTION_CLICK,
                        "Luggage Option Clicks - ViewReservation"
                    )

                }
                holder.layoutviewTicket.setOnClickListener {
                    firebaseLogEvent(
                        context,
                        VIEW_TICKET,
                        loginModelPref.userName,
                        loginModelPref.travels_name,
                        loginModelPref.role,
                        VIEW_TICKET,
                        "View ticket"
                    )
                    val intent = Intent(context, TicketDetailsActivity::class.java)
                    intent.putExtra(
                        context.getString(R.string.TICKET_NUMBER),
                        passengerSearchModel.pnrNumber
                    )
                    intent.putExtra("returnToDashboard", false)

                    context.startActivity(intent)
                }

            }
            holder.imageexpandless.setOnClickListener {
                holder.imageexpandless.visibility = View.GONE
                holder.imageexpandmore.visibility = View.VISIBLE

                holder.detailedpart.visibility = View.GONE
            }
            if (privilegeResponse != null) {
                privilegeResponse?.let {
                    var isAgentLogin: Boolean = false
                    var loginModelPref: LoginModel = LoginModel()
                    loginModelPref = PreferenceUtils.getLogin()

                    val role = getUserRole(loginModelPref, isAgentLogin = isAgentLogin, context)

                    if (role == context.getString(R.string.role_field_officer)) {


                        if (privilegeResponse.boLicenses?.updatePassengerTravelStatus == true) {
                            holder.layoutstatus.visible()
                            holder.boardedLoayout.visible()
                        } else {
                            holder.layoutstatus.gone()
                            holder.smsView.gone()
                            holder.boardedLoayout.gone()
                        }
                    } else {
                        if (privilegeResponse.updatePassengerTravelStatus) {
                            holder.layoutstatus.visible()
                            holder.boardedLoayout.visible()
                            if (privilegeResponse.availableAppModes?.allowCall == true) {
                                holder.callPassenger.visible()
                                holder.smsView.visible()
//
                            } else {
                                holder.callPassenger.gone()
                                holder.smsView.gone()
                            }
                        } else {
                            holder.layoutstatus.gone()
                            holder.boardedLoayout.gone()
                            holder.smsView.gone()
                            if (privilegeResponse.availableAppModes?.allowCall == true) {
                                holder.callPassenger.visible()

                            } else {
                                holder.callPassenger.gone()
                            }
                        }
                    }

                    if (privilegeResponse.availableAppModes?.allowModify == true) {
                        holder.layoutModify.visible()
                    } else {
                        holder.layoutModify.gone()
                    }

                    if (privilegeResponse.availableAppModes?.allowLuggage == true) {
                        holder.layoutluggage.visible()

                    } else {
                        holder.layoutluggage.gone()
                    }
                    if (privilegeResponse.currency.isNotEmpty()) {
                        holder.collection.text = "${privilegeResponse.currency} ${
                            (passengerSearchModel.ticketFare)?.convert(currencyFormat)
                        }"
                    } else {
                        holder.collection.text =
                            (passengerSearchModel.ticketFare)?.convert(currencyFormat)

                    }


                    holder.collection.setText(
                        "$currency ${
                            (passengerSearchModel.ticketFare)?.convert(
                                currencyFormat
                            )
                        }"
                    )


                }
            } else {
                context.toast(context.getString(R.string.server_error))
            }
        }
        holder.ivCall.setOnClickListener {
            onIvrCallClick.invoke(position)
        }
    }

    class ViewHolder(binding: PassengerStageAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val oldLayout = binding.oldView
        val newLayout = binding.newView


        val tvDateTime = binding.tvDateTime
        val header = binding.constraintLayout5
        val rvNestedItems = binding.rvNestedItems
        val closeChat = binding.closeChatButton
        val boardedPassengerCount = binding.boardedPassenger

        // OLD VIEW
        val detailextend = binding.extendDetail
        val detailedpart = binding.extendedPart
        val imageexpandmore = binding.imgExpandMore
        val imageexpandless = binding.imgExpandLess
        val layoutstatus = binding.layoutStatus
        val layoutModify = binding.layoutModify
        val layoutluggage = binding.layoutLuggage
        val layoutviewTicket = binding.layoutViewTicket
        val checkBoarded = binding.boardedSwitch
        val passengerName = binding.passengerName
        val bookedBy = binding.bookedBy
        val tvCollectedBy = binding.tvCollectedBy
        val collection = binding.tvCollection
        val seatnumber = binding.seatNumber
        val yetToBoard = binding.yetToBoard
        val callPassenger = binding.callPassenger
        val smsView = binding.smsView
        val boardedLoayout = binding.boardedLayout
        val pnrNumber = binding.pnrNumber
        val gstText = binding.incGst
        val remaks = binding.remarksField
        val friquentTraveller = binding.frequentTraveller
        val totalTrip = binding.totalTrip
        val phoneBookingIcon = binding.phoneBookingIcon
        val busBookingIcon = binding.busBookingIcon
        val dropOffPoint = binding.dropOffPoint
        val payAtBus = binding.payAtBusTag
        val tvPartiallyBooked = binding.tvPartiallyBooked

        //New View
        val expandCollapseButton = binding.expandCollapseButton
        val ivCloseChart = binding.ivCloseChart
        val rootView = binding.constraintLayout5
        val ivCall = binding.ivCall

    }

    fun notifyAdapter(itemList: ArrayList<RespHash>) {
        searchList = itemList
        notifyDataSetChanged()
    }

    fun oldNotifyAdapter(itemList: ArrayList<PassengerDetail>) {
        passengerSearchList = itemList
        notifyDataSetChanged()
    }

    private fun toUnderline(text: String): Spannable {
        val stringToSpan: Spannable = SpannableString(text)
        stringToSpan.setSpan(UnderlineSpan(), 0, text.length, 0)

        return stringToSpan
    }
}