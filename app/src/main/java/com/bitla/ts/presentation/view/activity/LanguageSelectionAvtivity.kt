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
import com.bitla.ts.presentation.adapter.MyContextWrapper
import com.bitla.ts.utils.application.LocaleManager.setNewLocale
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.constants.DEVICE_LANG_LOG_FILENAME
import com.bitla.ts.utils.constants.LANGUAGE_OPTION
import com.bitla.ts.utils.constants.LANGUAGE_SELECTED
import com.bitla.ts.utils.constants.LanguageOption
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import timber.log.Timber
import toast
import java.util.*


class LanguageSelectionAvtivity : BaseActivity() {

    private lateinit var binding: ActivityLanguageSelectionAvtivityBinding
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
        binding = ActivityLanguageSelectionAvtivityBinding.inflate(layoutInflater)
//        binding.simpleToolbar.toolbarHeaderText.setText(R.string.action_settings)


        val view = binding.root
        setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        binding.layoutToolbar.tvCurrentHeader.text = getString(R.string.language)
        binding.layoutToolbar.imgBack.setOnClickListener {
            super.onBackPressed()
        }
        if (PreferenceUtils.getlang() == "en") {
            binding.english.isChecked = true
        } else if (PreferenceUtils.getlang() == "es") {
            binding.spanish.isChecked = true
        } else if (PreferenceUtils.getlang() == "km") {
            binding.cambodian.isChecked = true
        } else if (PreferenceUtils.getlang() == "vi") {
            binding.vietnamese.isChecked = true
        } else {
            binding.indonesian.isChecked = true
        }

        binding.english.setOnClickListener {
            PreferenceUtils.setlang("en")
            toast("Language Selected: ENGLISH")
            changeLanguage(PreferenceUtils.getlang())
            writeOnFile(getSystemDetail("en"), DEVICE_LANG_LOG_FILENAME, true)
            startActivity(Intent(this, SplashScreen::class.java))
            finish()
        }

        binding.indonesian.setOnClickListener {
            PreferenceUtils.setlang("id")
            toast("Bahasa yang Dipilih: INDONESIAN")
            changeLanguage(PreferenceUtils.getlang())
            writeOnFile(getSystemDetail("id"), DEVICE_LANG_LOG_FILENAME, true)
            startActivity(Intent(this, SplashScreen::class.java))
            finish()
        }

        binding.cambodian.setOnClickListener {
            PreferenceUtils.setlang("km")
            toast("Language Changed: Comboadian")
            changeLanguage(PreferenceUtils.getlang())
            writeOnFile(getSystemDetail("km"), DEVICE_LANG_LOG_FILENAME, true)
            startActivity(Intent(this, SplashScreen::class.java))
            finish()
        }

        binding.spanish.setOnClickListener {
            PreferenceUtils.setlang("es")
            toast("Idioma seleccionado: SPANISH")
            changeLanguage(PreferenceUtils.getlang())
            writeOnFile(getSystemDetail("es"), DEVICE_LANG_LOG_FILENAME, true)
            startActivity(Intent(this, SplashScreen::class.java))
            finish()


            binding.btnChangeLanguage.setOnClickListener {
                changeLanguage(PreferenceUtils.getlang())
                startActivity(Intent(this, SplashScreen::class.java))
                finish()
            }
        }

        binding.vietnamese.setOnClickListener {
            PreferenceUtils.setlang("vi")
            toast("Ngôn ngữ đã chọn: VIETNAMESE")
            changeLanguage(PreferenceUtils.getlang())
            writeOnFile(getSystemDetail("vi"), DEVICE_LANG_LOG_FILENAME, true)
            startActivity(Intent(this, SplashScreen::class.java))
            finish()
        }

        firebaseLogEvent(
        this,
        LANGUAGE_OPTION,
        PreferenceUtils.getLogin().userName,
        PreferenceUtils.getLogin().travels_name,
        PreferenceUtils.getLogin().role,
        LANGUAGE_OPTION,
        LanguageOption.LANGUAGE_SELECTION
        )
    }

    override fun attachBaseContext(newBase: Context?) {
        val newsBase = newBase!!
        val lang: String = PreferenceUtils.getlang()
        super.attachBaseContext(MyContextWrapper.wrap(newsBase, lang))
    }

    private fun changeLanguage(language: String) {

        firebaseLogEvent(
        this,
        LANGUAGE_SELECTED,
        PreferenceUtils.getLogin().userName,
        PreferenceUtils.getLogin().travels_name,
        PreferenceUtils.getLogin().role,
        LANGUAGE_SELECTED,
        language
        )

        setNewLocale(language)
//        if (language.equals(SyncStateContract.Constants.ISHINDILANGUAGE, ignoreCase = true)) {
//            setNewLocale(language)
//        } else {
//            setNewLocale(language)
//        }
    }

    private fun setNewLocale(language: String?) {
        setNewLocale(applicationContext, language!!)
    }

    private fun writeOnFile(data: String, logFileName: String, isPrivateMode: Boolean) {
        if (!fileList.contains(logFileName)) {
            //  logFileName.removeSuffix(".txt")
            fileList.add(logFileName)
            PreferenceUtils.putLogFileNames(fileList)
        }
        try {
            val fileOutputStream =
                TsApplication.getAppContext().openFileOutput(
                    logFileName,
                    if (isPrivateMode) Context.MODE_PRIVATE else Context.MODE_APPEND
                )
            fileOutputStream.write(data.toByteArray())

        } catch (e: Exception) {
            Timber.d("serverException ${e.message}")
        }
    }

    @SuppressLint("HardwareIds")
    private fun getSystemDetail(language: String): String {
        return "$language-true"
    }
}