package com.bitla.ts.presentation.adapter.SortByAdaper

import android.Manifest
import android.annotation.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.net.*
import android.view.*
import androidx.core.app.*
import androidx.core.content.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.view_reservation.*
import com.bitla.ts.presentation.view.ticket_details_compose.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.*
import com.bumptech.glide.*
import gone
import timber.log.*
import toast
import visible


class ChildSortSublistAdapter(
    private val context: Context,
    private val directRoute: Boolean,
    private var searchList: List<PassengerDetail>,
    private var branchName: String,
    private val onItemClickListener: OnItemClickListener,
    private val onItemPassData: OnItemPassData,
    private val chartClosed: Boolean,
    private val onclickitemMultiView: OnclickitemMultiView,
    private val currency: String,
    private val currencyFormat: String,
    private val privilegeResponseModel: PrivilegeResponseModel?,
    private val loginModelPref: LoginModel,
    private val onPnrListener: OnPnrListener,
    private var chartType: String,
    private val onCallClickListener: (phoneNumber: String) -> Unit
) :
    RecyclerView.Adapter<ChildSortSublistAdapter.ViewHolder>() {
    private var count = 0
    private var countryList = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildSortSublistAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var statusCount = 0

        holder.sideLine.gone()
        holder.detailedpart.gone()
        holder.imageexpandless.gone()
        holder.imageexpandmore.visible()
        val searchModel: PassengerDetail = searchList[position]

        if (position == 0) {
            when (chartType) {
                "1" -> holder.tvRoute.text = searchModel.boardingCity
                "5" -> holder.tvRoute.text = searchModel.droppingCity
                "6" -> {
                    if(!branchName.isNullOrEmpty()) {
                        holder.tvRoute.text = branchName
                    } else {
                        holder.tvRoute.gone()
                        holder.tvRoute.text = ""
                    }
                }
                else -> {
                    holder.tvRoute.gone()
                    holder.tvRoute.text = ""
                }
            }
        } else {
            holder.tvRoute.gone()
        }

        if (searchModel.isPartiallyBooked)
            holder.tvPartiallyBooked.visible()
        else
            holder.tvPartiallyBooked.gone()

        if (!searchModel.droppingPoint.isNullOrEmpty()) {
            holder.dropOffPoint.visible()
            holder.dropOffPoint.text = ("${context.getString(R.string.drop_off_at)}: ${searchModel.droppingPoint}")
        } else {
            holder.dropOffPoint.gone()
        }
        holder.passengerName.text = "${searchModel.passengerName} (${searchModel.sex} ${searchModel.passengerAge})"
        if (searchModel.isPhoneBooking) {
            holder.pnrNumber.setText("${context.getString(R.string.pnr)}: ${searchModel.pnrNumber}(P)")
        } else {
            holder.pnrNumber.setText("${context.getString(R.string.pnr)}: ${searchModel.pnrNumber}")
        }

        val bookedBy = searchModel.bookedBy.split(",")
        holder.bookedBy.setText(
            "${context.getString(R.string.booked_by)}: ${bookedBy[0]} ${
                if (searchModel.isPayAtBus != null && searchModel.isPayAtBus!!) context.getString(
                    R.string.payAtBus
                ) else ""
            }"
        )

        if (searchModel.isPayAtBus != null && searchModel.isPayAtBus!! && searchModel.onBehalfOfBookedByUserOrAgent != null) {
            holder.tvCollectedBy.visible()
            holder.tvCollectedBy.text = "${context.getString(R.string.collected_by)} : ${
                searchModel.onBehalfOfBookedByUserOrAgent?.substringBefore(",")
            }"
        } else
            holder.tvCollectedBy.gone()

        if (searchModel.ticketFare != null) {
            holder.collection.text =
                ("$currency ${(searchModel.ticketFare)?.convert(currencyFormat)}")
        } else {
            holder.collection.gone()
            holder.gstText.gone()
        }
        //holder.collection.text = (searchModel.ticketFare).convert(currencyFormat)
        holder.seatnumber.text = searchModel.seatNumber


        if (searchModel.remarks.isNotEmpty()) {
            holder.remaks.visible()
            holder.remaks.text = searchModel.remarks
        } else {
            holder.remaks.gone()
        }

        // Condition changes as per discussion with Ahemad (implemented same condition as IOS)
        val totalTrip = searchModel.totalTrip ?: 0
        if (privilegeResponseModel?.allowToShowFrequentTravellerTag == true && totalTrip >= 5) {

            holder.friquentTraveller.visible()
            holder.totalTrip.visible()
            holder.totalTrip.text = "${context.getString(R.string.total_trips)}$totalTrip"

//            if (searchModel.totalTrip == null || searchModel.totalTrip == 0) {
//                holder.totalTrip.gone()
//                holder.friquentTraveller.gone()
//            } else {
////                if (searchModel.totalTrip!! >= 5) {
////                    holder.friquentTraveller.visible()
////                } else {
////                    holder.friquentTraveller.gone()
////                }
//                holder.friquentTraveller.visible()
//                holder.totalTrip.text = "${context.getString(R.string.total_trips)}${searchModel.tripCounts}"
//            }
        } else {
            holder.totalTrip.gone()
            holder.friquentTraveller.gone()
        }

        if (!searchModel.bookingSrcImage.isNullOrEmpty()) {
            Glide.with(context).load(searchModel.bookingSrcImage).into(holder.busBookingIcon)
        } else {
            holder.busBookingIcon.gone()
        }
        if (searchModel.isPhoneBooking) {
            holder.phoneBookingIcon.visible()
        } else {
            holder.phoneBookingIcon.gone()
        }
//        Timber.d("payAtBus: ${searchModel.isPayAtBus}")
//
//        if (searchModel.isPayAtBus == true) {
//            holder.payAtBus.visible()
//        } else {
//            holder.payAtBus.gone()
//        }


        when (searchModel.status) {
            0 -> {
                holder.yetToBoard.text = context.getString(R.string.yet_to_board)
                holder.yetToBoard.setTextColor(ContextCompat.getColor(context, R.color.colorRed2))
            }
            1 -> {
                holder.yetToBoard.text = context.getString(R.string.unboarded_status)
                holder.yetToBoard.setTextColor(ContextCompat.getColor(context, R.color.colorRed2))

            }
            2 -> {
                holder.yetToBoard.text = context.getString(R.string.boarded_status)
                holder.yetToBoard.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
            3 -> {
                holder.yetToBoard.text = context.getString(R.string.no_show)
                holder.yetToBoard.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            4 -> {
                holder.yetToBoard.text = context.getString(R.string.missing_status)
                holder.yetToBoard.setTextColor(ContextCompat.getColor(context, R.color.color_03_review_02_moderate))
            }
            5 -> {
                holder.yetToBoard.text = context.getString(R.string.dropped_off)
                holder.yetToBoard.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
            9 -> {
                holder.yetToBoard.text = context.getString(R.string.check_in)
                holder.yetToBoard.setTextColor(ContextCompat.getColor(context, R.color.colorRed2))
            }
        }

        holder.checkBoarded.setOnCheckedChangeListener(null)
        holder.checkBoarded.isChecked = searchModel.status == 2
        holder.checkBoarded.isClickable = true
        holder.callPassenger.isClickable = true
        holder.imageexpandmore.isClickable = true
        holder.parentChard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))

        holder.checkBoarded.setOnTouchListener(View.OnTouchListener { v, event -> event.actionMasked == MotionEvent.ACTION_MOVE }) //disable swipe
        holder.checkBoarded.setOnClickListener {
            if (holder.checkBoarded.isChecked) {
                holder.checkBoarded.isChecked = searchModel.status == 2
                holder.checkBoarded.tag = "boarded"
                if (statusCount == 0) {
                    val statusSelected =
                        PreferenceUtils.setPreference(
                            "pickUpChartStatus",
                            "${searchModel.status}"
                        )
                }
                statusCount += 1

                holder.layoutstatus.tag = searchModel.pnrNumber
                holder.detailedpart.tag = position
                onclickitemMultiView.onClickMuliView(
                    holder.layoutstatus,
                    holder.yetToBoard,
                    holder.checkBoarded,
                    holder.detailedpart,
                    searchModel.passengerName,
                    searchModel.seatNumber

                )
                onclickitemMultiView.onClickAdditionalData(
                    holder.boardedLoayout,
                    holder.layoutstatus
                )

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

        holder.callPassenger.setOnClickListener {
            if (searchModel.phoneNumber.contains("*") || searchModel.phoneNumber.isNullOrEmpty()) {
                context.toast(context.getString(R.string.phone_number_is_not_visible))

            } else {
                if (privilegeResponseModel?.tsPrivileges?.allowToDisplayCustomerPhoneNumber == false) {
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
                            try {
                                if (privilegeResponseModel?.country != null) {
                                    val countryName = privilegeResponseModel.country

                                    if (getCountryCodes() != null && getCountryCodes().isNotEmpty())
                                        countryList = getCountryCodes()

                                    val telNo = getPhoneNumber(passPhone = searchModel.phoneNumber, countryName)

                                    if (countryList.isNotEmpty()) {
                                        val finalTelNo = if (searchModel.phoneNumber.contains("+")) {
                                            "+$telNo"
                                        } else {
                                            "+${countryList[0]}$telNo"
                                        }

                                        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${finalTelNo}"))
                                        context.startActivity(intent)
                                    } else {
                                        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${searchModel.phoneNumber}"))
                                        context.startActivity(intent)
                                    }
                                } else {
                                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${searchModel.phoneNumber}"))
                                    context.startActivity(intent)
                                }
                            } catch (e:Exception){
                                context.toast(context.getString(R.string.something_went_wrong))
                            }
                        }
                    }
                } else {
                    onCallClickListener.invoke(searchModel.phoneNumber)
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
            holder.detailedpart.visible()
            holder.imageexpandless.visible()
            holder.imageexpandmore.gone()

            holder.layoutstatus.setOnClickListener {
                holder.checkBoarded.tag = "Status"
                if (statusCount == 0) {
                    val statusSelected =
                        PreferenceUtils.setPreference(
                            "pickUpChartStatus",
                            "${searchModel.status}"
                        )
                }
                holder.layoutstatus.tag = searchModel.pnrNumber
                statusCount += 1

                holder.detailedpart.tag = position
                onclickitemMultiView.onClickMuliView(
                    view = holder.layoutstatus,
                    view2 = holder.yetToBoard,
                    view3 = holder.checkBoarded,
                    view4 = holder.detailedpart,
                    resID = searchModel.passengerName,
                    remarks = searchModel.seatNumber

                )
                onclickitemMultiView.onClickAdditionalData(
                    holder.boardedLoayout,
                    holder.layoutstatus
                )

            }
            holder.layoutModify.setOnClickListener {
                holder.layoutModify.tag = "${searchModel.pnrNumber}&${searchModel.seatNumber}"
                    onItemClickListener.onClick(holder.layoutModify, position)
            }

            holder.layoutluggage.setOnClickListener {
                PreferenceUtils.putString(
                    "genderAge",
                    "${searchModel.status},${searchModel.sex},${searchModel.passengerAge} "
                )

                holder.layoutluggage.tag = "luggage"
                onItemPassData.onItemDataMore(
                    view = holder.layoutluggage,
                    str1 = searchModel.passengerName,
                    str2 = searchModel.seatNumber,
                    str3 = searchModel.pnrNumber
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

//                val intent = if(privilegeResponseModel?.country.equals("India", true) || privilegeResponseModel?.country.equals("Indonesia", true)) {
//                    Intent(context, TicketDetailsActivityCompose::class.java)
//                } else {
//                    Intent(context, TicketDetailsActivity::class.java)
//                }

//                val intent=Intent(context, TicketDetailsActivityCompose::class.java)
//
//                context.toast("Click")
//                intent.putExtra(
//                    context.getString(R.string.TICKET_NUMBER),
//                    searchModel.pnrNumber
//                )
//                intent.putExtra("returnToDashboard", false)
//
//                context.startActivity(intent)
                val pnrNumber = searchModel.pnrNumber
                onPnrListener?.onPnrSelection(context.getString(R.string.view_ticket), pnrNumber)
            }
            
            count++
        }
        
        holder.imageexpandless.setOnClickListener {
            holder.imageexpandless.gone()
            holder.imageexpandmore.visible()

            holder.detailedpart.gone()
        }


        if (privilegeResponseModel != null) {



            privilegeResponseModel?.let {
                if (it.updatePassengerTravelStatus) {
                    holder.boardedLoayout.visible()
                    if (it.availableAppModes?.allowStatus == true) {

                        var isAgentLogin: Boolean = false
                        var isAllowOnlyOnce = false
                        var checkInToBoard  =false
                        if (privilegeResponseModel?.isAgentLogin != null)
                            isAgentLogin = privilegeResponseModel.isAgentLogin
                        val role = getUserRole(loginModelPref, isAgentLogin = isAgentLogin, context)
                        if (role == context.getString(R.string.role_field_officer)) {
                            isAllowOnlyOnce =
                                privilegeResponseModel?.boLicenses?.allowUserToBoardingStatusOnlyOnce?:false
                            checkInToBoard  = privilegeResponseModel?.boLicenses?.allowUserToChangeCheckInStatusToBoardedOnly?: false

                        } else {
                            isAllowOnlyOnce =
                                privilegeResponseModel?.availableAppModes?.allow_user_to_change_the_the_boarding_status_only_once?: false
                            checkInToBoard  = privilegeResponseModel?.availableAppModes?.allow_user_to_change_the_check_in_status_to_boarded_status_only?: false
                        }



                        if (isAllowOnlyOnce) {
                            if (searchModel.status == 0|| searchModel.status == 9) {
                                if (searchModel.status == 9 && checkInToBoard){
                                    holder.layoutstatus.gone()
                                    holder.checkBoarded.isEnabled = true
                                }else{
                                    holder.layoutstatus.visible()
                                    holder.checkBoarded.isEnabled = true
                                }
                            } else {
                                holder.layoutstatus.gone()
                                holder.checkBoarded.isEnabled = false
                            }
                        }else{
                            if (searchModel.status == 9 && checkInToBoard){
                                holder.layoutstatus.gone()
                                holder.checkBoarded.isEnabled = true
                            }else{
                                holder.layoutstatus.visible()
                            }
                        }
                    } else {
                        holder.layoutstatus.gone()
                    }
                    if (it.availableAppModes?.allowCall == true) {
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
                    if (it.availableAppModes?.allowCall == true) {
                           holder.callPassenger.visible()

                    } else {
                        holder.callPassenger.gone()
                    }
                }


                if (it.availableAppModes?.allowModify == true) {
                    holder.layoutModify.visible()
                } else {
                    holder.layoutModify.gone()
                }

                if (it.availableAppModes?.allowLuggage == true) {
                    holder.layoutluggage.visible()

                } else {
                    holder.layoutluggage.gone()
                }
                if (searchModel.ticketFare != null) {
                    holder.collection.text =
                        "$currency ${(searchModel.ticketFare)?.convert(currencyFormat)}"
                } else {
                    holder.collection.gone()
                }


            }
        } else {
            Timber.d("privilege: false")
            context.toast(context.getString(R.string.server_error))
        }


    }

//    fun addItem(itemList: List<PassengerDetail>) {
//        searchList = itemList
//        notifyDataSetChanged()
//    }

    class ViewHolder(binding: ChildSortSublistAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val detailextend = binding.extendDetail
        val detailedpart = binding.extendedPart
        val imageexpandmore = binding.imgExpandMore
        val imageexpandless = binding.imgExpandLess
        val sideLine = binding.sideLine
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
        val parentChard = binding.parentCardLayout
        val gstText = binding.incGst
        val remaks = binding.remarksField
        val friquentTraveller = binding.frequentTraveller
        val totalTrip = binding.totalTrip
        val phoneBookingIcon = binding.phoneBookingIcon
        val busBookingIcon = binding.busBookingIcon
        val dropOffPoint = binding.dropOffPoint
        val payAtBus = binding.payAtBusTag
        val tvPartiallyBooked = binding.tvPartiallyBooked
        val tvRoute = binding.tvRoute
    }
}