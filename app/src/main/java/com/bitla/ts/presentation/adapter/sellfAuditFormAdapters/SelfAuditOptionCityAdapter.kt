package com.bitla.ts.presentation.adapter.sellfAuditFormAdapters


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.AdapterSelfAuditOptionCityBinding
import com.bitla.ts.domain.pojo.self_audit_question.response.BoardingData
import com.bitla.ts.domain.pojo.self_audit_question.response.Option
import gone
import visible


class SelfAuditOptionCityAdapter(
    private val context: Context,
    private val boardingPointsData: List<BoardingData>,
    private val options: List<Option>,
    private val onItemClick:((optionId: String, stageId: String)->Unit)

) :
    RecyclerView.Adapter<SelfAuditOptionCityAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterSelfAuditOptionCityBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return boardingPointsData.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem= boardingPointsData.get(position)

        holder.questionTextView.text=currentItem.city_name

        val adapter = SelfAuditStageAdapter(
            context,
            currentItem.boarding_points,
            options
        ){optionId: String, stageId: String ->
            onItemClick.invoke(
                optionId, stageId
            )

        }

        holder.optionRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        holder.optionRecyclerView.adapter = adapter


        holder.dropdownExpand.setOnClickListener{
            holder.dropdownExpand.gone()
            holder.dropdownCollapse.visible()
            holder.optionRecyclerView.visible()

        }

        holder.dropdownCollapse.setOnClickListener{

            holder.dropdownCollapse.gone()
            holder.dropdownExpand.visible()
            holder.optionRecyclerView.gone()

        }
    }

    class ViewHolder(binding: AdapterSelfAuditOptionCityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val questionTextView=binding.questionCityTV
        val optionRecyclerView=binding.optionsRV
        val dropdownExpand=binding.dropdownExpand
        val dropdownCollapse=binding.dropdownCollapse
    }


}