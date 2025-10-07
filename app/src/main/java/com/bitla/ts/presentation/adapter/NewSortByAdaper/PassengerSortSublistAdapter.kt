package com.bitla.ts.presentation.adapter.NewSortByAdaper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.res.stringResource
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.EditPassengerSheet
import com.bitla.ts.databinding.PassengerSortSubListChildBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.view_reservation.PnrGroup
import com.bitla.ts.presentation.view.ticket_details_compose.TicketDetailsActivityCompose
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.VIEW_TICKET
import com.bitla.ts.utils.sharedPref.PREF_PICKUP_DROPOFF_CHARGES_ENABLED
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import toast
import gone
import visible
import java.util.Locale


class PassengerSortSublistAdapter(
    private val context: Context,
    private var searchList: List<PnrGroup?>,
    private val privilegeResponse: PrivilegeResponseModel?,
    private val neededCountry: String,
    private val editPassengerSheet: EditPassengerSheet,
    private val boardedSwitchActionClicked: (dialogue: Boolean, boardedSwitch: SwitchCompat, statusText: TextView, seatNumber: String, passengerName: String, pnrNumber: String, remarks: String) -> Unit,
    private val boardedSwitchMultiSeatActionClicked: ((dialogue: Boolean, seatNumber: List<String>, pnrNumber: String, remarks: String) -> Unit)? = null,
    private val actionModify: (seatNumber: String, pnrNumber: String) -> Unit,
    private val actionLuggageClick: (seatNumber: String, passengerName: String, pnrNumber: String, passengerAge: String, passengerStatus: String, passengerSex: String) -> Unit,
    private val actionLuggageMultiSeat: ((passengerName: String, passengerAge: String, passengerSex: String, seatNumbers: List<String>, pnrNumber: String, pnrStatus: String) -> Unit)? = null,
    private val actionLuggageOptionClick: (pnrNumber: String) -> Unit,
    private val onCallClickListener: ((phoneNumber: String) -> Unit)? = null,
    private val onPnrClick: (pnrNumber: String) -> Unit
) :
    RecyclerView.Adapter<PassengerSortSublistAdapter.ViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    private var loginModelPref: LoginModel = LoginModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            PassengerSortSubListChildBinding.inflate(LayoutInflater.from(context), parent, false)
        loginModelPref = PreferenceUtils.getLogin()
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel: PnrGroup? = searchList[position]
        val pnrNumber= searchModel?.pnr_number
        val pnrStatus = searchModel?.pnr_status ?: ""
        val pnrStatusValue: Int = if(pnrStatus.isNotEmpty()) {
            pnrStatus.toInt()
        } else {
            0
        }
        val seatNumbers = searchModel?.seat_number_group ?: arrayListOf()
        val groupByPnrPickupChart = privilegeResponse?.tsPrivileges?.groupByPnrPickupChart ?: false
        var statusCount = 0
        var name = ""
        var age = ""
        var gender = ""
        if(seatNumbers.size == 1) {
            searchModel?.passenger_details?.get(0).let {
                age = it?.passenger_age.toString()
                name = it?.passenger_name.toString()
                gender = it?.sex.toString()
            }
        }

        holder.detailedPart.gone()
        holder.imageExpandLessOption.gone()

        if (!searchModel?.passenger_details.isNullOrEmpty()){
           searchModel?.passenger_details?.forEach {
               if (it?.is_phone_booking == true){
                   holder.isPhoneBooking.visible()
               }else{
                   holder.isPhoneBooking.gone()
               }
           }
        }else{
            holder.isPhoneBooking.gone()
        }

        if(searchModel?.remarks?.isNotEmpty() == true) {
            holder.remarksText.text = searchModel.remarks
        } else {
            holder.remarksLayout.gone()
        }

        if(searchModel?.remarks.equals("Via-Mobility App", true)) {
            holder.remarksLayout.gone()
        }

        if(searchModel?.is_pay_at_bus == true) {
            holder.tvPayAtBusTag.visible()
        } else {
            holder.tvPayAtBusTag.gone()
        }

        val pnrInfo = richText(normalText = "${context.getString(R.string.pnr)}:", boldText = pnrNumber ?: "")
        holder.pnrNumberText.text = pnrInfo
        holder.pnrNumberText.setOnClickListener {
            if (pnrNumber != null) {
                onPnrClick.invoke(pnrNumber)
            }
        }

        holder.apply {
            if(neededCountry.equals("India", true) && groupByPnrPickupChart) {
                passengerStatusValue.visible()
                expandContractCL.visible()
                passengerIconCL.visible()
            } else {
                passengerStatusValue.gone()
                expandContractCL.gone()
                passengerIconCL.gone()
            }
        }


        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        val mainPassengerListAdapter = MainPassengerListAdapter(
            context,
            searchModel?.passenger_details ?: arrayListOf(),privilegeResponse,
            boardedSwitchAction = {dialogue: Boolean, boardedSwitch: SwitchCompat, statusText: TextView, seatNumber: String, passengerName: String, remarks: String ->
                boardedSwitchActionClicked.invoke(
                    dialogue,
                    boardedSwitch,
                    statusText,
                    seatNumber,
                    passengerName,
                    pnrNumber ?: "",
                    remarks
                )
            },
            neededCountry,
            actionModifyPassenger = {seatNumber ->
                actionModify.invoke(seatNumber, pnrNumber ?: "")
            },
            onCallClickListener = { phoneNumber: String ->
                onCallClickListener?.invoke(phoneNumber)
            },
            actionluggage = {seatNumber: String, passengerName: String, passengerAge: String, passengerStatus: String, passengerSex: String ->
                actionLuggageClick.invoke(seatNumber, passengerName, pnrNumber ?: "", passengerAge, passengerStatus, passengerSex)
            }
        )
        val currency = privilegeResponse?.currency ?: ""
        val bookedBy = searchModel?.booked_by?.lowercase(Locale.getDefault()) ?: ""

        val operatorIconResId = when {
            bookedBy.contains("redbus") -> R.drawable.ic_red_bus_test
            bookedBy.contains("easybook") -> R.drawable.easybook
            bookedBy.contains("travel oka") -> R.drawable.traveloka
            bookedBy.contains("bookonlineticket") -> R.drawable.bookticket
            else -> 0 // No operator icon
        }

        holder.bookedByOperatorIcon.apply {
            if (operatorIconResId != 0) {
                visible()
                setImageResource(operatorIconResId)
            } else {
                gone()
            }
        }

        val hasPickupAddress = !searchModel?.pickup_address.isNullOrEmpty()
        val hasDropoffAddress = !searchModel?.dropoff_address.isNullOrEmpty()

        when {
            hasPickupAddress && !hasDropoffAddress -> {
                holder.clPickupDropoffAddress.visible()
                holder.clPickupAddress.visible()
                holder.pickupAddress.text = searchModel?.pickup_address
                holder.dottedLineDivider.gone()
                holder.clDropoffAddress.gone()
            }
            hasPickupAddress && hasDropoffAddress -> {
                holder.clPickupDropoffAddress.visible()
                holder.clPickupAddress.visible()
                holder.pickupAddress.text = searchModel?.pickup_address
                holder.dottedLineDivider.visible()
                holder.clDropoffAddress.visible()
                holder.dropoffAddress.text = searchModel?.dropoff_address
            }
            !hasPickupAddress && hasDropoffAddress -> {
                holder.clPickupDropoffAddress.visible()
                holder.clPickupAddress.gone()
                holder.dottedLineDivider.gone()
                holder.clDropoffAddress.visible()
                holder.dropoffAddress.text = searchModel?.dropoff_address
            }
            else -> {
                holder.clPickupDropoffAddress.gone()
            }
        }

        if(privilegeResponse?.country.equals("Indonesia", true) && !searchModel?.passenger_details.isNullOrEmpty() && !searchModel?.passenger_details.isNullOrEmpty()) {
           if( searchModel?.passenger_details!![0]?.terminal_ref_no.isNullOrBlank()){
               holder.terminalRefId.gone()
           }else{
               holder.terminalRefId.visible()
               holder.terminalRefId.text= "${ context.getString(R.string.terminal_ticket_id) } - ${searchModel?.passenger_details!![0]?.terminal_ref_no?:""}"
           }
        } else {
            holder.terminalRefId.gone()
        }

        if(privilegeResponse?.country.equals("Indonesia", true) && privilegeResponse?.tsPrivileges?.updateLuggageDetailsPostConfirmation == true) {
            holder.btnLuggage.visible()
        } else {
            holder.btnLuggage.gone()
        }

        holder.btnLuggage.setOnClickListener {
            actionLuggageOptionClick.invoke(searchModel?.pnr_number ?: "")
        }

        if(!searchModel?.pnr_status.isNullOrEmpty()) {
            holder.apply {
                when (searchModel?.pnr_status) {
                    "0" -> {
                        passengerStatusValue.text = context.getString(R.string.yet_to_board)
                        passengerStatusValue.setTextColor(ContextCompat.getColor(context, R.color.colorRed2))
                    }
                    "1" -> {
                        passengerStatusValue.text = context.getString(R.string.unboarded_status)
                        passengerStatusValue.setTextColor(ContextCompat.getColor(context, R.color.colorRed2))

                    }
                    "2" -> {
                        passengerStatusValue.text = context.getString(R.string.boarded_status)
                        passengerStatusValue.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    }
                    "3" -> {
                        passengerStatusValue.text = context.getString(R.string.no_show)
                        passengerStatusValue.setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                    "4" -> {
                        passengerStatusValue.text = context.getString(R.string.missing_status)
                        passengerStatusValue.setTextColor(ContextCompat.getColor(context, R.color.color_03_review_02_moderate))
                    }
                    "5" -> {
                        passengerStatusValue.text = context.getString(R.string.dropped_off)
                        passengerStatusValue.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    }
                    "9" -> {
                        passengerStatusValue.text = context.getString(R.string.check_in)
                        passengerStatusValue.setTextColor(ContextCompat.getColor(context, R.color.colorRed2))
                    }
                }
            }
        }

        when {
            operatorIconResId!=0 -> {
                if(!privilegeResponse?.country.equals("Indonesia", true) && privilegeResponse?.isAgentLogin == true) {
                    holder.bookedByInfo.text = ": $currency ${searchModel?.total_net_fare ?: 0}"
                } else if(privilegeResponse?.country.equals("India", true) && groupByPnrPickupChart) {
                    holder.bookedByInfo.text = ": $currency ${searchModel?.total_net_fare ?: 0}"
                } else {
                    holder.bookedByInfo.text = ": $currency ${searchModel?.total_ticket_fare ?: 0}"
                }
            }
            else -> {
                if(privilegeResponse?.country.equals("Indonesia", true) && privilegeResponse?.isAgentLogin == true) {
                    holder.bookedByInfo.text =
                        "${bookedBy.substringBefore(",")}: $currency ${searchModel?.total_net_fare ?: 0}"
                } else if(privilegeResponse?.country.equals("India", true) && groupByPnrPickupChart) {
                    holder.apply {
                        val bookedByName = bookedBy.substringBefore(",")
                        val fareText = "$currency${searchModel?.total_net_fare ?: 0}"
                        val label = "${context.getString(R.string.booked_by)}: "
                        val infoText = "$bookedByName : \u202F$fareText"

                        val spannable = SpannableStringBuilder()
                            .append(label)
                            .append(infoText)

                        spannable.setSpan(
                            ForegroundColorSpan(ContextCompat.getColor(context, R.color.light_gray)),
                            0,
                            label.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        val labelWidth = bookedByInfo.paint.measureText(label).toInt()
                        spannable.setSpan(
                            LeadingMarginSpan.Standard(0, labelWidth),
                            label.length,
                            spannable.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )


                        bookedByInfo.text = spannable
                    }
                } else {
                    holder.bookedByInfo.text =
                        "${bookedBy.substringBefore(",")}: $currency${searchModel?.total_ticket_fare ?: 0}"
                }
            }
        }


        if(searchModel?.is_meal == true) {
            holder.imgMeal.visible()
        } else {
            holder.imgMeal.gone()
        }

//        layoutManager.initialPrefetchItemCount = searchModel.passengerDetails.size
        holder.rvList.layoutManager = layoutManager
        holder.rvList.adapter = mainPassengerListAdapter
        holder.rvList.setRecycledViewPool(viewPool)

        holder.apply {
            imageExpandMoreOptions.setOnClickListener {
                detailedPart.visible()
                imageExpandLessOption.visible()
                imageExpandMoreOptions.gone()
                if (statusCount == 0) {
                    PreferenceUtils.setPreference(
                        "pickUpChartStatus",
                        pnrStatus
                    )
                }
                statusCount += 1
            }

            imageExpandLessOption.setOnClickListener {
                detailedPart.gone()
                imageExpandLessOption.gone()
                imageExpandMoreOptions.visible()
            }

            modifyPassengerDetail.setOnClickListener {
                val pnrNumberValue: Any = pnrNumber ?: ""
                editPassengerSheet.showEditPassengersSheet(pnrNumberValue)
            }

            if(privilegeResponse != null) {
                privilegeResponse.let {
                    var isAgentLogin: Boolean = false
                    var loginModelPref: LoginModel = LoginModel()
                    var checkInToBoard: Boolean  = false
                    var isAllowOnlyOnce: Boolean = false
                    var updatePassengerTravelStatus: Boolean = false
                    loginModelPref = PreferenceUtils.getLogin()

                    val role = getUserRole(loginModelPref, isAgentLogin = isAgentLogin, context)

                    if (role == context.getString(R.string.role_field_officer)) {
                        isAllowOnlyOnce =
                            privilegeResponse?.boLicenses?.allowUserToBoardingStatusOnlyOnce?:false
                        checkInToBoard  = privilegeResponse?.boLicenses?.allowUserToChangeCheckInStatusToBoardedOnly?: false
                        updatePassengerTravelStatus = privilegeResponse?.boLicenses?.updatePassengerTravelStatus ?: false
                    } else {
                        isAllowOnlyOnce =
                            privilegeResponse?.availableAppModes?.allow_user_to_change_the_the_boarding_status_only_once?: false
                        checkInToBoard  = privilegeResponse?.availableAppModes?.allow_user_to_change_the_check_in_status_to_boarded_status_only?: false
                        updatePassengerTravelStatus = privilegeResponse?.updatePassengerTravelStatus ?: false
                    }
                    if (updatePassengerTravelStatus) {
                        if (it.availableAppModes?.allowStatus == true) {

                            if (isAllowOnlyOnce) {
                                if (pnrStatusValue == 0 || pnrStatusValue == 9) {
                                    if (pnrStatusValue == 9 && checkInToBoard) {
                                        layoutStatus.gone()
                                    } else {
                                        layoutStatus.visible()
                                    }
                                } else {
                                    layoutStatus.gone()
                                }
                            } else {
                                if (pnrStatusValue == 9 && checkInToBoard) {
                                    layoutStatus.gone()
                                } else {
                                    layoutStatus.visible()
                                }
                            }
                        } else {
                            layoutStatus.gone()
                        }
                    } else {
                        layoutStatus.gone()
                    }

                    if (privilegeResponse.availableAppModes?.allowModify == true) {
                        modifyPassengerDetail.visible()
                    } else {
                        modifyPassengerDetail.gone()
                    }
                    if (privilegeResponse.availableAppModes?.allowLuggage == true) {
                        layoutLuggage.visible()

                    } else {
                        layoutLuggage.gone()
                    }
                }
            } else {
                context.toast(context.getString(R.string.server_error))
            }
        }

        holder.layoutLuggage.setOnClickListener {
            actionLuggageMultiSeat?.invoke(
                name,
                age,
                gender,
                seatNumbers,
                pnrNumber ?: "",
                pnrStatus ?: ""
            )
        }

        holder.layoutStatus.setOnClickListener {
            boardedSwitchMultiSeatActionClicked?.invoke(
                true,
                seatNumbers,
                pnrNumber ?: "",
                ""
            )
        }
        viewTicket(holder, pnrNumber)

    }

    private fun viewTicket(holder: ViewHolder, pnrNumber: String?) {
        holder.layoutViewTicket.setOnClickListener {
            if (pnrNumber != null) {
                firebaseLogEvent(
                    context,
                    VIEW_TICKET,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    VIEW_TICKET,
                    "View ticket"
                )

                val intent = Intent(context, TicketDetailsActivityCompose::class.java)

                intent.putExtra(
                    context.getString(R.string.TICKET_NUMBER),
                    pnrNumber
                )
                intent.putExtra("returnToDashboard", false)

                context.startActivity(intent)
            }
        }
    }

    class ViewHolder(binding: PassengerSortSubListChildBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val rvList = binding.pnrListRv
        val pnrNumberText = binding.pnrNumber
        val remarksText = binding.remarksText
        val remarksLayout = binding.remarksLayout
        val isPhoneBooking = binding.isPhoneBookImg
        val imgMeal = binding.imgMeal
        val tvPayAtBusTag = binding.tvPayAtBusTag
        val bookedByOperatorIcon = binding.bookedByOperatorIcon
//        val bookedByHeader= binding.bookedByHeader
        val bookedByInfo= binding.bookedByInfo
        val terminalRefId= binding.terminalRefId
        val clPickupDropoffAddress = binding.clPickupDropoffAddress
        val clPickupAddress = binding.clPickupAddress
        val dottedLineDivider = binding.dottedLineDivider
        val clDropoffAddress = binding.clDropoffAddress
        var pickupAddress = binding.tvPickupAddress
        var dropoffAddress = binding.tvDropoffAddress
        val btnLuggage = binding.btnLuggage
        val passengerStatusValue = binding.passengerStatusValue
        val layoutViewTicket = binding.layoutViewTicket
        val detailedPart = binding.extendedPart
        val imageExpandMoreOptions = binding.imgExpandMoreOptions
        val expandContractCL = binding.constraintLayout13
        val imageExpandLessOption = binding.imgExpandLessOptions
        val modifyPassengerDetail = binding.layoutModify
        val layoutLuggage = binding.layoutLuggage
        val layoutStatus = binding.layoutStatus
        val passengerIconCL = binding.passengerIconCL
    }
}