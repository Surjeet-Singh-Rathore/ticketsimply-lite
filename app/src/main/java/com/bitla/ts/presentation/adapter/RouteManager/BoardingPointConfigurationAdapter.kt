package com.bitla.ts.presentation.adapter.RouteManager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.provider.CalendarContract.Colors
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.databinding.AdapterConfigureBpDpBinding
import com.bitla.ts.domain.pojo.update_route.ViaCitiesData
import com.bitla.ts.presentation.viewModel.RouteManagerViewModel
import gone
import visible


class BoardingPointConfigurationAdapter(
    private val context: Context,
    //private var list: ArrayList<String>
    private val listener: DialogAnyClickListener,
    var list: ArrayList<ViaCitiesData>) :
    RecyclerView.Adapter<BoardingPointConfigurationAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterConfigureBpDpBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.addIV.setOnClickListener{
            listener.onAnyClickListener(1,"addIV",position)
        }
        holder.bind.editIV.setOnClickListener{
            listener.onAnyClickListener(1,"editIV",position)
        }

        if(list[position].stageList.size < 1){
            holder.bind.editIV.gone()
        }else{
            holder.bind.editIV.visible()
        }


        holder.cityTV.text = list[position].name
        holder.bind.stagesTV.text = list[position].stageList.size.toString() + " Stages"
        if(list[position].stageList.size == 0){
            holder.bind.stagesTV.setTextColor(getColor(context, R.color.stages_red))
        }else{
            holder.bind.stagesTV.setTextColor(getColor(context, R.color.black_grey))
        }

    }



    class ViewHolder(binding: AdapterConfigureBpDpBinding) :
        RecyclerView.ViewHolder(binding.root) {

            val addIV = binding.addIV
            val cityTV = binding.cityTV
            val bind = binding

    }
}