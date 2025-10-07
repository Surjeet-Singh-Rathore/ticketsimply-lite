package com.bitla.ts.presentation.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildChartListAdapterBinding
import com.bitla.ts.domain.pojo.view_reservation.ChartType
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import java.util.Locale

class ChartListAdapter(
    private val context: Context,
    private var searchList: List<ChartType>,
    private val chartClick:((chartTypeId:Int )-> Unit)
) :

    RecyclerView.Adapter<ChartListAdapter.ViewHolder>() {
    val type = PreferenceUtils.setPreference("chartlist", searchList)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildChartListAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
//    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel: ChartType = searchList.get(position)

        if (searchModel.label.isNotEmpty()) {
            holder.tvlable.text = searchModel.label.replaceFirstChar {
                if (it.isLowerCase())
                    it.titlecase(Locale.getDefault())
                else it.toString()
            }
        } else {
            holder.tvlable.text = searchModel.label
        }

        if (searchList.isNotEmpty() && searchList.size >= 5) {
            when (position) {
                0 -> {
                    holder.imgstage.setImageResource(R.drawable.current_location)
                }

                1 -> {
                    holder.imgstage.setImageResource(R.drawable.ic_walking_man)
                }

                2 -> {
                    holder.imgstage.setImageResource(R.drawable.ic_cancel_shape)
                    holder.imgstage.imageTintList =
                        ColorStateList.valueOf((context.resources.getColor(R.color.white)))
                }

                3 -> {
                    holder.imgstage.setImageResource(R.drawable.ic_legend)
                }

                4 -> {
                    holder.imgstage.setImageResource(R.drawable.ic_location)
                }

                5 -> {
                    holder.imgstage.setImageResource(R.drawable.ic_branch)
                }
            }
        } else {
            when (position) {
                0 -> {
                    holder.imgstage.setImageResource(R.drawable.current_location)
                }

                1 -> {
                    holder.imgstage.setImageResource(R.drawable.ic_location)
                }

            }
        }

        val chartTypeSelected = searchModel.isSelected

        if (chartTypeSelected) {
            holder.tvlable.setTextColor(getColor(context, R.color.colorPrimary))
            holder.imgstage.imageTintList =
                ColorStateList.valueOf((context.resources.getColor(R.color.colorButton)))

        }
        holder.tvlayout.setOnClickListener {
            holder.tvlayout.tag = "CHARTTYPE"
            chartClick.invoke(searchModel.id)
        }
    }


    class ViewHolder(binding: ChildChartListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvlable = binding.tvStageLable
        val tvlayout = binding.layoutStage
        val imgstage = binding.imgStage

    }

}