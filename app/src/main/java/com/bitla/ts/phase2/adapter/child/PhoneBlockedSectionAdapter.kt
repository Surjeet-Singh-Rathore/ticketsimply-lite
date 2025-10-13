package com.bitla.ts.phase2.adapter.child

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildPhoneblockedHeaderBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.phone_blocked_model.response.Detail
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.phone_blocked_model.response.PassengerDetail
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.phone_blocked_model.response.Result
import gone
import visible

class PhoneBlockedSectionAdapter(
    private val context: Context,
    private val items: MutableList<Result>?,
    private val pendingReleasedDetails: MutableList<Detail>?,
    val privilegeResponse: PrivilegeResponseModel?
) :
    RecyclerView.Adapter<PhoneBlockedSectionAdapter.PhoneBlockedSectionViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhoneBlockedSectionViewHolder {
        val binding =
            ChildPhoneblockedHeaderBinding.inflate(LayoutInflater.from(context), parent, false)
        return PhoneBlockedSectionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        if (pendingReleasedDetails != null) {
            return pendingReleasedDetails.count()
        } else if (items != null) {
            return items.count()
        }
        return 0
    }

    override fun onBindViewHolder(holder: PhoneBlockedSectionViewHolder, position: Int) {

        if (pendingReleasedDetails != null) {
            val sections = pendingReleasedDetails[position].passengerDetails
            val name = pendingReleasedDetails[position].serviceNo
            val origin = pendingReleasedDetails[position].origin
            val destination = pendingReleasedDetails[position].destination

//            val serviceTitle: String = if (serviceNoOriginDestination.length >= 37) {
//                "${serviceNoOriginDestination.dropLast(serviceNoOriginDestination.length - 37)}... (${pendingReleasedDetails[position].seatCount})"
//            } else {
//                "$name: $origin - $destination (${pendingReleasedDetails[position].seatCount})"
//            }

            val serviceTitle = "$name: $origin - $destination (${pendingReleasedDetails[position].seatCount})"
            holder.title.text = serviceTitle

            holder.title.setOnClickListener {

                if (holder.recyclerView.isVisible) {
                    holder.recyclerView.gone()
                    holder.title.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_arrow_down,
                        0
                    )

                } else {
                    holder.recyclerView.visible()
                    holder.title.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_arrow_up_24,
                        0
                    )
                }
            }

            val adapter =
                PhoneBlockedAdapter(
                    context = context,
                    items = sections as MutableList<PassengerDetail>?,
                    serviceTitle,
                    privilegeResponse
                )

            holder.recyclerView.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            holder.recyclerView.adapter = adapter
            holder.title.text = serviceTitle
        } else {
            val name = items?.get(position)?.serviceNo
            val origin = items?.get(position)?.origin
            val destination = items?.get(position)?.destination
            val sections = items?.get(position)?.passengerDetails

            val serviceTitle = "$name - $origin - $destination"
            val adapter = PhoneBlockedAdapter(
                context,
                sections as MutableList<PassengerDetail>?,
                serviceTitle,
                privilegeResponse
            )

            holder.recyclerView.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            holder.recyclerView.adapter = adapter
            holder.title.text = serviceTitle
        }
    }

    class PhoneBlockedSectionViewHolder(binding: ChildPhoneblockedHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var title: TextView
        var recyclerView: RecyclerView
        var container: ConstraintLayout

        init {
            this.title = binding.title
            this.recyclerView = binding.recyclerViewSection
            this.container = binding.container
        }
    }
}