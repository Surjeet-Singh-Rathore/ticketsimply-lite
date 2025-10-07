package com.bitla.ts.presentation.view.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityPushNotificationSoundSettingsBinding
import com.bitla.ts.databinding.ActivitySettingsBinding
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.constants.DEVICE_LANG_LOG_FILENAME
import com.bitla.ts.utils.constants.NOTIFICATION_DEFAULT_SOUND
import com.bitla.ts.utils.constants.NOTIFICATION_SILENT
import com.bitla.ts.utils.constants.NOTIFICATION_SYSTEM_SOUND
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import toast

class PushNotificationSoundSettingsActivity :  BaseActivity() {

    private lateinit var binding: ActivityPushNotificationSoundSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    override fun initUI() {
        binding = ActivityPushNotificationSoundSettingsBinding.inflate(layoutInflater)
        binding.layoutToolbar.tvCurrentHeader.setText(R.string.notification_sound)

        binding.layoutToolbar.imgBack.setOnClickListener {
            super.onBackPressed()
        }


        val view = binding.root
        setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        when(PreferenceUtils.getNotificationSoundType(applicationContext)) {
            NOTIFICATION_SILENT -> {
                binding.noSound.isChecked = true
            }

            NOTIFICATION_SYSTEM_SOUND -> {
                binding.systemSound.isChecked = true
            }

            NOTIFICATION_DEFAULT_SOUND -> {
                binding.defaultSound.isChecked = true
            }

            else -> {
                binding.defaultSound.isChecked = true
            }
        }

        binding.noSound.setOnClickListener {
            PreferenceUtils.setNotificationSoundType(applicationContext, NOTIFICATION_SILENT)
            toast("${getString(R.string.notification_sound_selected)} : ${getString(R.string.silent)}")
            startActivity(Intent(this, SplashScreen::class.java))
            finish()
        }

        binding.systemSound.setOnClickListener {
            PreferenceUtils.setNotificationSoundType(applicationContext, NOTIFICATION_SYSTEM_SOUND)
            toast("${getString(R.string.notification_sound_selected)} : ${getString(R.string.system_sound)}")
            startActivity(Intent(this, SplashScreen::class.java))
            finish()
        }

        binding.defaultSound.setOnClickListener {
            PreferenceUtils.setNotificationSoundType(applicationContext, NOTIFICATION_DEFAULT_SOUND)
            toast("${getString(R.string.notification_sound_selected)} : ${getString(R.string.default_sound)}")
            startActivity(Intent(this, SplashScreen::class.java))
            finish()
        }
    }

    override fun isInternetOnCallApisAndInitUI() {

    }
}