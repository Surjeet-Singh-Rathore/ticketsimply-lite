package com.bitla.ts.presentation.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemCheckedMultipledataListner
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildScanChangeStatusBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import gone
import timber.log.Timber
import toast
import visible
import java.util.*

class ScanDialogStatusAdapter(
    private val context: Context,
    val privilegeResponseModel: PrivilegeResponseModel?,
    private var searchList: MutableList<com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail?>,
    private val onItemCheckedMultipledataListner: OnItemCheckedMultipledataListner,
    private val onItemClickListener: OnItemClickListener

) :
    RecyclerView.Adapter<ScanDialogStatusAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildScanChangeStatusBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel: com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail? =
            searchList[position]
        val seat = arrayListOf<String>()
        var checked = false
        var tempratureText = holder.passseatTemp.text.toString()

//        loginUser = PreferenceUtils.getObject<LoginModel>(PREF_LOGGED_IN_USER)!!


        val status = searchModel!!.boardingStatus?.lowercase(Locale.getDefault())
        Timber.d("status-status: ${status}")

        if (privilegeResponseModel != null) {

            privilegeResponseModel?.let {
                if (it.allowToCapturePassAndCrewTemp) {
                    holder.passseatTemp.visible()
                } else {
                    holder.passseatTemp.gone()
                }
            }
        } else {
            context.toast(context.getString(R.string.server_error))
        }
        holder.passName.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                checked = isChecked
                onItemCheckedMultipledataListner.onItemChecked(
                    isChecked,
                    holder.passName,
                    searchModel.seatNumber,
                    searchModel.name!!,
                    tempratureText.toString(),
                    position
                )
                seat.add(searchModel.seatNumber)

            } else {
                checked = isChecked
                onItemCheckedMultipledataListner.onItemChecked(
                    isChecked,
                    holder.passName,
                    searchModel.seatNumber,
                    searchModel.name!!,
                    tempratureText.toString(),
                    position
                )
                seat.add(searchModel.seatNumber)

            }
            onItemClickListener.onClickOfItem(searchModel.seatNumber.toString(), position)
        }




        holder.passName.text = searchModel!!.name
        holder.passseatNumber.text = searchModel!!.seatNumber




        holder.passseatTemp.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable) {


                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    if (!s.toString().isNullOrEmpty()) {
                        holder.passName.isChecked = true
                        checked = true
                        tempratureText = s.toString()
                        onItemCheckedMultipledataListner.onItemChecked(
                            checked,
                            holder.passName,
                            searchModel.seatNumber,
                            searchModel.name!!,
                            tempratureText,
                            position
                        )
                    } else {
                        holder.passName.isChecked = false
                        checked = false
                        tempratureText = s.toString()
                        onItemCheckedMultipledataListner.onItemChecked(
                            checked,
                            holder.passName,
                            searchModel.seatNumber,
                            searchModel.name!!,
                            tempratureText,
                            position
                        )


                    }
                }
            })
    }


    class ViewHolder(binding: ChildScanChangeStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val passName = binding.passengerName
        val passseatNumber = binding.seatNumber
        val passseatTemp = binding.etPassTemp

    }
}