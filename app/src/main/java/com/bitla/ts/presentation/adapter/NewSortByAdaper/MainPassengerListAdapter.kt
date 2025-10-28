package com.bitla.ts.presentation.adapter.NewSortByAdaper

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.data.listener.OnclickitemMultiView
import com.bitla.ts.databinding.ChildSortSublistAdapterBinding
import com.bitla.ts.databinding.MainPassegerListChildBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.view_reservation.PassengerDetail
import com.bitla.ts.domain.pojo.view_reservation.PassengerDetailX
import com.bitla.ts.presentation.view.ticket_details_compose.TicketDetailsActivityCompose
import com.bitla.ts.presentation.view.activity.ServiceDetailsActivity
import com.bitla.ts.presentation.view.activity.SmsNotificationActivity
import com.bitla.ts.presentation.view.activity.reservationOption.announcement.AnnouncementActivity
import com.bitla.ts.presentation.view.activity.reservationOption.extendedFare.UpdateRateCardActivity
import com.bitla.ts.presentation.view.activity.ticketDetails.TicketDetailsActivity
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.*
import com.bumptech.glide.Glide
import gone
import org.imaginativeworld.whynotimagecarousel.utils.dpToPx
import timber.log.Timber
import toast
import visible


class MainPassengerListAdapter(
    private val context: Context,
    private val passengerList: ArrayList<PassengerDetailX?>,
    private val privilegeResponseModel: PrivilegeResponseModel?,
    private val boardedSwitchAction:((dialogue: Boolean,boardedSwitch: SwitchCompat, statusText: TextView, seatNumber:String, passengerName:String, remarks: String) -> Unit),
    private val neededCountry: String,
    private val actionModifyPassenger:((seatNumber:String) -> Unit),
    private val onCallClickListener: ((phoneNumber: String) -> Unit)? = null,
    private val actionluggage:((seatNumber:String, passengerName:String, passengerAge: String,passengerStatus:String,passengerSex:String) -> Unit)

) :
    RecyclerView.Adapter<MainPassengerListAdapter.ViewHolder>() {
    private var loginModelPref: LoginModel = LoginModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MainPassegerListChildBinding.inflate(LayoutInflater.from(context), parent, false)
        loginModelPref = PreferenceUtils.getLogin()
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return passengerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var statusCount = 0
        if(passengerList.size <= 1) {
            holder.dottedLine.gone()
        } else {
            holder.dottedLine.visible()
        }

        val passengerItem:PassengerDetailX?  = passengerList[position]
//
        holder.boardingStatus.gone()
        holder.dotIcon.gone()

        // check india -> country, true -> it will display
        holder.apply {
            if (neededCountry.equals("India", true)) {
                menuOption.gone()
                if (!passengerItem?.dropping_point.isNullOrEmpty()) {
                    dropOffTV.visible()
                    dropOffTV.text = context.getString(R.string.drop_off_at_colon, passengerItem?.dropping_point ?: "")
                } else {
                    dropOffTV.gone()
                }

            } else {
                menuOption.gone()
                dropOffTV.gone()
            }
        }

        when (passengerItem?.status) {
            0 -> {
                holder.boardingStatus.text = context.getString(R.string.yet_to_board)
                holder.boardingStatus.setTextColor(context.resources.getColor(R.color.colorRed2))
            }
            1 -> {
                holder.boardingStatus.text = context.getString(R.string.unboarded_status)
                holder.boardingStatus.setTextColor(context.resources.getColor(R.color.colorRed2))
            }
            2 -> {
                holder.boardingStatus.text = context.getString(R.string.boarded_status)
                holder.boardingStatus.setTextColor(context.resources.getColor(R.color.colorPrimary))
            }
            3 -> {
                holder.boardingStatus.text = context.getString(R.string.no_show)
                holder.boardingStatus.setTextColor(context.resources.getColor(R.color.black))
            }
            4 -> {
                holder.boardingStatus.text = context.getString(R.string.missing_status)
                holder.boardingStatus.setTextColor(context.resources.getColor(R.color.color_03_review_02_moderate))
            }
            5 -> {
                holder.boardingStatus.text = context.getString(R.string.dropped_off)
                holder.boardingStatus.setTextColor(context.resources.getColor(R.color.colorPrimary))
            }
        }

//        holder.checkBoarded.isChecked = searchModel.status == 2
////        if (chartClosed) {
////            holder.imageexpandmore.isClickable = false
////            holder.checkBoarded.isClickable = false
////            holder.callPassenger.isClickable = false
////            holder.parentChard.setCardBackgroundColor(context.resources.getColor(R.color.flash_white_bg))
////        } else {
//        holder.checkBoarded.isClickable = true
//        holder.callPassenger.isClickable = true
//        holder.imageexpandmore.isClickable = true
//        holder.parentChard.setCardBackgroundColor(context.resources.getColor(R.color.white))
        holder.seatNumber.text= passengerItem?.seat_number
        if(passengerItem?.total_trip != null && passengerItem.total_trip != 0) {
            val passengerTripInfo =
                "${passengerItem?.total_trip} ${context.getString(R.string.trips)}"
            holder.totalTrips.text = passengerTripInfo
        } else {
            holder.totalTrips.gone()
        }
        val passengerInfo =
            if(neededCountry.equals("India", true)) {
                "${passengerItem?.passenger_name} (${passengerItem?.sex} ${passengerItem?.passenger_age})"
            } else {
                "${passengerItem?.passenger_name} (${passengerItem?.sex}${passengerItem?.passenger_age})"
            }
        holder.passengerName.text= passengerInfo
        holder.checkBoarded.isChecked = passengerItem?.status== 2


        if(privilegeResponseModel?.updatePassengerTravelStatus == true) {
            holder.checkBoarded.visible()
        } else {
            holder.checkBoarded.gone()
        }
        holder.checkBoarded.setOnTouchListener(View.OnTouchListener { v, event -> event.actionMasked == MotionEvent.ACTION_MOVE })
        holder.checkBoarded.setOnClickListener {
            if (holder.checkBoarded.isChecked) {

                holder.checkBoarded.isChecked = passengerItem?.status == 2
                holder.checkBoarded.tag = "boarded"
                if (statusCount == 0) {
                    val statusSelected =
                        PreferenceUtils.setPreference(
                            "pickUpChartStatus",
                            "${passengerItem?.status}"
                        )
                }
                statusCount += 1
                boardedSwitchAction.invoke(
                    false,
                    holder.checkBoarded,
                    holder.boardingStatus,
                    passengerItem?.seat_number ?: "",
                    passengerItem?.passenger_name ?: "",
                    passengerItem?.remarks ?: ""
                )
//                holder.layoutstatus.tag = searchModel.pnrNumber
//                onclickitemMultiView.onClickMuliView(
//                    holder.layoutstatus,
//                    holder.yetToBoard,
//                    holder.checkBoarded,
//                    holder.detailedpart,
//                    searchModel.passengerName,
//                    searchModel.seatNumber
//
//                )

            }
            else {

                context.toast(context.getString(R.string.alreadyBoarded))
                holder.checkBoarded.isChecked = true
            }

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

        holder.callOption.setOnClickListener {
            if (passengerItem?.phone_number?.contains("*") == true || passengerItem?.phone_number.isNullOrEmpty()) {
                context.toast(context.getString(R.string.phone_number_is_not_visible))

            } else {
                if(privilegeResponseModel != null && neededCountry.equals("India", true)) {
                    if(privilegeResponseModel?.tsPrivileges?.allowToDisplayCustomerPhoneNumber == false) {
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
                            if (privilegeResponseModel != null) {

                                if (privilegeResponseModel?.country != null) {
                                    val countryName = privilegeResponseModel.country

                                    if (getCountryCodes() != null && getCountryCodes().isNotEmpty()) {
                                        val telNo =
                                            getPhoneNumber(
                                                passPhone = passengerItem?.phone_number,
                                                countryName
                                            )

                                        val finalTelNo = "+${getCountryCodes()}$telNo"
                                        val intent =
                                            Intent(Intent.ACTION_CALL, Uri.parse("tel:${finalTelNo}"))
                                        context.startActivity(intent)
                                    }

                                }
                            }


                        }
                    } else {
                        onCallClickListener?.invoke(passengerItem?.phone_number ?: "")
                    }

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
                        if (privilegeResponseModel != null) {

                            if (privilegeResponseModel?.country != null) {
                                val countryName = privilegeResponseModel.country

                                if (getCountryCodes() != null && getCountryCodes().isNotEmpty()) {
                                    val telNo =
                                        getPhoneNumber(
                                            passPhone = passengerItem?.phone_number,
                                            countryName
                                        )

                                    val finalTelNo = "+${getCountryCodes()}$telNo"
                                    val intent =
                                        Intent(Intent.ACTION_CALL, Uri.parse("tel:${finalTelNo}"))
                                    context.startActivity(intent)
                                }

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

        holder.menuOption.setOnClickListener {
            Timber.d("statusBtnCheck::3 done")

            val popup = PopupMenu(context, it)
            popup.inflate(R.menu.passenger_list_more_option)

            // sms is gone in the menu items according to figma
            val menu = popup.menu
            val sendSmsItem = menu.findItem(R.id.send_sms)

            sendSmsItem.isVisible = false

            popup.gravity = Gravity.RIGHT
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.update_passenger_status -> {
                        holder.checkBoarded.tag = "Status"
                if (statusCount == 0) {
                    val statusSelected =
                        PreferenceUtils.setPreference(
                            "pickUpChartStatus",
                            "${passengerItem?.status}"
                        )
                }
                        boardedSwitchAction.invoke(
                            true,
                            holder.checkBoarded,
                            holder.boardingStatus,
                            passengerItem?.seat_number ?: "",
                            passengerItem?.passenger_name ?: "",
                            ""
                        )



                    }

//
                    R.id.update_passenger_detail -> {
                        actionModifyPassenger.invoke(passengerItem?.seat_number ?: "")
                    }

                    R.id.luggage_detail -> {
                firebaseLogEvent(
                    context,
                    LUGGAGE_OPTION_CLICK,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    LUGGAGE_OPTION_CLICK,
                    "Luggage Option Clicks - ViewReservation"
                )
                        actionluggage.invoke(passengerItem?.seat_number ?: "", passengerItem?.passenger_name ?: "","${passengerItem?.passenger_age}", "${passengerItem?.status}", passengerItem?.sex ?: "")
                    }

//                    R.id.view_ticket -> {
//                        val intent = Intent(context, ExtendedFair::class.java)
//                        intent.putExtra("originID", searchList[position].originId.toString())
//                        intent.putExtra(
//                            "destinationID",
//                            searchList[position].destinationId.toString()
//                        )
//                        intent.putExtra("reservationID", searchList[position].reservationId)
//                        intent.putExtra("serviceNumber", searchList[position].number)
//                        context.startActivity(intent)
//
//                        firebaseLogEvent(
//                            context,
//                            EXTEND_FARE_SETTINGS,
//                            loginModelPref.userName,
//                            loginModelPref.travels_name,
//                            loginModelPref.role,
//                            EXTEND_FARE_SETTINGS,
//                            "Extend Fare Settings - PickupCharts"
//                        )
//                    }

                    R.id.view_ticket -> {

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
                            passengerList[position]?.pnr_number
                        )
                        intent.putExtra("returnToDashboard", false)

                        context.startActivity(intent)

                    }
                }
                true
            }

            popup.show()

        }
        if(passengerItem?.is_meal == true) {
            holder.imgMeal.visible()
        } else {
            holder.imgMeal.gone()
        }

        if(privilegeResponseModel != null && neededCountry.equals("India", true)) {
            privilegeResponseModel.let {
                if (it.updatePassengerTravelStatus) {
                    if (it.availableAppModes?.allowCall == true) {
                        holder.callOption.visible()
                    } else {
                        holder.callOption.gone()
                    }
                } else {
                    if (it.availableAppModes?.allowCall == true) {
                        holder.callOption.visible()

                    } else {
                        holder.callOption.gone()
                    }
                }
            }
            val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_frequent_traveller)
            drawable?.setBounds(0, 0, 16.dpToPx(context), 16.dpToPx(context))
            holder.totalTrips.setCompoundDrawables(drawable, null, null, null)
        }
    }


    class ViewHolder(binding: MainPassegerListChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val checkBoarded = binding.boardedSwitch
        val passengerName = binding.passengerName
        val seatNumber = binding.seatNumber
        val totalTrips = binding.totalTrips
        val boardingStatus = binding.passengerStatus
        val dotIcon = binding.dotIcon
        val menuOption= binding.threeDotMenu
        val parentChard = binding.parentCardLayout
        val callOption = binding.callIcon
        val imgMeal = binding.imgMeal
        val dottedLine = binding.dottedLine
        val dropOffTV = binding.dropOffTV

    }
}