package com.bitla.ts.presentation.adapter

import android.app.Dialog
import android.content.Context

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.AdapterCoachOptionsBinding
import com.bitla.ts.domain.pojo.CoachOptionsModel
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.sharedPref.PREF_NEW_BUS_LOCATION_ADDED_LOGO_DISPLAYED
import com.bitla.ts.utils.sharedPref.PreferenceUtils

import java.util.ArrayList

class CoachOptionsAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private val coachOptionsArray: ArrayList<CoachOptionsModel>,
    val privilegeResponseModel: PrivilegeResponseModel?,
) :

    RecyclerView.Adapter<CoachOptionsAdapter.ViewHolder>(), OnItemClickListener {

    //    val options = context.resources.getStringArray(R.array.arr_coach_options)
    val icons = context.resources.obtainTypedArray(R.array.arr_coach_options_icons)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterCoachOptionsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return coachOptionsArray.size
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = coachOptionsArray[holder.absoluteAdapterPosition]

        holder.text.text = item.coachOption

        if (item.coachOption == context.getString(R.string.bus_location)
            && PreferenceUtils.getString(PREF_NEW_BUS_LOCATION_ADDED_LOGO_DISPLAYED) == "false"
            && privilegeResponseModel?.country?.equals(
                context.getString(R.string.indonesia),
                true
            )==true
        ) {
            val newBusLocationLogo =
                ContextCompat.getDrawable(context, R.drawable.ic_new_bus_location_added)
            holder.text.setCompoundDrawablesWithIntrinsicBounds(
                item.coachOptionIcon,
                null,
                newBusLocationLogo,
                null
            )
            PreferenceUtils.putString(PREF_NEW_BUS_LOCATION_ADDED_LOGO_DISPLAYED, "true")
        } else {
            holder.text.setCompoundDrawablesWithIntrinsicBounds(
                item.coachOptionIcon,
                null,
                null,
                null
            )
        }

//        holder.text.text = item.coachOption
////        holder.text.setCompoundDrawablesWithIntrinsicBounds(icons.getResourceId(position,0),0,0,0)
//        holder.text.setCompoundDrawablesWithIntrinsicBounds(item.coachOptionIcon, null,null,null)

        holder.root.setOnClickListener {
            it.tag = item.coachOption
            onItemClickListener.onClick(holder.itemView, holder.absoluteAdapterPosition)
        }
    }


    class ViewHolder(binding: AdapterCoachOptionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val icon = binding.iconIV
        val text = binding.optionTV
        val root = binding.optionRootCL
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }


}