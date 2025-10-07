package com.bitla.ts.utils.biometric

import android.hardware.biometrics.BiometricPrompt

interface BiometricInterface {

    fun BiometricPrompt.AuthenticationCallback(listener: BiometricInterface) {

    }

}