package com.bitla.ts.presentation.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.TsApplication
import com.bitla.ts.databinding.ActivityLanguageSelectionAvtivityBinding
import com.bitla.ts.databinding.ActivityRapidBookingSelectionBinding
import com.bitla.ts.presentation.adapter.MyContextWrapper
import com.bitla.ts.utils.application.LocaleManager.setNewLocale
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.constants.DEVICE_LANG_LOG_FILENAME
import com.bitla.ts.utils.constants.LANGUAGE_OPTION
import com.bitla.ts.utils.constants.LANGUAGE_SELECTED
import com.bitla.ts.utils.constants.RAPID_BOOKING_SELECTION
import com.bitla.ts.utils.constants.RAPID_TYPE_DEFAULT
import com.bitla.ts.utils.constants.RAPID_TYPE_HIDE
import com.bitla.ts.utils.constants.RAPID_TYPE_OPTIONAL
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import timber.log.Timber
import toast
import java.util.*


class RapidBookingSelectionActivity : BaseActivity() {

    private var selectedType: Int = 0
    private lateinit var binding: ActivityRapidBookingSelectionBinding
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }


    override fun initUI() {
        binding = ActivityRapidBookingSelectionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        binding.layoutToolbar.tvCurrentHeader.text = getString(R.string.rapid_booking)
        binding.layoutToolbar.imgBack.setOnClickListener {
            super.onBackPressed()

        }
        selectedType = PreferenceUtils.getRapidBookingType()


        when(selectedType){
            RAPID_TYPE_DEFAULT -> {
                binding.defaultRB.isChecked = true
            }
            RAPID_TYPE_OPTIONAL -> {
                binding.optionalRB.isChecked = true
            }
            RAPID_TYPE_HIDE -> {
                binding.hideRB.isChecked = true
            }
        }

        binding.defaultRB.setOnClickListener {
            PreferenceUtils.putRapidBookingType(RAPID_TYPE_DEFAULT)
            gotoSettingsActivity()

        }

        binding.optionalRB.setOnClickListener {
            PreferenceUtils.putRapidBookingType(RAPID_TYPE_OPTIONAL)
            gotoSettingsActivity()

        }

        binding.hideRB.setOnClickListener {
            PreferenceUtils.putRapidBookingType(RAPID_TYPE_HIDE)
            gotoSettingsActivity()


        }

        firebaseLogEvent(
            this,
            RAPID_BOOKING_SELECTION,
            PreferenceUtils.getLogin().userName,
            PreferenceUtils.getLogin().travels_name,
            PreferenceUtils.getLogin().role,
            RAPID_BOOKING_SELECTION,
            "rapid booking selection"
        )
    }

    private fun gotoSettingsActivity() {
        val intent = Intent(this,SettingsActivity::class.java)
        startActivity(intent)
        finish()
    }


}