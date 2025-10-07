package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ItemBusAgentDetailsBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.sharedPref.PREF_RESERVATION_ID
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import timber.log.Timber
import toast
import visible

class BusDetailsAgentAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var availableRoutesList: MutableList<Result>,
    private var privilegeResponseModel: PrivilegeResponseModel,
) :
    RecyclerView.Adapter<BusDetailsAgentAdapter.ViewHolder>() {
    private var currency: String = ""
    private var currencyFormat: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ItemBusAgentDetailsBinding.inflate(LayoutInflater.from(context), parent, false)
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
        } else {
            holder.mainContainerCard.setCardBackgroundColor(context.resources.getColor(R.color.white))
            holder.itemView.setOnClickListener {
                if (position < availableRoutesList.size) {
                    PreferenceUtils.setPreference(
                        PREF_RESERVATION_ID, availableRoutesList[position].reservation_id
                    )
                    holder.itemView.tag = context.getString(R.string.tag_book_seat)
                    onItemClickListener.onClick(holder.itemView, position)
                } else
                    Timber.d("Invalid index")
            }
        }

        holder.srcDestination.text =
            availableRoutesModelItem.dep_time + " - " + availableRoutesModelItem.arr_time

        holder.busNameTV.text =
            availableRoutesModelItem.number

        holder.busType.text = availableRoutesModelItem.bus_type
        if (!availableRoutesModelItem.occupancy_percentage.isNullOrEmpty()) {
            val percentage = availableRoutesModelItem.occupancy_percentage.replace("%", "")
                val floatpercent = percentage.toFloat()
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
        }
        holder.seats.text =
            "${availableRoutesModelItem.available_seats}/${availableRoutesModelItem.total_seats}"
        if (!availableRoutesModelItem.fare_str.isNullOrEmpty()) {
            val fareArray = availableRoutesModelItem.fare_str.replace(",", "/ ").split("/")
            var fareCurrencyFormat = ""
            for (i in 0..fareArray.size.minus(1)) {
                fareCurrencyFormat =
                    "$fareCurrencyFormat ${(fareArray[i].toDouble()).convert(currencyFormat)}/"
            }
            holder.fare.text = "$currency${fareCurrencyFormat.removeSuffix("/")}"
        }
        if (!availableRoutesModelItem.seat_type_availability.isNullOrEmpty()) {
            val availability =
                "${context.getString(R.string.availability)} - ${availableRoutesModelItem.seat_type_availability}"
            holder.tvAvailability.text = availability
        }
    }


    private fun getPref() {
        if (privilegeResponseModel != null) {
            currency = privilegeResponseModel?.currency.toString()
            currencyFormat = privilegeResponseModel?.currencyFormat
                ?: context.getString(R.string.indian_currency_format)
        }
    }

    class ViewHolder(binding: ItemBusAgentDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        val mainContainerCard = binding.mainContainerCard

        val srcDestination = binding.textSourceDestination
        val busType = binding.textBusType
        val percentage = binding.textPercentage
        val seats = binding.textSeats
        val fare = binding.textFare
        val tvAvailability = binding.tvAvailability
        val occupancy = binding.textPercentage
        val busNameTV = binding.busNameTV
    }
}