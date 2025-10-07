package com.bitla.ts.presentation.adapter

import android.annotation.*
import android.content.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.annotation.*
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.*
import gone
import timber.log.*
import visible
import java.lang.reflect.*

class MyReservationAdapterBook(
    private val context: Context,
    private var searchList: ArrayList<Service>,
    val privilegeDetails: PrivilegeResponseModel?,
    private var loginModelPref: LoginModel?,
    private val onItemClickListener: OnItemClickListener,
    private val onMenuItemSelected: (menuItemPosition: Int, itemPosition: Int) -> Unit
) :
    RecyclerView.Adapter<MyReservationAdapterBook.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBusDetailsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    @SuppressLint("RtlHardcoded", "SetTextI18n", "NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val searchModel: com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service =
            searchList[position]

        val travelDate = inputFormatToOutput(
            searchModel.travelDate.toString(),
            DATE_FORMAT_Y_M_D, DATE_FORMAT_D_MON3
        )

        val deptDate = inputFormatToOutput(
            searchModel.arrivaldate.toString(),
            DATE_FORMAT_D_M_Y_SLASH, DATE_FORMAT_D_MON3
        )

        holder.srcDestination.text ="${searchModel.departureTime} - ${searchModel.arrivalTime}"
        holder.busType.text = searchModel.busType
        holder.outOfSeats.text = "${searchModel.availableSeats} of ${searchModel.totalSeats}"
        val availability =
            "${context.getString(R.string.availability)} - ${searchModel.seat_type_availability}"

        val fareArray = searchModel.fareStr?.replace(",", "/ ")?.split("/")
        var fareCurrencyFormat = ""

        for (i in 0..(fareArray?.size?.minus(1) ?: 0)) {
            fareCurrencyFormat =
                "$fareCurrencyFormat ${(fareArray?.get(i)?.toDouble())?.convert((privilegeDetails?.currencyFormat ?: context.getString(R.string.indian_currency_format)))}/"

        }
        holder.fare.text = "${privilegeDetails?.currency}${fareCurrencyFormat.removeSuffix("/")}"

        //holder.fare.text = "₹${searchModel.fareStr}"
        holder.tvAvailability.text = availability
        holder.busNameTV.text = "${searchModel.number}"

        holder.blockedSeatTV.gone()

        if (searchModel.seatOccupancyPercentage.isNotEmpty() && searchModel.seatOccupancyPercentage != "0.0%") {
            holder.occupancyPercentage.visible()
            holder.occupancyPercentage.text = searchModel.seatOccupancyPercentage
        } else {
            holder.occupancyPercentage.visible()
            holder.occupancyPercentage.text = "0.0%"
        }

        if (searchModel.phoneBlockedSeatCount != 0) {
            holder.tvPhoneBlocked.visible()
            holder.tvPhoneBlocked.text = searchModel.phoneBlockedSeatCount.toString()
        } else
            holder.tvPhoneBlocked.gone()

        holder.imageMoreOptions.setOnClickListener {

            val popup = PopupMenu(context, it)
            popup.inflate(R.menu.booking_options_new_booking_flow)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popup.gravity = Gravity.RIGHT
            }

            val menuItem1: MenuItem = popup.menu.getItem(1)
            val menuItem2: MenuItem = popup.menu.getItem(2)
            val menuItem3: MenuItem = popup.menu.getItem(3)
            val menuItem4: MenuItem = popup.menu.getItem(4)
            val menuItem6: MenuItem = popup.menu.getItem(6)
            val menuItem7: MenuItem = popup.menu.getItem(7)
            val menuItem8: MenuItem = popup.menu.getItem(8)
            val menuItem9: MenuItem = popup.menu.getItem(9)

            menuItem1.isVisible = false
            menuItem2.isVisible = false
            menuItem3.isVisible = false
            menuItem4.isVisible = false
            menuItem6.isVisible = false
            menuItem7.isVisible = false
            menuItem8.isVisible = false
            menuItem9.isVisible = false


            if (privilegeDetails?.allowRapidBookingFlow == true) {
                val rapidBookingMenuOption: MenuItem = popup.menu.getItem(0)
                rapidBookingMenuOption.isVisible = true
            } else {
                val rapidBookingMenuOption: MenuItem = popup.menu.getItem(0)
                rapidBookingMenuOption.isVisible = false
            }

            if (privilegeDetails?.notifyOption == true) {
                val sendSmsMenuOption: MenuItem = popup.menu.getItem(1)
                sendSmsMenuOption.isVisible = true
            } else {
                val sendSmsMenuOption: MenuItem = popup.menu.getItem(1)
                sendSmsMenuOption.isVisible = false
            }

            if (privilegeDetails?.showViewChartLinkInTheSearchResults == true) {
                val viewReservationChartMenuOption: MenuItem = popup.menu.getItem(5)
                viewReservationChartMenuOption.isVisible = true
            } else {
                val viewReservationChartMenuOption: MenuItem = popup.menu.getItem(5)
                viewReservationChartMenuOption.isVisible = false
            }

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.item_rapid_booking -> onMenuItemSelected.invoke(0, position)

                    R.id.item_sms_notification -> onMenuItemSelected.invoke(1, position)

                    R.id.item_view_reservation_chart ->
                        try {
                            if (position < searchList.size) {
                                PreferenceUtils.putString(
                                    "reservationid",
                                    "${searchList[position].reservationId}"
                                )
                                PreferenceUtils.putString(
                                    "ViewReservation_OriginId",
                                    "${searchList[position].originId}"
                                )
                                PreferenceUtils.putString(
                                    "ViewReservation_DestinationId",
                                    "${searchList[position].destinationId}"
                                )

                                PreferenceUtils.putString(
                                    "ViewReservation_data",
                                    "${searchList[position].number} | ${
                                        getDateDMY(
                                            searchList[position].travelDate ?: ""
                                        )
                                    } | ${searchList[position].origin} - ${searchList[position].destination} | ${searchList[position].busType}"
                                )
                                PreferenceUtils.putString(
                                    "ViewReservation_date",
                                    "${searchList[position].departureTime} "
                                )
                                PreferenceUtils.setPreference(
                                    PREF_RESERVATION_ID,
                                    searchList[position].reservationId
                                )
                                PreferenceUtils.putString(
                                    "ViewReservation_name",
                                    "${searchList[position].origin} - ${searchList[position].destination}"
                                )
                                PreferenceUtils.putString(
                                    "ViewReservation_number",
                                    "${searchList[position].number} "
                                )
                                PreferenceUtils.putString(
                                    "ViewReservation_seats",
                                    "${searchList[position].number} "
                                )
                                onMenuItemSelected.invoke(5, position)
                                firebaseLogEvent(
                                    context,
                                    RESERVATION_CHART,
                                    loginModelPref?.userName,
                                    loginModelPref?.travels_name,
                                    loginModelPref?.role,
                                    RESERVATION_CHART,
                                    "Reservation Chart - SRP"
                                )
                            } else
                                Timber.d("Invalid index")
                        } catch (e: Exception) {

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

        holder.itemView.setOnClickListener {
            if (position < searchList.size) {
                PreferenceUtils.setPreference(PREF_SOURCE, searchModel.origin.toString())
                PreferenceUtils.setPreference(PREF_DESTINATION, searchModel.destination.toString())
                PreferenceUtils.setPreference(PREF_SOURCE_ID, searchModel.originId.toString())
                PreferenceUtils.setPreference(PREF_DESTINATION_ID, searchModel.destinationId.toString())
                PreferenceUtils.setPreference(PREF_TRAVEL_DATE, getDateDMY(searchModel.travelDate.toString()))
//                PreferenceUtils.setPreference(PREF_RESERVATION_ID, searchModel.reservationId)
                holder.itemView.tag = context.getString(R.string.tag_book_seat)
                onItemClickListener.onClick(holder.itemView, position)

            } else
                Timber.d("Invalid index")
        }
    }

    inner class ViewHolder(binding: ItemBusDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val srcDestination = binding.textSourceDestination
        val busType = binding.textBusType
        val occupancyPercentage = binding.textPercentage
        val outOfSeats = binding.textSeats
        val fare = binding.textFare
        val seats = binding.textSeats
        val tvPhoneBlocked = binding.tvPhoneBlocked
        val tvAvailability = binding.tvAvailability
        val imageMoreOptions = binding.imageMoreOptions
        val busNameTV = binding.busNameTV
        val blockedSeatTV = binding.tvSeatBlocked

    }
}