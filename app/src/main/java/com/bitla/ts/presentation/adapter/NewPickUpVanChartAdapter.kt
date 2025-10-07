package com.bitla.ts.presentation.adapter

import android.view.*
import android.content.Context
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.NewPickUpVanChartAdapterBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.view_reservation.RespHash
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import visible

class NewPickUpVanChartAdapter(
    private val context: Context,
    private var role: String,
    private var pickupVanRespHash: ArrayList<RespHash>,
    private val privilegeResponse: PrivilegeResponseModel?,
    private var listener: DialogButtonAnyDataListener,
    private val boardedSwitchAction: ((switchView: SwitchCompat, status: TextView, seatNumber: String?, pnr: String, name: String, dialogBox: Boolean) -> Unit),
) : RecyclerView.Adapter<NewPickUpVanChartAdapter.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()
    private var loginModelPref: LoginModel = LoginModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            NewPickUpVanChartAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        loginModelPref = PreferenceUtils.getLogin()
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pickupVanRespHash.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val respHash: RespHash = pickupVanRespHash[position]

        holder.tvDateTime.text = respHash.stageDepTime + " - " + respHash.name
        val boardedText =
            "Count: " + respHash.pnr_group?.size + " - (" + respHash.boardedPassengers.toString() + " of " + respHash.totalPassengers.toString() + " boarded)"
        holder.boardedPassengerCount.text = boardedText

        if (position == 0) {
            holder.rvNestedItems.visible()
            holder.expandCollapseButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_arrow_up
                )
            )
        } else {
            holder.rvNestedItems.gone()
            holder.expandCollapseButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_arrow_down
                )
            )
        }

        holder.expandCollapseButton.setOnClickListener {
            if (holder.rvNestedItems.isVisible) {
                holder.rvNestedItems.gone()
                holder.expandCollapseButton.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context, R.drawable.ic_arrow_down
                    )
                )
            } else {
                holder.rvNestedItems.visible()
                holder.expandCollapseButton.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context, R.drawable.ic_arrow_up
                    )
                )
            }
        }

        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        val subPickUpVanChartAdapter = SubPickUpVanChartAdapter(
            context,
            role,
            respHash.pnr_group ?: arrayListOf(),
            privilegeResponse,
            listener,
            boardedSwitchAction = { switchView: SwitchCompat, status: TextView, seatNumber: String?, pnr: String, name: String, dialogBox: Boolean ->
                boardedSwitchAction.invoke(
                    switchView,
                    status,
                    seatNumber,
                    pnr,
                    name,
                    dialogBox
                )
            }
        )
        holder.rvNestedItems.layoutManager = layoutManager
        holder.rvNestedItems.adapter = subPickUpVanChartAdapter
        holder.rvNestedItems.setRecycledViewPool(viewPool)
    }


    class ViewHolder(binding: NewPickUpVanChartAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvDateTime = binding.tvDateTime
        val boardedPassengerCount = binding.boardedPassenger
        val rvNestedItems = binding.rvNestedItems
        val expandCollapseButton = binding.expandCollapseButton
    }
}