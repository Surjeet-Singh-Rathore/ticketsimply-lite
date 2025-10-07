package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildMultistationFareDetailsBinding
import com.bitla.ts.domain.pojo.update_rate_card.manage_fare_multistaion.request.FareDetailsRequest
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.FareDetail
import com.bitla.ts.utils.common.maxDigitPreventAfterDecimal
import setMaxLength

class MultiStationFareDetailsAdapter(
    private val context: Context,
    private var multistationFareDetailsRequestList: List<FareDetail>,
    private val isEmptyListener: EditTextEmptyListener
) : RecyclerView.Adapter<MultiStationFareDetailsAdapter.ViewHolder>() {
    class ViewHolder(binding: ChildMultistationFareDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvHeading = binding.tvSingleUperBirth
        val etFare = binding.etSingleUperBirth
        val tvFare = binding.lSingleUperBirth
    }

    var isEmpty: Boolean = false
    var hashMap = HashMap<Int, FareDetailsRequest>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ChildMultistationFareDetailsBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return multistationFareDetailsRequestList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.etFare.setText(multistationFareDetailsRequestList[position].fare.toString())
        val seatType = multistationFareDetailsRequestList[position].seatType
        holder.tvHeading.text = seatType
        holder.tvFare.hint = seatType
//        holder.etFare.setMaxLength(7)
        maxDigitPreventAfterDecimal(holder.etFare)


        if (holder.etFare.text.toString().isNullOrEmpty().not()) {
            isEmptyListener.isEmpty(false)
        }
        val tempFareDetail1 = FareDetailsRequest()
        tempFareDetail1.fare = holder.etFare.text.toString()
        tempFareDetail1.seat_type = null
        tempFareDetail1.seat_type_id = multistationFareDetailsRequestList[position].id
        hashMap[position] = tempFareDetail1
        isEmptyListener.getRvData(position, tempFareDetail1)
        holder.etFare.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int,
            ) {
                if (s.toString().startsWith(".")) {
                    holder.etFare.setText("0" + s.toString())
                    holder.etFare.setSelection(holder.etFare.length())
                }
                if (s.isNullOrEmpty() || s.last().toString().equals(".")) {
                    isEmpty = true
                } else
                    isEmpty = false
                isEmptyListener.isEmpty(isEmpty)
                var tempFareDetail = FareDetailsRequest()
                tempFareDetail.fare = s.toString()
                tempFareDetail.seat_type = null
                tempFareDetail.seat_type_id = multistationFareDetailsRequestList[position].id
                hashMap.put(position, tempFareDetail)
                isEmptyListener.getRvData(position, tempFareDetail)
                holder.etFare.setMaxLength(7)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        //  close keyboard
        holder.etFare.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_NEXT) {
                if (position==multistationFareDetailsRequestList.size-1){
                    val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
            false
        }
    }

    interface EditTextEmptyListener {
        fun isEmpty(isEmpty: Boolean)
        fun getRvData(position: Int, fareDetailsRequest: FareDetailsRequest)
    }
}