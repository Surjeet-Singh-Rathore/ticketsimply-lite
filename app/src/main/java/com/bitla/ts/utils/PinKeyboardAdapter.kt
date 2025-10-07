package com.bitla.ts.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.PinAuthCustomKeyboardBinding

class PinKeyboardAdapter(private val onKeyPressed: (Int) -> Unit) :
    RecyclerView.Adapter<PinKeyboardAdapter.KeyViewHolder>() {

    private val keys = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyViewHolder {
        val binding = PinAuthCustomKeyboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KeyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KeyViewHolder, position: Int) {
        holder.bind(keys[position])
    }

    override fun getItemCount() = keys.size

    inner class KeyViewHolder(private val binding: PinAuthCustomKeyboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(key: Int) {
            binding.tvKey.text = key.toString()
            setupTouchEffect(binding.root) { onKeyPressed(key) }
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun setupTouchEffect(view: View, onClick: () -> Unit) {
            view.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.setBackgroundColor(ContextCompat.getColor(v.context, R.color.colorPrimary))
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.setBackgroundColor(ContextCompat.getColor(v.context, android.R.color.transparent))
                        onClick()
                    }
                }
                true
            }
        }
    }
}