package com.bitla.ts.presentation.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityPrintTypeBinding
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.constants.LANGUAGE_OPTION
import com.bitla.ts.utils.constants.PRINTER_SELECTION
import com.bitla.ts.utils.sharedPref.*
import java.util.*


class PrinterTypeActivity : BaseActivity() {

    private lateinit var binding: ActivityPrintTypeBinding
    lateinit var myLangPref: PreferenceUtils
    lateinit var context: Context
    private var fileList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }


    override fun initUI() {

        val loginDetails=PreferenceUtils.getLogin()
        binding = ActivityPrintTypeBinding.inflate(layoutInflater)
//        binding.simpleToolbar.toolbarHeaderText.setText(R.string.action_settings)

        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        binding.layoutToolbar.tvCurrentHeader.text = getString(R.string.print_selection)
        binding.layoutToolbar.imgBack.setOnClickListener {
            super.onBackPressed()
        }

        if (PreferenceUtils.getPrintingType() == PRINT_TYPE_BLUETOOTH) {
            binding.bluetoothRB.isChecked = true
        } else if (PreferenceUtils.getPrintingType() == PRINT_TYPE_HARVARD) {
            binding.harvardRB.isChecked = true
        } else if(PreferenceUtils.getPrintingType() == PRINT_TYPE_PINELAB){
            binding.pineLabRB.isChecked = true
        }
        else {
            binding.sunmiRB.isChecked = true
        }

        binding.bluetoothRB.setOnClickListener {
            PreferenceUtils.putPrintingType(PRINT_TYPE_BLUETOOTH)
            super.onBackPressed()
            firebaseLogEvent(
                this,
                PRINTER_SELECTION,
                loginDetails.userName,
                loginDetails.travels_name,
                loginDetails.role,
                PRINTER_SELECTION,
                "Print_BT"
            )
        }

        binding.sunmiRB.setOnClickListener {
            PreferenceUtils.putPrintingType(PRINT_TYPE_SUNMI)
            super.onBackPressed()


            firebaseLogEvent(
                this,
                PRINTER_SELECTION,
                loginDetails.userName,
                loginDetails.travels_name,
                loginDetails.role,
                PRINTER_SELECTION,
                "Print_Sunmi"
            )

        }
        binding.pineLabRB.setOnClickListener {
            PreferenceUtils.putPrintingType(PRINT_TYPE_PINELAB)
            super.onBackPressed()
            firebaseLogEvent(
                this,
                PRINTER_SELECTION,
                loginDetails.userName,
                loginDetails.travels_name,
                loginDetails.role,
                PRINTER_SELECTION,
                "Print_PineLab"
            )

        }

        binding.harvardRB.setOnClickListener {
            PreferenceUtils.putPrintingType(PRINT_TYPE_HARVARD)
            super.onBackPressed()
            firebaseLogEvent(
                this,
                PRINTER_SELECTION,
                loginDetails.userName,
                loginDetails.travels_name,
                loginDetails.role,
                PRINTER_SELECTION,
                "Print_Harvard"
            )
        }

        firebaseLogEvent(
            this,
            LANGUAGE_OPTION,
            loginDetails.userName,
            loginDetails.travels_name,
            loginDetails.role,
            LANGUAGE_OPTION,
            "print selection"
        )
    }


}