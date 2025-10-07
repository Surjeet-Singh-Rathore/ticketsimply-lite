package com.bitla.ts.presentation.adapter

import android.annotation.*
import android.content.*
import android.content.res.*
import android.os.*
import android.view.*
import android.widget.PopupMenu
import androidx.annotation.*
import androidx.appcompat.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.activity.addRateCard.fetchShowRateCard.*
import com.bitla.ts.presentation.view.activity.reservationOption.extendedFare.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.*
import com.skydoves.balloon.*
import gone
import timber.log.*
import toast
import visible
import java.lang.reflect.*

class BusDetailsAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var availableRoutesList: MutableList<Result>,
    private var lifecycle: LifecycleOwner,
    private var privilegeResponseModel: PrivilegeResponseModel?,
    private var loginModelPref: LoginModel,
) :
    RecyclerView.Adapter<BusDetailsAdapter.ViewHolder>() {

    private var isCanBlockSeats: Boolean = false
    private var isCanUnblockSeats = false
    private var bulkUpdationOfTickets = false
    private var showViewChartLinkInTheSearchResults = false
    private var allowToExtendFareForServices = false
    private var isEditReservation = false

    private var isAllowRapidBookingFlow: Boolean = false
    private var isNotifyOption: Boolean = false
    private var isAllowUpdateDetailsOptionInReservationChart: Boolean = false
    private var allowBooking: Boolean = false
    private var allowRouteRateCard: Boolean = false
    private var country: String = "false"
    private var bookingAfterDoj: Int = 0
    private var isAgentLogin: Boolean = false
    private var isRestrictBooking: Boolean = false
    private var fareWithRupees: String = ""
    private var currency: String = ""
    private var currencyFormat: String = ""
    private var singlePageChartBlockUnblock: Boolean = false
    private var isAllowBpDpFare: Boolean = false
    private var isQuickBookingsForTSApp: Boolean? = false
    private var allowBookingChartShared: Boolean = false
    private var parentTravelId: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemBusDetailsBinding.inflate(LayoutInflater.from(context), parent, false)
        getPref()
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return availableRoutesList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: MutableList<Result>) {
        this.availableRoutesList = filteredList
        notifyDataSetChanged()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val availableRoutesModelItem: Result = availableRoutesList[position]

        if (availableRoutesModelItem.is_dynamic_pricing_available) {
            holder.dynamicPriceIV.visible()
        } else {
            holder.dynamicPriceIV.gone()
        }





        if(!availableRoutesModelItem.instructions.isNullOrEmpty()){
            holder.infoIV.visible()
        }else{
            holder.infoIV.gone()
        }

        holder.infoIV.setOnClickListener {
           // Toast.makeText(context, availableRoutesModelItem.instructions, Toast.LENGTH_LONG).show()
            showCustomToast(context = context,availableRoutesModelItem.instructions,context as BusDetailsActivity)
        }

        if (availableRoutesModelItem.is_allow_multistation_blocked_service != null
            && availableRoutesModelItem.is_allow_multistation_blocked_service
        ) {
            holder.mainContainerCard.setCardBackgroundColor(context.resources.getColor(R.color.button_color))
            holder.mainContainerCard.setOnClickListener {
                context.toast(
                    "${context.getString(R.string.booking_available_pre)} ${availableRoutesModelItem.multistation_allowed_time} ${
                        context.getString(
                            R.string.booking_available_post
                        )
                    }"
                )
            }
        }
        else {
            holder.mainContainerCard.setCardBackgroundColor(context.resources.getColor(R.color.white))
            //holder.imageMoreOptions.setOnClickListener {

            if(availableRoutesModelItem.is_edit_mode){
                holder.mainContainerCard.setCardBackgroundColor(ContextCompat.getColor(context,R.color.little_grey))

            }

            val moreOptionsPopupMenu = PopupMenu(context, holder.imageMoreOptions)
            moreOptionsPopupMenu.inflate(R.menu.booking_options_new_booking_flow)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                moreOptionsPopupMenu.gravity = Gravity.RIGHT
            }
            if (privilegeResponseModel?.rapidBookingWithMotCouponInTsApp == true) {
                val menuItem0: MenuItem = moreOptionsPopupMenu.menu.getItem(0)
                menuItem0.title = context.getString(R.string.rapid_mot_booking)
            }
            when {
                availableRoutesModelItem.is_service_blocked -> {
                    val menuItem0: MenuItem = moreOptionsPopupMenu.menu.getItem(0)
                    menuItem0.isVisible = false
                }

                availableRoutesModelItem.is_bima == true -> {
                    val menuItem0: MenuItem = moreOptionsPopupMenu.menu.getItem(0)
                    menuItem0.isVisible = false
                }

                isAllowRapidBookingFlow && !availableRoutesModelItem.is_service_blocked -> {
                    val menuItem0: MenuItem = moreOptionsPopupMenu.menu.getItem(0)
                    menuItem0.isVisible = true
                }

                else -> {
                    val menuItem0: MenuItem = moreOptionsPopupMenu.menu.getItem(0)
                    menuItem0.isVisible = false
                }
            }

            if (country == "Indonesia") {
                if (isEditReservation) {

                    when {
                        !availableRoutesModelItem.is_service_blocked -> {
                            val menuItem3: MenuItem = moreOptionsPopupMenu.menu.getItem(3)
                            menuItem3.title = context.getString(R.string.block)
                            availableRoutesModelItem.isSingleBlockUnblock =
                                context.getString(R.string.block)
                            menuItem3.icon = context.resources.getDrawable(R.drawable.ic_block)
                            menuItem3.isVisible = true
                        }

                        availableRoutesModelItem.is_service_blocked -> {
                            val menuItem3: MenuItem = moreOptionsPopupMenu.menu.getItem(3)
                            menuItem3.title = context.getString(R.string.unblock)
                            availableRoutesModelItem.isSingleBlockUnblock =
                                context.getString(R.string.unblock)
                            menuItem3.icon =
                                context.resources.getDrawable(R.drawable.ic_un_lock)
                            menuItem3.isVisible = true
                        }

                        else -> {
                            val menuItem3: MenuItem = moreOptionsPopupMenu.menu.getItem(3)
                            availableRoutesModelItem.isSingleBlockUnblock =
                                context.getString(R.string.empty)
                            menuItem3.isVisible = false
                        }
                    }
                } else {
                    val menuItem3: MenuItem = moreOptionsPopupMenu.menu.getItem(3)
                    availableRoutesModelItem.isSingleBlockUnblock =
                        context.getString(R.string.empty)
                    menuItem3.isVisible = false
                }

            } else {

                when {
                    !availableRoutesModelItem.is_service_blocked && singlePageChartBlockUnblock -> {
                        val menuItem3: MenuItem = moreOptionsPopupMenu.menu.getItem(3)
                        menuItem3.title = context.getString(R.string.block)
                        availableRoutesModelItem.isSingleBlockUnblock =
                            context.getString(R.string.block)
                        menuItem3.icon = context.resources.getDrawable(R.drawable.ic_block)
                        menuItem3.isVisible = true
                    }

                    availableRoutesModelItem.is_service_blocked && singlePageChartBlockUnblock -> {
                        val menuItem3: MenuItem = moreOptionsPopupMenu.menu.getItem(3)
                        menuItem3.title = context.getString(R.string.unblock)
                        availableRoutesModelItem.isSingleBlockUnblock =
                            context.getString(R.string.unblock)
                        menuItem3.icon = context.resources.getDrawable(R.drawable.ic_un_lock)
                        menuItem3.isVisible = true
                    }

                    else -> {
                        val menuItem3: MenuItem = moreOptionsPopupMenu.menu.getItem(3)
                        availableRoutesModelItem.isSingleBlockUnblock =
                            context.getString(R.string.empty)
                        menuItem3.isVisible = false
                    }
                }
            }


            if (!isNotifyOption || isAgentLogin) {
                val menuItem1: MenuItem = moreOptionsPopupMenu.menu.getItem(1)
                menuItem1.isVisible = false
            } else {
                val menuItem1: MenuItem = moreOptionsPopupMenu.menu.getItem(1)
                menuItem1.isVisible = true
            }

            if (privilegeResponseModel?.isEditReservation == false) {
                val menuItem1: MenuItem = moreOptionsPopupMenu.menu.getItem(8)
                menuItem1.isVisible = false
            } else {
                val menuItem1: MenuItem = moreOptionsPopupMenu.menu.getItem(8)
                menuItem1.isVisible = true
            }

            if (privilegeResponseModel?.allowToConfigureSeatWiseFare == false) {
                val menuItem1: MenuItem = moreOptionsPopupMenu.menu.getItem(9)
                menuItem1.isVisible = false
            } else {
                val menuItem1: MenuItem = moreOptionsPopupMenu.menu.getItem(9)
                menuItem1.isVisible = true
            }

            if (loginModelPref.role.equals(context.getString(R.string.role_field_officer), true)
                && privilegeResponseModel?.boLicenses?.allowToUpdateVehicleExpenses == true)
            {
                val menuItem2: MenuItem = moreOptionsPopupMenu.menu.getItem(2)
                menuItem2.isVisible = true
            } else if (!isAllowUpdateDetailsOptionInReservationChart) {
                val menuItem2: MenuItem = moreOptionsPopupMenu.menu.getItem(2)
                menuItem2.isVisible = false
            } else {
                val menuItem2: MenuItem = moreOptionsPopupMenu.menu.getItem(2)
                menuItem2.isVisible = true
            }

            if (privilegeResponseModel?.bulkUpdationOfTickets == true && !availableRoutesList[position].is_service_blocked) {
                if (privilegeResponseModel?.availableAppModes?.allowBpDpFare == false) {
                    val menuItem2: MenuItem = moreOptionsPopupMenu.menu.getItem(4)
                    menuItem2.isVisible = true
                } else {
                    val menuItem2: MenuItem = moreOptionsPopupMenu.menu.getItem(4)
                    menuItem2.isVisible = false
                }
            } else {
                val menuItem2: MenuItem = moreOptionsPopupMenu.menu.getItem(4)
                menuItem2.isVisible = false
            }

            if (!showViewChartLinkInTheSearchResults || isAgentLogin) {
                val menuItem2: MenuItem = moreOptionsPopupMenu.menu.getItem(5)
                menuItem2.isVisible = false
            }

            if (privilegeResponseModel?.allowToExtendFareForServices == false) {
                val menuItem2: MenuItem = moreOptionsPopupMenu.menu.getItem(6)
                menuItem2.isVisible = false
            }

            if (privilegeResponseModel?.isEditReservation == false) {
                val menuItem2: MenuItem = moreOptionsPopupMenu.menu.getItem(7)
                menuItem2.isVisible = false
            }

            if (privilegeResponseModel?.allowToShowFrequentTravellerTag == false) {
                val menuItem2: MenuItem = moreOptionsPopupMenu.menu.getItem(10)
                menuItem2.isVisible = false
            }

            if (privilegeResponseModel?.allowRouteRateCards == false || privilegeResponseModel?.country?.equals("India", true) == false) {
                val menuItem2: MenuItem = moreOptionsPopupMenu.menu.getItem(11)
                menuItem2.isVisible = false
            }


            moreOptionsPopupMenu.setOnMenuItemClickListener { item ->
                if (availableRoutesList!=null){
                    when (item.itemId) {
                        R.id.item_rapid_booking -> {
                            if(!availableRoutesModelItem.is_edit_mode){
                                onItemClickListener.onMenuItemClick(
                                    position,
                                    1,
                                    availableRoutesModelItem
                                )
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }

                        }
                        //R.id.item_phone_booking -> onItemClickListener.onMenuItemClick(position,2,availableRoutesModelItem)
                        R.id.item_sms_notification -> onItemClickListener.onMenuItemClick(
                            position,
                            3,
                            availableRoutesModelItem
                        )

                        R.id.item_modify_fare -> {

                            if(!availableRoutesModelItem.is_edit_mode){
                                val busDetails =
                                    "${availableRoutesList[position].number} | ${availableRoutesList[position].dep_date} ${availableRoutesList[position].origin} - ${availableRoutesList[position].destination} ${availableRoutesList[position].bus_type} "
                                PreferenceUtils.putString(
                                    context.getString(R.string.updateRateCard_resId),
                                    "${availableRoutesList[position].reservation_id}"
                                )

                                PreferenceUtils.putString(
                                    context.getString(R.string.updateRateCard_origin),
                                    availableRoutesList[position].origin
                                )
                                PreferenceUtils.putString(
                                    context.getString(R.string.updateRateCard_destination),
                                    availableRoutesList[position].destination
                                )
                                PreferenceUtils.putString(
                                    context.getString(R.string.updateRateCard_originId),
                                    availableRoutesList[position].origin_id.toString()
                                )
                                PreferenceUtils.putString(
                                    context.getString(R.string.updateRateCard_destinationId),
                                    availableRoutesList[position].destination_id.toString()
                                )
                                PreferenceUtils.putString(
                                    context.getString(R.string.updateRateCard_busType),
                                    busDetails
                                )

                                try {
                                    if (availableRoutesList[position].dep_date.contains("-")) {
                                        val date = availableRoutesList[position].dep_date.split("-")
                                        val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                                        PreferenceUtils.putString(
                                            context.getString(R.string.updateRateCard_travelDate),
                                            finalDate
                                        )
                                    } else {
                                        val date = availableRoutesList[position].dep_date.split("/")
                                        val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                                        PreferenceUtils.putString(
                                            context.getString(R.string.updateRateCard_travelDate),
                                            finalDate
                                        )
                                    }
                                } catch (e: Exception) {
                                    context.toast(e.message.toString())
                                }
                                val intent = Intent(context, ModifyFareActivity::class.java)
                                intent.putExtra(context.getString(R.string.bus_type), busDetails)

                                context.startActivity(intent)

                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }
                                                   }

                        R.id.item_seat_wise -> {

                            if(!availableRoutesModelItem.is_edit_mode){
                                try {
                                    val busDetails =
                                        "${availableRoutesList[position].number} | ${availableRoutesList[position].dep_date} ${availableRoutesList[position].origin} - ${availableRoutesList[position].destination} ${availableRoutesList[position].bus_type} "
                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_resId),
                                        "${availableRoutesList[position].reservation_id}"
                                    )
                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_origin),
                                        availableRoutesList[position].origin
                                    )
                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_destination),
                                        availableRoutesList[position].destination
                                    )
                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_originId),
                                        availableRoutesList[position].origin_id.toString()
                                    )
                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_destinationId),
                                        availableRoutesList[position].destination_id.toString()
                                    )
                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_busType),
                                        busDetails
                                    )


                                    if (availableRoutesList[position].dep_date.contains("-")) {
                                        val date = availableRoutesList[position].dep_date.split("-")
                                        val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                                        PreferenceUtils.putString(
                                            context.getString(R.string.updateRateCard_travelDate),
                                            finalDate
                                        )
                                    } else {
                                        val date = availableRoutesList[position].dep_date.split("/")
                                        val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                                        PreferenceUtils.putString(
                                            context.getString(R.string.updateRateCard_travelDate),
                                            finalDate
                                        )
                                    }

                                    val intent = Intent(context, SeatWiseFareActivity::class.java)
                                    intent.putExtra(context.getString(R.string.bus_type), busDetails)

                                    context.startActivity(intent)

                                } catch (e: Exception) {
                                    context.toast(e.message.toString())
                                }
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }



                        }

                        R.id.item_service_details -> {

                            if(!availableRoutesModelItem.is_edit_mode){
                                if (position < availableRoutesList.size) {
                                    PreferenceUtils.setPreference(
                                        PREF_RESERVATION_ID,
                                        availableRoutesList[position].reservation_id
                                    )
                                    onItemClickListener.onMenuItemClick(
                                        position,
                                        4,
                                        availableRoutesModelItem
                                    )
                                } else
                                    Timber.d("Invalid index")
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }

                        }

                        R.id.item_block_unblock ->
                            if(!availableRoutesModelItem.is_edit_mode){
                                onItemClickListener.onMenuItemClick(
                                    position,
                                    5,
                                    availableRoutesModelItem
                                )
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }


                        R.id.item_edit_chart ->
                            if(!availableRoutesModelItem.is_edit_mode){
                                onItemClickListener.onMenuItemClick(
                                    position,
                                    6,
                                    availableRoutesModelItem
                                )
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }


                        R.id.item_view_reservation_chart ->
                            try {
                                if (position < availableRoutesList.size) {
                                    PreferenceUtils.putString(
                                        "reservationid",
                                        "${availableRoutesList[position].reservation_id}"
                                    )
                                    PreferenceUtils.putString(
                                        "ViewReservation_OriginId",
                                        "${availableRoutesList[position].origin_id}"
                                    )
                                    PreferenceUtils.putString(
                                        "ViewReservation_DestinationId",
                                        "${availableRoutesList[position].destination_id}"
                                    )
                                    PreferenceUtils.putString(
                                        "ViewReservation_data",
                                        "${availableRoutesList[position].number} | ${
                                            getDateDMY(
                                                availableRoutesList[position].arr_date
                                            )
                                        } | ${availableRoutesList[position].origin} - ${availableRoutesList[position].destination} | ${availableRoutesList[position].bus_type}"
                                    )
                                    PreferenceUtils.putString(
                                        "ViewReservation_date",
                                        "${availableRoutesList[position].dep_date} "
                                    )
                                    PreferenceUtils.setPreference(
                                        PREF_RESERVATION_ID,
                                        availableRoutesList[position].reservation_id
                                    )
                                    PreferenceUtils.putString(
                                        "ViewReservation_name",
                                        "${availableRoutesList[position].origin} - ${availableRoutesList[position].destination}"
                                    )
                                    PreferenceUtils.putString(
                                        "ViewReservation_number",
                                        "${availableRoutesList[position].number} "
                                    )
                                    PreferenceUtils.putString(
                                        "ViewReservation_seats",
                                        "${availableRoutesList[position].number} "
                                    )
                                    holder.itemView.tag = "viewReservation"
                                    onItemClickListener.onClick(holder.itemView, position)

                                    firebaseLogEvent(
                                        context,
                                        RESERVATION_CHART,
                                        loginModelPref.userName,
                                        loginModelPref.travels_name,
                                        loginModelPref.role,
                                        RESERVATION_CHART,
                                        "Reservation Chart - SRP"
                                    )
                                } else
                                    Timber.d("Invalid index")
                            } catch (e: Exception) {
                                //
                            }

                        R.id.item_extend_fare_settings -> {
                            if(!availableRoutesModelItem.is_edit_mode){
                                try {
                                    if (position < availableRoutesList.size) {
                                        val intent = Intent(context, ExtendedFair::class.java)
                                        intent.putExtra(
                                            "originID",
                                            availableRoutesList[position].origin_id
                                        )
                                        intent.putExtra(
                                            "destinationID",
                                            availableRoutesList[position].destination_id
                                        )
                                        intent.putExtra(
                                            "reservationID",
                                            availableRoutesList[position].reservation_id
                                        )
                                        intent.putExtra(
                                            "serviceNumber",
                                            availableRoutesList[position].number
                                        )

                                        context.startActivity(intent)
                                        val date = availableRoutesList[position].dep_date.split("/")
                                        if (date.size > 2) {
                                            val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                                            PreferenceUtils.putString("ViewReservation_date", finalDate)
                                        }

                                        firebaseLogEvent(
                                            context,
                                            EXTEND_FARE_SETTINGS,
                                            loginModelPref.userName,
                                            loginModelPref.travels_name,
                                            loginModelPref.role,
                                            EXTEND_FARE_SETTINGS,
                                            "Extend Fare Settings - SRP"
                                        )
                                    }

                                } catch (e: Exception) {
                                    Timber.d("Invalid index")
                                }
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }

                        }

                        R.id.item_update_rate_card -> {
                            if(!availableRoutesModelItem.is_edit_mode){
                                if (position < availableRoutesList.size) {
                                    val busDetails =
                                        "${availableRoutesList[position].number} | ${availableRoutesList[position].dep_date} ${availableRoutesList[position].origin} - ${availableRoutesList[position].destination} ${availableRoutesList[position].bus_type} "
                                    val intent = Intent(context, UpdateRateCardActivity::class.java)
                                    intent.putExtra(
                                        context.getString(R.string.origin),
                                        availableRoutesList[position].origin
                                    )
                                    intent.putExtra(
                                        context.getString(R.string.destination),
                                        availableRoutesList[position].destination
                                    )
                                    intent.putExtra(context.getString(R.string.bus_type), busDetails)

                                intent.putExtra(context.getString(R.string.is_multi_hop_service),availableRoutesModelItem.multihop_service)

                                PreferenceUtils.putString(
                                    context.getString(R.string.updateRateCard_resId),
                                    "${availableRoutesList[position].reservation_id}"
                                )
                                PreferenceUtils.putString(
                                    context.getString(R.string.updateRateCard_origin),
                                    availableRoutesList[position].origin
                                )
                                PreferenceUtils.putString(
                                    context.getString(R.string.updateRateCard_destination),
                                    availableRoutesList[position].destination
                                )
                                PreferenceUtils.putString(
                                    context.getString(R.string.updateRateCard_originId),
                                    availableRoutesList[position].origin_id.toString()
                                )
                                PreferenceUtils.putString(
                                    context.getString(R.string.updateRateCard_destinationId),
                                    availableRoutesList[position].destination_id.toString()
                                )
                                PreferenceUtils.putString(
                                    context.getString(R.string.updateRateCard_busType),
                                    busDetails
                                )

                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_allow_booking_type_fare),
                                        "${availableRoutesList[position].allow_booking_type_fare}"
                                    )


                                    firebaseLogEvent(
                                        context,
                                        UPDATE_RATE_CARD,
                                        loginModelPref.userName,
                                        loginModelPref.travels_name,
                                        loginModelPref.role,
                                        UPDATE_RATE_CARD,
                                        "Update Rate Card - SRP"
                                    )

                                    context.startActivity(intent)
                                    try {
                                        if (availableRoutesList[position].dep_date.contains("-")) {
                                            val date = availableRoutesList[position].dep_date.split("-")
                                            val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                                            PreferenceUtils.putString(
                                                context.getString(R.string.updateRateCard_travelDate),
                                                finalDate
                                            )
                                        } else {
                                            val date = availableRoutesList[position].dep_date.split("/")
                                            val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                                            PreferenceUtils.putString(
                                                context.getString(R.string.updateRateCard_travelDate),
                                                finalDate
                                            )
                                        }
                                    } catch (e: Exception) {
                                        context.toast(e.message.toString())
                                    }

                                } else
                                    Timber.d("Invalid index")
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }

                        }


                        R.id.item_frequent_traveller -> {
                            if(!availableRoutesModelItem.is_edit_mode){
                                onItemClickListener.onMenuItemClick(
                                    position,
                                    10,
                                    availableRoutesModelItem
                                )
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }

                        }

                        R.id.item_rate_card -> {
                            if(!availableRoutesModelItem.is_edit_mode){
                                if (position < availableRoutesList.size) {
                                    val busDetails = "${availableRoutesList[position].number} | ${availableRoutesList[position].dep_date} ${availableRoutesList[position].origin} - ${availableRoutesList[position].destination} ${availableRoutesList[position].bus_type} "

                                    val intent = Intent(context, RateCardMainActivity::class.java)
                                    intent.putExtra(
                                        context.getString(R.string.updateRateCard_routeId),
                                        availableRoutesList[position].id.toString()
                                    )
                                    intent.putExtra(
                                        context.getString(R.string.origin),
                                        availableRoutesList[position].origin
                                    )
                                    intent.putExtra(
                                        context.getString(R.string.destination),
                                        availableRoutesList[position].destination
                                    )
                                    intent.putExtra(context.getString(R.string.bus_type), busDetails)


                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_resId),
                                        "${availableRoutesList[position].reservation_id}"
                                    )
                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_origin),
                                        availableRoutesList[position].origin
                                    )
                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_destination),
                                        availableRoutesList[position].destination
                                    )
                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_originId),
                                        availableRoutesList[position].origin_id.toString()
                                    )
                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_destinationId),
                                        availableRoutesList[position].destination_id.toString()
                                    )
                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_busType),
                                        busDetails
                                    )

                                    PreferenceUtils.putString(
                                        context.getString(R.string.updateRateCard_allow_booking_type_fare),
                                        "${availableRoutesList[position].allow_booking_type_fare}"
                                    )

                                    firebaseLogEvent(
                                        context,
                                        RATE_CARD,
                                        loginModelPref.userName,
                                        loginModelPref.travels_name,
                                        loginModelPref.role,
                                        RATE_CARD,
                                        "Rate Card - SRP"
                                    )

                                    context.startActivity(intent)
                                    try {
                                        if (availableRoutesList[position].dep_date.contains("-")) {
                                            val date = availableRoutesList[position].dep_date.split("-")
                                            val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                                            PreferenceUtils.putString(
                                                context.getString(R.string.updateRateCard_travelDate),
                                                finalDate
                                            )
                                        } else {
                                            val date = availableRoutesList[position].dep_date.split("/")
                                            val finalDate = "${date[2]}-${date[1]}-${date[0]}"
                                            PreferenceUtils.putString(
                                                context.getString(R.string.updateRateCard_travelDate),
                                                finalDate
                                            )
                                        }
                                    } catch (e: Exception) {
                                        context.toast(e.message.toString())
                                    }

                                } else
                                    Timber.d("Invalid index")
                            }else{
                                context.toast(context.getString(R.string.this_service_is_under_edit_mode))
                            }

                        }
                    }
                }
                true
            }
            if (position < availableRoutesList.size)
                availableRoutesList[position].reservation_id
            // show icons on popup menu
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                moreOptionsPopupMenu.setForceShowIcon(true)
            } else {
                try {
                    val fields = moreOptionsPopupMenu.javaClass.declaredFields
                    for (field in fields) {
                        if ("mPopup" == field.name) {
                            field.isAccessible = true
                            val menuPopupHelper = field[moreOptionsPopupMenu]
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

            //moreOptionsPopupMenu.show()
            //}

            if (availableRoutesModelItem.is_bima != null && availableRoutesModelItem.is_bima){
                holder.imageMoreOptions.gone()
            } else {
                if (moreOptionsPopupMenu.menu.hasVisibleItems()) {
                    holder.imageMoreOptions.visible()
                } else {
                    holder.imageMoreOptions.gone()
                }
                holder.imageMoreOptions.setOnClickListener {
                    moreOptionsPopupMenu.show()
                }
            }
//            holder.imageMoreOptions.gone()

            holder.itemView.setOnClickListener {
                if (position < availableRoutesList.size) {
                    PreferenceUtils.setPreference(
                        PREF_RESERVATION_ID, availableRoutesList[position].reservation_id
                    )
                    holder.itemView.tag = context.getString(R.string.tag_book_seat)
                    if(availableRoutesModelItem.is_edit_mode){
                        context.toast(context.getString(R.string.this_service_is_under_edit_mode))

                    }else{
                        onItemClickListener.onClick(holder.itemView, position)

                    }
                } else
                    Timber.d("Invalid index")
            }
        }


        if (availableRoutesModelItem.is_service_blocked) {
            holder.serviceBlockedText.visible()
            holder.serviceBlockedText.text = context.getString(R.string.service_blocked)
            holder.blockedSeatTV.gone()
        } else {
            holder.serviceBlockedText.gone()
            holder.blockedSeatTV.visible()
        }

        if (availableRoutesModelItem.is_bima != null && availableRoutesModelItem.is_bima
            && availableRoutesModelItem.travel_name.isNotEmpty()
        ) {
            holder.travelName.visible()
            holder.travelName.text = availableRoutesModelItem.travel_name
        } else {
            holder.travelName.gone()
        }

        holder.srcDestination.text =
            availableRoutesModelItem.dep_time + " - " + availableRoutesModelItem.arr_time

        holder.busNameTV.text =
            availableRoutesModelItem.number

        holder.busType.text = availableRoutesModelItem.bus_type
//        val percentage = availableRoutesModelItem.occupancy_percentage.replace("%", "")
//        val floatpercent = percentage.toFloat()

        val percentage = availableRoutesModelItem.occupancy_percentage.replace("%", "").trim()
        val floatpercent: Double = if (percentage.isEmpty()) {
            0.0
        } else {
            percentage.toDouble()
        }

        if (floatpercent <= 30.0) {
            holder.percentage.backgroundTintList = ColorStateList.valueOf(
                context.resources.getColor(
                    R.color.colorRed2
                )
            )
            holder.percentage.setTextColor(context.resources.getColor(R.color.white))

        } else if (floatpercent in 30.1..50.0) {
            holder.percentage.backgroundTintList = ColorStateList.valueOf(
                context.resources.getColor(
                    R.color.lightest_yellow
                )
            )
            holder.percentage.setTextColor(context.resources.getColor(R.color.gray_shade_a))
        } else if (floatpercent in 50.1..70.0) {
            holder.percentage.backgroundTintList = ColorStateList.valueOf(
                context.resources.getColor(
                    R.color.color_03_review_02_moderate
                )
            )
            holder.percentage.setTextColor(context.resources.getColor(R.color.white))

        } else if (floatpercent >= 70.1) {
            holder.percentage.backgroundTintList = ColorStateList.valueOf(
                context.resources.getColor(
                    R.color.booked_tickets
                )
            )
            holder.percentage.setTextColor(context.resources.getColor(R.color.white))

        }


        if (availableRoutesModelItem.occupancy_percentage.isNotEmpty() && availableRoutesModelItem.occupancy_percentage != "0.0%") {
            holder.percentage.visible()
            holder.percentage.text = availableRoutesModelItem.occupancy_percentage
        } else {
            holder.percentage.visible()
            holder.percentage.text = "0.0%"
        }

        if (isAgentLogin) {
            holder.tvPhoneBlocked.gone()
            holder.blockedSeatTV.gone()
        } else {
            if (availableRoutesModelItem.phone_blocked_seat_count != 0) {
                holder.tvPhoneBlocked.visible()
                holder.tvPhoneBlocked.text =
                    availableRoutesModelItem.phone_blocked_seat_count.toString()
            } else
                holder.tvPhoneBlocked.text = "0"
        }

        if (country == "India" ) {
            holder.seats.text =
                "${availableRoutesModelItem.available_seats}/${availableRoutesModelItem.total_seats}"
        }else{
            holder.seats.text =
                "${availableRoutesModelItem.available_seats}/${availableRoutesModelItem.total_seats}"
        }


        fareWithRupees = availableRoutesModelItem.fare_str.replace(",", "/ ")
        val fareArray = availableRoutesModelItem.fare_str
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val formattedFareList = mutableListOf<String>()

        if (fareArray.size > 4) {
            val sortedFares = fareArray.map { it.toDouble() }.sorted()
            val selectedFares = sortedFares.take(2) + sortedFares.takeLast(2)

            for (fare in selectedFares) {
                formattedFareList.add(fare.convert(currencyFormat))
            }
        } else {
            for (fare in fareArray) {
                if (!percentage.isEmpty() && fare.isNotEmpty()) {
                    formattedFareList.add(fare.toDouble().convert(currencyFormat))
                } else {
                    formattedFareList.add("0.0")
                }
            }
        }

        holder.fare.text = "$currency${formattedFareList.joinToString(" / ")}"


        val blockedSeats =
            "${availableRoutesModelItem.blocked_seats} ${context.getString(R.string.blockedSeats)}"
        holder.blockedSeatTV.text = blockedSeats
        val availability =
            "${context.getString(R.string.availability)} - ${availableRoutesModelItem.seat_type_availability}"
        holder.tvAvailability.text = availability

        if (availableRoutesModelItem.drop_off_details.isNotEmpty()) {
            val arrTime = availableRoutesModelItem.arr_time
            val arrPoint = availableRoutesModelItem.drop_off_details[0].name

            val getDateFormat = getServerDateFormat(availableRoutesModelItem.arr_date)
            val arrDate = inputFormatToOutput(
                availableRoutesModelItem.arr_date,
                getDateFormat, DATE_FORMAT_D_MON3
            )


        }

        holder.blockedSeatTV.text =
            availableRoutesModelItem.blocked_seats

        if(holder.position==availableRoutesList.size-1){
            holder.itemView.tag = "loadNextPage"
            onItemClickListener.onClick(holder.itemView, position)

    }}


    private fun getIntroPopups(holder: ViewHolder) {
        val balloon = Balloon.Builder(context)
            .setLayout(R.layout.popup_phone_booking_intro)
            .setWidthRatio(1.0f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextColorResource(R.color.white)
            .setTextSize(15f)
            .setIconDrawableResource(R.drawable.ic_edit)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setMargin(10)
            .setCornerRadius(8f)
            .setBackgroundColorResource(R.color.colorPrimary)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .setLifecycleOwner(lifecycle)
            .build()

        holder.tvPhoneBlocked.showAlignBottom(balloon)


        val button: AppCompatTextView =
            balloon.getContentView().findViewById(R.id.nextTV)
        button.setOnClickListener {
            balloon.dismiss()
            getBlockedSeatsPopup(holder)
        }

        val dismissTV: AppCompatTextView =
            balloon.getContentView().findViewById(R.id.dismissTV)
        dismissTV.setOnClickListener {
            balloon.dismiss()
        }

    }
    private fun getBlockedSeatsPopup(holder: ViewHolder) {
        val balloon = Balloon.Builder(context)
            .setLayout(R.layout.popup_phone_booking_intro)
            .setWidthRatio(1.0f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextColorResource(R.color.white)
            .setTextSize(15f)
            .setIconDrawableResource(R.drawable.ic_edit)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setMargin(10)
            .setCornerRadius(8f)
            .setBackgroundColorResource(R.color.colorPrimary)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .setLifecycleOwner(lifecycle)
            .build()

        holder.blockedSeatTV.showAlignBottom(balloon)


        val headingTV: AppCompatTextView =
            balloon.getContentView().findViewById(R.id.phoneBookingTV)
        headingTV.text = context.getString(R.string.blocked_seats_intro)

        val descriptionTV: AppCompatTextView = balloon.getContentView().findViewById(R.id.thisTV)
        descriptionTV.text = context.getString(R.string.seats_blocked_intro)

        val button: AppCompatTextView =
            balloon.getContentView().findViewById(R.id.nextTV)
        button.text = "Next 2/4"
        button.setOnClickListener {
            balloon.dismiss()
            getOccupancyPopup(holder)
        }


        val dismissTV: AppCompatTextView =
            balloon.getContentView().findViewById(R.id.dismissTV)
        dismissTV.setOnClickListener {
            balloon.dismiss()
        }

    }

    fun updateData(newList: List<Result>) {
        availableRoutesList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun getOccupancyPopup(holder: ViewHolder) {
        val balloon = Balloon.Builder(context)
            .setLayout(R.layout.popup_phone_booking_intro)
            .setWidthRatio(1.0f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextColorResource(R.color.white)
            .setTextSize(15f)
            .setIconDrawableResource(R.drawable.ic_edit)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setMargin(10)
            .setCornerRadius(8f)
            .setBackgroundColorResource(R.color.colorPrimary)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .setLifecycleOwner(lifecycle)
            .build()

        holder.occupancy.showAlignBottom(balloon)


        val headingTV: AppCompatTextView =
            balloon.getContentView().findViewById(R.id.phoneBookingTV)
        headingTV.text = context.getString(R.string.occupancy_)

        val descriptionTV: AppCompatTextView = balloon.getContentView().findViewById(R.id.thisTV)
        descriptionTV.text = context.getString(R.string.occupancy_status)

        val button: AppCompatTextView =
            balloon.getContentView().findViewById(R.id.nextTV)
        button.text = context.getString(R.string.next_3_4)
        button.setOnClickListener {
            balloon.dismiss()
            getSeatsAvailablePopup(holder)
        }

        val dismissTV: AppCompatTextView =
            balloon.getContentView().findViewById(R.id.dismissTV)
        dismissTV.setOnClickListener {
            balloon.dismiss()
        }

    }
    private fun getSeatsAvailablePopup(holder: ViewHolder) {
        val balloon = Balloon.Builder(context)
            .setLayout(R.layout.popup_phone_booking_intro)
            .setWidthRatio(1.0f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextColorResource(R.color.white)
            .setTextSize(15f)
            .setIconDrawableResource(R.drawable.ic_edit)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setMargin(10)
            .setCornerRadius(8f)
            .setBackgroundColorResource(R.color.colorPrimary)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .setLifecycleOwner(lifecycle)
            .build()

        holder.availabilityTV.showAlignBottom(balloon)


        val headingTV: AppCompatTextView =
            balloon.getContentView().findViewById(R.id.phoneBookingTV)
        headingTV.text = context.getString(R.string.seats_available_)

        val descriptionTV: AppCompatTextView = balloon.getContentView().findViewById(R.id.thisTV)
        descriptionTV.text = context.getString(R.string.seat_availability_intro)

        val button: AppCompatTextView =
            balloon.getContentView().findViewById(R.id.nextTV)
        button.gone()
        val dismissTV: AppCompatTextView =
            balloon.getContentView().findViewById(R.id.dismissTV)
        dismissTV.setOnClickListener {
            balloon.dismiss()
        }

    }


    private fun getPref() {
        if (privilegeResponseModel != null) {

            if (privilegeResponseModel?.isAllowQuickBookingsForTSMobileApp != null) {
                isQuickBookingsForTSApp = privilegeResponseModel?.isAllowQuickBookingsForTSMobileApp
            }

            if (privilegeResponseModel?.isCanBlockSeats != null) {
                isCanBlockSeats = privilegeResponseModel?.isCanBlockSeats!!
            }
            if (privilegeResponseModel?.isCanUnblockSeats != null) {
                isCanUnblockSeats = privilegeResponseModel?.isCanUnblockSeats!!
            }
            if (privilegeResponseModel?.bulkUpdationOfTickets != null) {
                bulkUpdationOfTickets = privilegeResponseModel?.bulkUpdationOfTickets!!
            }
            if (privilegeResponseModel?.showViewChartLinkInTheSearchResults != null) {
                showViewChartLinkInTheSearchResults =
                    privilegeResponseModel?.showViewChartLinkInTheSearchResults!!
            }
            if (privilegeResponseModel?.allowToExtendFareForServices != null) {
                allowToExtendFareForServices = privilegeResponseModel?.allowToExtendFareForServices!!
            }
            if (privilegeResponseModel?.isEditReservation != null) {
                isEditReservation = privilegeResponseModel?.isEditReservation!!
            }
            if (privilegeResponseModel?.allowBooking != null) {
                allowBooking = privilegeResponseModel?.allowBooking!!
            }
            if (privilegeResponseModel?.chartSharedPrivilege?.isNotEmpty() == true && privilegeResponseModel?.chartSharedPrivilege?.get(0)?.privileges?.allow_booking != null) {
                allowBookingChartShared = privilegeResponseModel!!.chartSharedPrivilege!![0].privileges.allow_booking
            }
            if (privilegeResponseModel?.chartSharedPrivilege?.isNotEmpty() == true && privilegeResponseModel?.chartSharedPrivilege?.get(0)?.parent_travel_id != null) {
                parentTravelId = privilegeResponseModel!!.chartSharedPrivilege!![0].parent_travel_id
            }
            if (privilegeResponseModel?.allowRouteRateCards != null) {
                allowRouteRateCard = privilegeResponseModel?.allowRouteRateCards!!
            }
            if (privilegeResponseModel?.country != null) {
                country = privilegeResponseModel?.country!!
            }
            if (privilegeResponseModel?.bookingAfterDoj == null) {
                bookingAfterDoj = 0
            } else {
                if (privilegeResponseModel?.bookingAfterDoj!!.trim().isEmpty()) {
                    bookingAfterDoj = 0
                } else {
                    bookingAfterDoj = privilegeResponseModel?.bookingAfterDoj!!.trim().toInt()
                }
            }
            if (privilegeResponseModel?.availableAppModes?.allowBpDpFare != null) {
                isAllowBpDpFare = privilegeResponseModel?.availableAppModes?.allowBpDpFare!!
            }

            privilegeResponseModel?.let {
                if (privilegeResponseModel?.isAgentLogin == true) {
                    isAgentLogin = true
                }

                privilegeResponseModel.let {
                    if (it?.restrictBooking == true) {
                        isRestrictBooking = it.restrictBooking
                    }
                }
            }

            currency = privilegeResponseModel?.currency.toString()
            currencyFormat = privilegeResponseModel?.currencyFormat
                ?: context.getString(R.string.indian_currency_format)

            if (privilegeResponseModel?.singlePageChartBlockUnblock != null) {
                singlePageChartBlockUnblock = privilegeResponseModel?.singlePageChartBlockUnblock!!
            }

            privilegeResponseModel?.apply {
                if (allowRapidBookingFlow != null) {
                    isAllowRapidBookingFlow = allowRapidBookingFlow
                }

                if (notifyOption != null) {
                    isNotifyOption = notifyOption
                }

                isAllowUpdateDetailsOptionInReservationChart =
                    allowUpdateDetailsOptionInReservationChart

            }
        }


    }

    class ViewHolder(binding: ItemBusDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        val travelName = binding.textTravelName
        val mainContainerCard = binding.mainContainerCard
        val srcDestination = binding.textSourceDestination
        val busType = binding.textBusType
        val percentage = binding.textPercentage
        val seats = binding.textSeats
        val fare = binding.textFare
        val blockedSeatTV = binding.tvSeatBlocked
        val tvAvailability = binding.tvAvailability
        val tvPhoneBlocked = binding.tvPhoneBlocked
        val occupancy = binding.textPercentage
        val availabilityTV = binding.textSeats
        val serviceBlockedText = binding.lockedServiceText
        val imageMoreOptions = binding.imageMoreOptions
        val dynamicPriceIV = binding.dynamicPriceIV
        val busNameTV = binding.busNameTV
        val infoIV = binding.infoIV
    }
}