package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildCrewSelectedItemBinding
import com.bitla.ts.domain.pojo.all_coach.response.AllCoach

class CrewSelectAllCoachItemAdapter(
    private val context: Context,
    private var searchList: MutableList<AllCoach>,
    private val onItemClickListener: OnItemClickListener

) :
    RecyclerView.Adapter<CrewSelectAllCoachItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildCrewSelectedItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val searchAllcoachModel: AllCoach = searchList[position]
        holder.currentServiceText.text = searchAllcoachModel.name

        holder.mainLayout.setOnClickListener {
            onItemClickListener.onClickOfItem(holder.currentServiceText.text.toString(), position)
        }
    }

    class ViewHolder(binding: ChildCrewSelectedItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val currentServiceText = binding.tvName
        val mainLayout = binding.mainLayout
    }
}