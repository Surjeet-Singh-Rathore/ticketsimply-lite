package com.bitla.ts.presentation.adapter

import android.app.*
import android.content.*
import android.view.*
import androidx.core.content.*
import androidx.recyclerview.widget.*
import com.bitla.ts.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.view_reservation.*

class CheckingInspectorDetailAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    val passengerList: ArrayList<PassengerDetail>,
    val isCompleted: Boolean?= false,
) :
    RecyclerView.Adapter<CheckingInspectorDetailAdapter.ViewHolder>(), OnItemClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterCheckingInspectorDetailBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return passengerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = passengerList[position]
        holder.seatNumber.text = data.seatNumber
        holder.boardedCB.isChecked = data.boardedStatus

        if(isCompleted!!){
            holder.boardedCB.isEnabled = false

        }

        if (data.sex == "M") {
            selectMale(holder)

        } else {
            selectFemale(holder)

        }

        holder.maleBT.setOnClickListener {
            if(!isCompleted!!){
                onItemClickListener.onClick(holder.maleBT, position)
                selectMale(holder)
            }
        }
        holder.femaleBT.setOnClickListener {
            if(!isCompleted!!){
                onItemClickListener.onClick(holder.femaleBT, position)
                selectFemale(holder)
            }
        }

        holder.boardedCB.setOnClickListener {
            if(!isCompleted!!){
                holder.boardedCB.isChecked = holder.boardedCB.isChecked
                var isCheck = if(holder.boardedCB.isChecked){
                    "YES"
                }else{
                    "NO"
                }
                onItemClickListener.onClickOfItem(isCheck,position)
            }


        }



    }

    private fun selectFemale(holder: ViewHolder) {
        holder.femaleBT.setBackgroundColor(
            ContextCompat.getColor(
                context,
                com.bitla.tscalender.R.color.slycalendar_defSelectedColor
            )
        )
        holder.femaleBT.setTextColor(ContextCompat.getColor(context, R.color.white))

        holder.maleBT.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.white
            )
        )
        holder.maleBT.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    private fun selectMale(holder: ViewHolder) {
        holder.maleBT.setBackgroundColor(
            ContextCompat.getColor(
                context,
                com.bitla.tscalender.R.color.slycalendar_defSelectedColor
            )
        )
        holder.maleBT.setTextColor(ContextCompat.getColor(context, R.color.white))

        holder.femaleBT.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.white
            )
        )
        holder.femaleBT.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    class ViewHolder(binding: AdapterCheckingInspectorDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val seatNumber = binding.seatNumber
        val maleBT = binding.passengerDetailsBtnMale
        val femaleBT = binding.passengerDetailsBtnFemale
        val boardedCB = binding.boardedSwitch
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