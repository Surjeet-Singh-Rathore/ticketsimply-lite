package com.bitla.ts.presentation.view.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.ActivityModifyFareBinding
import com.bitla.ts.databinding.ActivityScanQrBinding
import com.bitla.ts.databinding.ActivitySeatWiseFareBinding
import com.bitla.ts.databinding.DialogProgressBarBinding
import com.bitla.ts.domain.pojo.destination_pair.SearchModel
import com.bitla.ts.domain.pojo.my_bookings.response.Filter
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONObject
import toast


class ModifyFareActivity : BaseActivity(){

    private var busType: String = ""
    private lateinit var binding: ActivityModifyFareBinding

    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = ActivityModifyFareBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getStringExtra(getString(R.string.bus_type)) != null) {
            busType = intent.getStringExtra(getString(R.string.bus_type))?:""
        }


//        binding.updateRatecardToolbar.textHeaderTitle.text = "$originName - $destinationName"
        binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.modify_fare)
        binding.updateRatecardToolbar.headerTitleDesc.text = busType

        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()

        }

    }


    override fun isInternetOnCallApisAndInitUI() {


    }





    override fun onDestroy() {
        super.onDestroy()
//        PreferenceUtils.removeKey("seatwiseFare")
        PreferenceUtils.removeKey("isEditSeatWise")
        PreferenceUtils.removeKey("PERSEAT")
        PreferenceUtils.removeKey("fromBusDetails")
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_resId))
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_origin))
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_destination))
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_originId))
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_destinationId))
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_travelDate))
        PreferenceUtils.removeKey(this.getString(R.string.updateRateCard_busType))
    }

}