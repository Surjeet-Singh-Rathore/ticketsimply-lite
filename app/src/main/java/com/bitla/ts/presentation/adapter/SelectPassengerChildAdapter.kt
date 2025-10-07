package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemCheckedListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildSelectedPassengersSubitemBinding
import com.bitla.ts.domain.pojo.booking_summary.Booking
import timber.log.Timber


class SelectPassengerChildAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private val onItemCheckedListener: OnItemCheckedListener,
) :
    RecyclerView.Adapter<SelectPassengerChildAdapter.ViewHolder>(), Filterable {

    private var searchList: ArrayList<Booking> = ArrayList()
    var searchListFiltered: ArrayList<Booking> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildSelectedPassengersSubitemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = searchListFiltered.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking: Booking = searchListFiltered[position]

        holder.tvTicketNo.text = booking.ticket_number
        holder.tvPassengerName.text = booking.passenger_name.toString()
        if (booking.seats.isNotEmpty()) {
            val seatNo = booking.seats.split(",")
            holder.tvSeatNo.text = seatNo[0]
            val totalSeats = "+${seatNo.size.minus(1)}"
            if (seatNo.size.minus(1) > 0) {
                holder.tvTotalSeats.text = totalSeats
                holder.tvTotalSeats.setOnClickListener {
                    holder.tvTotalSeats.tag = context.getString(R.string.total_seats)
                    onItemClickListener.onClick(holder.tvTotalSeats, position)
                }
            } else
                holder.tvTotalSeats.text = context.getString(R.string.empty)
        }

        holder.tvBoardingPoint.text = booking.boarding_point

        holder.chkPassenger.isChecked = booking.isChecked

        Timber.d("chkPassengerIsChecked ${holder.chkPassenger.isChecked}")

        holder.chkPassenger.setOnClickListener { view ->
            val checkBox = view as CheckBox
            booking.isChecked = checkBox.isChecked
            notifyItemChanged(position)
            onItemCheckedListener.onItemChecked(booking.isChecked, holder.chkPassenger, position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(list: List<Booking>) {
        searchList = list as ArrayList<Booking>
        searchListFiltered = searchList
        notifyDataSetChanged()
    }

    class ViewHolder(binding: ChildSelectedPassengersSubitemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val chkPassenger = binding.chkPassenger
        val tvTicketNo = binding.tvTicketNo
        val tvPassengerName = binding.tvPassengerName
        val tvSeatNo = binding.tvSeatNo
        val tvTotalSeats = binding.tvTotalSeats
        val tvBoardingPoint = binding.tvBoardingPoint
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                searchListFiltered =
                    if (charString.isEmpty()) searchList else {
                        val filteredList = ArrayList<Booking>()
                        searchList
                            .filter {
                                (it.ticket_number.lowercase()
                                    .contains(constraint.toString().lowercase()))

                            }
                            .forEach { filteredList.add(it) }
                        filteredList

                    }
                return FilterResults().apply { values = searchListFiltered }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                searchListFiltered = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<Booking>
                notifyDataSetChanged()
            }
        }
    }
}