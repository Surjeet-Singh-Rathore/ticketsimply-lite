package com.bitla.ts.presentation.adapter

import android.content.*
import android.os.*
import android.view.*
import androidx.annotation.*
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.databinding.*
import gone
import timber.log.*
import visible

class LanguageChildAdapter(
    private val context: Context,
    private var languageList: List<String>,
    private var announcementSelectedLanguagesList: ArrayList<String> = ArrayList(),
    private var isClearLanguage: Boolean = false
//    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<LanguageChildAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildLanguageAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.announcementTitle.text = languageList[position]
        var selected = 0


        holder.layoutLanguage.setOnClickListener {
            holder.layoutLanguage.tag = context.getString(R.string.language)
//            onItemClickListener.onClickOfItem(holder.announcementTitle.text.toString(), position)

            if (selected == 0) {
                holder.imgCross.visible()
                holder.layoutLanguage.setCardBackgroundColor(context.getColor(com.bitla.tscalender.R.color.slycalendar_defSelectedColor))
                holder.announcementTitle.setTextColor(context.getColor(R.color.white))
                selected++

                announcementSelectedLanguagesList.add(holder.announcementTitle.text.toString())
                Timber.d("langList $announcementSelectedLanguagesList")

            } else {
                holder.imgCross.gone()
                holder.layoutLanguage.setCardBackgroundColor(context.getColor(R.color.grey_four_tint))
                holder.announcementTitle.setTextColor(context.getColor(R.color.dark_gray_pressed))
                selected--
                announcementSelectedLanguagesList.remove(holder.announcementTitle.text.toString())
                Timber.d("langList $announcementSelectedLanguagesList")
            }
        }
    }

    class ViewHolder(binding: ChildLanguageAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val layoutLanguage = binding.layoutLanguage
        val announcementTitle = binding.announcementTitle
        val imgCross = binding.imgCross
    }
}