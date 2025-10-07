package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnQuickBookListener
import com.bitla.ts.databinding.ChildQuickbookSeattypePassengercategoryBinding
import com.bitla.ts.domain.pojo.quick_book_chile.response.quickbook_service_details_response.Result
import timber.log.Timber
import toast

class QBSeatTypePassengerCategoryAdapter(
    private val context: Context,
//    private var quickBookingList: List<Type>,
    private var quickBookingResultList: MutableList<Result>,
    private val onItemClickListener: OnQuickBookListener
    ) :
    RecyclerView.Adapter<QBSeatTypePassengerCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildQuickbookSeattypePassengercategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return quickBookingResultList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quickBookingResultData : Result = quickBookingResultList[position]

        var totalQuickBookAmount: Double
        val totalPassengerCount = 1

        holder.tvSeatType.text = quickBookingResultData.types[position].label
        holder.tvSeatTypeAmount.text = quickBookingResultData.types[position].fare.toString()

        var passengerCount = quickBookingResultData.currentCount
        holder.tvSeatAndPassengerCount.text = passengerCount.toString()

        holder.imgAddSeat.setOnClickListener {
            holder.imgAddSeat.tag = context.getString(R.string.add_seat)

            if (quickBookingResultData.id == 2) {
                totalQuickBookAmount = quickBookingResultData.types[position].fare.toString().toDouble()
                passengerCount++

                quickBookingResultData.currentCount = passengerCount
                holder.tvSeatAndPassengerCount.text = passengerCount.toString()

                if (passengerCount == quickBookingResultData.currentCount) {
                    holder.tvSeatAndPassengerCount.text = passengerCount.toString()

                    Timber.d("passengerCount - $passengerCount")

                    onItemClickListener.quickBook(
                        holder.imgAddSeat,
                        position = position,
                        isAdd = true,
                        passengerCount = passengerCount,
                        totalPassengerCount = totalPassengerCount,
                        label = quickBookingResultData.label,
                        id = quickBookingResultData.id,
                        labelType = quickBookingResultData.types[position].label.toString(),
                        labelTypeId = quickBookingResultData.types[position].idType.toString().toInt(),
                        fare = totalQuickBookAmount
                    )
                }
            } else {

                totalQuickBookAmount = quickBookingResultData.types[position].fare.toString().toDouble()
                passengerCount++

                quickBookingResultData.currentCount = passengerCount
                holder.tvSeatAndPassengerCount.text = passengerCount.toString()

                if (passengerCount == quickBookingResultData.currentCount) {
                    holder.tvSeatAndPassengerCount.text = passengerCount.toString()

                    onItemClickListener.quickBook(
                        holder.imgAddSeat,
                        position = position,
                        isAdd = true,
                        passengerCount = passengerCount,
                        totalPassengerCount = totalPassengerCount,
                        label = quickBookingResultData.label,
                        id = quickBookingResultData.id,
                        labelType = quickBookingResultData.types[position].label.toString(),
                        labelTypeId = quickBookingResultData.types[position].idType.toString().toInt(),
                        fare = totalQuickBookAmount
                    )
                }
            }
        }

        holder.imgRemoveSeat.setOnClickListener {

            if (passengerCount > 0) {
                holder.imgRemoveSeat.tag = context.getString(R.string.remove_seat)

                if (quickBookingResultData.id == 2) {
                    totalQuickBookAmount = quickBookingResultData.types[position].fare.toString().toDouble()
                    passengerCount--

                    quickBookingResultData.currentCount = passengerCount
                    holder.tvSeatAndPassengerCount.text = passengerCount.toString()

                    if (passengerCount == quickBookingResultData.currentCount) {
                        holder.tvSeatAndPassengerCount.text = passengerCount.toString()

                        onItemClickListener.quickBook(
                            holder.imgAddSeat,
                            position = position,
                            isAdd = false,
                            passengerCount = passengerCount,
                            totalPassengerCount = totalPassengerCount,
                            label = quickBookingResultData.label,
                            id = quickBookingResultData.id,
                            labelType = quickBookingResultData.types[position].label.toString(),
                            labelTypeId = quickBookingResultData.types[position].idType.toString().toInt(),
                            fare = totalQuickBookAmount
                        )
                    }
                }
                else {
                    totalQuickBookAmount = quickBookingResultData.types[position].fare.toString().toDouble()
                    passengerCount--

                    quickBookingResultData.currentCount = passengerCount
                    holder.tvSeatAndPassengerCount.text = passengerCount.toString()

                    if (passengerCount == quickBookingResultData.currentCount) {
                        holder.tvSeatAndPassengerCount.text = passengerCount.toString()

                        onItemClickListener.quickBook(
                            holder.imgAddSeat,
                            position = position,
                            isAdd = false,
                            passengerCount = passengerCount,
                            totalPassengerCount = totalPassengerCount,
                            label = quickBookingResultData.label,
                            id = quickBookingResultData.id,
                            labelType = quickBookingResultData.types[position].label.toString(),
                            labelTypeId = quickBookingResultData.types[position].idType.toString().toInt(),
                            fare = totalQuickBookAmount
                        )
                    }
                }
            }
        }
    }

    class ViewHolder(binding: ChildQuickbookSeattypePassengercategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val tvSeatType = binding.tvSeatType
        val tvSeatTypeAmount = binding.tvSeatTypeAmount
        val imgAddSeat = binding.imgAddSeat
        val imgRemoveSeat = binding.imgRemoveSeat
        val tvSeatAndPassengerCount = binding.tvSeatAndPassengerCount
    }
}