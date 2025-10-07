package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.confirm_reset_password.request.ConfirmResetPasswordRequest
import com.bitla.ts.domain.pojo.reset_password_with_otp.request.ReqBody
import com.bitla.ts.domain.pojo.reset_password_with_otp.request.ResetPasswordRequest
import com.bitla.ts.koin.models.makeApiCall


class ResetPasswordRepository(private val apiInterface: ApiInterface) {

    suspend fun newResetPasswordApi(
        resetPasswordRequest: ReqBody
    ) = makeApiCall {apiInterface.newResetPasswordWithOtpApi(resetPasswordRequest)}

    suspend fun newConfirmResetPasswordApi(
        confirmResetPasswordRequest: com.bitla.ts.domain.pojo.confirm_reset_password.request.ReqBody
    ) = makeApiCall { apiInterface.newConfirmResetPasswordApi(confirmResetPasswordRequest) }
}
