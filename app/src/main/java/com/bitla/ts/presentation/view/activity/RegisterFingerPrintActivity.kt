package com.bitla.ts.presentation.view.activity

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.utils.sharedPref.PREF_LINKED_BIOMETRIC
import com.bitla.ts.utils.sharedPref.PREF_LOGGED_IN_USER
import com.bitla.ts.utils.sharedPref.PreferenceUtils

class RegisterFingerPrintActivity : BaseActivity() {
    override fun initUI() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_finger_print)


    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    fun onclickBack(v: View) {
        onBackPressed()
    }

    private var cancellationSignal: CancellationSignal? = null
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() = @RequiresApi(Build.VERSION_CODES.M)
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                notifyUser("Authentication Error: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                notifyUser("Fingerprint successfully added")
                var loginUser = PreferenceUtils.getObject<LoginModel>(PREF_LOGGED_IN_USER)
                loginUser?.linked = !(loginUser!!.linked)
                PreferenceUtils.setPreference(PREF_LINKED_BIOMETRIC, loginUser.linked)
                PreferenceUtils.putObject(loginUser, PREF_LOGGED_IN_USER)
                onBackPressed()
            }
        }

    @RequiresApi(Build.VERSION_CODES.P)
    fun scanFinger(v: View) {
//        Biometric.scanFinger(this)
        checkBiometricSupport()
        val biometricPrompt = BiometricPrompt.Builder(this)
            .setTitle("Scan your finger")
            .setSubtitle("Authentication is required")
            .setDescription("This app uses fingerprint protection to keep your data secure")
            .setNegativeButton(
                "Cancel",
                this.mainExecutor,
                DialogInterface.OnClickListener { dialog, which ->
                    notifyUser("Authentication cancelled")
                }).build()

        biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
    }

    private fun notifyUser(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport(): Boolean {
        val keygaurdManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keygaurdManager.isKeyguardSecure) {
            notifyUser("Fingerprint authentication has not been enabled in settings")
            return false
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notifyUser("Fingerprint authentication permission is not enabled")
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
    }
}