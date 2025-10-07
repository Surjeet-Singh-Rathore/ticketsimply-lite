package com.bitla.ts.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.databinding.AdapterCountryCodeBinding
import com.bitla.ts.databinding.ChildAmenitiesAdapterBinding
import com.bitla.ts.domain.pojo.Countries
import com.bitla.ts.utils.common.countryFlag
import java.util.Locale


class CountryCodeAdapter(
    private val context: Context,
    private val onItemClickListener: DialogAnyClickListener,
    private var countryList: List<Countries>
) :
    RecyclerView.Adapter<CountryCodeAdapter.ViewHolder>() {

    private var filteredCountries: List<Countries> = countryList


    companion object{
         var TAG: String = CountryCodeAdapter::class.java.simpleName
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterCountryCodeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredCountries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val data  = filteredCountries[position]
        val emoji = countryFlag(data.code)
        val countryText =  emoji + "   " + data.name


        holder.countryNameTV.text = countryText
        holder.countryCodeTV.text = data.phoneCode
        holder.mainLayout.setOnClickListener{
            onItemClickListener.onAnyClickListener(0,filteredCountries[position].phoneCode,position)
        }


    }

    fun filter(query: String) {
        filteredCountries = if (query.isBlank()) {
            countryList
        } else {
            countryList.filter { country ->
                country.name.toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault())) ||
                        country.phoneCode.toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault()))

            }

        }
        notifyDataSetChanged()
    }





    class ViewHolder(binding: AdapterCountryCodeBinding) : RecyclerView.ViewHolder(binding.root) {
        val countryNameTV: TextView = binding.countryNameTV
        val countryCodeTV: TextView = binding.countryCodeTV
        val mainLayout: ConstraintLayout = binding.mainLayout


    }


}