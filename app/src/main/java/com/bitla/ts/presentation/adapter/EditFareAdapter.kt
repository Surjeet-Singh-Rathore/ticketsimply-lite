package com.bitla.ts.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildEditFareBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_details_response.SeatDetail
import com.bitla.ts.domain.pojo.update_rate_card.update_rate_card_seatwise.per_seat.request.FareDetailPerSeat
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.maxDigitPreventAfterDecimal
import com.bitla.ts.utils.constants.APPLIED_VALUE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import onChange
import timber.log.Timber

class EditFareAdapter(
    private val context: Context,
    private var editFareSeatDetails: List<SeatDetail>,
    val privilegeResponseModel: PrivilegeResponseModel?,
    private val onItemClickListener: OnItemClickListener,
    private val editTextListener: EditTextListener,
    private val currencyFormat: String,
    private val listener: DialogButtonAnyDataListener
) :
    RecyclerView.Adapter<EditFareAdapter.ViewHolder>() {

    var hashMap = HashMap<Int, FareDetailPerSeat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildEditFareBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return editFareSeatDetails.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var isCorrectFare = true
        var minMaxFareError = ""
        val seatDetail: SeatDetail = editFareSeatDetails[position]
        maxDigitPreventAfterDecimal(holder.etNewFare)

        if (seatDetail.editFare != null)
            holder.etNewFare.setText(
                if (seatDetail.editFare.toString().isEmpty()) {
                    seatDetail.editFare.toString()
                }
                else {
                    seatDetail.editFare.toString()
//                    seatDetail.editFare.toString().toDouble().convert(currencyFormat)
                })
        else
            holder.etNewFare.setText(
                if (seatDetail.baseFareFilter.toString() != null
                    && seatDetail.baseFareFilter.toString().isNotEmpty())
                {
                    seatDetail.baseFareFilter.toString()
                }
                else
                {
                    seatDetail.baseFareFilter.toString().toDouble().convert(currencyFormat)
                })

        holder.etSeatNo.setText(seatDetail.number)

        val tempFareDetail1 = FareDetailPerSeat(
            holder.etNewFare.text.toString(),
            holder.etSeatNo.text.toString()
        )

        hashMap.put(position, tempFareDetail1)

        editTextListener.getRvData(
            position,
            tempFareDetail1,
            isCorrectFare = true,
            minMaxFareError
        )

        seatDetail.editFare = holder.etNewFare.text.toString()

        holder.etNewFare.onChange {

            holder.etNewFare.tag = it

            if (it != ".") {
                if(privilegeResponseModel?.isChileApp == true){

                    minMaxFareError = ""
                    isCorrectFare = true
                    seatDetail.editFare = it

                    val tempFareDetail = FareDetailPerSeat(it, holder.etSeatNo.text.toString())
                    hashMap.put(position, tempFareDetail)

                    editTextListener.getRvData(
                        position = position,
                        fareDetail = tempFareDetail,
                        isCorrectFare = isCorrectFare,
                        minMaxFareError = minMaxFareError
                    )
                    onItemClickListener.onClick(holder.etNewFare, position = position)
                }
                else {
                    if (seatDetail.minFare != null
                        && seatDetail.maxFare != null
                        && seatDetail.baseFareFilter != null
                        && ((seatDetail.minFare == seatDetail.maxFare)
                                && (seatDetail.maxFare == seatDetail.baseFareFilter))
                        && it.isNotEmpty()
                        && it != "null"
                        && it.toDouble() < seatDetail.maxFare!!
                        && it.toDouble() > seatDetail.minFare!!) {
                        minMaxFareError = ""
                        isCorrectFare = true

                        seatDetail.editFare = it
                    }
                    else {
                        try {
                            if (!it.isNullOrEmpty() && it != "null" && seatDetail.minFare != null && (it.toDoubleOrNull()
                                    ?: 0.0) < (seatDetail.minFare ?: 0.0)
                            ) {
                                isCorrectFare = false
                                seatDetail.editFare = seatDetail.baseFareFilter
                                minMaxFareError =
                                    "${context.getString(R.string.validate_min_fare)} ${seatDetail.minFare}"

                            } else if (!it.isNullOrEmpty() && it != "null" && seatDetail.maxFare != null && it.toDouble() > seatDetail.maxFare!!) {
                                isCorrectFare = false
                                seatDetail.editFare = seatDetail.baseFareFilter
                                minMaxFareError =
                                    "${context.getString(R.string.validate_max_fare)} ${seatDetail.maxFare}"
                                Timber.d("fareOnChange-ifElse ${seatDetail.maxFare} == $it = ${seatDetail.minFare}")
                            } else {
                                minMaxFareError = ""
                                isCorrectFare = true
                                seatDetail.editFare = it
                            }
                        }catch (_:Exception){}
                    }

                    val tempFareDetail = FareDetailPerSeat(it, holder.etSeatNo.text.toString())
                    hashMap.put(position, tempFareDetail)
                    editTextListener.getRvData(
                        position = position,
                        fareDetail = tempFareDetail,
                        isCorrectFare = isCorrectFare,
                        minMaxFareError = minMaxFareError
                    )
                    onItemClickListener.onClick(holder.etNewFare, position = position)
                }
            }
            if (position == 0) {
                listener.onDataSend(APPLIED_VALUE, holder.etNewFare.text.toString())
            }
        }


        if (editFareSeatDetails[0].isApplyToAll!!) {
            if (position != 0) {
                holder.etNewFare.setText(editFareSeatDetails[0].updatedFare.toString())
            }
        }
        else {
            if (editFareSeatDetails[0].isEditFareApply == false) {
                if (position != 0) {
                    if (seatDetail.baseFareFilter.toString() != null && seatDetail.baseFareFilter.toString()
                            .isNotEmpty()
                    ) {
                        holder.etNewFare.setText(seatDetail.baseFareFilter.toString())
                    } else {
                        holder.etNewFare.setText(
                            seatDetail.baseFareFilter.toString().toDouble().convert(currencyFormat)
                        )
                    }
                }
            }
        }
    }

    class ViewHolder(binding: ChildEditFareBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val etNewFare = binding.etNewFare
        val etSeatNo = binding.etSeatNo
    }

    interface EditTextListener {
        fun isEmpty(isEmpty: Boolean)
        fun getRvData(
            position: Int,
            fareDetail: FareDetailPerSeat,
            isCorrectFare: Boolean,
            minMaxFareError: String
        )
    }
}