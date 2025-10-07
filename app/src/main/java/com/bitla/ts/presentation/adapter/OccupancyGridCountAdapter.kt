package com.bitla.ts.presentation.adapter

import android.content.*
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.*
import androidx.core.content.*
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.occupancy_datewise.response.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.google.android.material.card.*
import gone
import invisible
import visible

class OccupancyGridCountAdapter(
    val context: Context, val privilegeResponseModel: PrivilegeResponseModel?, private var onClick: ((item: Occupancy) -> Unit)
) : RecyclerView.Adapter<OccupancyGridCountAdapter.ViewHolder>() {

    private var oldList: MutableList<Occupancy> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildOccupancyGridCountBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = oldList.size

    fun updateList(newList: MutableList<Occupancy>) {
        oldList = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = oldList[holder.absoluteAdapterPosition]
        if (item.reservationId == null || item.reservationId == 0L){
            if(privilegeResponseModel?.country?.equals("India", true) == true) {
                holder.cardOccupancy.visible()
                holder.tvCount.text = "X"

                holder.cardOccupancy.strokeColor = ContextCompat.getColor(context, R.color.white)
                holder.constraintLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
                holder.coachChangeIndicator.gone()
            } else {
                holder.cardOccupancy.invisible()
            }
            holder.cardOccupancy.setOnClickListener {

            }
        }else {
            holder.cardOccupancy.visible()
            if (item.isInactiveService == true) {
                holder.tvCount.text = context.getString(R.string.inactive)
                holder.cardOccupancy.strokeColor = ContextCompat.getColor(context, R.color.white)
                holder.constraintLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
                holder.coachChangeIndicator.gone()
            } else {

                if (item.isCoachChange == true) {
                    holder.coachChangeIndicator.visible()
                    holder.tvCount.text = "${item.occupiedSeats ?: ""}/${item.totalSeats ?: ""}"

                    holder.constraintLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorWhite
                        )
                    )
                    holder.cardOccupancy.strokeColor =
                        ContextCompat.getColor(context, R.color.white)

                } else {
                    holder.coachChangeIndicator.gone()
                    holder.tvCount.text = item.occupiedSeats.toString()

                    if(privilegeResponseModel?.occupancyForecastReport != true) {
                        changeCardAndTextColor(
                            holder.constraintLayout,
                            holder.cardOccupancy,
                            holder.tvCount,
                            item.occupiedSeats.toString()
                        )
                    }

                }


            }

            holder.cardOccupancy.setOnClickListener {
                onClick.invoke(item)
            }
            //holder.cardOccupancy.strokeColor = ContextCompat.getColor(context, R.color.colorRed2)
        }
    }

    private fun changeCardAndTextColor(constraintLayout: ConstraintLayout, cardView: MaterialCardView, textView: TextView, percent: String?) {

        val floatPercent = percent?.replace(",", "")?.toDouble() ?: 0.0

        if (floatPercent <= 30.0) {
            //textView.setTextColor(ContextCompat.getColor(context, R.color.colorRed2))
            cardView.strokeColor = ContextCompat.getColor(context, R.color.colorRed2)
            constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.color_user_red_dim))

        } else if (floatPercent in 30.1..50.0) {
            //textView.setTextColor(ContextCompat.getColor(context, R.color.lightest_yellow))
            cardView.strokeColor = ContextCompat.getColor(context, R.color.lightest_yellow)
            constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.lightest_yellow_dim))
        } else if (floatPercent in 50.1..70.0) {
            /*textView.setTextColor(
                ContextCompat.getColor(
                    context, R.color.color_03_review_02_moderate
                )
            )*/

            cardView.strokeColor = ContextCompat.getColor(context, R.color.color_03_review_02_moderate)
            constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.color_03_review_02_moderate_transparent))
        } else if (floatPercent >= 70.1) {
            //textView.setTextColor(ContextCompat.getColor(context, R.color.booked_tickets))
            cardView.strokeColor = ContextCompat.getColor(context, R.color.booked_tickets)
            constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.booked_tickets_dim))
        }
    }


    class ViewHolder(binding: ChildOccupancyGridCountBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val tvCount = binding.tvCount
        val cardOccupancy = binding.cardOccupancy
        val coachChangeIndicator = binding.coachChangeIndicator
        val constraintLayout = binding.constraintLayout
    }
}



