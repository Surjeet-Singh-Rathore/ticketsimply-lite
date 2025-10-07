package com.bitla.ts.presentation.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityBlackListNumberBinding
import com.bitla.ts.databinding.ActivityTextSizeBinding
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.constants.DEFAULT_TEXT_SIZE
import com.bitla.ts.utils.constants.LARGE_TEXT_SIZE
import com.bitla.ts.utils.constants.NOTIFICATION_DEFAULT_SOUND
import com.bitla.ts.utils.constants.NOTIFICATION_SILENT
import com.bitla.ts.utils.constants.NOTIFICATION_SYSTEM_SOUND
import com.bitla.ts.utils.constants.SMALL_TEXT_SIZE
import com.bitla.ts.utils.constants.XLARGE_TEXT_SIZE
import com.bitla.ts.utils.sharedPref.PreferenceUtils

class TextSizeActivity : BaseActivity() {
    lateinit var binding: ActivityTextSizeBinding

    override fun initUI() {
        binding = ActivityTextSizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutToolbar.tvCurrentHeader.text = getString(R.string.text_size)
        binding.layoutToolbar.imgBack.setOnClickListener {
            super.onBackPressed()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        when(PreferenceUtils.getTextSize(applicationContext)) {
            SMALL_TEXT_SIZE -> {
                binding.displaySizeSeekbar.progress = 0
            }

            DEFAULT_TEXT_SIZE -> {
                binding.displaySizeSeekbar.progress = 1
            }

            LARGE_TEXT_SIZE -> {
                binding.displaySizeSeekbar.progress = 2
            }

            XLARGE_TEXT_SIZE -> {
                binding.displaySizeSeekbar.progress = 3
            }

            else -> {
                binding.displaySizeSeekbar.progress = 1
            }
        }

        binding.saveButton.setOnClickListener {
            val selectedTextSize = binding.displaySizeSeekbar.progress

            when(selectedTextSize){
                0 -> {PreferenceUtils.setTextSize(applicationContext, SMALL_TEXT_SIZE)}
                1 -> {PreferenceUtils.setTextSize(applicationContext, DEFAULT_TEXT_SIZE)}
                2 -> {PreferenceUtils.setTextSize(applicationContext, LARGE_TEXT_SIZE)}
                3 -> {PreferenceUtils.setTextSize(applicationContext, XLARGE_TEXT_SIZE)}
                else -> {PreferenceUtils.setTextSize(applicationContext, DEFAULT_TEXT_SIZE)}
            }

            startActivity(Intent(this, SplashScreen::class.java))
            finish()
//            restartApp()
        }
    }

    private fun restartApp() {
        startActivity(Intent(this, SplashScreen::class.java))
        finish()
    }




    override fun isInternetOnCallApisAndInitUI() {}
}