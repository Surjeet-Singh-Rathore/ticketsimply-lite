package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.activity.*
import androidx.activity.result.contract.*
import androidx.biometric.*
import androidx.core.content.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.db.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.view.dashboard.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.*
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.*
import dagger.hilt.android.*
import gone
import toast
import visible


@AndroidEntryPoint
class SettingsActivity : BaseActivity(),
    RemoteConfigUpdateHelper.OnSendErrorReportListener, DialogSingleButtonListener {

    private var country: String? = ""
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val userViewModel: UserViewModel by viewModels()
    private var privilegeResponseModel: PrivilegeResponseModel? = null

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, DashboardNavigateActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        getPref()
        remoteConfigCheckForErrorReport()

        binding.printTypeLL.setOnClickListener{
            val intent = Intent(this, PrinterTypeActivity::class.java)
            startActivity(intent)
        }

        binding.textSizeLL.setOnClickListener {
            val intent = Intent(this, TextSizeActivity::class.java)
            startActivity(intent)
        }

        val previledeData = getPrivilegeBase()

        if (previledeData?.country?.isNotEmpty() == true) {
            country = previledeData.country
        }

        if(country.equals("india", ignoreCase = true)){
            binding.textSizeLL.visible()
        }else{
            binding.textSizeLL.gone()
        }
    }

    private fun getPref() {
        if (getPrivilegeBase() != null) {
          privilegeResponseModel = getPrivilegeBase()

        }
    }


    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun remoteConfigCheckForErrorReport() {
        RemoteConfigUpdateHelper.with(this).onCheckErrorReportEnabled(this).check()
    }

    override fun initUI() {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        binding.simpleToolbar.toolbarHeaderText.setText(R.string.action_settings)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        userViewModel.getCurrentUser()
        firebaseAnalytics = Firebase.analytics

        notificationSoundSettings()

    }





    @SuppressLint("SuspiciousIndentation")
    override fun onResume() {

        if(PreferenceUtils.getPrintingType() == PRINT_TYPE_BLUETOOTH){
            binding.printTypeValueTV.text = this.getString(R.string.bluetooth)
        }else if(PreferenceUtils.getPrintingType() == PRINT_TYPE_HARVARD){
            binding.printTypeValueTV.text = this.getString(R.string.harvard)
        }else if(PreferenceUtils.getPrintingType() == PRINT_TYPE_PINELAB){
            binding.printTypeValueTV.text = this.getString(R.string.pinelab)
        } else if(PreferenceUtils.getPrintingType() == PRINT_TYPE_SUNMI){
            binding.printTypeValueTV.text = this.getString(R.string.SUNMI)
        }
        else{
            binding.printTypeValueTV.text = this.getString(R.string.paytm_print_option)
        }

        super.onResume()


    }

    fun onclickBack(v: View) {
        onBackPressed()
    }



    override fun onErrorCheckListener(isSendErrorReportEnable: Boolean?) {
            if (!privilegeResponseModel?.country.isNullOrEmpty() && privilegeResponseModel?.country.equals(
                    "indonesia",
                    true
                ) && isSendErrorReportEnable!!
            )
                binding.layoutSendError.gone()
            else
                binding.layoutSendError.gone()

    }

    private fun notificationSoundSettings() {

        when(PreferenceUtils.getNotificationSoundType(applicationContext)) {
            NOTIFICATION_SILENT -> {
                binding.notificationSoundValueTV.text = getString(R.string.silent)
            }

            NOTIFICATION_SYSTEM_SOUND -> {
                binding.notificationSoundValueTV.text = getString(R.string.system_sound)
            }

            NOTIFICATION_DEFAULT_SOUND -> {
                binding.notificationSoundValueTV.text = getString(R.string.default_sound)
            }

            else -> {
                binding.notificationSoundValueTV.text = getString(R.string.empty)
            }
        }


        binding.notificationSoundLL.setOnClickListener {
            val intent = Intent(this, PushNotificationSoundSettingsActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onSingleButtonClick(str: String) {

    }
}