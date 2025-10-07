package com.bitla.ts.domain.pojo.announcement_model

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ReasionTypeItemBinding

class ReasonTypeTitleAdapter(
    private val context: Context,
    private var reasionTypeTitleList: ArrayList<String>,
    private val onItemClickListener: OnItemClickListener,

    ) : RecyclerView.Adapter<ReasonTypeTitleAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val binding = ReasionTypeItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        holder.tvReasionTypeTitle.text = reasionTypeTitleList[position]

        if (reasionTypeTitleList[position] == "Cancelled") {
            holder.tvReasionTypeTitle.setTextColor(context.getColor(R.color.colorRed))

        }
        holder.containerReasonType.setOnClickListener {
//            context.toast(reasionTypeTitleList[position])
            onItemClickListener.onClickOfItem(reasionTypeTitleList[position], position)

        }
    }

    override fun getItemCount(): Int {
        return reasionTypeTitleList.size
    }

    class UserViewHolder(binding: ReasionTypeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvReasionTypeTitle = binding.tvReasionTypeTitle
        val containerReasonType = binding.containerReasonType
    }
}

