package com.bitla.ts.presentation.adapter

import android.Manifest
import android.annotation.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.content.res.*
import android.net.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.annotation.*
import androidx.core.app.*
import androidx.core.content.*
import androidx.recyclerview.widget.*
import com.bitla.ts.*
import com.bitla.ts.R
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.block_unblock_reservation.ReasonList
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.activity.reservationOption.announcement.*
import com.bitla.ts.presentation.view.activity.reservationOption.extendedFare.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import timber.log.*
import toast
import visible
import java.lang.reflect.*
import java.util.*


class MyReservationAdapter(
    private val context: Context,
    val privilegeResponseModel: PrivilegeResponseModel?,
    private val onItemClickListener: OnItemClickListener,
    private val onclickitemMultiView: OnclickitemMultiView,
    private var searchList: ArrayList<Service>,
    private var onItemPassData: OnItemPassData,
    private var dialogButtonAnyDataListener: DialogButtonAnyDataListener,
    private var enableCoachLevelReporting: Boolean,
    private var serviceBlockReasonsList: MutableList<ReasonList>
) :
    RecyclerView.Adapter<MyReservationAdapter.ViewHolder>(), OnItemClickListener,
    DialogButtonMultipleView {

    private var loginModelPref: LoginModel = PreferenceUtils.getLogin()
    private var countryList = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildReservationChartBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size

    }

    @SuppressLint("RtlHardcoded", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position >= 0 && position < searchList.size) {

            val popup = PopupMenu(context, holder.imgMore)
            dialogButtonAnyDataListener.onDataSend(1,popup)


            val searchModel: Service = searchList[position]


            val isEditMode =searchModel.isEditMode?:false

            holder.tvstarttime.text = searchModel.departureTime
            holder.traveltime.text = "${searchModel.duration} ${context.getString(R.string.hr)}"
            holder.tvEndtime.text = searchModel.arrivalTime
            val percentage = searchModel.seatOccupancyPercentage.replace("%", "")
            val floatpercent = percentage.toFloat()
            if (floatpercent <= 30.0) {
                holder.occupancyPercentage.backgroundTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.colorRed2
                    )
                )
            } else if (floatpercent in 30.1..50.0) {
                holder.occupancyPercentage.backgroundTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.lightest_yellow
                    )
                )
                holder.occupancyPercentage.setTextColor(context.resources.getColor(R.color.gray_shade_a))
            } else if (floatpercent in 50.1..70.0) {
                holder.occupancyPercentage.backgroundTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.color_03_review_02_moderate
                    )
                )
            } else if (floatpercent >= 70.1) {
                holder.occupancyPercentage.backgroundTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.booked_tickets
                    )
                )
            }
            holder.occupancyPercentage.text = searchModel.seatOccupancyPercentage
            holder.outOfSeats.text = "${searchModel.availableSeats} of ${searchModel.totalSeats}"

            if (privilegeResponseModel != null) {
                privilegeResponseModel?.let {
                    if (it.country.equals("India", true) ) {
                        if(it.allowToShowSelfTripAuditFormUnderPickupChart== true){
                            holder.selfAuditFormIcon.visible()
                            if (searchModel.isSelfAuditForm == false){
                                holder.selfAuditFormIcon.setImageDrawable(context.resources.getDrawable(R.drawable.self_audit_complete))
//                                popup.menu.getItem(9).setVisible(true)
                            }else{
                                holder.selfAuditFormIcon.setImageDrawable(context.resources.getDrawable(R.drawable.self_audit_pending))
//                                popup.menu.getItem(9).setVisible(false)
                            }
                        }else{
                            holder.selfAuditFormIcon.gone() }
                    }
                }
            }


            if (searchModel?.totalAmount != null) {
                holder.totalAmount.visible()

                if (privilegeResponseModel != null) {

                    privilegeResponseModel?.let {
                        if (it.currencyFormat == null)
                            it.currencyFormat =
                                context.getString(R.string.indian_currency_format)
                        if (it.currency.isNotEmpty()) {
                            holder.totalAmount.text =
                                "${context.getString(R.string.totalAmount)}: ${it.currency} ${
                                    (searchModel.totalAmount)?.convert(
                                        it.currencyFormat
                                    )
                                }"
                        } else {
                            holder.totalAmount.text = "${context.getString(R.string.totalAmount)}: ${
                                (searchModel.totalAmount)?.convert(it.currencyFormat)
                            }"
                        }
                    }
                }
            } else
                holder.totalAmount.gone()


            holder.routeInfo.text = "${searchModel.origin} to ${searchModel.destination}"
            holder.numberInfo.text = "${searchModel.number}"
            if (searchModel.name == null || searchModel.name == "") {
                holder.coach_name.gone()
            } else {
                holder.coach_name.visible()
                holder.coach_name.text = " • ${searchModel.name}"
            }

            if (searchModel.viaRoute != null && searchModel.viaRoute!!)
                holder.viaRoute.visible()
            else
                holder.viaRoute.gone()


            if (searchModel.coachNumber == "") {
                holder.coachDetail.text = context.getString(R.string.notAvailable)
            } else {
                holder.coachDetail.text = "${searchModel.coachNumber}"
            }

            val status = searchModel.status!!.lowercase(Locale.getDefault())


            if (searchModel.phoneBlockedSeatCount == 0) {
                holder.phoneBlock.gone()
            } else {
                holder.phoneBlock.visible()
                holder.phoneBlock.text = searchModel.phoneBlockedSeatCount.toString()

            }
            Timber.d("ischartlocked: ${searchModel.isLocked}")

            if (enableCoachLevelReporting) {
                holder.btnViewCoachLayoutChart.visible()
            } else {
                holder.btnViewCoachLayoutChart.gone()
            }

            if (status.toString().equals(context.getString(R.string.active), true)) {
                holder.tvRoutestatus.text = searchModel.status.toString().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
                holder.tvRoutestatus.setTextColor(context.resources.getColor(R.color.booked_tickets))

            } else {
                holder.tvRoutestatus.text = searchModel.status.toString().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
                holder.tvRoutestatus.setTextColor(context.resources.getColor(R.color.blocked_tickets))

            }
            var isAgentLogin: Boolean = false

            if (privilegeResponseModel != null) {
                val privilegeResponse = privilegeResponseModel
                if (privilegeResponse?.isAgentLogin != null)
                    isAgentLogin = privilegeResponse.isAgentLogin

            }

            val role = getUserRole(loginModelPref, isAgentLogin = isAgentLogin, context)
            Timber.d("Service Not Bound 00 ${role}")

            if (role == context.getString(R.string.role_field_officer) && privilegeResponseModel?.country.equals("indonesia", true)
            ) {

                holder.mapbutton.visible()
            }
            Timber.d("Service Not Bound ${PreferenceUtils.getString("mapRoute")}--${searchModel.coachNumber}")
            if (!PreferenceUtils.getString("mapRoute").isNullOrEmpty()) {
                Timber.d("Service Not Bound 0 ${PreferenceUtils.getString("mapRoute")}--${searchModel.coachNumber}")

                if (PreferenceUtils.getString("mapRoute") == searchModel.reservationId.toString()) {
                    holder.mapbutton.text = context.getString(R.string.Continue)
                }
            } else {
                Timber.d("Service Not Bound 1 ${PreferenceUtils.getString("mapRoute")}--${searchModel.coachNumber}")

                holder.mapbutton.text = context.getString(R.string.view_route)
            }


            holder.mapbutton.setOnClickListener {

                holder.mapbutton.tag = "Mapp"
                PreferenceUtils.setPreference(
                    PREF_RESERVATION_ID, searchModel.reservationId
                )
                onItemClickListener.onClick(holder.mapbutton, position)
            }

            holder.btnViewReservationChart.setOnClickListener {
                PreferenceUtils.putString("reservationid", "${searchModel.reservationId}")
                PreferenceUtils.putString("ViewReservation_OriginId", "${searchModel.originId}")
                PreferenceUtils.putString(
                    "ViewReservation_DestinationId",
                    "${searchModel.destinationId}"
                )

                try {
                    PreferenceUtils.putString(
                        "ViewReservation_data",
                        "${searchModel.number} | ${getDateDMY(searchModel.travelDate.toString())} | ${searchModel.origin} - ${searchList[position].destination} | ${searchModel.busType}"
                    )
                }catch (e: Exception){
                    if(BuildConfig.DEBUG){
                        e.printStackTrace()
                    }
                }

                PreferenceUtils.putString("ViewReservation_date", "${searchModel.travelDate} ")
                PreferenceUtils.putString(
                    "toolbarheader",
                    "${searchModel.origin} - ${searchModel.destination}"
                )
                PreferenceUtils.putString(
                    "toolbarsubheader",
                    "${searchModel.number} | ${searchModel.travelDate} | ${searchModel.origin} - ${searchModel.destination} | ${searchModel.busType} ${searchModel.totalSeats}"
                )

                PreferenceUtils.setPreference(
                    PREF_RESERVATION_ID, searchModel.reservationId
                )
                PreferenceUtils.putString(
                    "ViewReservation_name",
                    "${searchModel.origin} - ${searchModel.destination}"
                )
                PreferenceUtils.putString("ViewReservation_number", "${searchModel.number} ")
                PreferenceUtils.putString("ViewReservation_coach_number", "${searchModel.coachNumber} ")
//            PreferenceUtils.putString ("ViewReservation_seats","${searchModel.number} ")
                PreferenceUtils.putString("ViewReservation_driverName", "${searchModel.driverName}")

                holder.btnViewReservationChart.tag = "viewReservation"

                onItemClickListener.onClick(holder.btnViewReservationChart, position)

                firebaseLogEvent(
                    context,
                    VIEW_RESERVATION_CHART,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    VIEW_RESERVATION_CHART,
                    "ViewReservation Chart Clicks"
                )
            }

            holder.btnViewCoachLayoutChart.setOnClickListener {

                holder.btnViewCoachLayoutChart.tag = context.getString(R.string.viewCoachLayout)

                onItemClickListener.onClick(holder.btnViewCoachLayoutChart, position)

                firebaseLogEvent(
                    context,
                    VIEW_COACH_LAYOUT_CHART,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    VIEW_COACH_LAYOUT_CHART,
                    context.getString(R.string.viewcoachlayout_chart_clicks)
                )
            }


            if (searchModel.driverName == ""|| searchModel.driverName== null) holder.lldriver_one.gone()
            else {
                if (!searchModel.driverContactNumber.isNullOrEmpty()) {
                    holder.driver_one_number.text = searchModel.driverContactNumber
                    holder.lldriver_one.setOnClickListener {
                        callFunction(searchModel.driverContactNumber!!)
                    }
                } else {
                    holder.driver_one_number.gone()
                }
                holder.lldriver_one.visible()
                holder.driver_one_name.text = "${searchModel.driverName}(D1)"

            }
            if (searchModel.driver2Name == ""|| searchModel.driver2Name== null) holder.lldriver_two.gone()
            else {
                if (!searchModel.driver2ContactNumber.isNullOrEmpty()) {
                    holder.driver_two_number.text = searchModel.driver2ContactNumber
                    holder.lldriver_two.setOnClickListener {
                        callFunction(searchModel.driver2ContactNumber!!)
                    }
                } else {
                    holder.driver_two_number.gone()
                }
                holder.lldriver_two.visible()
                holder.driver_two_name.text = "${searchModel.driver2Name}(D2)"

            }
            if (searchModel.driver3Name == "" || searchModel.driver3Name== null) holder.lldriver_three.gone()
            else {
                if (!searchModel.driver3ContactNumber.isNullOrEmpty()) {
                    holder.driver_3_number.text = searchModel.driver3ContactNumber
                    holder.lldriver_three.setOnClickListener {
                        callFunction(searchModel.driver3ContactNumber!!)
                    }
                } else {
                    holder.driver_3_number.gone()
                }
                holder.lldriver_three.visible()
                holder.driver_3_name.text = "${searchModel.driver3Name}(D3)"

            }
            if (searchModel.helperName == "") holder.llcleaner.gone()
            else {
                if (!searchModel.helperContactNumber.isNullOrEmpty()) {
                    holder.cleaner.text = searchModel.helperContactNumber
                    holder.llcleaner.setOnClickListener {
                        callFunction(searchModel.helperContactNumber!!)
                    }
                } else {
                    holder.cleaner.gone()
                }
                holder.cleaner_name.text = "${searchModel.helperName}(C1)"

                holder.llcleaner.visible()
            }
            if (searchModel.checkingInspector == "") holder.llchk_inspecter.gone()
            else {
                if (!searchModel.checkingInspectorNumber.isNullOrEmpty()) {
                    holder.chk_inspecter.text = searchModel.checkingInspectorNumber
                    holder.llchk_inspecter.setOnClickListener {
                        callFunction(searchModel.checkingInspectorNumber!!)
                    }
                } else {
                    holder.chk_inspecter.gone()
                }
                holder.chk_inspecter_name.text = "${searchModel.checkingInspector}(Chk.In.)"
                holder.llchk_inspecter.visible()
            }
            if (searchModel.contractorName == null) holder.llcontractor.gone()
            else {
                if (!searchModel.contractorNumber.isNullOrEmpty()) {
                    holder.contractor.text = searchModel.contractorNumber
                    holder.llcontractor.setOnClickListener {
                        callFunction(searchModel.contractorNumber!!)
                    }
                } else {
                    holder.contractor.gone()
                }
                holder.contractor_name.text = "${searchModel.contractorName}(cntr.)"
                holder.llcontractor.visible()
            }

            if (searchModel.isLocked!!) {
                holder.btnViewReservationChart.backgroundTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.colorRed
                    )
                )
                holder.btnViewReservationChart.text = context.getString(R.string.locked)
                holder.btnViewReservationChart.setTextColor(context.getColor(R.color.white))
                holder.btnViewReservationChart.isClickable = false

                // For the View Coach Layout Chart Button
                holder.btnViewCoachLayoutChart.backgroundTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.colorRed
                    )
                )
                holder.btnViewCoachLayoutChart.text = context.getString(R.string.locked)
                holder.btnViewCoachLayoutChart.setTextColor(context.getColor(R.color.white))
                holder.btnViewCoachLayoutChart.isClickable = false
            } else {
                holder.btnViewReservationChart.text = context.getString(R.string.view_reservation_chart)

                holder.btnViewReservationChart.backgroundTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.primaryLight
                    )
                )
                //  holder.btnViewReservationChart.setTextColor(context.getColor(R.color.colorPrimary))
                holder.btnViewReservationChart.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
                holder.btnViewReservationChart.isClickable = true

                // For the View Coach Layout Chart Button
                holder.btnViewCoachLayoutChart.backgroundTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.primaryLight
                    )
                )
                holder.btnViewCoachLayoutChart.text = context.getString(R.string.view_coach_layout_chart)
                holder.btnViewCoachLayoutChart.setTextColor(context.getColor(R.color.colorPrimary))
                holder.btnViewCoachLayoutChart.isClickable = true
            }


            holder.cardlayout.setCardBackgroundColor(context.resources.getColor(R.color.white))

//        } else {
//
//            holder.cardlayout.setCardBackgroundColor(context.resources.getColor(R.color.light_grey))
//            holder.btnViewReservationChart.backgroundTintList = ColorStateList.valueOf(
//                context.resources.getColor(
//                    R.color.colorShadow
//                )
//            )
//            holder.btnViewReservationChart.setTextColor(context.getColor(R.color.white))
//            holder.btnViewReservationChart.isClickable = false
//        }

            holder.imgMore.setOnClickListener {
                firebaseLogEvent(
                    context,
                    MENU,
                    loginModelPref.userName,
                    loginModelPref.travels_name,
                    loginModelPref.role,
                    MENU,
                    "menu click"
                )


                PreferenceUtils.putString("reservationid", "${searchModel.reservationId}")
                PreferenceUtils.putString("ViewReservation_OriginId", "${searchModel.originId}")
                PreferenceUtils.putString("ViewReservation_DestinationId", "${searchModel.destinationId}")

                if (position < searchList.size) {
                    PreferenceUtils.putString("ViewReservation_data",
                        "${searchList[position].number} | ${getDateDMY(searchModel.travelDate.toString())} | ${searchList[position].origin} - ${searchList[position].destination} | ${searchList[position].busType}"
                    )
                }

                PreferenceUtils.putString("ViewReservation_name", "${searchModel.name} ")
                PreferenceUtils.putString("ViewReservation_date", "${searchModel.travelDate} ")
                val popup = PopupMenu(context, holder.imgMore)
                popup.inflate(R.menu.pickup_more_options)
                popup.gravity = Gravity.RIGHT

                if (holder.tvRoutestatus.text.toString()
                        .equals(context.getString(R.string.active), true)
                ) {
                    popup.menu.getItem(6).title = context.getString(R.string.close_block_reservation)
                } else {
                    popup.menu.getItem(6).title = context.getString(R.string.allow_reservation)
                }


                if (privilegeResponseModel != null) {

                    privilegeResponseModel?.let {
                        popup.menu.getItem(8).isVisible =
                            it.updateCrewToolkitChecklist
                        if (it?.country.equals("India", true) ) {
                            if (it.allowToShowSelfTripAuditFormUnderPickupChart == true) {
                                if (searchModel.isSelfAuditForm == false){
                                    popup.menu.getItem(9).setVisible(false)
                                }else{
                                    popup.menu.getItem(9).setVisible(true)
                                }
                            }else{
                                popup.menu.getItem(9).setVisible(false)
                            }
                        }else{
                            popup.menu.getItem(9).setVisible(false)
                        }
                        if (it.showLockLinkInReservationCharts) {
                            if (holder.btnViewReservationChart.text == context.getString(R.string.locked)) {
                                popup.menu.getItem(7).setVisible(false)
                            } else {
                                popup.menu.getItem(7).setVisible(true)
                            }
                        } else {
                            popup.menu.getItem(7).setVisible(false)
                        }
                        if (it.handleBlockAllowFromReservationCharts) {
                            popup.menu.getItem(6).setVisible(true)
                        } else {
                            popup.menu.getItem(6).setVisible(false)
                        }

                        if (it.notifyOption == true) {
                            popup.menu.getItem(0).setVisible(true)
                        } else {
                            popup.menu.getItem(0).setVisible(false)
                        }
                        if (it.allowToExtendFareForServices) {
                            if (holder.tvRoutestatus.text.toString()
                                    .equals(context.getString(R.string.active), true)
                            ) {
                                if (holder.btnViewReservationChart.text == context.getString(R.string.locked)) {
                                    popup.menu.getItem(5).setVisible(false)
                                } else {
                                    popup.menu.getItem(5).setVisible(true)
                                }
                            } else {
                                popup.menu.getItem(5).setVisible(false)
                            }
                        } else {
                            popup.menu.getItem(5).setVisible(false)
                        }

                        if (it.manageCustomerAnnouncement) {
                            popup.menu.getItem(2).setVisible(true)
                        } else {
                            popup.menu.getItem(2).setVisible(false)
                        }

                        if ((loginModelPref.role.equals(context.getString(R.string.role_field_officer), true)
                                    && it.boLicenses?.allowToUpdateVehicleExpenses == true)
                            || it.allowUpdateDetailsOptionInReservationChart)
                        {
                            popup.menu.getItem(3).setVisible(true)
                        } else {
                            popup.menu.getItem(3).setVisible(false)

                        }
                        if (it.bulkUpdationOfTickets == true) {
                            if (holder.tvRoutestatus.text.toString()
                                    .equals(context.getString(R.string.active), true)
                            ) {
                                if (holder.btnViewReservationChart.text == context.getString(R.string.locked)) {
                                    popup.menu.getItem(1).setVisible(false)
                                } else {
                                    if (it.availableAppModes?.allowBpDpFare == false) {
                                        popup.menu.getItem(1).setVisible(true)
                                    } else {
                                        popup.menu.getItem(1).setVisible(false)
                                    }
                                }
                            } else {
                                popup.menu.getItem(1).setVisible(false)
                            }
                        } else {
                            popup.menu.getItem(1).setVisible(false)
                        }
                    }
                } else {
                    context.toast(context.getString(R.string.server_error))
                }

                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        /*R.id.get_current_location -> {
                            val intent = Intent(context, CurrentLocationActivity::class.java)
                            context.startActivity(intent)
                        }*/
                        R.id.edit_chart -> {

                            if(!isEditMode){
                                val tag = context.getString(R.string.edit_chart)
                                onItemClickListener.onClickOfItem(tag, position)
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }

                        }

                        R.id.update_service_details -> {
                            if(position<searchList.size) {
                                if (!isEditMode) {

                                    PreferenceUtils.setPreference(
                                        PREF_RESERVATION_ID, searchList[position].reservationId
                                    )
                                    /*    val busDetails =
                                        "${searchModel.travelDate} ${searchModel.origin} - ${searchModel.destination} ${searchModel.busType} "*/

                                    val busDetails = "${searchModel.number} | ${
                                        searchModel.travelDate?.let { it1 ->
                                            getDateDMY(it1)?.let { it2 ->
                                                getDateDMYY(
                                                    it2
                                                )
                                            }
                                        }
                                    } | ${searchModel.origin} - ${searchModel.destination} | ${searchModel.busType}"

                                    val intent = Intent(context, ServiceDetailsActivity::class.java)
                                    intent.putExtra(
                                        context.getString(R.string.origin),
                                        searchList[position].origin
                                    )
                                    intent.putExtra(
                                        context.getString(R.string.destination),
                                        searchList[position].destination
                                    )
                                    intent.putExtra(
                                        context.getString(R.string.bus_type),
                                        busDetails
                                    )
                                    PreferenceUtils.removeKey(context.getString(R.string.scannedUserName))
                                    PreferenceUtils.removeKey(context.getString(R.string.scannedUserId))
                                    PreferenceUtils.removeKey("selectedScanType")
                                    PreferenceUtils.removeKey(context.getString(R.string.scan_coach))
                                    PreferenceUtils.removeKey(context.getString(R.string.scan_driver_1))
                                    PreferenceUtils.removeKey(context.getString(R.string.scan_driver_2))
                                    PreferenceUtils.removeKey(context.getString(R.string.scan_cleaner))
                                    PreferenceUtils.removeKey(context.getString(R.string.scan_contractor))
                                    context.startActivity(intent)

                                    PreferenceUtils.setPreference(
                                        PREF_RESERVATION_ID,
                                        searchModel.reservationId?.toLong()
                                    )

                                    firebaseLogEvent(
                                        context,
                                        UPDATE_DETAILS,
                                        loginModelPref.userName,
                                        loginModelPref.travels_name,
                                        loginModelPref.role,
                                        UPDATE_DETAILS,
                                        "Update Details - PickupCharts"
                                    )
                                } else {
                                    context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                                }
                            }

                        }

                        R.id.close_reservation_chart -> {

                            if(!isEditMode){
                                PreferenceUtils.putString(
                                    "reservationid",
                                    "${searchModel.reservationId}"
                                )
                                PreferenceUtils.setPreference(
                                    PREF_RESERVATION_ID, searchModel.reservationId
                                )

                                if (item.title == context.getString(R.string.close_block_reservation)) {
                                    holder.tvRoutestatus.tag = context.getString(R.string.inactive)

                                    try {
                                        DialogUtils.closeReservation(
                                            context,
                                            context.getString(R.string.block_reservation),
                                            context.getString(R.string.block_reservation_message),
                                            "${searchModel.number} ${searchModel.name}",
                                            "${searchModel.coachNumber} (${searchModel.busType})",
                                            context.getString(R.string.goBack),
                                            context.getString(R.string.proceed),
                                            this,
                                            holder.imgMore,
                                            holder.btnViewReservationChart,
                                            holder.tvRoutestatus,
                                            holder.cardlayout,
                                            searchModel.reservationId.toString(),
                                            serviceBlockReasonsList

                                        )
                                    } catch (e: IllegalStateException) {
                                        if(BuildConfig.DEBUG){
                                            e.printStackTrace()
                                        }
                                    } catch (e: android.content.ActivityNotFoundException) {
                                        if(BuildConfig.DEBUG){
                                            e.printStackTrace()
                                        }
                                    }



                                } else {
                                    holder.tvRoutestatus.tag = "Activate"

                                    DialogUtils.closeReservationCancle(
                                        context,
                                        context.getString(R.string.allow_reservation),
                                        context.getString(R.string.allowe_reservation_message),
                                        "${searchModel.number} ${searchModel.name}",
                                        "${searchModel.coachNumber} (${searchModel.busType})",
                                        context.getString(R.string.goBack),
                                        context.getString(R.string.proceed),
                                        this,
                                        holder.imgMore,
                                        holder.btnViewReservationChart,
                                        holder.tvRoutestatus,
                                        holder.cardlayout,
                                        searchModel.reservationId.toString()
                                    )
                                }

                                firebaseLogEvent(
                                    context,
                                    CLOSE_RESERVATION,
                                    loginModelPref.userName,
                                    loginModelPref.travels_name,
                                    loginModelPref.role,
                                    CLOSE_RESERVATION,
                                    "Close Reservation - PickupCharts"
                                )
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }


                        }

                        R.id.announcement -> {
                            if(!isEditMode){
                                val busDetails =
                                    "${searchModel.number} | ${
                                        searchModel.travelDate?.let { it1 ->
                                            getDateDMY(it1)?.let { it2 ->
                                                getDateDMYY(
                                                    it2
                                                )
                                            }
                                        }
                                    } | ${searchModel.origin} - ${searchModel.destination} ${searchModel.busType} "

                                val intent = Intent(context, AnnouncementActivity::class.java)
                                intent.putExtra(
                                    context.getString(R.string.res_id),
                                    searchList[position].reservationId.toString()
                                )
                                intent.putExtra(
                                    context.getString(R.string.origin),
                                    searchList[position].origin
                                )
                                intent.putExtra(
                                    context.getString(R.string.destination),
                                    searchList[position].destination
                                )
                                intent.putExtra(
                                    context.getString(R.string.bus_number),
                                    searchList[position].number
                                )
                                intent.putExtra(
                                    context.getString(R.string.dep_time),
                                    searchList[position].departureTime
                                )
                                intent.putExtra(context.getString(R.string.bus_type), busDetails)
                                context.startActivity(intent)
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }

                        }

                        R.id.extend_fare_settings -> {
                            if(!isEditMode){
                                val intent = Intent(context, ExtendedFair::class.java)
                                intent.putExtra("originID", searchList[position].originId.toString())
                                intent.putExtra(
                                    "destinationID",
                                    searchList[position].destinationId.toString()
                                )
                                intent.putExtra("reservationID", searchList[position].reservationId)
                                intent.putExtra("serviceNumber", searchList[position].number)
                                context.startActivity(intent)

                                firebaseLogEvent(
                                    context,
                                    EXTEND_FARE_SETTINGS,
                                    loginModelPref.userName,
                                    loginModelPref.travels_name,
                                    loginModelPref.role,
                                    EXTEND_FARE_SETTINGS,
                                    "Extend Fare Settings - PickupCharts"
                                )
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }

                        }

                        R.id.send_sms -> {
                            PreferenceUtils.removeKey(PREF_EMPLOYEE_TYPE_OPTIONS)
                            PreferenceUtils.removeKey(PREF_SMS_TEMPLATE)
                            PreferenceUtils.removeKey(PREF_CHECKED_PNR)
                            PreferenceUtils.removeKey(PREF_SMS_PASSENGER_TYPE)

                            PreferenceUtils.setPreference(
                                PREF_RESERVATION_ID,
                                searchModel.reservationId
                            )

                            PreferenceUtils.putString(PREF_SOURCE, searchModel.origin)
                            PreferenceUtils.putString(
                                PREF_SOURCE_ID,
                                searchModel.originId.toString()
                            )
                            PreferenceUtils.putString(PREF_DESTINATION, searchModel.destination)
                            PreferenceUtils.putString(
                                PREF_DESTINATION_ID,
                                searchModel.destinationId.toString()
                            )
                            PreferenceUtils.putString(PREF_TRAVEL_DATE,
                                searchModel.travelDate?.let { it1 -> getDateDMY(it1) })
                            PreferenceUtils.putString(PREF_BUS_TYPE, searchModel.busType)
                            PreferenceUtils.putString(PREF_COACH_NUMBER, searchModel.number)
                            PreferenceUtils.putString(
                                PREF_DEPARTURE_TIME,
                                searchModel.departureTime
                            )

                            val intent = Intent(context, SmsNotificationActivity::class.java)
                            intent.putExtra(
                                context.getString(R.string.service_number),
                                searchModel.number
                            )
                            intent.putExtra(
                                context.getString(R.string.bus_type),
                                searchModel.busType
                            )
                            intent.putExtra(
                                context.getString(R.string.dep_time),
                                searchModel.departureTime
                            )
                            context.startActivity(intent)

                            firebaseLogEvent(
                                context,
                                SEND_SMS,
                                loginModelPref.userName,
                                loginModelPref.travels_name,
                                loginModelPref.role,
                                SEND_SMS,
                                "Send SMS - PickupCharts"
                            )
                        }

                        R.id.lock_chart -> {

                            if(!isEditMode){
                                onItemPassData.onItemData(
                                    holder.btnViewReservationChart,
                                    position.toString(),
                                    searchModel.reservationId.toString()
                                )

                                firebaseLogEvent(
                                    context,
                                    LOCK_CHART,
                                    loginModelPref.userName,
                                    loginModelPref.travels_name,
                                    loginModelPref.role,
                                    LOCK_CHART,
                                    "Lock Chart - PickupCharts"
                                )
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }


                        }

                        R.id.itemCrewToolkit -> {

                            if(!isEditMode){
                                PreferenceUtils.setPreference(
                                    PREF_RESERVATION_ID,
                                    searchModel.reservationId
                                )

                                PreferenceUtils.putString(PREF_SOURCE, searchModel.origin)
                                PreferenceUtils.putString(
                                    PREF_SOURCE_ID,
                                    searchModel.originId.toString()
                                )
                                PreferenceUtils.putString(PREF_DESTINATION, searchModel.destination)
                                PreferenceUtils.putString(
                                    PREF_DESTINATION_ID,
                                    searchModel.destinationId.toString()
                                )
                                PreferenceUtils.putString(PREF_TRAVEL_DATE,
                                    searchModel.travelDate?.let { it1 -> getDateDMY(it1) })
                                PreferenceUtils.putString(PREF_BUS_TYPE, searchModel.busType)
                                PreferenceUtils.putString(PREF_COACH_NUMBER, searchModel.number)
                                PreferenceUtils.putString(
                                    PREF_DEPARTURE_TIME,
                                    searchModel.departureTime
                                )

                                val intent = Intent(context, CrewToolkitActivity::class.java)
                                context.startActivity(intent)
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }

                        }


                        R.id.selfAuditFormMI -> {

                            if(!isEditMode){
                                val intent = Intent(context, SelfAuditFormActivity::class.java)
                                intent.putExtra("pickUpResid", searchModel.reservationId)
                                context.startActivity(intent)
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }

                        }
                    }
                    true
                }

                // show icons on popup menu
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popup.setForceShowIcon(true)
                } else {
                    try {
                        val fields = popup.javaClass.declaredFields
                        for (field in fields) {
                            if ("mPopup" == field.name) {
                                field.isAccessible = true
                                val menuPopupHelper = field[popup]
                                val classPopupHelper =
                                    Class.forName(menuPopupHelper.javaClass.name)
                                val setForceIcons: Method = classPopupHelper.getMethod(
                                    "setForceShowIcon",
                                    Boolean::class.javaPrimitiveType
                                )
                                setForceIcons.invoke(menuPopupHelper, true)
                                break
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                popup.show()
            }
        } else {
            context.toast(context.getString(R.string.something_went_wrong))
        }
    }

    inner class ViewHolder(binding: ChildReservationChartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvstarttime = binding.startBustime
        val traveltime = binding.travelTime
        val tvEndtime = binding.endBustime
        val tvRoutestatus = binding.routeStatus
        val imgMore = binding.imgMore

        //        val reservationSwitch = binding.allowReservationSwitch
        val btnViewReservationChart = binding.btnViewReservationChart
        val btnViewCoachLayoutChart = binding.btnViewCoachLayoutChart

        //        val rvDriverNumber = binding.rvDriverNumber
        val occupancyPercentage = binding.percent
        val phoneBlock = binding.blockNoCount
        val outOfSeats = binding.seats
        val totalAmount = binding.totalAmount
        val routeInfo = binding.routeInfo
        val coachDetail = binding.coachDetail
        val driver_one_number = binding.driverOne
        val driver_one_name = binding.driverOneName
        val driver_two_name = binding.driverTwoName
        val driver_two_number = binding.driverTwo
        val driver_3_name = binding.driverThreeName
        val driver_3_number = binding.driverThree
        val numberInfo = binding.numberinfo


        val cleaner = binding.tvCleaner
        val cleaner_name = binding.cleanerName
        val contractor = binding.tvContractor
        val contractor_name = binding.contractorName
        val chk_inspecter_name = binding.dchkInspecterName
        val chk_inspecter = binding.tvInspector
        val lldriver_one = binding.lldriverOne
        val lldriver_two =  binding.lldriverTwo
        val lldriver_three =  binding.lldriverThree
        val llcleaner = binding.llcleaner
        val llcontractor = binding.llcontractor
        val llchk_inspecter = binding.llinspector
        val coach_name = binding.coachName
        val mapbutton = binding.mapbutton

        //        val lockChartLayout = binding.lockChartLayout
        val cardlayout = binding.cardLayout
        val viaRoute = binding.viaRoute
        val selfAuditFormIcon = binding.selfAuditIV

    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }

    override fun onClickOfItem(data: String, position: Int) {
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onLeftButtonClick(
        view: View?,
        view1: View?,
        view2: View?,
        view3: View?,
        resId: String
    ) {
        view?.tag = "Left"


    }

    override fun onRightButtonClick(
        view: View?,
        view1: View?,
        view2: View?,
        view3: View?,
        resId: String,
        remark: String
    ) {
        view?.tag = "Right"

        onclickitemMultiView.onClickMuliView(view!!, view1!!, view2!!, view3!!, resId, remark)
    }

    private fun callFunction(number: String) {
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
                val privilegeResponseModel =
                    privilegeResponseModel!!

                if (privilegeResponseModel?.country != null) {
                    val countryName = privilegeResponseModel.country

                    if (getCountryCodes().isNotEmpty())
                        countryList = getCountryCodes()

                    val telNo = getPhoneNumber(passPhone = number, countryName)

                    if (countryList.isNotEmpty()) {
//                        val finalTelNo = "+${countryList[0]}$telNo"

                        val finalTelNo = if (number.contains("+")) {
                            "+$telNo"
                        } else {
                            "+${countryList[0]}$telNo"
                        }

                        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${finalTelNo}"))
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}