package com.bitla.ts.presentation.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.db.UserViewModel
import com.bitla.ts.presentation.adapter.MyContextWrapper
import com.bitla.ts.presentation.view.dashboard.DashboardNavigateActivity
import com.bitla.ts.utils.application.LocaleManager
import com.bitla.ts.utils.application.LocaleManager.setNewLocale
import com.bitla.ts.utils.constants.DEVICE_LANG_LOG_FILENAME
import com.bitla.ts.utils.sharedPref.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class SplashScreen : BaseActivity() {
    private var fileList = ArrayList<String>()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        writeReadLangDevice()
        userViewModel.getAllUsers()
        getAllUserObserver()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    override fun initUI() {
    }

    private fun finishActivityWithDelay()
    {
        lifecycleScope.launch {
            //delay(500)
            finish()
        }
    }

    private fun changeLanguage(language: String) {
//        setNewLocale(language)
        if (language.equals(LocaleManager.INDONESIAN, ignoreCase = true)) {
            PreferenceUtils.setlang("in")
            setNewLocale(language)
        } else if (language.equals(LocaleManager.SPANISH, ignoreCase = true)) {
            PreferenceUtils.setlang("es")
            setNewLocale(language)
        } else if (language.equals(LocaleManager.CAMBODIAN, ignoreCase = true)) {
            PreferenceUtils.setlang("km")
            setNewLocale(language)
        } else if (language.equals(LocaleManager.VIETNAMESE, ignoreCase = true)) {
            PreferenceUtils.setlang("vi")
            setNewLocale(language)
        } else{
            setNewLocale(language)
        }
    }

    private fun setNewLocale(language: String?) {
        if (language != null) {
            setNewLocale(this, language)
        }
    }


    override fun attachBaseContext(newBase: Context?) {
        val newsBase = newBase!!
        val lang: String = PreferenceUtils.getlang()
        super.attachBaseContext(MyContextWrapper.wrap(newsBase, lang))
    }

    private suspend fun readFileData(): String = withContext(Dispatchers.IO){
        val temp = StringBuilder()

        try {
            val file = File(DEVICE_LANG_LOG_FILENAME)
            if (file.exists()) {
                val fin: FileInputStream =
                    openFileInput(DEVICE_LANG_LOG_FILENAME)
                var a: Int
                while (fin.read().also { a = it } != -1) {
                    temp.append(a.toChar())
                }
                fin.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return@withContext temp.toString()
    }

    private suspend fun writeOnFile(data: String, logFileName: String, isPrivateMode: Boolean) = withContext(Dispatchers.IO){
        if (!fileList.contains(logFileName)) {
            //  logFileName.removeSuffix(".txt")
            fileList.add(logFileName)
            PreferenceUtils.putLogFileNames(fileList)
        }
        try {
            val fileOutputStream =
                openFileOutput(
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
        return "$language-false"
    }

    private fun writeReadLangDevice() {

        val deviceLang: String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0].language
        } else {
            Resources.getSystem().configuration.locale.language
        }

        if (PreferenceUtils.getlang().isNotEmpty()) {
            changeLanguage(PreferenceUtils.getlang())

            if (PreferenceUtils.getlang() == "in") {
                PreferenceUtils.putString(PREF_LOCALE, "id")
            } else {
                PreferenceUtils.putString(PREF_LOCALE, "en")
            }
        } else {
            lifecycleScope.launch {
                val readFileLang = readFileData()
                val langSplit: ArrayList<String>
                if (readFileLang.isNotEmpty()) {
                    langSplit = readFileLang.split("-") as ArrayList<String>

                    if (langSplit[1] == "false") {
                        writeOnFile(
                            getSystemDetail(deviceLang.toString()),
                            DEVICE_LANG_LOG_FILENAME,
                            true
                        )
                        val readFileLangFalse = readFileData()
                        val langSplitFalse = readFileLangFalse.split("-")
                        PreferenceUtils.setlang(langSplitFalse[0])
                    } else {
                        PreferenceUtils.setlang(langSplit[0])
                    }
                } else {
                    PreferenceUtils.setlang(deviceLang.toString())
                }
            }
        }
    }

    private fun getAllUserObserver() {
        userViewModel.getAllUsers.observe(this) {
            try {
                if (it.isNotEmpty() && (PreferenceUtils.getString(IS_LOGOUT_VIA_AUTH_FAIL).isNullOrEmpty() || PreferenceUtils.getString(IS_LOGOUT_VIA_AUTH_FAIL).equals("false"))) {
                    intent = Intent(this, DashboardNavigateActivity::class.java)
                    startActivity(intent)
                    finishActivityWithDelay()
                } else {
                    intent = Intent(this, DomainActivity::class.java)
                    startActivity(intent)
                    finishActivityWithDelay()
                }
            } catch (e: Exception) {
                Timber.d("${e.message}")
            }
        }
    }
}