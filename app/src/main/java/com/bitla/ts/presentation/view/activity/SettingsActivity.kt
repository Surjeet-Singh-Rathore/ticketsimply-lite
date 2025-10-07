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
import toLoginModel
import toast
import visible


@AndroidEntryPoint
class SettingsActivity : BaseActivity(),
    RemoteConfigUpdateHelper.OnSendErrorReportListener, DialogSingleButtonListener {

    private var country: String? = ""
    private var selectedRapidBookingType: Int = 0
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val userViewModel: UserViewModel by viewModels()
    private var currentUser = LoginModel()
    private var biometricUser = LoginModel()
    private var isFingerPrintRegisteredForOtherUser = false
    private var privilegeResponseModel: PrivilegeResponseModel? = null

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, DashboardNavigateActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        getPref()
        setPrivilegesCheck()
        remoteConfigCheckForErrorReport()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.fingerPrintCard.visible()
        } else {
            binding.fingerPrintCard.gone()
        }


        val coachPref = PreferenceUtils.getPreference("COACH_VIEW_SELECTION", "SingleViewSelected")
        if (coachPref == "SingleViewSelected") {
            binding.viewSelection.text = getString(R.string.single_view)
        } else if (coachPref == "SplitViewSelected") {
            binding.viewSelection.text = getString(R.string.split_view)
        }else if(coachPref == "WebViewSelected"){
            binding.viewSelection.text = getString(R.string.web_view)
        }

        binding.coachLayoutChoice.setOnClickListener {
            val intent = Intent(this, CoachViewSelectionActivity::class.java)
            startActivity(intent)
        }
        binding.languageSelection.setOnClickListener {
            val intent = Intent(this, LanguageSelectionAvtivity::class.java)
            startActivity(intent)
        }

        binding.rapidBookingCard.setOnClickListener {
            val intent = Intent(this, RapidBookingSelectionActivity::class.java)
            startActivity(intent)
        }


        binding.selectLandingPage.setOnClickListener {
            val intent = Intent(this, LandingPageActivity::class.java)
            intent.putExtra(getString(R.string.landing_page_key), getString(R.string.landing_page))
            resultLauncher.launch(intent)
        }

        binding.printTypeLL.setOnClickListener{
            val intent = Intent(this, PrinterTypeActivity::class.java)
            startActivity(intent)
        }

        binding.infoTV.setOnClickListener {
            DialogUtils.dialogRedelcomDetails(this@SettingsActivity, currentUser.redelcomData!!)

        }

        binding.blackListLayout.setOnClickListener {
            val intent = Intent(this, BlackListNumberActivity::class.java)
            startActivity(intent)

        }

        binding.textSizeLL.setOnClickListener {
            val intent = Intent(this, TextSizeActivity::class.java)
            startActivity(intent)

        }
//        binding.layoutSendError.setOnClickListener {
//            binding.includeProgress.progressBar.visible()
//            if (!PreferenceUtils.getLogFileNames().isNullOrEmpty()) {
//                PreferenceUtils.getLogFileNames()?.forEach {
//                    readFileData(it)
//                }
//            } else
//                Timber.d("log file not found")
//        }


        val selectedLandingPage =
            PreferenceUtils.getString(getString(R.string.landing_page)).toString()
        if (selectedLandingPage.isEmpty()) {
            binding.viewSelectionLandingPage.text = getString(R.string.bookings)
        } else {
            binding.viewSelectionLandingPage.text = selectedLandingPage
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


        if(country.equals("india",true) && previledeData?.allowRapidBookingFlow == true && previledeData.allowRapidBookingFlowBySelectingSeats){
            binding.rapidBookingCard.visible()
        }else{
            binding.rapidBookingCard.gone()
        }


        if (currentUser.redelcomData != null) {
            if (currentUser.redelcomData!!.is_redelcom_enabled) {
                binding.redelcommCB.isChecked = true
                binding.infoTV.visibility = View.VISIBLE
            } else {
                binding.redelcommCB.isChecked = false
                binding.infoTV.visibility = View.GONE
            }
        }

    }

    private fun setPrivilegesCheck() {
        if(privilegeResponseModel?.allowToAddBlacklistNumbers == true)
            binding.blackListLayout.visible()
        else
            binding.blackListLayout.gone()
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



        setGetCurrentUserObserver()

        firebaseAnalytics = Firebase.analytics

        binding.addBiometric.setOnClickListener {
            registerBiometric_sub()

            firebaseLogEvent(
            this,
            ADD_FINGERPRINT,
            PreferenceUtils.getLogin().userName,
            PreferenceUtils.getLogin().travels_name,
            PreferenceUtils.getLogin().role,
            ADD_FINGERPRINT,
            AddFingerprint.ADD_CLICK
            )
        }
        binding.deLinkBiometric.setOnClickListener {
            delinkFingerprint()

            logFeatureSelectedEvent(
                currentUser.userName,
                currentUser.travels_name,
                currentUser.role,
                "fingerPrint used"
            )
        }

        notificationSoundSettings()

    }

    /*private fun readFileData(fileName: String) {
        try {
            val storageRef = FirebaseStorage.getInstance().reference.child("logs/$fileName")
            val file = File(filesDir, fileName)
            val uri = Uri.fromFile(file)
            Timber.d("fileUri $uri")
            val uploadTask: UploadTask = storageRef.putFile(uri)

            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                println("Upload is $progress% done")
                //if (progress == 100.0)
                //  PreferenceUtils.removeKey(PREF_LOG_FILE_NAME)
                binding.includeProgress.progressBar.gone()
                //  toast(getString(R.string.error_report_sent_successfully))
            }.addOnPausedListener {
                fun onPaused(taskSnapshot: UploadTask.TaskSnapshot?) {
                    println("Upload is paused")
                }
                binding.includeProgress.progressBar.gone()
            }.addOnFailureListener {
                fun onFailure(@NonNull exception: Exception?) {
                    // Handle unsuccessful uploads
                }
                binding.includeProgress.progressBar.gone()
            }.addOnSuccessListener {
                fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot?) {
                    // Handle successful uploads on complete
                    // ...

                    //Deletes the file from the local system
                    file.delete()
                    Timber.d("File upload successfully")
                }
                binding.includeProgress.progressBar.gone()
            }

        } catch (e: IOException) {
            Timber.d("reading Logs exception ${e.message}")
            e.printStackTrace()
        }
    }*/

    private fun registerBiometric_sub() {

        if(!currentUser.linked) {
            if(isFingerPrintRegisteredForOtherUser) {
                notifyUser(getString(R.string.add_fingerprint_denied))
            } else {
                showBiometricPrompt()
            }
        }
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onResume() {
        if (PreferenceUtils.getlang() == "en") {
            binding.languageSelected.text =
                getString(R.string.english)
        } else if(PreferenceUtils.getlang() == "es"){
            binding.languageSelected.text = getString(R.string.spanish)
        } else if(PreferenceUtils.getlang() == "vi") {
            binding.languageSelected.text = getString(R.string.vietnamese)
        } else {
            binding.languageSelected.text = getString(R.string.indonesian)
        }

        if(PreferenceUtils.getPrintingType() == PRINT_TYPE_BLUETOOTH){
            binding.printTypeValueTV.text = this.getString(R.string.bluetooth)
        }else if(PreferenceUtils.getPrintingType() == PRINT_TYPE_HARVARD){
            binding.printTypeValueTV.text = this.getString(R.string.harvard)
        }else if(PreferenceUtils.getPrintingType() == PRINT_TYPE_PINELAB){
            binding.printTypeValueTV.text = this.getString(R.string.pinelab)
        }
        else{
            binding.printTypeValueTV.text = this.getString(R.string.SUNMI)
        }

        super.onResume()
        val coachPref = PreferenceUtils.getPreference("COACH_VIEW_SELECTION", "SingleViewSelected")

        if (coachPref == "SingleViewSelected") {
            binding.viewSelection.text = getString(R.string.single_view)
        } else if (coachPref == "SplitViewSelected") {
            binding.viewSelection.text = getString(R.string.split_view)
        }else if(coachPref == "WebViewSelected"){
            binding.viewSelection.text = getString(R.string.web_view)

        }

        selectedRapidBookingType = PreferenceUtils.getRapidBookingType()
        when(selectedRapidBookingType){
            RAPID_TYPE_DEFAULT -> {
                binding.rapidBookingTV.text = getString(R.string.defaultt)
            }
            RAPID_TYPE_OPTIONAL -> {
                binding.rapidBookingTV.text = getString(R.string.optional)
            }
            RAPID_TYPE_HIDE -> {
                binding.rapidBookingTV.text = getString(R.string.hide)
            }
        }

        setBiometricButtonVisibility()
    }

    fun onclickBack(v: View) {
        onBackPressed()
    }


    private fun notifyUser(message: String) {
        DialogUtils.oneTouchDialog(this, message, object: DialogSingleButtonListener {
            override fun onSingleButtonClick(str: String) {

            }

        })
    }

    private fun showBiometricPrompt() {

        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = ContextCompat.getMainExecutor(this)
                val biometricPrompt = BiometricPrompt(this, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence
                        ) {
                            super.onAuthenticationError(errorCode, errString)
                            toast("${getString(R.string.authenticationError)} : $errString")
                        }

                        override fun onAuthenticationSucceeded(
                            result: BiometricPrompt.AuthenticationResult
                        ) {
                            super.onAuthenticationSucceeded(result)
                            toast("${getString(R.string.authenticationSucceeded)}!")
                            //loginModel?.linked = true
//                            Timber.d("test auth", loginModel.toString())
                            currentUser.linked = true
                            setUserBiometricPreference()
                            setBiometricButtonVisibility()
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            toast(getString(R.string.authentication_failed))
                        }
                    })

                val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.add_fingerprint))
                    .setSubtitle(getString(R.string.please_place_your_fingertip))
                    .setNegativeButtonText(getString(R.string.cancel))
                    .build()
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                toast(getString(R.string.noBiometricFeatureAvaibleOnThisDevice))
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                toast(getString(R.string.biometricFeaturesAreCurrenyUnavailable))
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                toast(getString(R.string.fingerprintIsNotConfigured))
            }
        }

    }

    private fun delinkFingerprint() {
        currentUser.linked = false
        setUserBiometricPreference()
        binding.deLinkBiometric.gone()
        binding.addBiometric.visible()
    }

    private fun logFeatureSelectedEvent(
        loginId: String?,
        operatorName: String?,
        roleName: String?,
        fingerprint_option: String
    ) {

        firebaseAnalytics.logEvent("fingerprint_option") {
            param(LOGIN_ID, loginId.toString())
            param(OPERATOR_NAME, operatorName.toString())
            param(ROLE_NAME, roleName.toString())
            param(FINGERPRINT_OPTION, fingerprint_option)
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedLandingPageName =
                    result.data?.getStringExtra(getString(R.string.landing_page_key)).toString()
                binding.viewSelectionLandingPage.text = selectedLandingPageName
            }
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

        //binding.notificationSoundValueTV.text = PreferenceUtils.getNotificationSoundType(applicationContext)

        binding.notificationSoundLL.setOnClickListener {
            val intent = Intent(this, PushNotificationSoundSettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setGetCurrentUserObserver() {
        userViewModel.getCurrentUser.observe(this) {
            if(it != null) {
                currentUser = it.toLoginModel()
                biometricUser = PreferenceUtils.getObject(PREF_CURRENT_BIOMETRIC_USER) ?: LoginModel()

                if(biometricUser.name.isNotEmpty()
                    && biometricUser.domainName.isNotEmpty()
                    && biometricUser.userName.isNotEmpty()
                ) {
                    if (biometricUser.name.equals(currentUser.name)
                        && biometricUser.domainName.equals(currentUser.domainName)
                        && biometricUser.userName.equals(currentUser.userName)
                    ) {
                        currentUser.linked = biometricUser.linked
                    } else {
                        isFingerPrintRegisteredForOtherUser = true
                    }
                }
                setBiometricButtonVisibility()
            } else {
                finish()
            }
        }
    }

    private fun setBiometricButtonVisibility() {
        if (currentUser.linked) {
            binding.deLinkBiometric.visible()
            binding.addBiometric.gone()
        } else {
            binding.deLinkBiometric.gone()
            binding.addBiometric.visible()
        }
    }

    private fun setUserBiometricPreference() {

        biometricUser = currentUser

        PreferenceUtils.putObject(biometricUser, PREF_CURRENT_BIOMETRIC_USER)
    }

    override fun onSingleButtonClick(str: String) {

    }
}