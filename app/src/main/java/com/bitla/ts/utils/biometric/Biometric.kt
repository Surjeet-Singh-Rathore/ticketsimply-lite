package com.bitla.ts.utils.biometric

import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat


class Biometric {
    companion object {

        private var cancellationSignal: CancellationSignal? = null
        private val authenticationCallback: BiometricPrompt.AuthenticationCallback
            get() = @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    notifyUser("Authentication Error: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    notifyUser("Authentication Succeeded for user!")
                    notifySuccess()
                }
            }

        @RequiresApi(Build.VERSION_CODES.P)
        fun scanFinger(context: Context) {
            checkBiometricSupport(context)

            val biometricPrompt = BiometricPrompt.Builder(context)
                .setTitle("Scan your finger")
                .setSubtitle("Authentication is required")
                .setDescription("This app uses fingerprint protection to keep your data secure")
                .setNegativeButton(
                    "Cancel", context.mainExecutor
                ) { dialog, which ->
                    notifyUser("Authentication cancelled")
                }.build()

            biometricPrompt.authenticate(
                getCancellationSignal(),
                context.mainExecutor,
                authenticationCallback
            )
        }

        private fun notifyUser(message: String) {
        }

        private fun getCancellationSignal(): CancellationSignal {
            cancellationSignal = CancellationSignal()
            cancellationSignal?.setOnCancelListener {
                notifyUser("Authentication was cancelled by the user")
            }
            return cancellationSignal as CancellationSignal
        }


        private fun checkBiometricSupport(context: Context): Boolean {
            val keygaurdManager =
                (context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager)

            if (!keygaurdManager.isKeyguardSecure) {
                notifyUser("Fingerprint authentication has not been enabled in settings")
                return false
            }
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.USE_BIOMETRIC
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notifyUser("Fingerprint authentication permission is not enabled")
                return false
            }
            return if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
                true
            } else true
        }

        fun notifySuccess(): Boolean {
            return true
        }
    }
}