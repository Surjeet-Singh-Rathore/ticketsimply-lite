package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildPnrDetailsLayoutBinding
import com.bitla.ts.domain.pojo.passenger_history.PassengerHistoryModel
import com.bitla.ts.presentation.view.ticket_details_compose.TicketDetailsActivityCompose
import gone
import visible
import java.lang.reflect.Method


class PnrDetailsAdapter
    (
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var passengersList: ArrayList<PassengerHistoryModel>,
    private var country: String?,
    ) :
    RecyclerView.Adapter<PnrDetailsAdapter.ViewHolder>() {

    private lateinit var layoutManager: RecyclerView.LayoutManager


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildPnrDetailsLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return passengersList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val name = passengersList[position].name
        val phone = passengersList[position].phone_number
        val service = passengersList[position].service_number
        val coach = passengersList[position].coach_number
        val boardOn = passengersList[position].boarding_on
        val count = passengersList[position].passenger_count
        val bookedOn = passengersList[position].issued_on
        val ticketNo = passengersList[position].ticket_number
        val tripCounts = passengersList[position].trip_counts

        if (tripCounts.isNullOrEmpty()) {
            holder.layoutFrequentTraveller.gone()
        } else {
            holder.layoutFrequentTraveller.visible()
            holder.tvTotalTrips.text = tripCounts
        }

        holder.tvTicketNo.visible()
        holder.tvTicketNo.text = "${context?.getString(R.string.pnr)} $ticketNo"


        holder.name.text = "$name | $phone"
        holder.busType.text = "($service) $coach"
        holder.depInfo.text = "$boardOn | $count passengers | Booked on $bookedOn "

        holder.viewTicket.setOnClickListener {
            val intent=Intent(context, TicketDetailsActivityCompose::class.java)
            intent.putExtra("position", position)
            intent.putExtra(context?.getString(R.string.TICKET_NUMBER), ticketNo)
            intent.putExtra("returnToDashboard", false)
            intent.putExtra("fromPnrActivity", true)
            context.startActivity(intent)
        }

        holder.imgMore.setOnClickListener {
            val popup = PopupMenu(context, it)
            popup.inflate(R.menu.mybooking_options)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popup.gravity = Gravity.RIGHT
            }

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.editPassengerDetails -> {
                        onItemClickListener.onClickOfItem(
                            context.getString(R.string.edit_passenger_details),
                            ticketNo.toInt()
                        )
                    }

                    R.id.cancelTicket -> {
                        onItemClickListener.onClickOfItem(
                            context.getString(R.string.cancel_ticket),
                            ticketNo.toInt()
                        )
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
    }

    class ViewHolder(binding: ChildPnrDetailsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imgMore = binding.imgMore
        val viewTicket = binding.viewTicket
        val cardCollection = binding.cardCollection
        val name = binding.passengerName
        val busType = binding.busType
        val depInfo = binding.departureInfo
        val tvTicketNo = binding.tvTicketNo
        val tvTotalTrips = binding.totalTrips
        val layoutFrequentTraveller = binding.layoutFrequentTraveller

    }
}