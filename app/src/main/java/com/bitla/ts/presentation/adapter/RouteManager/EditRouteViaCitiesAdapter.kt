
package com.bitla.ts.presentation.adapter.RouteManager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.DEFAULT_HOURS
import com.bitla.ts.data.DEFAULT_MINUTES
import com.bitla.ts.databinding.AdapterViaCitiesBinding
import com.bitla.ts.domain.pojo.update_route.ViaCitiesData
import com.bitla.ts.utils.common.openHoursMinsPickerDialog
import gone
import toast
import visible
import java.util.Collections


class EditRouteViaCitiesAdapter(
    private val context: Context,
    private var viaCitiesList: ArrayList<ViaCitiesData>,
    val requireActivity: FragmentActivity
) :
    RecyclerView.Adapter<EditRouteViaCitiesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterViaCitiesBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return viaCitiesList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.cityName.text = viaCitiesList[position].name
      /*  if ( viaCitiesList.size == 2){
            if (position == 0 ) {
                holder.noCitiesAdded.visible()
            }
        }*/
            holder.hoursET.setText(viaCitiesList[position].hh)
            holder.minutesET.setText(viaCitiesList[position].mm)

            holder.dayET.setText(viaCitiesList[position].day)
            holder.destinationCB.isChecked = viaCitiesList[position].isDestination
            holder.originCB.isChecked = viaCitiesList[position].isOrigin



        if (position == 0 || position == viaCitiesList.size - 1){
            holder.cityName.setTypeface(null, Typeface.BOLD)
            holder.boardingBusIcon.setImageResource(R.drawable.ic_boarding_bus)
            val newTintColor = context.resources.getColor(R.color.lightColor)
            holder.deleteIV.setColorFilter(newTintColor, PorterDuff.Mode.SRC_IN)
            holder.originCB.gone()
            holder.destinationCB.gone()
            if(viaCitiesList[holder.adapterPosition].time.isNotBlank()){
                holder.hoursET.setText(viaCitiesList[holder.adapterPosition].time.substringBefore(":"))
                holder.minutesET.setText(viaCitiesList[holder.adapterPosition].time.substringAfter(":"))
            }

        }else{
            holder.cityName.setTypeface(null, Typeface.NORMAL)
            holder.boardingBusIcon.setImageResource(R.drawable.ic_rearrange)
            val newTintColor = context.resources.getColor(R.color.light_gray)
            holder.deleteIV.setColorFilter(newTintColor, PorterDuff.Mode.SRC_IN)
            holder.originCB.visible()
            holder.destinationCB.visible()
        }


      //  holder.hoursET.setText(viaCitiesList[position].hh)
        holder.hoursET.setOnClickListener {
            openHoursMinsPickerDialog(requireActivity,context, DEFAULT_HOURS,holder.hoursET)
        }
        holder.minutesET.setOnClickListener {
            openHoursMinsPickerDialog(requireActivity,context, DEFAULT_MINUTES,holder.minutesET)
        }
        holder.dayET.setOnClickListener {
            openHoursMinsPickerDialog(requireActivity,context,5,holder.dayET,true)
        }

        holder.minutesET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
               // viaCitiesList[position].time = holder.hoursET.text.toString() + ":" +holder.minutesET.text.toString()
                viaCitiesList[holder.adapterPosition].mm = holder.minutesET.text.toString()
            }
        })

        holder.dayET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                viaCitiesList[holder.adapterPosition].day = holder.dayET.text.toString()

            }
        })
        holder.hoursET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                viaCitiesList[holder.adapterPosition].hh = holder.hoursET.text.toString()

            }
        })

        holder.originCB.setOnClickListener {
            if(holder.originCB.isChecked){
                viaCitiesList[position].isOrigin = true
            }else{
                viaCitiesList[position].isOrigin = false

            }
        }
        holder.destinationCB.setOnClickListener {
            if(holder.destinationCB.isChecked){
                viaCitiesList[position].isDestination = true
            }else{
                viaCitiesList[position].isDestination = false
            }
        }


    }

    class ViewHolder(binding: AdapterViaCitiesBinding) :
        RecyclerView.ViewHolder(binding.root) {
            val deleteIV = binding.deleteIV
            val cityName = binding.titleTV
            val noCitiesAdded = binding.noViaCitiesAddedTV
            val boardingBusIcon = binding.busIV
            val hoursET = binding.hoursET
            val minutesET = binding.minutesET
            val dayET = binding.dayET
            val destinationCB = binding.destCB
            val originCB = binding.originCB

    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(viaCitiesList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(viaCitiesList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        val item = viaCitiesList.removeAt(fromPosition)
        viaCitiesList.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
    }
}