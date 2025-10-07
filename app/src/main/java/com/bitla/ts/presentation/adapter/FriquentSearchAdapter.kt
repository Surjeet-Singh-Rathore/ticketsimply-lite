package com.bitla.ts.presentation.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.databinding.ChildRoundOutlineBinding
import com.bitla.ts.domain.pojo.destination_pair.SearchModel

class FriquentSearchAdapter(
    private val context: Context,
    private val onItemPassData: OnItemPassData,
    private val selected: String,
    private val fromOrigin: Boolean,
    private var searchList: MutableList<SearchModel>
) :
    RecyclerView.Adapter<FriquentSearchAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildRoundOutlineBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel: SearchModel = searchList.get(position)
        holder.titleText.text = "${searchModel.name}"
        if (searchModel.id.toString().contains(":")) {
            var temp = searchModel.id.toString().split(":")
            if (temp[1] == selected) {
                holder.titleText.backgroundTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.colorPrimary
                    )
                )
                holder.titleText.setTextColor(
                    context.resources.getColor(
                        R.color.white
                    )
                )


            }
        } else if (selected == searchModel.id.toString()) {
            holder.titleText.backgroundTintList = ColorStateList.valueOf(
                context.resources.getColor(
                    R.color.colorPrimary
                )
            )
            holder.titleText.setTextColor(
                context.resources.getColor(
                    R.color.white
                )
            )
        }

//
        holder.mainLayout.setOnClickListener {
            holder.mainLayout.tag = fromOrigin
            onItemPassData.onItemData(
                holder.mainLayout,
                searchModel.name.toString(),
                searchModel.id.toString()
            )
        }
    }

    class ViewHolder(binding: ChildRoundOutlineBinding) : RecyclerView.ViewHolder(binding.root) {
        val titleText = binding.cityText
        val mainLayout = binding.mainLayout
    }
}