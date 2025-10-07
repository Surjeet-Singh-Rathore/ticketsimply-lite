package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.SeatLegendsItemBinding
import timber.log.Timber

class SeatLegendsAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var seatTitleName: List<String> = ArrayList()
    private var seatColorLiatOne: List<String> = ArrayList()
    private var seatColorLiatTwo: List<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = SeatLegendsItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return seatTitleName.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        Timber.d("SeatsLegendAdapter ${seatItem[position]}")
        when (holder) {
            is ViewHolder -> {
                val seatLegendsName: String = seatTitleName[position]
                val seatLegendsOne: String = seatColorLiatOne[position]
                val seatLegendsTwo: String = seatColorLiatTwo[position]
                with(holder) {
                    bind(seatLegendsName, seatLegendsOne, seatLegendsTwo)
                }
            }
        }


    }

    fun setList(
        seattitleList: List<String>,
        seatColorListOne: List<String>,
        seatColorListTwo: List<String>
    ) {
        seatTitleName = seattitleList
        seatColorLiatOne = seatColorListOne
        seatColorLiatTwo = seatColorListTwo
    }

    class ViewHolder(binding: SeatLegendsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvSeatsTitle = binding.tvSeatsTitle
        private val btnSeatColorOne = binding.btnSeatColorOne
        private val btnSeatColorTwo = binding.btnSeatColorTwo

        @SuppressLint("Range")
        fun bind(name: String, colorOne: String, colorTwo: String) {
            tvSeatsTitle.text = name
            Timber.d("unknownColor: ${colorOne}, ${colorTwo}")
            try {
                if (colorOne.contains(" ")) {
                    val one = colorOne.replace(" ", "")
                    btnSeatColorOne.setBackgroundColor(Color.parseColor(one))
                } else {
                    btnSeatColorOne.setBackgroundColor(Color.parseColor(colorOne))
                }
            } catch (e: Exception) {
                Timber.d("e:: ${e}")
            }

            try {
                if (colorOne.contains(" ")) {
                    val two = colorTwo.replace(" ", "")
                    btnSeatColorTwo.setBackgroundColor(Color.parseColor(two))
                } else {
                    btnSeatColorOne.setBackgroundColor(Color.parseColor(colorTwo))
                }
            } catch (e: Exception) {
                Timber.d("e:: ${e.message}")
            }
        }
    }
}