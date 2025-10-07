package com.bitla.ts.presentation.view.merge_bus.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.AdapterReviewSeatShiftingBinding
import com.bitla.ts.domain.pojo.merge_bus_seat_mapping.response.SeatMappingDetail
import com.bitla.ts.presentation.adapter.ReviewMappedSeatsAdapter
import com.bitla.ts.utils.common.convert
import gone
import toast
import visible


class ReviewSeatShiftingAdapter(
    private val context: Context,
    private val type: String,
    private var currency: String,
    private var currencyFormat: String,
    private val onCallButtonClick: ((phoneNumber: String) -> Unit),
    private val onIgnoreMenuItemClick: ((position: Int) -> Unit),
    private val onRemovePassengerMenuItemClick: ((position: Int) -> Unit)
) :
    RecyclerView.Adapter<ReviewSeatShiftingAdapter.ViewHolder>() {

    private var seatMappingList: MutableList<SeatMappingDetail?> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterReviewSeatShiftingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return seatMappingList.size
    }

    fun addItemsToList(tempList: MutableList<SeatMappingDetail?>) {
        seatMappingList.addAll(tempList)
        notifyDataSetChanged()
    }

    fun addItemToList(item: SeatMappingDetail?) {
        seatMappingList.add(item)
        notifyItemInserted(seatMappingList.size-1)
    }

    fun removeItemFromList(position: Int) {
        seatMappingList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clearList() {
        seatMappingList.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = seatMappingList[position]

        holder.pnrValueTV.text = item?.pnrNumber
        holder.boardingTV.text = item?.source
        holder.boardingTV.text = item?.source
        holder.droppingTV.text = item?.destination
        holder.bookedByTv.text = item?.bookingSource

        if (item?.seats != null) {
            val layoutManager = GridLayoutManager(context, 2)
            holder.rvReviewMappedSeats.layoutManager = layoutManager

            val reviewMappedSeatsAdapter = ReviewMappedSeatsAdapter(
                context, item.seats.toMutableList()
            )

            holder.rvReviewMappedSeats.adapter = reviewMappedSeatsAdapter

        }

        if(item?.isDisabled == true) {
            holder.amountTV.gone()
            holder.bookedByTv.gone()
            holder.callButton.gone()
            holder.moreOptionsIV.gone()
            holder.rootLayout.background = ContextCompat.getDrawable(context, R.drawable.merge_bus_deleted_pnr_bg_grey)

        } else {

            holder.amountTV.visible()
            holder.bookedByTv.visible()
            holder.callButton.visible()
            holder.moreOptionsIV.visible()

            holder.rootLayout.background = ContextCompat.getDrawable(context, R.drawable.bg_light_grey_round_stroke)

            val moreOptionsPopupMenu = PopupMenu(context, holder.moreOptionsIV)
            moreOptionsPopupMenu.inflate(R.menu.review_shift_passenger_menu)
            moreOptionsPopupMenu.menu.getItem(0).title =
                context.getString(R.string.ignore_update_to_currency_0, currency)

            moreOptionsPopupMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.ignoreMI -> {
                        onIgnoreMenuItemClick.invoke(position)
                    }

                    R.id.removeMI -> {
                        onRemovePassengerMenuItemClick.invoke(position)
                    }
                }

                true
            }

            val fare =
                if (item?.amount?.isNotEmpty() == true) {
                    currency + item.amount?.toDouble()?.convert(
                        currencyFormat
                    )

                } else {
                    ""
                }


            when (type) {

                "toReceive" -> {
                    holder.amountTV.text = "${context.getString(R.string.receive)} $fare"
                }

                "noDifference" -> {
                    holder.amountTV.text = context.getString(R.string.no_price_difference)
                    moreOptionsPopupMenu.menu.getItem(0).isVisible = false
                }
                else -> {
                    holder.amountTV.text = "${context.getString(R.string.pay)} $fare"
                }
            }


            holder.moreOptionsIV.setOnClickListener {
                moreOptionsPopupMenu.show()
            }

            if (item?.passengerContactNumber?.isNotEmpty() == true) {

                holder.callButton.visible()
                holder.callButton.setOnClickListener {
                    onCallButtonClick.invoke(item.passengerContactNumber)
                }
            } else {
                holder.callButton.gone()
            }

        }

    }

    class ViewHolder(binding: AdapterReviewSeatShiftingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val rootLayout = binding.rootLayout
        val pnrValueTV = binding.pnrValueTV
        val rvReviewMappedSeats = binding.rvReviewMappedSeats
        val boardingTV = binding.boardingTV
        val droppingTV = binding.droppingTV
        val bookedByTv = binding.bookedByTv
        val amountTV = binding.amountTV
        val moreOptionsIV = binding.moreOptionsIV
        val callButton = binding.callButton
    }
}